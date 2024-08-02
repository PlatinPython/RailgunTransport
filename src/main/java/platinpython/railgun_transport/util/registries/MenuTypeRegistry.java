package platinpython.railgun_transport.util.registries;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import platinpython.railgun_transport.menu.CapsuleMenu;
import platinpython.railgun_transport.util.RegistryHandler;

public class MenuTypeRegistry {
    public static final DeferredHolder<MenuType<?>, MenuType<CapsuleMenu>> CAPSULE =
        RegistryHandler.MENUS.register("capsule", () -> new MenuType<>(CapsuleMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void register() {}
}
