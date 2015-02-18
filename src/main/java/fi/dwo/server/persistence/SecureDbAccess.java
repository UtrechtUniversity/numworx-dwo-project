/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.persistence;

import fi.dwo.commons.exceptions.DwoXmlRpcException;
import fi.dwo.commons.persistence.DbAccessIF;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.xmlrpc.applet.XmlRpcException;

/**
 * This class extends DbAccess and adds a security layer
 *
 * @author plas0006
 */
public class SecureDbAccess implements DbAccessIF {

    @Override
    public Vector getCoursesForClass(int classID) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean selectCoursesForClass(int classID, int courseID) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deSelectCoursesForClass(int classID, int courseID) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getCourses(int userID) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getEditableCoursesAdmin() throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getEditableCourses(int schoolID) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Hashtable getRecord(String tableName, String idCol, int oid) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getTable(String tableName) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getTable(String tableName, String orderCol) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getTable(String tableName, Hashtable wheredef) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getTable(String tableName, Hashtable wheredef, String orderBy) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getTable(String tableName, Vector columnnames, Hashtable wheredef, String orderBy) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean renameClass(int classID, String newName, boolean iconizer) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean renameClass(int classID, String newName) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean reassignClass(int classID, int newTeacher) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean register(String username, String password, String firstname, String middlename, String lastname, String email) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String LMSGetValue(int scoID, int userID, String iDataModelElement) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String LMSSetValue(int scoID, int userID, String iDataModelElement, String iValue) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String LMSSetValue(int scoID, int userID, String iDataModelElement, String iValue, String random) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean register(String username, String password, String firstname, String middlename, String lastname, String email, String schoolLogin, int groupID, String groupPassword) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Hashtable login(String username, String password) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Hashtable addToSchool(int userID, String schoolLogin, int groupID, String groupPassword) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeAccount(int userID, String password, String newPassword, String firstname, String middlename, String lastname, String email, int classID) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeAccount(int userID, String password, String newPassword, String firstname, String middlename, String lastname, String email) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Hashtable addClass(int teacher, String className) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Hashtable addSchool(String schoolName, String schoolLogin, String studentPassw, String teacherPassw) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Hashtable addSchool(int schoolId, String schoolName, String schoolLogin, String studentPassw, String teacherPassw) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Hashtable editSchool(int schoolID, String schoolName, String schoolLogin, String studentPassw, String teacherPassw) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteUser(int userID) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteClass(int classID, boolean mustEmpty) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getResults(Vector courses, int userID) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getResults(Vector courses, int classID, int userID) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getResults(int courseID, int classID, int userID) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getResults(int courseID, int userID) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean disconnectFromClass(int uid) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean selectJar(String key, String jar) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean reconnect() throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean log(String s) throws IOException, XmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int addCourse(int schoolID, String name, String description, int dwoProfile, int parentID, boolean withChildren) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int addCourse(int schoolID, String name, String description, int dwoProfile) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeCourse(int courseID, String name, String description) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteCourse(int courseID) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int addSco(int courseID, String name, String description, int appletConfigID, int sequencenr) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int addSco(int courseID, String name, String description, int appletID, String launchdata, int sequencenr) throws IOException, XmlRpcException, SQLException, DwoXmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int addSco(int id, String name, String description, int appletConfigID, int sequencenr, boolean showScore) throws IOException, XmlRpcException, SQLException, DwoXmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeSco(int scoID, String name, String description, String launchdata) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeSco(int scoID, String name, String description) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeSco(int scoID, String name, String description, boolean showScore) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeSco(int id, String scoName, String description, String launchdataString, boolean showScore) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeSco(int scoID, String name, String description, boolean delete, String launchdata) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeScoSequenceNr(int scoID, int sequencenr, int scoID2, int sequencenr2) throws SQLException, DwoXmlRpcException, IOException, XmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteSco(int scoID) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteSchool(int schoolID) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Hashtable getFidentitySchools() throws IOException, XmlRpcException, SQLException, DwoXmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getUserResults(int courseID, int userID) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getUserResults(Vector courses, int i) throws SQLException, IOException, XmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean setLogo(int id, byte[] image) throws SQLException, IOException, XmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeCourse(int id, String name, String description, boolean export) throws IOException, XmlRpcException, SQLException, DwoXmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean editSchool(int schoolID, boolean export) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean editSchoolRights(int schoolID, String rights) throws IOException, SQLException, XmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getImportCourses(int schoolFrom, int schoolTo, int profileID) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Hashtable editSchool(int schoolID, String schoolName, String schoolLogin, Hashtable passwd) throws IOException, XmlRpcException, SQLException, DwoXmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Hashtable addSchool(int schoolId, String schoolName, String schoolLogin, Hashtable passwd) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException, DwoXmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteUserFromSchool(int id, int schoolID) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updateSchoolTo(int schoolID, Vector schoolTo) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteCourseDataFromClass(int courseID, int classID) throws SQLException, IOException, XmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getResultCount(int profile, int classId) throws SQLException, IOException, XmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String setRights(int uid, int profileid, String rights) throws SQLException, IOException, XmlRpcException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeCourse(int id, String name, String description, boolean export, int schoolID) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeCourse(int id, String name, String description, boolean export, int schoolID, int parentID) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean setCourseSequence(Vector vector, int schoolID, int classID, int parent, int profileID) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean moveSco(int scoId, int courseId, int sequencenr, String name) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean selectCoursesForClass(int classID, int courseID, int type, Date van, Date tot) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean selectCoursesForClass(int id, Vector v) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean setExpireDate(int schoolID, Date date) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Hashtable login_saml(String a, String b) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean link_saml(String userid, String orgid, int id) throws IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeSco(int scoid, String scoName, String description, boolean delete, byte[] launchdata, boolean showScore) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
