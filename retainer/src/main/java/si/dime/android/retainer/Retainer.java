package si.dime.android.retainer;

import android.app.Application;

/**
 * The Retainer's entry point
 *
 * Created by dime on 18/11/15.
 */
public class Retainer {

    //
    // region Class fields
    //

    // The application
    private Application app;

    // Should we auto-discover the Activities that need the retainer, by scanning for the appropriate annotation?
    private boolean activityAutoDiscovery;

    // Should we inject the bucket in the Activity, by scanning for the Inject annotation?
    private boolean bucketInjection;

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
        this.app = builder.app;
        this.activityAutoDiscovery = builder.activityAutoDiscovery;
        this.bucketInjection = builder.bucketInjection;
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
        private boolean activityAutoDiscovery;

        // Should we inject the bucket in the Activity, by scanning for the Inject annotation?
        // The default is false
        private boolean bucketInjection;
        
        //
        // endregion Class fields
        //

        /**
         * The only constructor
         *
         * @param app
         */
        public Builder(Application app) {
            this.app = app;
        }

        /**
         * Should we auto-discover the Activities that need the retainer, by scanning for the appropriate annotation?
         *
         * @return
         */
        public Builder enableActivityAutoDiscovery() {
            this.activityAutoDiscovery = true;
            return this;
        }

        /**
         * Should we inject the bucket in the Activity, by scanning for the Inject annotation?
         *
         * @return
         */
        public Builder enableBucketInjection() {
            this.bucketInjection = true;
            return this;
        }

        /**
         * Does the actual build
         *
         * @return
         */
        public Retainer build() {
            return new Retainer(this);
        }
    }

    //
    // endregion Inner classes
    //
}
