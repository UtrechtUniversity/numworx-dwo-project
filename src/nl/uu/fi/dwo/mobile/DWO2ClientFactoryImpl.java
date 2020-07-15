package nl.uu.fi.dwo.mobile;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
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
import nl.uu.fi.dwo.mobile.client.ui.views.Login3ViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationViewNumworx;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewBuilder;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Singleton
public final class DWO2ClientFactoryImpl extends ClientFactoryImpl {
  
        @Inject TrafficAgent agent;
        @Inject PlaceHistoryHandler placeHistoryHandler;
		@Inject Lazy<ConfirmEventHandler> confirmHandler;

		@Inject DWO2ClientFactoryImpl(EventBus bus, PlaceController controller,
            Provider<PlaceHistoryMapper> mapper,
            Provider<HeaderViewNone> none,
            @Named("header") Provider<HeaderView> numworx,
            Provider<NavigationViewNumworx> navigation,
		    Provider<ViewModuleViewBuilder> entry, RPCHandler rpcHandler
		    ) {
              super(bus, controller, mapper, navigation, entry);
              setRPCHandler(rpcHandler);
              setup(none,numworx);
        }

    @Override
		public void addBarrier(Promise<?> p) {
			agent.addBarrier(p);
		}

		@Override
		public Promise<Void> barrier() {
			return agent.barrier();
		}

		@Override
		public LoginView getLoginView()
		{
			if (loginView == null) loginView = new Login3ViewImpl();
			return loginView;
		}

		private Promise<Void> superLogout() {
			return super.logout();
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
							return superLogout();
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
				boolean secure = DWOplayer.PARAMETERS.getSecureMode() == SecureMode.SEB && RoleType.STUDENT == getRoleType();
                api = new SCORM_DWO5(getSchoolClass(),
						DwoGlobalVars.instance().getActiveSchoolRoleAndClass().getHasRole(),
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
			return DwoGlobalVars.instance().getCurrentUser() != null;
		}

		@Override
		public DomSchool getSchool() {
			try {
				return DwoGlobalVars.instance().getActiveSchoolRoleAndClass().getSchool();
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public DomSchoolClass getSchoolClass() {
			return DwoGlobalVars.instance().getCurrentSchoolClass();
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
				String roleName = DwoGlobalVars.instance().getActiveSchoolRoleAndClass().getRole().getRoleName();
				return RoleType.valueOf(roleName);
			} catch (Exception e) {
				return RoleType.ANONYMOUS;
			}
		}

		@Override
		public Object getUserID() {
			PersistenceId id = DwoGlobalVars.instance().getCurrentUser().getId();
			return PersistenceIdDecoderInterface.instance.idOf(id, PersistenceClassType.PersistentUser);
		}

		@Override
		public boolean isPremium() {
			//return withUser() && getSchool().getAboType() == AboType.premium;
			return !withUser() || getSchool().getAboType() == AboType.premium;
		}

		/* (non-Javadoc)
		   * @see nl.uu.fi.dwo.mobile.client.ui.ClientFactory#getPlaceHistoryHandler()
		   */
		  @Override
		  public PlaceHistoryHandler getPlaceHistoryHandler() {
		    return placeHistoryHandler;
		  }

	}