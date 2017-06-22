package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.TextInputCell;
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
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * GWT Panel that handles switching the role.
 *
 * @author G.A.J. van der Plas
 */
public class AddStudentsView extends Composite implements ClickHandler, ChangeHandler, AddStudentsPresenter.Display {

    private static final Logger LOG = Logger.getLogger(AddStudentsView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, AddStudentsView> {
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
    Button addBtn; // single school students
    @UiField
    Button importBtn;
//    @UiField
//    FileUpload fileUploadWidget;

//JavaScriptObject files = fileUploadWidget.getElement().getPropertyJSO("files");
//
//readTextFile(files);
//
//public static void fileLoaded(String fileContents) {
//    GWT.log("File contents: " + fileContents);
//}
//
//public static native void readTextFile(JavaScriptObject files)
///*-{
//    var reader = new FileReader();
//
//    reader.onload = function(e) {
//        @com.example.YourClass::fileLoaded(*)(reader.result);
//    }
//
//    return reader.readAsText(files[0]);
//}-*/;
    private AddStudentsPresenter addStudentsPresenter;
    private AddStudentsPresenter.StudentItem selected;
    private ListDataProvider<AddStudentsPresenter.StudentItem> dataProvider = new ListDataProvider<AddStudentsPresenter.StudentItem>();
    private TextInputCell cell;

    public class DwoInputCell extends TextInputCell {
        public DwoInputCell() {
//            super("click", "keydown");
        }

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.appendEscaped(value);
            }
        }

        @Override
        public void finishEditing(Element parent,java.lang.String value,java.lang.Object key,ValueUpdater<java.lang.String> valueUpdater){
//            if(key.)
        }
        
        @Override
        public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
            if (value == null) {
                return;
            }
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
            if ("click".equals(event.getType())) {
//                LOG.log(Level.INFO, "key "+context.getKey());
//                cellSelected(context.getIndex(), context.getColumn());
            }
        }
    }

    public class ClickCell extends AbstractCell<String> {

        public ClickCell() {
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
//                cellSelected(context.getIndex(), context.getColumn());
            }
        }
    }

    public AddStudentsView(AddStudentsPresenter sp) {
        addStudentsPresenter = sp;
        addStudentsPresenter.setView(this);
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
        cell = new TextInputCell();
        List<AddStudentsPresenter.StudentItem> data = dataProvider.getList();

        //givenName
        Column<AddStudentsPresenter.StudentItem, String> value = new Column<AddStudentsPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(AddStudentsPresenter.StudentItem object) {
                return object.givenName;
            }
        };
        value.setSortable(true);
        ListHandler<AddStudentsPresenter.StudentItem> columnSortHandler = new ListHandler<AddStudentsPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<AddStudentsPresenter.StudentItem>() {
            public int compare(AddStudentsPresenter.StudentItem o1, AddStudentsPresenter.StudentItem o2) {
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
        dataGrid.addColumn(value, tableHeaders[0]);

        //insertion
        value = new Column<AddStudentsPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(AddStudentsPresenter.StudentItem object) {
                return object.insertion;
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<AddStudentsPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<AddStudentsPresenter.StudentItem>() {
            public int compare(AddStudentsPresenter.StudentItem o1, AddStudentsPresenter.StudentItem o2) {
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
        dataGrid.addColumn(value, tableHeaders[1]);

        //familyName
        value = new Column<AddStudentsPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(AddStudentsPresenter.StudentItem object) {
                return object.familyName;
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<AddStudentsPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<AddStudentsPresenter.StudentItem>() {
            public int compare(AddStudentsPresenter.StudentItem o1, AddStudentsPresenter.StudentItem o2) {
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
        dataGrid.addColumn(value, tableHeaders[2]);

        //usercode
        value = new Column<AddStudentsPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(AddStudentsPresenter.StudentItem object) {
                return object.usercode;
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<AddStudentsPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<AddStudentsPresenter.StudentItem>() {
            public int compare(AddStudentsPresenter.StudentItem o1, AddStudentsPresenter.StudentItem o2) {
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
        dataGrid.addColumn(value, tableHeaders[3]);

        //password
        value = new Column<AddStudentsPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(AddStudentsPresenter.StudentItem object) {
                return object.password;
            }
        };
        value.setSortable(false);
        dataGrid.addColumn(value, tableHeaders[4]);

        //remove
        ClickCell clickCell = new ClickCell();
        Column<AddStudentsPresenter.StudentItem, String> bValue = new Column<AddStudentsPresenter.StudentItem, String>(clickCell) {
            @Override
            public String getValue(AddStudentsPresenter.StudentItem object) {
                    return "remove";
            }
        };

        bValue.setSortable(false);
        dataGrid.addColumn(bValue, tableHeaders[5]);

        dataGrid.setRowData(0, data);
        dataGrid.setRowCount(data.size(), true);
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
        pager.setPageSize(dataGrid.getPageSize());

        initWidget(uiBinder.createAndBindUi(this));
        //controller must be before clicks occur
//        addBtn.addClickHandler(this);
//        final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
//        dataGrid.setSelectionModel(selectionModel);
//        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//            public void onSelectionChange(SelectionChangeEvent event) {
//                String success = selectionModel.getSelectedObject();
//                LOG.log(Level.INFO, "selection key: " + selectionModel.getKey(success));
//                if (success != null) {
//                    Window.alert("You success: " + success + ".");
//                }
//            }
//        });
        backBtn.addClickHandler(this);
        addBtn.addClickHandler(this);
//        importBtn.addClickHandler(this);
    }

    public void init() {
//        addBtn.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == backBtn) {
            addStudentsPresenter.goBackToSchoolClasses();
        } else if (event.getSource() == addBtn) {
            addStudentsPresenter.addNewStudents();
        } else if (event.getSource() == importBtn) {
            addStudentsPresenter.loadData();
        }
    }

    @Override
    public void onChange(ChangeEvent event) {
        //    LOG.log(Level.INFO, "Listbox event:" + event.getSource().toString());
    }

    public void updateView(Map<String, AddStudentsPresenter.StudentItem> data) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(data.values());
        dataProvider.refresh();
    }

//        https://svn.science.uu.nl/viewvc/project.fisme.java/StatistiekGWT/trunk/src/fi/statistiekgwt/client/StatTable.java?view=markup
//            above code for importing a file.
}
