package platinpython.railgun_transport.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jspecify.annotations.Nullable;
import platinpython.railgun_transport.RailgunTransport;
import platinpython.railgun_transport.block.entity.CapsuleBlockEntity;
import platinpython.railgun_transport.util.registries.BlockEntityRegistry;
import platinpython.railgun_transport.util.registries.ItemRegistry;

import java.util.List;

public class CapsuleBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final ResourceLocation CONTENTS =
        ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "contents");

    public CapsuleBlock() {
        super(
            Properties.of()
                .isValidSpawn((state, blockGetter, pos, entityType) -> false)
                .sound(SoundType.METAL)
                .strength(5F, 6F)
                .noOcclusion()
        );
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.UP));
    }

    @SuppressWarnings("deprecation")
    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.CAPSULE.get().create(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        // noinspection DataFlowIssue
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hit
    ) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player.isSpectator()) {
            return InteractionResult.CONSUME;
        }
        if (level.getBlockEntity(pos) instanceof CapsuleBlockEntity capsuleBlockEntity) {
            player.openMenu(capsuleBlockEntity, buffer -> buffer.writeBlockPos(pos));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        level.getBlockEntity(pos, BlockEntityRegistry.CAPSULE.get()).ifPresent(capsuleBlockEntity -> {
            // noinspection DataFlowIssue
            IItemHandler items =
                level.getCapability(Capabilities.ItemHandler.BLOCK, pos, state, capsuleBlockEntity, null);
            if (items == null) {
                return;
            }
            boolean empty = true;
            for (int i = 0; i < items.getSlots(); i++) {
                if (!items.getStackInSlot(i).isEmpty()) {
                    empty = false;
                }
            }
            if (!level.isClientSide() && player.isCreative() && !empty) {
                ItemStack stack = ItemRegistry.CAPSULE.toStack();
                capsuleBlockEntity.saveToItem(stack, level.registryAccess());
                ItemEntity itemEntity =
                    new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        });
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof CapsuleBlockEntity capsuleBlockEntity) {
            // noinspection DataFlowIssue
            IItemHandler items = params.getLevel()
                .getCapability(
                    Capabilities.ItemHandler.BLOCK, capsuleBlockEntity.getBlockPos(), state, capsuleBlockEntity, null
                );
            if (items != null) {
                params = params.withDynamicDrop(CONTENTS, stackConsumer -> {
                    for (int i = 0; i < items.getSlots(); i++) {
                        stackConsumer.accept(items.getStackInSlot(i));
                    }
                });
            }
        }
        return super.getDrops(state, params);
    }

    @Override
    public void appendHoverText(
        ItemStack stack,
        Item.TooltipContext context,
        List<Component> tooltipComponents,
        TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int displayedCount = 0;
        int actualCount = 0;
        for (
            ItemStack item : stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems()
        ) {
            actualCount++;
            if (displayedCount <= 4) {
                displayedCount++;
                tooltipComponents.add(
                    Component.translatable("container.shulkerBox.itemCount", item.getHoverName(), item.getCount())
                );
            }
        }

        if (actualCount - displayedCount > 0) {
            tooltipComponents.add(
                Component.translatable("container.shulkerBox.more", actualCount - displayedCount)
                    .withStyle(ChatFormatting.ITALIC)
            );
        }
    }
}
