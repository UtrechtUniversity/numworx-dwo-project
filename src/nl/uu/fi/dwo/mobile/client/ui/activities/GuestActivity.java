package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Inject;

import org.osgi.util.function.Predicate;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class GuestActivity extends AbstractActivity implements Activity {

	private ClientFactory clientFactory;
	@Inject PlaceController placeController;
	@Inject HeaderView headerView;
	@Inject DwoGlobalVars instance;
	@Inject RPCHandler rpc;
	

	@Inject GuestActivity(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		clientFactory.logout();
		panel.setWidget(new Label());
		rpc.getDwoProfile()
			.filter(new Predicate<DomDwoProfileFull>() {

				@Override
				public boolean test(DomDwoProfileFull t) {
					String rights = t.getDwoProfileRights();
					return ! rights.contains("l");
				}
			})
			.then(new Success<DomDwoProfileFull, Void>() {

				@Override
				public Promise<Void> call(Promise<DomDwoProfileFull> resolved) throws Exception {
					instance.setCurrentLoginContext(null);
					instance.setCurrentUser(null);
					instance.setActiveSchoolRoleAndClass(null);
					headerView.setUserAndRole(null, RoleType.STUDENT);
					DWOplayer.gotoCourses();
					return null;
				}
				
			}, new Failure() {
				
				@Override
				public void fail(Promise<?> resolved) throws Exception {
					placeController.goTo(new LoginPlace());
				}
			}
		);
	
	}

}
