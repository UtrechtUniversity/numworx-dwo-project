package nl.uu.fi.dwo.lms.jclient.rest.managers;

import static org.junit.Assert.*;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import javax.swing.text.DateFormatter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.XapiManager;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.xapi.Account;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.dom.xapi.StateDocument;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsQuery;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsResult;
import nl.uu.fi.dwo.rest.dom.xapi.Verb;
import nl.uu.fi.dwo.rest.util.RestyDateTimeFormat;


/*
 * Deze test draait ten opzichte van ADL-LRS docker xAPI implementatie
 */
public class XapiManagerIT {

  private static final String VERB = "http://localhost:8080/verbs/test";
  private XapiManager manager;
  private DateFormatter format;
  private Agent agent;
  
  @Before
  public void setUp() throws Exception {
    URL base = new URL("http://localhost:8080/");
    DomLRS lrs = new DomLRS();
    agent = new Agent();
    agent.name = "testaccount";
    agent.account = new Account();
    agent.account.homePage = base.toString();
    agent.account.name = "testaccount";
    lrs.setAgent(agent);
    lrs.setAuth("Basic YWRtaW46YWRtaW4=");
    lrs.setEndpoint("/XAPI");
    manager = new XapiManager(lrs, base);
    format = new DateFormatter(new SimpleDateFormat(RestyDateTimeFormat.RESTY_DATETIME_FORMAT));
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void test() throws Exception {
    Date now = new Date();
    Statement out,in;
    out = new Statement();
    out.actor = null;
    out.verb = new Verb();
    out.verb.id = VERB;
    out.object = new Activity();
    out.object.id = "http://localhost:8080/activities/test";
    
    String id = manager.saveStatement(out).getValue();
    
    
    StatementsQuery q = new StatementsQuery();
    q.limit = 1;
    q.since = format.valueToString(now);
    Promise<StatementsResult> r = manager.queryStatements(q);
    in = r.getValue().statements.get(0);
    assertEquals(id, in.id);
  }
  @Test
  public void testState() throws Exception {
    
    StateDocument state = new StateDocument();
    state.activity = new Activity();
    String activityId = "http://localhost:8080/activities/test";
    state.activity.id = activityId;
    state.content = "dit is een test";
    state.contentType = "text/plain";
    state.id = "test";
    Promise<String> result = manager.saveState(state);
    System.out.println(result.getValue());
    
    Promise<StateDocument> r = manager.getState("test", state.activity, agent, null);
    
    assertEquals(state.content, r.getValue().content);
  }

  @Test
  public void testVoiding() throws Exception  {
	    Date now = new Date();
	    Statement out,in;
	    out = new Statement();
	    out.actor = null;
	    out.verb = new Verb();
	    out.verb.id = VERB;
	    out.object = new Activity();
	    out.object.id = "http://localhost:8080/activities/test";
	    
	    String id = manager.saveStatement(out).getValue();
  
	    out = new Statement();
	    out.verb = new Verb();
	    out.verb.id = "http://adlnet.gov/expapi/verbs/voided";
	    out.verb.display = Collections.singletonMap("en-US", "voided");
	    out.object = new Activity();
	    out.object.id = id;
	    out.object.objectType = Activity.STATEMENT_REF;
	    Promise<?> p = manager.saveStatement(out);
	    Promise<StatementsResult> q = p.then( s -> {  
	        StatementsQuery query = new StatementsQuery();
	        query.limit = 1;
	        query.since = format.valueToString(now);
	        Promise<StatementsResult> r = manager.queryStatements(query);
	        return r;
	    });
	    System.out.println(q.getValue().statements);		
	  
  }
  
  
}
