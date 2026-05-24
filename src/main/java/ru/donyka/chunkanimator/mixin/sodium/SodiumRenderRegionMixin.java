package ru.donyka.chunkanimator.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import ru.donyka.chunkanimator.compat.SodiumRenderRegionBridge;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.region.RenderRegion", remap = false)
public abstract class SodiumRenderRegionMixin implements SodiumRenderRegionBridge {
    @Shadow
    public abstract int getOriginX();

    @Shadow
    public abstract int getOriginY();

    @Shadow
    public abstract int getOriginZ();

    @Override
    public int chunkanimator$originX() {
        return getOriginX();
    }

    @Override
    public int chunkanimator$originY() {
        return getOriginY();
    }

    @Override
    public int chunkanimator$originZ() {
        return getOriginZ();
    }
}
