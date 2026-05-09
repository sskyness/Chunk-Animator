package ru.donyka.chunkanimator.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.donyka.chunkanimator.AnimationOffset;
import ru.donyka.chunkanimator.compat.SodiumRenderRegionBridge;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.DefaultChunkRenderer", remap = false)
public abstract class SodiumDefaultChunkRendererMixin {
    private static final String SET_MODEL_MATRIX_UNIFORMS =
            "setModelMatrixUniforms(Lnet/caffeinemc/mods/sodium/client/render/chunk/shader/ChunkShaderInterface;" +
                    "Lnet/caffeinemc/mods/sodium/client/render/chunk/region/RenderRegion;" +
                    "Lnet/caffeinemc/mods/sodium/client/render/viewport/CameraTransform;)V";
    private static final String SET_MODEL_MATRIX_UNIFORMS_WITH_CHUNK_DATA =
            "setModelMatrixUniforms(Lnet/caffeinemc/mods/sodium/client/render/chunk/shader/ChunkShaderInterface;" +
                    "Lnet/caffeinemc/mods/sodium/client/render/chunk/region/RenderRegion;" +
                    "Lnet/caffeinemc/mods/sodium/client/render/viewport/CameraTransform;" +
                    "Lnet/caffeinemc/mods/sodium/client/gl/buffer/GlBuffer;)V";

    @Redirect(
            method = {SET_MODEL_MATRIX_UNIFORMS, SET_MODEL_MATRIX_UNIFORMS_WITH_CHUNK_DATA},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/region/RenderRegion;getOriginX()I",
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
            method = {SET_MODEL_MATRIX_UNIFORMS, SET_MODEL_MATRIX_UNIFORMS_WITH_CHUNK_DATA},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/region/RenderRegion;getOriginY()I",
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
            method = {SET_MODEL_MATRIX_UNIFORMS, SET_MODEL_MATRIX_UNIFORMS_WITH_CHUNK_DATA},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/region/RenderRegion;getOriginZ()I",
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
