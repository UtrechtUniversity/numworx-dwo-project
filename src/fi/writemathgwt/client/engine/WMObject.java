package fi.writemathgwt.client.engine;

import java.util.ArrayList;

public class WMObject {

	private ArrayList<Stroke> strokes = new ArrayList<Stroke>();
	private DoubleRectangle box;
	private String teken;
	
	private WMObject isTellerVan = null;
	private WMObject isNoemerVan = null;
	private WMObject isMachtVan = null;
	private WMObject isOnderWortel = null;	
	private boolean isVerwerkt = false;
	private boolean isBreuk = false;
	private DoubleRectangle tellerBox = null;
	private DoubleRectangle noemerBox = null;
	private DoubleRectangle wortelBox = null;
	
	private String[] ascFonts = {"b","d","f","h","k","l","t","6","8"};
	private String[] descFonts = {"g","j","p","q","y","7","9"};
	
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
		isOnderWortel = wo;
		
		if(copyFrom!=null) {
			if(wo==null)
				copyFrom.setIsOnderWortel(null);
			else
				copyFrom.setIsOnderWortel(wo.getCopyFrom());
		}
	}
	
	public WMObject isOnderWortel() {
		return isOnderWortel;
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
	
	public boolean isBreuk() {
		return isBreuk;
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
		double y = hasAscent ? box.y+box.height/3 : box.y;
		double width = box.width;
		double height = hasAscent||hasDescent ? 2*box.height/3 : box.height;
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
	
	
}
