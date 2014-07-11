package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;




















import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;
import nl.uu.fi.dwo.mobile.utils.StringUtils;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.FormuleParser;


public class AntwoordTekstVak implements InteractionView{

	private Map<String, Object> launchState; 
	OpdrNavIF comRoot;
	
	String[] randomVarNamen = null;
	HashMap randomVarWaarden = null;
	
	private int mode;
	private boolean boxMetRand;
	
	private LayoutPanel basisPanel;
	private TouchPanel achtergrondPanel;
	int breedte = 110;
	int hoogte = 24; 	
	boolean volledigeBreedte = false;
	

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
	
	private TextBox antwoordTF;
	private FormuleEditor formuleVak;
	
	private String antwoordString = "";
	private String[] juisteAntwoorden;
	private ObjectList answerModels;
	private boolean hasFeedback;

	private int goedHalfFout;
	private int puntenFeedback;
	private String feedback;
	private boolean gelijkwaardig;
	private FlowPanel feedbackPanel = null;
	private PopupFacade feedbackPopup;
	private PopupButton feedbackButton;
	//private TekstArea feedbackTekst;
	//private FormuleButton feedbackButton;
	
	private boolean check;
	private boolean teltMee;
	
	Image goedKrulImage, foutKruisImage, goedKrulHalfImage;
	
	private boolean logOption;
	private String logID;
	
	private boolean[][] logObjectives;
	
	private int errorCount;
	private int attemptsCount;
	private Vector attempts;
	
	// TODO: Voor in formule-modus:
	//private Font formuleVakFont = (!WiskOpdr.formTimes) || WiskOpdr.mac || WiskOpdr.zoefi ? WiskOpdr.formuleFont1Mac : WiskOpdr.formuleFont1; //new Font("TimesRoman",Font.PLAIN,16);

	FormuleFont fm;
	private boolean formuleMode;
	//private FormuleVak formuleVak;
	private int minBreedte;
	private int ashoogte;
	private PopupFacade facade;

	private boolean tabletAan;
	private boolean formuleToolBijFocus;
	
	private TekstRegel parentRegel;
	

	public AntwoordTekstVak(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		if(h != null)
		{	ObjectMap map = JSONUtilities.wrapMap(h);
			if(map.containsKey("breedte"))
				breedte = map.getInt("breedte");
			if(map.containsKey("hoogte"))
				hoogte = map.getInt("hoogte");
			if(map.containsKey("volledigeBreedte"))
				volledigeBreedte = map.getBoolean("volledigeBreedte");
			if(h.containsKey("interactiePanelLaunchState"))
				launchState = map.getMap("interactiePanelLaunchState");
		}
		facade = new PopupFacade(h);
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		init(breedte, hoogte, launchState, randomVarWaarden);
		initialize(h, randomVarNamen, randomVarWaarden);
		
		
	}
	
	
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width;
		hoogte = height;
		
		ObjectMap map = JSONUtilities.wrapMap(launchData);
		if(map != null)
		{	
			if (map.containsKey("antwoordString"))
				antwoordString = map.getString("antwoordString");
			if (map.containsKey("scoreMax"))
				scoreMax = map.getInt("scoreMax");
			if (map.containsKey("answerModels"))
				answerModels = map.getObjectList("answerModels");
			if (map.containsKey("hasFeedback"))
				hasFeedback = map.getBoolean("hasFeedback");
			if (map.containsKey("check"))
				check = map.getBoolean("check");
			if (map.containsKey("teltMee"))
				teltMee = map.getBoolean("teltMee");
			if (map.containsKey("formuleMode"))
				formuleMode = map.getBoolean("formuleMode");
			if (map.containsKey("formuleToolBijFocus"))
			{
				formuleToolBijFocus = map.getBoolean("formuleToolBijFocus");
				
			}
			if (map.containsKey("logOption"))
				logOption = map.getBoolean("logOption");
			if (map.containsKey("logID"))
				logID = map.getString("logID");
			if (map.containsKey("boxMetRand"))
				boxMetRand = map.getBoolean("boxMetRand");
			if (map.containsKey("logObjectives"))
				logObjectives = (boolean[][]) map.get("logObjectives");
		}
		
		

	}
	
	private void initialize(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		attempts = new Vector();
		
		try
		{
			antwoordString = FormuleParser.randomizeTekstVakString(antwoordString, randomVarNamen, randomVarWaarden);
		}
		catch (Exception e)
		{
		}
		if (!formuleMode)
			antwoordString = StringUtils.replaceStr(antwoordString, "@", "");
		if (!formuleMode)
			antwoordString = StringUtils.replaceStr(antwoordString, "$f", "");
		antwoordString = StringUtils.replaceStr(antwoordString, " ", "");
		
		basisPanel = new LayoutPanel();
		basisPanel.setStylePrimaryName("antwoordtekstvak");
		basisPanel.setPixelSize(breedte - 2, hoogte - 3);
		//basisPanel.getElement().getStyle().setProperty("border", "1px solid gray");
		//basisPanel.getElement().getStyle().setBackgroundColor(CssColor.make(255, 255, 255).toString());
		//basisPanel.getElement().getStyle().setProperty("border", "1px solid gray");
		
		antwoordTF = new TextBox();
		antwoordTF.getElement().getStyle().setProperty("border", "1px solid gray");
		antwoordTF.getElement().getStyle().setFontSize(12, Style.Unit.PX);
		antwoordTF.setWidth((breedte - 4) + "px");
		
		//antwoordTF.setHeight((hoogte - 5) + "px");
		
		antwoordTF.getElement().getStyle().setPaddingLeft(0, Style.Unit.PX);
		antwoordTF.getElement().getStyle().setPaddingTop(0, Style.Unit.PX);
		antwoordTF.getElement().getStyle().setPaddingBottom(1, Style.Unit.PX);
		//antwoordTF.getElement().getStyle().setPaddingBottom(2, Style.Unit.PX);
		//antwoordTF.getElement().getStyle().setPaddingBottom(0, Style.Unit.PX);
		antwoordTF.getElement().getStyle().setPaddingRight(0, Style.Unit.PX);
		
		//antwoordTF.getElement().getStyle().setMarginRight(5, Style.Unit.PX);
		antwoordTF.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) 
			{	if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) 
		    	{	kijkNa();
		    	
		    	}
			}
		});
		
		antwoordTF.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				comRoot.getKeyboard().setEditor(null);
				
			}});
		
		
		//antwoordTF.setBorder(BorderFactory.createLineBorder(new Color(153, 153, 153)));
		//antwoordTF.setBounds(0, 0, 80, 21);
		//antwoordTF.addActionListener(this);
		
		formuleVak = new FormuleEditor() {

			@Override
			public void enter() {
				if(mode == OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS)
				{
					return; 
				}
				kijkNa();
			}

			@Override
			public void addElement(FormuleElement e)
			{
				super.addElement(e);
				resize();
				goedKrulImage.setVisible(false);
				goedKrulHalfImage.setVisible(false);
				foutKruisImage.setVisible(false);
			}

			@Override
			public void removeCurrentElement()
			{
				super.removeCurrentElement();
				resize();
				goedKrulImage.setVisible(false);
				goedKrulHalfImage.setVisible(false);
				foutKruisImage.setVisible(false);
			}

			@Override
			public void removeNextElement()
			{
				super.removeNextElement();
				resize();
				goedKrulImage.setVisible(false);
				goedKrulHalfImage.setVisible(false);
				foutKruisImage.setVisible(false);
			}

			@Override
			public void insert(String text)
			{
				super.insert(text);
				resize();
				goedKrulImage.setVisible(false);
				goedKrulHalfImage.setVisible(false);
				foutKruisImage.setVisible(false);
			}

			@Override
			public boolean isInputNeeded() {
				return false;
			}
			
			
			
			
		} ;
		//hier toetsenbord aan vastmaken. WIM??
		formuleVak.setFormuleToolBijFocus(formuleToolBijFocus);
		//formuleVak.getMainRegel().setMinimumWidth(breedte - 20);
		formuleVak.getMainRegel().setMinimumWidth(breedte);
		formuleVak.getMainRegel().setMinimumHeight(hoogte - 8);
		
		//formuleVak.setFont(formuleVakFont);
		//formuleVak.setBorder(false);
		//formuleVak.addActionListener(this);
		//formuleVak.setLocation(4, 4);
		//addMouseListener(this);
		
		if (formuleMode) 
		{	//basisPanel.setSize(Math.max(minBreedte, formuleVak.getSize().width + 24), formuleVak.getSize().height + 8);
			achtergrondPanel = new TouchPanel();
			achtergrondPanel.getElement().getStyle().setBackgroundColor("white");
			achtergrondPanel.getElement().getStyle().setProperty("border", "1px solid gray");
			basisPanel.add(achtergrondPanel);
			basisPanel.setWidgetLeftRight(achtergrondPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
			basisPanel.setWidgetTopBottom(achtergrondPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
			//basisPanel.setWidgetLeftRight(achtergrondPanel)
			//Panel panel = formuleVak.getAsPanel();
			achtergrondPanel.getElement().addClassName("insert_formule");
			achtergrondPanel.getElement().getStyle().setPaddingLeft(3, Style.Unit.PX);
			achtergrondPanel.getElement().getStyle().setPaddingTop(2, Style.Unit.PX);
			achtergrondPanel.add(formuleVak.getMainRegel().getCanvas());
			achtergrondPanel.addTouchHandler(new FormuleEditorTouchHandler(formuleVak));
			//basisPanel.add(formuleVak.getMainRegel().getCanvas());
			//basisPanel.setWidgetLeftRight(formuleVak.getMainRegel().getCanvas(), 4, Style.Unit.PX, 20, Style.Unit.PX);
			//basisPanel.setWidgetTopBottom(formuleVak.getMainRegel().getCanvas(), 4, Style.Unit.PX, 4, Style.Unit.PX);
			ashoogte = formuleVak.getMainRegel().getAsHoogte() + 2;
		}
		else
		{	//basisPanel.setSize(Math.max(minBreedte, antwoordTF.getSize().width + 2), antwoordTF.getSize().height + 4);
			basisPanel.add(antwoordTF);
			basisPanel.setWidgetLeftRight(antwoordTF, 0, Style.Unit.PX, 0, Style.Unit.PX);
			basisPanel.setWidgetTopBottom(antwoordTF, 0, Style.Unit.PX, 0, Style.Unit.PX);
			ashoogte = Math.round(hoogte) - 5;// /*antwoordTF.getOffsetHeight()*/ - 3;
		}
		
		//TODO: Noordhoff-instelling maken.
		goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		goedKrulHalfImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
		foutKruisImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
		
		basisPanel.add(goedKrulImage);
		basisPanel.add(goedKrulHalfImage);
		basisPanel.add(foutKruisImage);
		basisPanel.setWidgetRightWidth(goedKrulImage, 0, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(goedKrulImage, 0, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetRightWidth(goedKrulHalfImage, 0, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(goedKrulHalfImage, 0, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetRightWidth(foutKruisImage, 0, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(foutKruisImage, 0, Style.Unit.PX, 15, Style.Unit.PX);
		goedKrulImage.setVisible(false);
		goedKrulHalfImage.setVisible(false);
		foutKruisImage.setVisible(false);
		
		zetJuisteAntwoord(antwoordString);
		
		feedbackPanel = new FlowPanel();
		feedbackPanel.getElement().getStyle().setFontSize(14, Unit.PX);
		feedbackPanel.getElement().getStyle().setProperty("lineHeight", "1.2");
		feedbackPanel.getElement().getStyle().setWidth(200, Unit.PX);
		feedbackPanel.getElement().getStyle().setHeight(40, Style.Unit.PX);
		feedbackPanel.getElement().getStyle().setProperty("display", "inline-block");
		feedbackPanel.getElement().getStyle().setBackgroundColor("#FFFFDD");
		
		
		
		feedbackButton = new PopupButton(feedbackPanel, goedKrulHalfImage, null);
		feedbackButton.getElement().getStyle().setPaddingLeft(0, Style.Unit.PX);
		feedbackButton.getElement().getStyle().setPaddingTop(0, Style.Unit.PX);
		feedbackButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		//feedbackButton.getElement().getStyle().setBackgroundColor(CssColor.make(215, 215, 215).toString());
		feedbackButton.setVisible(false);
		//setLayer((Component) feedbackButton, JLayeredPane.PALETTE_LAYER.intValue());
		basisPanel.add(feedbackButton);
		basisPanel.setWidgetRightWidth(feedbackButton, 2, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetBottomHeight(feedbackButton, 2, Style.Unit.PX, 15, Style.Unit.PX);
		
		
	}
	
	public void setAnswerModel(int nr)
	{
		ObjectMap h = answerModels.getObjectMap(nr);
		if (h == null)
			return;

		String antwoordString = "$f@";
		int puntenFeedback = 0;
		String feedback = "";
		int goedHalfFout = 0;

		if (h != null)
		{
			if (h.containsKey("antwoordString"))
				antwoordString = h.getString("antwoordString");
			if (h.containsKey("puntenFeedback"))
				puntenFeedback = h.getInt("puntenFeedback");
			if (h.containsKey("feedback"))
				feedback = h.getString("feedback");
			if (h.containsKey("goedHalfFout"))
				goedHalfFout = h.getInt("goedHalfFout");

		}
		try
		{
			antwoordString = FormuleParser.randomizeTekstVakString(antwoordString, randomVarNamen, randomVarWaarden);
		}
		catch (Exception e)
		{
		}

		try
		{
			feedback = FormuleParser.randomizeTekstVakString(feedback, randomVarNamen, randomVarWaarden);
		}
		catch (Exception e)
		{
			feedback = "$f???@";
		}

		if (!formuleMode)
			antwoordString = StringUtils.replaceStr(antwoordString, "@", "");
		if (!formuleMode)
			antwoordString = StringUtils.replaceStr(antwoordString, "$f", "");
		antwoordString = StringUtils.replaceStr(antwoordString, " ", "");

		this.goedHalfFout = goedHalfFout;
		this.puntenFeedback = puntenFeedback;
		this.antwoordString = antwoordString;
		this.feedback = feedback;

		zetJuisteAntwoord(antwoordString);

	}
	
	public void zetJuisteAntwoord(String s)
	{	
		juisteAntwoorden = StringUtils.split(s, "::");
	}
	
	public HashMap<String, Object> getState()
	{
		boolean ingevuld = false;
		boolean nagekeken = false;
		String antwoord = "";
		Vector attempts = new Vector();
		int attemptsCount = 0;
		int errorCount = 0;

		if (this.ingevuld && (mode == 0 || this.nagekeken))
			kijkNa();
		else
			kijkNa(false);

		ingevuld = this.ingevuld;
		nagekeken = this.nagekeken;
		if (formuleMode)
			antwoord = formuleVak.toString();
		else
			antwoord = antwoordTF.getText();
		attempts = this.attempts;
		attemptsCount = this.attemptsCount;
		errorCount = this.errorCount;

		if (logOption)
		{
			HashMap logMap = new HashMap();

			String logString = "";
			if (formuleMode)
				logString = formuleVak.toString();
			else
				logString = antwoordTF.getText();

			logMap.put("logAnswer", logString);
			logMap.put("logScore", new Integer(score));
			logMap.put("logMaxScore", new Integer(scoreMax));
			logMap.put("logErrorCount", new Integer(errorCount));
			logMap.put("logAttemptsCount", new Integer(attemptsCount));
			logMap.put("logAttempts", attempts);

			//WiskOpdr.setLog(logID, logMap);
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
		if (h.containsKey("attempts"))
			attempts = (Vector) h.get("attempts");
		if (h.containsKey("attemptsCount"))
			attemptsCount = ((Number) h.get("attemptsCount")).intValue();
		if (h.containsKey("errorCount"))
			errorCount = ((Number) h.get("errorCount")).intValue();

		this.ingevuld = ingevuld;
		this.nagekeken = nagekeken;
		this.attempts = attempts;
		this.attemptsCount = attemptsCount;
		this.errorCount = errorCount;

		if (formuleMode)
			formuleVak.insert(antwoord);
		else
			antwoordTF.setText(antwoord);

		if (ingevuld && (mode == 0 || nagekeken))
			kijkNa();
	}
	
	public void setAttempt()
	{
		setAttempt(false);
	}

	public void setAttempt(boolean start)
	{
		String goedFout = "";
		if(goedKrulImage.isVisible())
			goedFout = "goed";
		else if(goedKrulHalfImage.isVisible())
			goedFout = "half";
		else if(foutKruisImage.isVisible())
			goedFout = "fout";

		String antwoord = "";
		if (formuleMode)
			antwoord = formuleVak.toString();
		else
			antwoord = antwoordTF.getText();
		if (antwoord.equals(""))
			return;

		if (formuleMode)
		{
			String attemptFormuleString = FormuleParser.schoon(FormuleParser.formuleString(antwoord));
			attemptFormuleString = StringUtils.replaceStr(attemptFormuleString, "(0-", "(-");
			antwoord = FormuleParser.pel(attemptFormuleString);
		}
		String fbTekst = "";
		
		//if (feedbackTekst.isVisible() && feedbackTekst.getParent() != null)
		//	fbTekst = feedbackTekst.getText();

		String s = antwoord;
		s = s + "   ;   ";
		s = s + new Date().toString();
		s = s + "   ;   ";
		s = s + "Regelnummer = " + 0;
		s = s + "   ;   ";
		s = s + goedFout;
		s = s + "   ;   ";
		s = s + "score = " + score;
		s = s + "   ;   ";
		s = s + fbTekst;

		attempts.addElement(s);
		System.out.println(s);
	}
	
	public void wis()
	{
		goedKrulImage.setVisible(false);
	    goedKrulHalfImage.setVisible(false);
	    foutKruisImage.setVisible(false);

		correct = false;
		score = 0;
		nagekeken = false;
		ingevuld = false;

		attempts = new Vector();
	}
	
	private void zetGoedFout(int uitslag)
	{
		if (!check)
			return;
		
		goedKrulImage.setVisible(false);
		goedKrulHalfImage.setVisible(false);
		foutKruisImage.setVisible(false);
		//if(!ingevuld)
		//	return;
		if (uitslag == GEEN)
			return;
		if (uitslag == GOED)
			goedKrulImage.setVisible(true); 
		else if (uitslag == FOUT)
			foutKruisImage.setVisible(true);
		else if (uitslag == HALF)
			goedKrulHalfImage.setVisible(true);
	}
	
	public void kijkNa()
	{
		kijkNa(true);
	}

	public void kijkNa(boolean show)
	{
		checkAntwoord(show);
		
		if (formuleMode)
			ingevuld = !(formuleVak.toString() == null || formuleVak.toString().equals(""));
		else
			ingevuld = !(antwoordTF.getText() == null || antwoordTF.getText().equals(""));

		correct = false;
		fout = true;
		score = 0;

		if (hasFeedback)
		{	if (goedHalfFout == 0)
			{	if (show)
					zetGoedFout(GOED);
				score = puntenFeedback;
				correct = true;
				fout = false;
			}
			else if (goedHalfFout == 1)
			{	if (show)
					zetGoedFout(HALF);
				score = puntenFeedback;
				correct = false;
				fout = false;
			}
			else if (goedHalfFout == 2)
			{	if (show)
					zetGoedFout(FOUT);
				score = puntenFeedback;
				correct = false;
				fout = true;
			}
		}
		else
		{
			if (gelijkwaardig)
			{
				if (show)
					zetGoedFout(GOED);
				correct = true;
				fout = false;
				score = scoreMax;
			}
			else
			{
				if (show)
					zetGoedFout(FOUT);
				correct = false;
				fout = true;
				score = 0;
			}
		}

		//if (ingevuld && show && mode != -1)
		//	comRoot.setChanged();
	}
	
	public void checkAntwoord()
	{
		checkAntwoord(true);
	}

	public void checkAntwoord(boolean show)
	{
		if (hasFeedback)
		{	
			int aantalAnswerModels = answerModels.size();
			for (int h = 0; h < aantalAnswerModels; h++)
			{
				setAnswerModel(h);
				gelijkwaardig = false;
				for (int i = 0; i < juisteAntwoorden.length; i++)
				{
					String antw = antwoordTF.getText();
						
					if (formuleMode)
					{
						antw = formuleVak.toString();
						antw = "$f" + antw + "@";
						//antw = antw.substring(2, antw.length()-1);
					}
					
					
					antw = StringUtils.replaceStr(antw, " ", "");
					gelijkwaardig = gelijkwaardig || antw.equals(juisteAntwoorden[i]);

				}

				if (gelijkwaardig || h == aantalAnswerModels - 1)
				{
					if (!feedback.trim().equals("") && show)
					{
						setFeedback(feedback, true);
						feedbackButton.setVisible(true);
					}
					else
					{
						feedbackButton.setVisible(false);
						if (feedbackPanel.getParent() != null)
						{
							basisPanel.remove(feedbackPanel);
						}

					}
					break;
				}

			}
		}
		else
		{
			gelijkwaardig = false;
			for (int i = 0; i < juisteAntwoorden.length; i++)
			{
				String antw = antwoordTF.getText();
				if (formuleMode)
				{
					antw = formuleVak.toString();
					antw = "$f" + antw + "@";
					//antw = antw.substring(2, antw.length()-1);
				}
				antw = StringUtils.replaceStr(antw, " ", "");
				gelijkwaardig = gelijkwaardig || antw.equals(juisteAntwoorden[i]);
			}
		}
	}
	
	public void setFeedback(String feedback, boolean closeable)
	{	hasFeedback = !"".equals(feedback.trim());
		feedbackPanel.clear();
		feedbackPanel.getElement().setInnerHTML(feedback);
		feedbackPanel.getElement().getStyle().setPadding(10, Unit.PX);
	}

	/*
	public void setAndAddFeedback(String feedback)
	{
		hasFeedback = !"".equals(feedback.trim());
		feedbackPanel.clear();
		//basisPanel.remove(feedbackPanel);
		feedbackPanel.getElement().setInnerHTML(feedback);
		feedbackPanel.getElement().getStyle().setPadding(10, Unit.PX);
		if (hasFeedback)
		{	basisPanel.add(feedbackPanel);
		
		}
	}
	*/


	public boolean heeftFormuleInvoer()
	{
		return formuleMode;
	}

	@Override
	public int getScore() {
		if (!teltMee)
			return 0;
		return score;
	}


	@Override
	public boolean isCorrect() {
		if (!teltMee)
			return true;
		return correct;
	}



	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		
	}


	public void resize()
	{
		if(!formuleMode)
			return;
		
		breedte = formuleVak.getMainRegel().getWidth() + 18;
		hoogte = formuleVak.getMainRegel().getHeight() + 4;
		//System.out.println("nieuwe breedte: " + breedte);
		//System.out.println("nieuwe hoogte: " + hoogte);
		//nog zorgen dat hoogte altijd minimaal 24 is?
		basisPanel.setSize((breedte) + "px", (hoogte) + "px");
		ashoogte = formuleVak.getMainRegel().getAsHoogte() + 2;
		if(parentRegel != null)
			parentRegel.resize();
	}
	
	public void setParentRegel(TekstRegel regel)
	{
		parentRegel = regel;
	}
	
	public Panel getAsPanel()
	{
		return basisPanel;
	}
	
	public TouchPanel getTouchPanel()
	{
		return achtergrondPanel;
	}


	@Override
	public int getAsHoogte() {
		return ashoogte;
	}


	@Override
	public int getHeight() {
		return hoogte; 
	}


	@Override
	public int getWidth() {
		return breedte;
	}
	
	public void zetVolledigeBreedte(int breedte)
	{
		if(volledigeBreedte)
			this.breedte = breedte;
	}


	@Override
	public void setAsHoogte(int ashoogte) {
		this.ashoogte = ashoogte;
	}


	@Override
	public Widget asWidget()
	{
		return facade.wrap(getAsPanel());
	}
	
	public void setFontSize(int size)
	{
		if(formuleMode)
			formuleVak.setFont(FormuleFont.createFromFontSize(size));
		//else
		//	antwoordTF.getElement().getStyle().setFontSize(size, Style.Unit.PX);
	}
	
}
