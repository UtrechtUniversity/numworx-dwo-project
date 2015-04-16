package fi.wiskopdr.expressies.repr;

import java.math.BigInteger;

import fi.wiskopdr.expressies.Expressie;

public class ContentMathML extends AbstractConverter {
	
	public static final ContentMathML INSTANCE = new ContentMathML();

	static final String WISKOPDR = "wiskopdr";
	
	private ContentMathML() {
	}

// official MathML functions
	private Object apply(String f, Object... args) {
		StringBuilder sb = new StringBuilder();
		sb.append("<apply><");
		sb.append(f);
		sb.append("/>");
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i]);
		}
		sb.append("</apply>");
		return sb;
	}
// wiskopdr/openmath functions
	private Object capply(String f, String cd, Object...args) {
		StringBuilder sb = new StringBuilder();
		sb.append("<apply><csymbol cd='").append(cd).append("' />");
		sb.append(f);
		sb.append("</csymbol>");
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i]);
		}
		sb.append("</apply>");
		return sb;
	}
		
	@Override
	public Object abs(Object visit) {
		return apply("abs", visit);
	}
	
	@Override
	public Object expressie(Expressie expressie) {
		return "<ctext>UNKNOWN</ctext>";
	}

	@Override
	public Object arccos(Object kind1) {
		return apply("arccos", kind1);
	}

	@Override
	public Object arcsin(Object kind1) {
		return apply("arcsin", kind1);
	}

	@Override
	public Object arctan(Object kind1) {
		return apply("arctan", kind1);
	}

	@Override
	public Object bin(Object kind1, Object kind2) {
		return capply("binomial", "combinat1", kind1, kind2);
	}

	
	@Override
	public Object binomcdf(Object kind1, Object kind2, Object kind3) {
		return capply("binomcdf", WISKOPDR, kind1, kind2, kind3);
	}

	@Override
	public Object binompdf(Object kind1, Object kind2, Object kind3) {
		return capply("binompdf", WISKOPDR, kind1, kind2, kind3);
	}

	@Override
	public Object conjug(Object kind1) {
		return apply("conjugate", kind1);
	}

	@Override
	public Object cosecans(Object kind1) {
		return apply("cosec", kind1);
	}

	@Override
	public Object cosinus(Object kind1) {
		return apply("cos", kind1);
	}

	@Override
	public Object cotangens(Object kind1) {
		return apply("cotan", kind1);
	}

	@Override
	public Object decround(Object kind1, Object kind2) {
		return capply("decround",WISKOPDR, kind1);
	}

	@Override
	public Object diff(Object kind1, Object kind2) {
		return apply("diff", "<bvar>", kind2, "</bvar>", kind1);
	}

	@Override
	public Object differentiaal(Object kind1) {
		return capply("d", WISKOPDR,kind1);
	}

	@Override
	public Object fac(Object kind1) {
		return apply("factorial", kind1);
	}

	@Override
	public Object gcd(Object kind1, Object kind2) {
		return apply("gcd", kind1, kind2);
	}

	@Override
	public Object integrate(Object kind1, Object kind2, Object kind3,
			Object kind4, String string) {
		return apply("int",
				"<bvar>", kind4 , "</bvar>",
				"<lowlimit>", kind2, "</lowlimit>",
				"<uplimit>", kind3, "</uplimit>",
				kind1);
	}

	@Override
	public Object invNorm(Object kind1, Object kind2, Object kind3) {
		return capply("invnorm", WISKOPDR,kind1, kind2, kind3);
	}

	@Override
	public Object limit(Object kind1, Object kind2, Object kind3, Object kind4) {
		String direction = kind4.toString();
// FIXME direction 1 en 2
		if("<cn>1</cn>".equals(direction))
		{ 
			
		} else if("<cn>2</cn>".equals(direction))
		{
			
		}
// het normale geval.		
		return apply("limit", "<bvar>", kind2, "</bvar>", "<lowlimit>", kind2, "</lowlimit>",  kind1);
	}

	@Override
	public Object ln(Object kind1) {
		return apply("ln", kind1);
	}

	@Override
	public Object log(Object kind1) {
		return apply("log", kind1, "<cn>10</cn>");
	}

	@Override
	public Object max(Object kind1, Object kind2) {
		return apply("max", kind1, kind2);
	}

	@Override
	public Object min(Object kind1, Object kind2) {
		return apply("min", kind1, kind2);
	}

	@Override
	public Object ndelog(Object kind1, Object kind2) {
		return apply("log", kind1, kind2);
	}

	@Override
	public Object root(Object kind1, Object kind2) {
		return apply("root", "<degree>" , kind2, "</degree>" , kind1);
	}

	@Override
	public Object normalcdf(Object kind1, Object kind2, Object kind3,
			Object kind4) {
		return capply("normalcdf", WISKOPDR,kind1, kind2, kind3, kind4);
	}

	@Override
	public Object poissoncdf(Object kind1, Object kind2) {
		return capply("poissoncdf", WISKOPDR,kind1, kind2);
	}

	@Override
	public Object poissonpdf(Object kind1, Object kind2) {
		return capply("poissonpdf", WISKOPDR,kind1, kind2);
	}

	@Override
	public Object primitieve(Object kind1, Object kind2, String string) {
		return apply("int", "<bvar>", kind2, "</bvar>", kind1);
	}

	@Override
	public Object prv(Object kind1, Object kind2, Object kind3, Object kind4) {
		return capply("prv", WISKOPDR,kind1, kind2, kind3, kind4);
	}

	@Override
	public Object secans(Object kind1) {
		return apply("sec", kind1);
	}

	@Override
	public Object sigma(Object kind1, Object kind2, Object kind3, Object kind4) {
		return apply("sum", "<bvar>", kind2, "</bvar>", "<lowlimit>", kind3, "</lowlimit>", "<uplimit>", kind4, "</uplimit>", kind1);
	}

	@Override
	public Object siground(Object kind1, Object kind2, Object kind3) {
		return capply("round", WISKOPDR,kind1, kind2, kind3);
	}

	@Override
	public Object sigroundstandard(Object kind1, Object kind2) {
		return capply("roundstandard", WISKOPDR,kind1, kind2);
	}

	@Override
	public Object sinus(Object kind1) {
		return apply("sin", kind1);
	}

	@Override
	public Object tangens(Object kind1) {
		return apply("tan", kind1);
	}

	@Override
	public Object wortel(Object kind1) {
		return apply("root", kind1);
	}

	@Override
	public Object aantalsign(Object kind1) {
		return capply("aantalsign", WISKOPDR, kind1);
	}

	@Override
	public Object basis(String basisString) {
		if("-\u221e".equals(basisString))
			return "<apply><minus/><infinity/></apply>";
		if("\u221e".equals(basisString))
			return "<infinity/>";
		if("\u03c0".equals(basisString)) 
			return "<pi/>";
		if("e".equals(basisString))
			return "<e/>";
		try { 
			Double.parseDouble(basisString);
			return "<cn>"+basisString+"<cn>"; // a number
		} catch(Exception _) {
			return "<ci>"+basisString+"</ci>"; // a variable
		}
	}

	@Override
	public Object aftrekking(Object s1, Object s2) {
		return apply("minus", s1, s2);
	}

	@Override
	public Object deling(Object s1, Object s2) {
		return apply("divide", s1, s2);
	}

	@Override
	public Object macht(Object s1, Object s2) {
		return apply("power", s1, s2);
	}

	@Override
	public Object optelling(Object s1, Object s2) {
		return apply("plus",s1, s2);
	}

	@Override
	public Object vermenigvuldiging(Object s1, Object s2) {
		return apply("times",s1, s2);
	}

	@Override
	public Object decroundstrict(Object kind1, Object kind2) {
		// TODO Auto-generated method stub
		return super.decroundstrict(kind1, kind2);
	}


	@Override
	public Object vergelijking(Object visit, String vergelijkingsTeken,
			Object visit2) {
		return apply(rel(vergelijkingsTeken), visit, visit2);
	}

	private String rel(String vergelijkingsTeken) {
		String t;
		switch ( vergelijkingsTeken.charAt(0) ) {
			case '\u2265': t = "geq"; break;
			case '\u2264': t = "leq"; break;
			case '<' : t = "lt"; break;
			case '>' : t = "gt"; break;
			case '≠' : t = "neq"; break;
			case '≈' : t = "approx"; break;
			default:   t  = "eq"; break;			
		}
		return t;
	}

	@Override
	public Object vergelijking(Object visit, String teken1, Object visit2,
			String teken2, Object visit3) {
		
		String rel1 = rel(teken1);
		String rel2 = rel(teken2);
		if(rel1 .equals( rel2)) {
			return apply(rel1, visit, visit2, visit3); // chain.
		}		
		return apply( "and", 
				apply(rel1, visit, visit2),
				apply(rel2, visit2, visit3));
	}

	@Override
	public Object vergelijkingmeerv(Object[] objects) {
		if(objects.length==1)
			return objects[0];
		return apply("or", objects);
	}
	
}
