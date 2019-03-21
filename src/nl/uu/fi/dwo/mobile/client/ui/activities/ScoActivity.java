package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Provider;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.s;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.GotoController;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.NoCourseView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

import fi.dwo.gwt.lib.rest.util.PromiseCallback;

public class ScoActivity extends MGWTAbstractActivity implements AnchorContext, ViewModuleView.Presenter, GotoController {

	@Inject SelectModuleItem item;
	@Inject ViewModuleView view;
	private String name;
	private AnchorContext defaultContext;
	final private LoginPlace next;
	final private String location;
	@Inject PlaceController placeController;
	@Inject nl.uu.fi.dwo.mobile.client.ui.RPCHandler rpcHandler;
	private boolean started;
    @Inject DomSchoolClass schoolClass;
    @Inject DomSchool school;
    @Inject RoleType  role;
    @Inject Provider<NoCourseView> noCourseView;
    @Inject HeaderView headerView;
	@Inject ScoActivity(s where) {
		next = new LoginPlace(where);
		location = where.getLocation();
	}
		
	public ScoActivity(ClientFactory clientFactory, SelectModuleItem item, s where) {
		this(where);
		this.item = item;
		placeController = clientFactory.getPlaceController();
		view = clientFactory.getEntryView();
		rpcHandler = clientFactory.getRPCHandler();
		schoolClass = clientFactory.getSchoolClass();
		noCourseView = clientFactory.getNoCourseView();
		school = clientFactory.getSchool();
		headerView = clientFactory.getHeaderView();
		role = clientFactory.getRoleType();
	}

	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus)
	{
		defaultContext = view.getAnchorContext();
		view.setPresenter(this);
		view.removeBtns(); // up en home
		view.getBackButton().setText(nl.uu.fi.dwo.mobile.client.text.Text.constants.login());
		
		String scoID = item.getID().toString();
		DWOplayer.insertCSS(scoID);
		view.setUnitId(scoID);

		addHandlerRegistration(
		view.getBackButton().addTapHandler(new TapHandler() {

			@Override
			public void onTap(TapEvent event) {
				gotoNext();
			}}));
		
		name = item.getName();
		Promise<String> namePromise;
		if(name == null) {
			name = scoID;
			Promise<DomScoContext> sco;
			if (schoolClass != null /*&& item.isFromSchool()*/) { // XXX unsure if isSchool correct
			  //sco = rpcHandler.getSco(item.getID());
			  sco = rpcHandler.getScoContextClass(item.getID(), schoolClass)
			  .filter(p -> !p.getScoContexts().isEmpty())
// should not happen. Server should prevent this.
			  .filter(p-> p.getClassCourses().get(0).getValue().getCourseType() != CourseType.assesment)
			  .map(p -> p.getScoContexts().get(0).getValue());
			} else {
	            sco = rpcHandler.getSco(item.getID())
	            // temporary		
	            .filter( ssc -> {
	            	PersistenceId schoolID = ssc.getSchoolId();
	            	if(schoolID == null) return true;
	            	if (school == null) return false;
	            	return school.getId().equals(schoolID);
	            })
	            ;
	  
			}
			namePromise = 
			  sco.then(new Success<DomScoContext, String>() {

				@Override
				public Promise<String> call(Promise<DomScoContext> resolved) throws Exception {
					name = resolved.getValue().getScoName();
					item.setName(name);
					item.setFromSchool(resolved.getValue().getSchoolId() != null);
					view.setScoType(resolved.getValue().getScoType());
					view.setTitle(name);
					return Promises.resolved(name);
				}
			});
			
		} else {
			namePromise = Promises.resolved(name);
		}
		final Failure failure = new Failure() {
			

			@Override
			public void fail(Promise<?> resolved) throws Exception {
				Throwable t = resolved.getFailure();
				if(t instanceof Dwo2Exception) {
					Dwo2Exception e = (Dwo2Exception) t;
					if( e.getDwo2Code() == Dwo2ExceptionCode.Rest_LoginNeeded && school == null)
					{	item.setFromSchool(true);
						started = false;
						gotoNext();
						return;
					}
				}
				if (t instanceof NoSuchElementException || t instanceof Dwo2Exception)
                {
                    NoCourseView view = noCourseView.get();
            		DomUserFull currentUser = DwoGlobalVars.instance().getCurrentUser();
            		headerView.setUserAndRole(currentUser, role);
            		headerView.setPresenter(ScoActivity.this);
                    panel.setWidget(view);
                    view.setHomePlace(next.getPlace());
                    view.render();
                    return;
                }
				Logger.getLogger("ScoActivity").log(Level.SEVERE, "initialize()", t);
				Window.alert(t.getMessage());
				gotoNext();
			}
		};
		namePromise.flatMap(
				new Function<String, Promise<? extends Void>>() {

					@Override
					public Promise<? extends Void> apply(String t) {
						PromiseCallback<Void> callback = new PromiseCallback<Void>();
						view.getApi().Initialize(callback);
						return callback.getPromise();
					}
				}
				).then(new Success<Void, Void>() {

					@Override
					public Promise<Void> call(Promise<Void> resolved)
							throws Exception {
						started = ! Memento.COMPLETED.equals(view.getApi().GetValue(Memento.COMPLETION_STATUS));
						if(location != null) {
							view.getApi().SetValue(Memento.LOCATION, location);
						}
						view.setupModule(name, item.getFile());
						panel.setWidget(view);
						return null;
					}
				}, failure);
		
	}

	@Override
	public void onStop() {
		started = false;
		view.setAnchorContext(defaultContext);
		view.close();
		super.onStop();
	}
	
	@Override public void onCancel() {
		started = false;
		view.setAnchorContext(defaultContext);
		super.onCancel();
	}

	@Override
	public String mayStop() {
		if (started && DWOplayer.withUser())
			return Text.constants.maybe_lost_data();
		return super.mayStop();
	}

	@Override
	public void gotoUrl(String href) {
		if("goto:0".equals(href))
			gotoNext();
		else
			defaultContext.gotoUrl(href);
	}

	void gotoNext() {
		started = false;
		placeController.goTo(next);
	}

	@Override
	public void goTo(Place place) {
		gotoNext();	
	}

}
