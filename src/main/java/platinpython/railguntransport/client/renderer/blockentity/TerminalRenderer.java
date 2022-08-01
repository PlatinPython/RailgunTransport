package platinpython.railguntransport.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.EmptyModelData;
import platinpython.railguntransport.block.TerminalBlock;
import platinpython.railguntransport.block.entity.TerminalBlockEntity;
import platinpython.railguntransport.util.ModelLocations;
import platinpython.railguntransport.util.multiblock.MultiblockType;

public class TerminalRenderer implements BlockEntityRenderer<TerminalBlockEntity> {
    private final ModelBlockRenderer modelRenderer;

    public TerminalRenderer(BlockEntityRendererProvider.Context context) {
        this.modelRenderer = context.getBlockRenderDispatcher().getModelRenderer();
    }

    @Override
    public void render(TerminalBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        MultiblockType multiblockType = blockEntity.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE);
        if (multiblockType != MultiblockType.NONE) {
            poseStack.pushPose();
            Direction direction = blockEntity.getBlockState().getValue(TerminalBlock.HORIZONTAL_FACING).getOpposite();
            poseStack.translate(direction.getStepX() * 2, direction.getStepY() * 2, direction.getStepZ() * 2);
            //noinspection ConstantConditions
            this.modelRenderer.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null,
                                           Minecraft.getInstance().getModelManager().getModel(ModelLocations.BASE), 1F,
                                           1F, 1F, LevelRenderer.getLightColor(Minecraft.getInstance().level,
                                                                               blockEntity.getBlockState(),
                                                                               blockEntity.getBlockPos()
                    ), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
            );
            if (multiblockType == MultiblockType.RAILGUN) {
                renderRailgun(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
            } else if (multiblockType == MultiblockType.TARGET) {
                renderTarget(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
            }
            poseStack.popPose();
        }
    }

    @SuppressWarnings("unused")
    private void renderRailgun(TerminalBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                               MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (blockEntity.getRailgunData().isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        float yawDegrees = (float) (blockEntity.getRailgunData().get().getYaw() + 180F);
        Quaternion yaw = Vector3f.YP.rotationDegrees(yawDegrees);
        poseStack.mulPose(yaw);
        poseStack.translate(-0.5, -0.5, -0.5);
        poseStack.translate(0, 4D / 16D, 0);
        //noinspection ConstantConditions
        this.modelRenderer.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null,
                                       Minecraft.getInstance().getModelManager().getModel(ModelLocations.Railgun.MOUNT),
                                       1F, 1F, 1F, LevelRenderer.getLightColor(Minecraft.getInstance().level,
                                                                               blockEntity.getBlockState(),
                                                                               blockEntity.getBlockPos().above()
                ), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );
        //TODO: figure out pitch
//        poseStack.translate(0.5, 0.5, 0.5);
//        yaw.conj();
//        poseStack.mulPose(yaw);
//        yaw.conj();
//        poseStack.translate(-0.5, -0.5, -0.5);
//        Vector3f localX = Vector3f.XP.copy();
//        localX.transform(yaw);
//        float x = (float) Math.cos(Math.toRadians(yawDegrees));
//        float z = (float) Math.sin(Math.toRadians(yawDegrees));
//        poseStack.translate(x, 7D / 16D, z);
//        poseStack.mulPose(localX.rotationDegrees((float) blockEntity.getRailgunData().get().getPitch()));
//        poseStack.translate(-x, -7D / 16D, -z);
//        poseStack.translate(0.5, 0.5, 0.5);
//        poseStack.mulPose(yaw);
//        poseStack.translate(-0.5, -0.5, -0.5);
        poseStack.translate(0, 7D / 16D, 11D / 16D);
        this.modelRenderer.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null,
                                       Minecraft.getInstance().getModelManager().getModel(ModelLocations.Railgun.BODY),
                                       1F, 1F, 1F, LevelRenderer.getLightColor(Minecraft.getInstance().level,
                                                                               blockEntity.getBlockState(),
                                                                               blockEntity.getBlockPos().above()
                ), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );
        poseStack.translate(0, 3D / 16D, -11D / 16D);
        this.modelRenderer.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null,
                                       Minecraft.getInstance().getModelManager().getModel(ModelLocations.Railgun.HOLD),
                                       1F, 1F, 1F, LevelRenderer.getLightColor(Minecraft.getInstance().level,
                                                                               blockEntity.getBlockState(),
                                                                               blockEntity.getBlockPos().above()
                ), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );
        poseStack.popPose();
    }

    //TODO: figure out pitch
    @SuppressWarnings("unused")
    private void renderTarget(TerminalBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                              MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (blockEntity.getTargetData().isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        float yawDegrees = (float) (blockEntity.getTargetData().get().getYaw() + 180F);
        Quaternion yaw = Vector3f.YP.rotationDegrees(yawDegrees);
        poseStack.mulPose(yaw);
        poseStack.translate(-0.5, -0.5, -0.5);
        poseStack.translate(0, 4D / 16D, 0);
        //noinspection ConstantConditions
        this.modelRenderer.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null,
                                       Minecraft.getInstance().getModelManager().getModel(ModelLocations.Target.MOUNT),
                                       1F, 1F, 1F, LevelRenderer.getLightColor(Minecraft.getInstance().level,
                                                                               blockEntity.getBlockState(),
                                                                               blockEntity.getBlockPos()
                ), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );
        poseStack.pushPose();
        poseStack.translate(0, 14D / 16D, 0);
        this.modelRenderer.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null,
                                       Minecraft.getInstance()
                                                .getModelManager()
                                                .getModel(ModelLocations.Target.SHAFT_FRONT), 1F, 1F, 1F,
                                       LevelRenderer.getLightColor(Minecraft.getInstance().level,
                                                                   blockEntity.getBlockState(),
                                                                   blockEntity.getBlockPos()
                                       ), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );
        poseStack.translate(0, 1D / 16D, 10D / 16D);
        this.modelRenderer.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null,
                                       Minecraft.getInstance()
                                                .getModelManager()
                                                .getModel(ModelLocations.Target.SHAFT_MIDDLE), 1F, 1F, 1F,
                                       LevelRenderer.getLightColor(Minecraft.getInstance().level,
                                                                   blockEntity.getBlockState(),
                                                                   blockEntity.getBlockPos()
                                       ), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );
        poseStack.translate(0, -1D / 16D, 10D / 16D);
        this.modelRenderer.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null,
                                       Minecraft.getInstance()
                                                .getModelManager()
                                                .getModel(ModelLocations.Target.SHAFT_BACK), 1F, 1F, 1F,
                                       LevelRenderer.getLightColor(Minecraft.getInstance().level,
                                                                   blockEntity.getBlockState(),
                                                                   blockEntity.getBlockPos()
                                       ), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.translate(0, 14D / 16D, -10D / 16D);
        this.modelRenderer.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null,
                                       Minecraft.getInstance()
                                                .getModelManager()
                                                .getModel(ModelLocations.Target.CLAW_UP), 1F, 1F, 1F,
                                       LevelRenderer.getLightColor(Minecraft.getInstance().level,
                                                                   blockEntity.getBlockState(),
                                                                   blockEntity.getBlockPos()
                                       ), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );
        this.modelRenderer.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null,
                                       Minecraft.getInstance()
                                                .getModelManager()
                                                .getModel(ModelLocations.Target.CLAW_DOWN), 1F, 1F, 1F,
                                       LevelRenderer.getLightColor(Minecraft.getInstance().level,
                                                                   blockEntity.getBlockState(),
                                                                   blockEntity.getBlockPos()
                                       ), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );
        this.modelRenderer.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null,
                                       Minecraft.getInstance()
                                                .getModelManager()
                                                .getModel(ModelLocations.Target.CLAW_LEFT), 1F, 1F, 1F,
                                       LevelRenderer.getLightColor(Minecraft.getInstance().level,
                                                                   blockEntity.getBlockState(),
                                                                   blockEntity.getBlockPos()
                                       ), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );
        this.modelRenderer.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null,
                                       Minecraft.getInstance()
                                                .getModelManager()
                                                .getModel(ModelLocations.Target.CLAW_RIGHT), 1F, 1F, 1F,
                                       LevelRenderer.getLightColor(Minecraft.getInstance().level,
                                                                   blockEntity.getBlockState(),
                                                                   blockEntity.getBlockPos()
                                       ), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );
        poseStack.popPose();
        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(TerminalBlockEntity blockEntity, Vec3 cameraPos) {
        if (blockEntity.getBlockState().getValue(TerminalBlock.MULTIBLOCK_TYPE) == MultiblockType.NONE) {
            return false;
        }
        return BlockEntityRenderer.super.shouldRender(blockEntity, cameraPos);
    }

    @Override
    public boolean shouldRenderOffScreen(TerminalBlockEntity blockEntity) {
        return true;
    }
}
