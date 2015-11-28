package si.dime.android.retainer.lifecycle;

import android.app.Activity;

import java.util.Set;

import si.dime.android.retainer.Bucket;

/**
 * Created by dime on 18/11/15.
 */
public class PureActivityBinder extends ActivityBinder {
    //
    // region Constructors
    //

    /**
     * Default constructor
     *
     * @param activities
     */
    public PureActivityBinder(Set<Class<? extends Activity>> activities) {
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
        // Do nothing
    }

    //
    // endregion Abstract method implementations
    //
}
