package si.dime.android.retainer.handlers;

/**
 * Created by dime on 01/12/15.
 */
public interface Task<T> {
    /**
     * Called in a background thread
     *
     * @return
     */
    T doInBackground();

    /**
     * Called after the doInBackground() finishes it's execution.
     * If an unhandled exception occurs in the doInBackground - this method is not called.
     * Instead the onError method is called.
     *
     * @param t
     */
    void onPostExecute(T t);

    /**
     * Called if an unhandled exception occurs in the doInBackground method.
     *
     * @param e
     */
    void onError(Throwable e);

    /**
     * Called before the user clears the reference to the given data object
     *
     * @param t
     */
    void destroy(T t);
}