package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelDataScore implements Cloneable {
    private DomStudentModelContextId modelId;
    private DomStudentModelStructureScore domStudentModelStructureScore;
    private Long fetchTimeStamp;

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

    /**
     * @return the fetchTimeStamp
     */
    public Long getFetchTimeStamp() {
      return fetchTimeStamp;
    }

    /**
     * @param fetchTimeStamp the fetchTimeStamp to set
     */
    public void setFetchTimeStamp(Long fetchTimeStamp) {
      this.fetchTimeStamp = fetchTimeStamp;
    }
    
}
