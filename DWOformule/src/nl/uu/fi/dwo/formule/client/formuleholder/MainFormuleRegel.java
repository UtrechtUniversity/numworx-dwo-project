package nl.uu.fi.dwo.formule.client.formuleholder;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;

public class MainFormuleRegel extends FormuleRegel implements IsWidget {

	private SVGImage svgImage;
	private OMSVGSVGElement svg;
	private final boolean isEditor;

	MainFormuleRegel(FormuleHolder holder) {
		super(holder);
		isEditor = false; // holder instanceof FormuleEditor;
		OMSVGDocument document = OMSVGParser.currentDocument();
		svg = document.createSVGSVGElement();
		svgImage = new SVGImage(svg);
	}

	@Override
	public Widget asWidget() {
		return isEditor ? getCanvas() : svgImage;
	}

	@Override
	public void paintObject() {
		for (FormuleElement e : children)
			e.paint();

		zetMaat();

		Context2d ctx = this.ctx;

		paintComponent(ctx);
		// Keep defs?.
		while (svg.getChildNodes().getLength() > 0) {
			svg.removeChild(svg.getLastChild());
		}
		if(!isEditor)
			paintComponent(svg);
		for (FormuleElement e : children) {
			if (!isEditor) {
				e.draw(svg);
			} else
				e.draw(ctx);
		}

		int x = this.width;
		if (this.currentPosition == -1 || this.children.isEmpty())
			x = 0;
		if (this.selectionStart == -1) // in dit geval geen selectie?
		{
			if(isEditor) this.drawCursor(x);
			else this.drawCursor(x,svg);
		}

		// draw selection line
		// ctx.setStrokeStyle("#f00");
		// ctx.setLineWidth(2.0);
		// this.drawline(ctx, selectioncords[0], selectioncords[1], selectioncords[2],
		// selectioncords[3]);

		if (children.isEmpty() && stippels) {
			if(isEditor) {
				String font = ctx.getFont();
				ctx.setFont(fm.getFontStyle());
				ctx.setFillStyle(color);
				ctx.fillText("...", 0, getAsHoogte());
				ctx.setFont(font);
			} else {
				OMSVGTextElement t = new OMSVGTextElement(0, getAsHoogte(), OMSVGLength.SVG_LENGTHTYPE_NUMBER, "...");
				OMSVGStyle style = t.getStyle();
				style.setFontSize(fm.getFontSize() , Unit.PX);
				style.setFontStyle(fm.isItalic() ? FontStyle.ITALIC: FontStyle.NORMAL);
				style.setSVGProperty(SVGConstants.CSS_FONT_FAMILY_PROPERTY, fm.getFont());
				style.setSVGProperty(SVGConstants.CSS_FILL_PROPERTY,color);
				style.setFontWeight(fm.isBold() ? FontWeight.BOLD : FontWeight.NORMAL);
				svg.appendChild(t);
			}
		}
	}

	public void paint() {
		if (!isChanged())
			return;
		// paint to canvas and svg
		super.paint();
		// copy canvas to svg
		svgImage.setPixelSize(width, height);
		svg.setHeight(Unit.PX, height);
		svg.setWidth(Unit.PX, width);
		svg.setViewBox(0, 0, width, height);
	}
}
