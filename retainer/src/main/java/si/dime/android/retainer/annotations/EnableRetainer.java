package si.dime.android.retainer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables the retainer for the annotated Activity and creates a bucket for it
 *
 * Created by dime on 18/11/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface EnableRetainer {
    /**
     * Should the retainer retain the bucket on configuration change?
     *
     * @return
     */
    boolean retainOnConfigChange() default true;
}
