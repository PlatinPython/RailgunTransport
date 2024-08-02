package platinpython.railgun_transport.util.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import platinpython.railgun_transport.RailgunTransport;
import platinpython.railgun_transport.util.ClientUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record RailgunScreenOpenPayload(
    BlockPos blockEntityPos,
    Map<BlockPos, Optional<String>> possibleTargets,
    Optional<BlockPos> selectedTarget
) implements CustomPacketPayload {
    public static final Type<RailgunScreenOpenPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "railgun_screen_open"));
    public static final StreamCodec<ByteBuf, RailgunScreenOpenPayload> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, RailgunScreenOpenPayload::blockEntityPos,
        ByteBufCodecs.map(HashMap::new, BlockPos.STREAM_CODEC, ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8)),
        RailgunScreenOpenPayload::possibleTargets, ByteBufCodecs.optional(BlockPos.STREAM_CODEC),
        RailgunScreenOpenPayload::selectedTarget, RailgunScreenOpenPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<RailgunScreenOpenPayload> {
        @Override
        public void handle(RailgunScreenOpenPayload payload, IPayloadContext context) {
            ClientUtils.openRailgunScreen(payload.blockEntityPos, payload.possibleTargets, payload.selectedTarget);
        }
    }
}
