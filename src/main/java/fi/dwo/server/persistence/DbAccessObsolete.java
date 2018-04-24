package fi.dwo.server.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.commons.exceptions.DwoXmlRpcException;

public interface DbAccessObsolete {

	Hashtable login(String username, String password) throws SQLException, DwoXmlRpcException, IOException, XmlRpcException;

	Vector<Object> getClassesOfTeacher(int userID, int schoolID)
			throws IOException, SQLException, XmlRpcException, DwoXmlRpcException;

	boolean changeCourse(int courseID, String name, String description, boolean export, int schoolID)
			throws DwoXmlRpcException, SQLException, IOException, XmlRpcException;

	boolean changeCourse(int courseID, String name, String description, boolean export, int schoolID, int parentID)
			throws DwoXmlRpcException, SQLException, IOException, XmlRpcException;

  public boolean changeSco(int scoID, String name, String description, String launchdata) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;

   public boolean changeSco(int scoID, String name, String description) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;
}
