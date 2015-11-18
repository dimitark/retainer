package si.dime.android.retainer;

import android.app.Application;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import si.dime.android.retainer.internal.AnnotationsProcessor;

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

    // The application
    private Application app;

    // Should we initialize an app wide bucket?
    private boolean appBucket;

    // The annotation processor. Null in case the user doesn't enable any annotations feature
    private AnnotationsProcessor annotationsProcessor;

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
        // Save the references
        this.app = builder.app;
        this.appBucket = builder.appBucket;

        // Should we initialise the annotation processor?
        if (builder.autoDiscoverActivities || builder.bucketInjection || builder.autoRegisterHandlers) {
            // Do we need the list of activities?
            ActivityInfo[] activitiesInfo = null;
            if (builder.autoDiscoverActivities) {
                try {
                    activitiesInfo = app.getPackageManager().getPackageInfo(app.getPackageName(), PackageManager.GET_ACTIVITIES).activities;
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
    }

    //
    // endregion Constructors
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