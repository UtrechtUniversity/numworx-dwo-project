/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.util;

/**
 * Abstract class defining required non interface functionality.
 * 
 * @author plas0006
 */
public abstract class PersistentId implements PersistentIdIF {

    PersistentClassType classType=PersistentClassType.none;
    
    
    public PersistentId(){
    }
    
    public PersistentId(PersistentClassType type){
        setClassType(type);
    }
    
    
    /**
     * Required implementation for implementing types.
     * 
     * @return 
     */
    public PersistentClassType getClassType() {
        return classType;
    }

    /**
     * Required implementation for implementing types.
     * 
     * @return 
     */
    public void setClassType(PersistentClassType type) {
        classType = type;
    }
    
    /**
     * Required implementation for implementing types.
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj){
        throw new UnsupportedOperationException("Implement in non-abstract class!"); //To change body of generated methods, choose Tools | Templates.
    }
}
