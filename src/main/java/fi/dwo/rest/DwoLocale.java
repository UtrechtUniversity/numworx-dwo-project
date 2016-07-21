/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.rest;


/**
 *
 * @author Gert van der Plas
 */
public class DwoLocale {
    private String locale;//IETF BCP 47 language tag required.
 
   public DwoLocale(){
        locale = "nl";
    }
     
    public DwoLocale(String lcl){
        locale = lcl;
    }
    /**
     * @return the locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * @param locale the locale to set
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }
}
