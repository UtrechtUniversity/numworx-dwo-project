package nl.uu.fi.dwo.rest.dom.entities;

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
    DomResultTreeRoot root;
    
}
