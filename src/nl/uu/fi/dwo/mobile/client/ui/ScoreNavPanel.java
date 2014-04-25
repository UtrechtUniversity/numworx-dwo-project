package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleClientBundle;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

public class ScoreNavPanel extends Composite {
	
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

        private int uploadStatus;

        public SimpleProgressBar(int i) {
                initWidget(widget);
                setProgress(i);
        }

        public int getProgress() {
                return uploadStatus;
        }

        public void setProgress(final int uploadStatus) {
                this.uploadStatus = uploadStatus;
                widget.setHTML(statusCellSafeHTMLTemplate.nostatus(uploadStatus));
        }

}

	
	
	static class ProgressBar extends Grid
	{

		static CssColor off = CssColor.make(180,180,180);
		static CssColor on  = CssColor.make(180,180,255);
		
		int progress;
		
		public ProgressBar() {
			super();
			setWidth("100%");
			setCellPadding(0);
			setCellSpacing(0);
		}

		private void initialize() {
			for(int i = 0; i < getColumnCount(); i++ ) {
				Widget widget = new ToggleButton(new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_groen()), new Image(FormuleHolder.FORMULE_BUNDLE.mw_kruisje_rood()));
				widget.setStylePrimaryName("progressToggle");
				setWidget(0, i, widget);
			}
			setProgress(progress);
		}
		public ProgressBar(int columns) {
			this(columns,0);
		}
		public ProgressBar(int columns, int progress) {
			super(1, columns);
			this.progress = progress;
			initialize();
		}

		public void setProgress(int v) {
			progress = v;
			for(int i = 0; i < getColumnCount(); i++ ) {
				boolean up  = i<v;
				ToggleButton bn = (ToggleButton) getWidget(0, i);
				bn.setDown(!up);
				if (!up && Math.random()>0.8)
					bn.getDownFace().setImage(new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_geel()));;
			}
		}

		public void setMax(int columns) {
			clear(true);
			super.resizeColumns(columns);
			initialize();
		}
		
	}
	

	VerticalPanel top;
	Label beantwoord;
	Label totaalscore;
	SimpleProgressBar totaalscoreBar;
	Grid  vragen;
	int rows = 10;
	public ScoreNavPanel() {
		top = new VerticalPanel();
		initialize();
		VerticalPanel vbox = new VerticalPanel();
		vbox.add(top);
		vbox.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		initWidget(vbox);
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

		top.add(grid);
		text = new Label("Beantwoord");
		grid.setWidget(0, 0, setFontFamily(text));
		text = new Label("Totaalscore");
		grid.setWidget(1, 0, setFontFamily(text));
		// dummy
		grid.setWidget(0, 1, new SimpleProgressBar(30));
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
		createVragen();
		top.add(vragen);
	}

	public Widget setFontFamily(Widget widget) {
		widget.getElement().getStyle().setFontSize(16, Unit.PX);
		widget.getElement().getStyle().setProperty("fontFamily", "Arial");
		return widget;
	}

	private void createVragen() {
		vragen.clear(true);
		vragen.resize(rows, 4);
		vragen.getColumnFormatter().setWidth(0, "80px");
		vragen.getColumnFormatter().setWidth(1, "160px");
		vragen.getColumnFormatter().setWidth(2, "80px");
		vragen.getColumnFormatter().setWidth(3, "8px");
		Label text;
		int totaal = 0;
		for(int i = 0; i < rows; i++) {
			double d = Math.random(); int punt = (int) ( d * 6 );
			totaal += punt;
			text = new Label("Vraag " + (i+1)); vragen.setWidget(i, 0, text);
			if(i == 4-1) text.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			setFontFamily(text);
			Widget widget = new ProgressBar(5,punt); vragen.setWidget(i, 1, widget); // dummy
			text = new Label( punt + " punt" + (punt != 1?"en":"")); vragen.setWidget(i,2, text); // dummy
			setFontFamily(text);
			text.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			TouchButton p = new TouchButton();p.setText(">"); vragen.setWidget(i, 3, p);
		}
		totaal *= 2;
		setTotaalScore(totaal);
		
	}

	public void setTotaalScore(int totaal) {
		totaalscore.setText(totaal + " %");
		totaalscoreBar.setProgress(totaal);
	}
}
