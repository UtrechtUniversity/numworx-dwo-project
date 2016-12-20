package nl.uu.fi.dwo.mobile.client.ui.activities;

import org.osgi.util.function.Predicate;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;

public class GuestActivity extends MGWTAbstractActivity implements Activity {

	private ClientFactory clientFactory;

	public GuestActivity(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		clientFactory.logout();
		panel.setWidget(new Label());
		
		DWOplayer.dwoProfile
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
					DwoGlobalVars instance = DwoGlobalVars.getInstance();
					instance.setCurrentLoginContext(null);
					instance.setCurrentUser(null);
					instance.setCurrentSchoolClass(null);
					DWOplayer.api = clientFactory.setupAPI();
					DWOplayer.gotoCourses();
					return null;
				}
				
			}, new Failure() {
				
				@Override
				public void fail(Promise<?> resolved) throws Exception {
					clientFactory.getPlaceController().goTo(new LoginPlace());
				}
			}
		);
	
	}

}
