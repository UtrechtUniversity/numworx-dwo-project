/* Copyrighted 2015.  */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * PersistentDwoSystemParameters stores database model dependent parameters.
 *
 * <center>
 * <table border=1 frame=hsides rules=rows>
 * <tr><th>variable name </th><th> data type </th><th> db model
 * version</th></tr>
 * <tr><td>DBVersion Major </td><td> integer as string</td><td> v1.2</td></tr>
 * <tr><td>DBVersion Minor</td><td>int as string</td><td>v1.2</td></tr>
 * <tr><td>DBVersion Revision</td><td>int as string</td><td>v1.2</td></tr>
 * <tr><td>DBVersion Built</td><td>int as string</td><td>v1.2</td></tr>
 * <tr><td>DBPlatform</td><td>string</td><td>v1.3</td></tr>
 * <tr><td>DwoSchoolIndex</td><td>int as string</td><td>v1.3</td></tr>
 * <tr><td>NoSchoolIndex</td><td>int as string</td><td>v1.3</td></tr>
 * </table>
 * </center>
 *
 * <ul>
 * <li>DwoSchoolIndex is the index of the DwoSchool.
 * <li> NoSchoolIndex is the index of the school in which users are placed that
 * do not register for any school.
 * <li> DBPlatform denotes the database platform, options: MySQL.
 * </ul>
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tbldwosystemparameters", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentDwoSystemParameters.findAll", query = "SELECT t FROM PersistentDwoSystemParameters t"),
    @NamedQuery(name = "PersistentDwoSystemParameters.findByName", query = "SELECT t FROM PersistentDwoSystemParameters t WHERE t.name = :name"),
    @NamedQuery(name = "PersistentDwoSystemParameters.findByValue", query = "SELECT t FROM PersistentDwoSystemParameters t WHERE t.value = :value")})
public class PersistentDwoSystemParameters implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Column(name = "value", length = 100)
    private String value;

    public PersistentDwoSystemParameters() {
    }

    public PersistentDwoSystemParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

//    public void setName(String name) {
//        this.name = name;
//    }
    public String getValue() {
        return value;
    }

//    public void setValue(String value) {
//        this.value = value;
//    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentDwoSystemParameters)) {
            return false;
        }
        PersistentDwoSystemParameters other = (PersistentDwoSystemParameters) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.DwoSystemParameters[ name=" + name + " ]";
    }

}
