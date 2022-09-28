package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.InlineHTML;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorContext;

public class ScoreWidget extends Composite implements InteractionView, ClickHandler, TekstElementWithFont {

	private HashMap<String, Object> launchState; 
	
	int breedte = 40;
	int hoogte = 300; 
	private boolean volledigeBreedte = false;
	int ashoogte = 12;
	
	int choicePageMode = 0;
    int activiteitNr = 0;
    int paginaNr = 0;
    int activiteitID = 0;
    int moduleID = 0;
    boolean score = false;
    boolean goedFout = false;
    boolean toonTitel = false;
    String paginaTitel = "";
    

    final Anchor anchor;
    final AnchorContext context;
    
    final InlineHTML span;
    
    
	private OpdrNavIF comRoot;

	private final ActivityInterface activity;

	private int fontSize;

	private String fontName;

	private int fontStyle;

	private TekstRegel parent;

	private final Scorm2004IF api;

	private String scoreRaw = "";

	private String successStatus = "";

	private HasSafeHtml html;

	private boolean linkActive = true;
	
	public ScoreWidget(ActivityInterface a, HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden, AnchorContext context)
	{
		this.activity = a;
		this.context = context;
		this.api = a.api();

		if (h != null && h.containsKey("breedte"))
			breedte = ((Number) h.get("breedte")).intValue();
		if (h != null && h.containsKey("hoogte"))
			hoogte = ((Number) h.get("hoogte")).intValue();
		if(h != null && h.containsKey("volledigeBreedte"))
			volledigeBreedte =((Boolean) h.get("volledigeBreedte"));
		if (h != null && h.containsKey("interactiePanelLaunchState"))
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		
		anchor = new Anchor();
		anchor.addClickHandler(this);
		anchor.setStylePrimaryName("scorewidget");
		
		span  = new InlineHTML();
		span.setStylePrimaryName("scorewidget");
				
		init(breedte, hoogte, launchState);
		
		
	}
	
	@Override
	public HashMap<String, Object> getState() {
		HashMap<String, Object> state = new HashMap<>();
		if (score) state.put("score.raw", scoreRaw);
		if (goedFout) state.put("success_status", successStatus);
		return state;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		if (h == null) return;
		if (goedFout && successStatus.isEmpty())
		{	successStatus = h.getOrDefault("success_status", successStatus).toString();
			goedfout(successStatus);
		}
		if (score && scoreRaw.isEmpty()) {
			scoreRaw = h.getOrDefault("score.raw", scoreRaw).toString();
			anchorSetText(scoreRaw);
		}
	}

	interface AnchorTemplate extends SafeHtmlTemplates {
		@Template("<span class='scorewidget-titel'>{0}</span><span class='scorewidget-score'>{1}</span>")
		SafeHtml titleScore(String title, String score);
	}
	
	static final AnchorTemplate ANCHOR_TEMPLATE = GWT.create(AnchorTemplate.class);
	
	private void anchorSetText(String score) {
		html.setHTML(ANCHOR_TEMPLATE.titleScore(paginaTitel, score));
	}

	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public int[][] getScoreObjectives() {
		return null;
	}

	@Override
	public Boolean isCorrect() {
		return null;
	}

	@Override
	public void kijkNa() {
	}

	@Override
	public void zetNagekeken(boolean b) {
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		if(volledigeBreedte) {
		}
		
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

	@Override
	public void setAsHoogte(int ashoogte) {
		// TODO Auto-generated method stub
		
	}

	
	public void init(int width, int height, Map<String, Object> launchData) {
		api.SetValue("cmi.exit", "suspend");
		Promise<String> start = api.Commit();
		ObjectMap map = JSONUtilities.wrapMap(launchData);
		if (map != null)
		{
			 if(map.containsKey("choicePageMode"))
			      choicePageMode = map.getInt("choicePageMode");
			 if(map.containsKey("activiteitNr"))
				 activiteitNr = map.getInt("activiteitNr");
			 activiteitNr = Math.max(1, activiteitNr);
			 if(map.containsKey("paginaNr"))
				 paginaNr = map.getInt("paginaNr");
			 paginaNr = Math.max(paginaNr, 1);
			 if(map.containsKey("activiteitID"))
				 activiteitID = map.getInt("activiteitID");
			 if(map.containsKey("moduleID"))
				 moduleID = map.getInt("moduleID");
			 
			 if(map.containsKey("score"))
				 score = map.getBoolean("score");
			 if(map.containsKey("goedFout"))
				 goedFout = map.getBoolean("goedFout");
			 toonTitel = map.getBoolean("toonTitel", toonTitel);
			 if (toonTitel && map.containsKey("paginaTitel"))
				 paginaTitel = map.getString("paginaTitel");
			 linkActive = map.getBoolean("linkActive", linkActive);
		}
		html = linkActive ? anchor : span;
		if (!linkActive) initWidget(span);else initWidget(anchor);
		setStyleDependentName("goedFout", goedFout);
		
		String pfx0 = "dme.scorewidget.";
		switch (choicePageMode) {
		case 0: 
			anchor.setHref("goto:." + (paginaNr));
			pfx0 += "cs"; break;
		case 1:
			anchor.setHref("goto:" + activiteitNr + "." + paginaNr);
			pfx0 += "cc." + activiteitNr; break;			
		case 2: 
			anchor.setHref("#xs:" + activiteitID + "." + (paginaNr-1));
			pfx0 += "s." + activiteitID; break;
		case 3:
			anchor.setHref("#xc:" + moduleID + "." + activiteitNr + "." + (paginaNr-1));
			pfx0 += "c." + moduleID + "." + activiteitNr; break;
		default: pfx0 += "null";
		}
		final String pfx = pfx0 + "." + paginaNr;
		
		if (score) {
			Promise<String> result;
			result = start.then( p -> api.getValuePromise(pfx + ".score.raw"));
			anchorSetText("0");
			result.then(this::doScore);
		} else {
			anchorSetText("");
		}
		if (goedFout) {
			Promise<String> result;
			result = start.then(p -> api.getValuePromise(pfx + ".success_status"));
			result.then(this::doGoedFout);			
		}
	}

	Promise<String> doScore(Promise<String> p) {
		String value = p.getValue();
		if (value == null || value.isEmpty()) value = "0";
		this.scoreRaw = value;
		anchorSetText(value);
		return p;
	}
	
	Promise<String> doGoedFout(Promise<String> p) {
		String value = p.getValue();
		this.successStatus = value;
		goedfout(value);
		return p;
	}

	private void goedfout(String value) {
		removeStyleDependentName("goedFout-passed");
		removeStyleDependentName("goedFout-failed");
		addStyleDependentName("goedFout-" + value);
	}
	
	
	@Override
	public void onClick(ClickEvent event) {
		context.prepareLeave();
		if (anchor.getHref().startsWith("goto:")) {
			event.preventDefault();
			context.gotoUrl(anchor.getHref());
		}
	}

	@Override
	public void setFontSize(int font_size) {
		this.fontSize = font_size;
	}

	@Override
	public void setFontName(String font_name) {
		this.fontName = font_name;
	}

	@Override
	public void setFontStyle(int font_style) {
		this.fontStyle = font_style;
	}

	@Override
	public void setParentRegel(TekstRegel regel) {
		this.parent = regel;
		String color = regel.getElement().getStyle().getColor();
		getElement().getStyle().setColor(color);
	}

}
