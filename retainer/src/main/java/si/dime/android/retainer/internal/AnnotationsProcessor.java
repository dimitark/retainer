package si.dime.android.retainer.internal;

import android.content.pm.ActivityInfo;

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
    private final boolean autoDiscoverActivities;

    // Should we inject the bucket in the Activity, by scanning for the inject annotation?
    private final boolean bucketInjection;

    // Should we auto register the data handlers, by scanning for the appropriate annotations?
    private final boolean autoRegisterHandlers;

    // The info of all activities
    private final ActivityInfo[] activitiesInfo;

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
     */
    public AnnotationsProcessor(boolean autoDiscoverActivities,
                                boolean bucketInjection,
                                boolean autoRegisterHandlers,
                                ActivityInfo[] activitiesInfo) {
        // Save the options
        this.autoDiscoverActivities = autoDiscoverActivities;
        this.bucketInjection = bucketInjection;
        this.autoRegisterHandlers = autoRegisterHandlers;
        this.activitiesInfo = activitiesInfo;
    }

    //
    // endregion Constructors
    //

}
