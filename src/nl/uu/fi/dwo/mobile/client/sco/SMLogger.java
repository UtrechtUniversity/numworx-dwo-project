package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Provider;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import fi.dwo.gwt.lib.rest.CallManagers.XapiManager;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
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

  public static class Provider implements javax.inject.Provider<Logging> {
    public Provider(javax.inject.Provider<Logging> delegate) {
      this.delegate = delegate;
    }

    public Logging get() {
      Memento instance = Memento.instance();
      boolean experiment = instance != null 
    		  && instance.pmodel != null 
    		  && instance.getLessonMode() == LessonMode.normal
              && DWOplayer.withUser() 
              && DWOplayer.clientfactory.getRoleType() == RoleType.STUDENT;
      if (experiment) {
        Promise<XapiManager> xapi = DWOplayer.clientfactory.getRPCHandler().getLRS();
        return new SMLogger(instance, xapi, delegate.get());
      }
      return delegate.get();
    }

    javax.inject.Provider<Logging> delegate;
  }

  public static final String ATTEMPTED = "http://www.dwo.nl/verbs/attempted";
  public static final DateTimeFormat FORMAT_8601 = DateTimeFormat.getFormat(PredefinedFormat.ISO_8601);

  final Memento memento;
  Promise<XapiManager> xapi;
  Logging delegate; // Chain of command;
  Statement prototype;
  Activity widget;
  ActivityDefinition definition;
  Extensions extensions;
  int maxScore;
  
  public SMLogger(Memento memento, Promise<XapiManager> xapi, Logging delegate) {
    this.memento = memento;
    this.xapi = xapi;
    prototype = new Statement();
    Verb verb = new Verb(); verb.id = ATTEMPTED;
    prototype.verb = verb;
    prototype.context = new Context();
    prototype.context.contextActivities = new ContextActivities();
    widget = new Activity();
    prototype.context.contextActivities.parent = Collections.singletonList(widget);
    definition = new ActivityDefinition();
    widget.definition = definition;
    extensions = new Extensions();
    extensions.objectives = Collections.emptyList();
    definition.extensions = extensions;
    memento.pmodel.then(this::createModel);
    this.delegate = delegate;
  }

  Promise<Void> createModel(Promise<DomStudentModelContext> context) {
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
    s.result = result;  
    xapi.then(manager -> manager.getValue().saveStatement(s));
    delegate.log(parameters);
  }

  @Override
  public void setCommunicationRoot(OpdrNavIF comRoot) {
    widget.id = "uuid:" + comRoot.getUUID();
    delegate.setCommunicationRoot(comRoot);
  }

  @Override
  public void setLogID(String string) {
    definition.name = string != null ? Collections.singletonMap("unk", string) : null;
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

  @Override
  public void setMaxScore(int max) {
    maxScore = max;
    delegate.setMaxScore(max);
    
  }
	public void setLogOption(boolean logOption) {
		delegate.setLogOption(logOption);
	}

}
