package platinpython.railguntransport.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.util.registries.BlockRegistry;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(DataGenerator generator) {
        super(generator, RailgunTransport.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(BlockRegistry.RAILGUN.get(), "Railgun");
    }
}
