package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.List;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.SecureMode;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.views.ExamModuleView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ExamModuleActivity extends AbstractActivity implements ExamModuleView.Presenter {


	private ClientFactory clientFactory;
	private SelectModuleItem item;
	private TreeModuleActivity delegate;
	private AcceptsOneWidget panel;
	private EventBus bus;


	public ExamModuleActivity(ClientFactory clientFactory, SelectModuleItem i)
	{
		this.clientFactory = clientFactory;
		this.item = i;
	}

	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		if(SecureMode.NORMAL == DWOplayer.PARAMETERS.getSecureMode()) {
			Widget w = new HTML(
					"<h1>Dit is een toets</h1>"
					+ "Ga naar de <a href='/toets/'>beveiligde toets omgeving</a>"
					+ " als je deze toets wilt maken"
					);
			panel.setWidget(w);
			return;
		} else {
			this.panel = panel;
			this.bus = eventBus;
			ExamModuleView view = new ExamModuleView();
			view.selectItem(item);
			view.setPresenter(this);
			panel.setWidget(view);
		}
	}


	@Override
	public void onOk(String password, final ExamModuleView view) {
		clientFactory.startExam(item.getClassCourse(), password);

		Promise<List<SelectModuleItem>> promise = item.getChildrenAsync();
		if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
			promise = DWOplayer.clientfactory.getRPCHandler().getScos(item.getID())
					.map(new SCO_TO_MODULEITEM(item));
			item.setChildrenAsync(promise);
		}
				
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
				delegate = new TreeModuleActivity(clientFactory, item);
				delegate.start(panel, bus);
				return null;
			}
		};
			
		promise.then(success, failed);
		
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
		return super.mayStop();
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
