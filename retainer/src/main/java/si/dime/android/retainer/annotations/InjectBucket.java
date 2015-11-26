package si.dime.android.retainer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a Bucket field for injection.
 *
 * Created by dime on 18/11/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface InjectBucket {
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
