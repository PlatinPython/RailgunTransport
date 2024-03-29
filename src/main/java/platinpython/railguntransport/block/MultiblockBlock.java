package platinpython.railguntransport.block;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.IBlockRenderProperties;
import org.jetbrains.annotations.Nullable;
import platinpython.railguntransport.block.entity.MultiblockBlockEntity;
import platinpython.railguntransport.block.entity.TerminalBlockEntity;
import platinpython.railguntransport.util.multiblock.MultiblockHelper;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;

import java.util.Optional;
import java.util.function.Consumer;

public class MultiblockBlock extends BaseEntityBlock {
    public MultiblockBlock() {
        super(Properties.of(Material.METAL)
                        .isValidSpawn((state, blockGetter, pos, entityType) -> false)
                        .sound(SoundType.METAL)
                        .strength(5F, 6F)
                        .noOcclusion());
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1F;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.MULTIBLOCK.get().create(pos, state);
    }

    @Override
    public void initializeClient(Consumer<IBlockRenderProperties> consumer) {
        IBlockRenderProperties renderProperties = new IBlockRenderProperties() {
            @Override
            public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
                return true;
            }

            @Override
            public boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager) {
                return true;
            }
        };
        consumer.accept(renderProperties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            Optional<MultiblockBlockEntity> blockEntity = level.getBlockEntity(pos,
                                                                               BlockEntityRegistry.MULTIBLOCK.get()
            );
            if (blockEntity.isPresent()) {
                Optional<TerminalBlockEntity> terminalBlockEntity = level.getBlockEntity(
                        blockEntity.get().getTerminalPos(), BlockEntityRegistry.TERMINAL.get());
                terminalBlockEntity.ifPresent(MultiblockHelper::disassemble);
                //TODO: somehow drop the correct block, this does not work
                if (newState.is(blockEntity.get().getBlockState().getBlock())) {
                    ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5, pos.getZ() + 0.5,
                                                           new ItemStack(
                                                                   blockEntity.get().getSavedBlockState().getBlock())
                    );
                    level.addFreshEntity(itemEntity);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
