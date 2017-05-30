package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GWT Panel that handles switching the role.
 *
 * @author G.A.J. van der Plas
 */
public class SwitchSchoolView extends Composite implements ClickHandler, SwitchSchoolPresenter.Display {

    private static final Logger LOG = Logger.getLogger(SwitchSchoolView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, SwitchSchoolView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private SwitchSchoolPresenter switchSchoolPresenter;

    @UiField
    FlexTable flexTable;
    @UiField
    Button cancelBtn;
    @UiField
    Button switchBtn;
    int schoolIndex = 1;

    public class ResultData {

        int width;
        int height;
        String[][] data; //height, width
    }

    public SwitchSchoolView(SwitchSchoolPresenter sp) {
        switchSchoolPresenter = sp;
        switchSchoolPresenter.setView(this);
        initWidget(uiBinder.createAndBindUi(this));
        //controller must be before clicks occur
        switchBtn.addClickHandler(this);
    }

    @Override
    public void init() {
        //create table
        String nulLabel = "School";
        HTML l = new HTML("<div style=\"text-align: left; background-color: #555555; padding: 2px; overflow auto;\">" + nulLabel + "</div>");

        flexTable.setWidget(0, 0, l);
        cancelBtn.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        switchBtn.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onClick(ClickEvent event) {
        //            switchSchoolPresenter.selectRow(schoolIndex);

        if (event.getSource() == switchBtn) {
            switchSchoolPresenter.switchSchool();
        }
    }

    public void updateView(int height, int width, String[][] data) {
        flexTable.removeAllRows();
        int i = height;
        int j = width;
        // column labels
        HTML html = new HTML(data[0][0]);
        html.setStyleName("flexTableHeader");
        flexTable.setWidget(0, 0, html);

        for (i = 0; i < width; i++) {
            html = new HTML(data[0][i]);
            html.setStyleName("flexTableHeader");
            flexTable.setWidget(0, i, html);
        }

        // row labels
        for (i = 0; i < height; i++) {
            html = new HTML(data[i][0]);
            html.setStyleName("flexTableHeader");
            flexTable.setWidget(i, 0, html);
        }

        for (j = 0; j < width; j++) {
            for (i = 1; i < height; i++) {
                html = new HTML(data[i][j]);
                html.setStyleName("flexTableCell");
                flexTable.setWidget(i, j, html);
            }
        }

        flexTable.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (flexTable.getCellForEvent(event) != null) {
                    int curSchoolIndex = schoolIndex;
                    schoolIndex = flexTable.getCellForEvent(event).getRowIndex() - 1;
                    switchSchoolPresenter.selectRow(schoolIndex);
                    if ((schoolIndex + 1) % 2 == 0) {
                        flexTable.getCellFormatter().removeStyleName(schoolIndex + 1, 0, "flexTableOddRow");
                    } else {
                        flexTable.getCellFormatter().removeStyleName(schoolIndex + 1, 0, "flexTableEvenRow");
                    }
                    flexTable.getRowFormatter().getElement(schoolIndex + 1).addClassName("flexTableSelectedBackground");
                    if (curSchoolIndex != schoolIndex) {
                        flexTable.getRowFormatter().getElement(curSchoolIndex + 1).removeClassName("flexTableSelectedBackground");
                        if ((schoolIndex + 1) % 2 == 0) {
                            flexTable.getCellFormatter().addStyleName(schoolIndex + 1, 0, "flexTableOddRow");
                        } else {
                            flexTable.getCellFormatter().addStyleName(schoolIndex + 1, 0, "flexTableEvenRow");
                        }
                    }
                    LOG.log(Level.INFO, "" + schoolIndex);
                }
            }
        });

        flexTable.setVisible(true);
    }

}
