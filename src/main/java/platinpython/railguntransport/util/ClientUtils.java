package platinpython.railguntransport.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.client.gui.screen.RailgunScreen;
import platinpython.railguntransport.client.gui.screen.TargetScreen;
import platinpython.railguntransport.client.gui.screen.inventory.CapsuleScreen;
import platinpython.railguntransport.client.renderer.blockentity.TerminalRenderer;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;
import platinpython.railguntransport.util.registries.BlockRegistry;
import platinpython.railguntransport.util.registries.MenuTypeRegistry;

import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = RailgunTransport.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientUtils {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(MenuTypeRegistry.CAPSULE.get(), CapsuleScreen::new));

        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.MULTIBLOCK.get(), RenderType.translucent());
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.TERMINAL.get(), TerminalRenderer::new);
    }

    @SubscribeEvent
    public static void onModelBake(ModelRegistryEvent event) {
        ForgeModelBakery.addSpecialModel(ModelLocations.BASE);

        ForgeModelBakery.addSpecialModel(ModelLocations.Railgun.MOUNT);
        ForgeModelBakery.addSpecialModel(ModelLocations.Railgun.BODY);
        ForgeModelBakery.addSpecialModel(ModelLocations.Railgun.HOLD);

        ForgeModelBakery.addSpecialModel(ModelLocations.Target.MOUNT);
        ForgeModelBakery.addSpecialModel(ModelLocations.Target.SHAFT_FRONT);
        ForgeModelBakery.addSpecialModel(ModelLocations.Target.SHAFT_MIDDLE);
        ForgeModelBakery.addSpecialModel(ModelLocations.Target.SHAFT_BACK);
        ForgeModelBakery.addSpecialModel(ModelLocations.Target.CLAW_UP);
        ForgeModelBakery.addSpecialModel(ModelLocations.Target.CLAW_DOWN);
        ForgeModelBakery.addSpecialModel(ModelLocations.Target.CLAW_LEFT);
        ForgeModelBakery.addSpecialModel(ModelLocations.Target.CLAW_RIGHT);
    }

    public static void openRailgunScreen(BlockPos blockEntityPos, Map<BlockPos, Optional<String>> possibleTargets,
                                         Optional<BlockPos> selectedTarget) {
        Minecraft.getInstance().setScreen(new RailgunScreen(blockEntityPos, possibleTargets, selectedTarget));
    }

    public static void openTargetScreen(BlockPos blockEntityPos, Optional<String> name) {
        Minecraft.getInstance().setScreen(new TargetScreen(blockEntityPos, name));
    }
}
