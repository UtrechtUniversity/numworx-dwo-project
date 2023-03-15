package nl.uu.fi.dwo.account.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.fusesource.restygwt.client.Defaults;
import org.osgi.util.promise.Promise;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.CallManagers.XapiManager;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.ActivityDefinition;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.dom.xapi.Context;
import nl.uu.fi.dwo.rest.dom.xapi.ContextActivities;
import nl.uu.fi.dwo.rest.dom.xapi.Extensions;
import nl.uu.fi.dwo.rest.dom.xapi.Result;
import nl.uu.fi.dwo.rest.dom.xapi.Score;
import nl.uu.fi.dwo.rest.dom.xapi.StateDocument;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsQuery;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsResult;
import nl.uu.fi.dwo.rest.dom.xapi.Verb;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class XapiTest implements EntryPoint, ClickHandler {
  private static final String ATTEMPTED = "http://www.dwo.nl/verbs/attempted";
  private static final DateTimeFormat FORMAT_8601 = DateTimeFormat.getFormat(PredefinedFormat.ISO_8601);

  static {
    //Initialize an Exception translator.
    Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
}

  Promise<XapiManager> xapi;
  private Button safe;
  private Button query;
  private Button state;
  private Button load;
  @Override
  public void onClick(ClickEvent event) {
    if (event.getSource() == safe) {
      nl.uu.fi.dwo.rest.dom.xapi.Account account = new nl.uu.fi.dwo.rest.dom.xapi.Account();
      account.homePage = GwtRestVars.instance().getServer();
      account.name = "pid:00000000003";
      Agent agent = new Agent();
      agent.account = account;
      agent.name = "project_wim";
      Verb verb = new Verb();
      verb.id = ATTEMPTED;
      verb.display = Collections.singletonMap("nl", "poging");
      Activity activity = new Activity();
      activity.id = "pid:00000000001";
      Result result = new Result();
      Score score = new Score();
      score.max = 10.0;
      score.raw = 10.0;
      score.scaled = 1.0;
      result.score = score;
      result.success = Boolean.TRUE;
      result.duration = "PT10S";
      result.response = "42";
      Extensions extensions = new Extensions();
      extensions.objectives = Arrays.asList("obj1", "obj2");
      Statement statement = new Statement();
      //statement.actor = agent;
      statement.verb = verb;
      statement.object = activity;
      statement.result = result;
      statement.timestamp = FORMAT_8601.format(new Date());
      Context context = new Context();
      //context.registration = "f44018b5-165f-4454-af29-f4231d269c8c"; // Some UUID
      context.contextActivities = new ContextActivities();
      context.contextActivities.parent = new ArrayList<>();
      Activity element = new Activity();
      element.id = "pid:000000002";
      element.definition = new ActivityDefinition();
      element.definition.type = "http://www.dwo.nl/activities/fi.wiskopdr.SimpleAntwoordFormuleVak";
      element.definition.extensions = extensions;
      context.contextActivities.parent.add(element);
      statement.context = context;
      xapi.getValue().saveStatement(statement).then(p -> {
        GWT.log("result id = " + p.getValue());
        return p;
      }, f -> { GWT.log("fail", f.getFailure());
    
    });
    }
    if (event.getSource() == query) {
      nl.uu.fi.dwo.rest.dom.xapi.Account account = new nl.uu.fi.dwo.rest.dom.xapi.Account();
      account.homePage = GwtRestVars.instance().getServer();
      account.name = "u:project_wim";
      Agent agent = new Agent();
      agent.account = account;
      agent.name = "Wim van Velthoven";
      StatementsQuery q = new StatementsQuery();
      q.verbID = ATTEMPTED;
      //q.agent = agent;
      q.since = FORMAT_8601.format(new Date(System.currentTimeMillis()-1000000000L));
      q.ascending = true;
      //q.registration = "f44018b5-165f-4454-af29-f4231d269c8c";
      
      Promise<StatementsResult> result;
      
      result = xapi.getValue().queryStatements(q);
      result.then(p -> { 
        GWT.log("result " + p.getValue());
        return p; }, p -> { GWT.log("fail", p.getFailure());});
    }
    
    if (event.getSource() == state) {
      nl.uu.fi.dwo.rest.dom.xapi.Account account = new nl.uu.fi.dwo.rest.dom.xapi.Account();
      account.homePage = GwtRestVars.instance().getServer();
      account.name = "u:project_wim";
      Agent agent = new Agent();
      agent.account = account;
      agent.name = "Wim van Velthoven";
      Activity activity = new Activity();
      activity.id = "pid:00000000001";
      StateDocument state = new StateDocument();
      state.activity = activity;
      state.agent = agent;
      state.content = "{}";
      state.contentType = "application/json";
      state.id = "StructureScore";
      state.registration = "f44018b5-165f-4454-af29-f4231d269c8c";
      xapi.getValue().saveState(state).then(p -> {GWT.log("okay"); return null;}, p-> GWT.log("failed", p.getFailure()));
      return;
    }
    if (event.getSource() == load) {
      nl.uu.fi.dwo.rest.dom.xapi.Account account = new nl.uu.fi.dwo.rest.dom.xapi.Account();
      account.homePage = GwtRestVars.instance().getServer();
      account.name = "u:project_wim";
      Agent agent = new Agent();
      agent.account = account;
      agent.name = "Wim van Velthoven";
      Activity activity = new Activity();
      activity.id = "pid:00000000001";
      StateDocument state = new StateDocument();
      state.activity = activity;
      state.agent = agent;
      state.content = "{}";
      state.contentType = "application/json";
      state.id = "StructureScore";
      state.registration = "f44018b5-165f-4454-af29-f4231d269c8c";
      xapi.getValue().getState(state.id, state.activity, state.agent, state.registration).then(p -> {GWT.log("okay " + p.getValue().content); return null;}, p-> GWT.log("failed", p.getFailure()));
    }
   
  }

  @Override
  public void onModuleLoad() {

//    xapi = new XapiManager();
    
    
    Defaults.ignoreJsonNulls();
    Defaults.setAddXHttpMethodOverrideHeader(false);
    GwtRestVars instance = GwtRestVars.instance();
    RPCHandlerV3 rpc = new RPCHandlerV3(null, 77, false);

    xapi = rpc.login("meesterwim", "paulien")
        .then(p -> {
          instance.setCurrentUser(p.getValue().getDomUserFull(), p.getValue().getDomLoginContext().getRealm());   
          return rpc.getSchoolLogins();
        })
        
        
        .then (p -> {
          return rpc.getLRS();
    }
    );

    xapi.onResolve(
      () -> {
        GWT.log("resolved " , xapi.getFailure());
      }
        );
    
//    xapi.setCredentials("root", "test");
//    xapi.setServer("http://localhost:8080/xapi");
 
//    xapi.setAuth("Basic MzdjZTEwMzE2NzQxN2NhODlmNDNkODA1ZDJhNGY3YjU1MzM3MzE3YjpjY2QzODMwYjc1NWJkY2E3ZDJlYzQ5NmQ0ZTkyZWQwYzJlNDljYjRh");
//    xapi.setServer("/data/xAPI/");
    
    Button hitme = new Button("SAFE");
    this.safe = hitme;
    hitme.addClickHandler(this);
    RootPanel.get().add(hitme);
    hitme = new Button("QUERY");
    this.query = hitme;
    hitme.addClickHandler(this);
    RootPanel.get().add(hitme);
    
    hitme = new Button("State");
    this.state = hitme;
    hitme.addClickHandler(this);
    RootPanel.get().add(hitme);
    
    hitme = new Button("Load state");
    this.load = hitme;
    hitme.addClickHandler(this);
    RootPanel.get().add(hitme);
    
    
  } 

}
