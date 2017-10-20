package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
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
	
	private static ExamModuleViewUiBinder uiBinder = GWT
			.create(ExamModuleViewUiBinder.class);

	interface ExamModuleViewUiBinder extends UiBinder<Widget, ExamModuleView> {
	}

	public ExamModuleView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler({"ok","textView"})
	void onOk(ClickEvent ev) {
		String password = textView.getValue();
		if(!password.isEmpty())
			presenter.onOk(password, this);
	}
	
	@UiHandler("cancel")
	void onCancel(ClickEvent ev) {
		presenter.onKO();
	}
	
	public void selectItem(SelectModuleItem item) {
		message.setText(item.getName());
	}

	public void showFailure(Throwable failure) {
		message.setText(String.valueOf(failure));
	}
}
