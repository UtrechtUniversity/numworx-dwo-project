package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoPage;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Utility pattern
 * 
 * @author peterboon
 *
 */
class Util {
  private static final JSONNumber NUMBER_NUL = new JSONNumber(0);
  //private static final JSONNumber[] EMPTY_NUMBERS = new JSONNumber[0];
  private static JSONNumber[] EMPTY_NUMBERS() { return new JSONNumber[0]; }
  private static final Logger LOG = Logger.getLogger(Util.class.getName());

  private Util() {}

  static int getAantalOpdrachten(JSONValue launch_data) {
    int aantalOpdrachten;
    try {
      aantalOpdrachten =
          Integer.parseInt(launch_data.isObject().get("aantalOpdrachten_1").isString().stringValue());
      LOG.log(Level.FINE, "aantalOpdrachten = " + aantalOpdrachten);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "aantalOpdrachten failed", e);
      aantalOpdrachten = 0;
    }
    return aantalOpdrachten;
  }

  static JSONNumber[] getScores(String suspend_data, int aantalOpdrachten) {
      try {
        if(suspend_data.isEmpty()) return EMPTY_NUMBERS();        
        JSONObject sd = JSONParser.parseLenient(suspend_data).isObject();
        JSONValue value = sd.get("onsState");
        if(value == null || value.isNull() != null) return EMPTY_NUMBERS();
        JSONObject onsState = value.isObject();
        value = onsState.get("orScores");
        if(value == null || value.isNull() != null) value = new JSONArray();
        JSONArray array = value.isArray();
        if(array.size() == 0) array.set(0, new JSONArray());
        value = array.get(0);
        if(value == null || value.isNull() != null) value = new JSONArray();
        JSONArray orScores = value.isArray();
        int max = Math.min(aantalOpdrachten, orScores.size());

        value = onsState.get("bezocht");
        JSONArray bezocht;
        try {
        	bezocht = value.isArray().get(0).isArray();
        	max = Math.max(max, bezocht.size());
        } catch(Exception oops) {
        	bezocht = new JSONArray();
        }
        
        JSONNumber[] scores = new JSONNumber[max];
        for (int i = 0; i < max; i++) {
            try {
                JSONNumber number = orScores.get(i).isNumber();
                LOG.log(Level.FINE, "score " + i + " = " + number);
                scores[i] = number;
                
                if (number.doubleValue() == 0.0) { 
                	if (i >= bezocht.size() || bezocht.get(i) == JSONBoolean.getInstance(false))
                		scores[i] = null; // niet bezocht, geen score!!!
                }
                
                
            } catch (Exception e) {
                LOG.log(Level.WARNING, "score " + i, e);
                if (i < bezocht.size() && bezocht.get(i) == JSONBoolean.getInstance(true))
                	scores[i] = new JSONNumber(0);
            }
        }
        return scores;
      } catch (Throwable e) {
        LOG.log(Level.WARNING, "getScores\n"+e);
        return EMPTY_NUMBERS();
      }
  }
  
  static Map<PersistenceId, DomResultStudentScoPage> getPages(JSONValue launchdata, String suspend_data, String review_data, String review_check, boolean premium) {
    HashMap<PersistenceId, DomResultStudentScoPage> result = new HashMap<>();
    
    int aantal = getAantalOpdrachten(launchdata);
    LOG.info("getPages aantal = " + aantal);
    
    JSONNumber scores[] = getScores(suspend_data, aantal);
    int ls = scores.length;
    LOG.info("getPages scores = " + Arrays.asList(scores)   + " " + ls);
    JSONNumber maxScores[] = getMaxScores(launchdata, aantal);
    LOG.info("getPages maxscores = " + Arrays.asList(maxScores));
    JSONNumber correctie[] = getCorrectie(review_data, aantal, premium);
    LOG.info("getPages correctie = " + Arrays.asList(correctie));
    boolean checkDocent[] = getCheckDocent(launchdata, review_check, correctie, aantal, premium);
    for(int i = 0; i < aantal; i++) {
     
      String label = String.valueOf(i+1);
      // if hasTitle, dan label = launchdata....getString("titel");
      JSONObject opdracht = launchdata.isObject().get("opdracht_1_"+(i+1)).isObject();
      if (JSONBoolean.getInstance(true).equals(opdracht.get("hasTitle"))) {
    	  label = opdracht.get("titel").isString().stringValue();
      }
      DomResultStudentScoPage item = new DomResultStudentScoPage(label);
      item.setNodeId(i);
      
      //if (scores[i] == null) scores[i] = new JSONNumber(0); // FIXME dit is alleen voor het testen XXX 

      if (i < ls && scores[i] != null)
      {
    	  if (checkDocent[i]) item.setScore(-1.0);
    	  else
    		  item.setScore(scores[i].doubleValue());
    	  item.setMaxScore(maxScores[i].doubleValue());
      } else 
      {
    	  item.setScore(0.0);
    	  item.setMaxScore(null);
      }
      if (i < correctie.length && correctie[i] != null)
      {
    	  item.setCorrectie(correctie[i].doubleValue());
    	  item.setMaxScore(maxScores[i].doubleValue()); // https://numworx.atlassian.net/browse/LMS-683
      }
      
      if (opdracht.containsKey("maxFactor")) {
    	  JSONValue value = opdracht.get("maxFactor");
    	  if (value != null) {
    		  JSONNumber n = value.isNumber();
    		  if (n != null) {
    			  item.setMaxFactor((float) n.doubleValue());
    		  }
    	  }
      }
      PersistenceId key = new PersistenceId("LOCAL;none;" + i);
      result.put(key, item);
    }
    return result;
    
  }

  private static boolean[] getCheckDocent(JSONValue launchdata, String string, JSONNumber[] correctie, int aantal, boolean premium) {
	boolean checkDocent[] = new boolean[aantal];
	if (premium) {
		JSONArray checked = ((string.isEmpty())  ? JSONNull.getInstance() : JSONParser.parseStrict(string)).isArray();
		for (int i = 0; i < aantal; i++) {
			JSONObject obj = launchdata.isObject().get("opdracht_1_" + (i+1)).isObject();
			JSONValue check = obj.get("checkDocent");
			checkDocent[i] = JSONBoolean.getInstance(true).equals(check);
			if (checked != null && i < checked.size()) {
				JSONBoolean bool = checked.get(i).isBoolean();
				if (bool != null) checkDocent[i] = bool.booleanValue();
			}
			//else if (i < correctie.length && correctie[i].doubleValue() != 0) checkDocent[i] = false; // FIXME alleen even omdat string niet bestaat....
		}
	}
	return checkDocent;
}

private static JSONNumber[] getCorrectie(String review_data, int aantal, boolean premium) {
    LOG.info("getCorrectie " + review_data);
    if(review_data == null || !review_data.startsWith("{") || !premium)
        return EMPTY_NUMBERS();
    
    try {
      JSONObject review = JSONParser.parseLenient(review_data).isObject();
      JSONArray  data = review.get("opdrContStates").isArray().get(0).isArray();
      JSONNumber[] result = new JSONNumber[data.size()];
      for(int i = 0; i < result.length; i++) {
        result[i] = null; // can be null as not corrected
        JSONValue value = data.get(i);
        if(value != null) {
          Double sum = sumCorrectie(value);
		result[i] = sum == null ? null : new JSONNumber(sum.doubleValue());
        }
      }
      return result;
    } catch (Exception e) {
        LOG.log(Level.WARNING, "getCorrectie catch", e);
        return EMPTY_NUMBERS();
    }
  }

  private static Double plus(Double a, Double b) {
	  if (a == null) return b;
	  if (b == null) return a;
	  return a + b;
  }

  private static Double sumCorrectie(JSONValue value) {
    if(value == null) return null;
    JSONArray a = value.isArray();
    if(a != null) {
      Double result = null;
      for(int i=0; i < a.size(); i++) {
        result = plus(result, sumCorrectie(a.get(i)));
      }
      return result;
    }
    JSONObject o = value.isObject();
    if(o != null) {
      double result = 0;
      value = o.get("interactiePanelStates");
      if(value != null) {
        result = plus( sumCorrectie(value), result);
      }
      value = o.get("reviewInteractieData");
      if(value != null) {
        result = plus (sumCorrectie(value), result);
      }
      value = o.get("reviewScoreCorrectie");
      if (value != null) 
        result = plus( value.isNumber().doubleValue(), result);
      return result;
    }
    return null;
  }

  private static JSONNumber[] getMaxScores(JSONValue launchdata, int aantal) {
    JSONNumber[] result = new JSONNumber[aantal];   
    for(int i = 0; i < aantal; i++) {
      result[i] = new JSONNumber(10);
      JSONValue value = launchdata.isObject().get("opdracht_1_" + (i+1));
      if(value == null) continue;
      JSONObject opdracht = value.isObject();
      value = opdracht.get("scoreMax");
      if(value != null) {
        result[i] = value.isNumber();
      }     
    }
    return result;
  }
  
}
