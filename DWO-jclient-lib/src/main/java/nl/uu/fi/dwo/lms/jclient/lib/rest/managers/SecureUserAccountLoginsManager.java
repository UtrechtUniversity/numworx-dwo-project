package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.entities.RestNewSchoolLogin;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolLogin;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.entities.RestSchoolRoleAndClassV2;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureUserAccountLoginsManager {

  private static final Logger LOG =
      Logger.getLogger(SecureUserAccountLoginsManager.class.getName());

  /**
   * Returns the current user 'logged in'. The information is extracted from the security context
   * which depends on the credentials used for accessing the rest interface. Technically it should
   * be equal to the data in the DwoHelper.
   *
   * @return
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  public static DomSchoolsRolesAndClassesV2 getSchoolLogins() throws Dwo2Exception {
    return getSchoolLogins(StoredRestManager.getInstance());
  }

  /**
   * Switches to the schoollogin requested.
   *
   * @param src
   * @return
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  public static DomSchoolRoleAndClassV2 switchToSchoolLogin(DomSchoolRoleAndClassV2 src)
      throws Dwo2Exception {
    RestSchoolRoleAndClassV2 rest = new RestSchoolRoleAndClassV2();
    DomContext context = StoredRestManager.getInstance().getContext();
    DomHasRole old = context.getDomHasRole();
    try {
    	rest.setRestContext(context);
    	context.setDomHasRole(src.getHasRole());
    	rest.setDomSchoolRoleAndClass(src);
    DomSchoolRoleAndClassV2 result = StoredRestManager.getInstance()
        .put("rest/sec:" + PathId.getId(context) + "/user/account/loginsV2/select", DomSchoolRoleAndClassV2.class, rest);
    return result;
    } catch(Dwo2Exception oops) {
    	context.setDomHasRole(old);
    	throw oops;
    }
  }

  /**
   *
   * @param newSchoolLogin
   * @return
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  public static boolean addASchoolLogin(DomNewSchoolLogin newSchoolLogin) throws Dwo2Exception {
    boolean r;
    RestNewSchoolLogin rest = new RestNewSchoolLogin();
    DomContext context = StoredRestManager.getInstance().getContext();
	rest.setRestContext(context);
    rest.setDomNewSchoolLogin(newSchoolLogin);
    r = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(context) + "/user/account/loginsV2/submit",
        Boolean.class, rest);
    return r;
  }

  /**
   *
   * @param toRemoveSchoolLogin
   * @return
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  public static boolean removeASchoolLogin(DomSchoolRoleAndClassV2 toRemoveSchoolLogin)
      throws Dwo2Exception {
    RestSchoolRoleAndClassV2 rest = new RestSchoolRoleAndClassV2();
    DomContext context = StoredRestManager.getInstance().getContext();
	rest.setRestContext(context);
    rest.setDomSchoolRoleAndClass(toRemoveSchoolLogin);
    boolean r;
    r = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(context) + "/user/account/loginsV2/remove",
        Boolean.class, rest);

    return r;

  }

	public static DomSchoolsRolesAndClassesV2 getSchoolLogins(StoredRestManager instance) throws Dwo2Exception {
	    DomSchoolsRolesAndClassesV2 src;
	    DomContext context = instance.getContext();
		src = instance.get("rest/sec:" + PathId.getId(context) + "/user/account/loginsV2/getList",
	        DomSchoolsRolesAndClassesV2.class);
	    return src;
	}

}
