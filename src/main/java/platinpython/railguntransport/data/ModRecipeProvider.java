package platinpython.railguntransport.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import platinpython.railguntransport.util.registries.BlockRegistry;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {
        ShapedRecipeBuilder.shaped(BlockRegistry.CAPSULE.get())
                           .define('I', Items.IRON_INGOT)
                           .define('C', Items.COPPER_BLOCK)
                           .define('B', Items.POLISHED_BLACKSTONE)
                           .define('#', Items.BARREL)
                           .pattern("ICI")
                           .pattern("B#B")
                           .pattern("ICI")
                           .unlockedBy("has_barrel", has(Items.BARREL))
                           .save(finishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(BlockRegistry.TERMINAL.get())
                           .define('B', Items.POLISHED_BLACKSTONE_SLAB)
                           .define('S', Items.SMOOTH_STONE_SLAB)
                           .define('D', Items.DIAMOND)
                           .define('H', Items.HOPPER)
                           .pattern("BSB")
                           .pattern("DHD")
                           .pattern(" S ")
                           .unlockedBy("has_diamond", has(Items.DIAMOND))
                           .save(finishedRecipeConsumer);
    }
}
