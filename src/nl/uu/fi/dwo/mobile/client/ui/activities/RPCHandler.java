package nl.uu.fi.dwo.mobile.client.ui.activities;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

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

	@Override
	public Promise<Void> startExam(String id, String password) {
		return Promises.resolved(null);
	}
	
}
