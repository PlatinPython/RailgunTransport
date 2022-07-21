package platinpython.railguntransport.util.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import platinpython.railguntransport.util.ClientUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class RailgunScreenOpenPKT {
    private final BlockPos blockEntityPos;
    private final Map<BlockPos, Optional<String>> possibleTargets;
    private final Optional<BlockPos> selectedTarget;

    public RailgunScreenOpenPKT(BlockPos blockEntityPos, Map<BlockPos, Optional<String>> possibleTargets, Optional<BlockPos> selectedTarget) {
        this.blockEntityPos = blockEntityPos;
        this.possibleTargets = possibleTargets;
        this.selectedTarget = selectedTarget;
    }

    public static void encode(RailgunScreenOpenPKT message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.blockEntityPos);
        buffer.writeInt(message.possibleTargets.size());
        message.possibleTargets.forEach((pos, name) -> {
            buffer.writeBlockPos(pos);
            buffer.writeOptional(name, FriendlyByteBuf::writeUtf);
        });
        buffer.writeOptional(message.selectedTarget, FriendlyByteBuf::writeBlockPos);
    }

    public static RailgunScreenOpenPKT decode(FriendlyByteBuf buffer) {
        BlockPos blockEntityPos = buffer.readBlockPos();
        int size = buffer.readInt();
        Map<BlockPos, Optional<String>> possibleTargets = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            possibleTargets.put(buffer.readBlockPos(), buffer.readOptional(FriendlyByteBuf::readUtf));
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
