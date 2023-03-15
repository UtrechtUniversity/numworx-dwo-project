package fi.writemathgwt.client.engine;

import java.util.ArrayList;
import java.util.logging.Logger;

import fi.writemathgwt.client.engine.WMObject;

public class FormulaProcessor {
	
	private static Logger logger = Logger.getLogger("FormulaProcessor");
	
	public static String addContext(String formuleString) {
		//logger.info("formuleString = "+formuleString);
		formuleString = formuleString.replaceAll("c0s", "cos");
		formuleString = formuleString.replaceAll("c0S", "cos");
		formuleString = formuleString.replaceAll("C0s", "cos");
		formuleString = formuleString.replaceAll("C0S", "cos");
		formuleString = formuleString.replaceAll("l0g", "log");
		formuleString = formuleString.replaceAll("l09", "log");
		formuleString = formuleString.replaceAll("/0g", "log");
		formuleString = formuleString.replaceAll("/09", "log");
		formuleString = formuleString.replace("("+"0s", "cos");
		formuleString = formuleString.replace("("+"0S", "cos");
		formuleString = formuleString.replace("Sin", "sin");
		formuleString = formuleString.replace("s1n", "sin");
		formuleString = formuleString.replace("S1n", "sin");
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
			WMObject wo = wmObjects.get(i);// new WMObject(wmObjects.get(i));//
			wo.setIsTellerVan(null);
			wo.setIsNoemerVan(null);
			wo.setIsOnderWortel(null);
			wo.setIsNdeVanWortel(null);
			wo.setIsExponentVan(null);
			wmObjectsToDo.add(wo);
		}
		labelBreuken(wmObjectsToDo);
		labelWortels(wmObjectsToDo);
		WMObjectLine wol = new WMObjectLine(wmObjectsToDo);
		labelMachten(wol.getWMObjects());
		WMObjectLine woll = new WMObjectLine(wmObjectsToDo);
		woll.setInContext();
		//WMObjectLine wol2 = new WMObjectLine(wmObjectsToDo);
		
		return addContext(woll.getFormula());
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
					wo.setIsOnderWortel(null);
				}
				else if( wo.isNoemerVan()!=null && !inWortelNdeBox(wo.isNoemerVan(), langsteWortel)) {
					langsteWortel.setIsNoemerVan(wo.isNoemerVan());
					wo.setIsNoemerVan(null);
					wo.setIsNdeVanWortel(langsteWortel);
					wo.setIsOnderWortel(null);
				}
				else if(wo.isTellerVan()==null && wo.isNoemerVan()==null) {
					wo.setIsNdeVanWortel(langsteWortel);
					wo.setIsOnderWortel(null);
				}
			}
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
		WMObject eersteMetSubscript = null;
		int expNr = 0;
		for (int i = 0; i < writeObjects.size()-1; i++) {
			WMObject wo = writeObjects.get(i);
			WMObject woNext = writeObjects.get(i+1);
			if(inMachtBox(wo,null,woNext,true)) {
				eersteMacht = wo;
			}
			if(inSubscriptBox(wo,null,woNext,true)) {
				eersteMetSubscript = wo;
			}
			if(eersteMacht==null && eersteMetSubscript==null){
				writeObjects.remove(wo);
				i--;
			}
			else {
				expNr = i+1;
				break;
			}
		}
		
		if(eersteMacht==null && eersteMetSubscript==null)
			return;
		ArrayList<WMObject> writeObjectsInExponent = new ArrayList<WMObject>();
		for (int i = 0; i < writeObjects.size() && eersteMacht!=null; i++) {
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
		ArrayList<WMObject> writeObjectsInSubscript = new ArrayList<WMObject>();
		for (int i = 0; i < writeObjects.size() && eersteMetSubscript!=null; i++) {
			WMObject wo = writeObjects.get(i);
			WMObject woLast = null;
			if(i>expNr) 
				woLast = writeObjects.get(i-1);
			if(inSubscriptBox(eersteMetSubscript, woLast, wo, i==expNr)) {
				writeObjectsInSubscript.add(wo);
				wo.setIsSubscriptVan(eersteMetSubscript);
				wo.setIsTellerVan(null);
				wo.setIsNoemerVan(null);
				wo.setIsOnderWortel(null);
				eersteMetSubscript.setIsMetSubscript(true);
			}
			else if(i>expNr) {
				break;
			}
		}
		writeObjects.remove(eersteMacht);
		writeObjects.removeAll(writeObjectsInExponent);
		writeObjects.removeAll(writeObjectsInSubscript);
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
		return (wo.getBox().x+wo.getBox().width < wortelBox.x+wortelBox.height/3
				&& wo.getBox().x>wortelBox.x-wortelBox.height/4
				&& wo.getBox().y > wortelBox.y-5
				&& wo.getXBox().y+wo.getXBox().height < wortelBox.y+2*wortelBox.height/3
				);
	}
	
	private static boolean inTellerBox(WMObject wo, WMObject breuk) {
		DoubleRectangle breukStreepBox = breuk.getBox();
		return (wo.getXBox().x+wo.getXBox().width/2 > breukStreepBox.x 
				&& wo.getXBox().x+wo.getXBox().width/2<breukStreepBox.x+breukStreepBox.width 
				&& wo.getXBox().y > breukStreepBox.y
				&& wo!=breuk);
	}
	
	private static boolean inNoemerBox(WMObject wo, WMObject breuk) {
		DoubleRectangle breukStreepBox = breuk.getBox();
		return (wo.getXBox().x+wo.getXBox().width/4 > breukStreepBox.x 
				&& wo.getXBox().x+wo.getXBox().width/2<breukStreepBox.x+breukStreepBox.width
				&& wo.getXBox().y+wo.getXBox().height < breukStreepBox.y+breukStreepBox.height
				&& wo!=breuk);
	}
	
	private static boolean inMachtBox(WMObject grondtal, WMObject exponentLast, WMObject exponent, boolean first) {
		DoubleRectangle grondtalBox = grondtal.getXBox();
		DoubleRectangle exponentBox = exponent.getXBox();
		if(isIllegaalGrondtal(grondtal)
				|| first && isIllegaalFirstExponent(exponent))
			return false;
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
	
	private static boolean inSubscriptBox(WMObject metSubscript, WMObject subscriptLast, WMObject subscript, boolean first) {
		DoubleRectangle metSubscriptBox = metSubscript.getXBox();
		DoubleRectangle subscriptBox = subscript.getXBox();
		if(isIllegaalMetSubscript(metSubscript)
				|| first && isIllegaalFirstSubscript(subscript))
			return false;
		else if(subscriptLast!=null) {
			DoubleRectangle subscriptLastBox = subscriptLast.getXBox();
			return (subscriptBox.x > subscriptLastBox.x+subscriptLastBox.width/2
					&& subscriptLastBox.x > metSubscriptBox.x+metSubscriptBox.width/2
					&& (subscriptBox.y+subscriptBox.height/2) - (subscriptLastBox.y+subscriptLastBox.height/2) > -subscriptLastBox.height/2
					|| subscriptBox.y > metSubscriptBox.y+3*metSubscriptBox.height/4);
		}
		return (subscriptBox.x > metSubscriptBox.x+metSubscriptBox.width/2
				&& subscriptBox.y > metSubscriptBox.y+metSubscriptBox.height/2);
	}
	
	private static boolean isIllegaalGrondtal(WMObject grondtal) {
		if(".".equals(grondtal.getTeken()) 
				|| ",".equals(grondtal.getTeken()) 
				|| "=".equals(grondtal.getTeken())  
				|| "+".equals(grondtal.getTeken())
				|| "-".equals(grondtal.getTeken()) && !grondtal.isBreuk() 
				|| "(".equals(grondtal.getTeken()) 
				|| "/".equals(grondtal.getTeken()))
			return true;
		return false;
	}
	
	private static boolean isIllegaalFirstExponent(WMObject exponent) {
		if(".".equals(exponent.getTeken()) 
				|| ",".equals(exponent.getTeken()) 
				|| "=".equals(exponent.getTeken())  
				|| "+".equals(exponent.getTeken())
				|| ")".equals(exponent.getTeken())
				|| "*".equals(exponent.getTeken())
				|| ">".equals(exponent.getTeken())
				|| "<".equals(exponent.getTeken())
				|| "/".equals(exponent.getTeken()))
			return true;
		return false;
	}
	
	private static boolean isIllegaalMetSubscript(WMObject grondtal) {
		if(".".equals(grondtal.getTeken()) 
				|| ",".equals(grondtal.getTeken()) 
				|| "=".equals(grondtal.getTeken())  
				|| "+".equals(grondtal.getTeken())
				|| "-".equals(grondtal.getTeken()) && !grondtal.isBreuk() 
				|| "(".equals(grondtal.getTeken()) 
				|| "/".equals(grondtal.getTeken())
				|| Character.isDigit(grondtal.getTeken().charAt(0)))
			return true;
		return false;
	}
	
	private static boolean isIllegaalFirstSubscript(WMObject exponent) {
		if(".".equals(exponent.getTeken()) 
				|| ",".equals(exponent.getTeken()) 
				|| "=".equals(exponent.getTeken())  
				|| "+".equals(exponent.getTeken())
				|| ")".equals(exponent.getTeken())
				|| "*".equals(exponent.getTeken())
				|| ">".equals(exponent.getTeken())
				|| "<".equals(exponent.getTeken())
				|| "/".equals(exponent.getTeken()))
			return true;
		return false;
	}
	
}
