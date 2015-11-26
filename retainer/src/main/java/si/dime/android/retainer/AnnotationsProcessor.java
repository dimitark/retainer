package si.dime.android.retainer;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import si.dime.android.retainer.annotations.*;

/**
 * Processes the annotations
 *
 * Created by dime on 18/11/15.
 */
public class AnnotationsProcessor {
    //
    // region Class fields
    //

    // Should we auto-discover the Activities that need the retainer, by scanning for the appropriate annotation?
    final boolean autoDiscoverActivities;

    // Should we inject the bucket in the Activity, by scanning for the inject annotation?
    public final boolean bucketInjection;

    // Should we auto register the data handlers, by scanning for the appropriate annotations?
    public final boolean autoRegisterHandlers;

    // The info of all activities
    private final ActivityInfo[] activitiesInfo;

    // The registered activities
    private final Set<Class<? extends Activity>> registeredActivities;

    // The annotations data
    public final Map<Class<? extends Activity>, ActivityAnnotationsData> annotationsData = new HashMap<>();

    //
    // endregion Class fields
    //

    //
    // region Constructors
    //

    /**
     * Default constructor
     *
     * @param autoDiscoverActivities
     * @param bucketInjection
     * @param autoRegisterHandlers
     * @param activitiesInfo
     * @param manuallyRegisteredActivities
     */
    public AnnotationsProcessor(boolean autoDiscoverActivities,
                                boolean bucketInjection,
                                boolean autoRegisterHandlers,
                                ActivityInfo[] activitiesInfo,
                                Set<Class<? extends Activity>> manuallyRegisteredActivities) {
        // Save the options
        this.autoDiscoverActivities = autoDiscoverActivities;
        this.bucketInjection = bucketInjection;
        this.autoRegisterHandlers = autoRegisterHandlers;
        this.activitiesInfo = activitiesInfo;
        this.registeredActivities = manuallyRegisteredActivities;

        // Process the annotations
        discoverActivities();
        scanTheFields();
    }

    //
    // endregion Constructors
    //

    
    //
    // region Get methods
    //

    /**
     * Returns a list of activities that have the @EnableRetainer annotation.
     * @return
     */
    public Set<Class<? extends Activity>> getActivities() {
        return registeredActivities;
    }
    
    //
    // endregion Get methods
    //

    //
    // region Private methods
    //

    /**
     * Searches for activities annotated with the @EnableRetainer annotation,
     * if the auto discovery is enabled
     */
    private void discoverActivities() {
        // Is the auto discovery enabled?
        if (!autoDiscoverActivities) {
            return;
        }

        // Loop through all of the application's activities
        for (ActivityInfo activityInfo : activitiesInfo) {
            try {
                // Get the activity class
                Class<? extends Activity> clazz = (Class<? extends Activity>) Class.forName(activityInfo.name);
                // Check if the class has the annotation
                if (clazz.isAnnotationPresent(EnableRetainer.class)) {
                    registeredActivities.add(clazz);
                    annotationsData.put(clazz, new ActivityAnnotationsData());
                    Log.i(Retainer.LOG_TAG, "Enabled the retainer for the Activity " + activityInfo.name);
                }
            } catch (ClassNotFoundException e) {
                Log.e(Retainer.LOG_TAG, "Could not find the Class from the activity " + activityInfo.name, e);
            }
        }
    }

    /**
     * Scans for fields annotated with @Bucket and @DataHandler in the registered activities,
     * if the bucket auto injection is enabled or/and the auto register is enabled
     */
    private void scanTheFields() {
        // Is the bucket injection or the auto registration enabled?
        if (!bucketInjection && !autoRegisterHandlers) {
            return;
        }

        // Loop through all the registered activities
        for (Class<? extends Activity> clazz : registeredActivities) {
            // Get the annotations data holder
            ActivityAnnotationsData activityAnnotations = annotationsData.get(clazz);
            // Create if it doesn't exist
            if (activityAnnotations == null) {
                activityAnnotations = new ActivityAnnotationsData();
                annotationsData.put(clazz, activityAnnotations);
            }

            // A flag indication if we need to search [more] @Bucket annotations
            boolean allBucketsFound = !bucketInjection;

            // Check all the fields
            for (Field field : clazz.getDeclaredFields()) {
                // Check for @Bucket
                if (!allBucketsFound && checkForBucket(field, activityAnnotations)) {
                    // Check if all the buckets are found
                    allBucketsFound = activityAnnotations.appBucketField != null && activityAnnotations.bucketField != null;
                } else if (autoRegisterHandlers) {
                    // Check for @DataHandler
                    checkForDataHandler(field, activityAnnotations);
                }
            }
        }
    }

    /**
     * Handles the @Bucket annotations
     *
     * @param field
     * @param activityAnnotations
     */
    private boolean checkForBucket(Field field, ActivityAnnotationsData activityAnnotations) {

        if (field.isAnnotationPresent(InjectBucket.class) && field.getType() == Bucket.class) {

            // Check what kind of bucket it is (Activity or App)
            InjectBucket annotation = field.getAnnotation(InjectBucket.class);
            switch (annotation.bindTo()) {
                case ACTIVITY:
                    activityAnnotations.bucketField = field;
                    break;
                case APP:
                    activityAnnotations.appBucketField = field;
                    break;
            }
            // Found the annotation
            return true;
        }
        // Didn't find the annotation
        return false;
    }

    /**
     * Handles the @DataHandler annotations
     *
     * @param field
     * @param activityAnnotations
     */
    private void checkForDataHandler(Field field, ActivityAnnotationsData activityAnnotations) {
        // Check
        if (field.isAnnotationPresent(DataHandler.class) && field.getType() == Bucket.DataHandler.class) {
            // Get the key of the data
            DataHandler annotation = field.getAnnotation(DataHandler.class);
            // Save the field
            activityAnnotations.dataHandlerFields.put(annotation.value(), field);
        }
    }

    //
    // endregion Private methods
    //

    //
    // region Inner classes
    //

    /**
     * Holds the annotated fields and methods.
     */
    public static class ActivityAnnotationsData {
        // The field reserved for the application bucket
        public Field appBucketField;

        // The field reserved for the [activity] bucket
        public Field bucketField;

        // The data handler fields
        public final Map<String, Field> dataHandlerFields = new HashMap<>();
    }

    //
    // endregion Inner classes
    //

}
