package platinpython.railguntransport.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;

public class RailgunBlock extends BaseEntityBlock {
    public RailgunBlock() {
        super(Properties.of(Material.HEAVY_METAL)
                        .isValidSpawn((state, blockGetter, pos, entityType) -> false)
                        .sound(SoundType.NETHERITE_BLOCK)
                        .strength(50F, 1200F));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.RAILGUN.get().create(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
