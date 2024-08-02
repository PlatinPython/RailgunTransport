package platinpython.railgun_transport;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;
import platinpython.railgun_transport.block.entity.CapsuleBlockEntity;
import platinpython.railgun_transport.block.entity.TerminalBlockEntity;
import platinpython.railgun_transport.util.RegistryHandler;
import platinpython.railgun_transport.util.network.NetworkHandler;
import platinpython.railgun_transport.util.registries.BlockEntityRegistry;
import platinpython.railgun_transport.util.registries.BlockRegistry;
import platinpython.railgun_transport.util.registries.ItemRegistry;

@Mod(RailgunTransport.MOD_ID)
public class RailgunTransport {
    public static final String MOD_ID = "railgun_transport";

    public static final Logger LOGGER = LogUtils.getLogger();

    public RailgunTransport(IEventBus bus) {
        bus.addListener(RailgunTransport::addItemsToTab);
        bus.addListener(RailgunTransport::registerCapabilities);
        bus.addListener(NetworkHandler::register);

        RegistryHandler.register(bus);
    }

    public static void addItemsToTab(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(CreativeModeTabs.REDSTONE_BLOCKS)) {
            return;
        }
        event.accept(ItemRegistry.CAPSULE);
        event.accept(BlockRegistry.TERMINAL);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.CAPSULE.get(), CapsuleBlockEntity::getItemHandler
        );
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.TERMINAL.get(), TerminalBlockEntity::getItemHandler
        );
    }
}
