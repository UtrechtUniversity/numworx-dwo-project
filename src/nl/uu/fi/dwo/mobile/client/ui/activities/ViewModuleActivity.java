package nl.uu.fi.dwo.mobile.client.ui.activities;

import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;

/**
 * Display module activity
 * 
 * @author Danny Hendrix
 * 
 */
public class ViewModuleActivity extends AbstractActivity
{
	private ClientFactory clientFactory;

	public ViewModuleActivity(ClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;

	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		ViewModuleView view = clientFactory.getEntryView();
		panel.setWidget(view.asWidget());

		Place place = clientFactory.getPlaceController().getWhere();

		if (place instanceof ViewModulePlace)
		{
			ViewModulePlace selectedModulePlace = (ViewModulePlace) place;
			int id = Integer.parseInt(selectedModulePlace.getToken());

			SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);

			view.setupModule(item.getName(), item.getFile());

			view.getBackButton().addTapHandler(new TapHandler()
			{

				@Override
				public void onTap(TapEvent event)
				{
					clientFactory.getPlaceController().goTo(new SelectModulePlace("Selecteer module"));

				}
			});
		}
	}
}
