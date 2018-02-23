package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.List;

import javax.inject.Inject;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;

public class CourseActivity extends MGWTAbstractActivity implements Activity {

	private ClientFactory clientFactory;
	private SelectModuleItem item;
	@Inject PlaceController placeController;

	public CourseActivity(ClientFactory clientFactory, SelectModuleItem item) {
		this.clientFactory = clientFactory;
		this.item = item;
		placeController = clientFactory.getPlaceController();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		final SelectModuleView view = clientFactory.getHomeView();
		if(true)
			view.setMenuWidget(clientFactory.getMenuWidget());

		view.setLogout(true); // terug of logout
		final Place next = 
				new LoginPlace(
						placeController.getWhere());
		
		addHandlerRegistration(view.getList().addCellSelectedHandler(new CellSelectedHandler()
		{
			@Override
			public void onCellSelected(CellSelectedEvent event)
			{
				final SelectModuleItem id = view.getItems().get(event.getIndex());
				placeController.goTo(new ViewModulePlace(id.getID()));
			}
		}));
		addHandlerRegistration(view.getBackBtn().addTapHandler(new TapHandler() {		
			@Override
			public void onTap(TapEvent event) {
				placeController.goTo(next);			
			}
		}));
		
		if(item.getName() == null) {
			item.setName("#c:" + item.getID());
			final Failure failure = new Failure() {
				
				@Override
				public void fail(Promise<?> resolved) throws Exception {
					Throwable t = resolved.getFailure();
					if(t instanceof Dwo2Exception) {
						Dwo2Exception e = (Dwo2Exception) t;
						if( e.getDwo2Code() == Dwo2ExceptionCode.Rest_LoginNeeded)
						{
							item.setFromSchool(true);
							placeController.goTo(next);
							return;
						}
					}
					GWT.log("failure", t);
				}
			};

			Promise<List<SelectModuleItem>> promise = item.getChildrenAsync();
// Start downloading sco's
			if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
				Success<List<SelectModuleItem>, List<SelectModuleItem>> success = 
						new Success<List<SelectModuleItem>, List<SelectModuleItem>>() {

							@Override
							public Promise<List<SelectModuleItem>> call(
									Promise<List<SelectModuleItem>> resolved)
									throws Exception {
								return resolved;
							}
				};
				promise = clientFactory.getRPCHandler().getScos(item.getID())
						.map(new SCO_TO_MODULEITEM(item)).then(success , failure);
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
					PersistenceId schoolId = resolved.getValue().getSchoolId();
					item.setFromSchool(schoolId != null);
					view.setDescription(item);
					view.render(item);
					return null;
				}
			}, failure);
		} else {
			view.render(item);
			
		}
		view.setDescription(item);
		panel.setWidget(view);
	}
}
