package ru.donyka.chunkanimator.mixin.sodium.legacy;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.donyka.chunkanimator.AnimationOffset;
import ru.donyka.chunkanimator.compat.SodiumRenderRegionBridge;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.chunk.DefaultChunkRenderer", remap = false)
public abstract class LegacySodiumDefaultChunkRendererMixin {
    private static final String SET_MODEL_MATRIX_UNIFORMS =
            "setModelMatrixUniforms(Lme/jellysquid/mods/sodium/client/render/chunk/shader/ChunkShaderInterface;" +
                    "Lme/jellysquid/mods/sodium/client/render/chunk/region/RenderRegion;" +
                    "Lme/jellysquid/mods/sodium/client/render/viewport/CameraTransform;)V";

    @Redirect(
            method = SET_MODEL_MATRIX_UNIFORMS,
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/render/chunk/region/RenderRegion;getOriginX()I",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private static int chunkanimator$getOriginX(@Coerce Object region) {
        SodiumRenderRegionBridge bridge = (SodiumRenderRegionBridge) region;
        AnimationOffset offset = bridge.chunkanimator$animationOffset();
        return bridge.chunkanimator$originX() + Math.round(offset.x());
    }

    @Redirect(
            method = SET_MODEL_MATRIX_UNIFORMS,
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/render/chunk/region/RenderRegion;getOriginY()I",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private static int chunkanimator$getOriginY(@Coerce Object region) {
        SodiumRenderRegionBridge bridge = (SodiumRenderRegionBridge) region;
        AnimationOffset offset = bridge.chunkanimator$animationOffset();
        return bridge.chunkanimator$originY() + Math.round(offset.y());
    }

    @Redirect(
            method = SET_MODEL_MATRIX_UNIFORMS,
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/render/chunk/region/RenderRegion;getOriginZ()I",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private static int chunkanimator$getOriginZ(@Coerce Object region) {
        SodiumRenderRegionBridge bridge = (SodiumRenderRegionBridge) region;
        AnimationOffset offset = bridge.chunkanimator$animationOffset();
        return bridge.chunkanimator$originZ() + Math.round(offset.z());
    }
}
