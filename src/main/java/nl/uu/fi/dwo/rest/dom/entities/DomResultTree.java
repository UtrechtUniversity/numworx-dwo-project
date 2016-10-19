package nl.uu.fi.dwo.rest.dom.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Client side class, not meant to be transported.
 * 
 * The information in the DomResultsPerTeacher class is inserted client-side 
 * into this simplified kd-range tree. The kd-tree has a search range of 1 and 
 * has a node type from root to leave a sequence of: DomTeacher, DomSchoolClass, 
 * DomClassCourse referred DomCourse,DomCourse, ..., DomCourse. 
 * A leave of the kd-tree is by definition a course-leave.
 * 
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public class DomResultTree {
    DomResultTeacher root;

    public DomResultTree(DomResultsPerTeacher resultData){
        buildResultTree(resultData);
        calculateResults();
    }
    
    private void buildResultTree(DomResultsPerTeacher resultData){
            //build tree from results collection
            root = new DomResultTeacher(resultData.getTeacher());
            List<DomResultSchoolClass> schoolClasses = new ArrayList<DomResultSchoolClass>(resultData.getSchoolClasses().size());
            root.setChildren(schoolClasses);
            //insert courses that are linked through classcourses
            //recursively build the courses.
            //add sco's
            //add studentScoResults
    }
    
    public final void calculateResults(){
        //
    }
}
