package platinpython.railgun_transport.client.gui.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import platinpython.railgun_transport.util.network.packets.TargetUpdateNamePayload;

import java.util.Optional;

public class TargetScreen extends Screen {
    private final BlockPos blockEntityPos;

    private Optional<String> name;

    public TargetScreen(BlockPos blockEntityPos, Optional<String> name) {
        super(Component.empty());
        this.blockEntityPos = blockEntityPos;
        this.name = name;
    }

    @Override
    protected void init() {
        EditBox editBox =
            new EditBox(this.font, this.width / 2 - 155, this.height / 2 - 10, 310, 20, Component.literal("Name:"));
        editBox.setResponder(s -> this.name = s.isBlank() ? Optional.empty() : Optional.of(s.trim()));
        this.name.ifPresent(editBox::setValue);
        this.addRenderableWidget(editBox);

        Button button = Button.builder(Component.literal("Confirm"), b -> {
            PacketDistributor.sendToServer(new TargetUpdateNamePayload(this.blockEntityPos, this.name));
            this.onClose();
        }).bounds(this.width / 2 - 25, this.height / 2 + 15, 50, 20).build();
        this.addRenderableWidget(button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
