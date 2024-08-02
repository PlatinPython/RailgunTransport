package platinpython.railgun_transport.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import platinpython.railgun_transport.RailgunTransport;
import platinpython.railgun_transport.util.registries.BlockRegistry;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> lookupProvider,
        ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, RailgunTransport.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.GUARDED_BY_PIGLINS).add(BlockRegistry.CAPSULE.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(BlockRegistry.CAPSULE.get(), BlockRegistry.TERMINAL.get(), BlockRegistry.MULTIBLOCK.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(BlockRegistry.CAPSULE.get(), BlockRegistry.TERMINAL.get());
    }
}
