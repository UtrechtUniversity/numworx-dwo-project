/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence;

/**
 * A interface to hide database index types.
 *
 * @author plas0006
 */
public interface PersistentIdIF<T extends PersistentId> extends Comparable<T> {

    public T getPersistentId();

    public void setPersistentId(T o);
    
    /**
     * Required implementation for implementing types.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj);

}
