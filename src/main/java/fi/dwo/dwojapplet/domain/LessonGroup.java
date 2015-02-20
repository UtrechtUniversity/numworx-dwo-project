// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\LessonGroup.java
package fi.dwo.dwojapplet.domain;

/**
 * This interface is used for the results overview of teachers.<br>
 * It must be implemented by classes that represents a Lesson (e.g. Course,
 * Sco)<br>
 *
 * @author M.J.B. Kupers
 *
 */
public interface LessonGroup {

    /**
     * Returs the name representing the LessonGroup object.
     *
     * @return The name representing the LessonGroup object.
     *
     */
    public String getName();

    /**
     * Returns a title representing this LessonGroup object.
     *
     * @return The title representing the LessonGroup object.
     */
    public String getTitle();

    /**
     * Returns the unique-identifier for the LessonGroup object.
     *
     * @return The unique-identifier for the LessonGroup object.
     *
     */
    public int getID();

    /**
     * Indicates if this is the deepest LessonGroup.
     *
     * @return If this is the deepest LessonGroup it returns true. Otherwise it
     * returns false.
     */
    public boolean isDeepestLevel();

    /**
     * Indicates if this is the highest LessonGroup.
     *
     * @return If this is the highest LessonGroup it returns true. Otherwise it
     * returns false.
     */
    public boolean isHighestLevel();

    /**
     * Returns a title represents the parent item.
     *
     * @return A title represents the parent item.
     */
    public String getParentTitle();

    /**
     * Returns a title represents the child item.
     *
     * @return A title represents the child item.
     */
    public String getChildTitle();

    /**
     * Returns a title represents the Ascending Order item.
     *
     * @return A title represents the Ascending Order item.
     */
    public String getOrderAscTitle();

    /**
     * Returns a title represents the Descending Order item.
     *
     * @return A title represents the Descending Order item.
     */
    public String getOrderDescTitle();

    /**
     * Returns a tooltip for the LessonGroup.
     *
     * @return A tooltip for the LessonGroup.
     */
    public String getToolTip();
}
