package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleClientBundle;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.mobile.DWOplayer;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.theme.base.ButtonCss;
import com.googlecode.mgwt.ui.client.widget.Button;

public class ScoreNavPanel extends Composite {
	
	
	
	interface GotoOpdracht {
		void gotoOpdracht(int i, ScoreNavPanel source);
		void reloadOpdracht(int i, ScoreNavPanel source);
	}
	
	GotoOpdracht listener;
	
	class TouchHandler implements TapHandler {

		int opdracht;

		public TouchHandler(int opdracht) {
			super();
			this.opdracht = opdracht;
		}
		@Override
		public void onTap(TapEvent event) {
			if( listener != null)
			{	listener.gotoOpdracht(this.opdracht, ScoreNavPanel.this);
			}
			if(popup != null) popup.hide();
		}
		
	}

	class ReloadHandler implements TapHandler {

		int opdracht;

		public ReloadHandler(int opdracht) {
			super();
			this.opdracht = opdracht;
		}
		@Override
		public void onTap(TapEvent event) {
			if( listener != null)
			{	listener.reloadOpdracht(this.opdracht, ScoreNavPanel.this);
			}
			if(popup != null) popup.hide();
		}
		
	}

	
	public static class SimpleProgressBar extends Composite  {

        public interface StatusCellSafeHTMLTemplate extends SafeHtmlTemplates {
                @Template("<div><div style=\"font-size:medium;height:1.2em;width:100%;cursor:default;border:thin #7ba5d5 solid;\">"
                                + "<div style=\"height:1.2em;width:{0}%; background:#8cb6e6; background-image: url('progress_background.png');\">"
                                + "</div><div style=\"height:1.2em; margin:-1.2em;font-weight:bold;color:#4e7fba;\">"
                                + "<center>{0}%</center></div></div></div>")
                                SafeHtml status(int percentage);
                @Template("<div><div style=\"font-size:medium;height:1.2em;width:100%;cursor:default;border:thin #7ba5d5 solid;\">"
                        + "<div style=\"height:1.2em;width:{0}%; background:#8cb6e6;\">"
                        + "</div><div style=\"height:1.2em; margin:-1.2em;font-weight:bold;color:#4e7fba;\">"
                        + "<center>\u00A0</center></div></div></div>")
                        SafeHtml nostatus(int percentage);
        }

        final private StatusCellSafeHTMLTemplate statusCellSafeHTMLTemplate = (StatusCellSafeHTMLTemplate) GWT
        .create(StatusCellSafeHTMLTemplate.class);

        final private HTML widget = new HTML();

        private int progress;

        public SimpleProgressBar(int i) {
                initWidget(widget);
                setProgress(i);
        }

        public int getProgress() {
                return progress;
        }

        public void setProgress(final int progress) {
                this.progress = progress;
                widget.setHTML(statusCellSafeHTMLTemplate.nostatus(progress));
        }

}

	
	
	

	VerticalPanel top;
	Label beantwoord;
	Label totaalscore;
	SimpleProgressBar totaalscoreBar, beantwoordBar;
	Grid  vragen;
	int rows = 10;
	public ScoreNavPanel() {
		top = new VerticalPanel();
		initialize();
		VerticalPanel vbox = new VerticalPanel();
		vbox.add(top);
		vbox.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		FlowPanel flow = new FlowPanel();
		flow.add(vbox);
		flow.getElement().getStyle().setOverflowY(Overflow.AUTO);
		initWidget(flow);
	}
	
	private void initialize() {
		Label text;
		setFontFamily(top);
		
		text = new Label("Score"); text.addStyleDependentName("bold");
		text.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		top.add(setFontFamily(text));
		top.add(new InlineHTML("<hr>"));
		Grid grid = new Grid(2,3);
		grid.getColumnFormatter().setWidth(0, "80px");
		grid.getColumnFormatter().setWidth(1, "160px");
		grid.getColumnFormatter().setWidth(2, "70px");
		grid.setCellPadding(10);
		grid.setCellSpacing(10);
		HorizontalPanel hbox = new HorizontalPanel();
		hbox.add(grid);
		reloadTotal = new Button(DWOplayer.DWO_BUNDLE.imgbutton());
		reloadTotal.addTapHandler(new ReloadHandler(-1));
		reloadTotal.getElement().getStyle().setBackgroundImage("url('" + DWOplayer.DWO_BUNDLE.reload().getSafeUri().asString() + "')");
		hbox.add(reloadTotal);
		reloadTotal.setVisible(false);
		top.add(hbox);
		text = new Label("Beantwoord");
		grid.setWidget(0, 0, setFontFamily(text));
		text = new Label("Totaalscore");
		grid.setWidget(1, 0, setFontFamily(text));
		// dummy
		grid.setWidget(0, 1, beantwoordBar = new SimpleProgressBar(30));
		grid.setWidget(1, 1, totaalscoreBar = new SimpleProgressBar(84));

		text = beantwoord = new Label("10 / 10");
		setFontFamily(text).getElement().getStyle().setFontWeight(FontWeight.BOLD);
		text = totaalscore = new Label("0 %");
		setFontFamily(text).getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		grid.setWidget(0, 2, setFontFamily(beantwoord));
		grid.setWidget(1, 2, setFontFamily(totaalscore));
		
		text = new Label("Voortgang"); text.addStyleDependentName("bold");
		setFontFamily(text).getElement().getStyle().setFontWeight(FontWeight.BOLD);
		top.add(text);
		top.add(new InlineHTML("<hr>"));
		vragen = new Grid(rows,4);
		vragen.setCellPadding(10);
		vragen.setCellSpacing(10);

		int[] max = new int[rows];
		for (int i = 0; i < max.length; i++) {
			max[i] = 10;
		}
		createVragen(max);
		top.add(vragen);
	}

	public Widget setFontFamily(Widget widget) {
		widget.getElement().getStyle().setFontSize(16, Unit.PX);
		widget.getElement().getStyle().setProperty("fontFamily", "Arial");
		return widget;
	}

	private Label[] vraagLabels;
	private SimpleProgressBar[] vraagBars;
	private Label[] vraagPunten;
	private int[] scoreMax;
	private int currentOpdracht;
	public PopupPanel popup;
	private boolean opnieuw;
	private boolean itemOpnieuw;
	private Button reloadTotal;

	private void createVragen(int[] scoreMax) {
		vragen.clear(true);
		vragen.resize(rows, 4 + (itemOpnieuw?1:0));
		vragen.getColumnFormatter().setWidth(0, "80px");
		vragen.getColumnFormatter().setWidth(1, "160px");
		vragen.getColumnFormatter().setWidth(2, "80px");
		vragen.getColumnFormatter().setWidth(3, "8px");
		if(itemOpnieuw) vragen.getColumnFormatter().setWidth(4, "70px");
		Label text;
		int totaal = 0;
		vraagLabels = new Label[rows];
		vraagBars = new SimpleProgressBar[rows];
		vraagPunten = new Label[rows];
		this.scoreMax = scoreMax;
		for(int i = 0; i < rows; i++) {
			text = new Label("Vraag " + (i+1)); vragen.setWidget(i, 0, text);
			vraagLabels[i] = text;
			if(i == currentOpdracht) text.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			setFontFamily(text);
			Widget widget = vraagBars[i] = new SimpleProgressBar(0); vragen.setWidget(i, 1, widget); // dummy
			widget.setVisible(scoreMax[i] != 0);
			text = vraagPunten[i] = new Label(""); vragen.setWidget(i,2, text); // dummy
			setFontFamily(text);
			text.getElement().getStyle().setFontWeight(FontWeight.BOLD);
// Wat voor button moet hier komen?
			Button p = new Button();p.setText(">"); vragen.setWidget(i, 3, p);
			//p.setStylePrimaryName("vraagButton");
			p.addTapHandler(new TouchHandler(i));
			
			ButtonCss css = DWOplayer.DWO_BUNDLE.imgbutton();
			Button reload = new Button(css);
			reload.getElement().getStyle().setBackgroundImage("url('" + DWOplayer.DWO_BUNDLE.reload().getSafeUri().asString() + "')");
			if(itemOpnieuw) 
				vragen.setWidget(i, 4, reload);
			reload.addTapHandler(new ReloadHandler(i));
		};
		setTotaalScore(totaal);
		
	}

	public void setTotaalScore(int totaal) {
		totaalscore.setText(totaal + " %");
		totaalscoreBar.setProgress(totaal);
	}
	
	public void setAantalOpdrachten(int aantal, int[] max) {
		rows = aantal;
		createVragen(max);
	}
	
	public void setItemScore(int item, int score) {
		if(scoreMax[item] <= 0) return;
		
		int percent = 100 * score / scoreMax[item];
		if(score < 0 ) {
			score = 0;
			percent = 0;
		}
		else if(percent > 100) percent = 100;
		vraagBars[item].setProgress(percent);
		vraagPunten[item].setText(score + " punt" + (score != 1?"en":""));
	}
	
	public void setItemScores(int[] scores) {
		for (int i = 0; i < rows; i++) {
			setItemScore(i, scores[i]);
		}
	}
	
	public void setBeantwoord(int aantal) {
		if(rows > 0)
		{
			beantwoord.setText( aantal + " / " + rows);
			beantwoordBar.setProgress(aantal * 100 / rows);
		}
	}
	
	public void setGotoOpdracht(GotoOpdracht listener)
	{
		this.listener = listener;
	}

	public void setOpdracht(int currentOpdracht) {
		vraagLabels[this.currentOpdracht].getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		this.currentOpdracht = currentOpdracht;
		vraagLabels[currentOpdracht].getElement().getStyle().setFontWeight(FontWeight.BOLD);	
	}

	public void setOpnieuw(boolean opnieuw) {
		this.opnieuw = opnieuw;
		reloadTotal.setVisible(opnieuw);
		
	}

	public void setItemOpnieuw(boolean itemOpnieuw) {
		this.itemOpnieuw = itemOpnieuw;
	}
	
}


