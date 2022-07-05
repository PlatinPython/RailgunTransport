package platinpython.railguntransport.util.registries;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.RegistryObject;
import platinpython.railguntransport.item.CapsuleBlockItem;
import platinpython.railguntransport.util.RegistryHandler;

public class ItemRegistry {
    public static final RegistryObject<Item> CAPSULE = RegistryHandler.ITEMS.register("capsule",
                                                                                      () -> new CapsuleBlockItem(
                                                                                              BlockRegistry.CAPSULE.get(),
                                                                                              new Item.Properties().tab(
                                                                                                                           CreativeModeTab.TAB_REDSTONE)
                                                                                                                   .rarity(Rarity.RARE)
                                                                                      )
    );

    public static void register() {
    }
}
