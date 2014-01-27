package nl.uu.fi.dwo.interaction.client;


/**
 * This class keeps track of changes made to the font (for instance if we have a
 * "breukvak" the children have smaller text, to keep the text smaller when the
 * main fontsize changes, we apply these changes)
 * 
 * @author Danny Hendrix
 * 
 */
public class FormuleFontChanges
{
	public static final int NOT_SET = -1;
	public static final int FALSE = 0;
	public static final int TRUE = 1;

	private int italic = NOT_SET;
	private int bold = NOT_SET;
	private int relativeSize = NOT_SET;
	private int smalltext = NOT_SET;

	public void applyToFont(FormuleFont font)
	{
		if (italic > NOT_SET)
			font.setItalic(italic == TRUE);
		if (bold > NOT_SET)
			font.setBold(bold == TRUE);
		if (relativeSize > NOT_SET)
			font.setSizeRelativeTo(relativeSize);
		if (smalltext > NOT_SET)
			font.setSmallText(smalltext == TRUE);
	}

	public void setBold(int val)
	{
		bold = val;
	}

	public void setSmallText(int val)
	{
		smalltext = val;
	}

	public void setItalic(int val)
	{
		italic = val;
	}

	public void setRelativeSize(int val)
	{
		relativeSize = val;
	}

	public boolean isSmallText()
	{
		return this.smalltext == TRUE;
	}
}
