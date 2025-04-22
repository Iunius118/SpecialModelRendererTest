package com.example.specialmodelrenderertes;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL14;

import java.util.function.Function;

public class ExampleItemSpecialRenderer implements SpecialModelRenderer<ItemStack> {
    ResourceLocation TEXTURE = SpecialModelRendererTest.id("textures/item/example_item.png");

    @Override
    public void render(@Nullable ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack,
                       MultiBufferSource multiBufferSource, int light, int overlay, boolean hasFoil) {
        if (itemStack == null) {
            return;
        }

        poseStack.pushPose();

        // Render with translucent render type
        PoseStack.Pose pose = poseStack.last();
        renderTestQuads(pose, multiBufferSource.getBuffer(ModRenderType.getTranslucentRenderType(TEXTURE)), light);
        // Render with unlit translucent render type
        pose.translate(0F, 1F, 0F);
        renderTestQuads(pose, multiBufferSource.getBuffer(ModRenderType.getUnlitTranslucentRenderType(TEXTURE)), light);
        // Render with add render type
        pose.translate(0F, 1F, 0F);
        renderTestQuads(pose, multiBufferSource.getBuffer(ModRenderType.getAddRenderType(TEXTURE)), light);
        // Render with sub render type
        pose.translate(0F, 1F, 0F);
        renderTestQuads(pose, multiBufferSource.getBuffer(ModRenderType.getSubRenderType(TEXTURE)), light);

        poseStack.popPose();
    }

    private static void renderTestQuads(PoseStack.Pose pose, VertexConsumer vertexConsumer, int light) {
        Vector3f pos1 = new Vector3f(0F, 0F, 0.5F);
        Vector3f pos2 = new Vector3f(0F, 0F, -0.5F);
        Vector3f pos3 = new Vector3f(0F, 1F, -0.5F);
        Vector3f pos4 = new Vector3f(0F, 1F, 0.5F);
        Vector3f pos5 = new Vector3f(0.25F, 0.25F, 0.25F);
        Vector3f pos6 = new Vector3f(0.25F, 0.25F, -0.25F);
        Vector3f pos7 = new Vector3f(0.25F, 0.75F, -0.25F);
        Vector3f pos8 = new Vector3f(0.25F, 0.75F, 0.25F);
        Vector3f normal1 = new Vector3f(1F, 0F, 0F);
        Vector3f normal2 = new Vector3f(-1F, 0F, 0F);

        // Render large square
        renderQuad(pose, vertexConsumer, 0xFFFF0000, light, pos1, pos2, pos3, pos4, normal1);
        // Render small square
        renderQuad(pose, vertexConsumer, 0xFFFFFFFF, light, pos5, pos6, pos7, pos8, normal1);
        // On the other side:
        // Render small square
        renderQuad(pose, vertexConsumer, 0x7FFFFFFF, light, pos8, pos7, pos6, pos5, normal2);
        // Render large square
        renderQuad(pose, vertexConsumer, 0x7FFF0000, light, pos4, pos3, pos2, pos1, normal2);
    }

    private static void renderQuad(PoseStack.Pose pose, VertexConsumer vertexConsumer, int color, int light,
                                   Vector3f pos1, Vector3f pos2, Vector3f pos3, Vector3f pos4, Vector3f normal) {
        addVertex(pose, vertexConsumer, color, light, pos1, normal);
        addVertex(pose, vertexConsumer, color, light, pos2, normal);
        addVertex(pose, vertexConsumer, color, light, pos3, normal);
        addVertex(pose, vertexConsumer, color, light, pos4, normal);
    }

    private static void addVertex(PoseStack.Pose pose, VertexConsumer vertexConsumer, int color, int light,
                                  Vector3f pos, Vector3f normal) {
        vertexConsumer.addVertex(pose, pos.x(), pos.y(), pos.z())
                .setColor(color)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, normal.x(), normal.y(), normal.z());
    }

    @Override
    public @Nullable ItemStack extractArgument(ItemStack itemStack) {
        return itemStack;
    }

    public static void register() {
        SpecialModelRenderers.ID_MAPPER.put(SpecialModelRendererTest.id("example_item"), Unbaked.MAP_CODEC);
    }

    public static class Unbaked implements SpecialModelRenderer.Unbaked {
        // No default values
        public static final MapCodec<ExampleItemSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new ExampleItemSpecialRenderer.Unbaked());

        @Override
        public @Nullable SpecialModelRenderer<?> bake(EntityModelSet p_388631_) {
            return new ExampleItemSpecialRenderer();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }

    public static class ModRenderPipelines {
        // Item render pipeline
        public static final RenderPipeline EXAMPLE_TRANSLUCENT = RenderPipelines.register(
                // Use the entity snippet
                RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
                        .withLocation("pipeline/example_translucent")
                        .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                        .withSampler("Sampler1")
                        .withBlend(BlendFunction.TRANSLUCENT)
                        .build()
        );
        // Unlit item render pipeline
        public static final RenderPipeline.Snippet EXAMPLE_UNLIT_SNIPPET = RenderPipeline
                .builder(RenderPipelines.MATRICES_COLOR_FOG_SNIPPET)
                // Use the beacon beam shaders
                .withVertexShader("core/rendertype_beacon_beam")
                .withFragmentShader("core/rendertype_beacon_beam")
                .withSampler("Sampler0")
                .withVertexFormat(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS)
                .buildSnippet();
        public static final RenderPipeline EXAMPLE_UNLIT_TRANSLUCENT = RenderPipelines.register(
                RenderPipeline.builder(EXAMPLE_UNLIT_SNIPPET)
                        .withLocation("pipeline/example_unlit_translucent")
                        .withBlend(BlendFunction.TRANSLUCENT)
                        .build()
        );
        public static final RenderPipeline EXAMPLE_ADD = RenderPipelines.register(
                RenderPipeline.builder(EXAMPLE_UNLIT_SNIPPET)
                        .withLocation("pipeline/example_add")
                        // Use the lightning blend function (additive and alpha)
                        .withBlend(BlendFunction.LIGHTNING)
                        .build()
        );
    }

    public static class ModRenderType {
        // Item render type
        private static final Function<ResourceLocation, RenderType> EXAMPLE_TRANSLUCENT = Util.memoize(
            resourceLocation -> {
                RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                        .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, TriState.FALSE, false))
                        .setLightmapState(RenderType.LIGHTMAP)
                        .setOverlayState(RenderType.OVERLAY)
                        .createCompositeState(true);
                return RenderType.create("example_translucent", 1536, true, true, ModRenderPipelines.EXAMPLE_TRANSLUCENT, compositeState);
            }
        );
        // Item unlit render type
        private static final Function<ResourceLocation, RenderType> EXAMPLE_UNLIT_TRANSLUCENT = Util.memoize(
                resourceLocation -> {
                    RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                            .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, TriState.FALSE, false))
                            .setLightmapState(RenderType.LIGHTMAP)
                            .setOverlayState(RenderType.OVERLAY)
                            .createCompositeState(true);
                    return RenderType.create("example_translucent_unlit", 1536, true, true, ModRenderPipelines.EXAMPLE_UNLIT_TRANSLUCENT, compositeState);
                }
        );
        // Item add render type
        private static final Function<ResourceLocation, RenderType> EXAMPLE_ADD = Util.memoize(
                resourceLocation -> {
                    RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                            .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, TriState.FALSE, false))
                            .setLightmapState(RenderType.LIGHTMAP)
                            .setOverlayState(RenderType.OVERLAY)
                            .createCompositeState(true);
                    return RenderType.create("example_add", 1536, true, true, ModRenderPipelines.EXAMPLE_ADD, compositeState);
                }
        );
        // Item sub render type
        private static final Function<ResourceLocation, RenderType> EXAMPLE_SUB = Util.memoize(
                resourceLocation -> {
                    // Create a custom output state that uses the main target with reverse subtract blend equation
                    RenderStateShard.OutputStateShard MAIN_TARGET_SUB = new RenderStateShard.OutputStateShard("main_target_sub",
                            () -> Minecraft.getInstance().getMainRenderTarget()) {

                        // Switch blend equation to reverse subtract
                        @Override
                        public void setupRenderState() {
                            RenderSystem.assertOnRenderThread();
                            GL14.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
                        }

                        // Return to default blend equation
                        @Override
                        public void clearRenderState() {
                            RenderSystem.assertOnRenderThread();
                            GL14.glBlendEquation(GL14.GL_FUNC_ADD);
                        }
                    };
                    RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                            .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, TriState.FALSE, false))
                            .setLightmapState(RenderType.LIGHTMAP)
                            .setOverlayState(RenderType.OVERLAY)
                            .setOutputState(MAIN_TARGET_SUB)
                            .createCompositeState(true);
                    return RenderType.create("example_sub", 1536, true, true, ModRenderPipelines.EXAMPLE_ADD, compositeState);
                }
        );

        public static RenderType getTranslucentRenderType(ResourceLocation texture) {
            return EXAMPLE_TRANSLUCENT.apply(texture);
        }

        public static RenderType getUnlitTranslucentRenderType(ResourceLocation texture) {
            return EXAMPLE_UNLIT_TRANSLUCENT.apply(texture);
        }

        public static RenderType getAddRenderType(ResourceLocation texture) {
            return EXAMPLE_ADD.apply(texture);
        }

        public static RenderType getSubRenderType(ResourceLocation texture) {
            return EXAMPLE_SUB.apply(texture);
        }
    }
}
