package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.dom.entities.util.DomResultScoreVisitor;

/**
 *
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 */
public class DomResultStudent extends DomResultScore<DomResultCourseInClass> {
    private DomStudent student;

    public DomResultStudent(DomStudent aStudent){
        student = aStudent;
        super.setLabel(student.getDisplayName());
    }
    
    /**
     * A root node has no parent.
     * 
     * @param score
     */
    @Override
    public void setParent(DomResultScore score){
        super.setParent(null);
    }

    /**
     * @return the student
     */
    public DomStudent getStudent() {
        return student;
    }

    /**
     * @param student the student to set
     */
    public void setStudent(DomStudent student) {
        this.student = student;
    }

	@Override
	public void visit(DomResultScoreVisitor v) {
		v.visitStudent(this);
		
	}
        
}
