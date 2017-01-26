package nl.uu.fi.dwo.account.client.boot.Results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
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

    public interface Style extends CssResource {
        String panel();
        String tableCelleven();
        String tableCellodd();
    }
    @UiField
    Style style;
//
//    interface MyUiRenderer extends UiRenderer {
//  // ... snip ...
//  Style getPanel();
//  // ... snip ...
//}
    private ResultsTeacherController control;

    //initial gridsize
    final int yInitialGridSize = 6;
    final int xInitialGridSize = 12;

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
            //Label l = new Label("colheader " + i);
            resultGrid.setWidget(0, i, new Label("colheader " + i));

        }

        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                Label l = new Label("data " + i + "x" + j);
                l.setStylePrimaryName(".widget");
//                l.setStyleName(style.panel());
                resultGrid.setWidget(i, j, l);
//                if ((j % 2) == 0) {
//                    resultGrid.getCellFormatter().setStyleName(i, j, "tableCell-even");
//                } else {
//                    resultGrid.getCellFormatter().setStyleName(i, j, "tableCell-odd");
//                }
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
