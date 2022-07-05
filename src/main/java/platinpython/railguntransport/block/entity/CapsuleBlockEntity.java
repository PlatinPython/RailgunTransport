package platinpython.railguntransport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;
import platinpython.railguntransport.block.CapsuleBlock;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;

public class CapsuleBlockEntity extends BlockEntity {
    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<IItemHandler> noInteractionHandler = LazyOptional.of(this::createNoInteractionHandler);

    public CapsuleBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.CAPSULE.get(), worldPosition, blockState);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        handler.invalidate();
        noInteractionHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        }
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(27) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    private IItemHandler createNoInteractionHandler() {
        return new CombinedInvWrapper(itemHandler) {
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return stack;
            }
        };
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            Direction facing = this.level.getBlockState(this.worldPosition).getValue(CapsuleBlock.FACING);
            if (side == facing || side == facing.getOpposite()) {
                return handler.cast();
            } else {
                return noInteractionHandler.cast();
            }
        }
        return super.getCapability(cap, side);
    }
}
