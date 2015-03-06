package fi.wiskopdr.expressies;

import java.util.HashMap;

import com.google.gwt.i18n.client.NumberFormat;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class Expressie
{
	public Expressie kind1, kind2, kind3, kind4;
	public String operatorString;
	boolean isVeelterm;
	boolean isProdukt;
	boolean isBasis;

	//static DecimalFormatSymbols dfs;

	public static NumberFormat df, dfe, df3;
	//public static DecimalFormat df;
	//public static DecimalFormat dfe;
	//public static DecimalFormat df3;
	//public static FontMetrics fm;

	private static HashMap casEvalStrings = new HashMap();

	static boolean hoekGraden;

	public Expressie()
	{
		//TODO: test if this formatting conversion is correct
		//dfs = new DecimalFormatSymbols();
		//if(WiskOpdr.language.toString().equals("nl")) 
		//dfs.setDecimalSeparator(',');
		//else dfs.setDecimalSeparator('.');
		//if(WiskOpdr.language.toString().equals("nl")) 
		//dfs.setGroupingSeparator(' ');
		//else dfs.setGroupingSeparator(' ');

		df = NumberFormat.getFormat("0.##########");//new DecimalFormat("0.##########", dfs);
		dfe = NumberFormat.getFormat("0.##########E0");//new DecimalFormat("0.##########E0", dfs);
		df3 = NumberFormat.getFormat("0.###");//new DecimalFormat("0.###", dfs);
	}

	public static void zetHoekGraden(boolean b)
	{
		hoekGraden = b;
	}

	/*
		public void zetMaat(FontMetrics fm)
		{
		}

		/*
			public void teken(Graphics g, int x, int y)
			{
			}
		*/
	public double geefWaarde()
	{
		return Double.NaN;
	}

	public Complex geefWaardeComplex()
	{
		return null;
	}

	public double geefWaarde(double subst)
	{
		return Double.NaN;
	}

	public Complex geefWaardeComplex(Complex subst)
	{
		return null;
	}

	public double geefWaarde(double[] subst, String[] vars)
	{
		return Double.NaN;
	}

	public Complex geefWaardeComplex(Complex[] subst, String[] vars)
	{
		return null;
	}

	public Expressie substitueer(double subst, String var)
	{
		return null;
	}

	public Expressie substitueer(Expressie subst, String var)
	{
		return null;
	}

	public boolean isWaarde(double subst)
	{
		return true;
	}

	public String geefVarNaam()
	{
		return null;
	}

	public boolean isVar()
	{
		return this instanceof BasisExpressie;
	}

	public boolean isWaarde()
	{
		return !Double.isNaN(geefWaarde());
	}

	public String toString()
	{
		return null;
	}

	public String toStringCAS()
	{
		return null;
	}

	public String toStringStrikt()
	{
		return null;
	}
	//TODO: do we need Ideas  || CAS
	/*
		public static Expressie evalWithCAS(Expressie e)
		{
			return evalWithCAS(e.toStringCAS());
			//return evalWithIdeas(e.toStringStrikt());
		}
	/*
		private static Expressie evalWithIdeas(String evalCommand)
		{
			Expressie expr = (Expressie) casEvalStrings.get(evalCommand);
			if (expr != null)
				return expr;
			RuleIF result = WiskOpdr.ideas.interpret(evalCommand);
			if (result.isException())
				return null;
			expr = FormuleParser.geefExpressie("$f" + result.getExpr() + "@");
			if (expr != null)
				casEvalStrings.put(evalCommand, expr);
			return expr;
		}

		/**
		 * Bereken de (double) waarde van een Expressie via een CAS.
		 * 
		 * @param e
		 * @return waarde
		 */
	/*
	public static double geefWaardeViaIdeas(Expressie e)
	{
		RuleIF result = WiskOpdr.ideas.interpret(IdeasIF.NUMERIC, e.toStringStrikt());
		if (result.isException())
			return Double.NaN;
		return Double.parseDouble(result.getExpr());
	}

	/*
		public static Expressie evalWithCAS(String evalCommand)
		{
			Expressie e = null;
			String s = "";

			if (casEvalStrings.containsKey(evalCommand))
				s = (String) casEvalStrings.get(evalCommand);
			else
			{
				System.out.println(evalCommand);

				try
				{
					WiskOpdr.phrasebook.eval("ClearAll[x]");
					s = WiskOpdr.phrasebook.eval("InputForm[" + evalCommand + "]");
					//s = WiskOpdr.phrasebook.eval(evalCommand);

					System.out.println(s);
				}
				catch (Exception ex)
				{
				}
				casEvalStrings.put(evalCommand, s);
			}
			/*
			evalCommand = StringUtils.replaceStr(evalCommand,"+","%2B");
			evalCommand = StringUtils.replaceStr(evalCommand,"/","%2F");
			
			String s = "";
			try
			{   URLConnection con;
			    URL u = new URL("http://www.fi.uu.nl/servlet/mathshell/mathshell?input=InputForm[" + evalCommand + "]&native=on");
			    con = u.openConnection();
			    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			    if (in != null) 
			    {   s = "";
			        String tmp = "";
			        while ((tmp = in.readLine()) != null) {
			            s += tmp;
			        }
			        in.close();
			        System.out.println(s);
			        int index1 = s.indexOf("<pre>");
			        int index2 = s.indexOf("</pre>");
			        s = s.substring(index1+5, index2).trim();
			        System.out.println(s);
			        
			    }
			}
			catch(Exception ex)
			{}*/
	/*
			s = s.substring(0, s.length() - 1);
			s = s.replace('[', '(');
			s = s.replace(']', ')');
			s = s.replace("Pi", "\u03C0");
			s = s.replace("E", "e");
			s = s.replace("Log", "ln");
			s = s.replace("Sin", "sin");
			s = s.replace("Cos", "cos");
			s = s.replace("Tan", "tan");
			s = s.replace("Arc", "arc");
			s = s.replace("Sqrt", "sqrt");

			System.out.println("$f" + s + "@");
			e = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + s + "@")));
			return e;
		}
	*/
	/*
	public static VergelijkingMeerv solveWithCAS(String evalCommand, String arg)
	{
		VergelijkingMeerv v = null;
		String s = "";

		if (casEvalStrings.containsKey(evalCommand))
			s = (String) casEvalStrings.get(evalCommand);
		else
		{
			System.out.println(evalCommand);

			try
			{
				System.out.println(s);
				s = WiskOpdr.phrasebook.eval("InputForm[" + arg + "/." + "Solve[" + evalCommand + "," + arg + "]" + "]");
				System.out.println(s);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			//casEvalStrings.put(evalCommand, s);
		}

		String[] oplossingen = s.substring(1, s.length() - 2).split(",");
		//StringUtils.split(s.substring(1,s.length()-2), ",");

		for (int i = 0; i < oplossingen.length; i++)
		{
			s = oplossingen[i];
			s = s.replace('[', '(');
			s = s.replace(']', ')');
			s = s.replace("Pi", "\u03C0");
			s = s.replace("E", "e");
			s = s.replace("I", "i");
			s = s.replace("Log", "ln");
			s = s.replace("Sin", "sin");
			s = s.replace("Cos", "cos");
			s = s.replace("Tan", "tan");
			s = s.replace("Arc", "arc");
			s = s.replace("Sqrt", "sqrt");
			oplossingen[i] = s;
			//System.out.println(oplossingen[i]);
		}
		Expressie[] es = new Expressie[oplossingen.length];

		Vergelijking[] vs = new Vergelijking[oplossingen.length];
		for (int i = 0; i < es.length; i++)
		{
			es[i] = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + oplossingen[i].trim() + "@")));
			//System.out.println(oplossingen[i]);
			//System.out.println(es[i].toString());
			vs[i] = new Vergelijking(new BasisExpressie(arg), es[i]);
		}
		v = new VergelijkingMeerv(vs);
		return v;
	}
	*/

	public Object visit(AbstractConverter converter) {
		return converter.expressie(this);
	}
}
