package ru.donyka.chunkanimator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import ru.donyka.chunkanimator.config.ChunkAnimatorConfig;
import ru.donyka.chunkanimator.handler.AnimationHandler;

public final class ChunkAnimator implements ClientModInitializer {
    public static final String MOD_ID = "chunkanimator";

    private static final AnimationHandler ANIMATION_HANDLER = new AnimationHandler();

    @Override
    public void onInitializeClient() {
        ChunkAnimatorConfig.load();
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> ANIMATION_HANDLER.clear());
    }

    public static AnimationHandler animationHandler() {
        return ANIMATION_HANDLER;
    }
}
