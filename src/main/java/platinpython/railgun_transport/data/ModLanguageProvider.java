package platinpython.railgun_transport.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import platinpython.railgun_transport.RailgunTransport;
import platinpython.railgun_transport.util.registries.BlockRegistry;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, RailgunTransport.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(BlockRegistry.CAPSULE.get(), "Capsule");
        add(BlockRegistry.TERMINAL.get(), "Terminal");
        add(BlockRegistry.MULTIBLOCK.get(), "");
    }
}
