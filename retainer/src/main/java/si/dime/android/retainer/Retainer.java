package si.dime.android.retainer;

import android.support.annotation.NonNull;
import android.util.Log;

import si.dime.android.retainer.managers.FragActivityBucketManager;
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

    // The bucket managers
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
        // Initialize the bucket managers
        // TODO

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
     * Returns the bucket for the v4 support FragmentActivity
     *
     * @param activity
     * @return
     */
    @SuppressWarnings("unused")
    public static @NonNull Bucket getBucket(@NonNull  android.support.v4.app.FragmentActivity activity) {
        return INSTANCE.fragActivityBucketManager.getBucket(activity);
    }
    
    //
    // endregion Public static methods
    //
}
