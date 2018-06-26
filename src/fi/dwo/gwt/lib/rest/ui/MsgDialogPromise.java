package fi.dwo.gwt.lib.rest.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;

import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * GWTDialogPromise.
 *
 * @author G.A.J. van der Plas
 */
@Deprecated //clearly this Promise contains gui code and should be removed with haste.
public class MsgDialogPromise<T> implements ClickHandler, Success<T,T>, Failure{ //should implement success and fail overload

    private T value;
    private Promise<T> promise;
    private String msg;
    private DialogBox dialogBox = new DialogBox();
    Button cancelButton = new Button(DwoLocalesForGWT.instance.GUI_BTN_Cancel());
    
    public void onClick(ClickEvent event) {
        if (event.getSource() == cancelButton) {
            dialogBox.hide();        
        }
    }

    public MsgDialogPromise(Promise<T> aPromise, String aMsg) {
        promise = aPromise;
        //chain success and fail of given promise to hiding of dialog.
        msg = aMsg;
        Label label = new Label();
        label.setText(aMsg);
        dialogBox.add(label);        
        dialogBox.add(cancelButton);
        dialogBox.setModal(true);
        dialogBox.setAutoHideEnabled(false);
        dialogBox.setGlassEnabled(true);
        dialogBox.setAnimationEnabled(true);
        dialogBox.center();
        cancelButton.addClickHandler(this);
        dialogBox.show();
        promise.then(this, this);
    }


    @Override
    public Promise<T> call(Promise<T> resolved) throws Exception {
        dialogBox.hide();
        return resolved ;
    }

    @Override
    public void fail(Promise<?> resolved) throws Exception {
        dialogBox.hide();
        //show fail dialog resolved.getFailure();
    }
}
