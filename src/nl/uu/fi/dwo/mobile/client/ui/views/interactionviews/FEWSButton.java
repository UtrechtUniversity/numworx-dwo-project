package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

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

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton;

public class FEWSButton extends SVGButton{

	private String code = "";
	
	public FEWSButton(String code) {
		super(code);
		this.code = code;
		
		if(isNoordhoff())
			defaultForegroundColor = CssColor.make("GRAY");
	}
	
	public FEWSButton(SVGResource resource) {
		super(resource);
	}
	
	protected void setBorderActive(boolean b) {
		if(isNoordhoff())
			return;
		else
			super.setBorderActive(b);
	}

	public void draw() {
		float w = width;
		float h = height;
		float e = w / 24;
		OMSVGRectElement rect = doc.createSVGRectElement(e, e, width - 2 * e, height - 2 * e, 2 * e, 2 * e);
		rect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, defaultBgColor.toString());
		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultBorderColor.toString());
		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + e);
		if(!isNoordhoff()) 
			svg.appendChild(rect);

		if ("down".equals(code)) {
			float d = h / 12;
			if(isNoordhoff()) {
				OMSVGPathElement pijlDown = doc.createSVGPathElement();
				OMSVGPathSegList segsPijlDown = pijlDown.getPathSegList();
				segsPijlDown.appendItem(pijlDown.createSVGPathSegMovetoAbs(6*e, 4*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(18*e, 4*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(18*e, 12*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(23*e, 12*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(12*e, 20*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(1*e, 12*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(6*e, 12*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(6*e, 4*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(18*e, 4*e));
				pijlDown.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_GRAY_VALUE);
				pijlDown.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_GRAY_VALUE);
				svg.appendChild(pijlDown);
			}
			else {
				OMSVGPathElement pijlDown = doc.createSVGPathElement();
				OMSVGPathSegList segsPijlDown = pijlDown.getPathSegList();
				segsPijlDown.appendItem(pijlDown.createSVGPathSegMovetoAbs(h / 2 - d, 2 * d));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(h / 2 + d, 2 * d));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(h / 2 + d, h / 2 + d));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(h / 2 + 3 * d, h / 2 - d));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(h / 2 + 3 * d, h / 2 + d));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(h / 2, h / 2 + 4 * d));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(h / 2 - 3 * d, h / 2 + d));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(h / 2 - 3 * d, h / 2 - d));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(h / 2 - d, h / 2 + d));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(h / 2 - d, 2 * d));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegClosePath());
				pijlDown.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultForegroundColor.toString());
				pijlDown.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, defaultForegroundColor.toString());
				svg.appendChild(pijlDown);
			}
		}
		else if ("up".equals(code)) {
			float d = h / 12;
			if(isNoordhoff()) {
				OMSVGPathElement pijlDown = doc.createSVGPathElement();
				OMSVGPathSegList segsPijlDown = pijlDown.getPathSegList();
				segsPijlDown.appendItem(pijlDown.createSVGPathSegMovetoAbs(6*e, 20*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(18*e, 20*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(18*e, 12*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(23*e, 12*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(12*e, 4*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(1*e, 12*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(6*e, 12*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(6*e, 20*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(18*e, 20*e));
				pijlDown.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_GRAY_VALUE);
				pijlDown.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_GRAY_VALUE);
				svg.appendChild(pijlDown);
			}
			else {
				OMSVGPathElement pijlUp = doc.createSVGPathElement();
				OMSVGPathSegList segsPijlUp = pijlUp.getPathSegList();
				segsPijlUp.appendItem(pijlUp.createSVGPathSegMovetoAbs(h / 2 - d, h - 2 * d));
				segsPijlUp.appendItem(pijlUp.createSVGPathSegLinetoAbs(h / 2 + d, h - 2 * d));
				segsPijlUp.appendItem(pijlUp.createSVGPathSegLinetoAbs(h / 2 + d, h / 2 - d));
				segsPijlUp.appendItem(pijlUp.createSVGPathSegLinetoAbs(h / 2 + 3 * d, h / 2 + d));
				segsPijlUp.appendItem(pijlUp.createSVGPathSegLinetoAbs(h / 2 + 3 * d, h / 2 - d));
				segsPijlUp.appendItem(pijlUp.createSVGPathSegLinetoAbs(h / 2, h / 2 - 4 * d));
				segsPijlUp.appendItem(pijlUp.createSVGPathSegLinetoAbs(h / 2 - 3 * d, h / 2 - d));
				segsPijlUp.appendItem(pijlUp.createSVGPathSegLinetoAbs(h / 2 - 3 * d, h / 2 + d));
				segsPijlUp.appendItem(pijlUp.createSVGPathSegLinetoAbs(h / 2 - d, h / 2 - d));
				segsPijlUp.appendItem(pijlUp.createSVGPathSegLinetoAbs(h / 2 - d, h - 2 * d));
				segsPijlUp.appendItem(pijlUp.createSVGPathSegClosePath());
				pijlUp.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultForegroundColor.toString());
				pijlUp.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, defaultForegroundColor.toString());
				svg.appendChild(pijlUp);
			}
		}

		else if ("copy".equals(code)) {
			
				
			OMSVGRectElement copyrect2 = doc.createSVGRectElement(4 * e, 4 * e, width - 12 * e, height - 12 * e, e, e);
			copyrect2.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_WHITE_VALUE);
			copyrect2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultForegroundColor.toString());
			copyrect2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + e);
			svg.appendChild(copyrect2);

			OMSVGRectElement copyrect1 = doc.createSVGRectElement(8 * e, 8 * e, width - 12 * e, height - 12 * e, e, e);
			copyrect1.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_WHITE_VALUE);
			copyrect1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultForegroundColor.toString());
			copyrect1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + e);
			svg.appendChild(copyrect1);

			OMSVGLineElement regel1 = doc.createSVGLineElement(11 * e, 11 * e, 17 * e, 11 * e);
			regel1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultForegroundColor.toString());
			regel1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + e);
			svg.appendChild(regel1);

			OMSVGLineElement regel2 = doc.createSVGLineElement(11 * e, 14 * e, 17 * e, 14 * e);
			regel2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultForegroundColor.toString());
			regel2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1.0");
			svg.appendChild(regel2);

			OMSVGLineElement regel3 = doc.createSVGLineElement(11 * e, 17 * e, 17 * e, 17 * e);
			regel3.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultForegroundColor.toString());
			regel3.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + e);
			svg.appendChild(regel3);
		}
		else if("plus".equals(code))	{	
			OMSVGLineElement stroke1 = doc.createSVGLineElement(8*e, h/2, 16*e, h/2);
			stroke1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultTextColor.toString());
			stroke1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 1.5*e);
			svg.appendChild(stroke1);
			
			OMSVGLineElement stroke2 = doc.createSVGLineElement(w/2, 8*e, w/2, 16*e);
			stroke2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultTextColor.toString());
			stroke2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 1.5*e);
			svg.appendChild(stroke2);
		}
		else if("min".equals(code))	{	
			OMSVGLineElement stroke1 = doc.createSVGLineElement(8*e, h/2, 16*e, h/2);
			stroke1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultTextColor.toString());
			stroke1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 1.5*e);
			svg.appendChild(stroke1);
		}
		else if("maal".equals(code))	{	
			OMSVGLineElement stroke1 = doc.createSVGLineElement(8*e, 8*e, 16*e, 16*e);
			stroke1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultTextColor.toString());
			stroke1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 1.5*e);
			svg.appendChild(stroke1);
			
			OMSVGLineElement stroke2 = doc.createSVGLineElement(8*e, h-8*e, w-8*e, 8*e);
			stroke2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultTextColor.toString());
			stroke2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 1.5*e);
			svg.appendChild(stroke2);
		}
		else if("deel".equals(code))	{	
			OMSVGCircleElement punt1 = doc.createSVGCircleElement(w/2, h/3, e);
			OMSVGCircleElement punt2 = doc.createSVGCircleElement(w/2, h-h/3, e);
			punt1.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, defaultTextColor.toString());
			punt2.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, defaultTextColor.toString());
			svg.appendChild(punt1);
			svg.appendChild(punt2);
			OMSVGLineElement stroke = doc.createSVGLineElement(8*e, h/2, 16*e, h/2);
			stroke.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultTextColor.toString());
			stroke.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 1.5*e);
			svg.appendChild(stroke);
		}
		else if("haakjes".equals(code)) {
			OMSVGPathElement haak1 = doc.createSVGPathElement();
	        OMSVGPathSegList segsHaak1 = haak1.getPathSegList();
	        segsHaak1.appendItem(haak1.createSVGPathSegMovetoAbs(11*e, 5*e));
	        segsHaak1.appendItem(haak1.createSVGPathSegCurvetoQuadraticAbs(11*e, h-5*e, 5*e, h/2));
	        segsHaak1.appendItem(haak1.createSVGPathSegMovetoAbs(13*e, 5*e));
	        segsHaak1.appendItem(haak1.createSVGPathSegCurvetoQuadraticAbs(13*e, h-5*e, 19*e, h/2));
	        segsHaak1.appendItem(haak1.createSVGPathSegMovetoAbs(5*e, 5*e));
	        segsHaak1.appendItem(haak1.createSVGPathSegLinetoAbs(w-5*e, h-5*e));
	        haak1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultTextColor.toString());
	        haak1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1.5");
	        haak1.getStyle().setSVGProperty(SVGConstants.CSS_FILL_OPACITY_PROPERTY, "0.0");
	        svg.appendChild(haak1);
		}
		else if("herleid".equals(code)) {
			OMSVGRectElement rect1 = doc.createSVGRectElement(6*e, 5*e, 4*e, 11*e, 0f, 0f);
			rect1.getStyle().setSVGProperty(SVGConstants.CSS_FILL_OPACITY_PROPERTY, "0.0");
			rect1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1.5");
			rect1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultTextColor.toString());
	    		svg.appendChild(rect1);
	    		
	    		OMSVGRectElement rect2 = doc.createSVGRectElement(14*e, 5*e, 4*e, 11*e, 0f, 0f);
			rect2.getStyle().setSVGProperty(SVGConstants.CSS_FILL_OPACITY_PROPERTY, "0.0");
			rect2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1.5");
			rect2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultTextColor.toString());
	    		svg.appendChild(rect2);
	    		
	    		OMSVGPathElement haak = doc.createSVGPathElement();
		    OMSVGPathSegList segsHaak = haak.getPathSegList();
		    segsHaak.appendItem(haak.createSVGPathSegMovetoAbs(5*e, 18*e));
		    segsHaak.appendItem(haak.createSVGPathSegLinetoAbs(6*e, 19*e));
		    segsHaak.appendItem(haak.createSVGPathSegLinetoAbs(18*e, 19*e));
		    segsHaak.appendItem(haak.createSVGPathSegLinetoAbs(19*e, 18*e));
		    haak.getStyle().setSVGProperty(SVGConstants.CSS_FILL_OPACITY_PROPERTY, "0.0");
		    haak.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1.5");
		    haak.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultTextColor.toString());
			svg.appendChild(haak);    
	    		
		}
		else if("ontbind".equals(code)) {
			OMSVGPathElement haak1 = doc.createSVGPathElement();
	        OMSVGPathSegList segsHaak1 = haak1.getPathSegList();
	        segsHaak1.appendItem(haak1.createSVGPathSegMovetoAbs(8*e, 5*e));
	        segsHaak1.appendItem(haak1.createSVGPathSegCurvetoQuadraticAbs(8*e, h-5*e, 14*e, h/2));
	        segsHaak1.appendItem(haak1.createSVGPathSegMovetoAbs(17*e, 5*e));
	        segsHaak1.appendItem(haak1.createSVGPathSegCurvetoQuadraticAbs(17*e, h-5*e, 11*e, h/2));
	       
	        haak1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultTextColor.toString());
	        haak1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1.5");
	        haak1.getStyle().setSVGProperty(SVGConstants.CSS_FILL_OPACITY_PROPERTY, "0.0");
	        svg.appendChild(haak1);
		}
		else if("splits".equals(code)) {
			OMSVGPathElement pijl = doc.createSVGPathElement();
	        OMSVGPathSegList segsPijl = pijl.getPathSegList();
	        segsPijl.appendItem(pijl.createSVGPathSegMovetoAbs(w/2, 5*e));
	        segsPijl.appendItem(pijl.createSVGPathSegLinetoAbs(w/2, h/2));
	        segsPijl.appendItem(pijl.createSVGPathSegLinetoAbs(6, h-6));
	        segsPijl.appendItem(pijl.createSVGPathSegMovetoAbs(6, h-10));
	        segsPijl.appendItem(pijl.createSVGPathSegLinetoAbs(6, h-6));
	        segsPijl.appendItem(pijl.createSVGPathSegLinetoAbs(10, h-6));
	        segsPijl.appendItem(pijl.createSVGPathSegMovetoAbs(w/2, h/2));
	        segsPijl.appendItem(pijl.createSVGPathSegLinetoAbs(w-6, h-6));
	        segsPijl.appendItem(pijl.createSVGPathSegMovetoAbs(w-6, h-10));
	        segsPijl.appendItem(pijl.createSVGPathSegLinetoAbs(w-6, h-6));
	        segsPijl.appendItem(pijl.createSVGPathSegLinetoAbs(w-10, h-6));
	        
	        pijl.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultTextColor.toString());
	        pijl.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1.5");
	        pijl.getStyle().setSVGProperty(SVGConstants.CSS_FILL_OPACITY_PROPERTY, "0.0");
	        svg.appendChild(pijl);
		}
		else if("wortelbewerk".equals(code)) {
			OMSVGPathElement wortel = doc.createSVGPathElement();
	        OMSVGPathSegList segsWortel = wortel.getPathSegList();
	        segsWortel.appendItem(wortel.createSVGPathSegMovetoAbs(6*e, 14*e));
	        segsWortel.appendItem(wortel.createSVGPathSegLinetoAbs(9*e, 18*e));
	        segsWortel.appendItem(wortel.createSVGPathSegLinetoAbs(12*e, 6*e));
	        segsWortel.appendItem(wortel.createSVGPathSegLinetoAbs(19*e, 6*e));
	        
	        wortel.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultTextColor.toString());
	        wortel.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1.5");
	        wortel.getStyle().setSVGProperty(SVGConstants.CSS_FILL_OPACITY_PROPERTY, "0.0");
	        svg.appendChild(wortel);
	        
		}
		else if("rekenmachine".equals(code)) {
			OMSVGRectElement copyrect2 = doc.createSVGRectElement(5 * e, 4.5f*e, 14 * e, 15 * e, e, e);
			copyrect2.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, defaultForegroundColor.toString());
			copyrect2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultForegroundColor.toString());
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
			stroke1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultForegroundColor.toString());
			stroke1.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 2*e);
			svg.appendChild(stroke1);
			
			OMSVGLineElement stroke2 = doc.createSVGLineElement(6*e, h-6*e, w-6*e, 6*e);
			stroke2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultForegroundColor.toString());
			stroke2.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 2*e);
			svg.appendChild(stroke2);
		}
		else {
			OMSVGTextElement label = doc.createSVGTextElement(4*e,h/2+3*e, OMSVGLength.SVG_LENGTHTYPE_PX, text);
			label.getStyle().setSVGProperty(SVGConstants.CSS_FONT_SIZE_PROPERTY, "12");
			label.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, defaultForegroundColor.toString());
			label.getStyle().setSVGProperty(SVGConstants.CSS_FONT_WEIGHT_PROPERTY,  SVGConstants.CSS_NORMAL_VALUE);
			svg.appendChild(label);
		}

	}
	
	public static boolean isNoordhoff() {
		String dependentName = DWOplayer.PARAMETERS.keyboardStyle();
		return "noordhoff".equals(dependentName);
	}
}
