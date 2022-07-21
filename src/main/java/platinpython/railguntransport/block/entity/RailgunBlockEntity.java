package platinpython.railguntransport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;

import java.util.Optional;

public class RailgunBlockEntity extends BlockEntity {
    private Optional<BlockPos> selectedTarget = Optional.empty();

    public RailgunBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.RAILGUN.get(), worldPosition, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.selectedTarget.ifPresent(blockPos -> tag.put("selectedTarget", NbtUtils.writeBlockPos(blockPos)));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("selectedTarget")) {
            this.selectedTarget = Optional.of(NbtUtils.readBlockPos(tag.getCompound("selectedTarget")));
        }
    }

    public Optional<BlockPos> getSelectedTarget() {
        return this.selectedTarget;
    }

    public void setSelectedTarget(Optional<BlockPos> selectedTarget) {
        this.selectedTarget = selectedTarget;
        this.setChanged();
    }

    public void tryCapsuleSend() {
        this.selectedTarget.ifPresentOrElse(
                blockPos -> RailgunTransport.LOGGER.info("Sending capsule to {}.", blockPos.toShortString()),
                () -> RailgunTransport.LOGGER.info("Not sending capsule.")
        );
    }
}
