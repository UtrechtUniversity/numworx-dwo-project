package nl.uu.fi.dwo.account.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import fi.dwo.gwt.lib.rest.CallManagers.MD5;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManager;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolLogin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionInterface;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import java.util.logging.Logger;

import org.osgi.util.function.Predicate;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

/**
 *
 * @author Gert van der Plas
 */
public class SchoolLoginController {

    private static final Logger LOG = Logger.getLogger(SchoolLoginController.class.getName());

    private SchoolLoginPanel view = null;
    private SecuredUserSchoolLoginManager manager = new SecuredUserSchoolLoginManager();
    private DomSchoolsRolesAndClassesV2 srcs;

	private HandlerRegistration registration;

	private AddSchoolLoginPanel addSchoolPanel;

  private Failure failure;

    /**
     *
     * @return
     */
    public DomSchoolsRolesAndClassesV2 getSrcs() {
        return srcs;
    }

    /**
     *
     * @param view
     * @param user
     * @throws Dwo2Exception
     */
    public SchoolLoginController(SchoolLoginPanel view, DomUserFull user, Failure failure) throws Dwo2Exception {
        this.view = view;
        this.failure = failure;
        this.init(user);
    }

    /**
     *
     * @param user
     * @throws Dwo2Exception
     */
    public void init(DomUserFull user) throws Dwo2Exception {
        manager.getSchoolLogins().then(
            p -> { srcs = p.getValue(); view.update(srcs); return null; },
            p -> { view.init(DwoGlobalVars.instance().getCurrentUser());}
            );
//            @Override
//            public void onFailure(Throwable t) {
//                view.init(DwoGlobalVars.instance().getCurrentUser());
//            }
//
//            @Override
//            public void onSuccess(DomSchoolsRolesAndClassesV2 result) {
//                //success and set all the data in the view
//                srcs = result;
//                view.update(srcs);
//            }
 //       }
        
    }
    
//    /**
//     *
//     * @param callBack
//     */
//    public void getSchoolLogins(MethodCallback<DomSchoolsRolesAndClassesV2> callBack) {
//        manager.getSchoolLogins(callBack);
//    }
    
    /**
     *
     * @param sc
     * @return
     */
    public Promise<DomSchoolRoleAndClassV2> switchToSchoolLogin(final DomSchoolRoleAndClassV2 sc) {
    	try {
    		switch(RoleType.valueOf(sc.getRole().getRoleName())) {
    		case SCHOOLADMIN:
    		case ADMIN:
    			return inputPassword().then(new Success<String, DomSchoolRoleAndClassV2>() {

					@Override
					public Promise<DomSchoolRoleAndClassV2> call(Promise<String> t) {
		    		    if (t.getValue() == null || t.getValue().isEmpty())
		    		    	return null;
						return manager.switchToSchoolLogin(sc);
					}
				});
    			
    		default: // no password
    		    return manager.switchToSchoolLogin(sc);

    		}
    		
    	} catch (Exception e) {
    		return Promises.failed(e);
    	}
    	
    	
    	
    }

    /**
     *
     * @param reqSrac
     * @return
     */
    public Promise<Boolean> removeASchoolLogin(final DomSchoolRoleAndClassV2 reqSrac) {
		return	inputPassword().then(new Success<String, Boolean>() {

					@Override
					public Promise<Boolean> call(Promise<String> t) {
							if(t.getValue() == null || t.getValue().isEmpty()) // if empty password cancel
								return null;
							return manager.removeASchoolLogin(DwoGlobalVars.instance().getContext(), reqSrac);
					}
				});
    }

    // FIXME USE POPUP
	private Promise<String> inputPassword() {
		return inputPassword0()
				
		.filter(new Predicate<String>() {

			@Override
			public boolean test(String t) {
				if(t == null || t.isEmpty()) return true;
    			return  DwoGlobalVars.instance().getCurrentUser().getPassword().equals(t);
			}
		});
	}

	private Promise<String> inputPassword0() {
		FlowPanel p = new FlowPanel();
		p.add(new InlineLabel(DwoLocalesForGWT.instance.GUI_Label_Password()));
		final PasswordTextBox txt = new PasswordTextBox();
		p.add(txt);
		final Button btn = new Button("Ok");
		p.add(btn);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.setStyleName("numworx-popup");
		popup.setWidget(p);
		popup.setPixelSize(300, -1);
		final Deferred<String> d = new Deferred<String>();
		registration = btn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String password = txt.getText();
				if(password == null)
					password = "";
				if(!password.isEmpty())
					password = MD5.md5(password);
				popup.hide();
				registration.removeHandler();
				d.resolve(password);
			}});
		
		popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
                int left = (Window.getClientWidth() - offsetWidth) / 3 + 30;
                int top = (Window.getClientHeight() - offsetHeight) / 3 + 20;
                popup.setPopupPosition(left, top);
			}
		});
		Promise<String> resolved = d.getPromise();
		return resolved;
	}


	public void setAddSchoolLoginPanel(AddSchoolLoginPanel addSchoolLoginPanel) {
		addSchoolPanel = addSchoolLoginPanel;
		
	}

//	void failedAddSchoolLogin(Promise<?> p) throws Exception {
//		Throwable t = p.getFailure();
//		if( t instanceof Dwo2ExceptionInterface) {
//			Dwo2ExceptionInterface e = (Dwo2ExceptionInterface) t;
//			Dwo2ExceptionCode code = e.getDwo2Code();
//			String message = Dwo2ExceptionTranslator.getLocalizedCodeExplanation(null, code);
//			t = (new RuntimeException(message, t));
//		}
//        Window.alert(t.getMessage()); // FIXME betere foutmelding
//	}
	
	
	public void addASchoolLogin(DomNewSchoolLogin request) {
		PromiseCallback<Boolean> df = new PromiseCallback<>();
		manager.addASchoolLogin(DwoGlobalVars.instance().getContext(), request)
		.filter(p -> p.booleanValue()) // must be true
		.then(p -> {
			addSchoolPanel.hide();
			init(DwoGlobalVars.instance().getCurrentUser());
			return null;
		}, p -> addSchoolPanel.enable()).then(null, failure);
		
	}
}
