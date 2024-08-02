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

public record RailgunUpdateSelectedTargetPayload(BlockPos blockEntityPos, Optional<BlockPos> selectedTarget)
    implements CustomPacketPayload {
    public static final Type<RailgunUpdateSelectedTargetPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "railgun_update_selected_target"));
    public static final StreamCodec<ByteBuf, RailgunUpdateSelectedTargetPayload> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, RailgunUpdateSelectedTargetPayload::blockEntityPos,
        ByteBufCodecs.optional(BlockPos.STREAM_CODEC), RailgunUpdateSelectedTargetPayload::selectedTarget,
        RailgunUpdateSelectedTargetPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<RailgunUpdateSelectedTargetPayload> {
        @Override
        public void handle(RailgunUpdateSelectedTargetPayload payload, IPayloadContext context) {
            context.player()
                .level()
                .getBlockEntity(payload.blockEntityPos, BlockEntityRegistry.TERMINAL.get())
                .flatMap(TerminalBlockEntity::getRailgunData)
                .ifPresent(data -> data.setSelectedTarget(payload.selectedTarget));
        }
    }
}
