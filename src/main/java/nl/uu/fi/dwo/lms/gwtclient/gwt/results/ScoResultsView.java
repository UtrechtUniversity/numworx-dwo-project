package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsPresenter.StudentItem;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

/**
 * GWT Panel that handles switching the role.
 *
 * @author G.A.J. van der Plas
 */
public class ScoResultsView extends Composite implements ScoResultsPresenter.Display {

    private static final Logger LOG = Logger.getLogger(ScoResultsView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, ScoResultsView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private ScoResultsPresenter scoResultsPresenter;
    @UiField(provided = true)
    SimplePager pager;
    @UiField(provided = true)
    CellTable dataTable;
    @UiField
    Button backBtn;
    @UiField
    Button sealBtn;
    @UiField
    Frame frame; // Hier komt /dwo/apps/player.html?locale=nl#cmi.launch_data:scoid

    private native static void setAPI(ScoResultsView view) /*-{
    	var api = {
    			"LMSGetValue" : function(key) {
    				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsView::getValue(Ljava/lang/String;)(key)
    			},
    			"LMSInitialize" : function(dummy) { 
    				return "true"
    			},
    			"LMSGetLastError": function () {
    				return "0"
    			},
    			"LMSGetDiagnostic": function(dummy) { 
    				return ""
    			},
    			"LMSGetErrorString": function( code ) {
    				return ""
    			},
    			"LMSCommit": function(dummy) {
    				return "true"
    			},
    			"LMSFinish": function(dummy) {
    				return "true"
    			},
    			"LMSSetValue" : function(key, value) {
    				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsView::setValue(Ljava/lang/String;Ljava/lang/String;)(key, value)
    			},
    			"GetValue" : function(key) {
    				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsView::getValue(Ljava/lang/String;)(key)
    			},
    			"SetValue" : function(key, value) {
    				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsView::setValue(Ljava/lang/String;Ljava/lang/String;)(key, value)
    			},
    // TODO more to follow...			
    		};
    	$wnd.API = api;
    	$wnd.API_1484_11 = api;
    }-*/;

    private String getValue(String key) {
    	LOG.info("GetValue " + key);
        String value = scoResultsPresenter.getScormAPIValue(key);
        String shortValue = value.length() > 10 ? value.substring(0, 10) + "..." : value;
        LOG.info("result GetValue: " + shortValue);
		return value;
    }

    private String setValue(String key, String value) {
        return scoResultsPresenter.setScormAPIValue(key, value);
    }

//    private ScoResultsPresenter.StudentItem selected;
    private ListDataProvider<ScoResultsPresenter.StudentItem> dataProvider = new ListDataProvider<ScoResultsPresenter.StudentItem>();

    //non-clickable cell
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
//                cellSelected(context.getIndex(), context.getColumn());
            }
        }
    }

    //clickable cell
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
                //TODO Wim, the line below fetches row/column info of a clicked MyClickCell and call
                //cellSelected
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

            	LOG.log(Level.INFO, "key " + context.getKey() + " boolean " + value);
            }
        }
    }

    
    
    ScoResultsPresenter.StudentItem selected;

    public ScoResultsView(ScoResultsPresenter sp) {
        scoResultsPresenter = sp;
        setAPI(this);
        scoResultsPresenter.setView(this);
        String[] tableHeaders = sp.getTableHeaders();
        dataTable = new CellTable<String>();
        dataProvider.addDataDisplay(dataTable);
        dataTable.setSkipRowHoverCheck(true);
        dataTable.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

        List<ScoResultsPresenter.StudentItem> data = dataProvider.getList();
        final MyCell cell = new MyCell();
        final MyClickCell clickCell = new MyClickCell();

        //fullname column
        Column<ScoResultsPresenter.StudentItem, String> value = new Column<ScoResultsPresenter.StudentItem, String>(clickCell) {
            @Override
            public String getValue(ScoResultsPresenter.StudentItem object) {
                return object.givenName + " " + object.insertion + " " + object.familyName + " - " + object.usercode;
            }
        };
        value.setSortable(true);
        ListHandler<ScoResultsPresenter.StudentItem> columnSortHandler = new ListHandler<ScoResultsPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<ScoResultsPresenter.StudentItem>() {
            public int compare(ScoResultsPresenter.StudentItem o1, ScoResultsPresenter.StudentItem o2) {
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
        dataTable.addColumnSortHandler(columnSortHandler);
        dataTable.addColumn(value, tableHeaders[0]);

        //total score column
        value = new Column<ScoResultsPresenter.StudentItem, String>(clickCell) {
            @Override
            public String getValue(ScoResultsPresenter.StudentItem object) {
                return (object.score != null) ? "" + object.score : "";
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<ScoResultsPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<ScoResultsPresenter.StudentItem>() {
            public int compare(ScoResultsPresenter.StudentItem o1, ScoResultsPresenter.StudentItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null && o1.score!=null) {
                    return (o2 != null && o2.score!=null) ? o1.score.compareTo(o2.score) : 1;
                }
                return -1;
            }
        });
        dataTable.addColumnSortHandler(columnSortHandler);
        dataTable.addColumn(value, tableHeaders[1]);

        Cell<Boolean> becel = new MyCheckBoxCell(false,false);
		Column<StudentItem, Boolean> value2 = new Column<ScoResultsPresenter.StudentItem, Boolean>(becel) {

			@Override
			public Boolean getValue(StudentItem object) {
				return object.sealed;
			}
        	
        };
        value2.setFieldUpdater(new FieldUpdater<ScoResultsPresenter.StudentItem, Boolean>() {
			
			@Override
			public void update(int index, StudentItem object, Boolean value) {
				LOG.info("update value " + index + ", " + object + ", " + value);
				scoResultsPresenter.setSeal(object, value);
				
			}
		});
        dataTable.addColumn(value2, tableHeaders[2]);
        
        
        

 //code for row selection        
        final SingleSelectionModel<ScoResultsPresenter.StudentItem> selectionModel = new SingleSelectionModel<ScoResultsPresenter.StudentItem>();
        dataTable.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
        {
            public void onSelectionChange(SelectionChangeEvent event) {
//                if(((ScoResultsPresenter.StudentItem) selectionModel.getSelectedObject()).score!=null){
                selected = selectionModel.getSelectedObject();
                //selectionModel.setSelected(selected, true);
                LOG.log(Level.INFO, "selection key: " + selectionModel.getSelectedObject().key);
            }
        }
);

//code for row/column selection
//        //for each of the subscores add a column and use a clickable cell
//        value = new Column<ScoResultsPresenter.StudentItem, String>(cell) {
//            @Override
//            public String getValue(ScoResultsPresenter.StudentItem object) {
//                return object.usercode;
//            }
//        };
//        value.setSortable(true);
//        columnSortHandler = new ListHandler<ScoResultsPresenter.StudentItem>(
//                data);
//        columnSortHandler.setComparator(value,
//                new Comparator<ScoResultsPresenter.StudentItem>() {
//            public int compare(ScoResultsPresenter.StudentItem o1, ScoResultsPresenter.StudentItem o2) {
//                if (o1 == o2) {
//                    return 0;
//                }
//
//                // Compare the name columns.
//                if (o1 != null) {
//                    return (o2 != null) ? o1.usercode.compareTo(o2.usercode) : 1;
//                }
//                return -1;
//            }
//        });
//        dataTable.addColumnSortHandler(columnSortHandler);
//        dataTable.addColumn(value, tableHeaders[0]);
//        
        dataTable.setRowData(0, data);
        dataTable.setRowCount(data.size(), true);
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataTable);
        pager.setPageSize(dataTable.getPageSize());
        //disable yellow keyboard selection bar and hover coloring 
        dataTable.setSkipRowHoverCheck(true);
        dataTable.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init() {
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @UiHandler("backBtn")
    public void onBack(ClickEvent event) {
            scoResultsPresenter.goBackToResults();
    }

    @UiHandler("sealBtn")
    public void onSeal(ClickEvent event) {
    	LOG.info("seal all students");
    	scoResultsPresenter.sealAllStudents();
    }
    
    
    public void updateView(ScoResultsPresenter.StudentItem selectedItem, Map<String, ScoResultsPresenter.StudentItem> data) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(data.values());
        dataProvider.refresh();
        if (selectedItem != null) {
            dataTable.getSelectionModel().setSelected(selectedItem, true);
        }
    }

    
    public void updateFrame(DomScoContext sco) {
    	String scoId = "96797";
    	String pid = sco.getId().getIdString();
    	int komma = pid.lastIndexOf(';');
    	if(komma >=0) {
    		scoId = pid.substring(komma+1);
    	}
    	String random = String.valueOf(System.currentTimeMillis());
    	LOG.info("Frame = "+random);
    	String url = "/dwo/apps/player.html?locale=nl&t=" + random + "#cmi.launch_data:"+scoId;
    	frame.setUrl(url);
    }
    
//    private void select(ScoResultsPresenter.StudentItem item) {
//    	LOG.info("select " + item.usercode);
//        scoResultsPresenter.select(item);
//    }

    private void cellSelected(int index, int col) {
       LOG.log(Level.FINE, "Clicked index x col " + index + "x" + col + " " + index + " " + dataTable.getHeader(col).getValue());
        //dataTable.getHeader(col);
        if(!dataTable.getHeader(col).getValue().equals(scoResultsPresenter.getTableHeaders()[scoResultsPresenter.getTableHeaders().length])){
            dataTable.getSelectionModel().setSelected(dataProvider.getList().get(index), true);
        }
        scoResultsPresenter.selectItem((ScoResultsPresenter.StudentItem) dataProvider.getList().get(index) , col);
    }
}
