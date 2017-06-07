package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
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

    @UiField(provided = true)
    CellTable dataGrid;
//    @UiField(provided = true)            
//    CellList dataGrid;
    @UiField(provided = true)
    SimplePager pager;
    @UiField
    Button addBtn;

    private SchoolclassesPresenter schoolclassesPresenter;
    SchoolclassesPresenter.ClassItem selected;
    ListDataProvider<SchoolclassesPresenter.ClassItem> dataProvider = new ListDataProvider<SchoolclassesPresenter.ClassItem>();
    
    public SchoolclassesView(SchoolclassesPresenter sp) {
        schoolclassesPresenter = sp;
        schoolclassesPresenter.setView(this);
        String[] tableHeaders = sp.getTableHeaders();
//        TextCell textCell = new TextCell();
        dataGrid = new CellTable<String>();

        ListDataProvider<String> dataProvider = new ListDataProvider<String>();

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(dataGrid);

        // Add the data to the data provider, which automatically pushes it to the
        // widget.
        List<String> data = dataProvider.getList();
        for (int i = 0; i < 100; i++) {
            data.add("row" + i);
        }

        for (String header : tableHeaders) {
            TextColumn<String> value = new TextColumn<String>() {
                @Override
                public String getValue(String object) {
                    return object;
                }
            };
            if(header.equals(tableHeaders[0])){
            value.setSortable(true);
            ListHandler<String> columnSortHandler = new ListHandler<String>(
                    data);
            columnSortHandler.setComparator(value,
                    new Comparator<String>() {
                public int compare(String o1, String o2) {
                    if (o1 == o2) {
                        return 0;
                    }

                    // Compare the name columns.
                    if (o1 != null) {
                        return (o2 != null) ? o1.compareTo(o2) : 1;
                    }
                    return -1;
                }
            });
            dataGrid.addColumnSortHandler(columnSortHandler);
        }else{
                value.setSortable(false);
            }
            dataGrid.addColumn(value, header);

        }

        dataGrid.setRowData(0, data);
        dataGrid.setRowCount(data.size(), true);
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
        pager.setPageSize(dataGrid.getPageSize());

        initWidget(uiBinder.createAndBindUi(this));
        //controller must be before clicks occur
        addBtn.addClickHandler(this);
        final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
        dataGrid.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                String selected = selectionModel.getSelectedObject();
                LOG.log(Level.INFO, "selection key: "+selectionModel.getKey(selected));
                if (selected != null) {
                    Window.alert("You selected: " + selected + ".");
                }
            }
        });

    }

    @Override
    public void init() {
        addBtn.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == addBtn) {
            schoolclassesPresenter.addASchoolClass();
        }
    }

    public void updateView(Map<String, SchoolclassesPresenter.ClassItem> data) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(data.values());
        dataProvider.refresh();
    }

}
