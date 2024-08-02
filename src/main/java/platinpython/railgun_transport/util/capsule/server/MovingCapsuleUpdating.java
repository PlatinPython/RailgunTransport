package platinpython.railgun_transport.util.capsule.server;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import platinpython.railgun_transport.RailgunTransport;
import platinpython.railgun_transport.util.saveddata.MovingCapsuleSavedData;

@EventBusSubscriber(modid = RailgunTransport.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class MovingCapsuleUpdating {
    @SubscribeEvent
    public static void onServerTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (MovingCapsuleSavedData.isPresent(serverLevel.getDataStorage())) {
            MovingCapsuleSavedData savedData = MovingCapsuleSavedData.get(serverLevel.getDataStorage());
            savedData.tick(serverLevel);
            if (serverLevel.getGameTime() % 5 == 0) {
                savedData.sync(serverLevel);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        MovingCapsuleSavedData.get(serverLevel.getDataStorage()).sync(serverPlayer);
    }
}
