package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.rest.util.RestyDateTimeFormat;

public class HeaderPrint extends Composite {

	private static HeaderPrintUiBinder uiBinder = GWT.create(HeaderPrintUiBinder.class);

	interface HeaderPrintUiBinder extends UiBinder<Widget, HeaderPrint> {
	}

	@UiField
	Label name, activity, description;
	
	public HeaderPrint() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public HeaderPrint(Memento memento) {
		this();
		name.setText(memento.getLearnerName());
		double score = memento.getScore();
		String duration = memento.getValue(Memento.TOTAL_TIME);
		String timestamp = memento.getValue("cmi.comments_from_lms.0.timestamp");
		String nowstr;
		Date now;
		DateTimeFormat formatter = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_LONG);
		if (timestamp.isEmpty()) {
			now = new Date();
			nowstr = formatter.format(now);
		} else {
			try {
				DateTimeFormat scanner = DateTimeFormat.getFormat(RestyDateTimeFormat.RESTY_DATETIME_FORMAT);
				now = scanner.parse(timestamp);
				nowstr = formatter.format(now);
			} catch (IllegalArgumentException e) {
				nowstr = timestamp;
			}
		}
		description.setText("Score: " + score + " in " + duration + ". " + nowstr);
		String sconame = memento.getValue("dme.sco_name");
		String team  = memento.getValue("dme.team");
		activity.setText("\"" + sconame  + "\" in " + team);
	}

}
