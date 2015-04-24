/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence;

/**
 * A persistent id based on object information.
 * 
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public interface PersistenceId extends Comparable<PersistenceId> {
    public PersistenceClassType getType();
}
