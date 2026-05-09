package ru.donyka.chunkanimator.mixin.sodium.legacy;

import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.donyka.chunkanimator.ChunkAnimator;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.chunk.region.RenderRegion", remap = false)
public abstract class LegacySodiumRenderRegionMixin {
    @Shadow
    public abstract int getOriginX();

    @Shadow
    public abstract int getOriginY();

    @Shadow
    public abstract int getOriginZ();

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void chunkanimator$init(int x, int y, int z, @Coerce Object stagingBuffer, CallbackInfo ci) {
        ChunkAnimator.animationHandler().setOrigin(this, new BlockPos(getOriginX(), getOriginY(), getOriginZ()));
    }
}
