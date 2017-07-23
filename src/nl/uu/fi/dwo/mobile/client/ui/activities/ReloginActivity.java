package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.NoSuchElementException;
import java.util.logging.Level;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.views.MessageDialog;

public class ReloginActivity extends MGWTAbstractActivity {

	private ClientFactory clientFactory;
	private Place next;

	private String username;
	private String password;

	public ReloginActivity(ClientFactory clientFactory, Place next) {
		this.clientFactory = clientFactory;
		this.next = next;
	}

	private final Success<Void, Void> LOGIN_STAP3 = new Success<Void, Void>() {

		@Override
		public Promise<Void> call(Promise<Void> resolved) throws Exception {
			if(next == null) {
				DWOplayer.api = clientFactory.setupAPI();
				DWOplayer.gotoCourses();
			} else
				clientFactory.getPlaceController().goTo(next);
			return null;

		}
	};

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
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		SelectModuleItemHolder.destroy();
		password = DwoGlobalVars.instance().getCurrentUser().getPassword();
		username = DwoGlobalVars.instance().getCurrentUser().getUserName();
		clientFactory.logout();
		panel.setWidget(new Label());
		clientFactory.getRPCHandler().loginMD5(getUsername(), getPassword())
			.then(LoginActivity.LOGIN_STAP1)
			.then(LoginActivity.LOGIN_STAP2, FAILURE1)
			.then(LOGIN_STAP3);
	}

	private String getUsername() {
		return this.username;
	}

	private String getPassword() {
		return this.password;
	}

}
