package si.dime.android.retainer.testapp.appcompatactivity;

import android.support.test.espresso.IdlingResource;

import si.dime.android.retainer.testapp.SimpleTaskActivity;

/**
 * Created by dime on 22/04/16.
 */
public class SimpleTaskIdlingResource implements IdlingResource {

    // The activity
    private SimpleTaskActivity simpleTaskActivity;
    private ResourceCallback resourceCallback;

    /**
     * Default constructor
     *
     * @param simpleTaskActivity
     */
    public SimpleTaskIdlingResource(SimpleTaskActivity simpleTaskActivity) {
        this.simpleTaskActivity = simpleTaskActivity;
    }

    @Override
    public String getName() {
        return SimpleTaskIdlingResource.class.getName();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = !simpleTaskActivity.simpleTaskIsLoading;
        resourceCallback.onTransitionToIdle();
        return idle;
    }
}
