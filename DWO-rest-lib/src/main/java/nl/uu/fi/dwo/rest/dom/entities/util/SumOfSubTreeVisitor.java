package nl.uu.fi.dwo.rest.dom.entities.util;

import java.util.Objects;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoPage;
import nl.uu.fi.dwo.rest.locale.Dwo2LocaleMessages;

public class SumOfSubTreeVisitor extends DomResultScoreVisitor {

	// GWT and/or Java @Inject
	public SumOfSubTreeVisitor(Dwo2LocaleMessages rb) {
		NAKIJKEN_NODIG = rb.NUM_TBL_KIJKNA_NEEDED();
		KIJK_NA = rb.NUM_TBL_KIJKNA();
	}

	// GWT only
	public SumOfSubTreeVisitor() {
		this(GWT.create(Dwo2LocaleMessages.class));
	}

	private final String NAKIJKEN_NODIG;
	private final String KIJK_NA;


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
    	boolean nakijken = false;
        if (!ss.getChildren().isEmpty()) {
        	double size = ss.getChildren().size();
        	int bezocht = 0;
        	for(DomResultStudentScoPage page: ss.getChildren().values()) {
        		if (page.getMaxScore() != null) bezocht ++;
        		if (page.getScore().doubleValue() == -1) nakijken = true;
        	}
        	ss.setFraction(bezocht / size);
        }
         
        if (ss.getScoType() == ScoType.INFO) {
        	ss.setTitle("ℹ");
        	ss.setDescription("ℹ in " + buildTime(tt));
        	ss.setMaxScore(0.0);
//        	ss.setScore(-3.0);
        } else if (nakijken) {
        	ss.setTitle(KIJK_NA);
        	ss.setDescription(NAKIJKEN_NODIG);
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
		if (page.getTitle() != null) return; // deze is niet idempotent. en wordt tig keer aangeroepen.
		if (page.getMaxScore() == null|| Double.valueOf(0.0).equals(page.getFraction())) {
			page.setTitle("&nbsp;");
			page.setDescription("&nbsp;");
			if (page.getMaxScore() != null && page.getMaxFactor() != null) {
				page.setMaxScore(page.getMaxScore() * page.getMaxFactor()); // display *maxfactor
			}
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
			title = KIJK_NA;
			description = NAKIJKEN_NODIG; // iets anders?
		} else 
		{
			String score = page.getScore().toString();
			if ("0".equals(score) && bonus.startsWith("+")) score = "";
			description = title = score + bonus + "/" + page.getMaxScore() ;
// FIXME maxFactor perikelen: i18n en  format score + correctie / maxscore
// FIXME als maxFactor = 0, dan "&nbsp;"
			Float maxFactor = page.getMaxFactor();
			if (maxFactor != null) {
				description = "(" + description + ")×" + maxFactor; // TODO i18n 
				float correctie = page.getCorrectie() == null ? 0.0f : page.getCorrectie().floatValue();
				//title = format(( page.getScore().floatValue() + correctie ) * maxFactor) + "/" + format((page.getMaxScore().floatValue() * maxFactor)) ;
// FIXME: niet idempotent
				page.setScore(page.getScore() * maxFactor);
				page.setMaxScore(page.getMaxScore() * maxFactor);
				bonus = "";
				if (correctie != 0.0f)
				{
					page.setCorrectie(Double.valueOf(correctie * maxFactor));
					bonus = format(page.getCorrectie().floatValue());
					if (!bonus.startsWith("-")) bonus = "+" + bonus;
				}
// Redo title:
				score = format(page.getScore().floatValue());
				if ("0".equals(score) && bonus.startsWith("+")) score = "";
				title = score + bonus + "/" + format(page.getMaxScore().floatValue());
			}
		}
		page.setTitle(title);
		page.setDescription(description);
		page.setFraction(1.0);
	}
	
	// een decimaal
	protected String format(float value) {
		return Float.toString(Math.round(value * 10.0f) / 10.0f);
	}
	
}
