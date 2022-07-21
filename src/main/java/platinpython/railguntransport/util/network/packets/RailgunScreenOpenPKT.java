package platinpython.railguntransport.util.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import platinpython.railguntransport.util.ClientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class RailgunScreenOpenPKT {
    private final BlockPos blockEntityPos;
    private final List<BlockPos> possibleTargets;
    private final Optional<BlockPos> selectedTarget;

    public RailgunScreenOpenPKT(BlockPos blockEntityPos, List<BlockPos> possibleTargets, Optional<BlockPos> selectedTarget) {
        this.blockEntityPos = blockEntityPos;
        this.possibleTargets = possibleTargets;
        this.selectedTarget = selectedTarget;
    }

    public static void encode(RailgunScreenOpenPKT message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.blockEntityPos);
        buffer.writeInt(message.possibleTargets.size());
        message.possibleTargets.forEach(buffer::writeBlockPos);
        buffer.writeOptional(message.selectedTarget, FriendlyByteBuf::writeBlockPos);
    }

    public static RailgunScreenOpenPKT decode(FriendlyByteBuf buffer) {
        BlockPos blockEntityPos = buffer.readBlockPos();
        int size = buffer.readInt();
        ArrayList<BlockPos> possibleTargets = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            possibleTargets.add(i, buffer.readBlockPos());
        }
        Optional<BlockPos> selectedTarget = buffer.readOptional(FriendlyByteBuf::readBlockPos);
        return new RailgunScreenOpenPKT(blockEntityPos, possibleTargets, selectedTarget);
    }

    public static class Handler {
        public static void handle(RailgunScreenOpenPKT message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> ClientUtils.openRailgunScreen(message.blockEntityPos, message.possibleTargets, message.selectedTarget));
            context.get().setPacketHandled(true);
        }
    }
}
