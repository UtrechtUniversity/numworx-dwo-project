package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelData  extends DomStudentModelDataId implements Cloneable {
    private DomStudentModelContextId modelId;
    private DomScoContextId scoContextId; 
    private DomStudentModelStructureScore domStudentModelStructureScore;

    /**
     * @return the scoContextId
     */
    public DomScoContextId getScoContextId() {
        return scoContextId;
    }

    /**
     * @param scoContextId the scoContextId to set
     */
    public void setScoContextId(DomScoContextId scoContextId) {
        this.scoContextId = scoContextId;
    }

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
