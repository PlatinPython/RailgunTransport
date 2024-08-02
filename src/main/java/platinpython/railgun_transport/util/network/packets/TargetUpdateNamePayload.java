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
import platinpython.railgun_transport.block.entity.TerminalBlockEntity;
import platinpython.railgun_transport.util.registries.BlockEntityRegistry;

import java.util.Optional;

public record TargetUpdateNamePayload(BlockPos blockEntityPos, Optional<String> name) implements CustomPacketPayload {
    public static final Type<TargetUpdateNamePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "target_update_name"));
    public static final StreamCodec<ByteBuf, TargetUpdateNamePayload> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, TargetUpdateNamePayload::blockEntityPos,
        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), TargetUpdateNamePayload::name, TargetUpdateNamePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<TargetUpdateNamePayload> {
        @Override
        public void handle(TargetUpdateNamePayload payload, IPayloadContext context) {
            context.player()
                .level()
                .getBlockEntity(payload.blockEntityPos, BlockEntityRegistry.TERMINAL.get())
                .flatMap(TerminalBlockEntity::getTargetData)
                .ifPresent(data -> data.setName(payload.name));
        }
    }
}
