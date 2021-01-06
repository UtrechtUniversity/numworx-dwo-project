package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;

public class StudentResultsWidget extends Composite {

	private static StudentResultsWidgetUiBinder uiBinder = GWT.create(StudentResultsWidgetUiBinder.class);

	interface StudentResultsWidgetUiBinder extends UiBinder<Widget, StudentResultsWidget> {
	}

	private final EventBus bus;

	@Inject StudentResultsWidget(EventBus bus) {
		initWidget(uiBinder.createAndBindUi(this));
		setHeight("100%");
		this.bus = bus;
		
	}

	@UiField InlineLabel title, perc, redPerc;
	@UiField Tree tree;
	@UiField SimplePanel description;
	@UiField SimplePanel outer;
	@UiField ListBox models;
	@UiField Button btn;
	@UiField DockLayoutPanel east;

	void setPerc(DomStudentModelScore<?> s) {
		Widget sh = s.getChildren() == null
				? Util.scoreItem("", s, Util.MAX_LEVEL)
				: Util.summaryItem("", s, Util.MAX_LEVEL);
		outer.setWidget(sh);
		double greenPerc = Util.getGreen(s) * 200;
		double redPerc =   Util.getRed(s) * 200;
		this.perc.setText(Math.round(greenPerc)+"%");
		this.redPerc.setText(Math.round(redPerc)+"%");
	}

	@UiHandler("models") void onChange(ChangeEvent ev) {
		bus.fireEventFromSource(ev, this);
	}

	@UiHandler("btn") void onGraph(ClickEvent ev) {
		bus.fireEventFromSource(ev, this);
	}
}
