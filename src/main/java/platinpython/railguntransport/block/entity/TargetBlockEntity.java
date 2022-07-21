package platinpython.railguntransport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.railguntransport.util.TargetSavedData;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;

import java.util.Optional;

public class TargetBlockEntity extends BlockEntity {
    private boolean isFree = true;
    private Optional<String> name = Optional.empty();

    public TargetBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.TARGET.get(), worldPosition, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isFree", this.isFree);
        this.name.ifPresent(name -> tag.putString("name", name));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("isFree")) {
            this.isFree = tag.getBoolean("isFree");
        }
        if (tag.contains("name")) {
            this.name = Optional.of(tag.getString("name"));
        }
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        this.isFree = free;
        this.setChanged();
    }

    public Optional<String> getName() {
        return name;
    }

    public void setName(Optional<String> name) {
        this.name = name;
        if (this.level instanceof ServerLevel serverLevel) {
            TargetSavedData.get(serverLevel.getDataStorage()).add(this.worldPosition, serverLevel);
        }
        this.setChanged();
    }
}
