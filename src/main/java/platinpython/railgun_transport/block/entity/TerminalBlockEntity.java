package platinpython.railgun_transport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jspecify.annotations.Nullable;
import platinpython.railgun_transport.block.TerminalBlock;
import platinpython.railgun_transport.util.multiblock.MultiblockType;
import platinpython.railgun_transport.util.registries.BlockEntityRegistry;
import platinpython.railgun_transport.util.registries.ItemRegistry;
import platinpython.railgun_transport.util.saveddata.MovingCapsuleSavedData;

import java.util.Objects;
import java.util.Optional;

public class TerminalBlockEntity extends BlockEntity {
    private final ItemStackHandler itemHandler = createHandler();

    private Optional<RailgunData> railgunData = Optional.empty();
    private Optional<TargetData> targetData = Optional.empty();

    public TerminalBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.TERMINAL.get(), worldPosition, blockState);
    }

    // @Override
    // public void invalidateCaps() {
    // super.invalidateCaps();
    // handler.invalidate();
    // }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", this.itemHandler.serializeNBT(registries));
        switch (this.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE)) {
            case RAILGUN ->
                this.railgunData.flatMap(RailgunData::saveToTag).ifPresent(data -> tag.put("railgun_data", data));
            case TARGET ->
                this.targetData.flatMap(TargetData::saveToTag).ifPresent(data -> tag.put("target_data", data));
            case NONE -> {}
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            this.itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        }
        switch (this.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE)) {
            case RAILGUN -> {
                if (tag.contains("railgun_data")) {
                    this.railgunData
                        .ifPresentOrElse(
                            data -> data.load(
                                Objects
                                    .requireNonNull(tag.get("railgun_data"), "null after CompoundTag#contains check.")
                            ), () -> this.railgunData = Optional.of(new RailgunData(this))
                        );
                }
            }
            case TARGET -> {
                if (tag.contains("target_data")) {
                    this.targetData.ifPresentOrElse(
                        data -> data.load(
                            Objects.requireNonNull(tag.get("target_data"), "null after CompoundTag#contains check.")
                        ), () -> this.targetData = Optional.of(new TargetData(this))
                    );
                }
            }
            case NONE -> {}
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return switch (this.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE)) {
            case RAILGUN -> this.railgunData.map(RailgunData::getUpdateTag).orElse(new CompoundTag());
            case TARGET -> this.targetData.map(TargetData::getUpdateTag).orElse(new CompoundTag());
            case NONE -> new CompoundTag();
        };
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        switch (this.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE)) {
            case RAILGUN -> {
                if (this.railgunData.isEmpty()) {
                    this.railgunData = Optional.of(new RailgunData(this));
                }
                this.railgunData.get().handleUpdateTag(tag);
            }
            case TARGET -> {
                if (this.targetData.isEmpty()) {
                    this.targetData = Optional.of(new TargetData(this));
                }
                this.targetData.get().handleUpdateTag(tag);
            }
            case NONE -> {}
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        this.handleUpdateTag(tag, registries);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler() {
            @Override
            protected void onContentsChanged(int slot) {
                TerminalBlockEntity.this.setChanged();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.is(ItemRegistry.CAPSULE.get());
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (TerminalBlockEntity.this.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE)
                    != MultiblockType.RAILGUN) {
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (TerminalBlockEntity.this.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE)
                    != MultiblockType.TARGET) {
                    return ItemStack.EMPTY;
                }
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    @Nullable
    public IItemHandler getItemHandler(@Nullable Direction side) {
        if (this.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE) == MultiblockType.NONE) {
            return null;
        }
        if (side == null) {
            return this.itemHandler;
        }
        Direction facing = this.getBlockState().getValue(TerminalBlock.HORIZONTAL_FACING);
        if (side == facing.getCounterClockWise() || side == facing.getClockWise() || side == Direction.DOWN) {
            return this.itemHandler;
        }
        return null;
    }

    public Optional<RailgunData> getRailgunData() {
        return railgunData;
    }

    public void setRailgunData(Optional<RailgunData> railgunData) {
        this.railgunData = railgunData;
        this.setChanged();
    }

    public Optional<TargetData> getTargetData() {
        return targetData;
    }

    public void setTargetData(Optional<TargetData> targetData) {
        this.targetData = targetData;
        this.setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TerminalBlockEntity blockEntity) {
        if (state.getValue(TerminalBlock.MULTIBLOCK_TYPE) != MultiblockType.RAILGUN) {
            return;
        }
        Optional<BlockPos> selectedTarget = blockEntity.railgunData.flatMap(RailgunData::getSelectedTarget);
        if (selectedTarget.isEmpty()) {
            return;
        }
        if (level instanceof ServerLevel serverLevel) {
            Optional<TerminalBlockEntity> optionalTargetTerminal =
                level.getBlockEntity(selectedTarget.get(), BlockEntityRegistry.TERMINAL.get());

            if (optionalTargetTerminal.isPresent()) {
                IItemHandler optionalHandler = optionalTargetTerminal.get().getItemHandler(null);
                if (optionalHandler != null && optionalHandler.getStackInSlot(0).isEmpty()) {
                    Optional<TargetData> optionalTargetData = optionalTargetTerminal.get().getTargetData();
                    if (optionalTargetData.isPresent() && optionalTargetData.get().isFree()) {
                        Direction senderDirection = state.getValue(TerminalBlock.HORIZONTAL_FACING).getOpposite();
                        BlockPos senderPos = pos.above().relative(senderDirection, 2);

                        Direction targetDirection = level.getBlockState(selectedTarget.get())
                            .getValue(TerminalBlock.HORIZONTAL_FACING)
                            .getOpposite();
                        BlockPos targetPos = selectedTarget.get().above().relative(targetDirection, 2);

                        if (!blockEntity.itemHandler.extractItem(0, 1, true).isEmpty()) {
                            MovingCapsuleSavedData.get(serverLevel.getDataStorage())
                                .add(
                                    blockEntity.itemHandler.extractItem(0, 1, false), senderPos, targetPos, serverLevel
                                );

                            optionalTargetData.get().setFree(false);
                            optionalTargetData.get().setOrigin(Optional.of(senderPos));
                        }
                    }
                }
            }
        }
    }
}
