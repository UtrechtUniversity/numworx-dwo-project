package nl.uu.fi.dwo.rest.dom.entities;

import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelDataScore implements Cloneable {
    private static final Logger LOG = Logger.getLogger(DomStudentModelDataScore.class.getName());
    private DomStudentModelContextId modelId;
    private DomStudentModelStructureScore domStudentModelStructureScore;

    /**
     * @return the domStudentModelStructureScore
     */
    public DomStudentModelStructureScore getDomStudentModelStructureScore() {
        return domStudentModelStructureScore;
    }

    /**
     * @param domStudentModelStructureScore the domStudentModelStructureScore to set
     */
    public void setDomStudentModelStructureScore(DomStudentModelStructureScore domStudentModelStructureScore) {
        this.domStudentModelStructureScore = domStudentModelStructureScore;
    }

    /**
     * @return the modelId
     */
    public DomStudentModelContextId getModelId() {
        return modelId;
    }

    /**
     * @param modelId the modelId to set
     */
    public void setModelId(DomStudentModelContextId modelId) {
        this.modelId = modelId;
    }
    
}
