package fi.wiskopdr.expressies.repr;

import java.math.BigInteger;

import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.Optelling;
import fi.wiskopdr.expressies.Vermenigvuldiging;

public abstract class AbstractConverter {

	public abstract Object abs(Object visit);

	/**
	 * @deprecated
	 */
	public abstract Object expressie(Expressie expressie);

	public Object basis(String basisString) {
		try {
			return new BigInteger(basisString); // if it looks like a duck?
		} catch(Exception e) {} // jammer dan
		return basisString;
	}

	public Object aftrekking(Object s1, Object s2) {
		if("0".equals(s1.toString())) s1="";
		if( s2 instanceof Veelterm)
			s2 = "(" + s2 + ")";
	    return new Veelterm ( s1 + "-" + s2 ) ;
	}

	public Object decroundstrict(Object kind1, Object kind2) {
		return decround(kind1, kind2); // normaal geen onderscheid
	}

	public Object deling(Object s1, Object s2) {
		return new Veelterm ("("+s1+")/("+s2+")");
	}
  	
	public Object diffpartial(Object kind1, Object kind2) {
		return diff(kind1, kind2); // normaal geen onderscheid.
	}

	public Object macht(Object s1, Object s2) {
        s1 = "(" + s1 + ")";
        return s1 + "^(" + s2 + ")";
	}
	
	public Object optelling(Object s1, Object s2) {
	    return new Veelterm ( s1 + "+" + s2 ) ;
	}
	
	public Object optelling(Optelling expr) {
		return optelling(expr.kind1.visit(this), expr.kind2.visit(this)); 
	}
	
	public Object vermenigvuldiging(Object s1, Object s2)
	{
		return new Veelterm ( "(" + s1 + ")*(" + s2 + ")" ) ;
	}
	
	public Object vermenigvuldiging(Vermenigvuldiging expr) {
		return vermenigvuldiging(expr.kind1.visit(this), expr.kind2.visit(this)); 
	}
	
	
	public abstract Object arccos(Object kind1);
	public abstract Object arcsin(Object kind1);
	public abstract Object arctan(Object kind1);
	public abstract Object bin(Object kind1, Object kind2);
	public abstract Object binomcdf(Object kind1, Object kind2, Object kind3);
	public abstract Object binompdf(Object kind1, Object kind2, Object kind3);
	public abstract Object conjug(Object kind1);
	public abstract Object cosecans(Object kind1);
	public abstract Object cosinus(Object kind1);
	public abstract Object cotangens(Object kind1);
	public abstract Object decround(Object kind1, Object kind2);
	public abstract Object diff(Object kind1, Object kind2);
	public abstract Object differentiaal(Object kind1);
	public abstract Object fac(Object kind1);
	public abstract Object gcd(Object kind1, Object kind2);
	public abstract Object integrate(Object kind1, Object kind2, Object kind3, Object kind4, String string);
	public abstract Object invNorm(Object kind1, Object kind2, Object kind3);
	public abstract Object limit(Object kind1, Object kind2, Object kind3, Object kind4);
	public abstract Object ln(Object kind1);
	public abstract Object log(Object kind1);
	public abstract Object max(Object kind1, Object kind2);
	public abstract Object min(Object kind1, Object kind2);
	public abstract Object ndelog(Object kind1, Object kind2);
	public abstract Object root(Object kind1, Object kind2);
	public abstract Object normalcdf(Object kind1, Object kind2, Object kind3, Object kind4);
	public abstract Object poissoncdf(Object kind1, Object kind2);
	public abstract Object poissonpdf(Object kind1, Object kind2);
	public abstract Object primitieve(Object kind1, Object kind2, String string);
	public abstract Object prv(Object kind1, Object kind2, Object kind3, Object kind4);
	public abstract Object secans(Object kind1);
	public abstract Object sigma(Object kind1, Object kind2, Object kind3, Object kind4);
	public abstract Object siground(Object kind1, Object kind2, Object kind3);
	public abstract Object sigroundstandard(Object kind1, Object kind2);
	public abstract Object sinus(Object kind1);
	public abstract Object tangens(Object kind1);
	public abstract Object wortel(Object kind1);

	public abstract Object aantalsign(Object visit);

	public Object vergelijking(Object visit, String vergelijkingsTeken, Object visit2) {
		return visit + vergelijkingsTeken + visit2;
	}

	// TODO trigsimp op de goede plaats en afmaken
	public Object vergelijking(Object visit, String teken1, Object visit2, String teken2, Object visit3) {
		return vergelijking(visit,teken1,visit2) + " and " + vergelijking(visit2,teken2,visit3);
	}
	
	public Object vergelijkingmeerv(Object[] objects) {
		if(objects.length == 1)
			return objects[0];
		String string = "or";
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < objects.length; i++) {
			builder.append("("); builder.append(objects[i]);
			builder.append(")"); builder.append(string);
		}
		builder.setLength(builder.length()-string.length());
		return builder;
	}

	public Object apply(Object s1, Object s2, Object s3, Object s4) {
		StringBuilder s = new StringBuilder(s1.toString());
		s.append("(");
		if( s2 != null) {
			s.append(s2);
			if( s3 != null) {
				s.append(",");
				s.append(s3);
				if(s4 != null) {
					s.append(",");
					s.append(s4);
				}
			}
		}
		s.append(')');
		return s;
	}
}
