package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.StateLess;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.event.CBookEvent;
import nl.uu.fi.dwo.mobile.client.ui.event.CBookEventListener;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.AntwoordFormuleVakChecker;
import fi.wiskopdr.AntwoordVakChecker;
import fi.wiskopdr.AntwoordVergelijkingVakChecker;

/**
 * Checks inserted formule with the correct answer
 * 
 * @author Danny Hendrix, Evertson Croes
 * 
 */
public class FormuleEditorWithAnswer extends FormuleEditor implements InteractionView
{
	
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
			return new FormuleEditorWithAnswer(h, isVergelijkingVak, this, randomVarNamen, randomVarWaarden)
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
				FormuleEditor other = FormuleEditorWithAnswer.this;
				String useranswer = getEditor().toString();
				other.clearMain();
				other.insert(useranswer);
			
			}
		}

		void setHeight(double hoogte) {
			logger.fine("setHeight(" + hoogte + ")");
			super.setHeight((int)hoogte);
		}

		
	}
	
	
	private static final String ANTWOORD_STRING = "antwoordString";
	private final static Logger logger = Logger.getLogger("FormuleEditorWithAnswer");
	OpdrNavIF comRoot;
	TouchPanel sp = null;
	Image checkimg;
	private ObjectMap launchState;
	private FormuleEditorWithSteps fe = null;
	private boolean strict = true;
	private Map<String, Object> instellingen = null;
	private int score = 0;
	private boolean correct = false;
	private String feedback = "";
	private int scoreMax = 0;
	private boolean check = true;
	private int breedte;
	private int hoogte;
	private boolean volledigeBreedte;
	//private String[] randomVarNamen = null;
	//private HashMap randomVarWaarden = null;
	private AntwoordVakChecker avChecker = null;
	private PopupFacade facade;
	private int mode;
	private boolean vakUitwerking;
	
	private TekstRegel parentRegel;
	private FormuleEditorPopup fews;
	
	public FormuleEditorWithAnswer(HashMap<String, Object> h, boolean isVergelijkingVak, FormuleEditorWithSteps fe, String[] randomVarNamen, HashMap<String, Object> randomVarWaarden)
	{
		super();
		boolean boxMetRand;
		boxMetRand = true;
		//getMainRegel().setEditorParent(this);
		//getMainRegel().setDefaultHeight(24);

		//this.randomVarNamen = randomVarNamen;
		//this.randomVarWaarden = randomVarWaarden;

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
			
				if(launchState.containsKey("formuleToolBijFocus"))
					setFormuleToolBijFocus(launchState.getBoolean("formuleToolBijFocus"));
			
				if(launchState.containsKey("boxMetRand"))
					boxMetRand = launchState.getBoolean("boxMetRand");
				if(launchState.containsKey("uitw")) 
				{
					vakUitwerking = launchState.getBoolean("uitw");
					logger.fine("vakuitwerking = " + vakUitwerking);
					if (vakUitwerking)
					{
						HashMap<String, Object> hh = new HashMap<String,Object>();
						hh.put("volledigeBreedte", Boolean.TRUE);
						hh.put("breedte", breedte);
						hh.put("hoogte" , 250); // FIXME wat is hier de goede hoogte?
						HashMap ll = new HashMap();
						hh.put("interactiePanelLaunchState", launchState);
						
						fews = new FormuleEditorPopup(hh,isVergelijkingVak,randomVarNamen,randomVarWaarden);
					}
				}
			}
		
			checkimg = new Image(FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
			checkimg.setVisible(false);
			checkimg.getElement().getStyle().setProperty("marginLeft", "3px");
			checkimg.getElement().getStyle().setProperty("verticalAlign", "top");
			
			if (fe == null)
			{
				//sp.getElement().getStyle().setProperty("width", (breedte - 9) + "px");
				this.getMainRegel().setMinimumWidth(breedte - 20);
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

				sp.getElement().getStyle().setMarginTop(2, Style.Unit.PX);
				sp.getElement().getStyle().setPaddingTop(3, Style.Unit.PX);

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
			sp.add(checkimg);
			sp.addTouchHandler(new FormuleEditorTouchHandler(this));
			
		}
	}

	public void zetInstellingen(Map<String, Object> instellingen)
	{
		this.instellingen = instellingen;
		//System.out.println("fontSize uit instellingen formuleEditorWithAnswer: " + ((Number) instellingen.get("fontSize")).intValue());
		setFont(FormuleFont.createFromFontSize(((Number) instellingen.get("fontSize")).intValue()));

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
		checkimg.setVisible(false);
	}

	@Override
	public void removeCurrentElement()
	{
		super.removeCurrentElement();
		resize();
		checkimg.setVisible(false);
	}

	@Override
	public void removeNextElement()
	{
		super.removeNextElement();
		resize();
		checkimg.setVisible(false);
	}

	@Override
	public void insert(String text)
	{
		super.insert(text);
		resize();
		checkimg.setVisible(false);
	}

	@Override 
	public void enter() {
		if(mode == OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS)
		{
			return; 
		}
		kijkNa();
	}
	
	
	public void kijkNa()
	{
		String useranswer = "$f" + this.toString() + "@";
		HashMap<String, Object> checkResults = avChecker.checkAnswer(useranswer);

		this.correct = (Boolean) checkResults.get("correct");
		this.score = (Integer) checkResults.get("score");
		this.feedback = (String) checkResults.get("feedback");

		int goedHalfFout = (Integer) checkResults.get("goedHalfFout");

		logger.fine("userAnswer: " + useranswer);
		logger.finer("correct: " + correct);
		logger.finer("score: " + score);
		logger.finer("goedHalfFout: " + goedHalfFout);
		logger.finer("feedback: " + feedback);

		if (goedHalfFout == AntwoordVakChecker.DOOR)
		{
			checkimg.setUrl(FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
			if (this.fe != null)
			{
				fe.setFeedback(feedback);
				fe.addStep(useranswer);
			}
		}
		else if (goedHalfFout == AntwoordVakChecker.HALF)
		{
			if (this.fe != null)
				fe.setFeedback(feedback);
			checkimg.setUrl(FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
		}
		else if (goedHalfFout == AntwoordVakChecker.GOED)
		{
			checkimg.setUrl(FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
			if (this.fe != null)
			{
				fe.setFeedback(feedback);
				fe.lastStep(useranswer);
			}
		}
		else if (goedHalfFout == AntwoordVakChecker.FOUT)
		{
			if (this.fe != null)
				fe.setAndAddFeedback(feedback);
			checkimg.setUrl(FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
		}
		
		checkimg.setVisible(check && goedHalfFout != AntwoordVakChecker.GEEN); // Wim: Hier verscheen het vinkje als goedhalfFout GEEN is
		//sp.setPixelSize(breedte, -1);
		if (this.fe == null)
			comRoot.setChanged();

	}
	
	public void resize()
	{
		breedte = this.getMainRegel().getWidth() + 20;
		hoogte = this.getMainRegel().getHeight() + 6;
		
		sp.setSize((breedte-3) + "px", (hoogte-8) + "px");
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
		return facade.wrapAsHoogte(this.getMainRegel().getAsHoogte() + 5 /* margin top + padding top */);
		
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
			h.put("formuleVakInhouden", formuleVakInhouden);
			h.put(ANTWOORD_STRING, formuleVakInhouden[0]);
		}
		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		if(fews != null)
		{
			fews.setState(h);
		}
		
		String antwoord = (String) h.get(ANTWOORD_STRING);
		if (antwoord != null && !"".equals(antwoord.trim()))
		{
			if (antwoord.startsWith("$f"))
			{
				antwoord = antwoord.substring(2, antwoord.length() - 1);
			}

			this.insert(antwoord);
			kijkNa();
		}

	}

	@Override
	public int getScore()
	{
		return score;
	}

	@Override
	public boolean isCorrect()
	{
		return correct;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());
		if(fews != null)
			fews.setCommunicationRoot(comRoot);
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
		if(vakUitwerking)
		{
			double hoogte = parent.uitklapHoogtes.get(1); // Marges??????
			fews.setHeight(hoogte);
			parent.addCBookEventListener(fews);
			return fews;
		}
		return null;
	}
	
	public void knip()
	{
		super.knip();
		resize();
	}
	
	public void plak()
	{
		super.plak();
		resize();
	}

}
