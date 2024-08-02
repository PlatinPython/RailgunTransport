package platinpython.railgun_transport.client.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import platinpython.railgun_transport.client.gui.widget.RailgunTargetList;
import platinpython.railgun_transport.util.network.packets.RailgunUpdateSelectedTargetPayload;

import java.util.Map;
import java.util.Optional;

public class RailgunScreen extends Screen {
    private final BlockPos blockEntityPos;
    private final Map<BlockPos, Optional<String>> possibleTargets;

    private Optional<BlockPos> selectedTarget;

    @SuppressWarnings("NotNullFieldNotInitialized")
    private RailgunTargetList targetList;

    public RailgunScreen(
        BlockPos blockEntityPos,
        Map<BlockPos, Optional<String>> possibleTargets,
        Optional<BlockPos> selectedTarget
    ) {
        super(Component.empty());
        this.blockEntityPos = blockEntityPos;
        this.possibleTargets = possibleTargets;
        this.selectedTarget = selectedTarget;
    }

    @Override
    protected void init() {
        if (this.minecraft == null) {
            return;
        }

        this.targetList = new RailgunTargetList(this.minecraft, this.width, this.height, 32, 25);

        this.possibleTargets.forEach((pos, name) -> this.targetList.addTarget(pos, name, button -> {
            this.selectedTarget = Optional.of(button.pos);
            PacketDistributor
                .sendToServer(new RailgunUpdateSelectedTargetPayload(this.blockEntityPos, this.selectedTarget));
        }));

        this.addRenderableWidget(this.targetList);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
