package nl.uu.fi.dwo.mobile.client.ui.formuleobjects;

/**
 * Because gwt does not have font metrics we use this to make the conversion
 * from the java DWO implementation easier
 * 
 * @author Danny Hendrix
 * 
 */
public class FormuleFont
{
	/*
	Arial 16px:

	ascent = 15 pixels,  
	descent = 3 pixels,  
	internal leading = 2 pixels
	font height = ascent + descent = 15 + 3 = 18 pixels
	font size = font height - internal leading = 18 - 2 = 16 pixels
	 */
	//private int fontSize = 16;
	private int ascent = 0;
	private int descent = 0;
	private int leading = 0;
	private int height = ascent + descent;
	private int fontSize = height - leading;

	private String font = "Arial";
	private boolean bold = false;
	private boolean italic = false;
	private boolean smalltext = false;

	public static FormuleFont getDefault()
	{
		return createFromFontSize(12);
	}

	public static FormuleFont createFromFontSize(int size)
	{
		FormuleFont fm = new FormuleFont();
		fm.ascent = size + 2;
		fm.descent = Math.round(fm.ascent / 12);
		fm.ascent = fm.ascent - fm.descent;
		fm.leading = 2;
		fm.height = fm.ascent + fm.descent;
		fm.fontSize = fm.height - fm.leading;
		return fm;
	}

	public FormuleFont createSmallCopy()
	{
		FormuleFont fm = createCopy();
		fm.setSmallText(true);
		return fm;
	}

	public FormuleFont createCopy()
	{
		FormuleFont fm = FormuleFont.getDefault();

		fm.ascent = this.ascent;
		fm.descent = this.descent;
		fm.leading = this.leading;
		fm.height = this.height;
		fm.fontSize = this.fontSize;
		fm.font = this.font;
		fm.bold = this.bold;
		fm.italic = this.italic;
		fm.smalltext = this.smalltext;

		return fm;
	}

	public static FormuleFont createFromChanges(FormuleFont font, FormuleFontChanges changes)
	{
		FormuleFont fm = font.createCopy();
		changes.applyToFont(fm);
		return fm;
	}

	private FormuleFont()
	{

	}

	public String getFontStyle()
	{
		String ret = "";
		if (bold)
			ret += "bold ";
		if (italic)
			ret += "italic ";
		ret += fontSize + "px ";
		ret += font;
		return ret;
	}

	public int getFontSize()
	{
		return fontSize;
	}

	public void setFontSize(int fontSize)
	{
		this.fontSize = fontSize;
	}

	public int getAscent()
	{
		return ascent;
	}

	public void setAscent(int ascent)
	{
		this.ascent = ascent;
	}

	public int getDescent()
	{
		return descent;
	}

	public void setDescent(int descent)
	{
		this.descent = descent;
	}

	public String getFont()
	{
		return font;
	}

	public void setFont(String font)
	{
		this.font = font;
	}

	public boolean isBold()
	{
		return bold;
	}

	public void setBold(boolean bold)
	{
		this.bold = bold;
	}

	public boolean isItalic()
	{
		return italic;
	}

	public void setItalic(boolean italic)
	{
		this.italic = italic;
	}

	public int getLeading()
	{
		return leading;
	}

	public int getHeight()
	{
		return height;
	}

	public double getStrokeWidth()
	{
		double ret = this.ascent / 12;
		//minimum 1
		if (ret < 1)
			ret = 1;
		return ret;
	}

	private void setSizes(int size)
	{
		ascent = size + 2;
		descent = Math.round(ascent / 12);
		ascent = ascent - descent;
		leading = 2;
		height = ascent + descent;
		fontSize = height - leading;
	}

	public void setSmallText(boolean smalltext)
	{
		if (smalltext != this.smalltext)
		{
			if (smalltext == true)
				this.setSizes(this.fontSize * 3/4);
			else
				this.setSizes(this.fontSize * 4/3);
		}
		this.smalltext = smalltext;
	}

	public void setSizeRelativeTo(int relativeSize)
	{
		int size = this.fontSize / 100 * relativeSize;
		this.ascent = size - 2;
		this.descent = Math.round(this.ascent / 12);
		this.ascent = this.ascent - this.descent;
		this.leading = 2;
		this.height = this.ascent + this.descent;
		this.fontSize = this.height - this.leading;
	}

	public boolean isSmallText()
	{
		return this.smalltext;
	}

	@Override
	public String toString()
	{
		return this.getFontStyle();
	}

}
