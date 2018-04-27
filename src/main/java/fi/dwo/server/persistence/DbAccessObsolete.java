package fi.dwo.server.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.commons.exceptions.DwoXmlRpcException;

public interface DbAccessObsolete {

  Hashtable login(String username, String password)
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

}
