package platinpython.railguntransport.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.util.registries.BlockRegistry;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, RailgunTransport.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        getVariantBuilder(BlockRegistry.RAILGUN.get()).forAllStates(state -> ConfiguredModel.builder()
                                                                                            .modelFile(
                                                                                                    models().withExistingParent(
                                                                                                            ModelProvider.BLOCK_FOLDER + "/" + BlockRegistry.RAILGUN.getId()
                                                                                                                                                                    .getPath(),
                                                                                                            new ResourceLocation(
                                                                                                                    RailgunTransport.MOD_ID,
                                                                                                                    "pedestal"
                                                                                                            )
                                                                                                    ))
                                                                                            .build());

        itemModels().withExistingParent(BlockRegistry.RAILGUN.getId().getPath(),
                                        modLoc(ModelProvider.BLOCK_FOLDER + "/" + BlockRegistry.RAILGUN.getId()
                                                                                                       .getPath())
        );
    }
}
