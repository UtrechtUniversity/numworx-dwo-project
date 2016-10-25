package nl.uu.fi.dwo.mobile.client.ui.activities;

import nl.uu.fi.dwo.account.client.RPCHandlerV1;
import nl.uu.fi.dwo.mobile.DWOplayer;

import com.google.gwt.user.client.Window;

/**
 * equivalent van de PersistenceFacade
 * @author velth101
 *
 */
public class RPCHandler extends RPCHandlerV1 implements nl.uu.fi.dwo.mobile.client.ui.RPCHandler {

	public RPCHandler() {
		super(Window.Location.getProtocol() +  "//" + DWOplayer.PARAMETERS.getHost() + "/DWOmAccess/dbaccess", DWOplayer.PROFILE_ID);
	}
	
}
