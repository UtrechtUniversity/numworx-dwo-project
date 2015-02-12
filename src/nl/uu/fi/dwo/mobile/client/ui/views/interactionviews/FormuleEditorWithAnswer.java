package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
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
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.FormuleKeyBoardButtons;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.utils.ImageUtils;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.AntwoordFormuleVakChecker;
import fi.wiskopdr.AntwoordVakChecker;
import fi.wiskopdr.AntwoordVergelijkingVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.text.Text;

/**
 * Checks inserted formule with the correct answer
 * 
 * @author Danny Hendrix, Evertson Croes
 * 
 */
public class FormuleEditorWithAnswer extends FormuleEditor implements InteractionView, CBookEventListener
{
	private boolean checkUitklapMogelijkheid() {
		return "noordhoff".equals(DWOplayer.PARAMETERS.keyboardStyle()); // FIXME beter!
	}
	private int extraWidth = 20 ; // of 40;
	
	
	class FormuleEditorPopup extends FormuleEditorWithSteps implements CBookEventListener, StateLess {

		public FormuleEditorPopup(HashMap<String, Object> h,
				boolean isVergelijkingVak, String[] randomVarNamen,
				HashMap randomVarWaarden) {
			super(h, isVergelijkingVak, randomVarNamen, randomVarWaarden);
		}

//		@Override
//		public void kijkNa() {
//			// TODO Auto-generated method stub
//			super.kijkNa();
//			String string = getEditor().toString();
//			transfer(string);
//		}

		void transfer(String string) {
			logger.fine("userstring = " + string);
			FormuleEditorWithAnswer other = FormuleEditorWithAnswer.this;
			other.clearMain();
			other.insert(string);
			other.enter();
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
			return new FormuleEditorWithAnswer(super.h, isVergelijkingVak, this, randomVarNamen, randomVarWaarden)
			{
				@Override
				public void enter() {
					super.enter();
					transfer(toString());
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
	Image checkimg;
	Label feedbackLabel;
	PopupPanel feedbackPanel;
	TekstVak feedbackTekst;
	Canvas feedbackSluitKnop;
	Context2d gIm;
	FlowPanel checkPanel;
	private ObjectMap launchState;
	private FormuleEditorWithSteps fe = null;
	private boolean strict = true;
	private ObjectMap instellingen = null;
	private int score = 0;
	private Boolean correct = null;
	
	//private Expressie substitutie;
	private String feedback = "";
	private int scoreMax = 0;
	private boolean ingevuld = false;
	private boolean nagekeken = false;
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
	private int goedHalfFout = AntwoordVakChecker.FOUT;
	
	private TekstRegel parentRegel;
	private FormuleEditorPopup fews;
	
	private static boolean fontOvererving = false;
	
	public static void zetFontOverervingForm(boolean b)
	{	fontOvererving = b;
	}
	
	public FormuleEditorWithAnswer(HashMap<String, Object> h, boolean isVergelijkingVak, FormuleEditorWithSteps fe, String[] randomVarNamen, HashMap<String, Object> randomVarWaarden)
	{
		super();
		boolean boxMetRand;
		boxMetRand = true;
		//getMainRegel().setEditorParent(this);
		//getMainRegel().setDefaultHeight(24);

		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;

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
			this.breedte = map.getInt("breedte");
			this.hoogte = map.getInt("hoogte");
			this.volledigeBreedte = map.getBoolean("volledigeBreedte");
			
			//this.hoogte = map.getInt("hoogte");
			//int breedte = ((Number) h.get("breedte")).intValue();
			//System.out.println("breedte formuleEditorWithAnswer: " + breedte);
			launchState = map.getObjectMap("interactiePanelLaunchState");

			if (isVergelijkingVak)
				avChecker = new AntwoordVergelijkingVakChecker((HashMap<String, Object>) launchState, randomVarNamen, randomVarWaarden);
			else
				avChecker = new AntwoordFormuleVakChecker((HashMap<String, Object>) launchState, randomVarNamen, randomVarWaarden);

			if(launchState != null) {
				if(launchState.containsKey("check") )
				{
					check = launchState.getBoolean("check");
				}
				if(launchState.containsKey("teltMee"))
				{
					teltMee = launchState.getBoolean("teltMee");
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
						
						fews = new FormuleEditorPopup(hh,isVergelijkingVak,randomVarNamen,randomVarWaarden);
					}
				}
			}
		
			checkimg = new Image(FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
			checkimg.setVisible(false);
			lastanswer = null;
			checkimg.getElement().getStyle().setProperty("marginLeft", "3px");
			checkimg.getElement().getStyle().setProperty("marginTop", "-5px"); //in plaats hiervan zou marginTop -5px ook goed kunnen werken.
			checkimg.getElement().getStyle().setProperty("marginBottom", "-6px");
			
			feedbackPanel = new PopupPanel(true);
			feedbackPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			feedbackPanel.getElement().getStyle().setBorderColor("black");
			feedbackPanel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
			feedbackPanel.getElement().getStyle().setPadding(2, Style.Unit.PX);
			feedbackPanel.getElement().getStyle().setBackgroundColor("#FFFFDD");
			
			feedbackTekst = new TekstVak();
			feedbackTekst.setSize(200, 50);
			feedbackTekst.setFontSize(XMLView.getDefaultFontSize());
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
			
			checkPanel = new FlowPanel();
			checkPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			checkPanel.getElement().getStyle().setProperty("verticalAlign", "top");
			checkPanel.getElement().getStyle().setMarginTop(-3, Style.Unit.PX);
			checkPanel.add(checkimg);
			checkPanel.add(feedbackLabel);
			
			if (fe == null)
			{
				//sp.getElement().getStyle().setProperty("width", (breedte - 9) + "px");
				this.getMainRegel().setMinimumWidth(breedte - extraWidth);
				//hoogte = 27;
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
			sp.add(this.getMainRegel().getCanvas());
			//sp.add(checkimg);
			//sp.add(feedbackLabel);
			sp.add(checkPanel);
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
	}
	
	@Override
	public void addElement(FormuleElement e)
	{
		super.addElement(e);
		resize();
		resetimg();
	}

	@Override
	public void removeCurrentElement()
	{
		super.removeCurrentElement();
		resize();
		resetimg();
	}

	@Override
	public void removeNextElement()
	{
		super.removeNextElement();
		resize();
		resetimg();
	}

	@Override
	public void insert(String text)
	{
		super.insert(text);
		resize();
		resetimg();
	}

	void resetimg() {
		checkimg.setVisible(false);
		feedbackLabel.setVisible(false);
		feedbackPanel.hide();
		lastanswer = null;
	}
	
	public void setimg(String answer)
	{
		checkimg.setVisible(true);
		lastanswer = answer;
	}

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
		if(mode == OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS)
			kijkNa(false, false, false);
		else
			kijkNa(false, true, false);
		if(comRoot != null) // alleen niet null als fewa een toplevel is.
			comRoot.fireEvent(new CBookEvent(this, "input", toString()));
		else if( fe != null) {
			fe.fire("input", toString());
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
	public void kijkNa(boolean backStep, boolean show, boolean setState)
	{
		String useranswer = "$f" + this.toString() + "@";
		if(useranswer.equals("$f@"))
			ingevuld = false;
		else
			ingevuld = true;
		
		HashMap<String, Object> checkResults = new HashMap<String, Object>();
		if(fe != null)
			checkResults = avChecker.checkAnswer(useranswer, fe.getLatestAnswer(), fe.getSubstitutie(), fe.getGebruikersSubstituties());
		else	
			checkResults = avChecker.checkAnswer(useranswer);

		this.correct = (Boolean) checkResults.get("correct");
		this.score = (Integer) checkResults.get("score");
		//System.out.println("score = " + score);
		this.feedback = (String) checkResults.get("feedback");
		this.syntaxFout = (Boolean) checkResults.get("syntaxFout");
		
		this.goedHalfFout = (Integer) checkResults.get("goedHalfFout");

		if(fe != null)
		{	boolean stapCorrect = fe.controleerStap();
			if(!stapCorrect)
				this.goedHalfFout = AntwoordVakChecker.FOUT;
		}
		if((mode == 2 || mode == 3) && !show)
		{	if(this.fe != null)
				fe.maakNakijkenAf(backStep, setState);
			
			if(syntaxFout)
			{	//checkimg.setUrl(FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
				//checkimg.setVisible(true);
				zetFeedback();
				
				//TODO: feedback syntaxfout tonen.
			}
			if(setState)
				comRoot.setChanged(goedHalfFout == AntwoordVakChecker.FOUT);
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
		//logger.finer(String.valueOf(checkimg.isVisible()));
		//sp.setPixelSize(breedte, -1);
		if (this.fe == null && !useranswer.equals(lastanswer))
		{
			lastanswer = useranswer;
			//if(mode == 0 || mode ==1) EVEN UIT VOOR FACET
			comRoot.setChanged(goedHalfFout == AntwoordVakChecker.FOUT);
		
		}
		//if(this.fe != null && !(mode == 2 || mode == 3))
		//	fe.maakNakijkenAf(backStep);
		if(!feedback.equals("") && fe == null)
		{
			zetFeedback();
			feedbackLabel.setVisible(true);
		}
		
		if(this.fe != null && ingevuld)
		{	fe.maakNakijkenAf(backStep, setState);
		}
	}
	
	public void zetFeedback()
	{
		TekstBuffer b = new TekstBuffer();
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
		breedte = this.getMainRegel().getWidth() + extraWidth;
		hoogte = this.getMainRegel().getHeight() + 6;
		sp.setPixelSize((breedte-3) , (hoogte-8) );
		if(parentRegel != null)
			parentRegel.resize();
		if(fe != null)
			fe.resize();
		
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
		return facade.wrapAsHoogte(this.getMainRegel().getAsHoogte() + 6 /* margin top + padding top */);
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
		HashMap<String, Object> h;
		if(fews != null)
		{
			h = fews.getState();
			h.put(ANTWOORD_STRING,  toString() );
		} else {
			h = new HashMap<String, Object>();
			String[] formuleVakInhouden = {"$f" + this.toString() + "@" } ;
			boolean ingevuld = true;
			boolean nagekeken = false;
			
			ingevuld = this.ingevuld;
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
		if(fews != null)
		{
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
		
		if (antwoord != null && !"".equals(antwoord.trim()))
		{
			antwoord = strip$f(antwoord);

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
		return score;
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
			nagekeken = b;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());
		if(fews != null)
			fews.setCommunicationRoot(comRoot);
		comRoot.addCBookEventListener("input", this);
		comRoot.addCBookEventListener("index", this);
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
			PopupButton popup = new PopupButton(fews, ImageUtils.newImage("images/resources/antwoordknop.gif"), this);
			Style popupstyle = popup.getElement().getStyle();
			popupstyle.setDisplay(Display.INLINE_BLOCK);
			popupstyle.setVerticalAlign(VerticalAlign.TOP);
			sp.add(popup);
			breedte += 20;	  // wordt niet bij breedte geteld.
			extraWidth += 20; // width of popup button
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

}
