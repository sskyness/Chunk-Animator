package ru.donyka.chunkanimator.mixin;

import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.donyka.chunkanimator.ChunkAnimator;

@Mixin(ChunkBuilder.BuiltChunk.class)
public abstract class BuiltChunkMixin {
    @Inject(method = "setSectionPos", at = @At("TAIL"))
    private void chunkanimator$setSectionPos(long sectionPos, CallbackInfo ci) {
        ChunkBuilder.BuiltChunk builtChunk = (ChunkBuilder.BuiltChunk) (Object) this;
        ChunkAnimator.animationHandler().setOrigin(builtChunk, builtChunk.getOrigin());
    }
}

