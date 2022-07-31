package platinpython.railguntransport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;

public class MultiblockBlockEntity extends BlockEntity {
    private BlockState savedBlockState = Blocks.AIR.defaultBlockState();
    private BlockPos terminalPos;

    public MultiblockBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.MULTIBLOCK.get(), worldPosition, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("SavedBlockState", NbtUtils.writeBlockState(this.savedBlockState));
        tag.put("TerminalPos", NbtUtils.writeBlockPos(this.terminalPos));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.savedBlockState = NbtUtils.readBlockState(tag.getCompound("SavedBlockState"));
        this.terminalPos = NbtUtils.readBlockPos(tag.getCompound("TerminalPos"));
    }

    public BlockState getSavedBlockState() {
        return this.savedBlockState;
    }

    public void setSavedBlockState(BlockState savedBlockState) {
        this.savedBlockState = savedBlockState;
    }

    public BlockPos getTerminalPos() {
        return this.terminalPos;
    }

    public void setTerminalPos(BlockPos terminalPos) {
        this.terminalPos = terminalPos;
    }
}
