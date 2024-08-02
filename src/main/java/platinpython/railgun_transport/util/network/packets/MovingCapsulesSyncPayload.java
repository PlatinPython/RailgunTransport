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

import java.util.HashSet;
import java.util.Set;

public record MovingCapsulesSyncPayload(
    Asymmetry<HashSet<MovingCapsuleClient>, HashSet<MovingCapsuleServer>> movingCapsules
) implements CustomPacketPayload {
    public static final Type<MovingCapsulesSyncPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "moving_capsules_sync"));
    @SuppressWarnings("UnstableApiUsage")
    public static final StreamCodec<ByteBuf, MovingCapsulesSyncPayload> STREAM_CODEC =
        AsymmetricalStreamCodecs
            .codec(
                StreamCodec
                    .composite(
                        BlockPos.STREAM_CODEC, MovingCapsuleClient::start, BlockPos.STREAM_CODEC,
                        MovingCapsuleClient::target, ByteBufCodecs.VAR_INT, MovingCapsuleClient::totalTicks,
                        ByteBufCodecs.VAR_INT, MovingCapsuleClient::remainingTicks, MovingCapsuleClient::new
                    )
                    .apply(ByteBufCodecs.collection(HashSet::new)),
                StreamCodec
                    .composite(
                        StreamCodec.unit(ItemStack.EMPTY), i -> ItemStack.EMPTY, BlockPos.STREAM_CODEC,
                        MovingCapsuleServer::start, BlockPos.STREAM_CODEC, MovingCapsuleServer::target,
                        ByteBufCodecs.VAR_INT, MovingCapsuleServer::totalTicks, ByteBufCodecs.VAR_INT,
                        MovingCapsuleServer::remainingTicks, MovingCapsuleServer::new
                    )
                    .apply(ByteBufCodecs.collection(HashSet::new))
            )
            .map(MovingCapsulesSyncPayload::new, MovingCapsulesSyncPayload::movingCapsules);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<MovingCapsulesSyncPayload> {
        @Override
        public void handle(MovingCapsulesSyncPayload payload, IPayloadContext context) {
            MovingCapsuleRendering.clearMovingCapsules();
            payload.movingCapsules.decoding()
                .resultOrPartial(RailgunTransport.LOGGER::error)
                .stream()
                .flatMap(Set::stream)
                .forEach(MovingCapsuleRendering::addMovingCapsule);
        }
    }
}
