package fi.dwo.client.gui;

import fi.dwo.client.domain.ResultScore;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolGroup;
import fi.dwo.client.domain.UserResultList;

abstract class Exporter {
	static abstract class ExportBuffer {
		protected abstract void export();
		protected abstract void export(String[] line);
		protected void exportHeader(String[] line)
		{
			export(line);
		}
	}
	
	protected abstract ExportBuffer createExportBuffer();

	public void export(School[] schools) {
		ExportBuffer sb = createExportBuffer();
    	if(schools == null)
    	{	sb.export();
    		return;
    	}
		String[] line = { "School", "Login", "Leerling", "Docent", "SchoolAdmin" };
		sb.exportHeader( line);
		for (int i = 0; i < schools.length; i++) {
			School school = schools[i];
			line[0] = school.getName();
			line[1] = school.getSchoolLogin();
			line[2] = school.getPasswd(SchoolGroup.STUDENT);
			line[3] = school.getPasswd(SchoolGroup.TEACHER);
			line[4] = school.getPasswd(SchoolGroup.SCHOOLADMIN);
			sb.export(line);
		}
		sb.export();
	}
    
    public void export(UserResultList[] userResults)
    {
    	ExportBuffer sb = createExportBuffer();
    	ResultScore[] resultScore = userResults[0].getResultScore();
    	String[] line = new String[resultScore.length+2];
    	line[0] = "";
    	line[1] = "";
    	if(resultScore.length>0)
    	{
    		line[1] = resultScore[0].getUserGroup().getTitle();
    	}
    	for (int i = 0; i < resultScore.length; i++) {
			ResultScore score = resultScore[i];
			line[i+2] = score.getLessonGroup().getName();	
		}
    	sb.exportHeader(line);
    	for (int i = 0; i < userResults.length; i++) {
			UserResultList results = userResults[i];
			resultScore = results.getResultScore();
			line[1] = resultScore[1].getUserGroup().getName();
			line[0] = resultScore[0].getUserGroup().getUsername();
			for (int j = 0; j < resultScore.length; j++) {
				ResultScore score = resultScore[j];
				if(score.getScore() == 0)
					line[j+2] = "";
				else
					line[j+2] = Integer.toString(Math.round(Math.max(0,score.getScore())));
			}
			sb.export(line);
		}
    	sb.export();
    }

}
