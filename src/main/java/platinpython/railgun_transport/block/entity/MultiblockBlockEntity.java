package platinpython.railgun_transport.block.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.codecextras.Asymmetry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.railgun_transport.RailgunTransport;
import platinpython.railgun_transport.util.registries.BlockEntityRegistry;

import java.util.function.Consumer;

public class MultiblockBlockEntity extends BlockEntity {
    public static final Codec<Asymmetry<Consumer<MultiblockBlockEntity>, MultiblockBlockEntity>> CODEC =
        Asymmetry.split(
            RecordCodecBuilder.create(
                instance -> instance
                    .group(
                        BlockState.CODEC.fieldOf("saved_block_state").forGetter(Pair::getFirst),
                        BlockPos.CODEC.fieldOf("terminal_pos").forGetter(Pair::getSecond)
                    )
                    .apply(instance, Pair::of)
            ), pair -> blockEntity -> {
                blockEntity.setSavedBlockState(pair.getFirst());
                blockEntity.setTerminalPos(pair.getSecond());
            }, blockEntity -> Pair.of(blockEntity.savedBlockState, blockEntity.terminalPos)
        );
    private BlockState savedBlockState = Blocks.AIR.defaultBlockState();
    private BlockPos terminalPos = BlockPos.ZERO;

    public MultiblockBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.MULTIBLOCK.get(), worldPosition, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.merge(
            (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, Asymmetry.ofEncoding(this))
                .resultOrPartial(RailgunTransport.LOGGER::error)
                .orElse(new CompoundTag())
        );
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        CODEC.parse(NbtOps.INSTANCE, tag)
            .flatMap(Asymmetry::decoding)
            .resultOrPartial(RailgunTransport.LOGGER::error)
            .ifPresent(consumer -> consumer.accept(this));
    }

    public BlockState savedBlockState() {
        return this.savedBlockState;
    }

    public void setSavedBlockState(BlockState savedBlockState) {
        this.savedBlockState = savedBlockState;
    }

    public BlockPos terminalPos() {
        return this.terminalPos;
    }

    public void setTerminalPos(BlockPos terminalPos) {
        this.terminalPos = terminalPos;
    }
}
