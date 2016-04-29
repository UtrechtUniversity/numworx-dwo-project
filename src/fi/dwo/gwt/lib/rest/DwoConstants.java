/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.gwt.lib.rest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

/**
 *
 * @author Gert van der Plas
 */
public interface DwoConstants extends Constants {
    DwoConstants constants = GWT.create(DwoConstants.class);
    public String server();
}
