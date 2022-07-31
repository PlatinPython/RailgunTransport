package platinpython.railguntransport.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import platinpython.railguntransport.block.entity.TerminalBlockEntity;
import platinpython.railguntransport.util.multiblock.MultiblockHelper;
import platinpython.railguntransport.util.multiblock.MultiblockType;
import platinpython.railguntransport.util.network.NetworkHandler;
import platinpython.railguntransport.util.network.packets.RailgunScreenOpenPKT;
import platinpython.railguntransport.util.network.packets.TargetScreenOpenPKT;
import platinpython.railguntransport.util.network.packets.TerminalScreenOpenPKT;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;
import platinpython.railguntransport.util.saveddata.TargetSavedData;

public class TerminalBlock extends BaseEntityBlock {
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<MultiblockType> MULTIBLOCK_TYPE = EnumProperty.create("type",
                                                                                           MultiblockType.class
    );

    public TerminalBlock() {
        super(Properties.of(Material.METAL)
                        .isValidSpawn((state, blockGetter, pos, entityType) -> false)
                        .sound(SoundType.METAL)
                        .strength(5F, 6F)
                        .noOcclusion());
        this.registerDefaultState(this.getStateDefinition()
                                      .any()
                                      .setValue(HORIZONTAL_FACING, Direction.NORTH)
                                      .setValue(MULTIBLOCK_TYPE, MultiblockType.NONE));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(HORIZONTAL_FACING, rotation.rotate(state.getValue(HORIZONTAL_FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(HORIZONTAL_FACING, mirror.mirror(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING).add(MULTIBLOCK_TYPE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.TERMINAL.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> blockEntityType) {
        return level.isClientSide ?
               null :
               createTickerHelper(blockEntityType, BlockEntityRegistry.TERMINAL.get(), TerminalBlockEntity::tick);
    }

    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getValue(MULTIBLOCK_TYPE) == MultiblockType.TARGET) {
            if (level instanceof ServerLevel serverLevel) {
                TargetSavedData.get(serverLevel.getDataStorage()).add(pos, serverLevel);
            }
        }
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            level.getBlockEntity(pos, BlockEntityRegistry.TERMINAL.get()).ifPresent(MultiblockHelper::disassemble);
        }
        super.onRemove(state, level, pos, newState, isMoving);
        if (state.getValue(MULTIBLOCK_TYPE) == MultiblockType.TARGET) {
            if (level instanceof ServerLevel serverLevel) {
                TargetSavedData.get(serverLevel.getDataStorage()).remove(pos);
            }
        }
    }

    @SuppressWarnings({"deprecation", "InstantiationOfUtilityClass"})
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player.isSpectator()) {
            return InteractionResult.CONSUME;
        }
        if (player.isShiftKeyDown()) {
            level.getBlockEntity(pos, BlockEntityRegistry.TERMINAL.get()).ifPresent(MultiblockHelper::tryAssemble);
            return InteractionResult.CONSUME;
        }
        if (level.getBlockEntity(pos) instanceof TerminalBlockEntity terminalBlockEntity) {
            switch (state.getValue(MULTIBLOCK_TYPE)) {
                case RAILGUN -> {
                    if (terminalBlockEntity.getRailgunData().isPresent()) {
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                                                     new RailgunScreenOpenPKT(pos, TargetSavedData.get(
                                                                                                          ((ServerLevel) level).getDataStorage())
                                                                                                  .getReachablePositions(
                                                                                                          pos),
                                                                              terminalBlockEntity.getRailgunData()
                                                                                                 .get()
                                                                                                 .getSelectedTarget()
                                                     )
                        );
                    }
                }
                case TARGET -> {
                    if (terminalBlockEntity.getTargetData().isPresent()) {
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                                                     new TargetScreenOpenPKT(pos, terminalBlockEntity.getTargetData()
                                                                                                     .get()
                                                                                                     .getName())
                        );
                    }
                }
                case NONE -> NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                                                          new TerminalScreenOpenPKT()
                );
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
