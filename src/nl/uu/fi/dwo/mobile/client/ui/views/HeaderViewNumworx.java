/**
 * 
 */
package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.account.client.ProfileCommand;
import nl.uu.fi.dwo.account.client.SchoolClassStudentCommand;
import nl.uu.fi.dwo.account.client.StudentModelCommand;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.StudentModelPanel;
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
public class HeaderViewNumworx extends Composite implements HasText, Command, HeaderView {
	
	private static HeaderViewNumworxUiBinder uiBinder = GWT.create(HeaderViewNumworxUiBinder.class);

	interface HeaderViewNumworxUiBinder extends UiBinder<Widget, HeaderViewNumworx> {
	}

	@UiField(provided=true) String pfx = DWOplayer.PARAMETERS.getResource("");
	@UiField Label loginLabel;
	@UiField Text rb;
	@UiField(provided=true) MenuItem user;
	/*@UiField*/ //TextBox searchInput;
	@UiField TreeModuleViewNumworxCss style;

	MenuBar items = new MenuBar(true);
	final private EventBus bus;


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
	@Inject HeaderViewNumworx(EventBus bus) {
		this.bus = bus;
        final int correctie = 10; // width popup 
		user = new MenuItem("<img width='26' height='26' src='" + pfx
				+ "images/numworx/account.svg' >", true, items) {
            @Override
            public int getAbsoluteLeft() {
                int w1 = items.getOffsetWidth();
                int w2 = this.getOffsetWidth();
                return super.getAbsoluteLeft() - w1 + w2 - correctie;
            }
		};
		initWidget(uiBinder.createAndBindUi(this));
		//searchInput.getElement().setPropertyString("placeholder", rb.search());
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
		this.presenter = presenter;
	}

	Place upPlace = new TreeModulePlace();
	Place homePlace = new TreeModulePlace();
	private Widget root;
	private NavigationView navigation;

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
			m=items.addItem(DwoLocalesForGWT.instance.GUI_MyProfile(), new ProfileCommand(bus));
			m.addStyleName(style.menuItem());
			if(role == RoleType.STUDENT) {
				ScheduledCommand cmd = new SchoolClassStudentCommand(this, bus);
				m=items.addItem(DwoLocalesForGWT.instance.GUI_MySchoolClasses(), cmd);
				m.addStyleName(style.menuItem());

				boolean experimenteel = DWOplayer.isPremium();//"test".equals(DWOplayer.PARAMETERS.getDwoEnv());
				if(experimenteel) {
					m=items.addItem(rb.STUDENT_MODELS(), new StudentModelCommand(StudentModelPanel.BUILDER, bus));
					m.addStyleName(style.menuItem());
				}
			}
			boolean single = Boolean.TRUE.equals(currentUser.getSingleSchool());
			if(!single)
			{
				Command cmd = null;
				cmd = new nl.uu.fi.dwo.account.client.SchoolLoginCommand(this, bus);			
				m=items.addItem(DwoLocalesForGWT.instance.GUI_MySchoolLogins(), cmd);
				m.addStyleName(style.menuItem());
			}

			m=items.addItem(rb.logout(), new ScheduledCommand() {
				
				@Override
				public void execute() {
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
		p.setWidgetTopBottom(root, 0, Unit.PX, 0, Unit.PX);		
		p.setWidgetTopBottom(navigation, 0, Unit.PX, 0, Unit.PX);
	}

	@Override
	public void show() {
		LayoutPanel p = RootLayoutPanel.get(); // parent of header
		p.setWidgetVisible(this, true);
		p.setWidgetTopBottom(root, 50, Unit.PX, 0, Unit.PX);
		p.setWidgetTopBottom(navigation, 50, Unit.PX, 0, Unit.PX);
		navigation.show();
	}

	@Override
	public void setDisplay(Widget display, NavigationView navigation) {
		root = display;
		this.navigation = navigation;
		RootLayoutPanel.get().setWidgetTopBottom(this, 0, Unit.PX, 50, Unit.PX);
	}

  @Override
  public void setTrail(List<SelectModuleItem> trail) {
  }


}
