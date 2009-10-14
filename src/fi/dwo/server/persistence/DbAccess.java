// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\server\\persistence\\DbAccess.java

package fi.dwo.server.persistence;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.beans.jdbc.DbConnect;
import fi.dwo.client.persistence.DbAccessIF;

public class DbAccess extends DbConnect implements DbAccessIF {
    private final static String QRY_DEFAULT_SELECT_ID = "SELECT * "
            + "FROM `{0}` " + "WHERE `{1}` = ?";

    private final static String QRY_DEFAULT_SELECT_TABLE = "SELECT * "
            + "FROM `{0}` ";

    private final static String QRY_DEFAULT_SELECT_TABLE_ORDER = "SELECT * "
        + "FROM `{0}` "
        + "ORDER BY `{1}` ";

    private final static String QRY_DEFAULT_SELECT_TABLE_WHERE = "SELECT * "
            + "FROM `{0}` " + "WHERE (1=1) ";

    private final static String QRY_SELECT_COURSES = "SELECT tblCourse.* "
            + "FROM tblUser LEFT JOIN tblSchoolGroup ON tblUser.schoolGroupID = tblSchoolGroup.schoolGroupID, tblCourse "
            + "WHERE ((tblSchoolGroup.schoolID = tblCourse.schoolID) "
            + "OR     (isnull(tblCourse.schoolID))) " + "AND   (userID = ?) "
            + "AND   (notVisible <= ?) " + "ORDER BY name ";

    private final static String QRY_SELECT_COURSES_GUEST = "SELECT tblCourse.* "
            + "FROM tblCourse "
            + "WHERE (isnull(tblCourse.schoolID)) "
            + "ORDER BY name ";

    ////peter
    private final static String QRY_SELECT_COURSES_CLASS = "SELECT tblCourse.* "
            + "FROM tblCourse,tblClassCourse "
            + "WHERE (tblCourse.CourseID = tblClassCourse.CourseID) "
            + "AND (tblClassCourse.ClassID = ?) " + "ORDER BY name ";

    private final static String QRY_INSERT_CLASS_COURSE = "INSERT INTO tblClassCourse(classID, courseID) "
            + "VALUES(?, ?) ";

    private final static String QRY_DELETE_CLASS_COURSE = "DELETE FROM tblClassCourse "
            + "WHERE (classID = ?) " + "AND (courseID = ?) ";

	private final static String QRY_SELECT_COURSES_EDITABLE_ADMIN = "SELECT tblCourse.* "
            + "FROM tblCourse "
            + "WHERE (isnull(tblCourse.schoolID)) "
            + "ORDER BY name ";
    ////peter

    private final static String QRY_SELECT_COURSES_EDITABLE = "SELECT tblCourse.* "
            + "FROM tblCourse "
            + "WHERE (tblCourse.schoolID = ?) "
            + "ORDER BY name ";

    private final static String QRY_GET_STUDENT_SCO = "SELECT `{0}` "
            + "FROM tblStudentSco " + "WHERE (scoID = ?) "
            + "AND   (userID = ?) ";

    private final static String QRY_ADD_EMPTY_STUDENT_SCO = "INSERT INTO tblStudentSco(scoID, userID, createDate, score) "
            + "VALUES(?, ?, CURDATE(), 0) ";

    private final static String QRY_UPDATE_STUDENT_SCO = "UPDATE tblStudentSco "
            + "SET `{0}` = ?, createDate = CURDATE() "
            + "WHERE (scoID = ?) "
            + "AND   (userID = ?) ";

    private final static String QRY_WHERE_COLUMN = "AND (`{0}` = ?) ";

    private final static String QRY_CHECK_USERNAME_EXISTS = "SELECT userID "
            + "FROM tblUser " + "WHERE (username = ?)";

	private final static String QRY_CHECK_SCHOOLLOGIN_EXISTS = "SELECT schoolID "
            + "FROM tblSchool " + "WHERE (schoollogin = ?)";
            
    private final static String QRY_CHECK_SCHOOLGROUP_EXISTS = "SELECT * "
            + "FROM tblSchoolGroup "
            + "WHERE (schoolID = ?) "
            + "AND   (groupID = ?) ";

    private final static String QRY_INSERT_USER = "INSERT INTO tblUser(firstname, middlename, lastname, username, passwd, email, registerDate) "
            + "VALUES (?, ?, ?, ?, ?, ?, CURDATE())";
            
    private final static String QRY_INSERT_SCHOOLGROUP = "INSERT INTO tblSchoolGroup(groupID, schoolID, passwd) "
            + "VALUES (?, ?, ?)";

    private final static String QRY_CHECK_SCHOOLGROUP = "SELECT schoolGroupID "
            + "FROM tblSchoolGroup, tblSchool "
            + "WHERE (tblSchoolGroup.schoolID = tblSchool.schoolID) "
            + "AND   (schoollogin = ?) " + "AND   (groupID = ?) "
            + "AND   (passwd = ?) ";

    private final static String QRY_INSERT_USER_SCHOOL = "INSERT INTO tblUser(schoolGroupID, firstname, middlename, lastname, username, passwd, email, registerDate) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE())";

    private final static String QRY_LOGIN = "SELECT * "
            + "FROM tblUser LEFT JOIN tblClass ON tblUser.classID = tblClass.classID "
            + "WHERE (username = ?) " + "AND   (passwd = ?) ";
    private final static String QRY_LOGIN_NO_PASSWD = "SELECT * "
        + "FROM tblUser LEFT JOIN tblClass ON tblUser.classID = tblClass.classID "
        + "WHERE (username = ?) ";

    private final static String QRY_GET_USER_DATA = "SELECT tblUser.*, tblGroup.*, tblSchool.schoolID, tblSchool.schoolName, tblSchool.schoollogin, tblSchool.image "
            + "FROM tblUser, tblSchoolGroup, tblGroup, tblSchool "
            + "WHERE (tblUser.schoolGroupID = tblSchoolGroup.schoolGroupID) "
            + "AND   (tblSchoolGroup.groupID = tblGroup.groupID) "
            + "AND   (tblSchoolGroup.schoolID = tblSchool.schoolID) "
            + "AND   (userID = ?) ";

    protected final static String QRY_ADD_TO_SCHOOL = "UPDATE tblUser "
            + "SET schoolGroupID = ? " + "WHERE (userID = ?) ";

    protected final static String QRY_SELECT_SCHOOL_USER = "SELECT tblSchool.* "
            + "FROM tblUser, tblSchoolGroup, tblSchool "
            + "WHERE (tblUser.schoolGroupID = tblSchoolGroup.schoolGroupID) "
            + "AND   (tblSchoolGroup.schoolID = tblSchool.schoolID) "
            + "AND   (tblUser.userID = ?) ";

    private final static String QRY_PASSWORD_CORRECT = "SELECT userID "
            + "FROM tblUser " + "WHERE (userID = ?) " + "AND   (passwd = ?) ";

    private final static String QRY_UPDATE_USER_CLASS = "UPDATE tblUser "
            + "SET classID = ? " + "WHERE (userID = ?) ";

    private final static String QRY_UPDATE_USER = "UPDATE tblUser "
            + "SET firstname = ?, " + "middlename = ?, " + "lastname = ?, "
            + "passwd = ?, " + "email = ? " + "WHERE (userID = ?)";

    private final static String QRY_UPDATE_USER_LAST_LOGIN = "UPDATE tblUser "
            + "SET lastLogin = CURDATE() " + "WHERE (tblUser.userID = ?) ";

    private final static String QRY_UPDATE_USER_NO_PWD = "UPDATE tblUser "
            + "SET firstname = ?, " + "middlename = ?, " + "lastname = ?, "
            + "email = ? " + "WHERE (userID = ?)";

    private final static String QRY_ADD_CLASS = "INSERT INTO tblClass(userID, schoolID, class) "
            + "VALUES(?, ?, ?) ";
            
    private final static String QRY_ADD_SCHOOLID = "INSERT INTO tblSchool(schoolName, schoollogin, schoolID) "
            + "VALUES(?, ?, ?) ";
            
    private final static String QRY_SELECT_SCHOOL_FROM_SCHOOLLOGIN = "SELECT schoolID "
            + "FROM tblSchool "
            + "WHERE schoollogin = ?";

    private final static String QRY_DELETE_DEFAULT = "DELETE FROM `{0}` "
            + "WHERE `{1}` = ?";

    private final static String QRY_DELETE_STUDENTS_FROM_CLASS = "UPDATE tblUser "
            + "SET classID = null " + "WHERE (classID = ?) ";

    private final static String QRY_SELECT_SCHOOL_FROM_USER = "SELECT schoolID "
            + "FROM tblUser, tblSchoolGroup "
            + "WHERE (tblUser.schoolGroupID = tblSchoolGroup.schoolGroupID) "
            + "AND   (tblUser.userID = ?) ";

    private final static String QRY_SELECT_SCHOOL_CLASS = "SELECT classID "
            + "FROM tblClass " + "WHERE (schoolID = ?) " + "AND   (class = ?) ";

    private final static String QRY_DISCONNECT_USER_CLASS = "UPDATE tblUser "
            + "SET classID = null " + "WHERE userID = ? ";

    private final static String QRY_RESULTS_ALL = "SELECT tblClass.classID, tblCourse.courseID, avg(score) as score, count(score) as totaal "
            + "FROM tblClass right join tblUser on tblClass.classID = tblUser.classID "
            + "left join tblStudentSco on tblStudentSco.userID = tblUser.userID "
            + "right join tblSco on tblStudentSco.scoID = tblSco.scoID "
            + "left join tblCourse on tblSco.courseID = tblCourse.courseID "
            + "where (tblCourse.courseID in ({0})) "
            + "and   (tblClass.userID = ?) "
            + "group by tblClass.classID, tblCourse.courseID "
            + "having tblClass.classID is not null "
            + "ORDER BY tblClass.classID";

    /**
     * results of selected courses from a single user.
     */
    private final static String QRY_RESULTS_SINGLE = "SELECT tblUser.userID, tblCourse.courseID, avg(score) as score, count(score) as totaal "
        + "FROM tblUser  "
        + "left join tblStudentSco on tblStudentSco.userID = tblUser.userID "
        + "right join tblSco on tblStudentSco.scoID = tblSco.scoID "
        + "left join tblCourse on tblSco.courseID = tblCourse.courseID "
        + "where (tblCourse.courseID in ({0})) "
        + "and   (tblUser.userID = ?) "
        + "group by tblCourse.courseID ";

    private final static String QRY_RESULTS_CLASS = "SELECT tblUser.userID, tblCourse.courseID, avg(score) as score, count(score) as totaal "
            + "FROM tblClass right join tblUser on tblClass.classID = tblUser.classID "
            + "left join tblStudentSco on tblStudentSco.userID = tblUser.userID "
            + "right join tblSco on tblStudentSco.scoID = tblSco.scoID "
            + "left join tblCourse on tblSco.courseID = tblCourse.courseID "
            + "where (tblClass.classID = ?) "
            + "and (tblCourse.courseID in ({0})) "
            + "and   (tblClass.userID = ?) "
            + "group by tblUser.userID, tblCourse.courseID "
            + "ORDER BY tblUser.userID";

    private final static String QRY_RESULTS_STUDENT_COURSE = "SELECT tblUser.userID, tblSco.scoID, tblSco.sequencenr,  if(score=0,-1,score) as score, total_time "
            + "FROM tblClass right join tblUser on tblClass.classID = tblUser.classID "
            + "left join tblStudentSco on tblStudentSco.userID = tblUser.userID "
            + "right join tblSco on tblStudentSco.scoID = tblSco.scoID "
            + "left join tblCourse on tblSco.courseID = tblCourse.courseID "
            + "where (tblClass.classID = ?) "
            + "and (tblCourse.courseID = ?) "
            + "and   (tblClass.userID = ?) "
            + "group by tblUser.userID, tblSco.scoID, tblSco.sequencenr "
            + "ORDER BY tblUser.userID, tblSco.sequencenr";

    /**
     * Select the SCO scores of one student.
     */
    private final static String QRY_RESULTS_SINGLE_STUDENT_COURSE = "SELECT tblUser.userID, tblSco.scoID, tblSco.sequencenr,  if(score=0,-1,score) as score, total_time "
        + "FROM tblUser "
        + "left join tblStudentSco on tblStudentSco.userID = tblUser.userID "
        + "right join tblSco on tblStudentSco.scoID = tblSco.scoID "
        + "left join tblCourse on tblSco.courseID = tblCourse.courseID "
        + "where (tblUser.userID = ?) "
        + "and (tblCourse.courseID = ?) "
        + "group by tblUser.userID, tblSco.scoID, tblSco.sequencenr "
        + "ORDER BY tblUser.userID, tblSco.sequencenr";

    private final static String QRY_RESULTS_COURSE = "SELECT tblClass.classID, tblSco.scoID, tblSco.sequencenr, avg(score) as score, count(score) as totaal "
            + "FROM tblClass right join tblUser on tblClass.classID = tblUser.classID "
            + "left join tblStudentSco on tblStudentSco.userID = tblUser.userID "
            + "right join tblSco on tblStudentSco.scoID = tblSco.scoID "
            + "left join tblCourse on tblSco.courseID = tblCourse.courseID "
            + "where  (tblCourse.courseID = ?) "
            + "and   (tblClass.userID = ?) "
            + "group by tblClass.classID, tblSco.scoID, tblSco.sequencenr "
            + "having tblClass.classID is not null "
            + "ORDER BY tblClass.classID, tblSco.sequencenr";

    private final static String QRY_UPDATE_CLASS_NAME = "UPDATE tblClass "
            + "SET class = ? " + "WHERE (classID = ?) ";
            
    private final static String QRY_UPDATE_SCHOOL = "UPDATE tblSchool "
            + "SET schoolName = ?, " + "schoollogin = ? "  + "WHERE (schoolID = ?) ";

	private final static String QRY_UPDATE_SCHOOLGROUP_PASSW = "UPDATE tblSchoolGroup "
            + "SET passwd = ? " + "WHERE (schoolGroupID = ?) ";
            
    private final static String QRY_JAR_INSERT_KEY = "INSERT INTO tblJars(`key`, `jarname`, `lastDate`) "
            + "VALUES(?, ?, CURDATE()) ";

    private final static String QRY_JAR_UPDATE_KEY = "UPDATE tblJars "
            + "SET `jarname` = ?, lastDate = CURDATE() " + "WHERE `key` = ?";

    private final static String QRY_JAR_SELECT_KEY = "SELECT jarname FROM tblJars where `key` = ?";

    private final static String QRY_JAR_COUNT_JARS = "SELECT count(*) as number FROM tblApplet ";

    private final static String QRY_ADD_COURSE = "INSERT INTO tblCourse(schoolID, name, description, image, dwoProfileID) "
            + "VALUES(?, ?, ?, ?, ?) ";
            
    private final static String QRY_ADD_COURSE_BASIC = "INSERT INTO tblCourse(name, description, image, dwoProfileID) "
            + "VALUES(?, ?, ?, ?) ";

    private final static String QRY_UPDATE_COURSE = "UPDATE tblCourse "
            + "SET name = ?, " + "description = ? " + "WHERE (courseID = ?) ";

    private final static String QRY_ADD_SCO = "INSERT INTO tblSco(courseID, appletID, sconame, description, launchdata, sequencenr) "
            + "VALUES(?, ?, ?, ?, ?, ?) ";

    private final static String QRY_UPDATE_SCO = "UPDATE tblSco "
            + "SET sconame = ?, " 
            + "description = ?, " 
            + "launchdata = ? "
            + "WHERE (scoID = ?) ";

    private final static String QRY_UPDATE_SCO_SEQUENCE = "UPDATE tblSco "
            + "SET sequencenr = sequencenr - 1 " + "WHERE (sequencenr > ?) "
            + "AND   (courseid = ?) ";

// TODO false bij een export.    
    static public final boolean DEBUG = false;
    /**
     */
    public DbAccess() {
        super(MYSQL, (DEBUG ? "dwo" : "dwo_tst"), "dwo", "_dwo");
        //        super(MYSQL, "thijsk_tst", "thijsk", "_thijsk");
        if(DEBUG)
        	log("Dbacces DEBUG aan");
    }

    /**
     * @param tableName
     * @param idCol
     * @param oid
     * @return java.util.Hashtable
     */
    public Hashtable getRecord(String tableName, String idCol, int oid)
            throws SQLException {
        String[] arguments = { tableName, idCol };
        String query = MessageFormat.format(QRY_DEFAULT_SELECT_ID, arguments);
        PreparedStatement ps = getStatement(query);
        ps.setInt(1, oid);

        return executeQueryWithRecord(ps);
    }

    /**
     * @param tableName
     *            The tablename of the table to select data.
     * @return java.util.Vector The vector contains Hashtables who contains the
     *         rows mapped on the columnname.
     */
    public Vector getTable(String tableName) throws SQLException {
        String[] arguments = { tableName };
        String query = MessageFormat
                .format(QRY_DEFAULT_SELECT_TABLE, arguments);

        return executeQueryWithResult(query);
    }

    /**
     * @param tableName
     *            The tablename of the table to select data.
     * @return java.util.Vector The vector contains Hashtables who contains the
     *         rows mapped on the columnname.
     */
    public Vector getTable(String tableName, String orderCol) throws SQLException {
        String[] arguments = { tableName, orderCol };
        String query = MessageFormat
                .format(QRY_DEFAULT_SELECT_TABLE_ORDER, arguments);

        log("DbAccess.getTable " + query);
        return executeQueryWithResult(query);
    }
    /**
     * @param tableName
     *            The tablename of the table to select data.
     * @return java.util.Vector The vector contains Hashtables who contains the
     *         rows mapped on the columnname.
     */
    public Vector getTable(String tableName, Hashtable wheredef, String orderBy)
            throws SQLException {
        String[] arguments = { tableName };
        String query = MessageFormat.format(QRY_DEFAULT_SELECT_TABLE_WHERE,
                arguments);

        Enumeration keys = wheredef.keys();
        String where = "";
        String[] items = new String[wheredef.size()];
        int i = 0;
        while (keys.hasMoreElements()) {
            where += QRY_WHERE_COLUMN;
            arguments[0] = (String) keys.nextElement();
            items[i] = arguments[0];
            where = MessageFormat.format(where, arguments);
            i++;
        }

        query += where;

        if ((orderBy != null) && (!orderBy.equals(""))) {
            query += "ORDER BY " + orderBy;

        }

        PreparedStatement ps = getStatement(query);

        for (i = 0; i < items.length; i++) {
            ps.setObject(i + 1, wheredef.get(items[i]));
        }
        return executeQueryWithResult(ps);
    }

    public Vector getTable(String tableName, Vector columnNames, Hashtable wheredef, String orderBy)
    throws SQLException {
    	StringBuffer sb = new StringBuffer("SELECT ");
        String[] items = new String[wheredef.size()];
    	int i = 0;
    	Enumeration names = columnNames.elements();
    	while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			if(i++ != 0) sb.append(" , ");
			sb.append(name);
		}
    	sb.append( " FROM ");
    	sb.append( tableName);
    	
    	
    	Enumeration keys = wheredef.keys();
    	i = 0;
    	while (keys.hasMoreElements()) {
			String item = (String) keys.nextElement();
			if(i != 0)
				sb.append(" AND ");
			else 
				sb.append(" WHERE ");
			items[i++] = item;
				sb.append(item);
				sb.append("= ?");
		}
    	
        if ((orderBy != null) && (!orderBy.equals(""))) {
            sb.append( " ORDER BY ");
            sb.append( orderBy );
        }
    	
    	PreparedStatement ps = getStatement(sb.toString());
        for (i = 0; i < items.length; i++) {
            ps.setObject(i + 1, wheredef.get(items[i]));
        }
        return executeQueryWithResult(ps);
   	
    }
    
    /**
     * Checks if a username already exists in the database
     * 
     * @param username
     *            the username to check.
     * @return If there exists a user with the specified username, true is
     *         returned. Otherwise, false is returned.
     * @throws SQLException
     */
    protected boolean usernameExists(String username) throws SQLException {
        PreparedStatement ps = getStatement(QRY_CHECK_USERNAME_EXISTS);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        boolean usernameExists = !isEmpty(rs);
        rs.close();
        return usernameExists;
    }
    
    /**
     * Checks if a schoolLogin already exists in the database
     * 
     * @param schoolLogin
     *            the schoolLogin to check.
     * @return If there exists a school with the specified schoolLogin, true is
     *         returned. Otherwise, false is returned.
     * @throws SQLException
     */
    private boolean schoolLoginExists(String schoolLogin) throws SQLException {
        PreparedStatement ps = getStatement(QRY_CHECK_SCHOOLLOGIN_EXISTS);
        ps.setString(1, schoolLogin);
        ResultSet rs = ps.executeQuery();
        boolean schoolLoginExists = !isEmpty(rs);
        rs.close();
        return schoolLoginExists;
    }

    protected int schoolGroupExists(String schoollogin, int groupID,
            String password) throws SQLException {
        PreparedStatement ps = getStatement(QRY_CHECK_SCHOOLGROUP);
        ps.setString(1, schoollogin);
        ps.setInt(2, groupID);
        ps.setString(3, password);
        ResultSet rs = ps.executeQuery();
        int schoolGroupID = -1;
        if (!isEmpty(rs)) {
            schoolGroupID = rs.getInt(1);
        }
        rs.close();
        return schoolGroupID;

    }

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
            throws DwoXmlRpcException, SQLException {
        if (usernameExists(username)) {
            throw new DwoXmlRpcException(DwoXmlRpcException.EXC_USER_EXISTS);
        } else {
            PreparedStatement ps = getStatement(QRY_INSERT_USER);
            ps.setString(1, firstname);
            ps.setString(2, middlename);
            ps.setString(3, lastname);
            ps.setString(4, username);
            ps.setString(5, password);
            ps.setString(6, email);

            ps.execute();
            ps.close();
        }
        return true;
    }

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
            throws DwoXmlRpcException, SQLException {
        if (usernameExists(username)) {
            throw new DwoXmlRpcException(DwoXmlRpcException.EXC_USER_EXISTS);
        } else {
            int schoolGroupID = schoolGroupExists(schoolLogin, groupID,
                    groupPassword);
            if (schoolGroupID == -1) {
                throw new DwoXmlRpcException(
                        DwoXmlRpcException.EXC_UNKNOWN_SCHOOLGROUP);
            } else {
                PreparedStatement ps = getStatement(QRY_INSERT_USER_SCHOOL);
                ps.setInt(1, schoolGroupID);
                ps.setString(2, firstname);
                ps.setString(3, middlename);
                ps.setString(4, lastname);
                ps.setString(5, username);
                ps.setString(6, password);
                ps.setString(7, email);

                ps.execute();
                ps.close();
            }
        }
        return true;
    }

    /**
     * @param username
     * @param password
     * @return java.util.Hashtable
     */
    public Hashtable login(String username, String password)
            throws SQLException, DwoXmlRpcException {
        close(); //for lazy connection
        boolean noPw = password .equals("");
        PreparedStatement ps = getStatement( noPw?QRY_LOGIN_NO_PASSWD:QRY_LOGIN);
        ps.setString(1, username);
        if(!noPw) ps.setString(2, password);

        Hashtable result = executeQueryWithRecord(ps);

        if (result == null) {
            throw new DwoXmlRpcException(DwoXmlRpcException.EXC_UNKNOWN_USER);
        } else {
            Object tmp = result.get("schoolGroupID");
            ps.close();

            /* Update the Last Login date */
            ps = getStatement(QRY_UPDATE_USER_LAST_LOGIN);
            ps.setInt(1, ((Integer) result.get("userID")).intValue());
            ps.execute();

            if (!(tmp instanceof String)) { //null-data is an empty string, so
                                            // if this is a string, it was null
                ps.close();

                ps = getStatement(QRY_GET_USER_DATA);
                ps.setInt(1, ((Integer) result.get("userID")).intValue());
                Hashtable result2 = executeQueryWithRecord(ps);
                if (result2 != null) {
                    Object key;
                    for (Enumeration keys = result2.keys(); keys
                            .hasMoreElements();) {
                        key = keys.nextElement();
                        result.put(key, result2.get(key));
                    }

                    result.putAll(result2);
                }
            }
        }

        return result;
    }

    /**
     * @param userID
     * @param password
     * @return java.util.Hashtable
     *  
     */
    protected boolean passwordCorrect(int userID, String password)
            throws SQLException {
        PreparedStatement ps = getStatement(QRY_PASSWORD_CORRECT);
        ps.setInt(1, userID);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        boolean isCorrect = (!isEmpty(rs));
        rs.close();
        ps.close();
        return isCorrect;
    }

    /**
     * @param userID
     * @param schoolLogin
     * @param groupID
     * @param groupPassword
     * @return java.util.Hashtable
     * @throws fi.dwo.client.system.RegisterException
     *  
     */
    public Hashtable addToSchool(int userID, String schoolLogin, int groupID,
            String groupPassword) throws DwoXmlRpcException, SQLException {
        Hashtable result = null;
        int schoolGroupID = schoolGroupExists(schoolLogin, groupID,
                groupPassword);
        if (schoolGroupID == -1) {
            throw new DwoXmlRpcException(
                    DwoXmlRpcException.EXC_UNKNOWN_SCHOOLGROUP);
        } else {
            PreparedStatement ps = getStatement(QRY_ADD_TO_SCHOOL);
            ps.setInt(1, schoolGroupID);
            ps.setInt(2, userID);
            ps.execute();
            ps.close();

            ps = getStatement(QRY_SELECT_SCHOOL_USER);
            ps.setInt(1, userID);

            result = executeQueryWithRecord(ps);
        }

        return result;
    }

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
     *  
     */
    public boolean changeAccount(int userID, String password,
            String newPassword, String firstname, String middlename,
            String lastname, String email, int classID)
            throws DwoXmlRpcException, SQLException {
        if (!passwordCorrect(userID, password)) {
            throw new DwoXmlRpcException(
                    DwoXmlRpcException.EXC_WRONG_USERNAME_PASSWORD);
        } else {
            PreparedStatement ps = getStatement(QRY_UPDATE_USER_CLASS);
            ps.setInt(1, classID);
            ps.setInt(2, userID);

            ps.execute();
            ps.close();

            changeAccount(userID, password, newPassword, firstname, middlename,
                    lastname, email);

        }
        return true;
    }

    /**
     * @param userID
     * @param password
     * @param firstname
     * @param middlename
     * @param lastname
     * @param email
     * @return boolean
     * @throws fi.dwo.client.system.RegisterException
     *  
     */
    public boolean changeAccount(int userID, String password,
            String newPassword, String firstname, String middlename,
            String lastname, String email) throws DwoXmlRpcException,
            SQLException {
        if (!passwordCorrect(userID, password)) {
            throw new DwoXmlRpcException(
                    DwoXmlRpcException.EXC_WRONG_USERNAME_PASSWORD);
        } else {
            PreparedStatement ps = null;
            if ((password == null) || (newPassword.equals(""))) {
                ps = getStatement(QRY_UPDATE_USER_NO_PWD);
                ps.setString(1, firstname);
                ps.setString(2, middlename);
                ps.setString(3, lastname);
                ps.setString(4, email);
                ps.setInt(5, userID);
            } else {
                ps = getStatement(QRY_UPDATE_USER);
                ps.setString(1, firstname);
                ps.setString(2, middlename);
                ps.setString(3, lastname);
                ps.setString(4, newPassword);
                ps.setString(5, email);
                ps.setInt(6, userID);
            }

            ps.execute();
            ps.close();
        }
        return true;
    }

    /**
     * @param teacher
     * @param className
     * @return java.util.Hashtable
     * @throws fi.dwo.client.system.ClassException
     *  
     */
    public Hashtable addClass(int teacher, String className)
            throws DwoXmlRpcException, SQLException {
        /* Search the school from the teacher */
        Hashtable result = null;
        PreparedStatement ps = getStatement(QRY_SELECT_SCHOOL_FROM_USER);
        ps.setInt(1, teacher);

        ResultSet rs = ps.executeQuery();
        if (!isEmpty(rs)) {
            int schoolID = rs.getInt(1);
            rs.close();

            ps = getStatement(QRY_ADD_CLASS);
            ps.setInt(1, teacher);
            ps.setInt(2, schoolID);
            ps.setString(3, className);

            try {
                ps.execute();
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    /* The class already exists */
                    throw new DwoXmlRpcException(
                            DwoXmlRpcException.EXC_CLASS_EXISTS);
                } else {
                    throw e;
                }
            }

            rs = ps.getGeneratedKeys();

            if (!isEmpty(rs)) {
                int classID = rs.getInt(1);
                result = getRecord("tblClass", "classID", classID);
            }
            rs.close();
        }

        return result;
    }
    
    /**
     * @param schoolName
     * @param schoolLogin
     * @param studentPassw
     * @param teacherPassw
     * @return java.util.Hashtable
     * @throws fi.dwo.client.system.SchoolException
     * @deprecated Gebruik expliciet nummer.
     */
    public Hashtable addSchool(String schoolName, String schoolLogin, String studentPassw, String teacherPassw)
    throws DwoXmlRpcException, SQLException {
    	return addSchool(0, schoolName, schoolLogin, studentPassw, teacherPassw);
    }

    /**
     * @param schoolID
     * @param schoolName
     * @param schoolLogin
     * @param studentPassw
     * @param teacherPassw
     * @return java.util.Hashtable
     * @throws fi.dwo.client.system.SchoolException
     *  
     */
    public Hashtable addSchool(int schoolID, String schoolName, String schoolLogin, String studentPassw, String teacherPassw)
            throws DwoXmlRpcException, SQLException {
        Hashtable result = null;
        if (schoolLoginExists(schoolLogin)) {
            throw new DwoXmlRpcException(DwoXmlRpcException.EXC_SCHOOL_EXISTS);
		} else {
			PreparedStatement ps = getStatement(QRY_ADD_SCHOOLID);
			ps.setString(1, schoolName);
            ps.setString(2, schoolLogin);
            ps.setInt(3, schoolID);
            ps.execute();
            if(schoolID == 0 )
            {	ResultSet rs = ps.getGeneratedKeys();
            	rs.first();
            	schoolID = rs.getInt(1);
            	rs.close();
            }
            if(studentPassw != null && !studentPassw.trim().equals("")) {
            	ps = getStatement(QRY_INSERT_SCHOOLGROUP);
        		ps.setInt(1, 1);
            	ps.setInt(2, schoolID);
            	ps.setString(3, studentPassw);
            	
            	ps.execute();
            	ps.close();
          	} 
          	if(teacherPassw != null && !teacherPassw.trim().equals("")) {
            	ps = getStatement(QRY_INSERT_SCHOOLGROUP);
        		ps.setInt(1, 2);
            	ps.setInt(2, schoolID);
            	ps.setString(3, teacherPassw);
            	
            	ps.execute();
            	ps.close();
          	}/**/ 

        	result = getRecord("tblSchool", "schoolID", schoolID);
            
            
		}
        return result;
    }
    
    /**
     * @param schoolName
     * @param schoolLogin
     * @param studentPassw
     * @param teacherPassw
     * @return java.util.Hashtable
     * @throws fi.dwo.client.system.SchoolException
     *  
     */
    public Hashtable editSchool(int schoolID, String schoolName, String schoolLogin, String studentPassw, String teacherPassw)
            throws DwoXmlRpcException, SQLException {
        Hashtable result = getRecord("tblSchool", "schoolID", schoolID);
        String schoolNameOld = (String)result.get("schoolName");
        String schoolLoginOld = (String)result.get("schoollogin");
        PreparedStatement ps = null;
        if(!schoolName.equals(schoolNameOld) || !schoolLoginOld.equals(schoolLogin)) {
        	ps = getStatement(QRY_UPDATE_SCHOOL);
            ps.setString(1, schoolName);
            ps.setString(2, schoolLogin);
            ps.setInt(3, schoolID);
            
            ps.execute();
            ps.close();
        }
        ps = getStatement(QRY_CHECK_SCHOOLGROUP_EXISTS);
        ps.setInt(1, schoolID);
        ps.setInt(2, 1);
            
        ResultSet rs = ps.executeQuery();
        int schoolGroupID = 0;
        if(!isEmpty(rs)) {
        	rs.first();
        	schoolGroupID = rs.getInt("schoolGroupID");
        	String passwdOld = rs.getString("passwd");
        	if(!passwdOld.equals(studentPassw)) {
        		ps = getStatement(QRY_UPDATE_SCHOOLGROUP_PASSW);
        		ps.setString(1, studentPassw);
        		ps.setInt(2, schoolGroupID);
        		ps.execute();
        		ps.close();
        	}
        }
        else {
        	ps = getStatement(QRY_INSERT_SCHOOLGROUP);
        	ps.setInt(1, 1);
        	ps.setInt(2, schoolID);
        	ps.setString(3, studentPassw);
        	ps.execute();
        	ps.close();	
        }
        
        ps = getStatement(QRY_CHECK_SCHOOLGROUP_EXISTS);
        ps.setInt(1, schoolID);
        ps.setInt(2, 2);
            
        rs = ps.executeQuery();
        schoolGroupID = 0;
        if(!isEmpty(rs)) {
        	rs.first();
        	schoolGroupID = rs.getInt("schoolGroupID");
        	String passwdOld = rs.getString("passwd");
        	if(!passwdOld.equals(teacherPassw)) {
        		ps = getStatement(QRY_UPDATE_SCHOOLGROUP_PASSW);
        		ps.setString(1, teacherPassw);
        		ps.setInt(2, schoolGroupID);
        		ps.execute();
        		ps.close();
        	}
        }
        else {
        	ps = getStatement(QRY_INSERT_SCHOOLGROUP);
        	ps.setInt(1, 2);
        	ps.setInt(2, schoolID);
        	ps.setString(3, teacherPassw);
        	ps.execute();
        	ps.close();	
        }

        result = getRecord("tblSchool", "schoolID", schoolID);
            
            
		
        return result;
    }
    
    /*
    public boolean register(String username, String password, String firstname,
            String middlename, String lastname, String email,
            String schoolLogin, int groupID, String groupPassword)
            throws DwoXmlRpcException, SQLException {
        if (usernameExists(username)) {
            throw new DwoXmlRpcException(DwoXmlRpcException.EXC_USER_EXISTS);
        } else {
            int schoolGroupID = schoolGroupExists(schoolLogin, groupID,
                    groupPassword);
            if (schoolGroupID == -1) {
                throw new DwoXmlRpcException(
                        DwoXmlRpcException.EXC_UNKNOWN_SCHOOLGROUP);
            } else {
                PreparedStatement ps = getStatement(QRY_INSERT_USER_SCHOOL);
                ps.setInt(1, schoolGroupID);
                ps.setString(2, firstname);
                ps.setString(3, middlename);
                ps.setString(4, lastname);
                ps.setString(5, username);
                ps.setString(6, password);
                ps.setString(7, email);

                ps.execute();
                ps.close();
            }
        }
        return true;
    }
	*/
	
    /**
     * @param userID
     *  
     */
    public boolean deleteUser(int userID) throws SQLException {
        String[] arguments = { "tblStudentSco", "userID" };
        String query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments);

        PreparedStatement ps = getStatement(query);
        ps.setInt(1, userID);

        ps.execute();
        ps.close();

        String[] arguments2 = { "tblUser", "userID" };
        query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments2);

        ps = getStatement(query);
        ps.setInt(1, userID);

        ps.execute();
        ps.close();

        return true;
    }

    /**
     * Deletes a class, and disconnect the students in it.
     * 
     * @param classID
     *            The class to delete.
     * @param mustEmpty
     *            If true, the class will be checked if there are students in.
     *            If so, the class is not deleted and false is returned.
     * @return boolean Indicates if the class is deleted or not.
     *  
     */
    public boolean deleteClass(int classID, boolean mustEmpty)
            throws SQLException {
        boolean canDelete = !mustEmpty;
        String query;
        PreparedStatement ps;

        if (mustEmpty) {
            /* Check for students in the class */
            String[] arguments = { "tblUser", "classID" };
            query = MessageFormat.format(QRY_DEFAULT_SELECT_ID, arguments);

            ps = getStatement(query);
            ps.setInt(1, classID);

            ResultSet rs = ps.executeQuery();
            if (isEmpty(rs)) {
                canDelete = true;
            } else {
                canDelete = false;
            }

            rs.close();
        }

        if (canDelete) {
            /* Disconnect students from the class */
            ps = getStatement(QRY_DELETE_STUDENTS_FROM_CLASS);
            ps.setInt(1, classID);
            ps.execute();
            ps.close();

            /* Delete the class */
            String[] arguments2 = { "tblClass", "classID" };
            query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments2);
            ps = getStatement(query);
            ps.setInt(1, classID);
            ps.execute();
            ps.close();
        }
        return canDelete;
    }

    /**
     * @param uid
     *  
     */
    public boolean disconnectFromClass(int uid) throws SQLException {
        PreparedStatement ps = getStatement(QRY_DISCONNECT_USER_CLASS);
        ps.setInt(1, uid);
        ps.execute();
        ps.close();

        return true;

    }

    /**
     * Executes a prepared statement, and returns a valid xml-rpc value (a
     * Hashtable) for the first row The row is a hashtable, where the key is the
     * columnname
     * 
     * @param ps
     *            The prepared statement. All the parameters must be prepared
     * @return A hashtable where every key is the columnname.
     * @throws SQLException
     */
    public Hashtable executeQueryWithRecord(PreparedStatement ps)
            throws SQLException {
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsMeta = rs.getMetaData();
        String colName;
        Hashtable h = null;

        if (!isEmpty(rs)) {
            h = new Hashtable();
            for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
                colName = rsMeta.getColumnName(i);
                h.put(colName, clearNull(rs.getObject(colName)));
            }
        }

        rs.close();
        return h;

    }

    /**
     * Executes a prepared statement, and returns a valid xml-rpc value (a
     * Vector) Every row is a hashtable, where the key is the columnname
     * 
     * @param ps
     *            The prepared statement. All the parameters must be prepared
     * @param first
     *            The number of the first record (0 = the first)
     * @param count
     *            The number of records to return. Use -1 for all records)
     * @return A Vector containing hashtables, where every key is a column name.
     * @throws SQLException
     */
    public Vector executeQueryWithResult(PreparedStatement ps, int first,
            int count) throws SQLException {
        ResultSet rs = ps.executeQuery();
        Vector v = new Vector();
        ResultSetMetaData rsMeta = rs.getMetaData();
        String colName;

        rs.relative(first);

        while (rs.next() && ((count == -1) || (v.size() < count))) {
            Hashtable h = new Hashtable();
            for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
                colName = rsMeta.getColumnName(i);
                h.put(colName, clearNull(rs.getObject(colName)));
            }
            v.addElement(h);
        }
        rs.close();
        return v;
    }

    public Vector executeQueryWithResult(PreparedStatement ps)
            throws SQLException {
        return executeQueryWithResult(ps, 0, -1);
    }

    public Vector executeQueryWithResult(String query, int first, int count)
            throws SQLException {
        PreparedStatement ps = getStatement(query);
        return executeQueryWithResult(ps, first, count);
    }

    public Vector executeQueryWithResult(String query) throws SQLException {
        return executeQueryWithResult(query, 0, -1);
    }

    /**
     * Indicates if the resultset is empty
     * 
     * @param rs
     *            The resultset to check
     * @return If true, the resultset is empty. Otherwise the resultset is not
     *         empty.
     * @throws SQLException
     */
    public boolean isEmpty(ResultSet rs) throws SQLException {
        return !rs.next();
    }

    /**
     * We can't add null to a hashtable, so convert nulls to an empty string.
     * We can't add Long in XML-RPC, so convert to Integer 
     * if obj == null this function returns an empty string. Otherwise
     * if obj is a Long, convert to a Integer.
     *       
     * @param obj
     *            the item that could be null or Long
     * @return a possibly new obj.
     */
    public Object clearNull(Object obj) {
        if (obj == null) {
            return "";
        } else if (obj instanceof Long) {
        	return new Integer( ((Long)obj).intValue());
        } else {
            return obj;
        }

    }

    ////peter
    public Vector getCoursesForClass(int classID) throws IOException,
            XmlRpcException, SQLException {
        close(); //for lazy connection
        PreparedStatement ps;
        {
            ps = getStatement(QRY_SELECT_COURSES_CLASS);
            ps.setInt(1, classID);
        }

        return executeQueryWithResult(ps);
    }

    public boolean selectCoursesForClass(int classID, int courseID)
            throws IOException, XmlRpcException, SQLException {
        close(); //for lazy connection
        PreparedStatement ps;
        {
            ps = getStatement(QRY_INSERT_CLASS_COURSE);
            ps.setInt(1, classID);
            ps.setInt(2, courseID);
        }
        ps.execute();
        return true;
    }

    public boolean deSelectCoursesForClass(int classID, int courseID)
            throws IOException, XmlRpcException, SQLException {
        close(); //for lazy connection
        PreparedStatement ps;
        {
            ps = getStatement(QRY_DELETE_CLASS_COURSE);
            ps.setInt(1, classID);
            ps.setInt(2, courseID);
        }
        ps.execute();
        return true;
    }

    ////peter

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getCourses(int)
     */
    public Vector getCourses(int userID) throws IOException, XmlRpcException,
            SQLException {
        return getCourses(userID, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getCourses(int)
     */
    public Vector getCourses(int userID, boolean showAll) throws IOException,
            XmlRpcException, SQLException {
        close(); //for lazy connection
        PreparedStatement ps;
        if (userID < 0) {
            /* User is a guest */
            ps = getStatement(QRY_SELECT_COURSES_GUEST);
        } else {
            ps = getStatement(QRY_SELECT_COURSES);
            ps.setInt(1, userID);
            if (showAll) {
                ps.setInt(2, 1);
            } else {
                ps.setInt(2, 0);
            }

        }

        return executeQueryWithResult(ps);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getTable(java.lang.String,
     *      java.util.Hashtable)
     */
    public Vector getTable(String tableName, Hashtable wheredef)
            throws IOException, XmlRpcException, SQLException {
        return getTable(tableName, wheredef, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#LMSGetValue(java.lang.String)
     */
    public String LMSGetValue(int scoID, int userID, String iDataModelElement)
            throws IOException, XmlRpcException, SQLException {
        String[] arguments = { iDataModelElement };
        String query = MessageFormat.format(QRY_GET_STUDENT_SCO, arguments);

        PreparedStatement ps = getStatement(query);
        ps.setInt(1, scoID);
        ps.setInt(2, userID);

        Hashtable ht = executeQueryWithRecord(ps);

        ps.close();
        if (ht == null) {
        	if("total_time".equals(iDataModelElement))
        		return "0000:00:00.00";
            return "";
        } else {
        	Object o = ht.get(iDataModelElement);
        	if(o == null)
        		return "";
        	if(iDataModelElement.equals("score") && o instanceof Number) {
				Number number = ((Number)o);
				if(number.doubleValue() == number.longValue())
					return String.valueOf(number.longValue());
			}
            return String.valueOf(o);
        }
    }

    private Date currentTime = new Date();
    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#LMSSetValue(java.lang.String,
     *      java.lang.String)
     */
    public String LMSSetValue(int scoID, int userID, String iDataModelElement,
            String iValue) throws SQLException {
        try {
			String[] arguments = { "studentSco" };
			String query = MessageFormat.format(QRY_GET_STUDENT_SCO, arguments);

			PreparedStatement ps = getStatement(query);
			ps.setInt(1, scoID);
			ps.setInt(2, userID);

			Hashtable ht = executeQueryWithRecord(ps);
			log("DbAccess.LMSSetValue("
			        + scoID + ", " + userID + ", " + iDataModelElement + ", "
			        + iValue + ")");
			if ((iValue == null) || (iValue.equals(""))) {
			    log("Hij is leeg... " + iDataModelElement + " "
			            + userID);
			}
			ps.close();
			if (ht == null) {
			    ps = getStatement(QRY_ADD_EMPTY_STUDENT_SCO);
			    ps.setInt(1, scoID);
			    ps.setInt(2, userID);
			    ps.execute();
			    int count = ps.getUpdateCount();
			    if (count != 1) {
			        log("iets mis1 " + count);
			    }
			    ps.close();
			}

			arguments[0] = iDataModelElement;
			query = MessageFormat.format(QRY_UPDATE_STUDENT_SCO, arguments);

			ps = getStatement(query);
			ps.setObject(1, iValue);
			ps.setInt(2, scoID);
			ps.setInt(3, userID);

			ps.execute();
			int count = ps.getUpdateCount();
			if (count != 1) {
			    // iets mis2 ...
			    log("iets mis2 " + count);

			}

			ps.close();

			return "";
		} catch (SQLException e) {
			log("DbAccess.setLMSSetValue " + iDataModelElement  + " throws " + userID);
			e.printStackTrace();
			throw e;
		} catch (RuntimeException e) {
			log("DbAccess.setLMSValue " + iDataModelElement  + " runtime " + userID);
			e.printStackTrace();
			throw e;
		}

    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getResults(int[])
     */
    public Vector getResults(Vector courses, int userID) throws IOException,
            XmlRpcException, SQLException {
        int i;
        String courseString = "";
        if (courses.size() > 0) {
            for (i = 0; i < courses.size(); i++) {
                courseString += ((Integer) courses.get(i)).toString() + ", ";
            }

            courseString = courseString.substring(0, courseString.length() - 2);

            String[] arguments = { courseString };
            String query = MessageFormat.format(QRY_RESULTS_ALL, arguments);
            PreparedStatement ps = getStatement(query);
            ps.setInt(1, userID);
            return executeQueryWithResult(ps);

        } else {
            /* No Courses, no result */
            return new Vector();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getResults(int[], int)
     */
    public Vector getResults(Vector courses, int classID, int userID)
            throws IOException, XmlRpcException, SQLException {
        int i;
        String courseString = "";
        if (courses.size() > 0) {
            for (i = 0; i < courses.size(); i++) {
                courseString += ((Integer) courses.get(i)).toString() + ", ";
            }

            courseString = courseString.substring(0, courseString.length() - 2);

            String[] arguments = { courseString };
            String query = MessageFormat.format(QRY_RESULTS_CLASS, arguments);

            PreparedStatement ps = getStatement(query);
            ps.setInt(1, classID);
            ps.setInt(2, userID);
            return executeQueryWithResult(ps);

        } else {
            /* No Courses, no result */
            return new Vector();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getResults(int, int)
     */
    public Vector getResults(int courseID, int classID, int userID)
            throws IOException, XmlRpcException, SQLException {
        PreparedStatement ps = getStatement(QRY_RESULTS_STUDENT_COURSE);
        ps.setInt(1, classID);
        ps.setInt(2, courseID);
        ps.setInt(3, userID);

        Vector v = executeQueryWithResult(ps);
        return v;
    }
    /**
     * The results of a single course by a single student.
     */
    public Vector getUserResults(int courseID, int userID)
            throws IOException, XmlRpcException, SQLException {
        PreparedStatement ps = getStatement(QRY_RESULTS_SINGLE_STUDENT_COURSE);
        ps.setInt(2, courseID);
        ps.setInt(1, userID);

        Vector v = executeQueryWithResult(ps);
        return v;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getResults(int)
     */
    public Vector getResults(int courseID, int userID) throws IOException,
            XmlRpcException, SQLException {
        PreparedStatement ps = getStatement(QRY_RESULTS_COURSE);
        ps.setInt(1, courseID);
        ps.setInt(2, userID);
        return executeQueryWithResult(ps);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#renameClass(int,
     *      java.lang.String)
     */
    public boolean renameClass(int classID, String newName)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        PreparedStatement ps = getStatement(QRY_UPDATE_CLASS_NAME);
        ps.setString(1, newName);
        ps.setInt(2, classID);
        try {
            ps.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                /* The class already exists */
                throw new DwoXmlRpcException(
                        DwoXmlRpcException.EXC_CLASS_EXISTS);
            } else {
                throw e;
            }
        }
        ps.execute();

        return true;
    }

    /**
     * 
     * @deprecated weg ermee
     * @see fi.dwo.client.persistence.DbAccessIF#selectJar(java.lang.String,
     *      java.lang.String)
     */
    public boolean selectJar(String key, String jar) throws IOException,
            XmlRpcException, SQLException {
        PreparedStatement ps = getStatement(QRY_JAR_INSERT_KEY);

        ps.setString(1, key);
        ps.setString(2, jar);

        try {
            ps.execute();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                ps = getStatement(QRY_JAR_UPDATE_KEY);

                ps.setString(1, jar);
                ps.setString(2, key);

                ps.execute();

            } else {
                throw e;
            }
        }
        return false;
    }
    /**
     * @deprecated weg ermee
     * @param key
     * @return
     * @throws IOException
     * @throws SQLException
     */
    private String getJar(String key) throws IOException, SQLException {
        PreparedStatement ps = getStatement(QRY_JAR_SELECT_KEY);

        ps.setString(1, key);

        Hashtable result = executeQueryWithRecord(ps);

        return (String) result.get("jarname");

    }
    /**
     * @deprecated weg ermee
     * @return
     * @throws IOException
     * @throws SQLException
     */
    private int getNrJars() throws IOException, SQLException {
        Hashtable result = executeQueryWithRecord(getStatement(QRY_JAR_COUNT_JARS));
        return ((Number) result.get("number")).intValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#reconnect()
     */
    public boolean reconnect() throws IOException, XmlRpcException,
            SQLException {
        close();
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#log(java.lang.String)
     */
    public boolean log(String s) {
    	currentTime.setTime(System.currentTimeMillis());
        System.err.println("DbAccessLog " + currentTime + ":"
                + s );
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getEditableCourses(int)
     */
    public Vector getEditableCourses(int schoolID) throws IOException,
            XmlRpcException, SQLException {

        PreparedStatement ps;
        ps = getStatement(QRY_SELECT_COURSES_EDITABLE);
        ps.setInt(1, schoolID);
        return executeQueryWithResult(ps);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getEditableCoursesAdmin()
     */
    public Vector getEditableCoursesAdmin() throws IOException,
            XmlRpcException, SQLException {

        PreparedStatement ps;
        ps = getStatement(QRY_SELECT_COURSES_EDITABLE_ADMIN);
        return executeQueryWithResult(ps);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#addCourse(java.lang.String,
     *      java.lang.String)
     */
    public int addCourse(int schoolID, String name, String description, int dwoProfile)
            throws DwoXmlRpcException, SQLException {
        Hashtable schoolData = getRecord("tblSchool", "schoolID", schoolID);
        log("DbAccess.addCourse" + schoolData);
        String image = "";
        if (schoolData.containsKey("image")
                && (!schoolData.get("image").equals(""))) {
            image = (String) schoolData.get("image");
        }
        
        PreparedStatement ps;
        
        if(schoolID==0){
	        ps = getStatement(QRY_ADD_COURSE_BASIC);
	        ps.setString(1, name);
	        ps.setString(2, description);
	        ps.setString(3, image);
	        ps.setInt(4, dwoProfile);
        }
		else {
	        ps = getStatement(QRY_ADD_COURSE);
	        ps.setInt(1, schoolID);
	        ps.setString(2, name);
	        ps.setString(3, description);
	        ps.setString(4, image);
	        ps.setInt(5, dwoProfile);
		}
        

        try {
            ps.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                /* The course already exists */
                throw new DwoXmlRpcException(
                        DwoXmlRpcException.EXC_COURSE_EXISTS);
            } else {
                throw e;
            }
        }

        ResultSet rs = ps.getGeneratedKeys();

        int result = -1;
        if (!isEmpty(rs)) {
            result = rs.getInt(1);
        }
        rs.close();
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#changeCourse(int,
     *      java.lang.String, java.lang.String)
     */
    public boolean changeCourse(int courseID, String name, String description)
            throws DwoXmlRpcException, SQLException {
        PreparedStatement ps;
        ps = getStatement(QRY_UPDATE_COURSE);
        ps.setString(1, name);
        ps.setString(2, description);
        ps.setInt(3, courseID);

        try {
            ps.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                /* The course already exists */
                throw new DwoXmlRpcException(
                        DwoXmlRpcException.EXC_COURSE_EXISTS);
            } else {
                throw e;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#deleteCourse(int)
     */
    public boolean deleteCourse(int courseID) throws DwoXmlRpcException,
            IOException, XmlRpcException, SQLException {
        Hashtable wheredef = new Hashtable();
        wheredef.put("courseID", new Integer(courseID));
        Vector scos = getTable("tblSco", wheredef);
        String[] arguments = new String[2];

        arguments[0] = "tblStudentSco";
        arguments[1] = "scoID";

        /* Delete results of sco's of the course */
        String statement = MessageFormat.format(QRY_DELETE_DEFAULT, arguments);
        PreparedStatement ps;
        int scoID;
        while (scos.size() > 0) {
            ps = getStatement(statement);
            scoID = ((Integer) ((Hashtable) scos.remove(0)).get("scoID"))
                    .intValue();
            ps.setInt(1, scoID);
            ps.execute();
            ps.close();
        }

        /* Delete Sco's of the course */
        arguments[0] = "tblSco";
        arguments[1] = "courseID";
        statement = MessageFormat.format(QRY_DELETE_DEFAULT, arguments);
        ps = getStatement(statement);
        ps.setInt(1, courseID);
        ps.execute();
        ps.close();

        /* Delete the course */
        arguments[0] = "tblCourse";
        arguments[1] = "courseID";
        statement = MessageFormat.format(QRY_DELETE_DEFAULT, arguments);
        ps = getStatement(statement);
        ps.setInt(1, courseID);
        ps.execute();
        ps.close();

        return true;
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#addSco(int, java.lang.String,
     *      java.lang.String, int)
     */
    public int addSco(int courseID, String name, String description,
            int appletConfigID, int sequencenr) throws DwoXmlRpcException,
            IOException, XmlRpcException, SQLException {
    	Hashtable data;
    	if(appletConfigID < 0)
    		data = getRecord("tblSco", "scoID", -appletConfigID);
    	else
        data = getRecord("tblAppletConfig", "appletConfigID",
                appletConfigID);
        log("DbAccess.addSco " + data);
        int appletID = -1;
        String launchdata = "";
        if (data.containsKey("appletID")) {
            appletID = ((Integer) data.get("appletID")).intValue();
        }
        if (data.containsKey("launchdata")) {
            launchdata = (String) data.get("launchdata");
        }

        return addSco(courseID, name, description, appletID, launchdata,
				sequencenr);
    }

	/**
	 * @param courseID
	 * @param name
	 * @param description
	 * @param appletID
	 * @param launchdata
	 * @param sequencenr
	 * @return
	 * @throws SQLException
	 * @throws org.apache.xmlrpc.XmlRpcException 
	 */
	public int addSco(int courseID, String name, String description,
			int appletID, String launchdata, int sequencenr)
			throws SQLException, DwoXmlRpcException {
		if (appletID != -1) {
            PreparedStatement ps;
            ps = getStatement(QRY_ADD_SCO);
            ps.setInt(1, courseID);
            ps.setInt(2, appletID);
            ps.setString(3, name);
            ps.setString(4, description);
            ps.setString(5, launchdata);
            ps.setInt(6, sequencenr);

            try {
                ps.execute();
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    /* The sco already exists */
                    throw new DwoXmlRpcException(
                            DwoXmlRpcException.EXC_SCO_EXISTS);
                } else {
                    throw e;
                }
            }

            ResultSet rs = ps.getGeneratedKeys();

            int result = -1;
            if (!isEmpty(rs)) {
                result = rs.getInt(1);
            }
            rs.close();
            return result;
        } else { //no appletconfig found
            throw new DwoXmlRpcException(DwoXmlRpcException.EXC_SCO_EXISTS);
        }
	}

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#changeSco(int,
     *      java.lang.String, java.lang.String)
     */
    public boolean changeSco(int scoID, String name, String description, String launchdata)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        PreparedStatement ps;
        ps = getStatement(QRY_UPDATE_SCO);
        ps.setString(1, name);
        ps.setString(2, description);
        ps.setString(3, launchdata);
        ps.setInt(4, scoID);
        
        log("DbAccess.changeSco " + ps);

        try {
            ps.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                /* The course already exists */
                throw new DwoXmlRpcException(DwoXmlRpcException.EXC_SCO_EXISTS);
            } else {
                throw e;
            }
        }
        
        ps.close();

        String[] arguments = new String[2];

        arguments[0] = "tblStudentSco";
        arguments[1] = "scoID";

        /* Delete results of sco's */
        String statement = MessageFormat.format(QRY_DELETE_DEFAULT, arguments);
        ps = getStatement(statement);
        ps.setInt(1, scoID);
        ps.execute();
        ps.close();
        return true;
    }

    static private final String QRY_UPDATE_SCO_SEQUENCENR =
    	"UPDATE tblSco SET sequencenr = ? WHERE (scoID = ?) ";
    
    /**
     * Update het sequencenr van een sco. Niet gecombineerd met changeSco, 
     * omdat er geen bijeffect is dat de studenten hun data verliezen.
     * Voor een swap zijn twee sco's nodig. Daarom hier meteen twee voor de prijs van ��n!
     * @param scoID 
     * @param sequencenr nieuw sequence
     * nummer voor scoID, bij swap oude van scoID2
     * @param scoID2
     * @param sequencenr2 nieuw sequencenummer voor scoID2, bij swap oude van scoID
     * @return always true
     * @throws SQLException
     * @see #changeSco(int, String, String, String)
     */
    public boolean changeScoSequenceNr(int scoID, int sequencenr, int scoID2, int sequencenr2) throws SQLException
     {
    	PreparedStatement ps;
    	ps = getStatement(QRY_UPDATE_SCO_SEQUENCENR);
    	try {
        	ps.setInt(1, sequencenr);
        	ps.setInt(2, scoID);
    		ps.execute();
    		ps.setInt(1, sequencenr2);
    		ps.setInt(2, scoID2);
    		ps.execute();
    	} finally  {
    		ps.close();
    	}
    	return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#deleteSco(int)
     */
    public boolean deleteSco(int scoID) throws DwoXmlRpcException, IOException,
            XmlRpcException, SQLException {
        Hashtable scodata = getRecord("tblSco", "scoID", scoID);
        int sequencenr = -1;
        int courseid = -1;
        PreparedStatement ps;
        if (scodata.containsKey("sequencenr")) {
            sequencenr = ((Integer) scodata.get("sequencenr")).intValue();
        }
        if (scodata.containsKey("courseID")) {
            courseid = ((Integer) scodata.get("courseID")).intValue();
        }

        if (courseid != -1) {
            ps = getStatement(QRY_UPDATE_SCO_SEQUENCE);
            ps.setInt(1, sequencenr);
            ps.setInt(2, courseid);
            log(ps.toString());
            ps.execute();
            ps.close();
        }

        String[] arguments = new String[2];

        arguments[0] = "tblStudentSco";
        arguments[1] = "scoID";

        /* Delete results of sco's */
        String statement = MessageFormat.format(QRY_DELETE_DEFAULT, arguments);
        ps = getStatement(statement);
        ps.setInt(1, scoID);
        ps.execute();
        ps.close();

        /* Delete Sco's */
        arguments[0] = "tblSco";
        arguments[1] = "scoID";
        statement = MessageFormat.format(QRY_DELETE_DEFAULT, arguments);
        ps = getStatement(statement);
        ps.setInt(1, scoID);
        ps.execute();
        ps.close();

        return true;
    }

    /**
     * Delete school from the database.
     * @param schoolID School Identifier.
     */
	public boolean deleteSchool(int schoolID) throws IOException, XmlRpcException, SQLException {
		
// 1) delete students from class
		String QRY_DELETE_STUDENT_FROM_CLASS_IN_SCHOOL = 
			"UPDATE tblUser SET classID = NULL WHERE classID in (SELECT classID from tblClass where schoolID = ?)";
		PreparedStatement ps;
		ps = getStatement(QRY_DELETE_STUDENT_FROM_CLASS_IN_SCHOOL);
		ps.setInt(1, schoolID);
		ps.executeUpdate();
		ps.close();
// 2) delete courses from class
		String QRY_DELETE_COURSES_FROM_CLASS_IN_SCHOOL = 
			"DELETE FROM tblClassCourse WHERE classID in (SELECT classID from tblClass where schoolID = ?)";
		ps = getStatement(QRY_DELETE_COURSES_FROM_CLASS_IN_SCHOOL);
		ps.setInt(1,schoolID);
		ps.executeUpdate();
		ps.close();
// 3) delete class from school
        String[] arguments2 = { "tblClass", "schoolID" };
        String query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments2);
        ps = getStatement(query);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        ps.close();
// 4) delete suspend data that become inaccessable.

        // TODO

// 5) delete sco's die bij courses van school horen.
        String QRY_DELETE_SCO_FROM_SCHOOL = 
        	"DELETE FROM tblSco WHERE courseID in (SELECT courseID FROM tblCourse WHERE schoolID = ?)";
        ps = getStatement(QRY_DELETE_SCO_FROM_SCHOOL);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        ps.close();
// 6) delete courses from school
        arguments2 = new String[] { "tblCourse", "schoolID" };
        query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments2);
        ps = getStatement(query);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        ps.close();
// 7) verwijder users uit school
        String QRY_DELETE_USERS_FROM_SCHOOL =
        	"UPDATE tblUser SET schoolGroupID = NULL WHERE " +
        	"SchoolGroupID in (SELECT schoolGroupID FROM tblSchoolGroup where schoolID = ?)";
        ps = getStatement(QRY_DELETE_USERS_FROM_SCHOOL);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        ps.close();
// 8) verwijder schoolgroup
        arguments2 = new String[] { "tblSchoolGroup", "schoolID" };
        query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments2);
        ps = getStatement(query);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        ps.close();
// 9) verwijder school
        arguments2 = new String[] { "tblSchool", "schoolID" };
        query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments2);
        ps = getStatement(query);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        ps.close();
        
        return true;
	}

	public Vector getUserResults(Vector courses, int userID) throws SQLException {
        int i;
        StringBuffer courseString = new StringBuffer();
        if (courses.size() > 0) {
            for (i = 0; i < courses.size(); i++) {
            	if(i!= 0) courseString.append(',');
            	// beware of source code injection here....
            	// assert courses.get(i) instanceof Integer
            	courseString.append(courses.get(i));
            }
            String[] arguments = { courseString.toString() };
            String query = MessageFormat.format(QRY_RESULTS_SINGLE, arguments);
            PreparedStatement ps = getStatement(query);
            ps.setInt(1, userID);
            return executeQueryWithResult(ps);
        } else {
            /* No Courses, no result */
            return courses; // an empty vector
        }
	}

	public boolean setLogo(int id, byte[] image) throws SQLException,
			IOException, XmlRpcException {
		
		PreparedStatement ps;
		ps = getStatement("UPDATE tblCourse SET imageData = ? where courseID = ?");
		ps.setObject(1, image);
		ps.setInt(2, id);
		ps.executeUpdate();
		ps.close();

		ps = getStatement("UPDATE tblimage SET image = ? where courseID = ?");
		ps.setObject(1, image);
		ps.setInt(2, id);
		boolean result = ps.executeUpdate() != 0;
		ps.close();
		if(!result)
		{
			ps = getStatement("INSERT INTO tblimage(courseID, image) VALUES (?,?)");
			ps.setInt(1, id);
			ps.setObject(2,image);
			ps.executeUpdate();
			ps.close();
		}
		return result;
	}

	public Hashtable getFidentitySchools() throws DwoXmlRpcException
	{
		throw new DwoXmlRpcException(DwoXmlRpcException.EXC_SCHOOL_UNSUPPORTED);
	}
}