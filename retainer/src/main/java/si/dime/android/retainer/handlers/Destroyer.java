package si.dime.android.retainer.handlers;

/**
 * The destroyer class. Called when the data needs to be destroyed.
 *
 * Created by dime on 30/11/15.
 */
public interface Destroyer<T> {
    /**
     * Called when the given object needs to be destroyed
     *
     * @param t
     */
    void destroy(T t);

    /**
     * Called when all of the objects have been destroyed
     */
    void destroyCompleted();
}
