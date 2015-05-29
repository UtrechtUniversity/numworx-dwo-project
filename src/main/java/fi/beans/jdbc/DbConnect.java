package fi.beans.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author wim
 *
 */
public class DbConnect implements DbConnectIF {

    private static final Logger LOG = Logger.getLogger(DbConnect.class.getName());
    public static final int DEFAULT = 1;
    //public static final int VECTOR  = 0;

    /**
     * Constant voor access naar mysql.fi.uu.nl. Voorbeeld van gebruik:
     * DbConnect dbc = new DbConnect(DbConnect.MYSQL,"dwo", "dwo", "!dwo");
     * Connection c = dbc.getConnection();
     */
    public static final int MYSQL = 1;
    /**
     * Constant voor access naar de Filemaker database. Vanaf versie 7 is er een
     * volledig ge&iuml;mplemeteerde JDBC koppeling
     */
    public static final int FILEMAKER = 2;
    public static final int FMSERVER7 = 2;
    public static final int FMSERVER = 2;
    // more to follow....

    /**
     * Connect to a MySQL 5 database.
     */
    public static final int MYSQL5 = 3;

    /**
     * Connect to MySQL Science, FISME specific. Username and database prefixed
     * with <i>fisme_</i>
     *
     * @see #DbConnect(int, String)
     */
    public static final int MYSQL_SCIENCE_FISME = 4;
    /**
     * Connect to MySQL Science, general. No prefixing in username/database
     */
    public static final int MYSQL_SCIENCE = 5;

    public static final int MYSQL2_SCIENCE = 6;
    public static final int MYSQL2_SCIENCE_FISME = 7;

    private Connection c;
    private String url;
    private Properties info;

    private static final String[] URLPATTERN
            = {
                "jdbc:freetds:sqlserver://vector.nt.fi.uu.nl/{0}",
                "jdbc:mysql://mysql.fi.uu.nl/{0}",
                "jdbc:sequelink://fmserver.fi.uu.nl:2399;serverdatasource={0}",
                "jdbc:mysql://mysql5.fi.uu.nl:3305/{0}",
                "jdbc:mysql://mysql.science.uu.nl/fisme_{0}",
                "jdbc:mysql://mysql.science.uu.nl/{0}",
                "jdbc:mysql://mysql2.science.uu.nl/{0}",
                "jdbc:mysql://mysql2.science.uu.nl/fisme_{0}",};
    private static final String[] driver
            = {
                "com.internetcds.jdbc.tds.Driver",
                "com.mysql.jdbc.Driver",
                "com.ddtek.jdbc.sequelink.SequeLinkDriver",};
    private static String[] passwordPfx
            = {
                "!",
                "_",
                "_",
                "_",
                "_fisme_",
                "_",
                "_",
                "_fisme_",};

    static {
        for (String driver1 : driver) {
            try {
                Class.forName(driver1).newInstance();
            } catch (Exception e) {
                //throw new NoClassDefFoundError(e.getMessage());
            }
        }
    }

    /*
     * Een database met standaard user/password.
     */
    public DbConnect(String database) {
        this(DEFAULT, database);
    }

    public DbConnect(int engine, String database) {
        url = MessageFormat.format(URLPATTERN[engine], new Object[]{database});
        String user, password;
        if (database.endsWith("_tst")) {
            user = database.substring(0, database.length() - 4);
        } else {
            user = database;
        }
        password = passwordPfx[engine] + user;
        if (engine == MYSQL_SCIENCE_FISME || engine == MYSQL2_SCIENCE_FISME) {
            user = "fisme_" + user;
        }
        setInfo(user, password);
    }

    /**
     * Een database met username/password opgegeven.
     *
     * @param database
     * @param user
     * @param password
     */
    public DbConnect(String database, String user, String password) {
        this(DEFAULT, database, user, password);
    }

    public DbConnect(int engine, String database, String user, String password) {
        url = MessageFormat.format(URLPATTERN[engine], new Object[]{database});
        setInfo(user, password);
    }

    public DbConnect() {
        this("fidb");
    }

    /**
     * Method setInfo.
     *
     * @param user
     * @param password
     */
    private void setInfo(String user, String password) {
        info = new Properties();
        info.put("user", user);
        info.put("password", password);
        info.put("TDS", "7.0");
    }

    /**
     * Gemaksmethode. Combineer getConnection met prepareStatement
     *
     * @param sql
     * @return
     * @throws java.sql.SQLException
     */
    protected PreparedStatement getStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    /**
     * Gemaksmethode. Combineer getConnection met prepareStatement
     *
     * @param sql
     * @return
     * @throws java.sql.SQLException
     */
    protected PreparedStatement getStatementWithGeneratedKeys(String sql) throws SQLException {
        return getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    /**
     * Gemaksmethode. Combineer getConnection met executeQuery
     *
     * @param sql
     * @return
     * @throws java.sql.SQLException
     */
    protected ResultSet executeQuery(String sql) throws SQLException {
        Statement s = getConnection().createStatement();
        ResultSet rs = s.executeQuery(sql);
        //s.close(); // hier of later? nee later!
        return new ResultSetWrapper(rs); // close Statement s after rs.close
    }

    /**
     * Geef mij een java.sql.Connection naar de database.
     *
     * @throws java.sql.SQLException
     * @return een Connection voor database access.
     */
    public Connection getConnection() throws SQLException {
        if (c == null || c.isClosed()) {
            c = DriverManager.getConnection(url, info);
        }
        return c;
    }

    /**
     * Gemaksmethode. Sluit de Connection. getConnection geeft je weer een
     * nieuwe.
     */
    @Override
    public void close() {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Could not close connection: {0}.", new Object[]{e.getMessage()});
        }
        c = null;
    }

}
