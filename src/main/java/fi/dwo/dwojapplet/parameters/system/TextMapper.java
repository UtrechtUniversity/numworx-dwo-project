// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\TextMapper.java

package fi.dwo.dwojapplet.parameters.system;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class TextMapper {
    public static final String DEFAULT_LANGUAGE = "nl";

    private static final String TEXT_CLASS = "Text";

    public final static String USER_GUEST = "USER_GUEST";
    
    /* The tooltip for the help-button */
    public final static String TLTP_HELP = "TLTP_HELP";
    /* The title for the help-dialog */
    public final static String TITLE_HELP = "TITLE_HELP";
    
    /* The tooltip for the delete-item-button */
    public final static String TLTP_DELETE_ITEM = "TLTP_DELETE_ITEM";
    public final static String BTN_CLOSE = "BTN_CLOSE";
    
    public final static String LBL_NO_ITEMS = "LBL_NO_ITEMS";

    public final static String BTN_TREE_NR_ITEMS = "BTN_TREE_NR_ITEMS";
    public final static String BTN_TREE_ADD_ITEM = "BTN_TREE_ADD_ITEM";
    public final static String BTN_TREE_DELETE_ITEM = "BTN_TREE_DELETE_ITEM";
    
    public final static String BOOLEAN_TRUE = "BOOLEAN_TRUE";
    public final static String BOOLEAN_FALSE = "BOOLEAN_FALSE";
    
    public final static String MSG_TO_MANY_TREE_ITEMS = "MSG_TO_MANY_TREE_ITEMS";

    private static ResourceBundle rb;

    private static String language;

    /**

     */
    public TextMapper() {

    }

    public static ResourceBundle getResourceBundle() {
        if (rb == null) {
            if (language == null) {
                language = DEFAULT_LANGUAGE;
            }
            Locale lang = new Locale(language, "");

            String className = "fi.dwo.parameters.system.text" + "."
            + TEXT_CLASS;
//            String className = TextMapper.class.getPackage().getName() + "."
//                    + TEXT_CLASS;
            rb = ResourceBundle.getBundle(className, lang);
        }

        return rb;
    }

    /**
     * @param text
     * @return java.lang.String

     */
    public static String getText(String text) {
        return getResourceBundle().getString(text);
    }

    /**
     * @return Returns the language.
     */
    public static String getLanguage() {
        return language;
    }

    /**
     * @param language
     *            The language to set.
     */
    public static void setLanguage(String language) {
        TextMapper.language = language;
        rb = null; // Wim: rb is een cache van de oude language
    }
}