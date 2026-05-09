package ru.donyka.chunkanimator;

public final class AnimationOffset {
    public static final AnimationOffset ZERO = new AnimationOffset(0.0F, 0.0F, 0.0F);

    private final float x;
    private final float y;
    private final float z;

    public AnimationOffset(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float z() {
        return z;
    }

    public boolean isZero() {
        return x == 0.0F && y == 0.0F && z == 0.0F;
    }
}

