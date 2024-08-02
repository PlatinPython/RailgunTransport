package platinpython.railgun_transport.util.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import platinpython.railgun_transport.util.network.packets.MovingCapsulesSyncPayload;
import platinpython.railgun_transport.util.network.packets.NewMovingCapsulePayload;
import platinpython.railgun_transport.util.network.packets.RailgunScreenOpenPayload;
import platinpython.railgun_transport.util.network.packets.RailgunUpdateSelectedTargetPayload;
import platinpython.railgun_transport.util.network.packets.TargetScreenOpenPayload;
import platinpython.railgun_transport.util.network.packets.TargetUpdateNamePayload;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(
            RailgunScreenOpenPayload.TYPE, RailgunScreenOpenPayload.STREAM_CODEC, new RailgunScreenOpenPayload.Handler()
        );
        registrar.playToServer(
            RailgunUpdateSelectedTargetPayload.TYPE, RailgunUpdateSelectedTargetPayload.STREAM_CODEC,
            new RailgunUpdateSelectedTargetPayload.Handler()
        );
        registrar.playToClient(
            TargetScreenOpenPayload.TYPE, TargetScreenOpenPayload.STREAM_CODEC, new TargetScreenOpenPayload.Handler()
        );
        registrar.playToServer(
            TargetUpdateNamePayload.TYPE, TargetUpdateNamePayload.STREAM_CODEC, new TargetUpdateNamePayload.Handler()
        );
        registrar.playToClient(
            NewMovingCapsulePayload.TYPE, NewMovingCapsulePayload.STREAM_CODEC, new NewMovingCapsulePayload.Handler()
        );
        registrar.playToClient(
            MovingCapsulesSyncPayload.TYPE, MovingCapsulesSyncPayload.STREAM_CODEC,
            new MovingCapsulesSyncPayload.Handler()
        );
    }
}
