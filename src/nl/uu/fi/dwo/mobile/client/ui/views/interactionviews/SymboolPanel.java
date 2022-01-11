package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMNodeList;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.CanvasBuilder;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.PathBuilder;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.SvgBuilder;
import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.TekstComponent;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;


public class SymboolPanel implements InteractionStub, FacetAware
{

	static final String holderId = "dockholder";
	private HashMap<String, Object> launchState; 
	
	int breedte = 100;
	int hoogte = 100; 
	private boolean volledigeBreedte = false;
	int ashoogte = 12;
	
	private Canvas symboolCanvas;
	private final SVGImage symboolSVG;
	private PathBuilder ctx;
	private Context2d ctx0;
	
	private int dikte, richting, type;
	private CssColor kleur;
	private boolean vulHoogte;
	
	public static int GEEN = 0;
	public static int LIJN = 1;
	public static int PIJL = 2;
	public static int ACCOLADE = 3;
	public static int ELLIPS = 4;
	public static int HAAK = 5;
	
	public static int RICHTING_LINKS = 0;
	public static int RICHTING_RECHTS = 1;
	public static int RICHTING_BOVEN = 2;
	public static int RICHTING_BENEDEN = 3;
	public static int RICHTING_RECHTSBOVEN = 4;
	public static int RICHTING_RECHTSONDER = 5;
	public static int RICHTING_LINKSONDER = 6;
	public static int RICHTING_LINKSBOVEN = 7;
	
	
	public SymboolPanel(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this();
		if (h != null && h.containsKey("breedte"))
			breedte = ((Number) h.get("breedte")).intValue();
		if (h != null && h.containsKey("hoogte"))
			hoogte = ((Number) h.get("hoogte")).intValue();
		if(h != null && h.containsKey("volledigeBreedte"))
			volledigeBreedte =((Boolean) h.get("volledigeBreedte"));
		if (h != null && h.containsKey("interactiePanelLaunchState"))
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		
		init0(breedte, hoogte, launchState, randomVarWaarden);		
		if (type == ELLIPS) initialize(); else initializeSVG();
	}
	
	public SymboolPanel() {
	     symboolCanvas = Canvas.createIfSupported();
		 OMSVGDocument doc = OMSVGParser.currentDocument();
	     symboolSVG = new SVGImage(doc.createSVGSVGElement());
	}

	@Override
	public void init(int width, int height, Map<String, Object> launchData,
            Map<String, Number> values ) {
	  init0(width, height, launchData, values);
	  if (type == ELLIPS) initialize(); else initializeSVG();
	}
	
	private void init0(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width;
		hoogte = height;
		ObjectMap map = JSONUtilities.wrapMap(launchData);
		if (map != null)
		{
			int kleurR = 0;
			int kleurG = 0;
			int kleurB = 0;
			if(map.containsKey("dikte"))
				dikte = map.getInt("dikte");
			if(map.containsKey("richting"))
				richting = map.getInt("richting");
			if(map.containsKey("type"))
				type = map.getInt("type");
			if(map.containsKey("kleurR"))
				kleurR = map.getInt("kleurR");
			if(map.containsKey("kleurG"))
				kleurG = map.getInt("kleurG");
			if(map.containsKey("kleurB"))
				kleurB = map.getInt("kleurB");
			if(map.containsKey("vulHoogte"))
				vulHoogte = map.getBoolean("vulHoogte");
			
			kleur = CssColor.make(kleurR, kleurG, kleurB);
		}
	}
	
	public void initialize()
	{
		Context2d ctx;
		ctx0 = ctx = symboolCanvas.getContext2d();
		double ratio = TekstComponent.getDeviceRatio(ctx);
		symboolCanvas.setPixelSize(breedte, hoogte);
		if(ratio > 1.0) {
			symboolCanvas.setCoordinateSpaceHeight((int)(hoogte*ratio));
			symboolCanvas.setCoordinateSpaceWidth((int) (breedte*ratio));
			ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
		} else {
		//change the canvas dimensions
			symboolCanvas.setCoordinateSpaceHeight(hoogte);
			symboolCanvas.setCoordinateSpaceWidth(breedte);
		}
		this.ctx = new CanvasBuilder(ctx);
		paintComponent(this.ctx);
	}

	public void initializeSVG()
	{	symboolCanvas = null;
		symboolSVG.setPixelSize(breedte, hoogte);
		this.ctx = new SvgBuilder(symboolSVG.getSvgElement(), 0, 0);
		paintComponent(this.ctx);
	}
	
	public void paintComponent(PathBuilder ctx)
	{	
		ctx.setStrokeStyle(kleur.value());
		ctx.setFillStyle(kleur.value());
		ctx.setLineWidth(dikte);
		
		if(type == PIJL)
		{
			if(richting == RICHTING_LINKS)
			{	ctx.beginPath();
				ctx.moveTo(breedte, hoogte / 2);
				ctx.lineTo(dikte, hoogte / 2);
				ctx.moveTo(dikte/2, hoogte / 2 + dikte/2);
				ctx.lineTo(5 + dikte, hoogte / 2 - 5 - dikte/2);
				ctx.moveTo(dikte/2, hoogte / 2 - dikte/2);
				ctx.lineTo(5 + dikte, hoogte / 2 + 5 + dikte/2);
				ctx.stroke();
//				g2.drawLine(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2);
//				g2.drawLine(0, this.getHeight()/2, 5, this.getHeight()/2 - 5);
//				g2.drawLine(0, this.getHeight()/2, 5, this.getHeight()/2 + 5);
			}
			else if(richting == RICHTING_RECHTS)
			{	ctx.beginPath();
				ctx.moveTo(0, hoogte / 2);
				ctx.lineTo(breedte - dikte, hoogte / 2);
				ctx.moveTo(breedte - dikte/2, hoogte / 2 + dikte/2);
				ctx.lineTo(breedte - 5 - dikte, hoogte / 2 - 5 - dikte/2);
				ctx.moveTo(breedte - dikte/2, hoogte / 2 - dikte/2);
				ctx.lineTo(breedte - 5 - dikte, hoogte / 2 + 5 + dikte/2);
				ctx.stroke();
				
//				g2.drawLine(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2);
//				g2.drawLine(this.getWidth(), this.getHeight()/2, this.getWidth() - 5, this.getHeight()/2 - 5);
//				g2.drawLine(this.getWidth(), this.getHeight()/2, this.getWidth() - 5, this.getHeight()/2 + 5);
			}
			else if(richting == RICHTING_BOVEN)
			{	
				ctx.beginPath();
				ctx.moveTo(breedte / 2, hoogte);
				ctx.lineTo(breedte / 2, dikte);
				ctx.moveTo(breedte / 2 + dikte / 2, dikte / 2);
				ctx.lineTo(breedte / 2 - 5 - dikte / 2, 5 + dikte);
				ctx.moveTo(breedte / 2 - dikte/2, dikte / 2);
				ctx.lineTo(breedte / 2 + 5 + dikte / 2, 5 + dikte);
				ctx.stroke();
				
//				g2.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());
//				g2.drawLine(this.getWidth()/2, 0, this.getWidth()/2 - 5, 5);
//				g2.drawLine(this.getWidth()/2, 0, this.getWidth()/2 + 5, 5);
			
			}
			else if(richting == RICHTING_BENEDEN)
			{	
				ctx.beginPath();
				ctx.moveTo(breedte / 2, 0);
				ctx.lineTo(breedte / 2, hoogte - dikte);
				ctx.moveTo(breedte / 2 + dikte / 2, hoogte - dikte/2);
				ctx.lineTo(breedte / 2 - 5 - dikte / 2, hoogte - 5 - dikte);
				ctx.moveTo(breedte / 2 - dikte/2, hoogte - dikte/2);
				ctx.lineTo(breedte / 2 + 5 + dikte / 2, hoogte - 5 - dikte);
				ctx.stroke();
				
//				g2.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());
//				g2.drawLine(this.getWidth()/2, this.getHeight(), this.getWidth()/2 - 5, this.getHeight() - 5);
//				g2.drawLine(this.getWidth()/2, this.getHeight(), this.getWidth()/2 + 5, this.getHeight() - 5);
			}
			else if(richting == RICHTING_LINKSBOVEN)
			{	
				ctx.beginPath();
				ctx.moveTo(breedte, hoogte);
				ctx.lineTo(dikte/2, dikte /2);
				ctx.lineTo(6 + dikte, dikte /2);
				ctx.moveTo(dikte /2, dikte /2);
				ctx.lineTo(dikte /2, 6 + dikte);
				ctx.stroke();
				
//				g2.drawLine(0, 0, this.getWidth(), this.getHeight());
//				g2.drawLine(0, 0, 5, 0);
//				g2.drawLine(0, 0, 0, 5);
			
			}
			else if(richting == RICHTING_RECHTSONDER)
			{	
				ctx.beginPath();
				ctx.moveTo(0, 0);
				ctx.lineTo(breedte - 1, hoogte - 1);
				
				ctx.lineTo(breedte - 1, hoogte - 6 - dikte);
				
				ctx.moveTo(breedte - 1, hoogte - 1);
				ctx.lineTo(breedte - 6 - dikte, hoogte - 1);
				ctx.stroke();
				
//				g2.drawLine(0, 0, this.getWidth(), this.getHeight());
//				g2.drawLine(this.getWidth() - 1, this.getHeight() - 1, this.getWidth() - 6, this.getHeight() - 1);
//				g2.drawLine(this.getWidth() - 1, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 6);
			
			}
			else if(richting == RICHTING_LINKSONDER)
			{	
				ctx.beginPath();
				ctx.moveTo(breedte, 0);
				ctx.lineTo(0, hoogte - 1);
				ctx.lineTo(dikte / 2, hoogte - 1);
				ctx.lineTo(dikte / 2, hoogte - 7 - dikte);
				ctx.moveTo(0, hoogte - 1 - dikte / 2);
				ctx.lineTo(6 + dikte, hoogte - 1 - dikte / 2);
				ctx.stroke();
				
//				g2.drawLine(0, this.getHeight(), this.getWidth(), 0);
//				g2.drawLine(0, this.getHeight() - 1, 5, this.getHeight() - 1);
//				g2.drawLine(0, this.getHeight() - 1, 0, this.getHeight() - 6);
			
			}
			else
			{	
				ctx.beginPath();
				ctx.moveTo(0, hoogte);
				//ctx.lineTo(breedte - 1, dikte / 2);
				ctx.lineTo(breedte - 1, 0);
				ctx.moveTo(breedte - 1, dikte / 2);
				ctx.lineTo(breedte - 7 - dikte, dikte / 2);
				ctx.moveTo(breedte - 1 - dikte/2, 0);
				ctx.lineTo(breedte - 1 - dikte/2 , 6 + dikte);
				ctx.stroke();
				
//				g2.drawLine(0, this.getHeight(), this.getWidth(), 0);
//				g2.drawLine(this.getWidth() - 1, 0, this.getWidth() - 6, 0);
//				g2.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, 5);
			
			}
		}


		else if(type == LIJN)
		{
			if(richting == RICHTING_LINKS || richting == RICHTING_RECHTS)
			{	
				ctx.beginPath();
				ctx.moveTo(0, hoogte / 2);
				ctx.lineTo(breedte, hoogte / 2);
				ctx.stroke();
				
//				g2.drawLine(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2);
			
			}
			else if(richting == RICHTING_BOVEN || richting == RICHTING_BENEDEN)
			{	
				ctx.beginPath();
				ctx.moveTo(breedte / 2, 0);
				ctx.lineTo(breedte / 2, hoogte);
				ctx.stroke();
				
//				g2.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());
			}
			else if(richting == RICHTING_LINKSBOVEN || richting == RICHTING_RECHTSONDER)
			{	
				ctx.beginPath();
				ctx.moveTo(0, 0);
				ctx.lineTo(breedte, hoogte);
				ctx.stroke();
				
//				g2.drawLine(0, 0, this.getWidth(), this.getHeight());
			}
			else
			{	
				ctx.beginPath();
				ctx.moveTo(0, hoogte);
				ctx.lineTo(breedte, 0);
				ctx.stroke();
				
//				g2.drawLine(0, this.getHeight(), this.getWidth(), 0);
			}
		}
		else if(type == ACCOLADE)
		{
			if(richting == RICHTING_LINKS)
			{	
				int x = breedte / 2;
				ctx.beginPath();
				ctx.arc(x + 5, 6, 5, 3 * Math.PI / 2, Math.PI, true);
				ctx.lineTo(x, hoogte / 2 - 3);
				ctx.arc(x - 4, hoogte / 2 - 4, 4, 0, Math.PI / 2, false);
				ctx.arc(x - 4, hoogte / 2 + 4, 4, 3 * Math.PI / 2, 0, false);
				ctx.lineTo(x, hoogte - 6);
				ctx.arc(x + 5, hoogte - 7, 5, Math.PI, Math.PI / 2, true);
				ctx.stroke();
				
				
//				int x = this.getWidth()/2;
//				g2.drawArc(x, 1, 10, 10, 90, 90);
//				g2.drawLine(x, 6, x, getSize().height / 2 - 3);
//				g2.drawArc(x - 5, getSize().height / 2 - 6, 5, 5, 270, 90);
//				g2.drawArc(x - 5, getSize().height / 2, 5, 5, 0, 90);
//				g2.drawLine(x, getSize().height / 2 + 3, x, getSize().height - 6);
//				g2.drawArc(x, getSize().height - 12, 10, 10, 180, 90);
			}
			else if(richting == RICHTING_RECHTS)
			{
				int x = breedte / 2;
				ctx.beginPath();
				ctx.arc(x - 5, 6, 5, 3 * Math.PI / 2, 2 * Math.PI, false);//kan het laatste argument hier ook 0 zijn?
				ctx.lineTo(x, hoogte / 2 - 3);
				ctx.arc(x + 4, hoogte / 2 - 4, 4, Math.PI, Math.PI / 2, true);
				ctx.arc(x + 4, hoogte / 2 + 4, 4, 3 * Math.PI / 2, Math.PI, true);
				ctx.lineTo(x, hoogte - 6);
				ctx.arc(x - 5, hoogte - 7, 5, 0, Math.PI / 2, false);
				ctx.stroke();
				
//				int x = this.getWidth()/2;
//				g2.drawArc(x - 10, 1, 10, 10, 0, 90);
//				g2.drawLine(x, 6, x, getSize().height / 2 - 3);
//				g2.drawArc(x, getSize().height / 2 - 6, 5, 5, 180, 90);
//				g2.drawArc(x, getSize().height / 2, 5, 5, 90, 90);
//				g2.drawLine(x, getSize().height / 2 + 3, x, getSize().height - 6);
//				g2.drawArc(x - 10, getSize().height - 12, 10, 10, 270, 90);
			}
			else if(richting == RICHTING_BENEDEN)
			{
				int y = hoogte / 2;
				ctx.beginPath();
				ctx.arc(6, y - 5, 5, Math.PI, Math.PI / 2, true);
				ctx.lineTo(breedte / 2 - 3, y);
				ctx.arc(breedte / 2 - 4, y + 4, 4, 3 * Math.PI / 2, 2 * Math.PI, false);
				ctx.arc(breedte / 2 + 4, y + 4, 4, Math.PI, 3 * Math.PI / 2, false);
				ctx.lineTo(breedte - 6, y);
				ctx.arc(breedte - 7, y - 5, 5, Math.PI / 2, 0, true);
				ctx.stroke();
				
//				int y = this.getHeight()/2;
//				g2.drawArc(1,  y - 10, 10, 10, 180, 90);
//				g2.drawLine(6, y, getSize().width / 2 - 3, y);
//				g2.drawArc(getSize().width / 2 - 6, y, 5, 5, 0, 90);
//				g2.drawArc(getSize().width / 2, y, 5, 5, 90, 90);
//				g2.drawLine(getSize().width / 2 + 3, y, getSize().width - 6, y);
//				g2.drawArc(getSize().width - 12, y - 10, 10, 10, 270, 90);
			}
			else if(richting == RICHTING_BOVEN)
			{
				int y = hoogte / 2;
				ctx.beginPath();
				ctx.arc(6, y + 5, 5, Math.PI, 3 * Math.PI / 2, false);
				ctx.lineTo(breedte / 2 - 3, y);
				ctx.arc(breedte / 2 - 4, y - 4, 4, Math.PI / 2, 0, true);
				ctx.arc(breedte / 2 + 4, y - 4, 4, Math.PI, Math.PI / 2, true);
				ctx.lineTo(breedte - 6, y);
				ctx.arc(breedte - 7, y + 5, 5, 3 * Math.PI / 2, 2 * Math.PI, false);
				ctx.stroke();
				
//				int y = this.getHeight()/2;
//				g2.drawArc(1,  y, 10, 10, 90, 90);
//				g2.drawLine(6, y, getSize().width / 2 - 3, y);
//				g2.drawArc(getSize().width / 2 - 6, y - 5, 5, 5, 270, 90);
//				g2.drawArc(getSize().width / 2, y - 5, 5, 5, 180, 90);
//				g2.drawLine(getSize().width / 2 + 3, y, getSize().width - 6, y);
//				g2.drawArc(getSize().width - 12, y, 10, 10, 0, 90);
			}
		}
		else if(type == ELLIPS)
		{
			ctx0.save(); // FIXME
			ctx.beginPath();
			ctx0.scale(breedte, hoogte);
			ctx.arc(0.5, 0.5, 0.5 - ((double) dikte)/((double) Math.min(breedte, hoogte)) , 0, 2 * Math.PI, false);
			ctx0.restore();
			ctx.stroke();			
			 
//			g2.draw(new Ellipse2D.Double(dikte, dikte, this.getWidth() - 2 * dikte, this.getHeight() - 2 * dikte));
		}
		else if(type == HAAK)
		{
			if(richting == RICHTING_LINKS)
			{	
				int x = breedte / 2;
				ctx.beginPath();
				ctx.arc(x + 5, 6, 5, 3 * Math.PI / 2, Math.PI, true);
				ctx.lineTo(x, hoogte - 6);
				ctx.arc(x + 5, hoogte - 7, 5, Math.PI, Math.PI / 2, true);
				ctx.stroke();
			}
			else if(richting == RICHTING_RECHTS)
			{
				int x = breedte / 2;
				ctx.beginPath();
				ctx.arc(x - 5, 6, 5, 3 * Math.PI / 2, 2 * Math.PI, false);//kan het laatste argument hier ook 0 zijn?
				ctx.lineTo(x, hoogte - 6);
				ctx.arc(x - 5, hoogte - 7, 5, 0, Math.PI / 2, false);
				ctx.stroke();
			}
		}
	}
	
	@Override
	public HashMap<String, Object> getState() {
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
	}

	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public int[][] getScoreObjectives() {
		return null;
	}

	@Override
	public Boolean isCorrect() {
		return Boolean.TRUE;
	}

	@Override
	public void kijkNa() {		
	}

	@Override
	public void zetNagekeken(boolean b) {
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		if(volledigeBreedte && symboolCanvas != null)
		{	this.breedte = breedte;
			symboolCanvas.setCoordinateSpaceWidth(breedte);
			symboolCanvas.setPixelSize(breedte, hoogte);
			paintComponent(ctx);
		} else if (volledigeBreedte) {
			this.breedte = breedte;
			symboolSVG.setPixelSize(breedte, hoogte);
			OMSVGSVGElement svg = symboolSVG.getSvgElement();
			while (svg.getChildNodes().getLength() > 0) {
				svg.removeChild(svg.getLastChild());
			}
			paintComponent(ctx);		
		}
	}
	
	public void zetVolledigeHoogte(int hoogte) {
		if(vulHoogte && symboolCanvas != null)
		{
			this.hoogte = hoogte;
			symboolCanvas.setCoordinateSpaceHeight(hoogte);
			symboolCanvas.setPixelSize(breedte, hoogte);
			paintComponent(ctx);
		} else if (vulHoogte) {
			this.hoogte = hoogte;
			symboolSVG.setPixelSize(breedte, hoogte);
			OMSVGSVGElement svg = symboolSVG.getSvgElement();
			while (svg.getChildNodes().getLength() > 0) {
				svg.removeChild(svg.getLastChild());
			}
			paintComponent(ctx);
			
		}
	}

	@Override
	public Widget asWidget() {
		if (symboolCanvas != null)
			return symboolCanvas;
		else 
			return symboolSVG;
	}

	@Override
	public int getAsHoogte() {
		return ashoogte;
	}

	@Override
	public int getHeight() {
		return hoogte;
	}

	@Override
	public int getWidth() {
		return breedte;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		this.ashoogte = ashoogte;
		
	}

	@Override
	public void getResponses(List<String> responses) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
