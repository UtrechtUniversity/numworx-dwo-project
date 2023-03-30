// Source file:
// C:\\parameters\\fi\\dwo\\parameters\\domain\\HTMLLaunchdataConvertor.java
package fi.dwo.dwojapplet.parameters.domain;

/**
 * This class creates out of the parameters-launchdata the HTML to launch the
 * applet. NOT IMPLEMENTED!
 *
 * @author M.J.B. Kupers
 *
 */
public class HTMLLaunchdataConvertor extends LaunchdataConvertor {

    private static ConvertorIF _launchdataConvertor = null;

    public HTMLLaunchdataConvertor() {

    }

    public static ConvertorIF instance() {
        if (_launchdataConvertor == null) {
            _launchdataConvertor = new HTMLLaunchdataConvertor();
        }
        return _launchdataConvertor;
    }

}
