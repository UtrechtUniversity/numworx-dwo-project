package nl.uu.fi.dwo.mobile.client.ui;

import java.util.ArrayList;
import java.util.HashMap;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.touch.TouchCancelEvent;
import nl.uu.fi.dwo.interaction.client.touch.TouchEndEvent;
import nl.uu.fi.dwo.interaction.client.touch.TouchHandler;
import nl.uu.fi.dwo.interaction.client.touch.TouchMoveEvent;
import nl.uu.fi.dwo.interaction.client.touch.TouchStartEvent;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

/**
 * Used for navigation between assignments
 * 
 * @author Evertson Croes
 * 
 */
public class OpdrNav implements OpdrNavIF, Runnable
{
	public static int OEFENEN = 0;
	public static int OEFENEN_STRAFPUNTEN = 1;
	public static int ZELFTOETS = 2;
	public static int EINDTOETS = 3;

	private ViewModuleViewImpl entry;
	private ListBox lb_activiteiten;
	private Panel fp_opdrachten;
	private Panel mainPanel;
	private Panel contentPanel;
	private int aantalActiviteiten;
	private int[] aantalOpdrachten;
	private String[] activiteitNamen;
	private int maxAantalOpdrachten = 50;

	private HashMap<String, Object>[][] opdrachten;
	private HashMap<String, Object>[][] states;
	private int[][] scoresMax;
	private int[][] scores;
	private boolean[][] isCorrect;

	private int scoreMax;
	private int currentOpdracht = 0;
	private int currentActiviteit = 0;
	private ArrayList<TouchButton> buttons = new ArrayList<TouchButton>();
	private Memento memento;

	public OpdrNav(HashMap<String, Object> launchData, ViewModuleViewImpl ev, Memento memento)
	{
		this.entry = ev;
		this.memento = memento;
		memento.setUnload(this);
		aantalActiviteiten = Integer.parseInt((String) launchData.get("aantalActiviteiten"));
		activiteitNamen = new String[aantalActiviteiten];
		aantalOpdrachten = new int[maxAantalOpdrachten];

		for (int i = 0; i < aantalActiviteiten; i++)
		{
			activiteitNamen[i] = (String) launchData.get("activiteit_" + (i + 1));
			String aantalString = (String) launchData.get("aantalOpdrachten_" + (i + 1));
			aantalOpdrachten[i] = Integer.parseInt(aantalString);
		}

		opdrachten = new HashMap[aantalActiviteiten][maxAantalOpdrachten];
		scoresMax = new int[aantalActiviteiten][maxAantalOpdrachten];
		scores = new int[aantalActiviteiten][maxAantalOpdrachten];
		isCorrect = new boolean[aantalActiviteiten][maxAantalOpdrachten];
		states = new HashMap[aantalActiviteiten][maxAantalOpdrachten];
		scoreMax = 0;

		for (int i = 0; i < aantalActiviteiten; i++)
		{
			for (int j = 0; j < aantalOpdrachten[i]; j++)
			{
				opdrachten[i][j] = (HashMap<String, Object>) launchData.get("opdracht_" + (i + 1) + "_" + (j + 1));
				HashMap<String, Object> ht = opdrachten[i][j];
				if (ht != null && ht.containsKey("scoreMax"))
					scoresMax[i][j] = ((Integer) ht.get("scoreMax")).intValue();
				else
					scoresMax[i][j] = 10;
				scoreMax += scoresMax[i][j];
			}
		}
		entry.setCommunicationRoot(this);
		states = memento.getOpdrContStates(states);
		//setOpdrachten(currentActiviteit); // kan dat nu al? of anders bij setchanged testen op  buttons.get() != null
		final HashMap<String, Object> state = states[currentActiviteit][currentOpdracht];
		if (state == null)
			entry.zetOpdracht(opdrachten[currentActiviteit][currentOpdracht]);
		else
			entry.zetOpdrachtPlusState(opdrachten[currentActiviteit][currentOpdracht], state);
	}

	public Panel getAsPanel()
	{
		mainPanel = new FlowPanel();
		contentPanel = new FlowPanel();
		contentPanel.getElement().getStyle().setMargin(5, Unit.PX);
		mainPanel.add(contentPanel);

		lb_activiteiten = new ListBox();
		if (aantalActiviteiten == 1)
			lb_activiteiten.setVisible(false);
		lb_activiteiten.getElement().getStyle().setFloat(Style.Float.LEFT);
		lb_activiteiten.getElement().getStyle().setMarginRight(10, Unit.PX);
		lb_activiteiten.addChangeHandler(new ChangeHandler()
		{
			public void onChange(ChangeEvent event)
			{
				saveCurrentState();
				int selectedIndex = lb_activiteiten.getSelectedIndex();
				currentActiviteit = selectedIndex;
				currentOpdracht = 0;
				setOpdrachten(selectedIndex);
				entry.clearContentPanel();
				entry.zetOpdracht(opdrachten[currentActiviteit][currentOpdracht]);
				if (states[currentActiviteit][currentOpdracht] != null)
					entry.setState(states[currentActiviteit][currentOpdracht]);
			}
		});
		for (int i = 0; i < aantalActiviteiten; i++)
		{
			lb_activiteiten.addItem(activiteitNamen[i]);
		}

		contentPanel.add(lb_activiteiten);
		setOpdrachten(0);
		return mainPanel;
	}

	public Panel getPanel()
	{
		return mainPanel;
	}

	public void setChanged()
	{
		scores[currentActiviteit][currentOpdracht] = entry.getScore();
		isCorrect[currentActiviteit][currentOpdracht] = entry.isCorrect();
		if (buttons != null && buttons.size() > currentOpdracht)
			setButtonCorrect(buttons.get(currentOpdracht), isCorrect[currentActiviteit][currentOpdracht]);
	}

	private void setOpdrachten(int index)
	{
		if (fp_opdrachten != null)
		{
			contentPanel.remove(fp_opdrachten);
		}
		if (buttons != null)
		{
			buttons.clear();
		}

		fp_opdrachten = new FlowPanel();
		fp_opdrachten.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		//fp_opdrachten.getElement().addClassName("opdrbutton-container");
		for (int j = 0; j < aantalOpdrachten[index]; j++)
		{
			setButton(j);
			setButtonCorrect(buttons.get(j), isCorrect[index][j]);
		}
		fp_opdrachten.getElement().getStyle().setFloat(Style.Float.LEFT);
		contentPanel.add(fp_opdrachten);
	}

	private void setButton(int j)
	{
		TouchButton button = new TouchButton();
		final int button_id = j;
		button.getElement().getStyle().setFloat(Style.Float.LEFT);
		button.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		button.getElement().getStyle().setMarginRight(10, Unit.PX);
		button.getElement().getStyle().setMarginTop(2, Unit.PX);
		button.getElement().getStyle().setMarginBottom(4, Unit.PX);
		button.getElement().getStyle().setPadding(10, Unit.PX);
		button.getElement().getStyle().setPaddingTop(5, Unit.PX);
		button.getElement().getStyle().setPaddingBottom(5, Unit.PX);
		button.getElement().getStyle().setBackgroundColor("#FFBBBB");
		if (scoresMax[currentActiviteit][j] == 0)
			button.getElement().getStyle().setBackgroundColor("#909090");
		button.getElement().getStyle().setProperty("borderRadius", "20px");

		button.getElement().getStyle().setBorderWidth(1, Unit.PX);
		button.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		button.getElement().getStyle().setBorderColor(CssColor.make(121, 127, 144).toString());//("#979797");

		button.setText(" " + (j + 1) + " ");
		if (currentOpdracht == j)
		{
			setButtonCursor(button);
		}
		addButtonHandler(button, j);

		buttons.add(button);
		fp_opdrachten.add(button);
	}

	private void addButtonHandler(TouchButton button, int id)
	{
		final int button_id = id;
		button.addTouchHandler(new TouchHandler()
		{

			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				saveCurrentState();
				setButtonCorrect(buttons.get(currentOpdracht), isCorrect[currentActiviteit][currentOpdracht]);

				removeButtonCursor(buttons.get(currentOpdracht));
				currentOpdracht = button_id;
				setButtonCursor(buttons.get(currentOpdracht));

				entry.clearContentPanel();
				if (states[currentActiviteit][currentOpdracht] == null)
					entry.zetOpdracht(opdrachten[currentActiviteit][currentOpdracht]);
				else
					entry.zetOpdrachtPlusState(opdrachten[currentActiviteit][currentOpdracht], states[currentActiviteit][currentOpdracht]);
			}

			@Override
			public void onTouchMove(TouchMoveEvent event)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onTouchEnd(TouchEndEvent event)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onTouchCanceled(TouchCancelEvent event)
			{
				// TODO Auto-generated method stub

			}

		});
	}

	public void setButtonCorrect(TouchButton button, boolean b)
	{
		button.getElement().getStyle().setBackgroundColor(b ? "#00BB00" : "#FFBBBB");
		if (scoresMax[currentActiviteit][currentOpdracht] == 0)
			button.getElement().getStyle().setBackgroundColor("#909090");
	}

	public void setButtonCursor(TouchButton button)
	{
		//button.getElement().getStyle().setBackgroundColor("#b4b4b4");
		button.getElement().getStyle().setBorderWidth(3, Unit.PX);
		button.getElement().getStyle().setBorderColor("#484848");
		button.getElement().getStyle().setMarginTop(0, Unit.PX);
	}

	public void removeButtonCursor(TouchButton button)
	{
		//button.getElement().getStyle().setBackgroundColor("#f0f0f0");
		button.getElement().getStyle().setBorderWidth(1, Unit.PX);
		button.getElement().getStyle().setBorderColor(CssColor.make(121, 127, 144).toString());//("#979797");
		button.getElement().getStyle().setMarginTop(2, Unit.PX);
	}

	/**
	 * Berekent de totale score van het applet, en geeft deze terug, geschaald
	 * naar 100%.
	 */
	public double getScore()
	{

		int mode = EINDTOETS; // TODO wat zijn de modes? Launchdata?
		int totaalScore = 0;
		int totaalMax = 0;
		for (int i = 0; i < aantalActiviteiten; i++)
		{
			if (mode == EINDTOETS)
			{
				for (int j = 0; j < aantalOpdrachten[i]; j++)
				{
					totaalScore += scores[i][j];
				}
			}
			else
			{ // TODO wat wordt hier bedoeld?
				//totaalScore += or[i].geefScore() - ((mode == ZELFTOETS) ? (nakijkStraf * (Math.max(0, aantalNakijken[i] - 1))) : 0);
			}
			for (int j = 0; j < aantalOpdrachten[i]; j++)
			{
				totaalMax += scoresMax[i][j];
			}
		}
		System.out.println("TotaalMax " + totaalMax);
		System.out.println("TotaalScore " + totaalScore);
		if (totaalMax == 0)
			return 0;
		double doubleScore = Math.round(100.0 * totaalScore / totaalMax);
		if (Double.isInfinite(doubleScore) || Double.isNaN(doubleScore))
			doubleScore = 0;
		return doubleScore;
	}

	public void run()
	{
		saveCurrentState();
		// TODO save scores.raw
		memento.setScore(getScore());

	}

	void saveCurrentState()
	{
		states[currentActiviteit][currentOpdracht] = entry.getState();
		scores[currentActiviteit][currentOpdracht] = entry.getScore();
		isCorrect[currentActiviteit][currentOpdracht] = entry.isCorrect();
		memento.setOpdrContStates(states);

	}

	public void close() {
		memento.close();
		memento = null;
	}

}
