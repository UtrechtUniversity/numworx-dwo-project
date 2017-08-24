package nl.uu.fi.dwo.lms.gwtclient.gwt.gui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

/**
 * GWTDialogPromise.
 *
 * @author G.A.J. van der Plas
 */
public class MsgDialogPromise<T> extends Deferred<T> implements ClickHandler{

    private T value;
    private Promise promise;
    private String msg;
    private DialogBox dialogBox = new DialogBox();
    Button cancelButton = new Button("Cancel");
    
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
    }

    @Override
    public void fail(Throwable caught) {
        dialogBox.hide();
        super.fail(caught);
    }

    @Override
    public void resolve(T result) {
        dialogBox.hide();
        value = result;
        super.resolve(getValue());
    }

    /**
     * @return the state
     */
    public T getValue() {
        return value;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }
}
