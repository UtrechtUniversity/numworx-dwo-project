package fi.writemathgwt.client.engine;

import java.util.ArrayList;
import java.util.logging.Logger;

import fi.writemathgwt.client.engine.WMObject;

public class FormulaProcessor {
	
	private static int cPanelAreaMin = -50000;
	private static int cPanelAreaMax = 50000;
	private static int cPanelAreaDelta = cPanelAreaMax - cPanelAreaMin;
	
	private static Logger logger = Logger.getLogger("FormulaProcessor");
	
	public static String addContext(String formuleString) {
		//logger.info("formuleString = "+formuleString);
		formuleString = formuleString.replaceAll("c0s", "cos");
		formuleString = formuleString.replaceAll("l0g", "log");
		formuleString = formuleString.replace("("+"0s", "cos");
		formuleString = formuleString.replace("s1n", "sin");
		formuleString = formuleString.replace("$"+"m1"+"@", "'");
		formuleString = formuleString.replace("$"+"ml"+"@", "'");
		formuleString = formuleString.replaceAll("->", "\u2192");
		//formuleString = formuleString.replaceAll("<-", "\u2190");
		formuleString = formuleString.replaceAll("-\\u27e9", "\u2192");
		//formuleString = formuleString.replaceAll("\u27e8-", "\u2190");
		return formuleString;
	}

	public static String parseFormuleNew(StrokeContainer strokeContainer, DoubleRectangle parseArea) {	
		ArrayList<WMObject> wmObjects = strokeContainer.getWMObjects();
		ArrayList<WMObject> wmObjectsToDo = new ArrayList<WMObject>();
		for (int i = 0; i < wmObjects.size(); i++) {	
			WMObject wo = new WMObject(wmObjects.get(i));
			wo.setIsMachtVan(null);
			wo.setIsTellerVan(null);
			wo.setIsNoemerVan(null);
			wo.setIsOnderWortel(null);
			wmObjectsToDo.add(wo);
		}
		
		labelBreuken(wmObjectsToDo);
		labelWortels(wmObjectsToDo);
		WMObjectLine wol = new WMObjectLine(wmObjectsToDo);
		return wol.getFormula();
		
		
//		// make a compact deep copy
//		for (int i = 0; i < wmObjects.size(); i++) {	
//			WMObject wo = new WMObject(wmObjects.get(i));
//			wo.setIsMachtVan(null);
//			wo.setIsTellerVan(null);
//			wo.setIsNoemerVan(null);
//			wo.setIsOnderWortel(null);
//			wmObjectsToDo.add(wo);
//		}
//		// bubble sort op links-positie
//		boolean swapped = true;
//		while (swapped)	{	
//			swapped = false;
//			for (int i = 1; i < wmObjectsToDo.size(); i++) {	
//				WMObject wo1 = wmObjectsToDo.get(i-1);
//				WMObject wo2 = wmObjectsToDo.get(i);
//				if (wo1.getBoxMid().x > wo2.getBoxMid().x) {	
//					wmObjectsToDo.set(i-1, wo2);
//					wmObjectsToDo.set(i, wo1);
//					swapped = true;
//				}
//			}
//		}
//		// haal de breukstrepen er even uit
//		ArrayList<WMObject> breukStrepen = new ArrayList<WMObject>();
//		for (int i = 0; i < wmObjectsToDo.size(); i++) {	
//			WMObject wo = wmObjectsToDo.get(i);
//			if (wo.getTeken().equals("-"))
//				breukStrepen.add(wo);
//		}
//		
//		// bubble sort op lengte breukstreep
//		// langste vooraan
//		swapped = true;
//		while (swapped)	{	
//			swapped = false;
//			for (int i = 1; i < breukStrepen.size(); i++) {	
//				WMObject wo1 = breukStrepen.get(i-1);
//				WMObject wo2 = breukStrepen.get(i);
//				if (wo1.getBox().width < wo2.getBox().width) {	
//					breukStrepen.set(i-1, wo2);
//					breukStrepen.set(i, wo1);
//					swapped = true;
//				}
//			}
//		}
//
//		// label de objecten die breukstreep zijn, dit initialiseeert ook de teller- en noemer boxes,
//		// en labelt alle objecten die in een teller(box) of in een noemer(box) voorkomen, maar zodanig
//		// dat een object alleen teller of (exclusief) noemer kan zijn en dat van de meest geneste
//		//(kortste) breukstreep
//		for (int i = 0; i < breukStrepen.size(); i++) {	
//			WMObject wo = breukStrepen.get(i);
//			isBreuk(wo, wmObjectsToDo);
//		}
		
		
		
		
	}
	
	
	public static String parseFormule(StrokeContainer strokeContainer, DoubleRectangle parseArea) {		
		ArrayList<WMObject> wmObjects = strokeContainer.getWMObjects();
		ArrayList<WMObject> wmObjectsToDo = new ArrayList<WMObject>();
		// make a compact deep copy
		for (int i = 0; i < wmObjects.size(); i++) {	
			WMObject wo = new WMObject(wmObjects.get(i));
			wo.setIsMachtVan(null);
			wo.setIsTellerVan(null);
			wo.setIsNoemerVan(null);
			wo.setIsOnderWortel(null);
			wmObjectsToDo.add(wo);
		}
		// bubble sort op links-positie
		boolean swapped = true;
		while (swapped)	{	
			swapped = false;
			for (int i = 1; i < wmObjectsToDo.size(); i++) {	
				WMObject wo1 = wmObjectsToDo.get(i-1);
				WMObject wo2 = wmObjectsToDo.get(i);
				if (wo1.getBoxMid().x > wo2.getBoxMid().x) {	
					wmObjectsToDo.set(i-1, wo2);
					wmObjectsToDo.set(i, wo1);
					swapped = true;
				}
			}
		}
		// haal de breukstrepen er even uit
		ArrayList<WMObject> breukStrepen = new ArrayList<WMObject>();
		for (int i = 0; i < wmObjectsToDo.size(); i++) {	
			WMObject wo = wmObjectsToDo.get(i);
			if (wo.getTeken().equals("-"))
				breukStrepen.add(wo);
		}
		
		// bubble sort op lengte breukstreep
		// langste vooraan
		swapped = true;
		while (swapped)	{	
			swapped = false;
			for (int i = 1; i < breukStrepen.size(); i++) {	
				WMObject wo1 = breukStrepen.get(i-1);
				WMObject wo2 = breukStrepen.get(i);
				if (wo1.getBox().width < wo2.getBox().width) {	
					breukStrepen.set(i-1, wo2);
					breukStrepen.set(i, wo1);
					swapped = true;
				}
			}
		}

		// label de objecten die breukstreep zijn, dit initialiseeert ook de teller- en noemer boxes,
		// en labelt alle objecten die in een teller(box) of in een noemer(box) voorkomen, maar zodanig
		// dat een object alleen teller of (exclusief) noemer kan zijn en dat van de meest geneste
		//(kortste) breukstreep
		for (int i = 0; i < breukStrepen.size(); i++) {	
			WMObject wo = breukStrepen.get(i);
			isBreuk(wo, wmObjectsToDo);
		}
		
		// haal de wortels er even uit
		ArrayList<WMObject> wortels = new ArrayList<WMObject>();
		for (int i = 0; i < wmObjectsToDo.size(); i++) {	
			WMObject wo = wmObjectsToDo.get(i);
			if (wo.getTeken().equals("sqrt"))
				wortels.add(wo);
		}
		// bubble sort op lengte wortel
		// langste vooraan
		swapped = true;
		while (swapped)	{	
			swapped = false;
			for (int i = 1; i < wortels.size(); i++) {	
				WMObject wo1 = wortels.get(i-1);
				WMObject wo2 = wortels.get(i);
				if (wo1.getBox().width < wo2.getBox().width) {	
					wortels.set(i-1, wo2);
					wortels.set(i, wo1);
					swapped = true;
				}
			}
		}
		
		// correcties bij wortels
		// maak de wortelboxen
		for (int v = 0; v < wortels.size(); v++) {	
			WMObject wo = wortels.get(v);
			double x = wo.getBox().x + 10;
			double y = wo.getBox().y;
			double w = wo.getBox().width - 10;
			double h = wo.getBox().height;
			DoubleRectangle wBox = new DoubleRectangle(x,y,w,h);
			wo.setWortelBox(wBox);
		}	
		
		for (int i = 0; i < wortels.size(); i++) {	
			WMObject wo = wortels.get(i);
			DoubleRectangle wBox = wo.getWortelBox(); 

			// objecten binnen de wortelBox, NB wo zit daar ook bij!!
			// en ook wortels die wo bevatten!!
			ArrayList<WMObject> objectsInside = wmObjectsInBox(wmObjectsToDo, wBox);
			
			// 1) object niet wo en geen wortel: object zit onder wo
			// 2) object niet wo en wortel die kleiner(!) is dan wo: object zit onder wo  
			// 3) objecten in een geneste wortel worden herlabeld omdat de wortels van groot naar 
			// klein verwerkt worden
			for (int k = 0; k < objectsInside.size(); k++) {	
				WMObject oi = objectsInside.get(k);
				if ((oi != wo) && !oi.getTeken().equals("sqrt")) {	
					oi.setIsOnderWortel(wo);
				}
				else if ((oi != wo) && oi.getTeken().equals("sqrt")) {
					if (oi.getWortelBox().width < wo.getWortelBox().width)
						oi.setIsOnderWortel(wo);
				}
			}
			
			for (int j = 0; j < breukStrepen.size(); j++) {	
				WMObject bs = breukStrepen.get(j);
				// breukstreep binnen de wortel
				// a) mag de wortel niet als teller/noemer hebben
				// b) objecten buiten de wortel mogen geen teller of noemer zijn van deze breukstreep
				// c) aanpassen teller- en noemerbox hoeft niet: bij uitvoeren van de wortel
				// bevat writeObjectsToDoNow alleen objecten in de wortelbox
				if (bs.isBreuk() && wBox.contains(bs.getBoxMid().x, bs.getBoxMid().y)) {	
					ArrayList<WMObject> tellerObjects = wmObjectsInBox(wmObjectsToDo, bs.getTellerBox());
					ArrayList<WMObject> noemerObjects = wmObjectsInBox(wmObjectsToDo, bs.getNoemerBox());
						
					int inTellerCnt = tellerObjects.size();
					int inNoemerCnt = noemerObjects.size();
						
					// wortel eruit indien die in de teller zit
					if (tellerObjects.contains(wo))	{	
						// NB wo kan al aan de juiste breukstreep zijn toegewezen!
						if ((wo.isTellerVan() != null) && (wo.isTellerVan() == bs))
							wo.setIsTellerVan(null);
						tellerObjects.remove(wo);
						inTellerCnt--;
					}
					// wortel eruit indien die in de noemer zit
					if (noemerObjects.contains(wo))	{	
						// NB wo kan al aan de juiste breukstreep zijn toegewezen!
						if ((wo.isNoemerVan() != null) && (wo.isNoemerVan() == bs))
							wo.setIsNoemerVan(null);
						noemerObjects.remove(wo);
						inNoemerCnt--;
					}
					// objecten buiten de wortel uit de teller van bs
					for (int tCnt = 0; tCnt < tellerObjects.size(); tCnt++)	{	
						WMObject to = tellerObjects.get(tCnt);
						if (!wBox.contains(to.getBoxMid().x, to.getBoxMid().y))	{
							if ((to.isTellerVan() != null) && (to.isTellerVan() == bs))	{	
								to.setIsTellerVan(null);
								tellerObjects.remove(wo);
								inTellerCnt--;
							}
						}
					}
					// objecten buiten de wortel uit de noemer van bs
					for (int nCnt = 0; nCnt < noemerObjects.size(); nCnt++)	{	
						WMObject no = noemerObjects.get(nCnt);
						if (!wBox.contains(no.getBoxMid().x, no.getBoxMid().y))	{
							if ((no.isNoemerVan() != null) && (no.isNoemerVan() == bs))	{	
								no.setIsNoemerVan(null);
								noemerObjects.remove(wo);
								inNoemerCnt--;
							}
						}
					}
					// check of bs nog breuk is	
					if ((inTellerCnt == 0) && (inNoemerCnt == 0)) {	
						bs.setBreuk(false);
						bs.setTellerBox(null);
						bs.setNoemerBox(null);
					}
				} // if bs binnend de wortel
				// breukstreep buiten de wortel, als wo teller of noemer van bs is, dan mag 
				// mag geen object binnen de wortel teller/noemer of noemer zijn van bs
				// maar: als er een breukstreep binnen de wortel is, dan is wo daar teller of noemer van
				// en niet van een breukstreep buiten de wortel
				// maak wo dus teller of noemer van de kleinste breukstreep die wo als
				// teller/noemer bevat (breukstrepen zijn gesorteerd
				// maar: doe dit niet als wo onder een wortel zit die bs al als teller of noemer heeft !!
				// let op dat bs buiten iha voor bs binnen komt omdat iha bs buiten langer is dan bs binnen 
				else if (bs.isBreuk() && !wBox.contains(bs.getBoxMid().x, bs.getBoxMid().y)) {	
					boolean tellerCorrection = true;
					// corrigeer niet als wo onder een wortel zit die bs al als teller heeft !!
					// deze bs-allocatie is correct want wo wordt later behandeld dan deze moederwortel
					// omdat de moederwortel groter is dan wo 
					if ((wo.isOnderWortel() != null) && (wo.isOnderWortel().isTellerVan() != null) && (wo.isOnderWortel().isTellerVan() == bs))
						tellerCorrection = false;
					// corrigeer als nodig	
					if (bs.getTellerBox().contains(wo.getBoxMid().x, wo.getBoxMid().y) && tellerCorrection)	{	
						wo.setIsTellerVan(bs);
						wo.setIsNoemerVan(null);
					}
					// corrigeer niet als wo onder een wortel zit die bs al als noemer heeft !!
					// deze bs-allocatie is correct want wo wordt later behandeld dan deze moederwortel
					// omdat de moederwortel groter is dan wo 
					boolean noemerCorrection = true;
					if ((wo.isOnderWortel() != null) && wo.isOnderWortel().isNoemerVan() != null && wo.isOnderWortel().isNoemerVan() == bs)
						noemerCorrection = false;
					// corrigeer als nodig	
					if (bs.getNoemerBox().contains(wo.getBoxMid().x, wo.getBoxMid().y) && noemerCorrection)	{	
						wo.setIsTellerVan(null);
						wo.setIsNoemerVan(bs);
					}	

					// indien(!) wo nu teller of noemer is van bs, dan kunnen alle objecten (exclusief wo)
					// geen teller of noemer meer zijn van bs; zet dus tellerVan of noemerVan op null
					if (((wo.isTellerVan() != null) && (wo.isTellerVan() == bs)) ||	((wo.isNoemerVan() != null) && (wo.isNoemerVan() == bs))) {	
						for (int k = 0; k < objectsInside.size(); k++) {	
							WMObject oi = objectsInside.get(k);
							if (oi != wo) {	
								if ((oi.isTellerVan() != null) && (oi.isTellerVan() == bs))	{	
									oi.setIsTellerVan(null);
								}
								if ((oi.isNoemerVan() != null) && (oi.isNoemerVan() == bs))	{	
									oi.setIsNoemerVan(null);
								}
							}
						} //for
					}
				} // breukstreep buiten de wortel
			} // for breukstrepen
		} // for wortels
		
//      Changed scope of the writePanel, size is now inifinite (as fars as that is possible within the int parameters)
//		return parseBox(new Rectangle(Integer.MIN_VALUE / 2, Integer.MIN_VALUE / 2, Integer.MAX_VALUE, Integer.MAX_VALUE), writeObjectsToDo, null, null);
				return parseBox(new DoubleRectangle(cPanelAreaMin, cPanelAreaMin, cPanelAreaDelta, cPanelAreaDelta), strokeContainer, wmObjectsToDo, null, null);
				//return parseBox(parseArea, wmObjectsToDo, null, null);
				//		return parseBox(new DoubleRectangle(0, 0, width, height), writeObjectsToDo, null, null);
	}
	
	private static void labelBreuken(ArrayList<WMObject> wmObjects) {
		ArrayList<WMObject> writeObjects = new ArrayList<WMObject>();
		writeObjects.addAll(wmObjects);
		if(writeObjects.size()==0)
			return;
		WMObject langsteStreep = null;
		for (int i = 0; i < writeObjects.size(); i++) {
			WMObject wo = writeObjects.get(i);
			if(("-".equals(wo.getTeken()) && (langsteStreep == null || wo.getBox().width > langsteStreep.getBox().width)))
				langsteStreep = wo;
		}
		if(langsteStreep==null)
			return;
			
		ArrayList<WMObject> writeObjectsToDoTeller = new ArrayList<WMObject>();
		ArrayList<WMObject> writeObjectsToDoNoemer = new ArrayList<WMObject>();
		for (int i = 0; i < writeObjects.size(); i++) {
			WMObject wo = writeObjects.get(i);
			if(inTellerBox(wo, langsteStreep)) {
				writeObjectsToDoNoemer.add(writeObjects.get(i));
				writeObjects.get(i).setIsNoemerVan(langsteStreep);
				writeObjects.get(i).setIsTellerVan(null);
				langsteStreep.setBreuk(true);
			}
			else if(inNoemerBox(wo, langsteStreep)) {
				writeObjectsToDoTeller.add(writeObjects.get(i));
				writeObjects.get(i).setIsTellerVan(langsteStreep);
				writeObjects.get(i).setIsNoemerVan(null);
				langsteStreep.setBreuk(true);
			}
		}
		writeObjects.remove(langsteStreep);
		writeObjects.removeAll(writeObjectsToDoTeller);
		writeObjects.removeAll(writeObjectsToDoNoemer);
		labelBreuken(writeObjects);
		labelBreuken(writeObjectsToDoTeller);
		labelBreuken(writeObjectsToDoNoemer);
	}
	
	private static void labelWortels(ArrayList<WMObject> wmObjects) {
		ArrayList<WMObject> writeObjects = new ArrayList<WMObject>();
		writeObjects.addAll(wmObjects);
		if(writeObjects.size()==0)
			return;
		WMObject langsteWortel = null;
		for (int i = 0; i < writeObjects.size(); i++) {
			WMObject wo = writeObjects.get(i);
			if(("sqrt".equals(wo.getTeken()) && (langsteWortel == null || wo.getBox().width > langsteWortel.getBox().width)))
				langsteWortel = wo;
		}
		if(langsteWortel==null)
			return;
		
		ArrayList<WMObject> writeObjectsOnderWortel = new ArrayList<WMObject>();
		langsteWortel.setWortel(true);
		for (int i = 0; i < writeObjects.size(); i++) {
			WMObject wo = writeObjects.get(i);
			if(inWortelBox(wo,langsteWortel)) {
				writeObjectsOnderWortel.add(wo);
				if( wo.isTellerVan()!=null && !inWortelBox(wo.isTellerVan(), langsteWortel)) {
					langsteWortel.setIsTellerVan(wo.isTellerVan());
					wo.setIsTellerVan(null);
					wo.setIsOnderWortel(langsteWortel);
				}
				else if( wo.isNoemerVan()!=null && !inWortelBox(wo.isNoemerVan(), langsteWortel)) {
					langsteWortel.setIsNoemerVan(wo.isNoemerVan());
					wo.setIsNoemerVan(null);
					wo.setIsOnderWortel(langsteWortel);
				}
				else if(wo.isTellerVan()==null && wo.isNoemerVan()==null) {
					wo.setIsOnderWortel(langsteWortel);
				}
			}
		}
		writeObjects.remove(langsteWortel);
		writeObjects.removeAll(writeObjectsOnderWortel);
		labelWortels(writeObjects);
		labelWortels(writeObjectsOnderWortel);
	}
	
	private static boolean inWortelBox(WMObject wo, WMObject wortel) {
		DoubleRectangle wortelBox = wortel.getBox();
		return (wo.getBox().x > wortelBox.x+5 
				&& wo.getXBox().x+wo.getXBox().width<wortelBox.x+wortelBox.width+10 
				&& wo.getXBox().y > wortelBox.y-5
				&& wo.getXBox().y+wo.getXBox().height < wortelBox.y+wortelBox.height+10
				);
	}
	
	private static boolean inTellerBox(WMObject wo, WMObject breuk) {
		DoubleRectangle breukStreepBox = breuk.getBox();
		return (wo.getXBox().x > breukStreepBox.x-5 
				&& wo.getXBox().x+wo.getXBox().width<breukStreepBox.x+breukStreepBox.width+5 
				&& wo.getXBox().y > breukStreepBox.y
				&& wo!=breuk);
	}
	
	private static boolean inNoemerBox(WMObject wo, WMObject breuk) {
		DoubleRectangle breukStreepBox = breuk.getBox();
		return (wo.getXBox().x > breukStreepBox.x-5 
				&& wo.getXBox().x+wo.getXBox().width<breukStreepBox.x+breukStreepBox.width+5 
				&& wo.getXBox().y+wo.getXBox().height < breukStreepBox.y+breukStreepBox.height
				&& wo!=breuk);
	}
	
	private static void isBreuk(WMObject wo, ArrayList<WMObject> writeObjectsToDo) {
		boolean hasTeller = false;
		boolean hasNoemer = false;
		
		// wo moet een (breuk)streep zijn
		if (!"-".equals(wo.getTeken()))	{	
			return; // false;
		}
		
		if ((wo.isTellerVan() == null) && (wo.isNoemerVan() == null)) {	
			// neem alle hoogte tot bovenaan
			double tx = wo.getBox().x;
			double ty= cPanelAreaMin ;
			double tw = wo.getBox().width;
			double th = wo.getBox().y - ty; 
			
			wo.setTellerBox(new DoubleRectangle(tx,ty,tw,th));			
			
			// neem alle hoogte tot onderaan
			double nx = wo.getBox().x;
			double ny = wo.getBox().y + wo.getBox().height;
			double nw = wo.getBox().width;
//			int nh = height - ny; //wo.getBox().width;
			double nh = cPanelAreaMax-ny;
			
			wo.setNoemerBox(new DoubleRectangle(nx,ny,nw,nh));
		}
		else if ((wo.isTellerVan() != null) && (wo.isNoemerVan() == null)) {
			// neem alle hoogte tot bovenaan
//			int tx = wo.getBox().x;
//			int ty = 0; // wo.getBox().y - wo.getBox().width;
//			int tw = wo.getBox().width;
//			int th = wo.getBox().y; //wo.getBox().width;
			
			double tx = wo.getBox().x;
			double ty= cPanelAreaMin ;
			double tw = wo.getBox().width;
			double th = wo.getBox().y - ty; 
			wo.setTellerBox(new DoubleRectangle(tx,ty,tw,th));
			
			// pas tussen wo boven wo.isTellerVan
			double nx = wo.getBox().x;
			double ny = wo.getBox().y + wo.getBox().height;
			double nw = wo.getBox().width;
			double nh = wo.isTellerVan().getBox().y - ny; 
			wo.setNoemerBox(new DoubleRectangle(nx,ny,nw,nh));
		}
		else if ((wo.isTellerVan() == null) && (wo.isNoemerVan() != null)) {	
			
			// pas tussen wo.isNoemerVan boven wo 
			double tx = wo.getBox().x;
			double ty = wo.isNoemerVan().getBox().y + wo.isNoemerVan().getBox().height;
			double tw = wo.getBox().width;
			double th = wo.getBox().y - ty; 
			wo.setTellerBox(new DoubleRectangle(tx,ty,tw,th));
			
			// neem alle hoogte tot onderaan
			double nx = wo.getBox().x;
			double ny = wo.getBox().y + wo.getBox().height;
			double nw = wo.getBox().width;
//			int nh = height - ny; //wo.getBox().width;
			double nh = cPanelAreaMax-ny;
			wo.setNoemerBox(new DoubleRectangle(nx,ny,nw,nh));
		}
		for (int i = 0; i < writeObjectsToDo.size(); i++) {	
			WMObject wmObject = writeObjectsToDo.get(i);
			if (wo!=wmObject && wo.getTellerBox().contains(wmObject.getXBox(),-5,-wo.getBox().height))	{	
				hasTeller = true;
				wmObject.setIsTellerVan(wo);
				wmObject.setIsNoemerVan(null);
			}
		}
		for (int i = 0; i < writeObjectsToDo.size(); i++) {	
			WMObject wmObject = writeObjectsToDo.get(i);
			if (wo!=wmObject && wo.getNoemerBox().contains(wmObject.getXBox(),-5,-wo.getBox().height)) {	
				hasNoemer = true;
				wmObject.setIsNoemerVan(wo);
				wmObject.setIsTellerVan(null);
			}
		}		

		if (!hasTeller && !hasNoemer) {	
			wo.setTellerBox(null);
			wo.setNoemerBox(null);
		}
		else {
			wo.setBreuk(true);
		}
		
	}
	
	private static ArrayList<WMObject> wmObjectsInBox(ArrayList<WMObject> wObjects, DoubleRectangle box) {
		ArrayList<WMObject> insideObjects = new ArrayList<WMObject>();
		for (int i = 0; i < wObjects.size(); i++) {	
			WMObject wo = wObjects.get(i);
			if (box.contains(wo.getBoxMid().x, wo.getBoxMid().y))
				insideObjects.add(wo);
		}
		return insideObjects;
	}
	
	public static String parseBox(DoubleRectangle box, StrokeContainer strokeContainer,  ArrayList<WMObject> writeObjectsToDo, WMObject lastWriteObject, WMObject boxOwner) {
		String string = "";
		DoubleRectangle correctedBox = null;

		double x = box.x; 
		double width = box.width;
		for (int i = 0; i < writeObjectsToDo.size(); i++) {	
			WMObject wo = writeObjectsToDo.get(i);
			if (box.contains(wo.getBoxMid().x, wo.getBoxMid().y) && wo.getTeken().equals("sqrt")) {
				x = Math.min(x, wo.getWortelBox().x);
				width = Math.max(width, wo.getWortelBox().width);
			}
		}
		correctedBox = new DoubleRectangle(x,box.y,width,box.height);

		ArrayList<WMObject> writeObjectsToDoNow = new ArrayList<WMObject>();
		for (int i = 0; i < writeObjectsToDo.size(); i++) {
			WMObject wo = writeObjectsToDo.get(i);
			if (correctedBox.contains(wo.getBoxMid().x, wo.getBoxMid().y))
				writeObjectsToDoNow.add(wo);
		}
		// vindt het meest linkse object in de box dat nog niet verwerkt is
		// en dat niet in een teller of noemer voorkomt, m.u.v. de teller of noemer 
		// van boxOwner
		WMObject nextWriteObject = null;
		boolean found = false;
		for (int i = 0; i < writeObjectsToDoNow.size(); i++) {	
			WMObject wo = writeObjectsToDoNow.get(i);
			boolean skipWo = skipWriteObject(wo,boxOwner);
			if (!skipWo && !found) {	
				nextWriteObject = wo;
				found = true;
			}
		}
		int stopCnt = 0;
		//tijdelijk		
		//while (writeObjectsToDoNow.size() > 0)
		while ((nextWriteObject != null) && (stopCnt < 50)) {
			stopCnt++;
			//breuk
			if (nextWriteObject.isBreuk()) {
				String teller = "";
				String noemer = "";
				teller = parseBox(nextWriteObject.getTellerBox(), strokeContainer, writeObjectsToDoNow, null, nextWriteObject);
				noemer = parseBox(nextWriteObject.getNoemerBox(), strokeContainer, writeObjectsToDoNow, null, nextWriteObject);

				if ((lastWriteObject != null) && isMacht(strokeContainer, lastWriteObject, nextWriteObject))
					string = string + processMacht(strokeContainer, lastWriteObject, nextWriteObject, "$b" + teller + "$n" + noemer + "@@"); 
				else	
					string = string + "$b" + teller + "$n" + noemer + "@@";
	
				nextWriteObject.setVerwerkt(true);
				zetAllInBoxVerwerkt(writeObjectsToDo, nextWriteObject.getTellerBox(), true);
				zetAllInBoxVerwerkt(writeObjectsToDo, nextWriteObject.getNoemerBox(), true);
			}
			//wortel
			else if (nextWriteObject.getTeken().equals("sqrt")) {
				DoubleRectangle wBox = nextWriteObject.getWortelBox(); 
				nextWriteObject.setVerwerkt(true);
				removeIsOnderWortel(writeObjectsToDoNow, wBox, nextWriteObject);
				String operand = parseBox(wBox, strokeContainer, writeObjectsToDoNow, null,nextWriteObject);

				if ((lastWriteObject != null) && isMacht(strokeContainer, lastWriteObject, nextWriteObject))
					string = string + processMacht(strokeContainer, lastWriteObject, nextWriteObject, "$w" + operand + "@"); 
				else	
					string = string + "$w" + operand + "@";
				zetAllInBoxVerwerkt(writeObjectsToDo, wBox, true);
			}
			//macht 
			else if(lastWriteObject != null && isMacht(strokeContainer, lastWriteObject, nextWriteObject)){
				// isMacht = false betekent 1) verboden situatie of 
				// 2) nextWriteObject staat niet boven lastWriteObject en 
				// lastWriteObject is geen macht
			
				if (staatBoven(strokeContainer, lastWriteObject, nextWriteObject)) {
					nextWriteObject.setIsMachtVan(lastWriteObject);
					// open de macht
					string = string + "$m" + nextWriteObject.getTeken();
					nextWriteObject.setVerwerkt(true);
				}
				else if (staatNaast(strokeContainer, lastWriteObject, nextWriteObject)) {
					if (lastWriteObject.isMachtVan() != null) {
						nextWriteObject.setIsMachtVan(lastWriteObject.isMachtVan());
						string = string + nextWriteObject.getTeken();
						nextWriteObject.setVerwerkt(true);
					}
					// else isMacht = false 
				}
				// staat lager en zou weer bij een eerdere macht kunnen horen
				else { // maak dit maar redundant
					if ((lastWriteObject.isMachtVan() != null) && staatNaast(strokeContainer, lastWriteObject.isMachtVan(), nextWriteObject)) {
						if (lastWriteObject.isMachtVan().isMachtVan() == null) {
							// macht afsluiten
							string = string + "@" + nextWriteObject.getTeken();
							// teken hier afhandelen
							nextWriteObject.setVerwerkt(true);
						}
						else { 
							// lastWriteObject.isMachtVan.isMachtVan != null)
							nextWriteObject.setIsMachtVan(lastWriteObject.isMachtVan().isMachtVan());
							// macht afsluiten
							string = string + "@" + nextWriteObject.getTeken();
							nextWriteObject.setVerwerkt(true);
						}	
					}
					if ((lastWriteObject.isMachtVan() != null) && (lastWriteObject.isMachtVan().isMachtVan() != null) && staatNaast(strokeContainer, lastWriteObject.isMachtVan().isMachtVan(), nextWriteObject)) {
						if (lastWriteObject.isMachtVan().isMachtVan().isMachtVan() == null)	{
							// macht 2 keer (!) afsluiten
							string = string + "@@" + nextWriteObject.getTeken();
							nextWriteObject.setVerwerkt(true);
						}
						else {
							// lastWriteObject.isMachtVan.isMachtVan.isMachtVan != null
							nextWriteObject.setIsMachtVan(lastWriteObject.isMachtVan().isMachtVan().isMachtVan());
							// macht 2 keer (!) afsluiten
							string = string + "@@" + nextWriteObject.getTeken();
							nextWriteObject.setVerwerkt(true);
						}
					}
					
					if ((lastWriteObject.isMachtVan() != null) && 
						(lastWriteObject.isMachtVan().isMachtVan() != null) &&
						(lastWriteObject.isMachtVan().isMachtVan().isMachtVan() != null) &&
						staatNaast(strokeContainer, lastWriteObject.isMachtVan().isMachtVan().isMachtVan(), nextWriteObject)) {
								
						if (lastWriteObject.isMachtVan().isMachtVan().isMachtVan().isMachtVan() == null) {
							// macht 3 keer (!) afsluiten
							string = string + "@@@" + nextWriteObject.getTeken();
							// teken hier afhandelen
							nextWriteObject.setVerwerkt(true);
						}
						else { 
							// lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan != null
							nextWriteObject.setIsMachtVan(lastWriteObject.isMachtVan().isMachtVan().isMachtVan().isMachtVan());
							// macht 3 keer (!) afsluiten
							string = string + "@@@" + nextWriteObject.getTeken();
							nextWriteObject.setVerwerkt(true);
						}
					}
					if ((lastWriteObject.isMachtVan() != null) && 
						(lastWriteObject.isMachtVan().isMachtVan() != null) &&
						(lastWriteObject.isMachtVan().isMachtVan().isMachtVan() != null) &&
						(lastWriteObject.isMachtVan().isMachtVan().isMachtVan().isMachtVan() != null) &&
						staatNaast(strokeContainer, lastWriteObject.isMachtVan().isMachtVan().isMachtVan().isMachtVan(), nextWriteObject)) {
									
						if (lastWriteObject.isMachtVan().isMachtVan().isMachtVan().isMachtVan().isMachtVan() == null) {
							// macht 4 keer (!) afsluiten
							string = string + "@@@@" + nextWriteObject.getTeken();
							// teken hier afhandelen
							nextWriteObject.setVerwerkt(true);
						}
						else {
							// lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan != null
							nextWriteObject.setIsMachtVan(lastWriteObject.isMachtVan().isMachtVan().isMachtVan().isMachtVan().isMachtVan());
							// macht 4 keer (!) afsluiten
							string = string + "@@@@" + nextWriteObject.getTeken();
							nextWriteObject.setVerwerkt(true);
						}
					}
				}
			} 
			// punt
			else if (nextWriteObject.getTeken().equals(".")) {
				double py = nextWriteObject.getBoxMid().y;
				if (lastWriteObject != null) {	
					double ly = lastWriteObject.getBox().y;
					if (py > ly + 2 * strokeContainer.averageHeight / 3)
						string = string + ".";
					else
						string = string + "*";
				}
				nextWriteObject.setVerwerkt(true);
			}
			else {
				String teken = "";
				if (!nextWriteObject.isVerwerkt()) {	
					nextWriteObject.setVerwerkt(true);
					teken = nextWriteObject.getTeken();
				}
				string = string + teken;
			}
			lastWriteObject = nextWriteObject;
					
			// vindt het meest linkse object dat nog niet verwerkt is (if any) 
			nextWriteObject = null;
			//minX = width;
			found = false;
			for (int i = 0; i < writeObjectsToDoNow.size(); i++) {	
				WMObject wo = writeObjectsToDoNow.get(i);
				boolean skipWo = skipWriteObject(wo,boxOwner);
				if (!skipWo && !found) {	
					nextWriteObject = wo;
					found = true;
				}
			}
			if (nextWriteObject == null) {
				string = string + sluitMachtenAf(lastWriteObject);
			}
				
		} // while
		string = removeHalfObjects(string);
		return addContext(string);
	}


	private static boolean skipWriteObject(WMObject wo, WMObject boxOwner) {
		if (wo == null)
			return true;
		
		boolean skipWo1 = wo.isVerwerkt(); 
		boolean skipWo2 = false;
		if (boxOwner == null)
			skipWo2 = (wo.isTellerVan() != null) ||(wo.isNoemerVan() != null);
		else
			skipWo2 = ((wo.isTellerVan() != null) && (wo.isTellerVan() != boxOwner)) ||
					  ((wo.isNoemerVan() != null) && (wo.isNoemerVan() != boxOwner));
		boolean skipWo3 = false;
		if (boxOwner == null)
			skipWo3 = wo.isOnderWortel() != null;
		else
			skipWo3 = (wo.isOnderWortel() != null) && (wo.isOnderWortel() != boxOwner);
	
		return skipWo1 || skipWo2 || skipWo3;
	}
	
	private static void removeIsOnderWortel(ArrayList<WMObject> wo, DoubleRectangle wortelBox, WMObject wortel) {
		for (int i = 0; i < wo.size(); i++)	{	
			WMObject wob = wo.get(i);
			if (wortelBox.contains(wob.getBoxMid()) && (wob.isOnderWortel() != null) && (wob.isOnderWortel() == wortel)) {	
				wob.setIsOnderWortel(null);
			}
		}
	}
	
	private static void zetAllInBoxVerwerkt(ArrayList<WMObject> wo, DoubleRectangle box, boolean b) {
		for (int i = 0; i < wo.size(); i++) {	
			WMObject wob = wo.get(i);
			if (box.contains(wob.getBoxMid().x, wob.getBoxMid().y))
				wob.setVerwerkt(b);
		}
	}
	
	private static boolean isMacht(StrokeContainer strokeContainer, WMObject lastWo, WMObject wo) {
		// lastWo^{".","=","+",")","/"} kan/mag niet
		
//		if (staatBoven(strokeContainer, lastWo,wo) &&
//			(".".equals(wo.getTeken()) || 
//			 "=".equals(wo.getTeken()) || 
//			 "+".equals(wo.getTeken()) || 
//			 ")".equals(wo.getTeken()) || 
//			 "/".equals(wo.getTeken()))
//		   )
//			return false;
		
		//{".","=","+","-",")","/"}^wo kan niet
		if (staatBoven(strokeContainer, lastWo,wo) &&
			(".".equals(lastWo.getTeken()) || 
			 ",".equals(lastWo.getTeken()) ||
			 "=".equals(lastWo.getTeken()) || 
			 "+".equals(lastWo.getTeken()) || 
			 "-".equals(lastWo.getTeken()) || 
			 "(".equals(lastWo.getTeken()) || 
			 "/".equals(lastWo.getTeken()))
			)
			return false;
		
		boolean isMacht = staatBoven(strokeContainer, lastWo,wo) || (lastWo.isMachtVan() != null);
		return isMacht;
	}
	
	private static boolean staatBoven(StrokeContainer strokeContainer, WMObject lastWo, WMObject wo) {
		return (wo.getBoxMid().y + strokeContainer.averageHeight / 2 < lastWo.getBoxMid().y) && (wo.getBox().x > lastWo.getBoxMid().x);
	}
	
	private static boolean staatNaast(StrokeContainer strokeContainer, WMObject lastWo, WMObject wo) {	
		if (wo.getTeken().equals("-"))
			return (wo.getBoxMid().y > lastWo.getBox().y) && 
				   (wo.getBoxMid().y < (lastWo.getBox().y + lastWo.getBox().height)) && 
				   (wo.getBox().x > lastWo.getBoxMid().x);
		else
			return (Math.abs(wo.getBoxMid().y - lastWo.getBoxMid().y) < strokeContainer.averageHeight / 3) && 
			   	   (wo.getBox().x > lastWo.getBoxMid().x);
	}
	
	private static String processMacht(StrokeContainer strokeContainer, WMObject lastWriteObject, WMObject nextWriteObject, String objectString) {
		String string = "";
		
		if (staatBoven(strokeContainer, lastWriteObject, nextWriteObject)) {
			nextWriteObject.setIsMachtVan(lastWriteObject);
			// open de macht
			string = string + "$m" + objectString;
			nextWriteObject.setVerwerkt(true);
			
		}
		else if (staatNaast(strokeContainer, lastWriteObject, nextWriteObject)) {
			if (lastWriteObject.isMachtVan() != null) {
				nextWriteObject.setIsMachtVan(lastWriteObject.isMachtVan());
				string = string + objectString;
				nextWriteObject.setVerwerkt(true);
			}
			// else isMacht = false 
		}
		// staat lager en zou weer bij een eerdere macht kunnen horen
		else {
			// maak dit maar redundant
		
			if ((lastWriteObject.isMachtVan() != null) && staatNaast(strokeContainer, lastWriteObject.isMachtVan(), nextWriteObject)) {
				if (lastWriteObject.isMachtVan().isMachtVan() == null) {
					// macht afsluiten
					string = string + "@" + objectString;
					// teken hier afhandelen
					nextWriteObject.setVerwerkt(true);
				}
				else {
					// lastWriteObject.isMachtVan.isMachtVan != null)
					nextWriteObject.setIsMachtVan(lastWriteObject.isMachtVan().isMachtVan());
					// macht afsluiten
					string = string + "@" + objectString;
					nextWriteObject.setVerwerkt(true);
				}	
			}
			
			if ((lastWriteObject.isMachtVan() != null) && 
				(lastWriteObject.isMachtVan().isMachtVan() != null) &&
				staatNaast(strokeContainer, lastWriteObject.isMachtVan().isMachtVan(), nextWriteObject)) {
				
				if (lastWriteObject.isMachtVan().isMachtVan().isMachtVan() == null) {
					// macht 2 keer (!) afsluiten
					string = string + "@@" + objectString;
					nextWriteObject.setVerwerkt(true);
				}
				else {
					// lastWriteObject.isMachtVan.isMachtVan.isMachtVan != null
					nextWriteObject.setIsMachtVan(lastWriteObject.isMachtVan().isMachtVan().isMachtVan());
					// macht 2 keer (!) afsluiten
					string = string + "@@" + objectString;
					nextWriteObject.setVerwerkt(true);
				}
			}
			
			if ((lastWriteObject.isMachtVan() != null) && 
				(lastWriteObject.isMachtVan().isMachtVan() != null) &&
				(lastWriteObject.isMachtVan().isMachtVan().isMachtVan() != null) &&
				staatNaast(strokeContainer, lastWriteObject.isMachtVan().isMachtVan().isMachtVan(), nextWriteObject)) {
					
				if (lastWriteObject.isMachtVan().isMachtVan().isMachtVan().isMachtVan() == null) {
					// macht 3 keer (!) afsluiten
					string = string + "@@@" + objectString;
					// teken hier afhandelen
					nextWriteObject.setVerwerkt(true);
				}
				else {
					// lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan != null
					nextWriteObject.setIsMachtVan(lastWriteObject.isMachtVan().isMachtVan().isMachtVan().isMachtVan());
					// macht 3 keer (!) afsluiten
					string = string + "@@@" + objectString;
					nextWriteObject.setVerwerkt(true);
					
				}
			}
			
			if ((lastWriteObject.isMachtVan() != null) && 
				(lastWriteObject.isMachtVan().isMachtVan() != null) &&
				(lastWriteObject.isMachtVan().isMachtVan().isMachtVan() != null) &&
				(lastWriteObject.isMachtVan().isMachtVan().isMachtVan().isMachtVan() != null) &&
				staatNaast(strokeContainer, lastWriteObject.isMachtVan().isMachtVan().isMachtVan().isMachtVan(), nextWriteObject)) {
						
				if (lastWriteObject.isMachtVan().isMachtVan().isMachtVan().isMachtVan().isMachtVan() == null) {
					// macht 4 keer (!) afsluiten
					string = string + "@@@@" + objectString;
					// teken hier afhandelen
					nextWriteObject.setVerwerkt(true);
				}
				else {
					// lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan != null
					nextWriteObject.setIsMachtVan(lastWriteObject.isMachtVan().isMachtVan().isMachtVan().isMachtVan().isMachtVan());
					// macht 4 keer (!) afsluiten
					string = string + "@@@@" + objectString;
					nextWriteObject.setVerwerkt(true);
				}
			}
		}
		return string;
	}
	
	private static String sluitMachtenAf(WMObject wo) {	
		String result = ""; 
		if (wo.isMachtVan() == null)
			return result;
		else if (wo.isMachtVan().isMachtVan() == null)
			return "@";
		else if (wo.isMachtVan().isMachtVan().isMachtVan() == null)
			return "@@";
		else if (wo.isMachtVan().isMachtVan().isMachtVan().isMachtVan() == null)
			return "@@@";
		else if (wo.isMachtVan().isMachtVan().isMachtVan().isMachtVan().isMachtVan() == null)
			return "@@@@";
	
		return result;
	}
	
	public static String removeHalfObjects(String s) {
		String result = new String(s);
		int tHIndex = result.indexOf("tH");
		if (tHIndex >= 0) {	
			String s1 = result.substring(0,tHIndex);
			String s2 = result.substring(tHIndex + 2);
			result = s1 + s2;
		}
		int fourHIndex = result.indexOf("4H");
		if (fourHIndex >= 0) {	
			String s1 = result.substring(0,fourHIndex);
			String s2 = result.substring(fourHIndex + 2);
			result = s1 + s2;
		}
		int fiveHIndex = result.indexOf("5H");
		if (fiveHIndex >= 0) {	
			String s1 = result.substring(0,fiveHIndex);
			String s2 = result.substring(fiveHIndex + 2);
			result = s1 + s2;
		}
		int jHIndex = result.indexOf("jH");
		if (jHIndex >= 0) {	
			String s1 = result.substring(0,jHIndex);
			String s2 = result.substring(jHIndex + 2);
			result = s1 + s2;
		}
		int xHIndex = result.indexOf("xH");
		if (xHIndex >= 0) {	
			String s1 = result.substring(0,xHIndex);
			String s2 = result.substring(xHIndex + 2);
			result = s1 + s2;
		}

		return result;
	}
}
