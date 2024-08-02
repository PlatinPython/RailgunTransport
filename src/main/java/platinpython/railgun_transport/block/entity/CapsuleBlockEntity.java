package platinpython.railgun_transport.block.entity;

import com.google.common.collect.Streams;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jspecify.annotations.Nullable;
import platinpython.railgun_transport.block.CapsuleBlock;
import platinpython.railgun_transport.menu.CapsuleMenu;
import platinpython.railgun_transport.util.registries.BlockEntityRegistry;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CapsuleBlockEntity extends BlockEntity implements MenuProvider, Nameable {
    public static final int CONTAINER_SIZE = 27;

    private final ItemStackHandler itemHandler = createHandler();
    private LockCode lockKey = LockCode.NO_LOCK;
    @Nullable
    private Component name;

    public CapsuleBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.CAPSULE.get(), worldPosition, blockState);
    }

    // @Override
    // public void invalidateCaps() {
    // super.invalidateCaps();
    // handler.invalidate();
    // }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Items", itemHandler.serializeNBT(registries));
        this.lockKey.addToTag(tag);
        if (this.name != null) {
            tag.putString("custom_name", Component.Serializer.toJson(this.name, registries));
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Items", Tag.TAG_COMPOUND)) {
            itemHandler.deserializeNBT(registries, tag.getCompound("Items"));
        }
        this.lockKey = LockCode.fromTag(tag);
        if (tag.contains("custom_name", Tag.TAG_STRING)) {
            this.name = parseCustomNameSafe(tag.getString("custom_name"), registries);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.name = componentInput.get(DataComponents.CUSTOM_NAME);
        this.lockKey = componentInput.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
        // There is no forEachWithIndex
        Streams.mapWithIndex(
            Streams
                .concat(
                    componentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).stream(),
                    Stream.generate(() -> ItemStack.EMPTY)
                ).limit(this.itemHandler.getSlots()),
            (stack, i) -> {
                this.itemHandler.setStackInSlot((int) i, stack);
                return null;
            }
        );
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, this.name);
        if (!this.lockKey.equals(LockCode.NO_LOCK)) {
            components.set(DataComponents.LOCK, this.lockKey);
        }
        components.set(
            DataComponents.CONTAINER,
            ItemContainerContents.fromItems(
                IntStream.range(0, this.itemHandler.getSlots()).mapToObj(this.itemHandler::getStackInSlot).toList()
            )
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("custom_name");
        tag.remove("Lock");
        tag.remove("Inventory");
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(CONTAINER_SIZE) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.getItem().canFitInsideContainerItems();
            }
        };
    }

    @Nullable
    public IItemHandler getItemHandler(@Nullable Direction side) {
        if (side == null) {
            return this.itemHandler;
        }
        Direction facing = this.getBlockState().getValue(CapsuleBlock.FACING);
        if (side == facing || side == facing.getOpposite()) {
            return this.itemHandler;
        }
        return null;
    }

    @Override
    public Component getName() {
        return this.name != null ? this.name : Component.translatable("block.railgun_transport.capsule");
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        if (!BaseContainerBlockEntity.canUnlock(player, lockKey, this.getDisplayName())) {
            return null;
        }
        if (this.level == null) {
            return null;
        }
        return new CapsuleMenu(
            containerId, playerInventory, this.itemHandler, ContainerLevelAccess.create(level, this.getBlockPos())
        );
    }
}
