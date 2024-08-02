package platinpython.railgun_transport.data;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import platinpython.railgun_transport.util.RegistryHandler;
import platinpython.railgun_transport.util.registries.BlockRegistry;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(
            output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK)),
            lookupProvider
        );
    }

    private static class Blocks extends BlockLootSubProvider {
        protected Blocks(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        }

        private LootTable.Builder createCapsuleBoxDrop(Block capsuleBlock) {
            return LootTable.lootTable()
                .withPool(
                    applyExplosionCondition(
                        capsuleBlock,
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(
                                LootItem.lootTableItem(capsuleBlock)
                                    .apply(
                                        CopyComponentsFunction
                                            .copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                            .include(DataComponents.CUSTOM_NAME)
                                            .include(DataComponents.CONTAINER)
                                            .include(DataComponents.LOCK)
                                            .include(DataComponents.CONTAINER_LOOT)
                                    )
                            )
                    )
                );
        }

        @Override
        protected void generate() {
            this.add(BlockRegistry.CAPSULE.get(), this::createCapsuleBoxDrop);
            this.dropSelf(BlockRegistry.TERMINAL.get());
            this.dropOther(BlockRegistry.MULTIBLOCK.get(), Items.AIR);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return RegistryHandler.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
        }
    }
}
