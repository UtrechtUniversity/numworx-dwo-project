package fi.writemathgwt.client;

/**
 * Covert string to formule objects
 * 
 * @author Danny Hendrix
 * 
 */
public class FormuleDecoder
{
	public FormuleDecoder()
	{
	}

	public static FormuleElement getElementFromCharacter(char ch1, FormuleElement holder)
	{
		if (ch1 == 'b')
		{
			return new Breukvak(holder);
		}
		else if (ch1 == 'w')
		{
			return new WortelVak(holder);
		}
		else if (ch1 == 'm')
		{
			Machtvak mv = new Machtvak(holder);
			return mv;
		}
		return null;
	}
}
