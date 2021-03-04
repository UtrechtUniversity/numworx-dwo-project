package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.dom.svg.utils.SVGConstants;

import nl.uu.fi.dwo.mobile.client.ui.SVGButton;

public class FullScreenButton extends SVGButton{

	
	public FullScreenButton(String code) {
		super(code);
	}
	public int getWidth() {
	  return width;
	}
	public int getHeight() {
	  return height;
	}
	public String getText() {
	  return text;
	}
	
	public FullScreenButton(SVGResource resource) {
		super(resource);
	}
	
	protected void setBorderActive(boolean b) {
		super.setBorderActive(b);
	}

	public void draw() {
		float w = width;
		float h = height;
		float e = w / 24;
		float d = 10;
		float m = 4;
		float x = w/2;
		float y = w/2;
		float r = 4; 
		float p = 5;
		OMSVGRectElement rect = doc.createSVGRectElement(e, e, width - 2 * e, height - 2 * e, 1 * e, 1 * e);
		rect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, bgColor.toString());
		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, borderColor.toString());
		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + e);
		

		if ("open".equals(text)) {
			OMSVGPathElement open = doc.createSVGPathElement();
			OMSVGPathSegList segsPijlDown = open.getPathSegList();
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x+r, y+r));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x+d, y+d));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x+d-p, y+d));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x+d, y+d));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x+d, y+d-p));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x-r, y-r));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x-d, y-d));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x-d+p, y-d));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x-d, y-d));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x-d, y-d+p));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x+r, y-r));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x+d, y-d));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x+d-p, y-d));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x+d, y-d));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x+d, y-d+p));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x-r, y+r));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x-d, y+d));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x-d+p, y+d));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x-d, y+d));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x-d, y+d-p));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x,y));
			
			
			segsPijlDown.appendItem(open.createSVGPathSegClosePath());
			open.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 2.0*e);
			open.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, foregroundColor.toString());
			open.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, "transparent");
			svg.appendChild(open);
		}
		else if ("close".equals(text)) {
			OMSVGPathElement open = doc.createSVGPathElement();
			OMSVGPathSegList segsPijlDown = open.getPathSegList();
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x+d, y+d));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x+r, y+r));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x+r+p, y+r));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x+r, y+r));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x+r, y+r+p));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x-d, y-d));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x-r, y-r));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x-r-p, y-r));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x-r, y-r));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x-r, y-r-p));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x+d, y-d));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x+r, y-r));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x+r+p, y-r));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x+r, y-r));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x+r, y-r-p));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x-d, y+d));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x-r, y+r));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x-r-p, y+r));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x-r, y+r));
			segsPijlDown.appendItem(open.createSVGPathSegLinetoAbs(x-r, y+r+p));
			segsPijlDown.appendItem(open.createSVGPathSegMovetoAbs(x,y));
			
			
			segsPijlDown.appendItem(open.createSVGPathSegClosePath());
			open.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 2.0*e);
			open.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, foregroundColor.toString());
			open.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, "transparent");
			svg.appendChild(open);
			
		}
	}
		
}
