package fi.wiskopdr.expressies;

import java.awt.*;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class Abs extends Expressie  
{	
	
	public Abs(Expressie e1 )
	{	kind1 = e1;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}
	
	public Expressie geefDiff(BasisExpressie basisExp)
	{	if(kind1!=null)
		{	return new Vermenigvuldiging(kind1.geefDiff(basisExp), new Deling(kind1, new Abs(kind1)));
		}
		return null;	
	}
	
	public double geefWaarde()
	{	return Math.abs(kind1.geefWaarde());
	}
	
	public double geefWaarde(double subst)
	{	return Math.abs(kind1.geefWaarde(subst));
	}
	
	public Complex geefWaardeComplex()
	{	Complex c1 = kind1.geefWaardeComplex();
		if(c1==null) return null;
		return new Complex(Complex.abs(c1));
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	return new Complex(Complex.abs(kind1.geefWaardeComplex(subst)));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return Math.abs(kind1.geefWaarde(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Abs(kind1.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new Abs(kind1.substitueer(subst,var));
	}
		
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst);
	}
	
	public String geefVarNaam()
	{	String s1 = kind1.geefVarNaam();
		if(s1!=null)return s1;
		return null;
	}
	
	public String toString()
	{	return "$r" + kind1.toString() + "@";
	}
	
	public String toStringStrikt()
	{	return "$r" + kind1.toStringStrikt() + "@";
	}

	public Object visit(AbstractConverter converter) 
	{
		return converter.abs( kind1.visit(converter));
	}
    
}
