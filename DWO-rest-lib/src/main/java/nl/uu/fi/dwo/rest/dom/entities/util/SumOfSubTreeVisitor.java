package nl.uu.fi.dwo.rest.dom.entities.util;

import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;

public class SumOfSubTreeVisitor extends DomResultScoreVisitor {

	@Override
	public void visitCourseInClass(DomResultCourseInClass<?> course) {
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
            course.setFraction(course.getStudentScoCount() / course.getScoCount());
			course.setTitle(String.valueOf(course.getScore() / course.getScoCount()));
			course.setDescription("gedaan " + course.getFraction() + ", score " + course.getScore() / course.getStudentScoCount());
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
        ss.setScore(ss.getStudentSco().getScore());
        ss.setTotalTime(ss.getStudentSco().getTotalTime());
        ss.setScoCount(0);
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
