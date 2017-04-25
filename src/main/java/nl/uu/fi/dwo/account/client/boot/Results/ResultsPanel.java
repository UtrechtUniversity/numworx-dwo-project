package nl.uu.fi.dwo.account.client.boot.Results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.account.client.boot.BootPanel;
import nl.uu.fi.dwo.rest.dom.DomResultPlotMatrix;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;

/**
 *
 *
 * @author G.A.J. van der Plas
 */
public class ResultsPanel extends Composite {

    private static final Logger LOG = Logger.getLogger(ResultsPanel.class.getName());

    interface MyUiBinder extends UiBinder<Widget, ResultsPanel> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public interface Style extends CssResource {

        String panel();

        String tableCelleven();

        String tableCellodd();
    }
//
//    interface MyUiRenderer extends UiRenderer {
//  // ... snip ...
//  Style getPanel();
//  // ... snip ...
//}

    private ResultsPanelHandler handler;
    private ResultsTeacherController control;

    //initial gridsize
    final int yInitialGridSize = 12;
    final int xInitialGridSize = 6;
    int xOffset = 0;
    int yOffset = 0;

    @UiField
    HorizontalPanel tablePanel;
    @UiField(provided = true)
    Grid resultGrid = new Grid(yInitialGridSize, xInitialGridSize);
    ListDataProvider<DomResultScore> dataProvider = new ListDataProvider<DomResultScore>();

    private BootPanel parent;

    public void setParent(BootPanel aParent) {
        parent = aParent;
    }

    /**
     * @return the parent
     */
    public BootPanel getParent() {
        return parent;
    }

//    @UiField(provided = true)
//    SimplePager pager;
    public ResultsPanel() {
        LOG.log(Level.INFO, "Grid size:" + resultGrid.getRowCount() + "x" + resultGrid.getColumnCount() + ".");
        resultGrid.setVisible(false);
        init();
//        tablePanel.setWidget(resultGrid);
        initWidget(uiBinder.createAndBindUi(this));
        handler = new ResultsPanelHandler(this);
        addRow();
        resultGrid.setBorderWidth(1);
        resultGrid.setVisible(true);

    }

    public void init() {
        int rows = resultGrid.getRowCount();
        int cols = resultGrid.getColumnCount();
        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                Label l = new Label("");
                l.setStylePrimaryName(".widget");
//                l.setStyleName(style.panel());
                resultGrid.setWidget(i, j, l);
            }
        }

//        //Set row headers
//        for (int i = 0; i < rows; i++) {
//            resultGrid.setWidget(i, 0, new Label("rowheader " + i));
//        }
//
//        //Set column headers
//        for (int i = 0; i < cols; i++) {
//            //Label l = new Label("colheader " + i);
//            resultGrid.setWidget(0, i, new Label("colheader " + i));
//
//        }
//        for (int i = 1; i < rows; i++) {
//            for (int j = 1; j < cols; j++) {
//                Label l = new Label("data " + i + "x" + j);
//                l.setStylePrimaryName(".widget");
////                l.setStyleName(style.panel());
//                resultGrid.setWidget(i, j, l);
////                if ((j % 2) == 0) {
////                    resultGrid.getCellFormatter().setStyleName(i, j, "tableCell-even");
////                } else {
////                    resultGrid.getCellFormatter().setStyleName(i, j, "tableCell-odd");
////                }
//            }
//        }
        //teacherRole.setText(DwoGlobalVars.instance().getSchoolLogins().getActiveSchoolRoleAndClass().getRole().getRoleName());
    }

    public void addRow() {
        int rows = resultGrid.getRowCount();
        int cols = resultGrid.getColumnCount();
        resultGrid.insertRow(rows);
        //Set row headers
//        resultGrid.setWidget(rows, 0, new Label("rowheader " + rows));
//
//        for (int j = 1; j < cols; j++) {
//            resultGrid.setWidget(rows, j, new Label("data " + rows + "x" + j));
//        }
    }

    public void updateView() {
        handler.init();
    }

    public void plot(DomResultPlotMatrix resultMatrix) {
        int i = 0;
        int j = 0;
        for (i = 0; i < resultGrid.getColumnCount(); i++) {
            if (i == resultMatrix.gethSize()) {
                break;
            }
            resultGrid.setWidget(0, i + 1, new Label(resultMatrix.gethIndex(i).getLabel()));
        }
        for (i = 0; i < resultGrid.getRowCount(); i++) {
            if (i == resultMatrix.getvSize()) {
                break;
            }
            resultGrid.setWidget(i + 1, 0, new Label(resultMatrix.getvIndex(i).getLabel()));
        }

        for (i = 0; i < resultGrid.getColumnCount(); i++) {
            for (j = 0; j < resultGrid.getRowCount(); j++) {
                double score = 0.0;
                if (resultMatrix.getMark(i, j).getScore() != null) {
                    if (resultMatrix.getMark(i, j).getScoCount() > 0.0) {
                        score = resultMatrix.getMark(i, j).getScore() / resultMatrix.getMark(i, j).getScoCount();
                    } else if (resultMatrix.getMark(i, j).getStudentScoCount() > 0.0) {
                        score = resultMatrix.getMark(i, j).getScore();
                    } else {
                        score = 0.0;
                    }
                } else {
                    score = 0.0;
                }
                resultGrid.setWidget(i + 1, j + 1, new Label(Double.toString(score)));
            }

        }

        //if null then no data available.
        //else plot data
    }
}
