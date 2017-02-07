package nl.uu.fi.dwo.account.client;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import org.osgi.util.function.Function;
import org.osgi.util.function.Predicate;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 *
 * @author G.A.J. van der Plas
 */
public class DwoGlobalVarPromise implements Promise<DwoGlobalVars.DwoGlobalVarsState> {

    @Override
    public boolean isDone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DwoGlobalVars.DwoGlobalVarsState getValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Throwable getFailure() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Promise<DwoGlobalVars.DwoGlobalVarsState> onResolve(Runnable callback) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <R> Promise<R> then(Success<? super DwoGlobalVars.DwoGlobalVarsState, ? extends R> success, Failure failure) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <R> Promise<R> then(Success<? super DwoGlobalVars.DwoGlobalVarsState, ? extends R> success) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Promise<DwoGlobalVars.DwoGlobalVarsState> filter(Predicate<? super DwoGlobalVars.DwoGlobalVarsState> predicate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <R> Promise<R> map(Function<? super DwoGlobalVars.DwoGlobalVarsState, ? extends R> mapper) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <R> Promise<R> flatMap(Function<? super DwoGlobalVars.DwoGlobalVarsState, Promise<? extends R>> mapper) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Promise<DwoGlobalVars.DwoGlobalVarsState> recover(Function<Promise<?>, ? extends DwoGlobalVars.DwoGlobalVarsState> recovery) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Promise<DwoGlobalVars.DwoGlobalVarsState> recoverWith(Function<Promise<?>, Promise<? extends DwoGlobalVars.DwoGlobalVarsState>> recovery) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Promise<DwoGlobalVars.DwoGlobalVarsState> fallbackTo(Promise<? extends DwoGlobalVars.DwoGlobalVarsState> fallback) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
