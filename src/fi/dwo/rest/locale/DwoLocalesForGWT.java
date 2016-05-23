/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.rest.locale;

import com.google.gwt.core.client.GWT;

/**
 *
 * @author G.A.J. van der Plas
 */
public interface DwoLocalesForGWT extends DwoLocaleMessages{
    DwoLocalesForGWT instance = GWT.create(DwoLocalesForGWT.class);
    
}
