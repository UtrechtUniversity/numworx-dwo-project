/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence;

/**
 * Abstract class defining required non interface functionality.
 * 
 * @author plas0006
 */
public abstract class PersistentId implements PersistentIdIF<PersistentId> {


    /**
     * Required implementation for implementing types.
     * 
     * @param obj
     * @return 
     */
    public boolean equals(T obj){
        throw new UnsupportedOperationException("Implement in non-abstract class!"); //To change body of generated methods, choose Tools | Templates.
    }
    
    public int compareTo(T o){
        throw new UnsupportedOperationException("Implement in non-abstract class!"); //To change body of generated methods, choose Tools | Templates.
    }
}
