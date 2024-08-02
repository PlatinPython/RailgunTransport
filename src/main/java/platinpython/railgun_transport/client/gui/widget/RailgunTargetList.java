package platinpython.railgun_transport.client.gui.widget;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public class RailgunTargetList extends ContainerObjectSelectionList<RailgunTargetList.RailgunTargetListEntry> {

    public RailgunTargetList(Minecraft minecraft, int width, int height, int top, int itemHeight) {
        super(minecraft, width, height, top, itemHeight);
    }

    public void addTarget(BlockPos pos, Optional<String> name, TargetButton.OnPress onPress) {
        this.addEntry(
            new RailgunTargetListEntry(
                new TargetButton(
                    this.width / 2 - 155, 0, 310, 20,
                    Component
                        .literal(name.orElse("") + (name.isPresent() ? " " : "") + "(" + pos.toShortString() + ")"),
                    onPress, pos
                )
            )
        );
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 32;
    }

    public static class RailgunTargetListEntry extends ContainerObjectSelectionList.Entry<RailgunTargetListEntry> {
        private final AbstractWidget child;

        public RailgunTargetListEntry(AbstractWidget child) {
            this.child = child;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(child);
        }

        @Override
        public void render(
            GuiGraphics guiGraphics,
            int index,
            int top,
            int left,
            int width,
            int height,
            int mouseX,
            int mouseY,
            boolean isMouseOver,
            float partialTick
        ) {
            this.child.setY(top);
            this.child.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(child);
        }
    }
}
