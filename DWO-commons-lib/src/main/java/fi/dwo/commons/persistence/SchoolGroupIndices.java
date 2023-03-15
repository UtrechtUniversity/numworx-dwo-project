package fi.dwo.commons.persistence;

/**
 * Maps a constant to database indices for MySQL table tblGroup for a given
 * group.
 *
 * @author Gert van der Plas
 */
public class SchoolGroupIndices {

    public static final int NONE = 0; //reserved for future use.
    public static final int STUDENT = 1;
    public static final int TEACHER = 2;
    public static final int ADMIN = 3;
    public static final int NOSCHOOL = 4;
    public static final int SCHOOLADMIN = 5;
}
