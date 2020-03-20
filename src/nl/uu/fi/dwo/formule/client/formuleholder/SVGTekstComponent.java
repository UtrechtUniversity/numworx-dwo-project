package nl.uu.fi.dwo.formule.client.formuleholder;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.TekstComponent;

public class SVGTekstComponent extends TekstComponent {

	
	private SVGImage svgImage;

	public SVGTekstComponent(FormuleFont fm, String tekst, int width, int height) {
		super(fm, tekst, width, height);
		OMSVGDocument document = OMSVGParser.currentDocument();
		OMSVGSVGElement svg = document.createSVGSVGElement();
		svgImage = new SVGImage(svg);
		svgImage.setPixelSize(width, height);
		svg.setHeight(Unit.PX, height);
		svg.setWidth(Unit.PX, width);
		svg.setViewBox(0, 0, width, height);
		int ashoogte = fm.getAscent();
		OMSVGTextElement t = new OMSVGTextElement(0, ashoogte, OMSVGLength.SVG_LENGTHTYPE_NUMBER, tekst);
		int fs = fm.getFontSize();
		t.getStyle().setFontSize(fs , Unit.PX);
		t.getStyle().setFontStyle(fm.isItalic() ? FontStyle.ITALIC: FontStyle.NORMAL);
		t.getStyle().setFontWeight(fm.isBold() ? FontWeight.BOLD: FontWeight.NORMAL);
		t.getStyle().setSVGProperty(SVGConstants.CSS_FONT_FAMILY_PROPERTY, fm.getFont());
		t.getStyle().setWhiteSpace(WhiteSpace.PRE);
		t.setXmlspace(SVGConstants.SVG_PRESERVE_VALUE);
		svg.appendChild(t);
	}

	@Override
	public void setColor(CssColor c) {
		super.setColor(c);
		svgImage.getSvgElement().getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, c.toString());
	}


//	@Override
//	public Panel getAsPanel() {
//		TouchPanel sp = new TouchPanel();
//		sp.add(this.svgImage);
//		return sp;
//	}

	@Override
	public Widget asWidget() {
		return svgImage;
	}


}
