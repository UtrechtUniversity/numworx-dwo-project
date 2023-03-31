package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Expressie;
import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;


public class GetallenlijnSprongPanel implements InteractionStub, FacetAware
{

	static final String holderId = "dockholder";
	private HashMap<String, Object> launchState; 
	String[] randomVarNamen = null;
	HashMap randomVarWaarden = null;
	OpdrNavIF comRoot;
	
	int breedte = 40;
	int hoogte = 300; 
	private boolean volledigeBreedte = false;
	int ashoogte = 12;
	
	private Canvas getallenlijnCanvas;
	private Context2d ctx;
	
	
	//variabelen uit java-versie
	private int minLijn, maxLijn, margePerc, min, max; 
	
	private double minWaarde = -3;
	private double maxWaarde=4;
	private double minWerkWaarde = -3;
	private double maxWerkWaarde=4;
	private double eenheidWaarde=1;
	double eenheid;
	private double posNul;
	private boolean nulZichtbaar;
	private boolean eenhedenZichtbaar;
	private boolean pijlOmlaag;
	private boolean pijlZichtbaar = true;
	private boolean tientallenZichtbaar;
	private boolean vijftallenZichtbaar;
	private boolean eenhedenNummers;
	private boolean vijftallenNummers;
	private boolean tientallenNummers;
	
	String minWaardeString = "-3";
    String maxWaardeString = "-4";
    String eenheidWaardeString = "1";
	
	
	private boolean horizontaal = false;
	private double marge = 10;
	
//	private double factor = 0.10;
//	static DecimalFormatSymbols dfs;
//	public static DecimalFormat df;
	
	//private FontMetrics fm;
	private String fontString = "12px sans-serif"; //Kan dit aangepast? Anders hoeft het ook niet globaal gedefinieerd..
	//private Font font = new Font("SansSerif", Font.PLAIN, 12);
	
	//Uit Java:
	//public GetallenlijnSprongPanel() {
		
		//dfs = new DecimalFormatSymbols();
		//df = new DecimalFormat("0.#####", dfs);
		//fm = getFontMetrics(font);
	//}
	
	public GetallenlijnSprongPanel(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		
		if (h != null && h.containsKey("breedte"))
			breedte = ((Number) h.get("breedte")).intValue();
		if (h != null && h.containsKey("hoogte"))
			hoogte = ((Number) h.get("hoogte")).intValue();
		if(h != null && h.containsKey("volledigeBreedte"))
			volledigeBreedte =((Boolean) h.get("volledigeBreedte"));
		if (h != null && h.containsKey("interactiePanelLaunchState"))
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		
		init(breedte, hoogte, launchState, randomVarWaarden);
		
		initialize(randomVarNamen, randomVarWaarden);
	}
	
	@Override
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width;
		hoogte = height;
		ObjectMap map = JSONUtilities.wrapMap(launchData);
		if (map != null)
		{
			 if(map.containsKey("eenhedenZichtbaar")) 
				 eenhedenZichtbaar = map.getBoolean("eenhedenZichtbaar");
			    if(map.containsKey("vijftallenZichtbaar")) 
			    	vijftallenZichtbaar = map.getBoolean("vijftallenZichtbaar");
			    if(map.containsKey("tientallenZichtbaar")) 
			    	tientallenZichtbaar = map.getBoolean("tientallenZichtbaar");
			    if(map.containsKey("eenhedenNummers")) 
			    	eenhedenNummers = map.getBoolean("eenhedenNummers");
			    if(map.containsKey("vijftallenNummers")) 
			    	vijftallenNummers = map.getBoolean("vijftallenNummers");
			    if(map.containsKey("tientallenNummers")) 
			    	tientallenNummers = map.getBoolean("tientallenNummers");
			    if(map.containsKey("horizontaal")) 
			    	horizontaal = map.getBoolean("horizontaal");
			    if(map.containsKey("nulZichtbaar")) 
			    	nulZichtbaar = map.getBoolean("nulZichtbaar");
			    if(map.containsKey("pijlZichtbaar")) 
			    	pijlZichtbaar = map.getBoolean("pijlZichtbaar");
			    if(map.containsKey("pijlOmlaag")) 
			    	pijlOmlaag = map.getBoolean("pijlOmlaag");
			    if(map.containsKey("minWaardeString")) 
			    	minWaardeString = map.getString("minWaardeString");
			    if(map.containsKey("maxWaardeString")) 
			    	maxWaardeString = map.getString("maxWaardeString");
			    if(map.containsKey("eenheidWaardeString")) 
			    	eenheidWaardeString = map.getString("eenheidWaardeString");
		}
	}
	
	public void initialize(String[] randomVarNamen, HashMap randomVarWaarden)
	{
		getallenlijnCanvas = Canvas.createIfSupported();
		//getallenlijnCanvas.addStyleName("canvas");
		ctx = getallenlijnCanvas.getContext2d();
		//getallenlijnCanvas.setSize(breedte + "px", hoogte + "px");
		//getallenlijnCanvas.setPixelSize(breedte, hoogte);
		//of is nodig (zoals in grafiekentool):
		//getallenlijnCanvas.setWidth(breedte + "px");
		//getallenlijnCanvas.setHeight(hoogte + "px");
		getallenlijnCanvas.setCoordinateSpaceHeight(hoogte);
		getallenlijnCanvas.setCoordinateSpaceWidth(breedte);
		
		paintComponent(ctx);
	}
	
	public void paintComponent(Context2d ctx)
	{	int d = 4;
	//hier pas alle waarden berekenen, want breedte kan nog veranderen door volledigeBreedte.
		margePerc = pijlZichtbaar?20:0;
		minLijn = horizontaal?0:hoogte;
		maxLijn = horizontaal?breedte:0;
		
	//	min = (int)Math.round((double)minLijn - (minLijn-maxLijn)*margePerc/100);
	//	max = (int)Math.round(maxLijn + (minLijn-maxLijn)*margePerc/100);
	//	eenheid = (double)(max - min)/(maxWaarde - minWaarde);
	//	posNul = (double)min - (minWaarde*(max - min)/(maxWaarde - minWaarde));
		
		min = minLijn - (minLijn-maxLijn)*margePerc/100;
		max = maxLijn + (minLijn-maxLijn)*margePerc/100;
		eenheid = (max - min)/(maxWaarde - minWaarde);
		posNul = min - (minWaarde*(max - min)/(maxWaarde - minWaarde));
		
		try{
	    	minWaardeString = FormuleParser.randomizeString("$f" + minWaardeString + "@", randomVarNamen, randomVarWaarden);
		}
		catch(Exception e){}	
		try{
			maxWaardeString = FormuleParser.randomizeString("$f" + maxWaardeString + "@", randomVarNamen, randomVarWaarden);
		}
		catch(Exception e){}
		try{
			eenheidWaardeString = FormuleParser.randomizeString("$f" + eenheidWaardeString + "@", randomVarNamen, randomVarWaarden);
		}
		catch(Exception e){}
		
		Expressie expressie = FormuleParser.geefExpressie(minWaardeString);
		if(expressie!=null) 
		{	
			minWerkWaarde = expressie.geefWaarde();
			minWaarde = minWerkWaarde/eenheidWaarde;
			//posNul = (double)min - (minWaarde*(max - min)/(maxWaarde - minWaarde));
			posNul = min - (minWaarde*(max - min)/(maxWaarde - minWaarde));
		
		}
		expressie = FormuleParser.geefExpressie(maxWaardeString);
		if(expressie!=null) 
		{	maxWerkWaarde = expressie.geefWaarde();
			maxWaarde = maxWerkWaarde/eenheidWaarde;
			//posNul = (double)min - (minWaarde*(max - min)/(maxWaarde - minWaarde));
			posNul = min - (minWaarde*(max - min)/(maxWaarde - minWaarde));
		}
		expressie = FormuleParser.geefExpressie(eenheidWaardeString);
		if(expressie!=null) 
		{	eenheidWaarde = expressie.geefWaarde();
			minWaarde = minWerkWaarde/eenheidWaarde;
			maxWaarde = maxWerkWaarde/eenheidWaarde;
			//posNul = (double)min - (minWaarde*(max - min)/(maxWaarde - minWaarde));
			posNul = min - (minWaarde*(max - min)/(maxWaarde - minWaarde));
		}
		marge = berekenMargeGetallen();
	
		marge = berekenMargeGetallen();
		
		ctx.setFont(fontString);
		ctx.setFillStyle("black");
		ctx.setStrokeStyle("black");
		ctx.setLineWidth(1.0d);
		//ctx.setLineWidth(0.7d);
		
		//setFont(font);
		if(!horizontaal)
		{	//((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        //g.setColor(getBackground());
	        //g.fillRect(0, 0, getWidth(), getHeight());
			
	        //g.setColor(Color.darkGray);
	        ctx.beginPath();
	        ctx.moveTo(marge + .5, maxLijn);
	        ctx.lineTo(marge + .5, minLijn);
	        ctx.stroke();
			//g.drawLine(marge,maxLijn,marge,minLijn);
			//g.setColor(Color.darkGray);		
			
			
			for(double i=-0.5 ; pijlZichtbaar && i<2; i++)
		    {	//g.drawLine(marge-d-1,max+i,marge+d+1,max+i);
		    	ctx.beginPath();
		    	ctx.moveTo(marge-d-1,max + i );
		    	ctx.lineTo(marge+d+1,max + i );
		    	ctx.stroke(); 
			
		    }
			for(double i=-0.5 ; pijlZichtbaar && i<2; i++)
		    {	//g.drawLine(marge-d-1,min+i,marge+d+1,min+i);
		    	ctx.beginPath();
		    	ctx.moveTo(marge-d-1,min+i);
		    	ctx.lineTo(marge+d+1,min+i);
		    	ctx.stroke();
		    }
		 
		 	if(nulZichtbaar && posNul>max && posNul<min)
		 	{	for(double i=-0.5 ; i<2; i++)
			    {	//g.drawLine(marge-d,(int)Math.round(posNul)+i,marge+d,(int)Math.round(posNul)+i);
				    ctx.beginPath();
			    	ctx.moveTo(marge-d,posNul + i);//(int)Math.round(posNul)+i);
			    	ctx.lineTo(marge+d,posNul + i);//(int)Math.round(posNul)+i);
			    	ctx.stroke();
			    }
			}
			double eenheid = (max - min)/(maxWaarde - minWaarde);
			int extraEenheden = (int)Math.round((minLijn-min)/eenheid);
			for(int i = (int)Math.round(minWaarde+extraEenheden) ; i<maxWaarde-extraEenheden+1; i++)
			{	
			 	double sprong = eenheid*(double)i + posNul + 0.5;
			  	if(eenhedenZichtbaar && sprong<minLijn && sprong>maxLijn)
			  	{	//g.drawLine(marge-d/2,(int)Math.round(sprong),marge+d/2+1,(int)Math.round(sprong));
			  		ctx.beginPath();
			  		ctx.moveTo(marge-d/2, sprong);//(int)Math.round(sprong));
			  		ctx.lineTo(marge+d/2+1,sprong);//(int)Math.round(sprong));
			  		ctx.stroke();
				}
			   	if(vijftallenZichtbaar && i%5==0)
			   	{	//g.drawLine(marge-d/2-1,(int)Math.round(sprong),marge+d/2+2,(int)Math.round(sprong));
			   		ctx.beginPath();
			   		ctx.moveTo(marge-d/2-1,sprong);//(int)Math.round(sprong));
			   		ctx.lineTo(marge+d/2+2,sprong);//(int)Math.round(sprong));
			   		ctx.stroke();
			   		if(eenhedenZichtbaar)
			   		{	//g.drawLine(marge-d/2-1,(int)Math.round(sprong)+1,marge+d/2+2,(int)Math.round(sprong)+1);
				   		ctx.beginPath();
				  		ctx.moveTo(marge-d/2-1,sprong + 1);//(int)Math.round(sprong)+1);
				  		ctx.lineTo(marge+d/2+2,sprong + 1);//(int)Math.round(sprong)+1);
				  		ctx.stroke();
			   		}
			   	}
			   	if(tientallenZichtbaar && i%10==0)
			   	{	//g.drawLine(10-d/2-2,(int)Math.round(sprong)-1,10+d/2+2,(int)Math.round(sprong)-1);
			    	//g.drawLine(marge-d/2-2,(int)Math.round(sprong),marge+d/2+3,(int)Math.round(sprong));
			    	ctx.beginPath();
			  		ctx.moveTo(marge-d/2-2,sprong);//(int)Math.round(sprong));
			  		ctx.lineTo(marge+d/2+3,sprong);//(int)Math.round(sprong));
			  		ctx.stroke();
			    	if(eenhedenZichtbaar||vijftallenZichtbaar)
			    	{	//g.drawLine(marge-d/2-2,(int)Math.round(sprong)+1,marge+d/2+3,(int)Math.round(sprong)+1);
				    	ctx.beginPath();
				  		ctx.moveTo(marge-d/2-2,sprong + 1);//(int)Math.round(sprong)+1);
				  		ctx.lineTo(marge+d/2+3,sprong + 1);//(int)Math.round(sprong)+1);
				  		ctx.stroke();
			    	}
			    }
			   	if(eenhedenNummers || vijftallenNummers && i%5==0 || tientallenNummers && i%10==0)
		   		{	String s = Double.toString((double) i * eenheidWaarde);
		   			s = verwijderDecimaleNullen(s);
			   		//String s = df.format((double)i*eenheidWaarde);
		   			double b = ctx.measureText(s).getWidth();
		   		//int b = g.getFontMetrics().stringWidth(s);
			   		int h = 12;
		   			//int h = g.getFontMetrics().getAscent();
			   		if((int)Math.round(sprong)+h/2<getHeight() && (int)Math.round(sprong)-h/2>0)
			   			//g.drawString(s, marge-d-b-2,(int)Math.round(sprong)+h/2);
			   			ctx.fillText(s, marge-d-b-2,sprong+h/2);
			   	}
		   	}
				
		 	
			
			
		    if(pijlZichtbaar)
		    {	ctx.beginPath();
		    
		    	//Polygon p = new Polygon();
			    if(!pijlOmlaag)
				{   ctx.beginPath();
			    	ctx.moveTo(marge+20,max);
					ctx.lineTo(marge+20-d,max+2*d);
					ctx.lineTo(marge+20+d,max+2*d);
					ctx.closePath();
					ctx.stroke();
					ctx.fill();
//			    	p.addPoint(marge+20,max);
//					p.addPoint(marge+20-d,max+2*d);
//				    p.addPoint(marge+20+d,max+2*d);
				    ctx.beginPath();
				    ctx.moveTo(marge+19.5,min);
				    ctx.lineTo(marge+19.5,max+2);
				    ctx.moveTo(marge+20.5,max+2);
				    ctx.lineTo(marge+20.5,min);
				    ctx.stroke();
				}
				else
				{   ctx.beginPath();
		    		ctx.moveTo(marge+20,min);
		    		ctx.lineTo(marge+20-d,min-2*d);
		    		ctx.lineTo(marge+20+d,min-2*d);
		    		ctx.stroke();
		    		ctx.fill();
					
//					p.addPoint(marge+20,min);
//				    p.addPoint(marge+20-d,min-2*d);
//				    p.addPoint(marge+20+d,min-2*d);
		    		 ctx.beginPath();
					    ctx.moveTo(marge+19.5,min-2);
					    ctx.lineTo(marge+19.5,max);
					    ctx.moveTo(marge+20.5,max);
					    ctx.lineTo(marge+20.5,min-2);
					    ctx.stroke();
//				    g.drawLine(marge+20,min-2,marge+20,max);
//				    g.drawLine(marge+21,min-2,marge+21,max);
				}
//			    g.drawPolygon(p);
//			   	g.fillPolygon(p);
		    }
		}
		else
		{	//g = (Graphics2D)g;
	        //((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        //g.setColor(getBackground());
	        //g.fillRect(0, 0, getWidth(), getHeight());
	        //g.setColor(Color.black);
			ctx.beginPath();
			ctx.moveTo(maxLijn, marge + .5);
			ctx.lineTo(minLijn, marge + .5);
			ctx.stroke();
			//g.drawLine(maxLijn,marge,minLijn,marge);
					
			
			for(double i=.5 ; pijlZichtbaar && i<1; i++)
		    {	ctx.beginPath();
				ctx.moveTo(max + i, marge - d);
				ctx.lineTo(max + i, marge + 20 + d);
				ctx.stroke();
				
				//g.drawLine(max+i,marge-d,max+i,marge+20+d);
		    }
			for(double i=.5 ; pijlZichtbaar && i<1; i++)
		    {	
				ctx.beginPath();
				ctx.moveTo(min + i, marge - d);
				ctx.lineTo(min + i, marge + 20 + d);
				ctx.stroke();
				//g.drawLine(min+i,marge-d,min+i,marge+20+d);
		    }
		 
		 	if(nulZichtbaar && posNul<max && posNul>min)
		 	{	for(double i=-0.5 ; i<2; i++)
			    {	
			 		ctx.beginPath();
					ctx.moveTo(posNul + i, marge - d);
					ctx.lineTo(posNul + i, marge + d);
					ctx.stroke();
		 		
		 		//g.drawLine((int)Math.round(posNul)+i,marge-d,(int)Math.round(posNul)+i,marge+d);
			    }
			}
			double eenheid = (max - min)/(maxWaarde - minWaarde);
			int extraEenheden = (int)((minLijn-min)/eenheid);
			for(int i = (int)minWaarde+extraEenheden ; i<maxWaarde-extraEenheden+1; i++)
			{	
			 	double sprong = eenheid*(double)i + posNul + 0.5;
			   	if(eenhedenZichtbaar && sprong<maxLijn && sprong>minLijn)
			   	{	
			   		ctx.beginPath();
					ctx.moveTo(sprong, marge - d/2);
					ctx.lineTo(sprong, marge + d/2 + 1);
					ctx.stroke();
			   		//g.drawLine((int)Math.round(sprong),marge-d/2,(int)Math.round(sprong),marge+d/2+1);
			   	}
			   	if(vijftallenZichtbaar && i%5==0)
			   	{	ctx.beginPath();
			   		ctx.moveTo(sprong, marge - d/2 - 1);
			   		ctx.lineTo(sprong, marge + d/2 + 2);
			   		ctx.stroke();
			   		//g.drawLine((int)Math.round(sprong),marge-d/2-1,(int)Math.round(sprong),marge+d/2+2);
			   		if(eenhedenZichtbaar)
			   		{
			   			ctx.beginPath();
						ctx.moveTo(sprong + 1, marge - d/2 - 1);
						ctx.lineTo(sprong + 1, marge + d/2 + 2);
						ctx.stroke();
			   			//g.drawLine((int)Math.round(sprong)+1,marge-d/2-1,(int)Math.round(sprong)+1,marge+d/2+2);
			   		}
			   	}
			   	if(tientallenZichtbaar && i%10==0)
			   	{	
			   		ctx.beginPath();
					ctx.moveTo(sprong, marge - d/2 - 2);
					ctx.lineTo(sprong, marge + d/2 + 3);
					ctx.stroke();
			   		//g.drawLine((int)Math.round(sprong),marge-d/2-2,(int)Math.round(sprong),marge+d/2+3);
			   		if(eenhedenZichtbaar||vijftallenZichtbaar)
			   		{
			   			ctx.beginPath();
						ctx.moveTo(sprong + 1, marge - d/2 - 2);
						ctx.lineTo(sprong + 1, marge + d/2 + 3);
						ctx.stroke();
			   			//g.drawLine((int)Math.round(sprong)+1,marge-d/2-2,(int)Math.round(sprong)+1,marge+d/2+3);
			   		}
			   	}
			   	if(eenhedenNummers || vijftallenNummers && i%5==0 || tientallenNummers && i%10==0)
		   		{	String s = Double.toString((double) i * eenheidWaarde);
		   			s = verwijderDecimaleNullen(s);
			   		double b = ctx.measureText(s).getWidth();
		   			int h = 12;
			   		//String s = df.format((double)i*eenheidWaarde);
			   		//int b = g.getFontMetrics().stringWidth(s);
			   		//int h = g.getFontMetrics().getAscent();
			   		if((int)Math.round(sprong)+b/2<getWidth() && (int)Math.round(sprong)-b/2>0)
			   			ctx.fillText(s, sprong - b /2, marge - d - h/2);
			   			//g.drawString(s,(int)Math.round(sprong)-b/2, marge-d-h/2);
		   		}
			}
			
		 	
		    
		    if(pijlZichtbaar)
		    {	//Polygon p = new Polygon();
			    if(!pijlOmlaag)
				{  
			    	ctx.beginPath();
			    	ctx.moveTo(max, marge + 20);
			    	ctx.lineTo(max - 2 * d, marge + 20 - d);
			    	ctx.lineTo(max - 2 * d, marge + 20 + d);
			    	ctx.closePath();
			    	ctx.stroke();
			    	ctx.fill();
			    	
			    	ctx.moveTo(min, marge + 19.5);
			    	ctx.lineTo(max - 2, marge + 19.5);
			    	ctx.moveTo(max - 2, marge + 20.5);
			    	ctx.lineTo(min, marge + 20.5);
			    	ctx.stroke();
//				    p.addPoint(max,marge+20);
//				    p.addPoint(max-2*d,marge+20-d);
//				    p.addPoint(max-2*d,marge+20+d);
				    
//				    g.drawLine(min,marge+20,max-2,marge+20);
//				    g.drawLine(min,marge+21,max-2,marge+21);
				}
				else
				{   
					ctx.beginPath();
			    	ctx.moveTo(min, marge + 20);
			    	ctx.lineTo(min + 2 * d, marge + 20 - d);
			    	ctx.lineTo(min + 2 * d, marge + 20 + d);
			    	ctx.closePath();
			    	ctx.stroke();
			    	ctx.fill();
			    	
			    	ctx.moveTo(min + 2, marge + 19.5);
			    	ctx.lineTo(max, marge + 19.5);
			    	ctx.moveTo(max, marge + 20.5);
			    	ctx.lineTo(min + 2, marge + 20.5);
			    	ctx.stroke();
					
//					p.addPoint(min,marge+20);
//					p.addPoint(min+2*d,marge+20-d);
//				    p.addPoint(min+2*d,marge+20+d);
//				    
//				    g.drawLine(min+2,marge+20,max,marge+20);
//				    g.drawLine(min+2,marge+21,max,marge+21);
				}
			    //g.drawPolygon(p);
			   	//g.fillPolygon(p);
		    }
		}
	}
	
	private double berekenMargeGetallen()
	{
			double margeGetallen = 0;
			double eenheid = (max - min)/(maxWaarde - minWaarde);
			int extraEenheden = (int)Math.round((minLijn-min)/eenheid);
			for(int i = (int)Math.round(minWaarde+extraEenheden) ; i<maxWaarde-extraEenheden+1; i++)
			{	double sprong = eenheid*(double)i + posNul;
			  	if(eenhedenNummers || vijftallenNummers && i%5==0 || tientallenNummers && i%10==0)
		   		{	String s = Double.toString((double)i * eenheidWaarde);
		   			s = verwijderDecimaleNullen(s);
			  		//String s = df.format((double)i*eenheidWaarde);
			   		//int b = fm.stringWidth(s);
		   			double b = ctx.measureText(s).getWidth();
			   		margeGetallen = Math.max(margeGetallen,b);
			   		if(horizontaal)
			   		{	margeGetallen = 12;//fm.getHeight();
			   			break;
			   		}
			   	}
			  	
		   	}
			return 	margeGetallen+7;
	}
	
	private String verwijderDecimaleNullen(String s)
	{
		if(s.contains("."))
		{	s = s.substring(0, Math.min(s.indexOf(".") + 3, s.length()));
			while(s.endsWith("0"))
			{
				s = s.substring(0, s.length() - 1);
			}
			if(s.endsWith("."))
			{
				s = s.substring(0, s.length() - 1);
			}
		}
		return s;
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
		// TODO Auto-generated method stub
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
		if(volledigeBreedte)
		{	this.breedte = breedte;
			getallenlijnCanvas.setCoordinateSpaceWidth(breedte);
			getallenlijnCanvas.setPixelSize(breedte, hoogte);
			paintComponent(ctx);
		}
	}

	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return getallenlijnCanvas;
	}

	@Override
	public int getAsHoogte() {
		// TODO Auto-generated method stub
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
