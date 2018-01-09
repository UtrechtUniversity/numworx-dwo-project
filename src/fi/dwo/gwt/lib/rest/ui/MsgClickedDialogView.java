package fi.dwo.gwt.lib.rest.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import fi.dwo.gwt.lib.rest.css.DwoStyle;

import java.util.logging.Logger;

/**
 * Panel to configure and add a schoolclass.
 *
 * @author Gert van der Plas
 */
public class MsgClickedDialogView extends Composite implements MsgClickedDialogPresenter.Display, ClickHandler {

    private static Logger LOG = Logger.getLogger(MsgClickedDialogView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, MsgClickedDialogView> {
    }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    Button okButton;
    @UiField
    HTML htmlText;
    
    @UiField(provided=true) 
    DwoStyle dwoStyle;
            
    MsgClickedDialogPresenter msgConfirmDialogPresenter;
    final DialogBox dialogBox = new DialogBox();

    public MsgClickedDialogView(MsgClickedDialogPresenter mp, DwoStyle style) {
    	dwoStyle = style;
        initWidget(uiBinder.createAndBindUi(this));
        msgConfirmDialogPresenter = mp;
        mp.setView(this);
        okButton.addClickHandler(this);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == okButton) {
            msgConfirmDialogPresenter.confirm();
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
    public void hideDialog(){
        dialogBox.hide();
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
