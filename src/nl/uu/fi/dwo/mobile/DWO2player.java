package nl.uu.fi.dwo.mobile;

import java.util.Map;

import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWO2;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.client.ui.activities.RPCHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.restrpcgwt.client.RestRPCHandler;

public class DWO2player extends DWOplayer implements EntryPoint {

	public DWO2player() {

	}

	
	protected ClientFactory createClientFactory() {
		ClientFactoryImpl factory = new ClientFactoryImpl() { 
			
			public SCORM_guest setupAPI(final Map<String, Object> profiledata) {
				SCORM_guest api;
				if(profiledata == null) {
					api = new SCORM_guest();
					menuWidget = null;
				} else {
					Object userID = profiledata.get("userID");
					Object sgID = profiledata.get("schoolGroupID");
					api = new SCORM_DWO2(userID, sgID);
					getUserBar().setProfile(profiledata);
				}
				return api;
			}

		};
		String host = PARAMETERS.getHost();
		String http = Window.Location.getProtocol();
		final RestRPCHandler restHandler = new RestRPCHandler(http + "//" + host + "/dwo2/rest/");
		factory.setRPCHandler(new RPCHandler(http + "//" + host + "/dwo2/xmlrpc"){

			@Override
			public void login(String name, String password,
					AsyncCallback<? super Map<String, Object>> callback) {
				restHandler.login(name, password, callback);
			}
			
		});
		return factory;
	}

}
