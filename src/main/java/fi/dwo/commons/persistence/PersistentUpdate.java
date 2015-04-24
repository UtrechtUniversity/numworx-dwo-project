/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public interface PersistentUpdate<T> {
    public abstract T update(T o);
}
