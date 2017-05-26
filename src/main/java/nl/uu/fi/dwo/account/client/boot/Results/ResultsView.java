package nl.uu.fi.dwo.account.client.boot.Results;

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
 *
 *
 * @author G.A.J. van der Plas
 */
public class ResultsView extends Composite {

    private static final Logger LOG = Logger.getLogger(ResultsView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, ResultsView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public interface Style extends CssResource {

        String panel();

        String tableCelleven();

        String tableCellodd();
    }
    private ResultsPresenter handler;

    //initial gridsize
    final int yInitialGridSize = 12;
    final int xInitialGridSize = 6;
    int xOffset = 0;
    int yOffset = 0;
    int timer = 0;

    @UiField(provided = true)
    FlexTable resultTable = new FlexTable();

    //ListDataProvider<DomResultScore> dataProvider = new ListDataProvider<DomResultScore>();
    public class ResultData {

        int width;
        int height;
        String[][] data; //height, width
    }

    public ResultsView() {
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

//                    resultTable.addClickHandler(new ClickHandler() {
//                @Override
//                public void onClick(ClickEvent event) {
//                    if (resultTable.getCellForEvent(event) != null) {
//                        int col = resultTable.getCellForEvent(event).getCellIndex();
//                        int row = resultTable.getCellForEvent(event).getRowIndex();
//                        LOG.log(Level.INFO, "row " + row + ", col " + col + " clicked.");
//                        if (col > 0) {
//                            DomResultScore score = handler.getResultMatrix().gethIndex(col - 1);
//                            if (row == 0 && col > 0 && score instanceof DomResultCourse) {
//                                LOG.log(Level.INFO, "Column label" + col + " clicked.");
//                                DomResultCourse sc = (DomResultCourse) score;
//                                handler.setCourse(sc);
//                                handler.updateResults();
//                                updateView();
//                            }
//                        }
//                    }
//                }
//                    });
//                resultTable.addClickHandler(new ClickHandler() {
//                    @Override
//                    public void onClick(ClickEvent event) {
//                        if (resultTable.getCellForEvent(event) != null) {
//                            int col = resultTable.getCellForEvent(event).getCellIndex();
//                            int row = resultTable.getCellForEvent(event).getRowIndex();
//                            LOG.log(Level.INFO, "row " + row + ", col " + col + " clicked.");
//                            if (row > 0) {
//                                DomResultScore score = handler.getResultMatrix().getvIndex(row - 1);
//                                if (col == 0 && row > 0 && score instanceof DomResultSchoolClass) {
//                                    LOG.log(Level.INFO, "Row label" + row + " clicked.");
//                                    DomResultSchoolClass sc = (DomResultSchoolClass) score;
//                                    handler.setSchoolClass(sc);
//                                    handler.updateResults();
//                                    updateView();
//                                }
//                            }
//                        }
//                    }
//                });
//        tablePanel.setWidget(resultGrid);
        initWidget(uiBinder.createAndBindUi(this));
        handler = new ResultsPresenter(this);
    }

    public void init() {
        handler.init();
        //handler.plotResultsEvent();
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
                    handler.selectRowAndCol(row, col);
                    handler.plotResultsEvent();
                }
            }
        });
        resultTable.setVisible(true);
    }

}
