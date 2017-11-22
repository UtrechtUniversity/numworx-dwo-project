package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.dom.svg.utils.SVGConstants;

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
}
