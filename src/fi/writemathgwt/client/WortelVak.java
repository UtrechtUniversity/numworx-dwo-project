package fi.writemathgwt.client;

//import java.awt.Point;
import java.util.ArrayList;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class WortelVak extends FormuleElementWithChildren
{
	public WortelVak(FormuleElement editor)
	{
		super(editor, 1);

		this.setChanged(true);

	}

	public void paint()
	{
		if (this.isChanged() == false)
			return;

		this.getChild().paint();

		this.setSize(width, height);

	}

	
	// eerst maar even met zelfde font
	public void findSize(int leafWidth, int leafHeight)
	{
		if ((getChild(0).getWidth() == 0) || (getChild(0).getHeight() == 0))
			getChild(0).findSize(leafWidth, leafHeight);
		width = getChild(0).getWidth() + Samples20.wortelBreedte;
		height = getChild(0).getHeight() + Samples20.wortelHoogte;

		FormuleRegel fr = getRegelParent();
		FormuleElement fe = null;
		if (fr != null)
			fe = fr.getParent();
		
		//if ((fe != null) && (fe instanceof Machtvak))
		//{	
			//ascent = getChild(0).getHeight() + Samples20.wortelHoogte - Samples20.powerShift;
		//	ascent = getChild(0).height + Samples20.wortelHoogte - 2 * (leafHeight - Samples20.powerShift);
//System.out.println("par Machtvak");		
		//}
		//else
		//{	
			ascent = (getChild(0).height + Samples20.wortelHoogte - leafHeight) / 2 ;
		
		//}
	
//System.out.println("WortelVak lw = " + leafWidth + " lh = " + leafHeight);		
//System.out.println("WortelVak findSizes w = " + width + " h = " + height);			
//System.out.println("WortelVak child(0) h = " + getChild(0).getHeight());		

	}

	public void setPosition(int xPos, int yPos)
	{
		x = xPos;
		y = yPos;
		
		FormuleRegel parentRegel = getRegelParent();
		y = yPos + parentRegel.ascent - ascent;
		
//System.out.println("WortelVak setPosition x = " + x + " y = " + y);
//System.out.println("WortelVak pasc = " + parentRegel.ascent);


		getChild(0).setPosition(x + Samples20.wortelBreedte, y + Samples20.wortelHoogte);
		
	}
	
	// de eerste vpoints punten alleen vertikaal schalen
	public ArrayList<Point> scaleAndPosition(ArrayList<Point> pList, int vPoints)
	{
		int xMin = 1000;
		int xMax = 0;
		int yMin = 1000;
		int yMax = 0;
		for (int i = 0; i < pList.size(); i++) 
		{ 
			if (i > (vPoints - 1))
				xMin = Math.min(xMin, pList.get(i).x);
			yMin = Math.min(yMin, pList.get(i).y);
			if (i > (vPoints - 1))
				xMax = Math.max(xMax, pList.get(i).x);
			yMax = Math.max(yMax, pList.get(i).y);
		}
		ArrayList<Point> newList = new ArrayList<Point>();
		for (int i = 0; i < pList.size(); i++) 
		{	int px = pList.get(i).x;
			int py = pList.get(i).y;
			int pMax = pList.get(vPoints - 1).x;
			
			double scaleX = ((double) Samples20.wortelBreedte) / pMax;
			if (i > (vPoints - 1))
				scaleX = ((double) width - Samples20.wortelBreedte) /(xMax - xMin);
			double scaleY = ((double) height) /(yMax - yMin);
			int npx = x + (int) Math.round(scaleX * px);
			if (i > (vPoints - 1)) 
				npx = x + Samples20.wortelBreedte + (int) Math.round(scaleX * (px - xMin));
			int npy = y + (int) Math.round(scaleY * (py - yMin));
			
			newList.add(new Point(npx,npy));
		}	
		
		return newList;
	}

	//convertToWriteObject
	public void convertToWriteObject()
	{ 
		FormuleElement root = findRoot();
		
		String oTeken = "sqrt";
		int[] oIntArray = WriteObject.samples.get(oTeken);
		ArrayList<Point> oPoints = WriteObject.intConvertSample(oIntArray);
		// schalen en op de goede plek zetten
		ArrayList<Point> sPoints = scaleAndPosition(oPoints, 15);
		// checken?
		((FormuleRoot) root).owner.addWriteObject(oTeken, sPoints);
		
		getChild(0).convertToWriteObject();
		
//System.out.println("WortelVak convertToWriteObject");		
		
	}

	public int getAsHoogte()
	{	return 0; //getChild().getAsHoogte() + fm.getAscent() / 4;
	}

	@Override
	public String toString()
	{
		return "$w" + getChild().toString() + "@";
	}
}
