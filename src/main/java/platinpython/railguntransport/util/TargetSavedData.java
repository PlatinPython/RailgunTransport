package platinpython.railguntransport.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.Vec3;
import platinpython.railguntransport.RailgunTransport;

import java.util.HashSet;
import java.util.List;

public class TargetSavedData extends SavedData {
    private static final String FILE_NAME = new ResourceLocation(RailgunTransport.MOD_ID, "targets").toString()
                                                                                                    .replace(':', '_');
    private final HashSet<BlockPos> targets = new HashSet<>();

    public TargetSavedData() {
        this.setDirty();
    }

    public static TargetSavedData get(DimensionDataStorage dimensionDataStorage) {
        return dimensionDataStorage.computeIfAbsent(TargetSavedData::load, TargetSavedData::new, FILE_NAME);
    }

    public void add(BlockPos pos) {
        this.targets.add(pos);
        this.setDirty();
    }

    public void remove(BlockPos pos) {
        this.targets.remove(pos);
        this.setDirty();
    }

    public List<BlockPos> getReachablePositions(BlockPos centerPos) {
        Vec3 center = Vec3.atCenterOf(centerPos);
        return this.targets.stream().filter(pos -> pos.closerToCenterThan(center, 10_000D)).toList();
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag listTag = new ListTag();
        targets.forEach(pos -> listTag.add(NbtUtils.writeBlockPos(pos)));
        compoundTag.put("Positions", listTag);
        return compoundTag;
    }

    public static TargetSavedData load(CompoundTag compoundTag) {
        TargetSavedData targetSavedData = new TargetSavedData();
        ListTag listTag = compoundTag.getList("Positions", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            targetSavedData.targets.add(NbtUtils.readBlockPos(listTag.getCompound(i)));
        }
        return targetSavedData;
    }
}
