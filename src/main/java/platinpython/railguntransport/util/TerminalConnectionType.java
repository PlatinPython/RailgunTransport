package platinpython.railguntransport.util;

import net.minecraft.util.StringRepresentable;

public enum TerminalConnectionType implements StringRepresentable {
    NONE("none"),
    LEFT("left"),
    RIGHT("right"),
    BOTH("both");

    private final String name;

    TerminalConnectionType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public TerminalConnectionType toggleBoth() {
        return switch (this) {
            case NONE -> BOTH;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case BOTH -> NONE;
        };
    }

    public TerminalConnectionType activateLeft() {
        return switch (this) {
            case NONE, LEFT -> LEFT;
            case RIGHT, BOTH -> BOTH;
        };
    }

    public TerminalConnectionType deactivateLeft() {
        return switch (this) {
            case NONE, LEFT -> NONE;
            case RIGHT, BOTH -> RIGHT;
        };
    }

    public TerminalConnectionType activateRight() {
        return switch (this) {
            case NONE, RIGHT -> RIGHT;
            case LEFT, BOTH -> BOTH;
        };
    }

    public TerminalConnectionType deactivateRight() {
        return switch (this) {
            case NONE, RIGHT -> NONE;
            case LEFT, BOTH -> LEFT;
        };
    }
}
