package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;

public class StudentResultsWidget extends Composite {

	private static StudentResultsWidgetUiBinder uiBinder = GWT.create(StudentResultsWidgetUiBinder.class);

	interface StudentResultsWidgetUiBinder extends UiBinder<DockLayoutPanel, StudentResultsWidget> {
	}
	enum Which { filter, graph, back };
	private final EventBus bus;
	private final DockLayoutPanel root;
	private Which which;

	@Inject StudentResultsWidget(EventBus bus, EastPanel east) {
		tree = new StudentResultsTree(bus);
		this.east = east;
		this.description = east.description;
		this.title = east.title;
		
		initWidget(root = uiBinder.createAndBindUi(this));
		setHeight("100%");
		this.bus = bus;
		setBackVisible(false);		
	}

	public void setBackVisible(boolean b) {
		root.setWidgetHidden(back, !b);
	}
	
	@UiField(provided=true) StudentResultsTree tree;
	@UiField ListBox models;
	@UiField Button btn;
	@UiField(provided=true) EastPanel east;
	@UiField Label filter;
	@UiField nl.uu.fi.dwo.lms.gwtclient.gwt.jsutil.CheckBox viewBtn;
	@UiField Anchor back;
	@Deprecated SimpleLayoutPanel description;
	@Deprecated Label title;
	


	@UiHandler("models") void onChange(ChangeEvent ev) {
		bus.fireEventFromSource(ev, this);
	}

	@UiHandler("btn") void onGraph(ClickEvent ev) {
		which = Which.graph;
		bus.fireEventFromSource(ev, this);
	}
	
	@UiHandler("filterBtn") void onFilter(ClickEvent ev) {
		which = Which.filter;
		bus.fireEventFromSource(ev, this);
	}
	
	@UiHandler("viewBtn") void viewChange(ValueChangeEvent<Boolean> ev) {
		bus.fireEventFromSource(ev, this);
	}
	
	
	@UiHandler("back") void onBack(ClickEvent ev) {
		which = Which.back;
		bus.fireEventFromSource(ev, this);
	}

	public void setFilter(Map<String, Map<String, Set<Integer>>> filter2, DomMethod method) {
		viewBtn.setText(method.getMethod());
		if (method.getId() == null) viewBtn.setValue(Boolean.FALSE);
		viewBtn.setEnabled(method.getId() != null);
		filter.setText(FilterUtil.setFilter(filter2, method));
		tree.filter = filter2;
	}
	
	public boolean isFilter() {
		return which == Which.filter;
	}
	public boolean isBack() {
		return which == Which.back;
	}
	
	public boolean isMethod() {
		return viewBtn.getValue().booleanValue();
	}

	public void setPerc(DomStudentModelScore s) {
		east.setPerc(s);
		
	}
}
