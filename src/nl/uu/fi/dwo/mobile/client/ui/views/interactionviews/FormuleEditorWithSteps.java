package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.touch.TouchCancelEvent;
import nl.uu.fi.dwo.interaction.client.touch.TouchEndEvent;
import nl.uu.fi.dwo.interaction.client.touch.TouchHandler;
import nl.uu.fi.dwo.interaction.client.touch.TouchMoveEvent;
import nl.uu.fi.dwo.interaction.client.touch.TouchPanel;
import nl.uu.fi.dwo.interaction.client.touch.TouchStartEvent;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.ui.FormuleKeyboard;
import nl.uu.fi.dwo.mobile.client.ui.TouchButton;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.VergelijkingMeerv;

/**
 * Used for showing formula's that can be solved in steps.
 * 
 * @author Evertson Croes
 * 
 */
public class FormuleEditorWithSteps implements InteractionView
{

	private String startString = "";
	private String antwoordString = null;
	private String prefix = "$f@";
	private boolean hasPrefix = false;
	boolean hasStartString = false;
	boolean boxMetRand = true;
	private Boolean exact = false;
	private int breedte = 600;
	private int hoogte = 250;
	private HashMap<String, Object> launchState;
	private HashMap<String, Object> instellingen;
	private ArrayList<FormuleViewer> viewers = new ArrayList<FormuleViewer>();
	private FormuleEditorWithAnswer editor = null;
	private Widget prefixViewer;
	private FormuleViewer latest_answer_viewer;
	private ScrollPanel sp = null;
	private FlowPanel contentPanel = null;
	private FlowPanel feedbackPanel = null;
	private FlowPanel mainPanel = null;
	private OpdrNavIF comRoot;
	private TouchButton tb = null;
	private TouchButton copyButton = null;
	private int steps = 0;
	private HashMap<String, Object> h = null;
	private String[] randomVarNamen = null;
	private HashMap randomVarWaarden = null;
	private ArrayList<FlowPanel> stepPanels = new ArrayList<FlowPanel>();
	private FormuleFont font = FormuleFont.createFromFontSize(16);

	private int score;
	private int scoreMax;
	private boolean correct;

	private static FormuleFont defaultfont = FormuleFont.createFromFontSize(18);
	//private boolean answeredCorrectly = false;
	private CssColor hlColor = CssColor.make(255, 255, 255);
	private CssColor bgColor = CssColor.make(240, 240, 240);
	private boolean ingevuld;
	private boolean nagekeken;
	private boolean hasFeedback;

	private boolean isVergelijkingVak = false;
	private PopupFacade facade;
	
	private boolean bordjesMethode; // TODO implement this

	public FormuleEditorWithSteps(HashMap<String, Object> h, boolean isVergelijkingVak, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		this.isVergelijkingVak = isVergelijkingVak;

		if (h != null)
			this.h = h;
		if (h.get("breedte") != null)
			breedte = (Integer) h.get("breedte");
		if (h.get("hoogte") != null)
			hoogte = (Integer) h.get("hoogte");

		facade = new PopupFacade(h);
		
		if (h.get("interactiePanelLaunchState") != null)
		{
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
			if (launchState.get("startString") != null)
				startString = (String) launchState.get("startString");
			if (launchState.get("exact") != null)
				exact = (Boolean) launchState.get("exact");
			if (launchState.get("boxMetRand") != null)
				boxMetRand = (Boolean) launchState.get("boxMetRand");
			if (launchState.get("antwoordString") != null)
				antwoordString = (String) launchState.get("antwoordString");
			if (launchState.get("scoreMax") != null)
				scoreMax = (Integer) launchState.get("scoreMax");
			bordjesMethode = Boolean.TRUE.equals( launchState.get("bordjesMethode"));
			
		}
		else
		{
			if (h.get("exact") != null)
				exact = (Boolean) h.get("exact");
			startString = extractStartString(h);
		}

		if (randomVarNamen != null && randomVarWaarden != null)
		{
			try
			{
				startString = FormuleParser.randomizeString(startString, randomVarNamen, randomVarWaarden);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if (randomVarNamen != null && randomVarWaarden != null)
		{
			try
			{
				antwoordString = FormuleParser.randomizeString(antwoordString, randomVarNamen, randomVarWaarden);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		if (!isVergelijkingVak)
		{
			int index = antwoordString.indexOf("=");
			if (index == -1)
				index = antwoordString.indexOf("\u2248");
			if (index > -1)
			{
				prefix = antwoordString.substring(0, index + 1) + "@";
				hasPrefix = true;
				antwoordString = "$f" + antwoordString.substring(index + 1);
			}

			FormuleViewer f = new FormuleViewer(prefix);
			prefixViewer = f.getAsPanel();
			prefixViewer.getElement().getStyle().setProperty("display", "inline-block");
			prefixViewer.getElement().getStyle().setProperty("clear", "both");
			prefixViewer.getElement().getStyle().setMarginLeft(23, Unit.PX);
		}
		
	}

	public void zetInstellingen(HashMap<String, Object> instellingen)
	{
		this.instellingen = instellingen;
	}

	public String extractStartString(HashMap<String, Object> h)
	{
		String result = "";
		String answer = "";

		if (h.get("antwoordString") != null)
			answer = (String) h.get("antwoordString");

		for (int i = 0; i < answer.length(); i++)
		{
			if (answer.charAt(i) == '#')
			{
				result = answer.substring(0, i + 1);
				break;
			}
		}
		return result;
	}

	public FormuleEditor getEditor()
	{
		return editor;
	}

	public void lastStep(String useranswer)
	{
		if (correct)
			return;
		FlowPanel current = stepPanels.get(stepPanels.size() - 1);
		highLight(current, false);
		current.remove(editor.getAsPanel());
		if (hasPrefix)
			current.remove(prefixViewer);
		tb.getElement().getStyle().setVisibility(Visibility.VISIBLE);

		FormuleViewer fv = new FormuleViewer(prefix.substring(2, prefix.length() - 1) + useranswer.substring(2, useranswer.length() - 1));

		fv.showResult(fv.CORRECT);
		if (latest_answer_viewer != null)
		{
			latest_answer_viewer.showResult(fv.NONE);

		}
		latest_answer_viewer = fv;
		viewers.add(fv);
		Panel p = fv.getAsPanel();
		p.getElement().getStyle().setProperty("display", "inline");
		contentPanel.remove(feedbackPanel);
		current.add(p);
		if (hasFeedback)
			contentPanel.add(feedbackPanel);
		nagekeken = true;
		correct = true;
		score = scoreMax;
		comRoot.setChanged();
	}

	public void addStep(String useranswer)
	{
		contentPanel.remove(feedbackPanel);
		FlowPanel current = stepPanels.get(stepPanels.size() - 1);
		current.remove(editor.getAsPanel());
		if (hasPrefix)
			current.remove(prefixViewer);
		highLight(current, false);
		tb.getElement().getStyle().setVisibility(Visibility.VISIBLE);

		//System.out.println(" useranswer1: "+ useranswer);
		if (!isVergelijkingVak && !hasPrefix && (useranswer.charAt(useranswer.length() - 2)) != '=')
			useranswer = useranswer.substring(0, useranswer.length() - 1) + "=@";
		//System.out.println(" useranswer2: "+ useranswer);
		FormuleViewer fv = new FormuleViewer(prefix.substring(2, prefix.length() - 1) + useranswer.substring(2, useranswer.length() - 1));
		//System.out.println(" useranswer3: "+ useranswer);
		fv.showResult(fv.ALMOSTCORRECT);
		if (latest_answer_viewer != null && !(hasStartString && steps == 1))
			latest_answer_viewer.showResult(fv.NONE);
		latest_answer_viewer = fv;
		viewers.add(fv);
		Panel p = fv.getAsPanel();
		p.getElement().getStyle().setProperty("display", "inline");
		current.add(p);
		if(bordjesMethode)
			addFormulePanelListeners((TouchPanel) p, fv); 

		FlowPanel stepPanel = new FlowPanel();
		layoutStepPanel(stepPanel);
		highLight(stepPanel, true);
		steps++;

		if (hasFeedback)
			contentPanel.add(feedbackPanel);

		if (hasPrefix)
			stepPanel.add(prefixViewer);
		editor = addNewEditor(stepPanel);
		editor.requestFocus();
		contentPanel.add(stepPanel);
		stepPanels.add(stepPanel);
		
		sp.getElement().setScrollTop(sp.getElement().getScrollHeight());
	}

	public void copyStep()
	{
		if (!correct)
		{
			String currentTekst = "";
			if (steps > 0)
			{
				editor.getMainRegel().deleteAll();
				currentTekst = viewers.get(steps - 1).toString();
				if (hasPrefix)
					currentTekst = removePrefix(currentTekst);
				currentTekst = removeIsTeken(currentTekst);
				editor.insert(currentTekst);
			}
		}
		return;

	}

	public void backStep()
	{
		FlowPanel current = stepPanels.get(steps);

		if (!correct && editor.toString().length() > 0)
		{
			current.remove(editor.getAsPanel());
			editor = addNewEditor(current);
		}
		else if (correct)
		{
			contentPanel.remove(feedbackPanel);
			current.remove(viewers.get(viewers.size() - 1).getAsPanel());
			current.getElement().getStyle().setBackgroundColor(hlColor.toString());
			viewers.remove(viewers.get(viewers.size() - 1));
			String currentTekst = latest_answer_viewer.toString();
			if (hasPrefix)
				currentTekst = removePrefix(currentTekst);
			editor = addNewEditor(current);
			editor.insert(currentTekst);
			if (steps > 1 || steps > 0 && !hasStartString)
				latest_answer_viewer = viewers.get(viewers.size() - 1);
			correct = false;
			comRoot.setChanged();
		}
		else if (steps > 1 || steps > 0 && !hasStartString)
		{
			contentPanel.remove(feedbackPanel);
			String currentTekst = latest_answer_viewer.toString();
			currentTekst = removeIsTeken(currentTekst);
			if (hasPrefix)
				currentTekst = removePrefix(currentTekst);
			contentPanel.remove(current);
			stepPanels.remove(stepPanels.size() - 1);

			current = stepPanels.get(stepPanels.size() - 1);
			current.getElement().getStyle().setBackgroundColor(hlColor.toString());

			current.remove(viewers.get(viewers.size() - 1).getAsPanel());
			viewers.remove(viewers.get(viewers.size() - 1));
			if (steps > 2 || steps > 1 && !hasStartString)
				latest_answer_viewer = viewers.get(viewers.size() - 1);

			editor = addNewEditor(current);
			editor.insert(currentTekst);

			steps--;
			if (steps == 0)
			{
				tb.getElement().getStyle().setVisibility(Visibility.HIDDEN);
			}

		}
	}

	public void setFeedback(String feedback)
	{
		hasFeedback = !"".equals(feedback.trim());
		feedbackPanel.clear();
		feedbackPanel.getElement().setInnerHTML(feedback);
		feedbackPanel.getElement().getStyle().setPadding(10, Unit.PX);
	}

	public void setAndAddFeedback(String feedback)
	{
		hasFeedback = !"".equals(feedback.trim());
		feedbackPanel.clear();
		contentPanel.remove(feedbackPanel);
		feedbackPanel.getElement().setInnerHTML(feedback);
		feedbackPanel.getElement().getStyle().setPadding(10, Unit.PX);
		if (hasFeedback)
			contentPanel.add(feedbackPanel);
	}

	public Panel getAsPanel()
	{
		mainPanel = new FlowPanel();
		mainPanel.getElement().getStyle().setWidth(breedte, Unit.PX);
		mainPanel.getElement().getStyle().setHeight(hoogte, Unit.PX);
		mainPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		mainPanel.getElement().getStyle().setBorderColor("gray");
		mainPanel.getElement().getStyle().setBackgroundColor(CssColor.make(240, 240, 240).toString());
		mainPanel.getElement().getStyle().setBorderWidth(boxMetRand ? 1 : 0, Unit.PX);
		mainPanel.getElement().getStyle().setProperty("lineHeight", "2.0");

		Image buttonImg = new Image("images/resources/pijlterug.gif");
		buttonImg.getElement().getStyle().setMargin(2, Unit.PX);
		tb = new TouchButton();
		tb.add(buttonImg);
		tb.getElement().getStyle().setFloat(Style.Float.RIGHT);
		tb.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		addButtonHandler(tb);

		Image copyButtonImg = new Image("images/resources/pijlcopy.gif");
		copyButtonImg.getElement().getStyle().setMargin(2, Unit.PX);
		copyButton = new TouchButton();
		copyButton.add(copyButtonImg);
		copyButton.getElement().getStyle().setFloat(Style.Float.RIGHT);
		addCopyButtonHandler(copyButton);

		mainPanel.add(copyButton);
		mainPanel.add(tb);

		sp = new ScrollPanel();

		sp.getElement().getStyle().setWidth(breedte - 5, Unit.PX);
		sp.getElement().getStyle().setHeight(hoogte - 50, Unit.PX);
		sp.getElement().getStyle().setOverflow(Overflow.AUTO);
		sp.getElement().getStyle().setFloat(Style.Float.LEFT);

		contentPanel = new FlowPanel();
		contentPanel.getElement().getStyle().setPadding(5, Unit.PX);
		contentPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		contentPanel.getElement().getStyle().setProperty("display", "block");

		feedbackPanel = new FlowPanel();
		feedbackPanel.getElement().getStyle().setFontSize(14, Unit.PX);
		feedbackPanel.getElement().getStyle().setProperty("lineHeight", "1.2");
		feedbackPanel.getElement().getStyle().setWidth(breedte - 25, Unit.PX);
		feedbackPanel.getElement().getStyle().setProperty("display", "inline-block");
		feedbackPanel.getElement().getStyle().setBackgroundColor("#FFFFDD");

		sp.setWidget(contentPanel);
		mainPanel.add(sp);

		FlowPanel stepPanel = new FlowPanel();
		layoutStepPanel(stepPanel);
		highLight(stepPanel, true);
		if (hasPrefix)
			stepPanel.add(prefixViewer);

		if (!startString.equals("$f@") && steps == 0)
		{
			if(bordjesMethode) {
				// convert to stringStrikt.
				VergelijkingMeerv e = FormuleParser.parseVergelijking(startString);
				startString = "$f"+ e.toStringStrikt() + "@";
			}
			
			
			
			
			if (!isVergelijkingVak && !hasPrefix && (startString.charAt(startString.length() - 2)) != '=')
				startString = startString.substring(0, startString.length() - 1) + "=@";

			hasStartString = true;
			//System.out.println("prefix+startstring: " + prefix.substring(2, prefix.length() - 1) + startString.substring(2, startString.length() - 1));

			//System.out.println("waarden: "+randomVarWaarden);
			//System.out.println("randvarnamen: "+randomVarNamen[0]);
			FormuleViewer f = new FormuleViewer(prefix.substring(2, prefix.length() - 1) + startString.substring(2, startString.length() - 1));
			f.getAsPanel().getElement().getStyle().setMarginLeft(23, Unit.PX);

			viewers.add(f);
			Panel pnl = f.getAsPanel();
			pnl.getElement().getStyle().setProperty("display", "inline-block");
			pnl.getElement().getStyle().setProperty("clear", "both");
			stepPanel.add(pnl);
			if(bordjesMethode){
				Logger.getLogger("FormuleEditorWithStep").info("bordjesmethode");
				addFormulePanelListeners((TouchPanel) pnl, f);
			}
			highLight(stepPanel, false);
			contentPanel.add(stepPanel);
			stepPanels.add(stepPanel);

			steps++;

			FlowPanel stepPanelNew = new FlowPanel();
			layoutStepPanel(stepPanelNew);
			highLight(stepPanelNew, true);

			if (hasPrefix)
				stepPanelNew.add(prefixViewer);
			editor = addNewEditor(stepPanelNew);

			contentPanel.add(stepPanelNew);
			stepPanels.add(stepPanelNew);
		}
		else
		{
			if (hasPrefix)
				stepPanel.add(prefixViewer);
			editor = addNewEditor(stepPanel);
			contentPanel.add(stepPanel);
			stepPanels.add(stepPanel);
		}

		contentPanel.getElement().addClassName("insert_formule_steps");

		return mainPanel;
	}

	public Boolean getExact()
	{
		return exact;
	}

	public String removePrefix(String s)
	{
		int index = s.indexOf("=");
		if (index == -1)
			index = s.indexOf("\u2248");
		if (index > -1)
		{
			s = s.substring(index + 1);
		}
		return s;
	}

	public String removeIsTeken(String s)
	{
		if (s.charAt(s.length() - 1) == '=' || s.charAt(s.length() - 1) == '\u2248')
		{
			int isIndex = s.length() - 1;
			s = s.substring(0, isIndex);
		}
		return s;
	}

	public FormuleEditorWithAnswer addNewEditor(Panel p)
	{
		FormuleEditorWithAnswer editor = new FormuleEditorWithAnswer(h, isVergelijkingVak, this, randomVarNamen, randomVarWaarden);
		if (!hasPrefix)
			editor.getAsPanel().getElement().getStyle().setMarginLeft(13, Unit.PX);
		editor.getAsPanel().getElement().getStyle().setMarginTop(5, Unit.PX);
		editor.setFont(defaultfont);
		TouchPanel tp = (TouchPanel) editor.getAsPanel();
		tp.getElement().getStyle().setProperty("display", "inline-block");
		editor.setCurrent(0, 0);
		editor.requestFocus();
		if (hasPrefix)
			p.add(prefixViewer);
		p.add(tp);
		addFormulePanelListeners(tp, editor);
		return editor;
	}

	public void layoutStepPanel(Widget w)
	{
		w.getElement().getStyle().setWidth(breedte - 5, Unit.PX);
		w.getElement().getStyle().setFloat(Float.LEFT);
		w.getElement().getStyle().setProperty("clear", "both");
		w.getElement().getStyle().setProperty("display", "block");
		w.getElement().getStyle().setBackgroundColor(hlColor.toString());
	}

	public void highLight(Widget w, boolean b)
	{
		w.getElement().getStyle().setBackgroundColor(b ? hlColor.toString() : bgColor.toString());
	}

	private void addButtonHandler(final TouchButton tb)
	{
		tb.addTouchHandler(new TouchHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				backStep();
			}

			@Override
			public void onTouchMove(TouchMoveEvent event)
			{
			}

			@Override
			public void onTouchEnd(TouchEndEvent event)
			{
			}

			@Override
			public void onTouchCanceled(TouchCancelEvent event)
			{
			}
		});
	}

	private void addCopyButtonHandler(final TouchButton tb)
	{
		tb.addTouchHandler(new TouchHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				copyStep();
			}

			@Override
			public void onTouchMove(TouchMoveEvent event)
			{
			}

			@Override
			public void onTouchEnd(TouchEndEvent event)
			{
			}

			@Override
			public void onTouchCanceled(TouchCancelEvent event)
			{
			}
		});
	}

	
	class BordjesTouchHandler extends FormuleEditorTouchHandler {

		BordjesTouchHandler(FormuleHolder editor) {
			super(editor);
		}

		@Override
		public void onTouchEnd(TouchEndEvent event) {
			super.onTouchEnd(event);
			if(bordjesMethode)
			{ 
				FormuleEditor ed = FormuleEditorWithSteps.this.editor;
				if(ed != null && editor != ed && editor.hasSelection()) {
					String select = editor.getSelectionString();
					if(select != null && select.length()>0)
					{	ed.clearAll();
						ed.insert(select);
						ed.insert("=");
						editor.clearSelection();
						editor.paint();
					}
				}
			}
		}
	}
	
	
	private void addFormulePanelListeners(final TouchPanel tp, final FormuleHolder editor)
	{
		tp.addTouchHandler(new BordjesTouchHandler(editor));
	}

	@Override
	public HashMap<String, Object> getState()
	{
		int stapNr = 0;
		String[] formuleVakInhouden = null;
		boolean ingevuld = true;
		boolean nagekeken = false;
		String antwoordString;

		editor.check();

		stapNr = steps;
		formuleVakInhouden = new String[steps + 1];
		for (int i = 0; i < steps + 1; i++)
		{
			if (viewers.size() > i && viewers.get(i) != null)
				formuleVakInhouden[i] = "$f" + (viewers.get(i)).toString() + "@" ;
			else
				formuleVakInhouden[i] = "$f@";
		}
		antwoordString = editor.toString();
		ingevuld = this.ingevuld;
		nagekeken = this.nagekeken;

		HashMap<String, Object> h = new HashMap<String, Object>();
		h.put("stapNr", new Integer(stapNr));
		h.put("formuleVakInhouden", formuleVakInhouden);
		h.put("antwoordString", antwoordString);
		h.put("ingevuld", new Boolean(ingevuld));
		h.put("nagekeken", new Boolean(nagekeken));
		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		int stapNr = 0;
		String[] formuleVakInhouden = null;
		boolean ingevuld = true;
		boolean nagekeken = false;
		String antwoordString = "";

		if (h.get("stapNr") != null)
			stapNr = ((Number) h.get("stapNr")).intValue();
		if (h.get("ingevuld") != null)
			ingevuld = (Boolean) h.get("ingevuld");
		if (h.get("nagekeken") != null)
			nagekeken = (Boolean) h.get("nagekeken");
		if (h.get("formuleVakInhouden") != null)
		{
			formuleVakInhouden = Memento.toStringArray(h.get("formuleVakInhouden"));
			for (int i = 0; i < formuleVakInhouden.length; i++) {
				if(formuleVakInhouden[i].startsWith("$f"))
					formuleVakInhouden[i] = formuleVakInhouden[i].substring(2, formuleVakInhouden[i].length()-1);
			}
		}
		if (h.get("antwoordString") != null)
			antwoordString = (String) h.get("antwoordString");

		this.steps = stapNr;
		this.ingevuld = ingevuld;
		this.nagekeken = nagekeken;

		for (int i = 0; i < steps + 1; i++)
		{
			if (i == 0 && hasStartString)
				i++;

			FormuleViewer fv = new FormuleViewer(formuleVakInhouden[i]);
			if (i == steps && nagekeken)
				fv.showResult(FormuleViewer.CORRECT);
			else if (i == steps - 1 && !nagekeken)
				fv.showResult(FormuleViewer.ALMOSTCORRECT);
			else
				fv.showResult(FormuleViewer.NONE);
			viewers.add(fv);
			Panel p = fv.getAsPanel();
			p.getElement().getStyle().setProperty("display", "inline");

			FlowPanel stepPanel = null;
			if (i == 0 || i == 1 && hasStartString)
			{
				stepPanel = stepPanels.get(i);
				stepPanel.remove(editor.getAsPanel());
				if (hasPrefix && (i < steps || nagekeken))
					stepPanel.remove(prefixViewer);
			}
			else
			{
				stepPanel = new FlowPanel();
				layoutStepPanel(stepPanel);
				stepPanels.add(stepPanel);
				if (hasPrefix && !nagekeken)
					stepPanel.add(prefixViewer);

			}

			if ("".equals(formuleVakInhouden[i]))
			{
				viewers.remove(fv);
				editor = addNewEditor(stepPanel);
				editor.insert(antwoordString);
				highLight(stepPanel, true);
			}
			else
			{
				stepPanel.add(p);
				highLight(stepPanel, false);
			}
			contentPanel.add(stepPanel);

			if (viewers.size() > 0)
				latest_answer_viewer = viewers.get(viewers.size() - 1);
			correct = nagekeken;
			score = correct ? scoreMax : 0;

		}

		if (steps > 0 || steps == 0 && !hasStartString)
			tb.getElement().getStyle().setVisibility(Visibility.VISIBLE);

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
