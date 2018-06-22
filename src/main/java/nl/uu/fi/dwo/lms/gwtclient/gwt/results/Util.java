package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONArray;
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
      JSONObject sd = JSONParser.parseLenient(suspend_data).isObject();
      JSONObject onsState = sd.get("onsState").isObject();
      JSONArray orScores = onsState.get("orScores").isArray().get(0).isArray();
      int max = Math.min(aantalOpdrachten, orScores.size());
      JSONNumber[] scores = new JSONNumber[max];
      for (int i = 0; i < max; i++) {
          try {
              JSONNumber number = orScores.get(i).isNumber();
              LOG.log(Level.FINE, "score " + i + " = " + number);
              scores[i] = number;
          } catch (Exception e) {
              LOG.log(Level.WARNING, "score " + i, e);
          }
      }
      return scores;
  }
  
  static Map<PersistenceId, DomResultStudentScoPage> getPages(JSONValue launchdata, String suspend_data, String review_data) {
    HashMap<PersistenceId, DomResultStudentScoPage> result = new HashMap<>();
    
    int aantal = getAantalOpdrachten(launchdata);
    LOG.info("getPages aantal = " + aantal);
    
    JSONNumber scores[] = getScores(suspend_data, aantal);
    LOG.info("getPages scores = " + Arrays.asList(scores));
    JSONNumber maxScores[] = getMaxScores(launchdata, aantal);
    LOG.info("getPages maxscores = " + Arrays.asList(maxScores));
    JSONNumber correctie[] = getCorrectie(review_data, aantal);
    LOG.info("getPages correctie = " + Arrays.asList(correctie));
    
    for(int i = 0; i < aantal; i++) {
      String label = String.valueOf(i+1);
      DomResultStudentScoPage item = new DomResultStudentScoPage(label);
      if (i < scores.length)
        item.setScore(scores[i].doubleValue());
      item.setMaxScore(maxScores[i].doubleValue());
      if (i < correctie.length)
        item.setCorrectie(correctie[i].doubleValue());
    }
    
    return result;
    
  }

  private static JSONNumber[] getCorrectie(String review_data, int aantal) {
    LOG.info("getCorrectie " + review_data);
    if(review_data == null || !review_data.startsWith("{"))
        return new JSONNumber[0];
    
    JSONObject review = JSONParser.parseLenient(review_data).isObject();
    //JSONArray  data = review.get("xxx").isArray();
    JSONNumber[] result = new JSONNumber[aantal];
    for(int i = 0; i < aantal; i++) {
      result[i] = new JSONNumber(0); // TODO ....
    }
    return result;
  }

  private static JSONNumber[] getMaxScores(JSONValue launchdata, int aantal) {
    JSONNumber[] result = new JSONNumber[aantal];
    for(int i = 0; i < aantal; i++) {
      result[i] = new JSONNumber(10); // TODO ....
    }
    return result;
  }
  
}
