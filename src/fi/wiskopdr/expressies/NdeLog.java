package fi.wiskopdr.expressies;

import java.awt.*;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class NdeLog extends Expressie  
{	
	
	public NdeLog(Expressie e1, Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}
	
	public Expressie geefDiff(BasisExpressie basisExp)
	{	if(kind1!=null && kind2!=null && kind2.isWaarde())
		{	return new Deling(kind1.geefDiff(basisExp),new Vermenigvuldiging(new Ln(new BasisExpressie(kind2.geefWaarde())),kind1));
		}
		return null;	
	}
	
	public double geefWaarde()
	{	return Math.log(kind1.geefWaarde())/Math.log(kind2.geefWaarde());
	}
	
	public Complex geefWaardeComplex()
	{	Complex c1 = kind1.geefWaardeComplex();
		if(c1==null) return null;
		return Complex.over(Complex.log(c1),Math.log(kind2.geefWaarde()));
	}
	
	public double geefWaarde(double subst)
	{	return Math.log(kind1.geefWaarde(subst))/Math.log(kind2.geefWaarde(subst));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return Math.log(kind1.geefWaarde(subst,vars))/Math.log(kind2.geefWaarde(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new NdeLog(kind1.substitueer(subst,var), kind2.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new NdeLog(kind1.substitueer(subst,var), kind2.substitueer(subst,var));
	}
	
	public Expressie vervangDifferentialen(String var)
	{	return new NdeLog(kind1.vervangDifferentialen(var), kind2.vervangDifferentialen(var));
	}
	
	public Expressie vervangDiffs(Expressie subst, String var)
	{
		return new NdeLog(kind1.vervangDiffs(subst, var), kind2.vervangDiffs(subst, var));
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst);
	}
	
	public String geefVarNaam()
	{	String s1 = kind1.geefVarNaam();
		String s2 = kind2.geefVarNaam();
		if(s1!=null && s2!=null && (s1.equals("") || s2.equals("")))return "";
		else if(s1!=null && s2!=null && !s1.equals(s2))return "";
		else if(s1!=null && s2!=null && s1.equals(s2))return s1;
		else if(s1!=null && s2==null)return s1;
		else if(s1==null && s2!=null)return s2;
		else return null;
	}
	
	public String toString()
	{	return "$L" + kind1.toString() + "$n" +kind2.toString() + "@@";
	}
	
	public String toStringStrikt()
	{	return "$L" + kind1.toStringStrikt() + "$n" +kind2.toStringStrikt() + "@@";
	}
    
    public Object visit(AbstractConverter converter) {
    	return converter.ndelog(kind1.visit(converter), kind2.visit(converter));
    }
}
