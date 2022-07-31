package platinpython.railguntransport.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import platinpython.railguntransport.util.saveddata.TargetSavedData;

import java.util.Optional;

public class TargetData {
    private final TerminalBlockEntity parent;

    private boolean isFree = true;
    private Optional<String> name = Optional.empty();

    public TargetData(TerminalBlockEntity parent) {
        this.parent = parent;
    }

    protected void saveAdditional(CompoundTag tag) {
        tag.putBoolean("IsFree", this.isFree);
        this.name.ifPresent(name -> tag.putString("Name", name));
    }

    public void load(CompoundTag tag) {
        if (tag.contains("IsFree")) {
            this.isFree = tag.getBoolean("IsFree");
        }
        if (tag.contains("Name")) {
            this.name = Optional.of(tag.getString("Name"));
        }
    }

    @SuppressWarnings("unused")
    public boolean isFree() {
        return isFree;
    }

    @SuppressWarnings("unused")
    public void setFree(boolean free) {
        this.isFree = free;
        this.parent.setChanged();
    }

    public Optional<String> getName() {
        return name;
    }

    public void setName(Optional<String> name) {
        this.name = name;
        if (this.parent.getLevel() instanceof ServerLevel serverLevel) {
            TargetSavedData.get(serverLevel.getDataStorage()).add(this.parent.getBlockPos(), serverLevel);
        }
        this.parent.setChanged();
    }
}
