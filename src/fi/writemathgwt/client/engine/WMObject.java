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
	private WMObject isMachtVan = null;
	private WMObject isOnderWortel = null;
	private WMObject isNdeVanWortel = null;
	private WMObject wasOnderWortel = null;
	private WMObject isExponentVan = null;
	private boolean isVerwerkt = false;
	private boolean isBreuk = false;
	private boolean isWortel = false;
	private boolean isGrondtal = false;
	private DoubleRectangle tellerBox = null;
	private DoubleRectangle noemerBox = null;
	private DoubleRectangle wortelBox = null;
	
	private WMObjectLine wmObjectParentLine;
	private WMObjectLine wmObjectChildLine1;
	private WMObjectLine wmObjectChildLine2;
	private WMObjectLine wmObjectChildLineExponent;
	
	private String[] ascFonts = {"b","d","f","h","k","l","t","6","8","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","δ"};
	private String[] descFonts = {"g","j","p","q","y","7","9","β","μ" };
	
	private boolean hasAscent;
	private boolean hasDescent;
	
	private WMObject copyFrom;
	
	
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
	}
	
	public WMObject(Stroke stroke1, Stroke stroke2, Stroke stroke3, String teken) {
		strokes.add(stroke1);
		strokes.add(stroke2);
		strokes.add(stroke3);
		this.teken = teken;
		hasAscent = isAscFont(teken);
		hasDescent = isDescFont(teken);
		makeBox();
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
	}
	
	public WMObject(ArrayList<Stroke> strokes, String teken) {
		this.strokes = strokes;
		this.teken = teken;
		hasAscent = isAscFont(teken);
		hasDescent = isDescFont(teken);
		makeBox();
	}
	
	public WMObject(WMObject wo) {
		strokes = wo.getStrokes();
		this.teken = wo.getTekenRaw();
		hasAscent = isAscFont(teken);
		hasDescent = isDescFont(teken);
		this.box = new DoubleRectangle(wo.getBox().x , wo.getBox().y , wo.getBox().width , wo.getBox().height);
		this.copyFrom = wo;
	}
	
	public WMObject getCopyFrom() {
		return copyFrom;
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
//		if (teken!=null && (teken.equals("-") || teken.equals("back")))	{	
//			int height = 2;
//			if (box.height < height)
//				return new DoubleRectangle(box.x - height, box.y - height / 2, box.width + 2 * height, height);
//			else
//				return new DoubleRectangle(box.x - box.height, box.y, box.width + 2 * box.height, box.height);
//		}
		return box;
	}
	
	
//	public DoublePoint getBoxMid() {
//		return new DoublePoint(box.x+box.width/2 , box.y+box.height/2);
//	}
	
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
		
		if(copyFrom!=null) {
			if(wo==null)
				copyFrom.setIsTellerVan(null);
			else
				copyFrom.setIsTellerVan(wo.getCopyFrom());
		}
	}
	
	public WMObject isTellerVan() {
		return isTellerVan;
	}
	
	public void setIsNoemerVan(WMObject wo) {
		isNoemerVan = wo;
		
		if(copyFrom!=null) {
			if(wo==null)
				copyFrom.setIsNoemerVan(null);
			else
				copyFrom.setIsNoemerVan(wo.getCopyFrom());
		}
	}
	
	public WMObject isNoemerVan() {
		return isNoemerVan;
	}
	
	public void setIsMachtVan(WMObject wo) {
		isMachtVan = wo;
		
		if(copyFrom!=null) {
			if(wo==null)
				copyFrom.setIsMachtVan(null);
			else
				copyFrom.setIsMachtVan(wo.getCopyFrom());
		}
	}
	
	public WMObject isMachtVan() {
		return isMachtVan;
	}
	
	public void setIsOnderWortel(WMObject wo) {
//		if(wo==null && isOnderWortel!=null)
//			setWasOnderWortel(isOnderWortel);
		
		isOnderWortel = wo;
		
		if(copyFrom!=null) {
			if(wo==null)
				copyFrom.setIsOnderWortel(null);
			else
				copyFrom.setIsOnderWortel(wo.getCopyFrom());
		}
	}
	
	public void setIsNdeVanWortel(WMObject wo) {
//		if(wo==null && isOnderWortel!=null)
//			setWasOnderWortel(isOnderWortel);
		
		isNdeVanWortel = wo;
		
		if(copyFrom!=null) {
			if(wo==null)
				copyFrom.setIsNdeVanWortel(null);
			else
				copyFrom.setIsNdeVanWortel(wo.getCopyFrom());
		}
	}
	
//	public void setWasOnderWortel(WMObject wo) {
//		wasOnderWortel = wo;
//		
//		if(copyFrom!=null) {
//			if(wo==null)
//				copyFrom.setWasOnderWortel(null);
//			else
//				copyFrom.setWasOnderWortel(wo.getCopyFrom());
//		}
//	}
	
	public WMObject isOnderWortel() {
		return isOnderWortel;
	}
	
	public WMObject wasOnderWortel() {
		return wasOnderWortel;
	}
	
	public WMObject isNdeVanWortel() {
		return isNdeVanWortel;
	}
	
	
	public void setTellerBox(DoubleRectangle box) {
		tellerBox = box;
	}
	
	public DoubleRectangle getTellerBox() {
		return tellerBox;
	}
	
	public void setNoemerBox(DoubleRectangle box) {
		noemerBox = box;
	}
	
	public DoubleRectangle getNoemerBox() {
		return noemerBox;
	}
	
	public void setWortelBox(DoubleRectangle box) {
		wortelBox = box;
		isWortel = wortelBox!=null;
	}
	
	public DoubleRectangle getWortelBox() {
		return wortelBox;
	}
	
	public void setVerwerkt(boolean b) {
		isVerwerkt = b;
	}
	
	public boolean isVerwerkt() {
		return isVerwerkt;
	}
	
	public void setBreuk(boolean b) {
		isBreuk = b;
	}
	
	public void setWortel(boolean b) {
		isWortel = b;
	}
	
	public boolean isBreuk() {
		return isBreuk;
	}
	
	public boolean isWortel() {
		return ( isWortel);//wortelBox!=null ||
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
	
	public void setIsExponentVan(WMObject wo) {
		isExponentVan = wo;
		
		if(copyFrom!=null) {
			if(wo==null)
				copyFrom.setIsExponentVan(null);
			else
				copyFrom.setIsExponentVan(wo.getCopyFrom());
		}
	}
	
	public WMObject isExponentVan() {
		return isExponentVan;
	}
	
	public void setIsGrondtal(boolean b) {
		isGrondtal = b;
	}
	
	public boolean isGrondtal() {
		return isGrondtal;
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
//		if(isMachtVan!=null)
//			factor /= 0.5;
//		if(isTellerVan!=null)
//			factor /= 0.75;
//		if(isNoemerVan!=null)
//			factor /= 0.75;
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
	
//	public boolean hasAscent()
//	{	
//		if (getTeken().equals("f") || getTeken().equals("b") || getTeken().equals("d") || getTeken().equals("h") || getTeken().equals("k") ||
//			getTeken().equals("l") || getTeken().equals("t") || getTeken().equals("6") || getTeken().equals("8"))
//		{	return true;
//		}
//		else 
//			return false;
//	}
//	
//	public boolean hasDescent()
//	{	
//		if (getTeken().equals("f") || getTeken().equals("g") || getTeken().equals("j") || getTeken().equals("p") ||
//			getTeken().equals("q") || getTeken().equals("y") || getTeken().equals("7") || getTeken().equals("9"))
//		{	return true;
//		}
//		else
//			return false;
//	}
	
	public void translate(double dx, double dy) {
		box.translate(dx, dy);
	}
	
	public void scale(double cx, double cy,double factor) {
		box.scale(cx, cy, factor);
	}
	
	public String getFormulaString() {
		String formulaString = "";
		if(isGrondtal) {
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
			
			formulaString = formulaString + "$m";
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
			formulaString = getTeken();
			if(".".equals(getTeken())) {
				logger.info("averageBaseLine: "+getAverageBaseLine());
				logger.info("averageHeight: "+getAverageLineHeight());
				logger.info("y: "+getBox().y);
			}
			if(".".equals(getTeken()) && getBox().y < getAverageBaseLine() - getAverageLineHeight()/3 && getBox().y > getAverageBaseLine() - 3*getAverageLineHeight()/4) {
				formulaString = "*";
				
			}

		}
		
		return formulaString;
	}
	
	
}
