package nl.uu.fi.dwo.account.client;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

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

    /**
     *
     */
    CellTable<DomSchoolClass> table = new CellTable<DomSchoolClass>();

    /**
     *
     */
    ListDataProvider<DomSchoolClass> dataProvider = new ListDataProvider<DomSchoolClass>();

    /**
     *
     */
    Command resetLogin;

    private HandlerRegistration registration;

    private Failure failure;

    private DwoGlobalVars vars;

    /**
     *
     * @return
     */
    public PopupPanel getPopup() {
        return popup;
    }

    /**
     *
     * @param popup
     */
    public void setPopup(PopupPanel popup) {
        this.popup = popup;
    }

    /**
     *
     * @param resetLogin
     * @param user
     * @param failure 
     */
    SchoolClassStudentPanel(Command resetLogin, DwoGlobalVars vars, DomContext context, Failure failure) {
        this.resetLogin = resetLogin;
        this.failure = failure;
        this.vars = vars;
        init(vars.getCurrentUser());
        control = new SchoolClassStudentController(this, vars.getCurrentUser(), context, failure);
    }

    private boolean isCurrentSchoolClass(DomSchoolClass object) {
        return vars.getCurrentSchoolClass() != null
                && object != null
                && object.getId().equals(vars.getCurrentSchoolClass().getId());
    }

    /**
     *
     * @param user
     */
    public void init(DomUserFull user) {
        table = new CellTable<DomSchoolClass>();
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
                if (isCurrentSchoolClass(object)) {
                    return null;
                }
                return AccountImageBundle.instance.student();
            }

        };
        Column<DomSchoolClass, ImageResource> deleteColumn
                = new Column<DomSchoolClass, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(DomSchoolClass object) {
                if (vars.getActiveSchoolRoleAndClass().getSchool().studentsCanRegisterForSchoolClasses()) {
                    return AccountImageBundle.instance.delete();
                } else {
                    return AccountImageBundle.instance.empty();
                }

            }
        };

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
                        case 1:
                            if (isCurrentSchoolClass(sc)) {
                                return;
                            }
                            deregister(); // remove popup, select currentclass by hand
                            popup.hide();
                            control.setActiveSchoolClass(sc).then(p-> {resetLogin.execute();return null;}, failure);
                            break;
                        case 2:     //remove schoolclass and relogin if it was the active schoolclass.
                            if (vars.getActiveSchoolRoleAndClass().getSchool().studentsCanRegisterForSchoolClasses()) {
                                
                                popup.hide();
                                control.removeSchoolClass(sc)
                                .then(p -> control.getActiveSchoolClass())
                                .recoverWith(fail ->  {
                                  Throwable t = fail.getFailure();
                                  if (t instanceof Dwo2Exception) {
                                    Dwo2ExceptionCode code = ((Dwo2Exception) t).getDwo2Code();
                                    if (code == Dwo2ExceptionCode.Rest_Active_SchoolClass_Not_Set) {            
                                      return Promises.resolved(null);
                                  }       }    
                                  return (Promise<? extends DomSchoolClass>)fail;
                                })
                                .then(p -> {
                                  DomSchoolClass result = p.getValue();
                                  DomSchoolClass current = vars.getCurrentSchoolClass();
                                  if (result != current
                                          && (result == null
                                          || current == null
                                          || !result.getId().equals(current.getId()))) {
                                      deregister();
                                      resetLogin.execute();
                                      // no need to update schoolclassses.
                                  } else {
                                      control.updateStudentsSchoolClassesInView();
                                  }                              
                                  return p;
                                },failure);
                            }
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
        if (vars.getActiveSchoolRoleAndClass().getSchool().studentsCanRegisterForSchoolClasses()) {
        table.addColumn(deleteColumn, DwoLocalesForGWT.instance.GUI_Delete());
        }
        dataProvider.addDataDisplay(table);

        VerticalPanel vPanel = new VerticalPanel();
        vPanel.add(table);

        HorizontalPanel hPanel = new HorizontalPanel();
        vPanel.setHorizontalAlignment(HorizontalAlignmentConstant.endOf(Direction.DEFAULT));
        hPanel.setHorizontalAlignment(HorizontalAlignmentConstant.endOf(Direction.DEFAULT));
//            hPanel.getElement().getStyle().setPadding(20, Unit.PX);
        closeBtn = new Button("Close");
        closeBtn.addClickHandler(this);

        addBtn = new Button("Add");
        if (vars.getActiveSchoolRoleAndClass().getSchool().studentsCanRegisterForSchoolClasses()) {
            addBtn.setVisible(true);
        } else {
            addBtn.setVisible(false);
        }

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
            final PopupPanel popup = new PopupPanel(true);//hide if clicked outside panel
            //popup.setSize("500", "400");
            AddSchoolClassStudentPanel panel = new AddSchoolClassStudentPanel(vars.getCurrentUser(), control);
            control.setAddSchoolClassView(panel);
            panel.setPopup(popup);
//            panel.setPixelSize(-1,200);
//            final ScrollPanel scrollPanel = new ScrollPanel();
//            scrollPanel.add(panel);
//            scrollPanel.setPixelSize(-1, 400);
//            popup.add(scrollPanel);
            popup.add(panel);
//            popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
//                @Override
//                public void setPosition(int offsetWidth, int offsetHeight) {
//                    scrollPanel.setPixelSize(-1, 2*(Window.getClientHeight() - offsetHeight) / 3);
//                    int left = (Window.getClientWidth() - offsetWidth) / 3;
//                    int top = (Window.getClientHeight() - offsetHeight) / 3;
//                    popup.setPopupPosition(left, top);
//                }
//            });
            popup.center();

// If current school class is empty, and we have available classes, relogin            
            if (vars.getCurrentSchoolClass() == null && registration == null) {
                registration = this.popup.addCloseHandler(new CloseHandler<PopupPanel>() {

                    @Override
                    public void onClose(CloseEvent<PopupPanel> event) {
                        registration.removeHandler();
                        registration = null;
                        if (table.getRowCount() > 0) {
                            resetLogin.execute();
                        }
                    }
                });
            }
            popup.show();
        } else if (event.getSource()
                == closeBtn) {
            LOG.log(Level.INFO, "Done, hiding window.");
            popup.hide();
        } else if (event.getSource()
                == delBtn) {
            LOG.log(Level.INFO, "" + event.getSource());
        }

        LOG.log(Level.INFO, event.getSource().toString());
    }

    /**
     *
     * @param schoolClasses
     */
    public void setSchoolClasses(List<DomSchoolClass> schoolClasses) {
        List<DomSchoolClass> list = dataProvider.getList();
        list.clear();
        for (DomSchoolClass schoolClass : schoolClasses) {
            list.add(schoolClass);
        }
    }

    private void deregister() {
        if (registration != null) {
            registration.removeHandler();
        }
        registration = null;
    }
}
