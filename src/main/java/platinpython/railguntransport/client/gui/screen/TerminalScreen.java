package platinpython.railguntransport.client.gui.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

public class TerminalScreen extends Screen {
    public TerminalScreen() {
        super(TextComponent.EMPTY);
    }

    @Override
    protected void init() {
        this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 2 - 23, 310, 20, new TextComponent("Railgun"), b -> {}));
        this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 2 + 3, 310, 20, new TextComponent("Target"), b -> {}));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
