package nl.uu.fi.dwo.account.client;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;

import nl.uu.fi.dwo.account.client.icons.AccountImageBundle;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

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
    
    /**
     *
     */
    Logger LOG = Logger.getLogger("SchoolLoginPanel");

    /**
     *
     */
    SchoolLoginController control;

    /**
     *
     */
    PopupPanel popup;
    private Button delBtn;
    private Button addBtn;
    private Button closeBtn;

    /**
     *
     */
    CellTable<DomSchoolRoleAndClassV2> table = new CellTable<DomSchoolRoleAndClassV2>();

    /**
     *
     */
    ListDataProvider<DomSchoolRoleAndClassV2> dataProvider = new ListDataProvider<DomSchoolRoleAndClassV2>();

	private Command resetLogin;

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
     * @throws Dwo2Exception
     */
    SchoolLoginPanel(Command resetLogin, DomUserFull user) throws Dwo2Exception {
        init(user);
        this.resetLogin = resetLogin;
        control = new SchoolLoginController(this, user);
        //control.init(user); dubbel!
    }

    /**
     *
     * @param user
     */
    protected void init(DomUserFull user) {
        //this.setSize("400", "500");
        //
        // Create name column.
        TextColumn<DomSchoolRoleAndClassV2> schoolColumn = new TextColumn<DomSchoolRoleAndClassV2>() {
            @Override
            public String getValue(DomSchoolRoleAndClassV2 data) {
                return data.getSchool().getSchoolName();
            }
        };
        TextColumn<DomSchoolRoleAndClassV2> roleColumn = new TextColumn<DomSchoolRoleAndClassV2>() {
            @Override
            public String getValue(DomSchoolRoleAndClassV2 data) {
                return data.getRole().getRoleName();
            }
        };

        schoolColumn.setSortable(true);

        Column<DomSchoolRoleAndClassV2, ImageResource> loginColumn
                = new Column<DomSchoolRoleAndClassV2, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(DomSchoolRoleAndClassV2 object) {
                return AccountImageBundle.instance.student();
            }
        };
       Column<DomSchoolRoleAndClassV2, ImageResource> deleteColumn
                = new Column<DomSchoolRoleAndClassV2, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(DomSchoolRoleAndClassV2 object) {
                return AccountImageBundle.instance.delete();
            }
        };                

        
        
        
        
        CellPreviewEvent.Handler<DomSchoolRoleAndClassV2> cellPreviewHandler = new CellPreviewEvent.Handler<DomSchoolRoleAndClassV2>() {
            @Override
            public void onCellPreview(CellPreviewEvent<DomSchoolRoleAndClassV2> event) {
                int rowIndex = event.getIndex();
                int columnIndex = event.getColumn();
                int button = event.getNativeEvent().getButton();
                NativeEvent nativeEvent = event.getNativeEvent();
                if ("click".equals(nativeEvent.getType())
                        //                       && columnIndex == 0 // klik op rijnummer doet selectie
                        && button == NativeEvent.BUTTON_LEFT) {
                    LOG.log(Level.INFO, "x,y:" + rowIndex + "," + columnIndex + ":" + event.getSource());
                    DomSchoolRoleAndClassV2 sc = dataProvider.getList().get(rowIndex);
                    switch (rowIndex) {
                        case 2: //relogin with schoolclass set...
                        	control.switchToSchoolLogin(sc)
                        		.then(new HideAndReset<DomSchoolRoleAndClassV2>(), new RestFailure());
                        	
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
            //popup.hide();
        }
    }

    /**
     *
     * @param srcs
     */
    void update(DomSchoolsRolesAndClassesV2 srcs) {
    	List<DomSchoolRoleAndClassV2> list = dataProvider.getList();
    	list.clear();
    	//current = srcs.getActiveSchoolRoleAndClass().getHasRole();
    	list.addAll(srcs.getSchoolsRolesAndClassesList());
    	
    
    }
}
