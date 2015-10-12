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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilities for preparing a mysql database
 *
 * @author G.A.J. van der Plas
 */
public class TestDatabaseManager {
    private static final Logger LOG = Logger.getLogger(TestDatabaseManager.class.getName());

    Connection con = null;

    public TestDatabaseManager(String source){
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
    
    public void ClearDatabase() {
        RunScript("ClearTestDatabase.sql");
    }

    public void IntializeDatabase() {
        RunScript("InitTestDatabase.sql");
    }

    private void RunScript(String script) {  
        ScriptRunner runner = new ScriptRunner(con, false, false);
        try {
            runner.runScript(new BufferedReader(new BufferedReader(new InputStreamReader(
                           getClass().getResourceAsStream(script)))));
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
