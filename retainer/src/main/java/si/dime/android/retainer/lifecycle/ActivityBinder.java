package si.dime.android.retainer.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

import si.dime.android.retainer.Bucket;

/**
 * Created by dime on 18/11/15.
 */
public abstract class ActivityBinder implements Application.ActivityLifecycleCallbacks {
    //
    // region Class fields
    //

    // The buckets
    private final Map<Activity, Bucket> buckets = new HashMap<>();

    // A reference to the bucket who's activity is being destroyed
    private Bucket buckedInTheMiddleOfAConfigChange;

    //
    // endregion Class fields
    //

    //
    // region ActivityLifecycleCallbacks
    //

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
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
