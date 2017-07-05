package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Logger;

/**
 * Panel to configure and add a schoolclass.
 *
 * @author Gert van der Plas
 */
public class MsgConfirmDialogView extends Composite implements MsgConfirmDialogPresenter.Display, ClickHandler {

    private static Logger LOG = Logger.getLogger(MsgConfirmDialogView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, MsgConfirmDialogView> {
    }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    PushButton cancelButton;
    @UiField
    PushButton okButton;
    @UiField
    HTML htmlText;
            
    MsgConfirmDialogPresenter msgConfirmDialogPresenter;
    final DialogBox dialogBox = new DialogBox();

    public MsgConfirmDialogView(MsgConfirmDialogPresenter mp) {
        initWidget(uiBinder.createAndBindUi(this));
        msgConfirmDialogPresenter = mp;
        mp.setView(this);
        okButton.addClickHandler(this);
        cancelButton.addClickHandler(this);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == okButton) {
            msgConfirmDialogPresenter.confirm();
            dialogBox.hide();
        } else if (event.getSource() == cancelButton) {
            msgConfirmDialogPresenter.cancel();
            dialogBox.hide();
        }
    }

    @Override
    public void clear() {
    }

    @Override
    public void init() {
        clear();
    }

    @Override
    public void showConfirmDialog(String msg) {
        htmlText.setText(msg);
        if (dialogBox.getWidget() == null) {
            dialogBox.add(this.asWidget());
            dialogBox.setModal(true);
            dialogBox.setAutoHideEnabled(false);
            dialogBox.setGlassEnabled(true);
            dialogBox.setAnimationEnabled(true);
            dialogBox.center();
        }
        dialogBox.show();
    }

}
