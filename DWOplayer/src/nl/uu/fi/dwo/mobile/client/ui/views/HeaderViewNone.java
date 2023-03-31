package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.SecureMode;
import nl.uu.fi.dwo.mobile.client.ui.Actions;
import nl.uu.fi.dwo.mobile.client.ui.MessageEvent;
import nl.uu.fi.dwo.mobile.client.ui.MessageEventHandler;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;
import nl.uu.fi.dwo.mobile.client.ui.places.LogoutPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

@Singleton
public class HeaderViewNone extends HTML implements HeaderView, MessageEventHandler {

  final Logger LOG = Logger.getLogger(getClass().getName());
  private static final String SEARCH = "SEARCH:";
  private static final String GOTO = "GOTO:";
  private TrafficAgent agent;
  private DWOplayerParameters PARAMETERS; 
  
  @Inject
  public HeaderViewNone(EventBus eventBus, PlaceHistoryMapper mapper, PlaceController controller, TrafficAgent a, DWOplayerParameters p) {
    this.mapper = mapper;
    this.controller = controller;
    this.agent = a;
    this.PARAMETERS = p;
    eventBus.addHandler(MessageEvent.TYPE, this);
  }

  @Override
  public void setUserAndRole(DomUserFull currentUser, RoleType roleType) {
  }

  private Place homePlace  = new TreeModulePlace();
  @Override
  public Place getHomePlace() {
    return homePlace;
  }

  @Override
  public void setUpPlace(Place upPlace) {
    this.upPlace = upPlace;
  }

  @Override
  public void setHomePlace(Place homePlace) {
    this.homePlace = homePlace;
  }

  private Widget root;
  private NavigationView navigation;
  private PlaceController controller;
  private GotoController presenter = controller::goTo;
  private Place upPlace = homePlace;
  
  @Override
  public void hide() {
      //Actions.hideMainNav.execute();
      LayoutPanel p = RootLayoutPanel.get(); // parent of header
      p.setWidgetVisible(this, false);
      navigation.hide();
      p.setWidgetTopBottom(root, -50, Unit.PX, 0, Unit.PX);     
      p.setWidgetTopBottom(navigation, 0, Unit.PX, 0, Unit.PX);
  }

  @Override
  public void show() {
      LayoutPanel p = RootLayoutPanel.get(); // parent of header
      p.setWidgetVisible(this, false);
      p.setWidgetTopBottom(root, 0, Unit.PX, 0, Unit.PX);
      p.setWidgetTopBottom(navigation, 0, Unit.PX, 0, Unit.PX);
      navigation.show();
  }

  @Override
  public void setDisplay(Widget display, NavigationView navigationView) {
    root = display;
    navigation = navigationView;

  }

@Override
public void setPresenter(GotoController presenter) {
  if (presenter == null) presenter = controller::goTo; // never null
  this.presenter = presenter;
}

private PlaceHistoryMapper mapper;

@Override
public void onMessage(MessageEvent event) {
  String message = event.getMessage();
  if (Actions.ARROWUP.name().equals(message)) {
    presenter.goTo(upPlace);
  } else
//	  if (message.startsWith(SEARCH)) {
//    message = message.substring(SEARCH.length());
//    JSONValue dom = JSONParser.parseStrict(message);
//    message = dom.isObject().get("input").isString().stringValue();
//    presenter.goTo(HeaderViewNumworx.computeSearch(message, Text.constants));
//  } else 
	  if (message.startsWith(GOTO)) {
		  LOG.info("found goto " + message);
		  message = message.substring(GOTO.length());
		  Place place = mapper.getPlace(message);
		  LOG.info("place = " + place);
		  if (place != null) {
	    	presenter.goTo(place);
		  }
		  else presenter.goTo(getHomePlace());
	  } else if (Actions.LOGOUT.name().equals(message)) {
	      LOG.info("logout received");
		  presenter.goTo(LogoutPlace.INSTANCE);
		  LOG.info("wait");
		  agent.barrier().onResolve(() -> { LOG.info("execute");Actions.LOGOUT.execute();});
		  LOG.info("waiting");
	  } 
}

public void setTrail(List<SelectModuleItem> trail) {
  if (!Actions.isAvailable()) return;
  if (trail == null) {
    Actions.TRAIL.execute();
    return;
  }
  if(PARAMETERS.getSecureMode() == SecureMode.SEB)
      trail.clear();
  JSONArray array = new JSONArray();
  ListIterator<SelectModuleItem> iter = trail.listIterator(Math.min(trail.size(),3));
  while (iter.hasPrevious()) {
      SelectModuleItem selectModuleItem = (SelectModuleItem) iter.previous();
      String title = selectModuleItem.getName();
      final Place place = selectModuleItem.getPlace();
      String command = "GOTO:" + mapper.getToken(place);
      JSONObject obj = new JSONObject();
      obj.put("title", new JSONString(title));
      obj.put("command", new JSONString(command));
      array.set(array.size(), obj);
  }
  Actions.TRAIL.execute(array.toString());
  
  Place upId;
  if(!trail.isEmpty())
      upId = trail.get(0).getPlace();
  else
      upId = new TreeModulePlace();
  setUpPlace(upId);
}


}
