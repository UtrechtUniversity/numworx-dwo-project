package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.NoCourseView;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

public class NoCourseActivity extends AbstractActivity {

	@Inject Provider<NoCourseView> noCourseView;
	@Inject @Named("defaultPlace") Place defaultPlace;
	private HeaderView header;
	@Inject NoCourseActivity(HeaderView header) {
		super();
		this.header = header;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		NoCourseView w = noCourseView.get();
		panel.setWidget(w);
		w.setHomePlace(new TreeModulePlace());
		String text = "Geen toegang";
		//text = DwoLocalesForGWT.instance.NO_ACCESS();
		w.setTitle(new SafeHtmlBuilder().appendEscaped(text).toSafeHtml());
		//w.fail(new Dwo2Exception(Dwo2ExceptionCode.Rest_ResourceNotFound, "Not Found"));
		//w.render();
		header.show(); // navigate.show
	}

}
