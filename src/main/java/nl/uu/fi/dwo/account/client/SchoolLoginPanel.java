package nl.uu.fi.dwo.account.client;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.account.client.icons.AccountImageBundle;

/**
 *
 * @author G.A.J. van der Plas
 */
public class SchoolLoginPanel extends VerticalPanel implements ClickHandler {

    private final class HideAndReset<T> implements Success<T, Void> {
		@Override
		public Promise<Void> call(Promise<T> notused) throws Exception {
			popup.hide();
			resetLogin.execute();;
			return null;
		}
	}

    private final class RestFailure implements Failure {

		@Override
		public void fail(Promise<?> resolved) throws Exception {
			Throwable t = resolved.getFailure();
            Window.alert(t.getMessage()); // FIXME betere foutmelding
            LOG.log(Level.WARNING, "failure", t);
		}
    	
    }
    
	Logger LOG = Logger.getLogger("SchoolLoginPanel");

    SchoolLoginController control;
    PopupPanel popup;
    private Button delBtn;
    private Button addBtn;
    private Button closeBtn;
    CellTable<DomSchoolRoleAndClass> table = new CellTable<DomSchoolRoleAndClass>();
    ListDataProvider<DomSchoolRoleAndClass> dataProvider = new ListDataProvider<DomSchoolRoleAndClass>();

	private Command resetLogin;

    public PopupPanel getPopup() {
        return popup;
    }

    public void setPopup(PopupPanel popup) {
        this.popup = popup;
    }

    SchoolLoginPanel(Command resetLogin, DomUserFull user) throws Dwo2Exception {
        init(user);
        this.resetLogin = resetLogin;
        control = new SchoolLoginController(this, user);
        //control.init(user); dubbel!
    }

    protected void init(DomUserFull user) {
        //this.setSize("400", "500");
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
                        	control.switchToSchoolLogin(sc)
                        		.then(new HideAndReset<DomSchoolRoleAndClass>(), new RestFailure());
                        	
                              break;
                        case 3:     //remove school and relogin if it was the active schoolclass.
//                            if (sc.getId().equals(DwoGlobalVars.instance().getCurrentSchoolClass().getId())) {
                            control.removeASchoolLogin(sc)
                    			.then(new HideAndReset<Boolean>(), new RestFailure());
                          break;
                        default:
                    }
                }
            }
        };
        table.addCellPreviewHandler(cellPreviewHandler);

        // Add the columns.
        table.addColumn(schoolColumn, DwoLocalesForGWT.instance.GUI_SchoolName());
        table.addColumn(roleColumn, DwoLocalesForGWT.instance.GUI_RoleName());
        table.addColumn(loginColumn, DwoLocalesForGWT.instance.GUI_Login());
        table.addColumn(deleteColumn, DwoLocalesForGWT.instance.GUI_Delete());
        dataProvider.addDataDisplay(table);

        VerticalPanel vPanel = new VerticalPanel();
        vPanel.add(table);               
    }

    @Override
    public void onClick(ClickEvent event) {
        //logger.log(Level.INFO, "object {0}", new Object[]{event.getSource()});
        if (event.getSource() == this.closeBtn) {
            popup.hide();
        } else if (event.getSource() == this.addBtn) {
            Window.alert("ADD!");
            popup.hide();
        }
    }

    void update(DomSchoolsRolesAndClasses srcs) {
    	// FIXME Gert?
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
        this.add(g);
    }
}
