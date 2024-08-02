package platinpython.railgun_transport.util.capsule.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import platinpython.railgun_transport.block.CapsuleBlock;
import platinpython.railgun_transport.util.registries.BlockRegistry;

public class MovingCapsuleClient {
    private final BlockPos start;
    private final BlockPos target;
    private final int totalTicks;
    private int remainingTicks;

    public MovingCapsuleClient(BlockPos start, BlockPos target, int totalTicks, int remainingTicks) {
        this.start = start;
        this.target = target;
        this.totalTicks = totalTicks;
        this.remainingTicks = remainingTicks;
    }

    public boolean tick() {
        return this.remainingTicks-- <= 0;
    }

    public void render(float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource) {
        Vec3 start = Vec3.atLowerCornerOf(this.start);
        Vec3 target = Vec3.atLowerCornerOf(this.target);
        Vec3 distance = target.subtract(start);

        double yDiff = target.y - start.y;
        double throwHeight = distance.length() * 0.6D + yDiff;

        float progress =
            ((this.totalTicks + 0.5F) - (this.remainingTicks + 1F - partialTicks)) / (this.totalTicks + 0.5F);
        Vec3 blockLocationXZ = Vec3.ZERO.add(target.subtract(start).scale(progress).multiply(1D, 0D, 1D));

        double yOffset = 2 * (1 - progress) * progress * throwHeight + progress * progress * yDiff;
        Vec3 blockLocation = blockLocationXZ.add(0D, yOffset, 0D);

        poseStack.pushPose();
        poseStack.translate(this.start.getX(), this.start.getY(), this.start.getZ());
        poseStack.translate(blockLocation.x, blockLocation.y, blockLocation.z);

        poseStack.translate(0.5, 0.5, 0.5);
        Quaternionf yRot = Axis.YN.rotation((float) Math.atan2(distance.z, distance.x));

        poseStack.mulPose(yRot);
        poseStack.translate(-0.5, -0.5, -0.5);
        // noinspection ConstantConditions
        Minecraft.getInstance()
            .getBlockRenderer()
            .renderSingleBlock(
                BlockRegistry.CAPSULE.get().defaultBlockState().setValue(CapsuleBlock.FACING, Direction.EAST),
                poseStack, bufferSource,
                LevelRenderer.getLightColor(
                    Minecraft.getInstance().level, Blocks.AIR.defaultBlockState(),
                    this.start.offset((int) blockLocation.x, (int) blockLocation.y, (int) blockLocation.z)
                ), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid()
            );
        poseStack.popPose();
    }

    public BlockPos start() {
        return start;
    }

    public BlockPos target() {
        return target;
    }

    public int totalTicks() {
        return totalTicks;
    }

    public int remainingTicks() {
        return remainingTicks;
    }
}
