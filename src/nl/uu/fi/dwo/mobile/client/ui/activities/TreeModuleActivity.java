package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.SecureMode;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.WaitScreen;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerStudentCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;

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
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		if(item.getCourseType() == CourseType.assesment)
		{
			if(SecureMode.NORMAL == DWOplayer.PARAMETERS.getSecureMode()) {
				Widget w = new HTML(
						"<h1>Dit is een toets</h1>"
						+ "Ga naar de <a href='/toets/'>beveiligde toets omgeving</a>"
						+ " als je deze toets wilt maken"
						);
				panel.setWidget(w);
				return;
			} else {
				// ask password 
			}
		}
		
		
		view = clientFactory.getTreeModuleView();
		//if(true)
		view.setMenuWidget(clientFactory.getMenuWidget());
		if(item.getType() == Type.MODULE && DWOplayer.withUser()) {
			Object userID = DWOplayer.clientfactory.getUserID();
		if(userID != null && item.getPromisedScoreMap() == null) {
			Promise<DomResultsPerStudentCourse> p = clientFactory.getRPCHandler().getUserResults(item.getID(), userID);
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
					java.util.logging.Logger.getLogger("TreeModuleActivity").log(Level.WARNING, "failure", t.getFailure());
					return new HashMap<>();
				}
			}));

		}}

		if(item.getPromisedScoreMap() == null) {
			Map<Object, Number> value = new HashMap<Object,Number>();
			item.setPromisedScoreMap(Promises.resolved(value));
		}
		WaitScreen.instance().w();
		currentModel = SelectModuleItemHolder.getItems();
		view.setPresenter(TreeModuleActivity.this);
		view.render(currentModel);
		clientFactory.barrier().then(new Success<Void, Map<Object,Number>>() {

			@Override
			public Promise<Map<Object, Number>> call(Promise<Void> resolved) throws Exception {
				return item.getPromisedScoreMap();			
			}})
		.fallbackTo(item.getPromisedScoreMap())
		.onResolve(
				new Runnable() {
					public void run() {
						view.selectModule(item);
						WaitScreen.instance().hide();
						panel.setWidget(view);
					}
				}
			);
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
