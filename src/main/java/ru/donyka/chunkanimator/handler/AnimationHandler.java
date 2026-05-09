package ru.donyka.chunkanimator.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import ru.donyka.chunkanimator.AnimationOffset;
import ru.donyka.chunkanimator.config.AnimationMode;
import ru.donyka.chunkanimator.config.ChunkAnimatorConfig;

import java.util.WeakHashMap;

public final class AnimationHandler {
    private final Minecraft client = Minecraft.getInstance();
    private final WeakHashMap<Object, AnimationData> timeStamps = new WeakHashMap<>();

    public AnimationOffset offsetFor(Object renderSection) {
        AnimationData animationData = timeStamps.get(renderSection);

        if (animationData == null) {
            return AnimationOffset.ZERO;
        }

        ChunkAnimatorConfig config = ChunkAnimatorConfig.get();
        int animationDuration = config.animationDuration;

        if (animationDuration <= 0) {
            timeStamps.remove(renderSection);
            return AnimationOffset.ZERO;
        }

        long time = animationData.timeStamp;

        if (time == -1L) {
            time = System.currentTimeMillis();
            animationData.timeStamp = time;

            if (config.mode == AnimationMode.HORIZONTAL_SLIDE_ALTERNATE && client.player != null) {
                animationData.chunkFacing = getChunkFacing(client.player, animationData.origin);
            }
        }

        long timeDifference = System.currentTimeMillis() - time;

        if (timeDifference >= animationDuration) {
            timeStamps.remove(renderSection);
            return AnimationOffset.ZERO;
        }

        return getOffset(config, animationData, timeDifference);
    }

    public void setOrigin(Object renderSection, BlockPos position) {
        if (client.player == null) {
            return;
        }

        BlockPos playerPos = zeroedPlayerPos(client.player);
        BlockPos centeredChunkPos = zeroedCenteredChunkPos(position);
        long distanceX = playerPos.getX() - centeredChunkPos.getX();
        long distanceZ = playerPos.getZ() - centeredChunkPos.getZ();
        boolean nearPlayer = distanceX * distanceX + distanceZ * distanceZ <= 64L * 64L;

        if (!ChunkAnimatorConfig.get().disableAroundPlayer || !nearPlayer) {
            Direction facing = ChunkAnimatorConfig.get().mode == AnimationMode.HORIZONTAL_SLIDE
                    ? getChunkFacing(playerPos, centeredChunkPos)
                    : null;
            timeStamps.put(renderSection, new AnimationData(-1L, facing, new BlockPos(position.getX(), position.getY(), position.getZ())));
        } else {
            timeStamps.remove(renderSection);
        }
    }

    public void clear() {
        timeStamps.clear();
    }

    private AnimationOffset getOffset(ChunkAnimatorConfig config, AnimationData animationData, long timeDifference) {
        BlockPos origin = animationData.origin;
        AnimationMode mode = config.mode;

        if (mode == AnimationMode.HYBRID) {
            mode = origin.getY() < horizonHeight() ? AnimationMode.BELOW : AnimationMode.ABOVE;
        }

        if (mode == AnimationMode.BELOW) {
            float distance = Math.abs(origin.getY() - minY());
            return new AnimationOffset(0.0F, -distance + ease(config, timeDifference, distance), 0.0F);
        }

        if (mode == AnimationMode.ABOVE) {
            float distance = Math.max(0, maxY() - origin.getY());
            return new AnimationOffset(0.0F, distance - ease(config, timeDifference, distance), 0.0F);
        }

        if (mode == AnimationMode.HORIZONTAL_SLIDE || mode == AnimationMode.HORIZONTAL_SLIDE_ALTERNATE) {
            Direction facing = animationData.chunkFacing;

            if (facing != null) {
                float distance = -(200.0F - ease(config, timeDifference, 200.0F));
                return new AnimationOffset(facing.getStepX() * distance, 0.0F, facing.getStepZ() * distance);
            }
        }

        return AnimationOffset.ZERO;
    }

    private float ease(ChunkAnimatorConfig config, long timeDifference, float distance) {
        return config.easingFunction.easeOut(timeDifference, 0.0F, distance, config.animationDuration);
    }

    private int minY() {
        return client.level == null ? 0 : client.level.getMinY();
    }

    private int maxY() {
        return client.level == null ? 256 : client.level.getMaxY();
    }

    private double horizonHeight() {
        return 63.0D;
    }

    private static BlockPos zeroedPlayerPos(LocalPlayer player) {
        BlockPos playerPos = player.blockPosition();
        return new BlockPos(playerPos.getX(), 0, playerPos.getZ());
    }

    private static BlockPos zeroedCenteredChunkPos(BlockPos position) {
        return new BlockPos(position.getX() + 8, 0, position.getZ() + 8);
    }

    private static Direction getChunkFacing(LocalPlayer player, BlockPos chunkOrigin) {
        return getChunkFacing(zeroedPlayerPos(player), zeroedCenteredChunkPos(chunkOrigin));
    }

    private static Direction getChunkFacing(BlockPos from, BlockPos to) {
        int differenceX = from.getX() - to.getX();
        int differenceZ = from.getZ() - to.getZ();
        int absoluteX = Math.abs(differenceX);
        int absoluteZ = Math.abs(differenceZ);

        if (absoluteX > absoluteZ) {
            return differenceX > 0 ? Direction.EAST : Direction.WEST;
        }

        return differenceZ > 0 ? Direction.SOUTH : Direction.NORTH;
    }

    private static final class AnimationData {
        private long timeStamp;
        private Direction chunkFacing;
        private final BlockPos origin;

        private AnimationData(long timeStamp, Direction chunkFacing, BlockPos origin) {
            this.timeStamp = timeStamp;
            this.chunkFacing = chunkFacing;
            this.origin = origin;
        }
    }
}

