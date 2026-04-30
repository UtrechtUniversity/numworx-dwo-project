/**
 * 
 */
package nl.uu.fi.dwo.mobile.client.ui.views;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author wim
 *
 */
public class NoCourseView extends ResizeComposite {

	private static NoCourseViewUiBinder uiBinder = GWT.create(NoCourseViewUiBinder.class);

	interface NoCourseViewUiBinder extends UiBinder<Widget, NoCourseView> {
	}

	private HeaderView header;
	private NavigationView navigation;

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	  *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	@Inject public NoCourseView(HeaderView header, NavigationView navigation) {
		initWidget(uiBinder.createAndBindUi(this));
		this.header = header;
		this.navigation = navigation;
	}
	@UiField SimplePanel description;
	@UiField nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleViewNumworxCss style;
	@UiField  HTML title;
	
	public void setHomePlace(Place place) {
		header.setHomePlace(place);
		header.setUpPlace(place);
	}
	
	public void render() {
		header.show();
		navigation.hide();
	}

	public void fail(Throwable t) {
		String message = t.getLocalizedMessage();
		Label widget = new Label(message);
		widget.setStyleName(style.description());
		description.setWidget(widget);	
	}
	
	public void setTitle(SafeHtml html) {
		title.setHTML(html);
	}
}
