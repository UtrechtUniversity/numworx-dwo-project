// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\server\\persistence\\DbAccess.java
package fi.dwo.server.persistence;

import fi.dwo.commons.exceptions.DwoXmlRpcException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.beans.jdbc.DbConnect;
import fi.beans.scorm2xml.Scorm2Xml;
import fi.dwo.commons.entities.SchoolGroupRoles;
//import fi.dwo.client.domain.SchoolGroup;
import fi.dwo.commons.persistence.DbAccessIF;
//import fi.dwo.client.persistence.PersistenceFacade;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides handles persistent entity operations on the database.
 *
 *
 */
public class DbAccess extends DbConnect implements DbAccessIF {

    private static final Logger log = Logger.getLogger(DbAccess.class.getName());

    /**
     *
     * @return
     */
    protected String session() {
        return "";
    }

    private void log(Level level, String msg, Throwable t) {
        log.log(level, session() + msg, t);
    }

    //TODO Wim, explain what it does
    private static final int PROFILEOFFSET = -1234;

    /**
     * Fetch generic database entity.
     */
    private final static String QRY_DEFAULT_SELECT_ID = "SELECT * "
            + "FROM {0} " + "WHERE `{1}` = ?";

    /**
     * Fetch all database entities of given type.
     */
    private final static String QRY_DEFAULT_SELECT_TABLE = "SELECT * "
            + "FROM {0} ";

    /**
     * Fetch all database entities of this type in given order.
     */
    private final static String QRY_DEFAULT_SELECT_TABLE_ORDER = "SELECT * "
            + "FROM {0} "
            + "ORDER BY `{1}` ";

    /**
     * Fetch all database entities of given type for a given condition.
     */
    private final static String QRY_DEFAULT_SELECT_TABLE_WHERE = "SELECT * "
            + "FROM {0} " + "WHERE (1=1) ";

//    private final static String QRY_DEFAULT_SELECT_CLASS_STUDENT = "SELECT userID "
//            + "FROM tblTeacherOf " + "WHERE (classID={0} and userID={1}) ";
    private final static String QRY_SELECT_CLASS_TEACHER = "SELECT userID "
            + "FROM tblTeacherOf " + "WHERE (classID={0} and userID={1}) ";

    private final static String QRY_SELECT_CLASS_STUDENT = "SELECT userID "
            + "FROM tblStudentOf " + "WHERE (classID={0} and userID={1}) ";

//TODO V1_3 DONE adjust lastLogin, registerDate, rights to be in hasRole    
    private final static String QRY_SELECT_TEACHERS_OF_CLASS = "SELECT u.userID, "
            + "u.schoolGroupID, u.firstname, u.middlename, u.lastname, u.username, "
            + "u.email, r.registerDate, r.rights, r.lastLogin t.classID" // skipping u.password
            + "FROM tblteacherof t "
            + "join tblUser u using (userID) "
            + "join tblHasRole r on (u.schoolGroupID=r.schoolgroupID and u.userID = r.userID) "
            + "WHERE t.classID = {0}";

//TODO V1_3 DONE adjust lastLogin, registerDate, rights to be in hasRole    
    private final static String QRY_SELECT_CLASSSTUDENTS_OF_CLASS = "SELECT u.userID, "
            + "u.schoolGroupID, u.firstname, u.middlename, u.lastname, u.username, "
            + "u.email, r.registerDate, r.rights, r.lastLogin, t.classID " // skipping u.password
            + "FROM tblStudentOf t "
            + "join tblUser u using (userID) "
            + "join tblHasRole r on (u.schoolGroupID=r.schoolgroupID and u.userID = r.userID) "
            + "WHERE t.classID = {0}";

//TODO V1_3 DONE Adjust for just one school.
    private final static String QRY_SELECT_CLASSES_OF_STUDENT = "SELECT c.classID, "
            + "s.userID, c.schoolID, c.class, c.registrationKey, c.iconizer "
            + "FROM tblStudentOf s join tblClass c using (classID) "
            + "WHERE s.userID={0} and c.schoolID = {1}";

    //TODO V1_3 DONE
    private final static String QRY_IS_IN_STUDENT_ROLE = "SELECT count(userID) = 1 "
            + "FROM tblGroup g join tblSchoolGroup sg using (groupID) "
            + "join tblHasRole using (schoolGroupID) join tblUser u using (schoolGroupID) "
            + "WHERE u.userID = {0} and sg.schoolID = {1} and g.groupname = \"STUDENT\"";

//TODO V1_3 DONE
    private final static String QRY_IS_IN_TEACHER_ROLE = "SELECT count(userID) = 1 "
            + "FROM tblGroup g join tblSchoolGroup sg using (groupID) "
            + "join tblHasRole using (schoolGroupID) join tblUser u using (schoolGroupID) "
            + "WHERE u.userID = {0} and sg.schoolID = {1} and g.groupname = \"TEACHER\"";

//TODO V1_3 DONE
    private final static String QRY_SELECT_CLASSES_OF_TEACHER = "SELECT c.classID, "
            + "s.userID, c.schoolID, c.class, c.registrationKey, c.iconizer "
            + "FROM tblTeacherOf s join tblClass c using (classID) "
            + "WHERE s.userID={0} and c.schoolID = {1}";

//    /**
//     * Select all top level course entities a given user may access. Either
//     * because they are freely accessible or because he is a member of a
//     * school.
//     */    
//V1_2
//    private final static String QRY_SELECT_COURSES = "SELECT tblCourse.* "
//            + "FROM tblUser LEFT JOIN tblSchoolGroup ON tblUser.schoolGroupID = tblSchoolGroup.schoolGroupID, tblCourse "
//            + "WHERE ((tblSchoolGroup.schoolID = tblCourse.schoolID) "
//            + "OR     (isnull(tblCourse.schoolID))) " + "AND   (userID = ?) "
//            + "AND parentID = 0 " + "ORDER BY name ";
    /**
     * Select all top level course entities a given user may access in
     * alphabetical order of the course name.
     *
     * Either because they are freely accessible or because he is a member of a
     * school.
     */

//    //TODO DONE V1_3
//    private final static String QRY_SELECT_COURSES = "SELECT tblCourse.* "
//            + "FROM tblCourse WHERE (tblCourse.schoolID = ? "
//            + "OR isnull(tblCourse.schoolID)) "
//            + "AND parentID = 0 " + "ORDER BY name ";

    private final static String QRY_SELECT_IMPORT_COURSES = "SELECT DISTINCT c.* FROM tblCourse c, tblfromto ft, tblSchool s"
            + " WHERE c.schoolID = ? AND c.export = 1 AND c.schoolID = ft.schoolFrom AND (ft.schoolTo = -1 OR ft.schoolTO = ?) AND c.dwoProfileID = ?"
            + " AND s.schoolID = c.schoolID AND s.export = 1"
            + " ORDER BY c.name ASC";

    /**
     * Select all top level course entities an anonymous user may access in
     * alphabetical order of the course name.
     */
    private final static String QRY_SELECT_COURSES_GUEST = "SELECT tblCourse.* "
            + "FROM tblCourse "
            + "WHERE (isnull(tblCourse.schoolID)) and parentID = 0 "
            + "ORDER BY name ";

    /**
     * Selects all top level course entities an anonymous user may access in
     * alphabetical order of the course name for a given ProfileID.
     *
     */
    private final static String QRY_SELECT_COURSES_PROFILE_GUEST = "SELECT tblCourse.* "
            + "FROM tblCourse "
            + "WHERE (isnull(tblCourse.schoolID)) and dwoProfileID = ? and parentID = 0 "
            + "ORDER BY name ";

    /**
     * Returns all the courses and class courses for the class with the
     * specified classID.
     */
    private final static String QRY_SELECT_COURSES_CLASS = "SELECT tblCourse.*, tblClassCourse.* "
            + "FROM tblCourse,tblClassCourse "
            + "WHERE (tblCourse.CourseID = tblClassCourse.CourseID) "
            + "AND (tblClassCourse.ClassID = ?) " + "ORDER BY name ";

    private final static String QRY_SELECT_COURSES_CLASS_NOMAP = "SELECT tblCourse.*, tblClassCourse.* "
            + "FROM tblCourse,tblClassCourse "
            + "WHERE (tblCourse.CourseID = tblClassCourse.CourseID) "
            + "AND (tblCourse.withChildren = 0) "
            + "AND (tblClassCourse.ClassID = ?) " + "ORDER BY name ";

    private final static String QRY_INSERT_CLASS_COURSE = "INSERT INTO tblClassCourse(classID, courseID) "
            + "VALUES(?, ?) ";

    private final static String QRY_INSERT_CLASS_COURSE2 = "INSERT INTO tblClassCourse(classID, courseID, type, notBefore, notAfter) "
            + "VALUES(?,?,?,?,?) ";

    private final static String QRY_DELETE_CLASS_COURSE = "DELETE FROM tblClassCourse "
            + "WHERE (classID = ?) " + "AND (courseID = ?) ";

    private final static String QRY_DELETE_COURSES_FROM_CLASS_IN_SCHOOL
            = "DELETE FROM tblClassCourse WHERE classID in (SELECT classID from tblClass where schoolID = ?)";

    private final static String QRY_DELETE_SCO_BY_ID = "delete tblScoContext, tblScoData from tblScoContext join tblScoData using (scoID) where scoID = ?";

    private final static String QRY_SELECT_COURSES_EDITABLE_ADMIN = "SELECT tblCourse.* "
            + "FROM tblCourse "
            + "WHERE (isnull(tblCourse.schoolID)) and (tblCourse.parentID = 0)"
            + "ORDER BY name ";

    private final static String QRY_SELECT_COURSES_EDITABLE = "SELECT tblCourse.* "
            + "FROM tblCourse "
            + "WHERE (tblCourse.schoolID = ?) and (parentID = 0)"
            + "ORDER BY name ";

    private final static String QRY_GET_STUDENT_SCO = "SELECT `{0}` "
            + "FROM tblStudentScoContext join tblStudentScoData using (studentSco) "
            + "WHERE (scoID = ?) "
            + "AND   (userID = ?) ";

    private final static String QRY_ADD_EMPTY_STUDENT_SCO_CONTEXT = "INSERT INTO tblStudentScoContext(scoID, userID, createDate, score) "
            + "VALUES(?, ?, CURDATE(), 0) ";

    private final static String QRY_ADD_EMPTY_STUDENT_SCO_DATA = "INSERT INTO tblStudentScoData(studentSco,suspendData) "
            + "VALUES(?, '') ";

    private final static String QRY_UPDATE_STUDENT_SCO = "UPDATE tblStudentScoContext, tblStudentScoData "
            + "SET `{0}` = ?, createDate = CURDATE() "
            + "WHERE (scoID = ?) "
            + "AND   (userID = ?) AND (tblStudentScoContext.studentSco = tblStudentScoData.studentSco)";

    private final static String QRY_WHERE_COLUMN = " AND ({0} = ?) ";

    private final static String QRY_CHECK_USERNAME_EXISTS = "SELECT userID "
            + "FROM tblUser " + "WHERE (username = ?)";

    private final static String QRY_CHECK_SCHOOLLOGIN_EXISTS = "SELECT schoolID "
            + "FROM tblSchool " + "WHERE (schoollogin = ?)";

    private final static String QRY_CHECK_SCHOOLGROUP_EXISTS = "SELECT * "
            + "FROM tblSchoolGroup "
            + "WHERE (schoolID = ?) "
            + "AND   (groupID = ?) ";

//TODO V1_3 DONE (Schoolless user)
    private final static String QRY_INSERT_USER = "INSERT INTO tblUser(firstname, middlename, lastname, username, passwd, email, registerDate) "
            + "VALUES (?, ?, ?, ?, ?, ?, CURDATE())";

    private final static String QRY_INSERT_SCHOOLGROUP = "INSERT INTO tblSchoolGroup(groupID, schoolID, passwd) "
            + "VALUES (?, ?, ?)";

    private final static String QRY_CHECK_SCHOOLGROUP = "SELECT schoolGroupID, expire, tblSchoolGroup.schoolID "
            + "FROM tblSchoolGroup, tblSchool "
            + "WHERE (tblSchoolGroup.schoolID = tblSchool.schoolID) "
            + "AND   (schoollogin = ?) " + "AND   (groupID = ?) "
            + "AND   (passwd = ?) ";

//TODO DONE V1_3 
    private final static String QRY_INSERT_USER_SCHOOLGROUP = "INSERT INTO tblUser(schoolGroupID, firstname, middlename, lastname, username, passwd, email, registerData, lastLogin) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE(),CURDATE())";

//TODO V1_2    
//    private final static String QRY_INSERT_USER_SCHOOL = "INSERT INTO tblUser(schoolGroupID, firstname, middlename, lastname, username, passwd, email, registerDate) "
//            + "VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE())";
    /**
     * Inserts an association between a user and a schoolGroup.
     *
     * The schoolGroup has
     */
// TODO V1_3 DONE Use QRY_INSERT_USER and then QRY_ADD_USER_TO_SCHOOL 
    private final static String QRY_ADD_USER_TO_SCHOOL = "INSERT INTO tblHasRole(userID,schoolGroupID, registerDate, lastLogin ) VALUES (?, ?, CURDATE(), CURDATE());";

    // TODO V1_3 DONE Used whenever changing school role.
    private final static String QRY_UPDATE_DEFAULT_SCHOOLGROUP = "UPDATE tblUser schoolGroupID = ? WHERE userID = ? ";
    
    
// TODO DONE V1_3 picks default class
    private final static String QRY_USER_LOGIN = "SELECT * "
            + "FROM tblUser u "
            + "join tblHasRole r on (u.schoolGroupID=r.schoolgroupID and u.userID = r.userID) "
            + "JOIN tblClass ON r.classID = tblClass.classID "
            + "WHERE (u.username = ?) " + "AND   (u.passwd = ?) ";

// TODO DONE V1_3 picks no class
    private final static String QRY_USER_LOGIN_NO_PASSWD = "SELECT * "
            + "FROM tblUser "
            + "WHERE (username = ?) " + "AND   (passwd = ?) ";

//    private final static String QRY_USER_LOGIN_NO_PASSWD = "SELECT * "
//            + "FROM tblUser "
//            + "WHERE (username = ?) ";
    /**
     * Gets the user entity and school entity for a given userid using the
     * default last saved values.
     */
    //TODO DONE V1_3
    private final static String QRY_GET_USER_DATA = "SELECT tblUser.*, tblHasRole.*, tblGroup.*, tblSchool.schoolID, tblSchool.schoolName, tblSchool.schoollogin, tblSchool.image, tblSchool.export, tblSchool.schoolRights, tblSchool.expire "
            + "FROM tblUser, tblSchoolGroup, tblGroup, tblSchool "
            + "WHERE (tblUser.schoolGroupID = tblSchoolGroup.schoolGroupID) "
            + "AND   (tblSchoolGroup.groupID = tblGroup.groupID) "
            + "AND   (tblSchoolGroup.schoolID = tblSchool.schoolID) "
            + "AND (tblUser.schoolGroupID=tblHasRole.schoolgroupID and tblUser.userID = tblHasRole.userID) "
            + "AND   (userID = ?) ";
//    /**
//     * Gets the user entity for a given user id.
//     */
//    // TODO DONE obsolete V1_3
//    private final static String QRY_GET_USER_DATA = "SELECT tblUser.* "
//            + "FROM tblUser WHERE (userID = ?)";

//    /**
//     * Gets the user entity and school entity for a given user id and school id.
//     */
//    // TODO DONE V1_3
//    private final static String QRY_GET_SCHOOL_USER_DATA = "SELECT tblUser.*, tblGroup.*, tblSchool.schoolID, tblSchool.schoolName, tblSchool.schoollogin, tblSchool.image, tblSchool.export, tblSchool.schoolRights, tblSchool.expire "
//            + "FROM tblUser, tblSchoolGroup, tblGroup, tblSchool "
//            + "WHERE "
//            + "AND   (userID = ?) AND (schoolID = ?)";
//
    // TODO V1_2 obsolete using QRY_ADD_USER_TO_SCHOOL now.
//    protected final static String QRY_ADD_TO_SCHOOL = "UPDATE tblUser "
//            + "SET schoolGroupID = ? " + "WHERE (userID = ?) ";
    /**
     * retrieves the default School user data.
     *
     * uses the default school in the tblUser.
     */
    // TODO DONE V1_3
    protected final static String QRY_SELECT_SCHOOL_USER = "SELECT tblSchool.* "
            + "FROM tblUser, tblSchoolGroup, tblSchool "
            + "WHERE (tblUser.schoolGroupID = tblSchoolGroup.schoolGroupID) "
            + "AND   (tblSchoolGroup.schoolID = tblSchool.schoolID) "
            + "AND   (tblUser.userID = ?) ";

    protected static final String SELECT_USERNAME_FROM_USERID = "select username, passwd from tblUser where userID=?";

    private final static String QRY_PASSWORD_CORRECT = "SELECT userID "
            + "FROM tblUser " + "WHERE (userID = ?) " + "AND   (passwd = ?) ";

// TODO V1_2 replaced by add to class and remove from class.
//    private final static String QRY_UPDATE_USER_CLASS = "UPDATE tblUser "
//            + "SET classID = ? " + "WHERE (userID = ?) ";
    /**
     * Adds a teacher to a class.
     */
    // TODO DONE V1_3
    private final static String QRY_ADD_TEACHER = "INSERT INTO tblTeacherOf(classID, userID) "
            + "VALUES(?, ?) ";

    /**
     * Adds a student to a class.
     */
    // TODO DONE V1_3
    private final static String QRY_ADD_STUDENT = "INSERT INTO tblStudentOf(classID, userID) "
            + "VALUES(?, ?) ";

//    /**
//     * Removes a teacher from a class.
//     */
//    // TODO V1_3 DONE
//    private final static String QRY_DELETE_TEACHER = "DELETE FROM tblTeacherOf "
//            + "WHERE classID = ? AND userID=? ";
//
//    /**
//     * Removes a student from a class.
//     */
//    // TODO V1_3 DONE
//    private final static String QRY_DELETE_STUDENT = "DELETE FROM tblStudentOf "
//            + "WHERE classID = ? AND userID=? ";

    private final static String QRY_UPDATE_USER = "UPDATE tblUser "
            + "SET firstname = ?, " + "middlename = ?, " + "lastname = ?, "
            + "passwd = ?, " + "email = ? " + "WHERE (userID = ?)";

//TODO V1_3 DONE fixed last login using QRY_UPDATE_USER_ROLE_LAST_LOGIN in code
    private final static String QRY_UPDATE_USER_LAST_LOGIN = "UPDATE tblUser "
            + "SET lastLogin = CURDATE() " + "WHERE (tblUser.userID = ?) ";

    private final static String QRY_UPDATE_USER_ROLE_LAST_LOGIN = "UPDATE tblHasRole "
            + "SET lastLogin = CURDATE() WHERE (userID = ? and schoolGroupID = ?) ";

    private final static String QRY_CLEAR_USER_ROLE_DEFAULT_CLASS = "UPDATE tblHasRole "
            + "SET classID = NULL WHERE (userID = ? and classID = ?) ";
    
    private final static String QRY_CLEAR_ALLUSERS_ROLE_DEFAULT_CLASS = "UPDATE tblHasRole "
            + "SET classID = NULL WHERE (classID = ?) ";
        
//TODO DONE V1_3 adjust lastLogin to be in hasRole, see source usage.
    private final static String QRY_UPDATE_HASROLE_LAST_LOGIN = "UPDATE tblHasRole "
            + "SET lastLogin = CURDATE() " + "WHERE (tblHasRole.userID = ? AND "
            + "tblHasRole.schoolGroupID = ?) ";

    private final static String QRY_UPDATE_USER_NO_PWD = "UPDATE tblUser "
            + "SET firstname = ?, " + "middlename = ?, " + "lastname = ?, "
            + "email = ? " + "WHERE (userID = ?)";

    private final static String QRY_ADD_CLASS = "INSERT INTO tblClass(schoolID, class) "
            + "VALUES(?, ?) ";

    private final static String QRY_ADD_SCHOOLID = "INSERT INTO tblSchool(schoolName, schoollogin, schoolID) "
            + "VALUES(?, ?, ?) ";

    private final static String QRY_SELECT_SCHOOL_FROM_SCHOOLLOGIN = "SELECT schoolID "
            + "FROM tblSchool "
            + "WHERE schoollogin = ?";

    private final static String QRY_DELETE_DEFAULT = "DELETE FROM `{0}` "
            + "WHERE `{1}` = ?";

    private final static String QRY_DELETE_STUDENT_FROM_CLASS_IN_SCHOOL
            = "delete from tblStudentOf join tblClass using (classID) where schoolID = ?";
    
    private final static String QRY_DELETE_TEACHER_FROM_CLASS_IN_SCHOOL
            = "delete from tblTeacherOf join tblClass using (classID) where schoolID = ?";

    private final static String QRY_DELETE_STUDENTSCO_FROM_SCHOOL
            = "Select * FROM tblStudentSco where scoID in "
            + "(select scoID from tblSco join tblCourse using (CourseID)  where schoolID = ? )";

    private final static String QRY_DELETE_SCO_FROM_SCHOOL
                = "delete tblScoContext, tblScoData from tblScoContext join tblScoData using (scoID) where courseID in (SELECT courseID FROM tblCourse WHERE schoolID = ?)";
    
//TODO V1_3 DONE Delete tblHasRole references removed in  QRY_DELETE_ROLES_FROM_SCHOOL
    private final static String QRY_DELETE_USERS_FROM_SCHOOL
            = "UPDATE tblUser SET schoolGroupID = NULL WHERE "
            + "SchoolGroupID in (SELECT schoolGroupID FROM tblSchoolGroup where schoolID = ?)";

    private final static String QRY_DELETE_ROLES_FROM_SCHOOL
            = "DELETE tblHasRole FROM tblHasRole join tblSchoolGroup on (schoolGroupID) WHERE "
            + "schoolID = ?)";
    
//TODO V1_3 DONE Usage verified.
    private final static String QRY_DELETE_STUDENTSCO_BY_STUDENT = "DELETE tblStudentScoContext, tblStudentScoData "
            + "FROM tblStudentScoContext join tblStudentScoData using (studentSco) WHERE (userID = ?) ";

    private final static String QRY_DELETE_STUDENTSCO_BY_SCO = "DELETE tblStudentScoContext, tblStudentScoData "
            + "FROM tblStudentScoContext join tblStudentScoData using (studentSco) WHERE (scoID = ?) ";

    private final static String QRY_DELETE_CLASS_TEACHER = "DELETE FROM tblTeacherOf WHERE userID = ? AND classID=?";

//TODO V1_3 DONE verify and fix tblHasRole default classID value
    private final static String QRY_DELETE_CLASS_STUDENT = "DELETE FROM tblStudentOf WHERE userID = ? AND classID=?";

//TODO V1_3 DONE verify and fix tblHasRole default classID value
    private final static String QRY_DELETE_STUDENTS_AND_TEACHERS_FROM_CLASS
            = "DELETE c,s,t FROM tblClass c JOIN tblStudentOf  s using (classID) JOIN tblTeacherOf t using (classID) where classID = ?";

//TODO DONE V1_3: Fetches default...
    private final static String QRY_SELECT_SCHOOL_FROM_USER = "SELECT schoolID "
            + "FROM tblUser, tblSchoolGroup "
            + "WHERE (tblUser.schoolGroupID = tblSchoolGroup.schoolGroupID) "
            + "AND   (tblUser.userID = ?) ";

//// TODO DONE V1_3
//    private final static String QRY_SELECT_SCHOOL_CLASS = "SELECT classID "
//            + "FROM tblClass " + "WHERE (schoolID = ?) " + "AND   (class = ?) ";
    /**
     * disconnects a user from a class
     */
//TODO V1_3 DONE verify and fix tblHasRole default classID value
    private final static String QRY_DISCONNECT_USER_CLASS = "DELETE FROM tblStudentOf,tblTeacherOf "
            + "WHERE classID = ? " + "AND userID = ? ";

    // TODO V1_3 DONE merge with
//     private final static String QRY_RESULTS_ALL
//            = "SELECT tblTeacherOf.classID, tblCourse.courseID, avg(score) as score, count(score) as totaal"
//            + "FROM (tblTeacherOf, tblCourse) join  tblStudentOf on tblStudentOf.classId =  tblTeacherOf.classId "
//            + "left join tblSco  on tblSco.courseId =  tblCourse.courseId "
//            + "left join  tblStudentSco on tblStudentSco.userid =   tblStudentOf.userId and tblStudentSco.scoId =   tblSco.scoId"
//            + "where (tblCourse.courseID in ({0} )"
//            + "and   (tblTeacherOf.userID = ?)"
//            + "group by tblTeacherOf.classID, tblCourse.courseID"
//            + "having tblTeacherOf.classID is not null"
//            + "ORDER BY tblTeacherOf.classID";
//    private final static String QRY_RESULTS_ALL = "SELECT tblClass.classID, tblCourse.courseID, avg(score) as score, count(score) as totaal "
//            + "FROM (tblClass, tblCourse) join  tblUser on tblUser.classId =  tblClass.classId "
//            + "left join tblScoContext  on tblScoContext.courseId =  tblCourse.courseId "
//            + "left join  tblStudentScoContext on tblStudentScoContext.userid =   tblUser.userId and tblStudentScoContext.scoId =   tblScoContext.scoId "
//            + "where (tblCourse.courseID in ({0})) "
//            + "and   (tblClass.userID = ?) "
//            + "group by tblClass.classID, tblCourse.courseID "
//            + "having tblClass.classID is not null "
//            + "ORDER BY tblClass.classID";
    private final static String QRY_RESULTS_ALL
            = "SELECT tblTeacherOf.classID, tblCourse.courseID, avg(score) as score, count(score) as totaal"
            + "FROM (tblTeacherOf, tblCourse) join  tblStudentOf on tblStudentOf.classId =  tblTeacherOf.classId "
            + "left join tblScoContext  on tblScoContext.courseId =  tblCourse.courseId "
            + "left join  tblStudentScoContext on tblStudentScoContext.userid =   tblStudentOf.userId and tblStudentScoContext.scoId =   tblScoContext.scoId"
            + "where (tblCourse.courseID in ({0} )"
            + "and   (tblTeacherOf.userID = ?)"
            + "group by tblTeacherOf.classID, tblCourse.courseID"
            + "having tblTeacherOf.classID is not null"
            + "ORDER BY tblTeacherOf.classID";

    /**
     * results of selected courses from a single user.
     */
    //TODO V1_4 fix for many school/role options 
    private final static String QRY_RESULTS_SINGLE = "SELECT tblStudentScoContext.userID, tblCourse.courseID, avg(score) as score, count(score) as totaal "
            + "FROM tblUser  "
//            + "left join tblStudentScoContext on tblStudentScoContext.userID = tblUser.userID "
            + "right join tblScoContext on tblStudentScoContext.scoID = tblScoContext.scoID "
            + "left join tblCourse on tblScoContext.courseID = tblCourse.courseID "
            + "where (tblCourse.courseID in ({0})) "
            + "and   (tblStudentScoContext.userID = ?) "
            + "group by tblCourse.courseID ";
    
    /**
     * Returns the results for a <course[], class, teacher> combination.
     */

    // TODO V1_3 DONE merge with new Context/Data StudentSco/Sco.
//    private final static String QRY_RESULTS_CLASS_COURSE = "SELECT tblStudentOf.userID, tblCourse.courseID, avg(score) as score, count(score) as totaal "
//            + "FROM (tblStudentOf, tblCourse) "
//            + "join tblTeacherOf on tblTeacherOf.classID = tblStudentOf.classID "
//            + "join tblSco on tblSco.courseID = tblCourse.courseID "
//            + "left join tblStudentSco on tblStudentSco.userID = tblStudentOf.userID and tblStudentSco.scoId = tblSco.scoId "
//            + "where (tblTeacherOf.classID = ? and tblTeacherOf.userID = ?) and (tblCourse.courseID = ?) "
//            + "group by tblStudentOf.userID, tblCourse.courseID ORDER BY tblStudentOf.userID";
    private final static String QRY_RESULTS_CLASS_COURSE = "SELECT tblStudentOf.userID, tblCourse.courseID, avg(score) as score, count(score) as totaal "
            + "FROM (tblStudentOf, tblCourse) "
            + "join tblTeacherOf on tblTeacherOf.classID = tblStudentOf.classID "
            + "join tblScoContext on tblScoContext.courseID = tblCourse.courseID "
            + "left join tblStudentScoContext on tblStudentScoContext.userID = tblStudentOf.userID and tblStudentScoContext.scoId = tblScoContext.scoId "
            + "where (tblTeacherOf.classID = ? and tblTeacherOf.userID = ?) and (tblCourse.courseID = ?) "
            + "group by tblStudentOf.userID, tblCourse.courseID ORDER BY tblStudentOf.userID";

//    private final static String QRY_RESULTS_CLASS_COURSE = "SELECT tblUser.userID, tblCourse.courseID, avg(score) as score, count(score) as totaal "
//            + "FROM (tblUser, tblCourse) "
//            + "join tblClass on tblClass.classID = tblUser.classID "
//            + "join tblScoContext on tblScoContext.courseID = tblCourse.courseID "
//            + "left join tblStudentScoContext on tblStudentScoContext.userID = tblUser.userID and tblStudentScoContext.scoId = tblScoContext.scoId "
//            + "where (tblUser.classID = ?) "
//            + "and (tblCourse.courseID = ?) "
//            + "group by tblUser.userID, tblCourse.courseID "
//            + "ORDER BY tblUser.userID";
//    private final static String QRY_RESULTS_CLASS = "SELECT tblUser.userID, tblCourse.courseID, avg(score) as score, count(score) as totaal "
//            + "FROM (tblUser, tblCourse) "
//            + "join tblClass on tblClass.classID = tblUser.classID "
//            + "join tblScoContext on tblScoContext.courseID = tblCourse.courseID "
//            + "left join tblStudentScoContext on tblStudentScoContext.userID = tblUser.userID and tblStudentScoContext.scoId = tblScoContext.scoId "
//            + "where (tblUser.classID = ?) "
//            + "and (tblCourse.courseID in ({0})) "
//            + "and   (tblClass.userID = ?) "
//            + "group by tblUser.userID, tblCourse.courseID "
//            + "ORDER BY tblUser.userID";
    //TODO DONE V1_3 fix sco table
    private final static String QRY_RESULTS_COURSE_PROFILE = "SELECT c.courseID, count(sco.scoid) FROM tblStudentScoContext ssco "
            + "join tblStudentOf stu using (userID)  "
            + "join tblScoContext course on (ssco.scoID = course.scoid) "
            + "join tblCourse c on (c.courseID = course.courseID) "
            + "WHERE stu.classID=? and  c.dwoProfileID = ? group by courseid";

    // TODO DONE V1_3
    private final static String QRY_RESULTS_STUDENT_COURSE = "SELECT tblStudentOf.userID, tblScoContext.scoID, tblScoContext.sequencenr,  if(score=0,-1,score) as score, total_time "
            + "FROM (tblStudentOf, tblScoContext)  join tblTeacherOf on tblTeacherOf.classID = tblStudentOf.classID "
            + "join tblCourse on tblScoContext.courseID = tblCourse.courseID "
            + "left join tblStudentScoContext on tblStudentScoContext.userID = tblStudentOf.userID and tblStudentScoContext.scoId = tblScoContext.scoId "
            + "where (tblStudentOf.classID = ?) " // student tblUser.classID =>tblStudentOf.classID 
            + "and (tblCourse.courseID = ?) " //course
            + "and   (tblTeacherOf.userID = ?) " //teacher tblClass.userID =>tblTeacherOf.userID 
            + "ORDER BY tblStudentOf.userID, tblScoContext.sequencenr";

    /**
     * Select the SCO scores of one student.
     */
    //TODO V1_4 fix for many school/role options 
    private static String QRY_RESULTS_SINGLE_STUDENT_COURSE
            = "SELECT tblUser.userID, tblScoContext.scoID, tblScoContext.sequencenr,  if(score=0,-1,score) as score, total_time "
            + "FROM ( tblScoContext, tblUser ) left join tblStudentScoContext on tblStudentScoContext.userID = tblUser.userID and tblStudentScoContext.scoID = tblScoContext.scoID "
            + "where tblUser.userID = ? and tblScoContext.courseID = ? "
            + "order by tblScoContext.sequencenr";

    // TODO V1_3 DONE merge:
//        private final static String QRY_RESULTS_COURSE = "SELECT tblTeacherOf.classID, tblSco.scoID, tblSco.sequencenr, "
//            + "avg(score) as score, count(score) as totaal "
//            + "FROM (tblTeacherOf, tblSco) "
//            + "join tblStudentOf on tblTeacherOf.classID = tblStudentOf.classID "
//            + "left join tblStudentSco on tblStudentSco.userID = tblStudentOf.userID and tblStudentSco.scoID = tblSco.scoID "
//            + "where  (tblSco.courseID = ?) "
//            + "and   (tblTeacherOf.userID = ?) "
//            + "group by tblTeacherOf.classID, tblSco.scoID "
//            + "ORDER BY tblTeacherOf.classID, tblSco.sequencenr";
//    private final static String QRY_RESULTS_COURSE = "SELECT tblClass.classID, tblScoContext.scoID, tblScoContext.sequencenr, avg(score) as score, count(score) as totaal "
//            + "FROM (tblClass, tblScoContext) join tblUser on tblClass.classID = tblUser.classID "
//            + "left join tblStudentScoContext on tblStudentScoContext.userID = tblUser.userID and tblStudentScoContext.scoID = tblScoContext.scoID "
//            + "where  (tblScoContext.courseID = ?) "
//            + "and   (tblClass.userID = ?) "
//            + "group by tblClass.classID, tblScoContext.scoID "
//            + "ORDER BY tblClass.classID, tblScoContext.sequencenr";
   //TODO V1_3 DONE RELATED TO COURSE
     private final static String QRY_RESULTS_COURSE = "SELECT tblTeacherOf.classID, tblScoContext.scoID, tblScoContext.sequencenr, "
            + "avg(score) as score, count(score) as totaal "
            + "FROM (tblTeacherOf, tblScoContext) "
            + "join tblStudentOf on tblTeacherOf.classID = tblStudentOf.classID "
            + "left join tblStudentScoContext on tblStudentScoContext.userID = tblStudentOf.userID and tblStudentScoContext.scoID = tblScoContext.scoID "
            + "where  (tblScoContext.courseID = ?) "
            + "and   (tblTeacherOf.userID = ?) "
            + "group by tblTeacherOf.classID, tblScoContext.scoID "
            + "ORDER BY tblTeacherOf.classID, tblScoContext.sequencenr";

    private final static String QRY_UPDATE_CLASS_NAME = "UPDATE tblClass "
            + "SET class = ? " + "WHERE (classID = ?) ";
    private final static String QRY_UPDATE_CLASS_NAME2 = "UPDATE tblClass "
            + "SET class = ?, iconizer = ? " + "WHERE (classID = ?) ";
    private final static String QRY_UPDATE_CLASS_NAME3 = "UPDATE tblClass "
            + "SET class = ?, registrationKey = ?, iconizer = ? " + "WHERE (classID = ?) ";

    // TODO DONE V1_3
//    private final static String QRY_UPDATE_CLASS_USER = "UPDATE tblClass "
//            + "SET userID = ? WHERE (classID = ?)";
    private final static String QRY_UPDATE_SCHOOL = "UPDATE tblSchool "
            + "SET schoolName = ?, " + "schoollogin = ? " + "WHERE (schoolID = ?) ";

    private final static String QRY_UPDATE_SCHOOL2 = "UPDATE tblSchool "
            + "SET export = ? WHERE (schoolID = ?) ";

    private final static String QRY_UPDATE_SCHOOL3 = "UPDATE tblSchool "
            + "SET schoolRights = ? WHERE (schoolID = ?) ";

    private final static String QRY_UPDATE_SCHOOLGROUP_PASSW = "UPDATE tblSchoolGroup "
            + "SET passwd = ? " + "WHERE (schoolGroupID = ?) ";

//    private final static String QRY_JAR_INSERT_KEY = "INSERT INTO tblJars(`key`, `jarname`, `lastDate`) "
//            + "VALUES(?, ?, CURDATE()) ";
//    private final static String QRY_JAR_UPDATE_KEY = "UPDATE tblJars "
//            + "SET `jarname` = ?, lastDate = CURDATE() " + "WHERE `key` = ?";
//    private final static String QRY_JAR_SELECT_KEY = "SELECT jarname FROM tblJars where `key` = ?";
//    private final static String QRY_JAR_COUNT_JARS = "SELECT count(*) as number FROM tblApplet ";
    private final static String QRY_ADD_COURSE = "INSERT INTO tblCourse(schoolID, name, description, image, dwoProfileID, parentID, withChildren) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?) ";

    private final static String QRY_ADD_COURSE_BASIC = "INSERT INTO tblCourse(name, description, image, dwoProfileID, parentID, withChildren) "
            + "VALUES(?, ?, ?, ?, ?, ?) ";

    private final static String QRY_UPDATE_COURSE = "UPDATE tblCourse "
            + "SET name = ?, " + "description = ? " + "WHERE (courseID = ?) ";
    private final static String QRY_UPDATE_COURSE2 = "UPDATE tblCourse "
            + "SET name = ?, description = ?, export = ? WHERE (courseID = ?) ";
    private final static String QRY_UPDATE_COURSE3 = "UPDATE tblCourse "
            + "SET name = ?, description = ?, export = ?, schoolID = ? WHERE (courseID = ?) ";
    private final static String QRY_UPDATE_COURSE4 = "UPDATE tblCourse "
            + "SET name = ?, description = ?, export = ?, schoolID = ?, parentID = ? WHERE (courseID = ?) ";
    private final static String QRY_ADD_SCO_CONTEXT = "INSERT INTO tblScoContext(courseID, appletID, sconame, sequencenr) "
            + "VALUES(?, ?, ?, ?) ";
    private final static String QRY_ADD_SCO_DATA = "INSERT INTO tblScoData(scoID, description, launchdata) "
            + "VALUES(?, ?, ?) ";

    private final static String QRY_UPDATE_SCO = "UPDATE tblScoContext, tblScoData "
            + "SET tblScoContext.sconame = ?, "
            + "tblScoData.description = ?, "
            + "tblScoData.launchdata = ? "
            + "WHERE (tblScoContext.scoID = ? and tblScoContext.scoID = tblScoData.scoID)  ";
    private final static String QRY_UPDATE_SCO2 = "UPDATE tblScoContext, tblScoData "
            + "SET tblScoContext.sconame = ?, "
            + "tblScoData.description = ? "
            + "WHERE (tblScoContext.scoID = ? and tblScoContext.scoID = tblScoData.scoID) ";
    private final static String QRY_UPDATE_SCO3 = "UPDATE tblScoContext, tblScoData "
            + "SET tblScoContext.sconame = ?, "
            + "tblScoData.description = ?, "
            + "tblScoContext.showscore = ? "
            + "WHERE (tblScoContext.scoID = ? and tblScoContext.scoID = tblScoData.scoID) ";

    private final static String QRY_UPDATE_SCO_SEQUENCE = "UPDATE tblScoContext "
            + "SET sequencenr = sequencenr - 1 " + "WHERE (sequencenr > ?) "
            + "AND   (courseid = ?) ";

    private final static String QRY_SELECT_TO_SCHOOLS_FROM = "select * from tblSchoolFrom where schoolFrom = ? ";

// TODO false bij een export.    
    /**
     *
     */
    static public final boolean DEBUG = false;

    /**
     * @param check
     */
    protected DbAccess(boolean check) {
        super(MYSQL2_SCIENCE_FISME, "dwo");
        if (check && checkVersion()) {
            throw new RuntimeException("old sofware trying to use new database.");
        }
    }

    /**
     *
     */
    public DbAccess() {
        this(true);
    }

    /**
     * Checks if we support the proper database data model.
     *
     * @return
     */
    public boolean checkVersion() {
        try {
            if (DEBUG) {
                log.log(Level.INFO, "Dbacces DEBUG aan");
            }
            //check for proper DB version
            PreparedStatement ps = getStatement("select * from tblDWOSystemParameters where name like 'DBVersion%'");
            ResultSet rs = ps.executeQuery();
            HashMap<String, String> hashMap = new HashMap<String, String>(5);
            while (rs.next()) {
                hashMap.put(rs.getString("name"), rs.getString("value"));
            }

            if (hashMap.get("DBVersion Major").matches("1") && hashMap.get("DBVersion Minor").matches("3")) {
                log.log(Level.INFO, "We are compatible with the database model version: {0}.{1}.{2}",
                        new Object[]{hashMap.get("DBVersion Major"),
                            hashMap.get("DBVersion Minor"),
                            hashMap.get("DBVersion Revision")});

            } else {
                log.log(Level.SEVERE, "Database version of server not compatible with v1.3.x. Exiting.");
                return true;
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Database model version of server not compatible with v1.3.x. Missing version numbers. Exiting.", ex);
            return true;
        }
        return false; // all ok...
    }

    /**
     * @param tableName
     * @param idCol
     * @param oid
     * @return java.util.Hashtable
     * @throws java.sql.SQLException
     */
    @Override
    public Hashtable getRecord(String tableName, String idCol, int oid)
            throws SQLException {
        String[] arguments = {tableName, idCol};
        String query = MessageFormat.format(QRY_DEFAULT_SELECT_ID, (Object[]) arguments);
        PreparedStatement ps = getStatement(query);
        ps.setInt(1, oid);

        return executeQueryWithRecord(ps);
    }

    /**
     * @param tableName The tablename of the table to select data.
     * @return java.util.Vector The vector contains Hashtables who contains the
     * rows mapped on the columnname.
     * @throws java.sql.SQLException
     */
    @Override
    public Vector getTable(String tableName) throws SQLException {
        String[] arguments = {tableName};
        String query = MessageFormat
                .format(QRY_DEFAULT_SELECT_TABLE, (Object[]) arguments);

        return executeQueryWithResult(query);
    }

    /**
     * @param tableName The tablename of the table to select data.
     * @return java.util.Vector The vector contains Hashtables who contains the
     * rows mapped on the columnname.
     * @throws java.sql.SQLException
     */
    @Override
    public Vector getTable(String tableName, String orderCol) throws SQLException {
        String[] arguments = {tableName, orderCol};
        String query = MessageFormat
                .format(QRY_DEFAULT_SELECT_TABLE_ORDER, arguments);

        //log("DbAccess.getTable " + query);
        return executeQueryWithResult(query);
    }

    /**
     * @param tableName The tablename of the table to select data.
     * @param wheredef
     * @param orderBy
     * @return java.util.Vector The vector contains Hashtables who contains the
     * rows mapped on the columnname.
     * @throws java.sql.SQLException
     */
    @Override
    public Vector getTable(String tableName, Hashtable wheredef, String orderBy)
            throws SQLException {
        String[] arguments = {tableName};
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
            query += " ORDER BY " + orderBy;

        }

        PreparedStatement ps = getStatement(query);

        for (i = 0; i < items.length; i++) {
            ps.setObject(i + 1, wheredef.get(items[i]));
        }
        return executeQueryWithResult(ps);
    }

    @Override
    public Vector getTable(String tableName, Vector columnNames, Hashtable wheredef, String orderBy)
            throws SQLException {
        StringBuffer sb = new StringBuffer("SELECT ");
        String[] items = new String[wheredef.size()];
        int i = 0;
        Enumeration names = columnNames.elements();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            if (i++ != 0) {
                sb.append(" , ");
            }
            sb.append(name);
        }
        sb.append(" FROM ");
        sb.append(tableName);

        Enumeration keys = wheredef.keys();
        i = 0;
        while (keys.hasMoreElements()) {
            String item = (String) keys.nextElement();
            if (i != 0) {
                sb.append(" AND ");
            } else {
                sb.append(" WHERE ");
            }
            items[i++] = item;
            sb.append(item);
            sb.append("= ?");
        }

        if ((orderBy != null) && (!orderBy.equals(""))) {
            sb.append(" ORDER BY ");
            sb.append(orderBy);
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
     * @param username the username to check.
     * @return If there exists a user with the specified username, true is
     * returned. Otherwise, false is returned.
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
     * return null or userID as Number.
     *
     * @param username
     * @return userID as a number
     * @throws SQLException
     */
    protected Number getUserID(String username) throws SQLException {
        PreparedStatement ps = getStatement(QRY_CHECK_USERNAME_EXISTS);
        ps.setString(1, username);
        Hashtable h = executeQueryWithRecord(ps);
        if (h != null) {
            return (Number) h.get("userID");
        }
        return null;
    }

    /**
     * Checks if a schoolLogin already exists in the database
     *
     * @param schoolLogin the schoolLogin to check.
     * @return If there exists a school with the specified schoolLogin, true is
     * returned. Otherwise, false is returned.
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

    /**
     *
     * @param schoollogin
     * @param groupID
     * @param password
     * @return
     * @throws SQLException
     */
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
            if (checkValidLicence(rs.getDate(2), rs.getInt(3))) {
                return -1;
            }
        }
        rs.close();
        return schoolGroupID;

    }

    /**
     *
     * @param date
     * @param int1
     * @return
     */
    protected boolean checkValidLicence(Date date, int int1) {
        if (date == null) {
            return false;
        }
        return date.getTime() < System.currentTimeMillis();
    }

    /**
     * Add a new user without membership to a school.
     * 
     * 
     *
     * @param username
     * @param password
     * @param firstname
     * @param middlename
     * @param lastname
     * @param email
     * @return boolean
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     * @throws java.sql.SQLException
     */
    @Override
    public boolean register(String username, String password, String firstname,
            String middlename, String lastname, String email)
            throws DwoXmlRpcException, SQLException {
        if (usernameExists(username)) {
            throw new DwoXmlRpcException(DwoXmlRpcException.EXC_USER_EXISTS);
        } else {
            // insert user data
            PreparedStatement ps = getStatement(QRY_INSERT_USER);
            ps.setString(1, firstname);
            ps.setString(2, middlename);
            ps.setString(3, lastname);
            ps.setString(4, username);
            ps.setString(5, password);
            ps.setString(6, email);

            ps.execute();
        }
        return true;
    }

    /**
     * Add a new user and the user into a school.
     *
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
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     * @throws java.sql.SQLException
     */
    //TODO V1_3 DONE Added defaults and hasRole insertion
    @Override
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

                Connection c = getConnection();
                c.setAutoCommit(false);
                //First add user
                PreparedStatement ps = getStatement(QRY_INSERT_USER_SCHOOLGROUP);
                ps.setInt(1, schoolGroupID);
                ps.setString(2, firstname);
                ps.setString(3, middlename);
                ps.setString(4, lastname);
                ps.setString(5, username);
                ps.setString(6, password);
                ps.setString(7, email);

                try {
                    ps.execute();
                } catch (SQLException e) {
                    if (e.getErrorCode() == 1062) {
                        /* The user already exists */
                        throw new DwoXmlRpcException(
                                DwoXmlRpcException.EXC_USER_EXISTS);
                    } else {
                        throw e;
                    }
                }

                ResultSet rs = ps.getGeneratedKeys();
                int id = -1;
                if (rs.next()) {
                    id = rs.getInt(1);
                    ps = getStatement(QRY_ADD_USER_TO_SCHOOL);

                    ps.setInt(1, id);
                    ps.setInt(2, schoolGroupID);

                    ps.execute();
                    int count = ps.getUpdateCount();
                    if (count != 1) {
                        log.log(Level.SEVERE, "Error while adding user {0} to schoolgroup {1}.", new Object[]{id, schoolGroupID});
                        id = -1;
                        c.rollback();
                    }
                } else {
                    StringBuilder userData = new StringBuilder();
                    ps.setString(1, firstname);
                    ps.setString(2, middlename);
                    ps.setString(3, lastname);
                    ps.setString(4, username);
                    ps.setString(5, password);
                    ps.setString(6, email);
                    userData.append(firstname);
                    userData.append(" ");
                    userData.append(middlename);
                    userData.append(" ");
                    userData.append(lastname);
                    userData.append(" ");
                    userData.append(username);
                    userData.append(" ");
                    if (log.getLevel() == Level.FINEST) {
                        userData.append(password);// only log unencrypted password at highest log level.
                        userData.append(" ");
                    }
                    userData.append(email);
                    log.log(Level.SEVERE, "Error while adding user data to the datbase: {0}", new Object[]{userData});
                    c.rollback();
                }
                c.commit();
            }
        }
        return true;
    }

    /**
     * @param username
     * @param password
     * @return java.util.Hashtable
     * @throws java.sql.SQLException
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     */
    @Override
    public Hashtable login(String username, String password)
            throws SQLException, DwoXmlRpcException {
        close(); //for lazy connection
        boolean noPw = password.equals("");
        //This needs a fix, the whole login gui part needs changes.
        PreparedStatement ps = getStatement(noPw ? QRY_USER_LOGIN_NO_PASSWD : QRY_USER_LOGIN);
        ps.setString(1, username);
        if (!noPw) {
            ps.setString(2, password);
        }
        return login_tail(ps);
    }

    private Hashtable login_tail(PreparedStatement ps) throws SQLException,
            DwoXmlRpcException {
        Hashtable result = executeQueryWithRecord(ps);

        if (result == null || result.isEmpty()) {
            throw new DwoXmlRpcException(DwoXmlRpcException.EXC_UNKNOWN_USER);
        } else {
            Object tmp = result.get("schoolGroupID");
            ps.close();

            /* Update the Last Login date */
            Integer userid = ((Integer) result.get("userID")).intValue();
            ps = getStatement(QRY_UPDATE_USER_LAST_LOGIN);
            ps.setInt(1, userid);
            ps.execute();
            ps = getStatement(QRY_UPDATE_USER_ROLE_LAST_LOGIN);
            ps.setInt(1, userid);
            ps.setInt(2, (Integer) this.getCurSchoolGroup(userid));
            ps.execute();

            if (!(tmp instanceof String)) { //null-data is an empty string, so
                // if this is a string, it was null
                ps.close();
                /* Update the Last Login in HasRole date */
                ps = getStatement(QRY_UPDATE_HASROLE_LAST_LOGIN);
                ps.setInt(1, ((Integer) result.get("userID")).intValue());
                ps.setInt(2, ((Integer) result.get("schoolGroupID")).intValue());
                ps.execute();
                //Request default schoold ID data. Ensure in add school default field is set.
                ps = getStatement(QRY_GET_USER_DATA);
                ps.setInt(1, ((Integer) result.get("userID")).intValue());
                Hashtable result2 = executeQueryWithRecord(ps);
                if (result2 != null) {
                    result.putAll(result2);
                }
            }
            result.put("timestamp", String.valueOf(System.currentTimeMillis()));
        }

        return result;
    }

    /**
     * @param userID
     * @param password
     * @return java.util.Hashtable
     * @throws java.sql.SQLException
     *
     */
    protected boolean passwordCorrect(int userID, String password)
            throws SQLException {
        PreparedStatement ps;
        if (password.length() == 0) {
            ps = getStatement(SELECT_USERNAME_FROM_USERID);
            ps.setInt(1, userID);
        } else {
            ps = getStatement(QRY_PASSWORD_CORRECT);
            ps.setInt(1, userID);
            ps.setString(2, password);
        }
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
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     * @throws java.sql.SQLException
     *
     */
    @Override
    public Hashtable addToSchool(int userID, String schoolLogin, int groupID,
            String groupPassword) throws DwoXmlRpcException, SQLException {
        Hashtable result = null;
        int schoolGroupID = schoolGroupExists(schoolLogin, groupID,
                groupPassword);
        if (schoolGroupID == -1) {
            throw new DwoXmlRpcException(
                    DwoXmlRpcException.EXC_UNKNOWN_SCHOOLGROUP);
        } else {
            PreparedStatement ps = getStatement(QRY_ADD_USER_TO_SCHOOL);
            ps.setInt(1, userID);
            ps.setInt(2, schoolGroupID);
            ps.execute();
            ps.close();

            ps = getStatement(QRY_UPDATE_DEFAULT_SCHOOLGROUP);
            ps.setInt(2, schoolGroupID);
            ps.setInt(1, userID);
            ps.execute();
            ps.close();

            ps = getStatement(QRY_SELECT_SCHOOL_USER);
            ps.setInt(1, userID);

            result = executeQueryWithRecord(ps);
        }

        return result;
    }

//    /**
//     * @param userID
//     * @param password
//     * @param newPassword
//     * @param firstname
//     * @param middlename
//     * @param lastname
//     * @param email
//     * @param classID
//     * @return boolean
//     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
//     * @throws java.sql.SQLException
//     *
//     */
//    @Override
//    public boolean changeAccount(int userID, String password,
//            String newPassword, String firstname, String middlename,
//            String lastname, String email)
//            throws DwoXmlRpcException, SQLException {
//        if (!passwordCorrect(userID, password)) {
//            throw new DwoXmlRpcException(
//                    DwoXmlRpcException.EXC_WRONG_USERNAME_PASSWORD);
//        } else {
//            // Changes account TODO nieuwe klas wordt altijd gezet 
//            PreparedStatement ps = getStatement(QRY_UPDATE_USER_CLASS);
//            if (classID != 0) {
//                ps.setInt(1, classID);
//            } else {
//                ps.setNull(1, Types.INTEGER);
//            }
//            ps.setInt(2, userID);
//
//            ps.executeUpdate();
//            ps.close();
//
//            return changeAccount(userID, password, newPassword, firstname, middlename,
//                    lastname, email);
//
//        }
//        //return true;
//    }
    /**
     * @param userID
     * @param password
     * @param newPassword
     * @param firstname
     * @param middlename
     * @param lastname
     * @param email
     * @return boolean
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     * @throws java.sql.SQLException
     *
     */
    @Override
    public boolean changeAccount(int userID, String password,
            String newPassword, String firstname, String middlename,
            String lastname, String email) throws DwoXmlRpcException,
            SQLException {
        if (!passwordCorrect(userID, password)) {
            throw new DwoXmlRpcException(
                    DwoXmlRpcException.EXC_WRONG_USERNAME_PASSWORD);
        } else {
            PreparedStatement ps;
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
     * Adds a class to the school and adds the teacher to the class.
     *
     * @param teacher
     * @param className
     * @return java.util.Hashtable
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     * @throws fi.dwo.server.persistence.DwoXmlRpcException
     * @throws java.sql.SQLException
     * @@
     *
     */
    @Override
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

            // Add class a class to the school
            ps = getStatement(QRY_ADD_CLASS);
            ps.setInt(1, schoolID);
            ps.setString(2, className);

            try {
                ps.execute();
                log.log(Level.FINE, "Added teacher {0} to class named {1} in school {2}.", new Object[]{teacher, className, schoolID});
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    /* The class already exists */
                    log.log(Level.FINE, "Class {0} already exists in school {1}.", new Object[]{className, schoolID});
                    throw new DwoXmlRpcException(
                            DwoXmlRpcException.EXC_CLASS_EXISTS);
                } else {
                    //log.log(Level.INFO, "Exception adding teacher {0} to class named {1} of school {2}: {3}", new Object[]{teacher, className, schoolID, e.getMessage()});
                    throw e;
                }
            }

            rs = ps.getGeneratedKeys();

            if (!isEmpty(rs)) {
                int classID = rs.getInt(1);
                result = getRecord("tblClass", "classID", classID);
                // By definition an empty class exists now, add the Teacher
                ps = getStatement(QRY_ADD_TEACHER);
                ps.setInt(1, classID);
                ps.setInt(2, teacher);
                ps.execute();
                log.log(Level.FINE, "Added teacher {0} to class {1} of school {2}.", new Object[]{teacher, className, schoolID});
            }
            rs.close();
        }

        return result;
    }

    @Override
    public Vector<Object> getStudentsOfClass(int schoolClassID) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        PreparedStatement ps = getStatement(QRY_SELECT_CLASSSTUDENTS_OF_CLASS);
        ps.setInt(1, schoolClassID);
        Vector v = executeQueryWithResult(ps);
        log.log(Level.FINE, "Retrieved ClassStudent's of SchoolClass {0}.", new Object[]{schoolClassID});
        return v;
    }

    @Override
    public Vector<Object> getTeachersOfClass(int schoolClassID) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        PreparedStatement ps = getStatement(QRY_SELECT_TEACHERS_OF_CLASS);
        ps.setInt(1, schoolClassID);
        Vector v = executeQueryWithResult(ps);
        log.log(Level.FINE, "Retrieved Teacher's of SchoolClass {0}.", new Object[]{schoolClassID});
        return v;
    }

    /**
     * @param schoolName
     * @param schoolLogin
     * @param studentPassw
     * @param teacherPassw
     * @return java.util.Hashtable
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     * @throws java.sql.SQLException
     * @deprecated Gebruik expliciet nummer.
     */
    @Override
    public Hashtable addSchool(String schoolName, String schoolLogin, String studentPassw, String teacherPassw)
            throws DwoXmlRpcException, SQLException {
        return addSchool(0, schoolName, schoolLogin, studentPassw, teacherPassw);
    }

    /**
     * Add an existing user to a school.
     *
     * @param schoolID
     * @param schoolName
     * @param schoolLogin
     * @param studentPassw
     * @param teacherPassw
     * @return java.util.Hashtable
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     * @throws java.sql.SQLException
     *
     */
    @Override
    public Hashtable addSchool(int schoolID, String schoolName, String schoolLogin, String studentPassw, String teacherPassw)
            throws DwoXmlRpcException, SQLException {
        Hashtable result = null;
        if (schoolLoginExists(schoolLogin)) {
            throw new DwoXmlRpcException(DwoXmlRpcException.EXC_SCHOOL_EXISTS);
        } else {
            PreparedStatement ps = getStatementWithGeneratedKeys(QRY_ADD_SCHOOLID);
            ps.setString(1, schoolName);
            ps.setString(2, schoolLogin);
            ps.setInt(3, schoolID);
            ps.execute();
            if (schoolID == 0) {
                ResultSet rs = ps.getGeneratedKeys();
                rs.first();
                schoolID = rs.getInt(1);
                rs.close();
            }
            if (studentPassw != null && !studentPassw.trim().equals("")) {
                ps = getStatement(QRY_INSERT_SCHOOLGROUP);
                ps.setInt(1, 1);
                ps.setInt(2, schoolID);
                ps.setString(3, studentPassw);

                ps.execute();
                ps.close();
            }
            if (teacherPassw != null && !teacherPassw.trim().equals("")) {
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
     * Add an existing user to a school.
     *
     * @param schoolID
     * @param schoolName
     * @param schoolLogin
     * @param passwdMap
     * @return
     * @throws DwoXmlRpcException
     * @throws SQLException
     */
    @Override
    public Hashtable addSchool(int schoolID, String schoolName, String schoolLogin, Hashtable passwdMap)
            throws DwoXmlRpcException, SQLException {
        Hashtable result = null;
        if (schoolLoginExists(schoolLogin)) {
            throw new DwoXmlRpcException(DwoXmlRpcException.EXC_SCHOOL_EXISTS);
        } else {
            PreparedStatement ps = getStatementWithGeneratedKeys(QRY_ADD_SCHOOLID);
            ps.setString(1, schoolName);
            ps.setString(2, schoolLogin);
            ps.setInt(3, schoolID);
            ps.execute();
            if (schoolID == 0) {
                ResultSet rs = ps.getGeneratedKeys();
                rs.first();
                schoolID = rs.getInt(1);
                rs.close();
            }
        }
        updateSchoolGroupPasswdMap(schoolID, passwdMap);
        result = getRecord("tblSchool", "schoolID", schoolID);
        return result;
    }

    /**
     * @param schoolName
     * @param schoolLogin
     * @param studentPassw
     * @param teacherPassw
     * @return java.util.Hashtable
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     * @throws java.sql.SQLException
     *
     */
    @Override
    public Hashtable editSchool(int schoolID, String schoolName, String schoolLogin, String studentPassw, String teacherPassw)
            throws DwoXmlRpcException, SQLException {
        Hashtable result = getRecord("tblSchool", "schoolID", schoolID);
        updateSchoolNameLogin(schoolID, schoolName, schoolLogin, result);
        updateSchoolGroupPasswd(schoolID, SchoolGroupRoles.STUDENT, studentPassw);
        updateSchoolGroupPasswd(schoolID, SchoolGroupRoles.TEACHER, teacherPassw);
        result = getRecord("tblSchool", "schoolID", schoolID);
        return result;
    }

    private void updateSchoolGroupPasswd(int schoolID, int groupID, String passwd)
            throws SQLException {
        PreparedStatement ps;
        ps = getStatement(QRY_CHECK_SCHOOLGROUP_EXISTS);
        ps.setInt(1, schoolID);
        ps.setInt(2, groupID);

        ResultSet rs = ps.executeQuery();
        int schoolGroupID;
        if (!isEmpty(rs)) {
            rs.first();
            schoolGroupID = rs.getInt("schoolGroupID");
            String passwdOld = rs.getString("passwd");
            if (!passwdOld.equals(passwd)) {
                ps = getStatement(QRY_UPDATE_SCHOOLGROUP_PASSW);
                ps.setString(1, passwd);
                ps.setInt(2, schoolGroupID);
                ps.execute();
                ps.close();
            }
        } else {
            ps = getStatement(QRY_INSERT_SCHOOLGROUP);
            ps.setInt(1, groupID);
            ps.setInt(2, schoolID);
            ps.setString(3, passwd);
            ps.execute();
            ps.close();
        }
    }

    private void updateSchoolNameLogin(int schoolID, String schoolName,
            String schoolLogin, Hashtable result) throws SQLException {
        String schoolNameOld = (String) result.get("schoolName");
        String schoolLoginOld = (String) result.get("schoollogin");
        PreparedStatement ps;
        if (!schoolName.equals(schoolNameOld) || !schoolLoginOld.equals(schoolLogin)) {
            ps = getStatement(QRY_UPDATE_SCHOOL);
            ps.setString(1, schoolName);
            ps.setString(2, schoolLogin);
            ps.setInt(3, schoolID);

            ps.execute();
            ps.close();
        }
    }

    /**
     *
     * @param schoolID
     * @param schoolName
     * @param schoolLogin
     * @param passwdMap
     * @return
     * @throws SQLException
     */
    @Override
    public Hashtable editSchool(int schoolID, String schoolName, String schoolLogin, Hashtable passwdMap) throws SQLException {
        Hashtable result;
        result = getRecord("tblSchool", "schoolID", schoolID);
        updateSchoolNameLogin(schoolID, schoolName, schoolLogin, result);
        updateSchoolGroupPasswdMap(schoolID, passwdMap);
        result = getRecord("tblSchool", "schoolID", schoolID);
        return result;
    }

    private void updateSchoolGroupPasswdMap(int schoolID, Hashtable passwdMap)
            throws SQLException {
        Set entries = passwdMap.entrySet();
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            //int groupID = ((Number) entry.getKey()).intValue();
            int groupID = Integer.parseInt(entry.getKey().toString());
            String passwd = entry.getValue().toString();
            updateSchoolGroupPasswd(schoolID, groupID, passwd);
        }
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
     * @return
     * @throws java.sql.SQLException
     *
     */
    @Override
    public boolean deleteUser(int userID) throws SQLException {
// Student suspend_data
        PreparedStatement ps = getStatement(QRY_DELETE_STUDENTSCO_BY_STUDENT);
        ps.setInt(1, userID);
        ps.execute();
        ps.close();
//        String[] arguments = {"tblStudentSco", "userID"};
//        String query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments);
//        PreparedStatement ps = getStatement(query);
//        ps.setInt(1, userID);
//        ps.execute();
//        ps.close();

// Link aan SAML
        String[] arguments = {"tblSamlUser", "userID"};
        String query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments);
        ps = getStatement(query);
        ps.setInt(1, userID);
        ps.execute();
        ps.close();
// Link aan tblStudentOfClass
        arguments[0] = "tblStudentOf";
        query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments);
        ps = getStatement(query);
        ps.setInt(1, userID);
        ps.execute();
        ps.close();
        // Link aan tblTeacherOfClass
        arguments[0] = "tblTeacherOf";
        query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments);
        ps = getStatement(query);
        ps.setInt(1, userID);
        ps.execute();
        ps.close();
        // Link aan tblHasRole
        arguments[0] = "tblHasRole";
        query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments);
        ps = getStatement(query);
        ps.setInt(1, userID);
        ps.execute();
        ps.close();
// user zelf        
        arguments[0] = "tblUser";
        query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments);
        ps = getStatement(query);
        ps.setInt(1, userID);
        ps.execute();
        ps.close();

        return true;
    }

    /**
     * Deletes a class, and disconnect the students in it.
     *
     * @param classID The class to delete.
     * @param mustEmpty If true, the class will be checked if there are students
     * in. If so, the class is not deleted and false is returned.
     * @return boolean Indicates if the class is deleted or not.
     * @throws java.sql.SQLException
     *
     */
    @Override
    public boolean deleteClass(int classID, boolean mustEmpty)
            throws SQLException {
        boolean canDelete = !mustEmpty;
        String query;
        PreparedStatement ps;
        // needs to be replaced by QRY_DELETE_CLASS_IF_EMPTY and QRY_DELETE_CLASS_AND_TEACHERS_AND_STUDENTS
        if (mustEmpty) {
            /* Check for students in the class */
            String[] arguments = {"tblUser", "classID"};
            query = MessageFormat.format(QRY_DEFAULT_SELECT_ID, (Object[]) arguments);

            ps = getStatement(query);
            ps.setInt(1, classID);

            ResultSet rs = ps.executeQuery();
            log.log(Level.FINE, "Class must be empty for deletion and there are {0} students in the class with id {1}.", new Object[]{ps.getUpdateCount() - 1, classID});

            canDelete = isEmpty(rs); // no students
            //students
            rs.close();
        }

        if (canDelete) {
            // do multi table delete
            // In example delete c,u from tblClass c join tbluser u  where c.classID =5219 and u.classID = 5219
            // Disconnect students from the class and simultaneous delete the students from the class 
            ps = getStatement(QRY_DELETE_STUDENTS_AND_TEACHERS_FROM_CLASS);
            ps.setInt(1, classID);
            ps.execute();
            log.log(Level.FINE, "Deleted totally {0} rows, in tblClass, tblTeacherOf and tblStudentOf.", new Object[]{ps.getUpdateCount() - 1, classID});

            
            ps = getStatement(QRY_CLEAR_ALLUSERS_ROLE_DEFAULT_CLASS);
            ps.setInt(1, classID);
            ps.execute();
            ps.close();
            
            // TODO merge this line with above multi-table delete
            Object[] arguments2 = {"tblClassCourse", "classID"};
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
     * @param schoolClassID
     * @return
     * @throws java.sql.SQLException
     *
     */
    @Override
    public boolean disconnectFromClass(int userID, int schoolClassID) throws SQLException {
        PreparedStatement ps = getStatement(QRY_DISCONNECT_USER_CLASS);
        ps.setInt(1, userID);
        ps.setInt(2, schoolClassID);
        ps.execute();

        ps = getStatement(QRY_CLEAR_USER_ROLE_DEFAULT_CLASS);
        ps.setInt(1, userID);
        ps.setInt(2, schoolClassID);
        ps.execute();
        
        ps.close();

        return true;
    }

    /**
     * Executes a prepared statement, and returns a valid xml-rpc value (a
     * Hashtable) for the first row The row is a hashtable, where the key is the
     * columnname
     *
     * @param ps The prepared statement. All the parameters must be prepared
     * @return A hashtable where every key is the columnname.
     * @throws SQLException
     */
    public Hashtable executeQueryWithRecord(PreparedStatement ps)
            throws SQLException {
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsMeta = rs.getMetaData();
        String colName;
        Hashtable h;
        h = new Hashtable();	// NEVER RETURN NULL WIM, XMLRPC errors expected
// if not found, return empty map        
        if (!isEmpty(rs)) {
            //h = new Hashtable();
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
     * @param ps The prepared statement. All the parameters must be prepared
     * @param first The number of the first record (0 = the first)
     * @param count The number of records to return. Use -1 for all records)
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

    /**
     *
     * @param ps
     * @return
     * @throws SQLException
     */
    public Vector executeQueryWithResult(PreparedStatement ps)
            throws SQLException {
        return executeQueryWithResult(ps, 0, -1);
    }

    /**
     *
     * @param query
     * @param first
     * @param count
     * @return
     * @throws SQLException
     */
    public Vector executeQueryWithResult(String query, int first, int count)
            throws SQLException {
        PreparedStatement ps = getStatement(query);
        return executeQueryWithResult(ps, first, count);
    }

    /**
     *
     * @param query
     * @return
     * @throws SQLException
     */
    public Vector executeQueryWithResult(String query) throws SQLException {
        return executeQueryWithResult(query, 0, -1);
    }

    /**
     * Indicates if the resultset is empty
     *
     * @param rs The resultset to check
     * @return If true, the resultset is empty. Otherwise the resultset is not
     * empty.
     * @throws SQLException
     */
    public boolean isEmpty(ResultSet rs) throws SQLException {
        return !rs.next();
    }

    /**
     * We can't add null to a hashtable, so convert nulls to an empty string. We
     * can't add Long in XML-RPC, so convert to Integer if obj == null this
     * function returns an empty string. Otherwise if obj is a Long, convert to
     * a Integer.
     *
     * @param obj the item that could be null or Long
     * @return a possibly new obj.
     */
    public Object clearNull(Object obj) {
        if (obj == null) {
            return "";
        } else if (obj instanceof Long) {
            return new Integer(((Long) obj).intValue());
        } else {
            return obj;
        }

    }

    @Override
    public Vector getCoursesForClass(int classID) throws IOException,
            XmlRpcException, SQLException {
        close(); //for lazy connection        
        PreparedStatement ps;
        ps = getStatement("Select iconizer from tblClass where classID = ?");
        ps.setInt(1, classID);
        ResultSet r = ps.executeQuery();
        if (r.next() && r.getBoolean(1)) {
            r.close();
            ps = getStatement(QRY_SELECT_COURSES_CLASS);
        } else {
            r.close();
            ps = getStatement(QRY_SELECT_COURSES_CLASS_NOMAP); // geen mappen in het resultaat
        }
        ps.setInt(1, classID);
        return executeQueryWithResult(ps);
    }

    /**
     *
     */
    protected final static long DATE_OFFSET = 36L * 3600L * 1000L; // 36 uur. 

    /**
     *
     * @param classID
     * @param courseID
     * @param type
     * @param van
     * @param tot
     * @return
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean selectCoursesForClass(int classID, int courseID, int type,
            Date van, Date tot) throws IOException, XmlRpcException,
            SQLException {
        close(); //for lazy connection
        PreparedStatement ps;
        {
            ps = getStatement(QRY_INSERT_CLASS_COURSE2);
            ps.setInt(1, classID);
            ps.setInt(2, courseID);
            ps.setInt(3, type);
            if (van.getTime() <= DATE_OFFSET) {
                ps.setNull(4, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(4, new java.sql.Timestamp(van.getTime()));
            }
            if (tot.getTime() <= DATE_OFFSET) {
                ps.setNull(5, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(5, new java.sql.Timestamp(tot.getTime()));
            }
        }
        ps.execute();
        return true;
    }

    @Override
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

    @Override
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

    /**
     * Returns all the available courses for the specified user.
     *
     * @param profileValue
     * @param schoolID The school for which the courses must selected.
     *
     * @return A Vector containing hash tables with the course data.
     *
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     *
     * @see fi.dwo.client.persistence.DbAccessIF#getCourses(int)
     */
    @Override
    public Vector getCourses(int profileValue) throws IOException, XmlRpcException,
            SQLException {
        close(); //for lazy connection
        PreparedStatement ps;
        if (profileValue < 0) {
            /* User is a guest */
            if (profileValue < PROFILEOFFSET) {
                ps = getStatement(QRY_SELECT_COURSES_PROFILE_GUEST);
                ps.setInt(1, PROFILEOFFSET - profileValue);
                return executeQueryWithResult(ps);
            }
        } else {
            throw new RuntimeException("profileValue has non-negative ");
        };

//            else {
//                ps = getStatement(QRY_SELECT_COURSES_GUEST);
//        else {
//            ps = getStatement(QRY_SELECT_COURSES);
//            ps.setInt(1, schoolID);
//        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getTable(java.lang.String,
     *      java.util.Hashtable)
     */
    @Override
    public Vector getTable(String tableName, Hashtable wheredef)
            throws IOException, XmlRpcException, SQLException {
        return getTable(tableName, wheredef, null);
    }

    /**
     *
     * @param schoolID
     * @return
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public Vector getToSchoolsFrom(int schoolID)
            throws IOException, XmlRpcException, SQLException {
        close(); //for lazy connection
        PreparedStatement ps;
        ps = getStatement(QRY_SELECT_TO_SCHOOLS_FROM);
        ps.setInt(1, schoolID);
        return executeQueryWithResult(ps);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#LMSGetValue(java.lang.String)
     */
    /**
     *
     * @param scoID
     * @param userID
     * @param iDataModelElement
     * @return
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public String LMSGetValue(int scoID, int userID, String iDataModelElement)
            throws IOException, XmlRpcException, SQLException {
        if (iDataModelElement.startsWith("cmi.")) {
            // botte interface naar Xml2Scorm, no caching 
            String xmlStr = LMSGetValue(scoID, userID, "cocd");
            Scorm2Xml xml = new Scorm2Xml(String.valueOf(xmlStr));
            return xml.getValue(iDataModelElement);
        }

        String[] arguments = {iDataModelElement};
        String query = MessageFormat.format(QRY_GET_STUDENT_SCO, arguments);

        PreparedStatement ps = getStatement(query);
        ps.setInt(1, scoID);
        ps.setInt(2, userID);

        Hashtable ht = executeQueryWithRecord(ps);

        ps.close();
        if (ht == null) {
            if ("total_time".equals(iDataModelElement)) {
                return "0000:00:00.00";
            }
            return "";
        } else {
            Object o = ht.get(iDataModelElement);
            if (o == null) {
                return "";
            }
            if (iDataModelElement.equals("score") && o instanceof Number) {
                Number number = ((Number) o);
                if (number.doubleValue() == number.longValue()) {
                    return String.valueOf(number.longValue());
                }
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

    /**
     *
     * @param scoID
     * @param userID
     * @param iDataModelElement
     * @param iValue
     * @return
     * @throws SQLException
     * @throws IOException
     * @throws XmlRpcException
     */
    @Override
    public String LMSSetValue(int scoID, int userID, String iDataModelElement,
            String iValue) throws SQLException, IOException, XmlRpcException {

        if (iDataModelElement.startsWith("cmi.")) {
            // eerste botte implementatie
            String xmlStr = LMSGetValue(scoID, userID, "cocd");
            Scorm2Xml xml = new Scorm2Xml(String.valueOf(xmlStr));
            xml.LMSSetValue(iDataModelElement, iValue);
            iDataModelElement = "cocd";
            iValue = xml.toString();
        }

        try {
            String[] arguments = {"studentSco"};
            String query = MessageFormat.format(QRY_GET_STUDENT_SCO, arguments);

            PreparedStatement ps = getStatement(query);
            ps.setInt(1, scoID);
            ps.setInt(2, userID);

            Hashtable ht = executeQueryWithRecord(ps); // Never returns null, emtpy instead!
            log(Level.FINE, "LMSSetValue("
                    + scoID + ", " + userID + ", " + iDataModelElement + ", "
                    + iValue + ")", null);
            ps.close();
            if (ht == null || ht.isEmpty()) {
                Connection c = getConnection();
                c.setAutoCommit(false);

                ps = getStatementWithGeneratedKeys(QRY_ADD_EMPTY_STUDENT_SCO_CONTEXT);

                ps.setInt(1, scoID);
                ps.setInt(2, userID);
                ps.execute();
                ResultSet rs = ps.getGeneratedKeys();
                int id = -1;
                if (rs.next()) {
                    id = rs.getInt(1);
                    ps = getStatement(QRY_ADD_EMPTY_STUDENT_SCO_DATA);

                    ps.setInt(1, id);
                    ps.execute();
                    int count = ps.getUpdateCount();
                    if (count != 1) {
                        log("Error with inserting tblStudentScoData" + count);
                    }
                } else {
                    log("Error with inserting tblStudentScoContext");
                }
                c.commit();
            }

            Connection c = getConnection();
            c.setAutoCommit(false);

            arguments[0] = iDataModelElement;

            query = MessageFormat.format(QRY_UPDATE_STUDENT_SCO, arguments);

            ps = getStatement(query);
            ps.setObject(1, iValue);
            ps.setInt(2, scoID);
            ps.setInt(3, userID);

            ps.execute();
            int count = ps.getUpdateCount();
// XXX we gaan uit 2 goed is, en niet een dubbele 1
// FIXME voor variablen in context is het 1 voor suspend data is het 2
            if (count != 2 && count != 1) {
                // iets mis2 ...
                log("QRY_UPDATE_STUDENT_SCO count is  " + count);

            }
            c.commit();

            //ps.close();
            return "";
        } catch (SQLException e) {
            log.log(Level.SEVERE, "DbAccess.setLMSSetValue {0} throws {1}, exception message: {2}", new Object[]{iDataModelElement, userID, e.getMessage()});
            throw e;
        } catch (RuntimeException e) {
            log.log(Level.SEVERE, "DbAccess.setLMSValue {0} runtime {1}, exception message: {2}", new Object[]{iDataModelElement, userID, e.getMessage()});
            throw e;
        }

    }

    /**
     *
     * @param scoID
     * @param userID
     * @param iDataModelElement
     * @param iValue
     * @param random
     * @return
     * @throws SQLException
     * @throws IOException
     * @throws XmlRpcException
     */
    @Override
    public String LMSSetValue(int scoID, int userID, String iDataModelElement,
            String iValue, String random) throws SQLException, IOException, XmlRpcException {
        LMSSetValue(scoID, userID, iDataModelElement, iValue);
        return random;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getResults(int[])
     */
    /**
     *
     * @param courses
     * @param userID
     * @return
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public Vector getResults(Vector courses, int userID) throws IOException,
            XmlRpcException, SQLException {
        int i;
        String courseString = "";
        if (courses.size() > 0) {
            for (i = 0; i < courses.size(); i++) {
                courseString += ((Integer) courses.get(i)).toString() + ", ";
            }

            courseString = courseString.substring(0, courseString.length() - 2);

            String[] arguments = {courseString};
            String query = MessageFormat.format(QRY_RESULTS_ALL, arguments);
            PreparedStatement ps = getStatement(query);
            ps.setInt(1, userID);
            Vector v = executeQueryWithResult(ps);

            log.log(Level.FINE, "Got {0} results for <course[], teacher> "
                    + "= <{1},{2}> combination.", new Object[]{v.size(), courseString, userID});
            return v;

        } else {
            /* No Courses, no result */
            log.log(Level.FINE, "Queried with 0 courses and teacherID "
                    + "= {0}. Returning empty vector.", new Object[]{userID});

            return new Vector();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getResults(int[], int)
     */
//    public Vector getResults_slow(Vector courses, int classID, int userID)
//            throws IOException, XmlRpcException, SQLException {
//        int i;
//        String courseString = "";
//        if (courses.size() > 0) {
//            for (i = 0; i < courses.size(); i++) {
//                courseString += ((Integer) courses.get(i)).toString() + ", ";
//            }
//
//            courseString = courseString.substring(0, courseString.length() - 2);
//
//            String[] arguments = {courseString};
//            String query = MessageFormat.format(QRY_RESULTS_CLASS, arguments);
//
//            PreparedStatement ps = getStatement(query);
//            ps.setInt(1, classID);
//            ps.setInt(2, userID);
//            return executeQueryWithResult(ps);
//
//        } else {
//            /* No Courses, no result */
//            return new Vector();
//        }
//    }
    private Vector get1Results(PreparedStatement ps, Object courseID, int classID) throws SQLException {
        ps.setInt(1, classID);
        ps.setObject(2, courseID);
        return executeQueryWithResult(ps);
    }

    static final Comparator comparator = new Comparator() {
        // sort on userID, courseID
        @Override
        public int compare(Object arg0, Object arg1) {
            Hashtable h0 = (Hashtable) arg0;
            Hashtable h1 = (Hashtable) arg1;
            Integer i0 = (Integer) h0.get("userID");
            Integer i1 = (Integer) h1.get("userID");
            int r = i0.compareTo(i1);
            if (r == 0) {
                i0 = (Integer) h0.get("courseID");
                i1 = (Integer) h1.get("courseID");
                r = i0.compareTo(i1);
            }
            return r;
        }
    };

    /**
     *
     * @param courses
     * @param classID
     * @param teacherID
     * @return
     * @throws SQLException
     */
    @Override
    public Vector getResults(Vector courses, int classID, int teacherID)
            throws SQLException {
        if (courses.isEmpty()) {
            return new Vector();
        }
        Iterator iterator = courses.iterator();
        PreparedStatement ps = getStatement(QRY_RESULTS_CLASS_COURSE);
        Vector all = get1Results(ps, iterator.next(), classID);
        while (iterator.hasNext()) {
            Object courseID = iterator.next();
            all.addAll(get1Results(ps, courseID, classID));
        }
        ps.close();
        Collections.sort(all, comparator);
        return all;
    }

    /**
     * geef alle courses waarbij er student-data aanwezig is voor een profiel en
     * klas
     *
     * @param profileID
     * @param classID
     * @throws java.sql.SQLException
     */
    @Override
    public Vector getResultCount(int profileID, int classID) throws SQLException {
        long start = System.currentTimeMillis();
        String query = QRY_RESULTS_COURSE_PROFILE;

        PreparedStatement ps = getStatement(query);
        ps.setInt(1, classID);
        ps.setInt(2, profileID);
        Vector result = executeQueryWithResult(ps);
        long stop = System.currentTimeMillis();
        log("getResultCount " + profileID + "," + classID + " " + (stop - start) + " ms");
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getResults(int, int)
     */
    /**
     *
     * @param courseID
     * @param classID
     * @param userID
     * @return
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public Vector getResults(int courseID, int classID, int userID)
            throws IOException, XmlRpcException, SQLException {
        PreparedStatement ps = getStatement(QRY_RESULTS_STUDENT_COURSE);
        ps.setInt(1, classID);
        ps.setInt(2, classID);
        ps.setInt(3, courseID);
        ps.setInt(4, userID);

        Vector v = executeQueryWithResult(ps);
        log.log(Level.FINE, "Got {0} results for <course, class, teacher> "
                + "= <{1},{2},{3}> classes.", new Object[]{v.size(), classID, courseID, userID});
        return v;
    }

    /**
     * The results of a single course by a single student.
     *
     * @param courseID
     * @param userID
     * @return
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     */
    @Override
    public Vector getUserResults(int courseID, int userID)
            throws IOException, XmlRpcException, SQLException {
        PreparedStatement ps = getStatement(QRY_RESULTS_SINGLE_STUDENT_COURSE);
        ps.setInt(2, courseID);
        ps.setInt(1, userID);

        Vector v = executeQueryWithResult(ps);
        log.log(Level.FINE, "Got {0} results for <course, student> = <{1},{2}> combination.", new Object[]{v.size(), courseID, userID});

        return v;
    }

    /*
     * Get the student results for a <course, teacher> combination.
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getResults(int)
     */
    /**
     *
     * @param courseID
     * @param userID
     * @return
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public Vector getResults(int courseID, int userID) throws IOException,
            XmlRpcException, SQLException {
        PreparedStatement ps = getStatement(QRY_RESULTS_COURSE);
        ps.setInt(1, courseID);
        ps.setInt(2, userID);
        Vector v = executeQueryWithResult(ps);
        log.log(Level.FINE, "Got {0} results for <course, teacher> = <{1},{2}> combination.", new Object[]{v.size(), courseID, userID});

        return v;

    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#renameClass(int,
     *      java.lang.String)
     */
    /**
     *
     * @param classID
     * @param newName
     * @return
     * @throws DwoXmlRpcException
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean renameClass(int classID, String newName)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        PreparedStatement ps = getStatement(QRY_UPDATE_CLASS_NAME);
        ps.setString(1, newName);
        ps.setInt(2, classID);
        return renameCommon(ps);
    }

    /**
     * Updates a class entry and the sets the iconizer and bool
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     */
    @Override
    public boolean renameClass(int classID, String newName, boolean iconizer) throws SQLException, DwoXmlRpcException {
        PreparedStatement ps = getStatement(QRY_UPDATE_CLASS_NAME2);
        ps.setString(1, newName);
        ps.setBoolean(2, iconizer);
        ps.setInt(3, classID);
        //System.out.println(">>"+this.getClass().getName()+".ps:"+ps);
        return renameCommon(ps);
    }

    /**
     * Updates a class entry and the sets the iconizer and bool
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     */
    @Override
    public boolean renameClass(int classID, String newName, String newRegistrationKey, boolean iconizer) throws SQLException, DwoXmlRpcException {
        PreparedStatement ps = getStatement(QRY_UPDATE_CLASS_NAME3);
        ps.setString(1, newName);
        ps.setString(2, newRegistrationKey);
        ps.setBoolean(3, iconizer);
        ps.setInt(4, classID);
        // System.out.println(">>"+this.getClass().getName()+".ps:"+ps);
        return renameCommon(ps);
    }

    private boolean renameCommon(PreparedStatement ps)
            throws DwoXmlRpcException, SQLException {
        try {
            log.log(Level.FINE, "Trying query: {0}.", new Object[]{ps.toString()});
            ps.execute();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                /* The class already exists */
                log.log(Level.FINE, "Class {0} already exists. Throwing error.");
                throw new DwoXmlRpcException(
                        DwoXmlRpcException.EXC_CLASS_EXISTS);
            } else {
                throw e;
            }
        }
        return true;
    }

//    /**
//     * Maak userID de Teacher van classID.
//     *
//     * @param classID een klas
//     * @param userID een docent
//     * @throws java.sql.SQLException
//     */
//    @Override
//    public boolean reassignClass(int classID, int userID)
//            throws java.sql.SQLException {
//        PreparedStatement ps = getStatement(QRY_UPDATE_CLASS_USER);
//        ps.setInt(1, userID);
//        ps.setInt(2, classID);
//        ps.execute();
//        return true;
//    }
    //TODO V1_2 obsolete
//    /**
//     * Maak userID de Teacher van classID.
//     *
//     * @param classID een klas
//     * @param userID een docent
//     * @throws java.sql.SQLException
//     */
//    @Override
//    public boolean reassignClass(int classID, int userID)
//            throws SQLException {
//        PreparedStatement ps = getStatement(QRY_UPDATE_CLASS_USER);
//        ps.setInt(1, userID);
//        ps.setInt(2, classID);
//        ps.execute();
//        return true;
//    }
//    /**
//     *
//     * @throws java.io.IOException
//     * @throws java.sql.SQLException
//     * @throws org.apache.xmlrpc.applet.XmlRpcException
//     * @deprecated weg ermee
//     * @see fi.dwo.client.persistence.DbAccessIF#selectJar(java.lang.String,
//     * java.lang.String)
//     */
//    @Override
//    public boolean selectJar(String key, String jar) throws IOException,
//            XmlRpcException, SQLException {
//        PreparedStatement ps = getStatement(QRY_JAR_INSERT_KEY);
//
//        ps.setString(1, key);
//        ps.setString(2, jar);
//
//        try {
//            ps.execute();
//
//        } catch (SQLException e) {
//            if (e.getErrorCode() == 1062) {
//                ps = getStatement(QRY_JAR_UPDATE_KEY);
//
//                ps.setString(1, jar);
//                ps.setString(2, key);
//
//                ps.execute();
//
//            } else {
//                throw e;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * @deprecated weg ermee
//     * @param key
//     * @return
//     * @throws IOException
//     * @throws SQLException
//     */
//    private String getJar(String key) throws IOException, SQLException {
//        PreparedStatement ps = getStatement(QRY_JAR_SELECT_KEY);
//
//        ps.setString(1, key);
//
//        Hashtable result = executeQueryWithRecord(ps);
//
//        return (String) result.get("jarname");
//
//    }
//
//    /**
//     * @deprecated weg ermee
//     * @return
//     * @throws IOException
//     * @throws SQLException
//     */
//    private int getNrJars() throws IOException, SQLException {
//        Hashtable result = executeQueryWithRecord(getStatement(QRY_JAR_COUNT_JARS));
//        return ((Number) result.get("number")).intValue();
//    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#log(java.lang.String)
     */
    @Override
    public boolean log(String s) {
        log(Level.INFO, s, null);
        return false;
    }


    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#log(java.lang.String)
     */
    @Override
    public boolean log(Level level, String s) {
        //TODO V1_3 make log handler.
        log(level, s, null);
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#getEditableCourses(int)
     */
    @Override
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
    @Override
    public Vector getEditableCoursesAdmin() throws IOException,
            XmlRpcException, SQLException {

        PreparedStatement ps;
        ps = getStatement(QRY_SELECT_COURSES_EDITABLE_ADMIN);
        return executeQueryWithResult(ps);
    }

    /**
     *
     * @param schoolID
     * @param name
     * @param description
     * @param dwoProfile
     * @return
     * @throws DwoXmlRpcException
     * @throws SQLException
     */
    @Override
    public int addCourse(int schoolID, String name, String description, int dwoProfile) throws DwoXmlRpcException, SQLException {
        return addCourse(schoolID, name, description, dwoProfile, 0, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#addCourse(java.lang.String,
     *      java.lang.String)
     */
    /**
     *
     * @param schoolID
     * @param name
     * @param description
     * @param dwoProfile
     * @param parentID
     * @param isMap
     * @return
     * @throws DwoXmlRpcException
     * @throws SQLException
     */
    @Override
    public int addCourse(int schoolID, String name, String description, int dwoProfile, int parentID, boolean isMap)
            throws DwoXmlRpcException, SQLException {
        Hashtable schoolData = getRecord("tblSchool", "schoolID", schoolID);
        log("DbAccess.addCourse" + schoolData);
        String image = "";
        if (schoolData.containsKey("image")
                && (!schoolData.get("image").equals(""))) {
            image = (String) schoolData.get("image");
        }

        PreparedStatement ps;

        if (schoolID == 0) {
            ps = getStatementWithGeneratedKeys(QRY_ADD_COURSE_BASIC);
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, image);
            ps.setInt(4, dwoProfile);
            ps.setInt(5, parentID);
            ps.setBoolean(6, isMap);
        } else {
            ps = getStatementWithGeneratedKeys(QRY_ADD_COURSE);
            ps.setInt(1, schoolID);
            ps.setString(2, name);
            ps.setString(3, description);
            ps.setString(4, image);
            ps.setInt(5, dwoProfile);
            ps.setInt(6, parentID);
            ps.setBoolean(7, isMap);
        }

        try {
            ps.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                /* The course already exists */
                DwoXmlRpcException dwoXmlRpcException = new DwoXmlRpcException(
                        DwoXmlRpcException.EXC_COURSE_EXISTS);
                dwoXmlRpcException.initCause(e);
                throw dwoXmlRpcException;
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
    /**
     *
     * @param courseID
     * @param name
     * @param description
     * @return
     * @throws DwoXmlRpcException
     * @throws SQLException
     */
    @Override
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

    @Override
    public boolean changeCourse(int courseID, String name, String description, boolean export)
            throws DwoXmlRpcException, SQLException {
        PreparedStatement ps;
        ps = getStatement(QRY_UPDATE_COURSE2);
        ps.setString(1, name);
        ps.setString(2, description);
        ps.setBoolean(3, export);
        ps.setInt(4, courseID);

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

    /**
     *
     * @param courseID
     * @param name
     * @param description
     * @param export
     * @param schoolID
     * @return
     * @throws DwoXmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean changeCourse(int courseID, String name, String description, boolean export, int schoolID)
            throws DwoXmlRpcException, SQLException {

        if (schoolID == 0) {
            log("Course id " + courseID + " " + name + " dreigt te worden gepubliceerd");
        } else {
            log("Course id " + courseID + " " + name + " changed, schoolid = " + schoolID);
        }

//    	if(true)
//    	{
//    		return changeCourse(courseID, name, description, export);
//    	} 
//
//    	return false;
        PreparedStatement ps;
        ps = getStatement(QRY_UPDATE_COURSE3);
        ps.setString(1, name);
        ps.setString(2, description);
        ps.setBoolean(3, export);
        if (schoolID == 0) {
            ps.setNull(4, Types.INTEGER);
        } else {
            ps.setInt(4, schoolID);
        }
        ps.setInt(5, courseID);

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

    /**
     *
     * @param courseID
     * @param name
     * @param description
     * @param export
     * @param schoolID
     * @param parentID
     * @return
     * @throws DwoXmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean changeCourse(int courseID, String name, String description,
            boolean export, int schoolID, int parentID)
            throws DwoXmlRpcException, SQLException {
// TODO als course schoolID verandert EN withChildren = true, dan ook kinderen updaten van school!!!!!!
        PreparedStatement ps;
        ps = getStatement(QRY_UPDATE_COURSE4);
        ps.setString(1, name);
        ps.setString(2, description);
        ps.setBoolean(3, export);
        if (schoolID == 0) {
            ps.setNull(4, Types.INTEGER);
        } else {
            ps.setInt(4, schoolID);
        }
        ps.setInt(5, parentID);
        ps.setInt(6, courseID);
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
    /**
     *
     * @param courseID
     * @return
     * @throws DwoXmlRpcException
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean deleteCourse(int courseID) throws DwoXmlRpcException,
            IOException, XmlRpcException, SQLException {
        Hashtable wheredef = new Hashtable();
        wheredef.put("parentID", courseID);
        Vector children = getTable("tblCourse", wheredef);
        /* delete the children, recurse */
        Iterator iter = children.iterator();
        while (iter.hasNext()) {
            Hashtable course = (Hashtable) iter.next();
            int id = ((Number) course.get("courseID")).intValue();
            deleteCourse(id);
        }

        wheredef.clear();
        wheredef.put("courseID", new Integer(courseID));
        Vector scos = getTable("tblScoContext", wheredef);
        String[] arguments = new String[2];

        /* Delete results of sco's of the course */
        String statement;
        //String statement = MessageFormat.format(QRY_DELETE_DEFAULT, arguments);
        PreparedStatement ps;
        int scoID;
        while (scos.size() > 0) {
            //ps = getStatement(statement);
            ps = getStatement(QRY_DELETE_STUDENTSCO_BY_SCO);
            scoID = ((Integer) ((Hashtable) scos.remove(0)).get("scoID"));
            ps.setInt(1, scoID);
            ps.execute();
            ps.close();
        }

        /* Delete Sco's of the course */
        ps = getStatement("delete tblScoContext, tblScoData  from tblScoContext join tblScoData using (scoID) where courseid = ?");
        //ps = getStatement(statement);
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
    /**
     *
     * @param courseID
     * @param name
     * @param description
     * @param appletConfigID
     * @param sequencenr
     * @return
     * @throws DwoXmlRpcException
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public int addSco(int courseID, String name, String description,
            int appletConfigID, int sequencenr) throws DwoXmlRpcException,
            IOException, XmlRpcException, SQLException {
        Hashtable data;
        if (appletConfigID < 0) {
            data = getRecord("tblScoView", "scoID", -appletConfigID); // Wim: 24 dec Data, geen context
        } else {
            data = getRecord("tblAppletConfig", "appletConfigID",
                    appletConfigID);
        }
        //log("DbAccess.addSco " + data);
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
     *
     * @param courseID
     * @param name
     * @param description
     * @param appletConfigID
     * @param sequencenr
     * @param showScore
     * @return
     * @throws DwoXmlRpcException
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public int addSco(int courseID, String name, String description,
            int appletConfigID, int sequencenr, boolean showScore) throws DwoXmlRpcException,
            IOException, XmlRpcException, SQLException {
        int result = addSco(courseID, name, description, appletConfigID, sequencenr);
        if (!showScore) {
            changeSco(result, name, description, false);
        }
        return result;
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
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     */
    @Override
    public int addSco(int courseID, String name, String description,
            int appletID, String launchdata, int sequencenr)
            throws SQLException, DwoXmlRpcException {
        int result;
        if (appletID != -1) {

            Connection c = getConnection();
            c.setAutoCommit(false);

            PreparedStatement ps = getStatementWithGeneratedKeys(QRY_ADD_SCO_CONTEXT);

            ps.setInt(1, courseID);
            ps.setInt(2, appletID);
            ps.setString(3, name);
            ps.setInt(4, sequencenr);
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
            int id = -1;
            if (rs.next()) {
                id = rs.getInt(1);
                ps = getStatement(QRY_ADD_SCO_DATA);

                ps.setInt(1, id);
                ps.setString(2, description);
                ps.setString(3, launchdata);
                ps.execute();
                int count = ps.getUpdateCount();
                if (count != 1) {
                    log("Error with inserting tblScoData" + count);
                    id = -1;
                    c.rollback();
                }
            } else {
                log("Error with inserting tblScoContext");
                c.rollback();
            }
            result = id;
            c.commit();
            return result;
        } else { //no appletconfig found
            throw new DwoXmlRpcException(DwoXmlRpcException.EXC_SCO_EXISTS);
        }
    }

    /**
     * Shortcut. Geen update van launchdata.
     *
     * @param scoID
     * @param name
     * @param description
     * @return
     * @throws java.sql.SQLException
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     * @see #changeSco(int, String, String, String)
     */
    @Override
    public boolean changeSco(int scoID, String name, String description)
            throws SQLException, DwoXmlRpcException {
        PreparedStatement ps;
        ps = getStatement(QRY_UPDATE_SCO2);
        ps.setString(1, name);
        ps.setString(2, description);
        ps.setInt(3, scoID);

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
        return true;
    }

    /**
     * Shortcut. Geen update van launchdata.
     *
     * @param scoID
     * @param name
     * @param description
     * @param showScore
     * @return
     * @throws java.sql.SQLException
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     * @see #changeSco(int, String, String, String)
     */
    @Override
    public boolean changeSco(int scoID, String name, String description, boolean showScore)
            throws SQLException, DwoXmlRpcException {
        PreparedStatement ps;
        ps = getStatement(QRY_UPDATE_SCO3);
        ps.setString(1, name);
        ps.setString(2, description);
        ps.setBoolean(3, !showScore); // Note: Reverse Logic
        ps.setInt(4, scoID);

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
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#changeSco(int,
     *      java.lang.String, java.lang.String)
     */
    /**
     *
     * @param scoID
     * @param name
     * @param description
     * @param launchdata
     * @param showScore
     * @return
     * @throws DwoXmlRpcException
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean changeSco(int scoID, String name, String description, String launchdata, boolean showScore)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        changeSco(scoID, name, description, showScore);
        return changeSco(scoID, name, description, launchdata);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#changeSco(int,
     *      java.lang.String, java.lang.String)
     */
    /**
     *
     * @param scoID
     * @param name
     * @param description
     * @param launchdata
     * @return
     * @throws DwoXmlRpcException
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean changeSco(int scoID, String name, String description, String launchdata)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return changeSco(scoID, name, description, true, launchdata);
    }

    /**
     *
     * @param scoID
     * @param name
     * @param description
     * @param delete
     * @param launchdata
     * @return
     * @throws DwoXmlRpcException
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean changeSco(int scoID, String name, String description, boolean delete, String launchdata)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        PreparedStatement ps;
        ps = getStatement(QRY_UPDATE_SCO);
        ps.setString(1, name);
        ps.setString(2, description);
        ps.setString(3, launchdata);
        ps.setInt(4, scoID);

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
        deleteSuspendData(scoID, delete);
        return true;
    }

    private void deleteSuspendData(int scoID, boolean delete)
            throws SQLException {
        PreparedStatement ps;
        if (delete) {
            /* Delete results of sco's */
            ps = getStatement(QRY_DELETE_STUDENTSCO_BY_SCO);
            ps.setInt(1, scoID);
            ps.execute();
            ps.close();
        }
    }

    /**
     *
     * @param scoID
     * @param name
     * @param description
     * @param delete
     * @param launchdata
     * @param showScore
     * @return
     * @throws DwoXmlRpcException
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean changeSco(int scoID, String name, String description, boolean delete, byte[] launchdata, boolean showScore)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        changeSco(scoID, name, description, showScore);
        PreparedStatement ps;
        String query = "UPDATE tblScoData "
                + "SET "
                + "launchdatabytes = ? "
                + "WHERE (scoID = ?) ";
        ps = getStatement(query);
        ps.setObject(1, launchdata);
        ps.setInt(2, scoID);

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
        deleteSuspendData(scoID, delete);
        return true;
    }

    static private final String QRY_UPDATE_SCO_SEQUENCENR
            = "UPDATE tblScoContext SET sequencenr = ? WHERE (scoID = ?) ";

    /**
     * Update het sequencenr van een sco. Niet gecombineerd met changeSco, omdat
     * er geen bijeffect is dat de studenten hun data verliezen. Voor een swap
     * zijn twee sco's nodig. Daarom hier meteen twee voor de prijs van Ã©Ã©n!
     *
     * @param scoID
     * @param sequencenr nieuw sequence nummer voor scoID, bij swap oude van
     * scoID2
     * @param scoID2
     * @param sequencenr2 nieuw sequencenummer voor scoID2, bij swap oude van
     * scoID
     * @return always true
     * @throws SQLException
     * @see #changeSco(int, String, String, String)
     */
    @Override
    public boolean changeScoSequenceNr(int scoID, int sequencenr, int scoID2, int sequencenr2) throws SQLException {
        PreparedStatement ps;
        ps = getStatement(QRY_UPDATE_SCO_SEQUENCENR);
        try {
            ps.setInt(1, sequencenr);
            ps.setInt(2, scoID);
            ps.execute();
            ps.setInt(1, sequencenr2);
            ps.setInt(2, scoID2);
            ps.execute();
        } finally {
            ps.close();
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.DbAccessIF#deleteSco(int)
     */
    /**
     *
     * @param scoID
     * @return
     * @throws DwoXmlRpcException
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean deleteSco(int scoID) throws DwoXmlRpcException, IOException,
            XmlRpcException, SQLException {
        Hashtable scodata = getRecord("tblScoView", "scoID", scoID);
        int sequencenr = -1;
        int courseid = -1;
        PreparedStatement ps;
        if (scodata.containsKey("sequencenr")) {
            sequencenr = ((Integer) scodata.get("sequencenr"));
        }
        if (scodata.containsKey("courseID")) {
            courseid = ((Integer) scodata.get("courseID"));
        }

        if (courseid != -1) {
            ps = getStatement(QRY_UPDATE_SCO_SEQUENCE);
            ps.setInt(1, sequencenr);
            ps.setInt(2, courseid);
            ps.execute();
            ps.close();
        }

        String[] arguments = new String[2];
        String statement;
        ps = getStatement(QRY_DELETE_STUDENTSCO_BY_SCO);
        ps.setInt(1, scoID);
        ps.execute();
        ps.close();

        /* Delete Sco's */
        ps = getStatement(QRY_DELETE_SCO_BY_ID);
        ps.setInt(1, scoID);
        ps.execute();
        ps.close();

        return true;
    }

//TODO V1_3 DONE
    /**
     * Delete school from the database.
     *
     * @param schoolID School Identifier.
     * @return
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     */
    @Override
    public boolean deleteSchool(int schoolID) throws IOException, XmlRpcException, SQLException {

        log.log(Level.FINE, "Attempting to delete school with id {0} and associated data.", new Object[]{schoolID});

        
// 1) delete students from class which is in the school.
        PreparedStatement ps;
        ps = getStatement(QRY_DELETE_STUDENT_FROM_CLASS_IN_SCHOOL);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        log.log(Level.FINE, "Deleted {0} students from class.", new Object[]{ps.getUpdateCount()});

// 2) delete teachers from class which is in the school.
        ps = getStatement(QRY_DELETE_TEACHER_FROM_CLASS_IN_SCHOOL);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        log.log(Level.FINE, "Deleted {0} teachers from class.", new Object[]{ps.getUpdateCount()});
        ps.close();

// 2) delete teachers from class which is in the school.
        ps = getStatement(QRY_DELETE_TEACHER_FROM_CLASS_IN_SCHOOL);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        log.log(Level.FINE, "Deleted {0} teachers from class.", new Object[]{ps.getUpdateCount()});
        ps.close();
        
        
// 2) delete courses from tblClassCourse which are in the school.
        ps = getStatement(QRY_DELETE_COURSES_FROM_CLASS_IN_SCHOOL);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        log.log(Level.FINE, "Deleted {0} classcourses.", new Object[]{ps.getUpdateCount()});
        ps.close();
// 3) delete class from school which is in the school.
        String[] arguments2 = {"tblClass", "schoolID"};
        String query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments2);
        ps = getStatement(query);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        log.log(Level.FINE, "Deleted {0} classes.", new Object[]{ps.getUpdateCount()});
        ps.close();
// 4) delete suspend data that become inaccessable.
        //TODO Wim discuss
        ps = getStatement(QRY_DELETE_STUDENTSCO_FROM_SCHOOL);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        log.log(Level.FINE, "Deleted {0} StudentSco's.", new Object[]{ps.getUpdateCount()});
        ps.close();
// 5) delete sco's die bij courses van school horen.
//         String QRY_DELETE_SCO_FROM_SCHOOL
//                = "delete tblScoContext, tblScoData from tblScoContext join tblScoData using (scoID) where courseID in (SELECT courseID FROM tblCourse WHERE schoolID = ?)";
        ps = getStatement(QRY_DELETE_SCO_FROM_SCHOOL);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        log.log(Level.FINE, "Deleted {0} SCO's.", new Object[]{ps.getUpdateCount()});
        ps.close();
// 6) delete courses from school
        arguments2 = new String[]{"tblCourse", "schoolID"};
        query = MessageFormat.format(QRY_DELETE_DEFAULT, (Object) arguments2);
        ps = getStatement(query);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        log.log(Level.FINE, "Deleted {0} courses.", new Object[]{ps.getUpdateCount()});
        ps.close();
// 7) verwijder users uit school, teachers and students are already gone.
//TODO DONE V1_3 fix query
        ps = getStatement(QRY_DELETE_USERS_FROM_SCHOOL);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        log.log(Level.FINE, "Deleted {0} users defaults.", new Object[]{ps.getUpdateCount()});
        //QRY_DELETE_ROLES_FROM_SCHOOL
        ps = getStatement(QRY_DELETE_ROLES_FROM_SCHOOL);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        log.log(Level.FINE, "Detached {0} users from a school.", new Object[]{ps.getUpdateCount()});
        ps.close();
// 8) verwijder schoolgroup
        arguments2 = new String[]{"tblSchoolGroup", "schoolID"};
        query = MessageFormat.format(QRY_DELETE_DEFAULT, (Object) arguments2);
        ps = getStatement(query);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        log.log(Level.FINE, "Deleted {0} tblSchoolGroup.", new Object[]{ps.getUpdateCount()});
        ps.close();
// 9) verwijder school
        arguments2 = new String[]{"tblSchool", "schoolID"};
        query = MessageFormat.format(QRY_DELETE_DEFAULT, arguments2);
        ps = getStatement(query);
        ps.setInt(1, schoolID);
        ps.executeUpdate();
        log.log(Level.FINE, "Deleted {0} school(s).", new Object[]{ps.getUpdateCount()});
        ps.close();

        return true;
    }

    /**
     *
     * @param courses
     * @param userID
     * @return
     * @throws SQLException
     */
    @Override
    public Vector getUserResults(Vector courses, int userID) throws SQLException {
        int i;
        StringBuilder courseString = new StringBuilder();
        if (courses.size() > 0) {
            for (i = 0; i < courses.size(); i++) {
                if (i != 0) {
                    courseString.append(',');
                }
                // beware of source code injection here....
                // assert courses.get(i) instanceof Integer
                courseString.append(courses.get(i));
            }
            String[] arguments = {courseString.toString()};
            String query = MessageFormat.format(QRY_RESULTS_SINGLE, (Object[]) arguments);
            PreparedStatement ps = getStatement(query);
            ps.setInt(1, userID);
            log.log(Level.FINE, "Going to query: {0}.", new Object[]{ps.toString()});
            Vector v = executeQueryWithResult(ps);
            return v;
        } else {
            /* No Courses, no result */
            log.log(Level.FINE, "Submitted 0 courses, returning empty Vector.");
            return courses; // an empty vector
        }
    }

    /**
     *
     * @param id
     * @param image
     * @return
     * @throws SQLException
     * @throws IOException
     * @throws XmlRpcException
     */
    @Override
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
        if (!result) {
            ps = getStatement("INSERT INTO tblimage(courseID, image) VALUES (?,?)");
            ps.setInt(1, id);
            ps.setObject(2, image);
            ps.executeUpdate();
            ps.close();
        }
        return result;
    }

    /**
     *
     * @return @throws DwoXmlRpcException
     */
    @Override
    public Hashtable getFidentitySchools() throws DwoXmlRpcException {
        throw new DwoXmlRpcException(DwoXmlRpcException.EXC_SCHOOL_UNSUPPORTED);
    }

    /**
     *
     * @param schoolID
     * @param export
     * @return
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean editSchool(int schoolID, boolean export) throws IOException,
            XmlRpcException, SQLException {

        PreparedStatement ps = getStatement(QRY_UPDATE_SCHOOL2);
        ps.setBoolean(1, export);
        ps.setInt(2, schoolID);
        ps.execute();
        ps.close();
        return export;
    }

    /**
     *
     * @param schoolID
     * @param rights
     * @return
     * @throws IOException
     * @throws SQLException
     */
    @Override
    public boolean editSchoolRights(int schoolID, String rights) throws IOException, SQLException {
        PreparedStatement ps = getStatement(QRY_UPDATE_SCHOOL3);
        ps.setString(1, rights);
        ps.setInt(2, schoolID);
        int r = ps.executeUpdate();
        ps.close();
        return r != 0;
    }

    /**
     *
     * @param schoolFrom
     * @param schoolTo
     * @param profileID
     * @return
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public Vector getImportCourses(int schoolFrom, int schoolTo, int profileID)
            throws IOException, XmlRpcException, SQLException {
        String sql = QRY_SELECT_IMPORT_COURSES;
        PreparedStatement ps = getStatement(sql);
        ps.setInt(1, schoolFrom);
        ps.setInt(2, schoolTo);
        ps.setInt(3, profileID);
        Vector v = executeQueryWithResult(ps);
        log.log(Level.FINE, "Retrieved {0} courses to import.", new Object[]{v.size()});
        return v;

    }

    //TODO V1_3 DONE fix method below. Clearly only a single role must be deleted.
    
    /**
     * Removes a user from the school. Removes occur for both student and
     * teacher roles for the userID within the school with the given schoolID.
     *
     * @param userID
     * @param schoolID
     *
     * @return
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean deleteUserWithRoleFromSchool(int userID, int schoolGroupID)
            throws IOException, XmlRpcException, SQLException {

        Connection c = getConnection();
        try {
            c.setAutoCommit(false);
            log.log(Level.FINE, "Transaction started.");

            //delete userID  from student links pointing to tblClass where schoolID matches. 
            //String sql = "DELETE FROM tblStudentOf WHERE userID = ? AND classID IN (SELECT classID FROM tblClass WHERE tblSchoolGroup.schoolID = ?)";
            String sql = "select * from tblStudentOf WHERE userID = ? AND tblStudentOf.classID "
                    + "IN (SELECT tblClass.classID FROM tblClass join tblSchoolGroup using (schoolID) "
                    + "join tblGroup using (groupID) join tblHasRole using (schoolGroupID) "
                    + "WHERE schoolGroupID = ? and tblHasRole.userid = ? and groupname = 'STUDENT' )";
            PreparedStatement ps = getStatement(sql);
            ps.setInt(1, userID);
            ps.setInt(2, schoolGroupID);
            ps.setInt(3, userID);
            int cnt = ps.executeUpdate();
            log.log(Level.FINE, "Deleted the student from {0} classes.", new Object[]{cnt});

            //delete user as teacherID  from teacher links pointing to tblClass where schoolID matches. 
            //sql = "DELETE FROM tblTeacherOf WHERE tblTeacherOf join tblHasRole using (userID = ? AND classID IN (SELECT classID FROM tblClass WHERE schoolID = ?)";
            sql = "select * from tblTeacherOf WHERE userID = ? AND tblTeacherOf.classID "
                    + "IN (SELECT tblClass.classID FROM tblClass join tblSchoolGroup using (schoolID) "
                    + "join tblGroup using (groupID) join tblHasRole using (schoolGroupID) "
                    + "WHERE schoolGroupID = ? and tblHasRole.userid = ? and "
                    + "(groupname = 'TEACHER' or groupname = 'SCHOOLADMIN')";
            ps = getStatement(sql);
            ps.setInt(1, userID);
            ps.setInt(2, schoolGroupID);
            ps.setInt(3, userID);
            cnt = ps.executeUpdate();
            log.log(Level.FINE, "Deleted the teacher from {0} classes.", new Object[]{cnt});

            //then delete user from hasRole
            sql = "DELETE FROM tblHasRole  WHERE "
                    + " userID = ? AND schoolGroupID=?";
            ps = getStatement(sql);
            ps.setInt(1, userID);
            ps.setInt(2, schoolGroupID);
            cnt = ps.executeUpdate();
            log.log(Level.FINE, "Deleted the role of  <user {0}, schoolGroup {1}>.", new Object[]{userID,schoolGroupID});
            
            
            //then delete student from schoolgroup
            sql = "UPDATE tblUser SET schoolGroupID = NULL WHERE "
                    + " userID = ? AND schoolGroupID IN (SELECT schoolGroupID FROM tblSchoolGroup where schoolID = ?)";
            ps = getStatement(sql);
            ps.setInt(1, userID);
            ps.setInt(2, schoolGroupID);
            cnt = ps.executeUpdate();
            log.log(Level.FINE, "Cleared the default role for user {0} with schoolID {0}.", new Object[]{userID,schoolGroupID});
            c.commit();
            log.log(Level.FINE, "Transaction commited.");

            return cnt != 0;
        } finally {
            c.setAutoCommit(true);
        }
    }

    /**
     *
     * @param schoolID
     * @param schoolTo
     * @return
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean updateSchoolTo(int schoolID, Vector schoolTo)
            throws IOException, XmlRpcException, SQLException {
        Connection c = getConnection();
        try {
            c.setAutoCommit(false);
            String sql = "DELETE FROM tblfromto WHERE schoolFrom = ?";
            PreparedStatement ps = getStatement(sql);
            ps.setInt(1, schoolID);
            ps.executeUpdate();
            sql = "INSERT INTO tblfromto(schoolFrom, schoolTo) VALUES (?,?)";
            ps = getStatement(sql);
            Enumeration e = schoolTo.elements();
            while (e.hasMoreElements()) {
                Number to = (Number) e.nextElement();
                ps.setInt(1, schoolID);
                ps.setInt(2, to.intValue());
                ps.executeUpdate();
            }
            c.commit();
        } finally {
            c.setAutoCommit(true);
        }
        return true;
    }

    /**
     * Deletes Saved SCO data from a class
     *
     *
     * @param courseID
     * @param classID
     * @return
     * @throws SQLException
     */
    @Override
    public boolean deleteCourseDataFromClass(int courseID, int classID)
            throws SQLException {
        Connection c = getConnection();
        try {
            String sql;
            PreparedStatement ps;
            ResultSet rs;
            Vector scos, users;

            sql = "select scoID from tblScoContext where courseID = ?";
            ps = c.prepareStatement(sql);
            ps.setInt(1, courseID);
            rs = ps.executeQuery();
            scos = new Vector();
            while (rs.next()) {
                int sco = rs.getInt(1);
                scos.add(new Integer(sco));
            }
            rs.close();
            ps.close();
            sql = "select userID from tblStudentOf where classID = ?";
            ps = c.prepareStatement(sql);
            ps.setInt(1, classID);
            rs = ps.executeQuery();
            users = new Vector();
            while (rs.next()) {
                int user = rs.getInt(1);
                users.add(user);
            }
            rs.close();
            ps.close();
            int n = 0;
            sql = "delete tblStudentScoContext, tblStudentScoData from tblStudentScoContext join tblStudentScoData using (studentSco) where scoID=? and userID =?";
            ps = c.prepareStatement(sql);
            Enumeration sco, user;
            sco = scos.elements();
            while (sco.hasMoreElements()) {
                Object s = sco.nextElement();
                user = users.elements();
                while (user.hasMoreElements()) {
                    Object u = user.nextElement();
                    ps.setObject(1, s);
                    ps.setObject(2, u);
                    n += ps.executeUpdate();
                }
            }
            ps.close();
            log.log(Level.FINE, "Deleted SCO data for course {0} and class {1}.", new Object[]{courseID, classID});
// Old Wim code.
//			c.setAutoCommit(false);
//			c.commit();
//			String sql = "DELETE FROM tblStudentSco where scoID in (select scoID from tblSco where courseID = ?) and userID in (select userID from tblUser where classID = ?)";
//			PreparedStatement ps = c.prepareStatement(sql);
//			ps.setInt(1, courseID);
//			ps.setInt(2, classID);
//			int n = ps.executeUpdate();
//			log("course " + courseID + " class " + classID + " deleted: " + n);
//			
//			c.commit();
//			ps.close();
        } finally {
            c.setAutoCommit(true);
        }

        return true;
    }

    @Override
    public String setRights(int uid, int schoolGroupID, int profileid, String rights)
            throws SQLException, IOException, XmlRpcException {
        //TODO V1_3 DONE adjust to rights in tblHasRole
        String sql = "SELECT rights FROM tblHasRole where userID = ? and schoolGroupID = ?";
        PreparedStatement ps = this.getStatement(sql);
        ps.setInt(1, uid);
        ps.setInt(2, schoolGroupID);
        Vector v = executeQueryWithResult(ps, 0, 1);
        String oldrights;
        if (v.isEmpty()) {
            oldrights = "";
        } else {
            oldrights = ((Hashtable) v.firstElement()).get("rights").toString();
        }
        // split string
        String pstr = "[" + profileid + "]";
        int start = oldrights.indexOf(pstr);
        if (start < 0) {
            oldrights = oldrights + pstr;
            start = oldrights.length();
        } else {
            start += pstr.length();
        }
        int end = oldrights.indexOf("[", start);
        if (end < 0) {
            end = oldrights.length();
        }
        rights = oldrights.substring(0, start) + rights + oldrights.substring(end);
        //TODO V1_3 DONE adjust to rights in tblHasRole
        sql = "UPDATE tblHasRole SET rights = ? where userID = ? and schoolGroupID = ?";
        ps = getStatement(sql);
        ps.setString(1, rights);
        ps.setInt(2, uid);
        ps.setInt(3, schoolGroupID);
        ps.executeUpdate();
        return rights;
    }

    /**
     *
     * @param vector
     * @param schoolID
     * @param classID
     * @param parent
     * @param profileID
     * @return
     * @throws SQLException
     */
    @Override
    public boolean setCourseSequence(Vector vector, int schoolID, int classID,
            int parent, int profileID) throws SQLException {
        Connection c = getConnection();
        boolean auto = c.getAutoCommit();
        try {
            c.setAutoCommit(false);
            PreparedStatement ps;
            ps = getStatement("DELETE FROM tblCourseSequence WHERE schoolID=? AND classID=? AND parent=? and profileID=?");
            ps.setInt(1, schoolID);
            ps.setInt(2, classID);
            ps.setInt(3, parent);
            ps.setInt(4, profileID);
            ps.executeUpdate();
            ps.close();
            ps = getStatement("INSERT INTO tblCourseSequence(courseID, schoolID, classID, parent, profileID, sequencenr) VALUES(?,?,?,?,?,?)");
            int len = vector.size();
            for (int i = 0; i < len; i++) {
                ps.setObject(1, vector.get(i));
                ps.setInt(2, schoolID);
                ps.setInt(3, classID);
                ps.setInt(4, parent);
                ps.setInt(5, profileID);
                ps.setInt(6, i);
                ps.executeUpdate();
            }
            c.commit();
        } finally {
            c.rollback();
            c.setAutoCommit(auto);
        }
        return true;
    }
    /*
     * TODO eventueel een nieuwe naam meegeven, ivm clashes. 
     * (non-Javadoc)
     * @see fi.dwo.client.persistence.DbAccessIF#moveSco(int, int, int, String)
     */

    /**
     *
     * @param scoID
     * @param courseID
     * @param sequencenr
     * @param name
     * @return
     * @throws DwoXmlRpcException
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean moveSco(int scoID, int courseID, int sequencenr, String name)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        Connection c = getConnection();
        boolean result = false;
        boolean auto = c.getAutoCommit();
        try {
            c.setAutoCommit(false);
            PreparedStatement ps;
            /*
             get old courseId/sequencenr
             if courseid = oldcourseid 
             seqnr updaten van alle sco's van deze course:
             als newseqnr > oldseqnr dan seqnr aflagen tussen (old..new]
             als newseqnr < oldseqnr dan seqnr ophogen tussen [new..old)
             else
             seqnr aflagen als seqnr > oldseqnr bij oldcourse
             seqnr ophogen als seqnr >= newseqnr bij newcourse
		
             set (courseid, seqnr) in sco
             */
            ps = getStatement("SELECT courseID, sequencenr from tblScoContext where scoID = ?");
            ps.setInt(1, scoID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int oldCourseID = rs.getInt(1);
                int oldSequencenr = rs.getInt(2);
                rs.close();
                ps.close();
                if (courseID == oldCourseID) {
                    // gelijk.....
                    if (sequencenr > oldSequencenr) {
                        ps = getStatement("UPDATE tblScoContext SET sequencenr = sequencenr -1 WHERE courseID = ? AND sequencenr > ? AND sequencenr <= ?");
                    } else {
                        ps = getStatement("UPDATE tblScoContext SET sequencenr = sequencenr + 1 WHERE courseID = ? AND sequencenr < ? AND sequencenr >= ?");
                    }
                    ps.setInt(1, courseID);
                    ps.setInt(2, oldSequencenr);
                    ps.setInt(3, sequencenr);
                    ps.executeUpdate();
                    ps.close();
                } else {
                    // ongelijk
                    ps = getStatement("UPDATE tblScoContext SET sequencenr = sequencenr - 1 WHERE courseID = ? AND sequencenr > ?");
                    ps.setInt(1, oldCourseID);
                    ps.setInt(2, oldSequencenr);
                    ps.executeUpdate();
                    ps.close();
                    ps = getStatement("UPDATE tblScoContext SET sequencenr = sequencenr + 1 WHERE courseID = ? AND sequencenr >= ?");
                    ps.setInt(1, courseID);
                    ps.setInt(2, sequencenr);
                    ps.executeUpdate();
                    ps.close();
                }
                ps = getStatement("UPDATE tblScoContext SET courseID = ?, sequencenr = ?, sconame = ? where scoID = ?");
                ps.setInt(1, courseID);
                ps.setInt(2, sequencenr);
                ps.setString(3, name);
                ps.setInt(4, scoID);
                ps.executeUpdate();
                result = true;
            } else {
                rs.close();
            }
            ps.close();
            c.commit();
        } finally {
            c.rollback();
            c.setAutoCommit(auto);
        }
        return result;
    }

    static final Integer DEFAULT_TYPE = 0;

    /**
     *
     * @param id
     * @param v
     * @return
     * @throws SQLException
     */
    @Override
    public boolean selectCoursesForClass(int id, Vector v) throws SQLException {
        // XXX 
        Connection c = getConnection();
        try {
            PreparedStatement ps;
            c.setAutoCommit(false);
            if (!v.isEmpty()) {
                Hashtable map = (Hashtable) v.firstElement();
                Object profileID = map.get("dwoProfileID");
                if (profileID == null) {
                    map = getRecord("tblCourse", "courseID", ((Number) map.get("courseID")).intValue());
                    profileID = map.get("dwoProfileID");
                }
                ps = c.prepareStatement("DELETE FROM tblClassCourse WHERE classID = ? and courseID in (SELECT courseID from tblCourse where dwoProfileID = ?)");
                ps.setInt(1, id);
                ps.setObject(2, profileID);
                int r = ps.executeUpdate();
                System.out.println(r + " deletes from classcourse " + id + " and " + profileID);
                ps.close();
                if (!map.containsKey("courseID")) {
                    v.remove(0);
                }

            } else {
                ps = c.prepareStatement("DELETE FROM tblClassCourse WHERE classID = ?");
                ps.setInt(1, id);
                System.out.println(ps.executeUpdate() + " deletes from classcourse " + id);
                ps.close();
            }
            ps = getStatement(QRY_INSERT_CLASS_COURSE2);
            ps.setInt(1, id);
            for (Iterator iterator = v.iterator(); iterator.hasNext();) {
                Hashtable map = (Hashtable) iterator.next();
                Object courseID = map.get("courseID");
                ps.setObject(2, courseID);
                Object type = map.get("type");
                if (type == null) {
                    type = DEFAULT_TYPE;
                }
                ps.setObject(3, type);
                Date van = (Date) map.get("notBefore");
                if (van == null || van.getTime() <= DATE_OFFSET) {
                    ps.setNull(4, Types.TIMESTAMP);
                } else {
                    ps.setTimestamp(4, new java.sql.Timestamp(van.getTime()));
                }
                Date tot = (Date) map.get("notAfter");
                if (tot == null || tot.getTime() <= DATE_OFFSET) {
                    ps.setNull(5, Types.TIMESTAMP);
                } else {
                    ps.setTimestamp(5, new java.sql.Timestamp(tot.getTime()));
                }
                ps.executeUpdate();
            }
            ps.close();
            c.commit();
        } catch (SQLException s) {
            c.rollback();
            throw s;
        } finally {
            c.setAutoCommit(true);
        }
        return true;
    }

    /**
     *
     * @param schoolID
     * @param date
     * @return
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public boolean setExpireDate(int schoolID, Date date) throws IOException,
            XmlRpcException, SQLException {
        PreparedStatement ps = getStatement("UPDATE tblSchool SET expire = ? WHERE (schoolID = ?) LIMIT 1");
        if (date == null || date.getTime() < DATE_OFFSET) {
            ps.setNull(1, Types.DATE);
        } else {
            ps.setDate(1, new java.sql.Date(date.getTime()));
        }
        ps.setInt(2, schoolID);
        int x = ps.executeUpdate();
        return x > 0;
    }

    /**
     *
     * @param userid
     * @param orgid
     * @return
     * @throws DwoXmlRpcException
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    @Override
    public Hashtable login_saml(String userid, String orgid) throws DwoXmlRpcException,
            IOException, XmlRpcException, SQLException {
        close(); //for lazy connection
        //TODO DONE V1_3
        String QRY_LOGIN_SAML = "SELECT * FROM tblSamlUser "
                + "LEFT JOIN tblUser ON tblSamlUser.userID = tblUser.userID "
                + "LEFT JOIN tblStudentOf ON tblUser.classID = tblStudentOf.classID "
                + "WHERE (samluserid = ?) AND   (samlorgid = ?)";
        PreparedStatement ps = getStatement(QRY_LOGIN_SAML);
        ps.setString(1, userid);
        ps.setString(2, orgid);
        return login_tail(ps);
    }

    /**
     *
     * @param userid
     * @param orgid
     * @param id
     * @return
     * @throws SQLException
     */
    @Override
    public boolean link_saml(String userid, String orgid, int id)
            throws SQLException {
        String INSERT_SAML_USER = "INSERT INTO tblSamlUser(samluserid, samlorgid, userID) VALUE(?,?,?)";
        PreparedStatement ps = getStatement(INSERT_SAML_USER);
        try {
            ps.setString(1, userid);
            ps.setString(2, orgid);
            ps.setInt(3, id);
            ps.execute();
        } finally {
            ps.close();
        }
        return true;
    }

    /**
     * Adds a teacher to an existing class, only if he is not a member yet.
     *
     * @param classID
     * @param teacherID
     * @return true if (teacher,class) exists in table on exit.
     * @throws IOException
     * @throws SQLException
     * @throws XmlRpcException
     * @throws DwoXmlRpcException
     */
    @Override
    public boolean addTeacherToClass(int classID, int teacherID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
        // Fetch any teachers that are a member of the class.
        PreparedStatement ps = getStatement(QRY_SELECT_CLASS_TEACHER);
        ps.setInt(1, classID);
        ps.setInt(2, teacherID);
        ResultSet rs = ps.executeQuery();

        if (!isEmpty(rs)) {
            //teacher already exists hence return
            log.log(Level.FINE, "Teacher {0} already a member of class {1}. No insert done.", new Object[]{teacherID, classID});
            rs.close();
        } else {
            rs.close();
            // try inserting the teacher, though due to concurrency it might 
            // have been inserted already, via other page or applet
            ps = getStatement(QRY_ADD_TEACHER);
            ps.setInt(1, classID);
            ps.setInt(2, teacherID);
            try {
                log.log(Level.FINE, "Select did not find a teacher, attempting to insert teacher {0} to class {1}.", new Object[]{teacherID, classID});
                ps.execute();
                ps.close();
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    // MySQL duplicate entry error code detected.
                    log.log(Level.FINE, "Teacher {0} has been inserted concurrently into table. Proceeding without exception.", new Object[]{teacherID, classID});
                } else {
                    log.log(Level.FINE, "Unexpected error inserting teacher {0} into class {1}. Throwing exception.", new Object[]{teacherID, classID});
                    throw e;
                }
            }
        }
        return true;
    }

    /**
     * Removes a teacher of a class if a member
     *
     * @param classID
     * @param teacherID
     * @return true if teacher is no longer in table
     * @throws IOException
     * @throws SQLException
     * @throws XmlRpcException
     * @throws DwoXmlRpcException
     */
    @Override
    public boolean removeTeacherFromClass(int classID, int teacherID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
        // Delete the <teacher,class> entry
        PreparedStatement ps = getStatement(QRY_DELETE_CLASS_TEACHER);
        ps.setInt(1, classID);
        ps.setInt(2, teacherID);
        ps.executeQuery();
        ps.close();
        return true;
    }

    /**
     * Adds a student to a class if not exists
     *
     * @param classID
     * @param studentID
     * @return true if (student,class) exists in table on exit.
     * @throws IOException
     * @throws SQLException
     * @throws XmlRpcException
     * @throws DwoXmlRpcException
     */
    @Override
    public boolean addStudentToClass(int classID, int studentID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
        // Fetch any student that are a member of the class.
        PreparedStatement ps = getStatement(QRY_SELECT_CLASS_STUDENT);
        ps.setInt(1, classID);
        ps.setInt(2, studentID);
        ResultSet rs = ps.executeQuery();

        if (!isEmpty(rs)) {
            //student already exists hence return
            log.log(Level.FINE, "Student {0} already a member of class {1}. No insert done.", new Object[]{studentID, classID});
            rs.close();
            // return
        } else {
            // try inserting the student, though due to concurrency it might 
            // have been inserted already, via other page or applet
            ps = getStatement(QRY_ADD_STUDENT);
            ps.setInt(1, classID);
            ps.setInt(2, studentID);
            try {
                log.log(Level.FINE, "Select did not find a student, attempting to insert student {0} to class {1}.", new Object[]{studentID, classID});
                ps.execute();
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    // MySQL duplicate entry error code detected.
                    log.log(Level.FINE, "Student {0} has been inserted concurrently into table. Proceeding without exception.", new Object[]{studentID, classID});
                    //return
                } else {
                    log.log(Level.FINE, "Unexpected error inserting student {0} into class {1}. Throwing exception.", new Object[]{studentID, classID});
                    throw e;
                }
            }
        }
        return true;
    }

    /**
     * Removes a student from a class if a member
     *
     * @param classID
     * @param studentID
     * @return true if student is no longer in table
     * @throws IOException
     * @throws SQLException
     * @throws XmlRpcException
     * @throws DwoXmlRpcException
     */
    @Override
    public boolean removeStudentFromClass(int classID, int studentID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
        // Delete the <teacher,class> entry
        PreparedStatement ps = getStatement(QRY_DELETE_CLASS_STUDENT);
        ps.setInt(1, classID);
        ps.setInt(2, studentID);
        ps.executeQuery();

        ps = getStatement(QRY_CLEAR_USER_ROLE_DEFAULT_CLASS);
        ps.setInt(1, studentID);
        ps.setInt(2, classID);
        ps.executeQuery();
        
        ps.close();
        return true;
        // no error if deleting non-existing teacher
    }

    /**
     * Returns the classes to which the student is subscribed.
     *
     * @param userID
     * @return
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     */
    @Override
    public Vector<Object> getClassesOfStudent(int userID, int schoolID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {

        PreparedStatement ps = getStatement(QRY_SELECT_CLASSES_OF_STUDENT);
        ps.setInt(1, userID);
        ps.setInt(2, schoolID);
        Vector v = executeQueryWithResult(ps);
        log.log(Level.FINE, "Retrieved student-role classes of user {0} in school {1}.", new Object[]{userID,schoolID});
        return v;
    }

    /**
     * Returns the classes to which the student is subscribed.
     *
     * @param userID
     * @return
     * @throws fi.dwo.commons.exceptions.DwoXmlRpcException
     */
    @Override
    public Vector<Object> getClassesOfTeacher(int userID, int schoolID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {

        PreparedStatement ps = getStatement(QRY_SELECT_CLASSES_OF_TEACHER);
        ps.setInt(1, userID);
        ps.setInt(2, schoolID);
        Vector v = executeQueryWithResult(ps);
        log.log(Level.FINE, "Retrieved teaching-role classes of user {0} in school {1}.", new Object[]{userID,schoolID});
        return v;
    }

    @Override
    public boolean isInStudentRole(int userID, int schoolID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
        PreparedStatement ps = getStatement(QRY_IS_IN_STUDENT_ROLE);
        ps.setInt(1, userID);
        ps.setInt(1, schoolID);
        Vector v = executeQueryWithResult(ps);
        log.log(Level.FINE, "Testing if user {0} is a teacher at school {1}, result is: {2}", new Object[]{userID, schoolID, v});
        return v.size() > 0;
    }

    @Override
    public boolean isInTeacherRole(int userID, int schoolID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
        PreparedStatement ps = getStatement(QRY_IS_IN_TEACHER_ROLE);
        ps.setInt(1, userID);
        ps.setInt(1, schoolID);
        Vector v = executeQueryWithResult(ps);
        log.log(Level.FINE, "Testing if user {0} is a teacher at school {1}, result is: {2}", new Object[]{userID, schoolID, v});
        return v.size() > 0;
    }

    private int getCurSchoolGroup(int userID) throws SQLException, DwoXmlRpcException {
        PreparedStatement ps = getStatement("select schoolGroupID from tblUser where userID = ? ");
        ps.setInt(1, userID);
        Vector v = executeQueryWithResult(ps);
        if(v.get(0)==null) return -1;
        return (Integer) v.get(0);
    }

    
}
