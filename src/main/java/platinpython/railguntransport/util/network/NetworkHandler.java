package platinpython.railguntransport.util.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.util.network.packets.MovingCapsulesSyncPKT;
import platinpython.railguntransport.util.network.packets.NewMovingCapsulePKT;
import platinpython.railguntransport.util.network.packets.RailgunScreenOpenPKT;
import platinpython.railguntransport.util.network.packets.RailgunUpdateSelectedTargetPKT;
import platinpython.railguntransport.util.network.packets.TargetScreenOpenPKT;
import platinpython.railguntransport.util.network.packets.TargetUpdateNamePKT;
import platinpython.railguntransport.util.network.packets.TerminalScreenOpenPKT;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RailgunTransport.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    @SuppressWarnings("UnusedAssignment")
    public static void register() {
        int index = 0;
        INSTANCE.messageBuilder(TerminalScreenOpenPKT.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(TerminalScreenOpenPKT::encode)
                .decoder(TerminalScreenOpenPKT::decode)
                .consumer(TerminalScreenOpenPKT.Handler::handle)
                .add();
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
        INSTANCE.messageBuilder(TargetScreenOpenPKT.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(TargetScreenOpenPKT::encode)
                .decoder(TargetScreenOpenPKT::decode)
                .consumer(TargetScreenOpenPKT.Handler::handle)
                .add();
        INSTANCE.messageBuilder(TargetUpdateNamePKT.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(TargetUpdateNamePKT::encode)
                .decoder(TargetUpdateNamePKT::decode)
                .consumer(TargetUpdateNamePKT.Handler::handle)
                .add();
        INSTANCE.messageBuilder(NewMovingCapsulePKT.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(NewMovingCapsulePKT::encode)
                .decoder(NewMovingCapsulePKT::decode)
                .consumer(NewMovingCapsulePKT.Handler::handle)
                .add();
        INSTANCE.messageBuilder(MovingCapsulesSyncPKT.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(MovingCapsulesSyncPKT::encode)
                .decoder(MovingCapsulesSyncPKT::decode)
                .consumer(MovingCapsulesSyncPKT.Handler::handle)
                .add();
    }
}
