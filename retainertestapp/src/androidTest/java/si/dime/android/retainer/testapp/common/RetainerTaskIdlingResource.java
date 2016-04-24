package si.dime.android.retainer.testapp.common;

import android.support.test.espresso.IdlingResource;

import si.dime.android.retainer.testapp.LoadingIndicator;

/**
 * Created by dime on 22/04/16.
 */
public class RetainerTaskIdlingResource implements IdlingResource {

    // The activity
    private LoadingIndicator loadingIndicator;
    private ResourceCallback resourceCallback;

    /**
     * Default constructor
     *
     * @param loadingIndicator
     */
    public RetainerTaskIdlingResource(LoadingIndicator loadingIndicator) {
        this.loadingIndicator = loadingIndicator;
    }

    @Override
    public String getName() {
        return RetainerTaskIdlingResource.class.getName();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = !loadingIndicator.isDataLoading();
        resourceCallback.onTransitionToIdle();
        return idle;
    }
}
