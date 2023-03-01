package fi.dwo.commons.domainmodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import nl.numworx.async.Async;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.dom.xapi.StateDocument;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsQuery;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsResult;

public class XapiManager {

  private static final Logger LOG = Logger.getLogger(XapiManager.class.getName());
  private final Agent agent;
  private final String authentication;
  private final URL endpoint;
  private final Async async;
  private final XapiService service;
  
  public interface XapiService {
    List<String> createStatements(List<Statement> statements) throws IOException;
    StatementsResult queryStatements(StatementsQuery query) throws IOException;
    StateDocument getState(String stateId, Activity activity, Agent agent, String registration) throws IOException;
    String saveState(StateDocument state) throws IOException;
    String updateState(StateDocument state) throws IOException;
  }
  
  class XapiImpl implements XapiService {

    Genson genson;
    
    private XapiImpl() {
      genson = new GensonBuilder()
          .setSkipNull(true)
          .create();
    }

    @Override
    public List<String> createStatements(List<Statement> statements) throws IOException {
      for(Statement item: statements) {
        if (item.actor == null) item.actor = agent;
      }
      URL url = new URL(endpoint, "statements");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      OutputStream outStream;
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Accept", "application/json");
      conn.setRequestProperty("Accept-Encoding", "application/json");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Authorization", authentication);
      conn.setRequestProperty("Accept-Charset", "UTF-8");
      conn.setRequestProperty("X-Experience-API-Version", "1.0.1");
      // conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setUseCaches(false);
      outStream = conn.getOutputStream();
      Object o = statements;
      if (statements.size() == 1) {
        o = statements.get(0);
      }
      genson.serialize(o, outStream);
      outStream.close();
      int responseCode = conn.getResponseCode();
      if (responseCode == 204) 
          return null; // No content
      if (responseCode != 200) {
        LOG.log(Level.WARNING, "Code: {0}. Reason {1}",
            new Object[] {responseCode, conn.getResponseMessage()});
        throw new IOException(conn.getResponseMessage());
      }
      List<String> result = genson.deserialize(conn.getInputStream(), new GenericType<List<String>>() {});
      conn.disconnect();
      return result;
    }

    @Override
    public StatementsResult queryStatements(StatementsQuery q) throws IOException {
      StringBuilder sb = new StringBuilder("statements?");
      if (q.verbID != null) append(sb,"verb", q.verbID);
      if (q.registration != null) append(sb, "registration", q.registration);
      if (q.since != null) append(sb,"since", q.since);
      if (q.until != null) append(sb, "until", q.until);
      if (q.limit != null) append(sb, "limit", q.limit.toString());
      if (q.ascending != null) append(sb,"ascending", q.ascending.toString());
      if (q.agent != null) append(sb,"agent", encode(q.agent));
      if (q.relatedActivities != null) append(sb, "related_activities", q.relatedActivities.toString());
      if (q.activityID != null) append(sb, "activity", q.activityID);
      
      sb.setLength(sb.length()-1);
      URL url = new URL(endpoint, sb.toString());
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestProperty("Accept", "application/json");
      conn.setRequestProperty("Accept-Encoding", "application/json");
      conn.setRequestProperty("Authorization", authentication);
      conn.setRequestProperty("Accept-Charset", "UTF-8");
      conn.setRequestProperty("X-Experience-API-Version", "1.0.1");
      StatementsResult result = genson.deserialize(conn.getInputStream(), StatementsResult.class);
      conn.disconnect();
      return result;
    }

    private String encode(Agent agent) {
      return genson.serialize(agent);
    }

    private void append(StringBuilder sb, String key, String value) {
      if (value == null) return;
      try {
        sb.append(key).append('=').append(URLEncoder.encode(value, "UTF-8")).append('&');
      } catch (UnsupportedEncodingException e) {
      }      
    }

    public StateDocument getState(String stateId, Activity activity, Agent agent, String registration) throws IOException {
      StringBuilder sb = new StringBuilder("activities/state?");
      append(sb, "stateId", stateId);
      append(sb, "activityId", activity.id);
      if (agent == null) agent = XapiManager.this.agent;
      append(sb, "agent", encode(agent));
      append(sb, "registration", registration);
      sb.setLength(sb.length()-1);
      URL url = new URL(endpoint, sb.toString());
      
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestProperty("X-Experience-API-Version", "1.0.1");
      conn.setRequestProperty("Authorization", authentication);
      int code = conn.getResponseCode();
      if (code != 200) {
        throw new IOException(conn.getResponseMessage());
      }
      sb.setLength(0);
      StateDocument state  = new StateDocument();
      state.activity = activity;
      state.agent = agent;
      state.registration = registration;
      state.contentType = conn.getContentType();
      state.id = stateId;
      InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
      int ch;
      while ( (ch = reader.read()) >= 0) sb.append((char)ch);
      state.content = sb.toString();
      state.etag = conn.getHeaderField("ETag");
      state.timestamp = conn.getHeaderField("Last-Modified");
      return state;
    }

    @Override
    public String saveState(StateDocument state) throws IOException {
      if (state.agent == null) state.agent = agent;
      HashMap<String,String> queryParams = new HashMap<String,String>();
      queryParams.put("stateId", state.id);
      queryParams.put("activityId", state.activity.id);
      queryParams.put("agent", encode(state.agent));
      queryParams.put("registration", state.registration);
      return sendDocument("activities/state?", queryParams, state, "PUT");
    }

    @Override
    public String updateState(StateDocument state) throws IOException {
      if (state.agent == null) state.agent = agent;
      HashMap<String,String> queryParams = new HashMap<String,String>();
      queryParams.put("stateId", state.id);
      queryParams.put("activityId", state.activity.id);
      queryParams.put("agent", encode(state.agent));
      queryParams.put("registration", state.registration);
      return sendDocument("activities/state?", queryParams, state, "POST");
    }

    private String sendDocument(String resource, HashMap<String, String> queryParams,
        StateDocument state, String method) throws IOException {
      final StringBuilder sb = new StringBuilder(resource);
      queryParams.forEach( (k,v) -> append(sb,k,v));
      sb.setLength(sb.length()-1);
      URL url = new URL(endpoint, sb.toString());
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod(method);
      conn.setDoOutput(true);
      conn.setRequestProperty("Content-Type", state.contentType);
      conn.setRequestProperty("X-Experience-API-Version", "1.0.1");
      if (state.etag != null) {     
        conn.setRequestProperty("If-Match", state.etag);
      } else {
//        conn.setRequestProperty("If-None-Match", "*");        // optional, LL wel de ander niet,
      }
      conn.setRequestProperty("Authorization", authentication);
      OutputStream out = conn.getOutputStream();
      out.write(state.content.getBytes(StandardCharsets.UTF_8));
      out.close();
      int code = conn.getResponseCode();
      if (code == 204)
        return null;
      if (code >= 400) {
        throw new IOException(conn.getResponseMessage());
      }
      InputStream in = conn.getInputStream();
      InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
      sb.setLength(0);;
      int ch;
      while ( (ch = reader.read()) >= 0) sb.append( (char) ch);
      return sb.toString();
    }
    
  }
  
  
  public XapiManager(DomLRS lrs, URL base) throws MalformedURLException {
    agent = lrs.getAgent();
    authentication = lrs.getAuth();
    String s = lrs.getEndpoint();
    if (!s.endsWith("/")) s += "/"; // ends with /
    endpoint = new URL(base, s);
    async = new Async();
    service = async.mediate(new XapiImpl(), XapiService.class);
  }

  public Promise<String> saveStatement(Statement statement) {
    try {
      return async.call(service.createStatements(Collections.singletonList(statement)))
          .map(list -> list.get(0));
    } catch (IOException e) {
      return Promises.failed(e);
    }
  }

  public Promise<StatementsResult> queryStatements(StatementsQuery q) {
    try {
      return async.call(service.queryStatements(q));
    } catch (IOException e) {
      return Promises.failed(e);
    }
  }

  public Promise<StateDocument> getState(String stateId, Activity activity, Agent agent, String registration) {
    try {
      return async.call(service.getState(stateId, activity, agent, registration));
    } catch (IOException e) {
      return Promises.failed(e);
    }
  }
  
  public Promise<String> saveState(StateDocument state) {
    try {
      return async.call(service.saveState(state));
    } catch (IOException e) {
      return Promises.failed(e);
    }
  }

  public Promise<String> updateState(StateDocument state) {
    try {
      return async.call(service.updateState(state));
    } catch (IOException e) {
      return Promises.failed(e);
    }
  }
}
