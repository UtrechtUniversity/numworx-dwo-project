package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.FacetHelper;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.StateLess;
import nl.uu.fi.dwo.interaction.client.TekstElement;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.utils.AutoHidePopupPanel;
import nl.uu.fi.dwo.mobile.utils.ImageUtils;
import nl.uu.fi.dwo.mobile.utils.LaTransport;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.AntwoordFormuleVakChecker;
import fi.wiskopdr.AntwoordVakChecker;
import fi.wiskopdr.AntwoordVergelijkingVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.expressies.repr.ContentMathML;
import fi.wiskopdr.text.Text;

/**
 * Checks inserted formule with the correct answer
 * 
 * @author Danny Hendrix, Evertson Croes
 * 
 */
public class FormuleEditorWithAnswer extends FormuleEditor implements InteractionView, CBookEventListener, FacetAware
{
	private int extraWidth = 23 ; // of 43; breedte voor nakijkplaatje en als nodig voor knop voor uitklappen.
	
	
	class FormuleEditorPopup extends FormuleEditorWithSteps implements CBookEventListener, StateLess {

		public FormuleEditorPopup(HashMap<String, Object> h,
				boolean isVergelijkingVak, String[] randomVarNamen,
				HashMap randomVarWaarden) {
			super(h, isVergelijkingVak, randomVarNamen, randomVarWaarden, null);
		}
		
		public FormuleEditorPopup(HashMap<String, Object> h, boolean isVergelijkingVak, AntwoordVakChecker avChecker)
		{
			super(h, isVergelijkingVak, randomVarNamen, randomVarWaarden, avChecker);
		}

//		@Override
//		public void kijkNa() {
//			super.kijkNa();
//			String string = getEditor().toString();
//			transfer(string);
//		}

		boolean transfer;
		void transfer(String string) {
			logger.fine("userstring = " + string);
			FormuleEditorWithAnswer other = FormuleEditorWithAnswer.this;
			other.clearMain();
			other.insert(string);
			other.processAntwoord();
		}

//		@Override
//		public void lastStep(String useranswer) {
//			super.lastStep(useranswer);
//			//transfer(useranswer);
//		}

//		@Override
//		public void addStep(String useranswer) {
//			super.addStep(useranswer);
//			//transfer(useranswer);
//		}

		@Override
		FormuleEditorWithAnswer editorInstance() {
			return new FormuleEditorWithAnswer(super.h, isVergelijkingVak, this, randomVarNamen, randomVarWaarden, avChecker)
			{
				@Override
				public void enter() {
					super.enter();
					transfer(toString());
				}

				@Override // Er wordt in backstep om focus gevraagd, bij transfer is dit niet nodig. XXX hoe moet dit netter worden gemaakt.
				public void requestFocus() {
					if(!transfer)
						super.requestFocus();
				}
				
				
			};
		}

		@Override
		public void acceptCBookEvent(CBookEvent event) {
			if(TekstVakPanel.TVP_KLAPUIT == event.getCommand())
			{
				FormuleEditor other = FormuleEditorWithAnswer.this;
				String useranswer = other.toString();
				getEditor().clearMain();
				getEditor().insert(useranswer);
				getEditor().requestFocus();
			}
			if(TekstVakPanel.TVP_KLAPIN == event.getCommand())
			{
				FormuleEditorWithAnswer other = FormuleEditorWithAnswer.this;
				String useranswer = getEditor().toString();
				other.clearMain();
				other.insert(useranswer);
				boolean show = mode == OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN;
				other.kijkNa(false, show, false);
			
			}
		}

		void setHeight(double hoogte) {
			logger.fine("setHeight(" + hoogte + ")");
			super.setHeight((int)hoogte);
		}

		@Override
		void fire(String command, String message) {
		}

		
	}
	
	
	private static final String ANTWOORD_STRING = "antwoordString";
	private final static Logger logger = Logger.getLogger("FormuleEditorWithAnswer");
	OpdrNavIF comRoot;
	TouchPanel sp = null;
	//FlowPanel prefixPanel = null;
	private Image checkimg;
	Label feedbackLabel;
	AutoHidePopupPanel feedbackPanel;
	TekstVak feedbackTekst;
	Canvas feedbackSluitKnop;
	Context2d gIm;
	TouchPanel checkPanel;
	private ObjectMap launchState;
	private FormuleEditorWithSteps fe = null;
	private boolean strict = true;
	private ObjectMap instellingen = null;
	private int score = 0;
	private Boolean correct = null;
	
	//private Expressie substitutie;
	private String feedback = "";
	private boolean hasFeedback = false;
	private int scoreMax = 0;
	private boolean ingevuld = false;
	private boolean nagekeken = false;
	
	private boolean[][] logObjectives;
	
	private boolean check = true;
	private boolean teltMee = true;
	private boolean syntaxFout = false;
	private int breedte;
	private int hoogte;
	private boolean volledigeBreedte;
	private String[] randomVarNamen = null;
	private HashMap randomVarWaarden = null;
	private AntwoordVakChecker avChecker = null;
	private PopupFacade facade;
	private int mode;
	private boolean vakUitwerking;
	private boolean boxMetRand = true;
	private int goedHalfFout = AntwoordVakChecker.FOUT;
	private FacetHelper facet;
	private Logging logging;
	
	private TekstRegel parentRegel;
	private FormuleEditorPopup fews;
	
	private static boolean fontOvererving = false;
	
	public static void zetFontOverervingForm(boolean b)
	{	fontOvererving = b;
	}
	
//	public FormuleEditorWithAnswer(HashMap<String, Object> h, boolean isVergelijkingVak, FormuleEditorWithSteps fe, AntwoordVakChecker avChecker)
//	{
//		this(h, isVergelijkingVak, fe, null, null, avChecker);
//	}
	
	public FormuleEditorWithAnswer(HashMap<String, Object> h, boolean isVergelijkingVak, FormuleEditorWithSteps fe, String[] randomVarNamen, HashMap<String, Object> randomVarWaarden, AntwoordVakChecker avChecker)
	{
		super();
		//getMainRegel().setEditorParent(this);
		//getMainRegel().setDefaultHeight(24);

		//this.randomVarNamen = randomVarNamen;
		//this.randomVarWaarden = randomVarWaarden;
		this.isVergelijkingVak = isVergelijkingVak;

		if (fe != null)
		{
			this.fe = fe;
		}
		facade = new PopupFacade(h);
		sp = new TouchPanel();
		
		if(h == null)
			return;
		if (h.containsKey("interactiePanelLaunchState") )
		{
			ObjectMap map = JSONUtilities.wrapMap(h);
			facet = new FacetHelper(map);
			this.breedte = map.getInt("breedte");
			this.hoogte = map.getInt("hoogte");
			this.volledigeBreedte = map.getBoolean("volledigeBreedte");
			
			//this.hoogte = map.getInt("hoogte");
			//int breedte = ((Number) h.get("breedte")).intValue();
			//System.out.println("breedte formuleEditorWithAnswer: " + breedte);
			launchState = map.getObjectMap("interactiePanelLaunchState");
			if(avChecker == null)
			{
				if (isVergelijkingVak)
					this.avChecker = new AntwoordVergelijkingVakChecker((HashMap<String, Object>) launchState, randomVarNamen, randomVarWaarden);
				else
					this.avChecker = new AntwoordFormuleVakChecker((HashMap<String, Object>) launchState, randomVarNamen, randomVarWaarden);
			}
			else
				this.avChecker = avChecker;
			
			if(launchState != null) {
				if(launchState.containsKey("check") )
				{
					check = launchState.getBoolean("check");
				}
				if(launchState.containsKey("teltMee"))
				{
					teltMee = launchState.getBoolean("teltMee");
				}
				if(launchState.getBoolean("logOption", false)) {
					logging = DWOplayer.PARAMETERS.getLogging();
					logging.setLogID( launchState.getString("logID"));
					logging.setClassName("fi.wiskOpdr.SimpelAntwoordFormuleVak");
				}
				
				if(launchState.containsKey("formuleToolBijFocus"))
					setFormuleToolBijFocus(launchState.getBoolean("formuleToolBijFocus"));
			
				if(launchState.containsKey("boxMetRand"))
					boxMetRand = launchState.getBoolean("boxMetRand");
				if(fe == null && launchState.containsKey("uitw")) 
				{
					vakUitwerking = launchState.getBoolean("uitw");
					//logger.fine("vakuitwerking = " + vakUitwerking);
					if (vakUitwerking)
					{
						HashMap<String, Object> hh = new HashMap<String,Object>();
						hh.put("volledigeBreedte", Boolean.TRUE);
						hh.put("breedte", breedte);
						hh.put("hoogte" , 250); // FIXME wat is hier de goede hoogte?
						hh.put("breedte", 300); // FIXME wat is hier de goede breedte?
						HashMap ll = new HashMap();
						hh.put("interactiePanelLaunchState", launchState);
						
						fews = new FormuleEditorPopup(hh,isVergelijkingVak,this.avChecker);
					}
				}
				if(launchState.containsKey("hasFeedback"))
				{	hasFeedback = launchState.getBoolean("hasFeedback");
				}
				if(launchState.containsKey("logObjectives"))
				{	ObjectList logObjectivesList = ( launchState.getObjectList("logObjectives") );
					logObjectives = new boolean[logObjectivesList.size()][];
					for(int i = 0; i < logObjectivesList.size(); i++)
					{	logObjectives[i] = logObjectivesList.getBooleanArray(i);
					}
				}
				
			}
		
			checkimg = new Image(FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
			checkimg.setVisible(false);
			lastanswer = null;
			checkimg.getElement().getStyle().setProperty("marginLeft", "0px");
			checkimg.getElement().getStyle().setProperty("marginRight", "0px");
			checkimg.getElement().getStyle().setProperty("marginTop", "-2px"); 
			checkimg.getElement().getStyle().setProperty("marginBottom", "-7px");
			
			feedbackPanel = new AutoHidePopupPanel(true);
			PopupFacade.addPopup(feedbackPanel);
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
			feedbackLabel.getElement().getStyle().setMarginTop(-1, Style.Unit.PX);
			feedbackLabel.getElement().getStyle().setMarginLeft(1, Style.Unit.PX);
			feedbackLabel.getElement().getStyle().setPaddingLeft(2, Style.Unit.PX);
			//feedbackLabel.getElement().getStyle().setBackgroundColor("white");
			feedbackLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			feedbackLabel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
			feedbackLabel.setWidth(10 + "px");
			feedbackLabel.setVisible(false);
			
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
					if(feedbackLabel.isVisible())
					{	int yPos = asWidget().getAbsoluteTop() + asWidget().getOffsetHeight() + 10;
						if(yPos + feedbackTekst.hoogte + 10 > Window.getClientHeight())
							yPos = Window.getClientHeight() - feedbackTekst.hoogte - 10;
						
						feedbackPanel.setPopupPosition(asWidget().getAbsoluteLeft() + 10, yPos);
						feedbackPanel.show();
					}
				}
				
			});
			
			
			
//			checkPanel.addDomHandler(new ClickHandler(){
//				public void onClick(ClickEvent e)
//				{
//					if(feedbackLabel.isVisible())
//					{	feedbackPanel.setPopupPosition(asWidget().getAbsoluteLeft() + 10, asWidget().getAbsoluteTop() + asWidget().getOffsetHeight() + 10);
//						feedbackPanel.show();
//					}
//				}
//			}, ClickEvent.getType());
//			
			
			if (fe == null)
			{
				//sp.getElement().getStyle().setProperty("width", (breedte - 9) + "px");
				//this.getMainRegel().setMinimumWidth(breedte - extraWidth);
				//hoogte = 27;
				this.getMainRegel().setMinimumWidth(breedte - 20);
				this.getMainRegel().setMinimumHeight(hoogte - 6);
				
				//Maat zetten:
				//breedte - 3: 1 pixel marge links, 1 pixel rand links, 1 pixel rand rechts
				//hoogte - 6: 2 pixels marge boven, 2 pixels marge onder, 1 pixel rand boven, 1 pixel rand onder.

				//sp.setSize((breedte - 3) + "px", (hoogte - 6) + "px"); 

				//sp.setPixelSize((breedte - 3) , (hoogte - 6) ); 

				//sp.getElement().getStyle().setBackgroundColor(CssColor.make(255, 0, 0).toString());
				Style spStyle = sp.getElement().getStyle();
				if(boxMetRand)
				{
					spStyle.setBackgroundColor("white");
					spStyle.setProperty("border", "1px solid gray");
				}
				else
				{
					spStyle.setBorderStyle(Style.BorderStyle.NONE);
					//spStyle.setProperty("borderBottom", "thin dotted");
					//this.getMainRegel().vulVak("...");
					this.getMainRegel().zetStippels(true);
					
					//this.getMainRegel().paintObject();
					spStyle.setProperty("background", "none");
				}
				//sp.getElement().getStyle().setPadding(3, Style.Unit.PX);
				
				spStyle.setMarginLeft(1, Style.Unit.PX);
				//sp.getElement().getStyle().setMarginRight(1, Style.Unit.PX);
				//sp.getElement().getStyle().setMarginRight(1, Style.Unit.PX);

				
				//probeersels 9-1
				spStyle.setMarginTop(0, Style.Unit.PX);
				spStyle.setMarginBottom(2, Style.Unit.PX);
				spStyle.setPaddingTop(1, Style.Unit.PX);
				spStyle.setPaddingBottom(2,Style.Unit.PX);
				
				
				//oorspronkelijke versie 9-1: volgende twee regels.
			//	sp.getElement().getStyle().setMarginTop(2, Style.Unit.PX);
			//	sp.getElement().getStyle().setPaddingTop(3, Style.Unit.PX);
				

				//spStyle.setMarginTop(2, Style.Unit.PX);
				//spStyle.setPaddingTop(1, Style.Unit.PX);

				//sp.getElement().getStyle().setMarginBottom(0, Style.Unit.PX);
				
				//sp.getElement().getStyle().setPaddingLeft(1, Style.Unit.PX);
				//sp.getElement().getStyle().setPaddingRight(0, Style.Unit.PX);
				//sp.getElement().getStyle().setPaddingTop(1, Style.Unit.PX);
				//sp.getElement().getStyle().setPaddingBottom(5, Style.Unit.PX);
				//(Weggehaald Sietske) sp.getElement().getStyle().setProperty("backgroundColor", "#e9e9e9");
				
				//sp.getElement().getStyle().setProperty("backgroundColor", "yellow");
				//this.getMainRegel().getCanvas().getElement().getStyle().setProperty("marginTop", "3px");
				//this.getMainRegel().getCanvas().getElement().getStyle().setProperty("marginBottom", "0px");
			}

			//sp.getElement().addClassName("insert_formule");
			//sp.add(this.getMainRegel().getCanvas());
			//sp.add(checkimg);
			//sp.add(feedbackLabel);
			//sp.add(contentPanel);
			//sp.add(prefixPanel);
			
			sp.add(this.getMainRegel().getCanvas());
			sp.add(checkPanel);
			
			//checkPanel.getElement().getStyle().setBackgroundColor("red");
			//prefixPanel.getElement().getStyle().setBackgroundColor("yellow");
			//this.getMainRegel().getCanvas().getElement().getStyle().setBackgroundColor("blue");
			sp.addTouchHandler(new FormuleEditorTouchHandler(this));
			
		}
	}

	public void zetInstellingen(ObjectMap instellingen2)
	{
		this.instellingen = instellingen2;
		//System.out.println("fontSize uit instellingen formuleEditorWithAnswer: " + ((Number) instellingen.get("fontSize")).intValue());
		setFont(FormuleFont.createFromFontSize(instellingen2.getInt("fontSize")));

	}
	
	public void voegFeedbackSluitKnopToe()
	{
		feedbackTekst.add(feedbackSluitKnop);
		feedbackTekst.setWidgetRightWidth(feedbackSluitKnop, 0, Style.Unit.PX, 10, Style.Unit.PX);
		feedbackTekst.setWidgetTopHeight(feedbackSluitKnop, 0, Style.Unit.PX, 10, Style.Unit.PX);
	}

//	public Object getFe()
//	{
//		return fe;
//	}

	// !(holder instanceof FormuleEditorWithAnswer && ((FormuleEditorWithAnswer)holder).getFe()==null)
	public boolean isInputNeeded() {
		return fe != null;
	}
	
	public void setParentRegel(TekstRegel regel)
	{
		parentRegel = regel;
		if(fontOvererving)
		{	FormuleFont font = FormuleFont.createFromFontSize(parentRegel.getFont().getFontSize(), false);
			if(!FormuleFont.formTimes)
				font.setFont(parentRegel.getFont().getFont());
			setFont(font);
		}
				
		
	}
	
	@Override
	public void addElement(FormuleElement e)
	{
		super.addElement(e);
		resetimg();
		resize();
	}

	@Override
	public void removeCurrentElement()
	{
		super.removeCurrentElement();
		resetimg();
		resize();
		
	}

	@Override
	public void removeNextElement()
	{
		super.removeNextElement();
		resetimg();
		resize();
	}

	@Override
	public void insert(String text)
	{
		super.insert(text);
		resetimg();
		resize();
	}

	void resetimg() {
		checkimg.setVisible(false);
		zetFeedbackZichtbaar(false);
		feedbackPanel.hide();
		lastanswer = null;
	}
	
	public void zetFeedbackZichtbaar(boolean b)
	{
		feedbackLabel.setVisible(b);
		if(b)
			checkPanel.getElement().getStyle().setCursor(Cursor.POINTER);
		else
			checkPanel.getElement().getStyle().setCursor(Cursor.DEFAULT);
	}
	
//	public void setimg(String answer)
//	{
//		checkimg.setVisible(true);
//		lastanswer = answer;
//	}

	@Override 
	public void enter() {
		
		if(!this.toString().equals(""))
			ingevuld = true;
		else
			ingevuld = false;
		
		//Onderstaand if-statement was weggehaald, maar is nodig bij zelftoets en eindtoets, lijkt me.
		//Maar waarschijnlijk wil ik toch ook in het geval van zelftoets en eindtoets door kijkna heen, om syntaxfouten te onderscheppen.
		/*
		if(mode == OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS)
		{
			if(this.fe != null && ingevuld)
				fe.maakNakijkenAf(false);
			return;
		}
		*/
		if(fews != null)
		{
			fews.transfer = true;
			//doen alsof het in de laatste regel van de fews is ingevuld; dan komt het automatisch terug naar de fewa.
			if(fews.getEditor() == null || fews.getEditor().toString().equals(""))
				fews.backStep(false);
			fews.getEditor().clearAll();
			fews.getEditor().insert(this.toString());
			fews.getEditor().enter();
			fews.transfer = false;
			return;
		}
		else
			processAntwoord();
	}
	
	private void processAntwoord() {
		new Runnable() {
			public void run() {
				try {
					processAntwoord0();
				} catch(RestartException e) {
					e.restart(this);
				}
			}
		}.run();
	}
	
	private void processAntwoord0() throws RestartException
	{
		if(mode == OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS)
			kijkNa0(false, false, false);
		else
			kijkNa0(false, true, false);
		if(comRoot != null) // alleen niet null als fewa een toplevel is.
		{
			comRoot.fireEvent(new CBookEvent(this, "input", toString()));
			if(logging != null) {
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("response", "<math xmlns='http://www.w3.org/1998/Math/MathML'>" + getMainRegel().toMathML() + "</math>");
				map.put("score", Collections.singletonMap("raw", getScore()));
				if(correct != null) {
					map.put("success", correct);
				}
				logging.log(map);
			}
		}
		else if( fe != null) {
			fe.fire("input", toString());
		}
	}
	
	public void haalAntwoordOp() 
	{
		if(fews != null && fews.getEditor() != null && !fews.getEditor().toString().equals(""))
		{
			clearMain();
			insert(fews.getEditor().toString());
		}
	}
	
	public void kijkNa()
	{
		kijkNa(false);
	}
	
	public void kijkNa(boolean setState)
	{
		kijkNa(false, true, setState);
	}
	
	
	
	private String lastanswer = "$f@";
	private boolean isVergelijkingVak;
	
	public void kijkNa(final boolean backStep, final boolean show, final boolean setState) {
		try {
			kijkNa0(backStep, show, setState);
		} catch(RestartException r) {
			r.restart(new Runnable() {
				public void run() {
					try {
						kijkNa0(backStep, show, setState);
					} catch (RestartException e) {
						e.restart(this);
					}					
				}});
		}
	}
	
	private void kijkNa0(boolean backStep, boolean show, boolean setState) throws RestartException
	{
		String useranswer = "$f" + this.toString() + "@";
		if(useranswer.equals("$f@"))
			ingevuld = false;
		else
			ingevuld = true;
		if(fe != null)
			fe.zetIngevuld(ingevuld);
		
		HashMap<String, Object> checkResults = new HashMap<String, Object>();
		if(fe != null)
			checkResults = avChecker.checkAnswer(useranswer, fe.getLatestAnswer(), fe.getSubstitutie(), fe.getGebruikersSubstituties());
		else	
			checkResults = avChecker.checkAnswer(useranswer);

		this.correct = (Boolean) checkResults.get("correct");
		this.score = (Integer) checkResults.get("score");
		//System.out.println("score = " + score);
		if(hasFeedback || correct == null || !correct)
		{	this.feedback = (String) checkResults.get("feedback");
		}
		else
			this.feedback = "";
		this.syntaxFout = (Boolean) checkResults.get("syntaxFout");
		
		this.goedHalfFout = (Integer) checkResults.get("goedHalfFout");

		if(fe != null)
		{	boolean stapCorrect = fe.controleerStap();
			if(!stapCorrect)
				this.goedHalfFout = AntwoordVakChecker.FOUT;
		}
		if((mode == 2 || mode == 3) && !show)
		{	if(this.fe != null)
				fe.maakNakijkenAf(backStep, show, setState);
			
			if(syntaxFout)
			{	//checkimg.setUrl(FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
				//checkimg.setVisible(true);
				zetFeedback();
				
				//TODO: feedback syntaxfout tonen.
			}
			if(setState)
				comRoot.setChanged(teltMee && goedHalfFout == AntwoordVakChecker.FOUT);
			return;
		}
		
//		logger.fine("userAnswer: " + useranswer);
//		logger.fine("correct: " + correct);
//		logger.fine("score: " + score);
//		logger.fine("goedHalfFout: " + goedHalfFout);
//		logger.fine("feedback: " + feedback);

//		if(fe != null)
//			fe.zetStapOk(goedHalfFout);
		if (goedHalfFout == AntwoordVakChecker.DOOR)
		{
			checkimg.setUrl(FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
//			if (this.fe != null)
//			{
//				if(backStep)
//					fe.setAndAddFeedback(feedback);
//				else
//				{	fe.setFeedback(feedback);
//					fe.addStep(useranswer);
//				}
//			}
		}
		else if (goedHalfFout == AntwoordVakChecker.HALF)
		{
//			if (this.fe != null)
//				fe.setFeedback(feedback);
			checkimg.setUrl(FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
		}
		else if (goedHalfFout == AntwoordVakChecker.GOED)
		{
			checkimg.setUrl(FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
//			if (this.fe != null)
//			{
//				fe.setFeedback(feedback);
//				fe.lastStep(useranswer);
//			}
		}
		else if (goedHalfFout == AntwoordVakChecker.FOUT)
		{
//			if (this.fe != null)
//				fe.setAndAddFeedback(feedback);
			checkimg.setUrl(FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
		}
		
		checkimg.setVisible(check && goedHalfFout != AntwoordVakChecker.GEEN); // Wim: Hier verscheen het vinkje als goedhalfFout GEEN is
		resize();
		//logger.finer(String.valueOf(checkimg.isVisible()));
		//sp.setPixelSize(breedte, -1);
		if (this.fe == null && !useranswer.equals(lastanswer))
		{
			lastanswer = useranswer;
			if(mode == 0 || mode ==1) 
				comRoot.setChanged(teltMee && goedHalfFout == AntwoordVakChecker.FOUT);
		
		}
		//if(this.fe != null && !(mode == 2 || mode == 3))
		//	fe.maakNakijkenAf(backStep);
		if(!feedback.equals("") && fe == null)
		{
			zetFeedback();
		}
		
		if(this.fe != null && ingevuld)
		{	fe.maakNakijkenAf(backStep, show, setState);
		}
	}
	
	public void zetFeedback()
	{
		TekstBuffer b = new TekstBuffer();
		//Volgens mij zijn randomvariabelen feedback bij aanmaken antwoordmodel al ingevuld, dus hier weggelaten.
//		try{
//			feedback = FormuleParser.randomizeTekstVakString(feedback, randomVarNamen, randomVarWaarden);
//		}
//		catch(Exception e){}
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
		zetFeedbackZichtbaar(true);
	}
	
	public String getFeedback()
	{
		return feedback;
	}
	
	public int getGoedHalfFout()
	{
		return goedHalfFout;
	}
	
	public boolean isSyntaxFout()
	{
		return syntaxFout;
	}
	
	public void resize()
	{
		breedte = this.getMainRegel().getWidth() + extraWidth; //checkPanel.getOffsetWidth() + extraWidth;// + (getImageVisible()?26:0);
		hoogte = this.getMainRegel().getHeight() + 6;
		sp.setPixelSize((breedte-3) , (hoogte-8) );
		if(parentRegel != null)
		{	parentRegel.resize();
		}
		if(fe != null)
		{	fe.resize();
		}
		
	}
	
	public void setFont(FormuleFont fm)
	{
		super.setFont(fm);
		this.getMainRegel().setMinimumHeight(fm.getHeight() + 3);
		resize();
	}

	@Override
	public Panel getAsPanel()
	{
		return sp;
	}
	
	public int getHeight()
	{
		return facade.wrapHeight(hoogte);
	}
	
	public int getWidth()
	{
		return facade.wrapWidth(breedte);
	}
	
	public void zetVolledigeBreedte(int breedte)
	{
		if(volledigeBreedte)
			this.breedte = breedte;
	}
	
	public int getAsHoogte()
	{
		return facade.wrapAsHoogte(this.getMainRegel().getAsHoogte() + (boxMetRand?2:1)); //+ 6 /* margin top + padding top */);
		
	}

	public void setStrict(boolean strict)
	{
		this.strict = strict;
	}

	public boolean isStrict()
	{
		return this.strict;
	}

	@Override
	public HashMap<String, Object> getState()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		if(fews != null)
		{
			HashMap<String, Object> h2 = fews.getState();
			if(h2.containsKey("ingevuld") && ((Boolean) h2.get("ingevuld")).booleanValue())
			{
				h = h2;
			}
			else
			{
				h2 = new HashMap<String, Object>();
				String[] formuleVakInhouden = {"$f" + this.toString() + "@" } ;
				if(!this.toString().equals(""))
					this.ingevuld = true;
				boolean ingevuld = this.ingevuld;
				boolean nagekeken = false;
				
				nagekeken = this.nagekeken;
				
				h2.put("formuleVakInhouden", formuleVakInhouden);
				h2.put(ANTWOORD_STRING, "");
				h2.put("ingevuld", new Boolean(ingevuld));
				h2.put("nagekeken", new Boolean(nagekeken));
				
				fews.setState(h2);
				h2 = fews.getState();
				h = h2;
				
			}
		}			
		else	
		{
			kijkNa();
			
			String[] formuleVakInhouden = {"$f" + this.toString() + "@" } ;
			if(!this.toString().equals(""))
				this.ingevuld = true;
			boolean ingevuld = this.ingevuld;
			boolean nagekeken = false;
			
			//ingevuld = this.ingevuld;
			nagekeken = this.nagekeken;
			
			
			h.put("formuleVakInhouden", formuleVakInhouden);
			h.put(ANTWOORD_STRING, formuleVakInhouden[0]);
			h.put("ingevuld", new Boolean(ingevuld));
			h.put("nagekeken", new Boolean(nagekeken));
			
		}
		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		logger.fine("setState " + h);
		//antwoord eruit halen en dan uit h halen, zodat de antwoordstring niet wordt meegenomen in setState. 
		
		if(fews != null)
		{
			fews.wis();
			fews.setState(h);
		}
		
		boolean ingevuld = true;
		boolean nagekeken = false;
		if (h.get("ingevuld") != null)
			ingevuld = (Boolean) h.get("ingevuld");
		if (h.get("nagekeken") != null)
			nagekeken = ((Boolean) h.get("nagekeken")).booleanValue();
		
		this.ingevuld = ingevuld;
		this.nagekeken = nagekeken;
		String antwoord = (String) h.get(ANTWOORD_STRING);
		if(antwoord == null || ("".equals(antwoord.trim()) || "$f@".equals(antwoord.trim())) && fews != null)
			antwoord = fews.getLatestAnswer();
		if (antwoord != null && !"".equals(antwoord.trim()))
		{
			antwoord = strip$f(antwoord);

			this.clearMain();
			this.insert(antwoord);
			setCurrentElementRepaint();
			lastanswer = "$f" + toString() + "@";
			//if(mode != 2 && mode != 3)
			//	kijkNa();
			
			if (mode == 0 || mode == 1 || nagekeken)
				kijkNa();
			//TODO: kijken of dit goed gaat met de strafpunten als een antwoord niet goed is..
		}

	}

	public String strip$f(String antwoord) {
		if (antwoord.startsWith("$f"))
		{
			antwoord = antwoord.substring(2, antwoord.length() - 1);
		}
		return antwoord;
	}

	@Override
	public int getScore()
	{
		if(!teltMee)
			return 0;
		return score;
	}

	@Override
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
	
	@Override
	public Boolean isCorrect()
	{
		if(!teltMee)
			return Boolean.TRUE;
		return correct;
	}
	
	public Boolean isCorrectStrikt()
	{
		return correct;
	}
	
	public void zetNagekeken(boolean b) {
		if (ingevuld)
		{	nagekeken = b;
			if(fews != null)
			{	fews.zetNagekeken(b);
			}
		}
	}
	
	public boolean isIngevuld()
	{
		return ingevuld;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());
		if(fews != null)
		{	fews.setCommunicationRoot(comRoot);
		}
		comRoot.addCBookEventListener("input", this);
		comRoot.addCBookEventListener("index", this);
		if(logging != null) 
			logging.setCommunicationRoot(comRoot);
	}

	@Override
	public Widget asWidget()
	{
		return facade.wrap(getAsPanel());
	}

	public void zetMode(int mode) {
		this.mode = mode;
	}

	public Object getUitwerking(TekstVakPanel parent) {

		if(parent != null && vakUitwerking && parent.uitklapHoogtes != null && parent.uitklapHoogtes.size() > 1)
		{
			double hoogte = parent.uitklapHoogtes.get(1); // Marges??????
			fews.setHeight(hoogte);
			parent.addCBookEventListener(fews);
			return fews;
		}
// van constructor naar hier....
		if( vakUitwerking )
		{
			PopupButton popup = new PopupButton(fews, ImageUtils.newImage("images/resources/popup_voor_uitw_icoon.png"), this);
			PopupFacade.addPopup(popup);
			Style popupstyle = popup.getElement().getStyle();
			popupstyle.setDisplay(Display.INLINE_BLOCK);
			popupstyle.setVerticalAlign(VerticalAlign.TOP);
			breedte += 20; // wordt niet bij breedte geteld. ???
			extraWidth = 43;
			sp.setPixelSize((breedte-3) , (hoogte-8) );
			if(parentRegel != null)
			{	parentRegel.resize();
			}
			sp.add(popup);
			
				  
			//extraWidth += 20; // width of popup button
		}

		
		return null;
	}
	
	public void knip(FormuleClipboardIF clip)
	{
		super.knip(clip);
		resize();
	}
	
	public void plak(FormuleClipboardIF clip)
	{
		super.plak(clip);
		resize();
	}

	@Override
	public void acceptCBookEvent(CBookEvent event) {
		String message = event.getMessage();
		message = strip$f(message);
		insert(message); // Of zo iets.Strip $F en @
		setCurrentElementRepaint();
	}

	private String toMathML(String source) {
		if(isVergelijkingVak)
		{
			VergelijkingMeerv verg = FormuleParser.parseVergelijking(source);
			if(verg == null) return "";
			return ContentMathML.INSTANCE.toString(verg);
		} else {
			Expressie antwoord = FormuleParser.geefExpressie(source);
			if(antwoord == null) return "";
			return ContentMathML.INSTANCE.toString(antwoord);
		}
	}

	@Override
	public void getResponses(List<String> responses) {
		List<Type> responseTypes = facet.getResponseTypes();
		int size = responseTypes.size();
		if( size > 0 ) {
			Type type = responseTypes.get(0);
			int start = 0;
			String useranswer = "$f" + this.toString() + "@";
			if(type == Type.mathml) {
				responses.add(toMathML(useranswer));
				start = 1;
			} 
// kandidaat instelling: type is "decimal/integer" alleen bij formulevak!
			else if (type == Type.decimal || type == Type.integer) {
				Expressie antwoord = FormuleParser.geefExpressie(useranswer);
				if(antwoord != null) {
					double r = antwoord.geefWaarde();
					//if(type == Type.integer) r = Math.round(r); TODO wat zeggen de specs
					responses.add(Double.toString(r)); start = 1;
				}
			}
			if(size>start) { // should not happen!
				for(; start<size; start ++) responses.add("");
			}	
		}
	}

}
