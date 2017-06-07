package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shows the students results of activities individually or grouped by schoolclass
 * or leave course.
 *
 * @author G.A.J. van der Plas
 */
public class ResultsView extends Composite implements ResultsPresenter.Display{

    private static final Logger LOG = Logger.getLogger(ResultsView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, ResultsView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public interface Style extends CssResource {

        String panel();
        String tableCelleven();
        String tableCellodd();
    }
    
    private ResultsPresenter resultsPresenter;

    //initial gridsize
    final int yInitialGridSize = 12;
    final int xInitialGridSize = 6;
    int xOffset = 0;
    int yOffset = 0;
    int timer = 0;

    @UiField(provided = true)
    FlexTable resultTable = new FlexTable();

    public class ResultData {
        int width;
        int height;
        String[][] data; //height, width
    }

    public ResultsView(ResultsPresenter rp) {
        resultsPresenter = rp;
        rp.setView(this);
        LOG.log(Level.INFO, "Grid size:" + resultTable.getRowCount() + "x.");
        resultTable.setWidget(0, 0, new Label("class\\course"));
        resultTable.getCellFormatter().addStyleName(0, 0, "flexTableHeader");
        Timer t = new Timer() {
            @Override
            public void run() {
                timer += 1;
//                try {
//                    if (parent.getAutoUpdateResults().getValue()) {
//                        //handler.updateServerResults();
//                        parent.setStatus("Server OK, updating results every 5 seconds.");
//                    } else {
//                        parent.setStatus("Refresh paused.");
//                    }
//                } catch (Exception e) {
//                    parent.setStatus("Server Offline");
//                }
            }
        };
        t.scheduleRepeating(5000);

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void init() {
        resultsPresenter.plotResultsEvent();
    }

    public void plot(int height, int width, String[][] data) {
        resultTable.removeAllRows();
        int i = height;
        int j = width;
                resultTable.getCellFormatter().addStyleName(i, j, "flexTableHeader");
        // column labels
        HTML html = new HTML(data[0][0]);
        html.setStyleName("flexTableHeader");
        resultTable.setWidget(0, 0, html);

        for (i = 0; i < width; i++) {
            html = new HTML(data[0][i]);
            html.setStyleName("flexTableHeader");
            resultTable.setWidget(0, i, html);
        }

        // row labels
        for (i = 0; i < height; i++) {
            html = new HTML(data[i][0]);
            html.setStyleName("flexTableHeader");
            resultTable.setWidget(i, 0, html);
        }

        for (j = 1; j < width; j++) {
            for (i = 1; i < height; i++) {
            html = new HTML(data[i][j]);
            html.setStyleName("flexTableCell");
                resultTable.setWidget(i, j, html);
            }
        }

        // add clickhandler to labels
        resultTable.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (resultTable.getCellForEvent(event) != null) {
                    int col = resultTable.getCellForEvent(event).getCellIndex();
                    int row = resultTable.getCellForEvent(event).getRowIndex();
                    LOG.log(Level.INFO, "Clicked on datafield (row,col) =  (" + row + "," + col + ").");
                    resultsPresenter.selectRowAndCol(row, col); 
                    resultsPresenter.plotResultsEvent();
                }
            }
        });
        resultTable.setVisible(true);
    }

}
