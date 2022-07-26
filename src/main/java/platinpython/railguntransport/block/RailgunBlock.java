package platinpython.railguntransport.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import platinpython.railguntransport.block.entity.RailgunBlockEntity;
import platinpython.railguntransport.util.network.NetworkHandler;
import platinpython.railguntransport.util.network.packets.RailgunScreenOpenPKT;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;
import platinpython.railguntransport.util.saveddata.TargetSavedData;

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
        if (level.getBlockEntity(pos) instanceof RailgunBlockEntity railgunBlockEntity) {
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                                         new RailgunScreenOpenPKT(pos, TargetSavedData.get(
                                                 ((ServerLevel) level).getDataStorage()).getReachablePositions(pos),
                                                                  railgunBlockEntity.getSelectedTarget()
                                         )
            );
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
