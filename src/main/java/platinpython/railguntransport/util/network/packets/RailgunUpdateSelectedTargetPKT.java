package platinpython.railguntransport.util.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import platinpython.railguntransport.block.entity.TerminalBlockEntity;

import java.util.Optional;
import java.util.function.Supplier;

public class RailgunUpdateSelectedTargetPKT {
    private final BlockPos blockEntityPos;
    private final Optional<BlockPos> selectedTarget;

    public RailgunUpdateSelectedTargetPKT(BlockPos blockEntityPos, Optional<BlockPos> selectedTarget) {
        this.blockEntityPos = blockEntityPos;
        this.selectedTarget = selectedTarget;
    }

    public static void encode(RailgunUpdateSelectedTargetPKT message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.blockEntityPos);
        buffer.writeOptional(message.selectedTarget, FriendlyByteBuf::writeBlockPos);
    }

    public static RailgunUpdateSelectedTargetPKT decode(FriendlyByteBuf buffer) {
        return new RailgunUpdateSelectedTargetPKT(buffer.readBlockPos(),
                                                  buffer.readOptional(FriendlyByteBuf::readBlockPos)
        );
    }

    public static class Handler {
        public static void handle(RailgunUpdateSelectedTargetPKT message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                //noinspection ConstantConditions
                BlockEntity blockEntity = context.get().getSender().getLevel().getBlockEntity(message.blockEntityPos);
                if (blockEntity instanceof TerminalBlockEntity terminalBlockEntity) {
                    terminalBlockEntity.getRailgunData()
                                       .ifPresent(data -> data.setSelectedTarget(message.selectedTarget));
                    terminalBlockEntity.tryCapsuleSend();
                }
            });
            context.get().setPacketHandled(true);
        }
    }
}
