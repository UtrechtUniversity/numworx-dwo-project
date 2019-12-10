/**
 * 
 */
package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;

/**
 * @author peterboon
 *
 */
public class FEWACheckPanel extends Composite {

	private static FEWACheckPanelUiBinder uiBinder = GWT.create(FEWACheckPanelUiBinder.class);

	interface FEWACheckPanelUiBinder extends UiBinder<Widget, FEWACheckPanel> {
	}

	public FEWACheckPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		checkimg.setUrl(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
	}

	@UiField Image checkimg;
	@UiField Label feedbackLabel;
	
	Runnable run;

	@UiHandler({"checkimg", "feedbackLabel"})
	void onClick(ClickEvent e) {
		run.run();
	}

	void setHandler(Runnable r) {
		run = r;
	}
}
