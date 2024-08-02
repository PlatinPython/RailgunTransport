package platinpython.railgun_transport.util.capsule.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import platinpython.railgun_transport.RailgunTransport;

import java.util.HashSet;

@EventBusSubscriber(modid = RailgunTransport.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class MovingCapsuleRendering {
    private static final HashSet<MovingCapsuleClient> movingCapsules = new HashSet<>();

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
        movingCapsules.forEach(
            c -> c.render(
                event.getPartialTick().getGameTimeDeltaPartialTick(false), poseStack,
                Minecraft.getInstance().renderBuffers().bufferSource()
            )
        );
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch(Sheets.cutoutBlockSheet());
        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!Minecraft.getInstance().isPaused()) {
            movingCapsules.removeIf(MovingCapsuleClient::tick);
        }
    }

    public static void addMovingCapsule(MovingCapsuleClient capsule) {
        movingCapsules.add(capsule);
    }

    public static void clearMovingCapsules() {
        movingCapsules.clear();
    }
}
