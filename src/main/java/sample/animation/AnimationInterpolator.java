package sample.animation;

import javafx.animation.Interpolator;

public final class AnimationInterpolator{

    public AnimationInterpolator() {
        throw new IllegalStateException("AnimateFX Interpolator");
    }

    public static final Interpolator WEB_EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);

}
