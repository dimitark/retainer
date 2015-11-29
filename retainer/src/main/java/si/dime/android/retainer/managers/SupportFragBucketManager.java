package si.dime.android.retainer.managers;

import android.support.v4.app.Fragment;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

import si.dime.android.retainer.Bucket;
import si.dime.android.retainer.BucketManager;
import si.dime.android.retainer.SupportFragmentHolder;

/**
 * Created by dime on 29/11/15.
 */
public class SupportFragBucketManager implements BucketManager<Fragment, SupportFragmentHolder> {
    //
    // region Class fields
    //

    // The buckets of all the registered fragments
    private final Map<Fragment, Bucket> buckets = new HashMap<>();

    // A sparse array that holds the buckets while their fragments are
    // recreated because of a configuration change
    private final SparseArray<Bucket> retainedBuckets = new SparseArray<>();

    //
    // endregion Class fields
    //

    //
    // region BucketManager implementation
    //

    @Override
    public Bucket getBucket(Fragment parent) {
        // Check if there is already a bucket available
        Bucket bucket = buckets.get(parent);

        // If there is - that's all for now
        if (bucket != null) {
            return bucket;
        }

        // If the retained buckets map is not empty - the user is using the library wrong.
        // Crash and inform him of the error
        if (retainedBuckets.size() > 0) {
            throw new IllegalStateException("The recreation of the fragments due to a configuration change is still in progress. The bucket is not available before the onCreateView() method of the fragment's lifecycle!");
        }

        // If there isn't, that means this is the first call,
        // and we need to create the fragment that holds the bucket,
        // add it to the fragment manager so it can register itself
        SupportFragmentHolder fragmentHolder = SupportFragmentHolder.newInstance(false);
        parent.getChildFragmentManager()
                .beginTransaction()
                .add(fragmentHolder, BucketManager.HOLDER_FRAGMENT_TAG)
                .commit();
        parent.getChildFragmentManager().executePendingTransactions();

        // Create a brand new bucket
        Bucket newBucket = new Bucket();
        // Inject it in the newly created fragment holder
        fragmentHolder.setBucket(newBucket);
        // Put it in our internal map
        buckets.put(parent, newBucket);

        // Return it
        return newBucket;
    }

    @Override
    public void registerFragment(SupportFragmentHolder fragment) {
        // Check if we have an existing bucket for this fragment.
        // This is true only when the system recreates the fragment
        // because of a configuration change
        int uid = fragment.getArguments().getInt(SupportFragmentHolder.EXTRA_BUCKET_ID, -1);
        // Remove the uid from the arguments
        fragment.getArguments().remove(SupportFragmentHolder.EXTRA_BUCKET_ID);

        // Inject the existing bucket - if any
        // If not - the fragment already have a bucket
        // and we already have it in our internal map
        if (uid != -1) {
            // We need to inject the existing bucket
            Bucket bucket = retainedBuckets.get(uid);
            // Remove it from the retained buckets map
            retainedBuckets.remove(uid);
            // Pass it to the holder fragment
            fragment.setBucket(bucket);
            // Save it to our internal map
            buckets.put(fragment.getParentFragment(), bucket);
        }
    }

    @Override
    public void unregisterFragment(SupportFragmentHolder fragment) {
        // Check why this fragment is unregistering.
        // Is it because of a configuration change?
        if (fragment.getActivity().isChangingConfigurations()) {
            // In this case - we need to save the bucket instance
            // so we can inject it again in the new fragment

            // Generate an UID
            int uid = retainedBuckets.size();
            // Save the bucket
            retainedBuckets.append(uid, fragment.getBucket());

            // Save the UID in the fragments Bundle,
            // so when the system "recreates" it
            // the bundle will contain the UID
            // and we will be able to inject the correct
            // bucket back to the new fragment
            fragment.getArguments().putInt(SupportFragmentHolder.EXTRA_BUCKET_ID, uid);

            // At the end - inform the bucket that it's owner is being destroyed
            fragment.getBucket().onOwnerDestroyed();
        } else {
            // Inform the bucket that it's being destroyed
            fragment.getBucket().destroy();
        }

        // Just remove the Fragment - Bucket pairing from our internal map
        buckets.remove(fragment.getParentFragment());
    }

    //
    // endregion BucketManager implementation
    //
}
