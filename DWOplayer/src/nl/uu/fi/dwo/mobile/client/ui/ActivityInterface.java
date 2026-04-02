package nl.uu.fi.dwo.mobile.client.ui;


import java.util.Optional;

import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.ScoreWidgetIF;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.utils.LogBuilder;

public interface ActivityInterface {

	LogBuilder logBuilder();

	boolean isPremium();

	boolean isReview();

	boolean isEindtoetsVerzegeld();

	Scorm2004IF api();

	boolean isTest();

	String getResource(String string);

	LessonMode getLessonMode();

	boolean isNoordhoff();

	TrafficAgent agent();

	Optional<DwoGlobalVars> vars();

	EventBus getEventBus();

	void tickle();

	String getStubView();

	default int getWindowHeight() {
		return Window.getClientHeight();
	}
	
	default Memento memento() {
		return null;
	}
	
	default Lazy<ScoreWidgetIF> scoreWidgetIF() { return this::api; }

	default TimedBarrier barrier() { return new TimedBarrier(agent()); }
}
