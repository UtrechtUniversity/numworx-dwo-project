package nl.numworx.schoolyear.jclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

/**
 * Schoolyear client for Java.
 * 
 * see https://docs.schoolyear.com/docs/schoolyear-api/
 * 
 */

public class SchoolyearClient {
	
	private static final DateFormat yourDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
	static {
		yourDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		System.out.println(yourDateFormat.format(new java.util.Date()));
	}
	private final Genson genson = new GensonBuilder()
			  .useDateFormat(yourDateFormat)
			  .useDateAsTimestamp(false)
			  .setSkipNull(true)
			  .create();

	private static final Logger LOG = Logger.getLogger(SchoolyearClient.class.getName());
	private final URL api;
	private final String key;

	static public class Builder {
		URI url = URI.create("https://beta.api.schoolyear.app/");
		String key = "8d5b7acd-5bdc-46cb-97dc-0b12b2c79a87.5hsdgmWvy6tqPN-JOnv-ZDUOD0ntipeR";
		public Builder() {
			
		}
		
		public SchoolyearClient build() {
			URL api = null;
			try {
				api = url.toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException("should not happen", e);
			}
			return new SchoolyearClient(api, key);
		}
	}
	
	SchoolyearClient(URL endpoint, String key) {
		this.api = endpoint;
		this.key = key;
		
	}
	
	public ExamDTO createExam(ExamDTO exam) throws IOException {
		return send("v2/exam", "POST", exam, ExamDTO.class);
	}
	
	void updateExam() {
		
	}
	
	void openSettingsUI() {
		
	}
	
	void openDashboardUI() {
		
	}
	
	private <T> T send(String endpoint, String method, Object input, Class<T> outputType) throws IOException {
		URL url = new URL(api, endpoint);
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      DataOutputStream outStream = null;
	      conn.setRequestMethod(method);
	      conn.setRequestProperty("Accept", "application/json");
	      conn.setRequestProperty("Content-Type", "application/json");
	      conn.setRequestProperty("Accept-Charset", "UTF-8");
	      conn.setRequestProperty("X-Sy-Api",key);

	      // conn.setDoInput(true);
	      conn.setDoOutput(true);
	      conn.setUseCaches(false);
	      outStream = new DataOutputStream(conn.getOutputStream());
	      String jsonOut = genson.serialize(input);
	      LOG.log(Level.FINEST, "Sending: {0}", new Object[] {jsonOut.toString()});
	      outStream.write(jsonOut.getBytes("UTF-8"));
	      outStream.close();
	      int responseCode = conn.getResponseCode();
	      if (responseCode == 204) 
	    	  return null; // No content
		  if (responseCode != 200 && responseCode != 201) {
	          // String json = conn.getResponseMessage();
	          BufferedReader br = new BufferedReader(
	              new InputStreamReader((conn.getErrorStream()), StandardCharsets.UTF_8));

	          String output;
	          StringBuilder json = new StringBuilder();
	          while ((output = br.readLine()) != null) {
	            json.append(output);
	          }
	          conn.disconnect();
	        LOG.log(Level.WARNING, "Code: {0}. Reason {1} Message {2}",
	            new Object[] {responseCode, conn.getResponseMessage(), json});
	        return null;
	      }

	      BufferedReader br = new BufferedReader(
	          new InputStreamReader((conn.getInputStream()), StandardCharsets.UTF_8));

	      String output;
	      StringBuilder json = new StringBuilder();
	      while ((output = br.readLine()) != null) {
	        json.append(output);
	      }
	      conn.disconnect();
	      // decode JSON
	      // DomResultsPerTeacher user = genson.deserialize(json.toString(), new
	      // GenericType<DomResultsPerTeacher>(){});
	      LOG.log(Level.FINEST, "Received: {0}", new Object[] {json.toString()});

	      T result = genson.deserialize(json.toString(), outputType);
	      return result;
	}
	
	
}
