package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.SecureMode;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.views.ExamModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.UnSafeModuleView;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import dagger.MembersInjector;
import dagger.Reusable;

public class ExamModuleActivity implements Activity, ExamModuleView.Presenter {


	@Inject ClientFactory clientFactory;
	@Inject DWOplayerParameters PARAMETERS;
	@Inject Provider<UnSafeModuleView> unsafe;
	private SelectModuleItem item;
	private Activity delegate;
	private Provider<? extends Activity> provider;
	private AcceptsOneWidget panel;
	private EventBus bus;
	final private boolean skipPassword;

	@Reusable
	public static class Factory {
	  @Inject MembersInjector<ExamModuleActivity> injector;
	  @Inject Factory() {}
	  public ExamModuleActivity create(SelectModuleItem item, Provider<? extends Activity> provider) {
	    return new ExamModuleActivity(item, provider, injector);
	  }
	}
	
		
	ExamModuleActivity(SelectModuleItem i, Provider<? extends Activity> provider, MembersInjector<ExamModuleActivity> injector) {
	  injector.injectMembers(this);
	  this.item = i;
	  this.provider = provider;
	  this.skipPassword = item.getType() == SelectModuleItem.Type.SCO;
	}
	
//	public ExamModuleActivity(ClientFactory factory, SelectModuleItem i) {
//		this(factory, i, null, false);
//		provider = new Provider<Activity>() {
//
//			@Override
//			public Activity get() {
//				return new TreeModuleActivity(clientFactory, item);
//			}
//		};
//	}
	
	
	
	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		if(SecureMode.NORMAL == PARAMETERS.getSecureMode()) {
			final UnSafeModuleView w = unsafe.get();
			w.selectItem(item);
			clientFactory.barrier().onResolve(		
			new Runnable() {
				public void run() {
					panel.setWidget(w);
				}
			});
		} else {
			this.panel = panel;
			this.bus = eventBus;
			if(skipPassword && clientFactory.inExam(item.getClassCourse()))
			{
				delegate = provider.get();
				delegate.start(panel, eventBus);
			} else {
				ExamModuleView view = new ExamModuleView(clientFactory.getHeaderView());
				view.selectItem(item);
				view.setPresenter(this);
				panel.setWidget(view);
			}
		}
	}


	@Override
	public void onOk(String password, final ExamModuleView view) {
		Success<? super List<SelectModuleItem>, Void> success;
		Failure failed;
		
		failed = new Failure() {

			@Override
			public void fail(Promise<?> resolved) throws Exception {
				view.showFailure(resolved.getFailure());			
			}};
		
		success = new Success<Object, Void>() {

			@Override
			public Promise<Void> call(Promise<Object> resolved)
					throws Exception {
				delegate = provider.get();
				delegate.start(panel, bus);
				return null;
			}
		};

		
		
		clientFactory.startExam(item.getClassCourse(), password)
		.then(new Success<Void, List<SelectModuleItem>>(){

			@Override
			public Promise<List<SelectModuleItem>> call(Promise<Void> resolved)
					throws Exception {
				Promise<List<SelectModuleItem>> promise = item.getChildrenAsync();
				if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
					promise = clientFactory.getRPCHandler().getScos(item.getID())
							.map(new SCO_TO_MODULEITEM(item));
					item.setChildrenAsync(promise);
				}
				return promise;
			}})
		.then(success, failed);
		
	}

	public void onKO() {
		History.back();
	}


	/* (non-Javadoc)
	 * @see com.google.gwt.activity.shared.AbstractActivity#mayStop()
	 */
	@Override
	public String mayStop() {
		if(delegate != null)
			return delegate.mayStop();
		return null;
	}


	/* (non-Javadoc)
	 * @see com.google.gwt.activity.shared.AbstractActivity#onCancel()
	 */
	@Override
	public void onCancel() {
		if(delegate != null)
			delegate.onCancel();
	}


	/* (non-Javadoc)
	 * @see com.google.gwt.activity.shared.AbstractActivity#onStop()
	 */
	@Override
	public void onStop() {
		if(delegate != null)
			delegate.onStop();
	}
	
}
