package si.dime.android.retainer.annotations;

/**
 * Marks a Bucket field for injection.
 *
 * Created by dime on 18/11/15.
 */
public @interface Bucket {
    /**
     * Tells to who this bucket is bind? The activity in which is defined or the application?
     *
     * @return
     */
    Binder bindTo() default Binder.ACTIVITY;

    /**
     * Indicates who is the binder for this Bucket
     */
    enum Binder {
        ACTIVITY, APP
    }
}
