package platinpython.railguntransport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.block.TerminalBlock;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;
import platinpython.railguntransport.util.registries.BlockRegistry;
import platinpython.railguntransport.util.saveddata.MovingCapsuleSavedData;

import java.util.Optional;

public class TerminalBlockEntity extends BlockEntity {
    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    private Optional<RailgunData> railgunData = Optional.empty();
    private Optional<TargetData> targetData = Optional.empty();

    public TerminalBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.TERMINAL.get(), worldPosition, blockState);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        handler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
        switch (this.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE)) {
            case RAILGUN -> {
                if (this.railgunData.isPresent()) {
                    CompoundTag compoundTag = new CompoundTag();
                    this.railgunData.get().saveAdditional(compoundTag);
                    tag.put("RailgunData", compoundTag);
                }
            }
            case TARGET -> {
                if (this.targetData.isPresent()) {
                    CompoundTag compoundTag = new CompoundTag();
                    this.targetData.get().saveAdditional(compoundTag);
                    tag.put("TargetData", compoundTag);
                }
            }
            case NONE -> {
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        }
        switch (this.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE)) {
            case RAILGUN -> {
                if (tag.contains("RailgunData")) {
                    if (this.railgunData.isEmpty()) {
                        this.railgunData = Optional.of(new RailgunData(this));
                    }
                    this.railgunData.get().load(tag.getCompound("RailgunData"));
                }
            }
            case TARGET -> {
                if (tag.contains("TargetData")) {
                    if (this.targetData.isEmpty()) {
                        this.targetData = Optional.of(new TargetData(this));
                    }
                    this.targetData.get().load(tag.getCompound("TargetData"));
                }
            }
            case NONE -> {
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return switch (this.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE)) {
            case RAILGUN -> {
                if (this.railgunData.isPresent()) {
                    yield this.railgunData.get().getUpdateTag();
                } else {
                    yield new CompoundTag();
                }
            }
            case TARGET, NONE -> new CompoundTag();
        };
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        switch (this.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE)) {
            case RAILGUN -> {
                if (this.railgunData.isEmpty()) {
                    this.railgunData = Optional.of(new RailgunData(this));
                }
                this.railgunData.get().handleUpdateTag(tag);
            }
            case TARGET, NONE -> {
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            this.handleUpdateTag(tag);
        }
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.getItem() == BlockRegistry.CAPSULE.get().asItem();
            }
        };
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            //noinspection ConstantConditions
            Direction facing = this.level.getBlockState(this.worldPosition).getValue(TerminalBlock.HORIZONTAL_FACING);
            if (side == null || side == facing.getCounterClockWise() || side == facing || side == facing.getClockWise()) {
                return handler.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(this.getBlockPos().offset(-1, 0, -1), this.getBlockPos().offset(1, 2, 1));
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

    public void tryCapsuleSend() {
        switch (this.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE)) {
            case RAILGUN -> this.railgunData.ifPresent(data -> data.getSelectedTarget()
                                                                   .ifPresentOrElse(
                                                                           blockPos -> RailgunTransport.LOGGER.info(
                                                                                   "Sending capsule to {}.",
                                                                                   blockPos.toShortString()
                                                                           ), () -> RailgunTransport.LOGGER.info(
                                                                                   "Not sending capsule.")));
            case TARGET -> RailgunTransport.LOGGER.info("Not sending capsule because is target.");
            case NONE -> RailgunTransport.LOGGER.info("Not sending capsule because is none.");
        }
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, TerminalBlockEntity blockEntity) {
        if (blockEntity.railgunData.isPresent() && blockEntity.railgunData.get()
                                                                          .getSelectedTarget()
                                                                          .isPresent() && level instanceof ServerLevel serverLevel) {
            MovingCapsuleSavedData.get(serverLevel.getDataStorage())
                                  .add(new CompoundTag(), blockEntity.getBlockPos().offset(0, 1, 0),
                                       blockEntity.railgunData.get().getSelectedTarget().get(), level.dimension()
                                  );
        }
    }
}
