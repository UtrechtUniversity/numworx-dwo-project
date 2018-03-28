package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.LoggingProvider;
import nl.uu.fi.dwo.mobile.utils.NoLogging;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class StudentModelLogger implements Logging {

	
	static final String SUCCESS_SCORE = "logSuccessScore";
	
	public static class Provider extends LoggingProvider {
		
		@Override
		public Logging get() {
			Memento instance = Memento.instance();
			boolean experiment = instance != null && instance.pmodel != null;
			experiment &= DWOplayer.clientfactory.withUser();
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
		if(logID != null && objectives != null)
		{
			StudentModelLogger old = all.put(logID, this);
			if(old != null) {
				attempts += old.attempts;
				score += old.score;
		}}
	}

	@Override
	public void setClassName(String string) {
	}

	@Override
	public String getLogID() {
		return logID;
	}

	@Override
	public void setLogObjectives(boolean[][] objectives) {
		this.objectives = objectives;
		addToSet();
	}

	public void accumulateScore(DomStudentModelStructureScore studentModel) {
		for ( int i = 0; i < objectives.length; i++) {
			for ( int j = 0; j < objectives[i].length; j++) {
				if (objectives[i][j]) {
					DomStudentModelScore<?> s = studentModel.getCategories().get(i).getObjectives().get(j);
// more strategie
					if(attempts > 0) {
						s.setCount(s.getCount()+1);
						s.setScore(s.getScore()+score/attempts);
					}
				}
			}
		}
	}
	
	public static void accumulateAllScores(DomStudentModelStructureScore studentModel) {
		for(StudentModelLogger l: all.values()) {
			l.accumulateScore(studentModel);
		}
	}

	public static void destroy() {
		all.clear();
	}
}
