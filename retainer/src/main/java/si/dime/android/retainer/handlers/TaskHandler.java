package si.dime.android.retainer.handlers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * A task handler
 *
 * Created by dime on 30/11/15.
 */
public class TaskHandler implements DataHandler {
    //
    // region Class fields
    //

    // The task
    private final Task task;

    // The observable
    private final Observable observable;

    // The subscriber
    private final Subscriber subscriber;

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
     * @param task
     */
    public TaskHandler(final Task task) {
        this.task = task;

        // Build the observable
        this.observable = Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                try {
                    // Call the tasks doInBackground()
                    Object data = task.doInBackground();
                    // Pass it on
                    subscriber.onNext(data);
                    // We are done
                    subscriber.onCompleted();
                } catch (Throwable t) {
                    // Pass the error
                    subscriber.onError(t);
                }
            }
        });

        // Build the subscriber
        this.subscriber = new Subscriber() {
            @Override
            public void onCompleted() {
                // Do nothing
            }

            @Override
            public void onError(Throwable t) {
                // Pass the error on
                task.onError(t);
            }

            @Override
            public void onNext(Object o) {
                // I guess we are done
                task.onPostExecute(o);
            }
        };

        // Build the destroyer
        this.destroyer = new Destroyer() {
            @Override
            public void destroy(Object o) {
                task.destroy(o);
            }

            @Override
            public void destroyCompleted() {
                // Do nothing
            }
        };
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
        return subscriber;
    }

    @Nullable
    @Override
    public Destroyer getDestroyer() {
        return destroyer;
    }

    @Override
    public boolean hasDestroyer() {
        return true;
    }

    //
    // endregion DataHandler implementation
    //

    //
    // region Interface definitions
    //

    /**
     * The task definition
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
         * @param data
         */
        void onPostExecute(T data);

        /**
         * Called if an unhandled exception occurs in the doInBackground method.
         *
         * @param t
         */
        void onError(Throwable t);

        /**
         * Called before the user clears the reference to the given data object
         *
         * @param data
         */
        void destroy(T data);
    }

    //
    // endregion Interface definitions
    //
}
