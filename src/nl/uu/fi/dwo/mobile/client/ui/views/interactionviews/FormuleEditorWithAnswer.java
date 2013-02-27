package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.client.ui.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.mobile.client.ui.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.mobile.client.ui.formuleobjects.FormuleFont;

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
	private static final String ANTWOORD_STRING = "antwoordString";
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
	//private String[] randomVarNamen = null;
	//private HashMap randomVarWaarden = null;
	private AntwoordVakChecker avChecker = null;

	public FormuleEditorWithAnswer(HashMap<String, Object> h, boolean isVergelijkingVak, FormuleEditorWithSteps fe, String[] randomVarNamen, HashMap<String, Object> randomVarWaarden)
	{
		super();

		//this.randomVarNamen = randomVarNamen;
		//this.randomVarWaarden = randomVarWaarden;

		if (fe != null)
		{
			this.fe = fe;
		}

		if (h.get("interactiePanelLaunchState") != null)
		{
			int breedte = ((Integer) h.get("breedte")).intValue();
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");

			if (isVergelijkingVak)
				avChecker = new AntwoordVergelijkingVakChecker(launchState, randomVarNamen, randomVarWaarden);
			else
				avChecker = new AntwoordFormuleVakChecker(launchState, randomVarNamen, randomVarWaarden);

			checkimg = new Image("images/resources/mw_vinkje_groen.png");
			checkimg.setVisible(false);
			checkimg.getElement().getStyle().setProperty("marginLeft", "3px");
			sp = new TouchPanel();
			if (fe == null)
			{
				sp.getElement().getStyle().setProperty("width", breedte + "px");
				sp.getElement().getStyle().setProperty("border", "1px solid gray");
				sp.getElement().getStyle().setProperty("backgroundColor", "#e9e9e9");
				this.getMainRegel().getCanvas().getElement().getStyle().setProperty("marginTop", "3px");
				this.getMainRegel().getCanvas().getElement().getStyle().setProperty("marginBottom", "0px");
			}

			sp.getElement().addClassName("insert_formule");
			sp.add(this.getMainRegel().getCanvas());
			sp.add(checkimg);
		}
	}

	public void zetInstellingen(HashMap<String, Object> instellingen)
	{
		this.instellingen = instellingen;
		setFont(FormuleFont.createFromFontSize((Integer) instellingen.get("fontSize")));

	}

	public Object getFe()
	{
		return fe;
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

	public void check()
	{
		String useranswer = "$f" + this.toString() + "@";
		HashMap<String, Object> checkResults = avChecker.checkAnswer(useranswer);

		this.correct = (Boolean) checkResults.get("correct");
		this.score = (Integer) checkResults.get("score");
		this.feedback = (String) checkResults.get("feedback");

		int goedHalfFout = (Integer) checkResults.get("goedHalfFout");

		System.out.println("userAnswer: " + useranswer);
		System.out.println("correct: " + correct);
		System.out.println("score: " + score);
		System.out.println("goedHalfFout: " + goedHalfFout);
		System.out.println(" feedback: " + feedback);

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

		checkimg.setVisible(true);
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
		h.put(ANTWOORD_STRING, "$f" + this.toString() + "@");
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
		return getAsPanel();
	}

}
