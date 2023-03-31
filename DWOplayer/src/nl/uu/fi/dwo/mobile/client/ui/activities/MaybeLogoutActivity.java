package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import dagger.Lazy;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.IdleDetect;
import nl.uu.fi.dwo.mobile.client.ui.places.HasHash;
import nl.uu.fi.dwo.mobile.client.ui.places.MaybeLogout;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.MaybeLogoutView;

public class MaybeLogoutActivity extends AbstractActivity implements IdleDetect.IdleHandler, ClickHandler {

  private Place next, logout;
  private PlaceController controller;
  private IdleDetect idle;
  private HeaderView header;
  @Inject Lazy<MaybeLogoutView> view;
  
  @Inject MaybeLogoutActivity(PlaceController controller, @Named("defaultPlace") Place defaultPlace, IdleDetect idle, HeaderView header) {
    this.controller = controller;
    this.logout = defaultPlace;
    this.idle = idle;
    this.header = header;
    place(controller.getWhere());
  }
    
  public MaybeLogoutActivity place(Place place) {
    next = ((HasHash) place).getPlace();
    if (next == null) next = new TreeModulePlace();
    return this;
  }
  
  @Override
  public void start(AcceptsOneWidget panel, EventBus eventBus) {
    eventBus.addHandler(IdleDetect.TYPE, this);
    eventBus.addHandlerToSource(ClickEvent.getType(), view.get(), this);
    panel.setWidget(view.get());
    idle.reset();
    header.hide();
  }

  @Override
  public void onIdle(IdleDetect.IdleEvent ev) {
    if (ev.getCnt() == IdleDetect.FAST) {
      controller.goTo(logout);     
    }   
  }

  @Override
  public void onClick(ClickEvent event) {
    controller.goTo(next);
  }

}
