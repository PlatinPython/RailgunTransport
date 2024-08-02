package platinpython.railgun_transport.util.registries;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import platinpython.railgun_transport.block.CapsuleBlock;
import platinpython.railgun_transport.block.MultiblockBlock;
import platinpython.railgun_transport.block.TerminalBlock;
import platinpython.railgun_transport.util.RegistryHandler;

import java.util.function.Supplier;

public class BlockRegistry {
    public static final DeferredBlock<CapsuleBlock> CAPSULE = registerNoItem("capsule", CapsuleBlock::new);

    public static final DeferredBlock<TerminalBlock> TERMINAL = register("terminal", TerminalBlock::new);

    public static final DeferredBlock<MultiblockBlock> MULTIBLOCK = registerNoItem("multiblock", MultiblockBlock::new);

    public static void register() {}

    @SuppressWarnings("SameParameterValue")
    private static <T extends Block> DeferredBlock<T> register(String name, Supplier<T> block) {
        DeferredBlock<T> ret = registerNoItem(name, block);
        RegistryHandler.ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().rarity(Rarity.RARE)));
        return ret;
    }

    private static <T extends Block> DeferredBlock<T> registerNoItem(String name, Supplier<T> block) {
        return RegistryHandler.BLOCKS.register(name, block);
    }
}
