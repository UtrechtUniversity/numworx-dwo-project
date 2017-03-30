package nl.uu.fi.dwo.interaction.client;


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
	private int ascent;
	private int descent;
	//private int leading = 0;
	private int height;
	private int fontSize;// - leading;

	private static String defaultFont = "Arial";
	private static String defaultTekstFont = "Arial";
	public static boolean formTimes;
	private String font = "Arial";
	private boolean bold;
	//private boolean italic = true;
	private boolean italic;
	private boolean smalltext;
	
	public static FormuleFont createFromFontSize(int size)
	{
		return createFromFontSize(size, false);
	}
	
	public static FormuleFont createFromFontSize(int size, boolean tekst)
	{
		FormuleFont fm = new FormuleFont();
		if(tekst)
			fm.font = defaultTekstFont;
		else
			fm.font = defaultFont;
		if(formTimes && !tekst)
			size += 2;
		
		//even proberen..
		fm.descent = Math.round(size / 4); //mogelijk erg font-specifiek
		fm.ascent = size + 1;
		fm.height = fm.descent + fm.ascent;
		fm.fontSize = size;
		
		/*
		fm.ascent = size + 2;
		fm.descent = Math.round(fm.ascent / 12);
		fm.ascent = fm.ascent - fm.descent;
		fm.leading = 2;
		fm.height = fm.ascent + fm.descent;
		fm.fontSize = fm.height - fm.leading;
		*/
		
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
		FormuleFont fm = FormuleFont.createFromFontSize(fontSize, false);

		fm.ascent = this.ascent;
		fm.descent = this.descent;
		//fm.leading = this.leading;
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
	
	public static void zetDefaultFont(String font)
	{
		if(font.contains("SansSerif"))
			font = font.replace("SansSerif", "sans-serif");
		if(font.contains("Dialog"))
			font = font.replace("Dialog", "Arial");
		defaultFont = font;
		defaultTekstFont = font;
	}
	
	public static void zetFormTimes(boolean formTimes)
	{
		FormuleFont.formTimes = formTimes;
		if(formTimes)
			defaultFont = "TimesRoman";
//		else  /*zetDefaultFont is net geweest*/
//			defaultFont = defaultTekstFont;
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

	/*
	public int getLeading()
	{
		return leading;
	}*/

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

		//fm.descent = Math.round(size / 4); //mogelijk erg font-specifiek
		//fm.ascent = size + 1;
		//fm.height = fm.descent + fm.ascent;
		//fm.fontSize = size;
		
		descent = Math.round(size / 4);
		ascent = size + 1;
		height = descent + ascent;
		fontSize = size;
		
		/*
		ascent = size + 2;
		descent = Math.round(ascent / 12);
		ascent = ascent - descent;
		leading = 2;
		height = ascent + descent;
		fontSize = height - leading;
		*/
	}

	public void setSmallText(boolean smalltext)
	{
		if (smalltext != this.smalltext)
		{
			if (smalltext)
				this.setSizes((int)Math.round((double) this.fontSize * 2/3));
			else
				this.setSizes(this.fontSize * 3/2);
		}
		this.smalltext = smalltext;
	}

	public void setSizeRelativeTo(int relativeSize)
	{
		int size = (int) (this.fontSize * relativeSize / 100);
		this.ascent = size + 1;
		this.descent = Math.round(size / 4);
		this.height = ascent + descent;
		this.fontSize = size;
		
		
		/*
		int size = (int) (this.fontSize * relativeSize / 100) + 4;
		
		this.ascent = size - 2;
		this.descent = Math.round(this.ascent / 12);
		this.ascent = this.ascent - this.descent;
		this.leading = 2;
		this.height = this.ascent + this.descent;
		this.fontSize = this.height - this.leading;
		*/
		
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (bold ? 1231 : 1237);
		result = prime * result + ((font == null) ? 0 : font.hashCode());
		result = prime * result + fontSize;
		result = prime * result + (italic ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FormuleFont))
			return false;
		FormuleFont other = (FormuleFont) obj;
		if (bold != other.bold)
			return false;
		if (font == null) {
			if (other.font != null)
				return false;
		} else if (!font.equals(other.font))
			return false;
		if (fontSize != other.fontSize)
			return false;
		if (italic != other.italic)
			return false;
		return true;
	}

}
