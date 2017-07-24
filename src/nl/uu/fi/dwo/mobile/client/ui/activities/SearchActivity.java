package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.util.promise.Promises;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView.Presenter;

public class SearchActivity extends MGWTAbstractActivity implements Activity, Presenter {

	private ClientFactory clientFactory;
	private long id;
	private TreeModuleView view;
	private List<SelectModuleItem> currentModel;

	public SearchActivity(ClientFactory clientFactory, long id) {
		this.clientFactory = clientFactory;
		this.id = id;
	}

	@Override
	public void onStop() {
		view.close();
		super.onStop();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view = clientFactory.getTreeModuleView();
		currentModel = SelectModuleItemHolder.getItems();
		view.setPresenter(this);
		view.render(currentModel);
		final SelectModuleItem item = SelectModuleItemHolder.getSearch(id);
		if(item == null)
		{
			goTo(new TreeModulePlace());
			return;
		}
		if(item.getPromisedScoreMap() == null) {
			Map<Object, Number> value = new HashMap<Object,Number>();
			item.setPromisedScoreMap(Promises.resolved(value));
		}
		item.getChildrenAsync().onResolve(new Runnable() {

			@Override
			public void run() {
				view.selectModule(item);
			} });
		panel.setWidget(view);
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

}
