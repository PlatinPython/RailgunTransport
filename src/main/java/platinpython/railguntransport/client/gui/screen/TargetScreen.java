package platinpython.railguntransport.client.gui.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import platinpython.railguntransport.util.network.NetworkHandler;
import platinpython.railguntransport.util.network.packets.TargetUpdateNamePKT;

import java.util.Optional;

public class TargetScreen extends Screen {
    private final BlockPos blockEntityPos;

    private Optional<String> name;

    public TargetScreen(BlockPos blockEntityPos, Optional<String> name) {
        super(TextComponent.EMPTY);
        this.blockEntityPos = blockEntityPos;
        this.name = name;
    }

    @Override
    protected void init() {
        EditBox editBox = new EditBox(this.font, this.width / 2 - 155, this.height / 2 - 10, 310, 20,
                                      new TextComponent("Name:")
        );
        editBox.setResponder(s -> this.name = s.isBlank() ? Optional.empty() : Optional.of(s.trim()));
        this.name.ifPresent(editBox::setValue);
        this.addRenderableWidget(editBox);

        Button button = new Button(this.width / 2 - 25, this.height / 2 + 15, 50, 20, new TextComponent("Confirm"),
                                   b -> {
                                       NetworkHandler.INSTANCE.sendToServer(
                                               new TargetUpdateNamePKT(this.blockEntityPos, this.name));
                                       this.onClose();
                                   }
        );
        this.addRenderableWidget(button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
