package nl.uu.fi.dwo.rest.dom.entities.util;

import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;

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
		if (DomResultScore.isVisibleForTeachers(course.getViewState())) {
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

	@Override
	public void visitStudentScoContext(DomResultStudentScoContext ss) {
		String completionStatus = ss.getStudentSco().getCompletionStatus();
        ss.setScore(ss.getStudentSco().getScore());
        ss.setScoCount(0);

        if ("not attempted".equals(completionStatus)) {
			ss.setTitle("");
			ss.setStudentScoCount(0);
			ss.setFraction(0.0);
			ss.setTotalTime("0s");
			ss.setDescription("");
			return;
		}
        ss.setTotalTime(ss.getStudentSco().getTotalTime());
        ss.setTitle(ss.getScore() + " in " + buildTime(ss.getTotalTime()));
        ss.setFraction(1.0);
        ss.setDescription(ss.getScore() + "% in " + ss.getTotalTime());
        ss.setStudentScoCount(1);
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
	
	
}
