package fi.servlet.dwomaccess;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.persistence.DbAccessIF;
import fi.dwo.server.persistence.DwoXmlRpcException;

public class DbAccessWrapper implements DbAccessIF {
	
	static class CourseSorter<T extends Map<?,?>> implements Comparator<T> {

		private Map<Object, Integer> ranking;
		
		CourseSorter(List<Map<String,Object>> sequences) {
			ranking = new Hashtable<Object, Integer>();
			for (Iterator<Map<String, Object>> iterator = sequences.iterator(); iterator.hasNext();) {
				Map<String, Object> map = iterator.next();
				Object id = map.get("courseID");
				Number n  = (Number) map.get("sequencenr");
				ranking.put(id, n.intValue());
			}
		}

		@Override
		public int compare(T o1, T o2) {
			Object i1 = o1.get("courseID");
			Object i2 = o1.get("courseID");
			Integer r1 = ranking.get(i1);
			Integer r2 = ranking.get(i2);
			if(r1 == null && r2 == null) 
			{
				Comparable n1 = (Comparable) o1.get("name");
				Object n2 = o2.get("name");			
				return n1.compareTo(n2); // fall back: by name.
			}
			if(r1 == null) return +1;
			if(r2 == null) return -1;
			return r1.compareTo(r2);
		}
	}

	private DbAccessIF delegate;

	/**
	 * fix course ordering for this delegate.
	 * @param delegate
	 */
	DbAccessWrapper(DbAccessIF delegate) {
		this.delegate = delegate;
	}

	public String LMSGetValue(int arg0, int arg1, String arg2)
			throws IOException, XmlRpcException, SQLException {
		return delegate.LMSGetValue(arg0, arg1, arg2);
	}

	public String LMSSetValue(int arg0, int arg1, String arg2, String arg3,
			String arg4) throws IOException, XmlRpcException, SQLException {
		return delegate.LMSSetValue(arg0, arg1, arg2, arg3, arg4);
	}

	public String LMSSetValue(int arg0, int arg1, String arg2, String arg3)
			throws IOException, XmlRpcException, SQLException {
		return delegate.LMSSetValue(arg0, arg1, arg2, arg3);
	}

	public Hashtable addClass(int arg0, String arg1) throws DwoXmlRpcException,
			IOException, XmlRpcException, SQLException {
		return delegate.addClass(arg0, arg1);
	}

	public int addCourse(int arg0, String arg1, String arg2, int arg3,
			int arg4, boolean arg5) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return delegate.addCourse(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public int addCourse(int arg0, String arg1, String arg2, int arg3)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.addCourse(arg0, arg1, arg2, arg3);
	}

	public Hashtable addSchool(int arg0, String arg1, String arg2,
			Hashtable arg3) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException, DwoXmlRpcException {
		return delegate.addSchool(arg0, arg1, arg2, arg3);
	}

	public Hashtable addSchool(int arg0, String arg1, String arg2, String arg3,
			String arg4) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return delegate.addSchool(arg0, arg1, arg2, arg3, arg4);
	}

	public Hashtable addSchool(String arg0, String arg1, String arg2,
			String arg3) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return delegate.addSchool(arg0, arg1, arg2, arg3);
	}

	public int addSco(int arg0, String arg1, String arg2, int arg3, int arg4,
			boolean arg5) throws IOException, XmlRpcException, SQLException,
			DwoXmlRpcException {
		return delegate.addSco(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public int addSco(int arg0, String arg1, String arg2, int arg3, int arg4)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.addSco(arg0, arg1, arg2, arg3, arg4);
	}

	public int addSco(int arg0, String arg1, String arg2, int arg3,
			String arg4, int arg5) throws IOException, XmlRpcException,
			SQLException, DwoXmlRpcException {
		return delegate.addSco(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public Hashtable addToSchool(int arg0, String arg1, int arg2, String arg3)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.addToSchool(arg0, arg1, arg2, arg3);
	}

	public boolean changeAccount(int arg0, String arg1, String arg2,
			String arg3, String arg4, String arg5, String arg6, int arg7)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.changeAccount(arg0, arg1, arg2, arg3, arg4, arg5, arg6,
				arg7);
	}

	public boolean changeAccount(int arg0, String arg1, String arg2,
			String arg3, String arg4, String arg5, String arg6)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.changeAccount(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}

	public boolean changeCourse(int arg0, String arg1, String arg2,
			boolean arg3, int arg4, int arg5) throws DwoXmlRpcException,
			IOException, XmlRpcException, SQLException {
		return delegate.changeCourse(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public boolean changeCourse(int arg0, String arg1, String arg2,
			boolean arg3, int arg4) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return delegate.changeCourse(arg0, arg1, arg2, arg3, arg4);
	}

	public boolean changeCourse(int arg0, String arg1, String arg2, boolean arg3)
			throws IOException, XmlRpcException, SQLException,
			DwoXmlRpcException {
		return delegate.changeCourse(arg0, arg1, arg2, arg3);
	}

	public boolean changeCourse(int arg0, String arg1, String arg2)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.changeCourse(arg0, arg1, arg2);
	}

	public boolean changeSco(int arg0, String arg1, String arg2, boolean arg3,
			byte[] arg4, boolean arg5) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return delegate.changeSco(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public boolean changeSco(int arg0, String arg1, String arg2, boolean arg3,
			String arg4) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return delegate.changeSco(arg0, arg1, arg2, arg3, arg4);
	}

	public boolean changeSco(int arg0, String arg1, String arg2, boolean arg3)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.changeSco(arg0, arg1, arg2, arg3);
	}

	public boolean changeSco(int arg0, String arg1, String arg2, String arg3,
			boolean arg4) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return delegate.changeSco(arg0, arg1, arg2, arg3, arg4);
	}

	public boolean changeSco(int arg0, String arg1, String arg2, String arg3)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.changeSco(arg0, arg1, arg2, arg3);
	}

	public boolean changeSco(int arg0, String arg1, String arg2)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.changeSco(arg0, arg1, arg2);
	}

	public boolean changeScoSequenceNr(int arg0, int arg1, int arg2, int arg3)
			throws SQLException, DwoXmlRpcException, IOException,
			XmlRpcException {
		return delegate.changeScoSequenceNr(arg0, arg1, arg2, arg3);
	}

	public boolean deSelectCoursesForClass(int arg0, int arg1)
			throws IOException, XmlRpcException, SQLException {
		return delegate.deSelectCoursesForClass(arg0, arg1);
	}

	public boolean deleteClass(int arg0, boolean arg1) throws IOException,
			XmlRpcException, SQLException {
		return delegate.deleteClass(arg0, arg1);
	}

	public boolean deleteCourse(int arg0) throws DwoXmlRpcException,
			IOException, XmlRpcException, SQLException {
		return delegate.deleteCourse(arg0);
	}

	public boolean deleteCourseDataFromClass(int arg0, int arg1)
			throws SQLException, IOException, XmlRpcException {
		return delegate.deleteCourseDataFromClass(arg0, arg1);
	}

	public boolean deleteSchool(int arg0) throws IOException, XmlRpcException,
			SQLException {
		return delegate.deleteSchool(arg0);
	}

	public boolean deleteSco(int arg0) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return delegate.deleteSco(arg0);
	}

	public boolean deleteUser(int arg0) throws IOException, XmlRpcException,
			SQLException {
		return delegate.deleteUser(arg0);
	}

	public boolean deleteUserFromSchool(int arg0, int arg1) throws IOException,
			XmlRpcException, SQLException {
		return delegate.deleteUserFromSchool(arg0, arg1);
	}

	public boolean disconnectFromClass(int arg0) throws IOException,
			XmlRpcException, SQLException {
		return delegate.disconnectFromClass(arg0);
	}

	public boolean editSchool(int arg0, boolean arg1) throws IOException,
			XmlRpcException, SQLException {
		return delegate.editSchool(arg0, arg1);
	}

	public Hashtable editSchool(int arg0, String arg1, String arg2,
			Hashtable arg3) throws IOException, XmlRpcException, SQLException,
			DwoXmlRpcException {
		return delegate.editSchool(arg0, arg1, arg2, arg3);
	}

	public Hashtable editSchool(int arg0, String arg1, String arg2,
			String arg3, String arg4) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return delegate.editSchool(arg0, arg1, arg2, arg3, arg4);
	}

	public boolean editSchoolRights(int arg0, String arg1) throws IOException,
			SQLException, XmlRpcException {
		return delegate.editSchoolRights(arg0, arg1);
	}

	public Vector getCourses(int arg0) throws IOException, XmlRpcException,
			SQLException {
		return fixSequence(delegate.getCourses(arg0));
	}

	public Vector getCoursesForClass(int arg0) throws IOException,
			XmlRpcException, SQLException {
		return fixSequence(delegate.getCoursesForClass(arg0));
	}

	public Vector getEditableCourses(int arg0) throws IOException,
			XmlRpcException, SQLException {
		return fixSequence(delegate.getEditableCourses(arg0));
	}

	public Vector getEditableCoursesAdmin() throws IOException,
			XmlRpcException, SQLException {
		return fixSequence(delegate.getEditableCoursesAdmin());
	}

	public Hashtable getFidentitySchools() throws IOException, XmlRpcException,
			SQLException, DwoXmlRpcException {
		return delegate.getFidentitySchools();
	}

	public Vector getImportCourses(int arg0, int arg1, int arg2)
			throws IOException, XmlRpcException, SQLException {
		return delegate.getImportCourses(arg0, arg1, arg2);
	}

	public Hashtable getRecord(String arg0, String arg1, int arg2)
			throws IOException, XmlRpcException, SQLException {
		return delegate.getRecord(arg0, arg1, arg2);
	}

	public Vector getResultCount(int arg0, int arg1) throws SQLException,
			IOException, XmlRpcException {
		return delegate.getResultCount(arg0, arg1);
	}

	public Vector getResults(int arg0, int arg1, int arg2) throws IOException,
			XmlRpcException, SQLException {
		return delegate.getResults(arg0, arg1, arg2);
	}

	public Vector getResults(int arg0, int arg1) throws IOException,
			XmlRpcException, SQLException {
		return delegate.getResults(arg0, arg1);
	}

	public Vector getResults(Vector arg0, int arg1, int arg2)
			throws IOException, XmlRpcException, SQLException {
		return delegate.getResults(arg0, arg1, arg2);
	}

	public Vector getResults(Vector arg0, int arg1) throws IOException,
			XmlRpcException, SQLException {
		return delegate.getResults(arg0, arg1);
	}

	public Vector getTable(String table, Hashtable wheredef, String orderby)
			throws IOException, XmlRpcException, SQLException {
		if("tblCourse".equals(table) && "name".equals(orderby))
		{
			return fixSequence(getTable(table,wheredef));
		}
		return delegate.getTable(table, wheredef, orderby);
	}

	Vector fixSequence(Vector unordered) {
		List sequences = getSequences(unordered);
		Comparator sorter = new CourseSorter(sequences);
		Collections.sort(unordered, sorter);
		return unordered; // not any more....
	}

	private List<Map<String,Object>> getSequences(Vector<Map<String,Object>> unordered) {
		if(unordered.isEmpty())
			return unordered;
		Map<String,Object> first = unordered.firstElement();
		Hashtable<String,Object> wheredef = new Hashtable<String,Object>();
		Object schoolID = first.get("schoolID"); 
		wheredef.put("classID", 0);
		if(schoolID == null) schoolID = Integer.valueOf(0);
		wheredef.put("schoolID", schoolID);
		wheredef.put("profileID", first.get("profileID"));
		String method = "getTable";
		try {
			return getTable( "tblCourseSequence", wheredef, "sequencenr" );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList(); // unsorted.
	}

	public Vector getTable(String arg0, Hashtable arg1) throws IOException,
			XmlRpcException, SQLException {
		return delegate.getTable(arg0, arg1);
	}

	public Vector getTable(String arg0, String arg1) throws IOException,
			XmlRpcException, SQLException {
		return delegate.getTable(arg0, arg1);
	}

	public Vector getTable(String arg0, Vector arg1, Hashtable arg2, String arg3)
			throws IOException, XmlRpcException, SQLException {
		return delegate.getTable(arg0, arg1, arg2, arg3);
	}

	public Vector getTable(String arg0) throws IOException, XmlRpcException,
			SQLException {
		return delegate.getTable(arg0);
	}

	public Vector getUserResults(int arg0, int arg1) throws IOException,
			XmlRpcException, SQLException {
		return delegate.getUserResults(arg0, arg1);
	}

	public Vector getUserResults(Vector arg0, int arg1) throws SQLException,
			IOException, XmlRpcException {
		return delegate.getUserResults(arg0, arg1);
	}

	public boolean link_saml(String arg0, String arg1, int arg2)
			throws IOException, XmlRpcException, SQLException {
		return delegate.link_saml(arg0, arg1, arg2);
	}

	public boolean log(String arg0) throws IOException, XmlRpcException {
		return delegate.log(arg0);
	}

	public Hashtable login(String arg0, String arg1) throws DwoXmlRpcException,
			IOException, XmlRpcException, SQLException {
		return delegate.login(arg0, arg1);
	}

	public Hashtable login_saml(String arg0, String arg1)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.login_saml(arg0, arg1);
	}

	public boolean moveSco(int arg0, int arg1, int arg2, String arg3)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.moveSco(arg0, arg1, arg2, arg3);
	}

	public boolean reassignClass(int arg0, int arg1) throws IOException,
			SQLException, XmlRpcException, DwoXmlRpcException {
		return delegate.reassignClass(arg0, arg1);
	}

	public boolean reconnect() throws IOException, XmlRpcException,
			SQLException {
		return delegate.reconnect();
	}

	public boolean register(String arg0, String arg1, String arg2, String arg3,
			String arg4, String arg5, String arg6, int arg7, String arg8)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.register(arg0, arg1, arg2, arg3, arg4, arg5, arg6,
				arg7, arg8);
	}

	public boolean register(String arg0, String arg1, String arg2, String arg3,
			String arg4, String arg5) throws DwoXmlRpcException, IOException,
			XmlRpcException, SQLException {
		return delegate.register(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public boolean renameClass(int arg0, String arg1, boolean arg2)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.renameClass(arg0, arg1, arg2);
	}

	public boolean renameClass(int arg0, String arg1)
			throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.renameClass(arg0, arg1);
	}

	public boolean selectCoursesForClass(int arg0, int arg1, int arg2,
			Date arg3, Date arg4) throws IOException, XmlRpcException,
			SQLException {
		return delegate.selectCoursesForClass(arg0, arg1, arg2, arg3, arg4);
	}

	public boolean selectCoursesForClass(int arg0, int arg1)
			throws IOException, XmlRpcException, SQLException {
		return delegate.selectCoursesForClass(arg0, arg1);
	}

	public boolean selectCoursesForClass(int arg0, Vector arg1)
			throws IOException, XmlRpcException, SQLException {
		return delegate.selectCoursesForClass(arg0, arg1);
	}

	public boolean selectJar(String arg0, String arg1) throws IOException,
			XmlRpcException, SQLException {
		return delegate.selectJar(arg0, arg1);
	}

	public boolean setCourseSequence(Vector arg0, int arg1, int arg2, int arg3,
			int arg4) throws DwoXmlRpcException, IOException, XmlRpcException,
			SQLException {
		return delegate.setCourseSequence(arg0, arg1, arg2, arg3, arg4);
	}

	public boolean setExpireDate(int arg0, Date arg1) throws IOException,
			XmlRpcException, SQLException {
		return delegate.setExpireDate(arg0, arg1);
	}

	public boolean setLogo(int arg0, byte[] arg1) throws SQLException,
			IOException, XmlRpcException {
		return delegate.setLogo(arg0, arg1);
	}

	public String setRights(int arg0, int arg1, String arg2)
			throws SQLException, IOException, XmlRpcException {
		return delegate.setRights(arg0, arg1, arg2);
	}

	public boolean updateSchoolTo(int arg0, Vector arg1) throws IOException,
			XmlRpcException, SQLException {
		return delegate.updateSchoolTo(arg0, arg1);
	}
	
	
}
