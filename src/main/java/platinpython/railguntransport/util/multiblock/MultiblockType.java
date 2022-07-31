package platinpython.railguntransport.util.multiblock;

import net.minecraft.util.StringRepresentable;

public enum MultiblockType implements StringRepresentable {
    NONE("none"),
    RAILGUN("railgun"),
    TARGET("target");

    private final String name;

    MultiblockType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
