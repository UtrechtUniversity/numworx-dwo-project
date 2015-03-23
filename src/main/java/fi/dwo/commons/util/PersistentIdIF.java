/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.util;

/**
 * A interface to hide database index types.
 *
 * @author plas0006
 */
public interface PersistentIdIF {

    public PersistentClassType getClassType();

    public void setClassType(PersistentClassType type);

    /**
     * Required implementation for implementing types.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj);

}
