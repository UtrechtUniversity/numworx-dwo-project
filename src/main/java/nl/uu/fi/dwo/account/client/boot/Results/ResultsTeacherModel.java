package nl.uu.fi.dwo.account.client.boot.Results;

import nl.uu.fi.dwo.rest.dom.ResultTree;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;

/**
 *
 * @author Gert van der Plas
 */
public class ResultsTeacherModel {
    
    //cached, allows for updating with diffs
    ResultTree resultTree;
    //horizontal axis course -> sco
    //vertical axis  schoolclass -> student
    // if sco, result load stuff.
    // tuple (teacher -> schoolclass -> course -> student -> sco, result)
    // have a list of sco's with scores per tuple
    // do an inorder treewalk per 
    
    public ResultsTeacherModel(){
        
    }
    
    public void init(DomResultsPerTeacher domResults){
        //Build result tree
        //resultTree.build(domResults);
    }
}
