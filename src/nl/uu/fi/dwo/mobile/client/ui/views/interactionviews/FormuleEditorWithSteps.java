package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.FormuleKeyboard;
import nl.uu.fi.dwo.mobile.client.ui.TouchButton;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
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
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.VergelijkingMeerv;








/**
 * Used for showing formula's that can be solved in steps.
 * 
 * @author Evertson Croes
 * 
 */
public class FormuleEditorWithSteps implements InteractionView
{

	private String startString = "$f@";
	private String antwoordString = "$f@";
	private String prefix = "$f@";
	private boolean hasPrefix = false;
	boolean hasStartString = false;
	boolean boxMetRand = true;
	private Boolean exact = false;
	private int breedte = 600;
	private int hoogte = 250;
	private boolean volledigeBreedte = false;
	private HashMap<String, Object> launchState;
	private Map<String, Object> instellingen;
	private ArrayList<FormuleViewer> viewers = new ArrayList<FormuleViewer>();
	private FormuleEditorWithAnswer editor = null;
	private Widget prefixViewer;
	private FormuleViewer latest_answer_viewer;
	private ScrollPanel sp = null;
	
	private LayoutPanel contentPanel = null;
	//contentPanel is layoutPanel geworden om pijlvakken (met operatoren, abc, substitutie, etc) neer te kunnen zetten.
	//private FlowPanel contentPanel = null;
	private FlowPanel feedbackPanel = null;
	int feedbackPanelHeight = 34;
	private FlowPanel mainPanel = null;
	private OpdrNavIF comRoot;
	private int mode;
	
	//private PijlVak[] pijlVakken;
	private PijlVak pijlVak;
	private boolean pijl = true;
	//private int pijlX = "GR".equals(WiskOpdr.deployVariant) ? 105 : 130;
	private int pijlX = 130;
	//private int stepPanelX = 0;
	private int stepPanelY = 0; //locatie van bovenrand van het laatste (onderste) stepPanel
	private int stapH = 15;
	
	
	private TouchButton tb = null;
	private TouchButton copyButton = null;
	private TouchButton plusKnop, minKnop, maalKnop, deelKnop, haakjesKnop, herleidKnop, abcKnop, subKnop;
	private TouchButton ontbindKnop, splitsKnop, wortelBewerkKnop;
	private boolean abcVisible, subVisible;
	private int stapNr = 0;
	protected HashMap<String, Object> h = null;
	protected String[] randomVarNamen = null;
	protected HashMap randomVarWaarden = null;
	private ArrayList<FlowPanel> stepPanels = new ArrayList<FlowPanel>();
	private ArrayList<PijlVak> pijlVakken = new ArrayList<PijlVak>();
	private FormuleFont font = FormuleFont.createFromFontSize(16);

	private int score;
	private int scoreMax;
	private Boolean correct;
	
	private boolean stapOk;

	private static FormuleFont defaultfont = FormuleFont.createFromFontSize(18);
	//private boolean answeredCorrectly = false;
	private CssColor hlColor = CssColor.make(255, 255, 255);
	private CssColor bgColor = CssColor.make(240, 240, 240);
	private boolean ingevuld;
	private boolean nagekeken;
	private boolean hasFeedback;

	protected boolean isVergelijkingVak = false;
	private PopupFacade facade;
	
	private boolean bordjesMethode;
	private boolean linStrategieVersie; // TODO implement this

	public FormuleEditorWithSteps(HashMap<String, Object> h, boolean isVergelijkingVak, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		this.isVergelijkingVak = isVergelijkingVak;

		if (h != null)
			this.h = h;
		ObjectMap map = JSONUtilities.wrapMap(h);
		if(map.containsKey("breedte"))
			breedte = map.getInt("breedte");
		if(map.containsKey("hoogte"))
			hoogte = map.getInt("hoogte");
		if(map.containsKey("volledigeBreedte"))
			volledigeBreedte = map.getBoolean("volledigeBreedte");
		
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
				scoreMax = ((Number) launchState.get("scoreMax")).intValue();
			if (launchState.containsKey("abcKnop"))
				abcVisible = ((Boolean) launchState.get("abcKnop")).booleanValue();
			if (launchState.containsKey("pijl"))
				pijl = ((Boolean) launchState.get("pijl")).booleanValue();
			
			
			
			
			bordjesMethode = Boolean.TRUE.equals( launchState.get("bordjesMethode"));
			linStrategieVersie = Boolean.TRUE.equals(launchState.get("linStrategieVersie"));
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

	public void zetInstellingen(Map<String, Object> instellingen)
	{
		this.instellingen = instellingen;
	}

	public String extractStartString(HashMap<String, Object> h)
	{
		String result = "$f@";
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
		if (correct == Boolean.TRUE)
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
		{	contentPanel.add(feedbackPanel);
			contentPanel.setWidgetLeftRight(feedbackPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(feedbackPanel, stepPanelY + fv.getHeight(), Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX); //TODO: kijken of dit werkt; offsetHeight is mogelijk 0.	contentPanel.add(feedbackPanel);
		}
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
// bordjes methode parse string to strikt.
		if(bordjesMethode) {
			// convert to stringStrikt.
			VergelijkingMeerv e = FormuleParser.parseVergelijking(useranswer);
			useranswer = "$f"+ e.toStringStrikt() + "@";
		}
		FormuleViewer fv = new FormuleViewer(prefix.substring(2, prefix.length() - 1) + useranswer.substring(2, useranswer.length() - 1));
		//System.out.println(" useranswer3: "+ useranswer);
		fv.showResult(fv.ALMOSTCORRECT);
		if (latest_answer_viewer != null && !(hasStartString && stapNr == 1))
			latest_answer_viewer.showResult(fv.NONE);
		latest_answer_viewer = fv;
		viewers.add(fv);
		Panel p = fv.getAsPanel();
		p.getElement().getStyle().setProperty("display", "inline");
		current.add(p);
		if(bordjesMethode)
			addFormulePanelListeners((TouchPanel) p, fv); 

		pijlVak = new PijlVak("", this); //new PijlVak("implicatie");
		int y = stepPanelY + fv.getHeight()/2;
				//formuleVakken[stapNr].getLocation().y + formuleVakken[stapNr].getSize().height / 2;
		if (pijl)
		{	contentPanel.add(pijlVak);
			contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, pijlX, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(pijlVak, y, Style.Unit.PX, pijlVak.getHeight(), Style.Unit.PX);
		}
		pijlVakken.add(pijlVak);
		pijlVak.paintComponent();
		
		//pijlVak = pijlVakken[stapNr];
		
		
		
		FlowPanel stepPanel = new FlowPanel();
		layoutStepPanel(stepPanel);
		highLight(stepPanel, true);
		stapNr++;

		if (hasFeedback)
		{	contentPanel.add(feedbackPanel);
			contentPanel.setWidgetLeftRight(feedbackPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(feedbackPanel, stepPanelY + fv.getHeight(), Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX); 
		}

		if (hasPrefix)
			stepPanel.add(prefixViewer);
		editor = addNewEditor(stepPanel);
		stepPanelY += fv.getHeight() + stapH;
		contentPanel.add(stepPanel);
		contentPanel.setWidgetLeftRight(stepPanel, 5, Style.Unit.PX, 5, Style.Unit.PX); 
		contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
		stepPanels.add(stepPanel);
		//stepPanelY += editor.getHeight() + stapH;
		
		
		sp.getElement().setScrollTop(sp.getElement().getScrollHeight());
		editor.requestFocus();
	}
	
	public void resize()
	{
		if(editor != null)
		{	FlowPanel current = stepPanels.get(stepPanels.size() - 1);
			contentPanel.setWidgetTopHeight(current, stepPanelY, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
			if(feedbackPanel.isAttached())
			{
				contentPanel.setWidgetTopHeight(feedbackPanel, stepPanelY + editor.getHeight(), Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX);
			}
			//stepPanelY = current.getAbsoluteTop() - contentPanel.getAbsoluteTop() + editor.getHeight() + stapH;
			//System.out.println("resize: stepPanelY = " + stepPanelY);
		}
	}

	public void copyStep()
	{
		if ( correct != Boolean.TRUE)
		{
			String currentTekst = "";
			if (stapNr > 0)
			{
				editor.getMainRegel().deleteAll();
				currentTekst = viewers.get(stapNr - 1).toString();
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
		FlowPanel current = stepPanels.get(stapNr);

		if (correct != Boolean.TRUE && editor.toString().length() > 0)
		{
			current.remove(editor.getAsPanel());
			editor = addNewEditor(current);
		}
		else if (correct == Boolean.TRUE)
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
			if (stapNr > 1 || stapNr > 0 && !hasStartString)
				latest_answer_viewer = viewers.get(viewers.size() - 1);
			correct = null;
			comRoot.setChanged();
		}
		else if (stapNr > 1 || stapNr > 0 && !hasStartString)
		{
			contentPanel.remove(feedbackPanel);
			String currentTekst = latest_answer_viewer.toString();
			currentTekst = removeIsTeken(currentTekst);
			if (hasPrefix)
				currentTekst = removePrefix(currentTekst);
			contentPanel.remove(current);
			stepPanelY = stepPanelY - stapH - viewers.get(viewers.size() - 1).getHeight();
			stepPanels.remove(stepPanels.size() - 1);

			current = stepPanels.get(stepPanels.size() - 1);
			current.getElement().getStyle().setBackgroundColor(hlColor.toString());

			current.remove(viewers.get(viewers.size() - 1).getAsPanel());
			viewers.remove(viewers.get(viewers.size() - 1));
			if (stapNr > 2 || stapNr > 1 && !hasStartString)
				latest_answer_viewer = viewers.get(viewers.size() - 1);

			editor = addNewEditor(current);
			editor.insert(currentTekst);

			stapNr--;
			if (stapNr == 0)
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
		feedbackPanelHeight = 34;
		//TODO: hoogte feedbackPanel bepalen.
	}

	public void setAndAddFeedback(String feedback)
	{
		hasFeedback = !"".equals(feedback.trim());
		feedbackPanel.clear();
		contentPanel.remove(feedbackPanel);
		feedbackPanel.getElement().setInnerHTML(feedback);
		feedbackPanel.getElement().getStyle().setPadding(10, Unit.PX);
		feedbackPanelHeight= 34;
		if (hasFeedback)
		{	contentPanel.add(feedbackPanel);
			contentPanel.setWidgetLeftRight(feedbackPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			int height = editor.getHeight();
			if(height < 23)
				height = 23;
			//int height = viewers.isEmpty() ? 23 : viewers.get(viewers.size() - 1).getHeight();
			contentPanel.setWidgetTopHeight(feedbackPanel, stepPanelY + height, Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX); 
		}
	}

	public Panel getAsPanel()
	{
		mainPanel = new FlowPanel();
		mainPanel.getElement().getStyle().setWidth(breedte - 2, Unit.PX);//-2 om ook rand zichtbaar te krijgen
		mainPanel.getElement().getStyle().setHeight(hoogte - 2, Unit.PX);//-2 om ook rand zichtbaar te krijgen
		mainPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		mainPanel.getElement().getStyle().setBorderColor("gray");
		mainPanel.getElement().getStyle().setBackgroundColor(CssColor.make(240, 240, 240).toString());
		mainPanel.getElement().getStyle().setBorderWidth(boxMetRand ? 1 : 0, Unit.PX);
		mainPanel.getElement().getStyle().setProperty("lineHeight", "2.0");

		Image buttonImg = new Image(DWOplayer.DWO_BUNDLE.pijlterug().getSafeUri());
		buttonImg.getElement().getStyle().setMargin(2, Unit.PX);
		tb = new TouchButton();
		tb.add(buttonImg);
		tb.getElement().getStyle().setFloat(Style.Float.RIGHT);
		tb.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		addButtonHandler(tb);

		Image copyButtonImg = new Image(DWOplayer.DWO_BUNDLE.pijlcopy().getSafeUri());
		copyButtonImg.getElement().getStyle().setMargin(2, Unit.PX);
		copyButton = new TouchButton();
		copyButton.add(copyButtonImg);
		copyButton.getElement().getStyle().setFloat(Style.Float.RIGHT);
		addCopyButtonHandler(copyButton);
		
		Image abcKnopImg = new Image(DWOplayer.DWO_BUNDLE.abcknop().getSafeUri());
		abcKnop = new TouchButton();
		abcKnop.add(abcKnopImg);
		//abcKnop.setText("abc");
		abcKnop.getElement().getStyle().setFloat(Style.Float.RIGHT);
		//abcKnop.getElement().getStyle().setBackgroundColor("red");
		addAbcButtonHandler(abcKnop);

		mainPanel.add(copyButton);
		mainPanel.add(tb);
		
		mainPanel.add(abcKnop);
		abcKnop.setVisible(abcVisible);
		
		sp = new ScrollPanel();

		sp.getElement().getStyle().setWidth(breedte - 5, Unit.PX);
		sp.getElement().getStyle().setHeight(hoogte - 50, Unit.PX);
		sp.getElement().getStyle().setOverflow(Overflow.AUTO);
		sp.getElement().getStyle().setFloat(Style.Float.LEFT);

		contentPanel = new LayoutPanel();
		//onderstaande nog nodig?
		//contentPanel.getElement().getStyle().setPadding(5, Unit.PX);
		//contentPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		//contentPanel.getElement().getStyle().setProperty("display", "block");

		feedbackPanel = new FlowPanel();
		feedbackPanel.getElement().getStyle().setFontSize(14, Unit.PX);
		feedbackPanel.getElement().getStyle().setProperty("lineHeight", "1.2");
		feedbackPanel.getElement().getStyle().setWidth(breedte - 25, Unit.PX);
		feedbackPanel.getElement().getStyle().setProperty("display", "inline-block");
		feedbackPanel.getElement().getStyle().setBackgroundColor("#FFFFDD");
		feedbackPanel.getElement().getStyle().setFontSize(XMLView.getDefaultFontSize(), Style.Unit.PX);

		sp.setWidget(contentPanel);
		mainPanel.add(sp);

		//pijlVakken = new PijlVak[100];
		
		
		FlowPanel stepPanel = new FlowPanel();
		layoutStepPanel(stepPanel);
		highLight(stepPanel, true);
		if (hasPrefix)
			stepPanel.add(prefixViewer);

		if (!startString.equals("$f@") && stapNr == 0)
		{
			if(bordjesMethode && isVergelijkingVak) {
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
			latest_answer_viewer = f;
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
			contentPanel.setWidgetLeftRight(stepPanel, 5, Style.Unit.PX, 5, Style.Unit.PX); 
			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, f.getHeight(), Style.Unit.PX);
			stepPanels.add(stepPanel);

			pijlVak = new PijlVak("", this);
			int y = stepPanelY + f.getHeight()/2;
			if(pijl)
			{	contentPanel.add(pijlVak);
				contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, pijlX, Style.Unit.PX);
				contentPanel.setWidgetTopHeight(pijlVak, y, Style.Unit.PX, pijlVak.getHeight(), Style.Unit.PX);
			}
			
			pijlVakken.add(pijlVak);
			pijlVak.paintComponent();
			
			stapNr++;
			stepPanelY += f.getHeight() + stapH;

			FlowPanel stepPanelNew = new FlowPanel();
			layoutStepPanel(stepPanelNew);
			highLight(stepPanelNew, true);

			if (hasPrefix)
				stepPanelNew.add(prefixViewer);
			editor = addNewEditor(stepPanelNew);

			contentPanel.add(stepPanelNew);
			contentPanel.setWidgetLeftRight(stepPanelNew, 5, Style.Unit.PX, 5, Style.Unit.PX); 
			contentPanel.setWidgetTopHeight(stepPanelNew, stepPanelY, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
			
			stepPanels.add(stepPanelNew);
		}
		else
		{
			if (hasPrefix)
				stepPanel.add(prefixViewer);
			editor = addNewEditor(stepPanel);
			contentPanel.add(stepPanel);
			contentPanel.setWidgetLeftRight(stepPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
			stepPanels.add(stepPanel);
		}

		contentPanel.getElement().addClassName("insert_formule_steps");

		return mainPanel;
	}

	public Boolean getExact()
	{
		return exact;
	}
	
	public void zetEditorTerug()
	{
		FlowPanel current = stepPanels.get(stepPanels.size() - 1);
		current.add(editor.getAsPanel());
		editor.requestFocus();
	}
	
	public String getLatestAnswer()
	{
		return latest_answer_viewer.toString();
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
		FormuleEditorWithAnswer editor = editorInstance();
		editor.setFormuleToolBijFocus(true);
		if (!hasPrefix)
			editor.getAsPanel().getElement().getStyle().setMarginLeft(13, Unit.PX);
		editor.getAsPanel().getElement().getStyle().setMarginTop(5, Unit.PX);
		editor.setFont(defaultfont);
		TouchPanel tp = (TouchPanel) editor.getAsPanel();
		tp.getElement().getStyle().setProperty("display", "inline-block");
		editor.setCurrent(0, 0);
		//editor.requestFocus();
		if (hasPrefix)
			p.add(prefixViewer);
		p.add(tp);
		addFormulePanelListeners(tp, editor);
		return editor;
	}

	
	FormuleEditorWithAnswer editorInstance() {
		return new FormuleEditorWithAnswer(h, isVergelijkingVak, this, randomVarNamen, randomVarWaarden);
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
	
	private void addAbcButtonHandler(final TouchButton tb)
	{
		tb.addTouchHandler(new TouchHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				
			}

			@Override
			public void onTouchMove(TouchMoveEvent event)
			{
			}

			@Override
			public void onTouchEnd(TouchEndEvent event)
			{	maakStap("abc");
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
						//editor.clearSelection();
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

		editor.kijkNa();

		stapNr = this.stapNr;
		formuleVakInhouden = new String[stapNr + 1];
		for (int i = 0; i < stapNr + 1; i++)
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
		Boolean nagekeken = null;
		String antwoordString = "";

		if (h.get("stapNr") != null)
			stapNr = ((Number) h.get("stapNr")).intValue();
		if (h.get("ingevuld") != null)
			ingevuld = (Boolean) h.get("ingevuld");
		if (h.get("nagekeken") != null)
			nagekeken = (Boolean) h.get("nagekeken");
		if (h.get("formuleVakInhouden") != null)
		{
			formuleVakInhouden = JSONUtilities.toStringArray(h.get("formuleVakInhouden"));
			for (int i = 0; i < formuleVakInhouden.length; i++) {
				if(formuleVakInhouden[i].startsWith("$f"))
					formuleVakInhouden[i] = formuleVakInhouden[i].substring(2, formuleVakInhouden[i].length()-1);
			}
		}
		if (h.get("antwoordString") != null)
			antwoordString = (String) h.get("antwoordString");

		this.stapNr = stapNr;
		this.ingevuld = ingevuld;
		this.nagekeken = nagekeken;

		for (int i = 0; i < stapNr + 1; i++)
		{
			if (i == 0 && hasStartString)
				i++;

			FormuleViewer fv = new FormuleViewer(formuleVakInhouden[i]);
			if (i == stapNr && nagekeken)
				fv.showResult(FormuleViewer.CORRECT);
			else if (i == stapNr - 1 && !nagekeken)
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
				if (hasPrefix && (i < stapNr || nagekeken))
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
			stepPanel.removeFromParent();
			contentPanel.add(stepPanel);
			contentPanel.setWidgetLeftRight(stepPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
			stepPanelY += editor.getHeight() + stapH;

			if (viewers.size() > 0)
				latest_answer_viewer = viewers.get(viewers.size() - 1);
			correct = nagekeken;
			score = correct == Boolean.TRUE ? scoreMax : 0;

		}
		stepPanelY -= editor.getHeight() - stapH;

		if (stapNr > 0 || stapNr == 0 && !hasStartString)
			tb.getElement().getStyle().setVisibility(Visibility.VISIBLE);

	}
	
	public void kijkNa()
	{
		editor.kijkNa();
	}

	@Override
	public int getScore()
	{
		return score;
	}

	@Override
	public Boolean isCorrect()
	{
		return correct;
	}
	
	/*
	private void maakStap()
	{
		//if (!stappen)
		//	return;

		if (!stapOk && mode != 2 && mode != 3)
		{
			nagekeken = false;
			maakStap("implicatie");
		}
		else if (stapOk || mode == 2 || mode == 3)
		{
			nagekeken = false;
			stapOk = false;
			pijlVakken[stapNr] = new PijlVak("implicatie");
			int y = stepPanelY + editor.getHeight()/2;
					//formuleVakken[stapNr].getLocation().y + formuleVakken[stapNr].getSize().height / 2;
			if (pijl)
			{	contentPanel.add(pijlVakken[stapNr]);
				contentPanel.setWidgetRightWidth(pijlVakken[stapNr], 0, Style.Unit.PX, pijlX, Style.Unit.PX);
				contentPanel.setWidgetTopHeight(pijlVakken[stapNr], y, Style.Unit.PX, pijlVakken[stapNr].getHeight(), Style.Unit.PX);
			}
			pijlVak = pijlVakken[stapNr];

			// formuleVakken[stapNr].setEditable(false);
			if ((mode != 2 && mode != 3) || (hasStartString && stapNr == 0))
				formuleVakken[stapNr].setEditable(false);

			formuleVakken[stapNr + 1] = new FormuleVak();
			formuleVakken[stapNr + 1].setFont(formuleVakFont);
			y = formuleVakken[stapNr].getLocation().y + formuleVakken[stapNr].getSize().height + stapH;
			int x = formuleVakX;
			formuleVakken[stapNr + 1].setLocation(x, y);
			formuleVakken[stapNr + 1].addActionListener(this);
			add(formuleVakken[stapNr + 1]);

			formuleVak = formuleVakken[stapNr + 1];
			formuleVakSimpel = formuleVakken[stapNr + 1];

			stapNr++;
			formuleVak.requestFocus();
		}
	}
	*/

	
	private void maakStap(String operator)
	{
		nagekeken = false;
		//tipGebruikt = false;
		//hulpGebruikt = false;
		if (stapNr == 0 && editor != null && editor.toString().equals("")) 
				//formuleVakken[stapNr] != null && formuleVakken[stapNr].toString().equals("$f@"))
			return;
		if (!stapOk && editor != null && !editor.toString().equals(""))
				//formuleVakken[stapNr] != null && !formuleVakken[stapNr].toString().equals("$f@"))
			return;
		if (operator.equals("implicatie"))//komt dit voor?
		{
			addStep(editor.toString());
			/*
			if (formuleVakken[stapNr] != null)
				remove(formuleVakken[stapNr]);
			if (pijlVakken[stapNr - 1] != null)
				remove(pijlVakken[stapNr - 1]);
			pijlVakken[stapNr - 1] = new PijlVak(operator);
			int y = formuleVakken[stapNr - 1].getLocation().y + formuleVakken[stapNr - 1].getSize().height / 2;
			pijlVakken[stapNr - 1].setLocation(getSize().width - pijlX, y);
			if (pijl)
				add(pijlVakken[stapNr - 1]);
			pijlVak = pijlVakken[stapNr - 1];

			formuleVakken[stapNr] = new FormuleVak();
			formuleVakken[stapNr].setFont(formuleVakFont);
			int x = formuleVakX;
			y = formuleVakken[stapNr - 1].getLocation().y + formuleVakken[stapNr - 1].getSize().height + stapH;
			formuleVakken[stapNr].setLocation(x, y);
			formuleVakken[stapNr].addActionListener(this);
			add(formuleVakken[stapNr]);

			formuleVak = formuleVakken[stapNr];
			formuleVak.requestFocus();
			*/
		}
		else
		{
			if (stapOk)
				stapNr++;
			stapOk = false;
			FlowPanel current = stepPanels.get(stepPanels.size() - 1);
			current.remove(editor.getAsPanel());
			if (hasPrefix)
				current.remove(prefixViewer);
			if(pijlVak != null && pijlVak.isAttached())
				contentPanel.remove(pijlVak);
			//if (pijlVakken[stapNr - 1] != null)
			//	contentPanel.remove(pijlVakken[stapNr - 1]);
			pijlVak = new PijlVak(operator, this);
			
			//pijlVakken[stapNr - 1].addActionListener(this);
			int y = stepPanelY - stapH - editor.getHeight()/2;
			if(pijl)
			{	contentPanel.add(pijlVak);
				contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, pijlX, Style.Unit.PX);
				contentPanel.setWidgetTopHeight(pijlVak, y, Style.Unit.PX, pijlVak.getHeight(), Style.Unit.PX);
				if(operator.equals("abc") || operator.equals("sub"))
					contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, pijlX + 30, Style.Unit.PX);
			}
			
			//pijlVakken[stapNr - 1].setLocation(getSize().width - pijlX, y);
//			if (operator.equals("abc") || operator.equals("sub"))
//				pijlVakken[stapNr - 1].setLocation(getSize().width - pijlX - 30, y);
//			if ("GR".equals(WiskOpdr.deployVariant) && (operator.equals("abc") || operator.equals("sub")))
//				pijlVakken[stapNr - 1].setLocation(getSize().width - pijlX - 60, y);
//			if (pijl)
//				add(pijlVakken[stapNr - 1]);
			pijlVak.paintComponent();
			pijlVakken.add(pijlVak);
			
			
			//pijlVak = pijlVakken[stapNr - 1];
			//editor = pijlVak.getEditor();
			pijlVak.getEditor().requestFocus();
			//formuleVak = pijlVak.formuleVak;
			//pijlVak.requestFocus();

		}
		//repaint();
	}

	
	/*
	private void maakBewerkingStap()
	{
		if (fout)
			return;
		String operator = pijlVak.geefOperator();
		pijlVak.formuleVak.setEditable(false);
		Expressie en = pijlVak.formuleVak.geefExpressie();
		VergelijkingMeerv verg = formuleVakken[stapNr - 1].geefVergelijking();

		// System.out.println("foute vergelijiking?"+formuleVakken[stapNr-1].toString());
		// System.out.println(verg.toString());
		VergelijkingMeerv vergNieuw = null;

		if (linOefenVersie || linStrategieVersie)
		{
			int aantalDelen = verg.geefAantal();
			for (int i = 0; i < aantalDelen && aantalDelen > 0; i++)
			{
				if (formuleVakken[stapNr - 1].partEquationSelected(i))
				{
					vergNieuw = verg.bewerkVergelijking(operator, en, i);
					break;
				}

			}
			if (vergNieuw == null)
				vergNieuw = verg.bewerkVergelijking(operator, en);
		}
		else
			vergNieuw = verg.bewerkVergelijking(operator, en);

		// System.out.println(vergNieuw.toString());

		int x = pijlVak.getLocation().x;
		int y = pijlVak.getLocation().y;
		/*
		 * if(!operator.equals("abc") && !operator.equals("sub") &&
		 * !linStrategieVersie && !linOefenVersie) { remove(pijlVak);
		 * pijlVakken[stapNr-1] = new PijlVak("implicatie"); pijlVak =
		 * pijlVakken[stapNr-1]; pijlVak.setLocation(x,y); if(pijl)add(pijlVak);
		 * }
		 */

		
		/*
		if ((mode != 2 && mode != 3) || (hasStartString && stapNr - 1 == 0))
			formuleVakken[stapNr - 1].setEditable(false);
		// formuleVakken[stapNr-1].setEditable(false);
		if (formuleVakken[stapNr] != null)
			remove(formuleVakken[stapNr]);
		formuleVakken[stapNr] = new FormuleVak();
		formuleVakken[stapNr].setFont(formuleVakFont);

		// y = formuleVakken[stapNr-1].getLocation().y +
		// formuleVakken[stapNr-1].getSize().height +
		// pijlVakken[stapNr-1].getHeight();
		y = formuleVakken[stapNr - 1].getLocation().y + formuleVakken[stapNr - 1].getSize().height + stapH;
		x = formuleVakX;
		formuleVakken[stapNr].setLocation(x, y);
		formuleVakken[stapNr].addActionListener(this);
		add(formuleVakken[stapNr]);

		formuleVak = formuleVakken[stapNr];
		if (!verg.toString().equals(vergNieuw.toString()) || linStrategieVersie)
		{
			if (!linOefenVersie)
			{
				formuleVak.vulVak("$f" + vergNieuw.toString() + "@");
				formuleVak.finish();
			}
		}

		formuleVak.requestFocus();

	}
	*/
	
	

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		mode = comRoot.getMode();
		if(editor != null) editor.zetMode(mode); // FIXME why null? after init?
	}

	@Override
	public Widget asWidget()
	{
		return facade.wrap(getAsPanel());
	}
	
	@Override
	public int getAsHoogte() {
		if(viewers != null && viewers.size() > 0)
			return viewers.get(0).getAsHoogte();
		else
			return 0;
		//hier evt ook nog prefixviewers bij. En iets voor als viewers.get(0) niet bestaat?
	}

	@Override
	public int getHeight() {
		return hoogte;
	}
	protected void setHeight(int h) {
		hoogte = h;
	}
	
	@Override
	public int getWidth() {
		return breedte;
	}
	
	//voor aanpassen breedte in geval van volledigeBreedte
	public void zetVolledigeBreedte(int breedte)
	{
		if(volledigeBreedte)
			this.breedte = breedte;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		viewers.get(0).setAsHoogte(ashoogte);
	}
	
	
}
