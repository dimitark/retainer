package si.dime.android.retainer;

/**
 * A bucket holding data.
 *
 * A bucket is usually bind with an Activity and it's lifecycle,
 * but there are exceptions (e.g. Application wide bucket)
 *
 * Created by dime on 18/11/15.
 */
public class Bucket {

    //
    // region Protected methods
    //

    /**
     * Called when the owner of this bucket is destroyed.
     * Here the bucket releases any references pointing to the owner
     */
    protected void onOwnerDestroyed() {

    }

    /**
     * Destroys the bucket
     */
    protected void destroy() {

    }

    //
    // endregion Protected methods
    //
}
