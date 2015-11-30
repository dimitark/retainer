package si.dime.android.retainer;

/**
 * Holds the buckets for parents of the same type T
 *
 * Created by dime on 29/11/15.
 */
public interface BucketManager<ParentType, HolderFragmentType> {
    // The TAG of the holder fragments
    String HOLDER_FRAGMENT_TAG = "si.dime.android.retainer.BucketHolderFragment";

    /**
     * Returns the bucket of the given ParentType parent
     *
     * @param parent
     * @return
     */
    Bucket getBucket(ParentType parent);

    /**
     * Registers the given fragment. If this is a register after a configuration change,
     * the old bucket instance is injected in the fragment.
     *
     * @param fragment
     */
    void registerFragment(HolderFragmentType fragment);

    /**
     * Unregisters the fragment. Retains the bucket if this is called because of a configuration change.
     *
     * @param fragment
     */
    void unregisterFragment(HolderFragmentType fragment);
}
