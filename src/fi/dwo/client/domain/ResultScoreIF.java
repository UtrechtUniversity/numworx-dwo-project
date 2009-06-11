// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\ResultScoreIF.java

package fi.dwo.client.domain;

/**
 * This is the interface between a ResultScore (combination of a LessonGroup and UserGroup) and the Gui Representation (the ResultScoreButton).
 * M.J.B. Kupers
 */
public interface ResultScoreIF {

    /**
     * Called when the result of the current usergroup on the current
     * lessongroup must be showed. This will only do something if both groups
     * are on the deepest level
     * 
     *  
     */
    public void showResult();

    /**
     * Indicates if this ResultScore is at the deepest level.
     * 
     * @return If the usergroup and the lessongroup at the deepest level it
     *         returns true. Otherwise it returns false.
     */
    public boolean isDeepest();
    
    /**
     * Returns the current LessonGroup.
     * 
     * @return the current Lessongroup.
     */
    public LessonGroup getLessonGroup();
    
    /**
     * Returns the current UserGroup.
     * 
     * @return The current UserGroup.
     */
    public UserGroup getUserGroup();


}