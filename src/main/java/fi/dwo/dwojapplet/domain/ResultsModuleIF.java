// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\ResultsModuleIF.java

package fi.dwo.dwojapplet.domain;

import java.util.Vector;

/**
 * The interface for the GUI to communicate with the ResultsModule.
 * @author M.J.B. Kupers
 *  
 */
public interface ResultsModuleIF {
    public static int ASC = 0;

    public static int DESC = 1;

    /**
     * Zooms in to the specified usergroup.
     * 
     * @param ug The usergroup to zoom in.
     * @return The current list of results.
     *  
     */
    public Vector zoomIn(UserGroup ug);

    /**
     * Zooms out from the usergroup.
     * 
     * @param ug The usergroup to zoom out.
     * @return The current list of results.
     */
    public Vector zoomOut(UserGroup ug);

    /**
     * Zooms in to the specified lessongroup.
     * 
     * @param lg The lessongroup to zoom in.
     * @return The current list of results.
     *  
     */
    public Vector zoomIn(LessonGroup lg);

    /**
     * Zooms out from the lessongroup.
     * 
     * @param lg The lessongroup to zoom out.
     * @return The current list of results.
     */
    public Vector zoomOut(LessonGroup lg);
    
    /**
     * Returns the currently zoomed UserGroup.
     * @return The currently zoomed UserGroup.
     */
    public UserGroup getZoomedUserGroup();
    
    /**
     * Returns the currently zoomed LessonGroup.
     * @return The currently zoomed LessonGroup.
     */
    public LessonGroup getZoomedLessonGroup();

    /**
     * Order the result by the specified usergroup on the specified way.
     * 
     * @param ug The usergroup to sort.
     * @param orderWay The way of order.
     * @return The current list of results ordered as specified.
     *  
     */
    public Vector orderBy(UserGroup ug, int orderWay);

    /**
     * Order the result by the specified lessongroup on the specified way.
     * 
     * @param lg The lessongroup to sort.
     * @param orderWay The way of order.
     * @return The current list of results ordered as specified.
     *  
     */
    public Vector orderBy(LessonGroup lg, int orderWay);

    /**
     * Returns the results ordered and zoomed as specified.
     * 
     * @return The results ordered and zoomed as specified.
     *  
     */
    public Vector getResults();

    /**
     * Selects the specified courses.
     * 
     * @param courses The courses to select.
     *  
     */
    public void selectCourses(Course[] courses);

    /**
     * Selects the specified courses and returns the current results.
     * 
     * @param courses The courses to select.
     * @param getResults Indicates if the results must be returned.
     * @see fi.dwo.client.domain.ResultsModuleIF#selectCourses(fi.dwo.client.domain.Course[],
     *      boolean)
     */
    public Vector selectCourses(Course[] courses, boolean getResults);

    /**
     * Returns the seleced courses.
     * 
     * @return The selected courses.
     */
    public Course[] getSelectedCourse();

    /**
     * Returns all the available courses.
     * 
     * @return All the available courses.
     */
    public Course[] getAllCourses();

    /**
     * Reset the ResultsModule. The zoom and order values are reset.
     */
    public void reset();

	public void showResult(ResultScore rs);
}