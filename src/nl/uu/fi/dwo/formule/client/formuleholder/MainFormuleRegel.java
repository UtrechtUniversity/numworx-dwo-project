package nl.uu.fi.dwo.formule.client.formuleholder;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGImageElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;

class MainFormuleRegel extends FormuleRegel implements IsWidget {

	private final SVGImage image;
	private final OMSVGSVGElement element;
	private final OMSVGDocument document;
	private OMSVGImageElement svgcanvas;
	
	MainFormuleRegel(FormuleHolder holder) {
		super(holder);
		document = OMSVGParser.currentDocument();
		element = document.createSVGSVGElement();
		image = new SVGImage(element);
		svgcanvas = document.createSVGImageElement();
		element.appendChild(svgcanvas);
	}

	@Override
	public Widget asWidget() {
		return image;
	}

	public void paint() {
		if (!isChanged()) return;
// paint to canvsa
		super.paint();
// copy canvas to svg
		svgcanvas.getWidth().getBaseVal().setValue(width);
		svgcanvas.getHeight().getBaseVal().setValue(height);
		svgcanvas.getHref().setBaseVal(canvas.toDataUrl());
		image.setPixelSize(width, height);
		element.setHeight(Unit.PX, height);
		element.setWidth(Unit.PX, width);
		element.setViewBox(0, 0, width, height);
		
	}
}
