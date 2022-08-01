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
import platinpython.railguntransport.block.TerminalBlock;
import platinpython.railguntransport.util.multiblock.MultiblockType;
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
        return new ItemStackHandler() {
            @Override
            protected void onContentsChanged(int slot) {
                TerminalBlockEntity.this.setChanged();
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
            Direction facing;
            if (side == null) {
                facing = Direction.NORTH;
            } else {
                //noinspection ConstantConditions
                facing = this.level.getBlockState(this.worldPosition).getValue(TerminalBlock.HORIZONTAL_FACING);
            }
            if (side == null || side == facing.getCounterClockWise() || side == facing.getClockWise() || side == Direction.DOWN) {
                if (this.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE) != MultiblockType.NONE) {
                    return handler.cast();
                }
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public AABB getRenderBoundingBox() {
        Direction direction = this.getBlockState().getValue(TerminalBlock.HORIZONTAL_FACING).getOpposite();
        return new AABB(this.getBlockPos().relative(direction).relative(direction.getCounterClockWise()),
                        this.getBlockPos().above().relative(direction, 3).relative(direction.getClockWise())
        );
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
            Optional<TerminalBlockEntity> optionalTargetTerminal = level.getBlockEntity(selectedTarget.get(),
                                                                                        BlockEntityRegistry.TERMINAL.get()
            );

            if (optionalTargetTerminal.isPresent()) {
                Optional<IItemHandler> optionalHandler = optionalTargetTerminal.get()
                                                                               .getCapability(
                                                                                       CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                                                                               .resolve();
                if (optionalHandler.isPresent() && optionalHandler.get().getStackInSlot(0).isEmpty()) {
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
                                                  .add(blockEntity.itemHandler.extractItem(0, 1, false)
                                                                              .getOrCreateTag(), senderPos, targetPos,
                                                       level.dimension()
                                                  );

                            optionalTargetData.get().setFree(false);
                        }
                    }
                }
            }
        }
    }
}
