package fi.dwo.server.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.commons.exceptions.DwoXmlRpcException;

public interface DbAccessObsolete {
  public Vector getImportCourses(int schoolFrom, int schoolTo, int profileID) throws IOException, XmlRpcException, SQLException;
  public Vector getCoursesJS(int profileValue) throws IOException, XmlRpcException,
  SQLException;
  public String LMSGetValue(int scoID, int userID, int schoolGroupID, String iDataModelElement) throws IOException, XmlRpcException,
  SQLException;
  public String LMSSetValue(int scoID, int userID, int schoolGroupID, String iDataModelElement, String iValue, String random) throws IOException, XmlRpcException,
  SQLException;
  public Vector getUserResults(int courseID, int userID, int schoolGroupID) throws IOException, XmlRpcException, SQLException;
  public Vector getCoursesForClass(int classID) throws IOException, XmlRpcException, SQLException;
  public Vector getTable(String tableName, String orderCol) throws IOException,
  XmlRpcException, SQLException;
  public Vector getResultCount(int profile, int classId)
      throws SQLException, IOException, XmlRpcException;
  public boolean log(String s) throws IOException, XmlRpcException;
 /**
   * Returns all the available courses for the specified user.
   *
   * @param userID The user where for the courses must selected.
   * @return A Vector containing hash tables with the course data.
   * @throws java.sql.SQLException
   */
  public Vector getCourses(int profileValue) throws IOException, XmlRpcException, SQLException;
  public Vector getToSchoolsFrom(int schoolID) throws IOException, XmlRpcException, SQLException;

/*  Hashtable login(String username, String password)
      throws SQLException, DwoXmlRpcException, IOException, XmlRpcException;

  Vector<Object> getClassesOfTeacher(int userID, int schoolID)
      throws IOException, SQLException, XmlRpcException, DwoXmlRpcException;

  boolean changeCourse(int courseID, String name, String description, boolean export, int schoolID)
      throws DwoXmlRpcException, SQLException, IOException, XmlRpcException;

  boolean changeCourse(int courseID, String name, String description, boolean export, int schoolID,
      int parentID) throws DwoXmlRpcException, SQLException, IOException, XmlRpcException;

  public boolean changeSco(int scoID, String name, String description, String launchdata)
      throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;

  public boolean changeSco(int scoID, String name, String description)
      throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;

  public boolean moveSco(int scoId, int courseId, int sequencenr, String name)
      throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;
  public boolean changeScoSequenceNr(int scoID, int sequencenr, int scoID2, int sequencenr2) throws SQLException, DwoXmlRpcException, IOException, XmlRpcException;
  public boolean changeSco(int scoID, String name, String description, boolean showScore) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;

  public boolean changeSco(int id, String scoName, String description,
          String launchdataString, boolean showScore) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;

  public boolean changeSco(int scoID, String name, String description, boolean delete, String launchdata)
          throws DwoXmlRpcException, IOException, XmlRpcException,
          SQLException;
  public boolean changeSco(int scoid, String scoName, String description,
                           boolean delete, byte[] launchdata, boolean showScore)
                           throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;
  public boolean deleteSco(int scoID) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;
  public boolean setLogo(int id, byte[] image) throws SQLException, IOException, XmlRpcException;
  public boolean deleteCourse(int courseID) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;
  public Vector getTableJS(String tableName, Hashtable wheredef, String orderBy) throws IOException,
  XmlRpcException, SQLException;
public Vector getTable(String tableName) throws IOException,
XmlRpcException, SQLException;
public Vector getTable(String tableName, Hashtable wheredef) throws IOException,
XmlRpcException, SQLException;
*///-------------------------------------
public boolean setCourseSequence(Vector vector, int schoolID, int classID,
                                 int parent, int profileID)
                                 throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;

public int addCourse(int schoolID, String name, String description, int dwoProfile, int parentID, boolean withChildren) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;

public Hashtable addSchool(int schoolId, String schoolName, String schoolLogin, Hashtable passwd)
    throws DwoXmlRpcException, IOException, XmlRpcException, SQLException, DwoXmlRpcException;
public Hashtable editSchool(int schoolID, String schoolName, String schoolLogin, Hashtable passwd)
    throws IOException, XmlRpcException, SQLException, DwoXmlRpcException;
public boolean deleteSchool(int schoolID) throws IOException, XmlRpcException, SQLException;
public boolean setExpireDate(int schoolID, Date date) throws IOException, XmlRpcException, SQLException;
public boolean editSchoolRights(int schoolID, String rights) throws IOException, SQLException, XmlRpcException;
public boolean editSchool(int schoolID, boolean export) throws IOException, XmlRpcException, SQLException;
public boolean deleteCourseDataFromClass(int courseID, int classID)
    throws SQLException, IOException, XmlRpcException;
public boolean selectCoursesForClass(int id, Vector v) throws IOException, XmlRpcException, SQLException;
public boolean updateSchoolTo(int schoolID, Vector schoolTo)
    throws IOException, XmlRpcException, SQLException;

}
