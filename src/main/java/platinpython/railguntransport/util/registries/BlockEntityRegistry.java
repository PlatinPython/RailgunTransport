package platinpython.railguntransport.util.registries;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import platinpython.railguntransport.block.entity.CapsuleBlockEntity;
import platinpython.railguntransport.block.entity.MultiblockBlockEntity;
import platinpython.railguntransport.block.entity.TerminalBlockEntity;
import platinpython.railguntransport.util.RegistryHandler;

@SuppressWarnings("ConstantConditions")
public class BlockEntityRegistry {
    public static final RegistryObject<BlockEntityType<CapsuleBlockEntity>> CAPSULE = RegistryHandler.BLOCK_ENTITY_TYPES.register(
            "capsule",
            () -> BlockEntityType.Builder.of(CapsuleBlockEntity::new, BlockRegistry.CAPSULE.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<TerminalBlockEntity>> TERMINAL = RegistryHandler.BLOCK_ENTITY_TYPES.register(
            "terminal",
            () -> BlockEntityType.Builder.of(TerminalBlockEntity::new, BlockRegistry.TERMINAL.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<MultiblockBlockEntity>> MULTIBLOCK = RegistryHandler.BLOCK_ENTITY_TYPES.register(
            "multiblock",
            () -> BlockEntityType.Builder.of(MultiblockBlockEntity::new, BlockRegistry.MULTIBLOCK.get()).build(null)
    );

    public static void register() {
    }
}
