package fi.wiskopdr.expressies;

import java.awt.*;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class Wortel extends Expressie  
{	
	
	public Wortel(Expressie e1 )
	{	kind1 = e1;
		isVeelterm = false;
		isProdukt = false;
		isBasis = true;
	}
	
	public Expressie geefDiff(BasisExpressie basisExp)
	{	if(kind1!=null)
		{	return new Deling(kind1.geefDiff(basisExp), new Vermenigvuldiging(new BasisExpressie(2),new Wortel(kind1)));
		}
		return null;	
	}
	
	public double geefWaarde()
	{	double d1 = kind1.geefWaarde();
		if(d1>=0)
		{	return Math.sqrt(d1);
		}
		else return Double.NaN;
	}
	
	public double geefWaarde(double subst)
	{	//return Math.sqrt(kind1.geefWaarde(subst));
		double d1 = kind1.geefWaarde(subst);
		if(d1>=0)
		{	return Math.sqrt(d1);
		}
		else return Double.NaN;
	}
	
	public Complex geefWaardeComplex()
	{	Complex c1 = kind1.geefWaardeComplex();
		if(c1==null) return null;
		return Complex.sqrt(c1);
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	return Complex.sqrt(kind1.geefWaardeComplex(subst));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	//return Math.sqrt(kind1.geefWaarde(subst,vars));
		double d1 = kind1.geefWaarde(subst,vars);
		if(d1>=0)
		{	return Math.sqrt(d1);
		}
		else return Double.NaN;
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Wortel(kind1.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new Wortel(kind1.substitueer(subst,var));
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst) && kind1.geefWaarde(subst)>=0;
	}
	
	public String geefVarNaam()
	{	String s1 = kind1.geefVarNaam();
		if(s1!=null)return s1;
		return null;
	}
	
	public String toString()
	{	return "$w" + kind1.toString() + "@";
	}
	
	public String toStringStrikt()
	{	return "$w" + kind1.toStringStrikt() + "@";
	}
    
    public Object visit(AbstractConverter converter) {
    	return converter.wortel(kind1.visit(converter));
    }
}
