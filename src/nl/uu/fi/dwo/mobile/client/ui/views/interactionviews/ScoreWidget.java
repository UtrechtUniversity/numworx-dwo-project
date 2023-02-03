package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHTML;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;

public class ScoreWidget extends Composite implements InteractionView, ClickHandler, TekstElementWithFont {

	private static final String COMPLETION_STATUS = ".completion_status";

	private static final String ENTRY = ".entry";

	private static final String SUCCESS_STATUS = ".success_status";

	public class GoedFoutBezocht implements Success<Map<String, String>, Map<String, String>> {

		private String pfx;

		public GoedFoutBezocht(String pfx) {
			this.pfx = pfx;
		}

		@Override
		public Promise<Map<String, String>> call(Promise<Map<String, String>> resolved) throws Exception {
			Map<String,String> value = resolved.getValue();
			String completed = value.get(pfx + COMPLETION_STATUS);
			String bezocht   = value.getOrDefault(pfx + ENTRY, "");
			String goedfout  = value.getOrDefault(pfx + SUCCESS_STATUS, "");
			if ("completed".equals(completed)) {
				goedfout(goedfout);
			} else {
				bezocht(bezocht);
			}
			
			return null;
		}

	}

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
    boolean bezocht = false;
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
	private String entry = "";

	private HasSafeHtml html;

	private boolean linkActive = true;

	private HandlerRegistration reg;
	
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
		reg = anchor.addClickHandler(this);
		anchor.setStylePrimaryName("scorewidget");
		
		span  = new InlineHTML();
		span.setStylePrimaryName("scorewidget");
				
		init(breedte, hoogte, launchState);
		
//		a.vars().ifPresent(this::initVars);
	}
	
	private void initVars(DwoGlobalVars vars) {
		DomSchoolClass sc = vars.getCurrentSchoolClass();
		if (sc != null) {
			if (moduleID != 0) {
				SelectModuleItem item = SelectModuleItemHolder.getItemByID(Integer.toString(moduleID));
				if (item == null) {
//					reg.removeHandler();
//					anchor.setHref("javascript:return false;");
//					reg = anchor.addClickHandler(this::nop);
				}
			} else if (activiteitID != 0) {
				SelectModuleItem item = SelectModuleItemHolder.getScoByID(Integer.toString(activiteitID));
				if (item == null) {
					// haal item op met rpc.getScoContextClass en bepaal actief of niet.
				}
			}
		}
	}
	
	void nop(ClickEvent ev) {
		ev.preventDefault();
	}
	
	@Override
	public HashMap<String, Object> getState() {
		HashMap<String, Object> state = new HashMap<>();
		if (score) state.put("score.raw", scoreRaw);
		if (goedFout) state.put("success_status", successStatus);
		if (bezocht) state.put("entry", entry);
		return state;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		if (h == null) return;
		if (goedFout && successStatus.isEmpty())
		{	successStatus = h.getOrDefault("success_status", successStatus).toString();
			goedfout(successStatus);
		}
		if (bezocht && entry.isEmpty()) 
			entry = h.getOrDefault("entry", entry).toString();
			bezocht(entry);
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
		return Boolean.TRUE;
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
		Promise<String> start;
		if (Memento.COMPLETED.equals(api.GetValue(Memento.COMPLETION_STATUS)))
			start = Promises.resolved("true");
		else {
			api.SetValue("cmi.exit", "suspend");
			start = api.Commit();
		}
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
			 bezocht = map.getBoolean("bezocht", bezocht);
			 if (toonTitel && map.containsKey("paginaTitel"))
				 paginaTitel = map.getString("paginaTitel");
			 linkActive = map.getBoolean("linkActive", linkActive);
		}
		html = linkActive ? anchor : span;
		if (!linkActive) initWidget(span);else initWidget(anchor);
		setStyleDependentName("goedFout", goedFout);
		setStyleDependentName("bezocht", bezocht);
		
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

		Collection<String> keys = new TreeSet<String>();
		Deferred<Map<String,String>> defer = new Deferred<>();
		if (score) {
			Promise<String> result;
			String key = pfx + ".score.raw";
			result = defer.getPromise().map( m -> m.getOrDefault(key, ""));
			keys.add(key);
			anchorSetText("0");
			result.then(this::doScore);
		} else {
			anchorSetText("");
		}
		if (goedFout && bezocht) {
			String key;
			key = pfx + SUCCESS_STATUS;
			keys.add(key);
			key = pfx + ENTRY;
			keys.add(key);
			key = pfx + COMPLETION_STATUS;
			keys.add(key);
			Promise<Map<String, String>> result = defer.getPromise();
			result.then(new GoedFoutBezocht(pfx));
		
		} else 
		if (goedFout) {
			Promise<String> result;
			String key = pfx + SUCCESS_STATUS;
			result = defer.getPromise().map( m -> m.getOrDefault(key, ""));
			keys.add(key);			
			result.then(this::doGoedFout);			
		} else
		if (bezocht) {
			Promise<String> result;
			String key = pfx + ENTRY;
			result = defer.getPromise().map( m -> m.getOrDefault(key, ""));
			keys.add(key);			
			result.then(this::doBezocht);
		}
		if (!keys.isEmpty()) {
			defer.resolveWith(api.getValuesPromise(keys));
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
	
	private Promise<String> doBezocht(Promise<String> p) {
		this.entry = p.getValue();
		bezocht(this.entry);
		return p;
	}
	
	private void bezocht(String entry) {
		removeStyleDependentName("bezocht-resume");
		removeStyleDependentName("bezocht-ab-initio");
		addStyleDependentName("bezocht-" + entry);
		
	}
	
	@Override
	public void onClick(ClickEvent event) {
		context.prepareLeave();
		String href = anchor.getHref();
		if (href.startsWith("goto:")) {
			event.preventDefault();
			context.gotoUrl(anchor.getHref());
		} else if (href.contains("#")) {
			int index = href.lastIndexOf('#');
			event.preventDefault();
			context.gotoPlace(href.substring(1+index)); // aanpassing voor "HasBack"
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
		ashoogte = regel.getFont().getAscent();
	}

}
