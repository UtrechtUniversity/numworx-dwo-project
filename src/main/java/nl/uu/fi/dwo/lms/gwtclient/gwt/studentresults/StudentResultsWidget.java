package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;

public class StudentResultsWidget extends Composite {

	private static StudentResultsWidgetUiBinder uiBinder = GWT.create(StudentResultsWidgetUiBinder.class);

	interface StudentResultsWidgetUiBinder extends UiBinder<Widget, StudentResultsWidget> {
	}

	@Inject StudentResultsWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		setHeight("100%");
	}

	@UiField InlineLabel title, perc;
	@UiField Tree tree;
	@UiField SimplePanel description;
	@UiField InlineHTML outer;

	interface Template extends SafeHtmlTemplates
	{
		@SafeHtmlTemplates.Template("<div class='score-template'><div class='score-incorrect' style='width:{0}%' ></div><div class='score-correct' style='width:{1}%'></div></div>")
		SafeHtml content(float incorrect, float correct);
	}

	Template scoreTemplate = GWT.create(Template.class);

	void setPerc(int perc) {
		float red = 0.5f, green = 0.5f;
		if (perc > 50) { red = 0; green = perc-50;
		} else if (perc < 50) {
			red = 50 - perc; green = 0;
		}
		SafeHtml sh = scoreTemplate.content(red, green);
		outer.setHTML(sh);
		this.perc.setText(perc+"%");
	}

}
