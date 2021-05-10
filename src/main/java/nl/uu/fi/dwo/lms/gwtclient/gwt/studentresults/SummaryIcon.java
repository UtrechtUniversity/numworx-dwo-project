/**
 * 
 */
package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.ui.SVGImage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;

/**
 * @author peterboon
 *
 */
public class SummaryIcon extends ResizeComposite {

	private static SummaryIconUiBinder uiBinder = GWT.create(SummaryIconUiBinder.class);

	interface SummaryIconUiBinder extends UiBinder<DockLayoutPanel, SummaryIcon> {
	}

	  int green, red, white;
	  String redText, greenText;
	  @UiField InlineLabel title;
	  @UiField OMSVGRectElement r01,r02,r03,r04,r05,r06,r07,r08,r09,r10,r11,r12,r13,r14,r15,r16,r17,r18,r19,r20;
	  @UiField OMSVGGElement scale;
	  @UiField SummaryIconCSS style;
	  @UiField SVGImage image, icon;
	  private DockLayoutPanel root;
	  
	  private OMSVGRectElement r[] = new OMSVGRectElement[20];
	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	  *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public SummaryIcon(DomStudentModelScore<?> s) {
		this();
	    double green,red;
	    if (s.getGreenCount() > 0) {
	      green = (s.getGreenScore()/s.getGreenCount() - 0.5) * s.getGreenCount()/s.getTotalCount();
	    } else green = 0;
	    if (s.getRedCount() > 0) {
	      red = (0.5-s.getRedScore()/s.getRedCount()) * s.getRedCount()/s.getTotalCount();
	    } else red = 0;

	    calculate(green, red);
	}

	public SummaryIcon() {
		initWidget(root = uiBinder.createAndBindUi(this));
		r = new OMSVGRectElement[] { r01,r02,r03,r04,r05,r06,r07,r08,r09,r10,r11,r12,r13,r14,r15,r16,r17,r18,r19,r20 };
	}
	
	public SummaryIcon(String title) {
		this();
		setText(title);
		image.removeFromParent();
	}
	
	public SummaryIcon(String title, DomStudentModelScore<?> s, int level) {
		this(s);
		setText(title);
		if (title.isEmpty()) {
			root.setWidgetSize(image, 210);
			root.setStylePrimaryName("score-modelItem");
			root.setWidgetHidden(icon, true);
		} else {
			scale.getTransform().getBaseVal().clear();
			OMSVGRect viewbox = scale.getOwnerSVGElement().getViewBox().getBaseVal();
			viewbox.setX(-3f);
			viewbox.setWidth(105f);
		}
		
		
		getElement().getStyle().setMarginRight(level, Unit.EM);
		paint();
	}

	public void setText(String text) {
		title.setText(text);
	}
	
	private void calculate(double green, double red) {
		    this.green = Math.round((float) green * 40);
		    if (this.green == 0 && green > 0) this.green = 1;
		    this.red = Math.round((float)red * 40);
		    if(this.red == 0 && red > 0) this.red = 1;    
		    white = 20 - this.red - this.green;
		    redText = Math.round(red*200)+"%";
		    greenText = Math.round(green*200)+"%";
	}

	private void paint() {
		int i = 0;
		String s = style.green();
		for(i = 0; i < r.length; i++) {
			if (i < green) s = style.green();
			else if (i < green + red ) s = style.red();
			else s = style.white();
			r[i].setClassNameBaseVal(s);
		}
		
	}
	
	public Widget imageOnly() {
		return image;
	}
}
