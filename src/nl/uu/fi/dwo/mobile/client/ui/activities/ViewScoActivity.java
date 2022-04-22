package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Collections;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import dagger.Lazy;
import dagger.MembersInjector;
import dagger.Reusable;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.NeedLogin;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.s;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView.Presenter;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class ViewScoActivity extends AbstractActivity implements Presenter, AnchorContext {


	@Reusable public static class Factory implements ActivityFactory {
		@Inject MembersInjector<ViewScoActivity> injector;
		@Inject Factory() { }
	
		public ViewScoActivity create(SelectModuleItem item, Place place) {
			ViewScoActivity activity = new ViewScoActivity(injector, item, (s)place);
			return activity;
		}
	}

	private SelectModuleItem sco;
	private s place;
	private boolean started;
	@Inject DwoGlobalVars vars;
	@Inject Lazy<ViewModuleView> view;
	@Inject HeaderView headerView;
	@Inject RPCHandler rpcHandler;
	@SuppressWarnings("rawtypes")
	@Inject NeedLogin oops;
	@Inject PlaceController controller;
	
	private AnchorContext defaultContext;
	private DWOplayerParameters PARAMETERS;
	@Inject void setParameters(DWOplayerParameters p) {
		PARAMETERS = p;
	}

	private ViewScoActivity(MembersInjector<ViewScoActivity> injector, SelectModuleItem item, s place) {
		injector.injectMembers(this);
		this.sco = item;
		this.place = place;		
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		
		headerView.hide();
		headerView.setUpPlace(headerView.getHomePlace());		
		headerView.setTrail(Collections.emptyList()); // no trail
		PromiseCallback<Void> callback = new PromiseCallback<Void>();
		Promise<Void> promise = callback.getPromise();
		if (sco.getName() == null) {			
			promise = promise
					.then(p -> rpcHandler.getSco(sco.getID()))
					.then(p -> {
						DomScoContext s = p.getValue();
						sco = new SelectModuleItem(s);
						SelectModuleItemHolder.insert(sco);
						return null;
					});
		}
		final ViewModuleView view = this.view.get();
		String scoID = sco.getID().toString();
		DWOplayer.insertCSS(scoID);
		view.setUnitId(scoID);
		view.setTrail(Collections.emptyList());
		view.setPresenter(this);
		headerView.setPresenter(this);
		defaultContext = view.getAnchorContext();
		view.setAnchorContext(this);
		view.getApi().Initialize(callback);

		promise.then(p -> {
			PersistenceId modelid = sco.getStudentModelId();
			if(modelid != null) {
				view.setModel(Promises.resolved(new DomStudentModelContextId(modelid)));
			} else
				view.setModel(null);
			view.setTitle(sco.getName());
			view.setScoType(sco.getScoType());
			panel.setWidget(view);
			String location = place.getLocation();
			if(location != null) {
				view.getApi().SetValue(Memento.LOCATION, location);
			}
			started = !Memento.COMPLETED.equals(view.getApi().GetValue(Memento.COMPLETION_STATUS));	
			return view.setupModule(sco.getName(), PARAMETERS.getLaunchData() + scoID);
		}).then(p -> {
			if (p.getValue()) {
				Window.alert("Error: need a Premium subscription");
				started = false;
				History.back();
			}
			return null;
		}, p -> { 
			started = false;
			if (!oops.needed(oops.apply(p))) History.back();
		});
		
		
	}

	@Override
	public String mayStop() {	    
    	OpdrNavIF opdrNav = view.get().getOpdrNav();
    	if (opdrNav == null) return super.mayStop(); // komt voor als je een Premium activiteit start als standaard school
		opdrNav.setChanged(false);
		if (started && vars.withUser() && vars.getRoleType() == RoleType.STUDENT)
			return Text.constants.maybe_lost_data();
		return super.mayStop();
	}

	@Override
	public void onCancel() {
		started = false;
		if(defaultContext != null) {
			view.get().setAnchorContext(defaultContext); // unwrap
		}
		super.onCancel();
	}

	@Override
	public void onStop() {
	    headerView.setTrail(null);
		headerView.setPresenter(null);
		if(defaultContext != null) {
			view.get().setAnchorContext(defaultContext); // unwrap
			view.get().close();
			sco.setScore(view.get().getScoreRaw());
		}
		super.onStop();
	}

	@Override
	public void goTo(Place place) {
		started = false;
		controller.goTo(place);		
	}

	@Override
	public void gotoUrl(String href) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gotoPlace(String token) {
		// TODO Auto-generated method stub
		
	}

}
