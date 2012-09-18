package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.List;

import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;

import com.google.gwt.event.shared.EventBus;
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
		SelectModuleView view = clientFactory.getHomeView();

		currentModel = SelectModuleItemHolder.getItems();

		view.render(currentModel);

		addHandlerRegistration(view.getList().addCellSelectedHandler(new CellSelectedHandler()
		{

			@Override
			public void onCellSelected(CellSelectedEvent event)
			{
				clientFactory.getPlaceController().goTo(new ViewModulePlace(currentModel.get(event.getIndex()).getID() + ""));
			}
		}));

		panel.setWidget(view);
	}

}
