package platinpython.railguntransport.util.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import platinpython.railguntransport.util.ClientUtils;

import java.util.Optional;
import java.util.function.Supplier;

public class TargetScreenOpenPKT {
    private final BlockPos blockEntityPos;
    private final Optional<String> name;

    public TargetScreenOpenPKT(BlockPos blockEntityPos, Optional<String> name) {
        this.blockEntityPos = blockEntityPos;
        this.name = name;
    }

    public static void encode(TargetScreenOpenPKT message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.blockEntityPos);
        buffer.writeOptional(message.name, FriendlyByteBuf::writeUtf);
    }

    public static TargetScreenOpenPKT decode(FriendlyByteBuf buffer) {
        return new TargetScreenOpenPKT(buffer.readBlockPos(), buffer.readOptional(FriendlyByteBuf::readUtf));
    }

    public static class Handler {
        public static void handle(TargetScreenOpenPKT message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> ClientUtils.openTargetScreen(message.blockEntityPos, message.name));
            context.get().setPacketHandled(true);
        }
    }
}
