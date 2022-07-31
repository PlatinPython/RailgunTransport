package platinpython.railguntransport.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.client.gui.screen.RailgunScreen;
import platinpython.railguntransport.client.gui.screen.TargetScreen;
import platinpython.railguntransport.client.gui.screen.TerminalScreen;
import platinpython.railguntransport.client.gui.screen.inventory.CapsuleScreen;
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

    public static void openTerminalScreen() {
        Minecraft.getInstance().setScreen(new TerminalScreen());
    }

    public static void openRailgunScreen(BlockPos blockEntityPos, Map<BlockPos, Optional<String>> possibleTargets,
                                         Optional<BlockPos> selectedTarget) {
        Minecraft.getInstance().setScreen(new RailgunScreen(blockEntityPos, possibleTargets, selectedTarget));
    }

    public static void openTargetScreen(BlockPos blockEntityPos, Optional<String> name) {
        Minecraft.getInstance().setScreen(new TargetScreen(blockEntityPos, name));
    }
}
