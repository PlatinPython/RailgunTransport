package platinpython.railguntransport.util.multiblock;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.railguntransport.block.TerminalBlock;
import platinpython.railguntransport.block.entity.RailgunData;
import platinpython.railguntransport.block.entity.TargetData;
import platinpython.railguntransport.block.entity.TerminalBlockEntity;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;
import platinpython.railguntransport.util.registries.BlockRegistry;

import java.util.Optional;

public class MultiblockHelper {
    private static final ImmutableList<Block> BASE_BLOCKS = ImmutableList.of(Blocks.SMOOTH_STONE, Blocks.SMOOTH_STONE,
                                                                             Blocks.IRON_BLOCK, Blocks.SMOOTH_STONE,
                                                                             Blocks.REDSTONE_BLOCK,
                                                                             Blocks.NETHERITE_BLOCK,
                                                                             Blocks.SMOOTH_STONE, Blocks.SMOOTH_STONE,
                                                                             Blocks.IRON_BLOCK
    );

    private static final ImmutableList<Block> RAILGUN_BLOCKS = ImmutableList.of(Blocks.POLISHED_BLACKSTONE,
                                                                                Blocks.DIAMOND_BLOCK,
                                                                                Blocks.POLISHED_BLACKSTONE_WALL,
                                                                                Blocks.POLISHED_BLACKSTONE_STAIRS,
                                                                                Blocks.COPPER_BLOCK, Blocks.DISPENSER,
                                                                                Blocks.POLISHED_BLACKSTONE,
                                                                                Blocks.DIAMOND_BLOCK,
                                                                                Blocks.POLISHED_BLACKSTONE_WALL
    );

    private static final ImmutableList<Block> TARGET_BLOCKS = ImmutableList.of(Blocks.POLISHED_BLACKSTONE,
                                                                               Blocks.POLISHED_BLACKSTONE,
                                                                               Blocks.POLISHED_BLACKSTONE_WALL,
                                                                               Blocks.DIAMOND_BLOCK,
                                                                               Blocks.COPPER_BLOCK, Blocks.OBSERVER,
                                                                               Blocks.POLISHED_BLACKSTONE,
                                                                               Blocks.POLISHED_BLACKSTONE,
                                                                               Blocks.POLISHED_BLACKSTONE_WALL
    );

    public static void tryAssemble(TerminalBlockEntity blockEntity) {
        if (blockEntity == null) {
            return;
        }
        if (blockEntity.getLevel() == null) {
            return;
        }
        switch (getMultiblockType(blockEntity)) {
            case RAILGUN -> assembleRailgun(blockEntity);
            case TARGET -> assembleTarget(blockEntity);
            case NONE -> {
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static MultiblockType getMultiblockType(TerminalBlockEntity blockEntity) {
        Direction direction = blockEntity.getBlockState().getValue(TerminalBlock.HORIZONTAL_FACING).getOpposite();
        BlockPos frontLeftCorner = blockEntity.getBlockPos()
                                              .relative(direction)
                                              .relative(Rotation.COUNTERCLOCKWISE_90.rotate(direction));
        BlockPos backRightCorner = blockEntity.getBlockPos()
                                              .relative(direction, 3)
                                              .relative(Rotation.CLOCKWISE_90.rotate(direction));
        for (int i = 0; i < 9; i++) {
            BlockPos pos = blockEntity.getBlockPos()
                                      .relative(direction, i % 3 + 1)
                                      .relative(Rotation.CLOCKWISE_90.rotate(direction), i / 3 - 1);
            if (!blockEntity.getLevel().getBlockState(pos).is(BASE_BLOCKS.get(i))) {
                return MultiblockType.NONE;
            }
        }
        for (BlockPos pos : BlockPos.betweenClosed(frontLeftCorner.above(2), backRightCorner.above(2))) {
            if (!blockEntity.getLevel().getBlockState(pos).is(Blocks.AIR)) {
                return MultiblockType.NONE;
            }
        }
        MultiblockType type;
        BlockState determiningState = blockEntity.getLevel()
                                                 .getBlockState(blockEntity.getBlockPos().above().relative(direction));
        if (determiningState.is(RAILGUN_BLOCKS.get(3))) {
            type = MultiblockType.RAILGUN;
        } else if (determiningState.is(TARGET_BLOCKS.get(3))) {
            type = MultiblockType.TARGET;
        } else {
            return MultiblockType.NONE;
        }
        for (int i = 0; i < 9; i++) {
            BlockPos pos = blockEntity.getBlockPos()
                                      .above()
                                      .relative(direction, i % 3 + 1)
                                      .relative(Rotation.CLOCKWISE_90.rotate(direction), i / 3 - 1);
            switch (type) {
                case RAILGUN -> {
                    if (!blockEntity.getLevel().getBlockState(pos).is(RAILGUN_BLOCKS.get(i))) {
                        return MultiblockType.NONE;
                    }
                }
                case TARGET -> {
                    if (!blockEntity.getLevel().getBlockState(pos).is(TARGET_BLOCKS.get(i))) {
                        return MultiblockType.NONE;
                    }
                }
                default -> {
                }
            }
        }
        return type;
    }

    private static void assembleRailgun(TerminalBlockEntity blockEntity) {
        assembleMultiblockPart(blockEntity);
        //noinspection ConstantConditions
        blockEntity.getLevel()
                   .setBlock(blockEntity.getBlockPos(), blockEntity.getBlockState()
                                                                   .setValue(TerminalBlock.MULTIBLOCK_TYPE,
                                                                             MultiblockType.RAILGUN
                                                                   ), Block.UPDATE_ALL);
        blockEntity.setRailgunData(Optional.of(new RailgunData(blockEntity)));
    }

    private static void assembleTarget(TerminalBlockEntity blockEntity) {
        assembleMultiblockPart(blockEntity);
        //noinspection ConstantConditions
        blockEntity.getLevel()
                   .setBlock(blockEntity.getBlockPos(),
                             blockEntity.getBlockState().setValue(TerminalBlock.MULTIBLOCK_TYPE, MultiblockType.TARGET),
                             Block.UPDATE_ALL
                   );
        blockEntity.setTargetData(Optional.of(new TargetData(blockEntity)));
    }

    private static void assembleMultiblockPart(TerminalBlockEntity blockEntity) {
        Direction direction = blockEntity.getBlockState().getValue(TerminalBlock.HORIZONTAL_FACING).getOpposite();
        BlockPos frontLeftCorner = blockEntity.getBlockPos()
                                              .relative(direction)
                                              .relative(Rotation.COUNTERCLOCKWISE_90.rotate(direction));
        BlockPos backRightCorner = blockEntity.getBlockPos()
                                              .relative(direction, 3)
                                              .relative(Rotation.CLOCKWISE_90.rotate(direction));
        for (BlockPos pos : BlockPos.betweenClosed(frontLeftCorner, backRightCorner.above(2))) {
            //noinspection ConstantConditions
            BlockState state = blockEntity.getLevel().getBlockState(pos);
            blockEntity.getLevel().setBlock(pos, BlockRegistry.MULTIBLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
            blockEntity.getLevel()
                       .getBlockEntity(pos, BlockEntityRegistry.MULTIBLOCK.get())
                       .ifPresent(multiblockBlockEntity -> {
                           multiblockBlockEntity.setTerminalPos(blockEntity.getBlockPos());
                           multiblockBlockEntity.setSavedBlockState(state);
                       });
        }
    }

    public static void disassemble(TerminalBlockEntity blockEntity) {
        if (blockEntity == null) {
            return;
        }
        if (blockEntity.getLevel() == null) {
            return;
        }
        Direction direction = blockEntity.getBlockState().getValue(TerminalBlock.HORIZONTAL_FACING).getOpposite();
        BlockPos frontLeftCorner = blockEntity.getBlockPos()
                                              .relative(direction)
                                              .relative(Rotation.COUNTERCLOCKWISE_90.rotate(direction));
        BlockPos backRightCorner = blockEntity.getBlockPos()
                                              .relative(direction, 3)
                                              .relative(Rotation.CLOCKWISE_90.rotate(direction));
        for (BlockPos pos : BlockPos.betweenClosed(frontLeftCorner, backRightCorner.above(2))) {
            if (!blockEntity.getLevel().getBlockState(pos).is(BlockRegistry.MULTIBLOCK.get())) {
                continue;
            }
            blockEntity.getLevel()
                       .getBlockEntity(pos, BlockEntityRegistry.MULTIBLOCK.get())
                       .ifPresent(multiblockBlockEntity -> blockEntity.getLevel()
                                                                      .setBlock(pos,
                                                                                multiblockBlockEntity.getSavedBlockState(),
                                                                                Block.UPDATE_ALL
                                                                      ));
        }
        if (!blockEntity.getLevel().getBlockState(blockEntity.getBlockPos()).is(BlockRegistry.TERMINAL.get())) {
            return;
        }
        blockEntity.getLevel()
                   .setBlock(blockEntity.getBlockPos(),
                             blockEntity.getBlockState().setValue(TerminalBlock.MULTIBLOCK_TYPE, MultiblockType.NONE),
                             Block.UPDATE_ALL
                   );
    }
}
