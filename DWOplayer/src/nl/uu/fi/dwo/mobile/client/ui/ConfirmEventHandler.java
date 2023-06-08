package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.util.promise.Promise;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import fi.dwo.gwt.lib.rest.ui.ConfirmDialogEvent;
import fi.dwo.gwt.lib.rest.ui.ConfirmDialogEvent.EventType;
import fi.dwo.gwt.lib.rest.ui.ConfirmDialogEventHandler;
import fi.dwo.gwt.lib.rest.ui.ConfirmDialogPromise;
import nl.uu.fi.dwo.mobile.client.ui.views.ConfirmView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.DWOPopupPanel;
import nl.uu.fi.dwo.mobile.utils.PopupFacade.PopupListener;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
/**
 * Even het wiel opnieuw uitgevonden. MessageDialog was er al. FIXME samenvoegen.
 * @author peterboon
 * @deprecated use MessageDialog
 */
@Singleton // maar 1 op de bus.
public class ConfirmEventHandler implements ConfirmDialogEventHandler {

	private Lazy<ConfirmView> view;

	@Inject ConfirmEventHandler(EventBus bus, Lazy<ConfirmView> view) {
		bus.addHandler(ConfirmDialogEvent.TYPE, this);
		this.view = view;
	}

	@Override
	public void onDialogEvent(ConfirmDialogEvent dialogEvent) {
		String message = dialogEvent.getPromise().getMsg();
		ConfirmView confirmView = view.get();
		confirmView.setText(message); 
		HandlerRegistration r1 = confirmView.getConfirm().addClickHandler(ev -> dialogEvent.getPromise().resolve(Boolean.TRUE));
		HandlerRegistration r2 = confirmView.getCancel().addClickHandler(ev -> dialogEvent.getPromise().resolve(Boolean.FALSE));
		PopupListener popupListener = new PopupListener() {
			
			@Override
			public void onShow() {}
			
			@Override
			public void onHide() {
				r1.removeHandler();
				r2.removeHandler();
				if (! dialogEvent.getPromise().getPromise().isDone())
					dialogEvent.getPromise().resolve(Boolean.FALSE);			
			}
		};
		DWOPopupPanel popup = new DWOPopupPanel(DwoLocalesForGWT.instance.NUM_DLG_User_Alert(), popupListener);
		popup.addContent(confirmView);
		popup.center();
		dialogEvent.getPromise().getPromise().onResolve(() -> {popup.hide(); popupListener.onHide(); } );
	}


	public Promise<Boolean> confirm(String message) {		
		ConfirmDialogPromise aPromise = new ConfirmDialogPromise(message);
		ConfirmDialogEvent ev = new ConfirmDialogEvent(EventType.ConfirmDialog, aPromise);
		onDialogEvent(ev);
		return aPromise.getPromise();
	}
}
