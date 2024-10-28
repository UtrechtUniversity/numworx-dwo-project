package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Optional;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import dagger.Subcomponent;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.dagger.ActivityScope;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.MementoModule;
import nl.uu.fi.dwo.mobile.client.sco.SMLogger;
import nl.uu.fi.dwo.mobile.client.sco.ScoreManager;
import nl.uu.fi.dwo.mobile.client.sco.ScoreWidgetIF;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.utils.LogBuilder;
import nl.uu.fi.dwo.mobile.utils.Logging;

@Subcomponent(modules= {MementoModule.class, SMLogger.LoggingModule.class} )
@ActivityScope
public abstract class ActivityComponent implements ActivityInterface {

	public abstract EventBus getEventBus();
	public abstract DWOplayerParameters parameters();
	public abstract TrafficAgent agent();
	public abstract Optional<DwoGlobalVars> vars();
	@SuppressWarnings("rawtypes")
	public abstract NeedLogin needLogin();
	public abstract VisibilityDetect visibilityDetect();
	public abstract ScoreManager scoremanager();
	
	@Nullable public abstract Memento memento();
	@Named("API") public abstract Scorm2004IF api();
	
	public LogBuilder logBuilder() {
		return new LogBuilder(this);
	}
	
	@Override
	public Lazy<ScoreWidgetIF> scoreWidgetIF() {
		return () -> {
			if (isTest() && vars().isPresent()) { return scoremanager(); }
			return api(); 
		};
	}
	
	@Subcomponent.Builder
	public
	interface Builder {
		ActivityComponent build();
		Builder mementoModule(MementoModule module);
		Builder loggingModule(SMLogger.LoggingModule module);
	}

	@Named("premium")
	public abstract boolean isPremium();

	public abstract Logging getLogging();

	public boolean isReview() {
		return memento() != null && memento().isReview();
	}
	public boolean isEindtoetsVerzegeld() {
		return memento() != null && memento().isEindtoetsVerzegeld();
	}
	
	@Override
	public boolean isNoordhoff() {
		String dependentName = parameters().keyboardStyle();
		return "noordhoff".equals(dependentName);
	}
	public boolean isTest() {
		String dwoEnv = parameters().getDwoEnv();
		Logger.getLogger(getClass().getName()).severe("is Test " + dwoEnv);
		return dwoEnv.contains("test");
	}
	@Override
	public String getResource(String string) {
		return parameters().getResource(string);
	}
	@Override
	public LessonMode getLessonMode() {
		return memento().getLessonMode();
	}
	
	@Override
	public void tickle() {
		parameters().tickle();
	}
	
	@Override
	public String getStubView() {
		return parameters().getStubView();
	}
}
