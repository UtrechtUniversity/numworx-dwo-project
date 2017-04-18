package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.List;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;

public class CourseActivity extends MGWTAbstractActivity implements Activity {

	private ClientFactory clientFactory;
	private SelectModuleItem item;

	public CourseActivity(ClientFactory clientFactory, SelectModuleItem item) {
		this.clientFactory = clientFactory;
		this.item = item;
	}
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		DWOplayer.api = clientFactory.setupAPI();
		final SelectModuleView view = clientFactory.getHomeView();
		if(true)
			view.setMenuWidget(clientFactory.getMenuWidget());

		view.setLogout(true); // terug of logout
		final Place next = 
				new LoginPlace(
						clientFactory.getPlaceController().getWhere());
		
		addHandlerRegistration(view.getList().addCellSelectedHandler(new CellSelectedHandler()
		{
			@Override
			public void onCellSelected(CellSelectedEvent event)
			{
				final SelectModuleItem id = view.getItems().get(event.getIndex());
				clientFactory.getPlaceController().goTo(new ViewModulePlace(id.getID()));
			}
		}));
		addHandlerRegistration(view.getBackBtn().addTapHandler(new TapHandler() {		
			@Override
			public void onTap(TapEvent event) {
				clientFactory.getPlaceController().goTo(next);			
			}
		}));
		
		if(item.getName() == null) {
			item.setName("#c:" + item.getID());

			Promise<List<SelectModuleItem>> promise = item.getChildrenAsync();
// Start downloading sco's
			if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
				promise = DWOplayer.clientfactory.getRPCHandler().getScos(item.getID())
						.map(new SCO_TO_MODULEITEM(item));
				item.setChildrenAsync(promise);
			}
// start downloading description/name/attributes
			
			clientFactory.getRPCHandler().getCourse(item.getID()).then(new Success<DomCourseStudent, Void>() {

				@Override
				public Promise<Void> call(Promise<DomCourseStudent> resolved) throws Exception {
					String name = resolved.getValue().getName();
					String description = resolved.getValue().getDescription();
					item.setDescription(description);
					item.setName(name);
					item.showChildren(!resolved.getValue().isNotVisible());
					view.setDescription(item);
					view.render(item);
					return null;
				}
			});
		} else {
			view.render(item);
			
		}
		view.setDescription(item);
		panel.setWidget(view);
	}
}
