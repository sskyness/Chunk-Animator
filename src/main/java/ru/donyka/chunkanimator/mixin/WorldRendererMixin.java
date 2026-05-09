package ru.donyka.chunkanimator.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.joml.Vector3f;
import org.joml.Vector3fc;
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
                    target = "Lnet/minecraft/client/gl/DynamicUniforms$UniformValue;<init>(Lorg/joml/Matrix4fc;Lorg/joml/Vector4fc;Lorg/joml/Vector3fc;Lorg/joml/Matrix4fc;F)V"
            ),
            index = 2
    )
    private Vector3fc chunkanimator$offsetChunk(Vector3fc original, @Local ChunkBuilder.BuiltChunk builtChunk) {
        AnimationOffset offset = ChunkAnimator.animationHandler().offsetFor(builtChunk);

        if (offset.isZero()) {
            return original;
        }

        return new Vector3f(original).add(offset.x(), offset.y(), offset.z());
    }
}
