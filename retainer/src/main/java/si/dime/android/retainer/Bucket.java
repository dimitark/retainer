package si.dime.android.retainer;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import si.dime.android.retainer.handlers.DataHandler;
import si.dime.android.retainer.handlers.RxHandler;
import si.dime.android.retainer.handlers.Task;
import si.dime.android.retainer.handlers.TaskHandler;

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

    // The raw data <Key, Object>
    private final Map<String, Object> rawData = new HashMap<>();

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
     * Registers the given Rx handler handler with the given key.
     * Does nothing if there is a handler or a task already registered for the given key.
     *
     * @param key
     * @param dataHandler
     */
    public void registerRxHandler(String key, RxHandler dataHandler) {
        // Check if the key already exists
        if (handlers.containsKey(key)) {
            return;
        }

        // Just register the data handler
        handlers.put(key, dataHandler);
    }

    /**
     * Registers the given task with the given key.
     * Does nothing if there is a task or a handler already registered for the given key.
     *
     * @param key
     * @param task
     */
    public void registerTask(String key, Task task) {
        // Check if the key already exists
        if (handlers.containsKey(key)) {
            return;
        }

        // Just register it
        handlers.put(key, new TaskHandler(task));
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
     * Returns true if local data or local error already exist for the given key.
     *
     * @param key
     * @return
     */
    public boolean dataExists(String key) {
        return data.containsKey(key) || errors.containsKey(key);
    }

    /**
     * Returns true if raw data already exists for the given key
     *
     * @param key
     * @return
     */
    public boolean rawDataExists(String key) {
        return rawData.containsKey(key);
    }

    /**
     * Puts the given raw data in this bucket and maps it with the given key (Sync)
     *
     * @param key
     * @param data
     */
    public void putRawData(String key, Object data) {
        rawData.put(key, data);
    }

    /**
     * Removes any existing raw data for the given key
     *
     * @param key
     */
    public void removeRawData(String key) {
        rawData.remove(key);
    }

    /**
     * Synchronously retrieves the raw data for the given key.
     *
     * @param key
     */
    public Object getRawData(String key) {
        return rawData.get(key);
    }


    /**
     * Requests the data for the given key. If the data exists, the corresponding subscriber will be
     * called on the end of the event loop (within the same Thread that this method was called on).
     *
     * @return
     *      true - if the data already exists and the subscriber will be called on the end of this event loop
     *      false - otherwise
     *
     * @param key
     */
    public boolean requestData(String key) {
        return requestData(key, false, false);
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
    public boolean requestImmediateData(String key) {
        return requestData(key, false, true);
    }

    //
    // endregion Get data methods
    //

    //
    // region Remove methods
    //

    /**
     * Cancels the running request for the given key (if any)
     *
     * @param key
     */
    public void cancelRequest(String key) {
        // Sanity check
        if (!isRunning(key)) {
            return;
        }

        // Get the subscriber
        Subscriber subscriber = subscriptions.get(key); // Cannot be null at this moment

        // Unsubscribe
        subscriber.unsubscribe();

        // Remove the subscription
        subscriptions.remove(key);
        compositeSubscription.remove(subscriber);

        // Mark it as not-running
        running.remove(key);
    }

    /**
     * Removes any existing data for the given key
     *
     * @param key
     */
    public void removeData(String key) {
        // Cancel the running request
        cancelRequest(key);

        // Let the user destroy the items
        destroyHandler(key);

        // Remove any existing data
        data.remove(key);
        errors.remove(key);
    }

    /**
     * Unregisters the key, removes any existing data and cancels the current running observables (if any).
     *
     * @param key
     */
    public void unregisterKey(String key) {
        // Remove the data
        removeData(key);

        // Unregister
        handlers.remove(key);
    }

    //
    // endregion Remove methods
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
        // Call the destroyers
        for (String key : handlers.keySet()) {
            destroyHandler(key);
        }

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
                            dataHandler.getObserver().onNext(obj);
                        }
                        // Finish the emission
                        dataHandler.getObserver().onCompleted();
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
                        dataHandler.getObserver().onError(error);
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
            removeData(key);
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
                compositeSubscription.remove(this);

                // Inform the actual subscriber (if any)
                DataHandler theHandler = handlers.get(key);
                if (theHandler != null) {
                    theHandler.getObserver().onCompleted();
                }
            }

            @Override
            public void onError(Throwable e) {
                // Mark the key as completed
                running.remove(key);
                subscriptions.remove(key);
                compositeSubscription.remove(this);

                // Save the throwable
                data.remove(key);
                errors.put(key, e);

                // Inform the actual subscriber (if any)
                DataHandler theHandler = handlers.get(key);
                if (theHandler != null) {
                    theHandler.getObserver().onError(e);
                }
            }

            @Override
            public void onNext(Object o) {
                // Save the data
                dataList.add(o);

                // Inform the actual subscriber (if any)
                DataHandler theHandler = handlers.get(key);
                if (theHandler != null) {
                    theHandler.getObserver().onNext(o);
                }
            }
        };

        // Save the subscriber to the composite subscription
        subscriptions.put(key, subscriber);
        compositeSubscription.add(subscriber);

        // Do the actual subscribing
        dataHandler.getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    /**
     * Calls destroy on all data items for the given key.
     *
     * @param key
     */
    private void destroyHandler(@NonNull String key) {
        // Get the handler
        DataHandler handler = handlers.get(key);
        // Get the data
        List items = data.get(key);

        // Sanity check
        if (handler == null || handler.hasDestroyer() || items == null) {
            return;
        }

        // Call destroy for all items
        for (Object obj : items) {
            handler.getDestroyer().destroy(obj);
        }
        // And finally call destroyCompleted
        handler.getDestroyer().destroyCompleted();
    }

    //
    // endregion Private methods
    //
}
