package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

@Singleton
public class HeaderLessView extends HTML implements HeaderView {

  private PlaceController controller;
  private Place upPlace, homePlace;
  private Widget root;
  private NavigationView navigation;
  
  @Inject HeaderLessView(PlaceController controller) {
    this.controller = controller;
  }

  @Override
  public void setUserAndRole(DomUserFull currentUser, RoleType roleType) {
    // TODO Auto-generated method stub

  }

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

  @Override
  public void hide() {
    LayoutPanel p = RootLayoutPanel.get(); // parent of header
    p.setWidgetVisible(this, false);
    navigation.hide();
    p.setWidgetTopBottom(root, 0, Unit.PX, 0, Unit.PX);     
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
    this.root = display;
    this.navigation = navigationView;
  }

  @Override
  public void setPresenter(GotoController presenter) {
    if (presenter == null) 
      presenter = controller::goTo;

  }

  @Override
  public void setTrail(List<SelectModuleItem> trail) {
  }

}
