package platinpython.railguntransport.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import platinpython.railguntransport.util.registries.BlockRegistry;
import platinpython.railguntransport.util.registries.MenuTypeRegistry;

public class CapsuleMenu extends AbstractContainerMenu {
    private final BlockEntity blockEntity;
    private final Player player;
    private final IItemHandler playerInventory;

    public CapsuleMenu(int containerId, BlockPos pos, Inventory playerInventory, Player player) {
        super(MenuTypeRegistry.CAPSULE.get(), containerId);
        this.blockEntity = player.getCommandSenderWorld().getBlockEntity(pos);
        this.player = player;
        this.playerInventory = new InvWrapper(playerInventory);

        if (blockEntity != null) {
            blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                       .ifPresent(h -> addSlotBox(h, 0, 18));
        }
        layoutPlayerInventorySlots();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemStack = stack.copy();
            if (index < 27) {
                if (!this.moveItemStackTo(stack, 27, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, 27, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos()),
                          this.player, BlockRegistry.CAPSULE.get()
        );
    }

    private int addSlotRange(IItemHandler handler, int index, int y) {
        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(handler, index + i, 8 + i * 18, y));
        }
        return index + 9;
    }

    private void addSlotBox(IItemHandler handler, int index, int y) {
        for (int i = 0; i < 3; i++) {
            index = addSlotRange(handler, index, y + i * 18);
        }
    }

    private void layoutPlayerInventorySlots() {
        addSlotBox(this.playerInventory, 9, 85);
        addSlotRange(this.playerInventory, 0, 143);
    }
}
