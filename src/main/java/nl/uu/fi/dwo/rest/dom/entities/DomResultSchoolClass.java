package nl.uu.fi.dwo.rest.dom.entities;

/**
 *
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public class DomResultSchoolClass extends DomResultScore{
    private DomSchoolClass schoolClass;

    void DomResultSchoolClass(DomSchoolClass aSchoolClass){
        schoolClass = aSchoolClass;
        super.setLabel(schoolClass.getSchoolClassName());
    }
    /**
     * @return the schoolClass
     */
    public DomSchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * @param schoolClass the schoolClass to set
     */
    public void setSchoolClass(DomSchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }
    
}
