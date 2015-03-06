package fi.wiskopdr.expressies;

import java.awt.*;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class Faculteit extends Expressie  
{	
	
	public Faculteit(Expressie e1 )
	{	kind1 = e1;
		isVeelterm = false;
		isProdukt = false;
		isBasis = true;
	}
	
	public double geefWaarde()
	{	long w = (int)Math.round(kind1.geefWaarde());
		double antw = w;
		for(long i=w-1 ; i>0 ; i--) antw = antw*i;
		return antw;
	}
	
	public double geefWaarde(double subst)
	{	long w = (int)Math.round(kind1.geefWaarde(subst));
		double antw = w;
		for(long i=w-1 ; i>0 ; i--) antw = antw*i;
		return antw;
	}
	
	public Complex geefWaardeComplex()
	{	long w = 0;
		if(kind1!=null && kind1.geefWaardeComplex()!=null)w = (int)Math.round(kind1.geefWaardeComplex().getReal());
		double antw = w;
		for(long i=w-1 ; i>0 ; i--) antw = antw*i;
		return new Complex(antw);
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	long w = 0;
		if(kind1!=null && kind1.geefWaardeComplex()!=null)w = (int)Math.round(kind1.geefWaardeComplex(subst).getReal());
		double antw = w;
		for(long i=w-1 ; i>0 ; i--) antw = antw*i;
		return new Complex(antw);
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	long w = (int)Math.round(kind1.geefWaarde(subst, vars));
		double antw = w;
		for(long i=w-1 ; i>0 ; i--) antw = antw*i;
		return antw;
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Faculteit(kind1.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new Faculteit(kind1.substitueer(subst,var));
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
	{	return kind1.toString() + "!";
	}
	
	public String toStringStrikt()
	{	return  kind1.toStringStrikt() + "!";
	}
    
    public Object visit(AbstractConverter converter ) {
    	return converter.fac( kind1.visit(converter));
    }
}
