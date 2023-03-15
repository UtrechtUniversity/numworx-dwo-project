package fi.servlet.lti;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.uoc.elc.lti.tool.Context;
import edu.uoc.elc.lti.tool.Platform;
import edu.uoc.elc.lti.tool.Tool;
import edu.uoc.elc.lti.tool.User;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SystemManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class DbAccess {
	
    private final static Logger LOG = Logger.getLogger(DbAccess.class.getName());
  
    RestAuthenticator authenticator;
    StoredRestManager restManager;
    SystemManager systemManager;
  
	/**
	 * @param dbaccess
	 */
	public DbAccess() {
	  authenticator = new RestAuthenticator();
	  restManager = new StoredRestManager(authenticator);
	  systemManager = new SystemManager(restManager);
      Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
	}
	
	public DbAccess(ServletContext context) {
		this();
		String dbrest_url = context.getInitParameter("dbrest.url");
		
		if(dbrest_url != null)
			try {
			    authenticator.setServerUrlPath(new URL(dbrest_url));
				rest = new RestHandler(dbrest_url);
				
			} catch (MalformedURLException e) {
				LOG.log(Level.SEVERE, "dbrest.url", e);
			}
	}
	
	RestHandler rest = new RestHandler();
	
    private static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
    private static final String DWO_SAML_ORGANIZATION = "dwoSAMLOrganization";    
	private static final String DWO_SAML_USER_ID = "dwoSAMLUserID";
	private static final String DWO_SAML_AUTH_TOKEN = "dwoSAMLAuthToken";
	
	public boolean setEntreeCookie(HttpServletRequest request, HttpServletResponse response ) {
	   Object user_id = request.getAttribute("uid");
	   Object org_id  = request.getAttribute("nlEduPersonHomeOrganizationId");	   
	   DomSamlUser u = new DomSamlUser();
	   u.setSamlOrgId(s(org_id));
	   u.setSamlUserId(s(user_id));
	   String path = "/";
	   try {
	       u = systemManager.requestSamlToken(u);
	       Cookie user = new Cookie(DWO_SAML_USER_ID, u.getSamlUserId());
	       user.setPath(path);
	       user.setSecure(request.isSecure());
	       Cookie orgid = new Cookie(DWO_SAML_ORGANIZATION_ID, u.getSamlOrgId());
	       orgid.setPath(path);
	       orgid.setSecure(request.isSecure());
	       Cookie authToken = new Cookie(DWO_SAML_AUTH_TOKEN, u.getAuthToken());
	       authToken.setSecure(request.isSecure());
	       authToken.setPath(path);
	       response.addCookie(user);
	       response.addCookie(orgid);
	       response.addCookie(authToken);
	       return false;	  	       
	   } catch(Dwo2Exception e) {
	       LOG.log(Level.WARNING, "request SAML token: " + u.getSamlUserId() + " " + u.getSamlOrgId(), e);
	       try {
	         response.sendRedirect("/dwo/register/Register.jsp?cancel=/&next=" + 
	              URLEncoder.encode(request.getRequestURL().toString(), "UTF-8")
	         );
	       } catch (IOException e1) {
	       }
	       return true;
	   }
	}
	
	static String isoToUtf(Object encoded) {
		if (encoded == null) return null;
		byte[] bytes = encoded.toString().getBytes(StandardCharsets.ISO_8859_1);
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	
	public boolean setUUSAMLCookie(HttpServletRequest request, HttpServletResponse response, String schoolid, String organization) {
	  Object lti_id = request.getAttribute("uid");
// uuspecifiek: pick first, should be: user chooses.
	  String user_id = s(request.getAttribute("studentNumber"));
	  user_id = user_id.split(";")[0];
	  if (user_id.isEmpty())
	    user_id = s(lti_id);

	  Object name_given = isoToUtf(request.getAttribute("givenName"));
	  Object name_family = isoToUtf(request.getAttribute("sn"));
	  Object name_prefix = isoToUtf(request.getAttribute("insertion"));
	  String email = s(request.getAttribute("mail"));
// pick first, multiple valued
	  email = email.split(";")[0];

	  String path = "/";
	  
      Cookie user = new Cookie(DWO_SAML_USER_ID, s(lti_id));
      user.setPath(path);
      user.setSecure(request.isSecure());
      user.setHttpOnly(true);
      user.setMaxAge(30);
      String orgidStr = "saml:" + schoolid;
      Cookie orgid = new Cookie(DWO_SAML_ORGANIZATION_ID, orgidStr);
      orgid.setPath(path);
      orgid.setSecure(request.isSecure());
      orgidStr = "\"" + orgidStr + "\"";
      Cookie org = new Cookie(DWO_SAML_ORGANIZATION, u(organization));
      org.setPath(path);
      org.setSecure(request.isSecure());
      response.addCookie(user); request.setAttribute(DWO_SAML_USER_ID, lti_id);
      response.addCookie(orgid);request.setAttribute(DWO_SAML_ORGANIZATION_ID, orgidStr);
      response.addCookie(org); request.setAttribute(DWO_SAML_ORGANIZATION, organization);
      String roles = s(request.getAttribute("unscoped-affiliation"));
      String role = "STUDENT";
      if(roles != null && roles.toLowerCase().contains("employee"))
          role = "TEACHER";
      String authTokenStr;
      try {
          authTokenStr = rest.registerSAML(
                  s(user_id),
                  s(lti_id),
                  (orgidStr),
                  s(name_given), s(name_prefix), s(name_family),
                  s(email),
                  role,
                  schoolid,
                  ""
                  );
          Cookie authToken = new Cookie(DWO_SAML_AUTH_TOKEN, authTokenStr);
          authToken.setPath(path);
          authToken.setSecure(request.isSecure());
          authToken.setHttpOnly(true);
          authToken.setMaxAge(30);
          response.addCookie(authToken); request.setAttribute(DWO_SAML_AUTH_TOKEN, authTokenStr);
      } catch (IOException e) {
          logger.log(Level.SEVERE, "registerSAML", e);
      }
	  return false;
	}
	
	private String u(String string) {
	  try {
      return URLEncoder.encode(string, "UTF-8").replace("+", "%20");
    } catch (UnsupportedEncodingException e) {
    }
    return string;
  }

  private String s(Object o) {
    if(o == null) return "";
    return String.valueOf(o);
  }

	public boolean setEntreeCookie(Tool tool, HttpServletRequest request, HttpServletResponse response ) {
		User tuser = tool.getUser();
		Platform platform = tool.getPlatform();
		Context context = tool.getContext();
		String user_id = tool.getCustomParameter("userid");
		String lti_id = tuser.getId();
		if (user_id == null) user_id = lti_id;
		String organisation = platform.getName();
		String org_id = "\"lti13:" + tool.getIssuer() + "\"";
		String context_label = context.getLabel();

		   DomSamlUser u = new DomSamlUser();
		   u.setSamlOrgId(s(org_id));
		   u.setSamlUserId(s(user_id));
		   String path = "/";
	       Cookie user = new Cookie(DWO_SAML_USER_ID, u.getSamlUserId());
	       user.setPath(path);
	       user.setSecure(request.isSecure());
	       Cookie orgid = new Cookie(DWO_SAML_ORGANIZATION_ID, u.getSamlOrgId());
	       orgid.setPath(path);
	       orgid.setSecure(request.isSecure());
	       response.addCookie(user);
	       response.addCookie(orgid);
		   try {
		       u = systemManager.requestSamlToken(u);
		       Cookie authToken = new Cookie(DWO_SAML_AUTH_TOKEN, u.getAuthToken());
		       authToken.setSecure(request.isSecure());
		       authToken.setPath(path);
		       response.addCookie(authToken);
		       return false;	  	       
		   } catch(Dwo2Exception e) {
		       LOG.log(Level.WARNING, "request SAML token: " + u.getSamlUserId() + " " + u.getSamlOrgId(), e);
		       try {
		         response.sendRedirect("/dwo/register/Register.html?cancel="
		        	    + URLEncoder.encode(tool.getPresentation().getReturnUrl(), "UTF-8")
		         		+ "&next=" + 
		         		  URLEncoder.encode(request.getRequestURL().toString(), "UTF-8")
		         );
		       } catch (IOException e1) {
		       }
		       return true;
		   }
		}

  
  
  
  	public void setCookie(Tool tool, HttpServletResponse response, String schoolID) {
		User user = tool.getUser();
		Platform platform = tool.getPlatform();
		Context context = tool.getContext();
		String user_id = tool.getCustomParameter("userid");
		String lti_id = user.getId();
		if (user_id == null) user_id = lti_id;
		String name_given = user.getGivenName();
		String name_family = user.getFamilyName();
		String name_prefix = "";
		int komma = name_family.indexOf(',');
		if (komma > 0) {
			name_prefix = name_family.substring(komma + 1);
			name_family = name_family.substring(0, komma);
		}
		String email = user.getEmail();
		String organisation = platform.getName();
		String orgid = "lti13:" + tool.getIssuer();
		
		String role = "STUDENT";
		if(!tool.isLearner())
			role = "TEACHER";
		String context_label = context.getLabel();
		
		String authTokenStr;
		try {
			authTokenStr = rest.registerSAML(
					user_id,
					lti_id,
					orgid,
					name_given, name_prefix, name_family,
					email,
					role,
					schoolID,
					context_label
					);
			Cookie authToken = new Cookie(DWO_SAML_AUTH_TOKEN, authTokenStr);
			String path = "/DWOmAccess";
			authToken.setPath(path);
			response.addCookie(authToken);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "setCookie", e);
		}

		
	}
  
  
  
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
	    if(organization == null)organization = request.getParameter("tool_consumer_instance_name");
	    if(organization == null)organization = request.getParameter("context_title");
	    if(organization == null)organization = context_label;
	    if(organization == null)organization = request.getParameter("resource_link_title");
	    if(organization == null)organization = oauth_consumer_key;
	    
	    String path = request.getContextPath();
	    if(path.isEmpty()) path = "/DWOmAccess"; // EBServer fix
		Cookie user  = new Cookie(DWO_SAML_USER_ID, lti_id);
		user.setPath(path);
		String orgidStr = "lti:" + oauth_consumer_key;
		Cookie orgid = new Cookie(DWO_SAML_ORGANIZATION_ID, orgidStr);
		orgid.setPath(path);
		orgidStr = "\"" + orgidStr + "\"";
		Cookie org   = new Cookie(DWO_SAML_ORGANIZATION, organization);
		org.setPath(path);
		response.addCookie(user);
		response.addCookie(orgid);
		response.addCookie(org);
		
		String roles = request.getParameter("roles");
		String role = "STUDENT";
		if(roles != null && roles.toLowerCase().contains("instructor"))
			role = "TEACHER";
// teaching assistant
		if(roles != null && roles.toLowerCase().contains("teaching"))
			role = "TEACHER";
		
		String authTokenStr;
		try {
			authTokenStr = rest.registerSAML(
					user_id,
					lti_id,
					orgidStr,
					name_given, name_prefix, name_family,
					email,
					role,
					oauth_consumer_key,
					context_label
					);
			Cookie authToken = new Cookie(DWO_SAML_AUTH_TOKEN, authTokenStr);
			authToken.setPath(path);
			response.addCookie(authToken);
		} catch (IOException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @param facade
	 * @param oauth_consumer_key
	 * @param context_label
	 * @return
	 * @throws PersistenceException
	 */
	protected DomSchoolClass getSchoolClass(
			String oauth_consumer_key, String context_label)
			throws Dwo2Exception {
		if(context_label != null) {
	 		DomSchool s = getSchool(oauth_consumer_key);
	 		List<DomSchoolClass> array = systemManager.getSchoolClasses(s);
			DomSchoolClass c;
			for (int i = 0; i < array.size(); i++) {
				c = array.get(i);
				if(c.getSchoolClassName().equals(context_label))
					return c;
			}
		}
		return null;
	}
	
	public String getSecret(String key) {
		return getSecret(key, RoleType.STUDENT);
	}

	public String getSecret(String key, RoleType gid) {
		try {
			DomSchoolFull result = getSchool(key);
			return result.getPasswords().stream().filter(entry -> entry.getKey() == gid).findFirst().get().getValue();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "get secret " + key + "," + gid, e);
		}
		return null;
	}

	/**
	 * @param instance
	 * @param key
	 * @return
	 * @throws PersistenceException
	 */
	protected DomSchoolFull getSchool(String key)
			throws Dwo2Exception {
		Long id = Long.valueOf(key);
		PersistenceId pid = PersistentSchool.buildPersistenceId(id);
        DomSchoolId submit = new DomSchoolId(pid);
		DomSchoolFull result = systemManager.getSchool(submit);
		return result;
	}
	
	public String getOrganization(String key) {
	  try {
	      DomSchoolFull result = getSchool(key);
	      return result.getSchoolName();
	  } catch (Exception e) {
	      logger.log(Level.SEVERE, "organization of " + key, e);
	      return key;
	  }
	}
	
	public String getRealm(String key) {
		try {
			DomSchoolFull result = getSchool(key);
			return result.getSchoolLogin();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "getRealm for " + key, e);
		}
		return key;
	}
	
	private static final String SCO = "/sco/";
	private static final String COURSE = "/course/";
  private final Logger logger = Logger.getLogger(getClass().getName());
	public String getDeepLink(String info) {
		if(info == null) return "";
		if(info.startsWith(SCO))
		{
			String sco = info.substring(SCO.length());
			return "<param name='scoViewNr' value='" + sco + "'>";
		}
		if(info.startsWith(COURSE))
		{
			String course = info.substring(COURSE.length());
			return "<param name='courseViewNr' value='" + course + "'>";
		}
		return "";
	}
	
}
