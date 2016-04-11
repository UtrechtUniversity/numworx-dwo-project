/*Copyrighted 2015. */
package fi.dwo.commons.persistence;

/**
 *
 * @author G.A.J. van der Plas
 * @param <T>
 */
public interface PersistentUpdate<T> {

    public abstract T update(T o);
}
