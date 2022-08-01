package platinpython.railguntransport.data;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.block.CapsuleBlock;
import platinpython.railguntransport.block.TerminalBlock;
import platinpython.railguntransport.util.registries.BlockRegistry;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, RailgunTransport.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        getVariantBuilder(BlockRegistry.CAPSULE.get()).forAllStates(state -> {
            Direction dir = state.getValue(CapsuleBlock.FACING);
            return ConfiguredModel.builder()
                                  .modelFile(models().getExistingFile(new ResourceLocation(RailgunTransport.MOD_ID,
                                                                                           ModelProvider.BLOCK_FOLDER + "/capsule/capsule"
                                  )))
                                  .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                                  .rotationY(dir.getAxis().isVertical() ? 0 : (int) (dir.toYRot() + 180) % 360)
                                  .build();
        });

        itemModels().withExistingParent(BlockRegistry.CAPSULE.getId().toString(),
                                        modLoc(ModelProvider.BLOCK_FOLDER + "/capsule/capsule")
        );

        getVariantBuilder(BlockRegistry.TERMINAL.get()).forAllStates(state -> {
            Direction dir = state.getValue(TerminalBlock.HORIZONTAL_FACING);
            int yaw = (int) ((dir.toYRot() + 180) % 360);
            return ConfiguredModel.builder()
                                  .modelFile(models().getExistingFile(new ResourceLocation(RailgunTransport.MOD_ID,
                                                                                           ModelProvider.BLOCK_FOLDER + "/terminal/terminal"
                                  )))
                                  .rotationY(yaw)
                                  .build();
        });

        itemModels().withExistingParent(BlockRegistry.TERMINAL.getId().toString(),
                                        modLoc(ModelProvider.BLOCK_FOLDER + "/terminal/terminal")
        );
    }
}
