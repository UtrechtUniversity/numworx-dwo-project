package fi.writemathgwt.client;


import java.util.ArrayList;


/**
 * 
 * @author Danny Hendrix
 * 
 */
public class Breukvak extends FormuleElementWithChildren
{
	public Breukvak(FormuleElement holder)
	{
		super(holder);

		this.createChildren(2);

		this.setChanged(true);
	}
	
	private boolean onlyDigits(String s)
	{	for (int i = 0; i < s.length(); i++)
		{	if (!Character.isDigit(s.charAt(i))) 
				return false;
		}
		if (s.length() == 0) 
			return false;
		return true;
	}


	public void paint()
	{
		if (onlyDigits(getChild(0).toString()) && onlyDigits(getChild(1).toString()))
		{
			getChild(0).setSmallText(true);
			getChild(1).setSmallText(true);
		}
		else
		{
			getChild(0).setSmallText(false);
			getChild(1).setSmallText(false);
		}
		this.getChild(0).paint();
		this.getChild(1).paint();
		
		this.setSize(width, height);
		
		getChild(0).y = 0;

		getChild(0).x = (width - getChild(0).width)/2;
		getChild(1).x = (width - getChild(1).width)/2;

	}
	
	public void setPosition(int xPos, int yPos)
	{	
		x = xPos;
		y = yPos;
		
		FormuleRegel parentRegel = getRegelParent();
		y = yPos + parentRegel.ascent - ascent;
		
//System.out.println("Breukvak setPosition x = " + x + " y = " + y);		

		int txSpace = 0;
		int tySpace = 0;
		if (getChild(0).width > getChild(1).width)
			tySpace = (getChild(0).width - getChild(1).width) / 2;
		else
			txSpace = (getChild(1).width - getChild(0).width) / 2;
		
		
		getChild(0).setPosition(x + txSpace + Samples20.breuklijnExtraBreedte, y);
		getChild(1).setPosition(x + tySpace + Samples20.breuklijnExtraBreedte, 
				                y + getChild(0).getHeight() + 2 * Samples20.breuklijnExtraHoogte +
				                Samples20.breuklijnDikte);
		
	}

	// dit is alleen voor de breukstreep
	public ArrayList<Point> scaleAndPosition(ArrayList<Point> pList, int xPos, int yPos)
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
		
//System.out.println("yMin = " + yMin + "yMax = " + yMax);

		double scaleX = ((double) width) /(xMax - xMin);
		double scaleY = 1; //((double) Samples20.breuklijnDikte) /(yMax - yMin);
	
		ArrayList<Point> newList = new ArrayList<Point>();
		for (int i = 0; i < pList.size(); i++)  {	
			int px = pList.get(i).x;
			int py = pList.get(i).y;
			
			int npx = xPos + (int) Math.round(scaleX * (px - xMin));
			int npy = yPos + (py - yMin); // (int) Math.round(scaleY * (py - yMin));
			
			newList.add(new Point(npx,npy));			
		}	
		return newList;
	}
	
	//convertToWriteObject
	public void convertToWriteObject()
	{ 
		FormuleElement root = findRoot();
		
		String oTeken = "-";
		int[] oIntArray = WriteObject.samples.get(oTeken);
		ArrayList<Point> oPoints = WriteObject.intConvertSample(oIntArray);
		// schalen en op de goede plek zetten
		ArrayList<Point> sPoints = scaleAndPosition(oPoints, x, y + getChild(0).getHeight() + Samples20.breuklijnExtraHoogte);
		// checken?
		((FormuleRoot) root).owner.addWriteObject(oTeken, sPoints);
		
		getChild(0).convertToWriteObject();
		getChild(1).convertToWriteObject();
//System.out.println("Breukvak convertToWriteObject");		
	}


	public void findSize(int leafWidth, int leafHeight)
	{
		if ((getChild(0).getWidth() == 0) || (getChild(0).getHeight() == 0))
			getChild(0).findSize(leafWidth, leafHeight);
		if ((getChild(1).getWidth() == 0) || (getChild(1).getHeight() == 0))
			getChild(1).findSize(leafWidth, leafHeight);
		width = Math.max(getChild(0).getWidth(), getChild(1).getWidth()) + 2 * Samples20.breuklijnExtraBreedte;
		height = getChild(0).getHeight() + 2 * Samples20.breuklijnExtraHoogte + Samples20.breuklijnDikte + 
				 getChild(1).getHeight();
		
		ascent = getChild(0).getHeight() + Samples20.breuklijnExtraHoogte + Samples20.breuklijnDikte / 2 -
				 leafHeight / 2;
		
//System.out.println("Breukvak findSizes w = " + width + " h = " + height);
//System.out.println("Breukvak asc = " + ascent);
		
	}
	
	
	public int getAsHoogte()
	{
		return 0; //getChild(0).height + 5 * fm.getAscent() / 8;
	}

	@Override
	public FormuleElement getCurrentOnNew()
	{
		//the current element with a new instance is the first child
		return this.getChild(0);
	}

	@Override
	public FormuleElement getCurrentOnNewOnSelection()
	{
		return getChild(1);
	}

	@Override
	public String toString()
	{
		return "$b" + getChild(0).toString() + "$n" + getChild(1).toString() + "@@";
	}
}
