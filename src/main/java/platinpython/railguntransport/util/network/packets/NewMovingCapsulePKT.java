package platinpython.railguntransport.util.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import platinpython.railguntransport.util.capsule.client.MovingCapsuleRendering;

import java.util.function.Supplier;

public class NewMovingCapsulePKT {
    private final BlockPos start;
    private final BlockPos target;
    private final int totalTicks;
    private final int remainingTicks;

    public NewMovingCapsulePKT(BlockPos start, BlockPos target, int totalTicks, int remainingTicks) {
        this.start = start;
        this.target = target;
        this.totalTicks = totalTicks;
        this.remainingTicks = remainingTicks;
    }

    public static void encode(NewMovingCapsulePKT message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.start);
        buffer.writeBlockPos(message.target);
        buffer.writeInt(message.totalTicks);
        buffer.writeInt(message.remainingTicks);
    }

    public static NewMovingCapsulePKT decode(FriendlyByteBuf buffer) {
        return new NewMovingCapsulePKT(buffer.readBlockPos(), buffer.readBlockPos(), buffer.readInt(),
                                       buffer.readInt()
        );
    }

    public static class Handler {
        public static void handle(NewMovingCapsulePKT message, Supplier<NetworkEvent.Context> context) {
            context.get()
                   .enqueueWork(() -> MovingCapsuleRendering.addMovingCapsule(message.start, message.target,
                                                                              message.totalTicks, message.remainingTicks
                   ));
            context.get().setPacketHandled(true);
        }
    }
}
