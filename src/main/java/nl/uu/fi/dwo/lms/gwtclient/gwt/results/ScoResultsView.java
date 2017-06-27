package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Logger;

/**
 * GWT Panel that handles switching the role.
 *
 * @author G.A.J. van der Plas
 */
public class ScoResultsView extends Composite implements ClickHandler, ScoResultsPresenter.Display {

    private static final Logger LOG = Logger.getLogger(ScoResultsView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, ScoResultsView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private ScoResultsPresenter scoResultsPresenter;

    @UiField
    CellTable cellTable;
    @UiField
    Button backBtn;
    @UiField
    Frame frame; // Hier komt /dwo/apps/player.html?locale=nl#cmi.launch_data:scoid

    private native static void setAPI(ScoResultsView view) /*-{
    	var api = {
    			"LMSGetValue" : function(key) {
    				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsView::getValue(Ljava/lang/String;)(key)
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
    	return scoResultsPresenter.getScormAPIValue(key);
    }
    
    private String setValue(String key, String value) {
    	return scoResultsPresenter.setScormAPIValue(key, value);
    }
    
    public class ResultData {

        int width;
        int height;
        String[][] data; //height, width
    }

    public ScoResultsView(ScoResultsPresenter sp) {
        scoResultsPresenter = sp;
        scoResultsPresenter.setView(this);
        initWidget(uiBinder.createAndBindUi(this));
        //controller must be before clicks occur
        backBtn.addClickHandler(this);
    }

    @Override
    public void init() {
//        //create table
//        String nulLabel = "Result";
//        HTML l = new HTML("<div style=\"text-align: left; background-color: #555555; padding: 2px; overflow auto;\">" + nulLabel + "</div>");
//
//        cellTable.setWidget(0, 0, l);
//        backBtn.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onClick(ClickEvent event) {
        //            scoResultsPresenter.selectRow(schoolIndex);

        if (event.getSource() == backBtn) {
            scoResultsPresenter.goBackToResults();
        }
    }

    public void updateView(int height, int width, String[][] data) {
//        cellTable.removeAllRows();
//        int i = height;
//        int j = width;
//        // column labels
//        HTML html;
////= new HTML(data[0][0]);
////        html.setStyleName("flexTableHeader");
////        cellTable.setWidget(0, 0, html);
////
//        for (i = 0; i < width; i++) {
//            html = new HTML("<div style=\"text-align: left; background-color: #aaaaaa; padding: 2px; overflow auto;\">"+data[0][i]+"<div>");
//            cellTable.setWidget(0, i, html);
//        }
// //       cellTable.getRowFormatter().getElement(0).setClassName("flexTableHeader");         
////
////        // row labels
////        for (i = 0; i < height; i++) {
////            html = new HTML(data[i][0]);
////            html.setStyleName("flexTableHeader");
////            cellTable.setWidget(i, 0, html);
////        }
//
//        for (j = 0; j < width; j++) {
//            for (i = 1; i < height; i++) {
//                html = new HTML(data[i][j]);
////                html.setStyleName("flexTableCell");
//                cellTable.setWidget(i, j, html);
//            }
//        }
//
//        cellTable.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                if (cellTable.getCellForEvent(event) != null) {
//                    int curSchoolIndex = schoolIndex;
//                    schoolIndex = cellTable.getCellForEvent(event).getRowIndex() - 1;
//                    scoResultsPresenter.selectRow(schoolIndex);
//                    if ((schoolIndex + 1) % 2 == 0) {
//                        cellTable.getCellFormatter().removeStyleName(schoolIndex + 1, 0, "flexTableOddRow");
//                    } else {
//                        cellTable.getCellFormatter().removeStyleName(schoolIndex + 1, 0, "flexTableEvenRow");
//                    }
//                    cellTable.getRowFormatter().getElement(schoolIndex + 1).setClassName("flexTableSelectedBackground");
//                    if (curSchoolIndex != schoolIndex) {
//                        cellTable.getRowFormatter().getElement(curSchoolIndex + 1).removeClassName("flexTableSelectedBackground");
//                        if ((schoolIndex + 1) % 2 == 0) {
//                            cellTable.getCellFormatter().addStyleName(schoolIndex + 1, 0, "flexTableOddRow");
//                        } else {
//                            cellTable.getCellFormatter().addStyleName(schoolIndex + 1, 0, "flexTableEvenRow");
//                        }
//                    }
//                    LOG.log(Level.INFO, "Clicked school with index" + schoolIndex);
//                }
//            }
//        });
//
//        cellTable.setVisible(true);
    }

}
