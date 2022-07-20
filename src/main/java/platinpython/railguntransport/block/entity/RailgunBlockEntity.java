package platinpython.railguntransport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;

public class RailgunBlockEntity extends BlockEntity {
    public RailgunBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.RAILGUN.get(), worldPosition, blockState);
    }
}
