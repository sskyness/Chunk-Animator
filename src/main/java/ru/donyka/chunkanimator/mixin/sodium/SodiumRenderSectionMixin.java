package ru.donyka.chunkanimator.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.donyka.chunkanimator.compat.SodiumShaderSupport;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.RenderSection", remap = false)
public abstract class SodiumRenderSectionMixin {
    @Inject(method = "setInfo", at = @At("HEAD"), remap = false, require = 0)
    private void chunkanimator$setInfo(@Coerce Object info, CallbackInfoReturnable<Boolean> cir) {
        SodiumShaderSupport.markSectionBuilt(this, info != null);
    }
}
