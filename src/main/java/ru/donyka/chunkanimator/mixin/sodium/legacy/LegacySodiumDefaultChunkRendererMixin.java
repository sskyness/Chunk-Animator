package ru.donyka.chunkanimator.mixin.sodium.legacy;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.donyka.chunkanimator.compat.SodiumRenderRegionBridge;
import ru.donyka.chunkanimator.compat.SodiumShaderSupport;

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
        SodiumShaderSupport.apply(
                region,
                bridge.chunkanimator$originX(),
                bridge.chunkanimator$originY(),
                bridge.chunkanimator$originZ()
        );
        return bridge.chunkanimator$originX();
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
        return ((SodiumRenderRegionBridge) region).chunkanimator$originY();
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
        return ((SodiumRenderRegionBridge) region).chunkanimator$originZ();
    }
}
