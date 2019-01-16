package fi.writemathgwt.client.engine;

import java.util.ArrayList;
import java.util.logging.Logger;

import fi.writemathgwt.client.engine.WMObject;

public class FormulaProcessor {
	
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
			//wo.setIsMachtVan(null);
			wo.setIsTellerVan(null);
			wo.setIsNoemerVan(null);
			wo.setIsOnderWortel(null);
			wo.setIsExponentVan(null);
			wmObjectsToDo.add(wo);
		}
		labelBreuken(wmObjectsToDo);
		labelWortels(wmObjectsToDo);
		WMObjectLine wol = new WMObjectLine(wmObjectsToDo);
		labelMachten(wol.getWMObjects());
		WMObjectLine woll = new WMObjectLine(wmObjectsToDo);
		return woll.getFormula();
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
		ArrayList<WMObject> writeObjectsNdeVanWortel = new ArrayList<WMObject>();
		langsteWortel.setWortel(true);
		for (int i = 0; i < writeObjects.size(); i++) {
			WMObject wo = writeObjects.get(i);
			if(inWortelNdeBox(wo,langsteWortel)) {
				logger.info("inWortelNdeBox");
				writeObjectsNdeVanWortel.add(wo);
				
				if( wo.isTellerVan()!=null && !inWortelNdeBox(wo.isTellerVan(), langsteWortel)) {
					langsteWortel.setIsTellerVan(wo.isTellerVan());
					wo.setIsTellerVan(null);
					wo.setIsNdeVanWortel(langsteWortel);
				}
				else if( wo.isNoemerVan()!=null && !inWortelNdeBox(wo.isNoemerVan(), langsteWortel)) {
					langsteWortel.setIsNoemerVan(wo.isNoemerVan());
					wo.setIsNoemerVan(null);
					wo.setIsNdeVanWortel(langsteWortel);
				}
				else if(wo.isTellerVan()==null && wo.isNoemerVan()==null) 
					wo.setIsNdeVanWortel(langsteWortel);
			}
			else 
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
				else if(wo.isTellerVan()==null && wo.isNoemerVan()==null) 
					wo.setIsOnderWortel(langsteWortel);
			}
		}
		writeObjects.remove(langsteWortel);
		writeObjects.removeAll(writeObjectsOnderWortel);
		writeObjects.removeAll(writeObjectsNdeVanWortel);
		labelWortels(writeObjects);
		labelWortels(writeObjectsOnderWortel);
		labelWortels(writeObjectsNdeVanWortel);
	}
	

	private static void labelMachten(ArrayList<WMObject> wmObjects) {
		ArrayList<WMObject> writeObjects = new ArrayList<WMObject>();
		writeObjects.addAll(wmObjects);
		if(writeObjects.size()==0)
			return;
		
		for (int i = 0; i < writeObjects.size(); i++) {
			WMObject wo = writeObjects.get(i);
			if(wo.isBreuk()) { 
				if(wo.getWMObjectChildLine1()!=null)labelMachten(wo.getWMObjectChildLine1().getWMObjects());
				if(wo.getWMObjectChildLine2()!=null)labelMachten(wo.getWMObjectChildLine2().getWMObjects());
			}
			else if(wo.isWortel()) { 
				if(wo.getWMObjectChildLine1()!=null)labelMachten(wo.getWMObjectChildLine1().getWMObjects());
				if(wo.getWMObjectChildLine2()!=null)labelMachten(wo.getWMObjectChildLine2().getWMObjects());
			}
		}
		
		WMObject eersteMacht = null;
		int expNr = 0;
		for (int i = 0; i < writeObjects.size()-1; i++) {
			WMObject wo = writeObjects.get(i);
			WMObject woNext = writeObjects.get(i+1);
			if(inMachtBox(wo,null,woNext,true)) {
				eersteMacht = wo;
				expNr = i+1;
				break;
			}
			else {
				writeObjects.remove(wo);
				i--;
			}
		}
		
		if(eersteMacht==null)
			return;
		ArrayList<WMObject> writeObjectsInExponent = new ArrayList<WMObject>();
		for (int i = 0; i < writeObjects.size(); i++) {
			WMObject wo = writeObjects.get(i);
			WMObject woLast = null;
			if(i>expNr) 
				woLast = writeObjects.get(i-1);
			if(inMachtBox(eersteMacht, woLast, wo, i==expNr)) {
				writeObjectsInExponent.add(wo);
				wo.setIsExponentVan(eersteMacht);
				wo.setIsTellerVan(null);
				wo.setIsNoemerVan(null);
				wo.setIsOnderWortel(null);
				eersteMacht.setIsGrondtal(true);
			}
			else if(i>expNr) {
				break;
			}
		}
		writeObjects.remove(eersteMacht);
		writeObjects.removeAll(writeObjectsInExponent);
		labelMachten(writeObjects);
		labelMachten(writeObjectsInExponent);
	}
	
	private static boolean inWortelBox(WMObject wo, WMObject wortel) {
		DoubleRectangle wortelBox = wortel.getBox();
		return (wo.getBox().x > wortelBox.x+5 
				&& wo.getXBox().x+wo.getXBox().width<wortelBox.x+wortelBox.width+10 
				&& wo.getXBox().y > wortelBox.y-5
				&& wo.getXBox().y+wo.getXBox().height < wortelBox.y+wortelBox.height+10
				);
	}
	
	private static boolean inWortelNdeBox(WMObject wo, WMObject wortel) {
		DoubleRectangle wortelBox = wortel.getBox();
		return (wo.getBox().x+wo.getBox().width < wortelBox.x+wortelBox.height/4
				&& wo.getBox().x>wortelBox.x-wortelBox.width/2 
				&& wo.getBox().y > wortelBox.y-5
				&& wo.getXBox().y+wo.getXBox().height < wortelBox.y+2*wortelBox.height/3
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
	
	private static boolean inMachtBox(WMObject grondtal, WMObject exponentLast, WMObject exponent, boolean first) {
		DoubleRectangle grondtalBox = grondtal.getXBox();
		DoubleRectangle exponentBox = exponent.getXBox();
		if(".".equals(grondtal.getTeken()) 
				|| ",".equals(grondtal.getTeken()) 
				|| "=".equals(grondtal.getTeken())  
				|| "+".equals(grondtal.getTeken())
				|| "-".equals(grondtal.getTeken()) && !grondtal.isBreuk() 
				|| "(".equals(grondtal.getTeken()) 
				|| "/".equals(grondtal.getTeken()))
			return false;
		else if(first && ".".equals(exponent.getTeken())) {
			return false;
		}
		else if(exponentLast!=null) {
			DoubleRectangle exponentLastBox = exponentLast.getXBox();
			return (exponentBox.x > exponentLastBox.x+exponentLastBox.width/2
					&& exponentLastBox.x > grondtalBox.x+grondtalBox.width/2
					&& (exponentBox.y+exponentBox.height/2) - (exponentLastBox.y+exponentLastBox.height/2) < exponentLastBox.height/2
					|| exponentBox.y+exponentBox.height < grondtalBox.y+grondtalBox.height/4);
		}
		return (exponentBox.x > grondtalBox.x+grondtalBox.width/2
				&& exponentBox.y+exponentBox.height < grondtalBox.y+grondtalBox.height/2);
	}
}
