package nl.uu.fi.dwo.mobile.client.ui.activities;

import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView.Presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

public class LoginActivity extends MGWTAbstractActivity
{
	ClientFactory clientFactory;
	private LoginView view;

	public LoginActivity(ClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		view = clientFactory.getLoginView();
		view.setupModule((Presenter) view);
		panel.setWidget(view);
	}

	@Override
	public void onStop() {
		super.onStop();
		view.setupModule(null);
	}

}
