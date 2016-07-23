package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

public class ReloginActivity extends MGWTAbstractActivity {

	private ClientFactory clientFactory;
	private Place next;

	private final AsyncCallback<Map<String, Object>> LOGIN_CALLBACK = new AsyncCallback<Map<String, Object>>()
			{

				@Override
				public void onFailure(Throwable caught)
				{
					GWT.log("login failure", caught);
					if (caught.getMessage().contains("LoginException"))
						Window.alert(Text.constants.EXR_WRONG_USERNAME_PASSWORD());
					else
						Window.alert("Unable to login");

				}

				@Override
				public void onSuccess(Map<String, Object> result)
				{
					DWOplayer.profiledata = result;
					if(next == null)
						DWOplayer.gotoCourses();
					else
						clientFactory.getPlaceController().goTo(next);
				}

			};

	public ReloginActivity(ClientFactory clientFactory, Place next) {
		this.clientFactory = clientFactory;
		this.next = next;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		SelectModuleItemHolder.destroy();
		DWOplayer.profiledata = null;
		String user_id = Cookies.getCookie(LoginActivity.DWO_SAML_USER_ID);
		String org_id = Cookies.getCookie(LoginActivity.DWO_SAML_ORGANIZATION_ID);
		panel.setWidget(new Label());

//		if(user_id != null && org_id != null) {
//			clientFactory.getRPCHandler().samlLogin(user_id, org_id, LOGIN_CALLBACK);
//			return;		
//		}
		clientFactory.getRPCHandler().loginMD5(getUsername(), getPassword(), LOGIN_CALLBACK);
	}

	private String getUsername() {
		return DwoGlobalVars.instance().getCurrentUser().getUserName();
	}

	private String getPassword() {
		return DwoGlobalVars.instance().getCurrentUser().getPassword();
	}

}
