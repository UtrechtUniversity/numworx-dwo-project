package nl.uu.fi.dwo.account.client;

import org.osgi.util.promise.Deferred;

/**
 * A DwoGlobalVarPromise that promises a certain state of the DwoGlobalVars.
 * 
 * @author G.A.J. van der Plas
 */
public class DwoGlobalVarPromise<T> extends Deferred<T> {

    private DwoGlobalVars vars;
    private T value;

    DwoGlobalVarPromise(DwoGlobalVars aVars, T aValue) {
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
