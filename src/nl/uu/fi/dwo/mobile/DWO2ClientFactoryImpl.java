package nl.uu.fi.dwo.mobile;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.SecureMode;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWO4;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWO5;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.client.ui.ConfirmEventHandler;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderViewNone;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleViewNumworx;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewBuilder;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@SuppressWarnings("deprecation")
@Singleton
public final class DWO2ClientFactoryImpl extends ClientFactoryImpl {
  
        @Inject TrafficAgent agent;
		@Inject Lazy<ConfirmEventHandler> confirmHandler;
	    final Provider<? extends TreeModuleView> treeModuleViewProvider;
	    TreeModuleView treeModuleView;
	    final private DwoGlobalVars instance;
	    final private DWOplayerParameters PARAMETERS;

		@Inject DWO2ClientFactoryImpl(EventBus bus, PlaceController controller,
            Provider<PlaceHistoryMapper> mapper,
            Provider<HeaderViewNone> none,
            @Named("header") Provider<HeaderView> numworx,
		    Provider<ViewModuleViewBuilder> entry, RPCHandler rpcHandler,
		    Provider<TreeModuleViewNumworx> view,
		    DwoGlobalVars vars,
		    DWOplayerParameters params
		    ) {
              super(bus, controller, entry);
              treeModuleViewProvider = view;
              instance = vars;
              PARAMETERS = params;
              setRPCHandler(rpcHandler);
              setup(none,numworx);
        }

    @Override
		public void addBarrier(Promise<?> p) {
			agent.addBarrier(p);
		}

        @Override
        public TreeModuleView getTreeModuleView()
        {
          if (treeModuleView == null)
            return treeModuleView = treeModuleViewProvider.get();
          return treeModuleView;
        }

		@Override
		public Promise<Void> barrier() {
			return agent.barrier();
		}

		@Override
		public Promise<Void> logout() {
			return barrier().
					then(new Success<Void,Void>(){

						@Override
						public Promise<Void> call(Promise<Void> resolved) throws Exception {
//								menuWidget = null;
							if(withUser()) {
								return getRPCHandler().logout();
							}
							return resolved;
						}}).
					then(new Success<Void,Void>() {

						@Override
						public Promise<Void> call(Promise<Void> resolved) throws Exception {
							treeModuleView = null;
							return null;
						}});
		}

		public SCORM_guest setupAPI() {
			SCORM_guest api;
			if(!withUser()) {
				api = new SCORM_guest();
			} else {
// secure alleen voor studenten!
				boolean secure = PARAMETERS.getSecureMode() == SecureMode.SEB && RoleType.STUDENT == getRoleType();
                api = new SCORM_DWO5(getSchoolClass(),
						instance.getActiveSchoolRoleAndClass().getHasRole(),
						agent,
						secure,
						getEventBus(),
						getConfirmHandler());
			}
			return api;
		}

		private Lazy<ConfirmEventHandler> getConfirmHandler() {
			
			return confirmHandler;
		}

		@Override
		public boolean withUser() {
			return instance.getCurrentUser() != null;
		}

		@Override
		public DomSchool getSchool() {
			try {
				return instance.getActiveSchoolRoleAndClass().getSchool();
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public DomSchoolClass getSchoolClass() {
			return instance.getCurrentSchoolClass();
		}

		@Override
		public boolean isIconizer() {
			try {
				return getSchoolClass().getIconizer().booleanValue();
			} catch (Exception e) {
				return true;
			}
		}

		@Override
		public RoleType getRoleType() {
			try {
				String roleName = instance.getActiveSchoolRoleAndClass().getRole().getRoleName();
				return RoleType.valueOf(roleName);
			} catch (Exception e) {
				return RoleType.ANONYMOUS;
			}
		}

		@Override
		public Object getUserID() {
			PersistenceId id = instance.getCurrentUser().getId();
			return PersistenceIdDecoderInterface.instance.idOf(id, PersistenceClassType.PersistentUser);
		}

		@Override
		public boolean isPremium() {
			//return withUser() && getSchool().getAboType() == AboType.premium;
			return !withUser() || getSchool().getAboType() == AboType.premium;
		}

	}