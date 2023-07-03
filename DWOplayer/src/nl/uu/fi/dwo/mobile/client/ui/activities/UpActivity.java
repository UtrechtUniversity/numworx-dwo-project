package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import nl.uu.fi.dwo.mobile.client.ui.views.GotoController;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;

public class UpActivity extends AbstractActivity {

	private final PlaceController controller;
	private final HeaderView header;
	private final Place place;
	private Place up;

	@Inject UpActivity(PlaceController controller, HeaderView header, @Named("defaultPlace") Place place) {
		this.controller = controller;
		this.header = header;
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		up = header.getUpPlace();
		if (up == null) up = header.getHomePlace();
		if (up == null) up = place;
		GotoController presenter = header.getPresenter();

		Scheduler.get().scheduleDeferred(() ->
		{
			if (presenter != null) {
				presenter.goTo(up);
			} else
				controller.goTo(up);
		});
	}

}
