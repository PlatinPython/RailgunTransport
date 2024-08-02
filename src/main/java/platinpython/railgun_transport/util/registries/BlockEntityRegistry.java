package platinpython.railgun_transport.util.registries;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import platinpython.railgun_transport.block.entity.CapsuleBlockEntity;
import platinpython.railgun_transport.block.entity.MultiblockBlockEntity;
import platinpython.railgun_transport.block.entity.TerminalBlockEntity;
import platinpython.railgun_transport.util.RegistryHandler;

@SuppressWarnings("ConstantConditions")
public class BlockEntityRegistry {
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CapsuleBlockEntity>> CAPSULE =
        RegistryHandler.BLOCK_ENTITY_TYPES.register(
            "capsule",
            () -> BlockEntityType.Builder.of(CapsuleBlockEntity::new, BlockRegistry.CAPSULE.get()).build(null)
        );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TerminalBlockEntity>> TERMINAL =
        RegistryHandler.BLOCK_ENTITY_TYPES.register(
            "terminal",
            () -> BlockEntityType.Builder.of(TerminalBlockEntity::new, BlockRegistry.TERMINAL.get()).build(null)
        );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiblockBlockEntity>> MULTIBLOCK =
        RegistryHandler.BLOCK_ENTITY_TYPES.register(
            "multiblock",
            () -> BlockEntityType.Builder.of(MultiblockBlockEntity::new, BlockRegistry.MULTIBLOCK.get()).build(null)
        );

    public static void register() {}
}
