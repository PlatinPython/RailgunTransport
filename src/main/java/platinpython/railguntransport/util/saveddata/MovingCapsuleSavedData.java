package platinpython.railguntransport.util.saveddata;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.network.PacketDistributor;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.util.capsule.server.MovingCapsuleServer;
import platinpython.railguntransport.util.network.NetworkHandler;
import platinpython.railguntransport.util.network.packets.MovingCapsulesSyncPKT;
import platinpython.railguntransport.util.network.packets.NewMovingCapsulePKT;

import java.util.HashSet;

public class MovingCapsuleSavedData extends SavedData {
    private static final String FILE_NAME = new ResourceLocation(RailgunTransport.MOD_ID, "moving_capsules").toString()
                                                                                                            .replace(
                                                                                                                    ':',
                                                                                                                    '_'
                                                                                                            );
    private final HashSet<MovingCapsuleServer> movingCapsules = new HashSet<>();

    public MovingCapsuleSavedData() {
        this.setDirty();
    }

    public static MovingCapsuleSavedData get(DimensionDataStorage dimensionDataStorage) {
        return dimensionDataStorage.computeIfAbsent(MovingCapsuleSavedData::load, MovingCapsuleSavedData::new,
                                                    FILE_NAME
        );
    }

    public static boolean isPresent(DimensionDataStorage dimensionDataStorage) {
        return dimensionDataStorage.get(MovingCapsuleSavedData::load, FILE_NAME) != null;
    }

    public void add(CompoundTag capsuleData, BlockPos start, BlockPos target, ResourceKey<Level> dimension) {
        MovingCapsuleServer capsule = new MovingCapsuleServer(capsuleData, start, target);
        this.movingCapsules.add(capsule);
        NetworkHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> dimension),
                                     new NewMovingCapsulePKT(capsule.getStart(), capsule.getTarget(),
                                                             capsule.getTotalTicks(), capsule.getRemainingTicks()
                                     )
        );
        this.setDirty();
    }

    public void tick(ServerLevel level) {
        if (this.movingCapsules.removeIf(c -> c.tick(level))) {
            this.setDirty();
        }
    }

    public void sync(ServerPlayer player) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                                     MovingCapsulesSyncPKT.fromMovingCapsuleServerSet(this.movingCapsules)
        );
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag listTag = new ListTag();
        movingCapsules.forEach(c -> listTag.add(c.serializeNBT()));
        compoundTag.put("MovingCapsules", listTag);
        return compoundTag;
    }

    public static MovingCapsuleSavedData load(CompoundTag compoundTag) {
        MovingCapsuleSavedData movingCapsuleSavedData = new MovingCapsuleSavedData();
        ListTag listTag = compoundTag.getList("MovingCapsules", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag tag = listTag.getCompound(i);
            movingCapsuleSavedData.movingCapsules.add(MovingCapsuleServer.deserializeNBT(tag));
        }
        return movingCapsuleSavedData;
    }
}
