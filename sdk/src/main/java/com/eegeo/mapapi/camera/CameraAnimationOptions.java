package com.eegeo.mapapi.camera;


public class CameraAnimationOptions {
    public final double durationSeconds;
    public final double preferredAnimationSpeed;
    public final double minDuration;
    public final double maxDuration;
    public final double snapDistanceThreshold;
    public final boolean snapIfDistanceExceedsThreshold;
    public final boolean interruptByGestureAllowed;

    public final boolean hasExplicitDuration;
    public final boolean hasPreferredAnimationSpeed;
    public final boolean hasMinDuration;
    public final boolean hasMaxDuration;
    public final boolean hasSnapDistanceThreshold;

    private CameraAnimationOptions(
            double durationSeconds,
            double preferredAnimationSpeed,
            double minDuration,
            double maxDuration,
            double snapDistanceThreshold,
            boolean snapIfDistanceExceedsThreshold,
            boolean interruptByGestureAllowed,

            boolean hasExplicitDuration,
            boolean hasPreferredAnimationSpeed,
            boolean hasMinDuration,
            boolean hasMaxDuration,
            boolean hasSnapDistanceThreshold
    ) {
        this.durationSeconds = durationSeconds;
        this.preferredAnimationSpeed = preferredAnimationSpeed;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.snapDistanceThreshold = snapDistanceThreshold;
        this.snapIfDistanceExceedsThreshold = snapIfDistanceExceedsThreshold;
        this.interruptByGestureAllowed = interruptByGestureAllowed;

        this.hasExplicitDuration = hasExplicitDuration;
        this.hasPreferredAnimationSpeed = hasPreferredAnimationSpeed;
        this.hasMinDuration = hasMinDuration;
        this.hasMaxDuration = hasMaxDuration;
        this.hasSnapDistanceThreshold = hasSnapDistanceThreshold;
    }

    public static final class Builder {

        private double m_durationSeconds = 0.0;
        private double m_preferredAnimationSpeed = 0.0;
        private double m_minDuration = 0.0;
        private double m_maxDuration = 0.0;
        private double m_snapDistanceThreshold = 0.0;
        private boolean m_snapIfDistanceExceedsThreshold = true;
        private boolean m_interruptByGestureAllowed = true;


        private boolean m_hasExplicitDuration = false;
        private boolean m_hasPreferredAnimationSpeed = false;
        private boolean m_hasMinDuration = false;
        private boolean m_hasMaxDuration = false;
        private boolean m_hasSnapDistanceThreshold = false;

        public Builder duration(double durationSeconds) {
            m_durationSeconds = durationSeconds;
            m_hasExplicitDuration = true;
            return this;
        }

        public Builder preferredAnimationSpeed(double animationSpeedMetersPerSecond) {
            m_preferredAnimationSpeed = animationSpeedMetersPerSecond;
            m_hasPreferredAnimationSpeed = true;
            m_hasExplicitDuration = false;
            return this;
        }

        public Builder snapIfDistanceExceedsThreshold(boolean shouldSnap) {
            m_snapIfDistanceExceedsThreshold = shouldSnap;
            return this;
        }

        public Builder interruptByGestureAllowed(boolean isAllowed) {
            m_interruptByGestureAllowed = isAllowed;
            return this;
        }


        public Builder minDuration(double minDuration) {
            m_minDuration = minDuration;
            m_hasMinDuration = true;
            return this;
        }

        public Builder maxDuration(double maxDuration) {
            m_maxDuration = maxDuration;
            m_hasMaxDuration = true;
            return this;
        }

        public Builder snapDistanceThreshold(double snapDistanceThresholdMeters) {
            m_snapDistanceThreshold = snapDistanceThresholdMeters;
            m_hasSnapDistanceThreshold = true;
            return this;
        }

        public Builder() {
            super();
        }
        public CameraAnimationOptions Build() {
            return new CameraAnimationOptions(
                    m_durationSeconds,
                    m_preferredAnimationSpeed,
                    m_minDuration,
                    m_maxDuration,
                    m_snapDistanceThreshold,
                    m_snapIfDistanceExceedsThreshold,
                    m_interruptByGestureAllowed,
                    m_hasExplicitDuration,
                    m_hasPreferredAnimationSpeed,
                    m_hasMinDuration,
                    m_hasMaxDuration,
                    m_hasSnapDistanceThreshold
            );
        }

    }

}
