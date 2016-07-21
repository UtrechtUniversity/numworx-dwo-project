package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.stelselsvergelijkingen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleClientBundle;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FacetHelper;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithAnswer;
import fi.wiskopdr.AntwoordStelselVakChecker;
import fi.wiskopdr.AntwoordVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.Expressie;


public class StelselOplossingenVak //extends FormuleHolder //implements ActionListener, MouseListener, FormuleVakHouder
{
	//static Image GOEDKRUL,FOUTKRUIS, HALFKRUL;

	private int mode;

	//private boolean ingevuld;
	//private boolean nagekeken;

	private boolean correct;
	private boolean fout;

	private int score;
	private int scoreMax = 10;

	static int GOED = 1;
	static int FOUT = 0;
	static int HALF = 2;
	static int GEEN = 3;
	
	private FormuleFont font;
	private AntwoordStelselVakChecker avChecker = null;
	
	public static final FormuleClientBundle FORMULE_BUNDLE = GWT.create(FormuleClientBundle.class);
	
	
	//private String variabelenString;
	private String[] varNamen;
	private Expressie[][] juisteOplossingen;
	//private String[] juisteAntwoorden;
	private List<Map<String,Object>> answerModels;
	private boolean hasFeedback;
	
	private int breedte;
	private int hoogte;


	private boolean check;
	private boolean teltMee;

	

	private Image checkimg;

	private FormuleEditorWithAnswer editor;
	

	private boolean logOption;
	private String logID;

	private boolean[][] logObjectives;

	private int errorCount;
	private int attemptsCount;
	private Vector attempts;
	
	static boolean fontOvererving;
	private StelselAntwoordVak parent;
	
	private OpdrNavIF comRoot;
	
	public static void zetFontOverervingForm(boolean b)
	{	fontOvererving = b;
	}

	
	
	public StelselOplossingenVak(StelselAntwoordVak parent, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden)
	{
		
		ObjectMap map = JSONUtilities.wrapMap(h);
		this.breedte = map.getInt("breedte");
		this.hoogte = map.getInt("hoogte");
		font = FormuleFont.createFromFontSize(XMLView.getDefaultFontSize());
		
		this.parent = parent;
		attempts = new Vector();

		Map<String, Object> launchState = null;
		if(map.containsKey("interactiePanelLaunchState"))
		{	launchState = map.getMap("interactiePanelLaunchState");
			init(launchState, randomVarNamen, randomVarWaarden);
		}
		
		avChecker = new AntwoordStelselVakChecker((HashMap<String, Object>) launchState, randomVarNamen, randomVarWaarden);
		avChecker.zetJuisteOplossingen(juisteOplossingen);
		avChecker.zetVarNamen(varNamen);
		
		
		editor = new FormuleEditorWithAnswer(h, false, null, randomVarNamen, randomVarWaarden, avChecker);
		editor.setFormuleToolBijFocus(true);
		editor.setFont(font);
		editor.setCurrent(0, 0);
		editor.setParentStelselOplossingenVak(this);
		
		hoogte = editor.getHeight();
		editor.setCurrentElementRepaint();
	}

	
	public void requestFocus()
	{
		editor.requestFocus();
	}

	public void init(Map h, String[] randomVars, HashMap<String, Number> randomValues)
	{
		int puntenGelijkwaardig = 10;
		List<Map<String,Object>> answerModels = null;
		boolean hasFeedback = false;
		int scoreMax = 10;
		boolean check = true;
		boolean teltMee = true;
		boolean formuleToolBijFocus = false;
		boolean logOption = false;
		String logID = "";
		boolean boxMetRand = true;
		boolean[][] logObjectives = null;

		ObjectMap map = JSONUtilities.wrapMap(h);
		if (map.containsKey("scoreMax"))
			scoreMax = map.getInt("scoreMax");
		if (map.containsKey("answerModels"))
			answerModels = map.getMapList("answerModels");
		if (map.containsKey("hasFeedback"))
			hasFeedback = map.getBoolean("hasFeedback");
		if (map.containsKey("check"))
			check = map.getBoolean("check");
		if (map.containsKey("teltMee"))
			teltMee = map.getBoolean("teltMee");
		if (map.containsKey("formuleToolBijFocus"))
			formuleToolBijFocus = map.getBoolean("formuleToolBijFocus");
		if (map.containsKey("logOption"))
			logOption = map.getBoolean("logOption");
		if (map.containsKey("logID"))
			logID = map.getString("logID");
		if (map.containsKey("boxMetRand"))
			boxMetRand = map.getBoolean("boxMetRand");
		if (map.containsKey("logObjectives"))
		{
			ObjectList logObjectivesList = ( map.getObjectList("logObjectives") );
			logObjectives = new boolean[logObjectivesList.size()][];
			for(int i = 0; i < logObjectivesList.size(); i++)
			{	logObjectives[i] = logObjectivesList.getBooleanArray(i);
			}
		}

		this.scoreMax = scoreMax;
		this.answerModels = new ArrayList<Map<String, Object>> ();
		for(int i = 0; i < answerModels.size(); i++)
		{	this.answerModels.add(answerModels.get(i));
		}
		this.hasFeedback = hasFeedback;
		this.check = check;
		this.teltMee = teltMee;
		this.logOption = logOption;
		this.logID = logID;
		this.logObjectives = logObjectives;

//		if (editor != null)
//			editor.zetStippels(!boxMetRand);
		
		//add(editor);
		
		//TODO: fontOvererving
//		if(fontOvererving && getParent() instanceof TekstInteractiePanelVak)
//		{	Font geerftFont = ((TekstInteractiePanelVak)getParent()).getTekstVak().getFont();
//			if (!geerftFont.getName().equals("TimesRoman") && WiskOpdr.formTimes && !WiskOpdr.mac) {
//				geerftFont = new Font("TimesRoman", geerftFont.getStyle(), geerftFont.getSize() * 6 / 5);
//			}
//			formuleVakFont = geerftFont;
//			editor.setFont(formuleVakFont);
//		}

	}

	
	
	public void zetVarNamen(String[] namen)
	{
		varNamen = namen;
		avChecker.zetVarNamen(namen);
	}

	public void zetJuisteOplossingen(Expressie[][] oplossingen)
	{
		juisteOplossingen = oplossingen;
		avChecker.zetJuisteOplossingen(oplossingen);
	}
	
	public void zetVolledigeBreedte(int b)
	{
		editor.zetVolledigeBreedte(b);
	}

	public void setState(HashMap<String, Object> h)
	{
		boolean ingevuld = false;
		boolean nagekeken = false;
		String antwoord = "";
		Vector attempts = new Vector();
		int attemptsCount = 0;
		int errorCount = 0;

		if (h.containsKey("ingevuld"))
			ingevuld = ((Boolean) h.get("ingevuld")).booleanValue();
		if (h.containsKey("nagekeken"))
			nagekeken = ((Boolean) h.get("nagekeken")).booleanValue();
		if (h.containsKey("antwoord"))
			antwoord = (String) h.get("antwoord");
//		if (h.containsKey("attempts"))
//			attempts = h.get("attempts"));
			
		//	OpdrNavStruct.toVector(h.get("attempts"));
		if (h.containsKey("attemptsCount"))
			attemptsCount = ((Number) h.get("attemptsCount")).intValue();
		if (h.containsKey("errorCount"))
			errorCount = ((Number) h.get("errorCount")).intValue();

		//this.ingevuld = ingevuld;
		//this.nagekeken = nagekeken;
		this.attempts = attempts;
		this.attemptsCount = attemptsCount;
		this.errorCount = errorCount;
		editor.clearAll();
		editor.insert(antwoord);
		System.out.println("editor setState, antwoord geinsert");
		if (ingevuld && (mode == 0 || nagekeken))
		{	System.out.println("editor wordt nagekeken in setState..");
			editor.kijkNa(true);
		//met deze kijkNa komen ook de waarden voor ingevuld en nagekeken in de editor wel weer goed?
		}
	}

	public HashMap<String, Object> getState()
	{
		boolean ingevuld = false;
		boolean nagekeken = false;
		String antwoord = "";
		Vector attempts = new Vector();
		int attemptsCount = 0;
		int errorCount = 0;

		editor.kijkNa(false);

		ingevuld = editor.isIngevuld();
		nagekeken = editor.isNagekeken();
		antwoord = editor.toString();
		
		//TODO: deze info ook uit editor halen ipv uit this.
		attempts = this.attempts;
		attemptsCount = this.attemptsCount;
		errorCount = this.errorCount;

		if (logOption)
		{
			HashMap logMap = new HashMap<String, Object>();

			String logString = "";
			logString = editor.toString();
			
			logMap.put("logAnswer", logString);
			logMap.put("logScore", new Integer(score));
			logMap.put("logMaxScore", new Integer(scoreMax));
			logMap.put("logErrorCount", new Integer(errorCount));
			logMap.put("logAttemptsCount", new Integer(attemptsCount));
			logMap.put("logAttempts", attempts);

		//TODO: Hoe wel?	WiskOpdr.setLog(logID, logMap);
		}

		HashMap<String, Object> h = new HashMap<String, Object>();
		h.put("ingevuld", new Boolean(ingevuld));
		h.put("nagekeken", new Boolean(nagekeken));
		h.put("antwoord", antwoord);
		h.put("attempts", attempts);
		h.put("attemptsCount", new Integer(attemptsCount));
		h.put("errorCount", new Integer(errorCount));

		return h;
	}

	public void setAttempt()
	{
		setAttempt(false);
	}

	public void setAttempt(boolean start)
	{
//		String goedFout = "";
//		if (huidigIC == goedIC && huidigIC.isVisible())
//			goedFout = "goed";
//		if (huidigIC == halfIC && huidigIC.isVisible())
//			goedFout = "half";
//		if (huidigIC == foutIC && huidigIC.isVisible())
//			goedFout = "fout";
//
//		String antwoord = "";
//		antwoord = editor.toString();
//		if (antwoord.equals(""))
//			return;
//
//		String attemptFormuleString = FormuleParser.schoon(FormuleParser.formuleString(antwoord));
//		attemptFormuleString = StringUtils.replaceStr(attemptFormuleString, "(0-", "(-");
//		antwoord = FormuleParser.pel(attemptFormuleString);
//		String fbTekst = "";
//		if (feedbackTekst.isVisible() && feedbackTekst.getParent() != null)
//			fbTekst = feedbackTekst.getText();
//
//		String s = antwoord;
//		s = s + "   ;   ";
//		s = s + new Date().toString();
//		s = s + "   ;   ";
//		s = s + "Regelnummer = " + 0;
//		s = s + "   ;   ";
//		s = s + goedFout;
//		s = s + "   ;   ";
//		s = s + "score = " + score;
//		s = s + "   ;   ";
//		s = s + fbTekst;
//
//		attempts.addElement(s);
//		System.out.println(s);
	}

//	public void setBounds(int x, int y, int b, int h)
//	{
//		feedbackButton.setBounds(editor.getWidth() - 15, getSize().height - 14, 14, 14);
//		goedIC.setLocation(getWidth() - 18, 0);
//		halfIC.setLocation(getWidth() - 18, 0);
//		foutIC.setLocation(getWidth() - 18, 0);
//		super.setBounds(x, y, b, h);
//	}

	public void wis()
	{
//		if (huidigIC != null)
//		{
//			huidigIC.setVisible(false);
//
//		}
		checkimg.setVisible(false);

		correct = false;
		score = 0;
		//nagekeken = false;
		//ingevuld = false;

		attempts = new Vector();
	}

	
	public void resize()
	{
		hoogte = editor.getHeight();
	//	mainPanel.setHeight(hoogte + "px");
		parent.resize();
	}
	
	public int getHeight()
	{
		return hoogte;
	}
	
	public FormuleEditor geefFormuleVak()
	{
		return editor;
	}

	public int geefAsHoogte()
	{
		return editor.getAsHoogte();// + (getFontMetrics(formuleVakFont)).getAscent() / 2 + 5;
	}

	public int getIpId()
	{
		return 0;
	}

	public String getIpExpString()
	{
		return null;
	}

	public int getScore()
	{
		if (!teltMee)
			return 0;
		return score;
	}

	public int[][] getScoreObjectives()
	{
		if (logObjectives == null)
			return null;
		int[][] scoreObjectives = new int[logObjectives.length][];
		for (int i = 0; i < logObjectives.length; i++)
			scoreObjectives[i] = new int[logObjectives[i].length];
		for (int i = 0; i < logObjectives.length; i++)
			for (int j = 0; j < logObjectives[i].length; j++)
			{
				if (logObjectives[i][j])
					scoreObjectives[i][j] = score;
			}
		return scoreObjectives;
	}

	public int getScoreMax()
	{
		if (!teltMee)
			return 0;
		return scoreMax;
	}

	public Boolean isCorrect()
	{
		return editor.isCorrect();
	}

	public boolean isFout()
	{
		if (!teltMee)
			return false;
		return fout;
	}

	public void zetMode(int mode)
	{
		this.mode = mode;
	}

	public void zetNagekeken(boolean b)
	{
		if (editor.isIngevuld())
			editor.zetNagekeken(b);
	}


	public void start()
	{
	}

	public void destroy()
	{
	}

	public void opnieuw()
	{
		score = 0;
		correct = false;
	}

	
	public Panel asWidget()
	{
		return editor.getAsPanel();
	}


	
	public boolean isIngevuld()
	{
		return editor.isIngevuld();
	}
	
	public boolean isNagekeken()
	{
		return editor.isNagekeken();
	}
	
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());
		if(editor != null)
		{
			editor.setCommunicationRoot(comRoot);
		}
	}


}
