package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.gui.DwoImageToolTipClickCell;
import nl.uu.fi.dwo.lms.gwtclient.gwt.gui.SelectedCellHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.icons.DwoResources;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * GWT Panel that handles switching the role.
 *
 * @author G.A.J. van der Plas
 */
public class StudentsInSchoolclassView extends Composite implements ClickHandler, ChangeHandler, SelectedCellHandler, StudentsInSchoolclassPresenter.Display {

    private static final Logger LOG = Logger.getLogger(StudentsInSchoolclassView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, StudentsInSchoolclassView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField(provided = true)
    CellTable dataGrid;
//    @UiField(provided = true)            
//    CellList dataGrid;
    @UiField(provided = true)
    SimplePager pager;
    @UiField
    Button backBtn;
    @UiField
    Button addStudentsBtn; // single school students
    @UiField
    Button addToSchoolClass;
    @UiField
    Button removeSelectedBtn;
    @UiField
    ListBox schoolClassListBox;
    @UiField
    DwoLocalesForGWT rb = DwoLocalesForGWT.instance;
    
    private static final DwoResources resources = GWT.create(DwoResources.class);
    Image editImage = new Image(resources.editIcon());
    Image loadingImage = new Image(resources.loadingIcon());
    Image emptyImage = new Image(resources.emptyIcon());
    
    
    private StudentsInSchoolclassPresenter studentsInSchoolclassPresenter;
    private StudentsInSchoolclassPresenter.StudentItem selected;
    private ListDataProvider<StudentsInSchoolclassPresenter.StudentItem> dataProvider = new ListDataProvider<StudentsInSchoolclassPresenter.StudentItem>();
    private List<SchoolClassListBoxItem> schoolClassList;
    private MyCheckBoxCell checkBox;

    public class MyCell extends AbstractCell<String> {

        public MyCell() {
            super("click", "keydown");
        }

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.appendEscaped(value);
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

    public class MyCheckBoxCell extends CheckboxCell {

        boolean state = false;

        public MyCheckBoxCell(boolean a, boolean b) {
            super(a, b);
        }

        @Override
        public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Boolean value, NativeEvent event, ValueUpdater<Boolean> valueUpdater) {
            if (value == null) {
                return;
            }
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
            if ("change".equals(event.getType())) {
                studentsInSchoolclassPresenter.selectItem((StudentsInSchoolclassPresenter.StudentItem) context.getKey(), 5);
                LOG.log(Level.INFO, "key " + context.getKey() + " boolean " + value);
            }
        }
    }

    public StudentsInSchoolclassView(StudentsInSchoolclassPresenter sp) {
        studentsInSchoolclassPresenter = sp;
        studentsInSchoolclassPresenter.setView(this);
        String[] tableHeaders = sp.getTableHeaders();
        dataGrid = new CellTable<String>();
//        schoolClassListBox = new ValueListBox<SchoolClassItem>(new Renderer<SchoolClassItem>() {
//
//            public String render(SchoolClassListBoxItem item) {                
//                return item.getSchoolclassName();
//            }
//
//            public void render(SchoolClassListBoxItem user, Appendable appendable) throws IOException {
//                String s = render(user);
//                appendable.append(s);
//            }
//        });

        dataProvider.addDataDisplay(dataGrid);
        dataGrid.setSkipRowHoverCheck(true);
        dataGrid.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

        List<StudentsInSchoolclassPresenter.StudentItem> data = dataProvider.getList();
        final StudentsInSchoolclassView.MyCell cell = new StudentsInSchoolclassView.MyCell();
        final StudentsInSchoolclassView.MyClickCell clickCell = new StudentsInSchoolclassView.MyClickCell();

        //givenName
        Column<StudentsInSchoolclassPresenter.StudentItem, String> value = new Column<StudentsInSchoolclassPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(StudentsInSchoolclassPresenter.StudentItem object) {
                return object.givenName;
            }
        };
        value.setSortable(true);
        ListHandler<StudentsInSchoolclassPresenter.StudentItem> columnSortHandler = new ListHandler<StudentsInSchoolclassPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<StudentsInSchoolclassPresenter.StudentItem>() {
            public int compare(StudentsInSchoolclassPresenter.StudentItem o1, StudentsInSchoolclassPresenter.StudentItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.givenName.compareTo(o2.givenName) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, rb.GUI_Table_GivenName());
        dataGrid.getColumnSortList().push(value);

        //insertion
        value = new Column<StudentsInSchoolclassPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(StudentsInSchoolclassPresenter.StudentItem object) {
                return object.insertion;
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<StudentsInSchoolclassPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<StudentsInSchoolclassPresenter.StudentItem>() {
            public int compare(StudentsInSchoolclassPresenter.StudentItem o1, StudentsInSchoolclassPresenter.StudentItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.insertion.compareTo(o2.insertion) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, rb.GUI_Table_Insertion());

        //familyName
        value = new Column<StudentsInSchoolclassPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(StudentsInSchoolclassPresenter.StudentItem object) {
                return object.familyName;
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<StudentsInSchoolclassPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<StudentsInSchoolclassPresenter.StudentItem>() {
            public int compare(StudentsInSchoolclassPresenter.StudentItem o1, StudentsInSchoolclassPresenter.StudentItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.familyName.compareTo(o2.familyName) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, rb.GUI_Table_FamilyName());

        //usercode
        value = new Column<StudentsInSchoolclassPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(StudentsInSchoolclassPresenter.StudentItem object) {
                return object.usercode;
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<StudentsInSchoolclassPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<StudentsInSchoolclassPresenter.StudentItem>() {
            public int compare(StudentsInSchoolclassPresenter.StudentItem o1, StudentsInSchoolclassPresenter.StudentItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.usercode.compareTo(o2.usercode) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, rb.GUI_Table_Usercode());

        //edit student
        final DwoImageToolTipClickCell editClickCell = new DwoImageToolTipClickCell(editImage, "click to edit");
        editClickCell.addSelectedCellHandler(this);        
        value = new Column<StudentsInSchoolclassPresenter.StudentItem, String>(editClickCell) {
            @Override
            public String getValue(StudentsInSchoolclassPresenter.StudentItem object) {
                if (object.singleSchool == true) {
                    return tableHeaders[4];
                } else {
                    return null;
                }
            }
        };
        value.setSortable(false);
        dataGrid.addColumn(value, rb.GUI_Table_Edit());
        
        ColumnSortEvent.fire(dataGrid, dataGrid.getColumnSortList());
        //select student
        checkBox = new MyCheckBoxCell(true, true);
        Column<StudentsInSchoolclassPresenter.StudentItem, Boolean> bValue = new Column<StudentsInSchoolclassPresenter.StudentItem, Boolean>(checkBox) {
            @Override
            public Boolean getValue(StudentsInSchoolclassPresenter.StudentItem object) {
                return object.selected;
            }
        };

        bValue.setSortable(false);
        dataGrid.addColumn(bValue, rb.GUI_Table_Select());

        dataGrid.setRowData(0, data);
        dataGrid.setRowCount(data.size(), true);
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
        pager.setPageSize(dataGrid.getPageSize());

        initWidget(uiBinder.createAndBindUi(this));
        //controller must be before clicks occur
//        final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
//        dataGrid.setSelectionModel(selectionModel);
//        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//            public void onSelectionChange(SelectionChangeEvent event) {
//                String selected = selectionModel.getSelectedObject();
//                LOG.log(Level.INFO, "selection key: " + selectionModel.getKey(selected));
//                if (selected != null) {
//                    Window.alert("You selected: " + selected + ".");
//                }
//            }
//        });
        backBtn.addClickHandler(this);
        removeSelectedBtn.addClickHandler(this);
        addStudentsBtn.addClickHandler(this);
        addToSchoolClass.addClickHandler(this);
        schoolClassListBox.addChangeHandler(this);
    }

    public void init() {
//        addStudentsBtn.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void clear() {
        dataProvider.getList().clear();
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == backBtn) {
            studentsInSchoolclassPresenter.goBackToSchoolClasses();
        } else if (event.getSource() == addStudentsBtn) {
            studentsInSchoolclassPresenter.addStudents();
        } else if (event.getSource() == removeSelectedBtn) {
            studentsInSchoolclassPresenter.removeSelectedFromSchoolClass();
        } else if (event.getSource() == addToSchoolClass) {
            studentsInSchoolclassPresenter.addSelectedToSchoolClass(schoolClassList.get(schoolClassListBox.getSelectedIndex()).getKey());
//        } else if (event.getSource() == schoolClassListBox) {
//            studentsInSchoolclassPresenter.updateSchoolClasses();
//        } else if (event.getSource() == checkBox) {
//
        }
    }

    @Override
    public void onChange(ChangeEvent event) {
        //    LOG.log(Level.INFO, "Listbox event:" + event.getSource().toString());
    }

    public void updateView(Map<String, StudentsInSchoolclassPresenter.StudentItem> data) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(data.values());
        dataProvider.refresh();
    }

    public void updateSchoolClassList(List<SchoolClassListBoxItem> data) {
        schoolClassList = data;

        schoolClassListBox.clear();
        for (SchoolClassListBoxItem item : data) {
            schoolClassListBox.addItem(item.getSchoolclassName());
        }
    }

    private void cellSelected(int row, int column) {
        LOG.log(Level.FINE, "Clicked row x col " + row + "x" + column + " " + dataProvider.getList().get(row).usercode + " " + dataGrid.getHeader(column).getValue());
        dataGrid.getHeader(column);
        studentsInSchoolclassPresenter.selectItem((StudentsInSchoolclassPresenter.StudentItem) dataProvider.getList().get(row), column);
    }

    public void onSelectedCell(Cell.Context context, String value) {
        cellSelected(context.getIndex(), context.getColumn());
    }    

    public void setEmptyTableMessage(){
        dataGrid.setEmptyTableWidget(emptyImage);
    }

    public void setLoadingTableMessage(){
        dataGrid.setEmptyTableWidget(loadingImage);
    }    
}
