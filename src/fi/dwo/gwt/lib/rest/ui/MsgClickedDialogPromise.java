package fi.dwo.gwt.lib.rest.ui;

import org.osgi.util.promise.Deferred;

/**
 * An interface that defines of a DwoConfirmDialog has been confirmed or rejected. True means
 * accept. False rejects.
 * 
 * @author G.A.J. van der Plas
 */
public class MsgClickedDialogPromise extends Deferred<Boolean> {
    private Boolean value;
    private String msg;

    public MsgClickedDialogPromise(String aMsg) {
        msg = aMsg;        
    }

    @Override
    public void fail(Throwable caught) {
        super.fail(caught);
    }

     @Override
    public void resolve(Boolean result) {
        value = result;
        super.resolve(getValue());
    }

    /**
     * @return the state
     */
    public Boolean getValue() { // does not throw exception.
        return value;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }
    
}
