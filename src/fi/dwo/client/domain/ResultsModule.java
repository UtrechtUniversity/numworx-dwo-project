// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\ResultsModule.java

package fi.dwo.client.domain;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Vector;

//import fi.dwo.client.gui.DwoMessageDialog;
import fi.dwo.client.gui.ScoDialog;
import fi.dwo.client.gui.ScoPanel;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.MapperCreator;
import fi.dwo.client.persistence.MapperIF;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.persistence.UserResultListMapper;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JOptionPane;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.system.PersistenceException;

/**
 * This class managed the results (zooming, ordering, select courses) who are showed to the teacher.
 * @author M.J.B. Kupers
 *  
 */
public class ResultsModule implements ResultsModuleIF, Comparator {
    private int orderedLessonIndex;

    private int orderedWay;

    private LessonGroup currentlyZoomedLesson;

    private UserGroup currentlyZoomedUser;

    private UserGroup currentlyOrderedUser;

    private LessonGroup currentlyOrderedLesson;

    private Vector userResultList;

    private Course[] courses;

    private Teacher teacher;

    protected DWO dwo;

    /**
     * Creates a new ResultsModule Object.
     * 
     * @param courses The list of default courses to show.
     * @param teacher The teacher who wants to see the results
     * @param dwo The dwo to show errors.
     *  
     */
    public ResultsModule(Course[] courses, Teacher teacher, DWO dwo) {
        this.courses = courses;
        this.teacher = teacher;
        this.dwo = dwo;
        currentlyZoomedLesson = null;
        currentlyZoomedUser = null;
        currentlyOrderedUser = null;
        currentlyOrderedLesson = null;
        orderedLessonIndex = -1;
    }

    /**
     * Shows the result of the resultscore if the usergroup is an user, and the
     * lessongroup is a sco.
     * 
     * @param rs The resultscore wherefrom the result must been showed.
     *  
     */
    public void showResult(ResultScore rs) {
    	if((rs.getUserGroup() instanceof SchoolClass)
    			&& (rs.getLessonGroup() instanceof Course)
    		)
    	{
    		final SchoolClass sc = (SchoolClass) rs.getUserGroup();
    		final Course course = (Course) rs.getLessonGroup();
    		String klasnaam = sc.getName();
    		String coursenaam = course.getName();
    		Object[] params = { coursenaam, klasnaam };
    		String message = MessageFormat.format("Alle resultaten van ''{0}'' verwijderen voor {1}?", params);
    		int result = 
    		JOptionPane.showConfirmDialog(DwoHelper.getFrameForComponent(dwo), message, "Verwijderen", JOptionPane.OK_CANCEL_OPTION);
    		if(JOptionPane.OK_OPTION == result)
    		{
    			//System.out.println("VERWIJDEREN");
    			PersistenceFacade.instance().deleteCourseClassData(course, sc);
    			
    			rs.setScore(0.0f);
    		}
    		
    		return;
    	}
        if ((rs.getUserGroup() instanceof User)
                && (rs.getLessonGroup() instanceof Sco)) {
            
            final Sco sco = (Sco) rs.getLessonGroup();
            final User user = (User) rs.getUserGroup();
            boolean htmlSco = sco.getApplet().getClass().getName().equals("fi.popupurlapplet.PopUpURLApplet");
	        if(!htmlSco) {
	        dwo.setWait();
	            Thread thread = new Thread() {	
	                public void run() {	
	                	sco.setLessonMode(Sco.REVIEW);
			            ScoPanel sp = sco.getScoPanel(dwo, user);
			            dwo.setReady();
			            if(sp != null) {
			                ScoDialog.showScoDialog(dwo, sp, user, user.getInClass());
			            }
					}
				};
	            thread.start();/**/
	            //rs.end();
	        }
        }
    }

    /**
     * Zooms in to the specified usergroup.
     * 
     * @param ug The usergroup to zoom in.
     * @return The current list of results.
     *  
     */
    public Vector zoomIn(UserGroup ug) {
        orderedLessonIndex = -1;
        currentlyZoomedUser = ug;
        currentlyOrderedUser = null;
        return getResults();
    }

    /**
     * Zooms out from the usergroup.
     * 
     * @param ug The usergroup to zoom out.
     * @return The current list of results.
     */
    public Vector zoomOut(UserGroup ug) {
        orderedLessonIndex = -1;
        currentlyZoomedUser = null;
        currentlyOrderedUser = null;
        return getResults();
    }

    /**
     * Zooms in to the specified lessongroup.
     * 
     * @param lg The lessongroup to zoom in.
     * @return The current list of results.
     *  
     */
    public Vector zoomIn(LessonGroup lg) {
        orderedLessonIndex = -1;
        currentlyZoomedLesson = lg;
        currentlyOrderedLesson = null;
        return getResults();
    }

    /**
     * Zooms out from the lessongroup.
     * 
     * @param lg The lessongroup to zoom out.
     * @return The current list of results.
     */
    public Vector zoomOut(LessonGroup lg) {
        orderedLessonIndex = -1;
        currentlyZoomedLesson = null;
        currentlyOrderedLesson = null;
        return getResults();
    }

    /**
     * Order the result by the specified usergroup on the specified way.
     * 
     * @param ug The usergroup to sort.
     * @param orderWay The way of order.
     * @return The current list of results ordered as specified.
     *  
     */
    public Vector orderBy(UserGroup ug, int orderWay) {
        orderedLessonIndex = -1;
        orderedWay = orderWay;
        currentlyOrderedLesson = null;
        currentlyOrderedUser = ug;
        Collections.sort(userResultList, this);
        return userResultList;
    }

    /**
     * Order the result by the specified lessongroup on the specified way.
     * 
     * @param lg The lessongroup to sort.
     * @param orderWay The way of order.
     * @return The current list of results ordered as specified.
     *  
     */
    public Vector orderBy(LessonGroup lg, int orderWay) {
        orderedLessonIndex = -1;
        orderedWay = orderWay;
        currentlyOrderedLesson = lg;
        currentlyOrderedUser = null;
        Collections.sort(userResultList, this);
        return userResultList;
    }

    /**
     * Returns the results ordered and zoomed as specified.
     * 
     * @return The results ordered and zoomed as specified.
     *  
     */
    public Vector getResults() {
        if ((currentlyZoomedLesson == null) && (currentlyZoomedUser == null)) {
            try {
                userResultList = PersistenceFacade.instance().getResults(courses, teacher);
            } catch (PersistenceException e) {
            	JOptionPane.showMessageDialog(dwo, e.getMessage());
            }
        } else if ((currentlyZoomedUser == null)
                && (currentlyZoomedLesson instanceof Course)) {
            try {
                userResultList = PersistenceFacade.instance().getResults((Course) currentlyZoomedLesson, teacher);
            } catch (PersistenceException e) {
            	JOptionPane.showMessageDialog(dwo, e.getMessage());
            }
        } else if ((currentlyZoomedLesson == null)
                && (currentlyZoomedUser instanceof SchoolClass)) {
            try {
                userResultList = PersistenceFacade.instance().getResults(courses, (SchoolClass) currentlyZoomedUser, teacher);
            } catch (PersistenceException e) {
            	JOptionPane.showMessageDialog(dwo, e.getMessage());
            }
        } else {
            try {
                userResultList = PersistenceFacade.instance().getResults((Course) currentlyZoomedLesson, (SchoolClass) currentlyZoomedUser, teacher);
            } catch (PersistenceException e) {
            	JOptionPane.showMessageDialog(dwo, e.getMessage());
            }
        }

        if (userResultList != null) {
            Collections.sort(userResultList, this);
        }
        return userResultList;
    }

    /**
     * Selects the specified courses.
     * 
     * @param courses The courses to select.
     *  
     */
    public void selectCourses(Course[] courses) {
        this.courses = courses;
    }

    /**
     * Compares two UserResultList. The behaviour depends on the way how the
     * resultsmodule is specified.
     * 
     * @param o1 An UserResultList to compare.
     * @param o2 An UserResultList to compare with.
     * @return a negative integer, zero, or a positive integer as the first
     *         argument is less than, equal to, or greater than the second.
     * @see java.util.Comparator#compare(java.lang.Object,
     *      java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        UserResultList url1 = (UserResultList) o1;
        UserResultList url2 = (UserResultList) o2;

        if (currentlyOrderedLesson != null) {
            if (orderedLessonIndex != -1) {
                if (orderedWay == ResultsModuleIF.ASC) {
                    return compareFloats(url1.getResultScore()[orderedLessonIndex].getScore(), url2.getResultScore()[orderedLessonIndex].getScore());
                } else {
                    return compareFloats(url2.getResultScore()[orderedLessonIndex].getScore(), url1.getResultScore()[orderedLessonIndex].getScore());
                }
            } else {
                for (int i = 0; i < url1.getResultScore().length; i++) {
                    if (url1.getResultScore()[i].getLessonGroup() == currentlyOrderedLesson) {
                        orderedLessonIndex = i;
                        break;
                    }
                }
                return compare(o1, o2);
            }
        } else if (currentlyOrderedUser != null) {
            if (orderedWay == ResultsModuleIF.ASC) {
                return url1.getResultScore()[0].getUserGroup().getOrderName().compareTo(url2.getResultScore()[0].getUserGroup().getOrderName());
            } else {
                return url2.getResultScore()[0].getUserGroup().getOrderName().compareTo(url1.getResultScore()[0].getUserGroup().getOrderName());
            }

        }
        return 0;
    }

    /**
     * Compares the two specified <code>float</code> values. The sign of the
     * integer value returned is the same as that of the integer that would be
     * returned by the call:
     * 
     * <pre>
     * new Float(f1).compareTo(new Float(f2))
     * </pre>
     * 
     * @param f1 the first <code>float</code> to compare.
     * @param f2 the second <code>float</code> to compare.
     * @return the value <code>0</code> if <code>f1</code> is numerically
     *         equal to <code>f2</code>; a value less than <code>0</code>
     *         if <code>f1</code> is numerically less than <code>f2</code>;
     *         and a value greater than <code>0</code> if <code>f1</code> is
     *         numerically greater than <code>f2</code>.
     */
    public int compareFloats(Float f1, Float f2) {
        return compareFloats(f1.floatValue(), f2.floatValue());
    }

    /**
     * Compares the two specified <code>float</code> values. The sign of the
     * integer value returned is the same as that of the integer that would be
     * returned by the call:
     * 
     * <pre>
     * new Float(f1).compareTo(new Float(f2))
     * </pre>
     * 
     * @param f1 the first <code>float</code> to compare.
     * @param f2 the second <code>float</code> to compare.
     * @return the value <code>0</code> if <code>f1</code> is numerically
     *         equal to <code>f2</code>; a value less than <code>0</code>
     *         if <code>f1</code> is numerically less than <code>f2</code>;
     *         and a value greater than <code>0</code> if <code>f1</code> is
     *         numerically greater than <code>f2</code>.
     */
    public int compareFloats(float f1, float f2) {
        if (f1 < f2)
            return -1; // Neither val is NaN, thisVal is smaller
        if (f1 > f2)
            return 1; // Neither val is NaN, thisVal is larger
        return 0;
    }

    /**
     * Returns the seleced courses.
     * 
     * @return The selected courses.
     * @see fi.dwo.client.domain.ResultsModuleIF#getSelectedCourse()
     */
    public Course[] getSelectedCourse() {
        return courses;
    }

    /**
     * Returns all the available courses.
     * 
     * @return All the available courses.
     * @see fi.dwo.client.domain.ResultsModuleIF#getAllCourses()
     */
    public Course[] getAllCourses() {
        return dwo.getCourses();
    }

    /**
     * Reset the ResultsModule. The zoom and order values are reset.
     * 
     * @see fi.dwo.client.domain.ResultsModuleIF#reset()
     */
    public void reset() {
        MapperIF m = MapperCreator.instance(UserResultList.class);
        
        if(m instanceof UserResultListMapper) {
            ((UserResultListMapper) m).setResultsModule(this);
        }
        currentlyZoomedLesson = null;
        currentlyZoomedUser = null;
        currentlyOrderedUser = null;
        currentlyOrderedLesson = null;
        orderedLessonIndex = -1;
    }

    /**
     * Selects the specified courses and returns the current results.
     * 
     * @param courses The courses to select.
     * @param getResults Indicates if the results must be returned.
     * @see fi.dwo.client.domain.ResultsModuleIF#selectCourses(fi.dwo.client.domain.Course[],
     *      boolean)
     */
    public Vector selectCourses(Course[] courses, boolean getResults) {
        this.courses = courses;
        if (getResults) {
            if (currentlyZoomedLesson == null) {
                return getResults();
            } else {
                return userResultList;
            }
        } else {
            return new Vector();
        }
    }

    /**
     * Returns the currently zoomed UserGroup.
     * @return The currently zoomed UserGroup.
     * @see fi.dwo.client.domain.ResultsModuleIF#getZoomedUserGroup()
     */
    public UserGroup getZoomedUserGroup() {
        return currentlyZoomedUser;
    }

    /**
     * Returns the currently zoomed LessonGroup.
     * @return The currently zoomed LessonGroup.
     * @see fi.dwo.client.domain.ResultsModuleIF#getZoomedLessonGroup()
     */
    public LessonGroup getZoomedLessonGroup() {
        return currentlyZoomedLesson;
    }

}