package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ExamModuleView extends Composite {

	public interface Presenter {
		void onKO();
		void onOk(String password, ExamModuleView view);
	}
	private Presenter presenter;
	
	@UiField TextBox textView;
	@UiField Label message;
	@UiField HTML title;
	@UiField Image favIcon;
	@UiField TreeModuleViewNumworxCss style;
	@UiField SimplePanel description;
	@UiField ScrollPanel centerPanel;
	@UiField Text rb;
	HeaderView header;

	private static ExamModuleViewUiBinder uiBinder = GWT
			.create(ExamModuleViewUiBinder.class);

	interface ExamModuleViewUiBinder extends UiBinder<Widget, ExamModuleView> {
	}

	@Inject public ExamModuleView(HeaderView headerView) {
		header = headerView;
		initWidget(uiBinder.createAndBindUi(this));
// extra's
		textView.getElement().setAttribute("autocorrect", "off");
		textView.getElement().setAttribute("autocapitalize", "none");
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler({"ok"})
	void onOk(ClickEvent ev) {
		String password = textView.getValue();
		if(!password.isEmpty())
			presenter.onOk(password, this);
		else
			message.setText(rb.noExamKey());
	}
	
	@UiHandler("textView")
	void onEnter(KeyUpEvent ev) {
		if(ev.getNativeKeyCode() == KeyCodes.KEY_ENTER)
		{
			onOk(null);
		}
	}
	
	@UiHandler("cancel")
	void onCancel(ClickEvent ev) {
		presenter.onKO();
	}
	
	public void selectItem(SelectModuleItem item) {
		message.setText("");
		title.setText(item.getName());
		description.setWidget(getLabel(item));
		boolean hasImage = item.getImage() != null;
		String url = (hasImage) ? item.getImage(): ".";
		favIcon.setUrl(url);
		favIcon.setVisible(hasImage);
		centerPanel.setStyleName(style.centerBackground(), false);
		centerPanel.setStyleName(style.folderBackground(), !hasImage);
		favIcon.getParent().setStyleName(style.faviconOFF(), !isLabel(item));
		title.getParent().setStyleName(style.titlePanelFULL(), !isLabel(item));
		Object parent = item.getParentID();
		if (parent == null) parent = "0"; // wrong place?
		header.setUpPlace(new TreeModulePlace(parent));
	
	}
	private boolean isLabel(SelectModuleItem item) {
		String description = item.getDescription();
		if(description.startsWith(DescriptionView.GZIPPREFIX))
			return false;
		if(description.startsWith("<html>"))
			return false;
		return true;
	}
	private Widget getLabel(SelectModuleItem item) {
		Widget w;
		String description = item.getDescription();
		if(description.startsWith(DescriptionView.GZIPPREFIX))
		{
			w = new DescriptionViewImpl(item.getID()).asWidget();
		} else
		if(description.startsWith("<html>")) {
			w = new HTML(description);
			w.setStyleName(style.description());
		}else
		{
			w = new Label(description);
			w.setStyleName(style.description());
		}
		return w;
	}

	public void showFailure(Throwable failure) {
		String error;
		if (failure instanceof Dwo2Exception) {
			error = failure.getLocalizedMessage();
		} else {
			error = String.valueOf(failure);
		}		
		message.setText(error);
	}

}
