package platinpython.railguntransport.util.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import platinpython.railguntransport.block.entity.TerminalBlockEntity;

import java.util.Optional;
import java.util.function.Supplier;

public class TargetUpdateNamePKT {
    private final BlockPos blockEntityPos;
    private final Optional<String> name;

    public TargetUpdateNamePKT(BlockPos blockEntityPos, Optional<String> name) {
        this.blockEntityPos = blockEntityPos;
        this.name = name;
    }

    public static void encode(TargetUpdateNamePKT message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.blockEntityPos);
        buffer.writeOptional(message.name, FriendlyByteBuf::writeUtf);
    }

    public static TargetUpdateNamePKT decode(FriendlyByteBuf buffer) {
        return new TargetUpdateNamePKT(buffer.readBlockPos(), buffer.readOptional(FriendlyByteBuf::readUtf));
    }

    public static class Handler {
        public static void handle(TargetUpdateNamePKT message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                //noinspection ConstantConditions
                BlockEntity blockEntity = context.get().getSender().getLevel().getBlockEntity(message.blockEntityPos);
                if (blockEntity instanceof TerminalBlockEntity terminalBlockEntity) {
                    terminalBlockEntity.getTargetData().ifPresent(data -> data.setName(message.name));
                }
            });
            context.get().setPacketHandled(true);
        }
    }
}
