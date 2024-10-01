package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class HeaderViewSEB extends Composite implements HeaderView, ValueChangeHandler<String> {

	private InlineLabel label;
	private Button logout;
	private EventBus bus;
	private PlaceController controller;
	
	final static int HEIGHT = 40;
	
	@Inject HeaderViewSEB(PlaceController controller, EventBus bus, PlaceHistoryMapper mapper) {
		FlowPanel flow = new FlowPanel();
		logout = new Button("logoff");
		label = new InlineLabel(location());
		flow.add(logout);
		flow.add(label);
		flow.setPixelSize(-1, HEIGHT);
		initWidget(flow);
		History.addValueChangeHandler(this);
		presenter = controller::goTo; // never null!
		
		logout.addClickHandler(ev -> { presenter.goTo(new LoginPlace());});
		bus.addHandler(PlaceChangeEvent.TYPE, ev -> setText("P ",mapper.getToken(ev.getNewPlace())));
	}

	protected String location() {
		return Window.Location.createUrlBuilder().buildString();
	}

	void setText(String prefix, Object t) {
		label.setText(prefix + location() + " " + Objects.toString(t, "#"));
	}
	
	@Override
	public void setUserAndRole(DomUserFull currentUser, RoleType roleType) {
	}

	private Place homePlace, upPlace;
	private NavigationView navigation;
	private Optional<NavigationMenu> menu;
	
	@Override
	public Place getHomePlace() {
		return homePlace;
	}

	@Override
	public Place getUpPlace() {
		return upPlace;
	}

	@Override
	public void setUpPlace(Place homePlace) {
		this.upPlace = homePlace;
	}

	@Override
	public void setHomePlace(Place homePlace) {
		this.homePlace = homePlace;
	}

	@Override
	public void hide() {
	    LayoutPanel p = RootLayoutPanel.get(); // parent of header
	    p.setWidgetVisible(this, true);
	    navigation.hide();
	    p.setWidgetTopBottom(root, HEIGHT, Unit.PX, 0, Unit.PX);     
	    p.setWidgetTopBottom(navigation, HEIGHT, Unit.PX, 0, Unit.PX);
	    p.setWidgetTopHeight(this, 0, Unit.PX, HEIGHT, Unit.PX);
	    logout.setVisible(false);
	}

	@Override
	public void show() {
	    LayoutPanel p = RootLayoutPanel.get(); // parent of header
	    p.setWidgetVisible(this, true);
	    p.setWidgetTopBottom(root, HEIGHT, Unit.PX, 0, Unit.PX);     
	    p.setWidgetTopBottom(navigation, HEIGHT, Unit.PX, 0, Unit.PX);
	    p.setWidgetTopHeight(this, 0, Unit.PX, HEIGHT, Unit.PX);
	    navigation.show();
	    logout.setVisible(true);
	}

	@Override
	public void setDisplay(Widget display, NavigationView navigationView, Optional<NavigationMenu> menu) {
		root = display;
		this.navigation = navigationView;
		this.menu = menu;
		RootLayoutPanel.get().setWidgetTopHeight(this, 0, Unit.PX, 100, Unit.PX);
		if (menu.isPresent()) {
			RootLayoutPanel.get().setWidgetVisible(menu.get(), false);
		}
	}

	@Override
	public void setPresenter(GotoController presenter) {
		if (presenter == null) {
			presenter = controller::goTo;
		}
		this.presenter = presenter;

	}

	private GotoController presenter;
	@Override
	public GotoController getPresenter() {
		return presenter;
	}

	@Override
	public void setTrail(List<SelectModuleItem> trail) {
	}

	private Widget root;
	@Override
	public Widget getDisplay() {
		return root;
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		setText("H ",event.getValue());
	}

}
