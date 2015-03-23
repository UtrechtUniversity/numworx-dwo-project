/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.util;

/**
 *
 * @author plas0006
 */
public class MySQLPersistentId extends PersistentId {
    private int id=-1;
    
    public MySQLPersistentId() {
        super();
    }

    public MySQLPersistentId(PersistentClassType type) {
        super(type);
    }

    /**
     * Required implementation for implementing types.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object anId) {
        if(anId.getClass()!=getClass()) return false;
        MySQLPersistentId aId = (MySQLPersistentId) anId;
        if(aId.classType!=getClassType() || getId() !=aId.getId()) return false;
        return true;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

}
