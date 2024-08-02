package platinpython.railgun_transport.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import platinpython.railgun_transport.RailgunTransport;
import platinpython.railgun_transport.client.gui.screen.RailgunScreen;
import platinpython.railgun_transport.client.gui.screen.TargetScreen;
import platinpython.railgun_transport.client.gui.screen.inventory.CapsuleScreen;
import platinpython.railgun_transport.client.renderer.blockentity.TerminalRenderer;
import platinpython.railgun_transport.util.registries.BlockEntityRegistry;
import platinpython.railgun_transport.util.registries.MenuTypeRegistry;

import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = RailgunTransport.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientUtils {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MenuTypeRegistry.CAPSULE.get(), CapsuleScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.TERMINAL.get(), TerminalRenderer::new);
    }

    @SubscribeEvent
    public static void onModelBake(ModelEvent.RegisterAdditional event) {
        event.register(ModelLocations.BASE);

        event.register(ModelLocations.Railgun.MOUNT);
        event.register(ModelLocations.Railgun.BODY);
        event.register(ModelLocations.Railgun.HOLD);

        event.register(ModelLocations.Target.MOUNT);
        event.register(ModelLocations.Target.SHAFT_FRONT);
        event.register(ModelLocations.Target.SHAFT_MIDDLE);
        event.register(ModelLocations.Target.SHAFT_BACK);
        event.register(ModelLocations.Target.CLAW_UP);
        event.register(ModelLocations.Target.CLAW_DOWN);
        event.register(ModelLocations.Target.CLAW_LEFT);
        event.register(ModelLocations.Target.CLAW_RIGHT);
    }

    public static void openRailgunScreen(
        BlockPos blockEntityPos,
        Map<BlockPos, Optional<String>> possibleTargets,
        Optional<BlockPos> selectedTarget
    ) {
        Minecraft.getInstance().setScreen(new RailgunScreen(blockEntityPos, possibleTargets, selectedTarget));
    }

    public static void openTargetScreen(BlockPos blockEntityPos, Optional<String> name) {
        Minecraft.getInstance().setScreen(new TargetScreen(blockEntityPos, name));
    }
}
