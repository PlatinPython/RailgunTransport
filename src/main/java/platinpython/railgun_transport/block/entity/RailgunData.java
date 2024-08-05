package platinpython.railgun_transport.block.entity;

import com.mojang.serialization.Codec;
import dev.lukebemish.codecextras.Asymmetry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import platinpython.railgun_transport.RailgunTransport;

import java.util.Optional;
import java.util.function.Consumer;

public class RailgunData {
    public static final Codec<Asymmetry<Consumer<RailgunData>, RailgunData>> CODEC = Asymmetry.split(
        BlockPos.CODEC.optionalFieldOf("selected_target").codec(), optional -> data -> data.selectedTarget = optional,
        data -> data.selectedTarget
    );

    private final TerminalBlockEntity parent;

    private Optional<BlockPos> selectedTarget = Optional.empty();
    private double yaw;
    private double pitch;

    public RailgunData(TerminalBlockEntity parent) {
        this.parent = parent;
        double[] angles = this.getAngles();
        this.yaw = angles[0];
        this.pitch = angles[1];
    }

    public Optional<Tag> saveToTag() {
        return CODEC.encodeStart(NbtOps.INSTANCE, Asymmetry.ofEncoding(this))
            .resultOrPartial(RailgunTransport.LOGGER::error);
    }

    public void load(Tag tag) {
        CODEC.parse(NbtOps.INSTANCE, tag)
            .flatMap(Asymmetry::decoding)
            .resultOrPartial(RailgunTransport.LOGGER::error)
            .ifPresent(consumer -> consumer.accept(this));
    }

    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("yaw", this.yaw);
        tag.putDouble("pitch", this.pitch);
        return tag;
    }

    public void handleUpdateTag(CompoundTag tag) {
        this.yaw = tag.getDouble("yaw");
        this.pitch = tag.getDouble("pitch");
    }

    public Optional<BlockPos> getSelectedTarget() {
        return this.selectedTarget;
    }

    public void setSelectedTarget(Optional<BlockPos> selectedTarget) {
        this.selectedTarget = selectedTarget;
        double[] angles = this.getAngles();
        this.yaw = angles[0];
        this.pitch = angles[1];
        if (this.parent.getLevel() != null) {
            this.parent.getLevel()
                .sendBlockUpdated(
                    this.parent.getBlockPos(), this.parent.getBlockState(), this.parent.getBlockState(),
                    Block.UPDATE_CLIENTS
                );
        }
    }

    public double getYaw() {
        return this.yaw;
    }

    @SuppressWarnings("unused")
    public double getPitch() {
        return this.pitch;
    }

    private double[] getAngles() {
        double yaw;
        double pitch;

        if (this.selectedTarget.isPresent()) {
            // Calculate Angle of Cannon
            Vec3 diff =
                Vec3.atLowerCornerOf(this.selectedTarget.get().subtract(this.parent.getBlockPos().offset(0, 1, 0)));
            // if (tile.previousTarget != null) {
            // diff = (Vec3.atLowerCornerOf(tile.previousTarget)
            // .add(Vec3.atLowerCornerOf(this.selectedTarget.get().subtract(tile.previousTarget))
            // .scale(partialTicks))).subtract(Vec3.atLowerCornerOf(this.getBlockPos()));
            // }

            double diffX = diff.x();
            double diffZ = diff.z();
            yaw = Math.atan2(diffX, diffZ);
            yaw = yaw / Math.PI * 180;

            float distance = (float) Math.sqrt(diffX * diffX + diffZ * diffZ);
            double yOffset = 0 + distance * 2f;
            pitch = Math.atan2(distance, diff.y() * 3 + yOffset);
            pitch = pitch / Math.PI * 180 + 10;
        } else {
            yaw = 10;
            pitch = 40;
        }

        return new double[]{
            yaw, pitch
        };
    }
}
