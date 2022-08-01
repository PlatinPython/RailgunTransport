package platinpython.railguntransport.util.capsule.server;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import platinpython.railguntransport.block.entity.MultiblockBlockEntity;
import platinpython.railguntransport.block.entity.TerminalBlockEntity;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;
import platinpython.railguntransport.util.registries.BlockRegistry;

import java.util.Optional;

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

        BlockPos spawnPos = this.target;
        ItemStack stack = new ItemStack(BlockRegistry.CAPSULE.get().asItem());
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag tagCopy = tag.copy();
        tag.merge(this.capsuleData);
        if (!tag.equals(tagCopy)) {
            stack.setTag(tag);
        }

        Optional<MultiblockBlockEntity> maybeMultiblockBlockEntity = level.getBlockEntity(this.target,
                                                                                          BlockEntityRegistry.MULTIBLOCK.get()
        );
        if (maybeMultiblockBlockEntity.isPresent()) {
            spawnPos = maybeMultiblockBlockEntity.get().getTerminalPos();

            Optional<TerminalBlockEntity> maybeTerminalBlockEntity = maybeMultiblockBlockEntity.flatMap(
                    blockEntity -> level.getBlockEntity(blockEntity.getTerminalPos(),
                                                        BlockEntityRegistry.TERMINAL.get()
                    ));
            if (maybeTerminalBlockEntity.isPresent()) {
                Optional<IItemHandler> handler = maybeTerminalBlockEntity.get()
                                                                         .getCapability(
                                                                                 CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                                                                         .resolve();
                if (handler.isPresent()) {
                    if (handler.get().insertItem(0, stack, true).isEmpty()) {
                        handler.get().insertItem(0, stack, false);
                        maybeTerminalBlockEntity.flatMap(TerminalBlockEntity::getTargetData)
                                                .ifPresent(data -> data.setFree(true));
                        return true;
                    }
                }
            }
        }
        level.addFreshEntity(new ItemEntity(level, spawnPos.getX() + 0.5D, spawnPos.getY() + 0.5D, spawnPos.getZ() + 0.5D, stack));
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
