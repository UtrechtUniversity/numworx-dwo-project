package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.reviewvak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;
import nl.uu.fi.dwo.mobile.client.ui.views.CorrectieView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.utils.LogBuilder;

public class ReviewActivity implements ActivityInterface, CBookEventListener {

	static final String INTERACTIE_PANEL_STATES = "interactiePanelStates";
	static final String CHECK_DOCENT = "checkDocent";
	ActivityInterface delegate;
	private boolean checkDocent;
	private int cesuur;
	
	public ReviewActivity(ActivityInterface delegate, Object source, boolean checkDocent, ObjectMap launchdata) {
		this.delegate = delegate;
		this.checkDocent = checkDocent;
		getEventBus().addHandlerToSource(CBookEvent.TYPE, source, this);
		int scoreMax = launchdata.getInt("scoreMax");
		cesuur = (scoreMax+1)/2;
		if (cesuur == 0) this.checkDocent = false; // zonder cesuur geen docent nodig
	}

	public LogBuilder logBuilder() {
		return new ReviewLogBuilder(this);
	}

	public boolean isPremium() {
		return delegate.isPremium();
	}

	public boolean isReview() {
		return false;
	}

	public boolean isEindtoetsVerzegeld() {
		return false;
	}

	public Scorm2004IF api() {
		return delegate.api();
	}

	public boolean isTest() {
		return delegate.isTest();
	}

	public String getResource(String string) {
		return delegate.getResource(string);
	}

	public LessonMode getLessonMode() {
		return LessonMode.normal;
	}

	public boolean isNoordhoff() {
		return delegate.isNoordhoff();
	}

	public TrafficAgent agent() {
		return delegate.agent();
	}

	public Optional<DwoGlobalVars> vars() {
		return delegate.vars();
	}

	public EventBus getEventBus() {
		return delegate.getEventBus();
	}

	public void tickle() {
		delegate.tickle();
	}

	public String getStubView() {
		return delegate.getStubView();
	}

	public int getWindowHeight() {
		return delegate.getWindowHeight();
	}

	public OpdrNavIF wrap(OpdrNavIF comRoot2) {
		return new ReviewOpdrNav(comRoot2, this);
	}

	@SuppressWarnings("unchecked")
	public void getState(HashMap<String, Object> h, int correctie) {
		ArrayList<Object> states = (ArrayList<Object>) h.get(INTERACTIE_PANEL_STATES);
		if (states == null|| states.isEmpty()) return;
		
		if (cesuur == 0) checkDocent = false; // zonder cesuur geen docent nodig
		
		HashMap<String,Object> o = new HashMap<>();
		if (correctie != 0) {
			checkDocent = false;
			o.put(CorrectieView.REVIEW_SCORE_CORRECTIE, Math.max(0, correctie));
		}
		o.put(INTERACTIE_PANEL_STATES, states);
		o.put(CHECK_DOCENT, checkDocent);
// extra
		o.put("ingeklapt", h.get("ingeklapt"));
		o.put("hoogtes", h.get("hoogtes"));
        h.put(CorrectieView.REVIEW_INTERACTIE_DATA, o);
	}

	public ObjectMap setState(ObjectMap h) {
		if (h.containsKey(CorrectieView.REVIEW_INTERACTIE_DATA)) {
			ReviewObjectMap map = new ReviewObjectMap(h);
			checkDocent = cesuur != 0 && map.review.getBoolean(CHECK_DOCENT, checkDocent) ;
			return map;
		}
		return h;
	}
	
	public static ObjectMap wrap(ObjectMap h) {
		if (h.containsKey(CorrectieView.REVIEW_INTERACTIE_DATA)) {
			ReviewObjectMap map = new ReviewObjectMap(h);
			return map;
		}
		return h;	
	}

	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if (TekstVakPanel.TVP_POPUP.equals(event.getCommand()))
			checkDocent = false;		
	}
	
	public Boolean isCorrect(int score, Boolean old) {
		if (score >= cesuur) return Boolean.TRUE;
		return old;
	}
}
