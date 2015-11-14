package fi.writemathgwt.client;

//import java.awt.*;

public class FormuleRoot extends FormuleElementWithChildren 
{
	WritePanel owner;
	
	public FormuleRoot(WritePanel o)
	{	super(null, 1);
		owner = o;
		
//System.out.println("root created");

	}

	// findSizes()
	public void findSizes(int leafWidth, int leafHeight)
	{
		getChild(0).findSize(leafWidth, leafHeight);
		
//System.out.println("root findSizes w = " + getChild(0).getWidth() + " h = " + getChild(0).getHeight());		
	}
		
	// setPositions
	public void setPositions()
	{
		getChild(0).setPosition(30,30);

//System.out.println("root setPosition x = 30 y = 30");

		// kijk of een object een y heeft die kleiner is dan 30:
		// de hele boom een stukje naar beneden
		// dus gewoon nog een keer?  
	}
	
	// convertToWriteObjects
	public void convertToWriteObjects()
	{
//System.out.println("root convertToWriteObject start");		
				
		getChild(0).convertToWriteObject();

//System.out.println("root convertToWriteObject end");		
	}
	
}
