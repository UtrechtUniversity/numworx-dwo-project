package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.restygwt.client.JsonCallback;
import org.fusesource.restygwt.client.JsonEncoderDecoder;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.fusesource.restygwt.client.dispatcher.DefaultDispatcher;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;

import fi.dwo.gwt.lib.rest.client.RestCallers.XapiRestCaller;
import fi.dwo.gwt.lib.rest.util.Base64;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.dom.xapi.Document;
import nl.uu.fi.dwo.rest.dom.xapi.Group;
import nl.uu.fi.dwo.rest.dom.xapi.StateDocument;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsQuery;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsResult;

public class XapiManager {

  final private XapiRestCaller service = GWT.create(XapiRestCaller.class);

  private String server;
  private Map<String,String> headers = new HashMap<>();
  
  public XapiManager() {
    headers.put("X-Experience-API-Version", "1.0.1");
  }
  
  public Promise<String> saveStatement(Statement statement) {
    if(statement.actor == null) statement.actor = getAgent();
    if (statement.context != null && statement.context.team != null) {
    	Group g  = statement.context.team;
    	if (g.account != null && g.account.homePage == null) {
    		g.account.homePage = statement.actor.account.homePage;
    	}
    }
    PromiseCallback<List<String>> defer = new PromiseCallback<>();
    service.createStatement(statement, defer);
    return defer.getPromise().map(list -> list.get(0));
  }
  
  public Promise<List<String>> saveStatements(List<Statement> list) {
    PromiseCallback<List<String>> defer = new PromiseCallback<>();
    for(Statement i:list) {
      if (i.actor == null) i.actor = getAgent();
      if (i.context != null && i.context.team != null) {
      	Group g  = i.context.team;
      	if (g.account != null && g.account.homePage == null) {
      		g.account.homePage = i.actor.account.homePage;
      	}
      }
    }
    service.createStatements(list, defer);
    return defer.getPromise();
  }

  /**
   * @return the server
   */
  public String getServer() {
    return server;
  }

  /**
   * @param server the server to set
   */
  public void setServer(String server) {
    this.server = server;
    if (server != null) {
      Resource r = new Resource(server, headers);
      RestServiceProxy proxy = (RestServiceProxy) service;
      proxy.setResource(r);
      proxy.setDispatcher(new DefaultDispatcher());
    }
  }
  
  public void setCredentials(String username, String password) {
    headers.put("Authorization", "Basic " + Base64.btoa(username + ":" + password));
  }

  interface StatementsResultCodec extends JsonEncoderDecoder<StatementsResult> {};
  StatementsResultCodec codec = GWT.create(StatementsResultCodec.class);
  interface AgentCodec extends JsonEncoderDecoder<Agent> {};
  AgentCodec agentCodec = GWT.create(AgentCodec.class);

  private Agent agent;
  
  private Promise<String> sendDocument(String resource, Map<String,String> query, Document document, String method) {
    PromiseCallback<String> callback = new PromiseCallback<>();
    RestServiceProxy proxy = (RestServiceProxy) service;
    Resource r = proxy.getResource();
    r = r.resolve(resource);
    Map<String,String> headers = new HashMap<>(r.getHeaders());
    if (document.etag != null) {     
      headers.put("If-Match", document.etag);
    } else {
      //headers.put("If-None-Match", "*");        // optional, niet goed voor lrsql
    }
    headers.put("Content-Type", document.contentType);
    for(Map.Entry<String, String> entry : query.entrySet()) {
      r = r.addQueryParam(entry.getKey(), entry.getValue());
    }
    Method s = new Method(r, method).headers(headers);
    s.setDispatcher(proxy.getDispatcher());
    s.text(document.content); 
    s.send(callback);   
    return callback.getPromise();
  }
  
  public Promise<?> saveState(StateDocument state) {
      if (state.agent == null) state.agent = getAgent();
      HashMap<String,String> queryParams = new HashMap<String,String>();
      queryParams.put("stateId", state.id);
      queryParams.put("activityId", state.activity.id);
      queryParams.put("agent", encode(state.agent));
      queryParams.put("registration", state.registration);
      return sendDocument("/activities/state", queryParams, state, "PUT");
  }

  public Promise<?> updateState(StateDocument state) {
    if (state.agent == null) state.agent = getAgent();
    HashMap<String,String> queryParams = new HashMap<String,String>();
    queryParams.put("stateId", state.id);
    queryParams.put("activityId", state.activity.id);
    queryParams.put("agent", encode(state.agent));
    queryParams.put("registration", state.registration);
    return sendDocument("/activities/state", queryParams, state, "POST");
  }
 
  public Promise<StateDocument> getState(String id, Activity activity, Agent agent, String registration) {
    Deferred<StateDocument> defer = new Deferred<>();
    RestServiceProxy proxy = (RestServiceProxy) service;
    Resource r = proxy.getResource();
    Method m = r.resolve("/activities/state")
        .addQueryParam("stateId", id)
        .addQueryParam("activityId", activity.id)
        .addQueryParam("agent", encode(agent))
        .addQueryParam("registration", registration)
        .get()
        .expect(200,404);
    m.setDispatcher(proxy.getDispatcher());
    m.send( new MethodCallback<String>() {

      @Override
      public void onFailure(Method method, Throwable exception) {
        defer.fail(exception);
      }

      @Override
      public void onSuccess(Method method, String response) {
        StateDocument state = new StateDocument();
        state.activity = activity;
        state.agent = agent == null ? getAgent() : agent;
        state.registration = registration;
        if (method.getResponse().getStatusCode() == 200) {
          state.content =  response;
          state.contentType = method.getResponse().getHeader("Content-Type");
          state.etag = method.getResponse().getHeader("ETag");
          state.timestamp = method.getResponse().getHeader("Last-Modified");
        }
        state.id = id;
        defer.resolve(state);
      }});
    
    
    
    return defer.getPromise();
  }
  
  
  private String encode(Agent agent) {
    return agentCodec.encode(agent).toString();
  }
  
  public Promise<StatementsResult> queryStatements(StatementsQuery q) {
    Deferred<StatementsResult> callback = new Deferred<>();
    RestServiceProxy proxy = (RestServiceProxy) service;
    Resource r = proxy.getResource();
    r = new Resource(r.getUri() + "/statements", r.getHeaders());
    if (q.verbID != null) r = r.addQueryParam("verb", q.verbID);
    if (q.registration != null) r = r.addQueryParam("registration", q.registration);
    if (q.since != null) r = r.addQueryParam("since", q.since);
    if (q.ascending != null) r = r.addQueryParam("ascending", q.ascending.toString());
    if (q.agent != null) r = r.addQueryParam("agent", encode(q.agent));
    else {
      r = r.addQueryParam("agent", encode(getAgent()));
    }
    if (q.relatedActivities != null) r =  r.addQueryParam("related_activities", q.relatedActivities.toString());
    if (q.relatedAgents != null) r =  r.addQueryParam("related_agents", q.relatedAgents.toString());
    if (q.activityID != null) r = r.addQueryParam("activity", q.activityID);
    if (q.until != null) r = r.addQueryParam("until", q.until);
    if (q.limit != null) r = r.addQueryParam("limit", q.limit.toString());
    //etc

    Method method = r.get();
    method.setDispatcher(proxy.getDispatcher());
    method.send(asCallback(callback));
    
    return callback.getPromise().then(this::doMore);
  }
  
  private Promise<StatementsResult> doMore(Promise<StatementsResult> p) {
	  Deferred<StatementsResult> callback = new Deferred<>();
	  StatementsResult value = p.getValue();
	  String more = value.more;
	  if (more == null || more.isEmpty()) return p;
	  // moeilijk geval
	  RestServiceProxy proxy = (RestServiceProxy) service;
	  Resource r = proxy.getResource();
	  String path = r.getPath();
	  int end = path.indexOf("/", 10);
	  if (end >= 0) path = path.substring(0,end);
	  r = new Resource(path, r.getHeaders());
	  r = r.resolve(more);
	  Method method = r.get();
	  method.setDispatcher(proxy.getDispatcher());
	  method.send(asCallback(callback));
	  return callback.getPromise()
			  .map(v -> { value.statements.addAll(v.statements); v.statements = value.statements; return v; })
			  .then(this::doMore).recover(f -> value);
  }
  

protected JsonCallback asCallback(Deferred<StatementsResult> callback) {
	return new JsonCallback() {
      
      @Override
      public void onSuccess(Method method, JSONValue response) {
        StatementsResult result = codec.decode(response);
        callback.resolve(result);      
      }
      
      @Override
      public void onFailure(Method method, Throwable exception) {
        callback.fail(exception);    
      }
    };
}

  public void setAuth(String auth) {
    headers.put("Authorization", auth);
  }
  
  public void setAgent(Agent agent) {
    this.agent = agent;
  }
  public Agent getAgent() {
    return agent;
  }
}
