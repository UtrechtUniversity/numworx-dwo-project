package fi.wiskopdr.expressies;

import java.awt.*;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class Tangens extends Expressie  
{	
	
	public Tangens(Expressie e1 )
	{	kind1 = e1;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}
	
	public Expressie geefDiff(BasisExpressie basisExp)
	{	if(kind1!=null)
		{	return new Deling(kind1.geefDiff(basisExp),new Macht(new Cosinus(kind1),new BasisExpressie(2)));
		}
		return null;	
	}
	
	public double geefWaarde()
	{	if(hoekGraden)return Math.tan(kind1.geefWaarde()/180.0*Math.PI);
		return Math.tan(kind1.geefWaarde());
	}
	
	public double geefWaarde(double subst)
	{	if(hoekGraden)return Math.tan(kind1.geefWaarde(subst)/180.0*Math.PI);
		return Math.tan(kind1.geefWaarde(subst));
	}
	
	public Complex geefWaardeComplex()
	{	Complex c1 = kind1.geefWaardeComplex();
		if(c1==null) return null;
		return Complex.tan(c1);	
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	return Complex.tan(kind1.geefWaardeComplex(subst));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	if(hoekGraden)return Math.tan(kind1.geefWaarde(subst,vars)/180.0*Math.PI);
		return Math.tan(kind1.geefWaarde(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Tangens(kind1.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new Tangens(kind1.substitueer(subst,var));
	}
	
	public Expressie vervangDifferentialen(String var)
	{	return new Tangens(kind1.vervangDifferentialen(var));
	}
	
	public Expressie vervangDiffs(Expressie subst, String var)
	{
		return new Tangens(kind1.vervangDiffs(subst, var));
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
	{	return "tan" + "$h" + kind1.toString() + "@";
	}
	
	public String toStringStrikt()
	{	return "tan" + "$h" + kind1.toStringStrikt() + "@";
	}
    
    public Object visit(AbstractConverter converter) {
    	return converter.tangens(kind1.visit(converter));
    }
}
