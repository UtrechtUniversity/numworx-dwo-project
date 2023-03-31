package fi.wiskopdr.expressies;

import java.util.ArrayList;
import java.util.Vector;

import fi.wiskopdr.RestartException;
import fi.wiskopdr.expressies.repr.AbstractConverter;
import fi.wiskopdr.text.Text;

public class VergelijkingMeerv
{
	Vergelijking[] vergelijkingen;
	
	//OngelijkheidObject[] ongelijkheidObjecten;

	public VergelijkingMeerv(Vergelijking[] v)
	{
		vergelijkingen = v;
	}

	public int geefAantal()
	{
		return vergelijkingen.length;
	}

	public boolean isGelijkMet(VergelijkingMeerv verg)
	{
		if (geefAantal() != verg.geefAantal())
			return false;
		else if (geefAantal() == 1)
		{
			Expressie e01 = vergelijkingen[0].kind1;
			Expressie e02 = vergelijkingen[0].kind2;
			Expressie f01 = verg.geefVergelijking(0).kind1;
			Expressie f02 = verg.geefVergelijking(0).kind2;
			boolean b0 = Algebra.isGelijkwaardig(e01, f01) && Algebra.isGelijkwaardig(e02, f02);

			return b0;
		}
		else if (geefAantal() == 2)
		{
			Expressie e01 = vergelijkingen[0].kind1;
			Expressie e02 = vergelijkingen[0].kind2;
			Expressie e11 = vergelijkingen[1].kind1;
			Expressie e12 = vergelijkingen[1].kind2;
			Expressie f01 = verg.geefVergelijking(0).kind1;
			Expressie f02 = verg.geefVergelijking(0).kind2;
			Expressie f11 = verg.geefVergelijking(1).kind1;
			Expressie f12 = verg.geefVergelijking(1).kind2;
			boolean b0 = Algebra.isGelijkwaardig(e01, f01) && Algebra.isGelijkwaardig(e02, f02);
			boolean b1 = Algebra.isGelijkwaardig(e11, f11) && Algebra.isGelijkwaardig(e12, f12);
			boolean b2 = Algebra.isGelijkwaardig(e01, f01) && Algebra.isGelijkwaardig(e12, f12);
			boolean b3 = Algebra.isGelijkwaardig(e11, f11) && Algebra.isGelijkwaardig(e02, f02);

			return b0 && b1 || b2 && b3;
		}
		return false;

	}

	public VergelijkingMeerv bewerkVergelijking(String operator, Expressie en)
	{
		return bewerkVergelijking(operator, en, -1);
	}

	public VergelijkingMeerv bewerkVergelijking(String operator, Expressie en, int nr)
	{
		Vergelijking[] vergelijkingenNieuw = new Vergelijking[vergelijkingen.length];

		if (vergelijkingen.length == 1 && operator.equals("wortel"))
		{
			Expressie e1 = vergelijkingen[0].kind1;
			Expressie e2 = vergelijkingen[0].kind2;
			Expressie[] we = Algebra.geefWortels(e1, e2);
			if (we != null && we[2] == null)
			{
				vergelijkingenNieuw = new Vergelijking[1];
				e1 = we[0];
				e2 = we[1];
				vergelijkingenNieuw[0] = new Vergelijking(e1, e2);
			}
			else if (we != null)
			{
				vergelijkingenNieuw = new Vergelijking[2];
				e1 = we[0];
				e2 = we[1];
				Expressie e3 = we[0];
				Expressie e4 = we[2];
				vergelijkingenNieuw[0] = new Vergelijking(e1, e2);
				vergelijkingenNieuw[1] = new Vergelijking(e3, e4);
			}
		}
		else if (vergelijkingen.length == 1 && operator.equals("splits"))
		{	Expressie e1 = vergelijkingen[0].kind1;
			Expressie e2 = vergelijkingen[0].kind2;
			Expressie[] we = Algebra.geefSplitsing(e1, e2);
			if (we != null && we[1] != null)
			{	vergelijkingenNieuw = new Vergelijking[2];
				e1 = we[0];
				e2 = new BasisExpressie(0);
				Expressie e3 = we[1];
				Expressie e4 = new BasisExpressie(0);
				vergelijkingenNieuw[0] = new Vergelijking(e1, e2);
				vergelijkingenNieuw[1] = new Vergelijking(e3, e4);
			}
			else if (we != null)
			{
				vergelijkingenNieuw = new Vergelijking[1];
				e1 = we[0];
				e2 = new BasisExpressie(0);
				vergelijkingenNieuw[0] = new Vergelijking(e1, e2);
			}
			else
				vergelijkingenNieuw = vergelijkingen;

		}

		else
			for (int j = 0; j < vergelijkingen.length; j++)
			{
				if (nr == -1 || nr == j)
					vergelijkingenNieuw[j] = vergelijkingen[j].bewerkVergelijking(operator, en);
				else
					vergelijkingenNieuw[j] = vergelijkingen[j];
			}
		return new VergelijkingMeerv(vergelijkingenNieuw);
	}

	public boolean isOngelijkheid()
	{
		for (int i = 0; i < vergelijkingen.length; i++)
		{
			if (vergelijkingen[i].isOngelijkheid())
				return true;
		}
		return false;
	}

	public boolean isAfronding()
	{
		for (int i = 0; i < vergelijkingen.length; i++)
		{
			if (vergelijkingen[i].isAfronding())
				return true;
		}
		return false;
	}

	public Vergelijking geefVergelijking(int nr)
	{
		if (nr >= vergelijkingen.length)
			return null;
		return vergelijkingen[nr];
	}

	public String[] geefVergTekens()
	{
		String[] vergTekens = new String[vergelijkingen.length];
		for (int i = 0; i < vergelijkingen.length; i++)
		{
			vergTekens[i] = vergelijkingen[i].geefVergTeken();
		}
		return vergTekens;
	}

	public boolean isOplossing(double subst)
	{
		String[] varNamen = geefVarNamen();
		if (varNamen.length > 1)
			return false;
		boolean isOplossing = false;
		for (int j = 0; j < vergelijkingen.length; j++)
		{
			if (!isOplossing)
				isOplossing = vergelijkingen[j].isOplossing(subst);
		}
		return isOplossing;
	}
	
	public boolean isOplossing() {
		if (!geefVarN().isEmpty()) return false; // Geen substitutie, dus x=x is false!
		boolean isOplossing = false;
		for(int j = 0; j < vergelijkingen.length && !isOplossing; j++) {
			isOplossing = vergelijkingen[j].isOplossing();
		}
		return isOplossing;
	}
	

	public boolean isOplossing(double subst, String vergTeken)
	{
		String[] varNamen = geefVarNamen();
		if (varNamen.length > 1)
			return false;
		boolean isOplossing = false;
		for (int j = 0; j < vergelijkingen.length; j++)
		{
			if (!isOplossing)
				isOplossing = vergelijkingen[j].isOplossing(subst, vergTeken);
		}
		return isOplossing;
	}

	public boolean isOplossing(Expressie subst, String var) throws RestartException
	{
		String[] varNamen = geefVarNamen();
		boolean isOplossing = false;
		for (int j = 0; j < vergelijkingen.length; j++)
		{
			if (!isOplossing)
				isOplossing = vergelijkingen[j].isOplossing(subst, var);
		}
		return isOplossing;
	}

	public boolean isOplossing(Expressie subst, String var, String vergTeken) throws RestartException
	{
		String[] varNamen = geefVarNamen();
		boolean isOplossing = false;
		for (int j = 0; j < vergelijkingen.length; j++)
		{
			if (!isOplossing)
				isOplossing = vergelijkingen[j].isOplossing(subst, var, vergTeken);
		}
		return isOplossing;
	}

	public boolean isEindOplossingExact(Expressie subst, String var, String vergTeken) throws RestartException
	{
		String[] varNamen = geefVarNamen();
		boolean isOplossing = false;
		for (int j = 0; j < vergelijkingen.length; j++)
		{
			if (!isOplossing)
			{
				isOplossing = vergelijkingen[j].isOplossing(subst, var, vergTeken);
				if (isOplossing)
				{
					boolean exact = vergelijkingen[j].isEindOplossingExact(subst, var);
					if (!exact)
						return false;
				}
			}
		}
		return isOplossing;
	}

	public boolean isEindOplossing(Expressie subst, String var, String vergTeken) throws RestartException
	{
		for(int j = 0; j < vergelijkingen.length; j++)
		{
			if(vergelijkingen[j].isOplossing(subst, var, vergTeken))
			{	if(vergelijkingen[j].isEindOplossing(var))
					return true;
			}
			
		}
		return false;
	}
	
	public boolean bevatOplossing(Expressie[] subst, String var, String vergTekens) throws RestartException
	{	for(int i=0 ; i<vergelijkingen.length ; i++)
		{	if(vergelijkingen[i].bevatOplossingP(subst,var, vergTekens))return true;
		}
		return false;
	}
	
	public boolean isEindOplossingExact(Expressie[] subst, String var, String vergTeken) throws RestartException
	{
		String[] varNamen = geefVarNamen();
		boolean isOplossing = false;
		for (int j = 0; j < vergelijkingen.length; j++)
		{
			if (!isOplossing)
			{
				isOplossing = vergelijkingen[j].bevatOplossingP(subst, var, vergTeken);
				if (isOplossing)
				{
					boolean exact = vergelijkingen[j].isEindOplossingExact(subst, var);
					if (!exact)
						return false;
				}
			}
		}
		return isOplossing;
	}
	
	public boolean isEindOplossingSignificant(Expressie[] subst, String var, String vergTeken) throws RestartException
	{	String[] varNamen = geefVarNamen();
		boolean isOplossing = false;
		for(int j=0 ; j<vergelijkingen.length ; j++)
		{	if(!isOplossing)
			{	isOplossing = vergelijkingen[j].bevatOplossingP(subst, var, vergTeken);
				if(isOplossing)
				{	boolean exact = vergelijkingen[j].isEindOplossingSignificant(subst, var);
					if(!exact)return false;
				}
			}	
		}
		return isOplossing;
	}

	/*public boolean bevatFouteOplossing(VergelijkingMeerv antw)
	{	boolean isOplossing = true;
		for(int j=0 ; j<vergelijkingen.length ; j++)
		{	if(vergelijkingen[j].isEindOplossing())
			{	isOplossing = antw.isOplossing(vergelijkingen[j].geefEindOplossing());
				if(!isOplossing)return true;
			}
		}
		return false;
	}*/

	public boolean bevatFouteOplossing(VergelijkingMeerv antw, String var) throws RestartException
	{
		for (int j = 0; j < vergelijkingen.length; j++)
		{
			if (!vergelijkingen[j].bevatOplossing(antw.geefEindOplossing(var), var))
			{
				return true;
			}
		}
		return false;
	}

	public boolean bevatFouteOplossing(VergelijkingMeerv antw, String var, String[] vergTekens) throws RestartException
	{
		for (int j = 0; j < vergelijkingen.length; j++)
		{
			if (!vergelijkingen[j].bevatOplossing(antw.geefEindOplossingen(var), var, vergTekens))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean bevatFouteStelselOplossing(Expressie[][] oplossingen, String[] vars) throws RestartException
	{
		for(int j = 0; j < vergelijkingen.length; j++)
		{
			if(!vergelijkingen[j].bevatStelselOplossing(oplossingen, vars))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isOplossing(double[] subst)
	{
		String[] varNamen = geefVarNamen();
		if (varNamen.length > 1)
			return false;
		for (int i = 0; i < subst.length; i++)
		{
			if (!isOplossing(subst[i]))
				return false;
		}
		return true;
	}

	public boolean isOplossing(double[] subst, String[] vergTekens)
	{
		String[] varNamen = geefVarNamen();
		if (varNamen.length > 1)
			return false;
		for (int i = 0; i < subst.length; i++)
		{
			if (!isOplossing(subst[i], vergTekens[i]))
				return false;
		}
		return true;
	}

	public boolean isOplossing(Expressie[] subst, String var) throws RestartException
	{
		for (int i = 0; i < subst.length; i++)
		{
			if (!isOplossing(subst[i], var))
				return false;
		}
		return true;
	}

	public boolean isOplossing(Expressie[] subst, String var, String[] vergTekens) throws RestartException
	{
		for (int i = 0; i < subst.length; i++)
		{
			if (!isOplossing(subst[i], var, vergTekens[i]))
				return false;
		}
		return true;
	}

	public boolean isOplossing(Expressie[][] subst, String var, String[] vergTekens) throws RestartException
	{
		for (int i = 0; i < subst.length; i++)
		{
			if (!bevatOplossing(subst[i], var, vergTekens[i]))
				return false;
		}
		return true;
	}
	
	public boolean isStelselOplossing(Expressie[][] subst, String[] vars) throws RestartException
	{
		for(int i = 0; i < subst.length; i++)
		{
			if(!isStelselOplossing(subst[i], vars))return false;
		}
		return true;
	}
	
	public boolean isStelselOplossing(Expressie[] subst, String[] vars) throws RestartException
	{
		boolean isOplossing = false;
		for(int j=0 ; j<vergelijkingen.length ; j++)
		{	if(!isOplossing)
			isOplossing = vergelijkingen[j].isOplossing(subst, vars);
		}
		return isOplossing;
	}

	public boolean isDeelOplossing(double[] subst)
	{
		String[] varNamen = geefVarNamen();
		if (varNamen.length > 1)
			return false;
		for (int i = 0; i < subst.length; i++)
		{
			if (isOplossing(subst[i]))
				return true;
		}
		return false;
	}

	public boolean isDeelOplossing(Expressie[] subst, String var) throws RestartException
	{
		for (int i = 0; i < subst.length; i++)
		{
			if (isOplossing(subst[i], var))
				return true;
		}
		return false;
	}

	public boolean isDeelOplossing(Expressie[] subst, String var, String[] vergTekens) throws RestartException
	{
		for (int i = 0; i < subst.length; i++)
		{
			if (isOplossing(subst[i], var, vergTekens[i]))
				return true;
		}
		return false;
	}

	public boolean isDeelOplossing(Expressie[][] subst, String var, String[] vergTekens) throws RestartException
	{
		for (int i = 0; i < subst.length; i++)
		{
			for (int j = 0; j < vergelijkingen.length; j++)
			{
				if (vergelijkingen[j].bevatOplossingP(subst[i], var, vergTekens[i]))
					return true;
			}

		}
		return false;
	}
	
	public boolean isStelselDeelOplossing(Expressie[][] subst, String[] vars) throws RestartException
	{
		for(int i = 0; i < subst.length; i++)
		{
			if(isStelselOplossing(subst[i], vars))return true;
		}
		return false;
	}
	
	public boolean checkDiscriminant(int discriminant, String varNaam)
	{
		for (int i = 0; i < vergelijkingen.length; i++)
		{
			boolean isGeen = vergelijkingen[i].checkDiscriminant(discriminant, varNaam);
			if (isGeen)
				return true;
		}
		return false;
	}

	public Vector geefVarN()
	{
		Vector v = new Vector();
		for (int i = 0; i < vergelijkingen.length; i++)
		{
			Vector vNieuw = vergelijkingen[i].geefVarN();

			int lengte = v.size();
			for (int j = 0; j < vNieuw.size(); j++)
			{
				boolean anders = true;
				for (int k = 0; k < lengte; k++)
				{
					if (((String) v.elementAt(k)).equals(((String) vNieuw.elementAt(j))))
					{
						anders = false;
					}
				}
				if (anders)
					v.addElement(vNieuw.elementAt(j));
			}
		}
		return v;
	}

	public String[] geefVarNamen()
	{
		Vector varn = geefVarN();
		String[] varNamen = new String[varn.size()];
		for (int i = 0; i < varn.size(); i++)
		{
			varNamen[i] = (String) varn.elementAt(i);
		}
		return varNamen;
	}

	public String toString()
	{
		String s = vergelijkingen[0].toString();
		for (int i = 1; i < vergelijkingen.length; i++)
		{
			s = s + "  " + Text.constants.ofLabel() + "  " + vergelijkingen[i].toString();
		}

		return s;
	}

	public String toStringStrikt()
	{
		String s = vergelijkingen[0].toStringStrikt();
		for (int i = 1; i < vergelijkingen.length; i++)
		{
			s = s + "  " + Text.constants.ofLabel() + "  " + vergelijkingen[i].toStringStrikt();
		}

		return s;
	}

	/*public boolean isEindOplossing()
	{	for(int i=0 ; i<vergelijkingen.length ; i++)
		{	if(!vergelijkingen[i].isEindOplossing())return false;
		}
		return true;
	}*/

	public boolean isEindOplossing(String[] vars)
	{
		for (int i = 0; i < vergelijkingen.length; i++)
		{
			boolean isEindOpl = false;
			for(int j = 0; j < vars.length; j++)
			{
				if(vergelijkingen[i].isEindOplossing(vars[j]))
				{	isEindOpl = true;
					break;
				}
			}
			if(!isEindOpl)
				return false;
		}
		return true;
	}
	
	public boolean isEindOplossing(String var)
	{
		for (int i = 0; i < vergelijkingen.length; i++)
		{
			if (!vergelijkingen[i].isEindOplossing(var))
				return false;
		}
		return true;
	}

	public boolean isEindOplossingExact(Expressie[][] subst, String var, String[] vergTekens) throws RestartException
	{
		for (int i = 0; i < subst.length; i++)
		{
			if (!isEindOplossingExact(subst[i], var, vergTekens[i]))
				return false;
		}
		return true;
	}
	
	public boolean isEindOplossingSignificant(Expressie[][] subst, String var,  String[] vergTekens) throws RestartException
	{	for(int i=0 ; i<subst.length ; i++)
		{	if(!isEindOplossingSignificant(subst[i],var, vergTekens[i]))return false;
		}
		return true;
	}

	public boolean isStelselEindOplossing(String var, String[] vars)
	{
		for(int i = 0; i < vergelijkingen.length; i++)
		{
			if(!vergelijkingen[i].isStelselEindOplossing(var, vars))return false;
		}
		return true;
	}
	/*
	public double[] geefEindOplossing()
	{	double[] oplossingen = new double[vergelijkingen.length];
		if(isEindOplossing())
		{	for(int i=0 ; i<vergelijkingen.length ; i++)
			{	oplossingen[i] = vergelijkingen[i].geefEindOplossing();
			}
		}
		return oplossingen;
	}*/

	public Expressie[] geefEindOplossing(String var)
	{
		Expressie[] oplossingen = new Expressie[vergelijkingen.length];
		if (isEindOplossing(var))
		{
			for (int i = 0; i < vergelijkingen.length; i++)
			{
				oplossingen[i] = vergelijkingen[i].geefEindOplossing(var);
			}
		}
		return oplossingen;
	}

	public Expressie[][] geefEindOplossingen(String var)
	{
		Expressie[][] oplossingen = new Expressie[vergelijkingen.length][];
		if (isEindOplossing(var))
		{
			for (int i = 0; i < vergelijkingen.length; i++)
			{
				oplossingen[i] = vergelijkingen[i].geefEindOplossingen(var);
			}
		}
		return oplossingen;
	}

	public String geefVergelijkingVar()
	{
		String var = vergelijkingen[0].geefVergelijkingVar();
		for (int i = 1; i < vergelijkingen.length; i++)
		{
			String varNieuw = vergelijkingen[i].geefVergelijkingVar();
			if (!var.equals(varNieuw))
				return null;
		}
		return var;
	}

	public VergelijkingMeerv substitueer(Expressie subst, String var)
	{
		Vergelijking[] vergelijkingenNieuw = new Vergelijking[vergelijkingen.length];
		for (int i = 0; i < vergelijkingen.length; i++)
		{
			vergelijkingenNieuw[i] = vergelijkingen[i].substitueer(subst, var);
		}
		return new VergelijkingMeerv(vergelijkingenNieuw);
	}
	
	public VergelijkingMeerv substitueerEindOplossing(Expressie subst, String var)
	{
		Vergelijking[] vergelijkingenNieuw = new Vergelijking[vergelijkingen.length];
		for(int i = 0; i < vergelijkingen.length; i++)
		{
			vergelijkingenNieuw[i] = vergelijkingen[i].substitueerEindOplossing(subst, var);
		}
		return new VergelijkingMeerv(vergelijkingenNieuw);
	}
	
	public boolean isWareBeweringNummeriek() {
		boolean isWaar = false;
		boolean[] delenJuist = new boolean[(geefAantal())];
		for (int k=0 ; k<delenJuist.length ; k++)
		{
			delenJuist[k] = false;
			if (geefVergelijking(k).geefVergTeken().equals(">") 
					|| geefVergelijking(k).geefVergTeken().equals("<")
					|| geefVergelijking(k).geefVergTeken().equals("\u2265") //groter dan of gelijk aan
					|| geefVergelijking(k).geefVergTeken().equals("\u2264")
					|| geefVergelijking(k).geefVergTeken().equals("~")) //kleiner dan of gelijk aan
			{	
				Expressie expL = geefVergelijking(k).geefExpLinks();
				Expressie expR = geefVergelijking(k).geefExpRechts();
				if (expL.isWaarde() && expR.isWaarde() && geefVergelijking(k).geefVergTeken().equals("<"))
					delenJuist[k] = expL.geefWaarde() < expR.geefWaarde()-0.000000001;
				else if (expL.isWaarde() && expR.isWaarde() && geefVergelijking(k).geefVergTeken().equals(">"))
					delenJuist[k] = expL.geefWaarde() > expR.geefWaarde()+0.000000001;
				else if (expL.isWaarde() && expR.isWaarde() && geefVergelijking(k).geefVergTeken().equals("\u2264"))
					delenJuist[k] = expL.geefWaarde() < expR.geefWaarde()+0.000000001;
				else if (expL.isWaarde() && expR.isWaarde() && geefVergelijking(k).geefVergTeken().equals("\u2265"))
					delenJuist[k] = expL.geefWaarde() > expR.geefWaarde()-0.000000001;
				else if (geefVergelijking(k).geefVergTeken().equals("~"))
				{	
					Expressie e1 = expR.kind2.kind1;
					Expressie e2 = expL;
					Expressie e3 = expR.kind2.kind2;
					
					if (e1.isWaarde() && e2.isWaarde() && e3.isWaarde())
					{
						if (Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 0)) //{"<","<"}
							delenJuist[k] = e1.geefWaarde() < e2.geefWaarde()-0.000000001 && e2.geefWaarde() < e3.geefWaarde()-0.000000001;
						else if (Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 1)) //{"<","\u2264"}
							delenJuist[k] = e1.geefWaarde() < e2.geefWaarde()-0.000000001 && e2.geefWaarde() < e3.geefWaarde()+0.000000001;
						else if (Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 2)) //{"\u2264","<"}
							delenJuist[k] = e1.geefWaarde() < e2.geefWaarde()+0.000000001 && e2.geefWaarde() < e3.geefWaarde()-0.000000001;
						else if (Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 3)) //{"\u2264","\u2264"}
							delenJuist[k] = e1.geefWaarde() < e2.geefWaarde()+0.000000001 && e2.geefWaarde() < e3.geefWaarde()+0.000000001;
						else if (Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 4)) //{">",">"}
							delenJuist[k] = e1.geefWaarde() > e2.geefWaarde()+0.000000001 && e2.geefWaarde() > e3.geefWaarde()+0.000000001;
						else if (Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 5)) //{"\u2265",">"}
							delenJuist[k] = e1.geefWaarde() > e2.geefWaarde()-0.000000001 && e2.geefWaarde() > e3.geefWaarde()+0.000000001;
						else if (Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 6)) //{">","\u2265"}
							delenJuist[k] = e1.geefWaarde() > e2.geefWaarde()+0.000000001 && e2.geefWaarde() > e3.geefWaarde()-0.000000001;
						else if (Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 7)) //{"\u2265","\u2265"}
							delenJuist[k] = e1.geefWaarde() > e2.geefWaarde()-0.000000001 && e2.geefWaarde() > e3.geefWaarde()-0.000000001;
					}
				}
    				
			} else
				try {
					delenJuist[k] = geefVergelijking(k).isOplossing(new BasisExpressie(1.212131415),"q");
				} catch (RestartException e) {
					delenJuist[k] = false; // eigenlijk "weet niet"
				}
			
			if (k==0)
				isWaar = delenJuist[k];
			else
				isWaar = isWaar || delenJuist[k];
		}
		return isWaar;
	}
	
	public VergelijkingMeerv vervangDifferentialen(String diffVar)
	{
		Vergelijking[] vergelijkingenNieuw = new Vergelijking[vergelijkingen.length];
		for(int i = 0; i < vergelijkingen.length; i++)
		{
			vergelijkingenNieuw[i] = vergelijkingen[i].vervangDifferentialen(diffVar);
		}
		return new VergelijkingMeerv(vergelijkingenNieuw);
	}
	
	public VergelijkingMeerv vervangDiffs(Expressie[][] substs, String var)
	{
		Vergelijking[] vergelijkingenNieuw = new Vergelijking[vergelijkingen.length];
		Expressie subst = substs[0][0];
		for(int i = 0; i < vergelijkingen.length; i++)
		{
			vergelijkingenNieuw[i] = vergelijkingen[i].vervangDiffs(subst, var);
		}
		return new VergelijkingMeerv(vergelijkingenNieuw);
	}

	public Object visit(AbstractConverter instance) {
		Object[] objects  = new Object[vergelijkingen.length];
		for (int i = 0; i < objects.length; i++) {
			objects[i] = vergelijkingen[i].visit(instance);
		}
		return instance.vergelijkingmeerv(objects);
	}

	/**
	 * Als matrixvergelijking en de matrix-kinderen eindoplossingen zijn, dan true.
	 * 
	 * @param var
	 * @return
	 */
	public boolean isMatrixEindOplossing(String var)
	{
		boolean isEind = false;

		for (int i = 0; i < vergelijkingen.length; i++)
		{
			if (Algebra.isMatrix(vergelijkingen[i].kind1) && Algebra.isMatrix(vergelijkingen[i].kind2))
			{
				// check voor alle kinderparen of isEindOplossing(var)
				ArrayList<ArrayList<Expressie>> kinderen1 = vergelijkingen[i].kind1.geefMatrix().geefKinderen();
				ArrayList<ArrayList<Expressie>> kinderen2 = vergelijkingen[i].kind2.geefMatrix().geefKinderen();
				
				if (kinderen1 != null && kinderen2 != null && !kinderen1.isEmpty() && !kinderen2.isEmpty()
					&& kinderen1.size() == kinderen2.size())
				{
					for (int r = 0; r < kinderen1.size(); r++) // rijen
					{
						if (isEind) // 1 is genoeg
							break;
						
						for (int j = 0; j < kinderen1.get(0).size(); j++) // kolommen
						{
							Vergelijking kindVerg = new Vergelijking(kinderen1.get(r).get(j), kinderen2.get(r).get(j));
							isEind = kindVerg.isEindOplossing(var);
							if (isEind) // 1 is genoeg
								break;
						}
					}
				}
				else
				{
					isEind = false;
					break;
				}
			}
			else
			{
				isEind = false;
				break;
			}
		}

		return isEind;
	}
	
	public boolean isMatrixVergelijking()
	{
		boolean isMatrixVgl = false;
		
		for (int i = 0; i < vergelijkingen.length; i++)
		{
			if (Algebra.isMatrix(vergelijkingen[i].kind1) && Algebra.isMatrix(vergelijkingen[i].kind2))
				isMatrixVgl = true;
		}
		
		return isMatrixVgl;
	}

	/**
	 * Als vectorvergelijking en de vector-kinderen eindoplossingen zijn, dan true.
	 * 
	 * @param var
	 * @return
	 */
	public boolean isVectorEindOplossing(String var)
	{
		boolean isEind = false;

		for (int i = 0; i < vergelijkingen.length; i++)
		{
			if (Algebra.isVector(vergelijkingen[i].kind1) && Algebra.isVector(vergelijkingen[i].kind2))
			{
				// check voor alle kinderparen of isEindOplossing(var)
				ArrayList<Expressie> kinderen1 = vergelijkingen[i].kind1.geefVector().geefKinderen();
				ArrayList<Expressie> kinderen2 = vergelijkingen[i].kind2.geefVector().geefKinderen();
				
				if (kinderen1 != null && kinderen2 != null && !kinderen1.isEmpty() && !kinderen2.isEmpty()
					&& kinderen1.size() == kinderen2.size())
				{
					for (int j = 0; j < kinderen1.size(); j++)
					{
						Vergelijking kindVerg = new Vergelijking(kinderen1.get(j), kinderen2.get(j));
						isEind = kindVerg.isEindOplossing(var);
						if (isEind) // 1 is genoeg
							break;
					}
				}
				else
				{
					isEind = false;
					break;
				}
			}
			else
			{
				isEind = false;
				break;
			}
		}

		return isEind;
	}

	public boolean isVectorVergelijking()
	{
		boolean isVectorVgl = false;
		
		for (int i = 0; i < vergelijkingen.length; i++)
		{
			if (Algebra.isVector(vergelijkingen[i].kind1) && Algebra.isVector(vergelijkingen[i].kind2))
				isVectorVgl = true;
		}
		
		return isVectorVgl;
	}

}
