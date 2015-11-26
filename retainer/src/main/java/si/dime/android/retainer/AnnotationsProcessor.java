package si.dime.android.retainer;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final boolean bucketInjection;

    // Should we auto register the data handlers, by scanning for the appropriate annotations?
    private final boolean autoRegisterHandlers;

    // The info of all activities
    private final ActivityInfo[] activitiesInfo;

    // The registered activities
    private final Set<Class<? extends Activity>> registeredActivities = new HashSet<>();

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
    
    
    //
    // region Get methods
    //

    /**
     * Returns a list of activities that have the @EnableRetainer annotation.
     * @return
     */
    protected Set<Class<? extends Activity>> getActivities() {
        return registeredActivities;
    }
    
    //
    // endregion Get methods
    //

}
