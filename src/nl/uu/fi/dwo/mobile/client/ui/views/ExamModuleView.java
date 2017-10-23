package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
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
	@UiField(provided=true) String pfx;
	@UiField HasText loginLabel;
	@UiField Image favIcon;
	@UiField TreeModuleViewNumworxCss style;
	@UiField SimplePanel description;
	@UiField ScrollPanel centerPanel;
	@UiField Text rb;

	@UiField(provided=true) MenuItem user;
	private MenuBar items = new MenuBar(true);

	private Object upId;

	private static ExamModuleViewUiBinder uiBinder = GWT
			.create(ExamModuleViewUiBinder.class);

	interface ExamModuleViewUiBinder extends UiBinder<Widget, ExamModuleView> {
	}

	public ExamModuleView() {
		pfx=DWOplayer.PARAMETERS.getResource("");
        final int correctie = 10; // width popup 
		user = new MenuItem("<i class='fa fa-caret-down fa-2x'></i>", true, items) {
            @Override
            public int getAbsoluteLeft() {
                int w1 = items.getOffsetWidth();
                int w2 = this.getOffsetWidth();
                return super.getAbsoluteLeft() - w1 + w2 - correctie;
            }
		};
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
		else
			message.setText("Vul je code in");
	}
	
	@UiHandler("cancel")
	void onCancel(ClickEvent ev) {
		presenter.onKO();
	}
	
	public void selectItem(SelectModuleItem item) {
		String login = DWOplayer.withUser()? DwoGlobalVars.instance().getCurrentUser().getDisplayName() : "GUEST";
		loginLabel.setText(login);
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
		upId = item.getParentID();

		items.clearItems();
		items.addItem(rb.logout(), new ScheduledCommand() {
			
			@Override
			public void execute() {
				goTo(new LoginPlace());					
			}
		}).addStyleName(style.menuItem());		
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
		message.setText(String.valueOf(failure));
	}
	@UiHandler("homeBtn")
	void onHomeBtn(ClickEvent ev) {
		goTo(new TreeModulePlace());
	}

	private void goTo(Place place) {
		DWOplayer.clientfactory.getPlaceController().goTo(place);
	}

	@UiHandler("upBtn")
	void onUpBtn(ClickEvent ev) {
		Object parent = upId;
		if (parent == null) parent = "0"; // wrong place?
		goTo(new TreeModulePlace(parent));
	}

}
