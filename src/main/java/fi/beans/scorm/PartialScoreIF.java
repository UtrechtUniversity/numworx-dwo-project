package fi.beans.scorm;

import java.awt.Component;
import java.util.List;
import java.util.Map;

/**
 * Interface for DWO Applets. Allow extraction of partial scores. To return a
 * List<Map<String,String>> @
 *
 *
 * author wim
 *
 */
public interface PartialScoreIF {

    /**
     * The cmi.core.lesson_location (the ID of the core)
     */
    public String LOCATION = "location";
    /**
     * The cmi.core.score.max
     */
    public String SCORE_MAX = "score.max";
    /**
     * The cmi.core.score.raw
     */
    public String SCORE_RAW = "score.raw";
    /**
     * Short description/title
     */
    public String DESCRIPTION = "description";
    /**
     * time last visit, SCORM 1.2 format, "hh:mm:ss".
     */
    public String SESSION_TIME = "session_time";
    /**
     * total time, SCORM 1.2 format, "hh:mm:ss".
     */
    public String TOTAL_TIME = "total_time";

    /**
     * Get the list of maps. Keys are LOCATION, SCORE_MAX, SCORE_RAW,
     * DESCRIPTION, SESSION_TIME, TOTAL_TIME
     *
     * @param api
     * @return a List<Map<String,String>>
     */
    public List getScoreMapList(SCORM12APIInterface api);

    public Map getScoreObjectivesMap(SCORM12APIInterface api);

    /**
     * Get the main page of the applet, without all navigation stuff or
     * scrollbars.
     *
     * @return the applet or subcomponent
     */
    public Component getContentPage();

}
