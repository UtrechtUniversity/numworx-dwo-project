package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.LoggingProvider;
import nl.uu.fi.dwo.mobile.utils.NoLogging;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class StudentModelLogger implements Logging {

	
	static final String SUCCESS_SCORE = "logSuccessScore";
	
	public static class Provider extends LoggingProvider {
		
		@Override
		public Logging get() {
			Memento instance = Memento.instance();
			boolean experiment = instance != null && instance.pmodel != null;
			experiment &= DWOplayer.withUser();
			if (experiment)
				return new StudentModelLogger();
			else
				return NoLogging.instance;
		}
		
	}
	
	private StudentModelLogger() {
	}

	private static final String SUCCESS = "success";
	private String logID;
	private boolean[][] objectives;
	private Collection<String> smobjectives;
	private static Map<String,StudentModelLogger> all = new TreeMap<>();
// strategy, compatible with studentmodel;
	private int attempts;
	private double score;
	private JSONObject map;

	@Override
	public void log(Map<String, ?> parameters) {
// strategy:
		double success;
		if(Boolean.TRUE.equals(parameters.get(SUCCESS))) success = 1;
		else if(Boolean.FALSE.equals(parameters.get(SUCCESS))) success = 0;
		else success = 0.5;
		this.score += success;
		this.attempts += 1;
		map.put(SUCCESS_SCORE, new JSONNumber(score));
		map.put(DWOLogger.LOG_ATTEMPTS_COUNT, new JSONNumber(attempts));
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
	}

	@Override
	public void setLogID(String string) {
		logID = string;
		addToSet();
		map = Memento.instance().getLogState(logID);
		JSONValue n = map.get(DWOLogger.LOG_ATTEMPTS_COUNT);
		if(n != null && n.isNumber() != null)
			attempts = (int) n.isNumber().doubleValue();
		n = map.get(SUCCESS_SCORE);
		if(n != null && n.isNumber() != null) 
			score = n.isNumber().doubleValue();
	}

	private void addToSet() {
//		if(logID != null && objectives != null)
//		{
//			StudentModelLogger old = all.put(logID, this);
//			if(old != null && old != this) {
//				attempts += old.attempts;
//				score += old.score;
//		}}
	}

	@Override
	public void setClassName(String string) {
	  map.put("className", new JSONString(string));
	}

	@Override
	public void setLogObjectives(boolean[][] objectives) {
		this.objectives = objectives;
		addToSet();
	}

//	public void accumulateScore(DomStudentModelStructureScore studentModel) {
//		for ( int i = 0; i < objectives.length; i++) {
//			for ( int j = 0; j < objectives[i].length; j++) {
//				if (objectives[i][j]) {
//					DomStudentModelScore<?> s = studentModel.getCategories().get(i).getObjectives().get(j);
//// more strategie
//					if(attempts > 0) {
//						s.setCount(s.getCount()+1);
//						s.setScore(s.getScore()+score/attempts);
//					}
//				}
//			}
//		}
//	}
	
	public static void accumulateAllScores(DomStudentModelStructureScore studentModel, Memento memento) {
		ObjectMap map = JSONUtilities.wrapMap(memento.getLogState());
		for(String name: map.keySet()) {
			ObjectMap logItem = map.getObjectMap(name);
			if(!logItem.containsKey(DWOLogger.LOG_ATTEMPTS_COUNT)) continue;
			String[] smObjectives = logItem.getStringArray(DWOLogger.SM_OBJECTIVES);
			if (smObjectives != null ) {
			    viaSMObjectives(studentModel, logItem, smObjectives, memento.pmodel.getValue().getModelStructure());
			} else {
              ObjectList objectives = logItem.getObjectList(DWOLogger.LOG_OBJECTIVES);			
              if ( objectives != null ) viaObjectives(studentModel, logItem, objectives);
			}
		}
	}

  private static void viaSMObjectives(DomStudentModelStructureScore studentModel, ObjectMap logItem,
    String[] sm, DomStudentModelStructure modelStructure) {
    int attempts = logItem.getInt(DWOLogger.LOG_ATTEMPTS_COUNT);
    if (attempts <= 0) return;
    double score = logItem.getDouble(SUCCESS_SCORE);
    Collection<String> ids = new TreeSet<>(Arrays.asList(sm));
    List<DomStudentModelCategory> cat = modelStructure.getCategories(); List<DomStudentModelCategoryScore> catScore = studentModel.getCategories();
    int size = cat.size();
    for (int i = 0; i < size; i++ ) {
      List<DomStudentModelObj> obj = cat.get(i).getObjectives(); List<DomStudentModelObjectiveScore> objScore = catScore.get(i).getObjectives();
      viaSMObjectives(obj, objScore, ids, attempts, score);
    }
    
    
}

  private static void viaSMObjectives(List<DomStudentModelObj> obj,
      List<DomStudentModelObjectiveScore> objScore, Collection<String> ids, int attempts,
      double score) {
    int size = obj.size();
    for (int i = 0; i < size; i++) {
      DomStudentModelObj item = obj.get(i); DomStudentModelObjectiveScore s = objScore.get(i);
      String id = item.getInfo().getId();
      if (ids.contains(id)) {
//        s.setCount(s.getCount()+1);
        s.setScore(s.getScore()+score/attempts);
      }
      List<DomStudentModelObj> o = item.getObjectives(); List<DomStudentModelObjectiveScore> os = s.getChildren();
      if (o != null && os != null) {
        viaSMObjectives(o, os, ids, attempts, score);
      }
    }
    
  }

  private static void viaObjectives(DomStudentModelStructureScore studentModel, ObjectMap logItem,
      ObjectList objectives) {
    int attempts = logItem.getInt(DWOLogger.LOG_ATTEMPTS_COUNT);
    if(attempts > 0) {
    	double score = logItem.getDouble(SUCCESS_SCORE);
    	int size = objectives.size();
    	for (int i = 0; i < size; i++ ) {
    		boolean[] objective = objectives.getBooleanArray(i);
    		for( int j = 0; j < objective.length; j++) {
    			if(objective[j]) {
    				DomStudentModelScore<?> s = studentModel.getCategories().get(i).getObjectives().get(j);
//    				s.setCount(s.getCount()+1);
    				s.setScore(s.getScore()+score/attempts);
    			}
    		}
    	}
    }
  }

	public static void destroy() {
		all.clear();
	}

  @Override
  public void setSMObjectives(String[] objectives) {
    smobjectives = 
    		objectives == null ? Collections.emptySet() :    		
    		new TreeSet<>(Arrays.asList(objectives));
  }

  @Override
  public void setMaxScore(int max) {
    // TODO Auto-generated method stub
    
  }
}
