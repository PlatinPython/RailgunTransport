package platinpython.railgun_transport.util.registries;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import platinpython.railgun_transport.item.CapsuleBlockItem;
import platinpython.railgun_transport.util.RegistryHandler;

public class ItemRegistry {
    public static final DeferredItem<CapsuleBlockItem> CAPSULE = RegistryHandler.ITEMS.register(
        "capsule",
        () -> new CapsuleBlockItem(BlockRegistry.CAPSULE.get(), new Item.Properties().rarity(Rarity.RARE).stacksTo(1))
    );

    public static void register() {}
}
