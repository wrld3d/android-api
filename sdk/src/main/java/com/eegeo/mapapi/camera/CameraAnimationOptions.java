package com.eegeo.mapapi.camera;


/**
 * Encapsulates optional parameters for performing a camera animation
 */

public class CameraAnimationOptions {
    /**
     * The duration of the animation, in seconds.
     */
    public final double durationSeconds;

    /**
     * The speed of the animation, in meters per second.
     */
    public final double preferredAnimationSpeed;

    /**
     * The minimum animation duration, in seconds
     */
    public final double minDuration;

    /**
     * The maximum animation duration, in seconds
     */
    public final double maxDuration;

    /**
     * The distance threshold, in meters, above which a camera animation will jump to its destination immediately.
     */
    public final double snapDistanceThreshold;

    /**
     * True if the camera animation should jump to its destination for large distances.
     */
    public final boolean snapIfDistanceExceedsThreshold;

    /**
     * True if a user touch gesture may interrupt the camera animation.
     */
    public final boolean interruptByGestureAllowed;

    /**
     * @eegeo.internal
     */
    public final boolean hasExplicitDuration;
    /**
     * @eegeo.internal
     */
    public final boolean hasPreferredAnimationSpeed;
    /**
     * @eegeo.internal
     */
    public final boolean hasMinDuration;
    /**
     * @eegeo.internal
     */
    public final boolean hasMaxDuration;
    /**
     * @eegeo.internal
     */
    public final boolean hasSnapDistanceThreshold;

     /**
     * @eegeo.internal
     */
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

    /**
     * A builder class for creating a CameraAnimationOptions instance.
     */
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

        /**
         * An explicit animation duration. If this method is not called, the resultant CameraAnimationOptions
         * will indicate that an appropriate duration should be automatically calculated based on the
         * distance of the camera transition.
         * @param durationSeconds The duration of the animation, in seconds.
         * @return
         */
        public Builder duration(double durationSeconds) {
            m_durationSeconds = durationSeconds;
            m_hasExplicitDuration = true;
            return this;
        }

        /**
         * The preferred speed at which the camera target should travel, in meters per second.
         * If this method not called, the resultant CameraAnimationOptions will indicate that a default
         * speed should be used.
         * @param animationSpeedMetersPerSecond The speed of the animation, in meters per second.
         * @return
         */
        public Builder preferredAnimationSpeed(double animationSpeedMetersPerSecond) {
            m_preferredAnimationSpeed = animationSpeedMetersPerSecond;
            m_hasPreferredAnimationSpeed = true;
            m_hasExplicitDuration = false;
            return this;
        }

        /**
         * Configure the options to immediately jump to the animation destination for distances
         * above a threshold. The default is True.
         * @param shouldSnap True if the camera animation should jump to its destination for large distances.
         * @return
         */
        public Builder snapIfDistanceExceedsThreshold(boolean shouldSnap) {
            m_snapIfDistanceExceedsThreshold = shouldSnap;
            return this;
        }

        /**
         * Configure whether the animation may be interrupted by user touch gestures. The default is True.
         * @param isAllowed True if a user touch gesture may interrupt the camera animation.
         * @return
         */
        public Builder interruptByGestureAllowed(boolean isAllowed) {
            m_interruptByGestureAllowed = isAllowed;
            return this;
        }

        /**
         * The minimum duration of the camera animation, if automatically calculated. The default is 1s.
         * @param minDuration The minimum animation duration, in seconds
         * @return
         */
        public Builder minDuration(double minDuration) {
            m_minDuration = minDuration;
            m_hasMinDuration = true;
            return this;
        }

        /**
         * The maximum duration of the camera animation, if automatically calculated. The default is 5s.
         * @param maxDuration The maximum animation duration, in seconds
         * @return
         */
        public Builder maxDuration(double maxDuration) {
            m_maxDuration = maxDuration;
            m_hasMaxDuration = true;
            return this;
        }

        /**
         * The distance threshold above which an animation will jump to its destination immediately.
         * The default is 5000m.
         * @param snapDistanceThresholdMeters The distance, in meters.
         * @return
         */
        public Builder snapDistanceThreshold(double snapDistanceThresholdMeters) {
            m_snapDistanceThreshold = snapDistanceThresholdMeters;
            m_hasSnapDistanceThreshold = true;
            return this;
        }

        public Builder() {
            super();
        }

        /**
         * Builds the CameraAnimationOptions object.
         * @return The final CameraAnimationOptions object.
         */
        public CameraAnimationOptions build() {
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
