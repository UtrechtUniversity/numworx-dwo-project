package nl.uu.fi.dwo.rest.dom.entities.util;

import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoPage;
import nl.uu.fi.dwo.rest.dom.entities.DomResultTeacher;

public abstract class DomResultScoreVisitor {

	public void visitCourseInClass(DomResultCourseInClass<?> domResultCourseInClass) {
	}

	public void visitStudentScoContext(DomResultStudentScoContext domResultStudentScoContext) {
	}

	public void visitScoContext(DomResultScoContext domResultScoContext) {
	}

	public void visit(DomResultScore<?> rs) {
	}

	public void visitSchoolClass(DomResultSchoolClass<?> domResultSchoolClass) {
		visit(domResultSchoolClass);		
	}

	public void visitStudent(DomResultStudent domResultStudent) {
		visit(domResultStudent);
	}

	public void visitStudentScoPage(DomResultStudentScoPage domResultStudentScoPage) {
		visit(domResultStudentScoPage);
	}

	public void visitTeacher(DomResultTeacher<?> domResultTeacher) {
		visit(domResultTeacher);		
	}

}
