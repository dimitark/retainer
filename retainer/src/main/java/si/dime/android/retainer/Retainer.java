package si.dime.android.retainer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import si.dime.android.retainer.managers.ActivityBucketManager;
import si.dime.android.retainer.managers.FragActivityBucketManager;
import si.dime.android.retainer.managers.FragmentBucketManager;
import si.dime.android.retainer.managers.SupportFragBucketManager;

/**
 * The Retainer's entry point. Singleton.
 *
 * Created by dime on 29/11/15.
 */
@SuppressWarnings("unused")
public class Retainer {

    //
    // region Static fields
    //

    // The singleton instance
    private static final Retainer INSTANCE = new Retainer();

    // The log tag
    public static final String LOG_TAG = "Retainer";

    //
    // endregion Static fields
    //

    //
    // region Class fields
    //

    // The native bucket managers
    private BucketManager activityBucketManager;
    private BucketManager fragmentBucketManager;

    // The support bucket managers
    private BucketManager supportFragBucketManager;
    private BucketManager fragActivityBucketManager;

    //
    // endregion Class fields
    //

    //
    // region Singleton pattern
    //

    /**
     * Private constructor
     */
    private Retainer() {
        // Initialize the native bucket managers
        activityBucketManager = new ActivityBucketManager();

        // We support fragment buckets only from API_LEVEL 17+, because
        // the lower versions are missing the getParentFragment() method on the Fragment class.
        // If you want to use fragment buckets for API_LEVEL 16
        // you need to use the fragments and activities from the Android v4 support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            fragmentBucketManager = new FragmentBucketManager();
        }

        // Initialize the support bucket managers if the support library is included
        try {
            // Check if the support library is available
            Class.forName("android.support.v4.app.Fragment");

            // Init the bucket managers
            supportFragBucketManager = new SupportFragBucketManager();
            fragActivityBucketManager = new FragActivityBucketManager();
        } catch (ClassNotFoundException e) {
            Log.d(Retainer.LOG_TAG, "The support library is not included in the project.");
        }
    }

    /**
     * We don't support cloning
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    //
    // endregion Singleton pattern
    //
    
    //
    // region 'Library' methods. Not for external use
    //

    /**
     * Registers the given fragment. if this is a register after a configuration change,
     * the old bucket instance is injected in the fragment.
     *
     * @param fragment
     * @param isRetainedFragment
     */
    static void registerFragment(Fragment fragment, boolean isRetainedFragment) {
        // Choose the right bucket manager
        if (isRetainedFragment) {
            INSTANCE.activityBucketManager.registerFragment(fragment);
        } else {
            INSTANCE.fragmentBucketManager.registerFragment(fragment);
        }
    }

    /**
     * Registers the given fragment. if this is a register after a configuration change,
     * the old bucket instance is injected in the fragment.
     *
     * @param fragment
     * @param isRetainedFragment
     */
    static void registerFragment(android.support.v4.app.Fragment fragment, boolean isRetainedFragment) {
        // Choose the right bucket manager
        if (isRetainedFragment) {
            INSTANCE.fragActivityBucketManager.registerFragment(fragment);
        } else {
            INSTANCE.supportFragBucketManager.registerFragment(fragment);
        }
    }

    /**
     * Unregisters the fragment. Retains the bucket if this is called because of a configuration change.
     *
     * @param fragment
     * @param isRetainedFragment
     */
    static void unregisterFragment(Fragment fragment, boolean isRetainedFragment) {
        if (isRetainedFragment) {
            INSTANCE.activityBucketManager.unregisterFragment(fragment);
        } else {
            INSTANCE.fragmentBucketManager.unregisterFragment(fragment);
        }
    }

    /**
     * Unregisters the fragment. Retains the bucket if this is called because of a configuration change.
     *
     * @param fragment
     * @param isRetainedFragment
     */
    static void unregisterFragment(android.support.v4.app.Fragment fragment, boolean isRetainedFragment) {
        if (isRetainedFragment) {
            INSTANCE.fragActivityBucketManager.unregisterFragment(fragment);
        } else {
            INSTANCE.supportFragBucketManager.unregisterFragment(fragment);
        }
    }
    
    //
    // endregion 'Library' methods. Not for external use
    //

    //
    // region Public static methods
    //

    /**
     * Returns the bucket for the given Fragment.
     *
     * This is supported for API versions 17+.
     * For API version 16, you can use the getBucket(android.support.v4.app.Fragment fragment) method.
     *
     * @param fragment
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressWarnings("unused")
    public static @NonNull Bucket getBucket(@NonNull Fragment fragment) {
        return INSTANCE.fragmentBucketManager.getBucket(fragment);
    }

    /**
     * Returns the bucket for the v4 support Fragment
     *
     * @param fragment
     * @return
     */
    @SuppressWarnings("unused")
    public static @NonNull Bucket getBucket(@NonNull android.support.v4.app.Fragment fragment) {
        return INSTANCE.supportFragBucketManager.getBucket(fragment);
    }

    /**
     * Returns the bucket for the given Activity
     *
     * @param activity
     * @return
     */
    @SuppressWarnings("unused")
    public static @NonNull Bucket getBucket(@NonNull Activity activity) {
        return INSTANCE.activityBucketManager.getBucket(activity);
    }

    /**
     * Returns the bucket for the v4 support FragmentActivity
     *
     * @param activity
     * @return
     */
    @SuppressWarnings("unused")
    public static @NonNull Bucket getBucket(@NonNull android.support.v4.app.FragmentActivity activity) {
        return INSTANCE.fragActivityBucketManager.getBucket(activity);
    }
    
    //
    // endregion Public static methods
    //
}
