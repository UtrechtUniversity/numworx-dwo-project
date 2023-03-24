package nl.uu.fi.dwo.lms.gwtclient.gwt;

import org.osgi.util.promise.Deferred;

/**
 * A DwoGlobalVarDeferred that promises a certain state of the DwoGlobalVars.
 * 
 * @author G.A.J. van der Plas
 */
public class DwoGlobalVarDeferred<T> extends Deferred<T> {

    private DwoGlobalVars vars;
    private T value;

    DwoGlobalVarDeferred(DwoGlobalVars aVars, T aValue) {
        vars = aVars;
        value = aValue;
    }

    @Override
    public void fail(Throwable caught) {
        super.fail(caught);
    }

     @Override
    public void resolve(T result) {
        super.resolve(getValue());
    }

    /**
     * @return the state
     */
    public T getValue() {
        return value;
    }

}
