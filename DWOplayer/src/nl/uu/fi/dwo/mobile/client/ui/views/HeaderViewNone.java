package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
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
public class HeaderViewNone extends HTML implements HeaderView, MessageEventHandler, RequiresResize, ValueChangeHandler<Boolean> {

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

  public Place getUpPlace() {
	return upPlace;
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
  private Optional<NavigationMenu> menu;
  
  @Override
  public void hide() {
      LayoutPanel p = RootLayoutPanel.get(); // parent of header
      showNavigation = false;
      navigation.hide();
	  menu.ifPresent(m -> p.setWidgetVisible(m, false));
      p.setWidgetTopBottom(root, -50, Unit.PX, 0, Unit.PX);     
      p.setWidgetTopBottom(navigation, 0, Unit.PX, 0, Unit.PX);
  }

  @Override
  public void show() {
      LayoutPanel p = RootLayoutPanel.get(); // parent of header
	  menu.ifPresent(m -> p.setWidgetVisible(m, false));
      p.setWidgetTopBottom(root, 0, Unit.PX, 0, Unit.PX);
      p.setWidgetTopBottom(navigation, 0, Unit.PX, 0, Unit.PX);
      navigation.show(); showNavigation = true;
      onResize();
  }

  @Override
  public void setDisplay(Widget display, NavigationView navigationView, Optional<NavigationMenu> menu) {
    root = display;
    navigation = navigationView;
    this.menu = menu;
	if (menu.isPresent()) {
		RootLayoutPanel.get().setWidgetTopHeight(menu.get(), 0, Unit.PX, getMenuHeight(), Unit.PX );
		menu.get().addValueChangeHandler(this);
	}
	RootLayoutPanel.get().setWidgetVisible(this, false);
  }

	private int getMenuHeight() {
		if (menu.isPresent() ) return menu.get().getHeight();
		return 0;
	}

  
@Override
public void setPresenter(GotoController presenter) {
  if (presenter == null) presenter = controller::goTo; // never null
  this.presenter = presenter;
}

@Override
public GotoController getPresenter() {
	return presenter;
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
  if(PARAMETERS.inExam())
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

public Widget getDisplay() { return root; }

@Override
public void onValueChange(ValueChangeEvent<Boolean> event) {
	if (event.getValue()) {
		navigation.wide();
	} else {
		navigation.hide();
	}
}

final static private int MIN_WIDTH = 700;
boolean showNavigation = true; 

@Override
public void onResize() {
	int width = Window.getClientWidth();
	LOG.severe("headernone on resize  " + width);
	if (menu.isPresent() && showNavigation && width > 0) {
		NavigationMenu m = menu.get();
		RootLayoutPanel r = RootLayoutPanel.get();
		if (width < MIN_WIDTH) {
			if (!m.isVisible()) {
				m.setUpDown(false, false);
				navigation.hide();
				r.setWidgetVisible(m, true);
				r.setWidgetTopBottom(root, getMenuHeight(), Unit.PX, 0, Unit.PX);
				r.setWidgetTopBottom(navigation, getMenuHeight(), Unit.PX, 0, Unit.PX);			
			}
		} else if (m.isVisible()) {
			navigation.show();
			r.setWidgetVisible(m, false);
			r.setWidgetTopBottom(root, 0, Unit.PX, 0, Unit.PX);
			r.setWidgetTopBottom(navigation, 0, Unit.PX, 0, Unit.PX);			
		}
	}
	
}

@Override
public HeaderView install() {
	RootLayoutPanel.get().addStyleName("header-none");
	return HeaderView.super.install();
}

@Override
public void installTop(Widget w) {
	Roles.getHeadingRole().setAriaHiddenState(w.getElement(), true);
}

}
