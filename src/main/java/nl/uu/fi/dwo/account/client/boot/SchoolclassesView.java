package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.List;
import java.util.logging.Logger;

/**
 * GWT Panel that handles switching the role.
 *
 * @author G.A.J. van der Plas
 */
public class SchoolclassesView extends Composite implements ClickHandler, SchoolclassesPresenter.Display {

    private static final Logger LOG = Logger.getLogger(SchoolclassesView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, SchoolclassesView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private SchoolclassesPresenter schoolclassesPresenter;

    @UiField(provided = true)
    CellTable dataGrid;
//    @UiField(provided = true)            
//    CellList dataGrid;
    @UiField
    SimplePager pager;
//    @UiField
    Button cancelBtn;
//    @UiField
    Button switchBtn;
    int schoolIndex = 1;

    public SchoolclassesView(SchoolclassesPresenter sp) {
        schoolclassesPresenter = sp;
        schoolclassesPresenter.setView(this);
        //dataGrid
        String[] tableHeaders = sp.getTableHeaders();
//        TextCell textCell = new TextCell();
        dataGrid = new CellTable<String>();
        for (String header : tableHeaders) {
            TextColumn<String> value = new TextColumn<String>() {
                @Override
                public String getValue(String object) {
                    return object;
                }
            };
            dataGrid.addColumn(value, header);
        }

        ListDataProvider<String> dataProvider = new ListDataProvider<String>();

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(dataGrid);

        // Add the data to the data provider, which automatically pushes it to the
        // widget.
        List<String> data = dataProvider.getList();
        data.add("een");
        data.add("twee");
        dataGrid.setRowData(0, data);
        dataGrid.setRowCount(data.size(), true);

        initWidget(uiBinder.createAndBindUi(this));
        //controller must be before clicks occur
//        switchBtn.addClickHandler(this);
//        final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
//        dataGrid.setSelectionModel(selectionModel);
//        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//            public void onSelectionChange(SelectionChangeEvent event) {
//                Address selected = selectionModel.getSelectedObject();
//                if (selected != null) {
//                    Window.alert("You selected: " + selected.houseNumber + " " + selected.streetName + " " + selected.county
//                            + " " + selected.postCode + " " + selected.country);
//                }
//            }
//        });

    }

    @Override
    public void init() {
        //create table
        String nulLabel = "Schoolclasses";
        HTML l = new HTML("<div style=\"text-align: left; background-color: #555555; padding: 2px; overflow auto;\">" + nulLabel + "</div>");

//        flexTable.setWidget(0, 0, l);
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
            schoolclassesPresenter.switchSchool();
        }
    }

    public void updateView(int height, int width, String[][] data) {
//          flexTable.removeAllRows();
//        int i = height;
//        int j = width;
//        // column labels
//        HTML html;
////= new HTML(data[0][0]);
////        html.setStyleName("flexTableHeader");
////        flexTable.setWidget(0, 0, html);
////
//        for (i = 0; i < width; i++) {
//            html = new HTML("<div style=\"text-align: left; background-color: #aaaaaa; padding: 2px; overflow auto;\">" + data[0][i] + "<div>");
//            flexTable.setWidget(0, i, html);
//        }
//        //       flexTable.getRowFormatter().getElement(0).setClassName("flexTableHeader");         
////
////        // row labels
////        for (i = 0; i < height; i++) {
////            html = new HTML(data[i][0]);
////            html.setStyleName("flexTableHeader");
////            flexTable.setWidget(i, 0, html);
////        }
//
//        for (j = 0; j < width; j++) {
//            for (i = 1; i < height; i++) {
//                html = new HTML(data[i][j]);
////                html.setStyleName("flexTableCell");
//                flexTable.setWidget(i, j, html);
//            }
//        }
//
//        flexTable.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                if (flexTable.getCellForEvent(event) != null) {
//                    int curSchoolIndex = schoolIndex;
//                    schoolIndex = flexTable.getCellForEvent(event).getRowIndex() - 1;
//                    schoolclassesPresenter.selectRow(schoolIndex);
//                    if ((schoolIndex + 1) % 2 == 0) {
//                        flexTable.getCellFormatter().removeStyleName(schoolIndex + 1, 0, "flexTableOddRow");
//                    } else {
//                        flexTable.getCellFormatter().removeStyleName(schoolIndex + 1, 0, "flexTableEvenRow");
//                    }
//                    flexTable.getRowFormatter().getElement(schoolIndex + 1).setClassName("flexTableSelectedBackground");
//                    if (curSchoolIndex != schoolIndex) {
//                        flexTable.getRowFormatter().getElement(curSchoolIndex + 1).removeClassName("flexTableSelectedBackground");
//                        if ((schoolIndex + 1) % 2 == 0) {
//                            flexTable.getCellFormatter().addStyleName(schoolIndex + 1, 0, "flexTableOddRow");
//                        } else {
//                            flexTable.getCellFormatter().addStyleName(schoolIndex + 1, 0, "flexTableEvenRow");
//                        }
//                    }
//                    LOG.log(Level.INFO, "Clicked school with index" + schoolIndex);
//                }
//            }
//        });
//
//        flexTable.setVisible(true);
    }

}
