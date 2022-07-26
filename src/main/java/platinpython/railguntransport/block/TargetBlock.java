package platinpython.railguntransport.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import platinpython.railguntransport.block.entity.TargetBlockEntity;
import platinpython.railguntransport.util.network.NetworkHandler;
import platinpython.railguntransport.util.network.packets.TargetScreenOpenPKT;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;
import platinpython.railguntransport.util.saveddata.TargetSavedData;

public class TargetBlock extends BaseEntityBlock {
    public TargetBlock() {
        super(Properties.of(Material.HEAVY_METAL)
                        .isValidSpawn((state, blockGetter, pos, entityType) -> false)
                        .sound(SoundType.NETHERITE_BLOCK)
                        .strength(50F, 1200F));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.TARGET.get().create(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
                            ItemStack stack) {
        if (level instanceof ServerLevel serverLevel) {
            TargetSavedData.get(serverLevel.getDataStorage()).add(pos, serverLevel);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        if (level instanceof ServerLevel serverLevel) {
            TargetSavedData.get(serverLevel.getDataStorage()).remove(pos);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player.isSpectator()) {
            return InteractionResult.CONSUME;
        }
        if (level.getBlockEntity(pos) instanceof TargetBlockEntity targetBlockEntity) {
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                                         new TargetScreenOpenPKT(pos, targetBlockEntity.getName())
            );
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
