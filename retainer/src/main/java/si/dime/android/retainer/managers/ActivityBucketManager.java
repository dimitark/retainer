package si.dime.android.retainer.managers;

import android.app.Activity;

import si.dime.android.retainer.Bucket;
import si.dime.android.retainer.BucketManager;
import si.dime.android.retainer.FragmentHolder;

/**
 * Created by dime on 29/11/15.
 */
public class ActivityBucketManager implements BucketManager<Activity, FragmentHolder> {
    @Override
    public Bucket getBucket(Activity parent) {
        // The holder fragment
        FragmentHolder fragmentHolder;

        // First check if the holder fragment already exists
        fragmentHolder = (FragmentHolder) parent.getFragmentManager().findFragmentByTag(BucketManager.HOLDER_FRAGMENT_TAG);
        if (fragmentHolder == null) {
            // If there isn't, that means this is the first call,
            // and we need to create the fragment that holds the bucket,
            // add it to the fragment manager
            fragmentHolder = FragmentHolder.newInstance(true);
            parent.getFragmentManager()
                    .beginTransaction()
                    .add(fragmentHolder, BucketManager.HOLDER_FRAGMENT_TAG)
                    .commit();
            parent.getFragmentManager().executePendingTransactions();

            // Create a brand new bucket
            Bucket newBucket = new Bucket();
            // Inject it in the newly created fragment holder
            fragmentHolder.setBucket(newBucket);
        }

        // Return it
        return fragmentHolder.getBucket();
    }

    @Override
    public void registerFragment(FragmentHolder fragment) {
        // Do nothing
    }

    @Override
    public void unregisterFragment(FragmentHolder fragment) {
        // Inform the bucket that it's being destroyed
        fragment.getBucket().destroy();
    }
}
