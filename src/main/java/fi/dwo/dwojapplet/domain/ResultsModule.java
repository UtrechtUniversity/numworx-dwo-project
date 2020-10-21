// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\ResultsModule.java
package fi.dwo.dwojapplet.domain;

import fi.beans.numworxlf.JOptionPane;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.gui.ScoDialog;
import fi.dwo.dwojapplet.gui.ScoPanel;
import fi.dwo.dwojapplet.gui.action.WrapSco;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
//import fi.dwo.dwojapplet.persistence.UserResultListMapper;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecuredTeacherResultsManager;
import nl.uu.fi.dwo.rest.dom.DomCoursesOfSchoolclassTree;
import nl.uu.fi.dwo.rest.dom.DomMappedResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.DomResultPlotMatrix;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.ResultTreeCalculator;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * This class managed the results (zooming, ordering, select courses) who are
 * showed to the teacher.
 *
 * @author M.J.B. Kupers
 *
 */
public class ResultsModule implements ResultsModuleIF, Comparator {

    private int orderedLessonIndex;

    private int orderedWay;

    private LessonGroup currentlyZoomedLesson;

    private SchoolClass currentlyZoomedUser;

    private UserGroup currentlyOrderedUser;

    private LessonGroup currentlyOrderedLesson;

    private Vector<UserResultList> userResultList;

    private Course[] courses;

    private Teacher teacher;

    protected DWO dwo;

    private DomResultsPerTeacher domresults;
    private DomResultTree resulttree;
    private DomCoursesOfSchoolclassTree coursetree;
    private DomResultPlotMatrix matrix;

    private DomMappedResultsPerTeacher mappedresults;

    ResultsModule(DomResultsPerTeacher domresults, DWO dwo)
    {
      this.dwo = dwo;
      this.domresults = domresults;
      this.mappedresults = new DomMappedResultsPerTeacher(domresults);
      try {
        PersistenceFacade.instance().toUser(mappedresults.getStudents().values());
      } catch (PersistenceException e) {
        // ignore, cacheing only.
      }
      this.resulttree = new DomResultTree(mappedresults);
      DomSchool school = DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool();
      this.coursetree = new DomCoursesOfSchoolclassTree(school, domresults);
      reset();
    }
    public ResultsModule(DomResultsPerTeacher domresults, DWO dwo, DomSchoolClass domclass, Course[] selection) throws Dwo2Exception, PersistenceException
    {
      this(domresults, dwo);
      this.courses = selection;
      currentlyZoomedUser = PersistenceFacade.instance().toSchoolClass(Collections.singleton(domclass))[0];      
      calculateCoursesByStudents(domclass);
    }
    private void calculateCoursesByStudents(DomSchoolClassId domclass)
        throws Dwo2Exception, PersistenceException {
      DomResultSchoolClass<DomResultCourseInClass> resultclass = resulttree.getResultTree().getChildren().get(domclass.getId());
      matrix = ResultTreeCalculator.GetScoreOfLeafCoursesByStudentsInClass(resulttree, resultclass);
      calculateUserResultList();
    }
    private void calculateUserResultList() throws Dwo2Exception, PersistenceException {
      userResultList = new Vector<>();
      int vSize = matrix.getvSize();
      int hSize = matrix.gethSize();
      if(hSize == 0) 
        return;
      for(int i = 0; i < vSize; i++) {
          UserResultList list = new UserResultList();
          list.setResultsModule(this);
          UserGroup userGroup = getUser(matrix.getvIndex(i));
          ResultScore[] scores = new ResultScore[hSize];
          list.setResultScore(scores);
          for(int j = 0; j < hSize; j++) {
            ResultScore scoresj = new ResultScore();
            scores[j] = scoresj;
            DomResultScore<?> mark = matrix.getMark(i, j);
            if (mark != null) 
            {
              long total_time = 0L; // mark.getTotalTime().longValue();
              float value = mark.getScore().floatValue();
              total_time = PersistenceFacade.instance().toTimeInMillis(mark.getTotalTime());
              if (value == 0.0f && total_time > 0) { // FIXME criterium
                value = -1f;
              }
              scoresj.setScore(value);
              scoresj.setTotal_time(total_time);
            }
            scoresj.setUserResultList(list);
            LessonGroup lessonGroup = getCourse(matrix.gethIndex(j));
            scoresj.setLessonGroup(lessonGroup);
            scoresj.setUserGroup(userGroup);
          }
          userResultList.add(list);
      }
    }
    
    
    private User getUser(DomResultStudent student) throws Dwo2Exception, PersistenceException {
      DomStudent s = student.getStudent();
      return PersistenceFacade.instance().getUser(s);
    }
    
    private UserGroup getUser(DomResultSchoolClass sc) throws Dwo2Exception, PersistenceException {
      DomSchoolClass s = sc.getSchoolClass();
      return PersistenceFacade.instance().toSchoolClass(Collections.singleton(s))[0];
   }
    
    private UserGroup getUser(DomResultScore r) throws Dwo2Exception, PersistenceException {
      if ( r instanceof DomResultStudent) {
        return getUser( (DomResultStudent) r);
      }
      if( r instanceof DomResultSchoolClass) {
        return getUser( (DomResultSchoolClass) r);
      }
      
      String id = r.getId();
      DomStudent student = mappedresults.getStudents().get(new PersistenceId(id));
      return PersistenceFacade.instance().getUser(student);
      //return DwoHelper.getCurrentFacadeUser();
    }

    private LessonGroup getCourse(DomResultScore r) throws Dwo2Exception, PersistenceException {
      String id = r.getId();
      PersistenceId pid = new PersistenceId(id);
      if (pid.getType() == PersistenceClassType.PersistentCourse)
      {
        DomCourse course = mappedresults.getCourses().get(pid);
        return PersistenceFacade.instance().toCourse(Collections.singletonList(course))[0];
      } 
      if (pid.getType() == PersistenceClassType.PersistentScoContext)
      {
        DomScoContext sco = mappedresults.getScoContexts().get(pid);
        return PersistenceFacade.instance().toSco(sco);
      }
      return null;
    }
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
    @Override
    public void showResult(ResultScore rs) {
        if ((rs.getUserGroup() instanceof SchoolClass)
                && (rs.getLessonGroup() instanceof Course)) {
            final SchoolClass sc = (SchoolClass) rs.getUserGroup();
            final Course course = (Course) rs.getLessonGroup();
            String klasnaam = sc.getName();
            String coursenaam = course.getName();
            Object[] params = {coursenaam, klasnaam}; // FIXME
            String message = TextMapper.format((TextMapper.GUIRSDLG_MSG), params);
            int result
                    = JOptionPane.showConfirmDialog(DwoHelper.getFrameForComponent(dwo), message, TextMapper.getText("delete"), JOptionPane.OK_CANCEL_OPTION);
            if (JOptionPane.OK_OPTION == result) {
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
	        if(!htmlSco && !sco.getLessonMode().equals(Sco.REVIEW)) {
	        	sco.setLessonMode(Sco.REVIEW);
	        	final boolean old = PersistenceFacade.instance().setAllowSuspendData(true);
	        	dwo.setWait();
	            Thread thread = new Thread() {	
	                public void run() {	
	                    boolean html5 = DwoHelper.isTest();
	                    final Sco wrap = html5 ? new WrapSco(sco) : sco;

			            ScoPanel sp = wrap.getScoPanel(dwo, user,(SchoolClass)currentlyZoomedUser);
			            dwo.setReady();
			            if(sp != null) {
			                ScoDialog.showScoDialog(dwo, sp, user, (SchoolClass)currentlyZoomedUser);
			            }
			            PersistenceFacade.instance().setAllowSuspendData(old);
			            sco.setLessonMode(Sco.NORMAL);
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
     * @throws PersistenceException 
     * @throws Dwo2Exception 
     *
     */
    //@Override
    public Vector zoomIn(UserGroup ug)  {
        orderedLessonIndex = -1;
        currentlyZoomedUser = (SchoolClass) ug;
        currentlyOrderedUser = null;
        
        if (currentlyZoomedLesson == null) {
          PersistenceId cid = PersistentSchoolClass.buildPersistenceId(Long.valueOf(ug.getID()));
          DomSchoolClass domclass = mappedresults.getSchoolClasses().get(cid);
          try {
            calculateCoursesByStudents(domclass);
          } catch (Dwo2Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (PersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        } else {
          PersistenceId sid = PersistentSchoolClass.buildPersistenceId(Long.valueOf(currentlyZoomedUser.getID()));
          PersistenceId cid = PersistentCourse.buildPersistenceId(Long.valueOf(currentlyZoomedLesson.getID()));
          DomResultSchoolClass<DomResultCourseInClass> resultClass = resulttree.getResultTree().getChildren().get(sid);
          DomResultCourseInClass resultCourse = resultClass.getChildren().get(cid);
          matrix = ResultTreeCalculator.GetScoreOfActivitiesOfCourseByStudentsInClass(resulttree, resultCourse, resultClass);
          try {
            calculateUserResultList();
          } catch (Dwo2Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (PersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        
        return getResults();
    }

    /**
     * Zooms out from the usergroup.
     *
     * @param ug The usergroup to zoom out.
     * @return The current list of results.
     */
    //@Override
    public Vector zoomOut(UserGroup ug) {
        orderedLessonIndex = -1;
        currentlyZoomedUser = null;
        currentlyOrderedUser = null;
        
        if(currentlyZoomedLesson == null) {
          matrix = ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(resulttree);
        } else {
          PersistenceId cid = PersistentCourse.buildPersistenceId(Long.valueOf(currentlyZoomedLesson.getID()));
          DomCourse aCourse = new DomCourse();
          aCourse.setId(cid);
          DomResultCourseInClass resultCourse = new DomResultCourseInClass(aCourse, ViewState.teachers);          
          matrix = ResultTreeCalculator.GetScoreOfTeacherClassesByActivitiesOfCourse(resulttree, resultCourse);
        }
        try {
          calculateUserResultList();
        } catch (Dwo2Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (PersistenceException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
        
        return getResults();
    }

    /**
     * Zooms in to the specified lessongroup.
     *
     * @param lg The lessongroup to zoom in.
     * @return The current list of results.
     *
     */
    //@Override
    public Vector zoomIn(LessonGroup lg) {
        orderedLessonIndex = -1;
        currentlyZoomedLesson = lg;
        currentlyOrderedLesson = null;
        if(currentlyZoomedUser != null) {
          PersistenceId sid = PersistentSchoolClass.buildPersistenceId(Long.valueOf(currentlyZoomedUser.getID()));
          PersistenceId cid = PersistentCourse.buildPersistenceId(Long.valueOf(lg.getID()));
          DomResultSchoolClass<DomResultCourseInClass> resultClass = resulttree.getResultTree().getChildren().get(sid);
          DomResultCourseInClass resultCourse = resultClass.getChildren().get(cid);
          matrix = ResultTreeCalculator.GetScoreOfActivitiesOfCourseByStudentsInClass(resulttree, resultCourse, resultClass);
        } else {
          PersistenceId cid = PersistentCourse.buildPersistenceId(Long.valueOf(currentlyZoomedLesson.getID()));
          DomCourse aCourse = new DomCourse();
          aCourse.setId(cid);
          DomResultCourseInClass resultCourse = new DomResultCourseInClass(aCourse, ViewState.teachers);          
            matrix = ResultTreeCalculator.GetScoreOfTeacherClassesByActivitiesOfCourse(resulttree, resultCourse);
        }
        try {
          calculateUserResultList();
        } catch (Dwo2Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (PersistenceException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
     
        
        return getResults();
    }

    /**
     * Zooms out from the lessongroup.
     *
     * @param lg The lessongroup to zoom out.
     * @return The current list of results.
     */
    //@Override
    public Vector zoomOut(LessonGroup lg) {
        orderedLessonIndex = -1;
        currentlyZoomedLesson = null;
        currentlyOrderedLesson = null;
        if(currentlyZoomedUser != null) {
          PersistenceId sid = PersistentSchoolClass.buildPersistenceId(Long.valueOf(currentlyZoomedUser.getID()));
          DomSchoolClassId sc = new DomSchoolClassId(sid);
          try {
            calculateCoursesByStudents(sc); // FIXME use "courses"
          } catch (Dwo2Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (PersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        } else {
          matrix = ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(resulttree);
          try {
            calculateUserResultList();
          } catch (Dwo2Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (PersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        
        
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
    @Override
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
    @Override
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
    @Override
    public Vector getResults() {
        return userResultList;
      
      
//        if ((currentlyZoomedLesson == null) && (currentlyZoomedUser == null)) {
//            try {
//                userResultList = PersistenceFacade.instance().getResults(courses, teacher);
//            }
//            catch (PersistenceException e) {
//                JOptionPane.showMessageDialog(dwo, e.getMessage());
//            }
//        } else if ((currentlyZoomedUser == null)
//                && (currentlyZoomedLesson instanceof Course)) {
//            try {
//                userResultList = PersistenceFacade.instance().getResults((Course) currentlyZoomedLesson, teacher);
//            }
//            catch (PersistenceException e) {
//                JOptionPane.showMessageDialog(dwo, e.getMessage());
//            }
//        } else if ((currentlyZoomedLesson == null)
//                && (currentlyZoomedUser instanceof SchoolClass)) {
//            try {
//                userResultList = PersistenceFacade.instance().getResults(courses, (SchoolClass) currentlyZoomedUser, teacher);
//            }
//            catch (PersistenceException e) {
//                JOptionPane.showMessageDialog(dwo, e.getMessage());
//            }
//        } else {
//            try {
//                userResultList = PersistenceFacade.instance().getResults((Course) currentlyZoomedLesson, (SchoolClass) currentlyZoomedUser, teacher);
//            }
//            catch (PersistenceException e) {
//                JOptionPane.showMessageDialog(dwo, e.getMessage());
//            }
//        }
//
//        if (userResultList != null) {
//            Collections.sort(userResultList, this);
//        }
//        return userResultList;
    }

    /**
     * Selects the specified courses.
     *
     * @param courses The courses to select.
     *
     */
    @Override
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
     * argument is less than, equal to, or greater than the second.
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
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
     * @return the value <code>0</code> if <code>f1</code> is numerically equal
     * to <code>f2</code>; a value less than <code>0</code> if <code>f1</code>
     * is numerically less than <code>f2</code>; and a value greater than
     * <code>0</code> if <code>f1</code> is numerically greater than
     * <code>f2</code>.
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
     * @return the value <code>0</code> if <code>f1</code> is numerically equal
     * to <code>f2</code>; a value less than <code>0</code> if <code>f1</code>
     * is numerically less than <code>f2</code>; and a value greater than
     * <code>0</code> if <code>f1</code> is numerically greater than
     * <code>f2</code>.
     */
    public int compareFloats(float f1, float f2) {
        if (f1 < f2) {
            return -1; // Neither val is NaN, thisVal is smaller
        }
        if (f1 > f2) {
            return 1; // Neither val is NaN, thisVal is larger
        }
        return 0;
    }

    /**
     * Returns the seleced courses.
     *
     * @return The selected courses.
     * @see fi.dwo.client.domain.ResultsModuleIF#getSelectedCourse()
     */
    @Override
    public Course[] getSelectedCourse() {
        return courses;
    }

    /**
     * Returns all the available courses.
     *
     * @return All the available courses.
     * @see fi.dwo.client.domain.ResultsModuleIF#getAllCourses()
     */
    @Override
    public Course[] getAllCourses() {
        return dwo.getCourses();
    }

    /**
     * Reset the ResultsModule. The zoom and order values are reset.
     *
     */
    //@Override
    public void reset() {
      PersistenceFacade.instance().setResultsModule(this);
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
     * @return
     * @see
     * fi.dwo.client.domain.ResultsModuleIF#selectCourses(fi.dwo.client.domain.Course[],
     * boolean)
     */
    @Override
    public Vector selectCourses(Course[] courses, boolean getResults) {
        this.courses = courses;
        if (getResults) {
            if (currentlyZoomedLesson == null) {
                // redo things.
                DomResultsPerTeacher result, source = new DomResultsPerTeacher();
                List<DomMapEntry<PersistenceId, DomCourse>> aCourses = new ArrayList<>(courses.length);
                for (Course c : courses) {
                  if (c.isWithChildren()) continue;
                  PersistenceId id = (PersistentCourse.buildPersistenceId(Long.valueOf(c.getID())));
                  aCourses.add(new DomMapEntry<PersistenceId, DomCourse>(id, null));
                }
                source.setCourses(aCourses);
                try {
                  result = SecuredTeacherResultsManager.selectedTeacherResults(DWO.getDwoProfile(), source);
                  this.domresults = result;
                  Iterator<DomMapEntry<PersistenceId, DomCourse>> i = result.getCourses().iterator();
                  while (i.hasNext()) {
                    DomMapEntry<nl.uu.fi.dwo.rest.persistence.PersistenceId, nl.uu.fi.dwo.rest.dom.entities.DomCourse> domMapEntry =
                        (DomMapEntry<nl.uu.fi.dwo.rest.persistence.PersistenceId, nl.uu.fi.dwo.rest.dom.entities.DomCourse>) i
                            .next();
                    if (domMapEntry.getValue() == null) i.remove(); // remove null entries, should be done at server side!
                  }
                  this.mappedresults = new DomMappedResultsPerTeacher(domresults);
                  this.resulttree = new DomResultTree(mappedresults);
                  DomSchool school = DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool();
                  this.coursetree = new DomCoursesOfSchoolclassTree(school, domresults);
                  zoomOut(currentlyZoomedLesson);
               } catch (Dwo2Exception e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
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
     *
     * @return The currently zoomed UserGroup.
     * @see fi.dwo.client.domain.ResultsModuleIF#getZoomedUserGroup()
     */
    @Override
    public UserGroup getZoomedUserGroup() {
        return currentlyZoomedUser;
    }

    /**
     * Returns the currently zoomed LessonGroup.
     *
     * @return The currently zoomed LessonGroup.
     * @see fi.dwo.client.domain.ResultsModuleIF#getZoomedLessonGroup()
     */
    @Override
    public LessonGroup getZoomedLessonGroup() {
        return currentlyZoomedLesson;
    }

}
