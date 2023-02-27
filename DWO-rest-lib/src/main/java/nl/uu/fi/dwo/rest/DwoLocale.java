package nl.uu.fi.dwo.rest;


/**
 *
 * @author Gert van der Plas
 */
public class DwoLocale {
    private String locale;//IETF BCP 47 language tag required.
 
   public static final DwoLocale DEFAULT = new DwoLocale("nl");
    
   public DwoLocale(){
        locale = getDefaultLocale();
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

    public String getDefaultLocale() {
        return DEFAULT.getLocale();
    }
}
