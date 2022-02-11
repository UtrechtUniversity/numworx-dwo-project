package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.canvas.dom.client.CssColor;

import nl.uu.fi.dwo.mobile.client.ui.SVGButton;

public class BerekeningVakButton extends SVGButton{

	private String code = "";
	
	public BerekeningVakButton(String code) {
		super(code);
		this.code = code;
	}
	
	public BerekeningVakButton(SVGResource resource) {
		super(resource);
	}
	
	protected void setBorderActive(boolean b) {
		super.setBorderActive(b);
	}

	public void draw() {
		float w = width;
		float h = height;
		float e = w / 24;
		OMSVGRectElement rect = doc.createSVGRectElement(e, e, width - 2 * e, height - 2 * e, 1 * e, 1 * e);
		rect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, bgColor.toString());
		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, borderColor.toString());
		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + e);
		svg.appendChild(rect);

	
		if("rekenmachine".equals(code)) {
			OMSVGRectElement copyrect2 = doc.createSVGRectElement(5 * e, 4.5f*e, 14 * e, 15 * e, e, e);
			copyrect2.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, foregroundColor.toString());
			copyrect2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, foregroundColor.toString());
			copyrect2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 0.7f*e);
			svg.appendChild(copyrect2);

			OMSVGRectElement copyrect1 = doc.createSVGRectElement(7.25f*e, 7.0f*e, 9.5f*e, e, 0.5f*e, 0.5f*e);
			copyrect1.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_WHITE_VALUE);
			copyrect1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_WHITE_VALUE);
			copyrect1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 1.5*e);
			svg.appendChild(copyrect1);

			OMSVGCircleElement punt1 = doc.createSVGCircleElement(8*e, 12.5f*e, 1.5f*e);
			OMSVGCircleElement punt2 = doc.createSVGCircleElement(12*e, 12.5f*e, 1.5f*e);
			OMSVGCircleElement punt3 = doc.createSVGCircleElement(16*e, 12.5f*e, 1.5f*e);
			OMSVGCircleElement punt4 = doc.createSVGCircleElement(8*e, 16.5f*e, 1.5f*e);
			OMSVGCircleElement punt5 = doc.createSVGCircleElement(12*e, 16.5f*e, 1.5f*e);
			OMSVGCircleElement punt6 = doc.createSVGCircleElement(16*e, 16.5f*e, 1.5f*e);
			
			punt1.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_WHITE_VALUE);
			punt2.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_WHITE_VALUE);
			punt3.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_WHITE_VALUE);
			punt4.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_WHITE_VALUE);
			punt5.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_WHITE_VALUE);
			punt6.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_WHITE_VALUE);
			
			svg.appendChild(punt1);
			svg.appendChild(punt2);
			svg.appendChild(punt3);
			svg.appendChild(punt4);
			svg.appendChild(punt5);
			svg.appendChild(punt6);
	        
		}
		else if("sluit".equals(code))	{	
			OMSVGLineElement stroke1 = doc.createSVGLineElement(6*e, 6*e, 18*e, 18*e);
			stroke1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, foregroundColor.toString());
			stroke1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 2*e);
			svg.appendChild(stroke1);
			
			OMSVGLineElement stroke2 = doc.createSVGLineElement(6*e, h-6*e, w-6*e, 6*e);
			stroke2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, foregroundColor.toString());
			stroke2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 2*e);
			svg.appendChild(stroke2);
		}
		else {
			OMSVGTextElement label = doc.createSVGTextElement(4*e,h/2+3*e, OMSVGLength.SVG_LENGTHTYPE_PX, text);
			label.getStyle().setSVGProperty(SVGConstants.CSS_FONT_SIZE_PROPERTY, "12");
			label.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, foregroundColor.toString());
			label.getStyle().setSVGProperty(SVGConstants.CSS_FONT_WEIGHT_PROPERTY,  SVGConstants.CSS_NORMAL_VALUE);
			svg.appendChild(label);
		}
	}
}



