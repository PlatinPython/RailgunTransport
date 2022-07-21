package platinpython.railguntransport.util.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.util.network.packets.RailgunScreenOpenPKT;
import platinpython.railguntransport.util.network.packets.RailgunUpdateSelectedTargetPKT;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RailgunTransport.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    @SuppressWarnings("UnusedAssignment")
    public static void register() {
        int index = 0;
        INSTANCE.messageBuilder(RailgunScreenOpenPKT.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(RailgunScreenOpenPKT::encode)
                .decoder(RailgunScreenOpenPKT::decode)
                .consumer(RailgunScreenOpenPKT.Handler::handle)
                .add();
        INSTANCE.messageBuilder(RailgunUpdateSelectedTargetPKT.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(RailgunUpdateSelectedTargetPKT::encode)
                .decoder(RailgunUpdateSelectedTargetPKT::decode)
                .consumer(RailgunUpdateSelectedTargetPKT.Handler::handle)
                .add();
    }
}
