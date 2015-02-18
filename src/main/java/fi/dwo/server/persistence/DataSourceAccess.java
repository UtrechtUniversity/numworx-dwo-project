package fi.dwo.server.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class DataSourceAccess extends DbAccess {

    private Connection c;
    private DataSource ds;

    /* (non-Javadoc)
     * @see fi.beans.jdbc.DbConnect#close()
     */
    @Override
    public void close() {
        if (c != null) {
            try {
                c.close();
            } catch (SQLException e) {
            } finally {
                c = null;
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (c != null && !c.isClosed()) {
            return c;
        }
        c = ds.getConnection();
        return c;
    }

    /**
     *
     * @param ds
     */
    public DataSourceAccess(DataSource ds) {
        super(false); // will not call checkVersion(), because ds uninitialized
        setDs(ds);
    }

    public void setDs(DataSource ds) {
        this.ds = ds;
    }

}
