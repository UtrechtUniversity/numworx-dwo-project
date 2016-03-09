package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;





































import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.TekstElement;
import nl.uu.fi.dwo.interaction.client.FacetAware.Type;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.DWOPlayerMC2;
import nl.uu.fi.dwo.mobile.client.sco.DWOLogger;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;
import nl.uu.fi.dwo.mobile.utils.StringUtils;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.repr.ContentMathML;


public class AntwoordTekstVak implements InteractionView, FacetAware, TekstElementWithFont {

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

	private Boolean correct;
	
	private int score;
	private int scoreMax = 10;
	private int errorCount;
	private int attemptsCount;
	private Vector attempts;
	private int foutStraf = 2;
	private boolean changed = false;

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
	Label feedbackLabel;
	private PopupPanel feedbackPanel;
	TekstVak feedbackTekst;
	Canvas feedbackSluitKnop;
	Context2d gIm;
	
	//private PopupFacade feedbackPopup;
	//private PopupButton feedbackButton;
	//private TekstArea feedbackTekst;
	//private FormuleButton feedbackButton;
	
	private boolean check;
	private boolean teltMee;
	
	Image goedKrulImage, foutKruisImage, goedKrulHalfImage;
	
	private boolean logOption;
	private String logID;
	private Logging logging;
	
	private boolean[][] logObjectives;
	
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
			if(logOption) {
				DWOLogger dwologger = new DWOLogger();
				dwologger.setMaxScore(scoreMax);
				dwologger.setClassName("fi.wiskopdr.AntwoordTekstVak");
				dwologger.setLogID(logID);
				logging = dwologger;
			}
			if (map.containsKey("boxMetRand"))
				boxMetRand = map.getBoolean("boxMetRand");
			if (map.containsKey("logObjectives"))
			{	
				//logObjectives = (boolean[][]) map.get("logObjectives"); 
				ObjectList list = map.getObjectList("logObjectives");
				logObjectives = new boolean[list.size()][];
				for (int i = 0; i < logObjectives.length; i++) {
					logObjectives[i]  = list.getBooleanArray(i);
				}
			}
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
			{	changed = true;
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) 
		    	{	kijkNa();
		    		setAttempt();
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
				if(comRoot != null)
					comRoot.setChanged(teltMee && Boolean.FALSE.equals(correct));
			}

			@Override
			public void addElement(FormuleElement e)
			{
				super.addElement(e);
				changed = true;
				resize();
				resetimg();
			}

			@Override
			public void removeCurrentElement()
			{
				super.removeCurrentElement();
				changed = true;
				resize();
				resetimg();
			}

			@Override
			public void removeNextElement()
			{
				super.removeNextElement();
				changed = true;
				resize();
				resetimg();
			}

			@Override
			public void insert(String text)
			{
				super.insert(text);
				changed = true;
				resize();
				resetimg();
			}

			@Override
			public boolean isInputNeeded() {
				return false;
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
				ashoogte = formuleVak.getMainRegel().getAsHoogte() + 3;
				if(parentRegel != null)
					parentRegel.resize();
			}
			
			void resetimg() {
				goedKrulImage.setVisible(false);
				goedKrulHalfImage.setVisible(false);
				foutKruisImage.setVisible(false);
				feedbackLabel.setVisible(false);
				feedbackPanel.hide();
				
			}
			
			
			
			
		} ;
		//hier toetsenbord aan vastmaken. WIM??
		formuleVak.setFormuleToolBijFocus(formuleToolBijFocus);
		//formuleVak.getMainRegel().setMinimumWidth(breedte - 20);
		formuleVak.getMainRegel().setMinimumWidth(breedte - 18);
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
			ashoogte = formuleVak.getMainRegel().getAsHoogte() + 3;
		}
		else
		{	//basisPanel.setSize(Math.max(minBreedte, antwoordTF.getSize().width + 2), antwoordTF.getSize().height + 4);
			basisPanel.add(antwoordTF);
			basisPanel.setWidgetLeftRight(antwoordTF, 0, Style.Unit.PX, 0, Style.Unit.PX);
			basisPanel.setWidgetTopBottom(antwoordTF, 0, Style.Unit.PX, 0, Style.Unit.PX);
			ashoogte = Math.round(hoogte) - 11;// /*antwoordTF.getOffsetHeight()*/ - 3;
		}
		
		//TODO: Noordhoff-instelling maken.
		goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		goedKrulHalfImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
		foutKruisImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
		
		basisPanel.add(goedKrulImage);
		basisPanel.add(goedKrulHalfImage);
		basisPanel.add(foutKruisImage);
		basisPanel.setWidgetRightWidth(goedKrulImage, 0, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(goedKrulImage, 0, Style.Unit.PX, 20, Style.Unit.PX);
		basisPanel.setWidgetRightWidth(goedKrulHalfImage, 0, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(goedKrulHalfImage, 0, Style.Unit.PX, 20, Style.Unit.PX);
		basisPanel.setWidgetRightWidth(foutKruisImage, 0, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(foutKruisImage, 0, Style.Unit.PX, 20, Style.Unit.PX);
		goedKrulImage.setVisible(false);
		goedKrulHalfImage.setVisible(false);
		foutKruisImage.setVisible(false);
		
		zetJuisteAntwoord(antwoordString);
		
		feedbackPanel = new PopupPanel(true);
		feedbackPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		feedbackPanel.getElement().getStyle().setBorderColor("black");
		feedbackPanel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		feedbackPanel.getElement().getStyle().setPadding(2, Style.Unit.PX);
		feedbackPanel.getElement().getStyle().setBackgroundColor("#FFFFDD");
		
		feedbackTekst = new TekstVak();
		feedbackTekst.setSize(200, 50);
		feedbackTekst.setFontSize(XMLView.getDefaultFontSize());
		feedbackTekst.setFontName(XMLView.getDefaultFontName());
		feedbackTekst.setColor(CssColor.make("black"));
		feedbackTekst.setCentering(false, true);
		feedbackTekst.setPasHoogteBreedteAan(true, false);
		feedbackTekst.setTekstVakBreedte(190);
		feedbackPanel.add(feedbackTekst);
		
		feedbackSluitKnop = Canvas.createIfSupported();
		gIm = feedbackSluitKnop.getContext2d();
		
		feedbackSluitKnop.setWidth(10 + "px");
		feedbackSluitKnop.setHeight(10 + "px");
		feedbackSluitKnop.setCoordinateSpaceWidth(10);
		feedbackSluitKnop.setCoordinateSpaceHeight(10);
		
		CanvasGradient gradient = gIm.createLinearGradient(0, 0, 10, 10);
		gradient.addColorStop(0, CssColor.make(242, 242, 242).toString());
		gradient.addColorStop(1, CssColor.make(221, 221, 221).toString());
		gIm.setFillStyle(gradient);
		//gIm.setFillStyle(CssColor.make(245, 245, 245).toString());
		gIm.fillRect(0, 0, 10, 10);
		gIm.setStrokeStyle("black");
		gIm.beginPath();
		gIm.moveTo(1, 1);
		gIm.lineTo(9, 9);
		gIm.moveTo(1, 9);
		gIm.lineTo(9, 1);
		gIm.stroke();
		
		feedbackSluitKnop.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		feedbackSluitKnop.getElement().getStyle().setProperty("verticalAlign", "top");
		voegFeedbackSluitKnopToe();
		
		
		feedbackSluitKnop.addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				feedbackPanel.hide();
			}
		}, ClickEvent.getType());
					
		feedbackLabel = new Label("?");
		feedbackLabel.getElement().getStyle().setFontSize(11, Style.Unit.PX);
		feedbackLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		feedbackLabel.getElement().getStyle().setPadding(0, Style.Unit.PX);
		feedbackLabel.getElement().getStyle().setMarginTop(0, Style.Unit.PX);
		feedbackLabel.getElement().getStyle().setMarginLeft(3, Style.Unit.PX);
		feedbackLabel.getElement().getStyle().setPaddingLeft(4, Style.Unit.PX);
		feedbackLabel.getElement().getStyle().setBackgroundColor(CssColor.make(230, 230, 230).toString());
		feedbackLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		feedbackLabel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
		feedbackLabel.setWidth(10 + "px");
		feedbackLabel.setVisible(false);
		
		feedbackLabel.addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				feedbackPanel.setPopupPosition(asWidget().getAbsoluteLeft() + 10, asWidget().getAbsoluteTop() + asWidget().getOffsetHeight() + 10);
				feedbackPanel.show();
			}
		}, ClickEvent.getType());
		basisPanel.add(feedbackLabel);
		basisPanel.setWidgetRightWidth(feedbackLabel, 2, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetBottomHeight(feedbackLabel, 1, Style.Unit.PX, 10, Style.Unit.PX);
		
		
	}
	
	public void voegFeedbackSluitKnopToe()
	{
		feedbackTekst.add(feedbackSluitKnop);
		feedbackTekst.setWidgetRightWidth(feedbackSluitKnop, 0, Style.Unit.PX, 10, Style.Unit.PX);
		feedbackTekst.setWidgetTopHeight(feedbackSluitKnop, 0, Style.Unit.PX, 10, Style.Unit.PX);
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

//		if (this.ingevuld && (mode == 0 || mode == 1 || this.nagekeken))
//			kijkNa();
//		else
		kijkNa(false, false);

		ingevuld = this.ingevuld;
		nagekeken = this.nagekeken;
		if (formuleMode)
			antwoord = "$f" + formuleVak.toString() + "@"; // Wiskopdr heeft $f ... @
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
		List attempts = new Vector();
		int attemptsCount = 0;
		int errorCount = 0;

		if (h.containsKey("ingevuld"))
			ingevuld = ((Boolean) h.get("ingevuld")).booleanValue();
		if (h.containsKey("nagekeken"))
			nagekeken = ((Boolean) h.get("nagekeken")).booleanValue();
		if (h.containsKey("antwoord"))
			antwoord = (String) h.get("antwoord");
		if (h.containsKey("attempts"))
			attempts = JSONUtilities.toArrayList( h.get("attempts") ); // do not expect Vector, it might be Object[]
		if (h.containsKey("attemptsCount"))
			attemptsCount = ((Number) h.get("attemptsCount")).intValue();
		if (h.containsKey("errorCount"))
			errorCount = ((Number) h.get("errorCount")).intValue();

		this.ingevuld = ingevuld;
		this.nagekeken = nagekeken;
		this.attempts = new Vector(attempts);
		this.attemptsCount = attemptsCount;
		this.errorCount = errorCount;

		if (formuleMode)
		{
			if(antwoord.startsWith("$f") && antwoord.endsWith("@")) // vanaf nu altijd!
				antwoord = antwoord.substring(2, antwoord.length()-1);
			formuleVak.insert(antwoord);
		}
		else
			antwoordTF.setText(antwoord);

		if (ingevuld && (mode == 0 || mode == 1 || nagekeken))
			kijkNa(true, false);
	}
	
	public void setAttempt()
	{
		setAttempt(false);
	}

	public void setAttempt(boolean start)
	{
		if(logOption) {
			Map log = new HashMap();
			if(goedKrulImage.isVisible())
				log.put("success", Boolean.TRUE);
			else if(foutKruisImage.isVisible())
				log.put("sucesss", Boolean.FALSE);
			String response = "";
			log.put("step", "0");
			if(formuleMode) {
				response = formuleVak.getMainRegel().toMathML();
			} else
				response = antwoordTF.getText();
			log.put("response", response);
			log.put("score", Collections.singletonMap("raw", score));
// TODO feedback		
			logging.log(log);
		}
//		String goedFout = "";
//		if(goedKrulImage.isVisible())
//			goedFout = "goed";
//		else if(goedKrulHalfImage.isVisible())
//			goedFout = "half";
//		else if(foutKruisImage.isVisible())
//			goedFout = "fout";
//
//		String antwoord = "";
//		if (formuleMode)
//			antwoord = formuleVak.toString();
//		else
//			antwoord = antwoordTF.getText();
//		if (antwoord.equals(""))
//			return;
//
//		if (formuleMode)
//		{
//			String attemptFormuleString = FormuleParser.schoon(FormuleParser.formuleString(antwoord));
//			attemptFormuleString = StringUtils.replaceStr(attemptFormuleString, "(0-", "(-");
//			antwoord = FormuleParser.pel(attemptFormuleString);
//		}
//		String fbTekst = "";
//		
//		//if (feedbackTekst.isVisible() && feedbackTekst.getParent() != null)
//		//	fbTekst = feedbackTekst.getText();
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
		kijkNa(true, true);
	}

	private void kijkNa(boolean show, boolean up)
	{
		checkAntwoord(show);
		
		if (formuleMode)
			ingevuld = !(formuleVak.toString() == null || formuleVak.toString().equals(""));
		else
			ingevuld = !(antwoordTF.getText() == null || antwoordTF.getText().equals(""));

		correct = null;
		score = 0;

		if (hasFeedback)
		{	if (goedHalfFout == 0)
			{	if (show)
					zetGoedFout(GOED);
				score = puntenFeedback;
				if(mode == 1)
					score = Math.max(0, puntenFeedback - errorCount * foutStraf);
				correct = Boolean.TRUE;
			}
			else if (goedHalfFout == 1)
			{	if (show)
					zetGoedFout(HALF);
				score = puntenFeedback;
				if(mode == 1)
					score = Math.max(0, puntenFeedback - errorCount * foutStraf);
				correct = null;

			}
			else if (goedHalfFout == 2)
			{	if (show)
					zetGoedFout(FOUT);
				score = puntenFeedback;
				if(mode == 1)
					score = Math.max(0, puntenFeedback - errorCount * foutStraf);
				correct = Boolean.FALSE;
				verhoogErrorCount();
			}
		}
		else
		{
			if (gelijkwaardig)
			{
				if (show)
					zetGoedFout(GOED);
				correct = Boolean.TRUE;
				score = scoreMax;
				if(mode == 1)
					score = Math.max(0, scoreMax - errorCount * foutStraf);
			}
			else
			{
				if (show)
					zetGoedFout(FOUT);
				correct = Boolean.FALSE;
				score = 0;
				verhoogErrorCount();
			}
		}
		if(show && check && ingevuld && up)
        {	comRoot.setChanged(teltMee && correct == Boolean.FALSE);
        }
		
		//if (ingevuld && show && mode != -1)
		//	comRoot.setChanged();
	}
	
	public void verhoogErrorCount()
	{
		System.out.println("verhoogErrorCount antwoordtekstvak");
		if(changed)
		{	errorCount++;
			System.out.println("errorCount antwoordtekstvak verhoogd naar " + errorCount);
		}
		changed = false;
	}
	
	public void zetNagekeken(boolean b)
	{
		if(ingevuld)
			nagekeken = b;
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
						//feedbackButton.setVisible(true);
					}
					else
					{
						//feedbackButton.setVisible(false);
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
	{	TekstBuffer b = new TekstBuffer();
		try{
			feedback = FormuleParser.randomizeTekstVakString(feedback, randomVarNamen, randomVarWaarden);
		}
		catch(Exception e){}
		ArrayList<Object> feedbackList = b.convertTekst(feedback, null, false);
		feedbackTekst.clear();
		int tekstVakBreedte = 190;
		for(int i = 0; i < feedbackList.size(); i++)
		{
			Object object = feedbackList.get(i);
			if(object instanceof TekstElement && ((TekstElement) object).getWidth() > tekstVakBreedte)
				tekstVakBreedte = ((TekstElement) object).getWidth();
		}
		feedbackTekst.setSize(tekstVakBreedte + 10, 50);
		feedbackTekst.setTekstVakBreedte(tekstVakBreedte);
		feedbackTekst.setObjects(feedbackList);
		voegFeedbackSluitKnopToe();
		feedbackTekst.resize();
		feedbackLabel.setVisible(true);
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
	public Boolean isCorrect() {
		if (!teltMee)
			return Boolean.TRUE;
		return correct;
	}



	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		mode = comRoot.getMode();
		if(logging != null) logging.setCommunicationRoot(comRoot);
	}


	public void setParentRegel(TekstRegel regel)
	{
		parentRegel = regel;
		antwoordTF.getElement().getStyle().setFontSize(parentRegel.getFont().getFontSize(), Style.Unit.PX);
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
	{	if(volledigeBreedte)
		{	this.breedte = breedte;
			basisPanel.setPixelSize(breedte, hoogte - 3);
			antwoordTF.setWidth((breedte - 2) + "px");
		}
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
		{
			FormuleFont fnt = FormuleFont.createFromFontSize(size);
			formuleVak.setFont(fnt);
			formuleVak.setDefaultFont(fnt);
		}
		//else
		//	antwoordTF.getElement().getStyle().setFontSize(size, Style.Unit.PX);
	}


	@Override
	public void getResponses(List<String> responses) {
		String antwoord;
		if(formuleMode)
		{
			antwoord = formuleVak.toString();
			String useranswer = "$f" + antwoord + "@";
			Expressie expr = FormuleParser.geefExpressie(useranswer);
			if(expr != null) 
			{
					antwoord = expr.visit(ContentMathML.INSTANCE).toString();
			} // antwoord = "Presentation MathML" ?
		}
		else
			antwoord = antwoordTF.getText();
		responses.add(antwoord);
	}


	@Override
	public int[][] getScoreObjectives() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setFontName(String font_name) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setFontStyle(int font_style) {
		// TODO Auto-generated method stub
		
	}
	
}
