package si.dime.android.retainer;

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
    final Bucket appBucket;

    // The lifecycle binder
    private final ActivityBinder binder;

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
    public BucketsManager(boolean appBucketEnabled, AnnotationsProcessor annotationsProcessor) {
        // Did the user enable the app bucket?
        appBucket = appBucketEnabled ? new Bucket() : null;

        // Init the right binder
        binder = annotationsProcessor == null ? new PureActivityBinder() : new ReflectionActivityBinder();
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
