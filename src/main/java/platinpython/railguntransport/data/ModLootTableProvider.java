package platinpython.railguntransport.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;
import platinpython.railguntransport.block.CapsuleBlock;
import platinpython.railguntransport.util.RegistryHandler;
import platinpython.railguntransport.util.registries.BlockEntityRegistry;
import platinpython.railguntransport.util.registries.BlockRegistry;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return ImmutableList.of(Pair.of(Blocks::new, LootContextParamSets.BLOCK));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        map.forEach((name, table) -> LootTables.validate(validationtracker, name, table));
    }

    private static class Blocks extends BlockLoot {
        @Override
        protected void addTables() {
            this.add(BlockRegistry.CAPSULE.get(), Blocks::createCapsuleBoxDrop);
            this.dropSelf(BlockRegistry.TERMINAL.get());
            this.dropOther(BlockRegistry.MULTIBLOCK.get(), Items.AIR);
        }

        private static LootTable.Builder createCapsuleBoxDrop(Block capsuleBlock) {
            return LootTable.lootTable()
                            .withPool(applyExplosionCondition(capsuleBlock, LootPool.lootPool()
                                                                                    .setRolls(
                                                                                            ConstantValue.exactly(1.0F))
                                                                                    .add(LootItem.lootTableItem(
                                                                                                         capsuleBlock)
                                                                                                 .apply(CopyNbtFunction.copyData(
                                                                                                                               ContextNbtProvider.BLOCK_ENTITY)
                                                                                                                       .copy("Inventory",
                                                                                                                             "BlockEntityTag.Inventory"
                                                                                                                       ))
                                                                                                 .apply(SetContainerContents.setContents(
                                                                                                                                    BlockEntityRegistry.CAPSULE.get())
                                                                                                                            .withEntry(
                                                                                                                                    DynamicLoot.dynamicEntry(
                                                                                                                                            CapsuleBlock.CONTENTS))))));
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
        }
    }
}
