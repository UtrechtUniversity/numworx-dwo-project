package nl.uu.fi.dwo.lms.gwtclient.gwt;

import org.osgi.util.promise.Deferred;

/**
 * An interface that defines of a DwoConfirmDialog has been confirmed or rejected. True means
 * accept. False rejects.
 * 
 * @author G.A.J. van der Plas
 */
public class ConfirmDialogPromise<T extends Boolean> extends Deferred<T> {
    private T value;
    private String msg;

    public ConfirmDialogPromise(String aMsg) {
        msg = aMsg;        
        //value = false;
    }

    @Override
    public void fail(Throwable caught) {
        super.fail(caught);
    }

     @Override
    public void resolve(T result) {
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
