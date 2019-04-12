package nl.uu.fi.dwo.register.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SystemManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class Manager {

	static final Logger LOG = Logger.getLogger(Manager.class.getName());

	private SystemManager manager;
	private URL dwoRest;

	public Manager(ServletContext context) {
		try {
			manager = getInstance(context);
		} catch (ServletException e) {
			LOG.log(Level.SEVERE, "Manager <init> fails", e);
		}
	}

	public Manager() {
	}

	public SystemManager getInstance(ServletContext context) throws ServletException {
		String dbrest_url = context.getInitParameter("dbrest.url");
		if (dbrest_url == null)
			dbrest_url = "http://localhost/dwo/";
		try {
			RestAuthenticator authenticator = new RestAuthenticator();
			authenticator.setServerUrlPath(dwoRest = new URL(dbrest_url));
			StoredRestManager rest = new StoredRestManager(authenticator);
			return new SystemManager(rest);
		} catch (MalformedURLException ex) {
			throw new ServletException("manager", ex);
		}

	}

	public String getSchoolCode(String schoolid, String role) {
		DomSchoolFull school = null;
		try {
			school = manager.getSchool(schoolid);
		} catch (Dwo2Exception e) {
			LOG.log(Level.SEVERE, "get school for " + schoolid, e);
		}
		if (school != null && AboType.premium == school.getAboType()) {
			
			List<DomMapEntry<RoleType, String>> passwords = school.getPasswords();
			if (passwords != null) 
				for (DomMapEntry<RoleType, String> item : passwords) {
				if (role.equals(item.getKey().name()))
					return item.getValue();
			}
		}
		return null;
	}
	
	public DomUserFull getUser(String auth) throws Dwo2Exception {
	  DomUserFull result = null;
      RestAuthenticator authenticator = new RestAuthenticator() {

        @Override
        public String getBasicAuthentication() {
          return auth;
        }
        
      };
      authenticator.setServerUrlPath(dwoRest);
      StoredRestManager rpc = new StoredRestManager(authenticator);
      rpc.setBasicAuthString(auth, auth, auth); // very dummy
      result = rpc.get("rest/secure/user/account/get", DomUserFull.class);
      return result;
	}
}
