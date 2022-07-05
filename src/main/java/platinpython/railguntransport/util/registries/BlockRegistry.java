package platinpython.railguntransport.util.registries;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import platinpython.railguntransport.block.CapsuleBlock;
import platinpython.railguntransport.block.RailgunBlock;
import platinpython.railguntransport.util.RegistryHandler;

import java.util.function.Supplier;

public class BlockRegistry {
    public static final RegistryObject<Block> RAILGUN = register("railgun", RailgunBlock::new);

    public static final RegistryObject<Block> CAPSULE = registerNoItem("capsule", CapsuleBlock::new);

    public static void register() {
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> ret = registerNoItem(name, block);
        RegistryHandler.ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().tab(
                CreativeModeTab.TAB_REDSTONE).rarity(Rarity.RARE)));
        return ret;
    }

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return RegistryHandler.BLOCKS.register(name, block);
    }
}
