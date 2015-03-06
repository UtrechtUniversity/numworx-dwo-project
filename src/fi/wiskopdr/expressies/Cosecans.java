package fi.wiskopdr.expressies;

import java.awt.*;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class Cosecans extends Expressie  
{	
	
	public Cosecans(Expressie e1 )
	{	kind1 = e1;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}
	
	public double geefWaarde()
	{	if(hoekGraden)return 1.0/Math.sin(kind1.geefWaarde()/180.0*Math.PI);
		return 1.0/Math.sin(kind1.geefWaarde());
	}
	
	public double geefWaarde(double subst)
	{	if(hoekGraden)return 1.0/Math.sin(kind1.geefWaarde(subst)/180.0*Math.PI);
		return 1.0/Math.sin(kind1.geefWaarde(subst));
	}
	
	public Complex geefWaardeComplex()
	{	Complex c1 = kind1.geefWaardeComplex();
		if(c1==null) return null;
		return Complex.csc(c1);
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	return Complex.csc(kind1.geefWaardeComplex(subst));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	if(hoekGraden) return 1.0/Math.sin(kind1.geefWaarde(subst,vars)/180.0*Math.PI);
		return 1.0/Math.sin(kind1.geefWaarde(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Cosecans(kind1.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new Cosecans(kind1.substitueer(subst,var));
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
	{	return "csc" + "$h" + kind1.toString() + "@";
	}
	
	public String toStringStrikt()
	{	return "csc" + "$h" + kind1.toStringStrikt() + "@";
	}
    
    public Object visit(AbstractConverter converter) {
    	return converter.cosecans(kind1.visit(converter));  			
    }
}
