package ru.donyka.chunkanimator.mixin.sodium.legacy;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.donyka.chunkanimator.compat.SodiumShaderSupport;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.chunk.RenderSection", remap = false)
public abstract class LegacySodiumRenderSectionMixin {
    @Inject(method = "setInfo", at = @At("HEAD"), remap = false, require = 0)
    private void chunkanimator$setInfo(@Coerce Object info, CallbackInfo ci) {
        SodiumShaderSupport.markSectionBuilt(this, info != null);
    }

    @Inject(method = "delete", at = @At("HEAD"), remap = false, require = 0)
    private void chunkanimator$delete(CallbackInfo ci) {
        SodiumShaderSupport.markSectionBuilt(this, false);
    }
}
