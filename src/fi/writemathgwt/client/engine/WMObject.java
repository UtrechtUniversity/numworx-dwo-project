package fi.writemathgwt.client.engine;

import java.util.ArrayList;

import fi.writemathgwt.client.Rectangle;
import fi.writemathgwt.client.WriteObject;

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
	
	public WMObject(Stroke stroke, String teken) {
		strokes.add(stroke);
		this.teken = teken;
		makeBox();
	}
	
	public WMObject(Stroke stroke1, Stroke stroke2, String teken) {
		strokes.add(stroke1);
		strokes.add(stroke2);
		this.teken = teken;
		makeBox();
	}
	
	public WMObject(Stroke stroke1, Stroke stroke2, Stroke stroke3, String teken) {
		strokes.add(stroke1);
		strokes.add(stroke2);
		strokes.add(stroke3);
		this.teken = teken;
		makeBox();
	}
	
	public WMObject(WMObject wo) {
		strokes = wo.getStrokes();
		this.teken = wo.getTekenRaw();
		this.box = wo.getBox();
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
	
	public String getTekenRaw() {
		return teken;
	}
	
	public String getTeken() {	
		return getTeken(teken);
	}
	
	public String getTeken(String s) {
		if (s != null && s.indexOf("_") > 0)
			s = s.substring(0, s.indexOf("_"));
		return s;
	}
	
	public ArrayList<Stroke> getStrokes() {
		return strokes;
	}
	
	public DoubleRectangle getBox() {
		return box;
	}
	
	public DoublePoint getBoxMid() {
		return new DoublePoint(box.x+box.width/2 , box.y+box.height/2);
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
			maxy = Math.max(maxx, strokes.get(i).getParsePointsbox().y+strokes.get(i).getParsePointsbox().height);
		}
		box = new DoubleRectangle(minx, miny, maxx-minx, maxy-miny);
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
	
	public void setIsMachtVan(WMObject wo) {
		isMachtVan = wo;
	}
	
	public WMObject isMachtVan() {
		return isMachtVan;
	}
	
	public void setIsOnderWortel(WMObject wo) {
		isOnderWortel = wo;
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
}
