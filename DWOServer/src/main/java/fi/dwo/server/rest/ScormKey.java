package fi.dwo.server.rest;

import java.util.Map;
import java.util.TreeMap;

enum ScormKey {
	COCD,
	SCORE,
	LOCATION,
	COMPLETION_STATUS,
	COMPLETION_TIMESTAMP,
	TOTAL_TIME,
	TOTAL_TIME2004,
	SCORE_WIDGET,
	SESSION_TIME,
	SESSION_TIME2004,
	STUDENT_MODEL,
	SUSPEND_DATA,
	SUSPEND_DIGEST,
	XML;
	
    static Map<String,ScormKey> keys = new TreeMap<String,ScormKey>();
    static {
    	keys.put("cmi.score.raw", SCORE);
    	keys.put("score", SCORE);
    	keys.put("cmi.completion_status", COMPLETION_STATUS);
    	keys.put("completionStatus", COMPLETION_STATUS);

    	keys.put("cmi.location", LOCATION);
    	keys.put("cmi.core.lesson_location", LOCATION);
    	keys.put("location", LOCATION);
    	keys.put("total_time", TOTAL_TIME);
    	keys.put("cmi.total_time", TOTAL_TIME2004);
    	keys.put("session_time", SESSION_TIME);
    	keys.put("cmi.session_time", SESSION_TIME2004);
    	keys.put("suspendData", SUSPEND_DATA);
    	keys.put("cmi.suspend_data", SUSPEND_DATA);
    	keys.put("cmi.suspend_digest", SUSPEND_DIGEST);
    	keys.put("cocd", XML);
    	keys.put("dme.student_model", STUDENT_MODEL);
    	keys.put("cmi.comments_from_lms.0.timestamp", COMPLETION_TIMESTAMP);
// MORE to go    	
    }
    
    static ScormKey getKey(String key) {
		ScormKey result = keys.get(key);
		if(result ==  null) 
		{
		  if (key.startsWith("dme.scorewidget.")) return SCORE_WIDGET;
		  else return COCD;
		}
		return result; // never null
	}

}