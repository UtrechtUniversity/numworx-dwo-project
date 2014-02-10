package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.touch.TouchPanel;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

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
	private HashMap<String, Object> instellingen = null;
	private int score = 0;
	private boolean correct = false;
	private String feedback = "";
	private int scoreMax = 0;
	private boolean check = true;
	//private String[] randomVarNamen = null;
	//private HashMap randomVarWaarden = null;
	private AntwoordVakChecker avChecker = null;
	private PopupFacade facade;
	
	public FormuleEditorWithAnswer(HashMap<String, Object> h, boolean isVergelijkingVak, FormuleEditorWithSteps fe, String[] randomVarNamen, HashMap<String, Object> randomVarWaarden)
	{
		super();

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
			int breedte = ((Number) h.get("breedte")).intValue();
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");

			if (isVergelijkingVak)
				avChecker = new AntwoordVergelijkingVakChecker(launchState, randomVarNamen, randomVarWaarden);
			else
				avChecker = new AntwoordFormuleVakChecker(launchState, randomVarNamen, randomVarWaarden);

			if(launchState != null && launchState.get("check") != null)
			{	System.out.println("check wordt uit launchstate gehaald");
				check = ((Boolean)launchState.get("check")).booleanValue();
			}
			checkimg = new Image("images/resources/mw_vinkje_groen.png");
			checkimg.setVisible(false);
			checkimg.getElement().getStyle().setProperty("marginLeft", "3px");
			if (fe == null)
			{
				sp.getElement().getStyle().setProperty("width", breedte + "px");
				sp.getElement().getStyle().setProperty("border", "1px solid gray");
				sp.getElement().getStyle().setPaddingTop(3, Style.Unit.PX);
				sp.getElement().getStyle().setProperty("backgroundColor", "#e9e9e9");
				//this.getMainRegel().getCanvas().getElement().getStyle().setProperty("marginTop", "3px");
				//this.getMainRegel().getCanvas().getElement().getStyle().setProperty("marginBottom", "0px");
			}

			sp.getElement().addClassName("insert_formule");
			sp.add(this.getMainRegel().getCanvas());
			sp.add(checkimg);
		}
	}

	public void zetInstellingen(HashMap<String, Object> instellingen)
	{
		this.instellingen = instellingen;
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
	
	@Override
	public void addElement(FormuleElement e)
	{
		super.addElement(e);
		checkimg.setVisible(false);
	}

	@Override
	public void removeCurrentElement()
	{
		super.removeCurrentElement();
		checkimg.setVisible(false);
	}

	@Override
	public void removeNextElement()
	{
		super.removeNextElement();
		checkimg.setVisible(false);
	}

	@Override
	public void insert(String text)
	{
		super.insert(text);
		checkimg.setVisible(false);
	}

	@Override 
	public void enter() {
		check();
	}
	
	
	void check()
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
			checkimg.setUrl("images/resources/mw_vinkje_geel.png");
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
			checkimg.setUrl("images/resources/mw_vinkje_geel.png");
		}
		else if (goedHalfFout == AntwoordVakChecker.GOED)
		{
			checkimg.setUrl("images/resources/mw_vinkje_groen.png");
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
			checkimg.setUrl("images/resources/mw_kruisje_rood.png");
		}
		
		checkimg.setVisible(check && goedHalfFout != AntwoordVakChecker.GEEN); // Wim: Hier verscheen het vinkje als goedhalfFout GEEN is
		if (this.fe == null)
			comRoot.setChanged();

	}
	

	@Override
	public Panel getAsPanel()
	{
		return sp;
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
			check();
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
