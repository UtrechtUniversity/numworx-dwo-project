/**
 * 
 */
package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import nl.uu.fi.dwo.account.client.DialogFailure;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.account.client.ProfileCommand;
import nl.uu.fi.dwo.account.client.SchoolClassStudentCommand;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.SecureMode;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.NeedLogin;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ReloginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SearchPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * @author peterboon
 *
 */
@Singleton
public class HeaderViewNumworx extends Composite implements HasText, Command, HeaderView, RequiresResize, ValueChangeHandler<Boolean>  {
	
	private static HeaderViewNumworxUiBinder uiBinder = GWT.create(HeaderViewNumworxUiBinder.class);

	static final class DialogFailureWithOops extends DialogFailure {
		private final NeedLogin oops;

		DialogFailureWithOops(EventBus bus, NeedLogin oops) {
			super(bus);
			this.oops = oops;
		}

		@Override
		public void fail(Promise<?> resolved) {
			if (oops.needed(resolved)) oops.apply(resolved);
			else super.fail(resolved);
		}
	}

	interface HeaderViewNumworxUiBinder extends UiBinder<Widget, HeaderViewNumworx> {
	}

	@UiField(provided=true) String pfx;
	@UiField Label loginLabel;
	@UiField Text rb;
	@UiField(provided=true) MenuItem user;
	/*@UiField*/ //TextBox searchInput;
	@UiField TreeModuleViewNumworxCss style;

	MenuBar items = new MenuBar(true);
	final private EventBus bus;
    final private DWOplayerParameters PARAMETERS;
    final private Optional<DwoGlobalVars> vars;
    @Inject Provider<Optional<XapiWrapper>> xapiprovider;
    boolean hasxapi;
    final private Failure failure;


    static final String account_svg = "<svg version='1.1' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' "+ 
    		" width='26px' height='26px' viewBox='0 0 64 64' >"+			
    		   	 "<path d='M56.9,0H7.1C3.2,0,0,3.2,0,7.1v49.8C0,60.8,3.2,64,7.1,64h49.8c3.9,0,7.1-3.2,7.1-7.1V7.1C64,3.2,60.8,0,56.9,0" +
    		   	 	"zM32.1,9.3c6,0,10.9,4.9,10.9,10.9c0,6-4.9,10.9-10.9,10.9c-6,0-10.9-4.9-10.9-10.9C21.2,14.1,26.1,9.3,32.1,9.3z M53.9,54.1H10.3"+
    		   	 	"v-3.6c0-7.3,14.5-10.9,21.8-10.9c7.3,0,21.8,3.6,21.8,10.9V54.1z'/></svg>";

    
	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	  *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	@Inject HeaderViewNumworx(EventBus bus, DWOplayerParameters PARAMETERS, Optional<DwoGlobalVars> vars, NeedLogin oops) {
		this.bus = bus;
		this.PARAMETERS = PARAMETERS;
		failure = new DialogFailureWithOops(bus, oops);
		pfx = PARAMETERS.getResource("");
        final int correctie = 10; // width popup 
		user = new MenuItem(account_svg, true, items) {
            @Override
            public int getAbsoluteLeft() {
                int w1 = items.getOffsetWidth();
                int w2 = this.getOffsetWidth();
                return super.getAbsoluteLeft() - w1 + w2 - correctie;
            }
		};
		initWidget(uiBinder.createAndBindUi(this));
//		String html = "<img width='26' height='26' " 
//				+" class='" + style.navIcon() + " profile-headerIcon'"
//				+" src='" + pfx
//				+ "images/numworx/account.svg' >";
//		
//		user.setHTML(html);
		this.vars = vars;
		
	}

	@Override
	public String getText() {
		return loginLabel.getText();
	}

	@Override
	public void setText(String text) {
		if(text == null) text = ""; // never null
		loginLabel.setText(text);
	}

	@Inject PlaceController controller;
	
	GotoController presenter = controller::goTo;
//	@UiHandler("homeBtn")
	void onHomeBtn(ClickEvent ev) {
		presenter.goTo(homePlace);
	}

	public void setPresenter(GotoController presenter) {
		if (presenter == null) presenter = controller::goTo; // never null
		this.presenter = presenter;
	}

	public GotoController getPresenter() {
		return presenter;
	}

	Place upPlace = new TreeModulePlace();
	Place homePlace = new TreeModulePlace();
	private Widget root;
	private NavigationView navigation;
	private Optional<NavigationMenu> menu;

	@UiHandler("upBtn")
	void onUpBtn(ClickEvent ev) {		
		presenter.goTo(upPlace);
	}

	public Place getUpPlace() {
		return upPlace;
	}

	public void setUpPlace(Place upPlace) {
		this.upPlace = upPlace;
	}

	public Place getHomePlace() {
		return homePlace;
	}

	public void setHomePlace(Place homePlace) {
		this.homePlace = homePlace;
	}

//	/*@UiHandler("searchBtn")*/
//	void onSearch(ClickEvent ev) {
//      String search = searchInput.getText().trim();
//		SearchPlace place = computeSearch(search, rb);
//    presenter.goTo(place);
//	}

  private static SearchPlace computeSearch(String search, Text rb) {
    long id = System.currentTimeMillis();
		SelectModuleItem item = SelectModuleItemHolder.getSearch(search);
		if(item == null)
		{
			item = new SelectModuleItem(id, SelectModuleItem.Type.SEARCH);
			item.setName(search);
			item.setDescription("Nog geen resultaat..."); // XXX wat komt hier....
// FIXME hier de zoek functie....
			Promise<List<SelectModuleItem>> searchMock = searchMock(search);			
			final SelectModuleItem i = item;
			item.setChildrenAsync(searchMock.then(new Success<List<SelectModuleItem>,List<SelectModuleItem>>(){

				@Override
				public Promise<List<SelectModuleItem>> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
					i.setDescription(rb.count_results() + resolved.getValue().size());
					return resolved;
				}})
				.recover(new Function<Promise<?>, List<SelectModuleItem>>(){

					@Override
					public List<SelectModuleItem> apply(Promise<?> t) {
						i.setDescription(rb.no_results() + t.getFailure().getMessage());
						return Collections.emptyList();
					}})	
					
					);
			SelectModuleItemHolder.insert(item);
		}
		SearchPlace place = new SearchPlace(item.getID());
    return place;
  }

	private static Promise<List<SelectModuleItem>> searchMock(String search) {
		search = search.toLowerCase();
		List<SelectModuleItem> list = new ArrayList<>();
		List<SelectModuleItem> items = SelectModuleItemHolder.getItems();
		search(search, items, list);
		return Promises.resolved(list);
	}
	
	private static void search(String search, List<SelectModuleItem> items, List<SelectModuleItem> list) {
		for(SelectModuleItem item: items) {
			String name = item.getName();
			String description = item.getDescription();
			if( (name + description).toLowerCase().contains(search)) {
				list.add(item);
			} else {
				Promise<List<SelectModuleItem>> p = item.getChildrenAsync();
				if(p != null && p.isDone() && p.getFailure() == null) {
					search(search, p.getValue(), list);
				}
			}
		}
	}

	public void setUserAndRole(DomUserFull currentUser, RoleType role) {
		boolean withUser = currentUser != null;
		String login = withUser? currentUser.getDisplayName() : null;
		setText(login);
		items.clearItems();
		MenuItem m;
		if(withUser) {
			if (!PARAMETERS.inExam())
			{	
				m=items.addItem(DwoLocalesForGWT.instance.GUI_MyProfile(), new ProfileCommand(bus, vars.get(), failure));
				m.addStyleName(style.menuItem());
	            if(role == RoleType.STUDENT) {
	                ScheduledCommand cmd = new SchoolClassStudentCommand(this, bus, vars.get(), failure);
	                m=items.addItem(DwoLocalesForGWT.instance.GUI_MySchoolClasses(), cmd);
	                m.addStyleName(style.menuItem());
	                
	                Optional<XapiWrapper> xapi = xapiprovider.get();
	                if (xapi.isPresent()) {
	                	items.addItem(xapi.get().getMenuItem());
	                	hasxapi = true;
	                	
	                }
	            }
			}
			boolean single = Boolean.TRUE.equals(currentUser.getSingleSchool());
			if(!single)
			{
				Command cmd = null;
				cmd = new nl.uu.fi.dwo.account.client.SchoolLoginCommand(this, bus, vars.get(), failure);			
				m=items.addItem(DwoLocalesForGWT.instance.GUI_MySchoolLogins(), cmd);
				m.addStyleName(style.menuItem());
			}

			m=items.addItem(rb.logout(), new ScheduledCommand() {
				
				@Override
				public void execute() {
					if (hasxapi) xapiprovider.get().ifPresent(XapiWrapper::destroy);
					LoginPlace place = new LoginPlace(homePlace);
					presenter.goTo(place);					
				}
			});
		} else {
			m=items.addItem(rb.aanmelden(), new ScheduledCommand() {
				
				@Override
				public void execute() {
					presenter.goTo(new LoginPlace(homePlace));
				}
			});
		}
		m.addStyleName(style.menuItem());
		
	}

	@Override
	public void execute() {
		ReloginPlace place;
		place = new ReloginPlace(homePlace);
		presenter.goTo(place);		
	}

	@Override
	public void hide() {
		LayoutPanel p = RootLayoutPanel.get(); // parent of header
		p.setWidgetVisible(this, false);
		navigation.hide();
		menu.ifPresent(m -> p.setWidgetVisible(m, false));
		p.setWidgetTopBottom(root, 0, Unit.PX, 0, Unit.PX);		
		p.setWidgetTopBottom(navigation, 0, Unit.PX, 0, Unit.PX);
	}

	@Override
	public void show() {
		LayoutPanel p = RootLayoutPanel.get(); // parent of header
		p.setWidgetVisible(this, true);
		menu.ifPresent(m -> p.setWidgetVisible(m, false));
		int top = 50;
		p.setWidgetTopBottom(root, top, Unit.PX, 0, Unit.PX);
		p.setWidgetTopBottom(navigation, top, Unit.PX, 0, Unit.PX);
		navigation.show();
		onResize();
	}

	@Override
	public void setDisplay(Widget display, NavigationView navigation, Optional<NavigationMenu> menu) {
		root = display;
		this.navigation = navigation;
		this.menu = menu;
		RootLayoutPanel.get().setWidgetTopHeight(this, 0, Unit.PX, 100, Unit.PX);
		if (menu.isPresent()) {
			RootLayoutPanel.get().setWidgetTopHeight(menu.get(), 50, Unit.PX, getMenuHeight(), Unit.PX );
			menu.get().addValueChangeHandler(this);
		}
	}

	private int getMenuHeight() {
		if (menu.isPresent() ) return menu.get().getHeight();
		return 0;
	}

  @Override
  public void setTrail(List<SelectModuleItem> trail) {
  }

  public Widget getDisplay() {
	  return root;
  }

  final static private int MIN_WIDTH = 700;
  boolean showNavigation = true; 
  
@Override
public void onResize() {
	int width = Window.getClientWidth();
	GWT.log("header on resize  " + width);
	if (menu.isPresent() && isVisible() && showNavigation) {
		NavigationMenu m = menu.get();
		RootLayoutPanel r = RootLayoutPanel.get();
		if (width < MIN_WIDTH) {
			if (!m.isVisible()) {
				m.setUpDown(false, false);
				navigation.hide();
				r.setWidgetVisible(m, true);
				r.setWidgetTopBottom(root, 50 + getMenuHeight(), Unit.PX, 0, Unit.PX);
				r.setWidgetTopBottom(navigation, 50 + getMenuHeight(), Unit.PX, 0, Unit.PX);			
			}
		} else if (m.isVisible()) {
			navigation.show();
			r.setWidgetVisible(m, false);
			r.setWidgetTopBottom(root, 50, Unit.PX, 0, Unit.PX);
			r.setWidgetTopBottom(navigation, 50, Unit.PX, 0, Unit.PX);			
		}
	}
	
}

@Override
public void onValueChange(ValueChangeEvent<Boolean> event) {
	GWT.log("value changed " + event.getValue());
	if (event.getValue()) {
		navigation.wide();
	} else {
		navigation.hide();
	}
}



public void showNav(boolean show) {
	showNavigation = show;
	// bookkeeping
	if (show) {
		navigation.show(); // or wide + hide + menu show
	} else {
		navigation.hide();
		//menu.ifPresent(NavigationMenu::hide);
	}
	
}
}
