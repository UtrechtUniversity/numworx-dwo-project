package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.List;

import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;

/**
 * Select module activity
 * 
 * @author Danny Hendrix
 * 
 */
public class SelectModuleActivity extends MGWTAbstractActivity
{
	ClientFactory clientFactory;
	private List<SelectModuleItem> currentModel;

	public SelectModuleActivity(ClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		final SelectModuleView view = clientFactory.getHomeView();

		currentModel = SelectModuleItemHolder.getItems();
		Place place = clientFactory.getPlaceController().getWhere();
		SelectModulePlace selectedModulePlace = (SelectModulePlace) place;
		int id = Integer.parseInt(selectedModulePlace.getToken());
		SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
		if(item == null)
			view.render(currentModel);
		else
			view.render(item);
		
		addHandlerRegistration(view.getList().addCellSelectedHandler(new CellSelectedHandler()
		{

			@Override
			public void onCellSelected(CellSelectedEvent event)
			{
				clientFactory.getPlaceController().goTo(new ViewModulePlace(view.getItems().get(event.getIndex()).getID()));
			}
		}));

		panel.setWidget(view);
	}

}
