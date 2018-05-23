package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONString;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.old.ResultsPresenter;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsResultsView implements ResultsPresenter.Display {

    @Override
    public void clear() {
        JsResultsDisplay.clear();
    }

    /** plots ResultPlot data via jsResultsDisplay. Data contains a horizontal index
     * including the column label for the row headers. Result data rows contains 
     * in the first element the row labels and row score. Vertical  index does
     * does not contain the column label for the row headers.
     * 
     * @param data
     * @param zoomedClass
     * @param zoomedCourse 
     */
    @Override
    public void plot(ResultsPresenter.ResultPlot data, boolean zoomedClass, boolean zoomedCourse) {
        JSONArray rows = new JSONArray();
        JSONArray line = new JSONArray();
        //set headers
        for (int x = 0; x < data.gethIndex().length; x++) {
                line.set(x, new JSONString(data.gethIndex()[x].label));
            }
        rows.set(0, line);
        //set rows
        for (int y = 0; y < data.getvIndex().length; y++) {
        //set row header appended by row scores
            line = new JSONArray();
                line.set(0, new JSONString(data.getMarks().get(y).get(0).label));
            for (int x = 1; x < data.getMarks().get(y).size(); x++) {
                line.set(x, new JSONString(""+data.getMarks().get(y).get(x).score));
            }
            rows.set(y+1, line);
        }
        JsResultsDisplay.plot(rows.getJavaScriptObject(), zoomedClass, zoomedCourse);     
    }
    

    @Override
        public void setEmptyTableMessage() {
        JsResultsDisplay.setEmptyTableMessage();
    }

    @Override
        public void setLoadingTableMessage() {
        JsResultsDisplay.setLoadingTableMessage();
    }
}
