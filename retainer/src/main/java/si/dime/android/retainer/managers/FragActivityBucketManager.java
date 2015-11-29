package si.dime.android.retainer.managers;

import android.support.v4.app.FragmentActivity;

import si.dime.android.retainer.Bucket;
import si.dime.android.retainer.BucketManager;
import si.dime.android.retainer.SupportFragmentHolder;

/**
 * Created by dime on 29/11/15.
 */
public class FragActivityBucketManager implements BucketManager<FragmentActivity, SupportFragmentHolder> {
    @Override
    public Bucket getBucket(FragmentActivity parent) {
        // The holder fragment
        SupportFragmentHolder fragmentHolder;

        // First check if the holder fragment already exists
        fragmentHolder = (SupportFragmentHolder) parent.getSupportFragmentManager().findFragmentByTag(BucketManager.HOLDER_FRAGMENT_TAG);
        if (fragmentHolder == null) {
            // If there isn't, that means this is the first call,
            // and we need to create the fragment that holds the bucket,
            // add it to the fragment manager
            fragmentHolder = SupportFragmentHolder.newInstance(true);
            parent.getSupportFragmentManager()
                    .beginTransaction()
                    .add(fragmentHolder, BucketManager.HOLDER_FRAGMENT_TAG)
                    .commit();
            parent.getSupportFragmentManager().executePendingTransactions();

            // Create a brand new bucket
            Bucket newBucket = new Bucket();
            // Inject it in the newly created fragment holder
            fragmentHolder.setBucket(newBucket);
        }

        // Return it
        return fragmentHolder.getBucket();
    }

    @Override
    public void registerFragment(SupportFragmentHolder fragment) {
        // Do nothing
    }

    @Override
    public void unregisterFragment(SupportFragmentHolder fragment) {
        // Inform the bucket that it's being destroyed
        fragment.getBucket().destroy();
    }
}
