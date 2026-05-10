package ru.donyka.chunkanimator.mixin.sodium.legacy;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.donyka.chunkanimator.compat.SodiumShaderSourcePatcher;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.gl.shader.ShaderLoader", remap = false)
public abstract class LegacySodiumShaderLoaderMixin {
    @Inject(method = "getShaderSource", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void chunkanimator$patchShaderSource(@Coerce Object name, CallbackInfoReturnable<String> cir) {
        if (chunkanimator$isChunkVertexShader(name)) {
            cir.setReturnValue(SodiumShaderSourcePatcher.patch(cir.getReturnValue()));
        }
    }

    private static boolean chunkanimator$isChunkVertexShader(Object name) {
        String id = String.valueOf(name);
        return id.contains("sodium:") && id.contains("blocks/block_layer_opaque.vsh");
    }
}
