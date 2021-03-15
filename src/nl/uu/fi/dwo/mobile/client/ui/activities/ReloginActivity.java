package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.NoSuchElementException;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.views.MessageDialog;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;

public class ReloginActivity extends AbstractActivity {

	private ClientFactory clientFactory;
	private Place next;

	private String username;
	private String password;
	private PlaceController placeController;

	public ReloginActivity(ClientFactory clientFactory, Place next, PlaceController controller) {
		this.clientFactory = clientFactory;
		this.next = next;
		this.placeController = controller;
	}


	public static final Failure FAILURE1 = new Failure() {
		
		@Override
		public void fail(Promise<?> promise) throws Exception {
			Throwable caught = promise.getFailure();
			//LOG.log(Level.WARNING, "login failure ", caught);
			if (caught instanceof NoSuchElementException)
				alert("Geen toegang voor deze site"); // Rekenwise limited
			else
			if (caught.getMessage().contains("LoginException"))
				alert(Text.constants.EXR_WRONG_USERNAME_PASSWORD());
			else
				alert("Unable to login"); // if exception is DWO2exception?
		}

		private void alert(String string) {
			MessageDialog.alert(string);
		}
	};

	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus)
	{
		password = DwoGlobalVars.instance().getCurrentUser().getPassword();
		username = DwoGlobalVars.instance().getCurrentUser().getUserName();
		String realm = DwoGlobalVars.instance().getCurrentLoginContext().getRealm();
		if (realm != null) username += realm;
		
		clientFactory.logout()
		.then(new Success<Void, DomUserFullwLoginContext>() {

			@Override
			public Promise<DomUserFullwLoginContext> call(Promise<Void> resolved) throws Exception {
				SelectModuleItemHolder.destroy();
				panel.setWidget(new Label());
				return clientFactory.getRPCHandler().loginMD5(getUsername(), getPassword());
			}
		})
		.then(new LoginActivity.Login_Stap1(clientFactory))
		.then(LoginActivity.LOGIN_STAP2, FAILURE1)
		.then(new Login_Stap3(clientFactory, next, placeController));
	}

	private String getUsername() {
		return this.username;
	}

	private String getPassword() {
		return this.password;
	}

}
