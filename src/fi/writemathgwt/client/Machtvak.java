package fi.writemathgwt.client;

//import java.awt.Point;
import java.util.ArrayList;

/**
 * Macht vak
 * 
 * @author Danny Hendrix
 * 
 */
public class Machtvak extends FormuleElementWithChildren
{
	public Machtvak(FormuleElement holder)
	{
		super(holder, 1);

		this.setChanged(true);
	}

	public void paintObject()
	{
		this.getChild().paint();

		zetMaat();

	}
	
	public void zetMaat()
	{
		FormuleRegel parentRegel = getRegelParent();
	
		//previous object in the line
		FormuleElement prev = parentRegel.getPrevious(this);
	
// verbeteren		
		int vgh = 0; //parentRegel.getFont().getHeight();
		int vgah = 0; //parentRegel.getFont().getAscent();
		if (prev != null)
		{
			vgh = prev.getHeight();
			vgah = prev.getAsHoogte();
		}
	
		width = getChild().width;
		
//		height = getChild().height - 2 * fm.getAscent() / 3 - fm.getDescent() + vgh;
//		this.setAsHoogte(getChild().height - 2 * fm.getAscent() / 3 - fm.getDescent() + vgah);
		
		this.setSize(width, height);
		
	}

	// font voor macht kleiner nemen
	public void findSize(int leafWidth, int leafHeight)
	{
		
		
		if ((getChild(0).getWidth() == 0) || (getChild(0).getHeight() == 0))
			getChild(0).findSize(2 * leafWidth / 3, 2 * leafHeight / 3);
		width = getChild(0).getWidth();
		height = getChild(0).getHeight();
		
		//ascent = getChild(0).ascent + Samples20.powerShift;
		if (getChild(0).height > Samples20.powerShift)
			ascent = getChild(0).height - (leafHeight - Samples20.powerShift);
		else
			ascent = Samples20.powerShift;

//System.out.println("child(0) ascent = " + getChild(0).ascent);
//System.out.println("child(0) height = " + getChild(0).height);

//System.out.println("Machtvak findSizes w = " + width + " h = " + height);

		FormuleRegel fr = getRegelParent();
		FormuleElement fe = fr.getPrevious(this);
		
		if (fe != null) // should be
		{
			
		}
		
		if ((fe != null) && (fe instanceof Breukvak))
		{
			
			int bvIndex = fr.getIndexOf(fe);
			FormuleTeken haakjeLinks = fr.insertFormuleTekenAt(bvIndex, '(');
			FormuleTeken haakjeRechts = fr.insertFormuleTekenAt(bvIndex + 2, ')');
			
			haakjeLinks.setSize(Samples20.haakjesBreedte, fe.height);
			haakjeLinks.ascent = fe.ascent;
			
			haakjeRechts.setSize(Samples20.haakjesBreedte, fe.height);
			haakjeRechts.ascent = fe.ascent;
			
			
		}

	}

	public void setPosition(int xPos, int yPos)
	{
		x = xPos - Samples20.charHSpace + Samples20.machtHSpace;
		y = yPos;
		//y = yPos - Samples20.powerShift * (int) Math.round((double) height / 30);
		
		FormuleRegel parentRegel = getRegelParent();
		y = yPos + parentRegel.ascent - ascent;

		
		getChild(0).setPosition(x, y);
//System.out.println("Machtvak setPosition x = " + x + " y = " + y);		
	}
	
	//convertToWriteObject
	public void convertToWriteObject()
	{ 
		getChild(0).convertToWriteObject();
		
//System.out.println("Machtvak convertToWriteObject");		
		
	}

	
	public int getAsHoogte()
	{	FormuleRegel parentRegel = getRegelParent();
		FormuleElement prev = parentRegel.getPrevious(this);
//verbeteren		
		int vgah = 0; //parentRegel.getFont().getAscent();
		if (prev != null)
		{
			vgah = prev.getAsHoogte();
		}
		return 0; //getChild().height - 2 * fm.getAscent() / 3 - fm.getDescent() + vgah;
		
	}

	@Override
	public String toString()
	{
		return "$m" + getChild().toString() + "@";
	}
}
