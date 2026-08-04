package ru.donyka.chunkanimator.mixin;

import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.donyka.chunkanimator.ChunkAnimator;

@Mixin(SectionRenderDispatcher.RenderSection.class)
public abstract class BuiltChunkMixin {
    @Inject(method = "setSectionNode", at = @At("TAIL"))
    private void chunkanimator$setSectionNode(long sectionNode, CallbackInfo ci) {
        SectionRenderDispatcher.RenderSection renderSection = (SectionRenderDispatcher.RenderSection) (Object) this;
        ChunkAnimator.animationHandler().setOrigin(renderSection, renderSection.getRenderOrigin());
    }
}

