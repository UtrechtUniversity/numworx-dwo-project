package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletFull;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletId;
import nl.uu.fi.dwo.rest.entities.RestAppletId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class PublicAppletManager {

	public static DomAppletFull getApplet(DomAppletId id) throws Dwo2Exception {
		RestAppletId rest = new RestAppletId();
		rest.setRestContext(RestAuthenticator.getInstance().getContext());
		rest.setDomAppletId(id);
		DomAppletFull result = StoredRestManager.getInstance()
		        .put("rest/public/applet/get", DomAppletFull.class, rest);
		return result;
	}
}
