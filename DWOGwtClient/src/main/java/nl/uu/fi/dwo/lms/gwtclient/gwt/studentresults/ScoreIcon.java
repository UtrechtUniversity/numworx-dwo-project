/**
 * 
 */
package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGRectElement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;

/**
 * @author peterboon
 *
 */
public class ScoreIcon extends ResizeComposite implements HasText {

	private static ScoreIconUiBinder uiBinder = GWT.create(ScoreIconUiBinder.class);
	private static final float WIDTH = 100f;
	
	interface ScoreIconUiBinder extends UiBinder<DockLayoutPanel, ScoreIcon> {
	}

	  @UiField HasText title;
	  @UiField OMSVGRectElement r01,r02,r03,r04;
	  @UiField OMSVGGElement poly, scale;
	  @UiField Widget image, icon;
	  private DockLayoutPanel root;
	  
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
	public ScoreIcon(DomStudentModelScore<?> s) {
		this();
	}

	public ScoreIcon() {
		initWidget(root = uiBinder.createAndBindUi(this));
	}
	
	
	
	
	public ScoreIcon(String title, DomStudentModelScore<?> s, int level) {
		this(s);
		setText(title);
		if (title.isEmpty())
		{
			root.setWidgetSize(image, 210);
			root.setWidgetHidden(icon, true);
			root.setStylePrimaryName("score-modelItem");
		} else {
			scale.getTransform().getBaseVal().clear();
			OMSVGRect viewbox = scale.getOwnerSVGElement().getViewBox().getBaseVal();
			viewbox.setX(-3f);
			viewbox.setWidth(105f);
		}
		
		getElement().getStyle().setMarginRight(level, Unit.EM);
		paint(s);
	}

	public ScoreIcon(String object) {
		this();
		setText(object);
		image.removeFromParent();
	}

	@Override
	public void setText(String text) {
		title.setText(text);
	}
	
	private void paint(DomStudentModelScore<?> s) {
		if (s.getRedCount() > 0) {
			float x = (float) (WIDTH * s.getRedScore());
			r03.getX().getBaseVal().setValue(x);
			r03.getWidth().getBaseVal().setValue(WIDTH/2f - x);
			poly.getTransform().getBaseVal().getItem(0).setTranslate(x, 0);
		} else if (s.getGreenCount() > 0) {
			float x = (float) (WIDTH * s.getGreenScore());
			r04.getWidth().getBaseVal().setValue(x - WIDTH/2);
			poly.getTransform().getBaseVal().getItem(0).setTranslate(x,0);
		}
		
	}

	public Widget imageOnly() {
		return image;
	}

	@Override
	public String getText() {
		return title.getText();
	}
}
