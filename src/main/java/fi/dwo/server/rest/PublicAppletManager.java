package fi.dwo.server.rest;

import javax.persistence.PersistenceException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentApplet;
import fi.dwo.server.PersistentDataManagers.core.AppletManager;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletFull;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletId;
import nl.uu.fi.dwo.rest.entities.RestAppletId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

@Path("/public/applet")
public class PublicAppletManager {

	@PUT
	@Produces({ "application/json" })
	@Path("/get")
	public DomAppletFull getApplet(RestAppletId rest) throws Dwo2Exception {
		DomAppletId appletid = rest.getDomAppletId();
		Long id = MySQLPersistenceId.getNativeId(appletid);
		PersistentApplet applet;
		try {
			applet = AppletManager.findEntity(id);
			if (applet == null)
				return null;
		} catch (PersistenceException e) {
			throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "getApplet failed");
		}
		return applet.buildDomAppletFull();
	}

}
