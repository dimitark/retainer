package si.dime.android.retainer.testapp;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;

import org.junit.After;
import org.junit.Before;

import java.util.Collection;

import si.dime.android.retainer.testapp.common.RetainerTaskIdlingResource;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static si.dime.android.retainer.testapp.common.OrientationChangeAction.orientationLandscape;
import static si.dime.android.retainer.testapp.common.OrientationChangeAction.orientationPortrait;

/**
 * Created by dime on 24/04/16.
 */
public abstract class BaseTest {

    // The idling resource. Saving the reference, because we need to unregister it in the @After method
    protected IdlingResource idlingResource;

    /**
     * Returns the loading indicator
     *
     * @return
     */
    protected abstract LoadingIndicator getLoadingIndicator();

    /**
     * Returns the activity
     *
     * @return
     */
    protected abstract Activity getActivity();

    @Before
    public void before() {
        // Register the idling resource
        idlingResource = new RetainerTaskIdlingResource(getLoadingIndicator());
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void after() {
        Espresso.unregisterIdlingResources(idlingResource);
    }

    protected void runTest() {
        // Get a reference to the activity and make sure it's a DataActivity
        DataActivity<String> dataActivity = (DataActivity) getActivity();

        // The simple task's result
        String mainData;

        // In the beginning the data should be null
        assertEquals(null, dataActivity.getData());

        // After the click on the "Simple task button" the data should be not be null
        onView(ViewMatchers.withId(R.id.simple_task_button)).perform(click());
        mainData = dataActivity.getData();
        assertNotEquals(null, mainData);

        // After the second task request, the bucket should return the same (cached) string
        onView(withId(R.id.simple_task_button)).perform(click());
        assertEquals(mainData, dataActivity.getData());

        // After rotation, the data var should point to null,
        // and after requesting the data, the bucket should return the same (cached) string
        onView(isRoot()).perform(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                orientationLandscape() : orientationPortrait());

        // Get the new activity
        dataActivity = (DataActivity) getCurrentActivity();

        // Make sure that the previous activity was "replaced"
        assertNotEquals(getActivity(), dataActivity);
        assertEquals(null, dataActivity.getData());

        // Make the request one last time
        onView(withId(R.id.simple_task_button)).perform(click());
        // Make sure we get the same object as before (the cached object)
        assertEquals(mainData, dataActivity.getData());
    }

    private Activity getCurrentActivity() {
        final Activity[] activity = new Activity[1];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                activity[0] = Iterables.getOnlyElement(activities);
            }
        });
        return activity[0];
    }
}
