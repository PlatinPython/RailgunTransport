package platinpython.railguntransport.util.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import platinpython.railguntransport.util.ClientUtils;

import java.util.function.Supplier;

public class TerminalScreenOpenPKT {
    public TerminalScreenOpenPKT() {
    }

    @SuppressWarnings("unused")
    public static void encode(TerminalScreenOpenPKT message, FriendlyByteBuf buffer) {
    }

    @SuppressWarnings({"unused", "InstantiationOfUtilityClass"})
    public static TerminalScreenOpenPKT decode(FriendlyByteBuf buffer) {
        return new TerminalScreenOpenPKT();
    }

    public static class Handler {
        @SuppressWarnings("unused")
        public static void handle(TerminalScreenOpenPKT message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(ClientUtils::openTerminalScreen);
            context.get().setPacketHandled(true);
        }
    }
}
