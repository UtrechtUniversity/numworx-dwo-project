package fi.wiskopdr.expressies;

import java.awt.*;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class Secans extends Expressie  
{	
	
	public Secans(Expressie e1 )
	{	kind1 = e1;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}

	public double geefWaarde()
	{	if(hoekGraden)return 1.0/Math.cos(kind1.geefWaarde()/180.0*Math.PI);
		return 1.0/Math.cos(kind1.geefWaarde());
	}
	
	public Complex geefWaardeComplex()
	{	Complex c1 = kind1.geefWaardeComplex();
		if(c1==null) return null;
		return Complex.sec(c1);
	}
	
	public double geefWaarde(double subst)
	{	if(hoekGraden)return 1.0/Math.cos(kind1.geefWaarde(subst)/180.0*Math.PI);
		return 1.0/Math.cos(kind1.geefWaarde(subst));
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	return Complex.sec(kind1.geefWaardeComplex(subst));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	if(hoekGraden)return 1.0/Math.cos(kind1.geefWaarde(subst,vars)/180.0*Math.PI);
		return 1.0/Math.cos(kind1.geefWaarde(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Secans(kind1.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new Secans(kind1.substitueer(subst,var));
	}
	
	public Expressie vervangDifferentialen(String var)
	{	return new Secans(kind1.vervangDifferentialen(var));
	}
	
	public Expressie vervangDiffs(Expressie subst, String var)
	{
		return new Secans(kind1.vervangDiffs(subst, var));
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
	{	return "sec" + "$h" + kind1.toString() + "@";
	}
	
	public String toStringStrikt()
	{	return "sec" + "$h" + kind1.toStringStrikt() + "@";
	}
    
    public Object visit(AbstractConverter converter) {
    	return converter.secans(kind1.visit(converter));
    }
}
