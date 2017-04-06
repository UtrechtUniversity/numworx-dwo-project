package fi.wiskopdr.expressies;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.ideas.client.AbstractRule;
import nl.uu.fi.dwo.ideas.client.IdeasIF;
import nl.uu.fi.dwo.ideas.client.RuleCallback;
import nl.uu.fi.dwo.ideas.client.RuleIF;

import com.google.gwt.i18n.client.NumberFormat;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.RestartException.RestartHandler;
import fi.wiskopdr.WiskOpdr;
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

	private static final HashMap<String,Expressie> casEvalStrings = new HashMap<String,Expressie>();
	private static final HashMap<String, VergelijkingMeerv> casSolveStrings = new HashMap<String, VergelijkingMeerv>();
	private static final Expressie FAILED = new Expressie();
	private static final VergelijkingMeerv VGL_FAILED = new VergelijkingMeerv(null);

	static boolean hoekGraden;

	public Expressie()
	{
	}
	static {
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
	
	public Expressie geefDiff(BasisExpressie e)
	{
		return null;
	}
	
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
	
		public static Expressie evalWithCAS(Expressie e) throws RestartException
		{
			if(e instanceof Diff)
			{	Expressie diff = ((Diff)e).evalDiff();
				if(diff!=null)return diff;
			}
			return evalWithIdeas(e.toStringStrikt());
			/*
			if(isCasLocal())
				return evalWithReduce(e);
			if(isCasIdeas())
				return evalWithIdeas(e.toStringStrikt());
			return evalWithCAS(e.toStringCAS());
			*/
		}
	
		public static Expressie decideWithCas(VergelijkingMeerv e) throws RestartException
		{
			return decideWithIdeas(e.toStringStrikt());
		}

	/**
	 * Class Restart.
	 * 
	 * @author borku102
	 *
	 */
	private static class Restart implements RestartHandler, RuleCallback
	{

		private String message;
		private Runnable run;
		private String command;

		Restart(String command)
		{
			this.command = command;
		}

		Restart()
		{
			this(IdeasIF.EVAL);
		}

		@Override
		public void onFailure(Throwable caught)
		{
			Logger.getLogger("Expressie").log(Level.WARNING, message, caught);
			casEvalStrings.put(message, FAILED);
			run.run();
		}

		@Override
		public void onSuccess(RuleIF result)
		{
			Expressie expr;
			if (!result.isException())
			{
				expr = FormuleParser.geefExpressie("$f" + result.getExpr() + "@");
				Logger.getLogger("Expressie").info(message + ": " + expr);
			}
			else
			{
				expr = FAILED;
				Logger.getLogger("Expressie").log(Level.WARNING, message + ": " + result.getExpr());
			}
			casEvalStrings.put(message, expr);
			run.run();
		}

		@Override
		public void restart(String message, Runnable run)
		{
			this.run = run;
			this.message = message;
			WiskOpdr.ideas.interpret(command, message, this);
		}

	} // class Restart
	
	private static class Rule extends AbstractRule
	{
		private String expr;

		/**
		 * @param expr
		 */
		Rule(String expr)
		{
			super();
			this.expr = expr;
		}

		@Override
		public String getExpr()
		{
			return expr;
		}

	} // class Rule

	/**
	 * Class Restart.
	 * 
	 * @author borku102
	 *
	 */
	private static class SolveRestart extends Restart
	{
		SolveRestart(String command)
		{
			super.command = command;
		}

		@Override
		public void restart(String message, Runnable run)
		{
			super.run = run;
			super.message = message;
			
			// split message in varNaam en Verg
			String separator = "\u0000";
			String varNaam = message.substring(0, message.indexOf(separator));
			String verg = message.substring(message.indexOf(separator) + separator.length(), message.length());
			
			RuleIF[] args = new RuleIF[] { 
				new Rule(verg), new Rule(varNaam)
			};

			WiskOpdr.ideas.interpret(super.command, args, this);
		}

		@Override
		public void onFailure(Throwable caught)
		{
			Logger.getLogger("Vergelijking").log(Level.WARNING, super.message, caught);
			casSolveStrings.put(super.message, VGL_FAILED);
			super.run.run();
		}

		VergelijkingMeerv parseVergelijking(String expr)
		{
			String[] strings = expr.split("\u2228"); // logical 'or' sign (v)

			Vergelijking[] v = new Vergelijking[strings.length];

			for (int i = 0; i < v.length; i++)
			{
				expr = strings[i];
				int index = expr.indexOf("=") + 1;
				String var = index >= 2 ? expr.substring(0, index - 1) : "x"; // varNaam
				expr = expr.substring(index);
				Expressie ps = FormuleParser.geefExpressie("@f" + var.trim() + "@");
				Expressie es = FormuleParser.geefExpressie("$f" + expr.trim() + "@");
				v[i] = new Vergelijking(ps, es);
			}

			return new VergelijkingMeerv(v);
		}
		
		@Override
		public void onSuccess(RuleIF result)
		{
			VergelijkingMeerv expr;
			if (!result.isException())
			{
				expr = parseVergelijking(result.getExpr());
				Logger.getLogger("Vergelijking").info(super.message + ": " + expr);
			}
			else
			{
				expr = VGL_FAILED;
				Logger.getLogger("Expressie").log(Level.WARNING, super.message + ": " + result.getExpr());
			}
			casSolveStrings.put(super.message, expr);
			super.run.run();
		}
		
	} // class SolveRestart

		private static Logger logger = Logger.getLogger("Expressie");
		private static Expressie evalWithIdeas(String evalCommand) throws RestartException
		{
			Expressie expr = casEvalStrings.get(evalCommand);
			if (expr == FAILED)
				return null;
			if (expr != null)
			{
				return expr;
			}	
			logger.fine("throw restart " + evalCommand);
			throw new RestartException(evalCommand, new Restart());		
		}

		private static Expressie decideWithIdeas(String decideCommand) throws RestartException
		{
			Expressie expr = casEvalStrings.get(decideCommand);
			if (expr == FAILED) 
				return null;
			if (expr != null)
				return expr;
			logger.fine("throw restart " + decideCommand);
			throw new RestartException(decideCommand, new Restart(IdeasIF.DECIDE));		
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

	public static VergelijkingMeerv solveWithCas(VergelijkingMeerv e, String varNaam) throws RestartException
	{
		return solveWithIdeas(e.toStringStrikt(), varNaam);
	}

	/**
	 * 
	 * @param expr vergelijking.toStringStrikt()
	 * @param varNaam bijv. "x"
	 * @return
	 */
	public static VergelijkingMeerv solveWithIdeas(String verg, String varNaam) throws RestartException
	{
		// construeer het solveCommand
		String solveCommand = varNaam + "\u0000" + verg;
		
		VergelijkingMeerv vgl = casSolveStrings.get(solveCommand); // geen expressie, maar vergelijkingMeerv...
		
		if (vgl == VGL_FAILED) 
			return null;
		if (vgl != null)
			return vgl;
		
		logger.fine("throw restart " + solveCommand);
		
		throw new RestartException(solveCommand, new SolveRestart(IdeasIF.SOLVE));		
	}

	public Object visit(AbstractConverter converter) {
		return converter.expressie(this);
	}
	
	public Expressie vervangDifferentialen(String diffVar) {
		
		return null;
	}
	
	public Expressie vervangDiffs(Expressie subst, String var) {
		return null;
	}
}
