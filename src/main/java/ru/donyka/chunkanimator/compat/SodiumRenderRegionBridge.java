package ru.donyka.chunkanimator.compat;

import ru.donyka.chunkanimator.AnimationOffset;

public interface SodiumRenderRegionBridge {
    int chunkanimator$originX();

    int chunkanimator$originY();

    int chunkanimator$originZ();

    AnimationOffset chunkanimator$animationOffset();
}
