package fi.dwo.dwojapplet.domain;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONValue;

import fi.beans.mainframe.JApplet;
import fi.beans.private_base64code.StringCodeObject;
import fi.beans.scorm.PartialScoreIF;
import fi.beans.scorm.SCORM12APIInterface;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class WiskOpdrPartialScore implements PartialScoreIF {
  
  private static final Logger LOG = Logger.getLogger(WiskOpdrPartialScore.class.getName());
  private static final String CMI_SUSPEND_DATA = "cmi.suspend_data";
  private static final String CMI_COMPLETION_STATUS = "cmi.completion_status";
  private static final String CMI_COMMENTS_FROM_LMS_0_COMMENT = "cmi.comments_from_lms.0.comment";
  private static final String LESSON_STATUS_completed = "completed";


  JApplet wiskOpdr;
  Map launchData;
  int aantalOpdrachten;
  String scoreMax[];
  
  public static Hashtable toHashtable(String JSONString) {
    Object object = JSONValue.parse(JSONString);
    Hashtable h = new Hashtable();
    Map map = null;
    if (object instanceof Map)
        h.putAll((Map) object);
    return h;
}

  private Map toSuspendData(String suspendData) {
    Object o;
    if(suspendData != null && suspendData.startsWith("{"))
        o = JSONValue.parse(suspendData);
    else
        o = StringCodeObject.decodeStringToObject(suspendData, wiskOpdr.getClass().getClassLoader());
    return (Map) o;
  }

  private  String[] geefPaginaScores(String suspendData) {
    Map<String,?> h = toSuspendData(suspendData);
    if(h == null) return null;
    ObjectMap m = JSONUtilities.wrapMap(h);
    
    int[] scores = null;
    boolean[] bezocht = null;
    if (h.containsKey("onsState")) {
        ObjectMap onsState =  m.getObjectMap("onsState");
        if (onsState.containsKey("orScores"))
        {
          scores = onsState.getObjectList("orScores").getIntArray(0);
        }
        bezocht = onsState.getObjectList("bezocht").getBooleanArray(0);
    }
    if (scores == null || scores.length == 0 )
        return null;
    String[] log = new String[scores.length];
    for (int i = 0; i < scores.length; i++) {
        if(bezocht == null || bezocht[i])
            log[i] = Integer.toString( scores[i] );
    }
    return log;
}
  
  public static int[] geefPaginaCorrectieScores(String reviewStateString) {
    Map reviewState = toHashtable(reviewStateString);
    if(reviewState == null) return null;
    ObjectMap m = JSONUtilities.wrapMap(reviewState);
    
    int[] correctieScores = null;
    if (reviewState.containsKey("opdrContStates")) {
        ObjectList opdrContReviewStates = m.getObjectList("opdrContStates").getObjectList(0);
        correctieScores = new int[opdrContReviewStates.size()];
        for(int i=0 ; i<correctieScores.length ; i++) {
            ObjectMap page = opdrContReviewStates.getObjectMap(i);
            correctieScores[i] = getScoreCorrectiePage(page);
        }
    }
    return correctieScores;
}

  
  public static int getScoreCorrectiePage (ObjectMap state) {
    int scoreCorrectie = 0;
    if(state==null) 
        return 0;
    ObjectList interactiePanelStates = null;
    Iterator<String> en = state.keySet().iterator();
    while (en.hasNext()) {
        String key = (String)en.next();
        if(key.equals("interactiePanelStates")) {
            interactiePanelStates = state.getObjectList("interactiePanelStates");
            for(int i=0 ; i<interactiePanelStates.size() ; i++) {
              ObjectMap objectMap = interactiePanelStates.getObjectMap(i);
                if(objectMap!=null) {
                    Iterator<String> eni = objectMap.keySet().iterator();
                    while (eni.hasNext()) {
                        String keyi = (String)eni.next();
                        if(keyi.equals("reviewInteractieData")) { 
                            ObjectMap reviewInteractieData = objectMap.getObjectMap("reviewInteractieData");
                            if (reviewInteractieData.containsKey("reviewScoreCorrectie")) {
	                            int reviewScoreCorrectie = reviewInteractieData.getInt("reviewScoreCorrectie");
	                            scoreCorrectie += reviewScoreCorrectie;
                            }
                            
                        }
                    }
                    scoreCorrectie += getScoreCorrectiePage(objectMap);
                }
            }
        }
    }
    return scoreCorrectie;
}

  /**
   * Hiermee haalt de DWO de paginaCorrectiescores uit de reviewdata. 
   * wordt ook aangeroepen door getScoreMapList(...)
   */
  public  String[] geefPaginaScores(String suspendData, int[] scoreCorrecties) {
      Map h = toSuspendData(suspendData);
      if(h == null) return null;
      ObjectMap m = JSONUtilities.wrapMap(h);
      int[] scores = null;
      
      boolean[] bezocht = null;
      if (h.containsKey("onsState")) {
          ObjectMap onsState =  m.getObjectMap("onsState");
          if (onsState.containsKey("orScores"))
              scores = onsState.getObjectList("orScores").getIntArray(0);
          bezocht = onsState.getObjectList("bezocht").getBooleanArray(0);
//situatie: bezocht = [[true,true]] en scores = null;          
          if(bezocht != null && scores == null) {
            scores = new int[bezocht.length];
          }
      }
      if (scores == null || scores.length == 0 )
          return null;
      String[] log = new String[scores.length];
      for (int i = 0; i < scores.length; i++) {
          if(bezocht == null || bezocht[i])
              log[i] = Integer.toString( scores[i] + getInt(scoreCorrecties,i) );
      }
      return log;
  }
  
  private static int getInt(int[] array, int i) {
    if(array==null) return 0;
    if(i >= array.length) return 0;
    return array[i];
}

  public WiskOpdrPartialScore(Sco sco) {
    try {
      wiskOpdr = sco.getApplet();
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "<init>", e);
      wiskOpdr = new JApplet() { };
    }
    
    launchData = sco.getLaunchdata();
    aantalOpdrachten = Integer.parseInt(launchData.get("aantalOpdrachten_1").toString());
    scoreMax = new String[aantalOpdrachten];
    for(int i = 1; i <= aantalOpdrachten; i++) {
      Object opdracht = launchData.get("opdracht_1_" + i);
      Map map = (Map) StringCodeObject.decodeStringToObject(opdracht.toString(), wiskOpdr.getClass().getClassLoader());
      scoreMax[i-1] = map.get("scoreMax").toString();
    }
    
  }
  private String[] geefPaginaTijden(String suspendData) {
    Map h = toSuspendData(suspendData);
    if(h == null) return null;
    ObjectMap m = JSONUtilities.wrapMap(h);
    String[] times = null;
    if ( m.containsKey("onsState")) {
        ObjectMap onsState = m.getObjectMap("onsState");
        ObjectList list = onsState.getObjectList("orTimes");
        if (list != null) times = list.getStringArray(0);
    }
    if (times == null || times.length == 0 )
        return null;
    return times;
}

  
  @Override
  public List getScoreMapList(SCORM12APIInterface api) {
    String suspendData = api.LMSGetValue(CMI_SUSPEND_DATA);
    String completed = api.LMSGetValue(CMI_COMPLETION_STATUS);
    String reviewStateString = 
            LESSON_STATUS_completed .equals(completed)
            ? api.LMSGetValue(CMI_COMMENTS_FROM_LMS_0_COMMENT)
            : "";
    int[] correcties = geefPaginaCorrectieScores(reviewStateString);
    String[] scoresRaw = geefPaginaScores(suspendData, correcties);
    String[] sessions = geefPaginaTijden(suspendData);

    List<Map<String,String>> result = new ArrayList<>(aantalOpdrachten);
    for(int i = 0; i < aantalOpdrachten; i++) {
      HashMap<String,String> map = new HashMap<>();
      map.put(LOCATION, String.valueOf(i));
      map.put(SCORE_MAX, scoreMax[i]);
      if (scoresRaw != null && i < scoresRaw.length) map.put(SCORE_RAW, scoresRaw[i]);
      map.put(DESCRIPTION, String.valueOf(i+1));
      if (sessions != null && i < sessions.length) map.put(SESSION_TIME, sessions[i]);

      if (correcties != null && correcties.length > i )
        map.put("isCorrected", String.valueOf( correcties[i] != 0 ));
      else
        map.put("isCorrected", "");
      result.add(map);
    }
    
    return result;
  }

  @Override
  public Map getScoreObjectivesMap(SCORM12APIInterface api) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Component getContentPage() {    
    return wiskOpdr;
  }

}
