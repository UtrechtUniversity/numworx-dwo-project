package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.Comparator;
import java.util.Map;
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
    
    @UiField(provided = true)
    CellTable cellTable;
    @UiField(provided = true)
    SimplePager pager;    
    @UiField
    Button cancelBtn;
    @UiField
    Button switchBtn;
    
    private SwitchSchoolPresenter switchSchoolPresenter;
    SwitchSchoolPresenter.SchoolItem selected;
    ListDataProvider<SwitchSchoolPresenter.SchoolItem> dataProvider = new ListDataProvider<SwitchSchoolPresenter.SchoolItem>();

    public class ResultData {

        int width;
        int height;
        String[][] data; //height, width
    }

    public SwitchSchoolView(SwitchSchoolPresenter sp) {
        switchSchoolPresenter = sp;
        switchSchoolPresenter.setView(this);
        
        String[] tableHeaders = sp.getTableHeaders();
//        TextCell textCell = new TextCell();
        cellTable = new CellTable<SwitchSchoolPresenter.SchoolItem>();

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(cellTable);

        for (String header : tableHeaders) {
            TextColumn<SwitchSchoolPresenter.SchoolItem> value = new TextColumn<SwitchSchoolPresenter.SchoolItem>() {
                @Override
                public String getValue(SwitchSchoolPresenter.SchoolItem object) {
                    return object.schoolName;
                }
            };
            value.setSortable(true);
            ColumnSortEvent.ListHandler<SwitchSchoolPresenter.SchoolItem> columnSortHandler = new ColumnSortEvent.ListHandler<SwitchSchoolPresenter.SchoolItem>(
                    dataProvider.getList());
            columnSortHandler.setComparator(value,
                    new Comparator<SwitchSchoolPresenter.SchoolItem>() {
                public int compare(SwitchSchoolPresenter.SchoolItem o1, SwitchSchoolPresenter.SchoolItem o2) {
                    if (o1 == o2) {
                        return 0;
                    }

                    // Compare the name columns.
                    if (o1 != null) {
                        return (o2 != null) ? o1.schoolName.compareTo(o2.schoolName) : 1;
                    }
                    return -1;
                }
            });
            cellTable.addColumnSortHandler(columnSortHandler);
            cellTable.addColumn(value, header);

        }

        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(cellTable);
        pager.setPageSize(cellTable.getPageSize());

        //controller must be before clicks occur
//        switchBtn.addClickHandler(this);
        final SingleSelectionModel<SwitchSchoolPresenter.SchoolItem> selectionModel = new SingleSelectionModel<SwitchSchoolPresenter.SchoolItem>();
        cellTable.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                selected = selectionModel.getSelectedObject();
                LOG.log(Level.INFO, "selection key: "+selectionModel.getSelectedObject().key);
            }
        });        
        
        initWidget(uiBinder.createAndBindUi(this));
        //controller must be before clicks occur
        switchBtn.addClickHandler(this);
    }
    

    @Override
    public void init() {
        //create table
//        String nulLabel = "School";
//        HTML l = new HTML("<div style=\"text-align: left; background-color: #555555; padding: 2px; overflow auto;\">" + nulLabel + "</div>");
//        cellTable.setWidget(0, 0, l);
        cancelBtn.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        switchBtn.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onClick(ClickEvent event) {

        if (event.getSource() == switchBtn) {
            switchSchoolPresenter.select(selected);
            switchSchoolPresenter.switchSchool();
        }
    }

    public void updateView(Map<String,SwitchSchoolPresenter.SchoolItem> data) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(data.values());
        dataProvider.refresh();
    }

}
