package platinpython.railguntransport.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;

import java.util.List;
import java.util.Optional;

public class RailgunTargetList extends ContainerObjectSelectionList<RailgunTargetList.RailgunTargetListEntry> {

    public RailgunTargetList(Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
        super(minecraft, width, height, top, bottom, itemHeight);
        this.setRenderBackground(false);
    }

    public void addTarget(BlockPos pos, Optional<String> name, TargetButton.OnPress onPress) {
        this.addEntry(new RailgunTargetListEntry(new TargetButton(this.width / 2 - 155, 0, 310, 20, new TextComponent(
                name.orElse("") + (name.isPresent() ? " " : "") + "(" + pos.toShortString() + ")"), onPress, pos)));
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
        public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX,
                           int mouseY, boolean isMouseOver, float partialTick) {
            this.child.y = top;
            this.child.render(poseStack, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(child);
        }
    }
}
