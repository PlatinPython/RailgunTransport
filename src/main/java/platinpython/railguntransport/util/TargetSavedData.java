package platinpython.railguntransport.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.Vec3;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.block.entity.TargetBlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TargetSavedData extends SavedData {
    private static final String FILE_NAME = new ResourceLocation(RailgunTransport.MOD_ID, "targets").toString()
                                                                                                    .replace(':', '_');
    private final HashMap<BlockPos, Optional<String>> targets = new HashMap<>();

    public TargetSavedData() {
        this.setDirty();
    }

    public static TargetSavedData get(DimensionDataStorage dimensionDataStorage) {
        return dimensionDataStorage.computeIfAbsent(TargetSavedData::load, TargetSavedData::new, FILE_NAME);
    }

    public void add(BlockPos pos, ServerLevel level) {
        if (level.getBlockEntity(pos) instanceof TargetBlockEntity blockEntity) {
            this.targets.put(pos, blockEntity.getName());
            this.setDirty();
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
                           .filter(entry -> horizontalBetweenDistances(entry.getKey(), center, 100D, 10_000D))
                           .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @SuppressWarnings("SameParameterValue")
    private static boolean horizontalBetweenDistances(BlockPos posToCheck, Vec3 center, double minDistance,
                                                      double maxDistance) {
        double x = posToCheck.getX() + 0.5D - center.x;
        double z = posToCheck.getZ() + 0.5D - center.z;
        double distanceFromCenter = x * x + z * z;
        return distanceFromCenter > Mth.square(minDistance) && distanceFromCenter < Mth.square(maxDistance);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag listTag = new ListTag();
        this.targets.forEach((pos, name) -> {
            CompoundTag tag = new CompoundTag();
            tag.put("Position", NbtUtils.writeBlockPos(pos));
            name.ifPresent(s -> tag.putString("Name", s));
            listTag.add(tag);
        });
        compoundTag.put("Positions", listTag);
        return compoundTag;
    }

    public static TargetSavedData load(CompoundTag compoundTag) {
        TargetSavedData targetSavedData = new TargetSavedData();
        ListTag listTag = compoundTag.getList("Positions", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag tag = listTag.getCompound(i);
            BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("Position"));
            Optional<String> name = Optional.empty();
            if (tag.contains("Name")) {
                name = Optional.of(tag.getString("Name"));
            }
            targetSavedData.targets.put(pos, name);
        }
        return targetSavedData;
    }
}
