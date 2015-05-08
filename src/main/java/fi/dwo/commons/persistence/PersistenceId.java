/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence;

import fi.dwo.commons.rest.RestClassType;

/**
 * A persistent id based on object information.
 * 
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public interface PersistenceId extends Comparable<PersistenceId> {
    public RestClassType getType();
    /**
     * This returns a string id that maps one to one to an internal id
     * of the implementing class.
     * 
     * @return 
     */
    public String getIdString();
}
