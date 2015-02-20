/*
 * Created on Mar 4, 2005
 *
 */
package fi.dwo.dwojapplet.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.LessonGroup;
import fi.dwo.dwojapplet.domain.ResultScore;
import fi.dwo.dwojapplet.domain.ResultsModuleIF;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.domain.UserGroup;
import fi.dwo.dwojapplet.domain.UserResultList;

/**
 * @author thijsk
 *
 */
public class UserResultListMapper extends XmlRpcMapper {

    private static final String TABLENAME = "tblUser";

    private static final String IDCOL = "userID";

    private static final String ORDERCOL = "lastname";

    private ResultsModuleIF resultsModule;

    /**
     *
     */
    public UserResultListMapper() {

    }

    /**
     * @param oid
     * @param obj
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     *
     */
    @Override
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        System.err.println("UserMapper.put() Not yet implemented!");
    }

    /**
     * @param data
     * @return Object
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     *
     */
    @Override
    public Object getObjectFromReturn(Hashtable data) throws IOException, SQLException, XmlRpcException {
        ResultScore rs = new ResultScore();
        return update(rs, data);
    }

    /**
     * @param obj
     * @return Object[]
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     *
     */
    @Override
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        return get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#getIDCol()
     */
    @Override
    protected String getIDCol() {
        return IDCOL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#getTableName()
     */
    @Override
    protected String getTableName() {
        return TABLENAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#update(java.lang.Object,
     *      java.util.Hashtable)
     */
    @Override
    protected Object update(Object obj, Hashtable data) throws IOException, SQLException, XmlRpcException {
        ResultScore rs = (ResultScore) obj;

        UserGroup ug = null;
        LessonGroup lg = null;

        if (data.containsKey("classID")) {
            ug = (SchoolClass) MapperCreator.instance(SchoolClass.class).get(((Integer) data.get("classID")).intValue());
        } else if (data.containsKey("userID")) {
            ug = (User) MapperCreator.instance(User.class).get(((Integer) data.get("userID")).intValue());
        }

        if (data.containsKey("courseID")) {
            lg = (Course) MapperCreator.instance(Course.class).get(((Integer) data.get("courseID")).intValue());
        } else if (data.containsKey("scoID")) {
            lg = (Sco) MapperCreator.instance(Sco.class).get(((Integer) data.get("scoID")).intValue());
        }

        rs.setLessonGroup(lg);
        rs.setUserGroup(ug);
        Object score = data.get("score");
        if (score instanceof String) {
            /* Score a string -> it was null */
            rs.setScore(0);
        } else if (score instanceof Float) {
            rs.setScore(((Float) score).floatValue());
        } else if (score instanceof Double) {
            rs.setScore(((Double) score).floatValue());
        } else {
            rs.setScore(Float.valueOf((String) score).floatValue());
        }
        Object totaal = data.get("totaal");
        if (totaal instanceof String) {
            rs.setTotaal(1);
        } else if (totaal instanceof Number) {
            rs.setTotaal(((Number) totaal).intValue());
        }
        Object total_time = data.get("total_time");
        if (total_time instanceof String && !("".equals(total_time))) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            formatter.setLenient(true);
            try {
                rs.setTotal_time(formatter.parse(total_time.toString()).getTime());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return rs;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    @Override
    protected Object[] createArray(int size) {
        return new UserResultList[size];
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    @Override
    protected String getOrderbyCol() {
        return ORDERCOL;
    }

    @Override
    public Object[] getObjectFromReturn(Vector data) throws IOException,
            SQLException, XmlRpcException {
        Vector result = new Vector();

        if (data.size() == 0) {
            Vector[] tmp = new Vector[1];
            tmp[0] = result;
            return tmp;
        }

        String userGroupKey = "classID";
        Hashtable ht;

        ht = (Hashtable) data.elementAt(0);
        if (!ht.containsKey(userGroupKey)) {
            userGroupKey = "userID";
        }

        /* Lookup the number of columns (through searching for the first difference in userGroupKEY) */
        int colLength = 0;
        Object curID = ht.get(userGroupKey);
        while (ht.get(userGroupKey).equals(curID) && (data.size() > colLength)) {
            colLength++;
            if (data.size() > colLength) {
                ht = (Hashtable) data.elementAt(colLength);
            }
        }

        UserResultList url;
        ResultScore[] rs = new ResultScore[colLength];
        int currentUrl = 0; //Current Row
        int currentResultScore = 0; //Current Column

        url = new UserResultList();
        url.setResultsModule(resultsModule);
        for (int i = 0; i < data.size(); i++) {
            ht = (Hashtable) data.elementAt(i);
            rs[currentResultScore] = (ResultScore) getObjectFromReturn(ht);
            rs[currentResultScore].setUserResultList(url);
            currentResultScore++;

            /* Total column done */
            if (currentResultScore >= rs.length) {
                currentResultScore = 0;
                url.setResultScore(rs);
                result.addElement(url);
                rs = new ResultScore[colLength];
                currentUrl++;
                url = new UserResultList();
                url.setResultsModule(resultsModule);
            }
        }

        Vector[] v = new Vector[1];
        v[0] = result;
        return v;

    }

    public void setResultsModule(ResultsModuleIF resultsModule) {
        this.resultsModule = resultsModule;
        removeAllObjects();
    }
}
