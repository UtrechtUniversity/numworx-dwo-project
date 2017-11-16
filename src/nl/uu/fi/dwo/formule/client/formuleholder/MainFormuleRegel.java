package nl.uu.fi.dwo.formule.client.formuleholder;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGImageElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleTeken;

class MainFormuleRegel extends FormuleRegel implements IsWidget {

	private SVGImage svgImage;
	private OMSVGSVGElement svg;
	// private OMSVGDocument document;
	private OMSVGImageElement svgcanvas;

	MainFormuleRegel(FormuleHolder holder) {
		super(holder);
		OMSVGDocument document = OMSVGParser.currentDocument();
		svg = document.createSVGSVGElement();
		svgImage = new SVGImage(svg);
		svgcanvas = document.createSVGImageElement();
		svg.appendChild(svgcanvas);
	}

	@Override
	public Widget asWidget() {
		return svgImage;
	}

	@Override
	public void paintObject() {
		for (FormuleElement e : children)
			e.paint();

		zetMaat();

		Context2d ctx = this.ctx;

		paintComponent(ctx);
		// Keep canvas.
		while (svg.getChildNodes().getLength() > 1) {
			svg.removeChild(svg.getLastChild());
		}
		for (FormuleElement e : children) {
			if (e instanceof FormuleTeken && holder instanceof FormuleViewer) {
				FormuleTeken ft = (FormuleTeken) e;
				ft.draw(svg, ctx);
			} else

				e.draw(ctx);
		}

		int x = this.width;
		if (this.currentPosition == -1 || this.children.isEmpty())
			x = 0;
		if (this.selectionStart == -1) // in dit geval geen selectie?
		{
			this.drawCursor(x);
		}

		// draw selection line
		// ctx.setStrokeStyle("#f00");
		// ctx.setLineWidth(2.0);
		// this.drawline(ctx, selectioncords[0], selectioncords[1], selectioncords[2],
		// selectioncords[3]);

		if (children.isEmpty() && stippels) {
			String font = ctx.getFont();
			ctx.setFont(fm.getFontStyle());
			ctx.setFillStyle(color);
			ctx.fillText("...", 0, getAsHoogte());
			ctx.setFont(font);
		}
	}

	public void paint() {
		if (!isChanged())
			return;
		// paint to canvas
		super.paint();
		// copy canvas to svg
		svgcanvas.getWidth().getBaseVal().setValue(width);
		svgcanvas.getHeight().getBaseVal().setValue(height);
		svgcanvas.getHref().setBaseVal(canvas.toDataUrl());
		svgImage.setPixelSize(width, height);
		svg.setHeight(Unit.PX, height);
		svg.setWidth(Unit.PX, width);
		svg.setViewBox(0, 0, width, height);

	}

}
