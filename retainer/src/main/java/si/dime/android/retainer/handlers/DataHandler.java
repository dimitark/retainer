package si.dime.android.retainer.handlers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.Observer;

/**
 * The DataHandler abstraction
 *
 * Created by dime on 30/11/15.
 */
public interface DataHandler {
    /**
     * Returns the observable
     *
     * @return
     */
    @NonNull
    Observable getObservable();

    /**
     * Returns the observer
     *
     * @return
     */
    @NonNull
    Observer getObserver();

    /**
     * Returns the destroyer
     *
     * @return
     */
    @Nullable
    Destroyer getDestroyer();

    /**
     * Returns true if this handler has a destroyer.
     *
     * @return
     */
    boolean hasDestroyer();
}
