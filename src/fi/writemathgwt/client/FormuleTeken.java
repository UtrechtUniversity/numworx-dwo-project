package fi.writemathgwt.client;

//import java.awt.*;
import java.util.*;
import java.util.logging.Logger;


public class FormuleTeken extends FormuleElement {
//	private static Logger logger = Logger.getLogger("FormuleTeken");	

	private String teken;
	char character;
	private boolean selected = false;
	private boolean functieTeken = false;
	private static boolean maalteken = false;

	public FormuleTeken(FormuleElement holder, char tk)
	{
		super(holder);
		character = tk;
		
//System.out.println("FT = " + character);		
		
		if (tk == '+' || tk == '=' || tk == '<' || tk == '>' || tk == '\u2264' || tk == '\u2265' || 
			tk == '\u2248' || tk == ':')
			teken = " " + tk + " ";
		
		//Sietske
		//else if(maalteken && (tk == '*' || tk == '\u00d7'))
		//	teken = " \u00d7 ";
		
		else if (tk == '*')
			teken = null;
		else if (tk == '\u00d7') //keerteken
			teken = null;
		else if (tk == '-')
			teken = null;
		
		//Sietske
		//else if (tk == ':')
		//	teken = null;

		else if (tk == '\u3008') // puntig haakje links
			teken = null;
		else if (tk == '\u3009') //puntig haakje rechts 
			teken = null;
		else if (tk == '[')
			teken = null;
		else if (tk == ']')
			teken = null;
		else if (tk == '\u2220') //hoek
			teken = null;
		else
			teken = "" + tk;
		

		selected = false;

		//this.setAsHoogte(fm.getAscent() / 2);
		//this.setAsHoogte(fm.getAscent());//maakt geen verschil..?
		
	}


	public static void zetMaalTeken(boolean b)
	{	maalteken = b;
	}
	

	public ArrayList<Point> scaleAndPosition(ArrayList<Point> pList)
	{
		int xMin = 1000;
		int xMax = 0;
		int yMin = 1000;
		int yMax = 0;
		for (int i = 0; i < pList.size(); i++) 
		{ 
			xMin = Math.min(xMin, pList.get(i).x);
			yMin = Math.min(yMin, pList.get(i).y);
			xMax = Math.max(xMax, pList.get(i).x);
			yMax = Math.max(yMax, pList.get(i).y);
		}
		ArrayList<Point> newList = new ArrayList<Point>();
		for (int i = 0; i < pList.size(); i++) 
		{	int px = pList.get(i).x;
			int py = pList.get(i).y;
			
			double scaleX = ((double) width) /(xMax - xMin);
			double scaleY = ((double) height) /(yMax - yMin);
			int npx = x + (int) Math.round(scaleX * (px - xMin));
			int npy = y + (int) Math.round(scaleY * (py - yMin));
			
			newList.add(new Point(npx,npy));
		}	
		
		return newList;
	}
	
	public ArrayList<Point> scaleAndPosition(ArrayList<Point> pList, ArrayList<Point> pListAssist)
	{
		int xMin = 1000;
		int xMax = 0;
		int yMin = 1000;
		int yMax = 0;
		for (int i = 0; i < pList.size(); i++) 
		{ 
			xMin = Math.min(xMin, pList.get(i).x);
			yMin = Math.min(yMin, pList.get(i).y);
			xMax = Math.max(xMax, pList.get(i).x);
			yMax = Math.max(yMax, pList.get(i).y);
		}
		for (int i = 0; i < pListAssist.size(); i++) 
		{ 
			xMin = Math.min(xMin, pListAssist.get(i).x);
			yMin = Math.min(yMin, pListAssist.get(i).y);
			xMax = Math.max(xMax, pListAssist.get(i).x);
			yMax = Math.max(yMax, pListAssist.get(i).y);
		}
		ArrayList<Point> newList = new ArrayList<Point>();
		for (int i = 0; i < pList.size(); i++) 
		{	int px = pList.get(i).x;
			int py = pList.get(i).y;
			
			double scaleX = ((double) width) /(xMax - xMin);
			double scaleY = ((double) height) /(yMax - yMin);
			int npx = x + (int) Math.round(scaleX * (px - xMin));
			int npy = y + (int) Math.round(scaleY * (py - yMin));
			
			newList.add(new Point(npx,npy));
		}	
		
		return newList;
	}

	
	//convertToWriteObject
	public void convertToWriteObject()
	{ 
//System.out.println("FT convertToWriteObject start");

		FormuleElement root = findRoot();
		// uitzonderingen
		String oTeken = "";
		
		if ((character == '<')||(character == '>')|| (character == '\u2264') || (character == '\u2265') ||
			(character == '=')	) {
			oTeken = " " + character + " ";
		}
		else
			oTeken = "" + character;
//System.out.println("character = " + character);		
//System.out.println("oTeken = " + oTeken);		
		
		// apart
		if (oTeken.equals(".")) {
			ArrayList<Point> sPoints = new ArrayList<Point>();
			sPoints.add(new Point(x + width / 2, y + height));
			((FormuleRoot) root).owner.addWriteObject(oTeken, sPoints);
			return;
		}
		if (oTeken.equals("*") || oTeken.equals("\u00d7")) {
			ArrayList<Point> sPoints = new ArrayList<Point>();
			sPoints.add(new Point(x + width / 2, y + height / 2));

			((FormuleRoot) root).owner.addWriteObject(oTeken, sPoints);
			return;
		}

		int[] oIntArray1=null;
		int[] oIntArray2=null;
		if ( Samples20.isTwoStroke(oTeken) ) {
			oIntArray1 = Samples20.getSamplePart(oTeken , 1);
			oIntArray2 = Samples20.getSamplePart(oTeken , 2);
		} else {
			oIntArray1 = WriteObject.samples.get(oTeken);
		}

		// sample not available
		if (oIntArray1 == null) {
			return;
		}
		
		if ( Samples20.isTwoStroke(oTeken) ) {
			ArrayList<Point> oPoints1 = WriteObject.intConvertSample(oIntArray1);
			ArrayList<Point> oPoints2 = WriteObject.intConvertSample(oIntArray2);
			ArrayList<Point> sPoints1 = scaleAndPosition(oPoints1, oPoints2);
			ArrayList<Point> sPoints2 = scaleAndPosition(oPoints2, oPoints1);
			((FormuleRoot) root).owner.addWriteObject(oTeken, sPoints1, sPoints2);
			
		} else {
			ArrayList<Point> oPoints = WriteObject.intConvertSample(oIntArray1);
			ArrayList<Point> sPoints = scaleAndPosition(oPoints);
			((FormuleRoot) root).owner.addWriteObject(oTeken, sPoints);
		}
	}
	
	// eerst maar even alles evenhoog
	public void findSize(int leafWidth, int leafHeight)
	{	
		width = leafWidth;
		height = leafHeight;
		
		setAscent();
		setDescent();
		
		String oTeken = "" + character;
		// apart
		if (oTeken.equals("."))
		{
			width = leafWidth / 4;
		}
		if (oTeken.equals("1"))
		{
			width = leafWidth / 4;
		}
		if (oTeken.equals("i") || oTeken.equals("j") || oTeken.equals("l"))
		{
			width = leafWidth / 2;
		}	
		if (oTeken.equals("*") || oTeken.equals("\u00d7"))
		{
			width = leafWidth / 2;
		}
		if (oTeken.equals("<") || oTeken.equals(">") || (character == '\u2264') || (character == '\u2265') ||
			oTeken.equals("="))
		{
			width = 3 * leafWidth / 2;
		}
		if (oTeken.equals("="))
		{
			height = height - 2 * Samples20.equalMinderHoog;
		}
		if (oTeken.equals("(") || oTeken.equals(")"))
		{
			width = Samples20.haakjesBreedte;
		}

//System.out.println("FT findSizes w = " + width + " h = " + height);		
	}	
	
	public void setPosition(int xPos, int yPos)
	{
		x = xPos;
		y = yPos;
		
		FormuleRegel parentRegel = getRegelParent();

		//parentRegel.setAscent();
		y = yPos + parentRegel.ascent - ascent;

		String oTeken = "" + character;
		
		if (oTeken.equals("="))
		{
			y += Samples20.equalMinderHoog;
		}
		if (oTeken.equals("-"))
		{
			y += (height - 2) / 2;
		}
		
//System.out.println("pasc = " + character + " " + parentRegel.ascent);
//System.out.println("asc = " + character + " " + ascent);
		
//System.out.println("FT setPosition x = " + x + " y = " + y);		
	}
	
/*	
	public void paint(Graphics g)
	{
		this.setSize(width, height);
		//this.setAsHoogte(fm.getAscent());
		
		//draw single character
		if (teken != null)
		{	//setFont(fm);
			//this.setupCTXState();
			//ctx.fillText(teken, 0, this.getAsHoogte());
		}
		else if (character == '*' || character == '\u00d7')
		{	//this.drawKeer();
		}
		else if (character == '-')
		{	//this.drawMin();
		}
		else if (character == ':')
		{	//this.drawDubbelePunt();
		}
		else if (character == 'z')
		{
		}

		else if (character == '\u3008')
		{
		}
		else if (character == '\u3009')
		{
		}
		else if (character == '[')
		{
		}
		else if (character == ']')
		{
		}
		else if (character == '\u2220')
		{
		}
	}
*/
	private void drawKeer()
	{
	}

	private void drawMin()
	{
	}
	
	private void drawDubbelePunt()
	{
	}

	public char geefChar()
	{	return this.character;
	}

	public void zetFunctieTeken(boolean b)
	{	functieTeken = b;
	}
	
	public boolean getFunctieTeken()
	{	return functieTeken;
	}

	public void setAscent()
	{	
		if ((character == 'b') || (character == 'd') || (character == 'h') ||(character == 'f') ||  
			(character == 'k') ||
			(character == 'l') || (character == 't') || (character == '6') || (character == '8'))
		{	height = 3 * height / 2;
			ascent = height / 3;
		}
	}
	
	public void setDescent()
	{	
		if ((character == 'g') || (character == 'j') || (character == 'p') ||
			(character == 'q') || (character == 'y') || (character == '7') || (character == '9'))
		{	height = 3 * height / 2;
		}
	}
	
	public boolean isNumber()
	{	if (this.character == '.' || this.character == ',')
			return true;
		if (this.character >= '0' && this.character <= '9')
			return true;
		return false;
	}

	public String toString()
	{
		return "" + this.character;
	}
}
