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
     * Tells the scope of this bucket. The activity in which is defined or the application?
     *
     * @return
     */
    Scope bindTo() default Scope.ACTIVITY;

    /**
     * The scope of the bucket (Activity or App)
     */
    enum Scope {
        ACTIVITY, APP
    }
}
