/**
 * Copyrighted Oct 12, 2015
 */
package fi.dwo.server.testutil;

/**
 * 
 * 
 * @author G.A.J. van der Plas
 */
public class Parameters {
        private static String resourceString = "jdbc:mysql://localhost:3306/dwojunittest?user=test&password=test";

    /**
     * @return the resourceString
     */
    public static String getResourceString() {
        return resourceString;
    }

}
