package fi.dwo.server.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

public interface DbAccessJS {
	public Vector getCoursesJS(int profileValue) throws IOException, XmlRpcException,
	SQLException;

	//Alleen sorteren binnen één parent.	
	public Vector getCoursesForClassJS(int classID) throws IOException,
		XmlRpcException, SQLException;
	
	public Vector getEditableCoursesJS(int schoolID) throws IOException,
		XmlRpcException, SQLException;
	
	public Vector getEditableCoursesAdminJS() throws IOException,
		XmlRpcException, SQLException;

	public Vector getTableJS(String table, Hashtable wheredef, String orderby)
			throws IOException, XmlRpcException, SQLException;

}
