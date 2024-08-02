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

import java.util.Optional;

public record TargetScreenOpenPayload(BlockPos blockEntityPos, Optional<String> name) implements CustomPacketPayload {
    public static final Type<TargetScreenOpenPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "target_screen_open"));
    public static final StreamCodec<ByteBuf, TargetScreenOpenPayload> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, TargetScreenOpenPayload::blockEntityPos,
        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), TargetScreenOpenPayload::name, TargetScreenOpenPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<TargetScreenOpenPayload> {
        @Override
        public void handle(TargetScreenOpenPayload payload, IPayloadContext context) {
            ClientUtils.openTargetScreen(payload.blockEntityPos, payload.name);
        }
    }
}
