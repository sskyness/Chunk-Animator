package ru.donyka.chunkanimator.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ChunkAnimatorConfigScreen {
    private ChunkAnimatorConfigScreen() {
    }

    public static Screen create(Screen parent) {
        ChunkAnimatorConfig config = ChunkAnimatorConfig.get();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Chunk Animator"));
        ConfigEntryBuilder entries = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

        general.addEntry(entries.startEnumSelector(Component.literal("Mode"), AnimationMode.class, config.mode)
                .setDefaultValue(ChunkAnimatorConfig.DEFAULT_MODE)
                .setSaveConsumer(config::setMode)
                .build());
        general.addEntry(entries.startEnumSelector(Component.literal("Easing"), EasingFunction.class, config.easingFunction)
                .setDefaultValue(ChunkAnimatorConfig.DEFAULT_EASING_FUNCTION)
                .setSaveConsumer(config::setEasingFunction)
                .build());
        general.addEntry(entries.startIntSlider(Component.literal("Duration"), config.animationDuration, 0, 10000)
                .setDefaultValue(ChunkAnimatorConfig.DEFAULT_ANIMATION_DURATION)
                .setSaveConsumer(config::setAnimationDuration)
                .build());
        general.addEntry(entries.startBooleanToggle(Component.literal("Disable Around Player"), config.disableAroundPlayer)
                .setDefaultValue(ChunkAnimatorConfig.DEFAULT_DISABLE_AROUND_PLAYER)
                .setSaveConsumer(value -> config.disableAroundPlayer = value)
                .build());

        builder.setSavingRunnable(ChunkAnimatorConfig::save);
        return builder.build();
    }
}
