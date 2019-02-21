package nl.uu.fi.dwo.mobile.client.ui.views;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.Actions;
import nl.uu.fi.dwo.mobile.client.ui.MessageEvent;
import nl.uu.fi.dwo.mobile.client.ui.MessageEventHandler;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

@Singleton
public class HeaderViewNone extends HTML implements HeaderView, MessageEventHandler {

  private static final String SEARCH = "SEARCH:";
  @Inject
  public HeaderViewNone(EventBus eventBus) {
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
  private GotoController presenter;
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
  this.presenter = presenter;
}

@Override
public void onMessage(MessageEvent event) {
  String message = event.getMessage();
  if (Actions.ARROWUP.name().equals(message)) {
    presenter.goTo(upPlace);
  } else if (message.startsWith(SEARCH)) {
    message = message.substring(SEARCH.length());
    JSONValue dom = JSONParser.parseStrict(message);
    message = dom.isObject().get("input").isString().stringValue();
    presenter.goTo(HeaderViewNumworx.computeSearch(message, Text.constants));
  }
  
}

}
