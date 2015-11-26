package si.dime.android.retainer.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import si.dime.android.retainer.Bucket;

/**
 * Created by dime on 18/11/15.
 */
public abstract class ActivityBinder implements Application.ActivityLifecycleCallbacks {
    //
    // region Class fields
    //

    // The list of the registered activities
    protected final Set<Class<? extends Activity>> activities;

    // The buckets
    private final Map<Activity, Bucket> buckets = new HashMap<>();

    // A reference to the bucket who's activity is being destroyed
    private Bucket buckedInTheMiddleOfAConfigChange;

    //
    // endregion Class fields
    //

    //
    // region Abstract method definitions
    //

    /**
     * Called at the end of the onActivityCreated(...) method.
     *
     * @param activity
     * @param bucket
     */
    protected abstract void postOnActivityCreated(Activity activity, Bucket bucket);

    //
    // endregion Abstract method definitions
    //

    //
    // region Constructors
    //

    /**
     * Default constructor
     *
     * @param activities
     */
    public ActivityBinder(Set<Class<? extends Activity>> activities) {
        // Save the activities
        this.activities = activities;
    }

    //
    // endregion Constructors
    //
    
    //
    // region Get methods
    //

    /**
     * Returns the bucket for the activity. Null if no such bucket.
     *
     * @param activity
     * @return
     */
    public Bucket getBucket(Activity activity) {
        return buckets.get(activity);
    }
    
    //
    // endregion Get methods
    //

    //
    // region ActivityLifecycleCallbacks
    //

    @Override
    public final void onActivityCreated(Activity activity, Bundle bundle) {
        // Does the activity has enabled the retainer?
        if (!activities.contains(activity.getClass())) {
            return;
        }

        // The Bucket
        Bucket bucket;

        // Is a config change recreation?
        if (buckedInTheMiddleOfAConfigChange != null) {
            bucket = buckedInTheMiddleOfAConfigChange;
            buckedInTheMiddleOfAConfigChange = null;
        }
        // If not - create new bucket
        else {
            bucket = new Bucket();
        }

        // Bind the bucket to this activity
        buckets.put(activity, bucket);

        // Delegate
        postOnActivityCreated(activity, bucket);
    }

    @Override
    public final void onActivityDestroyed(Activity activity) {
        // Check if we have a bucket for this activity
        Bucket bucket = buckets.get(activity);
        if (bucket == null) {
            // Nothing to do here
            return;
        }

        // Check if we have an ongoing config change. If we have we will need to save the bucket
        if (activity.isChangingConfigurations()) {
            // Save the bucket
            buckedInTheMiddleOfAConfigChange = bucket;
            // Inform the bucket about this
            bucket.onOwnerDestroyed();
        } else {
            // Destroy the bucket
            bucket.destroy();
        }

        // Remove the reference to the activity
        buckets.remove(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {}
    @Override
    public void onActivityResumed(Activity activity) {}
    @Override
    public void onActivityPaused(Activity activity) {}
    @Override
    public void onActivityStopped(Activity activity) {}
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

    //
    // endregion ActivityLifecycleCallbacks
    //
}
