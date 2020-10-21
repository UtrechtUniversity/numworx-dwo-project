package fi.dwo.dwojapplet.persistence;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.dwojapplet.domain.AppletConfig;
import fi.dwo.dwojapplet.domain.AppletData;
import fi.dwo.dwojapplet.domain.ClassCourse;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Guest;
import fi.dwo.dwojapplet.domain.ResultScore;
import fi.dwo.dwojapplet.domain.ResultsModuleIF;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.ScoBase;
import fi.dwo.dwojapplet.domain.Teacher;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.domain.UserResultList;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.persistence.cache.ReadOnly;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.CourseManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicCourseManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherFromToManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserCourseManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserResultsManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecuredStudentCoursesOfSchoolManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecuredTeacherResultsManager;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletConfig;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import java.applet.Applet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is the Facade between the Domain layer and the Persistence layer.
 * It functions as a singleton. <br>
 * An instance of the PersistanceFacade could be archived by calling the method
 * <code>instance()</code>.
 *
 * @author M.J.B. Kupers
 *
 */
public class PersistenceFacade {

    private static final SimpleDateFormat TOTAL_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.US);
    static {
      TOTAL_TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
      TOTAL_TIME_FORMAT.setLenient(true);

    }
    private static final Logger LOG = Logger.getLogger(PersistenceFacade.class.getName());

    //private static final Sco[] EMPTY_SCOS = new Sco[0];

    private volatile static PersistenceFacade _instance;

    private static final String[][] scormDatabaseLink = {
        {"cmi.core.score.raw", "score"},
        {"cmi.score.raw", "score"},
        {"cmi.suspend_data", "suspendData"},
        {"core.score.raw", "score"},
        {"suspend_data", "suspendData"},
        {"cmi.core.session_time", "session_time"},
        {"cmi.core.total_time", "total_time"},
        // { "cmi.session_time", "session_time" }, // pas op, wrong format!
        // { "cmi.total_time", "total_time" },
        {"core.session_time", "session_time"},
        {"core.total_time", "total_time"}
    };

    public static final int PROFILEOFFSET = -1234;

    private final CourseMapper courseMapper;
    private final UserMapper userMapper;
    private final ScoMapper scoMapper;
    private final SchoolMapper schoolMapper;
    private final ClassMapper  classMapper;
    
    /**
     * Empty constructor
     */
    private PersistenceFacade() {
      courseMapper = new CourseMapper();
      userMapper = new UserMapper();
      scoMapper = new ScoMapper();
      schoolMapper = new SchoolMapper();
      classMapper = new ClassMapper();
    }

    /**
     * Plak twee arrays aan elkaar.
     *
     * @param een
     * @param twee
     * @return
     */
    private Object[] combine_(Object[] een, Object[] twee) {
        Class c = een.getClass().getComponentType();
        Object[] result = (Object[]) java.lang.reflect.Array.newInstance(c, een.length + twee.length);
        System.arraycopy(een, 0, result, 0, een.length);
        System.arraycopy(twee, 0, result, een.length, twee.length);
        return result;
    }

    /**
     * =============================================================================
     * SYSTEM FUNCTIONALITY.
     *
     * Private support functions Server-side state information, logging and
     * cache manipulation CRUD operations on server-side.
     * =============================================================================
     */
    /**
     * Returns an instance of PersistenceFacade.
     *
     * Thread-safe with double locking for performance.
     *
     * @return fi.dwo.client.persistence.PersistenceFacade
     *
     */
    public static PersistenceFacade instance() {
        if (_instance == null) {
            synchronized (PersistenceFacade.class) {
                if (_instance == null) {
                    _instance = new PersistenceFacade();
                }
            }

        }
        return _instance;
    }

    

//    /**
//     * Returns all the objects of the specified class.<br>
//     * e.g. if the class is fi.dwo.client.domain.Course, all the Course objects
//     * representing the courses in the database are returned.
//     *
//     * @param c The class, indicating the type of Object to get.
//     * @return The objects representing the specified class.
//     * @throws fi.dwo.commons.exceptions.PersistenceException
//     *
//     */
//     private <T> T[] get(java.lang.Class<T> c) throws PersistenceException {
//        MapperIF<T> mapper = MapperCreator.instance(c);
//        try {
//            return mapper.get();
//        }
//        catch (IOException e) {
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (XmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//        catch (SQLException e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//    }
// choose one:
//     public Group[] getGroup() throws PersistenceException {
//       return get(Group.class);
//     }
     
//     public School[] getSchool() throws PersistenceException {
//       return get(School.class);
//     }
     
//     public AppletData[] getAppletData() throws PersistenceException {
//       return get(AppletData.class);
//     }
     
//    /**
//     * =============================================================================
//     * XMLRPC FUNCTIONALITY
//     * =============================================================================
//     */
//    /**
//     * Unpack the exception out of XML-RPC. The message of the specified
//     * exception is the name of the exceptionclass to return.
//     *
//     * @param e The exception.
//     * @param errorCode The errorcode.
//     * @return The unpackedException.
//     * @throws PersistenceException
//     */
//    private Exception getException(Exception e, int errorCode)
//            throws PersistenceException {
//        String exceptionClassName = e.getMessage();
//
//        Class excClass;
//        try {
//            excClass = Class.forName(exceptionClassName);
//            Class[] constrArgTypes = {int.class};
//            Constructor excConstr = excClass.getConstructor(constrArgTypes);
//            Object[] constrArgs = {new Integer(errorCode)};
//            return (Exception) excConstr.newInstance(constrArgs);
//        }
//        catch (Exception e1) {
//            throw new PersistenceException(
//                    PersistenceException.EX_UNKNOWN_ERROR, e);
//        }
//    }

    /*
     * =============================================================================
     * USER FUNCTIONALITY
     * =============================================================================
     */
    
    public boolean setAllowSuspendData(boolean allow) {
    	boolean old = ReadOnly.hasSuspendData;
    	ReadOnly.hasSuspendData = allow;
    	return old;
    }

    /*
     * =============================================================================
     * STUDENTSCO FUNCTIONALITY
     * =============================================================================
     */
    /**
     * Saves a value for a SCO and a user.
     *
     * @param sco The SCO of the value.
     * @param user The User of the value.
     * @param sc schoolclass of user
     * @param iDataModelElement Indicates which item must be saved.
     * @param iValue The value to save.
     * @return "true" or "false"
     * @throws PersistenceException
     */
    public String LMSSetValue(ScoBase sco, User user, SchoolClass sc, String iDataModelElement,
            String iValue) throws PersistenceException {
        if (user != null && !(user instanceof Guest)) {
            String result = "true";

            if (iValue == null) {
                iValue = "";
            }

            int uid = user.getUserID();
            int scoid = sco.getScoID();
            PersistenceId sgid = user.getSchoolGroupID();

            String key = mapDataModel(iDataModelElement);
            if (key.equals("score")) {
                double d;
                try {
                    d = Double.valueOf(iValue).doubleValue();
                }
                catch (NumberFormatException ex) {
                    d = 0;
                }
                if (Double.isNaN(d)) {
                    d = 0;
                }
                iValue = Double.toString(d);
            }

            int clsid = sc==null?0:sc.getID();
            result = StoreCreator.instance().setValue(uid, scoid, idOf(sgid), clsid, key, iValue);
            return result;
        } else {
            return "true";
        }

    }

    /**
     * Gets a value saved for a SCO and a user.
     *
     * @param sco The SCO wherefrom the value must be returned.
     * @param user The User wherefrom the value must be returned.
     * @param iDataModelElement The value to get.
     * @return The value for the iDataModelElement.
     * @throws PersistenceException If a database exception, or XML-RPC
     * exception occurres.
     */
    public String LMSGetValue(ScoBase sco, User user, SchoolClass cls, String iDataModelElement)
            throws PersistenceException {
        if (user != null && !(user instanceof Guest)) {
            int uid = user.getUserID();
            int scoid = sco.getScoID();
            int sgid = idOf(user.getSchoolGroupID());
            int clsid = cls==null?0:cls.getID();
            String key = mapDataModel(iDataModelElement);
            return StoreCreator.instance().getValue(uid, scoid, sgid, clsid, key);
        } else {
            return "";
        }
    }

    public String LMSCommit(ScoBase sco, User user, String dummy) throws PersistenceException {
        return StoreCreator.instance().commit(user.getUserID(), sco.getScoID(), dummy);
    }

//    boolean noRandom;
    /**
     * =============================================================================
     * COURSES FUNCTIONALITY
     * =============================================================================
     */
    /**
     * Als no_chilren, maar loaded wordt dan true
     */
    private static final Course[] NO_CHILDREN_LOADED = new Course[0];


    public List<DomCourse> getSelectedSchoolCourses(DomSchoolClass schoolClass) throws Dwo2Exception {
      DomCoursesOfSchoolClass4Teacher result = 
          SecureTeacherSchoolClassManager.getModules(schoolClass, DWO.getDwoProfile());
      
      Map<PersistenceId, DomCourse> allcourses = courseMapper.insertCache(result.getCourses());
      
      return result.getClassCourses().stream().parallel()
          .map(DomMapEntry::getValue)
          .filter(cc -> cc.getViewState() != ViewState.invisible)
          .map(cc -> allcourses.get(cc.getCourseId()))
          .filter(course -> !course.getWithChildren().booleanValue())
          .collect(Collectors.toList());  
    }
    
    
    
    /**
     * returns the selected courses for the specified schoolclass. folders are
     * removed
     *
     * @param schoolClass The schoolclass wherefrom the courses must selected.
     * @return The courses selected for the specified schoolclass.
     * @throws PersistenceException
     */
    public Course[] getSelectedSchoolCourses(SchoolClass schoolClass)
            throws PersistenceException {
        try {
            Vector<Course> v;
            DomSchoolClass domSchoolClass = new DomSchoolClass();
            domSchoolClass.setId(PersistentSchoolClass.buildPersistenceId((long)schoolClass.getID()));
            DomCoursesOfSchoolClass4Teacher result = 
                SecureTeacherSchoolClassManager.getModules(domSchoolClass, DWO.getDwoProfile());
            
            Map<PersistenceId, DomClassCourse4Teacher> cc = new HashMap<>();
            result.getClassCourses().forEach(
                entry -> {
                  DomClassCourse4Teacher dcc = entry.getValue();
                  if (dcc.getViewState() != ViewState.invisible) {
                    PersistenceId id = dcc.getCourseId();
                    cc.put((id), dcc);
                  }
                });
            Map<PersistenceId, DomCourse> allcourses = courseMapper.insertCache(result.getCourses());
            v = new Vector<>();
            for(PersistenceId i: cc.keySet()) {
              Course course = courseMapper.getObjectFromReturn(allcourses.get(i));
              if (course.isWithChildren()) continue;
              v.add(course);
              DomClassCourse4Teacher dcc = cc.get(i);
              if(dcc != null && dcc.getViewState() == ViewState.studentsAndTeachers) {
                course.link = new ClassCourse();
                course.link.setNotAfter(dcc.getNotAfter());
                course.link.setNotBefore(dcc.getNotBefore());
                Integer type = dcc.getType();
                if(type != null) course.link.setType(type);
                course.link.setAccessKey(dcc.getAccessKey());
                course.link.setClassCourseID(idOf(dcc.getId()));
                course.link.setCourseID(course.getID());
                course.link.setViewState(dcc.getViewState().ordinal());
                course.link.setClassID(schoolClass.getID());               
              } else {
                course.link = null;
              }
           }
            Course[] courses = v.toArray(new Course[v.size()]);
            
            return courses;
        }
        catch (Dwo2Exception e) {
          LOG.log(Level.SEVERE, null, e);
          throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }

    }

    public Course[] toCourse(List<DomCourse> org) throws PersistenceException {
      return courseMapper.getObjectFromReturn((org)); 
    }
    
    public User[] toUser(Collection<? extends DomUser> org) throws PersistenceException {
      return userMapper.getObjectFromReturn(new Vector(org)); 
   }
    
    
    /**
     * 
     * @param user
     * @return a server-sorted array of courses.
     * @throws PersistenceException
     */
    public Course[] getCoursesJS(User user) throws PersistenceException {
        try {
            List v;
            int profileId = getDwoProfileID();
            //int guestID = PROFILEOFFSET - profileId;
            if (user == null || user instanceof Guest) {
               // v = DbAccessCreator.instance().getCoursesJS(guestID);
              v = (PublicCourseManager.getCourses(DWO.getDwoProfile()));
            } else {
                if (user instanceof Teacher) {
                    //              v = DbAccessCreator.instance().getCourses(user.getUserID());
                    Object[] schoolCourses = courseMapper.getFromSchool(user.getSchool());
                    Object[] dwoCourses = courseMapper.getObjectFromReturn(new Vector<>(SecureUserCourseManager.getCourses(DWO.getDwoProfile())));
                    // caching side effect. UNDO, we doen nu lazy....
                    return (Course[]) combine_(dwoCourses, schoolCourses);
                } else {
                    SchoolClass schoolClass = user.getInClass();
                    if (schoolClass == null) {
                        //v = DbAccessCreator.instance().getCoursesJS(guestID);
                        v = (SecureUserCourseManager.getCourses(DWO.getDwoProfile()));

                    } else {
//                        v = DbAccessCreator.instance().getCoursesForClass(
//                                schoolClass.getID());
                        DomSchoolClass domschoolClass = new DomSchoolClass();
                        domschoolClass.setId(PersistentSchoolClass.buildPersistenceId((long)schoolClass.getID()));
                        DomCoursesOfSchoolClass coursesClass = SecuredStudentCoursesOfSchoolManager.getCoursesClass(domschoolClass, DWO.getDwoProfile());
                        Map<Integer, DomClassCourse> cc = new HashMap<>();
                        coursesClass.getClassCourses().forEach(
                            entry -> {
                              DomClassCourse dcc = entry.getValue();
                              int id = idOf(dcc.getCourseId());
                              cc.put(id, dcc);
                            });
                        Stream<DomCourseStudent> stream = coursesClass.getCourses().stream()
                           .map(DomMapEntry::getValue);
                        if(!schoolClass.hasIconizer())
                            stream = stream.filter(item -> !item.getWithChildren().booleanValue());
                        v = (stream.collect(Collectors.toList()));
                        
                        
                        
                        if(v.size()==0) return new Course[0];
// FIXME aanzetten als clipBeforeAfter weer in gebruik wordt genomen.
// Het XML-RPC protocol doet niet aan TIMEZONES 
// dat betekent dat date(0) niet werkt voor 'notAfter'
                        /*v = clipBeforeAfter(v);*/
                        Course[] courses = courseMapper.getObjectFromReturn(v);
                        for (Course course : courses) {
                          int id = course.getID();
                          DomClassCourse dcc = cc.get(id);
                          if(dcc != null) {
                            course.link = new ClassCourse();
                            course.link.setNotAfter(dcc.getNotAfter());
                            course.link.setNotBefore(dcc.getNotBefore());
                            Integer type = dcc.getType();
                            if(type != null) course.link.setType(type);
                          }
                        }
// FIXME hier maken we de caching effecten ongedaan.
                        undoCachingEffect(courses);
                        return courses;
                    }
                }
            }
            return courseMapper.getObjectFromReturn(v);
        }
        catch (Dwo2Exception e) {
          LOG.log(Level.SEVERE, null, e);
          throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
       }
    }
    
    public Course[] getImportCourses(School s, School school, int profileID) throws PersistenceException {
        try {
            List<DomCourse> v;
            DomSchoolAndProfile dom = new DomSchoolAndProfile();
            dom.setDomDwoProfile(DWO.getDwoProfile());
            dom.setDomSchool(new DomSchoolId(PersistentSchool.buildPersistenceId(Long.valueOf(s.getSchoolID()))));
            v = (SecureTeacherFromToManager.getCourses(dom));
            return courseMapper.getObjectFromReturn(v);
        }
        catch (Dwo2Exception e) {
          throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }
    }

    /**
     * Plak twee courses aan elkaar. Sorteer op naam. (a la mysql order by name)
     *
     * @param dwoCourses
     * @param schoolCourses
     * @return
     */
    private Course[] combineCourse(Object[] dwoCourses, Object[] schoolCourses) {
        Course[] courses = (Course[]) combine_(dwoCourses, schoolCourses);
        Arrays.sort(courses, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                Course c1 = (Course) o1;
                Course c2 = (Course) o2;
                return c1.getName().compareTo(c2.getName());
            }
        });
        return courses;
    }

    public final static Date DATE_NULL = new Date(0);


    /**
     * =============================================================================
     * DWOPROFILE FUNCTIONALITY
     * =============================================================================
     * @return 
     */
    private static int getDwoProfileID() {
        return DWO.getDwoProfileID();
    }

    /*
     * =============================================================================
     * REGISTRATION AND LOGIN FUNCTIONALITY
     * =============================================================================
     */

    /*
     * =============================================================================
     * SCHOOL FUNCTIONALITY
     * =============================================================================
     */
    
    @Deprecated
    static int idOf(PersistenceId id) {
		String s = id.getIdString();
		int dot = s.lastIndexOf(';');		
		return Integer.parseInt(s.substring(dot+1));
	}

    /*
     * =============================================================================
     * SCHOOLCLASS FUNCTIONALITY
     * =============================================================================
     */

    /*
     * =============================================================================
     * RESULTS FUNCTIONALITY
     * =============================================================================
     */

    /**
     * Returns the result for the SCO's in the specified course, for the
     * student.
     *
     * @param course The course where from the SCO's results must returned.
     * @return The result for the student and for the specified course.
     * @throws PersistenceException
     */
    public Vector<UserResultList> getUserResults(Course course) throws PersistenceException {
      User user = DwoHelper.getCurrentFacadeUser();
      StoreCreator.instance().commit(user.getUserID(), 0, "");
        try {
           {
            DomCourse dc = new DomCourse();
            dc.setId(PersistentCourse.buildPersistenceId(Long.valueOf(course.getID())));
            List<DomStudentScoContext> ssc = SecureUserResultsManager.getCourseResults(dc, DWO.getDwoProfile());
            UserResultList list = new UserResultList();
            if(course.getScoList()==null)course.loadScos();
            ResultScore[] scores = new ResultScore[course.getScoList().length];
            for (int i = 0; i < scores.length; i++) {
              scores[i] = new ResultScore();
            }
            for (int i = 0; i < ssc.size(); i++) {
              ResultScore r = new ResultScore();
              DomStudentScoContext s = ssc.get(i);
              r.setUserResultList(list);
              r.setScore((float)s.getScore());
              r.setUserGroup(user);
              long total_time = toTimeInMillis(s.getTotalTime());
              r.setTotal_time(total_time);
              PersistenceId pid = s.getScoID();
              Sco sco = getSco( idOf(pid)); // From cache
              r.setLessonGroup(sco);
              scores[(int)sco.getSequencenr()-1] = r;
            }
            list.setResultScore(scores);
            list.setResultsModule(resultsModule);
            Vector<UserResultList> vector = new Vector<>();
            vector.add(list);
            return vector;
          }
        }
        catch (Dwo2Exception e) {
          throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }
    }

    public long toTimeInMillis(String totalTime) {
      if (totalTime != null) 
      try {
          return TOTAL_TIME_FORMAT.parse(totalTime).getTime();
      } catch (Exception e) {
          LOG.log(Level.WARNING,"toTimeInMillis " + totalTime,e);
      }
      return 0;
    }


    public Vector getResultCount(int dwoProfile, int id) throws PersistenceException {
        try {
            DomSchoolClass schoolclass = new DomSchoolClass();
            schoolclass.setId(PersistentSchoolClass.buildPersistenceId(Long.valueOf(id)));
            DomCoursesOfSchoolClass4Teacher result = SecureTeacherSchoolClassManager.getModules(schoolclass, DWO.getDwoProfile());
            return getResultCount(result);
        }
        catch (Dwo2Exception e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }
    }

    /**
     * =============================================================================
     * CLASSCOURSE FUNCTIONALITY
     * =============================================================================
     * @return 
     */
    public boolean deleteCourseClassData(Course course, SchoolClass sc) {
        course.loadScos();
        Sco[] children = course.getScoList();
        User[] students = sc.getStudents();
        ArrayList<DomStudent> studentList = new ArrayList<>();
        for( User u: students) {
          DomStudent s = new DomStudent();
          studentList.add(s);
          s.setId(PersistentUser.buildPersistenceId(Long.valueOf(u.getUserID())));
        }
        DomClearStudentDataForScoAndClass dom = new DomClearStudentDataForScoAndClass();
        dom.setDomProfile(DWO.getDwoProfile());
// future...
        dom.setDomStudentList(studentList);
        DomSchoolClass domSchoolClass = new DomSchoolClass();
        dom.setDomSchoolClass(domSchoolClass);
        domSchoolClass.setId(PersistentSchoolClass.buildPersistenceId((long)sc.getID()));
        DomScoContext context = new DomScoContext();
        dom.setDomScoContext(context);
        for(Sco sco: children) {
          context.setId(PersistentScoContext.buildPersistenceId((long)sco.getID()));
          try {
            SecuredTeacherResultsManager.clearStudentResults(dom);
          } catch (Dwo2Exception e) {
            LOG.log(Level.SEVERE, "delete course data from class", e);
            return false;
          }
        }
        return true;
    }


    public void setCourseSequence(CourseMap[] courses, School school, CourseManager courseManager) throws PersistenceException {
        if (courses.length == 0) {
            return;
        }
//        Vector vector = new Vector(courses.length);
        for (int i = 0; i < courses.length; i++) {
            Course course = (Course) courses[i];
//			vector.add(new Integer(course.getID()));
			course.sequencenr = i; // install sequencenr

			DomCourseFull edit = new DomCourseFull();
			edit.setDwoProfileId(DWO.getDwoProfile().getId());
			edit.setSequenceNr(Long.valueOf(i));
			edit.setId(PersistentCourse.buildPersistenceId(Long.valueOf(course.getID())));
			edit.setNotVisible(course.isNotVisible()); // not nullable! FIXME
            try {
              courseManager.update(edit);
            } catch (Dwo2Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
        
        }

        int parent = 0;
        // selected courses are flat
        {
            parent = ((Course) courses[0]).getParentID();
            if (parent != ((Course) courses[courses.length - 1]).getParentID()) {
                try {
                    LOG.severe("Sequence error " + school);
                }
                catch (Exception e) {
                }
            }

        }

    }



    /**
     * Clears user data from mapper cache
     *
     * @param id userID whose data needs to be cleared.
     */
    public void clearCurrentUserDataCache(int id) {
        userMapper.removeObject(id);
    }

    /**
     * vul de children van de courses. Daarmee wordt een 'loadchildren' hopelijk
     * niet aangeroepen.
     *
     * @param courses
     */
    private void undoCachingEffect(Course[] courses) {
        for (int i = 0; i < courses.length; i++) {
            Course c = courses[i];
            if (c.isWithChildren()) {
                c.setChildren(NO_CHILDREN_LOADED);
            }
        }
        for (int i = 0; i < courses.length; i++) {
            Course c = courses[i];
            if (c.getParentID() != 0) {
                CourseMap parent = c.getParentMap();
                if (parent == null) {
                    try {
                        parent = courseMapper.get(c.getParentID()); // deze komt toch uit de cache?
                    }
                    catch (Exception e) {
                        continue;
                    }
                }
                parent.addChild(c);	// als dit werkt, zou dat mooi zijn!
            }
        }
    }


    private String mapDataModel(String element) {
        for (int i = 0; i < scormDatabaseLink.length; i++) {
            if (scormDatabaseLink[i][0].equals(element)) {
                return scormDatabaseLink[i][1];
            }
        }
        return element;
    }

    private ResultsModuleIF resultsModule;
    public void setResultsModule(ResultsModuleIF resultsModule) {
      this.resultsModule = resultsModule;
    }

    public Vector getResultCount(DomCoursesOfSchoolClass4Teacher result) {
      Vector v;
      v = new Vector();
      result.getClassCourses().forEach(entry -> {
        DomClassCourse4Teacher dcc = entry.getValue();
        int cid = idOf(dcc.getCourseId());
        Hashtable h = new Hashtable();
        h.put("courseID", cid);
        v.add(h);
      });
      return v;
    }

    
    


    public Course getCourse(int courseID) throws PersistenceException {
      return courseMapper.get(courseID);
    }

    public Sco getSco(int scoid) throws PersistenceException {
          return scoMapper.get(scoid);
    }

    public void clearCurrentScoDataCache() {
      scoMapper.removeAllObjects();     
    }

    public void clearCurrentCourseDataCache() {
      courseMapper.removeAllObjects();      
    }

    public void clearObjectInScoCache(int id) {
      scoMapper.removeObject(id);
    }

    public void removeObjectSco(int id) {
      scoMapper.removeObject(id);
    }

    public void removeObjectCourse(int id) {
      courseMapper.removeObject(id);
    }


    public void putUser(int id, User u) {
      userMapper.put(id, u);
    }

    public User getUser(DomStudent uid) throws PersistenceException {
      return toUser(Collections.singleton(uid))[0];
    }

    private AppletMapper appletMapper = new AppletMapper();
    public Class<Applet> getAppletClass(int appletID) throws PersistenceException {
      return appletMapper.get(appletID);
    }

    private AppletDataMapper appletDataMapper = new AppletDataMapper();
    public AppletData getAppletData(int appletID) throws PersistenceException {
      return appletDataMapper.get(appletID);
    }

    public void putSchool(int schoolID, School s) {
      schoolMapper.put(schoolID, s);
    }

    public void putSchoolClass(int id, SchoolClass cls) {
      classMapper.put(id, cls);
    }

    public SchoolClass[] getSchoolClass(User teacher) throws PersistenceException {
      return classMapper.getFromTeacher();
    }

    public User[] getUser(SchoolClass schoolClass) throws PersistenceException {
      return userMapper.get(schoolClass);
    }

    public SchoolClass[] getSchoolClass(School school) throws PersistenceException {
      return classMapper.getFromSchool();
    }

    public Sco[] getSco(Course course) throws PersistenceException {
      return scoMapper.get(course);
    }
    public Sco[] getTrash(Course course) throws PersistenceException {
      return scoMapper.getTrash(course);
    }

    public AppletConfig[] getAppletConfig(Locale locale) throws PersistenceException {
      try {
        List<DomAppletConfig> configurations = GuiCreator.instance().getConfigManager().getConfigurations(locale, DWO.getDwoProfile());
// Filter profile
        Iterator<DomAppletConfig> iter = configurations.iterator();
        while (iter.hasNext()) {
          DomAppletConfig ac = iter.next();
          if (ac.getDwoProfileId() == null) continue; // Global
          if (ac.getDwoProfileId().getId().equals(DWO.getDwoProfile().getId())) // Specifiek
            continue;
          iter.remove();
        }
        AppletConfig[] result = new AppletConfig[configurations.size()];
        for(int i = 0; i < result.length; i++) {
          result[i] = new AppletConfig( configurations.get(i) );
        }
        return result;
      } catch (Dwo2Exception e) {
        throw new PersistenceException(PersistenceException.EX_IO, e);
      }
    }

    public School[] getSchool(Boolean true1) throws PersistenceException {
      try {
          return schoolMapper.getFromExport();
      }
      catch (Dwo2Exception e) {
        throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
      }
    }

    public Course[] getCourses(Course parent) throws PersistenceException {
      return courseMapper.getFromCourse(parent);
    }

    public Course[] getCourses(School parent) throws PersistenceException {
      return courseMapper.getFromSchool(parent);
    }

    public School[] toSchool(Collection<? extends DomSchool> data) {
      return schoolMapper.getObjectFromDom(data);
    }
    
    public SchoolClass[] toSchoolClass(Collection<DomSchoolClass> data) throws PersistenceException {
      try {
        return classMapper.toSchoolClasses(new Vector(data));
      } catch (Dwo2Exception e) {
        throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
      }
    }

//    public Sco[] toSco(List<DomScoContext> org) throws PersistenceException {
//      Course parent = courseMapper.get(idOf(org.get(0).getCourseId()));
//      return scoMapper.toSco(parent, org);
//    }
    
    public Sco toSco(DomScoContext org) throws PersistenceException {
      Course parent = courseMapper.get(idOf(org.getCourseId()));
      return scoMapper.toSco(parent, Collections.singletonList(org))[0];
    }
}
