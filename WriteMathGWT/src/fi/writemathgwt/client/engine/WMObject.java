package fi.writemathgwt.client.engine;

import java.util.ArrayList;
import java.util.logging.Logger;

public class WMObject {
	
	private static Logger logger = Logger.getLogger("WMObject");

	private ArrayList<Stroke> strokes = new ArrayList<Stroke>();
	private DoubleRectangle box;
	private String teken;
	
	private WMObject isTellerVan = null;
	private WMObject isNoemerVan = null;
	private WMObject isOnderWortel = null;
	private WMObject isNdeVanWortel = null;
	private WMObject isExponentVan = null;
	private WMObject isSubscriptVan = null;
	
	private boolean isVerwerkt = false;
	private boolean isBreuk = false;
	private boolean isWortel = false;
	private boolean isGrondtal = false;
	private boolean isMetSubscript = false;
	
	private WMObjectLine wmObjectParentLine;
	private WMObjectLine wmObjectChildLine1;
	private WMObjectLine wmObjectChildLine2;
	private WMObjectLine wmObjectChildLineExponent;
	private WMObjectLine wmObjectChildLineSubscript;
	
	private String[] ascFonts = {"b","d","f","h","k","l","t","6","8","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","δ"};
	private String[] descFonts = {"g","j","p","q","y","7","9","β","μ" };
	
	private boolean hasAscent;
	private boolean hasDescent;
	
	private long timeStamp;
	
	public WMObject(Stroke stroke, String teken) {
		strokes.add(stroke);
		this.teken = teken;
		hasAscent = isAscFont(teken);
		hasDescent = isDescFont(teken);
		makeBox();
	}
	
	public WMObject(Stroke stroke1, Stroke stroke2, String teken) {
		strokes.add(stroke1);
		strokes.add(stroke2);
		this.teken = teken;
		hasAscent = isAscFont(teken);
		hasDescent = isDescFont(teken);
		makeBox();
		timeStamp = System.currentTimeMillis();
	}
	
	public WMObject(Stroke stroke1, Stroke stroke2, Stroke stroke3, String teken) {
		strokes.add(stroke1);
		strokes.add(stroke2);
		strokes.add(stroke3);
		this.teken = teken;
		hasAscent = isAscFont(teken);
		hasDescent = isDescFont(teken);
		makeBox();
		timeStamp = System.currentTimeMillis();
	}
	
	public WMObject(Stroke stroke1, Stroke stroke2, Stroke stroke3, Stroke stroke4, String teken) {
		strokes.add(stroke1);
		strokes.add(stroke2);
		strokes.add(stroke3);
		strokes.add(stroke4);
		this.teken = teken;
		hasAscent = isAscFont(teken);
		hasDescent = isDescFont(teken);
		makeBox();
		timeStamp = System.currentTimeMillis();
	}
	
	public WMObject(ArrayList<Stroke> strokes, String teken) {
		this.strokes = strokes;
		this.teken = teken;
		hasAscent = isAscFont(teken);
		hasDescent = isDescFont(teken);
		makeBox();
		timeStamp = System.currentTimeMillis();
	}
	
	public WMObject(WMObject wo) {
		strokes = wo.getStrokes();
		this.teken = wo.getTekenRaw();
		hasAscent = isAscFont(teken);
		hasDescent = isDescFont(teken);
		this.box = new DoubleRectangle(wo.getBox().x , wo.getBox().y , wo.getBox().width , wo.getBox().height);
		timeStamp = wo.getTimeStamp();
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
	
	public boolean isOneStroke() {
		return strokes.size()==1;
	}
	
	public boolean isTwoStroke() {
		return strokes.size()==2;
	}
	
	public boolean isThreeStroke() {
		return strokes.size()==3;
	}
	
	public boolean isFourStroke() {
		return strokes.size()==3;
	}
	
	public String getTekenRaw() {
		return teken;
	}
	
	public String getTeken() {	
		return getTeken(teken);
	}
	
	private String getTeken(String s) {
		if (s != null && s.indexOf("_") > 0)
			s = s.substring(0, s.indexOf("_"));
		if("of".equals(s))
			s = " of ";
		return s;
	}
	
	public ArrayList<Stroke> getStrokes() {
		return strokes;
	}
	
	public DoubleRectangle getBox() {
		return box;
	}
	
	private void makeBox() {
		double minx = 10000;
		double miny = 10000;
		double maxx = -10000;
		double maxy = -10000;
		for(int i=0 ; i<strokes.size() ; i++) {
			minx = Math.min(minx, strokes.get(i).getParsePointsbox().x);
			miny = Math.min(miny, strokes.get(i).getParsePointsbox().y);
			maxx = Math.max(maxx, strokes.get(i).getParsePointsbox().x+strokes.get(i).getParsePointsbox().width);
			maxy = Math.max(maxy, strokes.get(i).getParsePointsbox().y+strokes.get(i).getParsePointsbox().height);
		}
		box = new DoubleRectangle(minx, miny, maxx-minx, maxy-miny);
	}
	
	public boolean hasAscent() {
		return hasAscent;
	}
	
	public boolean hasDescent() {
		return hasDescent;
	}
	
	public boolean isAscFont(String teken ) {
		for(int i=0 ; i<ascFonts.length ; i++) {
			if(ascFonts[i].equals(teken))
				return true;
		}
		return false;
	}
	
	public boolean isDescFont(String teken ) {
		for(int i=0 ; i<descFonts.length ; i++) {
			if(descFonts[i].equals(teken))
				return true;
		}
		return false;
	}
	
	public void setIsTellerVan(WMObject wo) {
		isTellerVan = wo;
	}
	
	public WMObject isTellerVan() {
		return isTellerVan;
	}
	
	public void setIsNoemerVan(WMObject wo) {
		isNoemerVan = wo;
	}
	
	public WMObject isNoemerVan() {
		return isNoemerVan;
	}
	
	public void setIsOnderWortel(WMObject wo) {
		isOnderWortel = wo;
	}
	
	public WMObject isOnderWortel() {
		return isOnderWortel;
	}
	
	public void setIsNdeVanWortel(WMObject wo) {
		isNdeVanWortel = wo;
	}
	
	public WMObject isNdeVanWortel() {
		return isNdeVanWortel;
	}
	
	public void setIsExponentVan(WMObject wo) {
		isExponentVan = wo;
	}
	
	public WMObject isExponentVan() {
		return isExponentVan;
	}
	
	public void setIsSubscriptVan(WMObject wo) {
		isSubscriptVan = wo;
	}
	
	public WMObject isSubscriptVan() {
		return isSubscriptVan;
	}
	
	public void setBreuk(boolean b) {
		isBreuk = b;
	}
	
	public boolean isBreuk() {
		return isBreuk;
	}
	
	public void setWortel(boolean b) {
		isWortel = b;
	}
	
	public boolean isWortel() {
		return ( isWortel);//wortelBox!=null ||
	}
	
	public void setIsGrondtal(boolean b) {
		isGrondtal = b;
	}
	
	public boolean isGrondtal() {
		return isGrondtal;
	}
	
	public void setIsMetSubscript(boolean b) {
		isMetSubscript = b;
	}
	
	public boolean isMetSubscript() {
		return isMetSubscript;
	}
	
	public void setWMObjectParentLine (WMObjectLine wol) {
		wmObjectParentLine = wol;
	}
	
	public WMObjectLine getWMObjectParentLine() {
		return wmObjectParentLine;
	}
	
	public void setWMObjectChildLine1 (WMObjectLine wol) {
		wmObjectChildLine1 = wol;
	}
	
	public WMObjectLine getWMObjectChildLine1() {
		return wmObjectChildLine1;
	}
	
	public void setWMObjectChildLine2 (WMObjectLine wol) {
		wmObjectChildLine2 = wol;
	}
	
	public WMObjectLine getWMObjectChildLine2() {
		return wmObjectChildLine2;
	}
	
	public void setWMObjectChildLineExponent (WMObjectLine wol) {
		wmObjectChildLineExponent = wol;
	}
	
	public WMObjectLine getWMObjectChildLineExponent() {
		return wmObjectChildLineExponent;
	}
	
	public void setWMObjectChildLineSubscript (WMObjectLine wol) {
		wmObjectChildLineSubscript = wol;
	}
	
	public WMObjectLine getWMObjectChildLineSubscript() {
		return wmObjectChildLineSubscript;
	}
	
	
	public double getAverageLineHeight() {
		if(wmObjectParentLine!=null)
			return wmObjectParentLine.getAverageHeight();
		else
			return WMObjectLine.DEFAULTAVERAGEHEIGHT;
	}
	
	public double getAverageBaseLine() {
		if(wmObjectParentLine!=null)
			return wmObjectParentLine.getAverageBaseLine();
		else
			return getXBox().y + getXBox().height;
	}
	
	public double getXHeight() {
		double factor = 1;
		if(hasDescent || hasAscent)
			factor /= 1.5;
		return getBox().height*factor;
	}
	
	public DoubleRectangle getXBox() {
		double x = box.x;
		double width = box.width;
		double y = hasAscent ? box.y+box.height/3 : box.y;
		double height = hasAscent||hasDescent ? 2*box.height/3 : box.height;
		if("-".equals(getTeken())
				|| "sqrt".equals(getTeken()) 
				|| "=".equals(getTeken())
				|| "\u2190".equals(getTeken())
				|| "\u2192".equals(getTeken())) {
			y = box.y+box.height/2 - getAverageLineHeight()/2;
			height = getAverageLineHeight();
		}
		else if(",".equals(getTeken())) {
			y = box.y - getAverageLineHeight();
			height = getAverageLineHeight();
		}
		
		return new DoubleRectangle(x,y,width,height);
	}
	
	public DoublePoint getBoxMid() 
	{
		if (hasAscent())
			return new DoublePoint(box.x + box.width / 2, box.y + 2 * box.height / 3);
		else if (hasDescent())
			return new DoublePoint(box.x + box.width / 2, box.y + box.height / 3);	
		else
			return new DoublePoint(box.x + box.width / 2, box.y + box.height / 2);
	}
		
	public void translate(double dx, double dy) {
		box.translate(dx, dy);
	}
	
	public void scale(double cx, double cy,double factor) {
		box.scale(cx, cy, factor);
	}
	
	public void setInContext() {
		if(isGrondtal && wmObjectChildLineExponent!=null) {
			setTekenInContext();
			wmObjectChildLineExponent.setInContext();
			if(isWortel) { 
				if(wmObjectChildLine1!=null)
					wmObjectChildLine1.setInContext();
				if(wmObjectChildLine2!=null)
					wmObjectChildLine2.setInContext();
			}
			else if(isBreuk) { 
				if(wmObjectChildLine1!=null)
					wmObjectChildLine1.setInContext();
				if(wmObjectChildLine2!=null)
					wmObjectChildLine2.setInContext();
			}
		}
		else if(isWortel) { 
			if(wmObjectChildLine1!=null)
				wmObjectChildLine1.setInContext();
			if(wmObjectChildLine2!=null)
				wmObjectChildLine2.setInContext();
		}
		else if(isBreuk) { 
			if(wmObjectChildLine1!=null)
				wmObjectChildLine1.setInContext();
			if(wmObjectChildLine2!=null)
				wmObjectChildLine2.setInContext();
		}
		else
			setTekenInContext();
	}
	
	public String getFormulaString() {
		String formulaString = "";
		String subscriptString = "";
		if(isMetSubscript && wmObjectChildLineSubscript!=null) {
			subscriptString = "$s";
			if(wmObjectChildLineSubscript!=null)
				for(int i=0 ; i<wmObjectChildLineSubscript.getWMObjects().size() ; i++)
					subscriptString = subscriptString + wmObjectChildLineSubscript.getWMObjects().get(i).getFormulaString();
			subscriptString = subscriptString + "@";
		}
		if(isGrondtal && wmObjectChildLineExponent!=null) {
			if(isBreuk) {
				formulaString = formulaString + "$b";
				if(wmObjectChildLine1!=null)
					for(int i=0 ; i<wmObjectChildLine1.getWMObjects().size() ; i++)
						formulaString = formulaString + wmObjectChildLine1.getWMObjects().get(i).getFormulaString();
				formulaString = formulaString + "$n";
				if(wmObjectChildLine2!=null)
					for(int i=0 ; i<wmObjectChildLine2.getWMObjects().size() ; i++)
						formulaString = formulaString + wmObjectChildLine2.getWMObjects().get(i).getFormulaString();
				formulaString = formulaString + "@@";
			}
			else if(isWortel && wmObjectChildLine2==null) {
				formulaString = formulaString + "$w";
				if(wmObjectChildLine1!=null) {
					for(int i=0 ; i<wmObjectChildLine1.getWMObjects().size() ; i++)
						formulaString = formulaString + wmObjectChildLine1.getWMObjects().get(i).getFormulaString();
				}
				formulaString = formulaString + "@";
			}
			else if(isWortel && wmObjectChildLine2!=null) {	
				formulaString = formulaString + "$W";
				if(wmObjectChildLine1!=null) {
					for(int i=0 ; i<wmObjectChildLine1.getWMObjects().size() ; i++)
						formulaString = formulaString + wmObjectChildLine1.getWMObjects().get(i).getFormulaString();
				}
				formulaString = formulaString + "$n";
				if(wmObjectChildLine2!=null) {
					for(int i=0 ; i<wmObjectChildLine2.getWMObjects().size() ; i++)
						formulaString = formulaString + wmObjectChildLine2.getWMObjects().get(i).getFormulaString();
				}
				formulaString = formulaString + "@@";
			}
			else
				formulaString = formulaString + getTeken();
			
			formulaString = formulaString + subscriptString + "$m";
			if(wmObjectChildLineExponent!=null)
				for(int i=0 ; i<wmObjectChildLineExponent.getWMObjects().size() ; i++)
					formulaString = formulaString + wmObjectChildLineExponent.getWMObjects().get(i).getFormulaString();
			formulaString = formulaString + "@";
		}
		else if(isWortel && wmObjectChildLine2==null) {
			formulaString = formulaString + "$w";
			if(wmObjectChildLine1!=null) {
				for(int i=0 ; i<wmObjectChildLine1.getWMObjects().size() ; i++)
					formulaString = formulaString + wmObjectChildLine1.getWMObjects().get(i).getFormulaString();
			}
			formulaString = formulaString + "@";
		}
		else if(isWortel && wmObjectChildLine2!=null) {	
			logger.info("inWMObject nde wortel");
			formulaString = formulaString + "$W";
			if(wmObjectChildLine1!=null) {
				for(int i=0 ; i<wmObjectChildLine1.getWMObjects().size() ; i++)
					formulaString = formulaString + wmObjectChildLine1.getWMObjects().get(i).getFormulaString();
			}
			formulaString = formulaString + "$n";
			if(wmObjectChildLine2!=null) {
				for(int i=0 ; i<wmObjectChildLine2.getWMObjects().size() ; i++)
					formulaString = formulaString + wmObjectChildLine2.getWMObjects().get(i).getFormulaString();
			}
			formulaString = formulaString + "@@";
		}
		else if(isBreuk) {
			formulaString = formulaString + "$b";
			if(wmObjectChildLine1!=null)
				for(int i=0 ; i<wmObjectChildLine1.getWMObjects().size() ; i++)
					formulaString = formulaString + wmObjectChildLine1.getWMObjects().get(i).getFormulaString();
			formulaString = formulaString + "$n";
			if(wmObjectChildLine2!=null)
				for(int i=0 ; i<wmObjectChildLine2.getWMObjects().size() ; i++)
					formulaString = formulaString + wmObjectChildLine2.getWMObjects().get(i).getFormulaString();
			formulaString = formulaString + "@@";
		}
		
		else {
			formulaString = getTeken() + subscriptString;
			if(".".equals(getTeken())) {
				logger.info("averageBaseLine: "+getAverageBaseLine());
				logger.info("averageHeight: "+getAverageLineHeight());
				logger.info("y: "+getBox().y);
			}
			if(".".equals(getTeken()) && getBox().y < getAverageBaseLine() - getAverageLineHeight()/3 && getBox().y > getAverageBaseLine() - 4*getAverageLineHeight()/4) {
				formulaString = "*";
			}
		}
		return formulaString;
	}
	
	public String setTekenInContext() {
		if(wmObjectParentLine==null)
			return getTeken();
		String teken = getTeken();
		if("/".equals(getTeken()) && box.height<getAverageLineHeight())
			teken = "1";
		else if("\\".equals(getTeken()) && box.height<getAverageLineHeight())
			teken = "1";
		else if(")".equals(getTeken()) && box.height<getAverageLineHeight()/2) {
			teken = ",";
		}
		else if("\u27e9".equals(getTeken()) && box.height<getAverageLineHeight()/2)
			teken = ",";
		else if(")".equals(getTeken()) && box.height<20 && box.height<getAverageLineHeight() && 3*box.width<box.height)
			teken = "1";
		else if("(".equals(getTeken()) && box.height<20 && box.height<getAverageLineHeight() && 3*box.width<box.height)
			teken = "1";
		else if("l".equals(getTeken()) && box.height<1.2*getAverageLineHeight() && 3*box.width<box.height)
			teken = "1";
//		else if("1".equals(getTeken()) && box.height>1.3*getAverageLineHeight() )
//			teken = "l";
		else if("c".equals(getTeken()) && box.height>1.2*getAverageLineHeight())
			teken = "C";
		else if("o".equals(getTeken()) && box.height>1.2*getAverageLineHeight())
			teken = "O";
		else if("s".equals(getTeken()) && box.height>1.2*getAverageLineHeight())
			teken = "S";
		else if("u".equals(getTeken()) && box.height>1.2*getAverageLineHeight())
			teken = "U";
		else if("v".equals(getTeken()) && box.height>1.2*getAverageLineHeight())
			teken = "V";
		else if("w".equals(getTeken()) && box.height>1.2*getAverageLineHeight())
			teken = "W";
//		else if("x".equals(getTeken()) && box.height>1.3*getAverageLineHeight())
//			teken = "X";
//		else if("y".equals(getTeken()) && box.height>2.0*getAverageLineHeight())
//			teken = "Y";
//		else if("z".equals(getTeken()) && box.height>1.2*getAverageLineHeight())
//			teken = "Z";
		else if("+".equals(getTeken()) && box.height>1.2*getAverageLineHeight() && 2*box.width<box.height)
			teken = "t";
		
		this.teken = teken;
		hasAscent = isAscFont(teken);
		hasDescent = isDescFont(teken);
		makeBox();
		return teken;
	}
}
