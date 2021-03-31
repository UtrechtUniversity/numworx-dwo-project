package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;

import dagger.MembersInjector;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.MessageDialog;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;

public class ReloginActivity extends AbstractActivity {

	@Inject ClientFactory clientFactory;
	@Inject HeaderView headerView;
	@Inject DwoGlobalVars vars;
	@Inject TrafficAgent agent;

	private Place next;

	private String username;
	private String password;
	@Inject PlaceController placeController;
	@Inject RPCHandler rpc;
	
	private SecuredUserAccountManager manager = new SecuredUserAccountManager();

	public ReloginActivity(MembersInjector<ReloginActivity> injector, Place next) {
		injector.injectMembers(this);
		this.next = next;
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
		password = vars.getCurrentUser().getPassword();
		username = vars.getCurrentUser().getUserName();
		String realm = vars.getCurrentLoginContext().getRealm();
		if (realm != null) username += realm;
		
		(getPassword().isEmpty() ? agent.barrier() : clientFactory.logout())
		.then(new Success<Void, DomUserFullwLoginContext>() {

			@Override
			public Promise<DomUserFullwLoginContext> call(Promise<Void> resolved) throws Exception {
				SelectModuleItemHolder.destroy();
				panel.setWidget(new Label());
				if (getPassword().isEmpty()) {
					return getUserFullwLoginContext();
				}
				return rpc.loginMD5(getUsername(), getPassword());
			}
		})
		.then(new LoginActivity.Login_Stap1(rpc, vars))
		.then(new LoginActivity.Login_Stap2(vars), FAILURE1).map(nop -> next)
		.then(new Login_Stap3(clientFactory, placeController, headerView, vars));
	}

	protected Promise<DomUserFullwLoginContext> getUserFullwLoginContext() {
		Promise<DomLoginContext> lc = manager.getLoginContext();
		return lc.then(p -> { 
			DomContext context = new DomContext();
			context.setDomHasRole(new DomHasRole());
			context.getDomHasRole().setId(p.getValue().getHasRoleId());
			context.setRealm(p.getValue().getRealm());
			return manager.getAccountData(context);		
		}).map (u -> {
			DomUserFullwLoginContext ulc = new DomUserFullwLoginContext();
			ulc.setDomLoginContext(lc.getValue());
			ulc.setDomUserFull(u);
			return ulc;
		});
	}

	private String getUsername() {
		return this.username;
	}

	private String getPassword() {
		return this.password;
	}

}
