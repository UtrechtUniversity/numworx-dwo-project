// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\UserResultList.java

package fi.dwo.client.domain;

/**
 * This class represents a List of UserResults.
 * @author M.J.B. Kupers
 *  
 */
public class UserResultList {
    private ResultsModuleIF resultsModule;

    private ResultScore resultScore[];

    /**
     *  Creates a new UserResultLists.
     */
    public UserResultList() {

    }

    /**
     * Shows the result of the specified ResultScore.
     * @param rs The resultscore to show the result of.
     *  
     */
    public void showResult(ResultScore rs) {
        if (resultsModule != null) {
            resultsModule.showResult(rs);
        }

    }

    /**
     * Returns all the ResultScores in this list.
     * @return All the ResultScores in this list.
     */
    public ResultScore[] getResultScore() {
        return resultScore;
    }

    /**
     * Sets the list of ResultScores.
     * @param resultScore The list to set.
     */
    public void setResultScore(ResultScore[] resultScore) {
        this.resultScore = resultScore;
    }

    /**
     * Returns the ResultsModule where the list is part of.
     * @return The ResultsModule where the list is part of.
     */
    public ResultsModuleIF getResultsModule() {
        return resultsModule;
    }

    /**
     * Sets the ResultsModule where the list is part of.
     * @param resultsModule2 The ResultsModule to set.
     */
    public void setResultsModule(ResultsModuleIF resultsModule2) {
        this.resultsModule = resultsModule2;
    }
}