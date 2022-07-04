package platinpython.railguntransport.util.registries;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import platinpython.railguntransport.block.entity.CapsuleBlockEntity;
import platinpython.railguntransport.util.RegistryHandler;

public class BlockEntityRegistry {
    public static final RegistryObject<BlockEntityType<CapsuleBlockEntity>> CAPSULE = RegistryHandler.BLOCK_ENTITY_TYPES.register(
            "capsule",
            () -> BlockEntityType.Builder.of(CapsuleBlockEntity::new, BlockRegistry.CAPSULE.get()).build(null)
    );

    public static void register() {
    }
}
