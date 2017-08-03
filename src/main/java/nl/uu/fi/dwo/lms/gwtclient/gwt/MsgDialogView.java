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
public class MsgDialogView extends Composite implements MsgDialogPresenter.Display, ClickHandler {

    private static Logger LOG = Logger.getLogger(MsgDialogView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, MsgDialogView> {
    }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    PushButton okButton;
    @UiField
    HTML htmlText;
            
    MsgDialogPresenter msgDialogPresenter;
    final DialogBox dialogBox = new DialogBox();

    public MsgDialogView(MsgDialogPresenter mp) {
        initWidget(uiBinder.createAndBindUi(this));
        msgDialogPresenter = mp;
        mp.setView(this);
        okButton.addClickHandler(this);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == okButton) {
            dialogBox.hide();
            msgDialogPresenter.Back();
//        } else if (event.getSource() == cancelButton) {
//            LOG.log(Level.INFO, "Add schoolclass now");
//            dialogBox.hide();
//            editSchoolClassPresenter.Back();
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
    public void showDialog(String msg) {
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
