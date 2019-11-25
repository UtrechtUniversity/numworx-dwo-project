package nl.uu.fi.dwo.mobile.client.ui.views;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Reusable;

@Reusable
public class MaybeLogoutView extends Composite {

  private static MaybeLogoutViewUiBinder uiBinder = GWT.create(MaybeLogoutViewUiBinder.class);

  interface MaybeLogoutViewUiBinder extends UiBinder<Widget, MaybeLogoutView> {}

  private EventBus bus;

  @Inject MaybeLogoutView(EventBus bus) {
    this.bus = bus;
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiHandler("btn") void onBtn(ClickEvent ev) {
    bus.fireEventFromSource(ev, this);
  }
  
}
