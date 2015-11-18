package si.dime.android.retainer.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by dime on 18/11/15.
 */
public abstract class ActivityBinder implements Application.ActivityLifecycleCallbacks {
    //
    // region ActivityLifecycleCallbacks
    //

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

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
