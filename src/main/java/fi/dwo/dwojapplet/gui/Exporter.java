package fi.dwo.dwojapplet.gui;

import fi.beans.scorm.PartialScoreIF;
import fi.dwo.dwojapplet.domain.ResultScore;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolGroup;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.domain.UserResultList;
import java.util.List;
import java.util.Map;

abstract class Exporter {

    static abstract class ExportBuffer {

        protected abstract void export();

        protected abstract void export(String[] line);

        protected void exportHeader(String[] line) {
            export(line);
        }
    }

    protected abstract ExportBuffer createExportBuffer();

    public void export(School[] schools) {
        ExportBuffer sb = createExportBuffer();
        if (schools == null) {
            sb.export();
            return;
        }
        String[] line = {"School", "Login", "Leerling", "Docent", "SchoolAdmin"};
        sb.exportHeader(line);
        for (School school : schools) {
            line[0] = school.getName();
            line[1] = school.getSchoolLogin();
            line[2] = school.getPasswd(SchoolGroup.STUDENT);
            line[3] = school.getPasswd(SchoolGroup.TEACHER);
            line[4] = school.getPasswd(SchoolGroup.SCHOOLADMIN);
            sb.export(line);
        }
        sb.export();
    }

    public void export(UserResultList[] userResults) {
        ExportBuffer sb = createExportBuffer();
        ResultScore[] resultScore = userResults[0].getResultScore();
        String[] line = new String[resultScore.length + 2];
        line[0] = "";
        line[1] = "";
        if (resultScore.length > 0) {
            line[1] = resultScore[0].getUserGroup().getTitle();
        }
        for (int i = 0; i < resultScore.length; i++) {
            ResultScore score = resultScore[i];
            line[i + 2] = score.getLessonGroup().getName();
        }
        sb.exportHeader(line);
        for (int i = 0; i < userResults.length; i++) {
            UserResultList results = userResults[i];
            resultScore = results.getResultScore();
            line[1] = resultScore[0].getUserGroup().getName();
            line[0] = resultScore[0].getUserGroup().getUsername();
            for (int j = 0; j < resultScore.length; j++) {
                ResultScore score = resultScore[j];
                if (score.getScore() == 0) {
                    line[j + 2] = "";
                } else {
                    line[j + 2] = Integer.toString(Math.round(Math.max(0, score.getScore())));
                }
            }
            sb.export(line);
        }
        sb.export();
    }

    public void export(ScoDialog.ClassModel model) {
        ExportBuffer sb = createExportBuffer();
        int len = model.getSize();
        for (int i = 0; i < len; i++) {
            User u = model.getUser(i);
            List l = model.getScoreList(i);
            String[] line = new String[l.size() + 2];
            if (i == 0) {
                line[0] = "";
                line[1] = "max";
                for (int j = 0; j < l.size(); j++) {
                    line[j + 2] = ((Map) l.get(j)).get(PartialScoreIF.SCORE_MAX).toString();
                }
                sb.export(line);
            }
            line[0] = u.getUsername();
            line[1] = u.getName();
            for (int j = 0; j < l.size(); j++) {
                line[j + 2] = ((Map) l.get(j)).get(PartialScoreIF.SCORE_RAW).toString();
            }
            sb.export(line);
        }
        sb.export();
    }

}
