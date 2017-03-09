package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerStudentCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;

import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;

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
		view.setLogout(false);
		if(true)
			view.setMenuWidget(clientFactory.getMenuWidget());

		currentModel = SelectModuleItemHolder.getItems();
		if(item == null || item == SelectModuleItem.ROOT)
		{
			view.render(currentModel, true);
			view.setDescription(SelectModuleItem.ROOT);
		}
		else
		{
			if(item.getType() == Type.MODULE && DWOplayer.withUser()) {
				Object userID = DWOplayer.clientfactory.getUserID();
			if(userID != null && item.getPromisedScoreMap() == null) {	
				Object courseID = item.getID();
//				AsyncCallback<List<Map<String,Object>>> getUserResultsCallback = new AsyncCallback<List<Map<String,Object>>>() {
//
//					@Override
//					public void onFailure(Throwable caught) {
//						GWT.log("failure", caught);
//					}
//
//					@Override
//					public void onSuccess(List<Map<String,Object>> result) {
//						Map<Object, Number> scoreMap = item.getScoreMap();
//						if(scoreMap == null) {
//							scoreMap = new HashMap<Object,Number>();
//							item.setScoreMap(scoreMap);
//						}
//						for( Map<String,Object> entry : result) {
//							Object id = entry.get("scoID");
//							Object score = entry.get("score");
//							if(score instanceof Number) {
//								scoreMap.put(id, (Number) score);
//							} else {
//								scoreMap.remove(id);
//							}
//						}
//						GWT.log("succes " + result);
//						view.render(item);
//					}
//				};
//				clientFactory.getRPCHandler().getUserResults(courseID, userID, getUserResultsCallback);
				
				Promise<DomResultsPerStudentCourse> p = clientFactory.getRPCHandler().getUserResults(courseID, userID);
				item.setPromisedScoreMap(
				p.map(new Function<DomResultsPerStudentCourse, Map<Object, Number>>() {

					@Override
					public Map<Object, Number> apply(DomResultsPerStudentCourse resolved) {
						Map<Object, Number> scoreMap = new HashMap<>();
						Collection<DomStudentScoContext> entries = resolved.getStudentScoContexts().values();
						for(DomStudentScoContext entry: entries) {
							Object key = PersistenceIdDecoderInterface.instance.idOf(entry.getScoID(), PersistenceClassType.PersistentScoContext);
							scoreMap.put(key, entry.getScore());
						}
						return scoreMap;
					}
				}).recover(new Function<Promise<?>, Map<Object,Number>>() {

					@Override
					public Map<Object, Number> apply(Promise<?> t) {
						GWT.log("failure", t.getFailure());
						return new HashMap<>();
					}
				}));
				
				
			}}
			item.getPromisedScoreMap().onResolve(
					new Runnable() {
							public void run() {
								view.render(item);
							}
					}
			);
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
