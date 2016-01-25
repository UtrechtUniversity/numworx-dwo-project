/**
 * 
 */
package nl.uu.fi.dwo.mobile;

import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.places.FlatModulePlace;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Cookies;

/**
 * @author peterboon
 *
 */
public class CoursePlayer extends DWOplayer implements EntryPoint {

	public CoursePlayer() {
		super();
		defaultPlace = new FlatModulePlace();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	@Override
	public void onModuleLoad() {
		api = new SCORM_guest();
		String org_id, user_id;
		org_id = Cookies.getCookie(DWO_SAML_ORGANIZATION_ID);
		user_id = Cookies.getCookie(DWO_SAML_USER_ID);
		log("user = " + user_id);
		log("org  = " + org_id);
		
		
		super.onModuleLoad();
	}

    private static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
	private static final String DWO_SAML_USER_ID = "dwoSAMLUserID";
	
	
	
}
