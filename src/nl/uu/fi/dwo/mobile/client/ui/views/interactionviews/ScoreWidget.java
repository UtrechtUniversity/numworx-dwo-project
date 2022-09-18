package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
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

    final Anchor anchor;
    final AnchorContext context;
    
	private OpdrNavIF comRoot;

	private final ActivityInterface activity;

	private int fontSize;

	private String fontName;

	private int fontStyle;

	private TekstRegel parent;

	private final Scorm2004IF api;

	private String scoreRaw = "";

	private String successStatus = ""; 
	
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
		initWidget(anchor);
		
		
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
		if (goedFout && successStatus.isEmpty())
		{	successStatus = h.getOrDefault("success_status", successStatus).toString();
			goedfout(successStatus);
		}
		if (score && scoreRaw.isEmpty()) {
			scoreRaw = h.getOrDefault("score.raw", scoreRaw).toString();
			anchor.setText(scoreRaw);
		}
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
			 if(map.containsKey("paginaNr"))
				 paginaNr = map.getInt("paginaNr");
			 if(map.containsKey("activiteitID"))
				 activiteitID = map.getInt("activiteitID");
			 if(map.containsKey("moduleID"))
				 moduleID = map.getInt("moduleID");
			 
			 if(map.containsKey("score"))
				 score = map.getBoolean("score");
			 if(map.containsKey("goedFout"))
				 goedFout = map.getBoolean("goedFout");
		}
		anchor.setStyleDependentName("goedFout", goedFout);		
		if (score) {
			Promise<String> result = Promises.resolved("");
			switch(choicePageMode) {
			case 2: // activiteit id met pagina nr
				result = 
				start.then( p -> api.getValuePromise("dme.scorewidget.s." + activiteitID + "." + (paginaNr) + ".score.raw"));
				anchor.setHref("#s:" + activiteitID + "." + (paginaNr-1));
				break;
			}
			anchor.setText("0");
			result.then(this::doScore);
		}
		if (goedFout) {
			Promise<String> result = Promises.resolved("");
			switch(choicePageMode) {
			case 2: // activiteit id met pagina nr
				result = start.then(p -> api.getValuePromise("dme.scorewidget.s." + activiteitID + "." + (paginaNr) + ".success_status"));
				anchor.setHref("#s:" + activiteitID + "." + (paginaNr-1));
				break;
			}
			result.then(this::doGoedFout);			
		}
	}

	Promise<String> doScore(Promise<String> p) {
		String value = p.getValue();
		if (value == null || value.isEmpty()) value = "0";
		this.scoreRaw = value;
		anchor.setText(value);
		return p;
	}
	
	Promise<String> doGoedFout(Promise<String> p) {
		String value = p.getValue();
		this.successStatus = value;
		goedfout(value);
		return p;
	}

	private void goedfout(String value) {
		anchor.removeStyleDependentName("goedFout-passed");
		anchor.removeStyleDependentName("goedFout-failed");
		anchor.addStyleDependentName("goedFout-" + value);
	}
	
	
	@Override
	public void onClick(ClickEvent event) {
		context.prepareLeave();
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
	}

}
