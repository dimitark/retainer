package si.dime.android.retainer.lifecycle;

import android.app.Activity;

import java.util.Set;

import si.dime.android.retainer.Bucket;

/**
 * Created by dime on 18/11/15.
 */
public class ReflectionActivityBinder extends ActivityBinder {
    //
    // region Constructors
    //

    /**
     * Default constructor
     *
     * @param activities
     */
    public ReflectionActivityBinder(Set<Class<? extends Activity>> activities) {
        super(activities);
    }

    //
    // endregion Constructors
    //

    //
    // region Abstract method implementations
    //

    @Override
    protected void postOnActivityCreated(Activity activity, Bucket bucket) {
        // TODO inject the bucket if needed
        // TODO auto register the handlers if needed
    }

    //
    // endregion Abstract method implementations
    //
}
