package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.Collections;
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
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.TekstElement;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.DWOLogger;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
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
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.repr.ContentMathML;


public class AntwoordTekstVak2 implements InteractionView, FacetAware, TekstElementWithFont, CBookEventListener {

	private final class FormuleEditorVak extends FormuleEditor implements IsWidget {

		public Widget asWidget() {
			return getMainRegel().getCanvas();
		}

		void setEditable(boolean editable) {
			AntwoordTekstVak2.this.editable = editable;
			if(editable) {
				asWidget().getParent().getElement().getStyle().clearProperty("pointerEvents");
			} else {
				asWidget().getParent().getElement().getStyle().setProperty("pointerEvents", "none");

				// zorg dat de formule editor geen focus heeft
				if (getKeyboard() != null)
				{
					getKeyboard().setEditor(null);
					getKeyboard().blur();
				}

			}
		}
		
		@Override
		public void enter()
		{
			AntwoordTekstVak2.this.enter();
		}

		@Override
		public void addElement(FormuleElement e)
		{
			super.addElement(e);
			changed = true;
			resize();
			resetimg();
			
			if (nagekeken)
				zetIsVeranderdNaNakijken(true);
		}

		@Override
		public void removeCurrentElement()
		{
			super.removeCurrentElement();
			changed = true;
			resize();
			resetimg();
			
			if (nagekeken)
				zetIsVeranderdNaNakijken(true);
		}

		@Override
		public void removeNextElement()
		{
			super.removeNextElement();
			changed = true;
			resize();
			resetimg();
			
			if (nagekeken)
				zetIsVeranderdNaNakijken(true);
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

		/* (non-Javadoc)
		 * @see nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor#setCurrentElementRepaint()
		 */
		@Override
		public void setCurrentElementRepaint() {
			super.setCurrentElementRepaint();
			fireText();
		}

		@Override
		public void tab()
		{
			tabAntwoordTekstVak();
		}

		@Override
		public void shiftTab()
		{
			shiftTabAntwoordTekstVak();
		}
	}


	public static final String ACTION_CORRECT = "action.correct";
	public static final String ACTION_FALSE = "action.false";
	public static final String ACTION_FALSE2 = "action.false_2";
	public static final String ACTION_READONLY = "action.setNotEditable"; 

	private static final CBookEvent EVENT_CORRECT = new CBookEvent(ACTION_CORRECT); 
	private static final CBookEvent EVENT_FALSE = new CBookEvent(ACTION_FALSE); 
	private static final CBookEvent EVENT_FALSE2 = new CBookEvent(ACTION_FALSE2);
	private static final CBookEvent EVENT_READONLY = new CBookEvent(ACTION_READONLY);
	private static final String TEXT = "text";

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
	private boolean isVeranderdNaNakijken = false;

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
	
	private TextEditor antwoordTF;
	private FormuleEditorVak formuleVak;
	
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
	
	private boolean logOption, editable = true;
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
	

	public AntwoordTekstVak2(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
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
			if (map.containsKey("logObjectives"))
			{	
				//logObjectives = (boolean[][]) map.get("logObjectives"); 
				ObjectList list = map.getObjectList("logObjectives");
				logObjectives = new boolean[list.size()][];
				for (int i = 0; i < logObjectives.length; i++) {
					logObjectives[i]  = list.getBooleanArray(i);
				}
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
				if(map.containsKey("logIDLabel"))
					dwologger.setLogIDLabel(map.getString("logIDLabel"));
				dwologger.setLogObjectives(logObjectives);
				logging = dwologger;
			}
			if (map.containsKey("boxMetRand"))
				boxMetRand = map.getBoolean("boxMetRand");
			
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
		
		antwoordTF = new TextEditor(breedte-2, hoogte-3, boxMetRand) {

			@Override
			public void enter()
			{
				AntwoordTekstVak2.this.enter();
			}

			@Override
			public void tab()
			{
				AntwoordTekstVak2.this.tabAntwoordTekstVak();
			}

			@Override
			public void shiftTab()
			{
				AntwoordTekstVak2.this.shiftTabAntwoordTekstVak();
			}
			
			@Override
			public void insert(char charAt)
			{
				super.insert(charAt);
				changed = true;
				resetimg();
			}

			@Override
			public void removeCurrentElement()
			{
				super.removeCurrentElement();				
				changed = true;
				resetimg();
				
				if (nagekeken)
					zetIsVeranderdNaNakijken(true);
			}
			
			@Override
			public void removeNextElement()
			{
				super.removeNextElement();
				changed = true;
				resetimg();
				
				if (nagekeken)
					zetIsVeranderdNaNakijken(true);
			}
		};
//		if(boxMetRand)
//			antwoordTF.getElement().getStyle().setProperty("border", "1px solid gray");
//		else 
//		{	antwoordTF.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
//			antwoordTF.getElement().setAttribute("placeholder", "...");
//		}
//		
//		antwoordTF.setWidth((breedte - 4) + "px");
		
		//antwoordTF.setHeight((hoogte - 5) + "px");
		
//		antwoordTF.getElement().getStyle().setPaddingLeft(0, Style.Unit.PX);
//		antwoordTF.getElement().getStyle().setPaddingTop(0, Style.Unit.PX);
//		antwoordTF.getElement().getStyle().setPaddingBottom(1, Style.Unit.PX);
		//antwoordTF.getElement().getStyle().setPaddingBottom(2, Style.Unit.PX);
		//antwoordTF.getElement().getStyle().setPaddingBottom(0, Style.Unit.PX);
//		antwoordTF.getElement().getStyle().setPaddingRight(0, Style.Unit.PX);
		
		//antwoordTF.getElement().getStyle().setMarginRight(5, Style.Unit.PX);
//		antwoordTF.addKeyDownHandler(new KeyDownHandler() {
//			public void onKeyDown(KeyDownEvent event) 
//			{	
//				if (nagekeken)
//					zetIsVeranderdNaNakijken(true);
//
//				changed = true;
//				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) 
//		    	{	kijkNa();
//		    		setAttempt();
//		    		fireText();
//		    	}
//				if(event.getNativeKeyCode() == KeyCodes.KEY_TAB)
//				{
//					event.stopPropagation();
//					event.preventDefault();
//					if(event.isShiftKeyDown())
//						shiftTabAntwoordTekstVak();
//					else
//						tabAntwoordTekstVak();
//				}
//			}
//		});
//		
//		antwoordTF.addFocusHandler(new FocusHandler() {
//
//			@Override
//			public void onFocus(FocusEvent event) {
//				comRoot.getKeyboard().setEditor(null);
//				
//			}});
//		antwoordTF.addBlurHandler(new BlurHandler() {
//
//			@Override
//			public void onBlur(BlurEvent event) {
//				fireText();
//			}});
		
		//antwoordTF.setBorder(BorderFactory.createLineBorder(new Color(153, 153, 153)));
		//antwoordTF.setBounds(0, 0, 80, 21);
		//antwoordTF.addActionListener(this);
		
		formuleVak = new FormuleEditorVak() ;
		//hier toetsenbord aan vastmaken. WIM??
		formuleVak.setFormuleToolBijFocus(formuleToolBijFocus);
		//formuleVak.getMainRegel().setMinimumWidth(breedte - 20);
		formuleVak.getMainRegel().setMinimumWidth(breedte - 18);
		formuleVak.getMainRegel().setMinimumHeight(hoogte - 8);
		
		formuleVak.getMainRegel().zetStippels(!boxMetRand);
		//formuleVak.setFont(formuleVakFont);
		//formuleVak.setBorder(false);
		//formuleVak.addActionListener(this);
		//formuleVak.setLocation(4, 4);
		//addMouseListener(this);
		
		if (formuleMode) 
		{	//basisPanel.setSize(Math.max(minBreedte, formuleVak.getSize().width + 24), formuleVak.getSize().height + 8);
			achtergrondPanel = new TouchPanel();
			if(boxMetRand) {
				achtergrondPanel.getElement().getStyle().setBackgroundColor("white");
				achtergrondPanel.getElement().getStyle().setProperty("border", "1px solid gray");
			} else {
				achtergrondPanel.getElement().getStyle().setBackgroundColor("transparant");
				achtergrondPanel.getElement().getStyle().setProperty("border", "none");
			}
			basisPanel.add(achtergrondPanel);
			basisPanel.setWidgetLeftRight(achtergrondPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
			basisPanel.setWidgetTopBottom(achtergrondPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
			//basisPanel.setWidgetLeftRight(achtergrondPanel)
			//Panel panel = formuleVak.getAsPanel();
			achtergrondPanel.getElement().addClassName("insert_formule");
			achtergrondPanel.getElement().getStyle().setPaddingLeft(3, Style.Unit.PX);
			achtergrondPanel.getElement().getStyle().setPaddingTop(2, Style.Unit.PX);
			achtergrondPanel.add(formuleVak);
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
			ashoogte = Math.round(hoogte)/2 + 2;// /*antwoordTF.getOffsetHeight()*/ - 3;
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
	
	
	public String getText() {
		return formuleMode ? ("$f" + formuleVak.toString() + "@") : antwoordTF.getText();
	}
	
	
	private void fireText()
	{
		if (comRoot.hasListeners(TEXT))
		{
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("content", getText());
			if (logID != null)
				parameters.put("logID", logID);
			CBookEvent event = new CBookEvent(this, TEXT, parameters);
			comRoot.fireEvent(event);
		}
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
	
	public void requestFocus()
	{
		if(formuleMode)
			formuleVak.requestFocus();
		else
			antwoordTF.requestFocus(); //setFocus(true);
	}
	
	public HashMap<String, Object> getState()
	{
		boolean ingevuld = false;
		boolean nagekeken = false;
		boolean isVeranderdNaNakijken = false;
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
		isVeranderdNaNakijken = this.isVeranderdNaNakijken;
		antwoord = getText();
		attempts = this.attempts;
		attemptsCount = this.attemptsCount;
		errorCount = this.errorCount;

		if(logging instanceof DWOLogger) {
			((DWOLogger) logging).updateLog(buildLogParameters());
		}
		HashMap<String, Object> h = new HashMap<String, Object>();
		h.put("ingevuld", new Boolean(ingevuld));
		h.put("nagekeken", new Boolean(nagekeken));
		h.put("isVeranderdNaNakijken", new Boolean(isVeranderdNaNakijken));
		h.put("antwoord", antwoord);
		h.put("attempts", attempts);
		h.put("attemptsCount", new Integer(attemptsCount));
		h.put("errorCount", new Integer(errorCount));
		h.put("editable", Boolean.valueOf(editable));

		return h;
	}
	
	public void setState(HashMap<String, Object> h)
	{
		if(h == null) return; // setStateNull();
		boolean ingevuld = false;
		boolean nagekeken = false;
		boolean isVeranderdNaNakijken = false;
		String antwoord = "";
		List attempts = new Vector();
		int attemptsCount = 0;
		int errorCount = 0;

		ObjectMap map = JSONUtilities.wrapMap(h);
		PopupFacade.showReview(h, this);

		if (map.containsKey("ingevuld"))
			ingevuld = map.getBoolean("ingevuld");
		if (map.containsKey("nagekeken"))
			nagekeken = map.getBoolean("nagekeken");
		if (map.containsKey("isVeranderdNaNakijken"))
			isVeranderdNaNakijken = map.getBoolean("isVeranderdNaNakijken");
		if (map.containsKey("antwoord"))
			antwoord = map.getString("antwoord");
		if (map.containsKey("attempts"))
			attempts = map.getList("attempts"); // do not expect Vector, it might be Object[]
		if (map.containsKey("attemptsCount"))
			attemptsCount = map.getInt("attemptsCount");
		if (map.containsKey("errorCount"))
			errorCount = map.getInt("errorCount");

		this.ingevuld = ingevuld;
		this.nagekeken = nagekeken;
		this.isVeranderdNaNakijken = isVeranderdNaNakijken;
		this.attempts = new Vector(attempts);
		this.attemptsCount = attemptsCount;
		this.errorCount = errorCount;

		setText(antwoord);

		if (ingevuld && (mode == OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN || (nagekeken && !isVeranderdNaNakijken)))
			kijkNa(true, false);
		this.editable = map.getBoolean("editable", true);
		if(!editable) {
			if(formuleMode) {
				formuleVak.setEditable(editable);
			} else {
				antwoordTF.acceptCBookEvent(EVENT_READONLY);
			}
		}
	}


	public void setText(String antwoord) {
		if (formuleMode)
		{
			if(antwoord.startsWith("$f") && antwoord.endsWith("@")) // vanaf nu altijd!
				antwoord = antwoord.substring(2, antwoord.length()-1);
			formuleVak.clearAll();
			formuleVak.insert(antwoord);
		}
		else {
			antwoordTF.clearAll();
			antwoordTF.insert(antwoord);
		}
	}
	
	public void setAttempt()
	{
		setAttempt(false);
	}

	public void setAttempt(boolean start)
	{
		if(logOption) {
			Map<String, Object> log = buildLogParameters();
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


	private Map<String, Object> buildLogParameters() {
		Map<String, Object> log = new HashMap<String, Object>();
		if(goedKrulImage.isVisible())
			log.put("success", Boolean.TRUE);
		else if(foutKruisImage.isVisible())
			log.put("success", Boolean.FALSE);
		String response = "";
		if(formuleMode) {
			response = formuleVak.getMainRegel().toMathML();
		} else
			response = antwoordTF.getText();
		log.put("response", response);
		log.put("score", Collections.singletonMap("raw", score));
		return log;
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
	
	public void tabAntwoordTekstVak()
	{
		if(parentRegel != null)
		{
			parentRegel.getTekstVak().tabFocus(this, true);
		}
	}
	
	public void shiftTabAntwoordTekstVak()
	{
		
		if(parentRegel != null)
		{
			parentRegel.getTekstVak().shiftTabFocus(this, true);
		}
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
		// reset isVeranderdNaNakijken
		zetIsVeranderdNaNakijken(false);

		kijkNa(true, false);
	}

	/**
	 * 
	 * @param show
	 * @param setState
	 */
	private void kijkNa(boolean show, boolean setState)
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
				if (mode == OpdrNav.OEFENEN_STRAFPUNTEN)
					score = Math.max(0, puntenFeedback - errorCount * foutStraf);
				correct = Boolean.TRUE;
			}
			else if (goedHalfFout == 1)
			{	if (show)
					zetGoedFout(HALF);
				score = puntenFeedback;
				if (mode == OpdrNav.OEFENEN_STRAFPUNTEN)
					score = Math.max(0, puntenFeedback - errorCount * foutStraf);
				correct = null;

			}
			else if (goedHalfFout == 2)
			{	if (show)
					zetGoedFout(FOUT);
				score = puntenFeedback;
				if (mode == OpdrNav.OEFENEN_STRAFPUNTEN)
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
				if (mode == OpdrNav.OEFENEN_STRAFPUNTEN)
					score = Math.max(0, scoreMax - errorCount * foutStraf);
			}
			else
			{
				if (show && ingevuld) // bij een leeg antwoord geen kruis zetten
					zetGoedFout(FOUT);
				correct = Boolean.FALSE;
				score = 0;
				verhoogErrorCount();
			}
		}
		if (show && check && ingevuld && setState)
        {	
			comRoot.setChanged(teltMee && correct == Boolean.FALSE);
        }
		
		//if (ingevuld && show && mode != -1)
		//	comRoot.setChanged();
		
		// Voorkomen dat door een kijkNa() op de pagina, gevolgd door een comRoot.setChanged() en daarmee getState() van alle interactionviews op de pagina
		// ook van andere interactionviews de crosswidget-events worden getriggerd, terwijl er nog helemaal geen antwoord is.
		if (show) // alleen als er feedback moet worden geshowd
		{
			if (correct) 
				fireEvent(EVENT_CORRECT);
			if (!correct && errorCount > 1) 
				fireEvent(EVENT_FALSE2);
			if (!correct)
				fireEvent(EVENT_FALSE);
		}
	}

	private void fireEvent(CBookEvent event) 
	{
		DWOplayer.clientfactory.getEventBus().fireEventFromSource(event, this);
		comRoot.fireEvent(event);
	}

	public void verhoogErrorCount()
	{
		if(changed)
			errorCount++;
		changed = false;
	}
	
	public void zetNagekeken(boolean b)
	{
		if(ingevuld)
			nagekeken = b;
	}
	
	private void zetIsVeranderdNaNakijken(boolean b)
	{
		this.isVeranderdNaNakijken = b;
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
	
	public void tekenCursor()
	{
		if(formuleMode)
		{
			if(formuleVak.getCurrentElement() == null)
			{	formuleVak.setCurrentElementRepaint(formuleVak.getMainRegel());
			}
		}
	}
	
	public void enter()
	{
		setAttempt();
		fireText();
		
		if (mode == OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS)
		{
			return;
		}
		
		// reset isVeranderdNaNakijken
		zetIsVeranderdNaNakijken(false);

		kijkNa(true, true);
	}
	
	/**
	 * Reset het goed/fout-plaatje en verberg de feedback.
	 */
	void resetimg()
	{
		goedKrulImage.setVisible(false);
		goedKrulHalfImage.setVisible(false);
		foutKruisImage.setVisible(false);
		feedbackLabel.setVisible(false);
		feedbackPanel.hide();
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
		antwoordTF.comRoot = comRoot; // no actions
		this.comRoot = comRoot;
		mode = comRoot.getMode();
		if(logging != null) logging.setCommunicationRoot(comRoot);
		comRoot.addCBookEventListener(TEXT, this);
		comRoot.addCBookEventListener(ACTION_READONLY, this);
	}


	public void setParentRegel(TekstRegel regel)
	{
		parentRegel = regel;
		//antwoordTF.getElement().getStyle().setFontSize(parentRegel.getFont().getFontSize(), Style.Unit.PX);
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
			//antwoordTF.setWidth((breedte - 2) + "px");
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
	
	public boolean isPopup()
	{
		return facade.isPopup();
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
		else 
			antwoordTF.setFontSize(size);
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


	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if(TEXT.equals(event.getCommand())) 
		{
			String content = (String) event.getParameter("content");
			if(content == null) content = "";
			setText(content);
		} else if ( ACTION_READONLY.equals(event.getCommand())) {
			editable = false;
			if(formuleMode) {
				formuleVak.setEditable(false);
			} else {
				//antwoordTF
				antwoordTF.acceptCBookEvent(event);
			}
		}
		
	}

	/**
	 * voor tab/shifttab
	 * @return not editable
	 */
	public boolean isReadOnly() {
		return !editable;
	}
	
}
