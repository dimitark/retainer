package si.dime.android.retainer;

import android.app.Fragment;
import android.os.Bundle;

/**
 * The [support] fragment that holds the Bucket
 *
 * Created by dime on 29/11/15.
 */
public class FragmentHolder extends Fragment {
    //
    // region Static fields
    //

    // The EXTRA keys
    private static final String EXTRA_RETAIN_INSTANCE = "retainInstance";

    //
    // endregion Static fields
    //

    //
    // region Class fields
    //

    // The bucket
    private Bucket bucket;

    // Are we retaining the instance?
    private boolean retainInstance;

    //
    // endregion Class fields
    //

    //
    // region Lifecycle
    //

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Should we retain the instance?
        retainInstance = getArguments().getBoolean(EXTRA_RETAIN_INSTANCE);
        setRetainInstance(retainInstance);

        // Register self with the retainer
        Retainer.registerFragment(this, retainInstance);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister self with the retainer
        Retainer.unregisterFragment(this, retainInstance);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // If we are retaining the instance
        // we must clear any references to the previous parent
        if (retainInstance) {
            bucket.onOwnerDestroyed();
        }
    }

    //
    // endregion Lifecycle
    //

    //
    // region Getters
    //

    /**
     * Returns the bucket
     *
     * @return
     */
    public Bucket getBucket() {
        return bucket;
    }


    //
    // endregion Getters
    //

    //
    // region Setters
    //

    /**
     * Sets the bucket
     *
     * @param bucket
     */
    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }


    //
    // endregion Setters
    //


    //
    // region Static methods
    //

    /**
     * Creates a new instance of this fragment
     *
     * @param retainInstance
     * @return
     */
    public static FragmentHolder newInstance(boolean retainInstance) {
        // New instance
        FragmentHolder fragment = new FragmentHolder();

        // Set the arguments
        Bundle arguments = new Bundle();
        arguments.putBoolean(EXTRA_RETAIN_INSTANCE, retainInstance);
        fragment.setArguments(arguments);

        // Return the instance
        return fragment;
    }

    //
    // endregion Static methods
    //
}
