package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ViewFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.gui.DwoCell;
import nl.uu.fi.dwo.lms.gwtclient.gwt.gui.DwoImageToolTipClickCell;
import nl.uu.fi.dwo.lms.gwtclient.gwt.gui.SelectedCellHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.icons.DwoResources;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * GWT Panel that handles switching the role.
 *
 * @author G.A.J. van der Plas
 */
public class SchoolclassesView extends Composite implements ClickHandler, SelectedCellHandler, SchoolclassesPresenter.Display {

    private static final Logger LOG = Logger.getLogger(SchoolclassesView.class.getName());

    @Override
    public void setEmptyTableMessage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLoadingTableMessage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    interface MyUiBinder extends UiBinder<Widget, SchoolclassesView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private static final DwoResources resources = GWT.create(DwoResources.class);
    Image teachersImage = new Image(resources.teachersIcon());
    Image studentsImage = new Image(resources.studentsIcon());
    Image deleteImage = new Image(resources.deleteIcon());
    Image emptyImage = new Image(resources.emptyIcon());
    Image editImage = new Image(resources.editIcon());
    Image modulesImage = new Image(resources.modulesIcon());

    @UiField(provided = true)
    CellTable dataGrid;
    @UiField(provided = true)
    SimplePager pager;
    @UiField
    Button addBtn;
    @UiField
    DwoLocalesForGWT rb = DwoLocalesForGWT.instance;

    private SchoolclassesPresenter schoolclassesPresenter;
    private ViewFactory viewFactory;
    SchoolclassesPresenter.ClassItem selected;
    ListDataProvider<SchoolclassesPresenter.ClassItem> dataProvider = new ListDataProvider<SchoolclassesPresenter.ClassItem>();
    AddSchoolclassView addSchoolclassView;
    final DialogBox dialogBox = new DialogBox();

    public class MyClickCell extends AbstractCell<String> {

        public MyClickCell() {
            super("click", "keydown");
        }

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.appendHtmlConstant("<a href='javascript:;'>");
                sb.appendEscaped(value);
                sb.appendHtmlConstant("</a>");

            }
        }

        @Override
        public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
            if (value == null) {
                return;
            }
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
            if ("click".equals(event.getType())) {
//                LOG.log(Level.INFO, "key "+context.getKey());
                cellSelected(context.getIndex(), context.getColumn());
            }
        }
    }

    public SchoolclassesView(SchoolclassesPresenter sp, ViewFactory vf) {
        schoolclassesPresenter = sp;
        viewFactory = vf;
        schoolclassesPresenter.setView(this);
        String[] tableHeaders = sp.getTableHeaders();
        dataGrid = new CellTable<String>();

        dataProvider.addDataDisplay(dataGrid);
        dataGrid.setSkipRowHoverCheck(true);
        dataGrid.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

        List<SchoolclassesPresenter.ClassItem> data = dataProvider.getList();
        final DwoCell cell = new DwoCell();
        //classname
        Column<SchoolclassesPresenter.ClassItem, String> value = new Column<SchoolclassesPresenter.ClassItem, String>(cell) {
            @Override
            public String getValue(SchoolclassesPresenter.ClassItem object) {
                return object.getSchoolclassName();
            }
        };
        value.setSortable(true);
        ListHandler<SchoolclassesPresenter.ClassItem> columnSortHandler = new ListHandler<SchoolclassesPresenter.ClassItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<SchoolclassesPresenter.ClassItem>() {
            public int compare(SchoolclassesPresenter.ClassItem o1, SchoolclassesPresenter.ClassItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.getSchoolclassName().compareTo(o2.getSchoolclassName()) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, rb.GUI_Table_Schoolclass());
        dataGrid.getColumnSortList().push(value);

        //edit
        final DwoImageToolTipClickCell editClickCell = new DwoImageToolTipClickCell(editImage, "click to edit");
        editClickCell.addSelectedCellHandler(this);
        value = new Column<SchoolclassesPresenter.ClassItem, String>(editClickCell) {
            @Override
            public String getValue(SchoolclassesPresenter.ClassItem object) {
                return tableHeaders[1];
            }
        ;
        };
            value.setSortable(false);
        value.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
        dataGrid.addColumn(value, rb.GUI_Table_Edit());

        //modules
        final DwoImageToolTipClickCell modulesClickCell = new DwoImageToolTipClickCell(modulesImage, "click to edit");
        modulesClickCell.addSelectedCellHandler(this);

        value = new Column<SchoolclassesPresenter.ClassItem, String>(modulesClickCell) {
            @Override
            public String getValue(SchoolclassesPresenter.ClassItem object) {
                return tableHeaders[2];
            }
        ;
        };
            value.setSortable(false);
        value.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);

        dataGrid.addColumn(value, rb.GUI_Table_Courses());

        //students col
        final DwoImageToolTipClickCell studentClickCell = new DwoImageToolTipClickCell(studentsImage, "click to edit");
        studentClickCell.addSelectedCellHandler(this);

        value = new Column<SchoolclassesPresenter.ClassItem, String>(studentClickCell) {
            @Override
            public String getValue(SchoolclassesPresenter.ClassItem object) {
                return tableHeaders[3];
            }
        };
        value.setSortable(false);
        value.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);

        dataGrid.addColumn(value, rb.GUI_Table_Students());

        //teachers col
        final DwoImageToolTipClickCell teacherClickCell = new DwoImageToolTipClickCell(teachersImage, "click to edit");
        teacherClickCell.addSelectedCellHandler(this);
        value = new Column<SchoolclassesPresenter.ClassItem, String>(teacherClickCell) {
            @Override
            public String getValue(SchoolclassesPresenter.ClassItem object) {
                return tableHeaders[4];
            }
        };
        value.setSortable(false);
        value.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);

        dataGrid.addColumn(value, rb.GUI_Table_Teachers());

        //remove col
        final DwoImageToolTipClickCell deleteClickCell = new DwoImageToolTipClickCell(deleteImage, "select to delete");
        deleteClickCell.addSelectedCellHandler(this);
        value = new Column<SchoolclassesPresenter.ClassItem, String>(deleteClickCell) {
            @Override
            public String getValue(SchoolclassesPresenter.ClassItem object) {
                return tableHeaders[5];
            }
        };
        value.setSortable(false);
        value.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);

        dataGrid.addColumn(value, rb.GUI_Table_Remove());

        dataGrid.setRowData(0, data);
        dataGrid.setRowCount(data.size());

        ColumnSortEvent.fire(dataGrid, dataGrid.getColumnSortList());

        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);

        pager.setDisplay(dataGrid);

        pager.setPageSize(dataGrid.getPageSize());

        initWidget(uiBinder.createAndBindUi(this));
        addBtn.addClickHandler(this);

    }

    @Override
    public void init() {
        addSchoolclassView = (AddSchoolclassView) viewFactory.getAddSchoolclassView();
        addSchoolclassView.clear();
        if (dialogBox != null) {
            dialogBox.hide();
        }
        addBtn.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == addBtn) {
            schoolclassesPresenter.addSchoolClass();
        }
    }

    public void updateView(Map<String, SchoolclassesPresenter.ClassItem> data) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(data.values());
        dataProvider.refresh();
    }

    private void cellSelected(int row, int column) {
        LOG.log(Level.FINE, "Clicked row x col " + row + "x" + column + " " + dataProvider.getList().get(row).getSchoolclassName() + " " + dataGrid.getHeader(column).getValue());
        dataGrid.getHeader(column);
        schoolclassesPresenter.selectItem((SchoolclassesPresenter.ClassItem) dataProvider.getList().get(row), column);
    }

    public void onSelectedCell(Context context, String value) {
        cellSelected(context.getIndex(), context.getColumn());
    }
    
//    public void updateJSView(JSONObject object){
//       // object.
//    }
}
