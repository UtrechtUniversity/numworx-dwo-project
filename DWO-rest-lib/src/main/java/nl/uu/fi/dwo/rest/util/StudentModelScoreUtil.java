package nl.uu.fi.dwo.rest.util;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

/**
 * Parent class voor Xapi services.
 * Gedeelde berekeningen voor dwo-commons en gwtclient.
 */

public abstract class StudentModelScoreUtil {
	public static final String ATTEMPTED = "http://www.dwo.nl/verbs/attempted";
	public static final String CORRECTED = "http://www.dwo.nl/verbs/corrected";

	protected DomStudentModelDataScore eerstestap(DomStudentModelContext context) {
		    DomStudentModelStructure structure = context.getModelStructure();
		    return eerstestap(context, structure);
		  }
		  
	protected DomStudentModelDataScore eerstestap(DomStudentModelContext4Student context) {
			  return eerstestap(context, context.getModelStructure());
		  }

	private DomStudentModelDataScore eerstestap(DomStudentModelContextId context, DomStudentModelStructure structure) {
			DomStudentModelDataScore result = new DomStudentModelDataScore();
		    DomStudentModelStructureScore score = structure.generateStudentModelStructureScore();
		    result.setDomStudentModelStructureScore(score);
		    result.setModelId(context);
		    return result;
		}

}
