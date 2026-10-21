package nl.uu.fi.dwo.rest.dom.entities.util;

import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;

public class KijkNaVisitor extends DomResultScoreVisitor {
	
	
	public static boolean isSeen(String kijkna, DomResultScore<?>rs) {
		KijkNaVisitor v = new KijkNaVisitor(kijkna);
		rs.visit(v);
		return v.seen;
	}
	
	private boolean seen;
	private final String kijkna;

	
	public KijkNaVisitor(String kijkna) {
		this.kijkna = kijkna;
	}


	@Override
	public void visit(DomResultScore<?> rs) {
		seen = seen || kijkna.equals(rs.getTitle());
		if (!seen && rs.getChildren() != null)
		for ( DomResultScore<?> child: rs.getChildren().values())
		{
			child.visit(this);
			if (seen) break;
		}
	}
	@Override
	public void visitCourseInClass(DomResultCourseInClass domResultCourseInClass) {
		// TODO Auto-generated method stub
		visit(domResultCourseInClass);
	}
	@Override
	public void visitScoContext(DomResultScoContext domResultScoContext) {
		visit(domResultScoContext);
	}
	@Override
	public void visitStudentScoContext(DomResultStudentScoContext domResultStudentScoContext) {
		visit(domResultStudentScoContext);
	}
	
}
