package ru.donyka.chunkanimator.mixin.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import ru.donyka.chunkanimator.AnimationOffset;
import ru.donyka.chunkanimator.ChunkAnimator;

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

    @ModifyArgs(
            method = SET_MODEL_MATRIX_UNIFORMS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/shader/ChunkShaderInterface;setRegionOffset(FFF)V",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private static void chunkanimator$offsetRegion(Args args, @Local(argsOnly = true, ordinal = 1) Object region) {
        chunkanimator$applyOffset(args, region);
    }

    @ModifyArgs(
            method = SET_MODEL_MATRIX_UNIFORMS_WITH_CHUNK_DATA,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/shader/ChunkShaderInterface;setRegionOffset(FFF)V",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private static void chunkanimator$offsetRegionWithChunkData(Args args, @Local(argsOnly = true, ordinal = 1) Object region) {
        chunkanimator$applyOffset(args, region);
    }

    private static void chunkanimator$applyOffset(Args args, Object region) {
        AnimationOffset offset = ChunkAnimator.animationHandler().offsetFor(region);

        if (!offset.isZero()) {
            args.set(0, ((Float) args.get(0)) + offset.x());
            args.set(1, ((Float) args.get(1)) + offset.y());
            args.set(2, ((Float) args.get(2)) + offset.z());
        }
    }
}
