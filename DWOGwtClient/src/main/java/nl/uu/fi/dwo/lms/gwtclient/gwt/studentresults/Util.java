package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;

public class Util {
	private Util() {
	}

	interface Template extends SafeHtmlTemplates {
//		@SafeHtmlTemplates.Template("<div class='score-template'><div class='score-incorrect' style='width:{0}%' ></div><div class='score-correct' style='width:{1}%'></div></div>")
//		SafeHtml content(double incorrect, double correct);

		@SafeHtmlTemplates.Template("<div class='score-template'><div class='score-incorrect' style='{0}' ></div><div class='score-correct' style='{1}'></div></div>")
		SafeHtml content(SafeStyles incorrect, SafeStyles correct);

		
		
		@SafeHtmlTemplates.Template("<div class='score-treeItem' style='margin-right:{3}em'><span class='score-title'>{0}</span><div class='score-template'><div class='score-incorrect' style='width:{1}%'></div><div class='score-correct' style='width:{2}%'></div></div></div>")
		SafeHtml treeItem(String test, double incorrect, double correct, int margin);
	}

	static private Template scoreTemplate = GWT.create(Template.class);

	static SafeHtml percentageBar(DomStudentModelScore<?> s) {
		double green, red;
//schaal 0 .. 0.5
		green = getGreen(s);
		red = getRed(s);
//naar percentages 0 .. 50%
		red *= 100;
		green *= 100;
		SafeStyles redStyle = new SafeStylesBuilder().width(red, Unit.PCT).toSafeStyles();
		SafeStyles greenStyle = new SafeStylesBuilder().width(green, Unit.PCT).toSafeStyles();
		SafeHtml sh = scoreTemplate.content(redStyle, greenStyle);
		return sh;
	}

	public static double getRed(DomStudentModelScore<?> s) {
		double red;
		if (s.getRedCount() > 0) {
			red = (0.5 - s.getRedScore() / s.getRedCount()) * s.getRedCount() / s.getTotalCount();
		} else
			red = 0;
		return red;
	}

	public static double getGreen(DomStudentModelScore<?> s) {
		double green;
		if (s.getGreenCount() > 0) {
			green = (s.getGreenScore() / s.getGreenCount() - 0.5) * s.getGreenCount() / s.getTotalCount();
		} else
			green = 0;
		return green;
	}

	public static final int MAX_LEVEL = 3;

	static SafeHtml treeItem(String title, DomStudentModelScore<?> s, int level) {
		double green, red;
// schaal 0 .. 0.5
		green = getGreen(s);
		red = getRed(s);
// naar percentages 0 .. 50%
		red *= 100;
		green *= 100;
		level = Math.max(MAX_LEVEL - level, 0);
		return scoreTemplate.treeItem(title, red, green, level);
	}
	
	public static Widget summaryItem(String title, DomStudentModelScore<?>s, int level) {
		level = Math.max(MAX_LEVEL - level, 0);
		SummaryIcon summaryIcon = new SummaryIcon(title, s, level);
		//if (title.isEmpty()) return summaryIcon.imageOnly();
		return summaryIcon;
	}

	public static Widget scoreItem(String title, DomStudentModelScore<?>s, int level) {
		level = Math.max(MAX_LEVEL - level, 0);
		return new ScoreIcon(title, s, level);
	}

}
