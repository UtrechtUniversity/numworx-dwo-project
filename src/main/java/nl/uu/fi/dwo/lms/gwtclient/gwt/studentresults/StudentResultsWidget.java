package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
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

	void setPerc(float perc) {
		SafeHtml sh = Util.percentageBar(Math.round(perc));
		outer.setHTML(sh);
		perc = perc * 2 - 100;
		this.perc.setText(Math.round(perc)+"%");
	}

}
