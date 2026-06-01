package ru.donyka.chunkanimator.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class ChunkAnimatorMixinPlugin implements IMixinConfigPlugin {
    private boolean sodiumLoaded;
    private boolean caffeineSodium;
    private boolean legacyJellysquidSodium;

    @Override
    public void onLoad(String mixinPackage) {
        sodiumLoaded = FabricLoader.getInstance().isModLoaded("sodium");

        if (sodiumLoaded) {
            String sodiumVersion = FabricLoader.getInstance()
                    .getModContainer("sodium")
                    .map(container -> container.getMetadata().getVersion().getFriendlyString())
                    .orElse("");
            caffeineSodium = isAtLeast(sodiumVersion, 0, 6);
            legacyJellysquidSodium = isAtLeast(sodiumVersion, 0, 5) && !caffeineSodium;
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith(".BuiltChunkMixin") || mixinClassName.endsWith(".WorldRendererMixin")) {
            return !sodiumLoaded;
        }

        if (mixinClassName.contains(".sodium.legacy.")) {
            return legacyJellysquidSodium;
        }

        if (mixinClassName.contains(".sodium.")) {
            return caffeineSodium;
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static boolean isAtLeast(String version, int minimumMajor, int minimumMinor) {
        int[] parts = parseVersion(version);

        if (parts[0] != minimumMajor) {
            return parts[0] > minimumMajor;
        }

        return parts[1] >= minimumMinor;
    }

    private static int[] parseVersion(String version) {
        String[] parts = version.split("[.+-]", 3);
        int major = parsePart(parts, 0);
        int minor = parsePart(parts, 1);
        return new int[]{major, minor};
    }

    private static int parsePart(String[] parts, int index) {
        if (index >= parts.length) {
            return 0;
        }

        try {
            return Integer.parseInt(parts[index]);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}