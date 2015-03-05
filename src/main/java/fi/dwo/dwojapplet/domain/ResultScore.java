// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\ResultScore.java
package fi.dwo.dwojapplet.domain;

import fi.dwo.dwojapplet.gui.ResultScoreButton;
import java.awt.Component;

/**
 * This class handles the score of a user (schoolclass, user) and a
 * lesson(course, sco)
 *
 * @author M.J.B. Kupers
 */
public class ResultScore implements ResultScoreIF {

    private LessonGroup lessonGroup;

    private float score;
    private int totaal, corrtotaal;
    private long total_time;

    private UserGroup userGroup;

    private UserResultList userResultList;

    /**
     * Creates a new ResultScore Object.
     *
     */
    public ResultScore() {
        totaal = corrtotaal = 1;
    }

    /**
     * Returns a graphical representation of the resultscore.
     *
     * @return A graphical representation of the resultscore.
     */
    public Component getGui() {
        return new ResultScoreButton(getScore(), this);
    }

    /**
     * Returns the current LessonGroup.
     *
     * @return the current Lessongroup.
     */
    @Override
    public LessonGroup getLessonGroup() {
        return lessonGroup;
    }

    /**
     * Returns the score for the lessongroup and the usergroup. If corrtotaal is
     * zero, the result is also zero instead of Infinity/NaN.
     *
     * @return The score for the lessongroup and the usergroup.
     */
    public float getScore() {
        if (corrtotaal == 0) {
            return 0.0f;
        }
        return score * totaal / corrtotaal;
    }

    /**
     * Returns the current UserGroup.
     *
     * @return The current UserGroup.
     */
    @Override
    public UserGroup getUserGroup() {
        return userGroup;
    }

    /**
     * Returns the current UserResultList.
     *
     * @return The current UserResultList.
     */
    public UserResultList getUserResultList() {
        return userResultList;
    }

    /**
     * Indicates if this ResultScore is at the deepest level.
     *
     * @return If the usergroup and the lessongroup at the deepest level it
     * returns true. Otherwise it returns false.
     * @see fi.dwo.client.domain.ResultScoreIF#isDeepest()
     */
    @Override
    public boolean isDeepest() {
        return userGroup.isDeepestLevel() && lessonGroup.isDeepestLevel();
    }

    /**
     * Sets the current LessonGroup.
     *
     * @param lessonGroup The LessonGroup to set.
     */
    public void setLessonGroup(LessonGroup lessonGroup) {
        this.lessonGroup = lessonGroup;
    }

    /**
     * Sets the score for the lessongroup and the usergroup.
     *
     * @param score The score for the lessongroup and the usergroup to set.
     */
    public void setScore(float score) {
        this.score = score;
    }

    /**
     * Sets the current UserGroup.
     *
     * @param userGroup The UserGroup to set.
     */
    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    /**
     * Sets the current UserResultList.
     *
     * @param userResultList The current UserResultList.
     */
    public void setUserResultList(UserResultList userResultList) {
        this.userResultList = userResultList;
    }

    /**
     * Shows the result of the current usergroup/lessongroup.
     *
     */
    @Override
    public void showResult() {
        userResultList.showResult(this);
        // update score... werkt niet, want showResults is asynchroon.
//        if(lessonGroup instanceof Sco && userGroup instanceof User)
//        {
//        	String s = GuiCreator.instance().getDWO().LMSGetValue((Sco)lessonGroup, (User)userGroup, "cmi.core.score.raw");
//        	setScore(Float.parseFloat(s));
//        }
    }

    /**
     * @return Returns the totaal.
     */
    public int getTotaal() {
        return totaal;
    }

    /**
     * Zet het totaal aantal scores waar deze score over gemiddeld is.
     *
     * @param totaal The totaal to set.
     */
    public void setTotaal(int totaal) {
        this.totaal = totaal;
        this.corrtotaal = totaal;
    }

    /**
     * Zet het gecorrigeerde totaal. Dat is het totaal aantal elementen waar de
     * score op betrekking heeft.
     *
     * @param correctie
     */
    public void setCorrTotaal(int correctie) {
        this.corrtotaal = correctie;
    }

    /**
     * @param total_time the total_time to set
     */
    public void setTotal_time(long total_time) {
        this.total_time = total_time;
    }

    /**
     * @return the total_time
     */
    public long getTotal_time() {
        return total_time;
    }

    //public void end()
    //{	if(lessonGroup instanceof Sco) ((Sco)lessonGroup).end();
    //}
}
