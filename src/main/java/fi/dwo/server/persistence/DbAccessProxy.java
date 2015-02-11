package fi.dwo.server.persistence;

import fi.dwo.commons.exceptions.DwoXmlRpcException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.beans.jdbc.DbConnect;
import fi.beans.jdbc.DbConnectIF;
import fi.dwo.commons.persistence.DbAccessIF;
import fi.dwo.commons.exceptions.LoginException;

public abstract class DbAccessProxy implements DbAccessIF, DbConnectIF {

	protected abstract DbAccessIF createDelegate();
	
	ThreadLocal delegate = new ThreadLocal() {

		protected Object initialValue() {
			return createDelegate();
		}
		
	};
	
	
	public void close() {
		DbConnectIF connector = (DbConnectIF) delegate.get();
		connector.close();
	}

	protected DbAccessIF getDelegate() {
		DbAccessIF local = (DbAccessIF) delegate.get();
		return local;
	}

	public Vector getCoursesForClass(int classID) throws IOException,
			XmlRpcException, SQLException {
		return getDelegate().getCoursesForClass(classID);
	}

	public boolean selectCoursesForClass(int classID, int courseID)
			throws IOException, XmlRpcException, SQLException {
		return getDelegate().selectCoursesForClass(classID, courseID);
	}

	public boolean deSelectCoursesForClass(int classID, int courseID)
			throws IOException, XmlRpcException, SQLException {
		return getDelegate().deSelectCoursesForClass(classID, courseID);
	}

	public Vector getCourses(int userID) throws IOException, XmlRpcException,
			SQLException {
		return getDelegate().getCourses(userID);
	}

//	public Vector getCourses(int userID, boolean showAll) throws IOException,
//			XmlRpcException, SQLException {
//		return getDelegate().getCourses(userID, showAll);
//	}

	public Vector getEditableCoursesAdmin() throws IOException,
			XmlRpcException, SQLException {
		return getDelegate().getEditableCoursesAdmin();
	}

	public Vector getEditableCourses(int schoolID) throws IOException,
			XmlRpcException, SQLException {
		return getDelegate().getEditableCourses(schoolID);
	}

	public Hashtable getRecord(String tableName, String idCol, int oid)
			throws IOException, XmlRpcException, SQLException {
		return getDelegate().getRecord(tableName, idCol, oid);
	}

	public Vector getTable(String tableName) throws IOException,
			XmlRpcException, SQLException {
		return getDelegate().getTable(tableName);
	}

	public Vector getTable(String tableName, String orderCol)
			throws IOException, XmlRpcException, SQLException {
		return getDelegate().getTable(tableName, orderCol);
	}

	public Vector getTable(String tableName, Hashtable wheredef)
			throws IOException, XmlRpcException, SQLException {
		return getDelegate().getTable(tableName, wheredef);
	}

	public Vector getTable(String tableName, Hashtable wheredef, String orderBy)
			throws IOException, XmlRpcException, SQLException {
		return getDelegate().getTable(tableName, wheredef, orderBy);
	}

	public Vector getTable(String tableName, Vector columnnames,
			Hashtable wheredef, String orderBy) throws IOException,
			XmlRpcException, SQLException {
		return getDelegate().getTable(tableName, columnnames, wheredef, orderBy);
	}

	public boolean renameClass(int classID, String newName)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return getDelegate().renameClass(classID, newName);
	}
	
	public boolean renameClass(int classID, String newName, boolean iconizer)
	throws DwoXmlRpcException, IOException, XmlRpcException,
	SQLException {
		return getDelegate().renameClass(classID, newName, iconizer);
	}

	public boolean reassignClass(int classID, int newTeacher)
			throws IOException, SQLException, XmlRpcException,
			DwoXmlRpcException {
		return getDelegate().reassignClass(classID, newTeacher);
	}

	public boolean register(String username, String password, String firstname,
			String middlename, String lastname, String email)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return getDelegate().register(username, password, firstname, middlename,
				lastname, email);
	}

	public String LMSGetValue(int scoID, int userID, String iDataModelElement)
			throws IOException, XmlRpcException, SQLException {
		return getDelegate().LMSGetValue(scoID, userID, iDataModelElement);
	}

	public String LMSSetValue(int scoID, int userID, String iDataModelElement,
			String iValue) throws IOException, XmlRpcException, SQLException {
		return getDelegate().LMSSetValue(scoID, userID, iDataModelElement, iValue);
	}

	public String LMSSetValue(int scoID, int userID, String iDataModelElement,
			String iValue, String random) throws IOException, XmlRpcException,
			SQLException {
		return getDelegate().LMSSetValue(scoID, userID, iDataModelElement, iValue,
				random);
	}

	public boolean register(String username, String password, String firstname,
			String middlename, String lastname, String email,
			String schoolLogin, int groupID, String groupPassword)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return getDelegate().register(username, password, firstname, middlename,
				lastname, email, schoolLogin, groupID, groupPassword);
	}

	public Hashtable login(String username, String password)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		try {
			return getDelegate().login(username, password);
		} catch (DwoXmlRpcException go)
		{ if (go.code != LoginException.LE_UNKNOWN_USER) 
				go.printStackTrace(); // Expected
		  throw go; 
		} catch (XmlRpcException go)     { go.printStackTrace(); throw go;
		} catch (IOException go)         { go.printStackTrace(); throw go;
		} catch (SQLException go)        { go.printStackTrace(); throw go;
		} catch (RuntimeException go)    { go.printStackTrace(); throw go;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(),e); // wrap unexpected exception
		}
	}

	public Hashtable addToSchool(int userID, String schoolLogin, int groupID,
			String groupPassword) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return getDelegate()
				.addToSchool(userID, schoolLogin, groupID, groupPassword);
	}

	public boolean changeAccount(int userID, String password,
			String newPassword, String firstname, String middlename,
			String lastname, String email, int classID)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return getDelegate().changeAccount(userID, password, newPassword, firstname,
				middlename, lastname, email, classID);
	}

	public boolean changeAccount(int userID, String password,
			String newPassword, String firstname, String middlename,
			String lastname, String email) throws DwoXmlRpcException,
			IOException, XmlRpcException, SQLException {
		return getDelegate().changeAccount(userID, password, newPassword, firstname,
				middlename, lastname, email);
	}

	public Hashtable addClass(int teacher, String className)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return getDelegate().addClass(teacher, className);
	}

	public Hashtable addSchool(String schoolName, String schoolLogin,
			String studentPassw, String teacherPassw)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return getDelegate().addSchool(schoolName, schoolLogin, studentPassw,
				teacherPassw);
	}

	public Hashtable addSchool(int schoolId, String schoolName,
			String schoolLogin, String studentPassw, String teacherPassw)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return getDelegate().addSchool(schoolId, schoolName, schoolLogin,
				studentPassw, teacherPassw);
	}

	public Hashtable editSchool(int schoolID, String schoolName,
			String schoolLogin, String studentPassw, String teacherPassw)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return getDelegate().editSchool(schoolID, schoolName, schoolLogin,
				studentPassw, teacherPassw);
	}

	public boolean deleteUser(int userID) throws IOException, XmlRpcException,
			SQLException {
		return getDelegate().deleteUser(userID);
	}

	public boolean deleteClass(int classID, boolean mustEmpty)
			throws IOException, XmlRpcException, SQLException {
		return getDelegate().deleteClass(classID, mustEmpty);
	}

	public Vector getResults(Vector courses, int userID) throws IOException,
			XmlRpcException, SQLException {
		return getDelegate().getResults(courses, userID);
	}

	public Vector getResults(Vector courses, int classID, int userID)
			throws IOException, XmlRpcException, SQLException {
		return getDelegate().getResults(courses, classID, userID);
	}

	public Vector getResults(int courseID, int classID, int userID)
			throws IOException, XmlRpcException, SQLException {
		return getDelegate().getResults(courseID, classID, userID);
	}

	public Vector getResults(int courseID, int userID) throws IOException,
			XmlRpcException, SQLException {
		return getDelegate().getResults(courseID, userID);
	}

	public boolean disconnectFromClass(int uid) throws IOException,
			XmlRpcException, SQLException {
		return getDelegate().disconnectFromClass(uid);
	}

	public boolean selectJar(String key, String jar) throws IOException,
			XmlRpcException, SQLException {
		return getDelegate().selectJar(key, jar);
	}

	public boolean reconnect() throws IOException, XmlRpcException,
			SQLException {
		return getDelegate().reconnect();
	}

	public boolean log(String s) throws IOException, XmlRpcException {
		return getDelegate().log(s);
	}

	public int addCourse(int schoolID, String name, String description,
			int dwoProfile) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return getDelegate().addCourse(schoolID, name, description, dwoProfile);
	}

	public boolean changeCourse(int courseID, String name, String description)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return getDelegate().changeCourse(courseID, name, description);
	}

	public boolean deleteCourse(int courseID) throws DwoXmlRpcException,
			IOException, XmlRpcException, SQLException {
		return getDelegate().deleteCourse(courseID);
	}

	public int addSco(int courseID, String name, String description,
			int appletConfigID, int sequencenr) throws DwoXmlRpcException,
			IOException, XmlRpcException, SQLException {
		return getDelegate().addSco(courseID, name, description, appletConfigID,
				sequencenr);
	}

	public int addSco(int courseID, String name, String description,
			int appletID, String launchdata, int sequencenr)
			throws IOException, XmlRpcException, SQLException,
			DwoXmlRpcException {
		return getDelegate().addSco(courseID, name, description, appletID,
				launchdata, sequencenr);
	}

	public int addSco(int id, String name, String description,
			int appletConfigID, int sequencenr, boolean showScore)
			throws IOException, XmlRpcException, SQLException,
			DwoXmlRpcException {
		return getDelegate().addSco(id, name, description, appletConfigID,
				sequencenr, showScore);
	}

	public boolean changeSco(int scoID, String name, String description,
			String launchdata) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return getDelegate().changeSco(scoID, name, description, launchdata);
	}

	public boolean changeSco(int scoID, String name, String description)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return getDelegate().changeSco(scoID, name, description);
	}

	public boolean changeSco(int scoID, String name, String description,
			boolean showScore) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return getDelegate().changeSco(scoID, name, description, showScore);
	}
	
    public boolean changeSco(int scoID, String name, String description, boolean delete, String launchdata)
    throws DwoXmlRpcException, IOException, XmlRpcException,
    SQLException {
    	return getDelegate().changeSco(scoID, name, description, delete, launchdata);
    }

    public boolean changeSco(int scoID, String name, String description, boolean delete, byte[] launchdata, boolean showScore)
    throws DwoXmlRpcException, IOException, XmlRpcException,
    SQLException {
    	return getDelegate().changeSco(scoID, name, description, delete, launchdata, showScore);
    }
    
	public boolean changeSco(int id, String scoName, String description,
			String launchdataString, boolean showScore)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return getDelegate().changeSco(id, scoName, description, launchdataString,
				showScore);
	}

	public boolean changeScoSequenceNr(int scoID, int sequencenr, int scoID2,
			int sequencenr2) throws SQLException, DwoXmlRpcException,
			IOException, XmlRpcException {
		return getDelegate().changeScoSequenceNr(scoID, sequencenr, scoID2,
				sequencenr2);
	}

	public boolean deleteSco(int scoID) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return getDelegate().deleteSco(scoID);
	}

	public boolean deleteSchool(int schoolID) throws IOException,
			XmlRpcException, SQLException {
		return getDelegate().deleteSchool(schoolID);
	}

	public Hashtable getFidentitySchools() throws IOException, XmlRpcException,
			SQLException, DwoXmlRpcException {
		return getDelegate().getFidentitySchools();
	}

	public Vector getUserResults(int courseID, int userID) throws IOException,
			XmlRpcException, SQLException {
		return getDelegate().getUserResults(courseID, userID);
	}

	public Vector getUserResults(Vector courses, int i) throws SQLException,
			IOException, XmlRpcException {
		return getDelegate().getUserResults(courses, i);
	}

	public boolean setLogo(int id, byte[] image) throws SQLException,
			IOException, XmlRpcException {
		return getDelegate().setLogo(id, image);
	}

	public boolean changeCourse(int id, String name, String description,
			boolean export) throws IOException, XmlRpcException, SQLException,
			DwoXmlRpcException {
		return getDelegate().changeCourse(id, name, description, export);
	}

	public boolean editSchool(int schoolID, boolean export) throws IOException,
			XmlRpcException, SQLException {
		return getDelegate().editSchool(schoolID, export);
	}
	
	public boolean editSchoolRights(int schoolID, String rights) throws IOException,
		XmlRpcException, SQLException {
		return getDelegate().editSchoolRights(schoolID, rights);
	}

	public Vector getImportCourses(int schoolFrom, int schoolTo, int profileID)
			throws IOException, XmlRpcException, SQLException {
		return getDelegate().getImportCourses(schoolFrom, schoolTo, profileID);
	}

	public Hashtable editSchool(int schoolID, String schoolName,
			String schoolLogin, Hashtable passwd) throws IOException,
			XmlRpcException, SQLException, DwoXmlRpcException {
		return getDelegate().editSchool(schoolID, schoolName, schoolLogin, passwd);
	}

	public Hashtable addSchool(int schoolId, String schoolName,
			String schoolLogin, Hashtable passwd) throws DwoXmlRpcException,
			IOException, XmlRpcException, SQLException, DwoXmlRpcException {
		return getDelegate().addSchool(schoolId, schoolName, schoolLogin, passwd);
	}

	public boolean deleteUserFromSchool(int id, int schoolID)
			throws IOException, XmlRpcException, SQLException {
		return getDelegate().deleteUserFromSchool(id, schoolID);
	}

	public boolean updateSchoolTo(int schoolID, Vector schoolTo)
			throws IOException, XmlRpcException, SQLException {
		return getDelegate().updateSchoolTo(schoolID, schoolTo);
	}

	public boolean deleteCourseDataFromClass(int courseID, int classID)
			throws SQLException, IOException, XmlRpcException {
		return getDelegate().deleteCourseDataFromClass(courseID, classID);
	}

	public Vector getResultCount(int profile, int classId) throws SQLException,
			IOException, XmlRpcException {
		return getDelegate().getResultCount(profile, classId);
	}

	public String setRights(int uid, int profileid, String rights)
			throws SQLException, IOException, XmlRpcException {
		return getDelegate().setRights(uid, profileid, rights);
	}

	public boolean changeCourse(int id, String name, String description,
			boolean export, int schoolID) throws DwoXmlRpcException,
			IOException, XmlRpcException, SQLException {
		return getDelegate().changeCourse(id, name, description, export, schoolID);
	}

	public boolean changeCourse(int id, String name, String description,
			boolean export, int schoolID, int parentID) throws DwoXmlRpcException,
			IOException, XmlRpcException, SQLException {
		return getDelegate().changeCourse(id, name, description, export, schoolID, parentID);
	}

	public int addCourse(int schoolID, String name, String description,
			int dwoProfile, int parentID, boolean withChildren)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return getDelegate().addCourse(schoolID, name, description, dwoProfile, parentID, withChildren);
	}

	public boolean setCourseSequence(Vector vector, int schoolID, int classID,
			int parent, int profileID) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return getDelegate().setCourseSequence(vector, schoolID, classID, parent, profileID);
	}

	public boolean moveSco(int scoId, int courseId, int sequencenr, String name)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return getDelegate().moveSco(scoId, courseId, sequencenr, name);
	}

	public boolean selectCoursesForClass(int classID, int courseID, int type,
			Date van, Date tot) throws IOException, XmlRpcException,
			SQLException {
		return getDelegate().selectCoursesForClass(classID, courseID, type, van, tot);
	}

	public boolean selectCoursesForClass(int id, Vector v) throws IOException,
			XmlRpcException, SQLException {
		return getDelegate().selectCoursesForClass(id, v);
	}

	public boolean setExpireDate(int schoolID, Date date) throws IOException,
			XmlRpcException, SQLException {
		// TODO Auto-generated method stub
		return getDelegate().setExpireDate(schoolID, date);
	}

	public Hashtable login_saml(String a, String b) throws DwoXmlRpcException,
			IOException, XmlRpcException, SQLException {
		return getDelegate().login_saml(a, b);

	}

	public boolean link_saml(String userid, String orgid, int id)
			throws IOException, XmlRpcException, SQLException {
		return getDelegate().link_saml(userid, orgid, id);
	}
	
}
