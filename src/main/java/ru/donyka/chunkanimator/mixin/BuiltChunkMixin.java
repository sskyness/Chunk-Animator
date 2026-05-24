package ru.donyka.chunkanimator.mixin;

import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.donyka.chunkanimator.ChunkAnimator;

@Mixin(ChunkBuilder.BuiltChunk.class)
public abstract class BuiltChunkMixin {
    @Inject(method = "setOrigin", at = @At("TAIL"))
    private void chunkanimator$setOrigin(int x, int y, int z, CallbackInfo ci) {
        ChunkAnimator.animationHandler().setOrigin((ChunkBuilder.BuiltChunk) (Object) this, new BlockPos(x, y, z));
    }
}
