package platinpython.railguntransport.util.registries;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import platinpython.railguntransport.menu.CapsuleMenu;
import platinpython.railguntransport.util.RegistryHandler;

public class MenuTypeRegistry {
    public static final RegistryObject<MenuType<CapsuleMenu>> CAPSULE = RegistryHandler.MENUS.register("capsule",
                                                                                                       () -> IForgeMenuType.create(
                                                                                                               ((windowId, inv, data) -> new CapsuleMenu(
                                                                                                                       windowId,
                                                                                                                       data.readBlockPos(),
                                                                                                                       inv,
                                                                                                                       inv.player
                                                                                                               )))
    );

    public static void register() {
    }
}
