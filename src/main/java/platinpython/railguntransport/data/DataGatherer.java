package platinpython.railguntransport.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import platinpython.railguntransport.RailgunTransport;

@Mod.EventBusSubscriber(modid = RailgunTransport.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGatherer {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        if (event.includeClient()) {
            generator.addProvider(new ModLanguageProvider(generator));
            generator.addProvider(new ModBlockStateProvider(generator, existingFileHelper));
        }
    }
}
