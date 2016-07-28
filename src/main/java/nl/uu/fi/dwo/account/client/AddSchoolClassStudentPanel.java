package nl.uu.fi.dwo.account.client;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

import fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.locale.DwoLocalesForGWT;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author G.A.J. van der Plas
 */
public class AddSchoolClassStudentPanel extends VerticalPanel implements ClickHandler {

    private Logger LOG = Logger.getLogger("Account");

    private SchoolClassStudentController control;
    private PopupPanel popup;

//    private Button delBtn;
    private Button addBtn;
    private Button closeBtn;
    private CellTable<DomSchoolClass> table = new CellTable<DomSchoolClass>();
    private ListDataProvider<DomSchoolClass> dataProvider = new ListDataProvider<DomSchoolClass>();
    private DomSchoolClass selectedClass = null;

    public PopupPanel getPopup() {
        return popup;
    }

    public void setPopup(PopupPanel popup) {
        this.popup = popup;
    }

    AddSchoolClassStudentPanel(DomUserFull user, SchoolClassStudentController aControl) {
        dataProvider.getList().clear();
        selectedClass = null;
        control = aControl;
        init(user);
//        control.init(user);

    }
    
    final Comparator<DomSchoolClass> classComparator = new Comparator<DomSchoolClass>() {
    	public int compare(DomSchoolClass o1, DomSchoolClass o2) {
		    if (o1 == o2) {
		      return 0;
		    }
		
		    // Compare the name columns.
		    if (o1 != null) {
		      return (o2 != null) ? o1.getSchoolClassName().compareTo(o2.getSchoolClassName()) : 1;
		    }
		    return -1;
		  }
		};

	private SingleSelectionModel<DomSchoolClass> selectionModel;

    public void init(DomUserFull user) {
        this.setSize("400", "500");

        control.updateSchoolClassesAddSchoolClassView();

        CellTable<DomSchoolClass> table = new CellTable<DomSchoolClass>();
        // Create name column.
        TextColumn<DomSchoolClass> schoolClassColumn = new TextColumn<DomSchoolClass>() {
            @Override
            public String getValue(DomSchoolClass data) {
                return data.getSchoolClassName();
            }
        };

        schoolClassColumn.setSortable(true);

        ListHandler<DomSchoolClass> columnSortHandler = new ListHandler<DomSchoolClass>(
                dataProvider.getList());
			columnSortHandler.setComparator(schoolClassColumn,
                classComparator);
            table.addColumnSortHandler(columnSortHandler);
        
        
        
        CellPreviewEvent.Handler<DomSchoolClass> cellPreviewHandler = new CellPreviewEvent.Handler<DomSchoolClass>() {
            @Override
            public void onCellPreview(CellPreviewEvent<DomSchoolClass> event) {
                int rowIndex = event.getIndex();
                int columnIndex = event.getColumn();
                int button = event.getNativeEvent().getButton();
                NativeEvent nativeEvent = event.getNativeEvent();
                if ("click".equals(nativeEvent.getType())
                        //                       && columnIndex == 0 // klik op rijnummer doet selectie
                        && button == NativeEvent.BUTTON_LEFT) {
                    LOG.log(Level.INFO, "x,y:" + rowIndex + "," + columnIndex + ":" + event.getSource());
                    DomSchoolClass sc = dataProvider.getList().get(rowIndex);
                    selectedClass = sc;
                    AddSchoolClassStudentPanel.this.table.setKeyboardSelectedRow(rowIndex);
                }
            }
        };
       // table.addCellPreviewHandler(cellPreviewHandler);
		selectionModel = new SingleSelectionModel<DomSchoolClass>();
		selectionModel.addSelectionChangeHandler(new Handler() {

			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				LOG.info("selection event " + selectionModel.getSelectedObject());
				selectedClass = selectionModel.getSelectedObject();
			}}); 
		table.setSelectionModel(selectionModel);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

        // Add the columns.
        table.addColumn(schoolClassColumn, DwoLocalesForGWT.instance.GUI_SchoolclassName());
        dataProvider.addDataDisplay(table);

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(table);
        scrollPanel.setPixelSize(-1, 300);
        VerticalPanel vPanel = new VerticalPanel();
        vPanel.add(scrollPanel);
//            panel.setPixelSize(-1,200);

        HorizontalPanel hPanel = new HorizontalPanel();
        vPanel.setHorizontalAlignment(HorizontalAlignmentConstant.endOf(Direction.DEFAULT));
        hPanel.setHorizontalAlignment(HorizontalAlignmentConstant.endOf(Direction.DEFAULT));
//            hPanel.getElement().getStyle().setPadding(20, Unit.PX);
        closeBtn = new Button("Close");
        closeBtn.addClickHandler(this);
        addBtn = new Button("Add");
        addBtn.addClickHandler(this);
        addBtn.addStyleName("paddedHorizontalPanel");
        hPanel.add(addBtn);
        closeBtn.addStyleName("paddedHorizontalPanel");
        hPanel.add(closeBtn);

        vPanel.add(hPanel);

        this.add(vPanel);

    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == addBtn) {        	
            LOG.log(Level.INFO, "Should add new window for adding a schoolclass.");
            //check for password required
            if (selectedClass != null && selectedClass.getHasRegKey()) {
                PopupPanel popup = new PopupPanel(true);//hide if clicked outside panel
                SchoolClassAskRegistrationKeyPanel panel = new SchoolClassAskRegistrationKeyPanel(control, selectedClass);
                panel.setSchoolClassName(selectedClass.getSchoolClassName());
                panel.setRegKey("");
                panel.setPopup(popup);
                panel.setControl(control);
                popup.add(panel);
                popup.center();
            } else {
                DomNewSchoolClass4Student nsc = new DomNewSchoolClass4Student(selectedClass);
                control.registerStudentForSchoolClass(nsc, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable t) {
                        //fail and reset all the data.
                        Window.alert(t.getMessage());
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        //update a view list
                        control.updateStudentsSchoolClassesInView();
                        popup.hide();
                    }
                });
            }
            popup.hide();
        } else if (event.getSource() == closeBtn) {
            LOG.log(Level.INFO, "Done, hiding window.");
            popup.hide();
        } else if (event.getSource() == closeBtn) {
            LOG.log(Level.INFO, "" + event.getSource());
        }
        LOG.log(Level.INFO, event.getSource().toString());
    }

    protected void setSchoolClasses(List<DomSchoolClass> schoolClasses) {
        if (dataProvider == null) {
            dataProvider = new ListDataProvider<DomSchoolClass>();
        }
        List<DomSchoolClass> list = dataProvider.getList();
        list.clear();
        for (DomSchoolClass schoolClass : schoolClasses) {
            list.add(schoolClass);
        }
        //Collections.sort(list, classComparator);
    }
}
