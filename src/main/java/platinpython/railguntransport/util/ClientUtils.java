package platinpython.railguntransport.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.client.gui.screen.RailgunScreen;
import platinpython.railguntransport.client.gui.screen.inventory.CapsuleScreen;
import platinpython.railguntransport.client.particle.CapsuleParticle;
import platinpython.railguntransport.util.registries.MenuTypeRegistry;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = RailgunTransport.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientUtils {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(MenuTypeRegistry.CAPSULE.get(), CapsuleScreen::new));
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        ForgeModelBakery.addSpecialModel(new ResourceLocation(RailgunTransport.MOD_ID, "block/capsule"));
    }

    public static void openRailgunScreen(BlockPos blockEntityPos, List<BlockPos> possibleTargets,
                                         Optional<BlockPos> selectedTarget) {
        RailgunTransport.LOGGER.info(
                "Open Screen at position {} with selected target {} and these possible targets: {}", blockEntityPos,
                selectedTarget, possibleTargets
        );
        Minecraft.getInstance().setScreen(new RailgunScreen(blockEntityPos, possibleTargets, selectedTarget));
    }

    @Mod.EventBusSubscriber(modid = RailgunTransport.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE,
                            value = Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            if (event.getWorld().getBlockState(event.getPos()).is(Blocks.DIAMOND_BLOCK) && event.getItemStack()
                                                                                                .getItem() == Items.WARPED_FUNGUS_ON_A_STICK) {
                Minecraft.getInstance().particleEngine.add(new CapsuleParticle(Minecraft.getInstance().level,
                                                                               Vec3.atLowerCornerOf(event.getPos())
                                                                                   .add(0, 1, 0), Vec3.ZERO
                ));
            }
        }
    }
}
