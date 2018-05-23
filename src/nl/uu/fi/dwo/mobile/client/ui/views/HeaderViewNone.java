package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class HeaderViewNone extends HTML implements HeaderView {

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
  }

  @Override
  public void setHomePlace(Place homePlace) {
    this.homePlace = homePlace;
  }

  private Widget root;
  private NavigationView navigation;
  
  @Override
  public void hide() {
      LayoutPanel p = RootLayoutPanel.get(); // parent of header
      p.setWidgetVisible(this, false);
      navigation.show();
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

}
