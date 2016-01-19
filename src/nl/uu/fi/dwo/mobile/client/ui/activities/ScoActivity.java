package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

public class ScoActivity extends MGWTAbstractActivity {

	private ClientFactory clientFactory;
	private SelectModuleItem item;
	private ViewModuleView view;
	private String name;

	public ScoActivity(ClientFactory clientFactory, SelectModuleItem item) {
		super();
		this.clientFactory = clientFactory;
		this.item = item;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		view = clientFactory.getEntryView();
		view.getBackButton().setText(nl.uu.fi.dwo.mobile.client.text.Text.constants.login());
		
		String scoID = item.getID().toString();
		view.setUnitId(scoID);
		view.setApi(DWOplayer.api);
		final Place next = 
				new LoginPlace(
						clientFactory.getPlaceController().getWhere());

		addHandlerRegistration(
		view.getBackButton().addTapHandler(new TapHandler() {

			@Override
			public void onTap(TapEvent event) {
				clientFactory.getPlaceController().goTo(next);
			}}));
		
		name = item.getName();
		if(name == null) {
			name = scoID;
			AsyncCallback<Map<String, Object>> callback = new AsyncCallback<Map<String,Object>>() {

				@Override
				public void onFailure(Throwable caught) {					// TODO Auto-generated method stub
				}

				@Override
				public void onSuccess(Map<String, Object> result) {
					name = String.valueOf (result.get("sconame"));
					item.setName(name);
					view.setTitle(name);
				}
				
			};
			clientFactory.getRPCHandler().getSco(item.getID(), callback);
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
		view.close();
		super.onStop();
	}

}
