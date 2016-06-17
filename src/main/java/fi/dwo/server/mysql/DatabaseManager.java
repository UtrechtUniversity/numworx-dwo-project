/**
 * Copyrighted Oct 12, 2015
 */
package fi.dwo.server.mysql;

import fi.dwo.server.apache.util.ScriptRunner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilities for preparing a mysql database
 *
 * @author G.A.J. van der Plas
 */
public class DatabaseManager {

    private static final Logger LOG = Logger.getLogger(DatabaseManager.class.getName());

    private String conString = "";
    private Connection con = null;

    public DatabaseManager() {
        try {
            ReadConfigProperties();
        }
        catch (IOException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        init(conString);
    }
    
    public DatabaseManager(String source) {
        init(source);
    }

    private void init(String source) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(source);
        }
        catch (ClassNotFoundException e) {
            System.err.println("Unable to get mysql driver: " + e);
        }
        catch (SQLException e) {
            System.err.println("Unable to connect to server: " + e);
        }
    }

    private void ReadConfigProperties() throws IOException {
        ReadConfigProperties("mysqlconnection.properties");
    }

    private void ReadConfigProperties(String runPropFile) throws IOException {
        LOG.log(Level.INFO, "Opening property file for reading configuration parameters.");

        Properties properties = new Properties();

        InputStreamReader input;

        //file handle for main.properties
        input = new InputStreamReader(
                    getClass().getResourceAsStream(runPropFile));
        
        //load the properties
        properties.load(input);

        //done with file
        input.close();

        //assign properties to static value.
        conString = properties.getProperty("resourceString");
        LOG.log(Level.INFO, "Parsing property file.");
        LOG.log(Level.INFO, "Property {0} has value: {1}", new Object[]{"resourceString", conString.length()});
        LOG.log(Level.INFO, "Finnished property file..");
    }

    public void ClearDatabase() {
        RunScript("ClearTestDatabase.sql");
    }

    public void IntializeEmptyDatabase() {
        RunScript("InitDatabaseNoJars.sql");
    }

    public void IntializeTestDatabase() {
        RunScript("InitTestDatabase.sql");
    }

    private void RunScript(String script) {
        ScriptRunner runner = new ScriptRunner(con, false, false);
        try {
            runner.runScript(new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream(script))));
        }
        catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
