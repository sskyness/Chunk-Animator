package ru.donyka.chunkanimator.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import ru.donyka.chunkanimator.AnimationOffset;
import ru.donyka.chunkanimator.ChunkAnimator;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @ModifyArgs(
            method = "renderLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V",
                    ordinal = 0
            )
    )
    private void chunkanimator$offsetChunk(Args args, @Local ChunkBuilder.BuiltChunk builtChunk) {
        AnimationOffset offset = ChunkAnimator.animationHandler().offsetFor(builtChunk);

        if (!offset.isZero()) {
            args.set(0, ((Double) args.get(0)) + offset.x());
            args.set(1, ((Double) args.get(1)) + offset.y());
            args.set(2, ((Double) args.get(2)) + offset.z());
        }
    }
}
