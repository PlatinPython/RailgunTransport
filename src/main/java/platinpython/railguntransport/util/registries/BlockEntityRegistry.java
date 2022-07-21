package platinpython.railguntransport.util.registries;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import platinpython.railguntransport.block.entity.CapsuleBlockEntity;
import platinpython.railguntransport.block.entity.RailgunBlockEntity;
import platinpython.railguntransport.block.entity.TargetBlockEntity;
import platinpython.railguntransport.util.RegistryHandler;

@SuppressWarnings("ConstantConditions")
public class BlockEntityRegistry {
    public static final RegistryObject<BlockEntityType<RailgunBlockEntity>> RAILGUN = RegistryHandler.BLOCK_ENTITY_TYPES.register(
            "railgun",
            () -> BlockEntityType.Builder.of(RailgunBlockEntity::new, BlockRegistry.RAILGUN.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<TargetBlockEntity>> TARGET = RegistryHandler.BLOCK_ENTITY_TYPES.register(
            "target", () -> BlockEntityType.Builder.of(TargetBlockEntity::new, BlockRegistry.TARGET.get()).build(null));

    public static final RegistryObject<BlockEntityType<CapsuleBlockEntity>> CAPSULE = RegistryHandler.BLOCK_ENTITY_TYPES.register(
            "capsule",
            () -> BlockEntityType.Builder.of(CapsuleBlockEntity::new, BlockRegistry.CAPSULE.get()).build(null)
    );

    public static void register() {
    }
}
