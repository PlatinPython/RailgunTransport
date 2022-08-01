package platinpython.railguntransport.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.util.registries.BlockRegistry;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
        super(pGenerator, RailgunTransport.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(BlockTags.GUARDED_BY_PIGLINS).add(BlockRegistry.CAPSULE.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(BlockRegistry.CAPSULE.get(), BlockRegistry.TERMINAL.get(), BlockRegistry.MULTIBLOCK.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL)
            .add(BlockRegistry.CAPSULE.get(), BlockRegistry.TERMINAL.get());
    }
}
