package fi.wiskopdr;

import java.util.*;

import fi.wiskopdr.expressies.*;
import fi.wiskopdr.FormuleParser;

public class AntwoordChecker 
{
	private static int HERLEIDING_GEEN = 0;
	private static int HERLEIDING_VEELTERM_ZH = 1;
	private static int HERLEIDING_1_MACHT = 2;
	private static int HERLEIDING_MACHT_Z_NEG_BREUK_EXP = 3;
	private static int HERLEIDING_1_BREUK = 4;
	private static int HERLEIDING_1_LOG = 5;
	private static int HERLEIDING_GEMAAKT_VAN_LOGX = 6;
	
	private static boolean isHerleidingZH(Expressie gegevenExp, Expressie gevrExp)
	{	Expressie gevrHerlExpressie = Algebra.herleid(Algebra.verwijderHaakjes(gevrExp));
		boolean herleiding = false;
		String s = gegevenExp.toString();
		boolean haakjes = s.indexOf("$h")>-1;
		herleiding = !haakjes 
					&& Algebra.geefAantalMachten(gegevenExp)==Algebra.geefAantalMachten(gevrHerlExpressie) 
					&& Algebra.geefTermen(gegevenExp, new Vector()).size()==Algebra.geefTermen(gevrHerlExpressie, new Vector()).size() 
					&& Algebra.geefAantalFactorenTermen(gegevenExp)<=Algebra.geefAantalBreukPlusGetal(gevrHerlExpressie) + Algebra.geefAantalFactorenTermen(gevrHerlExpressie);
		
		Vector v = Algebra.geefDelingen(gegevenExp,new Vector());
			for(int i=0 ; i<v.size(); i++)
		    {	Expressie teller = ((Expressie)v.elementAt(i)).kind1;
		    	Expressie noemer = ((Expressie)v.elementAt(i)).kind2;
		    	if(!Double.isNaN(teller.geefWaarde()) && !Double.isNaN(noemer.geefWaarde()))
		    	{	if(!(teller instanceof BasisExpressie)  || !(noemer instanceof BasisExpressie))
		    		{	herleiding = false;
		    			break;
		    		}
		    	}
		    		
		    }
		
		return herleiding;
	}	
	

	
	private static boolean isMacht(Expressie gegevenExp, Expressie gevrExp)
	{	boolean herleiding = false;
		String s = gegevenExp.toString();
		boolean wortels = s.indexOf("$w")>-1 || s.indexOf("$W")>-1 ;
		herleiding = !wortels 
					&& (gegevenExp instanceof Macht
						|| gegevenExp instanceof Aftrekking && gegevenExp.kind1.geefWaarde()==0 && gegevenExp.kind2 instanceof Macht
						|| gegevenExp instanceof Vermenigvuldiging && !Double.isNaN(gegevenExp.kind1.geefWaarde()) && gegevenExp.kind2 instanceof Macht
						|| gegevenExp instanceof Aftrekking && gegevenExp.kind1.geefWaarde()==0 && gegevenExp.kind2 instanceof Vermenigvuldiging && !Double.isNaN(gegevenExp.kind2.kind1.geefWaarde())  && gegevenExp.kind2.kind2 instanceof Macht
						);
		return herleiding;
	}
	
	private static boolean isVorm(Expressie gegevenExp, Expressie gevrExp)
	{	boolean herleiding = Algebra.gelijkGevormd(gegevenExp,gevrExp);
		return herleiding;
	}
	
	
	private static boolean isLog(Expressie gegevenExp, Expressie gevrExp)
	{	boolean herleiding = false;
		herleiding = gegevenExp instanceof Log;
		return herleiding;
	}
	
	private static boolean isVormMetLogx(Expressie gegevenExp, Expressie gevrExp)
	{	boolean herleiding = true;
		Vector v = Algebra.geefLogExpressies(gegevenExp,new Vector());
		for(int i=0 ; i<v.size(); i++)
		{	Expressie e = (Expressie)v.elementAt(i);
			if(! (e.kind1 instanceof BasisExpressie))
			{	herleiding = false;
				break;
			}
		}
		return herleiding;
	}
	
	private static boolean isZonderGebrokenOfNegExp(Expressie gegevenExp, Expressie gevrExp)
	{	boolean herleiding = true;
		Vector v = Algebra.geefMachten(gegevenExp,new Vector());
		for(int i=0 ; i<v.size(); i++)
		{	Expressie e = (Expressie)v.elementAt(i);
		   	if(Algebra.geefDelingen(e.kind2, new Vector()).size()>0)
		   	{	herleiding = false;
		   		break;
		   	}
		   	else if(Algebra.geefAftrekkingen(e.kind2, new Vector()).size()>0)
		   	{	herleiding = false;
		   		break;
		   	}
		}
		if(Algebra.geefWortels(gegevenExp, new Vector()).size()>1)herleiding = false;
		return herleiding;
	}	
	
	private static boolean isBreukHerleiding(Expressie gegevenExp, Expressie gevrExp)
	{	boolean herleiding = true;
		Vector v = Algebra.geefTermen(gegevenExp,new Vector());
		if(v.size()>1)
		{	herleiding = false;
		}
		else
		{	boolean breuk = gegevenExp instanceof Deling 
							|| gegevenExp instanceof Aftrekking 
							&& gegevenExp.kind1.geefWaarde()==0
							&& gegevenExp.kind2 instanceof Deling;
			if(!breuk)herleiding = false;
		}
		return herleiding;
	}
	
	public static boolean checkGelijkwaardig(Expressie antwoord, Expressie juisteAntwoord, double precision)	
	{	if(precision==0)return checkGelijkwaardig(antwoord, juisteAntwoord);
		if(antwoord==null || juisteAntwoord==null) return false;
		Algebra.setAbsPrecision(precision);
		boolean	isGelijkwaardig = Algebra.isGelijkwaardig(antwoord,juisteAntwoord);
		Algebra.setDefaultAbsPrecision();
		return isGelijkwaardig;
	}
	
	public static boolean checkGelijkwaardig(Expressie antwoord, Expressie juisteAntwoord)	
	{	if(antwoord==null || juisteAntwoord==null) return false;
		boolean	isGelijkwaardig = Algebra.isGelijkwaardig(antwoord,juisteAntwoord);
		return isGelijkwaardig;
	}
	
	public static boolean checkHerleiding(Expressie antwoord, Expressie juisteAntwoord, int soortHerleiding)	
	{	if(antwoord==null || juisteAntwoord==null) return false;		
		boolean isHerleid = false;
		
		if(soortHerleiding==HERLEIDING_VEELTERM_ZH) isHerleid = isHerleidingZH(antwoord,juisteAntwoord);
		else if(soortHerleiding==HERLEIDING_1_MACHT) isHerleid = isMacht(antwoord,juisteAntwoord);
		else if(soortHerleiding==HERLEIDING_MACHT_Z_NEG_BREUK_EXP) isHerleid = isZonderGebrokenOfNegExp(antwoord,juisteAntwoord);
		else if(soortHerleiding==HERLEIDING_1_BREUK) isHerleid = isBreukHerleiding(antwoord,juisteAntwoord);
		else if(soortHerleiding==HERLEIDING_1_LOG) isHerleid = isLog(antwoord,juisteAntwoord);
		else if(soortHerleiding==HERLEIDING_GEMAAKT_VAN_LOGX) isHerleid = isVormMetLogx(antwoord,juisteAntwoord);
		else if(soortHerleiding==HERLEIDING_GEEN) isHerleid = isVorm(antwoord,juisteAntwoord);
		
		return isHerleid;
	}	
	
	public static boolean checkExact(Expressie antwoord, Expressie juisteAntwoord)	
	{	if(antwoord==null || juisteAntwoord==null) return false;
		//boolean isExact = antwoord.toString().equals(juisteAntwoord.toString());
		boolean isExact = Algebra.zijnGelijk(antwoord,juisteAntwoord);
		return isExact;
	}
	
	public static boolean checkSignificant(Expressie antwoord, Expressie juisteAntwoord)	
	{	if(antwoord==null || juisteAntwoord==null) return false;
		//boolean isExact = antwoord.toString().equals(juisteAntwoord.toString());
		
		boolean  aantalSignificantGelijk = Algebra.aantalSignificantGelijk(antwoord,juisteAntwoord);
		boolean	isGelijkwaardig = Algebra.isGelijkwaardig(antwoord,juisteAntwoord);
		return isGelijkwaardig && aantalSignificantGelijk;
	}
	
	// ondervangt het probleem dat er geen onderscheid gemaakt kan worden tussen 1 1/2 en 1+1/2
	public static boolean checkExactBreukPlusGetal(String antwoord, Expressie juisteAntwoord)	
	{	if(antwoord==null || juisteAntwoord==null) return false;
		boolean zonderPlus = antwoord.indexOf('+')<0;
		//System.out.println(antwoord);
		boolean isExact = Algebra.zijnGelijk(FormuleParser.geefExpressie(antwoord),juisteAntwoord);
		return isExact && zonderPlus;
	}
}
