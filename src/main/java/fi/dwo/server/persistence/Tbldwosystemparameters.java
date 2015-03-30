/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.ws.rs.GET;
import javax.xml.bind.annotation.XmlRootElement;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author plas0006
 */
@Entity
@Table(name = "tbldwosystemparameters", catalog = "dwo_large_v1_3", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tbldwosystemparameters.findAll", query = "SELECT t FROM Tbldwosystemparameters t"),
    @NamedQuery(name = "Tbldwosystemparameters.findByName", query = "SELECT t FROM Tbldwosystemparameters t WHERE t.name = :name"),
    @NamedQuery(name = "Tbldwosystemparameters.findByValue", query = "SELECT t FROM Tbldwosystemparameters t WHERE t.value = :value")})
@Path("/systemparameters")
public class Tbldwosystemparameters implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Column(name = "value", length = 100)
    private String value;

    public Tbldwosystemparameters() {
    }

    public Tbldwosystemparameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tbldwosystemparameters)) {
            return false;
        }
        Tbldwosystemparameters other = (Tbldwosystemparameters) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    @GET
    @Produces("text/plain")
    public String getStatus(){
        return "Testing ";
    }
    
    @Override
    public String toString() {
        return "fi.dwo.server.persistence.Tbldwosystemparameters[ name=" + name + " ]";
    }
    
}
