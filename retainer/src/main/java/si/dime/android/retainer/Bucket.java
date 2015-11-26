package si.dime.android.retainer;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Transformer;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A bucket holding data.
 *
 * A bucket is usually bind with an Activity and it's lifecycle,
 * but there are exceptions (e.g. Application wide bucket)
 *
 * Created by dime on 18/11/15.
 */
public class Bucket {

    //
    // region Class fields
    //

    // All of the subscriptions
    private final CompositeSubscription compositeSubscription = new CompositeSubscription();
    // The paired subscriptions
    private final Map<String, Subscriber> subscriptions = new HashMap<>();

    // The <Key, Handler> map
    private final Map<String, DataHandler> handlers = new HashMap<>();

    // The actual data <Key, List>
    private final Map<String, List> data = new HashMap<>();

    // The errors <Key, Throwable>
    private final Map<String, Throwable> errors = new HashMap<>();

    // A list of 'keys' that are in the fetching process
    private final Set<String> running = new HashSet<>();

    //
    // endregion Class fields
    //


    //
    // region Register methods
    //

    /**
     * Registers the given data handler with the given key.
     *
     * @param key
     * @param dataHandler
     * @throws IllegalStateException
     *              If such key already exist
     */
    public void registerDataHandler(String key, DataHandler dataHandler) {
        // Check if the key already exists
        if (handlers.containsKey(key)) {
            throw new IllegalStateException("The key " + key + " already exists!");
        }

        // Just register the data handler
        handlers.put(key, dataHandler);
    }
    
    //
    // endregion Register methods
    //

    //
    // region Get data methods
    //

    /**
     * Returns true if the observable for the given key is still running.
     *
     * @param key
     * @return
     */
    public boolean isRunning(String key) {
        return running.contains(key);
    }


    /**
     * Requests the data for the given key. If the data exists, the corresponding subscriber will be
     * called on the end of the event loop (within the same Thread that this method was called on).
     *
     * @param key
     */
    public void requestData(String key) {
        requestData(key, false, false);
    }

    /**
     * Requests refreshed data for the given key.
     * If the data already exists, it will be removed and the observable will be called once again.
     * If there is a running observable - it will be canceled.
     *
     * @param key
     */
    public void requestRefreshedData(String key) {
        requestData(key, true, false);
    }


    /**
     * Requests the data. If it already exists, the subscriber will be called from within this method.
     * If not - a new request will be made, and the subscriber will be called once the observable calls onComplete()
     * or onError().
     *
     * @param key
     * @return
     *      true - if the subscriber gets called from within this method
     *      false - otherwise
     */
    public boolean requestImmidiateData(String key) {
        return requestData(key, false, true);
    }

    //
    // endregion Get data methods
    //

    //
    // region Destroy methods
    //

    /**
     * Called when the owner of this bucket is destroyed.
     * Here the bucket releases any references pointing to the owner
     */
    public void onOwnerDestroyed() {
        // Clear all the handlers
        handlers.clear();
    }

    /**
     * Destroys the bucket
     */
    public void destroy() {
        // Clear the handlers
        onOwnerDestroyed();

        // Unsubscribe all running observables
        subscriptions.clear();
        compositeSubscription.unsubscribe();
    }

    //
    // endregion Destroy methods
    //


    //
    // region Private methods
    //

    /**
     * Requests the data for the given key. The result will be delivered to the registered subscriber.
     *
     * If the data is already fetched, the subscriber will be called immediately (from within this method) if and only if
     * the runImmediately param is set to true. If the param is set to false, the subscriber will be called
     * at the end of the event queue in the same Thread.
     *
     * Otherwise - after the registered observable finishes.
     *
     * @param key
     *      The key
     * @param forceRefresh
     *      If this is true - the data will be refreshed even if we have it cached
     * @param runImmediately
     *      If set to true & the data is already available - the subscriber will be called from within this method
     *      If false - the subscriber will be called at the end of the event queue in the same Thread.
     * @throws IllegalStateException
     *      If the user request a key that doesn't exist
     *
     * @return
     *      true - if the data was already emitted to the subscriber from within this method
     *      false - if the data will be emitted sometime in the future
     */
    private boolean requestData(String key, boolean forceRefresh, boolean runImmediately) {
        // Get the handler
        final DataHandler dataHandler = handlers.get(key);

        // Check if the key is registered
        if (dataHandler == null) {
            throw new IllegalStateException("The key " + key + " is not registered!");
        }

        // Check if we have data for the key (if needed)
        if (!forceRefresh) {
            // Check for success data
            final List successData = data.get(key);
            if (successData != null) {
                // The Runnable
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // Emit the elements to the subscriber
                        for (Object obj : successData) {
                            dataHandler.subscriber.onNext(obj);
                        }
                        // Finish the emission
                        dataHandler.subscriber.onCompleted();
                    }
                };

                // Check when the user want us to call the subscriber
                if (runImmediately) {
                    runnable.run();
                } else {
                    new Handler(Looper.myLooper()).post(runnable);
                }

                // Inform that we already have the data
                return true;
            }

            // Check for error data
            final Throwable error = errors.get(key);
            if (error != null) {
                // The runnable
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // Emit the error
                        dataHandler.subscriber.onError(error);
                    }
                };

                // Check when the user want us to call the subscriber
                if (runImmediately) {
                    runnable.run();
                } else {
                    new Handler(Looper.myLooper()).post(runnable);
                }

                // Inform that we already have the data
                return true;
            }
        } else {
            // If we have forceRefresh - we need to clear the existing data
            running.remove(key);
            data.remove(key);
            errors.remove(key);

            // Cancel the previous running observable - if any
            Subscriber subscriber = subscriptions.get(key);
            if (subscriber != null) {
                subscriber.unsubscribe();
            }
        }

        // Subscribe to the observable
        subscribeObservable(key, dataHandler);
        return false;
    }

    /**
     * Makes the initial subscription to the observable from the
     */
    private void subscribeObservable(final String key, final DataHandler dataHandler) {
        // Initialize the data list holder
        final List dataList = new ArrayList();
        // Register the key
        data.put(key, dataList);
        running.add(key);

        // Create the subscriber
        final Subscriber subscriber = new Subscriber() {
            @Override
            public void onCompleted() {
                // Mark the key as completed
                running.remove(key);
                subscriptions.remove(key);

                // Inform the actual subscriber (if any)
                DataHandler theHandler = handlers.get(key);
                if (theHandler != null) {
                    theHandler.subscriber.onCompleted();
                }
            }

            @Override
            public void onError(Throwable e) {
                // Mark the key as completed
                running.remove(key);
                subscriptions.remove(key);

                // Save the throwable
                errors.put(key, e);

                // Inform the actual subscriber (if any)
                DataHandler theHandler = handlers.get(key);
                if (theHandler != null) {
                    theHandler.subscriber.onError(e);
                }
            }

            @Override
            public void onNext(Object o) {
                // Save the data
                dataList.add(o);

                // Inform the actual subscriber (if any)
                DataHandler theHandler = handlers.get(key);
                if (theHandler != null) {
                    theHandler.subscriber.onNext(o);
                }
            }
        };

        // Save the subscriber to the composite subscription
        subscriptions.put(key, subscriber);
        compositeSubscription.add(subscriber);

        // Do the actual subscribing
        dataHandler.observable
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    //
    // endregion Private methods
    //
    
    //
    // region Inner classes
    //

    /**
     * A holder for the observable and the subscriber
     */
    public static class DataHandler {
        public final Observable observable;
        public final Subscriber subscriber;

        /**
         * Default constructor
         *
         * @param observable
         * @param subscriber
         */
        public DataHandler(Observable observable, Subscriber subscriber) {
            this.observable = observable;
            this.subscriber = subscriber;
        }
    }

    //
    // endregion Inner classes
    //
}
