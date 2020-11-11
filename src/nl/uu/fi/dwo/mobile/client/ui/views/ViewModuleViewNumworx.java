package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.SecureMode;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.Actions;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;

public class ViewModuleViewNumworx extends ResizeComposite implements ViewModuleViewBuilder {

	private static ViewModuleViewNumworxUiBinder uiBinder = GWT.create(ViewModuleViewNumworxUiBinder.class);

	public boolean nextPageAction() {
    return delegate.nextPageAction();
  }

  interface ViewModuleViewNumworxUiBinder extends UiBinder<DockLayoutPanel, ViewModuleViewNumworx> {
	}

	private DockLayoutPanel root;
	private MenuBar items = new MenuBar(true);
    final boolean seb = DWOplayer.PARAMETERS.getSecureMode() == SecureMode.SEB;
	
	
	public ViewModuleViewNumworx() {}

	public void initialize(Scorm2004IF api) {
      delegate = new ViewModuleViewImpl(false, api);
      pfx = DWOplayer.PARAMETERS.getResource("");
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
      initWidget(root = uiBinder.createAndBindUi(this));
      delegate.initialize();
      delegate.setWindowTop(90); // 90 pixels header
      delegate.zetMaat();
      center.setWidget(delegate);
      if(seb) {
          removeBtns();
      }
      up2Btn.setVisible(false && Actions.isAvailable());
      root.forceLayout();	  
	}
	
//	public ViewModuleViewNumworx(Scorm2004IF api) {
//	  initialize(api);
//	}

	public void removeBtns() {
//		homeBtn.removeFromParent();
		upBtn.removeFromParent();
	}

	public void setScoType(ScoType type) {
		boolean toets = false;
		switch(type) {
		case ZELFTOETS:
			toets = true;
			scoType.setText(rb.ZELFTOETS()); break;
		case EINDTOETS:
			toets = true;
			scoType.setText(rb.EINDTOETS()); break;
		default:
			scoType.setText(rb.LESSTOF());
			break;
		}
		headerBottom.setStyleName(t.toets(), toets);
		headerBottom.setStyleName(t.lesstof(), !toets);
		delegate.setScoType(type);
	}
	
	
	@UiField
	SimplePanel center;
	@UiField(provided=true)
	MenuItem user;
	@UiField(provided=true)
	String pfx;
//	@UiField
//	FocusPanel homeBtn;
	@UiField
	FocusPanel upBtn,up2Btn;
	@UiField Label loginLabel;
//	@UiField ToggleButton fullBtn;
	@UiField Label title;
	@UiField TreeModuleViewNumworxCss t;
	@UiField FlowPanel kruimels, loginflow, logo;
	@UiField FlowPanel headerBottom;
	@UiField HasText scoType;
	@UiField nl.uu.fi.dwo.mobile.client.text.Text rb;
	@UiField MenuBar bar;
	@UiField DockLayoutPanel headerTop;
	
	ViewModuleViewImpl delegate;

	public void setTitle(String title) {
		this.title.setText(title); 
		delegate.setTitle(title);
	}

	public Promise<Boolean> setupModule(String name, String file) {
		String login = DWOplayer.clientfactory.withUser()? DwoGlobalVars.instance().getCurrentUser().getDisplayName() : "";
		loginLabel.setText(login);
		headerTop.setWidgetHidden(loginflow, !DWOplayer.clientfactory.withUser());
		headerTop.forceLayout();
		headerTop.setWidgetSize(loginflow, loginLabel.getOffsetWidth());
		setupMenu(items);
		return delegate.setupModule(name, file);
	}

	public void close() {
		clearKruimels();
		delegate.close();
	}

	public AnchorContext getAnchorContext() {
		return delegate.getAnchorContext();
	}

	public void setAnchorContext(AnchorContext context) {
		delegate.setAnchorContext(context);
	}

	public void setUnitId(String id) {
		delegate.setUnitId(id);
	}

	public OpdrNavIF getOpdrNav() {
		return delegate.getOpdrNav();
	}

	public Number getScoreRaw() {
		return delegate.getScoreRaw();
	}

	public void setReadonly(boolean readonly) {
		delegate.setReadonly(readonly);
	}

	public boolean isReadonly() {
		return delegate.isReadonly();
	}

	public ViewModuleView initialize() {
		return this;
	}
	
	private void goTo(Place place) {
		presenter.goTo(place);
	}
		
	// FIXME SHARED CODE
	void setupMenu(MenuBar items) {
		items.clearItems();
		MenuItem m;
		String logout;
        if (seb)
		  logout = Text.constants.inleveren();
		else if(DWOplayer.clientfactory.withUser()) {
		  logout = Text.constants.logout();
		} else {
		  logout = Text.constants.aanmelden();
		}
		m=items.addItem(logout, () -> goTo(new LoginPlace()));
		m.addStyleName(t.menuItem());
		if (seb) {
			items.addStyleDependentName("seb");
			bar.setStylePrimaryName("seb-MenuBar");
		}
	}

	List<HandlerRegistration> register = new LinkedList<>();
	
	@Override
	public void setTrail(List<SelectModuleItem> trail) {
		if(seb)
			trail.clear();

		ListIterator<SelectModuleItem> iter = trail.listIterator(Math.min(trail.size(),3));
		clearKruimels();
		while (iter.hasPrevious()) {
			SelectModuleItem selectModuleItem = (SelectModuleItem) iter.previous();
			String title = selectModuleItem.getName();
			String id = selectModuleItem.getID().toString();
			final TreeModulePlace place = new TreeModulePlace(id);
			InlineLabel a = new InlineLabel(title);
			a.setStyleName(t.kruimelpad());
			kruimels.add(a);
			register.add(a.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					goTo(place);
					
				}
			}));
			kruimels.add(new InlineLabel(" > "));
		}

		if(!trail.isEmpty())
			upId = new TreeModulePlace(trail.get(0).getID());
		else
			upId = new TreeModulePlace();
		DWOplayer.clientfactory.getHeaderView().setUpPlace(upId);
	}
	
	private void clearKruimels() {
		Iterator<HandlerRegistration> iter = register.iterator();
		while (iter.hasNext()) {
			HandlerRegistration type = iter.next();
			iter.remove();
			type.removeHandler();
		}
		kruimels.clear();
	}

//	@UiHandler("homeBtn")
//	void onHomeBtn(ClickEvent ev) {
//		goTo(new TreeModulePlace());
//	}
	
	private TreeModulePlace upId = new TreeModulePlace();
	private Presenter presenter;
	@UiHandler({"upBtn","up2Btn"})
	void onUpBtn(ClickEvent ev) {
	    ev.stopPropagation();
		goTo(upId);
	}

	@Override
	public Scorm2004IF getApi() {
		return delegate.getApi();
	}
	
	@Override
	public void setPresenter(Presenter p) {
		this.presenter = p;
		if(delegate != null)
			delegate.setPresenter(p);
	}

	@Override
	public void setModel(Promise<DomStudentModelContextId> studentModel) {
		if(delegate != null)
			delegate.setModel(studentModel);
		
	}

  @Override
  public void showIcon(boolean b) {
//      if(Actions.isAvailable())
//      headerBottom.setStyleName("modules-icon2", b);    
  }
  
  @UiHandler({"scoType","modules"}) void onModules(ClickEvent e) {
    Actions.showMainNav.execute();
  }

@Override
public void onResize() {
	boolean hidden = Double.valueOf(10.0).equals(headerTop.getWidgetSize(logo));
	GWT.log("w=" + kruimels.getOffsetWidth() + " h=" + hidden);
	if (hidden && kruimels.getOffsetWidth() > 270) 
		hidden = false;
	else if (!hidden && kruimels.getOffsetWidth() < 100) 
		hidden = true;
	headerTop.setWidgetSize(logo, hidden? 10.0 : 170.0);
	super.onResize();
}
}
