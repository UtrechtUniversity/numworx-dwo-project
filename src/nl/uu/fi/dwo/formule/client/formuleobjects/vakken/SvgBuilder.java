package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.dom.svg.OMSVGPathSegArcAbs;
import org.vectomatic.dom.svg.OMSVGPathSegLinetoAbs;
import org.vectomatic.dom.svg.OMSVGPathSegMovetoAbs;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import nl.uu.fi.dwo.interaction.client.FormuleFont;

public class SvgBuilder implements PathBuilder {

	final OMSVGElement svg;
	OMSVGPathElement path;
	final float offx, offy;
	String stroke = "black";
	String fill = "none";
	String width = "1";

	public SvgBuilder(OMSVGElement svg, float x, float y) {
		this.svg = svg;
		path = new OMSVGPathElement();
		offx = x;
		offy = y;
	} 
	
	public void moveTo(float x, float y) {
		OMSVGPathSeg seg = path.createSVGPathSegMovetoAbs(x+offx, y+offy);
		path.getPathSegList().appendItem(seg);
	}
	public void lineTo(float x, float y) {
		OMSVGPathSeg seg = path.createSVGPathSegLinetoAbs(x+offx, y+offy);
		path.getPathSegList().appendItem(seg);
	}
	
	public void setStrokeStyle(String color) {
		stroke = color;
	}
	
	public void setLineWidth(double d) {
		width = Double.toString(d);
	}

	public void stroke() {
		path.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, stroke);
		path.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_NONE_VALUE);
		path.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, width);
		svg.appendChild(path);
		path = null;
	}

	@Override
	public void beginPath() {
		path = new OMSVGPathElement();		
	}

	@Override
	public void drawline(float x1, float y1, float x2, float y2) {
		OMSVGLineElement path = new OMSVGLineElement(x1+offx, y1+offy, x2+offx, y2+offy);
		path.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, stroke);
		path.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, width);
		svg.appendChild(path);
	}

	@Override
	public void setFillStyle(String color) {
		fill = color;
	}

	String fontFamily = "sans-serif";
	FontStyle fontStyle = FontStyle.NORMAL;
	FontWeight fontWeight = FontWeight.NORMAL;
	int fontSize = 12;

	@Override
	public void setFont(FormuleFont fm) {
		fontFamily = fm.getFont();
		fontStyle = fm.isItalic() ? FontStyle.ITALIC : FontStyle.NORMAL;
		fontWeight = fm.isBold() ? FontWeight.BOLD : FontWeight.NORMAL;
		fontSize = fm.getFontSize();		
	}

	@Override
	public void fillText(String data, float x, float y) {
		OMSVGTextElement t = text(data, x+offx, y+offy);
		t.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, fill);
		svg.appendChild(t);
	}

	protected OMSVGTextElement text(String data, float x, float y) {
		OMSVGTextElement t = new OMSVGTextElement(x, y, OMSVGLength.SVG_LENGTHTYPE_NUMBER, data);
		t.getStyle().setFontSize(fontSize , Unit.PX);
		t.getStyle().setFontStyle(fontStyle);
		t.getStyle().setFontWeight(fontWeight);
		t.getStyle().setSVGProperty(SVGConstants.CSS_FONT_FAMILY_PROPERTY, fontFamily);
		t.getStyle().setWhiteSpace(WhiteSpace.PRE);
		t.setXmlspace(SVGConstants.SVG_PRESERVE_VALUE);
		return t;
	}

	private static SVGImage invisible; 
	static {
		OMSVGDocument document = OMSVGParser.currentDocument();
		OMSVGSVGElement svg = document.createSVGSVGElement();
		invisible = new SVGImage(svg);
		invisible.setPixelSize(1, 1);
		svg.setHeight(Unit.PX, 1);
		svg.setWidth(Unit.PX, 1);
		svg.setViewBox(0, 0, 1, 1);
		invisible.getStyle().setVisibility(Visibility.HIDDEN);
		final RootLayoutPanel root = RootLayoutPanel.get();
		root.add(invisible);
		root.setWidgetBottomHeight(invisible, 0, Unit.PX, 1, Unit.PX);
		root.setWidgetRightWidth(invisible, 0, Unit.PX, 1, Unit.PX);
	}
	
	
	@Override
	public FontMetrics measureText(String string) {
		OMSVGTextElement t = text(string,0,0);
		OMSVGElement svg = invisible.getSvgElement();
		svg.appendChild(t);
		OMSVGRect r = t.getBBox();
		float w = r.getWidth();
		float a = -r.getY();
		float d = r.getMaxY();
		float h = r.getHeight();
		svg.removeChild(t);
		return new FontMetrics(w);
	}

	@Override
	public void arc(double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise) {
	// convert 
		float startx = (float)(Math.cos(startAngle)*radius+x) + offx;
		float starty = (float)(Math.sin(startAngle)*radius+y) + offy;
		float endx   = (float)(Math.cos(endAngle)*radius+x) + offx;
		float endy   = (float)(Math.sin(endAngle)*radius+y) + offy;
		float r = (float) radius;
		OMSVGPathSegMovetoAbs seg1 = path.createSVGPathSegMovetoAbs(startx, starty);
		path.getPathSegList().appendItem(seg1);
		if(anticlockwise) {
			while(endAngle > startAngle) endAngle -= (Math.PI*2);
		} else {
			while(endAngle < startAngle) endAngle += (Math.PI*2);
		}
		float angle = (float)Math.abs(endAngle-startAngle);
		boolean largeArcFlag = angle > Math.PI;
		boolean sweepFlag = !anticlockwise;
		OMSVGPathSegArcAbs seg2 = path.createSVGPathSegArcAbs(endx, endy, r, r, angle, largeArcFlag, sweepFlag);
		path.getPathSegList().appendItem(seg2);

		
//		OMSVGPathSegLinetoAbs seg3 = path.createSVGPathSegLinetoAbs(endx, endy);
//		path.getPathSegList().appendItem(seg1);
//		path.getPathSegList().appendItem(seg3);
		
		
	}
}
