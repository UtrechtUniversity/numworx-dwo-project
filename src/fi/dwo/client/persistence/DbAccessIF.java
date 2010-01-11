// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\DbAccessIF.java

package fi.dwo.client.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.server.persistence.DwoXmlRpcException;

/**
 * The interface between the client and the server.
 * @author M.J.B. Kupers
 * @xmlrpc.generate
 */
public interface DbAccessIF {
    
    public static String ERROR_CLASS = "__ERROR_CLASS__";
    public static String ERROR_CODE = "__ERROR_CODE__";
    
    /**
     * Returns all the courses for the class with the specified classID
     * @param classID The classID of the class to return the courses of.
     * @return The courses for the class with the specified classID
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    public Vector getCoursesForClass(int classID) throws IOException, XmlRpcException, SQLException;
    
    /**
     * Select a course for the specified class.
     * @param classID The class wherefor the courses must be selected.
     * @param courseID The course to select.
     * @return If true, te course was successfully connected to the class.
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    public boolean selectCoursesForClass(int classID, int courseID) throws IOException, XmlRpcException, SQLException;
    
    /**
     * Deselect a course for the specified class.
     * @param classID The class wherefor the courses must be deselected.
     * @param courseID The course to deselect.
     * @return If true, the course was successfully disconnected from the class.
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    public boolean deSelectCoursesForClass(int classID, int courseID) throws IOException, XmlRpcException, SQLException;
    ////peter

    /**
     * Returns all the available courses for the specified user.
     * @param userID The users wherefor the courses must selected.
     * @return A Vector containing hashtables with the coursedata.
     */
    public Vector getCourses(int userID) throws IOException, XmlRpcException, SQLException;

    /**
     * Returns all the available course for the specified user.
     * If showAll is <code>false</code> the invisible courses are not returned.
     * The invisible courses are the courses who the column <code>notVisible</code> in the database is true.
     * @param userID The usere wherefor the courses must selected.
     * @param showAll If <code>false</code> the invisible courses won't be returned.
     * @return All the (visible) courses for the specified user.
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    public Vector getCourses(int userID, boolean showAll) throws IOException, XmlRpcException, SQLException;

    /**
     * Returns all the courses which can be edited by a admin.
     * @return All the courses that are not from a school.
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    public Vector getEditableCoursesAdmin() throws IOException, XmlRpcException, SQLException;

	/**
     * Returns all the courses which can be edited by a teacher of the specified school.
     * @param schoolID The school wherefrom the courses must be selected.
     * @return All the editable courses for the specified school.
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    public Vector getEditableCourses(int schoolID) throws IOException, XmlRpcException, SQLException;

    /**
     * Returns one single record out the database, from the specified table, and the specified ID.
     * @param tableName The name of the table to select the record.
     * @param idCol The name of the column with the ID value.
     * @param oid The ID value.
     * @return A Hashtable where the column-values are mapped on the columnnames.

     */
    public Hashtable getRecord(String tableName, String idCol, int oid)
            throws IOException, XmlRpcException, SQLException;

    /**
     * Returns all the records of the specified table.
     * @param tableName The name of the table to select the data.
     * @return A vector contains hashtables where the column-values are mapped on the columnnames.
     */
    public Vector getTable(String tableName) throws IOException,
            XmlRpcException, SQLException;

    /**
     * Returns all the records of the specified table ordered by the specified column.
     * @param tableName The name of the table to select the data.
     * @param orderCol The column name to order the data by.
     * @return A Vector contains hashtables where the column-values are mapped on the columnnames.
     */
    public Vector getTable(String tableName, String orderCol) throws IOException,
            XmlRpcException, SQLException;

    /**
     * Returns all the records of the specified table with the specified restrictions.
     * @param tableName The name of the table to select the data.
     * @param wheredef A hashtable containing the restrictions. Every value is mapped on the columnname.
     * @return A Vector contains hashtables where the column-values are mapped on the columnames.

     */
    public Vector getTable(String tableName, Hashtable wheredef) throws IOException,
            XmlRpcException, SQLException;

    /**
     * Returns all the records of the specified table with the specified restrictions ordered by the specified column.
     * @param tableName The name of the table to select the data.
     * @param wheredef A hashtable containing the restrictions. Every value is mapped on the columnname.
     * @param orderBy The column name to order the data by.
     * @return java.util.Vector A Vector containing hashtables where the column-values are mapped on the columnames.
     */
    public Vector getTable(String tableName, Hashtable wheredef, String orderBy) throws IOException,
            XmlRpcException, SQLException;

    /**
     * Returns specified records of the specified table with the specified restrictions ordered by the specified column.
     * @param tableName The name of the table to select the data.
     * @param columnnames A Vector of columnnames.
     * @param wheredef A hashtable containing the restrictions. Every value is mapped on the columnname.
     * @param orderBy The column name to order the data by.
     * @return java.util.Vector A Vector containing hashtables where the column-values are mapped on the columnames.
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    public Vector getTable(String tableName, Vector columnnames, Hashtable wheredef, String orderBy) throws IOException,
    		XmlRpcException, SQLException;
    public boolean renameClass(int classID, String newName) throws DwoXmlRpcException, IOException, XmlRpcException,
    SQLException;

    /**
     * @param username
     * @param password
     * @param firstname
     * @param middlename
     * @param lastname
     * @param email
     * @return boolean
     * @throws fi.dwo.client.system.RegisterException

     */
    public boolean register(String username, String password, String firstname,
            String middlename, String lastname, String email)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException;

    public String LMSGetValue(int scoID, int userID, String iDataModelElement) throws IOException, XmlRpcException,
    SQLException;

    public String LMSSetValue(int scoID, int userID, String iDataModelElement, String iValue) throws IOException, XmlRpcException,
    SQLException;

    public String LMSSetValue(int scoID, int userID, String iDataModelElement, String iValue, String random) throws IOException, XmlRpcException,
    SQLException;

        /**
     * @param username
     * @param password
     * @param firstname
     * @param middlename
     * @param lastname
     * @param email
     * @param schoolLogin
     * @param groupID
     * @param groupPassword
     * @return boolean
     * @throws fi.dwo.client.system.RegisterException

     */
    public boolean register(String username, String password, String firstname,
            String middlename, String lastname, String email,
            String schoolLogin, int groupID, String groupPassword)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException;

    /**
     * @param username
     * @param password
     * @return java.util.Hashtable

     */
    public Hashtable login(String username, String password)
            throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;

    /**
     * @param userID
     * @param schoolLogin
     * @param groupID
     * @param groupPassword
     * @return java.util.Hashtable
     * @throws fi.dwo.client.system.RegisterException

     */
    public Hashtable addToSchool(int userID, String schoolLogin, int groupID,
            String groupPassword) throws DwoXmlRpcException, IOException,
            XmlRpcException, SQLException;

    /**
     * @param userID
     * @param password
     * @param firstname
     * @param middlename
     * @param lastname
     * @param email
     * @param classID
     * @return boolean
     * @throws fi.dwo.client.system.RegisterException

     */
    public boolean changeAccount(int userID, String password,
            String newPassword, String firstname, String middlename,
            String lastname, String email, int classID)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException;

    /**
     * @param userID
     * @param password
     * @param firstname
     * @param middlename
     * @param lastname
     * @param email
     * @return boolean
     * @throws fi.dwo.client.system.RegisterException

     */
    public boolean changeAccount(int userID, String password,
            String newPassword, String firstname, String middlename,
            String lastname, String email) throws DwoXmlRpcException,
            IOException, XmlRpcException, SQLException;

    /**
     * @param teacher
     * @param className
     * @return java.util.Hashtable
     * @throws fi.dwo.client.system.ClassException

     */
    public Hashtable addClass(int teacher, String className)
            throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;

	/**
     * @param schoolName
     * @param schoolLogin
     * @param studentPassw
     * @param teacherPassw 
     * @return java.util.Hashtable
     * @throws fi.dwo.client.system.ClassException

     */
    public Hashtable addSchool(String schoolName, String schoolLogin, String studentPassw, String teacherPassw)
            throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;

	/**
	 * @param schoolId
     * @param schoolName
     * @param schoolLogin
     * @param studentPassw
     * @param teacherPassw 
     * @return java.util.Hashtable
     * @throws fi.dwo.client.system.ClassException

     */
    public Hashtable addSchool(int schoolId, String schoolName, String schoolLogin, String studentPassw, String teacherPassw)
            throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;

	/**
     * @param schoolID
     * @param schoolName
     * @param schoolLogin
     * @param studentPassw
     * @param teacherPassw 
     * @return java.util.Hashtable
     * @throws fi.dwo.client.system.ClassException

     */
    public Hashtable editSchool(int schoolID, String schoolName, String schoolLogin, String studentPassw, String teacherPassw)
            throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;


    /**
     * @param userID

     */
    public boolean deleteUser(int userID) throws IOException, XmlRpcException,
            SQLException;

    /**
     * if mustEmpty is true, and the class contains students, this function
     * returns false. Otherwise it returns true
     * 
     * @param classID
     * @param mustEmpty
     * @return boolean

     */
    public boolean deleteClass(int classID, boolean mustEmpty)
            throws IOException, XmlRpcException, SQLException;


    public Vector getResults(Vector courses, int userID) throws IOException, XmlRpcException, SQLException;
    public Vector getResults(Vector courses, int classID, int userID) throws IOException, XmlRpcException, SQLException;
    public Vector getResults(int courseID, int classID, int userID) throws IOException, XmlRpcException, SQLException;
    public Vector getResults(int courseID, int userID) throws IOException, XmlRpcException, SQLException;
    
    /**
     * @param uid

     */
    public boolean disconnectFromClass(int uid) throws IOException,
            XmlRpcException, SQLException;
    
    /**
     * Select a jar on the serverside.
     * For the problem to dynamically load JAR's we must pre-set the JAR to select.
     * After the JAR is selected, the class can be loaded out of the jar and the default classloader (in the browser)
     * will do a get request ot the servler for a jar. Then, the correct jar is returned.
     * @param key A key-value which can used to read the jar out of the database.
     * @param jar The name of the jar to select.
     * @return
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     * @see <a href="doc-files/DbAccessIF-1.pdf">Dynamisch inladen van JAR's (dutch)</a>
     * @deprecated niet meer gebruiken
     */
    public boolean selectJar(String key, String jar) throws IOException,
    XmlRpcException, SQLException;
    
    public boolean reconnect() throws IOException, XmlRpcException, SQLException;
    
    /**
     * Logs a string to the error-log at the server (just for debugging perpose).
     * @param s The string to log.
     * @return
     * @throws IOException
     * @throws XmlRpcException
     */
    public boolean log(String s) throws IOException, XmlRpcException;
    
    public int addCourse(int schoolID, String name, String description, int dwoProfile) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;

    public boolean changeCourse(int courseID, String name, String description)  throws  DwoXmlRpcException, IOException, XmlRpcException, SQLException;

    public boolean deleteCourse(int courseID)  throws  DwoXmlRpcException, IOException, XmlRpcException, SQLException;

    public int addSco(int courseID, String name, String description, int appletConfigID, int sequencenr) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;

    public int addSco(int courseID, String name, String description,
			int appletID, String launchdata, int sequencenr)
			throws IOException, XmlRpcException, SQLException, DwoXmlRpcException;

    public boolean changeSco(int scoID, String name, String description, String launchdata)  throws  DwoXmlRpcException, IOException, XmlRpcException, SQLException;

    public boolean changeScoSequenceNr(int scoID, int sequencenr, int scoID2, int sequencenr2) throws SQLException,DwoXmlRpcException, IOException, XmlRpcException; 

    public boolean deleteSco(int scoID)  throws  DwoXmlRpcException, IOException, XmlRpcException, SQLException;

    public boolean deleteSchool(int schoolID)  throws IOException, XmlRpcException, SQLException;

    
    public Hashtable getFidentitySchools() throws IOException, XmlRpcException, SQLException, DwoXmlRpcException;

	public Vector getUserResults(int courseID, int userID) throws IOException, XmlRpcException, SQLException;

	public Vector getUserResults(Vector courses, int i) throws SQLException, IOException, XmlRpcException;

	public boolean setLogo(int id, byte[] image) throws SQLException, IOException, XmlRpcException;

	/**
	 * update course met name, description en export.
	 * @param id
	 * @param name
	 * @param description
	 * @param export
	 * @return
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	public boolean changeCourse(int id, String name, String description,
			boolean export) throws IOException, XmlRpcException, SQLException, DwoXmlRpcException;

	public boolean editSchool(int schoolID, boolean export) throws IOException, XmlRpcException, SQLException;

	public Vector getImportCourses(int schoolFrom, int schoolTo, int profileID) throws IOException, XmlRpcException, SQLException;

	public Hashtable editSchool(int schoolID, String schoolName, String schoolLogin, Hashtable passwd) 
	throws IOException, XmlRpcException, SQLException, DwoXmlRpcException ;

    public Hashtable addSchool(int schoolId, String schoolName, String schoolLogin, Hashtable passwd)
    throws DwoXmlRpcException, IOException, XmlRpcException, SQLException, DwoXmlRpcException ;

    public boolean deleteUserFromSchool(int id, int schoolID)
    throws IOException, XmlRpcException, SQLException;
    
    public boolean updateSchoolTo(int schoolID, Vector schoolTo)
    throws IOException, XmlRpcException, SQLException;
    
}