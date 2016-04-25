package fi.dwo.server.persistence;

import fi.dwo.commons.exceptions.DwoXmlRpcException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.beans.jdbc.DbConnectIF;
import fi.dwo.commons.persistence.DbAccessIF;
import fi.dwo.commons.persistence.ScormAccessIF;
import fi.dwo.commons.exceptions.LoginException;

import java.util.logging.Level;

public abstract class DbAccessProxy implements DbAccessIF, DbConnectIF, ScormAccessIF, DbAccessJS {
//TODO this class should use reflection to delegate stuff going to be purely serverside.
//
    protected abstract DbAccessIF createDelegate();
	
	ThreadLocal<DbAccessIF> delegate = new ThreadLocal<DbAccessIF>() {
		protected DbAccessIF initialValue() {
			return createDelegate();
		}
		
	};
	
	
	public void close() {
		DbConnectIF connector = (DbConnectIF) delegate.get();
		connector.close();
		delegate.remove();
	}

	protected DbAccessIF getDelegate() {
		DbAccessIF local = delegate.get();
		return local;
	}


    @Override
    public Vector getCoursesForClass(int classID) throws IOException,
            XmlRpcException, SQLException {
        return getDelegate().getCoursesForClass(classID);
    }

    @Override
    public boolean selectCoursesForClass(int classID, int courseID)
            throws IOException, XmlRpcException, SQLException {
        return getDelegate().selectCoursesForClass(classID, courseID);
    }

    @Override
    public boolean deSelectCoursesForClass(int classID, int courseID)
            throws IOException, XmlRpcException, SQLException {
        return getDelegate().deSelectCoursesForClass(classID, courseID);
    }

    @Override
    public Vector getCourses(int profileValue) throws IOException, XmlRpcException,
            SQLException {
        return getDelegate().getCourses(profileValue);
    }

//	public Vector getCourses(int userID, boolean showAll) throws IOException,
//			XmlRpcException, SQLException {
//		return getDelegate().getCourses(userID, showAll);
//	}
    @Override
    public Vector getEditableCoursesAdmin() throws IOException,
            XmlRpcException, SQLException {
        return getDelegate().getEditableCoursesAdmin();
    }

    @Override
    public Vector getEditableCourses(int schoolID) throws IOException,
            XmlRpcException, SQLException {
        return getDelegate().getEditableCourses(schoolID);
    }

    @Override
    public Hashtable getRecord(String tableName, String idCol, int oid)
            throws IOException, XmlRpcException, SQLException {
        return getDelegate().getRecord(tableName, idCol, oid);
    }

    @Override
    public Vector getTable(String tableName) throws IOException,
            XmlRpcException, SQLException {
        return getDelegate().getTable(tableName);
    }

    @Override
    public Vector getTable(String tableName, String orderCol)
            throws IOException, XmlRpcException, SQLException {
        return getDelegate().getTable(tableName, orderCol);
    }

    @Override
    public Vector getTable(String tableName, Hashtable wheredef)
            throws IOException, XmlRpcException, SQLException {
        return getDelegate().getTable(tableName, wheredef);
    }

    @Override
    public Vector getTable(String tableName, Hashtable wheredef, String orderBy)
            throws IOException, XmlRpcException, SQLException {
        return getDelegate().getTable(tableName, wheredef, orderBy);
    }

    @Override
    public Vector getTable(String tableName, Vector columnnames,
            Hashtable wheredef, String orderBy) throws IOException,
            XmlRpcException, SQLException {
        return getDelegate().getTable(tableName, columnnames, wheredef, orderBy);
    }

    @Override
    public boolean renameClass(int classID, String newName)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().renameClass(classID, newName);
    }

    @Override
    public boolean renameClass(int classID, String newName, boolean iconizer)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().renameClass(classID, newName, iconizer);
    }
//TODO V1_2 obsolete
//    @Override
//    public boolean reassignClass(int classID, int newTeacher)
//            throws IOException, SQLException, XmlRpcException,
//            DwoXmlRpcException {
//        return getDelegate().reassignClass(classID, newTeacher);
//    }

    @Override
    public boolean register(String username, String password, String firstname,
            String middlename, String lastname, String email)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().register(username, password, firstname, middlename,
                lastname, email);
    }

    @Override
    public String LMSGetValue(int scoID, int userID, int schoolGroupID, String iDataModelElement)
            throws IOException, XmlRpcException, SQLException {
        return getDelegate().LMSGetValue(scoID, userID, schoolGroupID, iDataModelElement);
    }

    @Override
    public String LMSSetValue(int scoID, int userID, int schoolGroupID, String iDataModelElement,
            String iValue) throws IOException, XmlRpcException, SQLException {
        return getDelegate().LMSSetValue(scoID, userID, schoolGroupID, iDataModelElement, iValue);
    }

    @Override
    public String LMSSetValue(int scoID, int userID, int schoolGroupID, String iDataModelElement,
            String iValue, String random) throws IOException, XmlRpcException,
            SQLException {
        return getDelegate().LMSSetValue(scoID, userID, schoolGroupID, iDataModelElement, iValue,
                random);
    }

    @Override
    public boolean register(String username, String password, String firstname,
            String middlename, String lastname, String email,
            String schoolLogin, int groupID, String groupPassword)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().register(username, password, firstname, middlename,
                lastname, email, schoolLogin, groupID, groupPassword);
    }

    @Override
    public Hashtable login(String username, String password)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        try {
            return getDelegate().login(username, password);
        } catch (DwoXmlRpcException go) {
            if (go.code != LoginException.LE_UNKNOWN_USER) {
                go.printStackTrace(); // Expected
            }
            throw go;
        } catch (XmlRpcException go) {
            go.printStackTrace();
            throw go;
        } catch (IOException go) {
            go.printStackTrace();
            throw go;
        } catch (SQLException go) {
            go.printStackTrace();
            throw go;
        } catch (RuntimeException go) {
            go.printStackTrace();
            throw go;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e); // wrap unexpected exception
        }
    }

    @Override
    public Hashtable addToSchool(int userID, String schoolLogin, int groupID,
            String groupPassword) throws DwoXmlRpcException, IOException,
            XmlRpcException, SQLException {
        return getDelegate()
                .addToSchool(userID, schoolLogin, groupID, groupPassword);
    }

    //TODO V1_2 obsolete
//    @Override
//    public boolean changeAccount(int userID, String password,
//            String newPassword, String firstname, String middlename,
//            String lastname, String email, int classID)
//            throws DwoXmlRpcException, IOException, XmlRpcException,
//            SQLException {
//        return getDelegate().changeAccount(userID, password, newPassword, firstname,
//                middlename, lastname, email, classID);
//    }
    @Override
    public boolean changeAccount(int userID, String password,
            String newPassword, String firstname, String middlename,
            String lastname, String email) throws DwoXmlRpcException,
            IOException, XmlRpcException, SQLException {
        return getDelegate().changeAccount(userID, password, newPassword, firstname,
                middlename, lastname, email);
    }

    @Override
    public Hashtable addClass(int teacher, String className)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().addClass(teacher, className);
    }

    @Override
    public Hashtable addSchool(String schoolName, String schoolLogin,
            String studentPassw, String teacherPassw)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().addSchool(schoolName, schoolLogin, studentPassw,
                teacherPassw);
    }

    @Override
    public Hashtable addSchool(int schoolId, String schoolName,
            String schoolLogin, String studentPassw, String teacherPassw)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().addSchool(schoolId, schoolName, schoolLogin,
                studentPassw, teacherPassw);
    }

    @Override
    public Hashtable editSchool(int schoolID, String schoolName,
            String schoolLogin, String studentPassw, String teacherPassw)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().editSchool(schoolID, schoolName, schoolLogin,
                studentPassw, teacherPassw);
    }

    @Override
    public boolean deleteUser(int userID) throws IOException, XmlRpcException,
            SQLException {
        return getDelegate().deleteUser(userID);
    }

    @Override
    public boolean deleteClass(int classID, boolean mustEmpty)
            throws IOException, XmlRpcException, SQLException {
        return getDelegate().deleteClass(classID, mustEmpty);
    }

    @Override
    public Vector getResults(Vector courses, int userID) throws IOException,
            XmlRpcException, SQLException {
        return getDelegate().getResults(courses, userID);
    }

    @Override
    public Vector getResults(Vector courses, int classID, int userID)
            throws IOException, XmlRpcException, SQLException {
        return getDelegate().getResults(courses, classID, userID);
    }

    @Override
    public Vector getResults(int courseID, int classID, int userID)
            throws IOException, XmlRpcException, SQLException {
        return getDelegate().getResults(courseID, classID, userID);
    }

    @Override
    public Vector getResults(int courseID, int userID) throws IOException,
            XmlRpcException, SQLException {
        return getDelegate().getResults(courseID, userID);
    }
//TODO V1_2 obsolete
//    @Override
//    public boolean disconnectFromClass(int uid) throws IOException,
//            XmlRpcException, SQLException {
//        return getDelegate().disconnectFromClass(uid);
//    }

//    @Override
//    public boolean selectJar(String key, String jar) throws IOException,
//            XmlRpcException, SQLException {
//        return getDelegate().selectJar(key, jar);
//    }

    @Override
    public boolean log(String s) throws IOException, XmlRpcException {
        return getDelegate().log(s);
    }

    @Override
    public boolean log(Level level, String s) throws IOException, XmlRpcException {
        return getDelegate().log(level,s);
    }
    
    @Override
    public int addCourse(int schoolID, String name, String description,
            int dwoProfile) throws DwoXmlRpcException, IOException,
            XmlRpcException, SQLException {
        return getDelegate().addCourse(schoolID, name, description, dwoProfile);
    }

    @Override
    public boolean changeCourse(int courseID, String name, String description)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().changeCourse(courseID, name, description);
    }

    @Override
    public boolean deleteCourse(int courseID) throws DwoXmlRpcException,
            IOException, XmlRpcException, SQLException {
        return getDelegate().deleteCourse(courseID);
    }

    @Override
    public int addSco(int courseID, String name, String description,
            int appletConfigID, int sequencenr) throws DwoXmlRpcException,
            IOException, XmlRpcException, SQLException {
        return getDelegate().addSco(courseID, name, description, appletConfigID,
                sequencenr);
    }

    @Override
    public int addSco(int courseID, String name, String description,
            int appletID, String launchdata, int sequencenr)
            throws IOException, XmlRpcException, SQLException,
            DwoXmlRpcException {
        return getDelegate().addSco(courseID, name, description, appletID,
                launchdata, sequencenr);
    }

    @Override
    public int addSco(int id, String name, String description,
            int appletConfigID, int sequencenr, boolean showScore)
            throws IOException, XmlRpcException, SQLException,
            DwoXmlRpcException {
        return getDelegate().addSco(id, name, description, appletConfigID,
                sequencenr, showScore);
    }

    @Override
    public boolean changeSco(int scoID, String name, String description,
            String launchdata) throws DwoXmlRpcException, IOException,
            XmlRpcException, SQLException {
        return getDelegate().changeSco(scoID, name, description, launchdata);
    }

    @Override
    public boolean changeSco(int scoID, String name, String description)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().changeSco(scoID, name, description);
    }

    @Override
    public boolean changeSco(int scoID, String name, String description,
            boolean showScore) throws DwoXmlRpcException, IOException,
            XmlRpcException, SQLException {
        return getDelegate().changeSco(scoID, name, description, showScore);
    }

    @Override
    public boolean changeSco(int scoID, String name, String description, boolean delete, String launchdata)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().changeSco(scoID, name, description, delete, launchdata);
    }

    @Override
    public boolean changeSco(int scoID, String name, String description, boolean delete, byte[] launchdata, boolean showScore)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().changeSco(scoID, name, description, delete, launchdata, showScore);
    }

    @Override
    public boolean changeSco(int id, String scoName, String description,
            String launchdataString, boolean showScore)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().changeSco(id, scoName, description, launchdataString,
                showScore);
    }

    @Override
    public boolean changeScoSequenceNr(int scoID, int sequencenr, int scoID2,
            int sequencenr2) throws SQLException, DwoXmlRpcException,
            IOException, XmlRpcException {
        return getDelegate().changeScoSequenceNr(scoID, sequencenr, scoID2,
                sequencenr2);
    }

    @Override
    public boolean deleteSco(int scoID) throws DwoXmlRpcException, IOException,
            XmlRpcException, SQLException {
        return getDelegate().deleteSco(scoID);
    }

    @Override
    public boolean deleteSchool(int schoolID) throws IOException,
            XmlRpcException, SQLException {
        return getDelegate().deleteSchool(schoolID);
    }

    @Override
    public Hashtable getFidentitySchools() throws IOException, XmlRpcException,
            SQLException, DwoXmlRpcException {
        return getDelegate().getFidentitySchools();
    }

    @Override
    public Vector getUserResults(int courseID, int userID) throws IOException,
            XmlRpcException, SQLException {
        return getDelegate().getUserResults(courseID, userID);
    }

    @Override
    public Vector getUserResults(Vector courses, int i) throws SQLException,
            IOException, XmlRpcException {
        return getDelegate().getUserResults(courses, i);
    }

    @Override
    public boolean setLogo(int id, byte[] image) throws SQLException,
            IOException, XmlRpcException {
        return getDelegate().setLogo(id, image);
    }

    @Override
    public boolean changeCourse(int id, String name, String description,
            boolean export) throws IOException, XmlRpcException, SQLException,
            DwoXmlRpcException {
        return getDelegate().changeCourse(id, name, description, export);
    }

    @Override
    public boolean editSchool(int schoolID, boolean export) throws IOException,
            XmlRpcException, SQLException {
        return getDelegate().editSchool(schoolID, export);
    }

    @Override
    public boolean editSchoolRights(int schoolID, String rights) throws IOException,
            XmlRpcException, SQLException {
        return getDelegate().editSchoolRights(schoolID, rights);
    }

    @Override
    public Vector getImportCourses(int schoolFrom, int schoolTo, int profileID)
            throws IOException, XmlRpcException, SQLException {
        return getDelegate().getImportCourses(schoolFrom, schoolTo, profileID);
    }

    @Override
    public Hashtable editSchool(int schoolID, String schoolName,
            String schoolLogin, Hashtable passwd) throws IOException,
            XmlRpcException, SQLException, DwoXmlRpcException {
        return getDelegate().editSchool(schoolID, schoolName, schoolLogin, passwd);
    }

    @Override
    public Hashtable addSchool(int schoolId, String schoolName,
            String schoolLogin, Hashtable passwd) throws DwoXmlRpcException,
            IOException, XmlRpcException, SQLException, DwoXmlRpcException {
        return getDelegate().addSchool(schoolId, schoolName, schoolLogin, passwd);
    }

    @Override
    public boolean deleteUserWithRoleFromSchool(int id, int schoolID)
            throws IOException, XmlRpcException, SQLException {
        return getDelegate().deleteUserWithRoleFromSchool(id, schoolID);
    }

    @Override
    public boolean updateSchoolTo(int schoolID, Vector schoolTo)
            throws IOException, XmlRpcException, SQLException {
        return getDelegate().updateSchoolTo(schoolID, schoolTo);
    }

    @Override
    public boolean deleteCourseDataFromClass(int courseID, int classID)
            throws SQLException, IOException, XmlRpcException {
        return getDelegate().deleteCourseDataFromClass(courseID, classID);
    }

    @Override
    public Vector getResultCount(int profile, int classId) throws SQLException,
            IOException, XmlRpcException {
        return getDelegate().getResultCount(profile, classId);
    }

    @Override
    public String setRights(int uid, int schoolGroup, int profileid, String rights)
            throws SQLException, IOException, XmlRpcException {
        return getDelegate().setRights(uid, schoolGroup, profileid, rights);
    }

    @Override
    public boolean changeCourse(int id, String name, String description,
            boolean export, int schoolID) throws DwoXmlRpcException,
            IOException, XmlRpcException, SQLException {
        return getDelegate().changeCourse(id, name, description, export, schoolID);
    }

    @Override
    public boolean changeCourse(int id, String name, String description,
            boolean export, int schoolID, int parentID) throws DwoXmlRpcException,
            IOException, XmlRpcException, SQLException {
        return getDelegate().changeCourse(id, name, description, export, schoolID, parentID);
    }

    @Override
    public int addCourse(int schoolID, String name, String description,
            int dwoProfile, int parentID, boolean withChildren)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().addCourse(schoolID, name, description, dwoProfile, parentID, withChildren);
    }

    @Override
    public boolean setCourseSequence(Vector vector, int schoolID, int classID,
            int parent, int profileID) throws DwoXmlRpcException, IOException,
            XmlRpcException, SQLException {
        return getDelegate().setCourseSequence(vector, schoolID, classID, parent, profileID);
    }

    @Override
    public boolean moveSco(int scoId, int courseId, int sequencenr, String name)
            throws DwoXmlRpcException, IOException, XmlRpcException,
            SQLException {
        return getDelegate().moveSco(scoId, courseId, sequencenr, name);
    }

    @Override
    public boolean selectCoursesForClass(int classID, int courseID, int type,
            Date van, Date tot) throws IOException, XmlRpcException,
            SQLException {
        return getDelegate().selectCoursesForClass(classID, courseID, type, van, tot);
    }

    @Override
    public boolean selectCoursesForClass(int id, Vector v) throws IOException,
            XmlRpcException, SQLException {
        return getDelegate().selectCoursesForClass(id, v);
    }

    @Override
    public boolean setExpireDate(int schoolID, Date date) throws IOException,
            XmlRpcException, SQLException {
        // TODO Auto-generated method stub
        return getDelegate().setExpireDate(schoolID, date);
    }

    @Override
    public Hashtable login_saml(String a, String b) throws DwoXmlRpcException,
            IOException, XmlRpcException, SQLException {
        return getDelegate().login_saml(a, b);

    }

    @Override
    public boolean link_saml(String userid, String orgid, int id)
            throws IOException, XmlRpcException, SQLException {
        return getDelegate().link_saml(userid, orgid, id);
    }

    @Override
    public Vector getToSchoolsFrom(int schoolID) throws IOException, XmlRpcException, SQLException {
        return getDelegate().getToSchoolsFrom(schoolID);
    }

    @Override
    public boolean isInTeacherRole(int userID, int schoolID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
        return getDelegate().isInTeacherRole(userID, schoolID);
    }

    @Override
    public Vector<Object> getStudentsOfClass(int schoolClassID) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        return getDelegate().getStudentsOfClass(schoolClassID);
    }

    @Override
    public Vector<Object> getTeachersOfClass(int schoolClassID) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        return getDelegate().getTeachersOfClass(schoolClassID);
    }

    @Override
    public boolean renameClass(int classID, String newName, String newRegistrationKey, boolean iconizer) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        return getDelegate().renameClass(classID, newName, newRegistrationKey, iconizer);
    }

    @Override
    public boolean disconnectFromClass(int userID, int classID) throws IOException, XmlRpcException, SQLException {
        return getDelegate().disconnectFromClass(userID, classID);
    }

    @Override
    public Vector<Object> getClassesOfTeacher(int userID, int schoolID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
        return getDelegate().getClassesOfTeacher(userID, schoolID);
    }

    @Override
    public boolean isInStudentRole(int userID, int schoolID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
        return getDelegate().isInStudentRole(userID, schoolID);
    }
    
    @Override
    public Vector<Object> getClassesOfStudent(int userID, int schoolID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
        return getDelegate().getClassesOfStudent(userID,schoolID);
    }

    @Override
    public boolean removeStudentFromClass(int classID, int studentID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
    return getDelegate().removeStudentFromClass(classID, studentID);
    }
    
    @Override
    public boolean addStudentToClass(int classID, int studentID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
        return getDelegate().removeStudentFromClass(classID, studentID);
    }
    
    @Override
    public boolean removeTeacherFromClass(int classID, int teacherID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
        return getDelegate().removeTeacherFromClass(classID, teacherID);
    }

    @Override
    public boolean addTeacherToClass(int classID, int teacherID) throws IOException, SQLException, XmlRpcException, DwoXmlRpcException {
        return getDelegate().addTeacherToClass(classID, teacherID);
    }

	@Override
	public boolean Commit(int userID, int schoolGroupID, int scoID,
			Hashtable map) throws Exception {
		return getScormAccess().Commit(userID, schoolGroupID, scoID, map);
	}
// TODO Need a super interface: DBConnectIF, DBAccessIF, ScormAccessIF
	private ScormAccessIF getScormAccess() {
		return (ScormAccessIF) getDelegate();
	}

	@Override
	public Hashtable Initialize(int userID, int schoolGroupID, int scoID)
			throws Exception {
		return getScormAccess().Initialize(userID, schoolGroupID, scoID);
	}

	@Override
	public Hashtable Initialize(int userID, int schoolGroupID, int scoID,
			Vector keys) throws Exception {
		return getScormAccess().Initialize(userID, schoolGroupID, scoID, keys);
	}
    
    private DbAccessJS getDbAccessJS() {
    	return (DbAccessJS) getDelegate();
    }

	@Override
	public Vector getCoursesJS(int profileValue) throws IOException,
			XmlRpcException, SQLException {
		return getDbAccessJS().getCoursesJS(profileValue);
	}

	@Override
	public Vector getCoursesForClassJS(int classID) throws IOException,
			XmlRpcException, SQLException {
		return getDbAccessJS().getCoursesForClassJS(classID);
	}

	@Override
	public Vector getEditableCoursesJS(int schoolID) throws IOException,
			XmlRpcException, SQLException {
		return getDbAccessJS().getEditableCoursesJS(schoolID);
	}

	@Override
	public Vector getEditableCoursesAdminJS() throws IOException,
			XmlRpcException, SQLException {
		return getDbAccessJS().getEditableCoursesAdminJS();
	}

	@Override
	public Vector getTableJS(String table, Hashtable wheredef, String orderby)
			throws IOException, XmlRpcException, SQLException {
		return getDbAccessJS().getTableJS(table, wheredef, orderby);
	}
    
}