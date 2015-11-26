package si.dime.android.retainer;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Retainer's entry point
 *
 * Created by dime on 18/11/15.
 */
public class Retainer {
    //
    // region Static fields
    //

    // The log tag
    public static final String LOG_TAG = "Retainer";

    //
    // endregion Static fields
    //

    //
    // region Class fields
    //

    // The Buckets manager
    private BucketsManager bucketsManager;

    //
    // endregion Class fields
    //

    //
    // region Constructors
    //

    /**
     * No args constructor
     */
    @SuppressWarnings("unused")
    private Retainer() {
        // Don't allow construction without the Builder
    }

    /**
     * Default constructor
     *
     * @param builder
     */
    private Retainer(Builder builder) {
        // The annotation processor. Null in case the user doesn't enable any annotations feature
        AnnotationsProcessor annotationsProcessor = null;
        // Should we initialise the annotation processor?
        if (builder.autoDiscoverActivities || builder.bucketInjection || builder.autoRegisterHandlers) {
            // Do we need the list of activities?
            ActivityInfo[] activitiesInfo = null;
            if (builder.autoDiscoverActivities) {
                try {
                    activitiesInfo = builder.app.getPackageManager().getPackageInfo(
                            builder.app.getPackageName(), PackageManager.GET_ACTIVITIES).activities;
                } catch (PackageManager.NameNotFoundException e) {
                    // Should not come to this
                    Log.e(LOG_TAG, "Could not get info about the activities! Activity auto discovery may not work!", e);
                }
            }
            // Init the annotations processor
            annotationsProcessor = new AnnotationsProcessor(
                    builder.autoDiscoverActivities,
                    builder.bucketInjection,
                    builder.autoRegisterHandlers,
                    activitiesInfo);
        }

        // Initialize the buckets manager
        bucketsManager = new BucketsManager(builder.appBucket, builder.activities, annotationsProcessor);
        // Register the buckets manager as listener to the Activities callbacks
        builder.app.registerActivityLifecycleCallbacks(bucketsManager.getActivityBinder());
    }

    //
    // endregion Constructors
    //

    //
    // region Public methods
    //

    /**
     * Returns the app bucket. Null if it wasn't enabled.
     *
     * @return
     */
    public Bucket getAppBucket() {
        return bucketsManager.appBucket;
    }

    /**
     * Returns the bucket for the given activity.
     * Null if the activity is not registered.
     *
     * @param activity
     * @return
     */
    public Bucket getBucket(Activity activity) {
        return bucketsManager.binder.getBucket(activity);
    }

    //
    // endregion Public methods
    //

    //
    // region Inner classes
    //

    /**
     * The builder class
     */
    public static class Builder {

        //
        // region Class fields
        //
        
        // The Application
        private Application app;

        // Should we auto-discover the Activities that need the retainer, by scanning for the appropriate annotation?
        // The default value is false
        private boolean autoDiscoverActivities;

        // Should we inject the bucket in the Activity, by scanning for the Inject annotation?
        // The default is false
        private boolean bucketInjection;

        // Should we auto register the data handlers, by scanning for the appropriate annotations?
        // Default = false
        private boolean autoRegisterHandlers;

        // Should we initialize an app wide bucket?
        // Default = false
        private boolean appBucket;

        // The set of the registered activities
        // Ignored if auto discovery is enabled!
        private final Set<Class<? extends Activity>> activities = new HashSet<>();
        
        //
        // endregion Class fields
        //

        /**
         * The only constructor
         *
         * @param app
         */
        @SuppressWarnings("unused")
        public Builder(Application app) {
            this.app = app;
        }

        /**
         * Should we auto-discover the Activities that need the retainer, by scanning for the appropriate annotation?
         *
         * @return
         */
        @SuppressWarnings("unused")
        public Builder enableActivityAutoDiscovery() {
            this.autoDiscoverActivities = true;
            return this;
        }

        /**
         * Should we inject the bucket in the Activity, by scanning for the Inject annotation?
         *
         * @return
         */
        @SuppressWarnings("unused")
        public Builder enableBucketInjection() {
            this.bucketInjection = true;
            return this;
        }

        /**
         * Should we auto register the data handlers, by scanning for the appropriate annotations?
         *
         * @return
         */
        @SuppressWarnings("unused")
        public Builder enableHandlersAutoRegistration() {
            this.autoRegisterHandlers = true;
            return this;
        }

        /**
         * Should we create an app wide bucket?
         * Use it if you want to have a single Bucket shared across the application.
         *
         * @return
         */
        @SuppressWarnings("unused")
        public Builder enableAppBucket() {
            this.appBucket = true;
            return this;
        }

        /**
         * Registers the given activity for retention.
         * This call is ignored if the Activity Auto Discovery is enabled.
         *
         * @param activityClass
         * @return
         */
        @SuppressWarnings("unused")
        public Builder registerActivity(Class<? extends Activity> activityClass) {
            activities.add(activityClass);
            return this;
        }

        /**
         * Does the actual build
         *
         * @return
         */
        @SuppressWarnings("unused")
        public Retainer build() {
            return new Retainer(this);
        }
    }

    //
    // endregion Inner classes
    //
}
