package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

public class TreeModuleActivity extends MGWTAbstractActivity implements TreeModuleView.Presenter//, Comparator<SelectModuleItem>
{

	ClientFactory clientFactory;
	private List<SelectModuleItem> currentModel;
	private TreeModuleView view;
	private SelectModuleItem item;

	public TreeModuleActivity(ClientFactory clientFactory, SelectModuleItem i)
	{
		this.clientFactory = clientFactory;
		this.item = i;
	}

	@SuppressWarnings("unused")
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		view = clientFactory.getTreeModuleView();
		if(true)
			view.setMenuWidget(clientFactory.getMenuWidget());
		boolean select = true;
		if(item.getType() == Type.MODULE && DWOplayer.withUser()) {
			Object userID = DWOplayer.profiledata.get("userID");
		if(userID != null) {	
			Map<Object, Number> scoreMap;
			scoreMap = item.getScoreMap();
			if(scoreMap == null)
			{	
				Object courseID = item.getID();
				AsyncCallback<List<Map<String,Object>>> getUserResultsCallback = new AsyncCallback<List<Map<String,Object>>>() {

				@Override
				public void onFailure(Throwable caught) {
					Logger.getLogger("TreeModuleActivity").log(Level.SEVERE, "failure", caught);
					view.selectModule(item);
				}

				@Override
				public void onSuccess(List<Map<String,Object>> result) {
					Map<Object, Number> scoreMap = item.getScoreMap();
					if(scoreMap == null) {
						scoreMap = new HashMap<Object,Number>();
						item.setScoreMap(scoreMap);
					}
					for( Map<String,Object> entry : result) {
						Object id = entry.get("scoID");
						Object score = entry.get("score");
						if(score instanceof Number) {
							scoreMap.put(id, (Number) score);
						} else {
							scoreMap.remove(id);
						}
					}
					//Logger.getLogger("TreeModuleActivity").fine("succes " + result);
					view.selectModule(item);
				}
			};
			clientFactory.getRPCHandler().getUserResults(courseID, userID, getUserResultsCallback);
			select = false;
		}}}

		panel.setWidget(view);
		currentModel = SelectModuleItemHolder.getItems();
		view.setPresenter(this);
		view.render(currentModel);
		if(select)
			view.selectModule(item);
	}
	@Override
	public void onStop() {
		view.close();
		super.onStop();
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

}
