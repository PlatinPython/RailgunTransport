package platinpython.railgun_transport.util.network.packets;

import dev.lukebemish.codecextras.Asymmetry;
import dev.lukebemish.codecextras.stream.AsymmetricalStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import platinpython.railgun_transport.RailgunTransport;
import platinpython.railgun_transport.util.capsule.client.MovingCapsuleClient;
import platinpython.railgun_transport.util.capsule.client.MovingCapsuleRendering;
import platinpython.railgun_transport.util.capsule.server.MovingCapsuleServer;

public record NewMovingCapsulePayload(Asymmetry<MovingCapsuleClient, MovingCapsuleServer> capsule)
    implements CustomPacketPayload {
    public static final Type<NewMovingCapsulePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "new_moving_capsule"));
    @SuppressWarnings("UnstableApiUsage")
    public static final StreamCodec<ByteBuf, NewMovingCapsulePayload> STREAM_CODEC = AsymmetricalStreamCodecs.codec(
        StreamCodec.composite(
            BlockPos.STREAM_CODEC, MovingCapsuleClient::start, BlockPos.STREAM_CODEC, MovingCapsuleClient::target,
            ByteBufCodecs.VAR_INT, MovingCapsuleClient::totalTicks, ByteBufCodecs.VAR_INT,
            MovingCapsuleClient::remainingTicks, MovingCapsuleClient::new
        ),
        StreamCodec.composite(
            StreamCodec.unit(ItemStack.EMPTY), i -> ItemStack.EMPTY, BlockPos.STREAM_CODEC, MovingCapsuleServer::start,
            BlockPos.STREAM_CODEC, MovingCapsuleServer::target, ByteBufCodecs.VAR_INT, MovingCapsuleServer::totalTicks,
            ByteBufCodecs.VAR_INT, MovingCapsuleServer::remainingTicks, MovingCapsuleServer::new
        )
    ).map(NewMovingCapsulePayload::new, NewMovingCapsulePayload::capsule);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<NewMovingCapsulePayload> {
        @Override
        public void handle(NewMovingCapsulePayload payload, IPayloadContext context) {
            payload.capsule.decoding()
                .resultOrPartial(RailgunTransport.LOGGER::error)
                .ifPresent(MovingCapsuleRendering::addMovingCapsule);
        }
    }
}
