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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
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
	private SelectModuleItem item;

	public SelectModuleActivity(ClientFactory clientFactory, SelectModuleItem item)
	{
		this.clientFactory = clientFactory;
		this.item = item;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		final SelectModuleView view = clientFactory.getHomeView();

		currentModel = SelectModuleItemHolder.getItems();
		if(item == null || item == SelectModuleItem.ROOT)
		{
			view.render(currentModel);
			view.setDescription(SelectModuleItem.ROOT);
		}
		else
		{
			view.render(item);
			view.setDescription(item);
		}

		addHandlerRegistration(view.getBackBtn().addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				History.back();
			}
		}));
		addHandlerRegistration(view.getList().addCellSelectedHandler(new CellSelectedHandler()
		{

			@Override
			public void onCellSelected(CellSelectedEvent event)
			{
				final SelectModuleItem id = view.getItems().get(event.getIndex());
				clientFactory.getPlaceController().goTo(new ViewModulePlace(id.getID()));
			}
		}));

		panel.setWidget(view);
	}

}
