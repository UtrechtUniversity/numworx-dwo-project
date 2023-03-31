package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import fi.dwo.gwt.lib.rest.CallManagers.XapiManager;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.utils.LaTransport;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.NoLogging;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.ActivityDefinition;
import nl.uu.fi.dwo.rest.dom.xapi.Context;
import nl.uu.fi.dwo.rest.dom.xapi.ContextActivities;
import nl.uu.fi.dwo.rest.dom.xapi.Extensions;
import nl.uu.fi.dwo.rest.dom.xapi.Result;
import nl.uu.fi.dwo.rest.dom.xapi.Score;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.Verb;

public class SMLogger implements Logging {
  
  @FunctionalInterface
  public interface LogStrategy {

    Promise<String> saveStatement(Statement s);
    
  }
  
  @Module public static class LoggingModule {
	  
	  @Provides Logging logging(ActivityComponent activity) {
		  return NoLogging.instance;
	  }
  }
    
  public static class WiskOpdrProvider extends LoggingModule {
    
    Logging tao() {
      return LaTransport.newTAOinstance();
    }
    
    public  Logging logging(ActivityComponent activity) {
      Memento memento = activity.memento();
      boolean premium = activity.isPremium();
      if (memento.pmodel == null
          || memento.getLessonMode() != LessonMode.review
          || !premium
      ) return tao(); 

      Promise<LogStrategy> strategy = Promises.resolved(memento);     
      return new SMLogger(memento, strategy, tao());
    }
  }
  
  public static class DWO2playerProvider extends LoggingModule {
    private DwoGlobalVars vars;
	private Lazy<RPCHandler> rpc;

	@Inject DWO2playerProvider(DwoGlobalVars vars, Lazy<RPCHandler> rpc) {
      this.vars = vars;
      this.rpc  = rpc;
    }

    public Logging logging(ActivityComponent activity) {
      Memento instance = activity.memento();
      boolean experiment = instance != null 
    		  && instance.pmodel != null 
    		  && (instance.getLessonMode() == LessonMode.normal||instance.getLessonMode() == LessonMode.review)
              && vars.withUser() 
              && vars.getRoleType() == RoleType.STUDENT;
      if (experiment) {
        Promise<XapiManager> xapi = rpc.get().getLRS();
        return new SMLogger(instance, xapi.map(x -> x::saveStatement), super.logging(activity));
      }
      return super.logging(activity);
    }

    
  }

  public static final String ATTEMPTED = "http://www.dwo.nl/verbs/attempted";
  public static final String CORRECTED = "http://www.dwo.nl/verbs/corrected";
  public static final String[] ASSESSMENT_TYPE = {
		  null,
		  null,
		  "self-test",
		  "test"
  };
  public static final DateTimeFormat FORMAT_8601 = DateTimeFormat.getFormat(PredefinedFormat.ISO_8601);

  final Memento memento;
  Promise<LogStrategy> xapi;
  Logging delegate; // Chain of command;
  Statement prototype;
  Activity widget;
  ActivityDefinition definition;
  Extensions extensions;
  int maxScore;
  
  public SMLogger(Memento memento, Promise<LogStrategy> xapi, Logging delegate) {
    this.memento = memento;
    this.xapi = xapi;
    prototype = new Statement();
    Verb verb = new Verb(); verb.id = ATTEMPTED;
    prototype.verb = verb;
    prototype.context = new Context();
    prototype.context.registration = memento.getRegistration();
    prototype.context.contextActivities = new ContextActivities();
    widget = new Activity();
    prototype.context.contextActivities.parent = Collections.singletonList(widget);
    definition = new ActivityDefinition();
    widget.definition = definition;
    extensions = new Extensions();
    extensions.objectives = Collections.emptyList();
    definition.extensions = extensions;
    this.xapi = memento.pmodel.then(m -> {createModel(m); return xapi; });
    this.delegate = delegate;
  }

  Promise<Void> createModel(Promise<DomStudentModelContextId> context) {
    prototype.object = new Activity();
    prototype.object.id = "pid:" + context.getValue().getId().getIdString();
    return null;
  }
  
  private Double getScore(Map<String, ?> parameters) {
    @SuppressWarnings("unchecked")
	Map<String,?> map = (Map<String,?>) parameters.get("score");
    if(map == null) return null;
    Number n = (Number) map.get("raw");
    if(n == null) return null;
        return Double.valueOf(n.doubleValue());
}

  @Override
  public void log(Map<String, ?> parameters) {
    if (extensions.objectives.isEmpty()) { // no logging if no objectives assigned.
      delegate.log(parameters);
      return;
    }
    Result result = new Result();
    Statement s = new Statement();
    s.actor = prototype.actor;
    s.context = prototype.context;
    s.verb = prototype.verb;
    s.object = prototype.object;

    Date now = new Date();
    s.timestamp = FORMAT_8601.format(now);
    result.duration = memento.format(now.getTime()-memento.startDate.getTime());
    Object success = parameters.get("success");
    if (success instanceof Boolean)
    	result.success = (Boolean) success;
    String response = (String) parameters.get("formula");
    if(response == null) response = (String) parameters.get("response");
    result.response = response;

    try {
      result.score = new Score();
      result.score.raw = getScore(parameters);
      result.score.max = Double.valueOf(maxScore);
      if (maxScore > 0 && result.score.raw != null)
        result.score.scaled = result.score.raw.doubleValue() / maxScore;
    } catch (Exception e) {
    }
    s.result = result;  
    xapi.then(manager -> manager.getValue().saveStatement(s));
    delegate.log(parameters);
  }

  @Override
  public void setCommunicationRoot(OpdrNavIF comRoot) {
    widget.id = "uuid:" + comRoot.getUUID();
    int mode = comRoot.getMode();
    extensions.assessmentType = ASSESSMENT_TYPE[mode];
    delegate.setCommunicationRoot(comRoot);
  }

  @Override
  public void setLogID(String string) {
    definition.name = string != null ? Collections.singletonMap("unk", string) : null; //Collections.singletonMap("en", "unknown");
    delegate.setLogID(string);
  }

  @Override
  public void setClassName(String string) {
    definition.type = "http://www.dwo.nl/widgets/" + string;
    delegate.setClassName(string);
  }

  @Override
  public void setLogObjectives(boolean[][] objectives) {
    delegate.setLogObjectives(objectives);
  }

  @Override
  public void setSMObjectives(String[] objectives) {
    extensions.objectives = 
    		objectives == null ? Collections.emptyList() :
    		Arrays.asList(objectives);
    delegate.setSMObjectives(objectives);
  }
  
  @Override public String[] getSMObjectives() {
	  return extensions.objectives == null ? null : extensions.objectives.toArray(new String[0]);
  }
  
  @Override
  public void setSMForeknowledge(String[] foreknowledge) {
	  extensions.foreknowledge = 
			  foreknowledge != null ? Arrays.asList(foreknowledge) : null;
	  delegate.setSMForeknowledge(foreknowledge);
  }

  @Override
  public String[] getSMForeknowledge() {
	  return extensions.foreknowledge == null ? null : extensions.foreknowledge.toArray(new String[0]);
  }

  @Override
  public void setMaxScore(int max) {
    maxScore = max;
    delegate.setMaxScore(max);
    
  }
	public void setLogOption(boolean logOption) {
		delegate.setLogOption(logOption);
	}

  @SuppressWarnings("unchecked")
@Override
  public void updateLog(Map<String, ?> parameters) {
    if (
        CORRECTED == parameters.get("verb") &&
        !extensions.objectives.isEmpty()) { // no logging if no objectives assigned.
      Result result = new Result();
      Statement s = new Statement();
      s.actor = prototype.actor;
      s.context = prototype.context;
      Verb verb = new Verb(); verb.id = CORRECTED;
      s.verb = verb;
      s.object = prototype.object;
  
      Date now = new Date();
      s.timestamp = FORMAT_8601.format(now);
      result.success = (Boolean) parameters.get("success");
      String response = (String) parameters.get("formula");
      if(response == null) response = (String) parameters.get("response");
      result.response = response;
  
      try {
        result.score = new Score();
        result.score.raw = getScore(parameters);
        result.score.max = Double.valueOf(maxScore);
        if (maxScore > 0 && result.score.raw != null)
          result.score.scaled = result.score.raw.doubleValue() / maxScore;
      } catch (Exception e) {
      }
// extensions      
      if (parameters.get(Extensions.OBJECTIVES) != null) {
    	  result.extensions = new Extensions();
    	  result.extensions.objectives = 
    			  Arrays.asList(JSONUtilities.toStringArray( parameters.get(Extensions.OBJECTIVES)));
      }

      s.result = result;  
      xapi.then(manager -> manager.getValue().saveStatement(s));
    }
    delegate.log(parameters);
  }

}
