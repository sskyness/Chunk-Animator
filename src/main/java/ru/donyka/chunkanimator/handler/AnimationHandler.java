package ru.donyka.chunkanimator.handler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import ru.donyka.chunkanimator.AnimationOffset;
import ru.donyka.chunkanimator.config.AnimationMode;
import ru.donyka.chunkanimator.config.ChunkAnimatorConfig;

import java.util.WeakHashMap;

public final class AnimationHandler {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final WeakHashMap<Object, AnimationData> timeStamps = new WeakHashMap<>();
    private final WeakHashMap<Object, BlockPos> completedOrigins = new WeakHashMap<>();

    public AnimationOffset offsetFor(Object builtChunk) {
        AnimationData animationData = timeStamps.get(builtChunk);

        if (animationData == null) {
            return AnimationOffset.ZERO;
        }

        ChunkAnimatorConfig config = ChunkAnimatorConfig.get();
        int animationDuration = config.animationDuration;

        if (animationDuration <= 0) {
            timeStamps.remove(builtChunk);
            return AnimationOffset.ZERO;
        }

        long time = animationData.timeStamp;

        if (time == -1L) {
            time = System.currentTimeMillis();
            animationData.timeStamp = time;

            if ((config.mode == AnimationMode.HORIZONTAL_SLIDE || config.mode == AnimationMode.HORIZONTAL_SLIDE_ALTERNATE)
                    && client.player != null) {
                animationData.chunkFacing = getChunkFacing(client.player, animationData.origin);
            }
        }

        long timeDifference = System.currentTimeMillis() - time;

        if (timeDifference >= animationDuration) {
            timeStamps.remove(builtChunk);
            completedOrigins.put(builtChunk, animationData.origin);
            return AnimationOffset.ZERO;
        }

        return getOffset(config, animationData, timeDifference);
    }

    public AnimationOffset offsetFor(Object builtChunk, BlockPos position) {
        if (!timeStamps.containsKey(builtChunk)) {
            BlockPos completedOrigin = completedOrigins.get(builtChunk);

            if (samePosition(completedOrigin, position)) {
                return AnimationOffset.ZERO;
            }

            setOrigin(builtChunk, position);
        }

        return offsetFor(builtChunk);
    }

    public void setOrigin(Object builtChunk, BlockPos position) {
        completedOrigins.remove(builtChunk);

        ClientPlayerEntity player = client.player;
        Direction facing = null;
        boolean nearPlayer = false;

        if (player != null) {
            BlockPos playerPos = zeroedPlayerPos(player);
            BlockPos centeredChunkPos = zeroedCenteredChunkPos(position);
            long distanceX = playerPos.getX() - centeredChunkPos.getX();
            long distanceZ = playerPos.getZ() - centeredChunkPos.getZ();
            nearPlayer = distanceX * distanceX + distanceZ * distanceZ <= 64L * 64L;

            if (ChunkAnimatorConfig.get().mode == AnimationMode.HORIZONTAL_SLIDE) {
                facing = getChunkFacing(playerPos, centeredChunkPos);
            }
        }

        if (!ChunkAnimatorConfig.get().disableAroundPlayer || !nearPlayer) {
            timeStamps.put(builtChunk, new AnimationData(-1L, facing, new BlockPos(position.getX(), position.getY(), position.getZ())));
        } else {
            timeStamps.remove(builtChunk);
            completedOrigins.put(builtChunk, new BlockPos(position.getX(), position.getY(), position.getZ()));
        }
    }

    public void clear() {
        timeStamps.clear();
        completedOrigins.clear();
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
                return new AnimationOffset(facing.getOffsetX() * distance, 0.0F, facing.getOffsetZ() * distance);
            }
        }

        return AnimationOffset.ZERO;
    }

    private float ease(ChunkAnimatorConfig config, long timeDifference, float distance) {
        return config.easingFunction.easeOut(timeDifference, 0.0F, distance, config.animationDuration);
    }

    private int minY() {
        return 0;
    }

    private int maxY() {
        return client.world == null ? 256 : client.world.getDimension().getLogicalHeight();
    }

    private double horizonHeight() {
        return 63.0D;
    }

    private static BlockPos zeroedPlayerPos(ClientPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        return new BlockPos(playerPos.getX(), 0, playerPos.getZ());
    }

    private static BlockPos zeroedCenteredChunkPos(BlockPos position) {
        return new BlockPos(position.getX() + 8, 0, position.getZ() + 8);
    }

    private static Direction getChunkFacing(ClientPlayerEntity player, BlockPos chunkOrigin) {
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

    private static boolean samePosition(BlockPos first, BlockPos second) {
        return first != null
                && second != null
                && first.getX() == second.getX()
                && first.getY() == second.getY()
                && first.getZ() == second.getZ();
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
