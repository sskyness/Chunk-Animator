package ru.donyka.chunkanimator.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.donyka.chunkanimator.AnimationOffset;
import ru.donyka.chunkanimator.ChunkAnimator;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @ModifyArg(
            method = "renderBlockLayers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gl/DynamicUniforms$ChunkSectionsValue;<init>(Lorg/joml/Matrix4fc;IIIFII)V"
            ),
            index = 0
    )
    private Matrix4fc chunkanimator$offsetChunk(Matrix4fc original, @Local ChunkBuilder.BuiltChunk builtChunk) {
        AnimationOffset offset = ChunkAnimator.animationHandler().offsetFor(builtChunk);

        if (offset.isZero()) {
            return original;
        }

        return new Matrix4f(original).translate(offset.x(), offset.y(), offset.z());
    }
}
