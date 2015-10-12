package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
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
	private Context2d ctx;
	
	private int dikte, richting, type;
	private CssColor kleur;
	private boolean vulHoogte;
	
	public static int GEEN = 0;
	public static int LIJN = 1;
	public static int PIJL = 2;
	public static int ACCOLADE = 3;
	public static int ELLIPS = 4;
	
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
		
		if (h != null && h.containsKey("breedte"))
			breedte = ((Number) h.get("breedte")).intValue();
		if (h != null && h.containsKey("hoogte"))
			hoogte = ((Number) h.get("hoogte")).intValue();
		if(h != null && h.containsKey("volledigeBreedte"))
			volledigeBreedte =((Boolean) h.get("volledigeBreedte"));
		if (h != null && h.containsKey("interactiePanelLaunchState"))
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		
		init(breedte, hoogte, launchState, randomVarWaarden);
		
		initialize();
	}
	
	@Override
	public void init(int width, int height, Map<String, Object> launchData,
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
		
		symboolCanvas = Canvas.createIfSupported();
		ctx = symboolCanvas.getContext2d();
		symboolCanvas.setPixelSize(breedte, hoogte);
		symboolCanvas.setCoordinateSpaceHeight(hoogte);
		symboolCanvas.setCoordinateSpaceWidth(breedte);
		
		paintComponent(ctx);
	}
	
	public void paintComponent(Context2d ctx)
	{	
		ctx.setStrokeStyle(kleur);
		ctx.setFillStyle(kleur);
		ctx.setLineWidth(dikte);
		
		if(type == PIJL)
		{
			if(richting == RICHTING_LINKS)
			{	ctx.beginPath();
				ctx.moveTo(breedte, hoogte / 2);
				ctx.lineTo(0, hoogte / 2);
				ctx.lineTo(5, hoogte / 2 - 5);
				ctx.moveTo(0, hoogte / 2);
				ctx.lineTo(5, hoogte / 2 + 5);
				ctx.stroke();
//				g2.drawLine(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2);
//				g2.drawLine(0, this.getHeight()/2, 5, this.getHeight()/2 - 5);
//				g2.drawLine(0, this.getHeight()/2, 5, this.getHeight()/2 + 5);
			}
			else if(richting == RICHTING_RECHTS)
			{	ctx.beginPath();
				ctx.moveTo(0, hoogte / 2);
				ctx.lineTo(breedte, hoogte / 2);
				ctx.lineTo(breedte - 5, hoogte / 2 - 5);
				ctx.moveTo(breedte, hoogte / 2);
				ctx.lineTo(breedte - 5, hoogte / 2 + 5);
				ctx.stroke();
				
//				g2.drawLine(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2);
//				g2.drawLine(this.getWidth(), this.getHeight()/2, this.getWidth() - 5, this.getHeight()/2 - 5);
//				g2.drawLine(this.getWidth(), this.getHeight()/2, this.getWidth() - 5, this.getHeight()/2 + 5);
			}
			else if(richting == RICHTING_BOVEN)
			{	
				ctx.beginPath();
				ctx.moveTo(breedte / 2, hoogte);
				ctx.lineTo(breedte / 2, 0);
				ctx.lineTo(breedte / 2 - 5, 5);
				ctx.moveTo(breedte / 2, 0);
				ctx.lineTo(breedte / 2 + 5, 5);
				ctx.stroke();
				
//				g2.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());
//				g2.drawLine(this.getWidth()/2, 0, this.getWidth()/2 - 5, 5);
//				g2.drawLine(this.getWidth()/2, 0, this.getWidth()/2 + 5, 5);
			
			}
			else if(richting == RICHTING_BENEDEN)
			{	
				ctx.beginPath();
				ctx.moveTo(breedte / 2, 0);
				ctx.lineTo(breedte / 2, hoogte);
				ctx.lineTo(breedte / 2 - 5, hoogte - 5);
				ctx.moveTo(breedte / 2, hoogte);
				ctx.lineTo(breedte / 2 + 5, hoogte - 5);
				ctx.stroke();
				
//				g2.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());
//				g2.drawLine(this.getWidth()/2, this.getHeight(), this.getWidth()/2 - 5, this.getHeight() - 5);
//				g2.drawLine(this.getWidth()/2, this.getHeight(), this.getWidth()/2 + 5, this.getHeight() - 5);
			}
			else if(richting == RICHTING_LINKSBOVEN)
			{	
				ctx.beginPath();
				ctx.moveTo(breedte, hoogte);
				ctx.lineTo(0, 0);
				ctx.lineTo(5, 0);
				ctx.moveTo(0, 0);
				ctx.lineTo(0, 5);
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
				ctx.lineTo(breedte - 1, hoogte - 6);
				ctx.moveTo(breedte - 1, hoogte - 1);
				ctx.lineTo(breedte - 6, hoogte - 1);
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
				ctx.lineTo(5, hoogte - 1);
				ctx.moveTo(0, hoogte - 1);
				ctx.lineTo(0, hoogte - 6);
				ctx.stroke();
				
//				g2.drawLine(0, this.getHeight(), this.getWidth(), 0);
//				g2.drawLine(0, this.getHeight() - 1, 5, this.getHeight() - 1);
//				g2.drawLine(0, this.getHeight() - 1, 0, this.getHeight() - 6);
			
			}
			else
			{	
				ctx.beginPath();
				ctx.moveTo(0, hoogte);
				ctx.lineTo(breedte - 1, 0);
				ctx.lineTo(breedte - 6, 0);
				ctx.moveTo(breedte - 1, 0);
				ctx.lineTo(breedte - 1, 5);
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
				ctx.arc(x - 4, hoogte / 2 - 4, 4, 0, Math.PI / 2);
				ctx.arc(x - 4, hoogte / 2 + 4, 4, 3 * Math.PI / 2, 0);
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
				ctx.arc(x - 5, 6, 5, 3 * Math.PI / 2, 2 * Math.PI);//kan het laatste argument hier ook 0 zijn?
				ctx.lineTo(x, hoogte / 2 - 3);
				ctx.arc(x + 4, hoogte / 2 - 4, 4, Math.PI, Math.PI / 2, true);
				ctx.arc(x + 4, hoogte / 2 + 4, 4, 3 * Math.PI / 2, Math.PI, true);
				ctx.lineTo(x, hoogte - 6);
				ctx.arc(x - 5, hoogte - 7, 5, 0, Math.PI / 2);
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
				ctx.arc(breedte / 2 - 4, y + 4, 4, 3 * Math.PI / 2, 2 * Math.PI);
				ctx.arc(breedte / 2 + 4, y + 4, 4, Math.PI, 3 * Math.PI / 2);
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
				ctx.arc(6, y + 5, 5, Math.PI, 3 * Math.PI / 2);
				ctx.lineTo(breedte / 2 - 3, y);
				ctx.arc(breedte / 2 - 4, y - 4, 4, Math.PI / 2, 0, true);
				ctx.arc(breedte / 2 + 4, y - 4, 4, Math.PI, Math.PI / 2, true);
				ctx.lineTo(breedte - 6, y);
				ctx.arc(breedte - 7, y + 5, 5, 3 * Math.PI / 2, 2 * Math.PI);
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
			ctx.save();
			ctx.beginPath();
			ctx.scale(breedte, hoogte);
			ctx.arc(0.5, 0.5, 0.5 - ((double) dikte)/((double) Math.min(breedte, hoogte)) , 0, 2 * Math.PI);
			ctx.restore();
			ctx.stroke();			
			 
//			g2.draw(new Ellipse2D.Double(dikte, dikte, this.getWidth() - 2 * dikte, this.getHeight() - 2 * dikte));
		}
		
	}
	
	@Override
	public HashMap<String, Object> getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[][] getScoreObjectives() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isCorrect() {
		return true;
	}

	@Override
	public void kijkNa() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zetNagekeken(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		if(volledigeBreedte)
		{	this.breedte = breedte;
			symboolCanvas.setCoordinateSpaceWidth(breedte);
			symboolCanvas.setPixelSize(breedte, hoogte);
			paintComponent(ctx);
		}
	}
	
	public void zetVolledigeHoogte(int hoogte) {
		if(vulHoogte)
		{
			this.hoogte = hoogte;
			symboolCanvas.setCoordinateSpaceHeight(hoogte);
			symboolCanvas.setPixelSize(breedte, hoogte);
			paintComponent(ctx);
		}
	}

	@Override
	public Widget asWidget() {
		return symboolCanvas;
	}

	@Override
	public int getAsHoogte() {
		return 12;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getResponses(List<String> responses) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
