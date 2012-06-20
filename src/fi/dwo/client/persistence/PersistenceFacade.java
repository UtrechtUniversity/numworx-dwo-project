// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\PersistenceFacade.java

package fi.dwo.client.persistence;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.beans.base64code.StringCodeObject;
import fi.dwo.client.domain.AppletConfig;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.CourseSequence;
import fi.dwo.client.domain.DWO;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.DwoProfile;
import fi.dwo.client.domain.Group;
import fi.dwo.client.domain.Guest;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.domain.Admin;
import fi.dwo.client.domain.User;
import fi.dwo.client.domain.UserResultList;
import fi.dwo.client.gui.GuiConstants;
import fi.dwo.client.persistence.cache.StoreCreator;
import fi.dwo.client.system.ClassException;
import fi.dwo.client.system.SchoolException;
import fi.dwo.client.system.CourseException;
import fi.dwo.client.system.LoginException;
import fi.dwo.client.system.MD5;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.RegisterException;
import fi.dwo.client.system.ScoException;
import fi.dwo.server.persistence.DwoXmlRpcException;

/**
 * This class is the Facade between the Domain layer and the Persistence layer.
 * It functions as a singleton. <br>
 * An instance of the PersistanceFacade could be archived by calling the method <code>instance()</code>.
 * @author M.J.B. Kupers
 *
 */
public class PersistenceFacade {
    private static final Sco[] EMPTY_SCOS = new Sco[0];

	private static PersistenceFacade _instance;

    private static final String[][] scormDatabaseLink = {
            { "cmi.core.score.raw", "score" },
            { "cmi.suspend_data", "suspendData" },
            { "core.score.raw", "score" }, 
            { "suspend_data", "suspendData" },
            { "cmi.core.session_time", "session_time" }, 
            { "cmi.core.total_time", "total_time" },
            { "core.session_time", "session_time" }, 
            { "core.total_time", "total_time" }
    };

	public static final int PROFILEOFFSET = -1234;

    /**
     *  
     */
    public PersistenceFacade() {

    }

    /**
     * Returns an instance of PersistenceFacade.
     * @return fi.dwo.client.persistence.PersistenceFacade
     *  
     */
    public static PersistenceFacade instance() {
        if (_instance == null) {
            _instance = new PersistenceFacade();
        }
        return _instance;
    }

    /**
     * Returns a object of the specified class, with the specified objectID.<br>
     * e.g. if the class is fi.dwo.client.domain.Course and the objectID is 1,
     * a Course object representing cours nr 1 (the ID field in the database) is returned.
     * @param oid The ID of the object to get.
     * @param c The class, indicating the type of Object to get.
     * @return Object The object representing the specified objectID and class.
     *  
     */
    public Object get(int oid, java.lang.Class c) throws PersistenceException {
        MapperIF mapper = MapperCreator.instance(c);

        try {
            return mapper.get(oid);
        } catch (IOException e) {
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_IO);
        } catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC);
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB);
        }
    }

    /**
     * This method saves an object in the database.<br>
     * NOT IMPLEMENTED!!
     * @param oid
     * @param c
     * @param obj
     *  
     */
    public void put(int oid, Object obj)
            throws PersistenceException {
        MapperIF mapper = MapperCreator.instance(obj.getClass());
        try {
            mapper.put(oid, obj);
        } catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO);
        } catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC);
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB);
        }

    }

    /**
     * Function for problems with lazy-connection.
     * It closes the connection.
     *  
     */
    public void reConnect() throws PersistenceException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            dbAccess.reconnect();
        } catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO);
        } catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC);
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB);
        }

    }

    private String mapDataModel(String element)
    {
    	for(int i = 0; i < scormDatabaseLink.length; i++)
    	{
    		if (scormDatabaseLink[i][0].equals(element))
    			return scormDatabaseLink[i][1];
    	}
    	return element;
    }
    
    /**
     * Gets a value saved for a SCO and a user.
     * @param sco The SCO wherefrom the value must be returned.
     * @param user The User wherefrom the value must be returned.
     * @param iDataModelElement The value to get.
     * @return The value for the iDataModelElement.
     * @throws PersistenceException If a database exception, or XML-RPC exception occurres.
     */
    public String LMSGetValue(Sco sco, User user, String iDataModelElement)
            throws PersistenceException {
        if (user != null && !(user instanceof Guest)) {
        	int uid = user.getUserID();
        	int scoid = sco.getScoID();
        	String key = mapDataModel(iDataModelElement);
        	return StoreCreator.instance().getValue(uid, scoid, key);
        } else {
            return "";
        }
    }

    boolean noRandom;
    /**
     * Saves a value for a SCO and a user.
     * @param sco The SCO of the value.
     * @param user The User of the value.
     * @param iDataModelElement Indicates which item must be saved.
     * @param iValue The value to save.
     * @return "true" or "false"
     * @throws PersistenceException
     */
    public String LMSSetValue(Sco sco, User user, String iDataModelElement,
            String iValue) throws PersistenceException {
        if (user != null && !(user instanceof Guest) ) {
            if (iDataModelElement.equals("cmi.core.score.raw")) {
			    double d;
			    try {
			        d = Double.valueOf(iValue).doubleValue();
			    } catch (NumberFormatException ex) {
			        d = 0;
			    }
			    if (Double.isNaN(d)) {
			        d = 0;
			    }
			    iValue = Double.toString(d);
			}
			String result = true + "";
      
			if (iValue == null) {
			    iValue = "";
			}
			
			int uid = user.getUserID();
			int scoid = sco.getScoID();
			String key = mapDataModel(iDataModelElement);
			result = StoreCreator.instance().setValue(uid, scoid, key, iValue);
			return result;
        } else {
            return true + "";
        }

    }

    
    public String LMSCommit(Sco sco, User user, String dummy)
    {
    	try {
			return StoreCreator.instance().commit(user.getUserID(), sco.getScoID(), dummy);
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "false";
    }
    
    /**
     * Returns all the objects of the specified class.<br>
     * e.g. if the class is fi.dwo.client.domain.Course,
     * all the Course objects representing the courses in the database are returned.
     * @param c The class, indicating the type of Object to get.
     * @return The objects representing the specified class.
     *  
     */
    public Object[] get(java.lang.Class c) throws PersistenceException {
        MapperIF mapper = MapperCreator.instance(c);
        try {
            return mapper.get();
        } catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO);
        } catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC);
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB);
        }
    }

    /**
     * Returns all the objects of the specified class with the restriction of the specified object.<br>
     * The meaning of this restriction is defined in the corresponding mapper-class (e.g. CourseMapper).
     * e.g. if the class is fi.dwo.client.domain.Course,
     * all the Course objects representing the courses in the database with the specified restriction are returned.
     * @param c The class, indicating the type of Object to get.
     * @param obj An object that specifies the restriction.
     * 
     * @return Object[]
     *  
     */
    public Object[] get(java.lang.Class c, Object obj)
            throws PersistenceException {
        MapperIF mapper = MapperCreator.instance(c);
        try {
            return mapper.get(obj);
        } catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO, e);
        } catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC,e);
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB, e);
        }
    }


    /**
     * Returns all the courses for the specified user.
     * A teacher could select some courses for a schoolclass.
     * @param user The user to select courses from.
     * @return The courses for the specified user.
     * @throws PersistenceException If a database or xml-rpc exception occurs.
     * @see fi.dwo.client.persistence.PersistenceFacade#selectCoursesForClass(int, int)
     */
    public Course[] getCourses(User user) throws PersistenceException {
        try {
            MapperIF mapper = MapperCreator.instance(Course.class);
            Vector v;
            int profileId = ((DwoIF) DwoHelper.getApplet()).getDwoProfile().getID();
			int guestID = PROFILEOFFSET-profileId;
            if (user == null) {
				v = DbAccessCreator.instance().getCourses(guestID);
            } else {
                if (user instanceof Teacher) {
      //              v = DbAccessCreator.instance().getCourses(user.getUserID());
                	Object[] schoolCourses = mapper.get(user.getSchool());
                	Object[] dwoCourses     = mapper.getObjectFromReturn(DbAccessCreator.instance().getCourses(guestID));
      // caching side effect. UNDO, we doen nu lazy....
                	//MapperCreator.instance(Sco.class).get(new Object[] { user.getSchool(), ((DwoIF) DwoHelper.getApplet()).getDwoProfile()} );
                	return combine(dwoCourses, schoolCourses);
                } else {
                    SchoolClass schoolClass = user.getInClass();
                    if (schoolClass == null) {
                        v = DbAccessCreator.instance().getCourses(guestID);
                    } else {
                        v = DbAccessCreator.instance().getCoursesForClass(
                                schoolClass.getID());
// FIXME aanzetten als clipBeforeAfter weer in gebruik wordt genomen.
// Het XML-RPC protocol doet niet aan TIMEZONES 
// dat betekent dat date(0) niet werkt voor 'notAfter'
                        //v = clipBeforeAfter(v);
                        Course[] courses = (Course[]) mapper.getObjectFromReturn(v);
// FIXME hier maken we de caching effecten ongedaan.
                        undoCachingEffect(courses);
						return courses;
                    }
                }
            }
            return (Course[]) mapper.getObjectFromReturn(v);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_IO);
        } catch (XmlRpcException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_XML_RPC);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_DB);
        }

    }
    
    /**
     * Als no_chilren, maar loaded wordt dan true
     */
	private static final Course[] NO_CHILDREN_LOADED = new Course[0];
	/**
	 * vul de children van de courses. Daarmee wordt een 'loadchildren' hopelijk niet aangeroepen.
	 */
    private void undoCachingEffect(Course[] courses) {
    	MapperIF mapper = MapperCreator.instance(Course.class);
    	sequence(courses);
    	for (int i = 0; i < courses.length; i++) {
			Course c = courses[i];
			if(c.isWithChildren())
				c.setChildren(NO_CHILDREN_LOADED);
		}
		for (int i = 0; i < courses.length; i++) {
			Course c = courses[i];
			if(c.getParentID() != 0)
			{
				CourseMap parent = c.getParentMap();
				if(parent == null)
					try {
						parent = (CourseMap) mapper.get(c.getParentID()); // deze komt toch uit de cache?
					} catch (Exception e) {
						continue;
					} 				
				parent.addChild(c);	// als dit werkt, zou dat mooi zijn!
			}
		}
	}

	private Vector clipBeforeAfter(Vector v) {
		Iterator iter = v.iterator();
		long now = System.currentTimeMillis();
		while (iter.hasNext()) {
			Hashtable ht = (Hashtable) iter.next();
			Object o = ht.get("notBefore");
			if(o instanceof Date)
			{
				if(now < ((Date)o).getTime())
				{
					iter.remove();
					continue;
				}
			}
			o = ht.get("notAfter");
			if(o instanceof Date) {
				if(now > ((Date)o).getTime())
				{
					iter.remove();
					continue;
				}
			}
		}
		return v;
	}

	private Course[] combine(Object[] dwoCourses, Object[] schoolCourses) {
    	Course[] courses = new Course[dwoCourses.length + schoolCourses.length];
    	System.arraycopy(dwoCourses, 0, courses, 0, dwoCourses.length);
    	System.arraycopy(schoolCourses, 0, courses, dwoCourses.length, schoolCourses.length);
    	Arrays.sort(courses, new Comparator() {

			public int compare(Object o1, Object o2) {
				Course c1 = (Course)o1;
				Course c2 = (Course)o2;
				return c1.getName().compareTo(c2.getName());
			}});
    	return courses;
	}

	/**
     * Returns all the courses that are selected for the specified class.
     * A teacher could select some courses for a schoolclass.
     * @param schoolClass The user to select courses from.
     * @return The courses for the specified class.
     * @throws PersistenceException If a database or xml-rpc exception occurs.
     * @see fi.dwo.client.persistence.PersistenceFacade#selectCoursesForClass(int, int)
     */
    public Course[] getCourses(SchoolClass schoolClass) throws PersistenceException {
        try {
            Vector v;
            v = DbAccessCreator.instance().getCoursesForClass(schoolClass.getID());
            MapperIF mapper = MapperCreator.instance(Course.class);
            return (Course[]) mapper.getObjectFromReturn(v);
        
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_IO);
        } catch (XmlRpcException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_XML_RPC);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_DB);
        }

    }

    /**
     * returns the selected courses for the specified schoolclass.
     * @param schoolClass The schoolclass wherefrom the courses must selected.
     * @return The courses selected for the specified schoolclass.
     * @throws PersistenceException
     */
    public Course[] getSelectedSchoolCourses(SchoolClass schoolClass)
            throws PersistenceException {
        try {
            Vector v;
            {
                v = DbAccessCreator.instance().getCoursesForClass(
                        schoolClass.getID());
            }
// geen mappen hier teruggeven! alleen modules
            for (Iterator iterator = v.iterator(); iterator.hasNext();) {
				Hashtable map = (Hashtable) iterator.next();
				if(Boolean.TRUE.equals(map.get("withChildren"))) // FIXME CONSTANT
					iterator.remove();
			}
            MapperIF mapper = MapperCreator.instance(Course.class);
            return (Course[]) mapper.getObjectFromReturn(v);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_IO);
        } catch (XmlRpcException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_XML_RPC);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_DB);
        }

    }

    public final static Date DATE_NULL = new Date(0);
    
    /**
     * Select a course for a schoolclass.
     * @param classID The class to select the course.
     * @param courseID The course to select.
     * @param tot 
     * @param van 
     * @param type 
     * @throws PersistenceException
     */
    public void selectCoursesForClass(int classID, int courseID, int type, Date van, Date tot)
            throws PersistenceException {
    	if(van == null) van = DATE_NULL;
    	if(tot == null) tot = DATE_NULL;
        try {
            DbAccessCreator.instance().selectCoursesForClass(classID, courseID, type, van, tot);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_IO);
        } catch (XmlRpcException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_XML_RPC);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_DB);
        }

    }

    /**
     * Deselect a course for a schoolclass.
     * @param classID The class to deselect the course.
     * @param courseID The course to deselect.
     * @throws PersistenceException
     */
    public void deSelectCoursesForClass(int classID, int courseID)
            throws PersistenceException {
        try {
            DbAccessCreator.instance().deSelectCoursesForClass(classID,
                    courseID);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_IO);
        } catch (XmlRpcException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_XML_RPC);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_DB);
        }

    }

    ////peter

    /**
     * Register a new user in the DWO.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param firstname The firstname of the user.
     * @param middlename The middlename of the user. <br>
     *            e.g: <code>Van</code>
     * @param lastname The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @return If the user was successfully registered true is returned.
     *         Otherwise false is returned.
     * @throws fi.dwo.client.system.RegisterException If some
     *             register-information is incorrect or the user already exists.
     *  
     */
    public boolean register(String username, String password, String firstname,
            String middlename, String lastname, String email)
            throws RegisterException {
// password null: LDAP user
        password = password == null ? "" : MD5.getHashString(password);
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
		    return dbAccess.register(username, password, firstname,
		            middlename, lastname, email);
		} catch (IOException e) {
		    throw new RegisterException(RegisterException.EX_IO);
		} catch (XmlRpcException e) {
		    if (e.code != 0) {
		        throw  new RegisterException( e.code, username);
		    } else {
		        throw new RegisterException(RegisterException.EX_XML_RPC);
		    }
		} catch (SQLException e) {
		    throw new RegisterException(RegisterException.EX_DB);
		} catch (DwoXmlRpcException e) {
		    throw new RegisterException(e.code, username);
		}
    }

    /**
     * Register a user in the system. Als links a user to a school.
     * 
     * @param username The username of the user.
     * @param password The password of the user.
     * @param firstname The firstname of the user.
     * @param middlename The middlename of the user. <br>
     *            e.g: <code>Van</code>
     * @param lastname The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @param schoolLogin The schoolloginname of the school of the user.
     * @param group The group from the user.
     * @param groupPassword The password corresponding with the specified group
     *            and the school.
     * @return If the user was successfully registered true is returned.
     *         Otherwise false is returned.
     * @throws fi.dwo.client.system.RegisterException If some
     *             register-information is incorrect or the user already exists.
     *  
     */
    public boolean register(String username, String password, String firstname,
            String middlename, String lastname, String email,
            String schoolLogin, Group group, String groupPassword)
            throws RegisterException {
        password = MD5.getHashString(password);
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
		    return dbAccess.register(username, password, firstname,
		            middlename, lastname, email, schoolLogin, group
		                    .getGroupID(), groupPassword);
		} catch (IOException e) {
		    throw new RegisterException(RegisterException.EX_IO);
		} catch (XmlRpcException e) {
		    if (e.code != 0) {
		        throw new RegisterException(e.code, username);
		    } else {
		        throw new RegisterException(RegisterException.EX_XML_RPC);
		    }
		} catch (SQLException e) {
		    throw new RegisterException(RegisterException.EX_DB);
		} catch (DwoXmlRpcException e) {
		    throw new RegisterException(e.code, username);
		}
    }

    /**
     * Logs a user in into the system. The user-data will be checked in the database.
     * 
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The user who logged in. If an exception occurs, null is returned.
     * @throws fi.dwo.client.system.LoginException If some login-information is
     *             incorrect.
     *  
     */
    public User login(String username, String password) throws LoginException {
        password = MD5.getHashString(password);
        return login_intern(username, password);
    }

    /**
     * @param username
     * @param password
     * @return user/null?
     * @throws LoginException
     */
    private User login_intern(String username, String password) throws LoginException
    {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            try {
                MapperIF mapper = MapperCreator.instance(User.class);
                return (User) mapper.getObjectFromReturn(dbAccess.login(
                        username, password));
            } catch (IOException e) {
                e.printStackTrace();
                throw new LoginException(LoginException.EX_IO, e);
            } catch (XmlRpcException e) {
                if (e.code != 0) {
                    //e.printStackTrace();
                    throw (LoginException) getException(e, e.code);
                } else {
                    e.printStackTrace();
                    throw new LoginException(LoginException.EX_XML_RPC, e);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new LoginException(LoginException.EX_DB, e);
            } catch (DwoXmlRpcException e) {
                e.printStackTrace();
                throw (LoginException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
            e.printStackTrace();
            throw new LoginException(LoginException.EX_UNKNOWN_ERROR, e);
        }
    }

    /**
     * Connects a user to a school.
     * @param user The user to connect.
     * @param schoolLogin The schoolname.
     * @param group The usergroup of the user.
     * @param groupPassword The password of the usergroup.
     * @return fi.dwo.client.domain.School The school of the user.
     * @throws fi.dwo.client.system.RegisterException
     *  
     */
    public School addToSchool(User user, String schoolLogin, Group group,
            String groupPassword) throws RegisterException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            try {
                MapperIF mapper = MapperCreator.instance(School.class);
                Hashtable result = dbAccess.addToSchool(user.getUserID(),
                        schoolLogin, group.getGroupID(), groupPassword);
                return (School) mapper.getObjectFromReturn(result);
            } catch (IOException e) {
                throw new RegisterException(RegisterException.EX_IO);
            } catch (XmlRpcException e) {
                if (e.code != 0) {
                    throw (RegisterException) getException(e, e.code);
                } else {
                    throw new RegisterException(RegisterException.EX_XML_RPC);
                }
            } catch (SQLException e) {
                throw new RegisterException(RegisterException.EX_DB);
            } catch (DwoXmlRpcException e) {
                throw (RegisterException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
            throw new RegisterException(RegisterException.EX_UNKNOWN_ERROR);
        }
    }

    /**
     * Changes the account of the user.
     * @param user The user to change.
     * @param password The new password.
     * @param firstname The new firstname.
     * @param middlename The new middlename.
     *            e.g: <code>Van</code>
     * @param lastname The new lastname.
     * @param email The new e-mail address of the user.
     * @param c The new schoolclass of the user.
     * @return boolean If the user was successfully changed it returns true, otherwise false is returned.
     * @throws fi.dwo.client.system.RegisterException
     *  
     */
    public boolean changeAccount(User user, String password,
            String newPassword, String firstname, String middlename,
            String lastname, String email, SchoolClass c)
            throws RegisterException {
// bypass password check
    	if(password == null)
    		password = "";
    	else
    		password = MD5.getHashString(password);
    	if(newPassword == null)
    		newPassword = "";
    	else
    		newPassword = MD5.getHashString(newPassword);
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            try {
                int classid = c==null?0:c.getID();
				return dbAccess.changeAccount(user.getUserID(), password,
                        newPassword, firstname, middlename, lastname, email, classid);
            } catch (IOException e) {
            	e.printStackTrace();
                throw new RegisterException(RegisterException.EX_IO);
            } catch (XmlRpcException e) {
            	e.printStackTrace();
                if (e.code != 0) {
                    throw (RegisterException) getException(e, e.code);
                } else {
                    throw new RegisterException(RegisterException.EX_XML_RPC);
                }
            } catch (SQLException e) {
            	e.printStackTrace();
                throw new RegisterException(RegisterException.EX_DB);
            } catch (DwoXmlRpcException e) {
            	e.printStackTrace();
                throw (RegisterException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
        	e.printStackTrace();
            throw new RegisterException(RegisterException.EX_UNKNOWN_ERROR);
        }
    }

    /**
     * Changes the account of the user.
     * @param user The user to change.
     * @param password The new password.
     * @param firstname The new firstname.
     * @param middlename The new middlename.
     *            e.g: <code>Van</code>
     * @param lastname The new lastname.
     * @param email The new e-mail address of the user.
     * @return boolean If the user was successfully changed it returns true, otherwise false is returned.
     * @throws fi.dwo.client.system.RegisterException
     *  
     */
    public boolean changeAccount(User user, String password,
            String newPassword, String firstname, String middlename,
            String lastname, String email) throws RegisterException {
        if(password != null) 
        	password = MD5.getHashString(password);
        else 
        	password = "";
        newPassword = MD5.getHashString(newPassword);

        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            try {
                return dbAccess.changeAccount(user.getUserID(), password,
                        newPassword, firstname, middlename, lastname, email);
            } catch (IOException e) {
                throw new RegisterException(RegisterException.EX_IO);
            } catch (XmlRpcException e) {
                if (e.code != 0) {
                    throw (RegisterException) getException(e, e.code);
                } else {
                    throw new RegisterException(RegisterException.EX_XML_RPC);
                }
            } catch (SQLException e) {
                throw new RegisterException(RegisterException.EX_DB);
            } catch (DwoXmlRpcException e) {
                throw (RegisterException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
            throw new RegisterException(RegisterException.EX_UNKNOWN_ERROR);
        }
    }

    /**
     * Creates a new schoolclass for the specified teacher
     * @param teacher The teacher of the new schoolclass.
     * @param className The name of the new schoolclass.
     * @return The new schoolclass. If an exception occurs, null is returned.
     * @throws fi.dwo.client.system.ClassException If something went wrong with the classname.
     *  
     */
    public SchoolClass addClass(Teacher teacher, String className)
            throws ClassException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            try {
                MapperIF mapper = MapperCreator.instance(SchoolClass.class);
                Hashtable result = dbAccess.addClass(teacher.getUserID(),
                        className);
                return (SchoolClass) mapper.getObjectFromReturn(result);
            } catch (IOException e) {
                throw new ClassException(ClassException.EX_IO);
            } catch (XmlRpcException e) {
                if (e.code != 0) {
                    throw (ClassException) getException(e, e.code);
                } else {
                    throw new ClassException(ClassException.EX_XML_RPC);
                }
            } catch (SQLException e) {
                throw new ClassException(ClassException.EX_DB);
            } catch (DwoXmlRpcException e) {
                throw (ClassException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
            throw new ClassException(ClassException.EX_UNKNOWN_ERROR);
        }
    }
    
    /**
     * Creates a new school
     * @deprecated use {@link #addSchool(int, String, String, Hashtable)}
     * @param id The id of the new school
     * @param schoolName The name of the new school.
     * @param schoolLogin The login name of the new school.
     * @param studentPassw Password for students.
     * @param teacherPassw Password for teachers.
     * @return The new schoolclass. If an exception occurs, null is returned.
     * @throws fi.dwo.client.system.ClassException If something went wrong with the classname.
     *  
     */
     
    
    public School addSchool(int id, String schoolName, String schoolLogin, String studentPassw, String teacherPassw)
            throws SchoolException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            try {
                MapperIF mapper = MapperCreator.instance(School.class);
                Hashtable result = dbAccess.addSchool(id, schoolName, schoolLogin, studentPassw, teacherPassw);
                return (School) mapper.getObjectFromReturn(result);
            } catch (IOException e) { System.out.println(e.toString());
                throw new SchoolException(SchoolException.EX_IO);
            } catch (XmlRpcException e) {
                if (e.code != 0) {
                    throw (SchoolException) getException(e, e.code);
                } else {
                    throw new SchoolException(SchoolException.EX_XML_RPC);
                }
            } catch (SQLException e) { System.out.println(e.toString());
                throw new SchoolException(SchoolException.EX_DB);
            } catch (DwoXmlRpcException e) {
                throw (SchoolException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
            throw new SchoolException(SchoolException.EX_UNKNOWN_ERROR);
        }
    }
    
    public School addSchool(int id, String schoolName, String schoolLogin, Hashtable passw)
    throws SchoolException {
    	DbAccessIF dbAccess = DbAccessCreator.instance();
    	try {
    		try {
    			MapperIF mapper = MapperCreator.instance(School.class);
    			Hashtable result = dbAccess.addSchool(id, schoolName, schoolLogin, passw);
    			return (School) mapper.getObjectFromReturn(result);
    		} catch (IOException e) { System.out.println(e.toString());
    		throw new SchoolException(SchoolException.EX_IO);
    		} catch (XmlRpcException e) {
    			if (e.code != 0) {
    				throw (SchoolException) getException(e, e.code);
    			} else {
    				throw new SchoolException(SchoolException.EX_XML_RPC);
    			}
    		} catch (SQLException e) { System.out.println(e.toString());
    			throw new SchoolException(SchoolException.EX_DB);
    		} catch (DwoXmlRpcException e) {
    			throw (SchoolException) getException(e, e.code);
    		}
    	} catch (PersistenceException e) {
    		throw new SchoolException(SchoolException.EX_UNKNOWN_ERROR);
    	}
    }
    
    /** Edit an old school
     * @deprecated use {@link #editSchool(int, String, String, Hashtable)}
     * @param schoolName The name of the new school.
     * @param schoolLogin The login name of the new school.
     * @param studentPassw Password for students.
     * @param teacherPassw Password for teachers.
     * @return The new schoolclass. If an exception occurs, null is returned.
     * @throws fi.dwo.client.system.ClassException If something went wrong with the classname.
     * 
     */
    public School editSchool(int schoolID, String schoolName, String schoolLogin, String studentPassw, String teacherPassw)
            throws SchoolException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            try {
                MapperIF mapper = MapperCreator.instance(School.class);
                Hashtable result = dbAccess.editSchool(schoolID,schoolName, schoolLogin, studentPassw, teacherPassw);
                return (School) mapper.getObjectFromReturn(result);
            } catch (IOException e) { System.out.println(e.toString());
                throw new SchoolException(SchoolException.EX_IO);
            } catch (XmlRpcException e) {
                if (e.code != 0) {
                    throw (SchoolException) getException(e, e.code);
                } else {
                    throw new SchoolException(SchoolException.EX_XML_RPC);
                }
            } catch (SQLException e) { System.out.println(e.toString());
                throw new SchoolException(SchoolException.EX_DB);
            } catch (DwoXmlRpcException e) {
                throw (SchoolException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
            throw new SchoolException(SchoolException.EX_UNKNOWN_ERROR);
        }
    }


    /**
     * Unpack the exception out of XML-RPC.
     * The message of the specified exception is the name of the exceptionclass to return.
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
            Class[] constrArgTypes = { int.class };
            Constructor excConstr = excClass.getConstructor(constrArgTypes);
            Object[] constrArgs = { new Integer(errorCode) };
            return (Exception) excConstr.newInstance(constrArgs);
        } catch (Exception e1) {
            throw new PersistenceException(
                    PersistenceException.EX_UNKNOWN_ERROR);
        }
    }
    /**
     * Deletes a user out of the database.
     * @param user The user to delete.
     *  
     */
    public void deleteUser(User user) throws RegisterException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            dbAccess.deleteUser(user.getUserID());
        } catch (IOException e) {
            throw new RegisterException(RegisterException.EX_IO);
        } catch (XmlRpcException e) {
            throw new RegisterException(RegisterException.EX_XML_RPC);
        } catch (SQLException e) {
            throw new RegisterException(RegisterException.EX_DB);
        }

    }

    /**
     * Deletes the specified schoolclass.
     * if mustEmpty is true, and the class contains students, this function
     * returns false. Otherwise it returns true
     * 
     * @param c The class to delete
     * @param mustEmpty if true and the schoolclass is not empty, the schoolclass is not deleted.
     * @return  If mustEmpty is true, and the class contains students, this function
     * returns false. Otherwise it returns true
     *  
     */
    public boolean deleteClass(SchoolClass c, boolean mustEmpty)
            throws ClassException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            boolean returnvalue = dbAccess.deleteClass(c.getID(), mustEmpty);
            if (returnvalue) {
                MapperCreator.instance(SchoolClass.class).removeObject(
                        c.getID());
            }
            return returnvalue;
        } catch (IOException e) {
            throw new ClassException(ClassException.EX_IO);
        } catch (XmlRpcException e) {
            throw new ClassException(ClassException.EX_XML_RPC);
        } catch (SQLException e) {
            throw new ClassException(ClassException.EX_DB);
        }
    }

    /**
     * Returns all the results for the specified courses and the classes of the specified teacher.
     * @param courses The courses wherefrom the result must be returned.
     * @param teacher The teacher wherefrom the results of the schoolclasses must be returned. 
     * @return The results for the specified courses and the classes of the specified teacher.
     * @throws PersistenceException
     */
    public Vector getResults(Course[] courses, Teacher teacher)
            throws PersistenceException {
        Vector courseIDs = new Vector();

        for (int i = 0; i < courses.length; i++) {
            courseIDs.addElement(new Integer(courses[i].getID()));
        }
        try {
            Vector v = DbAccessCreator.instance().getResults(courseIDs,
                    teacher.getUserID());
            MapperIF mapper = MapperCreator.instance(UserResultList.class);
            Object[] oa = mapper.getObjectFromReturn(v);
            if (oa.length > 0) {
                return (Vector) (oa[0]);
            } else {
                return new Vector();
            }
        } catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO);
        } catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC);
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB);
        }
    }

    /**
     * Returns the results for the specified courses and the students in the specified schoolclass.
     * @param courses The courses wherefrom the result must be returned.
     * @param schoolClass The schoolclass wherefrom the student-results must be returned.
     * @param teacher The teacher of the schoolclass.
     * @return The result for the students in the specified class and for the specified courses.
     * @throws PersistenceException
     */
    public Vector getResults(Course[] courses, SchoolClass schoolClass,
            Teacher teacher) throws PersistenceException {
        Vector courseIDs = new Vector();

        for (int i = 0; i < courses.length; i++) {
            courseIDs.addElement(new Integer(courses[i].getID()));
        }
        try {
            Vector v = DbAccessCreator.instance().getResults(courseIDs,
                    schoolClass.getID(), teacher.getUserID());
            MapperIF mapper = MapperCreator.instance(UserResultList.class);
            return (Vector) (mapper.getObjectFromReturn(v)[0]);
        } catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO, e);
        } catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB, e);
        }
    }

    /**
     * Returns the result for the SCO's in the specified course, for the students in the specified SchoolClass.
     * @param course The course wherefrom the SCO's results must returned.
     * @param schoolClass  The schoolclass wherefrom the student-results must be returned.
     * @param teacher The teacher of the schoolclass.
     * @return The result for the students in the specified class and for the specified course.
     * @throws PersistenceException
     */
    public Vector getResults(Course course, SchoolClass schoolClass,
            Teacher teacher) throws PersistenceException {
        try {
            Vector v = DbAccessCreator.instance().getResults(course.getID(),
                    schoolClass.getID(), teacher.getUserID());
            MapperIF mapper = MapperCreator.instance(UserResultList.class);
            return (Vector) (mapper.getObjectFromReturn(v)[0]);
        } catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO);
        } catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC);
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB);
        }
    }
    /**
     * Returns the result for the SCO's in the specified course, for the student.
     * @param course The course where from the SCO's results must returned.
     * @param user The student.
     * @return The result for the student and for the specified course.
     * @throws PersistenceException
     */
    public Vector getUserResults(Course course,
            User user) throws PersistenceException {
        try {
            Vector v = DbAccessCreator.instance().getUserResults(course.getID(),
                     user.getUserID());
            MapperIF mapper = MapperCreator.instance(UserResultList.class);
            return (Vector) (mapper.getObjectFromReturn(v)[0]);
        } catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO);
        } catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC);
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB);
        }
    }

    /**
     * Returns all the results of the SCO's of the course, for the classes from the specified teacher.
     * @param course The course wherefrom the SCO's results must returned.
     * @param teacher The teacher wherefrom the results of the schoolclasses must be returned.
     * @return The results for the SCO's of the classes from the teacher.
     * @throws PersistenceException
     */
    public Vector getResults(Course course, Teacher teacher)
            throws PersistenceException {
        try {
            Vector v = DbAccessCreator.instance().getResults(course.getID(),
                    teacher.getUserID());
            MapperIF mapper = MapperCreator.instance(UserResultList.class);
            return (Vector) (mapper.getObjectFromReturn(v)[0]);
        } catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO);
        } catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC);
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB);
        }
    }

    /**
     * Disconnects a user from a class.
     * @param user The user to disconnect.
     *  
     */
    public void disconnectFromClass(User user) throws RegisterException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            dbAccess.disconnectFromClass(user.getUserID());
        } catch (IOException e) {
            throw new RegisterException(RegisterException.EX_IO);
        } catch (XmlRpcException e) {
            throw new RegisterException(RegisterException.EX_XML_RPC);
        } catch (SQLException e) {
            throw new RegisterException(RegisterException.EX_DB);
        }

    }

    /**
     * Renames the name of the schoolclass in the database.
     * @param schoolClass The class to rename.
     * @param newName The new name of the class.
     * @param iconizer 
     * @throws ClassException
     */
    public void renameClass(SchoolClass schoolClass, String newName, boolean iconizer)
            throws ClassException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            try {
                dbAccess.renameClass(schoolClass.getID(), newName, iconizer);
            } catch (IOException e) {
                throw new ClassException(ClassException.EX_IO);
            } catch (XmlRpcException e) {
                if (e.code != 0) {
                    throw (ClassException) getException(e, e.code);
                } else {
                    throw new ClassException(ClassException.EX_XML_RPC);
                }
            } catch (SQLException e) {
                throw new ClassException(ClassException.EX_DB);
            } catch (DwoXmlRpcException e) {
                throw (ClassException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
            throw new ClassException(ClassException.EX_UNKNOWN_ERROR);
        }
    }

    /**
     * Returns all the courses which can be edited by the teacher.
     * @param teacher The teacher wherefrom the courses must be returned.
     * @return The courses which can be edited by the specified teacher.
     * @throws PersistenceException
     */
    public Course[] getEditableCourses(User user)
            throws PersistenceException {
        if(user instanceof Teacher){
        	Teacher teacher = (Teacher)user;
	        try {
	            Vector v;
	            v = DbAccessCreator.instance().getEditableCourses(
	                    teacher.getSchool().getSchoolID());
	            if(user.hasRight(User.PROFILE_ADMIN_RIGHT) && !GuiConstants.GUI_ICONIZED)
	            {
	            	Vector v2 = DbAccessCreator.instance().getEditableCoursesAdmin();
	            	v.addAll(v2);
	            }
	            
	            MapperIF mapper = MapperCreator.instance(Course.class);
	            return (Course[]) mapper.getObjectFromReturn(v);
	        } catch (IOException e) {
	            System.out.println(e.getMessage());
	            e.printStackTrace();
	            throw new PersistenceException(PersistenceException.EX_IO);
	        } catch (XmlRpcException e) {
	            System.out.println(e.getMessage());
	            e.printStackTrace();
	            throw new PersistenceException(PersistenceException.EX_XML_RPC);
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	            e.printStackTrace();
	            throw new PersistenceException(PersistenceException.EX_DB);
	        }
	    }
	    else if(user instanceof Admin){
        	try {
	            Vector v;
	            v = DbAccessCreator.instance().getEditableCoursesAdmin();
	            MapperIF mapper = MapperCreator.instance(Course.class);
	            return (Course[]) mapper.getObjectFromReturn(v);
	        } catch (IOException e) {
	            System.out.println(e.getMessage());
	            e.printStackTrace();
	            throw new PersistenceException(PersistenceException.EX_IO);
	        } catch (XmlRpcException e) {
	            System.out.println(e.getMessage());
	            e.printStackTrace();
	            throw new PersistenceException(PersistenceException.EX_XML_RPC);
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	            e.printStackTrace();
	            throw new PersistenceException(PersistenceException.EX_DB);
	        }
	    }
	    else return null;

    }

    /**
     * Creates a new course for the specified school.
     * @param school The school wherefore the course must created.
     * @param name The name of the course.
     * @param description The description of the course.
     * @return The new course. If an exception occurs, null is returned.
     * @throws CourseException
     */
    public Course addCourse(School school, String name, String description, DwoProfile dwoProfile, Course parent, boolean withChildren)
            throws CourseException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            try {
                int parentID = parent==null ? 0 : parent.getID();
				int schoolID = parent==null ? school.getSchoolID() : parent.getSchoolID();
				int result = dbAccess.addCourse(schoolID, name,
                        description, dwoProfile.getID(), parentID, withChildren);
                Course c = new Course();
                c.setCourseID(result);
                c.setDescription(description);
                c.setName(name);
                c.setImageUrl(school.getImage());
                c.setDwoProfile(dwoProfile.getID());
                c.setSchoolID(schoolID); // DEZE IS VERGETEN, WIM 9/5/2011
                c.setParentID(parentID);
                c.resetParent();
                if(withChildren) c.setChildren(Course.NO_CHILDREN);
                else c.setScoList(Course.NO_SCOS);
                MapperIF map = MapperCreator.instance(Course.class);
                map.put(result, c);
                return c;
            } catch (IOException e) {
                throw new CourseException(CourseException.EX_IO);
            } catch (XmlRpcException e) {
                if (e.code != 0) {
                    throw (CourseException) getException(e, e.code);
                } else {
                    throw new CourseException(CourseException.EX_XML_RPC);
                }
            } catch (SQLException e) {
                throw new CourseException(CourseException.EX_DB);
            } catch (DwoXmlRpcException e) {
                throw (CourseException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
            throw new CourseException(CourseException.EX_UNKNOWN_ERROR);
        }
    }

    /**
     * Updates the coursedata (name and description) in the database.
     * @param course The course to update in the database.
     * @return If true, the coursedata was successfully changed. Otherwise, false is returned.
     */
    public boolean updateCourse(Course course) throws CourseException {
    
    	
        DbAccessIF dbAccess = DbAccessCreator.instance();
    	if(course.getSchoolID()==0)
    	{
    		try {
				dbAccess.log("course " + course.getID() + " " + course.getName() + " geen schoolID in updateCourse");
			} catch (Exception e) {
			} 
    	}
        try {
            try {
        		MapperCreator.instance(Course.class).removeObject(course.getID());
            	if(course.parentChanged())
            	{
            		boolean result =
            			dbAccess.changeCourse(course.getID(), course.getName(), 
            					course.getDescription(),
            					course.isExport(),
            					course.getSchoolID(), 
            					course.getParentID());
            		if(result) course.resetParent();
            		return result;
            	}
            	
                return dbAccess.changeCourse(course.getID(), course.getName(),
                        course.getDescription()
                        , course.isExport()                // FIXME fallback naar 2parameter methode.
                        , course.getSchoolID()			   // TODO fallback!
                );
            } catch (IOException e) {
                throw new CourseException(CourseException.EX_IO);
            } catch (XmlRpcException e) {
                if (e.code != 0) {
                    throw (CourseException) getException(e, e.code);
                } else {
                    throw new CourseException(CourseException.EX_XML_RPC);
                }
            } catch (SQLException e) {
            	e.printStackTrace();
                throw new CourseException(CourseException.EX_DB);
            } catch (DwoXmlRpcException e) {
                throw (CourseException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
            throw new CourseException(CourseException.EX_UNKNOWN_ERROR);
        }
    }

    /**
     * Adds a sco to the specified course.
     * @param course The course where the sco must be added.
     * @param appletConfig The AppletConfig of the new sco (it contains the applet and the default launchdata).
     * @param name The name of the new sco.
     * @param description The description of the new sco.
     * @return The new sco. If an exception occurs, null was returned.
     * @throws ScoException
     */
    public Sco addSco(Course course, AppletConfig appletConfig, String name,
            String description, boolean showScore) throws ScoException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            try {
                Sco[] scos = course.getScoList();
                int max = 0;
                for (int i = 0; i < scos.length; i++) {
                    if (scos[i].getSequencenr() > max) {
                        max = scos[i].getSequencenr();
                    }
                }
                int result;
                Sco sco = new Sco();
// if true use 'oldschool'
                if(showScore)
                	result = dbAccess.addSco(course.getID(), name, description,
                            appletConfig.getID(), ++max);
                else
                {	result = dbAccess.addSco(course.getID(), name, description,
                        appletConfig.getID(), ++max, false);
                	sco.setShowScore(Boolean.FALSE);
                }
                sco.setScoID(result);
                sco.setAppletID(appletConfig.getAppletID());
                sco.setName(name);
                sco.setDescription(description);
                sco.setSequencenr(max);
                sco.setCourse(course);
                sco.setLaunchdata((Hashtable)new StringCodeObject((String) appletConfig.getLaunchdata()).toObject());
                sco.setCourseChanged(false);
                MapperCreator.instance(Sco.class).put(result, sco);
                return sco;
            } catch (IOException e) {
                throw new ScoException(ScoException.EX_IO);
            } catch (XmlRpcException e) {
                if (e.code != 0) {
                    throw (ScoException) getException(e, e.code);
                } else {
                    throw new ScoException(ScoException.EX_XML_RPC);
                }
            } catch (SQLException e) {
                throw new ScoException(ScoException.EX_DB);
            } catch (DwoXmlRpcException e) {
                throw (ScoException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
            throw new ScoException(ScoException.EX_UNKNOWN_ERROR);
        }
    }

    /**
     * Updates the name and the description of the sco in the database.
     * @param sco The sco to update in the database.
     * @return True if the sco was successfully changed.
     * @throws ScoException
     */
    public boolean updateSco(Sco sco) throws ScoException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            try {
        		MapperCreator.instance(Sco.class).removeObject(sco.getID());
            	if(sco.isCourseChanged())
            	{
            		dbAccess.moveSco(sco.getID(), sco.getCourse().getID(), sco.getSequencenr(), sco.getScoName());
            		sco.setCourseChanged(false);
            	}
            	if(sco.isDataChanged())
            	{	boolean result;
         			if(sco.getShowScore() != null)
         		            		result = dbAccess.changeSco(sco.getID(), sco.getScoName(), sco
					        .getDescription(), sco.getLaunchdataString(), sco.isShowScore());
         			else
		            		result = dbAccess.changeSco(sco.getID(), sco.getScoName(), sco
							        .getDescription(), sco.getLaunchdataString());
         			
         			sco.setDataChanged(false);
					return result;
            	} else
            	{
            		if(sco.getShowScore() != null)
            			return dbAccess.changeSco(sco.getID(), sco.getScoName(), sco.getDescription(), sco.isShowScore());
            		else
            			return dbAccess.changeSco(sco.getID(), sco.getScoName(), sco.getDescription());
            			
            	}
            	
            } catch (IOException e) {
                throw new ScoException(ScoException.EX_IO);
            } catch (XmlRpcException e) {
                if (e.code != 0) {
                    throw (ScoException) getException(e, e.code);
                } else {
                    throw new ScoException(ScoException.EX_XML_RPC);
                }
            } catch (SQLException e) {
                throw new ScoException(ScoException.EX_DB);
            } catch (DwoXmlRpcException e) {
                throw (ScoException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
            throw new ScoException(ScoException.EX_UNKNOWN_ERROR);
        }
    }

/**
 * Swap de sequencenrs van twee sco's.
 * Beide sco's moeten van dezelfde course zijn.
 * @param sco1
 * @param sco2
 * @return true
 * @throws ScoException
 */
    public boolean swapScoSequenceNr(Sco sco1, Sco sco2) throws ScoException
    {
        boolean result = false;
        if(sco1.getCourse() != sco2.getCourse())
        	throw new ScoException(ScoException.EX_DB);
        
    	DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
			try {
		    	int nr1 = sco1.getSequencenr();
				int nr2 = sco2.getSequencenr();
				result = dbAccess.changeScoSequenceNr(sco1.getScoID(), nr2, sco2.getScoID(), nr1);
				if(result) {
					Sco[] scos = sco1.getCourse().getScoList();
					scos[nr2-1] = sco1;
					scos[nr1-1] = sco2;
					sco1.setSequencenr(nr2);
					sco2.setSequencenr(nr1);
					sco1.setCourseChanged(false);
					sco1.setCourseChanged(false);
				}
			} catch (DwoXmlRpcException e) {
				throw (ScoException) getException(e,e.code);
			} catch (SQLException e) {
				throw new ScoException(ScoException.EX_DB);
			} catch (IOException e) {
				throw new ScoException(ScoException.EX_IO);
			} catch (XmlRpcException e) {
			    if (e.code != 0) {
			        throw (ScoException) getException(e, e.code);
			    } else {
			        throw new ScoException(ScoException.EX_XML_RPC);
			    }
			}
		} catch (PersistenceException e) {
            throw new ScoException(ScoException.EX_UNKNOWN_ERROR);
		}
        return result;
    }
    
    
    /**
     * Deletes the specified course. The sco's and corresponding results will also be deleted.
     * @param course The course to delete.
     * @return Returns true if the course is deleted.
     * @throws CourseException
     */
    public boolean deleteCourse(Course course) throws CourseException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            try {
                boolean returnValue = dbAccess.deleteCourse(course.getID());
                if (returnValue) {
                    MapperCreator.instance(Course.class).removeObject(
                            course.getID());
                }
                return returnValue;
            } catch (IOException e) {
                throw new CourseException(CourseException.EX_IO);
            } catch (XmlRpcException e) {
                if (e.code != 0) {
                    throw (CourseException) getException(e, e.code);
                } else {
                    throw new CourseException(CourseException.EX_XML_RPC);
                }
            } catch (SQLException e) {
                throw new CourseException(CourseException.EX_DB);
            } catch (DwoXmlRpcException e) {
                throw (CourseException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
            throw new CourseException(CourseException.EX_UNKNOWN_ERROR);
        }
    }

    /**
     * Deletes the specified sco. The results of the students at this sco will also been deleted.
     * @param sco The sco to delete.
     * @return Returns true if the sco is successfully deleted.
     * @throws ScoException
     */
    public boolean deleteSco(Sco sco) throws ScoException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            try {
                boolean returnValue = dbAccess.deleteSco(sco.getID());
                if (returnValue) {
                    MapperCreator.instance(Sco.class).removeObject(sco.getID());

                    /*
                     * Delete the sco in the course, and reset all the
                     * sequencenrs
                     */
                    Sco[] scos = sco.getCourse().getScoList();
                    Sco[] tmp = new Sco[scos.length - 1];
                    int div = 0;
                    for (int i = 0; i < scos.length; i++) {
                        if (scos[i] != sco) {
                            if (scos[i].getSequencenr() > sco.getSequencenr()) {
                                scos[i]
                                        .setSequencenr(scos[i].getSequencenr() - 1);
                                scos[i].setCourseChanged(false);
                            }
                            tmp[i + div] = scos[i];
                        } else {
                            div--;
                        }
                    }
                    sco.getCourse().setScoList(tmp);
                }
                return returnValue;
            } catch (IOException e) {
                throw new ScoException(CourseException.EX_IO);
            } catch (XmlRpcException e) {
                if (e.code != 0) {
                    throw (ScoException) getException(e, e.code);
                } else {
                    throw new ScoException(ScoException.EX_XML_RPC);
                }
            } catch (SQLException e) {
                throw new ScoException(ScoException.EX_DB);
            } catch (DwoXmlRpcException e) {
                throw (ScoException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
            throw new ScoException(ScoException.EX_UNKNOWN_ERROR);
        }
    }

    public User login(String username) throws LoginException
    {
        return login_intern(username, "");
    }

	public boolean deleteSchool(School sc) throws SchoolException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
        	boolean result = dbAccess.deleteSchool(sc.getSchoolID());
        	if(result)
        	{
        		// remove caching van school,
        		// TODO classes, users, courses, scos
                MapperCreator.instance(School.class).removeObject(sc.getSchoolID());
        	}
        	return result;
        } catch(IOException ioe) {
ioe.printStackTrace();
        	throw new SchoolException(SchoolException.EX_IO);
        } catch (SQLException e) {
e.printStackTrace();
			throw new SchoolException(SchoolException.EX_DB);
		} catch (XmlRpcException e) {
e.printStackTrace();
			if(e.code == 0)
				throw new SchoolException(SchoolException.EX_XML_RPC);
			try {
				throw (SchoolException) getException(e, e.code);
			} catch (PersistenceException e1) {
e1.printStackTrace();
				throw new SchoolException(SchoolException.EX_UNKNOWN_ERROR);
			}
		}
	}

	public Hashtable getFidentitySchools() {
		DbAccessIF dbaccess = DbAccessCreator.instance();
		try {
			return dbaccess.getFidentitySchools();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlRpcException e) {
			if(e.code == SchoolException.SE_SCHOOL_UNSUPPORTED && e.getMessage().equals(SchoolException.class.getName()))
				return null;
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (DwoXmlRpcException e) {
			return null;
		}
		return new Hashtable();
	}

	public Vector getUserResults(Course[] courses, User user) throws PersistenceException {
        Vector courseIDs = new Vector();

        for (int i = 0; i < courses.length; i++) {
            courseIDs.addElement(new Integer(courses[i].getID()));
        }
        try {
            Vector v = DbAccessCreator.instance().getUserResults(courseIDs,
                    user.getUserID());
            MapperIF mapper = MapperCreator.instance(UserResultList.class);
            Object[] oa = mapper.getObjectFromReturn(v);
            if (oa.length > 0) {
                return (Vector) (oa[0]);
            } else {
                return new Vector();
            }
        } catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO, e);
        } catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC,e);
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB,e);
        }
	}
	/**
	 * 
	 * @param school
	 * @throws PersistenceException 
	 */
	public void updateSchool(School school) throws PersistenceException {
			int schoolID = school.getSchoolID();
			boolean export = school.isExport();
			try {
				DbAccessCreator.instance().editSchool(schoolID, export);
			} catch (IOException e) {
				throw new PersistenceException(PersistenceException.EX_IO);
			} catch (XmlRpcException e) {
				e.printStackTrace();
				throw new PersistenceException(PersistenceException.EX_XML_RPC);
			} catch (SQLException e) {
				throw new PersistenceException(PersistenceException.EX_DB);
			}
		
	}

	public Course[] getImportCourses(School s, School school, int profileID) throws PersistenceException {
        try {
            Vector v;
            v = DbAccessCreator.instance().getImportCourses(s.getSchoolID(), school.getSchoolID(), profileID);
            MapperIF mapper = MapperCreator.instance(Course.class);
            return (Course[]) mapper.getObjectFromReturn(v);
        } catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO);
        } catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC);
        } catch (SQLException e) {
        	e.printStackTrace();
            throw new PersistenceException(PersistenceException.EX_DB);
        }

	}

	public School editSchool(int schoolID, String schoolName,
			String schoolLogin, Hashtable passwd) throws SchoolException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        try {
            try {
                MapperIF mapper = MapperCreator.instance(School.class);
                Hashtable result = dbAccess.editSchool(schoolID,schoolName, schoolLogin, passwd);
                return (School) mapper.getObjectFromReturn(result);
            } catch (IOException e) { System.out.println(e.toString());
                throw new SchoolException(SchoolException.EX_IO);
            } catch (XmlRpcException e) {
                if (e.code != 0) {
                    throw (SchoolException) getException(e, e.code);
                } else {
                    throw new SchoolException(SchoolException.EX_XML_RPC);
                }
            } catch (SQLException e) { System.out.println(e.toString());
                throw new SchoolException(SchoolException.EX_DB);
            } catch (DwoXmlRpcException e) {
                throw (SchoolException) getException(e, e.code);
            }
        } catch (PersistenceException e) {
            throw new SchoolException(SchoolException.EX_UNKNOWN_ERROR);
        }
	}

	public boolean deleteUserFromSchool(User u) {
		if(u.getSchool()!= null)
			try {
				boolean result =  DbAccessCreator.instance().deleteUserFromSchool(u.getID(), u.getSchool().getSchoolID());
				MapperCreator.instance(User.class).removeObject(u.getID());
				return result;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlRpcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return false;
	}
	
	public boolean updateSchoolTo(School from, School[] to)
	{
		int schoolID = from.getSchoolID();
		Vector schoolTo = new Vector(to.length);
		for (int i = 0; i < to.length; i++) {
			schoolTo.add(new Integer(to[i].getSchoolID()));
		}
		try {
			return DbAccessCreator.instance().updateSchoolTo(schoolID, schoolTo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteCourseClassData(Course course, SchoolClass sc) {
		int courseID = course.getID();
		int classID = sc.getID();
		try {
			return DbAccessCreator.instance().deleteCourseDataFromClass(courseID, classID);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}

	public Sco[] getEditableScos(School school, DwoProfile profile) {
		try {
			return (Sco[]) MapperCreator.instance(Sco.class).get( new Object[] { school, profile } );
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return EMPTY_SCOS;
	}

	public CourseSequence[] getCourseSequence(User currentUser) {
		School school = currentUser.getSchool();
		SchoolClass inClass = currentUser.getInClass();
		MapperIF instance = MapperCreator.instance(CourseSequence.class);
		try {
			if(inClass != null)
			{
				return (CourseSequence[]) instance.get(inClass);
			} 
			return (CourseSequence[]) instance.get(school);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new CourseSequence[0];
		} 
	}
	
	public CourseSequence[] getCourseSequence(SchoolClass forClass)
	{
		try {
			MapperIF instance = MapperCreator.instance(CourseSequence.class);
			return (CourseSequence[]) instance.get(forClass);
		} catch (Exception e) {
			return new CourseSequence[0];
		}		
	}
	
	public void setCourseSequence(Course[] courses, School school, SchoolClass forClass) throws PersistenceException
	{
		if(courses.length==0)
			return;
		Vector vector = new Vector(courses.length);
		for (int i = 0; i < courses.length; i++) {
			vector.add(new Integer(courses[i].getID()));
		}
		int schoolID = 0;
		int classID = 0;
		DbAccessIF access = DbAccessCreator.instance();
		
		int parent = 0;
		if(forClass == null)  // selected courses are flat
		{
			parent = courses[0].getParentID();
			if(parent != courses[courses.length-1].getParentID())
				try {
					access.log("Sequence error " + school);
				} catch (Exception e) {
				}
		
		}
		int profileID = ((DwoIF)DwoHelper.getApplet()).getDwoProfile().getID();
		if(school != null) schoolID = school.getSchoolID();
		if(forClass != null) classID = forClass.getID();
		MapperIF instance = MapperCreator.instance(CourseSequence.class);
		instance.removeAllObjects();
		try {
			access.setCourseSequence(vector, schoolID, classID, parent, profileID);
		} catch (Exception e) {
			throw new PersistenceException(PersistenceException.EX_DB, e);
		}
		
	}

	public Course addCourse(School s, String name, String description,
			DwoProfile profile) throws CourseException {
		return addCourse(s, name, description, profile, null, false);
	}

	public Course[] sequence(Course[] courses)
	{
	   	if(!DWO.SEQUENCE)
			return courses;
	   	return sequence(courses, User.getCurrentUser());
	}
	
    public Course[] sequence(Course[] courses, User currentUser) {
 		CourseSequence[] css = getCourseSequence(currentUser);
		return sequence(courses, css);
	}

	public Course[] sequence(Course[] courses, CourseSequence[] css) {
		int start = 0;
		if(css != null)
		for(int i = 0; i < css.length; i++)
		{
			int c = css[i].getCourseID();
			for(int j = start; j < courses.length; j++)
				if(courses[j].getID() == c)
				{
					Course tmp = courses[start];
					courses[start] = courses[j];
					courses[j] = tmp;
					start++;
					break;
				}
		}
		return courses;
	}
	
}