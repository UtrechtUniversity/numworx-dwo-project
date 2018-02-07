package nl.uu.fi.dwo.rest.dom.entities;

import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelData extends DomStudentModelDataId implements Cloneable {
    private static final Logger LOG = Logger.getLogger(DomStudentModelData.class.getName());
    
    
}
