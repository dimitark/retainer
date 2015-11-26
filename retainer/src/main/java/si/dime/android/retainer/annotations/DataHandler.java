package si.dime.android.retainer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a Data handler
 *
 * Created by dime on 26/11/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface DataHandler {
    /**
     * Defines the key of the data handler
     *
     * @return
     */
    String value();
}
