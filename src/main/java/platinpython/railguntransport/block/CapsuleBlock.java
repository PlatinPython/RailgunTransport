package platinpython.railguntransport.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import platinpython.railguntransport.block.entity.CapsuleBlockEntity;
import platinpython.railguntransport.menu.CapsuleMenu;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;
import platinpython.railguntransport.util.registries.BlockRegistry;

import java.util.List;
import java.util.Optional;

public class CapsuleBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public CapsuleBlock() {
        super(Properties.of(Material.METAL)
                        .isValidSpawn((state, blockGetter, pos, entityType) -> false)
                        .sound(SoundType.METAL)
                        .strength(5F, 6F)
                        .noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.UP));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
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

    @SuppressWarnings("deprecation")
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
        if (level.getBlockEntity(pos) instanceof CapsuleBlockEntity) {
            MenuProvider menuProvider = new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return new TranslatableComponent("block.railguntransport.capsule");
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                    return new CapsuleMenu(containerId, pos, playerInventory, player);
                }
            };
            NetworkHooks.openGui((ServerPlayer) player, menuProvider, pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        Optional<CapsuleBlockEntity> blockEntity = level.getBlockEntity(pos, BlockEntityRegistry.CAPSULE.get());
        if (blockEntity.isPresent()) {
            Optional<IItemHandler> items = blockEntity.get()
                                                      .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                                                      .resolve();
            if (items.isPresent()) {
                boolean empty = true;
                for (int i = 0; i < items.get().getSlots(); i++) {
                    if (!items.get().getStackInSlot(i).isEmpty()) {
                        empty = false;
                    }
                }
                if (!level.isClientSide && player.isCreative() && !empty) {
                    ItemStack stack = new ItemStack(BlockRegistry.CAPSULE.get().asItem());
                    blockEntity.get().saveToItem(stack);

                    ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D,
                                                           pos.getZ() + 0.5D, stack
                    );
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof CapsuleBlockEntity capsuleBlockEntity) {
            Optional<IItemHandler> items = capsuleBlockEntity.getCapability(
                    CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
            if (items.isPresent()) {
                builder = builder.withDynamicDrop(ShulkerBoxBlock.CONTENTS, (lootContext, stackConsumer) -> {
                    for (int i = 0; i < items.get().getSlots(); i++) {
                        stackConsumer.accept(items.get().getStackInSlot(i));
                    }
                });
            }
        }
        return super.getDrops(state, builder);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip,
                                TooltipFlag flag) {
        CompoundTag compoundTag = BlockItem.getBlockEntityData(stack);
        if (compoundTag != null) {
            if (compoundTag.contains("Inventory", Tag.TAG_COMPOUND)) {
                NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
                ContainerHelper.loadAllItems(compoundTag.getCompound("Inventory"), items);
                int displayedCount = 0;
                int actualCount = 0;

                for (ItemStack item : items) {
                    if (!item.isEmpty()) {
                        actualCount++;
                        if (displayedCount <= 4) {
                            displayedCount++;
                            MutableComponent itemName = item.getHoverName().copy();
                            itemName.append(" x").append(String.valueOf(item.getCount()));
                            tooltip.add(itemName);
                        }
                    }
                }

                if (actualCount - displayedCount > 0) {
                    tooltip.add((new TranslatableComponent("container.shulkerBox.more",
                                                           actualCount - displayedCount
                    )).withStyle(ChatFormatting.ITALIC));
                }
            }
        }
    }
}
