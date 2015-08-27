package fi.wiskopdr.expressies;

import java.awt.*;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class ArcTangens extends Expressie  
{	
	
	public ArcTangens(Expressie e1 )
	{	kind1 = e1;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}
	
	public Expressie geefDiff(BasisExpressie basisExp)
	{	if(kind1!=null)
		{	return new Deling(kind1.geefDiff(basisExp), new Optelling(new BasisExpressie(1),new Macht(kind1,new BasisExpressie(2))));
		}
		return null;	
	}
	
	public double geefWaarde()
	{	if(hoekGraden) return 180.0/Math.PI*Math.atan(kind1.geefWaarde());
		return Math.atan(kind1.geefWaarde());
	}
	
	public Complex geefWaardeComplex()
	{	Complex c1 = kind1.geefWaardeComplex();
		if(c1==null) return null;
		return Complex.atan(c1);
	}
	
	public double geefWaarde(double subst)
	{	if(hoekGraden) return 180.0/Math.PI*Math.atan(kind1.geefWaarde(subst));
		return Math.atan(kind1.geefWaarde(subst));
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	return Complex.atan(kind1.geefWaardeComplex(subst));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	if(hoekGraden) return 180.0/Math.PI*Math.atan(kind1.geefWaarde(subst,vars));
		return Math.atan(kind1.geefWaarde(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new ArcTangens(kind1.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new ArcTangens(kind1.substitueer(subst,var));
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
	{	return "arctan" + "$h" + kind1.toString() + "@";
	}
	
	public String toStringStrikt()
	{	return "arctan" + "$h" + kind1.toStringStrikt() + "@";
	}
    
    public Object visit(AbstractConverter converter) 
	{
		return converter.arctan( kind1.visit(converter));
	}
}
