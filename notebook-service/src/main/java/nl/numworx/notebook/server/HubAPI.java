package nl.numworx.notebook.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.ext.javadatetime.JavaDateTimeBundle;
import com.owlike.genson.ext.jaxb.JAXBBundle;

import nl.numworx.notebook.server.rest.Contents;
import nl.numworx.notebook.server.rest.File;
import nl.numworx.notebook.server.rest.Folder;
import nl.numworx.notebook.server.rest.Progress;
import nl.numworx.notebook.server.rest.Server;
import nl.numworx.notebook.server.rest.Token;
import nl.numworx.notebook.server.rest.TokenRequest;
import nl.numworx.notebook.server.rest.Tokens;
import nl.numworx.notebook.server.rest.User;


public class HubAPI {


	static final String APPLICATION_JSON = "application/json";

	private Logger LOG = Logger.getLogger(this.getClass().getName());
	
	final Genson genson = new GensonBuilder()
			  .withBundle(new JAXBBundle())
			  .useDateAsTimestamp(false)
			  .setSkipNull(true)
			  .withBundle(new JavaDateTimeBundle())
			  .create();
	
	final String token;
	final URI hubAPI = URI.create(System.getProperty(DWO_HUB, "https://hub-dev.dwo.nl/hub/api/"));

	public static final String DWO_HUB_TOKEN = "DWO_HUB_TOKEN";
	public static final String DWO_HUB = "DWO_HUB";
	
	
	  public HubAPI(String token2) {
		token = "token " + token2;
	  }

	  public HubAPI() {
		  this(System.getProperty(DWO_HUB_TOKEN));
	  }

	protected <T> T get(String path, Class<T> c) throws IOException {
	      URL url = new URL(hubAPI.toURL(), path); // TODO make login
	      HubException e;
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      conn.setRequestMethod("GET");
	      conn.setRequestProperty("Accept", APPLICATION_JSON);
	      conn.setRequestProperty("Authorization", token);
	      
	      conn.setUseCaches(false);

	      if (conn.getResponseCode() != 200) {
	        LOG.log(Level.WARNING, "Code: {0}. Reason: {1}",
	            new Object[] {conn.getResponseCode(), conn.getResponseMessage()});
	        if (conn.getResponseCode() == 400) {// Dwo2Exception
	          e = exception(conn);
	        } else if (conn.getResponseCode() == 401) { 
	          e = new HubException(401, "No Such User");
	        } else {
	          e = exception(conn);
	        }
	        LOG.log(Level.WARNING, "Error in restAPI", e);
	        throw e;
	      }

	      return extract(c, conn);
	  }

	 protected <T> void events(String path, Consumer<T> consumer, Class<T> c) throws IOException {
	      URL url = new URL(hubAPI.toURL(), path); // TODO make login
	      HubException e;
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      conn.setRequestMethod("GET");
	      conn.setRequestProperty("Accept", APPLICATION_JSON);
	      conn.setRequestProperty("Authorization", token);
	      
	      conn.setUseCaches(false);

	      if (conn.getResponseCode() != 200) {
	        LOG.log(Level.WARNING, "Code: {0}. Reason: {1}",
	            new Object[] {conn.getResponseCode(), conn.getResponseMessage()});
	        if (conn.getResponseCode() == 400) {// Dwo2Exception
	          e = exception(conn);
	        } else if (conn.getResponseCode() == 401) { 
	          e = new HubException(401, "No Such User");
	        } else {
	          e = exception(conn);
	        }
	        LOG.log(Level.WARNING, "Error in restAPI", e);
	        throw e;
	      }
			InputStream inputStream = conn.getInputStream();
			if (inputStream == null || !"text/event-stream".equals(conn.getContentType()))
				return;
			BufferedReader br = new BufferedReader(
			      new InputStreamReader(inputStream, StandardCharsets.UTF_8)); // XXX force
			  String output;
			  StringBuilder json = new StringBuilder();
			  while ((output = br.readLine()) != null) {
				  if (output.startsWith("data:"))
				  { T result = genson.deserialize(output.substring(5).trim(), c);
				  	consumer.accept(result);
				  }
			  }
			  conn.disconnect();
	 }
	
	
	  protected <T> T post(String path, Object params, Class<T> c) throws IOException {
	      URL url = new URL(hubAPI.toURL(), path); // TODO make login
	      HubException e;
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      conn.setRequestMethod("POST");
	      conn.setRequestProperty("Accept", APPLICATION_JSON);
	      conn.setRequestProperty("Authorization", token);
	      
	      conn.setUseCaches(false);
	
	      if(params != null) {
	    	  params(params, conn);
	      }
	      
	      
	      int responseCode = conn.getResponseCode();
		  if (responseCode != 200) {
	        LOG.log(Level.WARNING, "Code: {0}. Reason: {1}",
	            new Object[] {responseCode, conn.getResponseMessage()});
	        if (responseCode == 201) {
	        	return extract(c, conn);
	        }
	        if (responseCode == 400) { // general error 
	          e = exception(conn);
	        } else if (conn.getResponseCode() == 401) { 
	          e = new HubException(401,"No Such User");
	        } else {
	          e = exception(conn);
	        }
	        LOG.log(Level.WARNING, "Error in restAPI", e);
	        throw e;
      }

      return extract(c, conn);
  }
	  
	  
	  protected <T> T put(String path, Object o, Class<T> c) throws IOException {
	      URL url = new URL(hubAPI.toURL(), path); // TODO make login
	      HubException e;
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      conn.setRequestMethod("PUT");
	      conn.setRequestProperty("Accept", APPLICATION_JSON);
	      conn.setRequestProperty("Authorization", token);
	      conn.setUseCaches(false);
// with params
	      
	      params(o, conn);
	
	      int responseCode = conn.getResponseCode();
		  if (responseCode != 200) {
	        LOG.log(Level.WARNING, "Code: {0}. Reason: {1}",
	            new Object[] {responseCode, conn.getResponseMessage()});
	        if (responseCode == 201) {
	        	T result = extract(c, conn);
	        	return result;
	        }
	        
	        
	        if (responseCode == 400) { // general error 
	          e = exception(conn);
	        } else if (conn.getResponseCode() == 401) { 
	          e = new HubException(401,"No Such User");
	        } else {
	          e = exception(conn);
	        }
	        LOG.log(Level.WARNING, "Error in restAPI", e);
	        throw e;
      }

      T result = extract(c, conn);
      return result;
  }

	private void params(Object o, HttpURLConnection conn) throws IOException, UnsupportedEncodingException {
		conn.setRequestProperty("Content-Type", APPLICATION_JSON);
	      conn.setDoOutput(true);
	      OutputStream outStream = conn.getOutputStream();
	      String jsonOut = genson.serialize(o);
	      LOG.log(Level.FINEST, "Sending: {0}", new Object[] {jsonOut.toString()});
	      outStream.write(jsonOut.getBytes("UTF-8"));
	      outStream.close();
	}

	private <T> T extract(Class<T> c, HttpURLConnection conn) throws IOException {
		InputStream inputStream = conn.getInputStream();
		if (inputStream == null || !APPLICATION_JSON.equals(conn.getContentType()))
			return null;
		BufferedReader br = new BufferedReader(
		      new InputStreamReader(inputStream, StandardCharsets.UTF_8)); // XXX force
		  String output;
		  StringBuilder json = new StringBuilder();
		  while ((output = br.readLine()) != null) {
		    json.append(output);
		  }
		  conn.disconnect();
		  LOG.log(Level.FINEST, "Received: {0}", new Object[] {json.toString()});
		  T result = genson.deserialize(json.toString(), c);
		return result;
	}

	private HubException exception(HttpURLConnection conn) throws IOException {
		HubException e;
		InputStream errorStream = conn.getErrorStream();
		String type = conn.getContentType();
		if (errorStream == null || !Objects.equals(APPLICATION_JSON, type)) {
			return new HubException(conn.getResponseCode(), conn.getResponseMessage());
		}
		
		BufferedReader br = new BufferedReader(
		      new InputStreamReader(errorStream, StandardCharsets.UTF_8));

		  String output;
		  StringBuilder json = new StringBuilder();
		  while ((output = br.readLine()) != null) {
		    json.append(output);
		  }
		  conn.disconnect();
		  e = genson.deserialize(json.toString(), HubException.class);
		return e;
	}
	  
	  
	  public User getUserInfo(String user) throws IOException {		  
		  return get("users/" + user, User.class);
	  }
	  
	  public Server startServer(String user) throws IOException {
		Server s = getUserInfo(user).servers.get("");
		if (s != null) return s;
		try {
			post("users/" + user + "/servers/", null, Void.class);
		} catch (HubException e) {
			if (e.status == 400 && e.getMessage().contains("already running"))
					LOG.log(Level.WARNING, e.getMessage());
			else
				throw e;
		}
		return getUserInfo(user).servers.get("");
	  }
	  
	  public Folder mkdir(String user, String folder) throws IOException {
		  Contents contents = new Contents();
		  contents.type = "directory";
		  contents.name = folder;
		  contents.path = folder;																									
		  return put("/user/" + user + "/api/contents/" + contents.path, contents, Folder.class);
	  }
	  
	  public File create(String user, String path, Contents contents) throws IOException {
		  return put("/user/" + user + "/api/contents/" + path, contents, File.class);
	  }
	  
	  
	  public Folder listFolder(String user, String path) throws IOException {
		  return get("/user/" + user + "/api/contents/" + path, Folder.class);		  
	  }
	  
	  public File download(String user, String path) throws IOException {
		  return get("/user/" + user + "/api/contents/" + path, File.class);		  		  
	  }
	  
	  public Tokens getTokenFor(String user) throws IOException {
		  Tokens token = get("users/" + user + "/tokens", Tokens.class);
		  return token;
	  }

	  public void progress(String user, Consumer<Progress> consumer) throws IOException {
		  events("users/" + user + "/server/progress" , consumer, Progress.class);
	  }
	  
	  
	  
	  public String createTokenFor(String user) throws IOException {
		  TokenRequest request = new TokenRequest();
		  request.note = "no note";
		  request.expires_in = 3600L; // 1 hour
		  Token token = post("users/" + user + "/tokens", request, Token.class);
		return token.token;
	}
}
