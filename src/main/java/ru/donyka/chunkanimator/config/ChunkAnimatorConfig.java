package ru.donyka.chunkanimator.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ChunkAnimatorConfig {
    public static final AnimationMode DEFAULT_MODE = AnimationMode.BELOW;
    public static final EasingFunction DEFAULT_EASING_FUNCTION = EasingFunction.SINE;
    public static final int DEFAULT_ANIMATION_DURATION = 1000;
    public static final boolean DEFAULT_DISABLE_AROUND_PLAYER = false;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "chunkanimator.json";
    private static ChunkAnimatorConfig instance;

    public AnimationMode mode = DEFAULT_MODE;
    public EasingFunction easingFunction = DEFAULT_EASING_FUNCTION;
    public int animationDuration = DEFAULT_ANIMATION_DURATION;
    public boolean disableAroundPlayer = DEFAULT_DISABLE_AROUND_PLAYER;

    public static ChunkAnimatorConfig get() {
        if (instance == null) {
            load();
        }

        return instance;
    }

    public static void load() {
        Path path = path();

        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                instance = GSON.fromJson(reader, ChunkAnimatorConfig.class);
            } catch (Exception ignored) {
                instance = new ChunkAnimatorConfig();
            }
        } else {
            instance = new ChunkAnimatorConfig();
            save();
        }

        if (instance == null) {
            instance = new ChunkAnimatorConfig();
        }

        instance.sanitize();
    }

    public static void save() {
        ChunkAnimatorConfig config = get();
        config.sanitize();

        try {
            Path path = path();
            Files.createDirectories(path.getParent());

            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
            }
        } catch (Exception ignored) {
        }
    }

    public void setMode(AnimationMode value) {
        mode = value == null ? DEFAULT_MODE : value;
    }

    public void setEasingFunction(EasingFunction value) {
        easingFunction = value == null ? DEFAULT_EASING_FUNCTION : value;
    }

    public void setAnimationDuration(int value) {
        animationDuration = Math.max(0, value);
    }

    private void sanitize() {
        if (mode == null) {
            mode = DEFAULT_MODE;
        }

        if (easingFunction == null) {
            easingFunction = DEFAULT_EASING_FUNCTION;
        }

        animationDuration = Math.max(0, animationDuration);
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }
}

