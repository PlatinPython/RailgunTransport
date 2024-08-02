package platinpython.railgun_transport.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import platinpython.railgun_transport.RailgunTransport;
import platinpython.railgun_transport.util.registries.BlockEntityRegistry;
import platinpython.railgun_transport.util.registries.BlockRegistry;
import platinpython.railgun_transport.util.registries.ItemRegistry;
import platinpython.railgun_transport.util.registries.MenuTypeRegistry;

public class RegistryHandler {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RailgunTransport.MOD_ID);

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RailgunTransport.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, RailgunTransport.MOD_ID);

    public static final DeferredRegister<MenuType<?>> MENUS =
        DeferredRegister.create(BuiltInRegistries.MENU, RailgunTransport.MOD_ID);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITY_TYPES.register(bus);
        MENUS.register(bus);

        BlockRegistry.register();
        ItemRegistry.register();
        BlockEntityRegistry.register();
        MenuTypeRegistry.register();
    }
}
