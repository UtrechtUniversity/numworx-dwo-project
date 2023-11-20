package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.AbstractStudentModelPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;

public class EastPanel extends ResizeComposite {

	private final static EastPanelUiBinder uiBinder = GWT.create(EastPanelUiBinder.class);
    private final static String lang = LocaleInfo.getCurrentLocale().getLocaleName();

	interface EastPanelUiBinder extends UiBinder<DockLayoutPanel, EastPanel> {
	}
	
	
	@UiField DockLayoutPanel east;
	@UiField public Label title;
	@UiField InlineLabel redPerc, perc;
	@UiField FlowPanel north;
	@UiField SimplePanel outer;
	@UiField
	public SimpleLayoutPanel description;
	
	@Inject EastPanel(DescriptionPresenter s) {
		service = s;
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setPerc(DomStudentModelScore<?> s) {
		Widget sh = s.getChildren() == null
				? Util.scoreItem("", s, Util.MAX_LEVEL)
				: Util.summaryItem("", s, Util.MAX_LEVEL);
		outer.setWidget(sh);
		double greenPerc = Util.getGreen(s) * 200;
		double redPerc =   Util.getRed(s) * 200;
		this.perc.setText(Math.round(greenPerc)+"%");
		this.redPerc.setText(Math.round(redPerc)+"%");
	}
	
	public void clearVisibility() {
		getElement().getStyle().clearVisibility();
	}

	private String getTitle(DomStudentModelContextInfo info) {
		return AbstractStudentModelPresenter.getTitle(info, lang);
	}

	private final DescriptionPresenter service;
		
	public void setDescription(DomStudentModelContextId model, DomStudentModelContextInfo info) {
		title.setText(getTitle(info));
		service.get(model, info)
		.then(p -> { Widget value = p.getValue();
			description.setWidget(value);
			return p;
		});
	}
	
	public void enableScore(boolean b) {
		east.setWidgetHidden(north, !b);
	}
	
}
