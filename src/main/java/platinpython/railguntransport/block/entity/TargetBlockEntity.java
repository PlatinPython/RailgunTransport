package platinpython.railguntransport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;

public class TargetBlockEntity extends BlockEntity {
    public TargetBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.TARGET.get(), worldPosition, blockState);
    }
}
