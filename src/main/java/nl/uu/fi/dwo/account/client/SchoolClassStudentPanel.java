package nl.uu.fi.dwo.account.client;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;

import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.locale.Dwo2ExceptionsForGWT;
import fi.dwo.rest.locale.DwoLocalesForGWT;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.account.client.icons.AccountImageBundle;

/**
 *
 * @author G.A.J. van der Plas
 */
public class SchoolClassStudentPanel extends VerticalPanel implements ClickHandler {

    private Logger LOG = Logger.getLogger("Account");

    private SchoolClassStudentController control;
    private PopupPanel popup;
    private Button delBtn;
    private Button addBtn;
    private Button closeBtn;
    CellTable<DomSchoolClass> table = new CellTable<DomSchoolClass>();
    ListDataProvider<DomSchoolClass> dataProvider = new ListDataProvider<DomSchoolClass>();
    Command resetLogin;
    
    public PopupPanel getPopup() {
        return popup;
    }

    public void setPopup(PopupPanel popup) {
        this.popup = popup;
    }

    SchoolClassStudentPanel(Command resetLogin, DomUserFull user) {
    	this.resetLogin = resetLogin;
        init(user);
        control = new SchoolClassStudentController(this, user);
//        addSchoolClassPanel = new AddSchoolClassStudentPanel(user, control);
//        control.init(user); wordt al in constructor gedaan.

    }

    public void init(DomUserFull user) {
        //this.setSize("400", "500");

        //control.getSchoolClasses();
//        Grid g = new Grid(control.getSchoolClasses().size() + 1, 3);
//        for (int i = 0; i < control.getSchoolClasses().size(); i++) {
//            g.setText(i, 0, control.getSchoolClasses().get(i).getSchoolClassName());
//            loginBtn = new Button("login");
//            loginBtn.addClickHandler(this);
//            g.setWidget(i, 1, loginBtn);
//            delBtn = new Button("del");
//            delBtn.addClickHandler(this);
//            g.setWidget(i, 2, delBtn);
//        }
//
//        // Just for good measure, let's put a button in the center.
//        doneButton = new Button("Done");
//        doneButton.addClickHandler(this);
//        g.setWidget(control.getSchoolClasses().size(), 0, doneButton);
//        newBtn = new Button("NEW");
//        newBtn.addClickHandler(this);
//        g.setWidget(control.getSchoolClasses().size(), 2, newBtn);
//        this.clear();
//        this.add(g);
//
        CellTable<DomSchoolClass> table = new CellTable<DomSchoolClass>();
        // Create name column.
        TextColumn<DomSchoolClass> schoolClassColumn = new TextColumn<DomSchoolClass>() {
            @Override
            public String getValue(DomSchoolClass data) {
                return data.getSchoolClassName();
            }
        };

        schoolClassColumn.setSortable(true);

        Column<DomSchoolClass, ImageResource> loginColumn
                = new Column<DomSchoolClass, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(DomSchoolClass object) {
                return AccountImageBundle.instance.student();
            }
        };
        Column<DomSchoolClass, ImageResource> deleteColumn
                = new Column<DomSchoolClass, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(DomSchoolClass object) {
                return AccountImageBundle.instance.delete();
            }
        };
//        TextColumn<DomSchoolClass> loginColumn = new TextColumn<DomSchoolClass>() {
//            @Override
//            public Image getValue(DomSchoolClass data) {
//                return AccountImageBundle.instance.student();
//            }
//        };
//
//        ImageColumn<DomSchoolClass> deleteColumn = new TextColumn<DomSchoolClass>() {
//            @Override
//            public Image getValue(DomSchoolClass data) {
//                return AccountImageBundle.instance.delete();
//            }
//        };

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
                    //LOG.log(Level.INFO, "x,y:" + rowIndex + "," + columnIndex + ":" + event.getSource());
                    DomSchoolClass sc = dataProvider.getList().get(rowIndex);
                    switch (columnIndex) {
                        case 1: //relogin with schoolclass set...
                            if(Window.confirm(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_User_ConfirmSchoolClassSwitch())==false){
                                return;
                            }
                            control.setActiveSchoolClass(sc, new AsyncCallback<Boolean>() {
                                @Override
                                public void onFailure(Throwable t) {
                                    //fail and reset all the data.
                                    Window.alert(t.getMessage());
                                    //TODO Wim
                                    //Window.alert("wim handles error here.");
                                }

                                @Override
                                public void onSuccess(Boolean result) {
                                	popup.hide();
                                    resetLogin.execute();
                                }
                            });
                            break;
                        case 2:     //remove schoolclass and relogin if it was the active schoolclass.
//                            if (sc.getId().equals(DwoGlobalVars.instance().getCurrentSchoolClass().getId())) {
                            control.removeSchoolClass(sc, new AsyncCallback<Boolean>() {
                                @Override
                                public void onFailure(Throwable t) {
                                    //fail and reset all the data.
                                    Window.alert(t.getMessage());
                                    //TODO Wim
                                    //Window.alert("wim handles error here.");
                                }

                                @Override
                                public void onSuccess(Boolean result) {
                                    //If active schoolClass is the same in DwoGlobalsVars 
                                    //then we did not unsubscribe from the active schoolClass
                                    control.getActiveSchoolClass(new AsyncCallback<DomSchoolClass>() {
                                        @Override
                                        public void onFailure(Throwable t) {
                                            //fail and reset all the data.
                                            Window.alert(t.getMessage());
                                        }

                                        @Override
                                        public void onSuccess(DomSchoolClass result) {
                                            DomSchoolClass current = DwoGlobalVars.getInstance().getCurrentSchoolClass();
											if (result != current 
													&& (result == null || 
													    current == null || 
													    !result.getId().equals(current.getId()))) {
                                            	popup.hide();
                                                resetLogin.execute();
                                                // no need to update schoolclassses.
                                            } else
                                            	control.updateStudentsSchoolClassesInView();
                                        }
                                    });
                                }
                            });
                            break;
                        default:
                    }
                }
            }
        };
        table.addCellPreviewHandler(cellPreviewHandler);

        // Add the columns.
        table.addColumn(schoolClassColumn, DwoLocalesForGWT.instance.GUI_SchoolclassName());
        table.addColumn(loginColumn, DwoLocalesForGWT.instance.GUI_Login());
        table.addColumn(deleteColumn, DwoLocalesForGWT.instance.GUI_Delete());
        dataProvider.addDataDisplay(table);

        VerticalPanel vPanel = new VerticalPanel();
        vPanel.add(table);

        HorizontalPanel hPanel = new HorizontalPanel();
        vPanel.setHorizontalAlignment(HorizontalAlignmentConstant.endOf(Direction.DEFAULT));
        hPanel.setHorizontalAlignment(HorizontalAlignmentConstant.endOf(Direction.DEFAULT));
//            hPanel.getElement().getStyle().setPadding(20, Unit.PX);
        closeBtn = new Button("Close");
        closeBtn.addClickHandler(this);
//        TODO need to resolve unknown class id in hasRole first
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
            LOG.log(Level.INFO, "Popup of AddSchoolClassStudentPanel.");
            PopupPanel popup = new PopupPanel(true);//hide if clicked outside panel
            //popup.setSize("500", "400");
            AddSchoolClassStudentPanel panel = new AddSchoolClassStudentPanel(DwoGlobalVars.instance().getCurrentUser(), control);
            control.setAddSchoolClassView(panel);
            panel.setPopup(popup);
            //panel.setSize("300", "200");
            popup.add(panel);
            popup.center();
            popup.show();
        } else if (event.getSource() == closeBtn) {
            LOG.log(Level.INFO, "Done, hiding window.");
            popup.hide();
        } else if (event.getSource() == delBtn) {
            LOG.log(Level.INFO, "" + event.getSource());
        }
        LOG.log(Level.INFO, event.getSource().toString());
    }

    public void setSchoolClasses(List<DomSchoolClass> schoolClasses) {
        List<DomSchoolClass> list = dataProvider.getList();
        list.clear();
        for (DomSchoolClass schoolClass : schoolClasses) {
            list.add(schoolClass);
        }
    }
}
