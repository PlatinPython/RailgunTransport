package platinpython.railguntransport.client.gui.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class TargetButton extends Button {
    public final BlockPos pos;
    private final OnPress onPress;

    public TargetButton(int x, int y, int width, int height, Component message, OnPress onPress, BlockPos pos) {
        super(x, y, width, height, message, b -> {});
        this.pos = pos;
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    public interface OnPress {
        void onPress(TargetButton button);
    }
}
