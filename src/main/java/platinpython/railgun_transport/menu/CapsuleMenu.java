package platinpython.railgun_transport.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import platinpython.railgun_transport.block.entity.CapsuleBlockEntity;
import platinpython.railgun_transport.util.registries.BlockRegistry;
import platinpython.railgun_transport.util.registries.MenuTypeRegistry;

public class CapsuleMenu extends AbstractContainerMenu {
    private final IItemHandler playerInventory;
    private final ContainerLevelAccess access;

    public CapsuleMenu(int containerId, Inventory playerInventory) {
        this(
            containerId, playerInventory, new ItemStackHandler(CapsuleBlockEntity.CONTAINER_SIZE),
            ContainerLevelAccess.NULL
        );
    }

    public CapsuleMenu(int containerId, Inventory playerInventory, IItemHandler handler, ContainerLevelAccess access) {
        super(MenuTypeRegistry.CAPSULE.get(), containerId);
        this.playerInventory = new InvWrapper(playerInventory);
        this.access = access;
        this.addSlotBox(handler, 0, 18);
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
        return AbstractContainerMenu.stillValid(this.access, player, BlockRegistry.CAPSULE.get());
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
        addSlotBox(this.playerInventory, 9, 84);
        addSlotRange(this.playerInventory, 0, 142);
    }
}
