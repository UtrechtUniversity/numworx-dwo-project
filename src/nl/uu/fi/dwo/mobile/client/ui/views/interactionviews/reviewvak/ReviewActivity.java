package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.reviewvak;

import java.util.Optional;

import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;
import nl.uu.fi.dwo.mobile.utils.LogBuilder;

public class ReviewActivity implements ActivityInterface {

	private ActivityInterface delegate;
	
	public ReviewActivity(ActivityInterface delegate) {
		this.delegate = delegate;
	}

	public LogBuilder logBuilder() {
		return delegate.logBuilder();
	}

	public boolean isPremium() {
		return delegate.isPremium();
	}

	public boolean isReview() {
		return delegate.isReview();
	}

	public boolean isEindtoetsVerzegeld() {
		return delegate.isEindtoetsVerzegeld();
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
		return delegate.getLessonMode();
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
	
}
