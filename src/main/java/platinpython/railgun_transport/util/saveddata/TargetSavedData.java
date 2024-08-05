package platinpython.railgun_transport.util.saveddata;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.lukebemish.codecextras.Asymmetry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.Vec3;
import platinpython.railgun_transport.RailgunTransport;
import platinpython.railgun_transport.block.entity.TerminalBlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TargetSavedData extends SavedData {
    public static final Codec<Asymmetry<Consumer<TargetSavedData>, TargetSavedData>> CODEC = Asymmetry.split(
        Codec.pair(BlockPos.CODEC, Codec.STRING.optionalFieldOf("name").codec()).listOf(),
        entries -> targetSavedData -> entries
            .forEach(entry -> targetSavedData.targets.put(entry.getFirst(), entry.getSecond())),
        targetSavedData -> targetSavedData.targets.entrySet()
            .stream()
            .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
            .toList()
    );

    private static final String FILE_NAME =
        ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "targets").toString().replace(':', '_');
    private static final Factory<TargetSavedData> FACTORY = new Factory<>(TargetSavedData::new, TargetSavedData::load);
    private static final double MIN_DISTANCE = 100D;
    private static final double MAX_DISTANCE = 10_000D;

    private final HashMap<BlockPos, Optional<String>> targets = new HashMap<>();

    public TargetSavedData() {
        this.setDirty();
    }

    public static TargetSavedData get(DimensionDataStorage dimensionDataStorage) {
        return dimensionDataStorage.computeIfAbsent(FACTORY, FILE_NAME);
    }

    public void add(BlockPos pos, ServerLevel level) {
        if (level.getBlockEntity(pos) instanceof TerminalBlockEntity blockEntity) {
            if (blockEntity.getTargetData().isPresent()) {
                this.targets.put(pos, blockEntity.getTargetData().get().getName());
                this.setDirty();
            }
        }
    }

    public void remove(BlockPos pos) {
        this.targets.remove(pos);
        this.setDirty();
    }

    public Map<BlockPos, Optional<String>> getReachablePositions(BlockPos centerPos) {
        Vec3 center = Vec3.atCenterOf(centerPos);
        return this.targets.entrySet()
            .stream()
            .filter(entry -> horizontalBetweenDistances(entry.getKey(), center))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static boolean horizontalBetweenDistances(BlockPos posToCheck, Vec3 center) {
        double x = posToCheck.getX() + 0.5D - center.x;
        double z = posToCheck.getZ() + 0.5D - center.z;
        double distanceFromCenter = x * x + z * z;
        return distanceFromCenter >= Mth.square(MIN_DISTANCE) && distanceFromCenter <= Mth.square(MAX_DISTANCE);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CODEC.encodeStart(NbtOps.INSTANCE, Asymmetry.ofEncoding(this))
            .resultOrPartial(RailgunTransport.LOGGER::error)
            .ifPresent(targets -> tag.put("targets", targets));
        return tag;
    }

    public static TargetSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        TargetSavedData targetSavedData = new TargetSavedData();
        if (!tag.contains("targets")) {
            return targetSavedData;
        }
        CODEC.parse(NbtOps.INSTANCE, tag.get("targets"))
            .flatMap(Asymmetry::decoding)
            .resultOrPartial(RailgunTransport.LOGGER::error)
            .ifPresent(consumer -> consumer.accept(targetSavedData));
        return targetSavedData;
    }
}
