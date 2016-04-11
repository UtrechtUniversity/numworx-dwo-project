/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import fi.dwo.rest.dom.entities.DomUserFull;

/**
 *
 * @author Gert van der Plas
 */
public class GlobalState {
    private static DomUserFull curUser=null;

    /**
     * @return the curUser
     */
    public static DomUserFull getCurUser() {
        return curUser;
    }

    /**
     * @param curUser the curUser to set
     */
    public static void setCurUser(DomUserFull aCurUser) {
        curUser = aCurUser;
    }
}
