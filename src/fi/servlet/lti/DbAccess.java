package fi.servlet.lti;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.dwo.client.domain.Group;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.SchoolGroup;
import fi.dwo.client.domain.User;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.DbAccessIF;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.LoginException;
import fi.dwo.client.system.PersistenceException;

public class DbAccess {
	
	/**
	 * @param dbaccess
	 */
	public DbAccess(DbAccessIF dbaccess) {
		this.dbaccess = dbaccess;
		DbAccessCreator.setInstance(dbaccess);
	}

	public DbAccess() {
		dbaccess = new fi.dwo.server.persistence.DbAccess();
		DbAccessCreator.setInstance(dbaccess);
	}
	
	DbAccessIF dbaccess;
	
    private static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
    private static final String DWO_SAML_ORGANIZATION = "dwoSAMLOrganization";    
	private static final String DWO_SAML_USER_ID = "dwoSAMLUserID";
	
	public void setCookie(HttpServletRequest request, HttpServletResponse response) {
// persoonsgegevens:
		String user_id = request.getParameter("custom_userid");
		String lti_id = request.getParameter("user_id");
		if(user_id == null || user_id.length() == 0) {
			user_id = lti_id;
		}
		//String context_id = request.getParameter("context_id");
	    String oauth_consumer_key = request.getParameter("oauth_consumer_key");
	    String context_label = request.getParameter("context_label");
	    
		//String name_full = request.getParameter("lis_person_name_full");
		String name_given = request.getParameter("lis_person_name_given");
		String name_family = request.getParameter("lis_person_name_family");
		String name_prefix = "";
		int komma = name_family.indexOf(',');
		if(komma > 0) {
			name_prefix = name_family.substring(komma+1);
			name_family = name_family.substring(0,komma);
		}
		String email = request.getParameter("lis_person_contact_email_primary");
	    String organization = oauth_consumer_key;
	    
	    organization = request.getParameter("tool_consumer_instance_description");
	    if(organization == null)organization = request.getParameter("context_title");
	    if(organization == null)organization = context_label;
	    if(organization == null)organization = request.getParameter("resource_link_title");
	    if(organization == null)organization = oauth_consumer_key;
	    
		Cookie user  = new Cookie(DWO_SAML_USER_ID, lti_id);
		Cookie orgid = new Cookie(DWO_SAML_ORGANIZATION_ID, "lti:" + oauth_consumer_key);
		Cookie org   = new Cookie(DWO_SAML_ORGANIZATION, organization);
		response.addCookie(user);
		response.addCookie(orgid);
		response.addCookie(org);
		final PersistenceFacade facade = PersistenceFacade.instance();
		Group group = getGroup(request.getParameter("roles"));
		User u = null;
		try {
			u = facade.loginViaSAML(lti_id, orgid.getValue());
		} catch (LoginException e) {
			String schoolLogin = getRealm(oauth_consumer_key);
			String username = user_id + "@" + schoolLogin;
			String groupPassword = getSecret(oauth_consumer_key, group.getGroupID());
			try {
				facade.register(username, "", name_given, name_prefix, name_family, email, schoolLogin, group, groupPassword);
				u = facade.login(username);
				facade.linkViaSAML(u, lti_id, orgid.getValue());
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}
		} 
		if(group.getGroupID() == SchoolGroup.STUDENT)
		{
			try {
				SchoolClass c = getSchoolClass(facade, oauth_consumer_key, context_label);
				if(c != null)
					facade.changeAccount(u, "", "", name_given, name_prefix, name_family, email, c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * @param facade
	 * @param oauth_consumer_key
	 * @param context_label
	 * @return
	 * @throws PersistenceException
	 */
	protected SchoolClass getSchoolClass(final PersistenceFacade facade,
			String oauth_consumer_key, String context_label)
			throws PersistenceException {
		if(context_label != null) {
	 		School s = getSchool(facade, oauth_consumer_key);
			Object[] array = facade.get(SchoolClass.class, s);
			SchoolClass c;
			for (int i = 0; i < array.length; i++) {
				c = (SchoolClass) array[i];
				if(c.getName().equals(context_label))
					return c;
			}
		}
		return null;
	}
	
	private Group getGroup(String parameter) {
		int id = SchoolGroup.STUDENT;
		if(parameter != null && parameter.toLowerCase().contains("instructor"))
			id = SchoolGroup.TEACHER;
		Group g = new Group();
		g.setGroupID(id);
		g.setName(parameter);
		return g;
	}

	public String getSecret(String key) {
		return getSecret(key, SchoolGroup.STUDENT);
	}

	public String getSecret(String key, int gid) {
		PersistenceFacade instance = PersistenceFacade.instance();
		try {
			School result = getSchool(instance, key);
			return result.getPasswd(gid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param instance
	 * @param key
	 * @return
	 * @throws PersistenceException
	 */
	protected School getSchool(PersistenceFacade instance, String key)
			throws PersistenceException {
		int id = Integer.parseInt(key);
		School result = (School)instance.get(id, School.class);
		return result;
	}
	
	public String getRealm(String key) {
		PersistenceFacade instance = PersistenceFacade.instance();
		try {
			School result = getSchool(instance, key);
			return result.getSchoolLogin();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
	}
	
	private static final String SCO = "/sco/";
	private static final String COURSE = "/course/";
	public String getDeepLink(String info) {
		if(info.startsWith(SCO))
		{
			String sco = info.substring(SCO.length());
			return "<param name='scoViewNr' value='" + sco + "'>";
		}
		if(info.startsWith(COURSE))
		{
			String sco = info.substring(COURSE.length());
			return "<param name='courseViewNr' value='" + sco + "'>";
		}
		return "";
	}
	
}
