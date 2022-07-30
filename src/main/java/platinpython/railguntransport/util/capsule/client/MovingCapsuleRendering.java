package platinpython.railguntransport.util.capsule.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import platinpython.railguntransport.RailgunTransport;

import java.util.HashSet;

@Mod.EventBusSubscriber(modid = RailgunTransport.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
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
        movingCapsules.forEach(c -> c.render(event.getPartialTick(), poseStack,
                                             Minecraft.getInstance().renderBuffers().bufferSource()
        ));
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch(Sheets.cutoutBlockSheet());
        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !Minecraft.getInstance().isPaused()) {
            movingCapsules.removeIf(MovingCapsuleClient::tick);
        }
    }

    public static void addMovingCapsule(BlockPos start, BlockPos target, int totalTicks, int remainingTicks) {
        movingCapsules.add(new MovingCapsuleClient(start, target, totalTicks, remainingTicks));
    }

    public static void clearMovingCapsules() {
        movingCapsules.clear();
    }
}
