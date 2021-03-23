package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
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
	@UiField Label filter;

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
	
	void setFilter(Map<String, Map<String, Set<Integer>>> map) {
		if (map.size() == 1) {
			String methode = map.keySet().iterator().next();
			Map<String, Set<Integer>> m = map.get(methode);
			if (m.size() == 1) {
				String boek = m.keySet().iterator().next();
				Set<Integer> hfstk = m.get(boek);
				filter.setText(methode + " > " + boek + " >" + h(hfstk));
				return;
			} else {
				StringBuilder sb = new StringBuilder(methode).append(" > ");
				m.keySet().stream().sorted().forEach(k -> {
					Set<Integer> hfstk = m.get(k);
					sb.append(k).append("-h");
					hfstk.stream().sorted().forEach(i -> sb.append(i).append(',') );
					sb.deleteCharAt(sb.length()-1).append(" ; ");
				});
				filter.setText(sb.substring(0, sb.length()-3));
				return;
			}
		}
		filter.setText(String.valueOf(map));
	}

	private String h(Set<Integer> hfstk ) {
		StringBuilder sb = new StringBuilder(" ");
		hfstk.stream().sorted().forEach(i -> sb.append('h').append(i).append(','));
		return sb.deleteCharAt(sb.length()-1).toString();
	}
}
