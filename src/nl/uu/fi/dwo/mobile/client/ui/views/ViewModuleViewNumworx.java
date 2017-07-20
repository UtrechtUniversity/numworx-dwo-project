package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
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
	Widget homeBtn;
	
	ViewModuleViewImpl delegate;

	public void setTitle(String title) {
		delegate.setTitle(title);
	}

	public void setupModule(String name, String file) {
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
		
	
}
