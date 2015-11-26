package si.dime.android.retainer.lifecycle;

import android.app.Activity;
import android.util.Log;

import java.lang.reflect.Field;

import si.dime.android.retainer.AnnotationsProcessor;
import si.dime.android.retainer.Bucket;
import si.dime.android.retainer.Retainer;
import si.dime.android.retainer.annotations.DataHandler;

/**
 * Created by dime on 18/11/15.
 */
public class ReflectionActivityBinder extends ActivityBinder {
    //
    // region Class fields
    //

    // The annotations processor
    private AnnotationsProcessor annotationsProcessor;

    // The app bucket
    private Bucket appBucket;

    //
    // endregion Class fields
    //

    //
    // region Constructors
    //

    /**
     * Default constructor
     *
     * @param annotationsProcessor
     */
    public ReflectionActivityBinder(AnnotationsProcessor annotationsProcessor, Bucket appBucket) {
        super(annotationsProcessor.getActivities());

        // Save the annotations processor
        this.annotationsProcessor = annotationsProcessor;
        this.appBucket = appBucket;
    }

    //
    // endregion Constructors
    //

    //
    // region Abstract method implementations
    //

    @Override
    protected void postOnActivityCreated(Activity activity, Bucket bucket) {
        // Get the annotations data (cannot be null)
        AnnotationsProcessor.ActivityAnnotationsData annotationsData = annotationsProcessor.annotationsData.get(activity.getClass());

        // Inject the buckets - if enabled
        if (annotationsProcessor.bucketInjection) {
            try {
                // Inject the activity bucket
                if (annotationsData.bucketField != null) {
                    annotationsData.bucketField.setAccessible(true);
                    annotationsData.bucketField.set(activity, bucket);
                }

                // Inject the app bucket
                if (annotationsData.appBucketField != null) {
                    annotationsData.appBucketField.setAccessible(true);
                    annotationsData.appBucketField.set(activity, appBucket);
                }
            } catch (IllegalAccessException e) {
                // Should not come to this
                Log.d(Retainer.LOG_TAG, "Cannot inject the bucket!", e);
            }
        }

        // Register the handler - if enabled
        if (annotationsProcessor.autoRegisterHandlers && !annotationsData.dataHandlerFields.isEmpty()) {
            try {
                // Loop through all data handler fields
                for (String key : annotationsData.dataHandlerFields.keySet()) {
                    Field field =  annotationsData.dataHandlerFields.get(key);
                    field.setAccessible(true);
                    bucket.registerDataHandler(key, (Bucket.DataHandler) field.get(activity));
                }
            } catch (IllegalAccessException e) {
                // Should not come to this
                Log.d(Retainer.LOG_TAG, "Cannot get the data handler value!", e);
            }
        }
    }

    //
    // endregion Abstract method implementations
    //
}
