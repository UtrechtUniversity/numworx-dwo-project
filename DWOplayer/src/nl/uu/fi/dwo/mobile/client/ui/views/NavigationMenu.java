package nl.uu.fi.dwo.mobile.client.ui.views;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.UIObject;

import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationViewNumworx.NavigationViewNumworxUiBinder;

@Singleton
public class NavigationMenu extends Composite implements HasValueChangeHandlers<Boolean>, ClickHandler {
	
	private static final String UP = "<i class=\"fa fa-chevron-up\"></i>";
	private static final String DOWN = "<i class=\"fa fa-chevron-down\"></i>";
		
	private SafeHtml up, down;
	private boolean updown = true; // start up
	private HTML html;

	@UiField Text rb;

	private static NavigationMenuUiBinder uiBinder = GWT.create(NavigationMenuUiBinder.class);

	interface NavigationMenuUiBinder extends UiBinder<HTML, NavigationMenu> {
	}

	
	
	@Inject NavigationMenu() {
		html = uiBinder.createAndBindUi(this);
		String modules = rb.bibliotheek();
		//html.getElement().addClassName(css.navMenu());
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.appendHtmlConstant("<span>");
		builder.appendEscaped(modules);
		builder.appendHtmlConstant("</span>");
		builder.appendHtmlConstant(UP);
		SafeHtml safehtml = builder.toSafeHtml();
		html.setHTML(safehtml);
		up = safehtml;
		builder = new SafeHtmlBuilder();
		builder.appendHtmlConstant("<span>");
		builder.appendEscaped(modules);
		builder.appendHtmlConstant("</span>");
		builder.appendHtmlConstant(DOWN);
		down = builder.toSafeHtml();		
		initWidget(html);
		html.addClickHandler(this);
	}
	
	public void setUpDown(boolean updown, boolean fire) {
		if(fire) ValueChangeEvent.fireIfNotEqual(this, this.updown, updown);
		this.updown = updown;
		if (updown) html.setHTML(up); else html.setHTML(down);
	}

	public boolean isUpdown() {
		return updown;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public void fireEvent(GwtEvent<?> event) {
		super.fireEvent(event);
	}

	@Override
	public void onClick(ClickEvent event) {
		setUpDown(!updown, true);
	}

	public int getHeight() {
		return 40;
	}

}
