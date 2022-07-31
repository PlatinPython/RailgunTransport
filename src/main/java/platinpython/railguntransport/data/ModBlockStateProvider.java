package platinpython.railguntransport.data;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.util.registries.BlockRegistry;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, RailgunTransport.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerStatesBlockAndItemModels(BlockRegistry.CAPSULE, state -> {
            Direction dir = state.getValue(BlockStateProperties.FACING);
            return ConfiguredModel.builder()
                                  .modelFile(models().getExistingFile(BlockRegistry.CAPSULE.getId()))
                                  .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                                  .rotationY(dir.getAxis().isVertical() ? 0 : (int) (dir.toYRot() + 180) % 360)
                                  .build();
        });
    }

    @SuppressWarnings("SameParameterValue")
    private void registerStatesBlockAndItemModels(RegistryObject<Block> block,
                                                  Function<BlockState, ConfiguredModel[]> mapper) {
        getVariantBuilder(block.get()).forAllStates(mapper);

        itemModels().withExistingParent(block.getId().toString(),
                                        modLoc(ModelProvider.BLOCK_FOLDER + "/" + block.getId().getPath())
        );
    }
}
