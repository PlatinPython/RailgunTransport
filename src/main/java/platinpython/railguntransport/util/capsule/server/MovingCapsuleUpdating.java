package platinpython.railguntransport.util.capsule.server;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import platinpython.railguntransport.RailgunTransport;
import platinpython.railguntransport.util.saveddata.MovingCapsuleSavedData;

@Mod.EventBusSubscriber(modid = RailgunTransport.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MovingCapsuleUpdating {
    @SubscribeEvent
    public static void onServerTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side != LogicalSide.SERVER) {
            return;
        }
        if (!(event.world instanceof ServerLevel serverLevel)) {
            return;
        }
        if (MovingCapsuleSavedData.isPresent(serverLevel.getDataStorage())) {
            MovingCapsuleSavedData savedData = MovingCapsuleSavedData.get(serverLevel.getDataStorage());
            savedData.tick(serverLevel);
            if (serverLevel.getGameTime() % 5 == 0) {
                savedData.sync(serverLevel.dimension());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getWorld() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        MovingCapsuleSavedData.get(serverLevel.getDataStorage()).sync(serverPlayer);
    }
}
