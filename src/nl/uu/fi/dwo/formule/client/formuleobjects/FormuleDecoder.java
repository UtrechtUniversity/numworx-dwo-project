package nl.uu.fi.dwo.formule.client.formuleobjects;

import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Breukvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Haakjesvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.IntegraalVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Machtvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.NdeWortelVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.WortelVak;

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
		/*
		if(ch1=='o')
		{	return new OptelVak(formuleVak);
		}
		else if(ch1=='a')
		{	return new AftrekVak(formuleVak);
		}
		else if(ch1=='v')
		{	return new VermenigvuldigingVak(formuleVak);
		}
		else */if (ch1 == 'b')
		{
			return new Breukvak(holder);
			//bv.vulVak(s.substring(2, eind));
			//regel.addElement(bv);
			//insert(bv);
			//s = s.substring(eind);
		}/*
			else if(ch1=='p')
			{	return new PowerVak(formuleVak);
			}*/
		else if (ch1 == 'w')
		{
			return new WortelVak(holder);
		}
		else if (ch1 == 'W')
		{
			return new NdeWortelVak(holder);
		}
		/*
			else if(ch1=='L')
			{	return new NdeLogVak(formuleVak);
			}
			else if(ch1=='d')
			{	return new DiffVak(formuleVak);
			}
			else if(ch1=='P')
			{	return new PrimitieveVak(formuleVak);
			}
			else if(ch1=='T')
			{	return new LimietVak(formuleVak);
			}
			else if(ch1=='S')
			{   return new SigmaVak(formuleVak);
			}
			*/
		else if (ch1 == 'i')
		{
			return new IntegraalVak(holder);
		}
		/*
			else if(ch1=='q')
			{	return new PrvVak(formuleVak);
			
			}*/
		else if (ch1 == 'm')
		{
			Machtvak mv = new Machtvak(holder);
			return mv;
		}/*
			else if(ch1=='s')
			{   SubscriptVak sv = new SubscriptVak(formuleVak);
			   insert(sv);
			   sv.vulVak(s.substring(2,eind));
			   s = s.substring(eind);
			}*/
		else if (ch1 == 'h')
		{
			return new Haakjesvak(holder);
		}/*
			else if(ch1=='r')
			{	AbsVak av = new AbsVak(formuleVak);
			av.setFGColor(fgColor);
			av.vulVak(s.substring(2,eind));
			insert(av);
			s = s.substring(eind);
			}
			else if(ch1=='c')
			{   ConjugVak av = new ConjugVak(formuleVak);
			  av.vulVak(s.substring(2,eind));
			  insert(av);
			  s = s.substring(eind);
			}
			else if(ch1=='y')
			{	BinVak iv = new BinVak(formuleVak);
			iv.vulVak(s.substring(2,eind));
			insert(iv);
			s = s.substring(eind);
			}
			*/
		return null;
	}
}
