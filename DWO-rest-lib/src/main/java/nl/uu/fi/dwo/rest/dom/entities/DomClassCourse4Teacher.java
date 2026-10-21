package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.util.ResultState;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;

/**
 * The class defines which DomSchoolClass has which DomCourse.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 */
@XmlRootElement
public class DomClassCourse4Teacher extends DomClassCourse{
    private ViewState viewState;
	private String accessKey;
	private ResultState results;

	/**
	 * @return the accessKey
	 */
	public String getAccessKey() {
		return accessKey;
	}

	/**
	 * @param accessKey the accessKey to set
	 */
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

    /**
     * @return the viewState
     */
    public ViewState getViewState() {
        return viewState;
    }

    /**
     * @param viewState the viewState to set
     */
    public void setViewState(ViewState viewState) {
        this.viewState = viewState;
    }

    @Deprecated
	public void setResults(Boolean results) {
		if (results == null) this.results = null;
		else if (results.booleanValue()) this.results = ResultState.RESULTS; // cannot set CHECKS
		else this.results = ResultState.NONE;
	}
	
	public boolean hasResults() {
		return Boolean.TRUE == getResults();
	}

	@Deprecated
	public Boolean getResults() {
		if (results == null) return null;
		if (results == ResultState.NONE) return Boolean.FALSE;
		return Boolean.TRUE;
	}

	
	public void setResultState(ResultState r) {
		results = r;
	}
	
	public ResultState getResultState() {
		return results;
	}
}
