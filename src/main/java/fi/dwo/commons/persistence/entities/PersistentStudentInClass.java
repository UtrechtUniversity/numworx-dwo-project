/* Copyrighted 2015. */
package fi.dwo.commons.persistence.entities;

public class PersistentStudentInClass {
    private PersistentStudentOfClass studentOfClass;
    private PersistentUser user;

    /**
     * @return the course
     */
    public PersistentStudentOfClass getStudentOfClass() {
        return studentOfClass;
    }

    /**
     * @param soc the course to set
     */
    public void setStudentOfClass(PersistentStudentOfClass soc) {
        this.studentOfClass = soc;
    }

    /**
     * @return the classCourse
     */
    public PersistentUser getUser() {
        return user;
    }

    /**
     * @param u the classCourse to set
     */
    public void setUser(PersistentUser student) {
        this.user = student;
    }
    
    public static PersistentStudentInClass build(PersistentStudentOfClass soc, PersistentUser student){
        PersistentStudentInClass result = new PersistentStudentInClass();
        result.setUser(student);
        result.setStudentOfClass(soc);
        return result;        
    }
}
