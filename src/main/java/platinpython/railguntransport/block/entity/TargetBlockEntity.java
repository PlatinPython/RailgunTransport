package platinpython.railguntransport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;

public class TargetBlockEntity extends BlockEntity {
    private boolean isFree = true;

    public TargetBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.TARGET.get(), worldPosition, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isFree", this.isFree);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("isFree")) {
            this.isFree = tag.getBoolean("isFree");
        }
    }
}
