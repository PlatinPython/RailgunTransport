package platinpython.railgun_transport.util.saveddata;

import com.mojang.serialization.Codec;
import dev.lukebemish.codecextras.Asymmetry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.neoforge.network.PacketDistributor;
import platinpython.railgun_transport.RailgunTransport;
import platinpython.railgun_transport.util.capsule.server.MovingCapsuleServer;
import platinpython.railgun_transport.util.network.packets.MovingCapsulesSyncPayload;
import platinpython.railgun_transport.util.network.packets.NewMovingCapsulePayload;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MovingCapsuleSavedData extends SavedData {
    public static final Codec<Asymmetry<Consumer<MovingCapsuleSavedData>, MovingCapsuleSavedData>> CODEC =
        Asymmetry.split(
            MovingCapsuleServer.CODEC.listOf().xmap(Set::copyOf, List::copyOf),
            set -> movingCapsuleSavedData -> movingCapsuleSavedData.movingCapsules.addAll(set),
            movingCapsuleSavedData -> movingCapsuleSavedData.movingCapsules
        );

    private static final String FILE_NAME =
        ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "moving_capsules").toString().replace(':', '_');
    private static final Factory<MovingCapsuleSavedData> FACTORY =
        new Factory<>(MovingCapsuleSavedData::new, MovingCapsuleSavedData::load);

    private final HashSet<MovingCapsuleServer> movingCapsules = new HashSet<>();

    public MovingCapsuleSavedData() {
        this.setDirty();
    }

    public static MovingCapsuleSavedData get(DimensionDataStorage dimensionDataStorage) {
        return dimensionDataStorage.computeIfAbsent(FACTORY, FILE_NAME);
    }

    public static boolean isPresent(DimensionDataStorage dimensionDataStorage) {
        return dimensionDataStorage.get(FACTORY, FILE_NAME) != null;
    }

    public void add(ItemStack capsule, BlockPos start, BlockPos target, ServerLevel level) {
        MovingCapsuleServer movingCapsule = new MovingCapsuleServer(capsule, start, target);
        this.movingCapsules.add(movingCapsule);
        PacketDistributor
            .sendToPlayersInDimension(level, new NewMovingCapsulePayload(Asymmetry.ofEncoding(movingCapsule)));
        this.setDirty();
    }

    public void tick(ServerLevel level) {
        if (this.movingCapsules.removeIf(c -> c.tick(level))) {
            this.setDirty();
        }
    }

    public void sync(ServerPlayer player) {
        PacketDistributor
            .sendToPlayer(player, new MovingCapsulesSyncPayload(Asymmetry.ofEncoding(this.movingCapsules)));
    }

    public void sync(ServerLevel level) {
        PacketDistributor
            .sendToPlayersInDimension(level, new MovingCapsulesSyncPayload(Asymmetry.ofEncoding(this.movingCapsules)));
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CODEC.encodeStart(NbtOps.INSTANCE, Asymmetry.ofEncoding(this))
            .resultOrPartial(RailgunTransport.LOGGER::error)
            .ifPresent(capsules -> tag.put("moving_capsules", capsules));
        return tag;
    }

    public static MovingCapsuleSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        MovingCapsuleSavedData movingCapsuleSavedData = new MovingCapsuleSavedData();
        if (!tag.contains("moving_capsules")) {
            return movingCapsuleSavedData;
        }
        CODEC.parse(NbtOps.INSTANCE, tag.get("moving_capsules"))
            .flatMap(Asymmetry::decoding)
            .resultOrPartial(RailgunTransport.LOGGER::error)
            .ifPresent(consumer -> consumer.accept(movingCapsuleSavedData));
        return movingCapsuleSavedData;
    }
}
