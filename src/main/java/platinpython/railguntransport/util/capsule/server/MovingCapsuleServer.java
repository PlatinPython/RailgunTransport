package platinpython.railguntransport.util.capsule.server;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import platinpython.railguntransport.block.CapsuleBlock;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;
import platinpython.railguntransport.util.registries.BlockRegistry;

public class MovingCapsuleServer {
    private final CompoundTag capsuleData;
    private final BlockPos start;
    private final BlockPos target;
    private final int totalTicks;
    private int remainingTicks;

    public MovingCapsuleServer(CompoundTag capsuleData, BlockPos start, BlockPos target) {
        this(capsuleData, start, target, calculateTicksForDistance(start, target),
             calculateTicksForDistance(start, target)
        );
    }

    private MovingCapsuleServer(CompoundTag capsuleData, BlockPos start, BlockPos target, int totalTicks,
                                int remainingTicks) {
        this.capsuleData = capsuleData;
        this.start = start;
        this.target = target;
        this.totalTicks = totalTicks;
        this.remainingTicks = remainingTicks;
    }

    private static int calculateTicksForDistance(BlockPos start, BlockPos target) {
        return (int) Mth.map(Math.sqrt(Math.sqrt(start.distSqr(target))) * 10, 100, 1000, 50, 1000);
    }

    public boolean tick(ServerLevel level) {
        if (this.remainingTicks > 0) {
            this.remainingTicks--;
            return false;
        }

        level.setBlock(this.target,
                       BlockRegistry.CAPSULE.get().defaultBlockState().setValue(CapsuleBlock.FACING, Direction.DOWN),
                       Block.UPDATE_ALL
        );
        level.getBlockEntity(this.target, BlockEntityRegistry.CAPSULE.get()).ifPresent(blockEntity -> {
            CompoundTag tag = blockEntity.saveWithoutMetadata();
            CompoundTag tagCopy = tag.copy();
            tag.merge(this.capsuleData);
            if (!tag.equals(tagCopy)) {
                blockEntity.load(tag);
                blockEntity.setChanged();
            }
        });
        return true;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("CapsuleData", this.capsuleData);
        tag.put("Start", NbtUtils.writeBlockPos(this.start));
        tag.put("Target", NbtUtils.writeBlockPos(this.target));
        tag.putInt("TotalTicks", this.totalTicks);
        tag.putInt("RemainingTicks", this.remainingTicks);
        return tag;
    }

    public static MovingCapsuleServer deserializeNBT(CompoundTag tag) {
        CompoundTag capsuleData = tag.getCompound("CapsuleData");
        BlockPos start = NbtUtils.readBlockPos(tag.getCompound("Start"));
        BlockPos target = NbtUtils.readBlockPos(tag.getCompound("Target"));
        int totalTicks = tag.getInt("TotalTicks");
        int remainingTicks = tag.getInt("RemainingTicks");
        return new MovingCapsuleServer(capsuleData, start, target, totalTicks, remainingTicks);
    }

    public BlockPos getStart() {
        return start;
    }

    public BlockPos getTarget() {
        return target;
    }

    public int getTotalTicks() {
        return totalTicks;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }
}
