package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class FilterDialog extends Composite implements CloseHandler<PopupPanel> {

	private static FilterDialogUiBinder uiBinder = GWT.create(FilterDialogUiBinder.class);

	interface FilterDialogUiBinder extends UiBinder<Widget, FilterDialog> {
	}

	@UiField FilterSettings settings;
	@UiField Label title;
	@UiField Button okBtn;
	@UiField CssResource style;
	
	private CloseHandler<PopupPanel> close;
	private PopupPanel popup;
	
	@Inject FilterDialog() {
		initWidget(uiBinder.createAndBindUi(this));
		style.ensureInjected();
		popup = new PopupPanel(true,true);		
		popup.setStylePrimaryName("filter-dialog");
		popup.setWidget(this);
		popup.addCloseHandler(this);
		
	}

	public void setTitle(String t) {
		title.setText(t);
	}
	
	void setValue(Map<String, Map<String, Set<Integer>>> value) {
		settings.setValue(value);
	}
	
	Map<String, Map<String, Set<Integer>>> getValue() {
		return settings.getValue();
	}
	
	@UiHandler("okBtn") void onClick(ClickEvent e) {
		hide();
	}
	
	void show() {
		Scheduler.get().scheduleDeferred(popup::center);

	}
	void hide() {
		popup.hide();
	}
	
	void addCloseHandler(CloseHandler<PopupPanel> h) {
		this.close = h;
	}

	@Override
	public void onClose(CloseEvent<PopupPanel> event) {
		close.onClose(event);		
	}
}
