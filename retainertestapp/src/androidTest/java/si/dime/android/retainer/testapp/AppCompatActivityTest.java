package si.dime.android.retainer.testapp;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by dime on 22/04/16.
 */

@RunWith(AndroidJUnit4.class)
public class AppCompatActivityTest extends BaseTest {

    @Rule
    public ActivityTestRule<TestAppCompatActivity> activityRule = new ActivityTestRule<>(TestAppCompatActivity.class);

    @Override
    protected LoadingIndicator getLoadingIndicator() {
        return activityRule.getActivity();
    }

    @Override
    protected Activity getActivity() {
        return activityRule.getActivity();
    }

    @Test
    public void testConfigChangeRecreation() {
        runTest();
    }
}
