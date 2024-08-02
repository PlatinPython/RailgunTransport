package platinpython.railgun_transport.data;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import platinpython.railgun_transport.RailgunTransport;
import platinpython.railgun_transport.block.CapsuleBlock;
import platinpython.railgun_transport.block.TerminalBlock;
import platinpython.railgun_transport.util.registries.BlockRegistry;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RailgunTransport.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        getVariantBuilder(BlockRegistry.CAPSULE.get()).forAllStates(state -> {
            Direction dir = state.getValue(CapsuleBlock.FACING);
            return ConfiguredModel.builder()
                .modelFile(models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/capsule/capsule")))
                .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                .rotationY(dir.getAxis().isVertical() ? 0 : (int) (dir.toYRot() + 180) % 360)
                .build();
        });

        itemModels().withExistingParent(
            BlockRegistry.CAPSULE.getId().toString(), modLoc(ModelProvider.BLOCK_FOLDER + "/capsule/capsule")
        );

        getVariantBuilder(BlockRegistry.TERMINAL.get()).forAllStatesExcept(state -> {
            Direction dir = state.getValue(TerminalBlock.HORIZONTAL_FACING);
            int yaw = (int) ((dir.toYRot() + 180) % 360);
            return ConfiguredModel.builder()
                .modelFile(models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/terminal/terminal")))
                .rotationY(yaw)
                .build();
        }, TerminalBlock.MULTIBLOCK_TYPE);

        itemModels().withExistingParent(
            BlockRegistry.TERMINAL.getId().toString(), modLoc(ModelProvider.BLOCK_FOLDER + "/terminal/terminal")
        );
    }
}
