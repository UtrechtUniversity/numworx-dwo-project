package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.Actions;
import nl.uu.fi.dwo.mobile.client.ui.MessageEvent;
import nl.uu.fi.dwo.mobile.client.ui.MessageEventHandler;
import nl.uu.fi.dwo.mobile.client.ui.NeedLogin;
import nl.uu.fi.dwo.mobile.client.ui.NeedLoginEvent;
import nl.uu.fi.dwo.mobile.client.ui.NeedLoginHandler;
import nl.uu.fi.dwo.mobile.client.ui.PageTracker;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.HasBack;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.xs;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.EmptyView;
import nl.uu.fi.dwo.mobile.client.ui.views.GotoController;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.MessageDialog;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckButton;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

import dagger.Lazy;
import dagger.MembersInjector;
import fi.dwo.gwt.lib.rest.ui.IdleDetect;
import fi.dwo.gwt.lib.rest.ui.IdleDetect.IdleEvent;
import fi.dwo.gwt.lib.rest.ui.IdleDetect.IdleHandler;

/**
 * Display module activity
 * 
 * @author Danny Hendrix
 * 
 */
public class ViewModuleActivity extends AbstractActivity implements AnchorContext, ViewModuleView.Presenter, 
  CBookEventListener, MessageEventHandler, GotoController, IdleHandler, NeedLoginHandler
{
	@Inject Provider<ViewModuleView> clientFactory;
	@Inject DwoGlobalVars vars;
	@Inject PlaceController placeController;
	@Inject OnCloseDelegate onClose;
	@Inject RPCHandler rpc;
	@Inject HeaderView headerView;
	@Inject PlaceHistoryMapper mapper;
	@SuppressWarnings("rawtypes")
	@Inject NeedLogin oops;
	@Inject @Named("profile") int profile;
	@Inject Lazy<LastExamActivity> lastExam;
	@Inject PageTracker tracker;

	private DWOplayerParameters PARAMETERS;
	@Inject void setParameters(DWOplayerParameters p) {
		PARAMETERS = p;
		isSEB = p.inExam();
		EXIT_AFTER = isSEB ? new LoginPlace() : SelectModuleItem.ROOT.getPlace();
	}

	private ViewModuleView view;
	private AnchorContext defaultContext;
	SelectModuleItem sco;
	private Timer tm;
	boolean started;
	private String location, hash;
	private ViewModulePlace where;
	
	@Override
	public String mayStop() {
	    if (view != null) {
	    	OpdrNavIF opdrNav = view.getOpdrNav();
	    	if (opdrNav == null) return super.mayStop(); // komt voor als je een Premium activiteit start als standaard school
			opdrNav.setChanged(false);			
	    }
		if (started && vars.withUser() && vars.getRoleType() == RoleType.STUDENT && onClose.isOnClose())
			return Text.constants.maybe_lost_data();
		return super.mayStop();
	}

	public ViewModuleActivity(MembersInjector<ViewModuleActivity> injector, SelectModuleItem sco, ViewModulePlace where)
	{
		injector.injectMembers(this);
		this.sco = sco;
		this.where = where;
		this.location = where.getLocation();
		this.hash = where.getHash();
	}
	
	final static private long BEFORE_AFTER = 30000L;
	final static private long PREPARE_AFTER = BEFORE_AFTER + 5*60000L; // 5 minuten voor tijd.
	boolean isSEB;
	private Place EXIT_AFTER;
	
	private boolean setNotAfter(final AcceptsOneWidget panel) {
		Date notAfter = sco.getNotAfter();
		final Text rb = Text.constants;
		//final Date notAfter = new Date(System.currentTimeMillis()+10000L+ PREPARE_AFTER);

		if(notAfter != null && notAfter.getTime() < System.currentTimeMillis() + DWOplayer.timezone)
		{
			started = false;
			Widget widget = new EmptyView();
			panel.setWidget(widget);
			Promise<Integer> p = MessageDialog.alert(rb.sco_expired());
			p.then( x -> {
				goTo(EXIT_AFTER);
				return x;
			});
			view = null;
			return true;
		} else if (notAfter != null) {
			long timeToGo = notAfter.getTime()-System.currentTimeMillis() - DWOplayer.timezone;
			timeToGo = Math.min(timeToGo, Integer.MAX_VALUE);
			timeToGo = Math.max(timeToGo,1);
			tm = new Timer() {

			@Override
			public void run() {
				commitView();
				tm = null;
				started = false;
				Promise<Integer> p = MessageDialog.alert(rb.sco_expired());
				p.then( x -> {
					goTo(EXIT_AFTER);
					return x;
				});
			}};

			if (timeToGo > PREPARE_AFTER) {
				timeToGo -= PREPARE_AFTER;
				timeToGo = Math.max(1, timeToGo);
				final Timer oldTimer = tm;
				tm = new Timer() {

					@Override
					public void run() {
						commitView();
						final Timer deze = this;
						Promise<Date> p;
						if (isSEB) {
						  p = 
						  rpc.refreshExam().then(pr -> { 
							JSONString jwt = pr.getValue().isString();
							if (jwt != null) {
								String[] split = jwt.stringValue().split(" ");
								if (split.length >= 3) {
									long after = Long.parseLong(split[2]);
									sco.setNotAfter(new Date(after*1000));
								}
							}						
							return Promises.resolved(sco.getNotAfter());
						});
						} else {
							p = Promises.resolved(sco.getNotAfter());
							if ( vars.getCurrentSchoolClass() != null) {
								final Promise<Date> p0 = p;
								p = rpc.getCourseClass(sco.getParentID(), vars.getCurrentSchoolClass()).
								filter(pr-> !pr.getClassCourses().isEmpty()).
								then(pr -> { 
									DomClassCourse cc = pr.getValue().getClassCourses().get(0).getValue();
									sco.setNotAfter(cc.getNotAfter());
									return Promises.resolved(cc.getNotAfter());
								}).recoverWith(f -> p0);
							}
						}
						
						
						
						//Promise<Integer> iiiii = 
						p.then( (Promise<Date>pr) -> {
							Date notAfter = pr.getValue();
							long timeToGo = notAfter.getTime()-System.currentTimeMillis() - DWOplayer.timezone - BEFORE_AFTER;
							timeToGo = Math.max(1,  timeToGo);
							if (timeToGo > PREPARE_AFTER) {
								timeToGo -= PREPARE_AFTER;
								deze.schedule((int) timeToGo);
								return null;
							}
							
							MessageDialog dialog = new MessageDialog();
							dialog.addLine(new Label(rb.sco_almost_expired()));
							dialog.addOk();
							tm = new Timer() {

								@Override
								public void run() {
									dialog.close();
									oldTimer.run();
								}
								
							};
							tm.schedule((int)timeToGo);
							return dialog.showDialog();
						});
						

					}					
					
				};
				
				
				
			} else {
				timeToGo = (Math.max(1, timeToGo - BEFORE_AFTER)); // 30 seconden marge.
			}
			tm.schedule((int)timeToGo); 
		}
		return false;
		
	}
	
	HandlerRegistration registration;
	
	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus)
	{
		if (setNotAfter(panel)) return;			
// All systems go
        view = clientFactory.get();
		eventBus.addHandler(MessageEvent.TYPE, this);
		registration = eventBus.addHandler(NeedLoginEvent.TYPE, this);
		onMessage(MessageEvent.getLastEvent());

		started = true;
		eventBus.addHandler(CBookEvent.TYPE, this);
		eventBus.addHandler(IdleDetect.TYPE, this);
		headerView.hide();
		panel.setWidget(view); // terug naar af. problemen met gekke scrolls
		{
			vars.scoreCache().ifPresent(sc -> sc.init(sco.original()));
			final String id = sco.getID().toString();
			DWOplayer.insertCSS(id);
			List<SelectModuleItem> trail = new ArrayList<SelectModuleItem>();
			SelectModuleItem parent = sco.getParent();
			boolean profilecheck = !profileCheck(); // profile/test/premium?
			while(parent != null) {
				if (profilecheck || parent.getCourseType() != CourseType.invisible)
					trail.add(parent);
				parent = parent.getParent();
			}
			setTrail(trail);
			view.setTitle(sco.getName());
			Window.setTitle(sco.getName());
			view.setScoType(sco.getScoType());
			view.setPresenter(this);
			headerView.setPresenter(this);
			defaultContext = view.getAnchorContext();
			view.setAnchorContext(this);
			view.setUnitId(id);
			PersistenceId modelid = sco.getStudentModelId();

			if(modelid != null) {
				view.setModel(Promises.resolved(new DomStudentModelContextId(modelid)));
			} else
				view.setModel(null);
			
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Logger.getLogger("ViewModuleActivity").log(Level.SEVERE, "initialize()", caught);
				//Window.alert(caught.getMessage());
				started = false;
				view = null;
				if (!oops.needed(oops.apply(Promises.failed(caught)))) tracker.back();
			}

			@Override
			public void onSuccess(Void result) {
				if(location != null) {
					view.setLocation(location);
					Window.setTitle(sco.getName() + " - " + location);
				} else {
					location = view.getApi().GetValue(Memento.LOCATION);
					if (location == null||location.isEmpty()) location = "0";
					where.setLocation(location);
					sco.setPlace(where);
					Window.setTitle(sco.getName() + " - " + location);
					tracker.replaceItem(mapper.getToken(where), false);	
				}
					started = !Memento.COMPLETED.equals(view.getApi().GetValue(Memento.COMPLETION_STATUS));
					view.setupModule(sco.getName(), PARAMETERS.getLaunchData() + sco.getID())
					.then(p -> {
						if (p.getValue()) {
							Window.alert("Error: need a Premium subscription");
							started = false;
							Place back = headerView.getUpPlace();
							if (back != null) goTo(back); else
							tracker.back();
						}
						return null;
					}, p -> { started = false; if (!oops.needed(p))tracker.back(); })
					.onResolve(() -> {
						if (started && hash != null) {
							gotoElement(hash);
						}
					})
					
					;
			}
		};
		view.getApi().Initialize(callback);
		}
	}

	protected void setTrail(List<SelectModuleItem> trail) {
		view.setTrail(trail);
		headerView.setTrail(trail);
	}

	private boolean profileCheck() {
		Promise<DomDwoProfileFull> dwoProfile = rpc.getDwoProfile();
		return profile == 111 ||  // INFORMATICA
			dwoProfile.isDone() && 
			dwoProfile.getFailure() == null &&
			dwoProfile.getValue().getDwoProfileRights().contains("v");
	}

	private boolean setNotAfter_org(final AcceptsOneWidget panel) {
		// Eindtijd
		// History.back is terug naar waar je vandaan komt, werkt niet!
				Date notAfter = sco.getNotAfter();
				if(notAfter != null && notAfter.getTime() < System.currentTimeMillis() + DWOplayer.timezone)
				{
					started = false;
					panel.setWidget(new Label("Activiteit verlopen"));
					tracker.back();
					view = null;
					return true;
				} else if (notAfter != null) {
					long timeToGo = notAfter.getTime()-System.currentTimeMillis() - DWOplayer.timezone;
					timeToGo = Math.min(timeToGo, Integer.MAX_VALUE);
					timeToGo = Math.max(timeToGo,1);
					tm = new Timer() {
		
						@Override
						public void run() {
							tm = null;
							started = false;
							tracker.back();
						}};
					tm.schedule((int)timeToGo); 
				}
				return false;
	}

	@Override
	public void onStop() {
	    headerView.setTrail(null);
		headerView.setPresenter(placeController::goTo);
		if (tm != null) {
			tm.cancel();
			tm = null;
		}
		if(view != null) {
			view.setAnchorContext(defaultContext); // unwrap
			view.close();
			sco.setScore(view.getScoreRaw());
		}
		super.onStop();
	}
	
	@Override
	public void onCancel() {
		started = false;
		if(tm!=null) {
			tm.cancel();
			tm=null;
		}
		if(view != null) {
			view.setAnchorContext(defaultContext); // unwrap
		}
		super.onCancel();
	}

	@Override
	public void prepareLeave() {
		started = false;
	}
			
	@Override
	public void addElement(String anchor, Element e) {
		defaultContext.addElement(anchor, e);
	}

	@Override
	public void gotoElement(String anchor) {
		defaultContext.gotoElement(anchor);
	}

	@Override
	public void gotoUrl(final String href) {
		if (href.startsWith("anchor:")) {
			gotoElement(href.substring(7));
			gotoPage(where.getLocation(), href.substring(7) );
		} else
		if("goto:0".equals(href)) {
			started = false;
			//History.back(); // FIXME Niet meer goed als je goto gebruikt.
			if (isSEB)
				goTo(new LoginPlace());
			else
			{
				if (sco.getParent() == null)
					tracker.back();
				else
					goTo(sco.getParent().getPlace());
			}
		}
		else if(href.startsWith("goto:.")) defaultContext.gotoUrl(href);
		else if(href.startsWith("goto:")){
			final SelectModuleItem parent = sco.getParent();
			if (parent == null) return;
			// FIXME getChildrenAsync kan null zijn, dan eerst vullen, zie ...
			if (parent.getChildrenAsync() == null) {
				Promise<List<SelectModuleItem>> promise = rpc.getScos(parent.getID())
						.map(new SCO_TO_MODULEITEM(parent)).recoverWith(oops);
				parent.setChildrenAsync(promise);
			}
			Runnable run = new Runnable() {
				public void run() {
					String[] split = href.split("#", 2);
					String href1 = split[0].substring(5);
					final int page = href1.lastIndexOf('.');
		            String location = null;
					if (page >= 0) {
			          location = Integer.toString(Integer.parseInt(href1.substring(page+1))-1);
					  href1 = href1.substring(0, page);
					}

					
					
					int sconr = -1;
					try {
						sconr = Integer.parseInt(href1)-1;
					} catch(Exception e) {}
				
					List<SelectModuleItem> list = parent.getChildren();
					if (sconr <= -1 || sconr >= list.size()) {
						for (sconr = 0; sconr < list.size(); sconr++) {
							if (list.get(sconr).getName().startsWith(href1))
								break; // found by prefix
						}
					}
					if (sconr == list.size()) {
						sconr = Integer.parseInt(href1) - 1;
					}
					SelectModuleItem item = list.get(sconr);
					Object scoid = item.getID();
					if (item != sco) {
						String hash = null; if (split.length == 2) hash = split[1];
						goTo(new ViewModulePlace(scoid, location, hash));
					} else if (page >= 0) {
						String hash = "";
						if (split.length == 2) hash = "#" + split[1];
						defaultContext.gotoUrl("goto:." + (Integer.parseInt(location) + 1) + hash);
					}
				}
			};
			parent.getChildrenAsync().onResolve(run);
		}
	}

	@Inject @Named("defaultPlace") Place defaultPlace;
	@Override
	public void gotoPlace(String token) {
		Place place = mapper.getPlace(token);
		if (place==null) place = defaultPlace;
		if (place instanceof HasBack) {
			SelectModuleItem item;
			Place x = headerView.getUpPlace();
			String id = mapper.getToken(x);
			id = id.split(":",2)[1]; // has ':'
			item = SelectModuleItemHolder.getItemByID(id);
			((HasBack) place).setBack(item);
		} else if (place instanceof xs) {
			((xs) place).setBack(sco);
		}
		goTo(place);
	}

	@Override
	public void goTo(Place place) {
		started = false;
		placeController.goTo(place);
	}

  @Override
  public void acceptCBookEvent(CBookEvent event) {
    if (CheckButton.ACTION_NEXT_PAGE.equals(event.getCommand()))
    {
      if ( !view.nextPageAction())  {
        SelectModuleItem parent = sco.getParent();
        List<SelectModuleItem> list = parent.getChildren();
        String url;
        int index = list.indexOf(sco);
        if (index == list.size()-1) {
            url = "goto:0";
        } else {
            url = "goto:" + (index+2) + ".1";
        }
        gotoUrl(url);
      }
    }
    
  }

  @Override
  public void onMessage(MessageEvent event) {
    String message = event.getMessage();
    if (Actions.CLOSING.getCommand().equals(message)) {
    	commitView();
    }
  }

  @Override
  public void onIdle(IdleEvent ev) {   
    OpdrNavIF opdrNav = view == null ? null : view.getOpdrNav(); // komt soms te vroeg, en dan heb je nog geen opdrnav
    if (opdrNav != null 
    		&& opdrNav.getLessonMode() == LessonMode.normal
    		&& (opdrNav.getMode() == OpdrNavIF.ZELFTOETS || opdrNav.getMode() == OpdrNavIF.EINDTOETS || sco.isExam() ))
      opdrNav.setChanged(false); 
    if (ev.isSlow() && !sco.isExam()) // no timeout bij exams
    {
      Place place = sco.getParent().getPlace();
      goTo(place);
    }
  }

private void commitView() {
	if (view != null)
	{
		OpdrNavIF opdrNav = view.getOpdrNav();
		if (opdrNav != null) opdrNav.setChanged(false);
		else {
			GWT.log("opdr nav null");
		}
	}
}

@Override
public void onNeedLogin(NeedLoginEvent ev) {
	// if (view) view.abort????
	view = null;
	started = false;
	registration.removeHandler();
	oops.onNeedLogin(ev);
}

	//boolean magterug = view.magterug(); // nog bepalen wanneer je deze op false moet zetten. false is de veilige waarde!
	// FIXME Er zijn activiteiten zonder "TERUG NAAR VORIGE PAGINA";

	@Override
	public void gotoPage(String location, String hash) {
		if (Objects.equals(location, where.getLocation()) && Objects.equals(hash, where.getHash()) )
			return; // same same, do nothing.
		where.setLocation(location);
		where.setHash(hash);
		String token = mapper.getToken(where);
		if (view.magterug())
			tracker.newItem(token, false);
		else 
			tracker.replaceItem(token, false);
		Window.setTitle(sco.getName() + " - " + location);
//		fire(token);
	}

//	private void fire(String token) {
//		if (headerView instanceof HeaderViewSEB) {
//			((HeaderViewSEB) headerView).onHistoryChange(token);
//		}
//		
//	}
	
}
