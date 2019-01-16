package fi.dwo.dwojapplet.persistence;

import fi.beans.private_base64code.StringCodeObject;
import fi.dwo.commons.exceptions.ClassException;
import fi.dwo.commons.exceptions.CourseException;
import fi.dwo.commons.exceptions.DwoXmlRpcException;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.exceptions.RegisterException;
import fi.dwo.commons.exceptions.SchoolException;
import fi.dwo.commons.exceptions.ScoException;
import fi.dwo.commons.persistence.DbAccessIF;
import fi.dwo.commons.persistence.DbAccessLogin;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.system.MD5;
import fi.dwo.dwojapplet.domain.Admin;
import fi.dwo.dwojapplet.domain.AppletConfig;
import fi.dwo.dwojapplet.domain.AppletData;
import fi.dwo.dwojapplet.domain.ClassCourse;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Group;
import fi.dwo.dwojapplet.domain.Guest;
import fi.dwo.dwojapplet.domain.ResultScore;
import fi.dwo.dwojapplet.domain.ResultsModule;
import fi.dwo.dwojapplet.domain.ResultsModuleIF;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.SchoolPasswdMap;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.ScoBase;
import fi.dwo.dwojapplet.domain.Teacher;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.domain.UserResultList;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.persistence.cache.ReadOnly;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.CourseManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicCourseManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherFromToManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserCourseManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserResultsManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecuredStudentCoursesOfSchoolManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecuredTeacherResultsManager;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.util.DomCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.xmlrpc.applet.XmlRpcException;

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

    private static final Sco[] EMPTY_SCOS = new Sco[0];

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

    /**
     * Empty constructor
     */
    private PersistenceFacade() {

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

    /**
     * Returns a object of the specified class, with the specified objectID.<br>
     * e.g. if the class is fi.dwo.client.domain.Course and the objectID is 1, a
     * Course object representing cours nr 1 (the ID field in the database) is
     * returned.
     *
     * @param oid The ID of the object to get.
     * @param c The class, indicating the type of Object to get.
     * @return Object The object representing the specified objectID and class.
     * @throws fi.dwo.commons.exceptions.PersistenceException
     *
     */
    public <T> T get(int oid, java.lang.Class<T> c) throws PersistenceException {
        MapperIF<T> mapper = MapperCreator.instance(c);

        try {
            return mapper.get(oid);
        }
        catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
            throw new PersistenceException(PersistenceException.EX_IO, e);
        }
        catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }
        catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB, e);
        }
    }

    /**
     * Returns all the objects of the specified class.<br>
     * e.g. if the class is fi.dwo.client.domain.Course, all the Course objects
     * representing the courses in the database are returned.
     *
     * @param c The class, indicating the type of Object to get.
     * @return The objects representing the specified class.
     * @throws fi.dwo.commons.exceptions.PersistenceException
     *
     */
     private <T> T[] get(java.lang.Class<T> c) throws PersistenceException {
        MapperIF<T> mapper = MapperCreator.instance(c);
        try {
            return mapper.get();
        }
        catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO, e);
        }
        catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }
        catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB, e);
        }
    }
// choose one:
     public Group[] getGroup() throws PersistenceException {
       return get(Group.class);
     }
     
     public School[] getSchool() throws PersistenceException {
       return get(School.class);
     }
     
     public AppletData[] getAppletData() throws PersistenceException {
       return get(AppletData.class);
     }
     
    /**
     * Returns all the objects of the specified class with the restriction of
     * the specified object.<br>
     * The meaning of this restriction is defined in the corresponding
     * mapper-class (e.g. CourseMapper). e.g. if the class is
     * fi.dwo.client.domain.Course, all the Course objects representing the
     * courses in the database with the specified restriction are returned.
     *
     * @param c The class, indicating the type of Object to get.
     * @param obj An object that specifies the restriction.
     *
     * @return Object[]
     * @throws fi.dwo.commons.exceptions.PersistenceException
     *
     */
    public <T> T[] get(java.lang.Class<T> c, Object obj)
            throws PersistenceException {
        MapperIF<T> mapper = MapperCreator.instance(c);
        try {
            return mapper.get(obj);
        }
        catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO, e);
        }
        catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }
        catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB, e);
        }
    }

    /**
     * This method saves an object in the database.<br>
     * school/schoolclass
     *
     * @param oid
     * @param obj
     * @throws fi.dwo.commons.exceptions.PersistenceException
     *
     */
    public <T> void put(int oid, T obj, Class<T> clz)
            throws PersistenceException {
        MapperIF<T> mapper = MapperCreator.instance(clz);
        mapper.put(oid, obj);

    }

//    public Hashtable getRecord(String tableName, String idCol, int oid)
//            throws IOException, XmlRpcException, SQLException, PersistenceException {
//        try {
//            Hashtable h = DbAccessCreator.instance().getRecord(tableName, idCol, oid);
//            return h;
//        }
//        catch (IOException e) {
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (SQLException e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//        catch (XmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//
//    }

//    public Vector getRecord(String tableName, Hashtable restriction, String seq)
//            throws IOException, XmlRpcException, SQLException, PersistenceException {
//        try {
//            Vector h = DbAccessCreator.instance().getTable(tableName, restriction, seq);
//            return h;
//        }
//        catch (IOException e) {
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (SQLException e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//        catch (XmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//
//    }

    /**
     * =============================================================================
     * XMLRPC FUNCTIONALITY
     * =============================================================================
     */
    /**
     * Unpack the exception out of XML-RPC. The message of the specified
     * exception is the name of the exceptionclass to return.
     *
     * @param e The exception.
     * @param errorCode The errorcode.
     * @return The unpackedException.
     * @throws PersistenceException
     */
    private Exception getException(Exception e, int errorCode)
            throws PersistenceException {
        String exceptionClassName = e.getMessage();

        Class excClass;
        try {
            excClass = Class.forName(exceptionClassName);
            Class[] constrArgTypes = {int.class};
            Constructor excConstr = excClass.getConstructor(constrArgTypes);
            Object[] constrArgs = {new Integer(errorCode)};
            return (Exception) excConstr.newInstance(constrArgs);
        }
        catch (Exception e1) {
            throw new PersistenceException(
                    PersistenceException.EX_UNKNOWN_ERROR, e);
        }
    }

    /**
     * =============================================================================
     * USER FUNCTIONALITY
     * =============================================================================
     */
//    /**
//     * Deletes a user out of the database.
//     *
//     * @param user The user to delete.
//     * @throws fi.dwo.commons.exceptions.RegisterException
//     *
//     */
//    public void deleteUser(User user) throws RegisterException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            dbAccess.deleteUser(user.getUserID());
//        }
//        catch (IOException e) {
//            throw new RegisterException(RegisterException.EX_IO);
//        }
//        catch (XmlRpcException e) {
//            throw new RegisterException(RegisterException.EX_XML_RPC);
//        }
//        catch (SQLException e) {
//            throw new RegisterException(RegisterException.EX_DB);
//        }
//
//    }

//    /**
//     * =============================================================================
//     * STUDENT FUNCTIONALITY
//     * =============================================================================
//     * @throws fi.dwo.commons.exceptions.PersistenceException
//     */
//    public void addStudentToClass(SchoolClass c, int id) throws PersistenceException {
//        try {
//            DbAccessCreator.instance().addStudentToClass(c.getID(), id);
//        }
//        catch (IOException e) {
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (SQLException e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//        catch (XmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//        catch (DwoXmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//    }

//    public boolean removeStudentFromClass(int classId, int userId) throws PersistenceException {
//        try {
//            return DbAccessCreator.instance().removeStudentFromClass(classId, userId);
//        }
//        catch (IOException e) {
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (SQLException e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//        catch (XmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//        catch (DwoXmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//    }

//    /**
//     * =============================================================================
//     * TEACHER FUNCTIONALITY
//     * =============================================================================
//     * @throws fi.dwo.commons.exceptions.PersistenceException
//     */
//    public void addTeacherToClass(SchoolClass c, int id) throws PersistenceException {
//        try {
//            DbAccessCreator.instance().addTeacherToClass(c.getID(), id);
//        }
//        catch (IOException e) {
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (SQLException e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//        catch (XmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//        catch (DwoXmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//    }
    
    public boolean setAllowSuspendData(boolean allow) {
    	boolean old = ReadOnly.hasSuspendData;
    	ReadOnly.hasSuspendData = allow;
    	return old;
    }

//    public boolean removeTeacherFromClass(int classId, int userId) throws PersistenceException {
//        try {
//            return DbAccessCreator.instance().removeTeacherFromClass(classId, userId);
//        }
//        catch (IOException e) {
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (SQLException e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//        catch (XmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//        catch (DwoXmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//    }

//    /**
//     * =============================================================================
//     * SCO FUNCTIONALITY
//     * =============================================================================
//     * @param sequencenr
//     * @throws PersistenceException
//     * @deprecated
//     */
//    private int addSco(int courseID, String name, String description, int appletID, String launchdata, int sequencenr) throws PersistenceException {
//    	try {
//			return DbAccessCreator.instance().addSco(courseID, name, description, appletID, launchdata, sequencenr);
//		} catch (DwoXmlRpcException e) {
//			throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//		} catch (IOException e) {
//			throw new PersistenceException(PersistenceException.EX_IO, e);
//		} catch (XmlRpcException e) {
//			throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//		} catch (SQLException e) {
//			throw new PersistenceException(PersistenceException.EX_DB, e);
//		}
//    
//    }

//    public Vector getScos(Hashtable restriction, String SEQUENCE_NR)
//            throws IOException, XmlRpcException, SQLException, PersistenceException {
//        try {
//            Vector v = DbAccessCreator.instance().getTable("tblScoView", restriction, SEQUENCE_NR);
//            return v;
//        }
//        catch (IOException e) {
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (SQLException e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//        catch (XmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//    }

//    /**
//     * Adds a sco to the specified course.
//     *
//     * @param course The course where the sco must be added.
//     * @param appletConfig The AppletConfig of the new sco (it contains the
//     * applet and the default launchdata).
//     * @param name The name of the new sco.
//     * @param description The description of the new sco.
//     * @param showScore
//     * @return The new sco. If an exception occurs, null was returned.
//     * @throws ScoException
//     * @deprecated
//     */
//    private Sco addSco(Course course, AppletConfig appletConfig, String name,
//            String description, boolean showScore) throws ScoException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                Sco[] scos = course.getScoList();
//                int max = 0;
//                for (int i = 0; i < scos.length; i++) {
//                    if (scos[i].getSequencenr() > max) {
//                        max = scos[i].getSequencenr();
//                    }
//                }
//                int result;
//                Sco sco = new Sco();
//// if true use 'oldschool'
//                if (showScore) {
//                    result = dbAccess.addSco(course.getID(), name, description,
//                            appletConfig.getID(), ++max);
//                } else {
//                    result = dbAccess.addSco(course.getID(), name, description,
//                            appletConfig.getID(), ++max, false);
//                    sco.setShowScore(Boolean.FALSE);
//                }
//                sco.setScoID(result);
//                sco.setAppletID(appletConfig.getAppletID());
//                sco.setName(name);
//                sco.setDescription(description);
//                sco.setSequencenr(max);
//                sco.setCourse(course);
//                sco.setLaunchdata((Hashtable) new StringCodeObject((String) appletConfig.getLaunchdata()).toObject());
//                sco.setCourseChanged(false);
//                MapperCreator.instance(Sco.class).put(result, sco);
//                return sco;
//            }
//            catch (IOException e) {
//                throw new ScoException(ScoException.EX_IO);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (ScoException) getException(e, e.code);
//                } else {
//                    throw new ScoException(ScoException.EX_XML_RPC);
//                }
//            }
//            catch (SQLException e) {
//                throw new ScoException(ScoException.EX_DB);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (ScoException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new ScoException(ScoException.EX_UNKNOWN_ERROR);
//        }
//    }

//    public Sco[] getEditableScos(School school, DwoProfile profile) {
//        try {
//            return (Sco[]) MapperCreator.instance(Sco.class).get(new Object[]{school, profile});
//        }
//        catch (Exception e) {
//            LOG.log(Level.SEVERE, null, e);
//        }
//        return EMPTY_SCOS;
//    }

//    /**
//     * Updates the name and the description of the sco in the database.
//     *
//     * @param sco The sco to update in the database.
//     * @return True if the sco was successfully changed.
//     * @throws ScoException
//     */
//    public boolean updateSco(Sco sco) throws ScoException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                MapperCreator.instance(Sco.class).removeObject(sco.getID());
//                if (sco.isCourseChanged()) {
//                    dbAccess.moveSco(sco.getID(), sco.getCourse().getID(), sco.getSequencenr(), sco.getScoName());
//                    sco.setCourseChanged(false);
//                }
//                if (sco.isDataChanged()) {
//                    boolean result;
//
//                    if (sco.hasFeature(Sco.JSON_OUT)) {
//                        byte[] launchdataBytes = sco.getLaunchdataBytes();
//                        System.out.println("JSON launchdata " + sco.getID() + " " + launchdataBytes.length + " bytes");
//                        result = StoreCreator.instance().changeSco(sco.getID(), sco.getScoName(), sco.getDescription(),
//                                true, launchdataBytes, sco.getShowScore());
//                    }
//                    result = StoreCreator.instance().changeSco(
//                            sco.getID(), sco.getScoName(), sco.getDescription(),
//                            true, sco.getLaunchdataString(), sco.getShowScore());
//
////         			if(sco.getShowScore() != null)
////         		            		result = dbAccess.changeSco(sco.getID(), sco.getScoName(), sco
////					        .getDescription(), sco.getLaunchdataString(), sco.isShowScore());
////         			else
////		            		result = dbAccess.changeSco(sco.getID(), sco.getScoName(), sco
////							        .getDescription(), sco.getLaunchdataString());
////         			
//                    sco.setDataChanged(false);
//                    return result;
//                } else {
//                    if (sco.getShowScore() != null) {
//                        return dbAccess.changeSco(sco.getID(), sco.getScoName(), sco.getDescription(), sco.isShowScore());
//                    } else {
//                        return dbAccess.changeSco(sco.getID(), sco.getScoName(), sco.getDescription());
//                    }
//
//                }
//
//            }
//            catch (IOException e) {
//                throw new ScoException(ScoException.EX_IO);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (ScoException) getException(e, e.code);
//                } else {
//                	if(e.getMessage().contains("PacketTooBigException"))
//                		throw new ScoException(ScoException.SE_TOO_BIG, e);
//                    throw new ScoException(ScoException.EX_XML_RPC, e);
//                }
//            }
//            catch (SQLException e) {
//                throw new ScoException(ScoException.EX_DB, e);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (ScoException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new ScoException(ScoException.EX_UNKNOWN_ERROR, e);
//        }
//    }

//    /**
//     * Deletes the specified sco. The results of the students at this sco will
//     * also been deleted.
//     *
//     * @param sco The sco to delete.
//     * @return Returns true if the sco is successfully deleted.
//     * @throws ScoException
//     */
//    public boolean deleteSco(Sco sco) throws ScoException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                boolean returnValue = dbAccess.deleteSco(sco.getID());
//                if (returnValue) {
//                    MapperCreator.instance(Sco.class).removeObject(sco.getID());
//
//                    /*
//                     * Delete the sco in the course, and reset all the
//                     * sequencenrs
//                     */
//                    Sco[] scos = sco.getCourse().getScoList();
//                    Sco[] tmp = new Sco[scos.length - 1];
//                    int div = 0;
//                    for (int i = 0; i < scos.length; i++) {
//                        if (scos[i] != sco) {
//                            if (scos[i].getSequencenr() > sco.getSequencenr()) {
//                                scos[i]
//                                        .setSequencenr(scos[i].getSequencenr() - 1);
//                                scos[i].setCourseChanged(false);
//                            }
//                            tmp[i + div] = scos[i];
//                        } else {
//                            div--;
//                        }
//                    }
//                    sco.getCourse().setScoList(tmp);
//                }
//                return returnValue;
//            }
//            catch (IOException e) {
//                throw new ScoException(CourseException.EX_IO, e);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (ScoException) getException(e, e.code);
//                } else {
//                    throw new ScoException(ScoException.EX_XML_RPC, e);
//                }
//            }
//            catch (SQLException e) {
//                throw new ScoException(ScoException.EX_DB, e);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (ScoException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new ScoException(ScoException.EX_UNKNOWN_ERROR, e);
//        }
//    }

// ALLEEN VOOR ADMINISTRATORS
//    public boolean unsafeSaveSco(Sco sco) throws ScoException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                MapperCreator.instance(Sco.class).put(sco.getID(), sco);
//                if (sco.isDataChanged()) {
//                    boolean result;
//                    if (sco.hasFeature(Sco.JSON_OUT)) {
//                        byte[] launchdataBytes = sco.getLaunchdataBytes();
//                        System.out.println("JSON launchdata " + sco.getID() + " " + launchdataBytes.length + " bytes");
//                        result = StoreCreator.instance().changeSco(sco.getID(), sco.getScoName(), sco.getDescription(),
//                                false, // hier dus ook.
//                                launchdataBytes, sco.getShowScore());
//                    }
//                    result = StoreCreator.instance().changeSco(sco.getID(), sco.getScoName(),
//                            sco.getDescription(),
//                            false, // Daar is het om begonnen...
//                            sco.getLaunchdataString(), sco.getShowScore());
//                    sco.setDataChanged(false);
//                    return result;
//                } 
//                return true;
//            }
//            catch (IOException e) {
//                throw new ScoException(ScoException.EX_IO, e);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (ScoException) getException(e, e.code);
//                } else {
//                    throw new ScoException(ScoException.EX_XML_RPC, e);
//                }
//            }
//            catch (SQLException e) {
//                throw new ScoException(ScoException.EX_DB, e);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (ScoException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new ScoException(ScoException.EX_UNKNOWN_ERROR, e);
//        }
//    }

//    /**
//     * Swap de sequencenrs van twee sco's. Beide sco's moeten van dezelfde
//     * course zijn.
//     *
//     * @param sco1
//     * @param sco2
//     * @return true
//     * @throws ScoException
//     */
//    public boolean swapScoSequenceNr(Sco sco1, Sco sco2) throws ScoException {
//        boolean result = false;
//        if (sco1.getCourse() != sco2.getCourse()) {
//            throw new ScoException(ScoException.EX_DB);
//        }
//
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                int nr1 = sco1.getSequencenr();
//                int nr2 = sco2.getSequencenr();
//                result = dbAccess.changeScoSequenceNr(sco1.getScoID(), nr2, sco2.getScoID(), nr1);
//                if (result) {
//                    Sco[] scos = sco1.getCourse().getScoList();
//                    scos[nr2 - 1] = sco1;
//                    scos[nr1 - 1] = sco2;
//                    sco1.setSequencenr(nr2);
//                    sco2.setSequencenr(nr1);
//                    sco1.setCourseChanged(false);
//                    sco1.setCourseChanged(false);
//                }
//            }
//            catch (DwoXmlRpcException e) {
//                throw (ScoException) getException(e, e.code);
//            }
//            catch (SQLException e) {
//                throw new ScoException(ScoException.EX_DB, e);
//            }
//            catch (IOException e) {
//                throw new ScoException(ScoException.EX_IO, e);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (ScoException) getException(e, e.code);
//                } else {
//                    throw new ScoException(ScoException.EX_XML_RPC, e);
//                }
//            }
//        }
//        catch (PersistenceException e) {
//            throw new ScoException(ScoException.EX_UNKNOWN_ERROR, e);
//        }
//        return result;
//    }

    /**
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

//    /**
//     * Add a course to a school.
//     *
//     * @param s
//     * @param name
//     * @param description
//     * @param profile
//     * @return
//     * @throws CourseException
//     */
//    public Course addCourse(School s, String name, String description,
//            int profile) throws CourseException {
//        return addCourse(s, name, description, profile, null, false);
//    }

//    /**
//     * Returns all the courses that are selected for the specified class. A
//     * teacher could select some courses for a schoolclass.
//     *
//     * @param schoolClass The user to select courses from.
//     * @return The courses for the specified class.
//     * @throws PersistenceException If a database or xml-rpc exception occurs.
//     * @see
//     * fi.dwo.client.persistence.PersistenceFacade#selectCoursesForClass(int,
//     * int)
//     */
//    public Course[] getCourses(SchoolClass schoolClass) throws PersistenceException {
//        try {
//            Vector v;
//            v = DbAccessCreator.instance().getCoursesForClass(schoolClass.getID());
//            MapperIF mapper = MapperCreator.instance(Course.class);
//            return (Course[]) mapper.getObjectFromReturn(v);
//
//        }
//        catch (IOException e) {
//            System.out.println(e.getMessage());
//            LOG.log(Level.SEVERE, null, e);
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (XmlRpcException e) {
//            System.out.println(e.getMessage());
//            LOG.log(Level.SEVERE, null, e);
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//        catch (SQLException e) {
//            System.out.println(e.getMessage());
//            LOG.log(Level.SEVERE, null, e);
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//
//    }

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
            Vector v;
            DomSchoolClass domSchoolClass = new DomSchoolClass();
            domSchoolClass.setId(PersistentSchoolClass.buildPersistenceId((long)schoolClass.getID()));
            DomCoursesOfSchoolClass4Teacher result = 
                SecureTeacherSchoolClassManager.getModules(domSchoolClass, DWO.getDwoProfile());
            
            Map<Integer, DomClassCourse4Teacher> cc = new HashMap<>();
            result.getClassCourses().forEach(
                entry -> {
                  DomClassCourse4Teacher dcc = entry.getValue();
                  int id = idOf(dcc.getCourseId());
                  cc.put(id, dcc);
                });
            v = result.getCourses().stream().map(DomMapEntry::getValue).filter(
              // p.getWithChildren can be null, meaning false
                p-> (p.getWithChildren() == null || !p.getWithChildren().booleanValue())
                && cc.containsKey(idOf(p.getId())))
                .filter(p-> cc.get(idOf(p.getId())).getViewState() != ViewState.invisible) // remove invisible classcourses
                .collect(()->{ return new Vector<DomCourse>();}, (l,e) -> l.add(e), (l,ee)-> l.addAll(ee));
            MapperIF<Course> mapper = MapperCreator.instance(Course.class);
            if(mapper instanceof CourseMapper) {
              ((CourseMapper) mapper).insertCache(result.getCourses());
            }
            Course[] courses = mapper.getObjectFromReturn(v);
            for (Course course : courses) {
              int id = course.getID();
              DomClassCourse4Teacher dcc = cc.get(id);
              if(dcc != null && dcc.getViewState() == ViewState.studentsAndTeachers) {
                course.link = new ClassCourse();
                course.link.setNotAfter(dcc.getNotAfter());
                course.link.setNotBefore(dcc.getNotBefore());
                Integer type = dcc.getType();
                if(type != null) course.link.setType(type);
                course.link.setAccessKey(dcc.getAccessKey());
                course.link.setClassCourseID(idOf(dcc.getId()));
                course.link.setCourseID(id);
                course.link.setViewState(dcc.getViewState().ordinal());
                course.link.setClassID(schoolClass.getID());               
              } else {
                course.link = null;
              }
            }
              return courses;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            LOG.log(Level.SEVERE, null, e);
            throw new PersistenceException(PersistenceException.EX_IO, e);
        }
        catch (XmlRpcException e) {
            System.out.println(e.getMessage());
            LOG.log(Level.SEVERE, null, e);
            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            LOG.log(Level.SEVERE, null, e);
            throw new PersistenceException(PersistenceException.EX_DB, e);
        } catch (Dwo2Exception e) {
          System.out.println(e.getMessage());
          LOG.log(Level.SEVERE, null, e);
          throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }

    }

    
    /**
     * 
     * @param user
     * @return a server-sorted array of courses.
     * @throws PersistenceException
     */
    public Course[] getCoursesJS(User user) throws PersistenceException {
        try {
            MapperIF<Course> mapper = MapperCreator.instance(Course.class);
            Vector v;
            int profileId = getDwoProfileID();
            //int guestID = PROFILEOFFSET - profileId;
            if (user == null || user instanceof Guest) {
               // v = DbAccessCreator.instance().getCoursesJS(guestID);
              v = new Vector<>(PublicCourseManager.getCourses(DWO.getDwoProfile()));
            } else {
                if (user instanceof Teacher) {
                    //              v = DbAccessCreator.instance().getCourses(user.getUserID());
                    Object[] schoolCourses = mapper.get(user.getSchool());
                    Object[] dwoCourses = mapper.getObjectFromReturn(new Vector<>(SecureUserCourseManager.getCourses(DWO.getDwoProfile())));
                    // caching side effect. UNDO, we doen nu lazy....
                    //MapperCreator.instance(Sco.class).get(new Object[] { user.getSchool(), ((DwoIF) DwoHelper.getApplet()).getDwoProfile()} );
                    return (Course[]) combine_(dwoCourses, schoolCourses);
                } else {
                    SchoolClass schoolClass = user.getInClass();
                    if (schoolClass == null) {
                        //v = DbAccessCreator.instance().getCoursesJS(guestID);
                        v = new Vector<>(SecureUserCourseManager.getCourses(DWO.getDwoProfile()));

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
                        v = new Vector(stream.collect(Collectors.toList()));
                        
                        
                        
                        if(v.size()==0) return new Course[0];
// FIXME aanzetten als clipBeforeAfter weer in gebruik wordt genomen.
// Het XML-RPC protocol doet niet aan TIMEZONES 
// dat betekent dat date(0) niet werkt voor 'notAfter'
                        /*v = clipBeforeAfter(v);*/
                        Course[] courses = mapper.getObjectFromReturn(v);
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
            return mapper.getObjectFromReturn(v);
        }
        catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
            throw new PersistenceException(PersistenceException.EX_IO, e);
        }
        catch (XmlRpcException e) {
            LOG.log(Level.SEVERE, null, e);
            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }
        catch (SQLException e) {
            LOG.log(Level.SEVERE, null, e);
            throw new PersistenceException(PersistenceException.EX_DB, e);
        } catch (Dwo2Exception e) {
          LOG.log(Level.SEVERE, null, e);
          throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
       }

   	
    }
    
    
    
    
//    /**
//     * Returns all the courses for the specified user. A teacher could select
//     * some courses for a schoolclass. A student sees active selected courses
//     * from his schoolclass or a profile.
//     *
//     * @param user The user to select courses from.
//     * @return The courses for the specified user.
//     * @throws PersistenceException If a database or xml-rpc exception occurs.
//     */
//    public Course[] getCourses(User user) throws PersistenceException {
////        throw new RuntimeException("This routine must be rewriten to use DbAccessCreator.instance().getCourses(SchoolID)");
//        try {
//            MapperIF<Course> mapper = MapperCreator.instance(Course.class);
//            Vector<?> v;
//            int profileId = getDwoProfileID();
//            int guestID = PROFILEOFFSET - profileId;
//            if (user == null) {
//            //    v = DbAccessCreator.instance().getCourses(guestID);
//                v = new Vector<>(PublicCourseManager.getCourses(DWO.getDwoProfile()));
//            } else {
//                if (user instanceof Teacher) {
//                    //              v = DbAccessCreator.instance().getCourses(user.getUserID());
//                    Object[] schoolCourses = mapper.get(user.getSchool());
//                    Object[] dwoCourses = mapper.getObjectFromReturn(DbAccessCreator.instance().getCourses(guestID));
//                    // caching side effect. UNDO, we doen nu lazy....
//                    //MapperCreator.instance(Sco.class).get(new Object[] { user.getSchool(), ((DwoIF) DwoHelper.getApplet()).getDwoProfile()} );
//                    return combineCourse(dwoCourses, schoolCourses);
//                } else {
//                    SchoolClass schoolClass = user.getInClass();
//                    if (schoolClass == null) {
//                        v = DbAccessCreator.instance().getCourses(guestID);
//                    } else {
//                        v = DbAccessCreator.instance().getCoursesForClass(
//                                schoolClass.getID());
//                        if(v.size()==0) return new Course[0];
//// FIXME aanzetten als clipBeforeAfter weer in gebruik wordt genomen.
//// Het XML-RPC protocol doet niet aan TIMEZONES 
//// dat betekent dat date(0) niet werkt voor 'notAfter'
//                        v = clipBeforeAfter(v);
//                        Course[] courses = (Course[]) mapper.getObjectFromReturn(v);
//// FIXME hier maken we de caching effecten ongedaan.
//                        undoCachingEffect(courses);
//                        return courses;
//                    }
//                }
//            }
//            return mapper.getObjectFromReturn(v);
//        }
//        catch (IOException e) {
//            System.out.println(e.getMessage());
//            LOG.log(Level.SEVERE, null, e);
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (XmlRpcException e) {
//            System.out.println(e.getMessage());
//            LOG.log(Level.SEVERE, null, e);
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//        catch (SQLException e) {
//            System.out.println(e.getMessage());
//            LOG.log(Level.SEVERE, null, e);
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        } catch (Dwo2Exception e) {
//          System.out.println(e.getMessage());
//          LOG.log(Level.SEVERE, null, e);
//          throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//       }
//
//    }

    public Course[] getImportCourses(School s, School school, int profileID) throws PersistenceException {
        try {
            Vector v;
            DomSchoolAndProfile dom = new DomSchoolAndProfile();
            dom.setDomDwoProfile(DWO.getDwoProfile());
            dom.setDomSchool(new DomSchoolId(PersistentSchool.buildPersistenceId(Long.valueOf(s.getSchoolID()))));
            //v = DbAccessCreator.instance().getImportCourses(s.getSchoolID(), school.getSchoolID(), profileID);
            v = new Vector<>(SecureTeacherFromToManager.getCourses(dom));
            MapperIF<Course> mapper = MapperCreator.instance(Course.class);
            return mapper.getObjectFromReturn(v);
        }
        catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO, e);
        }
        catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }
        catch (SQLException e) {
            LOG.log(Level.SEVERE, null, e);
            throw new PersistenceException(PersistenceException.EX_DB, e);
        } catch (Dwo2Exception e) {
          throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }

    }

//    /**
//     * Deletes the specified course. The sco's and corresponding results will
//     * also be deleted.
//     *
//     * @param course The course to delete.
//     * @return Returns true if the course is deleted.
//     * @throws CourseException
//     */
//    public boolean deleteCourse(Course course) throws CourseException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                boolean returnValue = dbAccess.deleteCourse(course.getID());
//                if (returnValue) {
//                    MapperCreator.instance(Course.class).removeObject(
//                            course.getID());
//                }
//                return returnValue;
//            }
//            catch (IOException e) {
//                throw new CourseException(CourseException.EX_IO, e);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (CourseException) getException(e, e.code);
//                } else {
//                    throw new CourseException(CourseException.EX_XML_RPC, e);
//                }
//            }
//            catch (SQLException e) {
//                throw new CourseException(CourseException.EX_DB, e);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (CourseException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new CourseException(CourseException.EX_UNKNOWN_ERROR, e);
//        }
//    }

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

    private static final long DATE_OFFSET = 1000L * 3600L * 24L;

//    /**
//     * Select a course for a schoolclass.
//     *
//     * @param classID The class to select the course.
//     * @param courseID The course to select.
//     * @param tot
//     * @param van
//     * @param type
//     * @throws PersistenceException
//     */
//    @Deprecated //broken    ?
//    public void selectCoursesForClass(int classID, int courseID, int type, Date van, Date tot)
//            throws PersistenceException {
//        if (van == null) {
//            van = DATE_NULL;
//        }
//        if (tot == null) {
//            tot = DATE_NULL;
//        }
//        try {
//            DbAccessCreator.instance().selectCoursesForClass(classID, courseID, type, van, tot);
//        }
//        catch (IOException e) {
//            System.out.println(e.getMessage());
//            LOG.log(Level.SEVERE, null, e);
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (XmlRpcException e) {
//            System.out.println(e.getMessage());
//            LOG.log(Level.SEVERE, null, e);
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//        catch (SQLException e) {
//            System.out.println(e.getMessage());
//            LOG.log(Level.SEVERE, null, e);
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//
//    }

//    /**
//     * Deselect a course for a schoolclass.
//     *
//     * @param classID The class to deselect the course.
//     * @param courseID The course to deselect.
//     * @throws PersistenceException
//     */
//    @Deprecated //broken ?
//    public void deSelectCoursesForClass(int classID, int courseID)
//            throws PersistenceException {
//        try {
//            DbAccessCreator.instance().deSelectCoursesForClass(classID,
//                    courseID);
//        }
//        catch (IOException e) {
//            System.out.println(e.getMessage());
//            LOG.log(Level.SEVERE, null, e);
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (XmlRpcException e) {
//            System.out.println(e.getMessage());
//            LOG.log(Level.SEVERE, null, e);
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//        catch (SQLException e) {
//            System.out.println(e.getMessage());
//            LOG.log(Level.SEVERE, null, e);
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//
//    }

//    /**
//     * Returns all the courses which can be edited by the teacher.
//     *
//     * @param user
//     * @return The courses which can be edited by the specified teacher.
//     * @throws PersistenceException
//     */
//    public Course[] getEditableCourses(User user)
//            throws PersistenceException {
//        if (user instanceof Teacher) {
//            Teacher teacher = (Teacher) user;
//            try {
//                Vector v;
//                v = DbAccessCreator.instance().getEditableCourses(
//                        teacher.getSchool().getSchoolID());
//                if (user.hasRight(User.PROFILE_ADMIN_RIGHT) && !GuiConstants.GUI_ICONIZED) {
//                    Vector v2 = DbAccessCreator.instance().getEditableCoursesAdmin();
//                    v.addAll(v2);
//                }
//
//                MapperIF mapper = MapperCreator.instance(Course.class);
//                return (Course[]) mapper.getObjectFromReturn(v);
//            }
//            catch (IOException e) {
//                System.out.println(e.getMessage());
//                LOG.log(Level.SEVERE, null, e);
//                throw new PersistenceException(PersistenceException.EX_IO, e);
//            }
//            catch (XmlRpcException e) {
//                System.out.println(e.getMessage());
//                LOG.log(Level.SEVERE, null, e);
//                throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//            }
//            catch (SQLException e) {
//                System.out.println(e.getMessage());
//                LOG.log(Level.SEVERE, null, e);
//                throw new PersistenceException(PersistenceException.EX_DB, e);
//            }
//        } else if (user instanceof Admin) {
//            try {
//                Vector v;
//                v = DbAccessCreator.instance().getEditableCoursesAdmin();
//                MapperIF mapper = MapperCreator.instance(Course.class);
//                return (Course[]) mapper.getObjectFromReturn(v);
//            }
//            catch (IOException e) {
//                System.out.println(e.getMessage());
//                LOG.log(Level.SEVERE, null, e);
//                throw new PersistenceException(PersistenceException.EX_IO, e);
//            }
//            catch (XmlRpcException e) {
//                System.out.println(e.getMessage());
//                LOG.log(Level.SEVERE, null, e);
//                throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//            }
//            catch (SQLException e) {
//                System.out.println(e.getMessage());
//                LOG.log(Level.SEVERE, null, e);
//                throw new PersistenceException(PersistenceException.EX_DB, e);
//            }
//        } else {
//            return null;
//        }
//
//    }

    /**
     * Creates a new course for the specified school.
     *
     * @param school The school wherefore the course must created.
     * @param name The name of the course.
     * @param description The description of the course.
     * @param profileID
     * @param withChildren
     * @param parent
     * @return The new course. If an exception occurs, null is returned.
     * @throws CourseException
     */
//    public Course addCourse(School school, String name, String description, int profileID, Course parent, boolean withChildren)
//            throws CourseException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                int parentID = parent == null ? 0 : parent.getID();
//                int schoolID = parent == null ? school.getSchoolID() : parent.getSchoolID();
//                int result = dbAccess.addCourse(schoolID, name,
//                        description, profileID, parentID, withChildren);
//                Course c = new Course();
//                c.setCourseID(result);
//                c.setDescription(description);
//                c.setName(name);
//                c.setImageUrl(school.getImage());
//                c.setDwoProfile(profileID);
//                c.setSchoolID(schoolID); // DEZE IS VERGETEN, WIM 9/5/2011
//                c.setParentID(parentID);
//                c.resetParent();
//                if (withChildren) {
//                    c.setChildren(Course.NO_CHILDREN);
//                } else {
//                    c.setScoList(Course.NO_SCOS);
//                }
//                MapperIF map = MapperCreator.instance(Course.class);
//                map.put(result, c);
//                return c;
//            }
//            catch (IOException e) {
//                throw new CourseException(CourseException.EX_IO);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (CourseException) getException(e, e.code);
//                } else {
//                    throw new CourseException(CourseException.EX_XML_RPC);
//                }
//            }
//            catch (SQLException e) {
//                throw new CourseException(CourseException.EX_DB);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (CourseException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new CourseException(CourseException.EX_UNKNOWN_ERROR);
//        }
//    }

//    /**
//     * Updates the coursedata (name and description) in the database.
//     *
//     * @param course The course to update in the database.
//     * @return If true, the coursedata was successfully changed. Otherwise,
//     * false is returned.
//     * @throws fi.dwo.commons.exceptions.CourseException
//     */
//    public boolean updateCourse(Course course) throws CourseException {
//
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        if (course.getSchoolID() == 0) {
//            try {
//                dbAccess.log("course " + course.getID() + " " + course.getName() + " geen schoolID in updateCourse");
//            }
//            catch (Exception e) {
//            }
//        }
//        try {
//            try {
//                MapperCreator.instance(Course.class).put(course.getID(), course); // clear 1 level cache, keep 2nd level cache!!!!
//                if (course.parentChanged()) {
//                    boolean result
//                            = dbAccess.changeCourse(course.getID(), course.getName(),
//                                    course.getDescription(),
//                                    course.isExport(),
//                                    course.getSchoolID(),
//                                    course.getParentID());
//                    if (result) {
//                        course.resetParent();
//                    }
//                    return result;
//                }
//
//                return dbAccess.changeCourse(course.getID(), course.getName(),
//                        course.getDescription(), course.isExport() // FIXME fallback naar 2parameter methode.
//                        , course.getSchoolID() // TODO fallback!
//                );
//            }
//            catch (IOException e) {
//                throw new CourseException(CourseException.EX_IO);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (CourseException) getException(e, e.code);
//                } else {
//                    throw new CourseException(CourseException.EX_XML_RPC);
//                }
//            }
//            catch (SQLException e) {
//                LOG.log(Level.SEVERE, null, e);
//                throw new CourseException(CourseException.EX_DB);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (CourseException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new CourseException(CourseException.EX_UNKNOWN_ERROR);
//        }
//    }

    public Course[] getCourseFromMapper(Object o) throws IOException, SQLException, XmlRpcException {
        return MapperCreator.instance(Course.class).get(o);
    }


    /**
     * =============================================================================
     * DWOPROFILE FUNCTIONALITY
     * =============================================================================
     * @return 
     */
    private static int getDwoProfileID() {
        return DWO.getDwoProfileID();
    }

    /**
     * =============================================================================
     * REGISTRATION AND LOGIN FUNCTIONALITY
     * =============================================================================
     */
    ////peter
    /**
     * Register a new user in the DWO.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param firstname The firstname of the user.
     * @param middlename The middlename of the user. <br>
     * e.g: <code>Van</code>
     * @param lastname The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @return If the user was successfully registered true is returned.
     * Otherwise false is returned.
     * @throws fi.dwo.commons.exceptions.RegisterException
     *
     */
//    public boolean register(String username, String password, String firstname,
//            String middlename, String lastname, String email)
//            throws RegisterException {
//// password null: LDAP user
//        password = password == null ? "" : MD5.getHashString(password);
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            return dbAccess.register(username, password, firstname,
//                    middlename, lastname, email);
//        }
//        catch (IOException e) {
//            throw new RegisterException(RegisterException.EX_IO);
//        }
//        catch (XmlRpcException e) {
//            if (e.code != 0) {
//                throw new RegisterException(e.code, username);
//            } else {
//                throw new RegisterException(RegisterException.EX_XML_RPC);
//            }
//        }
//        catch (SQLException e) {
//            throw new RegisterException(RegisterException.EX_DB);
//        }
//        catch (DwoXmlRpcException e) {
//            throw new RegisterException(e.code, username);
//        }
//    }

//    /**
//     * Register a user in the system. Als links a user to a school.
//     *
//     * @param username The username of the user.
//     * @param password The password of the user.
//     * @param firstname The firstname of the user.
//     * @param middlename The middlename of the user. <br>
//     * e.g: <code>Van</code>
//     * @param lastname The lastname (familyname) of the user.
//     * @param email The e-mail address of the user.
//     * @param schoolLogin The schoolloginname of the school of the user.
//     * @param group The group from the user.
//     * @param groupPassword The password corresponding with the specified group
//     * and the school.
//     * @return If the user was successfully registered true is returned.
//     * Otherwise false is returned.
//     * @throws fi.dwo.commons.exceptions.RegisterException
//     *
//     */
//    public boolean register(String username, String password, String firstname,
//            String middlename, String lastname, String email,
//            String schoolLogin, Group group, String groupPassword)
//            throws RegisterException {
//        password = MD5.getHashString(password);
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            return dbAccess.register(username, password, firstname,
//                    middlename, lastname, email, schoolLogin, group
//                    .getGroupID(), groupPassword);
//        }
//        catch (IOException e) {
//            throw new RegisterException(RegisterException.EX_IO);
//        }
//        catch (XmlRpcException e) {
//            if (e.code != 0) {
//                throw new RegisterException(e.code, username);
//            } else {
//                throw new RegisterException(RegisterException.EX_XML_RPC);
//            }
//        }
//        catch (SQLException e) {
//            throw new RegisterException(RegisterException.EX_DB);
//        }
//        catch (DwoXmlRpcException e) {
//            throw new RegisterException(e.code, username);
//        }
//    }

//    /**
//     * Logs a user in into the system. The user-data will be checked in the
//     * database.
//     *
//     * @param username The username of the user.
//     * @param password The password of the user.
//     * @return The user who logged in. If an exception occurs, null is returned.
//     * @throws fi.dwo.commons.exceptions.LoginException
//     *
//     */
//    public User login(String username, String password) throws LoginException {
//        return login_intern(username, password, DbAccessCreator.instance());
//    }

//    /**
//     * @param username
//     * @param password
//     * @param dbAccess
//     * @return user/null?
//     * @throws LoginException
//     */
//    private User login_intern(String username, String password, DbAccessLogin dbAccess) throws LoginException {
//        try {
//            try {
//                MapperIF mapper = MapperCreator.instance(User.class);
//                Hashtable h = dbAccess.login(username, password);
//                return (User) mapper.getObjectFromReturn(h);
//            }
//            catch (IOException e) {
//                LOG.log(Level.SEVERE, null, e);
//                throw new LoginException(LoginException.EX_IO, e);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    //LOG.log(Level.SEVERE,null,e);
//                    throw (LoginException) getException(e, e.code);
//                } else {
//                    LOG.log(Level.SEVERE, null, e);
//                    throw new LoginException(LoginException.EX_XML_RPC, e);
//                }
//            }
//            catch (SQLException e) {
//                LOG.log(Level.SEVERE, null, e);
//                throw new LoginException(LoginException.EX_DB, e);
//            }
//            catch (DwoXmlRpcException e) {
//                LOG.log(Level.SEVERE, null, e);
//                throw (LoginException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            LOG.log(Level.SEVERE, null, e);
//            throw new LoginException(LoginException.EX_UNKNOWN_ERROR, e);
//        }
//    }

//    /**
//     * Changes the account of the user.
//     *
//     * @param user The user to change.
//     * @param password The new password.
//     * @param newPassword
//     * @param firstname The new firstname.
//     * @param middlename The new middlename. e.g: <code>Van</code>
//     * @param lastname The new lastname.
//     * @param email The new e-mail address of the user.
//     * @return boolean If the user was successfully changed it returns true,
//     * otherwise false is returned.
//     * @throws fi.dwo.commons.exceptions.RegisterException
//     *
//     */
//    public boolean changeAccount(User user, String password,
//            String newPassword, String firstname, String middlename,
//            String lastname, String email) throws RegisterException {
//        if (password != null) {
//            password = MD5.getHashString(password);
//        } else {
//            password = "";
//        }
//        newPassword = MD5.getHashString(newPassword);
//
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                return dbAccess.changeAccount(user.getUserID(), password,
//                        newPassword, firstname, middlename, lastname, email);
//            }
//            catch (IOException e) {
//                throw new RegisterException(RegisterException.EX_IO);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (RegisterException) getException(e, e.code);
//                } else {
//                    throw new RegisterException(RegisterException.EX_XML_RPC);
//                }
//            }
//            catch (SQLException e) {
//                throw new RegisterException(RegisterException.EX_DB);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (RegisterException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new RegisterException(RegisterException.EX_UNKNOWN_ERROR);
//        }
//    }

//    private static final DbAccessLogin LOGIN_SAML = new DbAccessLogin() {
//
//        @Override
//        public Hashtable login(String a, String b) throws IOException,
//                SQLException, XmlRpcException, DwoXmlRpcException {
//            return DbAccessCreator.instance().login_saml(a, b);
//        }
//
//    };

//    public User login(String username) throws LoginException {
//        return login_intern(username, "", DbAccessCreator.instance());
//    }

//    public User loginViaSAML(String samlUserID, String samlOrgID) throws LoginException {
//        return login_intern(samlUserID, samlOrgID, LOGIN_SAML);
//    }

//    public void linkViaSAML(User user, String userid, String orgid) {
//        try {
//            DbAccessCreator.instance().link_saml(userid, orgid, user.getID());
//        }
//        catch (Exception e) {
//            LOG.log(Level.SEVERE, null, e);
//        }
//
//    }

//    public String setRights(int userID, int schoolGroup, int profileID, String newRights) throws PersistenceException {
//        String result = null;
//        try {
//            result = DbAccessCreator.instance().setRights(userID, schoolGroup, profileID, newRights);
//        }
//        catch (IOException e) {
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (SQLException e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//        catch (XmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//
//        return result;
//    }

    /**
     * =============================================================================
     * SCHOOL FUNCTIONALITY
     * =============================================================================
     */
//    /**
//     * Connects a user to a school.
//     *
//     * @param user The user to connect.
//     * @param schoolLogin The schoolname.
//     * @param group The usergroup of the user.
//     * @param groupPassword The password of the usergroup.
//     * @return fi.dwo.client.domain.School The school of the user.
//     * @throws fi.dwo.commons.exceptions.RegisterException
//     *
//     */
//    public School addToSchool(User user, String schoolLogin, Group group,
//            String groupPassword) throws RegisterException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                MapperIF mapper = MapperCreator.instance(School.class);
//                Hashtable result = dbAccess.addToSchool(user.getUserID(),
//                        schoolLogin, group.getGroupID(), groupPassword);
//                return (School) mapper.getObjectFromReturn(result);
//            }
//            catch (IOException e) {
//                throw new RegisterException(RegisterException.EX_IO);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (RegisterException) getException(e, e.code);
//                } else {
//                    throw new RegisterException(RegisterException.EX_XML_RPC);
//                }
//            }
//            catch (SQLException e) {
//                throw new RegisterException(RegisterException.EX_DB);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (RegisterException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new RegisterException(RegisterException.EX_UNKNOWN_ERROR);
//        }
//    }

//    /**
//     * Creates a new school
//     *
//     * @throws fi.dwo.commons.exceptions.SchoolException
//     * @deprecated use {@link #addSchool(int, String, String, Hashtable)}
//     * @param id The id of the new school
//     * @param schoolName The name of the new school.
//     * @param schoolLogin The login name of the new school.
//     * @param studentPassw Password for students.
//     * @param teacherPassw Password for teachers.
//     * @return The new schoolclass. If an exception occurs, null is returned.
//     *
//     */
//    public School addSchool(int id, String schoolName, String schoolLogin, String studentPassw, String teacherPassw)
//            throws SchoolException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                MapperIF mapper = MapperCreator.instance(School.class);
//                Hashtable result = dbAccess.addSchool(id, schoolName, schoolLogin, studentPassw, teacherPassw);
//                return (School) mapper.getObjectFromReturn(result);
//            }
//            catch (IOException e) {
//                System.out.println(e.toString());
//                throw new SchoolException(SchoolException.EX_IO);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (SchoolException) getException(e, e.code);
//                } else {
//                    throw new SchoolException(SchoolException.EX_XML_RPC);
//                }
//            }
//            catch (SQLException e) {
//                System.out.println(e.toString());
//                throw new SchoolException(SchoolException.EX_DB);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (SchoolException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new SchoolException(SchoolException.EX_UNKNOWN_ERROR);
//        }
//    }

//    private School addSchool(int id, String schoolName, String schoolLogin, Hashtable passw)
//            throws SchoolException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                MapperIF mapper = MapperCreator.instance(School.class);
//                Hashtable result = dbAccess.addSchool(id, schoolName, schoolLogin, passw);
//                return (School) mapper.getObjectFromReturn(result);
//            }
//            catch (IOException e) {
//                System.out.println(e.toString());
//                throw new SchoolException(SchoolException.EX_IO);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (SchoolException) getException(e, e.code);
//                } else {
//                    throw new SchoolException(SchoolException.EX_XML_RPC);
//                }
//            }
//            catch (SQLException e) {
//                System.out.println(e.toString());
//                throw new SchoolException(SchoolException.EX_DB);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (SchoolException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new SchoolException(SchoolException.EX_UNKNOWN_ERROR);
//        }
//    }

//    public boolean deleteSchool(School sc) throws SchoolException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            boolean result = dbAccess.deleteSchool(sc.getSchoolID());
//            if (result) {
//                // remove caching van school,
//                // TODO classes, users, courses, scos
//                MapperCreator.instance(School.class).removeObject(sc.getSchoolID());
//            }
//            return result;
//        }
//        catch (IOException e) {
//            LOG.log(Level.SEVERE, null, e);
//            throw new SchoolException(SchoolException.EX_IO, e);
//        }
//        catch (SQLException e) {
//            LOG.log(Level.SEVERE, null, e);
//            throw new SchoolException(SchoolException.EX_DB, e);
//        }
//        catch (XmlRpcException e) {
//            LOG.log(Level.SEVERE, null, e);
//            if (e.code == 0) {
//                throw new SchoolException(SchoolException.EX_XML_RPC, e);
//            }
//            try {
//                throw (SchoolException) getException(e, e.code);
//            }
//            catch (PersistenceException e1) {
//                LOG.log(Level.SEVERE, null, e1);
//                throw new SchoolException(SchoolException.EX_UNKNOWN_ERROR, e1);
//            }
//        }
//    }

//    public Hashtable getFidentitySchools() {
//        DbAccessIF dbaccess = DbAccessCreator.instance();
//        try {
//            return dbaccess.getFidentitySchools();
//        }
//        catch (IOException e) {
//            LOG.log(Level.SEVERE, null, e);
//        }
//        catch (XmlRpcException e) {
//            if (e.code == SchoolException.SE_SCHOOL_UNSUPPORTED && e.getMessage().equals(SchoolException.class.getName())) {
//                return null;
//            }
//            LOG.log(Level.SEVERE, null, e);
//        }
//        catch (SQLException e) {
//            LOG.log(Level.SEVERE, null, e);
//        }
//        catch (DwoXmlRpcException e) {
//            return null;
//        }
//        return new Hashtable();
//    }

    /**
     *
     * @param school
     * @throws PersistenceException
     */
//    public void updateSchool(School school) throws PersistenceException {
//        int schoolID = school.getSchoolID();
//        boolean export = school.isExport();
//        try {
//            DbAccessCreator.instance().editSchool(schoolID, export);
//        }
//        catch (IOException e) {
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (XmlRpcException e) {
//            LOG.log(Level.SEVERE, null, e);
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//        catch (SQLException e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//
//    }

//    public School editSchool(int schoolID, String schoolName,
//            String schoolLogin, Hashtable passwd) throws SchoolException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                MapperIF mapper = MapperCreator.instance(School.class);
//                Hashtable result = dbAccess.editSchool(schoolID, schoolName, schoolLogin, passwd);
//                return (School) mapper.getObjectFromReturn(result);
//            }
//            catch (IOException e) {
//                System.out.println(e.toString());
//                throw new SchoolException(SchoolException.EX_IO, e);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (SchoolException) getException(e, e.code);
//                } else {
//                    throw new SchoolException(SchoolException.EX_XML_RPC, e);
//                }
//            }
//            catch (SQLException e) {
//                System.out.println(e.toString());
//                throw new SchoolException(SchoolException.EX_DB, e);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (SchoolException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new SchoolException(SchoolException.EX_UNKNOWN_ERROR, e);
//        }
//    }

//    /**
//     *
//     * This function should be removed for MANY TO SCHOOLS
//     *
//     * @param userWithRole
//     * @return
//     *
//     * @depreciated
//     */
//
////TODO MANY TO SCHOOLS    change signature to has role reference
//    public boolean deleteUserFromSchool(User userWithRole) {
//        if (userWithRole.getSchool() != null) {
//            try {
//                boolean result = DbAccessCreator.instance().deleteUserWithRoleFromSchool(userWithRole.getUserID(), userWithRole.getSchoolGroupID());
//                MapperCreator.instance(User.class).removeObject(userWithRole.getUserID());
//                return result;
//
//            }
//            catch (IOException e) {
//
//                LOG.log(Level.SEVERE, null, e);
//            }
//            catch (XmlRpcException e) {
//
//                LOG.log(Level.SEVERE, null, e);
//            }
//            catch (SQLException e) {
//
//                LOG.log(Level.SEVERE, null, e);
//            }
//        }
//        return false;
//    }

    @Deprecated
    static int idOf(PersistenceId id) {
		String s = id.getIdString();
		int dot = s.lastIndexOf(';');		
		return Integer.parseInt(s.substring(dot+1));
	}

//	public boolean updateSchoolTo(School from, School[] to) {
//        int schoolID = from.getSchoolID();
//        Vector schoolTo = new Vector(to.length);
//        for (int i = 0; i < to.length; i++) {
//            schoolTo.add(new Integer(to[i].getSchoolID()));
//        }
//        try {
//            return DbAccessCreator.instance().updateSchoolTo(schoolID, schoolTo);
//        }
//        catch (IOException e) {
//            // TODO Auto-generated catch block
//            LOG.log(Level.SEVERE, null, e);
//        }
//        catch (XmlRpcException e) {
//            // TODO Auto-generated catch block
//            LOG.log(Level.SEVERE, null, e);
//        }
//        catch (SQLException e) {
//            // TODO Auto-generated catch block
//            LOG.log(Level.SEVERE, null, e);
//        }
//        return false;
//    }

//    /**
//     * Edit an old school
//     *
//     * @param schoolID
//     * @throws fi.dwo.commons.exceptions.SchoolException
//     * @deprecated use {@link #editSchool(int, String, String, Hashtable)}
//     * @param schoolName The name of the new school.
//     * @param schoolLogin The login name of the new school.
//     * @param studentPassw Password for students.
//     * @param teacherPassw Password for teachers.
//     * @return The new schoolclass. If an exception occurs, null is returned.
//     *
//     */
//    public School editSchool(int schoolID, String schoolName, String schoolLogin, String studentPassw, String teacherPassw)
//            throws SchoolException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                MapperIF mapper = MapperCreator.instance(School.class);
//                Hashtable result = dbAccess.editSchool(schoolID, schoolName, schoolLogin, studentPassw, teacherPassw);
//                return (School) mapper.getObjectFromReturn(result);
//            }
//            catch (IOException e) {
//                System.out.println(e.toString());
//                throw new SchoolException(SchoolException.EX_IO);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (SchoolException) getException(e, e.code);
//                } else {
//                    throw new SchoolException(SchoolException.EX_XML_RPC);
//                }
//            }
//            catch (SQLException e) {
//                System.out.println(e.toString());
//                throw new SchoolException(SchoolException.EX_DB);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (SchoolException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new SchoolException(SchoolException.EX_UNKNOWN_ERROR);
//        }
//    }

//    public void editSchool(School school, String string) throws PersistenceException {
//        try {
//            if (DbAccessCreator.instance().editSchoolRights(school.getSchoolID(), string)) {
//                school.setRights(string);
//            }
//        }
//        catch (IOException e) {
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (SQLException e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//        catch (XmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//
//    }

//    public School editSchool(int schoolID, String schoolName,
//            String schoolLogin, SchoolPasswdMap schoolPasswdMap, Date date) throws SchoolException {
//
//        School school = editSchool(schoolID, schoolName, schoolLogin, schoolPasswdMap);
//        setExpireDate(schoolID, date, school);
//        return school;
//    }

//    public School addSchool(int id, String schoolName, String schoolLogin,
//            SchoolPasswdMap schoolPasswdMap, Date date) throws SchoolException {
//        School school = addSchool(id, schoolName, schoolLogin, schoolPasswdMap);
//        if (date != null) {
//            setExpireDate(school.getSchoolID(), date, school);
//        }
//        return school;
//    }

//    private void setExpireDate(int schoolID, Date date, School school) {
//        try {
//            Date date0 = date;
//            if (date == null) {
//                date0 = DATE_NULL;
//            }
//// safe the date in UTC:
//            date0 = new Date(Date.UTC(date0.getYear(), date0.getMonth(), date.getDate(), 0, 0, 0));
//            if (DbAccessCreator.instance().setExpireDate(schoolID, date0)) {
//                school.setExpire(date);
//            }
//        }
//        catch (IOException e) {
//            // TODO Auto-generated catch block
//            LOG.log(Level.SEVERE, null, e);
//        }
//        catch (XmlRpcException e) {
//            System.err.println(e); // no such method?
//        }
//        catch (SQLException e) {
//            // TODO Auto-generated catch block
//            LOG.log(Level.SEVERE, null, e);
//        }
//    }

//
//    /**
//     * Changes the account of the user.
//     *
//     * @param user The user to change.
//     * @param password The new password.
//     * @param newPassword
//     * @param firstname The new firstname.
//     * @param middlename The new middlename. e.g: <code>Van</code>
//     * @param lastname The new lastname.
//     * @param email The new e-mail address of the user.
//     * @param c The new schoolclass of the user.
//     * @return boolean If the user was successfully changed it returns true,
//     * otherwise false is returned.
//     * @throws fi.dwo.commons.exceptions.RegisterException
//     *
//     */
//    public boolean changeAccount(User user, String password,
//            String newPassword, String firstname, String middlename,
//            String lastname, String email, SchoolClass c)
//            throws RegisterException {
//// bypass password check
//        if (password == null) {
//            password = "";
//        } else {
//            password = MD5.getHashString(password);
//        }
//        if (newPassword == null) {
//            newPassword = "";
//        } else {
//            newPassword = MD5.getHashString(newPassword);
//        }
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                int classid = c == null ? 0 : c.getID();
//                return dbAccess.changeAccount(user.getUserID(), password,
//                        newPassword, firstname, middlename, lastname, email, classid);
//            } catch (IOException e) {
//                LOG.log(Level.SEVERE, null, e);
//                throw new RegisterException(RegisterException.EX_IO);
//            } catch (XmlRpcException e) {
//                LOG.log(Level.SEVERE, null, e);
//                if (e.code != 0) {
//                    throw (RegisterException) getException(e, e.code);
//                } else {
//                    throw new RegisterException(RegisterException.EX_XML_RPC);
//                }
//            } catch (SQLException e) {
//                LOG.log(Level.SEVERE, null, e);
//                throw new RegisterException(RegisterException.EX_DB);
//            } catch (DwoXmlRpcException e) {
//                LOG.log(Level.SEVERE, null, e);
//                throw (RegisterException) getException(e, e.code);
//            }
//        } catch (PersistenceException e) {
//            LOG.log(Level.SEVERE, null, e);
//            throw new RegisterException(RegisterException.EX_UNKNOWN_ERROR);
//        }
//    }
    /**
     * =============================================================================
     * SCHOOLCLASS FUNCTIONALITY
     * =============================================================================
     */
//    /**
//     * Creates a new schoolclass for the specified teacher
//     *
//     * @param teacher The teacher of the new schoolclass.
//     * @param className The name of the new schoolclass.
//     * @return The new schoolclass. If an exception occurs, null is returned.
//     * @throws fi.dwo.commons.exceptions.ClassException
//     *
//     */
//    public SchoolClass addClass(Teacher teacher, String className)
//            throws ClassException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                MapperIF mapper = MapperCreator.instance(SchoolClass.class);
//                Hashtable result = dbAccess.addClass(teacher.getUserID(),
//                        className);
//                return (SchoolClass) mapper.getObjectFromReturn(result);
//            }
//            catch (IOException e) {
//                throw new ClassException(ClassException.EX_IO);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (ClassException) getException(e, e.code);
//                } else {
//                    throw new ClassException(ClassException.EX_XML_RPC);
//                }
//            }
//            catch (SQLException e) {
//                throw new ClassException(ClassException.EX_DB);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (ClassException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new ClassException(ClassException.EX_UNKNOWN_ERROR);
//        }
//    }

//    /**
//     * Deletes the specified schoolclass. if mustEmpty is true, and the class
//     * contains students, this function returns false. Otherwise it returns true
//     *
//     * @param c The class to delete
//     * @param mustEmpty if true and the schoolclass is not empty, the
//     * schoolclass is not deleted.
//     * @return If mustEmpty is true, and the class contains students, this
//     * function returns false. Otherwise it returns true
//     * @throws fi.dwo.commons.exceptions.ClassException
//     *
//     */
//    public boolean deleteClass(SchoolClass c, boolean mustEmpty)
//            throws ClassException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            boolean returnvalue = dbAccess.deleteClass(c.getID(), mustEmpty);
//            if (returnvalue) {
//                MapperCreator.instance(SchoolClass.class).removeObject(
//                        c.getID());
//            }
//            return returnvalue;
//        }
//        catch (IOException e) {
//            throw new ClassException(ClassException.EX_IO);
//        }
//        catch (XmlRpcException e) {
//            throw new ClassException(ClassException.EX_XML_RPC);
//        }
//        catch (SQLException e) {
//            throw new ClassException(ClassException.EX_DB);
//        }
//    }

//    /**
//     * Renames the name of the schoolclass in the database.
//     *
//     * @param schoolClass The class to rename.
//     * @param newName The new name of the class.
//     * @param iconizer
//     * @throws ClassException
//     */
//    public void renameClass(SchoolClass schoolClass, String newName, boolean iconizer)
//            throws ClassException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                dbAccess.renameClass(schoolClass.getID(), newName, iconizer);
//            }
//            catch (IOException e) {
//                throw new ClassException(ClassException.EX_IO);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (ClassException) getException(e, e.code);
//                } else {
//                    throw new ClassException(ClassException.EX_XML_RPC);
//                }
//            }
//            catch (SQLException e) {
//                throw new ClassException(ClassException.EX_DB);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (ClassException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new ClassException(ClassException.EX_UNKNOWN_ERROR);
//        }
//    }

//    /**
//     * Renames the name of the schoolclass in the database.
//     *
//     * @param schoolClass The class to rename.
//     * @param newName The new name of the class.
//     * @param newRegistrationKey
//     * @param iconizer
//     * @throws ClassException
//     */
//    public void renameClass(SchoolClass schoolClass, String newName, String newRegistrationKey, boolean iconizer)
//            throws ClassException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                dbAccess.renameClass(schoolClass.getID(), newName, newRegistrationKey, iconizer);
//            }
//            catch (IOException e) {
//                throw new ClassException(ClassException.EX_IO);
//            }
//            catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (ClassException) getException(e, e.code);
//                } else {
//                    throw new ClassException(ClassException.EX_XML_RPC);
//                }
//            }
//            catch (SQLException e) {
//                throw new ClassException(ClassException.EX_DB);
//            }
//            catch (DwoXmlRpcException e) {
//                throw (ClassException) getException(e, e.code);
//            }
//        }
//        catch (PersistenceException e) {
//            throw new ClassException(ClassException.EX_UNKNOWN_ERROR);
//        }
//    }

    /**
     * =============================================================================
     * RESULTS FUNCTIONALITY
     * =============================================================================
     */
    /**
     * Returns all the results for the specified courses and the classes of the
     * specified current teacher.
     *
     * @param courses The courses wherefrom the result must be returned.
     * @param teacher The teacher wherefrom the results of the school classes
     * must be returned.
     * @return The results for the specified courses and the classes of the
     * specified teacher.
     * @throws PersistenceException
     */
//    public Vector getResults(Course[] courses, Teacher teacher)
//            throws PersistenceException {
//        Vector courseIDs = new Vector();
//
//        for (int i = 0; i < courses.length; i++) {
//            courseIDs.addElement(new Integer(courses[i].getID()));
//        }
//        try {
//            Vector v = DbAccessCreator.instance().getResults(courseIDs,
//                    teacher.getUserID());
//            MapperIF mapper = MapperCreator.instance(UserResultList.class);
//            Object[] oa = mapper.getObjectFromReturn(v);
//            if (oa.length > 0) {
//                return (Vector) (oa[0]);
//            } else {
//                return new Vector();
//            }
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

    /**
     * Returns the results for the specified courses and the students in the
     * specified schoolclass.
     *
     * @param courses The courses wherefrom the result must be returned.
     * @param schoolClass The schoolclass wherefrom the student-results must be
     * returned.
     * @param teacher The teacher of the schoolclass.
     * @return The result for the students in the specified class and for the
     * specified courses.
     * @throws PersistenceException
     */
//    public Vector getResults(Course[] courses, SchoolClass schoolClass,
//            Teacher teacher) throws PersistenceException {
//        Vector courseIDs = new Vector();
//
//        for (int i = 0; i < courses.length; i++) {
//            courseIDs.addElement(new Integer(courses[i].getID()));
//        }
//        try {
//            Vector v = DbAccessCreator.instance().getResults(courseIDs,
//                    schoolClass.getID(), teacher.getUserID());
//            MapperIF mapper = MapperCreator.instance(UserResultList.class);
//            return (Vector) (mapper.getObjectFromReturn(v)[0]);
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

    /**
     * Returns the result for the SCO's in the specified course, for the
     * students in the specified SchoolClass.
     *
     * @param course The course wherefrom the SCO's results must returned.
     * @param schoolClass The schoolclass wherefrom the student-results must be
     * returned.
     * @param teacher The teacher of the schoolclass.
     * @return The result for the students in the specified class and for the
     * specified course.
     * @throws PersistenceException
     */
//    public Vector getResults(Course course, SchoolClass schoolClass,
//            Teacher teacher) throws PersistenceException {
//        try {
//            //TODO NOW add schoolgroupid to query
//            Vector v = DbAccessCreator.instance().getResults(course.getID(),
//                    schoolClass.getID(), teacher.getUserID());
//            MapperIF mapper = MapperCreator.instance(UserResultList.class);
//            return (Vector) (mapper.getObjectFromReturn(v)[0]);
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
              Sco sco = get( idOf(pid), Sco.class);
              r.setLessonGroup(sco);
              scores[sco.getSequencenr()-1] = r;
            }
            list.setResultScore(scores);
            list.setResultsModule(resultsModule);
            Vector<UserResultList> vector = new Vector<>();
            vector.add(list);
            return vector;
          }
  // KOMT DIT VOOR??????        
          
          
//          Vector v = DbAccessCreator.instance().getUserResults(course.getID(),
//                    user.getUserID(), idOf(user.getSchoolGroupID()));
//            MapperIF mapper = MapperCreator.instance(UserResultList.class);
//            return (Vector<UserResultList>) (mapper.getObjectFromReturn(v)[0]);
        }
//        catch (IOException e) {
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (XmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//        catch (SQLException e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        } 
        catch (Dwo2Exception e) {
          throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }
    }

    public long toTimeInMillis(String totalTime) {
      try {
          return TOTAL_TIME_FORMAT.parse(totalTime).getTime();
      } catch (Exception e) {
          LOG.log(Level.WARNING,"toTimeInMillis " + totalTime,e);
      }
      return 0;
    }

    /**
     * Returns all the results of the SCO's of the course, for the classes from
     * the specified teacher.
     *
     * @param course The course wherefrom the SCO's results must returned.
     * @param teacher The teacher wherefrom the results of the schoolclasses
     * must be returned.
     * @return The results for the SCO's of the classes from the teacher.
     * @throws PersistenceException
     */
//    public Vector getResults(Course course, Teacher teacher)
//            throws PersistenceException {
//        try {
//            Vector v = DbAccessCreator.instance().getResults(course.getID(),
//                    teacher.getUserID());
//            MapperIF mapper = MapperCreator.instance(UserResultList.class);
//            return (Vector) (mapper.getObjectFromReturn(v)[0]);
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

//    public Vector getUserResults(Course[] courses) throws PersistenceException {
//        User user = DwoHelper.getCurrentFacadeUser();
//        Vector courseIDs = new Vector();
//
//        for (int i = 0; i < courses.length; i++) {
//            courseIDs.addElement(new Integer(courses[i].getID()));
//        }
//        try {
//            Vector v = DbAccessCreator.instance().getUserResults(courseIDs,
//                    user.getUserID(),idOf(user.getSchoolGroupID()));
//            MapperIF mapper = MapperCreator.instance(UserResultList.class);
//            Object[] oa = mapper.getObjectFromReturn(v);
//            if (oa.length > 0) {
//                return (Vector) (oa[0]);
//            } else {
//                return new Vector();
//            }
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

//
//    /**
//     * Disconnects a user from a class.
//     *
//     * @param user The user to disconnect.
//     * @throws fi.dwo.commons.exceptions.RegisterException
//     *
//     */
//    public void disconnectFromClass(User user) throws RegisterException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            dbAccess.re(user.getUserID());
//        } catch (IOException e) {
//            throw new RegisterException(RegisterException.EX_IO);
//        } catch (XmlRpcException e) {
//            throw new RegisterException(RegisterException.EX_XML_RPC);
//        } catch (SQLException e) {
//            throw new RegisterException(RegisterException.EX_DB);
//        }
//
//    }
//    /**
//     * Reassigns a teacher to a new class
//     *
//     * @param schoolClass
//     * @param teacher
//     * @return
//     * @throws ClassException
//     */
//    public boolean reassignClass(SchoolClass schoolClass, Teacher teacher) throws ClassException {
//        DbAccessIF dbAccess = DbAccessCreator.instance();
//        try {
//            try {
//                boolean result = dbAccess.addTeacherToClass(schoolClass.getID(), teacher.getID());
//                LOG.log(Level.FINER, "Added Teacher with ID {1} to SchoolClass with ID {0} to with result {2}", new Object[]{schoolClass.getID(), teacher.getID(), result});
//                boolean result2 = dbAccess.removeTeacherFromClass(schoolClass.getID(), teacher.getID());
//                LOG.log(Level.FINER, "Removed Teacher with ID {1} from SchoolClass with ID {0} to with result {2}.", new Object[]{schoolClass.getID(), teacher.getID(), result2});
//                result = result && result2;
//                LOG.log(Level.FINE, "Reassigned Teacher with ID {1} to SchoolClass with ID {0} to with result {2}", new Object[]{schoolClass.getID(), teacher.getID(), result});
//                return result;
//            } catch (IOException e) {
//                throw new ClassException(ClassException.EX_IO);
//            } catch (XmlRpcException e) {
//                if (e.code != 0) {
//                    throw (ClassException) getException(e, e.code);
//                } else {
//                    throw new ClassException(ClassException.EX_XML_RPC);
//                }
//            } catch (SQLException e) {
//                throw new ClassException(ClassException.EX_DB);
//            } catch (DwoXmlRpcException e) {
//                throw (ClassException) getException(e, e.code);
//            }
//        } catch (PersistenceException e) {
//            throw new ClassException(ClassException.EX_UNKNOWN_ERROR);
//        }
//    }
    /**
     * =============================================================================
     * CLASSCOURSE FUNCTIONALITY
     * =============================================================================
     * @return 
     */
    public boolean deleteCourseClassData(Course course, SchoolClass sc) {
//        int courseID = course.getID();
//        int classID = sc.getID();
//        try {
//            return DbAccessCreator.instance().deleteCourseDataFromClass(courseID, classID);
//        }
//        catch (Exception e) {
//            LOG.log(Level.SEVERE, null, e);
//            return false;
//        }
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

//    public void selectCoursesForClass(SchoolClass schoolClass, ClassCourse[] classCourses)
//            throws PersistenceException {
//        Vector v = new Vector(classCourses.length);
//        for (int i = 0; i < classCourses.length; i++) {
//            ClassCourse deze = classCourses[i];
//            Hashtable h = new Hashtable();
//            h.put("courseID", new Integer(deze.getCourseID()));
//            if (deze.getType() != 0) {
//                h.put("type", new Integer(deze.getType()));
//            }
//            if (deze.getNotAfter() != null && deze.getNotAfter().getTime() > DATE_OFFSET) {
//                h.put("notAfter", deze.getNotAfter());
//            }
//            if (deze.getNotBefore() != null && deze.getNotBefore().getTime() > DATE_OFFSET) {
//                h.put("notBefore", deze.getNotBefore());
//            }
//            h.put("viewState", new Integer(deze.getViewState()));
//            String accessKey = deze.getAccessKey();
//			if(accessKey != null) 
//				h.put("accessKey", accessKey);
//            v.add(h);
//        }
//// insert dwoProfileID.
//        if (v.isEmpty()) {
//            v.add(new Hashtable());
//        }
//        int id = getDwoProfileID(); // TODO denk aan parameter?
//        ((Hashtable) v.firstElement()).put("dwoProfileID", new Integer(id));
//
//        try {
//            DbAccessCreator.instance().selectCoursesForClass(schoolClass.getID(), v);
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
//
//    }

//    /**
//     * =============================================================================
//     * COURSESEQUENCE FUNCTIONALITY
//     * =============================================================================
//     * @return null
//     * @deprecated
//     */
//    public CourseSequence[] getCourseSequence(User currentUser) {
//        School school = currentUser.getSchool();
//        MapperIF instance = MapperCreator.instance(CourseSequence.class);
//        try {
//            CourseSequence[] standaard = (CourseSequence[]) instance.get(null);
//            if (school != null) {
//                return (CourseSequence[]) combine_(standaard, instance.get(school));
//            }
//            return standaard;
//        }
//        catch (Exception e) {
//            // TODO Auto-generated catch block
//            LOG.log(Level.SEVERE, null, e);
//            return new CourseSequence[0];
//        }
//    }

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
//        int schoolID = 0;
        //int classID = 0;
        //DbAccessIF access = DbAccessCreator.instance();

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
        //int profileID = getDwoProfileID();
//        if (school != null) {
//            schoolID = school.getSchoolID();
//        }
//        MapperIF instance = MapperCreator.instance(CourseSequence.class);
//        instance.removeAllObjects();
//        try {
//            access.setCourseSequence(vector, schoolID, classID, parent, profileID);
//        }
//        catch (Exception e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }

    }

//    @Deprecated
//    public Course[] sequence(Course[] courses) {
////        if (!DWO.SEQUENCE || true) { // NO SORTING HERE 
//            return courses;
////        }
////        return sequence(courses, DwoHelper.getCurrentFacadeUser());
//    }

//    @Deprecated
//    private Course[] sequence(Course[] courses, User currentUser) {
//        CourseSequence[] css = getCourseSequence(currentUser);
//        return sequence(courses, css);
//    }
//
//    @Deprecated
//    private Course[] sequence(Course[] courses, CourseSequence[] css) {
//        int start = 0;
//        if (css != null) {
//            for (int i = 0; i < css.length; i++) {
//                int c = css[i].getCourseID();
//                for (int j = start; j < courses.length; j++) {
//                    if (courses[j].getID() == c) {
//                        Course tmp = courses[start];
//                        courses[start] = courses[j];
//                        courses[j] = tmp;
//                        start++;
//                        break;
//                    }
//                }
//            }
//        }
//        return courses;
//    }

//    /**
//     * =============================================================================
//     * FROMTO FUNCTIONALITY
//     * =============================================================================
//     * @return 
//     */
//    public Vector getToSchoolsFrom(int schoolID)
//            throws IOException, XmlRpcException, SQLException, PersistenceException {
//        try {
//            Vector v = DbAccessCreator.instance().getToSchoolsFrom(schoolID);
//            return v;
//        }
//        catch (IOException e) {
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (SQLException e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//        catch (XmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//
//    }

//    /**
//     * =============================================================================
//     * BLA FUNCTIONALITY
//     * =============================================================================
//     * @return 
//     */
//    public boolean updateLogo(Course c) {
//
//        try {
//            if (c.getImageData() != null) {
//                DbAccessCreator.instance().setLogo(c.getID(), c.getImageData());
//            } else if (c.getImageUrl() != null && c.getImageUrl().length() > 0) {
//// TODO er is geen update by reference.
//                String imu = GuiConstants.RESOURCES + c.getImageUrl();
//                URL u = new URL(DwoHelper.getResourceUrlPath(),imu);
//                URLConnection connection = u.openConnection();
//                InputStream in = connection.getInputStream();
//// Check image contentType
//                if (connection.getContentType().startsWith("image/")) {
//                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                    int len;
//                    byte[] data = new byte[1024];
//                    do {
//                        len = in.read(data);
//                        if (len > 0) {
//                            bos.write(data, 0, len);
//                        }
//                    } while (len > 0);
//                    bos.close();
//                    DbAccessCreator.instance().setLogo(c.getID(), bos.toByteArray());
//                }
//                in.close();
//            }
//            return true;
//        }
//        catch (SQLException e) {
//            LOG.log(Level.SEVERE, null, e);
//        }
//        catch (IOException e) {
//            LOG.log(Level.SEVERE, null, e);
//        }
//        catch (XmlRpcException e) {
//            LOG.log(Level.SEVERE, null, e);
//        }
//        return false;
//    }

//    public void setLogo(int id, byte[] data) throws PersistenceException {
//        try {
//            DbAccessCreator.instance().setLogo(id, data);
//        }
//        catch (IOException e) {
//            throw new PersistenceException(PersistenceException.EX_IO, e);
//        }
//        catch (SQLException e) {
//            throw new PersistenceException(PersistenceException.EX_DB, e);
//        }
//        catch (XmlRpcException e) {
//            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
//        }
//    }

    /**
     * Clears user data from mapper cache
     *
     * @param id userID whose data needs to be cleared.
     */
    public void clearCurrentUserDataCache(int id) {
        MapperCreator.instance(User.class).removeObject(id);
    }

    /**
     * Clears data from mapper cache for a certain class
     *
     * @param c Class
     */
    public void clearCurrentMapperDataCache(Class c) {
        MapperCreator.instance(c).removeAllObjects();
    }

    /**
     * Clears data from mapper cache for a certain class
     *
     * @param c Class
     * @param id
     */
    public void clearObjectInMapperCache(Class<?> c, int id) {
        MapperCreator.instance(c).removeObject(id);
    }

    /**
     * vul de children van de courses. Daarmee wordt een 'loadchildren' hopelijk
     * niet aangeroepen.
     *
     * @param courses
     */
    private void undoCachingEffect(Course[] courses) {
        MapperIF<Course> mapper = MapperCreator.instance(Course.class);
//        sequence(courses);
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
                        parent = mapper.get(c.getParentID()); // deze komt toch uit de cache?
                    }
                    catch (Exception e) {
                        continue;
                    }
                }
                parent.addChild(c);	// als dit werkt, zou dat mooi zijn!
            }
        }
    }

//    private Vector clipBeforeAfter(Vector v) {
//        Iterator iter = v.iterator();
//        long now = System.currentTimeMillis() + DwoHelper.getCurrentFacadeUser().getTimeZone();
//        while (iter.hasNext()) {
//            Hashtable ht = (Hashtable) iter.next();
//            Object o = ht.get("notBefore");
//            if (o instanceof Date) {
//                if (now < ((Date) o).getTime()) {
//                    iter.remove();
//                    continue;
//                }
//            }
//            o = ht.get("notAfter");
//            if (o instanceof Date) {
//                if (now > ((Date) o).getTime()) {
//                    iter.remove();
//                    continue;
//                }
//            }
////            o = ht.get("type");
////            if (Integer.valueOf(2).equals(o)) { // INVISIBLE
////            	iter.remove();
////            	continue;
////            }
//        }
//        return v;
//    }

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
      MapperIF m = MapperCreator.instance(UserResultList.class);
      if (m instanceof UserResultListMapper) {
          ((UserResultListMapper) m).setResultsModule(resultsModule);
      }
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

}
