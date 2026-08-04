package ru.donyka.chunkanimator;

import net.fabricmc.api.ClientModInitializer;
import ru.donyka.chunkanimator.config.ChunkAnimatorConfig;
import ru.donyka.chunkanimator.handler.AnimationHandler;

public final class ChunkAnimator implements ClientModInitializer {
    public static final String MOD_ID = "chunkanimator";

    private static final AnimationHandler ANIMATION_HANDLER = new AnimationHandler();

    @Override
    public void onInitializeClient() {
        ChunkAnimatorConfig.load();
    }

    public static AnimationHandler animationHandler() {
        return ANIMATION_HANDLER;
    }
}
