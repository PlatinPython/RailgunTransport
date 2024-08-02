package platinpython.railgun_transport.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import platinpython.railgun_transport.util.registries.BlockRegistry;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.CAPSULE.get())
            .define('I', Items.IRON_INGOT)
            .define('C', Items.COPPER_BLOCK)
            .define('B', Items.POLISHED_BLACKSTONE)
            .define('#', Items.BARREL)
            .pattern("ICI")
            .pattern("B#B")
            .pattern("ICI")
            .unlockedBy("has_barrel", has(Items.BARREL))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.TERMINAL.get())
            .define('B', Items.POLISHED_BLACKSTONE_SLAB)
            .define('S', Items.SMOOTH_STONE_SLAB)
            .define('D', Items.DIAMOND)
            .define('H', Items.HOPPER)
            .pattern("BSB")
            .pattern("DHD")
            .pattern(" S ")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(output);
    }
}
