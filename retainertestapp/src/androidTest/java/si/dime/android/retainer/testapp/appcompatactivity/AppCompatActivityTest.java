package si.dime.android.retainer.testapp.appcompatactivity;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import si.dime.android.retainer.testapp.R;
import si.dime.android.retainer.testapp.SimpleTaskActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static si.dime.android.retainer.testapp.OrientationChangeAction.orientationLandscape;
import static si.dime.android.retainer.testapp.OrientationChangeAction.orientationPortrait;

/**
 * Created by dime on 22/04/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AppCompatActivityTest {

    // The simple task idling resource
    // Saving the reference, because we need to unregister it in the @After method
    private IdlingResource simpleTaskIdlingResource;

    @Rule
    public ActivityTestRule<SimpleTaskActivity> activityRule = new ActivityTestRule<>(SimpleTaskActivity.class);

    @Before
    public void before() {
        // Register the idling resource, because on the first call the doInBackground() should run 5 seconds
        simpleTaskIdlingResource = new SimpleTaskIdlingResource(activityRule.getActivity());
        Espresso.registerIdlingResources(simpleTaskIdlingResource);
    }

    @After
    public void after() {
        Espresso.unregisterIdlingResources(simpleTaskIdlingResource);
    }

    @Test
    public void testSimpleTask() {
        // Get a reference to the activity
        SimpleTaskActivity activity = activityRule.getActivity();

        // The simple task's result
        String mainData;

        // In the beginning the data should be null
        assertEquals(null, activity.getData());

        // After the click on the "Simple task button" the data should be not be null
        onView(ViewMatchers.withId(R.id.simple_task_button)).perform(click());
        mainData = activity.getData();
        assertNotEquals(null, mainData);

        // After the second task request, the bucket should return the same (cached) string
        onView(withId(R.id.simple_task_button)).perform(click());
        assertEquals(mainData, activity.getData());

        // After rotation, the data var should point to null,
        // and after requesting the data, the bucket should return the same (cached) string
        onView(isRoot()).perform(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                orientationLandscape() : orientationPortrait());

        // Get the new activity
        activity = (SimpleTaskActivity) getCurrentActivity();

        // Make sure that the previous activity was "replaced"
        assertNotEquals(activityRule.getActivity(), activity);
        assertEquals(null, activity.getData());

        // Make the request one last time
        onView(withId(R.id.simple_task_button)).perform(click());
        // Make sure we get the same object as before (the cached object)
        assertEquals(mainData, activity.getData());
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
