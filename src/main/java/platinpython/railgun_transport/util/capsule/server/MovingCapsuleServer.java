package platinpython.railgun_transport.util.capsule.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import platinpython.railgun_transport.block.entity.MultiblockBlockEntity;
import platinpython.railgun_transport.block.entity.TerminalBlockEntity;
import platinpython.railgun_transport.util.registries.BlockEntityRegistry;

import java.util.Optional;

public class MovingCapsuleServer {
    public static final Codec<MovingCapsuleServer> CODEC = RecordCodecBuilder.create(
        instance -> instance
            .group(
                ItemStack.SINGLE_ITEM_CODEC.fieldOf("capsule").forGetter(MovingCapsuleServer::capsule),
                BlockPos.CODEC.fieldOf("start").forGetter(MovingCapsuleServer::start),
                BlockPos.CODEC.fieldOf("target").forGetter(MovingCapsuleServer::target),
                Codec.INT.fieldOf("total_ticks").forGetter(MovingCapsuleServer::totalTicks),
                Codec.INT.fieldOf("remaining_ticks").forGetter(MovingCapsuleServer::remainingTicks)
            )
            .apply(instance, MovingCapsuleServer::new)
    );

    private final ItemStack capsule;
    private final BlockPos start;
    private final BlockPos target;
    private final int totalTicks;
    private int remainingTicks;

    public MovingCapsuleServer(ItemStack capsule, BlockPos start, BlockPos target) {
        this(
            capsule, start, target, calculateTicksForDistance(start, target), calculateTicksForDistance(start, target)
        );
    }

    public MovingCapsuleServer(ItemStack capsule, BlockPos start, BlockPos target, int totalTicks, int remainingTicks) {
        this.capsule = capsule;
        this.start = start;
        this.target = target;
        this.totalTicks = totalTicks;
        this.remainingTicks = remainingTicks;
    }

    private static int calculateTicksForDistance(BlockPos start, BlockPos target) {
        return (int) Mth.map(Math.sqrt(Math.sqrt(start.distSqr(target))) * 10, 100, 1000, 50, 1000);
    }

    public boolean tick(ServerLevel level) {
        if (this.remainingTicks > 0) {
            this.remainingTicks--;
            return false;
        }

        BlockPos spawnPos = this.target;

        Optional<MultiblockBlockEntity> maybeMultiblockBlockEntity =
            level.getBlockEntity(this.target, BlockEntityRegistry.MULTIBLOCK.get());
        if (maybeMultiblockBlockEntity.isPresent()) {
            spawnPos = maybeMultiblockBlockEntity.get().terminalPos();

            Optional<TerminalBlockEntity> maybeTerminalBlockEntity = maybeMultiblockBlockEntity.flatMap(
                blockEntity -> level.getBlockEntity(blockEntity.terminalPos(), BlockEntityRegistry.TERMINAL.get())
            );
            if (maybeTerminalBlockEntity.isPresent()) {
                IItemHandler handler = maybeTerminalBlockEntity.get().getItemHandler(null);
                if (handler != null) {
                    if (handler.insertItem(0, this.capsule, true).isEmpty()) {
                        handler.insertItem(0, this.capsule, false);
                        maybeTerminalBlockEntity.flatMap(TerminalBlockEntity::getTargetData)
                            .ifPresent(data -> data.setFree(true));
                        return true;
                    }
                }
            }
        }
        level.addFreshEntity(
            new ItemEntity(level, spawnPos.getX() + 0.5D, spawnPos.getY() + 0.5D, spawnPos.getZ() + 0.5D, this.capsule)
        );
        return true;
    }

    private ItemStack capsule() {
        return this.capsule;
    }

    public BlockPos start() {
        return this.start;
    }

    public BlockPos target() {
        return this.target;
    }

    public int totalTicks() {
        return this.totalTicks;
    }

    public int remainingTicks() {
        return this.remainingTicks;
    }
}
