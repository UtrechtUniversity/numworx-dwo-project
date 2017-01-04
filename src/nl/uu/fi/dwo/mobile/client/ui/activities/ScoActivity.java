package nl.uu.fi.dwo.mobile.client.ui.activities;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

public class ScoActivity extends MGWTAbstractActivity implements AnchorContext {

	private ClientFactory clientFactory;
	private SelectModuleItem item;
	private ViewModuleView view;
	private String name;
	private AnchorContext defaultContext;
	private Place next;

	public ScoActivity(ClientFactory clientFactory, SelectModuleItem item) {
		super();
		this.clientFactory = clientFactory;
		this.item = item;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		DWOplayer.api = clientFactory.setupAPI();
		view = clientFactory.getEntryView();
		defaultContext = view.getAnchorContext();
		view.setApi(DWOplayer.api);

		view.getBackButton().setText(nl.uu.fi.dwo.mobile.client.text.Text.constants.login());
		
		String scoID = item.getID().toString();
		view.setUnitId(scoID);
		next = new LoginPlace(
				clientFactory.getPlaceController().getWhere());

		addHandlerRegistration(
		view.getBackButton().addTapHandler(new TapHandler() {

			@Override
			public void onTap(TapEvent event) {
				gotoNext();
			}}));
		
		name = item.getName();
		if(name == null) {
			name = scoID;
			clientFactory.getRPCHandler().getSco(item.getID()).then(new Success<DomScoContext, Void>() {

				@Override
				public Promise<Void> call(Promise<DomScoContext> resolved) throws Exception {
					name = resolved.getValue().getScoName();
					item.setName(name);
					view.setTitle(name);
					return null;
				}
			});
			
		}
		
		DWOplayer.api.setScoID(scoID);
		
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setupModule(name, item.getFile());
			}

			@Override
			public void onSuccess(Void result) {
				view.setupModule(name, item.getFile());
			}
		};
		DWOplayer.api.Initialize(callback);
		panel.setWidget(view);
	}

	@Override
	public void onStop() {
		view.setAnchorContext(defaultContext);
		view.close();
		super.onStop();
	}

	@Override
	public void gotoUrl(String href) {
		if("goto:0".equals(href))
			gotoNext();
		else
			defaultContext.gotoUrl(href);
	}

	void gotoNext() {
		clientFactory.getPlaceController().goTo(next);
	}

}
