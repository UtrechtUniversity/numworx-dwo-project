package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONString;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;

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

    @Override
    public void plot(ResultsPresenter.ResultPlot data, boolean zoomedClass, boolean zoomedCourse) {
        JSONArray rows = new JSONArray();
        JSONArray line = new JSONArray();
        for (int x = 0; x < data.gethIndex().length; x++) {
                line.set(x, new JSONString(data.gethIndex()[x].label));
            }
            rows.set(0, line);
        for (int y = 0; y < data.getvIndex().length; y++) {
            line = new JSONArray();
                line.set(0, new JSONString(data.getMarks().get(y).get(0).label));
            for (int x = 0; x < data.getMarks().get(y).size(); x++) {
                line.set(x, new JSONString(data.getMarks().get(y).get(x).label));
            }
            rows.set(y, line);
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
