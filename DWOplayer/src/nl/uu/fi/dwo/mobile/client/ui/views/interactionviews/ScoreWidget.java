package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;

public class ScoreWidget extends Composite implements InteractionView, ClickHandler, TekstElementWithFont, CBookEventListener {

	class GoedFoutBezochtMultiple implements Success<Map<String, String>, Map<String, String>> {

		private String pfx;
		private Collection<Integer> paginaSet;

		public GoedFoutBezochtMultiple(String pfx, Collection<Integer> paginaSet) {
			this.pfx = pfx;
			this.paginaSet = paginaSet;
		}

		@Override
		public Promise<Map<String, String>> call(Promise<Map<String, String>> resolved) throws Exception {
			Map<String,String> value = resolved.getValue();
			Object first = paginaSet.stream().limit(1).findAny().get();
			String completed = value.get(pfx + first + COMPLETION_STATUS);
// FIXME eliminate first!!!!
			String bezocht   = value.getOrDefault(pfx + first + ENTRY, "");
			String goedfout  = value.getOrDefault(pfx + first + SUCCESS_STATUS, "");
			if ("completed".equals(completed)) {
				if (cesuur == null)
					goedfout(goedfout);
				else
					goedfout(value.get(pfx + first + SCORE_RAW));
			} else {
				bezocht(bezocht);
			}
			return null;
		}

	}

	private static final String SCORE_RAW = ".score.raw";

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
				if (cesuur == null)
					goedfout(goedfout);
				else
					goedfout(value.get(pfx + SCORE_RAW));
			} else {
				bezocht(bezocht);
			}
			
			return null;
		}

	}

	class IdMatch implements Success<Map<String,String>, Map<String,String>> {
		final String id, paginaNr;

		IdMatch(String id, int paginaNr) {
			this.id = id + "." + paginaNr + ".id";
			this.paginaNr = "-" + (paginaNr-1) + "-";
		}
		@Override
		public Promise<Map<String, String>> call(Promise<Map<String, String>> resolved) throws Exception {
			String scoid = resolved.getValue().getOrDefault(id, "");
			String uuid = comRoot.getUUID();
			if (uuid.startsWith(scoid + paginaNr)) {
				addHandler();
			}
			return resolved;
		}
		
	}
	
	private HashMap<String, Object> launchState; 
	
	int breedte = 40;
	int hoogte = 300; 
	private boolean volledigeBreedte = false;
	int ashoogte = 12;
	
	int choicePageMode = 0;
    int activiteitNr = 0;
    int paginaNr = 1;
    String paginaNrs = "";
    int activiteitID = 0;
    int moduleID = 0;
    boolean score = false;
    boolean goedFout = false;
    boolean toonTitel = false;
    boolean bezocht = false;
    boolean activiteitScore = false;
    String paginaTitel = "";
    Integer cesuur = null;
    

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

	private com.google.web.bindery.event.shared.HandlerRegistration addHandler() {
		return getEventBus().addHandler(CBookEvent.TYPE, this);
	}

	private EventBus getEventBus() {
		return OpdrNav.getEventBus();
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
		@Template("<span class='scorewidget-score'>{1}</span><span class='scorewidget-titel'>{0}</span>")
		SafeHtml titleScore(String title, String scorex);
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
			 if(map.containsKey("paginaNrs"))
				 paginaNrs = map.getString("paginaNrs");
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
			 
			 if (map.containsKey("cesuur")) {
				 cesuur = map.getInt("cesuur");
			 }
			 if (map.containsKey("activiteitScore")) {
				 activiteitScore = map.getBoolean("activiteitScore");
				 if (activiteitScore && choicePageMode == 0) 
					 linkActive = false;
			 }
			 if (!paginaNrs.isEmpty()) linkActive = false; // geen link bij meerdere pagina's
			 
		}
		html = linkActive ? anchor : span;
		if (!linkActive) initWidget(span);else initWidget(anchor);
		setStyleDependentName("goedFout", goedFout);
		setStyleDependentName("bezocht", bezocht);
		
		String pfx0 = "dme.scorewidget.";
		if (! activiteitScore) {
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
		} else {
			switch(choicePageMode) {
			case 0: pfx0 += "cs"; break;
			case 1: anchor.setHref("goto:" + activiteitNr);
				pfx0 += "cc." + activiteitNr;
				break;
			case 2:
				anchor.setHref("#xs:" + activiteitID);
				pfx0 += "s." + activiteitID; break;
			case 3:
				anchor.setHref("#xc:" + moduleID + "." + activiteitNr);
				pfx0 += "c." + moduleID + "." + activiteitNr; break;
			default: pfx0 += "null";
			}
			paginaNr = 0;
			paginaNrs = "";
		}
		Collection<Integer> paginaSet;
		if (paginaNrs.isEmpty()) 
			paginaSet = Collections.singleton(paginaNr);
		else {
			paginaSet = Util.parsePaginaNrs(paginaNrs);
		}
		
		Collection<String> keys = new TreeSet<String>();
		Deferred<Map<String,String>> defer = new Deferred<>();
		
		if (paginaSet.size() == 1 ) { // 1 pagina!
			paginaNr = paginaSet.iterator().next();
			final String pfx = pfx0 + "." + paginaNr;
// check for local page
			if (paginaNr > 0) {
				IdMatch match = new IdMatch(pfx0, paginaNr);
				keys.add(match.id);
				defer.getPromise().then(match);
			}
			
			if (score) {
				Promise<String> result;
				String key = pfx + SCORE_RAW;
				result = defer.getPromise().map( m -> m.getOrDefault(key, ""));
				keys.add(key);
				anchorSetText("0");
				result.then(this::doScore);
			} else {
				anchorSetText("");
			}
			if (goedFout && bezocht) {
				String key;
				if (cesuur != null) key = pfx + SCORE_RAW;
				else key = pfx + SUCCESS_STATUS;
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
				String key = cesuur != null ? pfx + SCORE_RAW : pfx + SUCCESS_STATUS;
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
		
		} else {
			final String pfx00 = pfx0 + ".";
			if (score) {
				Collection<String> scores = paginaSet.stream().map(n -> pfx00  + n + SCORE_RAW).collect(Collectors.toSet());
				keys.addAll(scores);
				Promise<String> result = defer.getPromise().map(m -> addScores(m, scores));
				result.then(this::doScore);
			} else {
				anchorSetText("");				
			}
			if (goedFout && bezocht) {
				String item = cesuur == 0 ? SUCCESS_STATUS : SCORE_RAW;
				Collection<String> scores = paginaSet.stream().map(n -> pfx00  + n + item).collect(Collectors.toSet());
				scores.addAll( paginaSet.stream().map(n-> pfx00 + n + ENTRY).collect(Collectors.toSet()));
				scores.addAll( paginaSet.stream().limit(1).map(n -> pfx00 + n + COMPLETION_STATUS).collect(Collectors.toList()));
				keys.addAll(scores);
				defer.getPromise().then(new GoedFoutBezochtMultiple(pfx00, paginaSet));
				
			} else
			if (goedFout) {
				Promise<String> result;
				Collection<String> scores;
				if (cesuur == null) {
					scores = paginaSet.stream().map(n -> pfx00  + n + SUCCESS_STATUS).collect(Collectors.toSet());
					result = defer.getPromise().map(m -> addGoedFout(m, scores));
				} else {
					scores = paginaSet.stream().map(n -> pfx00  + n + SCORE_RAW).collect(Collectors.toSet());
					result = defer.getPromise().map(m -> addScores(m, scores));
				}
				keys.addAll(scores);
				result.then(this::doGoedFout);			
			} else
			if (bezocht) {
				Collection<String> scores = paginaSet.stream().map(n -> pfx00  + n + ENTRY).collect(Collectors.toSet());
				keys.addAll(scores);
				Promise<String> result = defer.getPromise().map(m -> addBezocht(m, scores));
				result.then(this::doBezocht);
			
			}
			
		}
		if (!keys.isEmpty()) {
			defer.resolveWith(activity.agent().barrier().then(x -> api.getValuesPromise(keys)));
		}
	}
	
	private String addBezocht(Map<String, String> m, Collection<String> scores) {
		for (String key: scores) {
			String v = m.getOrDefault(key, "");
			if (!"resume".equals(v)) return v;
		}
		return "resume";
	}
	
	private String addGoedFout(Map<String, String> m, Collection<String> scores) {
		int passed = 0;
		int failed = 0;
		for (String key: scores) {
			String v = m.getOrDefault(key, "");
			if ("passed".equals(v)) passed++;
			if ("failed".equals(v)) failed++;
		}
		if (passed == scores.size()) return "passed";
		if (failed == scores.size()) return "failed";
		return "";
	}

	private String addScores(Map<String, String> m, Collection<String> scores) {
		int s = 0;
		for (String key: scores) {
			String v = m.get(key);
			try { 
				s += Integer.parseInt(v);
			} catch(Exception oops) {}
		}
		return Integer.toString(s);
	}

	static class Util {
		static  Collection<Integer> parsePaginaNrs(String string) {
			Collection<Integer> result = new TreeSet<>();
			if (string.isEmpty()) return result;
			String[] split = string.split(",");
			for (String item : split) {
				String[] bounds = item.split("-");
				Integer from = Integer.valueOf(bounds[0].trim());
				Integer to;
				if (bounds.length == 1) {
					to = from;
				} else {
					to = Integer.valueOf(bounds[1].trim());
				}
				for (int i = from; i <= to; i++) 
					result.add(i);
			}
 			return result;
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
		if (cesuur != null)
		{
			goedfout(value, cesuur.intValue());
		} else {
			removeStyleDependentName("goedFout-passed");
			removeStyleDependentName("goedFout-failed");
			addStyleDependentName("goedFout-" + value);
		}
	}
	
	private void goedfout(String value, int cesuur) {
		Double score;
		try { 
			score = Double.valueOf(value);
		} catch(Exception oops) {
			removeStyleDependentName("goedFout-passed");
			removeStyleDependentName("goedFout-failed");			
			return;
		}
		boolean passed = score.intValue() >= cesuur;
		setStyleDependentName("goedFout-passed", passed);
		setStyleDependentName("goedFout-failed", !passed);
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
		//getElement().getStyle().setColor(color);
		ashoogte = regel.getFont().getAscent();
	}

	
	boolean hasScore;
	
	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if ("setChanged".equals(event.getCommand()))
		{
			Map<String, ?> param = event.getParameters();
			Integer location = (Integer) param.get("location");
			if (paginaNr == location.intValue()+1) {
				if (score) {
					Promise<String> p;
					p = Promises.resolved(String.valueOf(param.get("score.raw")));
					p.then(this::doScore);
				}
				if (goedFout && bezocht) {
				} else 
				if (goedFout) {
					String  gf;
					if (cesuur == null) {
						Object success = param.get("success");
						if (Boolean.TRUE.equals(success)) gf = "passed";
						else if (Boolean.FALSE.equals(success)) gf = "failed";
						else gf = "";
					} else {
						gf = Objects.toString(param.get("score.raw"));
					}
					this.successStatus = gf;
					goedfout(gf);
				} else
				if (bezocht) {
					Object visited = param.get("visited");
					if ( visited instanceof Collection && ((Collection) visited).isEmpty()) {
						bezocht(entry = "resume");
					}
				}
			}
		}
		
	}

}
