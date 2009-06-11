// Source file:
// C:\\parameters\\fi\\dwo\\parameters\\domain\\ConvertorCreator.java

package fi.dwo.parameters.domain;

/**
 * The Convertor is used to convert the launchdata from the parameters in the right format.
 * 
 * @author M.J.B. Kupers
 *
 */
public final class ConvertorCreator {

    public final static int CONV_LAUNCHDATA = 1;

    public final static int CONV_HTML_LAUNCHDATA = 2;

    /**
     * @return fi.dwo.parameters.domain.ConvertorIF
     */
    public static ConvertorIF createConverter(int type) {
        switch(type) {
        	case CONV_LAUNCHDATA: return LaunchdataConvertor.instance();
        	case CONV_HTML_LAUNCHDATA: return HTMLLaunchdataConvertor.instance();
        	default: return null;
        }
    }
}