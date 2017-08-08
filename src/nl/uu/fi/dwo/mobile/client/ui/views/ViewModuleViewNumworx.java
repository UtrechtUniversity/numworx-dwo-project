package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;
import java.util.ListIterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;

public class ViewModuleViewNumworx extends ResizeComposite implements ViewModuleView {

	private static ViewModuleViewNumworxUiBinder uiBinder = GWT.create(ViewModuleViewNumworxUiBinder.class);

	interface ViewModuleViewNumworxUiBinder extends UiBinder<DockLayoutPanel, ViewModuleViewNumworx> {
	}

	private DockLayoutPanel root;
	private MenuBar items = new MenuBar(true);
	
	public ViewModuleViewNumworx() {
		delegate = new ViewModuleViewImpl(false);
		pfx = DWOplayer.PARAMETERS.getResource("");
        final int correctie = 10; // width popup 
		user = new MenuItem("<i class='fa fa-caret-down fa-2x'></i>", true, items) {
            @Override
            public int getAbsoluteLeft() {
                int w1 = items.getOffsetWidth();
                int w2 = this.getOffsetWidth();
                return super.getAbsoluteLeft() - w1 + w2 - correctie;
            }
		};
		initWidget(root = uiBinder.createAndBindUi(this));
		delegate.initialize();
		delegate.zetMaat();
		center.setWidget(delegate);
		root.forceLayout();
	}

	@UiField
	SimplePanel center;
	@UiField(provided=true)
	MenuItem user;
	@UiField(provided=true)
	String pfx;
	@UiField
	FocusPanel homeBtn;
	@UiField
	FocusPanel upBtn;
	@UiField Label loginLabel;
//	@UiField ToggleButton fullBtn;
	@UiField Label title;
	@UiField TreeModuleViewNumworxCss t;
	@UiField HTML kruimels;
	
	ViewModuleViewImpl delegate;

	public void setTitle(String title) {
		this.title.setText(title); 
		delegate.setTitle(title);
	}

	public void setupModule(String name, String file) {
		String login = DWOplayer.withUser()? DwoGlobalVars.instance().getCurrentUser().getDisplayName() : "GUEST";
		loginLabel.setText(login);
		setupMenu(items);
		delegate.setupModule(name, file);
	}

	public HeaderButton getBackButton() {
		return delegate.getBackButton();
	}

	public void setApi(Scorm2004IF scorm_guest) {
		delegate.setApi(scorm_guest);
	}

	public void close() {
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
	
	void goTo(Place place) {
		DWOplayer.clientfactory.getPlaceController().goTo(place);
	}
	
	
	// FIXME SHARED CODE
	void setupMenu(MenuBar items) {
		items.clearItems();
		MenuItem m;
		String back = delegate.getBackButton().getText();
		if(DWOplayer.withUser()) {
			m=items.addItem("Logout", new ScheduledCommand() {
				
				@Override
				public void execute() {
					
					goTo(new LoginPlace());					
				}
			});
		} else {
			m=items.addItem("Aanmelden", new ScheduledCommand() {
				
				@Override
				public void execute() {
					goTo(new LoginPlace());
				}
			});
		}
		m.addStyleName(t.menuItem());
	}

	@Override
	public void setTrail(List<SelectModuleItem> trail) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		ListIterator<SelectModuleItem> iter = trail.listIterator(Math.min(trail.size(),3));
		while (iter.hasPrevious()) {
			SelectModuleItem selectModuleItem = (SelectModuleItem) iter.previous();
			String title = selectModuleItem.getName();
			String id = selectModuleItem.getID().toString();
			builder.appendHtmlConstant("<a href='#TreeModulePlace:" + id + "'>");
			builder.appendEscaped(title);
			builder.appendHtmlConstant("</a> &gt; ");			
		}
		kruimels.setHTML(builder.toSafeHtml());

		if(!trail.isEmpty())
			upId = trail.get(0).getParentID();
		else
			upId = null;
	}
	
	@UiHandler("homeBtn")
	void onHomeBtn(ClickEvent ev) {
		goTo(new TreeModulePlace());
	}
	
	private Object upId;
	@UiHandler("upBtn")
	void onUpBtn(ClickEvent ev) {
		Object parent = upId;
		if(parent == null) parent = "0";
		goTo(new TreeModulePlace(parent));
	}
}
