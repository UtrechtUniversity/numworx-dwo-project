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

	private boolean ingevuld;
	private boolean nagekeken;

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

	private int goedHalfFout;
	private int puntenFeedback;
	private String feedback;
	private FlowPanel feedbackPanel;
	private boolean gelijkwaardig;
	//private TekstArea feedbackTekst;
	//private FormuleButton feedbackButton;
	private Label feedbackLabel;
	private TouchPanel checkPanel;

	private boolean check;
	private boolean teltMee;

	private String[] randomVars;
	private HashMap<String, Number> randomVarWaarden;

	private Image checkimg;

	private LayoutPanel mainPanel;
	private FormuleEditorWithAnswer editor;
	private int minBreedte;
	private int ashoogte;

	private boolean tabletAan;
	private boolean formuleToolBijFocus;

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

	/*private static String[] imageNames = 
	{	"goedkrul.gif",
		"goedkrulhalf.gif",
		"foutkruis.gif",
		"goedkrul_en.gif",
		
	};
	
	private static Hashtable images;*/

	/*public static void zetPlaatjes(Image gk, Image fk, Image hk)
	{	GOEDKRUL = gk;
		FOUTKRUIS = fk;
		HALFKRUL = hk;
	}*/
	
	public StelselOplossingenVak(StelselAntwoordVak parent, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden)
	{
		
		ObjectMap map = JSONUtilities.wrapMap(h);
		this.breedte = map.getInt("breedte");
		System.out.println("breedte oplossingenvak: " + breedte);
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
		
		mainPanel = new LayoutPanel();
		mainPanel.setWidth(breedte + "px");
		hoogte = editor.getMainRegel().getHeight();
		mainPanel.setHeight(hoogte + "px");
		
		feedbackLabel = new Label("?");
		feedbackLabel.getElement().getStyle().setFontSize(11, Style.Unit.PX);
		feedbackLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		feedbackLabel.getElement().getStyle().setPadding(0, Style.Unit.PX);
		feedbackLabel.getElement().getStyle().setMarginTop(-1, Style.Unit.PX);
		feedbackLabel.getElement().getStyle().setMarginLeft(1, Style.Unit.PX);
		feedbackLabel.getElement().getStyle().setPaddingLeft(2, Style.Unit.PX);
		//feedbackLabel.getElement().getStyle().setBackgroundColor("white");
		feedbackLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		feedbackLabel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
		feedbackLabel.setWidth(10 + "px");
		feedbackLabel.setVisible(false);
		
		checkimg = new Image(FORMULE_BUNDLE.goedkrul().getSafeUri());
		checkimg.getElement().getStyle().setMarginRight(10, Unit.PX);
		checkimg.setVisible(false);
		
		
		checkPanel = new TouchPanel();
		checkPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		checkPanel.getElement().getStyle().setPaddingLeft(5, Style.Unit.PX);
		checkPanel.getElement().getStyle().setProperty("verticalAlign", "top");
		checkPanel.getElement().getStyle().setMarginTop(-3, Style.Unit.PX);
		checkPanel.add(checkimg);
		checkPanel.add(feedbackLabel);
		checkPanel.setPixelSize(15, hoogte);
		checkPanel.addTapHandler(new TapHandler(){

			@Override
			public void onTap(TapEvent event) {
				//later invullen. Of toch FormuleEditorWithAnswer maken; daar komt dit rechtstreeks uit. Goede interactiePanelLaunchState meegeven..
//				if(feedbackLabel.isVisible())
//				{	int yPos = asWidget().getAbsoluteTop() + asWidget().getOffsetHeight() + 10;
//					if(yPos + feedbackTekst.hoogte + 10 > Window.getClientHeight())
//						yPos = Window.getClientHeight() - feedbackTekst.hoogte - 10;
//					
//					feedbackPanel.setPopupPosition(asWidget().getAbsoluteLeft() + 10, yPos);
//					feedbackPanel.show();
//				}
			}
			
		});
		
		//editor.getAsPanel().getElement().getStyle().setBackgroundColor("red");
		mainPanel.add(editor.getAsPanel());
		//mainPanel.setWidgetLeftRight(editor.getAsPanel(), 0, Style.Unit.PX, -100, Style.Unit.PX);
//		mainPanel.add(checkPanel);
//		mainPanel.setWidgetRightWidth(checkPanel, 0, Style.Unit.PX, 15, Style.Unit.PX);
		editor.setCurrentElementRepaint();
		System.out.println("mainPanel breedte = " + mainPanel.getOffsetWidth() + " en editor breedte = " + editor.getWidth());
		
		
		
	
	}

	public void zetMinBreedte(int b)
	{
		minBreedte = b;
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
		this.randomVars = randomVars;
		this.randomVarWaarden = randomValues;
		this.formuleToolBijFocus = formuleToolBijFocus;
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

	public void setAnswerModel(int nr)
	{
		Map h = answerModels.get(nr);
		if (h == null)
			return;

		String antwoordString = "$f@";
		int puntenFeedback = 0;
		String feedback = "";
		int goedHalfFout = 0;

		if (h != null)
		{
			if (h.containsKey("antwoordString"))
				antwoordString = (String) h.get("antwoordString");
			if (h.containsKey("puntenFeedback"))
				puntenFeedback = ((Integer) h.get("puntenFeedback")).intValue();
			if (h.containsKey("feedback"))
				feedback = (String) h.get("feedback");
			if (h.containsKey("goedHalfFout"))
				goedHalfFout = ((Integer) h.get("goedHalfFout")).intValue();

		}
		try
		{
			antwoordString = FormuleParser.randomizeTekstVakString(antwoordString, randomVars, randomVarWaarden);
		}
		catch (Exception e)
		{
		}

		try
		{
			feedback = FormuleParser.randomizeTekstVakString(feedback, randomVars, randomVarWaarden);
		}
		catch (Exception e)
		{
			feedback = "$f???@";
		}

		this.goedHalfFout = goedHalfFout;
		this.puntenFeedback = puntenFeedback;
		//this.antwoordString = antwoordString;
		this.feedback = feedback;

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

		this.ingevuld = ingevuld;
		this.nagekeken = nagekeken;
		this.attempts = attempts;
		this.attemptsCount = attemptsCount;
		this.errorCount = errorCount;
		editor.clearAll();
		editor.insert(antwoord);
		
		if (ingevuld && (mode == 0 || nagekeken))
			editor.kijkNa();
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

		ingevuld = this.ingevuld;
		nagekeken = this.nagekeken;
		antwoord = editor.toString();
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
		nagekeken = false;
		ingevuld = false;

		attempts = new Vector();
	}

//	public void zetMaat()
//	{
//		setSize(Math.max(minBreedte, editor.getSize().width + 24), editor.getSize().height + 8);
//		editor.setLocation(4, 4);
//		feedbackButton.setBounds(getSize().width - 15, getSize().height - 12, 15, 15);
//		ashoogte = editor.ashoogte + 4;
//		if (getParent() instanceof FormuleElement)
//			((FormuleElement) getParent()).zetMaat();
//		if (getParent() instanceof TekstElement)
//			((TekstElement) getParent()).zetMaat();
//	}

	public FormuleEditor geefFormuleVak()
	{
		return editor;
	}

	public void zetTabletAan(boolean b)
	{
		tabletAan = b;
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

	public boolean isCorrect()
	{
		if (!teltMee)
			return true;
		return correct;
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
		if (ingevuld)
			nagekeken = b;
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

	public void zetGoedFout(int uitslag)
	{
		if (uitslag == AntwoordVakChecker.GOED)
			checkimg.setUrl(FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		else if (uitslag == AntwoordVakChecker.DOOR || uitslag == AntwoordVakChecker.HALF)
			checkimg.setUrl(FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
		else if (uitslag == AntwoordVakChecker.FOUT)
			checkimg.setUrl(FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());

		checkimg.setVisible(check && goedHalfFout != AntwoordVakChecker.GEEN); // Wim: Hier verscheen het vinkje als goedhalfFout GEEN is
	}
	
	

	public void setFeedback(String tekst, boolean closeable)
	{
//		feedbackTekst.setText("");
//		feedbackTekst.setCloseable(closeable);
//		feedbackTekst.setSize(200, 20);
//		feedbackTekst.setText(tekst);
//		feedbackTekst.resize();
		//add(feedbackTekst);
		//produceAction("feedback");
	}

//	public void activateFeedback(Component c)
//	{
//		Container parent = getParent();
//		int x = parent.getLocation().x;
//		int y = parent.getLocation().y;
//		int h = parent.getSize().height;
//		for (int i = 0; parent != null && i < 40; i++)
//		{
//			if (parent instanceof OpdrNavStruct)
//			{
//				int cx = Math.min(parent.getSize().width - c.getSize().width, x + 10);
//				int cy = y + h + 10 > parent.getSize().height ? y - c.getSize().height - 10 : y + h + 10;
//				c.setLocation(cx, cy);
//				((OpdrNavStruct) parent).add(c, 0);
//				parent.repaint();
//				break;
//			}
//			else
//			{
//				parent = parent.getParent();
//				x += parent.getLocation().x;
//				y += parent.getLocation().y;
//			}
//		}
//	}

//	public void activateTablet()
//	{
//		Container parent = getParent();
//		int x = parent.getLocation().x;
//		int y = parent.getLocation().y;
//		int h = parent.getSize().height;
//		for (int i = 0; parent != null && i < 40; i++)
//		{
//			if (parent instanceof TabletOwner)
//			{
//				((TabletOwner) parent).addTablet(this, x + 20, y + h + 20);
//				Tablet tablet = ((TabletOwner) parent).getTablet();
//				int tx = Math.min(parent.getSize().width - tablet.getSize().width, x + 20);
//				int ty = y + h + 20 + tablet.getSize().height > parent.getSize().height ? y - tablet.getSize().height - 10 : y + h + 20;
//				tablet.setLocation(tx, ty);
//				break;
//			}
//			else
//			{
//				parent = parent.getParent();
//				if (parent == null)
//					return;
//				x += parent.getLocation().x;
//				y += parent.getLocation().y;
//			}
//		}
//
//		//if(getParent().getParent()instanceof TabletOwner)((TabletOwner)getParent().getParent()).addTablet(antwoordFormuleVak,getLocation().x+20, getLocation().y+getSize().height-120);
//		//else if(getParent().getParent().getParent() instanceof TabletOwner)((TabletOwner)getParent().getParent().getParent()).addTablet(antwoordFormuleVak,getLocation().x+20, getLocation().y+getSize().height+50);
//		//else if(getParent().getParent().getParent().getParent() instanceof TabletOwner)((TabletOwner)getParent().getParent().getParent().getParent()).addTablet(antwoordFormuleVak,getLocation().x+20,getLocation().y+getSize().height+70);
//		//else if(getParent().getParent().getParent().getParent().getParent() instanceof TabletOwner)((TabletOwner)getParent().getParent().getParent().getParent().getParent()).addTablet(antwoordFormuleVak,getLocation().x+20, getParent().getParent().getLocation().y+getSize().height+70);
//		//else if(getParent().getParent().getParent().getParent().getParent().getParent() instanceof TabletOwner)((TabletOwner)getParent().getParent().getParent().getParent().getParent().getParent()).addTablet(antwoordFormuleVak,getLocation().x+20, getParent().getParent().getLocation().y+getSize().height+70);
//	}

//	public void zetTabletUser()
//	{
//		Container parent = getParent();
//		for (int i = 0; parent != null && i < 40; i++)
//		{
//			if (parent instanceof TabletOwner)
//			{
//				((TabletOwner) parent).zetTabletUser(this);
//				break;
//			}
//			else
//			{
//				parent = parent.getParent();
//			}
//		}
//	}

	
	
	public LayoutPanel asWidget()
	{
		return mainPanel;
	}

//	public void mousePressed(MouseEvent e)
//	{
//		editor.requestFocus();
//		editor.zetOpEind();
//		if (formuleToolBijFocus)
//			activateTablet();
//	}

//	public void mouseClicked(MouseEvent e)
//	{
//		;
//	}
//
//	public void mouseReleased(MouseEvent e)
//	{
//		;
//	}
//
//	public void mouseEntered(MouseEvent e)
//	{
//		;
//	}
//
//	public void mouseExited(MouseEvent e)
//	{
//		;
//	}
	
	public boolean isIngevuld()
	{
		return ingevuld;
	}
	
	public boolean isNagekeken()
	{
		return nagekeken;
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

//	public void actionPerformed(ActionEvent e)
//	{
//		if (e.getSource() == editor && e.getActionCommand().equals("ingevuld"))
//		{
//			if (mode == 0 || mode == 1)
//			{
//				kijkNa();
//				zetNagekeken(true);
//				if (ingevuld)
//					parent.produceAction("checked");
//			}
//		}
//		else if (e.getSource() == editor && e.getActionCommand().equals("formChanged"))
//		{
//			if (feedbackTekst != null && getParent() == null && feedbackTekst.getParent() != null)
//			{
//				remove(feedbackTekst);
//				parent.produceAction("feedbackWeg");
//			}
//			zetGoedFout(GEEN);
//		}
//		else if(e.getSource() == editor && e.getActionCommand().equals("zetMaat"))
//			parent.resize();
//		else if(e.getSource() == editor && e.getActionCommand().equals("focus"))
//		{
//			if(parent.rekenVakZichtbaar)
//			{
//				parent.rekenVak.geefHoofdEditor().zetFocusOplossingenRegel();
//			}
//		}
//		else if (e.getSource() == feedbackButton)
//		{
//			feedbackTekst.setLocation(0, 0);
//			feedbackPanel.setSize(feedbackTekst.getSize().width, feedbackTekst.getSize().height);
//			feedbackPanel.add(feedbackTekst);
//			this.activateFeedback(feedbackPanel);
//			feedbackButton.setVisible(false);
//		}
//		else if (e.getSource() == feedbackTekst)
//		{
//			if (feedbackPanel.getParent() != null)
//			{
//				Container c = feedbackPanel.getParent();
//				c.remove(feedbackPanel);
//				c.repaint();
//			}
//			feedbackButton.setVisible(true);
//		}
//	}
}
