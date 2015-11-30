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
public class RxHandler implements DataHandler {

    //
    // region Class fields
    //

    // The observable
    private final Observable observable;

    // The observer
    private final Observer observer;

    // The destroyer
    private final Destroyer destroyer;

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
    // region DataHandler implementation
    //

    @NonNull
    @Override
    public Observable getObservable() {
        return observable;
    }

    @NonNull
    @Override
    public Observer getObserver() {
        return observer;
    }

    @Nullable
    @Override
    public Destroyer getDestroyer() {
        return destroyer;
    }

    @Override
    public boolean hasDestroyer() {
        return destroyer != null;
    }
    
    //
    // endregion DataHandler implementation
    //

}
