package nl.uu.fi.dwo.account.client.boot.Results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.boot.BootPanel;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;

/**
 *
 *
 * @author G.A.J. van der Plas 
 */
public class ResultPanel extends Composite {

    private static final Logger LOG = Logger.getLogger(ResultPanel.class.getName());

    interface MyUiBinder extends UiBinder<Widget, ResultPanel> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private ResultsTeacherController control;

    //initial gridsize
    final int xInitialGridSize = 6;
    final int yInitialGridSize = 6;

    @UiField
    HorizontalPanel tablePanel;
    @UiField(provided = true)
    Grid resultGrid = new Grid(xInitialGridSize, yInitialGridSize);
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

    public ResultPanel() {
        LOG.log(Level.INFO, "Grid size:" + resultGrid.getRowCount() + "x" + resultGrid.getColumnCount() + ".");
        init();
//        tablePanel.setWidget(resultGrid);
        initWidget(uiBinder.createAndBindUi(this));
        control = new ResultsTeacherController(this);
        addRow();
    }

    public void init() {
        int rows = resultGrid.getRowCount();
        int cols = resultGrid.getColumnCount();

        //Set row headers
        for (int i = 0; i < rows; i++) {
            resultGrid.setWidget(i, 0, new Label("rowheader " + i));
        }

        //Set column headers
        for (int i = 0; i < cols; i++) {
            resultGrid.setWidget(0, i, new Label("colheader " + i));
        }

        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                resultGrid.setWidget(i, j, new Label("data " + i + "x" + j));
            }
        }

    }

    public void addRow() {
        int rows = resultGrid.getRowCount();
        int cols = resultGrid.getColumnCount();
        resultGrid.insertRow(rows);
        //Set row headers
        resultGrid.setWidget(rows, 0, new Label("rowheader " + rows));

        for (int j = 1; j < cols; j++) {
            resultGrid.setWidget(rows, j, new Label("data " + rows + "x" + j));
        }
    }

}
