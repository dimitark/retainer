package si.dime.android.retainer;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import si.dime.android.retainer.lifecycle.ActivityBinder;
import si.dime.android.retainer.lifecycle.PureActivityBinder;
import si.dime.android.retainer.lifecycle.ReflectionActivityBinder;

/**
 * Manages the buckets, and binds then to the activities' lifecycle
 *
 * Created by dime on 18/11/15.
 */
public class BucketsManager {

    //
    // region Class fields
    //

    // The app wide bucket. Can be null if the user doesn't enable it
    protected final Bucket appBucket;

    // The lifecycle binder
    protected final ActivityBinder binder;

    //
    // endregion Class fields
    //

    //
    // region Constructors
    //

    /**
     * Default constructor
     *
     * @param appBucketEnabled
     * @param annotationsProcessor
     */
    public BucketsManager(boolean appBucketEnabled, Set<Class<? extends Activity>> activities, AnnotationsProcessor annotationsProcessor) {
        // Did the user enable the app bucket?
        appBucket = appBucketEnabled ? new Bucket() : null;

        // Check if we have enabled annotations
        if (annotationsProcessor != null) {
            // Initialize the Reflection Activity Binder
            binder = new ReflectionActivityBinder(annotationsProcessor.autoDiscoverActivities
                    ? annotationsProcessor.getActivities()
                    : activities);
        }
        // Otherwise - we have the 'pure' mode
        else {
            binder = new PureActivityBinder(activities);
        }
    }

    //
    // endregion Constructors
    //
    
    //
    // region Protected methods
    //

    /**
     * Returns the activity binder
     *
     * @return
     */
    protected ActivityBinder getActivityBinder() {
        return binder;
    }
    
    //
    // endregion Protected methods
    //
}
