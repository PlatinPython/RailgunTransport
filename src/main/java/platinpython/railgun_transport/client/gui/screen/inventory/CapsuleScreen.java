package platinpython.railgun_transport.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import platinpython.railgun_transport.menu.CapsuleMenu;

public class CapsuleScreen extends AbstractContainerScreen<CapsuleMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND =
        ResourceLocation.withDefaultNamespace("textures/gui/container/shulker_box.png");

    public CapsuleScreen(CapsuleMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(CONTAINER_BACKGROUND, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }
}
