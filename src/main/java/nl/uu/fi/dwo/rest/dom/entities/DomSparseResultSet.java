package nl.uu.fi.dwo.rest.dom.entities;

/**
 * It returns three sets of tuples. The first set of tuples
 * is the tuple for <SchoolClass, Student>, the second set is the set of Tuples 
 * for <Course with a treeIndex> and the third set is a <Course, Sco, StudentScoScoResult>
 * @author Gert van der Plas
 */
public class DomSparseResultSet {
   DomResultProducer producer;
   DomResultActivity activity;
   DomResultScore score;           
}
