package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Map;
import java.util.TreeMap;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.LoggingProvider;
import nl.uu.fi.dwo.mobile.utils.NoLogging;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class StudentModelLogger implements Logging {

	public static class Provider extends LoggingProvider {
		
		@Override
		public Logging get() {
			boolean experiment = true;
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

	@Override
	public void log(Map<String, ?> parameters) {
// strategy:
		double success;
		if(Boolean.TRUE.equals(parameters.get(SUCCESS))) success = 1;
		else if(Boolean.FALSE.equals(parameters.get(SUCCESS))) success = 0;
		else success = 0.5;
		this.score += success;
		this.attempts += 1;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
	}

	@Override
	public void setLogID(String string) {
		logID = string;
		addToSet();
	}

	private void addToSet() {
		if(logID != null && objectives != null)
		{
			StudentModelLogger old = all.put(logID, this);
			attempts += old.attempts;
			score += old.score;
		}
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
					s.setCount(s.getCount()+attempts);
					s.setScore(s.getScore()+score);
				}
			}
		}
		attempts = 0;
		score = 0;
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
