/*
 * Created on Nov 20, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package fi.dwo.server.persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;

import fi.beans.base64code.Base64StringEncoder;
import fi.beans.fidentity.FidentityManager;
import fi.dwo.client.system.LoginException;

public class DbAccessLdap extends DbAccess
{
    FidentityManager manager; 
    
    /** Verander account en klas gegevens.
     * Als het password "" is, wordt alleen de klas veranderd.
     * De klas mag worden verandert als het DWO password klopt of als het LDAP password klopt.
     * @param password het wachtwoord als MD5 string of ""
     * @param userID nummer van de gebruiker in de dwo database.
     * @see fi.dwo.server.persistence.DbAccess#changeAccount(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
     */
    public boolean changeAccount(int userID, String password, String newPassword, String firstname, String middlename, String lastname, String email, int classID) throws DwoXmlRpcException, SQLException
    {
        String user = getUser(userID);
        if( "".equals(password) || passwordCorrect(userID, password) || (user != null && manager.verifyMD5(user, password)) )
        {
            PreparedStatement ps = getStatement(QRY_UPDATE_USER_CLASS);
            ps.setInt(1, classID);
            ps.setInt(2, userID);

            ps.execute();
            ps.close();

        }
        if("".equals(password))
        {
        	return true;
        }
         return changeAccount(userID, password, newPassword, firstname,
                middlename, lastname, email);
    }

    /* (non-Javadoc)
     * @see fi.dwo.server.persistence.DbAccess#changeAccount(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean changeAccount(int userID, String password, String newPassword, String firstname, String middlename, String lastname, String email) throws DwoXmlRpcException, SQLException
    {        
        String user = getUser(userID);
        if( user != null && manager.verifyMD5(user, password) )
        {   manager.changeAccount(user, firstname, middlename, lastname, email);
// not null, not empty and changed
        if(newPassword != null && !"".equals(newPassword) && !newPassword.equals(password))
                manager.changeMD5Password(user, newPassword);
            PreparedStatement ps = getStatement(QRY_UPDATE_USER_NO_PWD);
            ps.setString(1, firstname);
            ps.setString(2, middlename);
            ps.setString(3, lastname);
            ps.setString(4, email);
            ps.setInt(5, userID);
            ps.execute();
            ps.close();
// update DWO password if LDAP password correct and old DWO password not empty.
            
            if(newPassword != null && !"".equals( newPassword) && !"".equals(passwd))
            {
            	ps = getStatement(QRY_UPDATE_PWD);
            	ps.setString(1, newPassword);
            	ps.setInt(2,userID);
            	ps.execute();
            	ps.close();
            }
            return true;
        } 
        return super.changeAccount(userID, password, newPassword, firstname,
                middlename, lastname, email);
    }

    final static private String SELECT_USERNAME_FROM_USERID = "select username, passwd from tblUser where userID=?";
    private final static String QRY_UPDATE_USER_NO_PWD = "UPDATE tblUser "
        + "SET firstname = ?, " + "middlename = ?, " + "lastname = ?, "
        + "email = ? " + "WHERE (userID = ?)";
    private final static String QRY_UPDATE_USER_CLASS = "UPDATE tblUser "
        + "SET classID = ? " + "WHERE (userID = ?) ";
    private final static String QRY_UPDATE_PWD = "UPDATE tblUser "
        + "SET passwd = ? WHERE (userID = ?)";
	static final int DIGICODE = 4;

	private static final String GET_SCHOOLGROUPID = "SELECT schoolGroupID FROM tblSchoolGroup WHERE groupID=? AND schoolID=?";
	private static final int TEACHER = 2;
	
    /* (non-Javadoc)
	 * @see fi.dwo.server.persistence.DbAccess#addToSchool(int, java.lang.String, int, java.lang.String)
	 */
	public Hashtable addToSchool(int userID, String schoolLogin, int groupID,
			String groupPassword) throws DwoXmlRpcException, SQLException {
		if(groupID == DIGICODE)
		{
			String uid = getUser(userID);
			manager.cashDigicode(uid, schoolLogin);
			Map m = manager.getAccount(uid);
			Iterator keys = m.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				if(key.startsWith("DL_FIUUNL_K"))
				{
					String value = (String)m.get(key);
					int schoolID = Integer.parseInt(value.substring(1));
					char ch = value.charAt(0);
					groupID = 1; // leerling
					if(ch == 'D' || ch == 'C') groupID=2; // (contact-)docent
					PreparedStatement ps = getStatement(GET_SCHOOLGROUPID);
					ps.setInt(1, groupID);
					ps.setInt(2, schoolID);
					int schoolGroupID;
					ResultSet rs = ps.executeQuery();
					if(!isEmpty(rs))
						schoolGroupID = rs.getInt(1);
					else 
						break;
					rs.close();
					ps.close();
		            ps = getStatement(QRY_ADD_TO_SCHOOL);
		            ps.setInt(1, schoolGroupID);
		            ps.setInt(2, userID);
		            ps.execute();
		            ps.close();

		            ps = getStatement(QRY_SELECT_SCHOOL_USER);
		            ps.setInt(1, userID);

		            return executeQueryWithRecord(ps);
				}
				
			}
            throw new DwoXmlRpcException(
                    DwoXmlRpcException.EXC_UNKNOWN_SCHOOLGROUP);
			
			
		}
		Hashtable result = super.addToSchool(userID, schoolLogin, groupID, groupPassword);
		Integer schoolId = (Integer) result.get("schoolID");
		String uid = getUser(userID);
		char rolId = 'L';
		if(groupID == TEACHER)
			rolId = 'D';
		manager.cashDigicode(uid, schoolId.intValue(), rolId);
		String errorCode = manager.getLastError();
		if(errorCode != null && errorCode.startsWith("210"))
			throw new DwoXmlRpcException(
                    DwoXmlRpcException.EXC_UNKNOWN_SCHOOLGROUP); 
		
		return result;
	}

	private Object passwd;
    
    String getUser(int userID)
    {
        PreparedStatement ps;
        try
        {
            ps = getStatement(SELECT_USERNAME_FROM_USERID);
            ps.setInt(1, userID);
            Hashtable h = executeQueryWithRecord(ps);
            ps.close();
            if(h == null)
               return null;
            passwd = h.get("passwd"); // HACK....
            return (String) h.get("username");
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 
     */
    public DbAccessLdap()
    {
        manager = new FidentityManager();
    }

	public boolean register(String username, String password, String firstname, String middlename, String lastname, String email, String schoolLogin, int groupID, String groupPassword) throws DwoXmlRpcException, SQLException {
        if (usernameExists(username)) {
            throw new DwoXmlRpcException(DwoXmlRpcException.EXC_USER_EXISTS);
        }
        if(groupID != DIGICODE)
		{ 
        	int schoolGroupId = schoolGroupExists(schoolLogin, groupID, groupPassword);
        	if(schoolGroupId == -1)
        		throw new DwoXmlRpcException(
                    DwoXmlRpcException.EXC_UNKNOWN_SCHOOLGROUP);
        	Hashtable h = getRecord("tblSchoolGroup", "schoolGroupID", schoolGroupId);
        	Object schoolID = h.get("schoolID");
        	if(schoolID instanceof Number && isNoDWOSchool(((Number) schoolID).intValue()))
    			throw new DwoXmlRpcException(
                    DwoXmlRpcException.EXC_UNKNOWN_SCHOOLGROUP);
        		
        	
        	
		}
		registerLDAP(username, password, firstname, middlename, lastname, email);
// groupID = 4: schoollogin is digicode.		
//		if(groupID == DIGICODE)
		{
			boolean result = 
			super.register(username, password, firstname, middlename, lastname, email);
			Hashtable rr = login(username, "");
			int userID = ((Integer)rr.get("userID")).intValue();
// TODO als add to school mislukt, remove user!!!!!
			try {
				addToSchool(userID, schoolLogin, groupID, groupPassword);
			} catch (SQLException e) {
				deleteUser(userID);
				throw e;
			} catch (DwoXmlRpcException e) {
				deleteUser(userID);
				throw e;
			}
			
			return result;
		}
//		
//		
//		boolean result = super.register(username, password, firstname, middlename, lastname,
//				email, schoolLogin, groupID, groupPassword);
//		return result;
	}

	/**
	 * Register een DWO user in de LDAP tree.
	 * Als password leeg is, staat de user al in de LDAP tree en slaan
	 * we registratie over.
	 * FIX: check op userExists
	 * @param username
	 * @param password
	 * @param firstname
	 * @param middlename
	 * @param lastname
	 * @param email
	 * @throws DwoXmlRpcException
	 * @throws SQLException 
	 */
	private void registerLDAP(String username, String password, String firstname, String middlename, String lastname, String email) throws DwoXmlRpcException, SQLException {
		if(!"".equals(password))
		{
			if(manager.getAccount(username) != null || usernameExists(username))
			{
	            throw new DwoXmlRpcException(DwoXmlRpcException.EXC_USER_EXISTS);
			}
			if ( manager.register(username, convertToBase64(password),lastname, email, middlename, firstname)) {
				;
			}
		}
	}
	
	/**
	 * Converteer password van 32char hex MD5 string naar {MD5}base64 string
	 * @param string hex MD5 password
	 * @return base64 password goed voor LDAP
	 */
	private static String convertToBase64(String string) {
		String twodig;
		byte[] bytes = new byte[16];
		for (int i = 0; i < 16; i++) {
			twodig = string.substring(i * 2, i * 2 + 2);
			bytes[i] = (byte) Integer.parseInt(twodig, 16);
		}
		return "{MD5}" + Base64StringEncoder.encode(bytes);
	}

	public boolean register(String username, String password, String firstname, String middlename, String lastname, String email) throws DwoXmlRpcException, SQLException {
        registerLDAP(username, password, firstname, middlename, lastname, email);
		return super.register(username, password, firstname, middlename, lastname,
				email);
	}

	/**
	 * Delete a user from 1) de dwo mysql database 2) de LDAP dwo tree.
	 */
	public boolean deleteUser(int userID) throws SQLException {
		String uid = getUser(userID);
		boolean result = super.deleteUser(userID);
		if(result && uid != null)
			manager.deleteAccount(uid);
		return result;
	}

	/** 
	 * login als DbAccces#login(String, String) maar dan ook voor fidentity users.
	 * Effect moet nog getest worden.
	 * @see fi.dwo.server.persistence.DbAccess#login(java.lang.String, java.lang.String)
	 */
	public Hashtable login(String username, String password) throws SQLException, DwoXmlRpcException {
		updateLogin(username);
		try {
			return restrict(super.login(username, password));
		} catch (DwoXmlRpcException e) {
			if( e.code == LoginException.LE_UNKNOWN_USER &&
				e.getMessage() .equals(  LoginException.class.getName() ))
			{
				if(manager.verifyMD5(username, password)) // TODO wat als het een school account betreft.
					return restrict(super.login(username, ""));
			}
			throw e;
		}
	}

private Hashtable restrict(Hashtable user) {
		if(user == null)
			return null;
		Object o =  user.get("schoolID"); // TODO Constant?
		if(!(o instanceof Number))
			return user;
		Number schoolID = (Number) o;
		int intValue = schoolID.intValue();
		if(intValue <= 1)
			return user;
		
//		if ( isNoDWOSchool(intValue))
//		{
//			user.remove("schoolID");
//			user.remove("groupname");
//			user.put("classID", "");
//		}
		return user;
	}

//TODO 1 call met 3, 7, 1, 8?
private boolean isNoDWOSchool(int intValue) {
	return !manager.isSchoolOK(intValue, 3) && !manager.isSchoolOK(intValue, 1)&& !manager.isSchoolOK(intValue, 7);
}

/**
 * Update de LastLogin bij fidentity.
 * Mag niet mislukken.
 * @param uid
 * @return Map met gebruikersgegevens of null
 */
	private Map updateLogin(String uid)
	{
		try { 
			return manager.getAccount(uid);
		} catch (Throwable t)
		{}
		return null;
	}

/**
 * De scholen in Fidentity die nog niet in de DWO database zitten.
 * Als een school (automatisch) wordt toegevoegd, krijgt die dezelfde SchoolID
 * als in de fidentity database. De koppeling is op nummer.
 * <br/>
 * Noot: Het nummer is een String en niet een Integer
 * @return Hashtable met (schoolID,schoolName).
 */
public Hashtable getFidentitySchools() {
	fi.beans.fidentity.db.DbAccess dba = new fi.beans.fidentity.db.DbAccess();
	Hashtable result = new Hashtable();
	try {
		Vector list = dba.getSchools();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Hashtable element = (Hashtable) iter.next();
			Object schoolName = element.get(fi.beans.fidentity.db.DbAccess.SCHOOLNAME);
			Object schoolId   = element.get(fi.beans.fidentity.db.DbAccess.SCHOOLID).toString();
			result.put((String)schoolId, (String)schoolName);
		}
		ResultSet rs = executeQuery("select schoolId from tblSchool");
		while(rs.next()) {
			result.remove(rs.getObject(1).toString());
		}
		rs.close();
	} catch (SQLException e) {
		log(e.toString());
	}
	return result;
}
}
