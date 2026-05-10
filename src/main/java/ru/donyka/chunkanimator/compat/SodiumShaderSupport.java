package ru.donyka.chunkanimator.compat;

import org.lwjgl.opengl.GL20C;
import ru.donyka.chunkanimator.ChunkAnimator;
import ru.donyka.chunkanimator.config.ChunkAnimatorConfig;
import ru.donyka.chunkanimator.handler.AnimationHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class SodiumShaderSupport {
    private static final int REGION_WIDTH = 8;
    private static final int REGION_HEIGHT = 4;
    private static final int REGION_LENGTH = 8;
    private static final int REGION_SIZE = REGION_WIDTH * REGION_HEIGHT * REGION_LENGTH;
    private static final int UNSEEN = -2;
    private static final int DONE = -1;
    private static final long START_TIME = System.currentTimeMillis();

    private static final WeakHashMap<Object, RegionState> REGIONS = new WeakHashMap<>();
    private static final Map<Integer, UniformLocations> UNIFORMS = new HashMap<>();
    private static final int[] DISABLED_TIMES = new int[REGION_SIZE];

    static {
        Arrays.fill(DISABLED_TIMES, DONE);
    }

    private SodiumShaderSupport() {
    }

    public static void apply(Object region, int regionOriginX, int regionOriginY, int regionOriginZ) {
        int program = GL20C.glGetInteger(GL20C.GL_CURRENT_PROGRAM);

        if (program == 0) {
            return;
        }

        UniformLocations uniforms = UNIFORMS.get(program);

        if (uniforms == null) {
            uniforms = new UniformLocations(program);
            UNIFORMS.put(program, uniforms);
        }

        ChunkAnimatorConfig config = ChunkAnimatorConfig.get();

        if (config.animationDuration <= 0) {
            uniforms.setInt("u_ChunkAnimatorEnabled", 0);
            uniforms.setIntArray("u_ChunkAnimatorStartTimes", DISABLED_TIMES);
            return;
        }

        int now = now();
        RegionState state = stateFor(region, regionOriginX, regionOriginY, regionOriginZ);
        state.update(now, regionOriginX, regionOriginY, regionOriginZ, config.animationDuration);

        AnimationHandler handler = ChunkAnimator.animationHandler();

        uniforms.setInt("u_ChunkAnimatorEnabled", 1);
        uniforms.setInt("u_ChunkAnimatorMode", config.mode.ordinal());
        uniforms.setInt("u_ChunkAnimatorEasing", config.easingFunction.ordinal());
        uniforms.setInt("u_ChunkAnimatorCurrentTime", now);
        uniforms.setInt("u_ChunkAnimatorDuration", config.animationDuration);
        uniforms.setInt("u_ChunkAnimatorDisableAroundPlayer", config.disableAroundPlayer ? 1 : 0);
        uniforms.setIntArray("u_ChunkAnimatorStartTimes", state.times);
        uniforms.setFloat("u_ChunkAnimatorMinY", handler.shaderMinY());
        uniforms.setFloat("u_ChunkAnimatorMaxY", handler.shaderMaxY());
        uniforms.setFloat("u_ChunkAnimatorHorizon", (float) handler.shaderHorizonHeight());
        uniforms.setFloat("u_ChunkAnimatorRegionOriginX", regionOriginX);
        uniforms.setFloat("u_ChunkAnimatorRegionOriginY", regionOriginY);
        uniforms.setFloat("u_ChunkAnimatorRegionOriginZ", regionOriginZ);
        uniforms.setFloat("u_ChunkAnimatorPlayerX", handler.shaderPlayerBlockX());
        uniforms.setFloat("u_ChunkAnimatorPlayerZ", handler.shaderPlayerBlockZ());
    }

    public static void clear() {
        REGIONS.clear();
    }

    private static RegionState stateFor(Object region, int originX, int originY, int originZ) {
        RegionState state = REGIONS.get(region);

        if (state == null) {
            state = new RegionState(originX, originY, originZ);
            REGIONS.put(region, state);
            return state;
        }

        if (state.originX != originX || state.originY != originY || state.originZ != originZ) {
            state.reset(originX, originY, originZ);
        }

        return state;
    }

    private static int now() {
        return (int) Math.min(Integer.MAX_VALUE, System.currentTimeMillis() - START_TIME);
    }

    private static int localX(int sectionId) {
        return (sectionId >> 5) & 7;
    }

    private static int localY(int sectionId) {
        return sectionId & 3;
    }

    private static int localZ(int sectionId) {
        return (sectionId >> 2) & 7;
    }

    private static final class RegionState {
        private final int[] times = new int[REGION_SIZE];
        private int originX;
        private int originY;
        private int originZ;

        private RegionState(int originX, int originY, int originZ) {
            reset(originX, originY, originZ);
        }

        private void reset(int originX, int originY, int originZ) {
            this.originX = originX;
            this.originY = originY;
            this.originZ = originZ;
            Arrays.fill(times, UNSEEN);
        }

        private void update(int now, int regionOriginX, int regionOriginY, int regionOriginZ, int duration) {
            AnimationHandler handler = ChunkAnimator.animationHandler();
            boolean disableAroundPlayer = ChunkAnimatorConfig.get().disableAroundPlayer;

            for (int sectionId = 0; sectionId < times.length; sectionId++) {
                int time = times[sectionId];

                if (time == UNSEEN) {
                    int sectionOriginX = regionOriginX + localX(sectionId) * 16;
                    int sectionOriginY = regionOriginY + localY(sectionId) * 16;
                    int sectionOriginZ = regionOriginZ + localZ(sectionId) * 16;

                    if (disableAroundPlayer && handler.shaderIsNearPlayer(sectionOriginX, sectionOriginZ)) {
                        times[sectionId] = DONE;
                    } else {
                        times[sectionId] = now;
                    }
                } else if (time >= 0 && now - time >= duration) {
                    times[sectionId] = DONE;
                }
            }
        }
    }

    private static final class UniformLocations {
        private final int program;
        private final Map<String, Integer> locations = new HashMap<>();

        private UniformLocations(int program) {
            this.program = program;
        }

        private void setInt(String name, int value) {
            int location = location(name);

            if (location >= 0) {
                GL20C.glUniform1i(location, value);
            }
        }

        private void setIntArray(String name, int[] value) {
            int location = location(name + "[0]");

            if (location >= 0) {
                GL20C.glUniform1iv(location, value);
            }
        }

        private void setFloat(String name, float value) {
            int location = location(name);

            if (location >= 0) {
                GL20C.glUniform1f(location, value);
            }
        }

        private int location(String name) {
            Integer cached = locations.get(name);

            if (cached != null) {
                return cached;
            }

            int location = GL20C.glGetUniformLocation(program, name);
            locations.put(name, location);
            return location;
        }
    }
}
