package nl.uu.fi.dwo.rest.dom;

import java.util.Iterator;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;

/**
 * Calculates the score value for each node in the ResultTree. This simplifies 
 * the calculation of the view matrices.
 * 
 * 
 * @author Gert van der Plas
 */
public class ResultTreeCalculator {
    
    public static void UpdateResultTree(ResultTree tree){
        DomResultScore node =tree.getRoot();
        updateNode(node);
    }

    public static void updateNode(DomResultScore resultScore){
        double score = 0;
        Iterator<DomResultScore> iterator = resultScore.getChildren().values().iterator();
        while(iterator.hasNext()){
            DomResultScore node = iterator.next();
            updateNode(node);
            score +=node.getScore();
        }
        resultScore.setScore(score);
    }
    //TODO Create view matrices
    // SchoolClass/Course
    // SchoolClass - Student/Course
    // SchoolClass/Activity
    // SchoolClass - Student/Activity
    
    
}
