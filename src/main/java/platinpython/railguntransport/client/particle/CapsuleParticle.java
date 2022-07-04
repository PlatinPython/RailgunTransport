package platinpython.railguntransport.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.renderable.BakedRenderable;
import platinpython.railguntransport.RailgunTransport;

public class CapsuleParticle extends Particle {
    private final BakedRenderable model = BakedRenderable.of(Minecraft.getInstance()
                                                                      .getModelManager()
                                                                      .getModel(new ResourceLocation(
                                                                              RailgunTransport.MOD_ID,
                                                                              "block/capsule"
                                                                      )));
    private final RenderType renderType = RenderType.solid();

    private final PoseStack poseStack = new PoseStack();

    public CapsuleParticle(ClientLevel level, Vec3 pos, Vec3 motion) {
        super(level, pos.x, pos.y, pos.z + 0F);
        this.xd = motion.x;
        this.yd = motion.y;
        this.zd = motion.z;
        this.gravity = 1F;
        this.lifetime = 200;
        this.hasPhysics = true;
        this.setSize(1.5F, 1.5F);
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        poseStack.setIdentity();
        Vec3 position = renderInfo.getPosition();
        poseStack.translate(this.x - position.x, this.y - position.y, this.z - position.z);
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(90F));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        model.render(poseStack, bufferSource, resLoc -> renderType, getLightColor(partialTicks),
                     OverlayTexture.NO_OVERLAY, partialTicks, EmptyModelData.INSTANCE
        );
        LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), getBoundingBox(), 1F, 1F, 1F, 1F);
        bufferSource.endBatch();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }
}
