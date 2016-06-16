package nl.uu.fi.dwo.account.client;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomSchoolRoleAndClass;
import fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.locale.DwoLocalesForGWT;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.icons.AccountImageBundle;

/**
 *
 * @author G.A.J. van der Plas
 */
public class SchoolLoginPanel extends VerticalPanel implements ClickHandler {

    Logger LOG = Logger.getLogger("Account");

    SchoolLoginController control;
    PopupPanel popup;
    private Button delBtn;
    private Button addBtn;
    private Button closeBtn;
    CellTable<DomSchoolRoleAndClass> table = new CellTable<DomSchoolRoleAndClass>();
    ListDataProvider<DomSchoolRoleAndClass> dataProvider = new ListDataProvider<DomSchoolRoleAndClass>();

    public PopupPanel getPopup() {
        return popup;
    }

    public void setPopup(PopupPanel popup) {
        this.popup = popup;
    }

    SchoolLoginPanel(DomUserFull user) throws Dwo2Exception {
        init(user);
        control = new SchoolLoginController(this, user);
        control.init(user);
    }

    protected void init(DomUserFull user) {
        this.setSize("400", "500");
        //
        CellTable<DomSchoolRoleAndClass> table = new CellTable<DomSchoolRoleAndClass>();
        // Create name column.
        TextColumn<DomSchoolRoleAndClass> schoolColumn = new TextColumn<DomSchoolRoleAndClass>() {
            @Override
            public String getValue(DomSchoolRoleAndClass data) {
                return data.getSchoolName();
            }
        };
        TextColumn<DomSchoolRoleAndClass> roleColumn = new TextColumn<DomSchoolRoleAndClass>() {
            @Override
            public String getValue(DomSchoolRoleAndClass data) {
                return data.getRoleName();
            }
        };

        schoolColumn.setSortable(true);

        Column<DomSchoolRoleAndClass, ImageResource> loginColumn
                = new Column<DomSchoolRoleAndClass, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(DomSchoolRoleAndClass object) {
                return AccountImageBundle.instance.student();
            }
        };
       Column<DomSchoolRoleAndClass, ImageResource> deleteColumn
                = new Column<DomSchoolRoleAndClass, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(DomSchoolRoleAndClass object) {
                return AccountImageBundle.instance.delete();
            }
        };                

        CellPreviewEvent.Handler<DomSchoolRoleAndClass> cellPreviewHandler = new CellPreviewEvent.Handler<DomSchoolRoleAndClass>() {
            @Override
            public void onCellPreview(CellPreviewEvent<DomSchoolRoleAndClass> event) {
                int rowIndex = event.getIndex();
                int columnIndex = event.getColumn();
                int button = event.getNativeEvent().getButton();
                NativeEvent nativeEvent = event.getNativeEvent();
                if ("click".equals(nativeEvent.getType())
                        //                       && columnIndex == 0 // klik op rijnummer doet selectie
                        && button == NativeEvent.BUTTON_LEFT) {
                    LOG.log(Level.INFO, "x,y:" + rowIndex + "," + columnIndex + ":" + event.getSource());
                    DomSchoolRoleAndClass sc = dataProvider.getList().get(rowIndex);
                    switch (rowIndex) {
                        case 2: //relogin with schoolclass set...
                            control.switchToSchoolLogin(sc, new AsyncCallback<DomSchoolRoleAndClass>() {
                                @Override
                                public void onFailure(Throwable t) {
                                    //fail and reset all the data.
                                    Window.alert(t.getMessage());
                                    //TODO Wim
                                    Window.alert("wim handles error here.");
                                }

                                @Override
                                public void onSuccess(DomSchoolRoleAndClass result) {
                                    //TODO Wim
                                    Window.alert("wim calls a new login here.");
                                }
                            });
                            break;
                        case 3:     //remove schoolclass and relogin if it was the active schoolclass.
//                            if (sc.getId().equals(DwoGlobalVars.instance().getCurrentSchoolClass().getId())) {
                            control.removeASchoolLogin(sc, new AsyncCallback<Boolean>() {
                                @Override
                                public void onFailure(Throwable t) {
                                    //fail and reset all the data.
                                    Window.alert(t.getMessage());
                                    //TODO Wim
                                    Window.alert("wim handles error here.");
                                }

                                @Override
                                public void onSuccess(Boolean result) {
                                    //TODO update table.
                                    
                                    //TODO Wim
                                    Window.alert("wim calls a new login here in case new.");
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
        table.addColumn(schoolColumn, DwoLocalesForGWT.instance.GUI_SchoolName());
        table.addColumn(schoolColumn, DwoLocalesForGWT.instance.GUI_RoleName());
        table.addColumn(loginColumn, DwoLocalesForGWT.instance.GUI_Login());
        table.addColumn(deleteColumn, DwoLocalesForGWT.instance.GUI_Delete());
        dataProvider.addDataDisplay(table);

        VerticalPanel vPanel = new VerticalPanel();
        vPanel.add(table);               
    }

    @Override
    public void onClick(ClickEvent event) {
        //logger.log(Level.INFO, "object {0}", new Object[]{event.getSource()});
        Window.alert(event.getSource().toString());
        if (event.getSource() == this.closeBtn) {
            Window.alert("CANCEL!");
            popup.hide();
        } else if (event.getSource() == this.addBtn) {
            Window.alert("OK!");
            popup.hide();
        }
    }

    void update(DomSchoolsRolesAndClasses srcs) {
        //reinitiale the table.
       Grid g = new Grid(5, 3);
        // Put some values in the grid cells.
        g.setText(0, 0, "school");
        g.setText(0, 1, "login");
        g.setText(0, 2, "delete");
        TextBox login = new TextBox();
        login.setText(DwoGlobalVars.instance().getCurrentUser().getUserName());
        g.setWidget(1, 0, login);
        TextBox name = new TextBox();
        name.setText(DwoGlobalVars.instance().getCurrentUser().getUniqueDisplayName());
        g.setWidget(1, 1, name);
        TextBox delete = new TextBox();
        delete.setText("X");
        g.setWidget(1, 2, delete);

        // Just for good measure, let's put a button in the center.
        addBtn = new Button(DwoLocalesForGWT.instance.GUI_NewSchoolLogin());
        addBtn.addClickHandler(this);
        g.setWidget(2, 0, addBtn);
        closeBtn = new Button(DwoLocalesForGWT.instance.GUI_Button_Cancel());
        closeBtn.addClickHandler(this);
        g.setWidget(2, 1, closeBtn);
        this.add(g);    }
}
