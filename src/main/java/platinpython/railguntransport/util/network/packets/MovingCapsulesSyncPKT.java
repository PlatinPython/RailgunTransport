package platinpython.railguntransport.util.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import platinpython.railguntransport.util.capsule.client.MovingCapsuleRendering;
import platinpython.railguntransport.util.capsule.server.MovingCapsuleServer;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MovingCapsulesSyncPKT {
    private final Set<MovingCapsuleSyncData> movingCapsules;

    private MovingCapsulesSyncPKT(Set<MovingCapsuleSyncData> movingCapsules) {
        this.movingCapsules = movingCapsules;
    }

    public static MovingCapsulesSyncPKT fromMovingCapsuleServerSet(Set<MovingCapsuleServer> movingCapsules) {
        return new MovingCapsulesSyncPKT(movingCapsules.stream()
                                                       .map(c -> new MovingCapsuleSyncData(c.getStart(), c.getTarget(),
                                                                                           c.getTotalTicks(),
                                                                                           c.getRemainingTicks()
                                                       ))
                                                       .collect(Collectors.toSet()));
    }

    public static void encode(MovingCapsulesSyncPKT message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.movingCapsules.size());
        message.movingCapsules.forEach(c -> {
            buffer.writeBlockPos(c.start);
            buffer.writeBlockPos(c.target);
            buffer.writeInt(c.totalTicks);
            buffer.writeInt(c.remainingTicks);
        });
    }

    public static MovingCapsulesSyncPKT decode(FriendlyByteBuf buffer) {
        HashSet<MovingCapsuleSyncData> movingCapsules = new HashSet<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            movingCapsules.add(new MovingCapsuleSyncData(buffer.readBlockPos(), buffer.readBlockPos(), buffer.readInt(),
                                                         buffer.readInt()
            ));
        }
        return new MovingCapsulesSyncPKT(movingCapsules);
    }

    public static class Handler {
        public static void handle(MovingCapsulesSyncPKT message, Supplier<NetworkEvent.Context> context) {
            context.get()
                   .enqueueWork(() -> message.movingCapsules.forEach(
                           c -> MovingCapsuleRendering.addMovingCapsule(c.start, c.target, c.totalTicks,
                                                                        c.remainingTicks
                           )));
            context.get().setPacketHandled(true);
        }
    }

    private record MovingCapsuleSyncData(BlockPos start, BlockPos target, int totalTicks, int remainingTicks) {}
}
