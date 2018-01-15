package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewNumworx;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

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

public class ScoActivity extends MGWTAbstractActivity implements AnchorContext, ViewModuleView.Presenter {

	@Inject SelectModuleItem item;
	@Inject ViewModuleView view;
	private String name;
	private AnchorContext defaultContext;
	private Place next;
	@Inject PlaceController placeController;
	@Inject nl.uu.fi.dwo.mobile.client.ui.RPCHandler rpcHandler;

	@Inject ScoActivity() {}
	
	public ScoActivity(ClientFactory clientFactory, SelectModuleItem item) {
		this.item = item;
		placeController = clientFactory.getPlaceController();
		view = clientFactory.getEntryView();
		//view = new ViewModuleViewNumworx().initialize().setupAPI();
		rpcHandler = clientFactory.getRPCHandler();
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
		next = new LoginPlace(placeController.getWhere());

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
			namePromise = 
			rpcHandler.getSco(item.getID()).then(new Success<DomScoContext, String>() {

				@Override
				public Promise<String> call(Promise<DomScoContext> resolved) throws Exception {
					name = resolved.getValue().getScoName();
					item.setName(name);
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
					if( e.getDwo2Code() == Dwo2ExceptionCode.Rest_LoginNeeded)
					{
						gotoNext();
						return;
					}
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
						view.setupModule(name, item.getFile());
						panel.setWidget(view);
						return null;
					}
				}, failure);
		
	}

	@Override
	public void onStop() {
		view.setAnchorContext(defaultContext);
		view.close();
		super.onStop();
	}
	
	@Override public void onCancel() {
		view.setAnchorContext(defaultContext);
		super.onCancel();
	}

	@Override
	public void gotoUrl(String href) {
		if("goto:0".equals(href))
			gotoNext();
		else
			defaultContext.gotoUrl(href);
	}

	void gotoNext() {
		placeController.goTo(next);
	}

	@Override
	public void goTo(Place place) {
		gotoNext();	
	}

}
