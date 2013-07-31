package nl.uu.fi.dwo.mobile.client.ui.activities;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

/**
 * Display module activity
 * 
 * @author Danny Hendrix
 * 
 */
public class ViewModuleActivity extends MGWTAbstractActivity
{
	private ClientFactory clientFactory;
	private ViewModuleView view;

	public ViewModuleActivity(ClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;

	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		view = clientFactory.getEntryView();
		panel.setWidget(view);

		Place place = clientFactory.getPlaceController().getWhere();

		if (place instanceof ViewModulePlace)
		{
			ViewModulePlace selectedModulePlace = (ViewModulePlace) place;
			final int id = Integer.parseInt(selectedModulePlace.getToken());
			final SelectModuleItem item = SelectModuleItemHolder.getScoByID(id);
			DWOplayer.api.setScoID(id);
			AsyncCallback<Void> callback = new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					view.setupModule(item.getName(), item.getFile());
				}

				@Override
				public void onSuccess(Void result) {
					view.setupModule(item.getName(), item.getFile());
				}
			};
			DWOplayer.api.Initialize(callback);
			addHandlerRegistration(view.getBackButton().addTapHandler(new TapHandler()
			{

				@Override
				public void onTap(TapEvent event)
				{
					History.back();
				}
			}));
		}
	}

	@Override
	public void onStop() {
		view.close();
		super.onStop();
	}
	
}
