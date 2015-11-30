package si.dime.android.retainer.handlers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.Observer;

/**
 * A RxJava observable/observer handler
 *
 * Created by dime on 30/11/15.
 */
public class RxHandler {

    //
    // region Class fields
    //

    // The observable
    public final Observable observable;

    // The observer
    public final Observer observer;

    // The destroyer
    public final Destroyer destroyer;

    //
    // endregion Class fields
    //

    //
    // region Constructors
    //

    /**
     * Default constructor
     *
     * @param observable
     * @param observer
     * @param destroyer
     */
    public RxHandler(@NonNull Observable observable, @NonNull Observer observer, @Nullable Destroyer destroyer) {
        this.observable = observable;
        this.observer = observer;
        this.destroyer = destroyer;
    }

    /**
     * Constructor without a destroyer
     *
     * @param observable
     * @param observer
     */
    public RxHandler(@NonNull Observable observable, @NonNull Observer observer) {
        this(observable, observer, null);
    }

    //
    // endregion Constructors
    //


    //
    // region Interface definitions
    //

    /**
     * The destroyer class. Called when the data needs to be destroyed.
     */
    public interface Destroyer<T> {
        /**
         * Called when the given object needs to be destroyed
         *
         * @param o
         */
        void destroy(T o);

        /**
         * Called when all of the objects have been destroyed
         */
        void destroyCompleted();
    }

    //
    // endregion Interface definitions
    //

}
