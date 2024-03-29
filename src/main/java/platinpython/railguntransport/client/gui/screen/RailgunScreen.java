package platinpython.railguntransport.client.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import platinpython.railguntransport.client.gui.widget.RailgunTargetList;
import platinpython.railguntransport.util.network.NetworkHandler;
import platinpython.railguntransport.util.network.packets.RailgunUpdateSelectedTargetPKT;

import java.util.Map;
import java.util.Optional;

public class RailgunScreen extends Screen {
    private final BlockPos blockEntityPos;
    private final Map<BlockPos, Optional<String>> possibleTargets;

    private Optional<BlockPos> selectedTarget;

    private RailgunTargetList targetList;

    public RailgunScreen(BlockPos blockEntityPos, Map<BlockPos, Optional<String>> possibleTargets,
                         Optional<BlockPos> selectedTarget) {
        super(TextComponent.EMPTY);
        this.blockEntityPos = blockEntityPos;
        this.possibleTargets = possibleTargets;
        this.selectedTarget = selectedTarget;
    }

    @Override
    protected void init() {
        this.targetList = new RailgunTargetList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);

        this.possibleTargets.forEach((pos, name) -> this.targetList.addTarget(pos, name, button -> {
            this.selectedTarget = Optional.of(button.pos);
            NetworkHandler.INSTANCE.sendToServer(
                    new RailgunUpdateSelectedTargetPKT(this.blockEntityPos, this.selectedTarget));
        }));

        this.addRenderableWidget(this.targetList);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
