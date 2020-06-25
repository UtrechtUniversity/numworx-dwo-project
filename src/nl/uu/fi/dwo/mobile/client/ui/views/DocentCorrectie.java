package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVak;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;

public class DocentCorrectie extends Composite {

	private static DocentCorrectieUiBinder uiBinder = GWT.create(DocentCorrectieUiBinder.class);

	@UiField(provided=true)
	Text rb = Text.constants;
	
	@UiField Label maxScore, score, correctie, opmerking;
	@UiField TekstVak content;

	interface DocentCorrectieUiBinder extends UiBinder<Widget, DocentCorrectie> {
	}

	public DocentCorrectie() {
		initWidget(uiBinder.createAndBindUi(this));
	}


	public DocentCorrectie(int maxScore, int score, int scoreCorrectie, String scoreComment) {
		this();
		this.maxScore.setText(rb.maximaleScore() + maxScore);
		this.score.setText(rb.score() + score);
		this.correctie.setText(rb.toevoeging() + scoreCorrectie);
		if (scoreComment == null || scoreComment.trim().isEmpty()) {
			content.removeFromParent();
			opmerking.removeFromParent();
		} else {
			TekstBuffer b = new TekstBuffer();
			ArrayList<Object> feedbackList = b.convertTekst(scoreComment, null, false);
			content.setFontSize(14);
			content.setFontName(XMLView.getDefaultFontName());
			content.setColor(CssColor.make("rgb(49,71,112)"));
			content.clear();
			content.setSize(100, 34);
			content.setPasHoogteBreedteAan(true, true);
			content.setObjects(feedbackList);
			content.resize();
		}
	}


}
