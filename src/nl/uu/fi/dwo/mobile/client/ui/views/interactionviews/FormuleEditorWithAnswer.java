package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.touch.TouchPanel;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

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
	private static final String ANTWOORD_STRING = "antwoordString";
	private final static Logger logger = Logger.getLogger("FormuleEditorWithAnswer");
	OpdrNavIF comRoot;
	TouchPanel sp = null;
	Image checkimg;
	private HashMap<String, Object> launchState;
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
	//private String[] randomVarNamen = null;
	//private HashMap randomVarWaarden = null;
	private AntwoordVakChecker avChecker = null;
	private PopupFacade facade;
	
	private TekstRegel parentRegel;
	
	public FormuleEditorWithAnswer(HashMap<String, Object> h, boolean isVergelijkingVak, FormuleEditorWithSteps fe, String[] randomVarNamen, HashMap<String, Object> randomVarWaarden)
	{
		super();
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
		if (h.get("interactiePanelLaunchState") != null)
		{
			ObjectMap map = JSONUtilities.wrapMap(h);
			this.breedte = map.getInt("breedte");
			//this.hoogte = map.getInt("hoogte");
			//int breedte = ((Number) h.get("breedte")).intValue();
			//System.out.println("breedte formuleEditorWithAnswer: " + breedte);
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");

			if (isVergelijkingVak)
				avChecker = new AntwoordVergelijkingVakChecker(launchState, randomVarNamen, randomVarWaarden);
			else
				avChecker = new AntwoordFormuleVakChecker(launchState, randomVarNamen, randomVarWaarden);

			if(launchState != null && launchState.get("check") != null)
			{	//System.out.println("check wordt uit launchstate gehaald");
				check = ((Boolean)launchState.get("check")).booleanValue();
			}
			checkimg = new Image(FORMULE_BUNDLE.mw_vinkje_groen());
			checkimg.setVisible(false);
			checkimg.getElement().getStyle().setProperty("marginLeft", "3px");
			checkimg.getElement().getStyle().setProperty("verticalAlign", "top");
			if (fe == null)
			{
				//sp.getElement().getStyle().setProperty("width", (breedte - 9) + "px");
				this.getMainRegel().setMinimumWidth(breedte - 20);
				hoogte = 24;
				this.getMainRegel().setMinimumHeight(hoogte);
				sp.setSize(breedte + "px", hoogte + "px");
				//sp.getElement().getStyle().setBackgroundColor(CssColor.make(255, 0, 0).toString());
				sp.getElement().getStyle().setProperty("border", "1px solid gray");
				
				//sp.getElement().getStyle().setPadding(3, Style.Unit.PX);
				
				sp.getElement().getStyle().setPaddingLeft(1, Style.Unit.PX);
				sp.getElement().getStyle().setPaddingRight(0, Style.Unit.PX);
				sp.getElement().getStyle().setPaddingTop(1, Style.Unit.PX);
				sp.getElement().getStyle().setPaddingBottom(5, Style.Unit.PX);
				//(Weggehaald Sietske) sp.getElement().getStyle().setProperty("backgroundColor", "#e9e9e9");
				sp.getElement().getStyle().setProperty("backgroundColor", "white");
				//this.getMainRegel().getCanvas().getElement().getStyle().setProperty("marginTop", "3px");
				//this.getMainRegel().getCanvas().getElement().getStyle().setProperty("marginBottom", "0px");
			}

			//sp.getElement().addClassName("insert_formule");
			sp.add(this.getMainRegel().getCanvas());
			sp.add(checkimg);
			
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
			checkimg.setResource(FORMULE_BUNDLE.mw_vinkje_geel());
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
			checkimg.setResource(FORMULE_BUNDLE.mw_vinkje_geel());
		}
		else if (goedHalfFout == AntwoordVakChecker.GOED)
		{
			checkimg.setResource(FORMULE_BUNDLE.mw_vinkje_groen());
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
			checkimg.setResource(FORMULE_BUNDLE.mw_kruisje_rood());
		}
		
		checkimg.setVisible(check && goedHalfFout != AntwoordVakChecker.GEEN); // Wim: Hier verscheen het vinkje als goedhalfFout GEEN is
		//sp.setPixelSize(breedte, -1);
		if (this.fe == null)
			comRoot.setChanged();

	}
	
	public void resize()
	{
		System.out.println("resize");
		breedte = this.getMainRegel().getWidth() + 20;
		hoogte = this.getMainRegel().getHeight();
		System.out.println("nieuwe breedte: " + breedte);
		System.out.println("nieuwe hoogte: " + hoogte);
		//nog zorgen dat hoogte altijd minimaal 24 is?
		sp.setSize(breedte + "px", hoogte + "px");
		if(parentRegel != null)
			parentRegel.resize();
		
		//en dan bestaat resize uit vulRegel en een resize van het omliggende tekstvak (wat dan weer leidt tot een resize van de hele kolom)..
	}
	
	public void setFont(FormuleFont fm)
	{
		System.out.println("formuleEditorWithAnswer setFont: " + fm.toString());
		super.setFont(fm);
	}
	

	@Override
	public Panel getAsPanel()
	{
		return sp;
	}
	
	public int getHeight()
	{
		return hoogte;
	}
	
	public int getWidth()
	{
		return breedte;
	}
	
	public int getAsHoogte()
	{
		return this.getMainRegel().getAsHoogte() + 3;
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
		String[] formuleVakInhouden = {"$f" + this.toString() + "@" } ;
		h.put("formuleVakInhouden", formuleVakInhouden);
		h.put(ANTWOORD_STRING, formuleVakInhouden[0]);
		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
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
	}

	@Override
	public Widget asWidget()
	{
		return facade.wrap(getAsPanel());
	}

}
