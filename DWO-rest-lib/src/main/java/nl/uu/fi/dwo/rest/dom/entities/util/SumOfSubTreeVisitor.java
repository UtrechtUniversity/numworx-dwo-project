package nl.uu.fi.dwo.rest.dom.entities.util;

import java.util.Objects;

import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoPage;

public class SumOfSubTreeVisitor extends DomResultScoreVisitor {

	private static String buildTime(String totalTime) {
		    if (totalTime == null || totalTime.isEmpty()) return "";
 		    if(totalTime.startsWith("00:00:0"))
		      totalTime = totalTime.substring(7) + "s";
		    else if (totalTime.startsWith("00:00:"))
		      totalTime = totalTime.substring(6) + "s";
		    else if (totalTime.startsWith("00:0"))
		      totalTime = totalTime.substring(4, 5) + "m";
		    else if (totalTime.startsWith("00:"))
		      totalTime = totalTime.substring(3, 5) + "m";
		    else if (totalTime.startsWith("0"))
		      totalTime = totalTime.substring(1,2) + "h";
		    else 
		      totalTime = totalTime.split(":",2)[0] + "h";
		    return totalTime;
		  }

	
	@Override
	public void visitCourseInClass(DomResultCourseInClass course) {
		if (isVisible(course)) {
			course.setScore(0.0);
			course.setScoCount(0.0);
			course.setStudentScoCount(0.0);
            if (!course.getCourse().getWithChildren()) {
                //is course leave
                for (DomResultScore<?> s : course.getChildren().values()) {
                    //recurse
                    s.visit(this);
                    //add score from children and set cnt
                    course.setScore(course.getScore() + s.getScore());
                    course.setStudentScoCount(course.getStudentScoCount() + s.getStudentScoCount());
                }
                course.setScoCount(course.getChildren().size());
            } else {
                //course node, not leave
                for (DomResultScore<?> s : course.getChildren().values()) {
                    //recurse
                    s.visit(this);
                    //add score from children and set cnt
                    course.setScore(course.getScore() + s.getScore());
                    course.setScoCount(course.getScoCount() + s.getScoCount());
                    course.setStudentScoCount(course.getStudentScoCount() + s.getStudentScoCount());
                }
            }
// dit is een strategie: 
            if (course.getScoCount() != 0) {
            	course.setFraction(course.getStudentScoCount() / course.getScoCount());
            	course.setTitle(String.valueOf(Math.round(course.getScore() / course.getScoCount())));
            	if (course.getStudentScoCount() != 0)
            		course.setDescription("gedaan " + Math.round(course.getFraction()*100) + "%, score " + Math.round(course.getScore() / course.getStudentScoCount()) + "%");
            	else {
            		course.setDescription("gedaan 0%");
            		course.setTitle("");
            	}
            } else {
            	course.setFraction(0.0);
            	course.setTitle("");
            	course.setDescription("");
            }
        } else {
			course.setScore(0.0);
			course.setScoCount(0);
			course.setStudentScoCount(0.0);
			course.setFraction(0.0);
			course.setTitle("");
			course.setDescription("");
		}
	}


	protected boolean isVisible(DomResultCourseInClass course) {
		return true; // || DomResultScore.isVisibleForTeachers(course.getViewState());
	}

	@Override
	public void visitStudentScoContext(DomResultStudentScoContext ss) {
		String completionStatus = ss.getStudentSco().getCompletionStatus();
        ss.setScore(ss.getStudentSco().getScore());
        ss.setScoCount(0);
        DomResultScoContext parent = (DomResultScoContext) ss.getParent();
        if ("not attempted".equals(completionStatus)||completionStatus == null) {
			ss.setTitle("");
			ss.setStudentScoCount(0);
			ss.setFraction(0.0);
			ss.setTotalTime("00:00:00");
			ss.setDescription("");
			return;
		}
        String tt = Objects.toString(ss.getStudentSco().getTotalTime(), "00:00:00");
		ss.setTotalTime(tt);
        ss.setFraction(1.0);
        if (!ss.getChildren().isEmpty()) {
        	double size = ss.getChildren().size();
        	int bezocht = 0;
        	for(DomResultStudentScoPage page: ss.getChildren().values()) {
        		if (page.getMaxScore() != null) bezocht ++;
        	}
        	ss.setFraction(bezocht / size);
        }
        
        
        
        
        if (ss.getScoType() == ScoType.INFO) {
        	ss.setTitle("ℹ");
        	ss.setDescription("ℹ in " + buildTime(tt));
        	ss.setMaxScore(0.0);
//        	ss.setScore(-3.0);
        } else {
        	ss.setTitle(ss.getScore() + " in " + buildTime(tt));
        	ss.setDescription(ss.getScore() + "% in " + tt + ", gedaan " + Math.round(ss.getFraction()*100) + "%");
        } 
        ss.setStudentScoCount(1);
        
        ss.getChildren().values().forEach(this::visitStudentScoPage);
        
	}

	@Override
	public void visitScoContext(DomResultScoContext sc) {
        sc.setScore(0.0);
        sc.setScoCount(1);
        sc.setStudentScoCount(0.0);
        for (DomResultScore<?> s : sc.getChildren().values()) {
            s.visit(this);
            sc.setScore(sc.getScore() + s.getScore());
            sc.setStudentScoCount(sc.getStudentScoCount() + s.getStudentScoCount());
        }
	}

	@Override
	public void visit(DomResultScore<?> rs) {
        //for DomResultSchoolClasses and Teachers and higher stuff
        rs.setScore(0.0);
        rs.setScoCount(0.0);
        rs.setStudentScoCount(0.0);
        for (DomResultScore<?> s : rs.getChildren().values()) {             
            s.visit(this);
            //add score from children and set cnt
            rs.setScore(rs.getScore() + s.getScore());
            rs.setScoCount(rs.getScoCount() + s.getScoCount());
            rs.setStudentScoCount(rs.getStudentScoCount() + s.getStudentScoCount());           
        }

	}


	@Override
	public void visitStudentScoPage(DomResultStudentScoPage page) {
		if (page.getMaxScore() == null) {
			page.setTitle("&nbsp;");
			page.setDescription("&nbsp;");
			page.setFraction(0.0);
			return;
		}
		String bonus = "";
		if (page.getCorrectie() != null) {
			if (page.getCorrectie().doubleValue() > 0 ) bonus = "+" + page.getCorrectie();
			else if (page.getCorrectie().doubleValue() < 0) bonus = page.getCorrectie().toString();
		}
		String title, description;
		if (page.getMaxScore().doubleValue() == 0.0) {
			title = "ℹ";
			description = "informatief";
		} else if (page.getScore().doubleValue() == -1) {
			title = "Kijk na";
			description = "Nakijken nodig"; // iets anders?
		} else 
			description = title = page.getScore().toString()+ bonus + "/" + page.getMaxScore() ;
		page.setTitle(title);
		page.setDescription(description);
		page.setFraction(1.0);
	}
	
	
	
}
