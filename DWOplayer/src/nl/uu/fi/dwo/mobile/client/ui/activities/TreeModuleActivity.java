package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.inject.Inject;
import javax.inject.Provider;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.ui.Actions;
import nl.uu.fi.dwo.mobile.client.ui.IdleDetect;
import nl.uu.fi.dwo.mobile.client.ui.IdleDetect.IdleEvent;
import nl.uu.fi.dwo.mobile.client.ui.IdleDetect.IdleHandler;
import nl.uu.fi.dwo.mobile.client.ui.MessageEvent;
import nl.uu.fi.dwo.mobile.client.ui.MessageEventHandler;
import nl.uu.fi.dwo.mobile.client.ui.NeedLogin;
import nl.uu.fi.dwo.mobile.client.ui.NeedLoginEvent;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;
import nl.uu.fi.dwo.mobile.client.ui.WaitScreen;
import nl.uu.fi.dwo.mobile.client.ui.places.HasBack;
import nl.uu.fi.dwo.mobile.client.ui.places.MaybeLogout;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.xc;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;
import nl.uu.fi.dwo.mobile.client.ui.views.GotoController;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerStudentCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import dagger.Lazy;
import dagger.MembersInjector;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;

public class TreeModuleActivity extends AbstractActivity implements GotoController, MessageEventHandler, IdleHandler
{

	@Inject PlaceController placeController;
	@Inject DwoGlobalVars vars;
	@Inject RPCHandler rpc;
	@Inject TrafficAgent agent;
	@Inject Provider<TreeModuleView> treeModuleView;
	@SuppressWarnings("rawtypes")
	@Inject NeedLogin OOPS;
	@Inject Lazy<LastActivity> last;
	
	private List<SelectModuleItem> currentModel;
	private TreeModuleView view;
	private SelectModuleItem item;

	public TreeModuleActivity(MembersInjector<TreeModuleActivity> injector, SelectModuleItem i)
	{
		injector.injectMembers(this);
		this.item = i;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		view = treeModuleView.get();
		
		RoleType role = vars.getRoleType();
		boolean beheerder = 
		    role == RoleType.TEACHER || 
		    role == RoleType.SCHOOLADMIN || 
		    role == RoleType.STUDENT;
        view.setBeheer(beheerder);
 
        if(beheerder) {
    	  eventBus.addHandler(NeedLoginEvent.TYPE, OOPS);
          eventBus.addHandler(MessageEvent.TYPE, this);
          onMessage(MessageEvent.getLastEvent());          
          eventBus.addHandler(IdleDetect.TYPE, this);
        }
		
		if(item.getType() == Type.MODULE && vars.withUser()) {
			SelectModuleItem t = item;
			if (t.getCourseType() == CourseType.invisible) t = t.getParent();
			last.get().putPlace(t.getPlace());
			Object userID = vars.getUserID();
		if(userID != null && item.getPromisedScoreMap() == null) {
			Promise<DomResultsPerStudentCourse> p = rpc.getUserResults(item.getID(), userID);
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
			})
			.recoverWith(OOPS).recover(new Function<Promise<?>, Map<Object,Number>>() {

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
		agent.barrier().then(new Success<Void, Map<Object,Number>>() {

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
						if (item == SelectModuleItem.ROOT && Actions.isAvailable()) Actions.INITED.execute();
						
						Place where = item.getPlace();
						if (where instanceof xc) {
							String token = ((xc) where).getToken();
							String[] split = token.split("\\.");
							item.setPlace(where = new TreeModulePlace(split[0]));
							last.get().putPlace(item.getPlace());
							int sconr = Integer.parseInt(split[1]);
// FIXME getChildrenAsync kan null zijn, dan eerst vullen, zie ...
							if (item.getChildrenAsync() == null) {
								Promise<List<SelectModuleItem>> promise = rpc.getScos(item.getID())
										.map(new SCO_TO_MODULEITEM(item)).recoverWith(OOPS);
								item.setChildrenAsync(promise);
							}
							
							item.getChildrenAsync().then(p -> {
								List<SelectModuleItem> list = p.getValue();
								SelectModuleItem item = list.get(sconr-1);
								ViewModulePlace place = new ViewModulePlace(item.getID(), split[2]);
								goTo(place); // AST?
								return p;
							});
						}						
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
		if (place instanceof HasBack) {
			((HasBack) place).setBack(item);
		}
		placeController.goTo(place);
	}

  @Override
  public void onMessage(MessageEvent event) {
      String message = event.getMessage();
      if(Actions.showMainNav.getCommand().equals(message))
          view.showIcon(false);
      if(Actions.hideMainNav.getCommand().equals(message))
        view.showIcon(true);
    
  }

  @Override
  public void onIdle(IdleEvent ev) {
    if (ev.isSlow()) {
      if (Actions.isAvailable())
        Actions.MAYBELOGOUT.execute();    
      else
        goTo(new MaybeLogout());
    }
  }

}
