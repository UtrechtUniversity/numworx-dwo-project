package nl.uu.fi.dwo.rest.dom.entities.util;

/**
 * The "mode" of a Sco.
 * @see fi.wiskopdr.WiskOpdr
 * @see org.cbook.AssessmentMode
 * @author velth101
 *
 */
public enum ScoType {
	OEFENEN, // oefenen
	OEFENEN_STRAFPUNTEN, // met strafpunten
	ZELFTOETS, // zelftoets
	EINDTOETS, // toets
	
	INFO,  // maxscore = 0
/**
 * overblijfsel van 2.1.23
 */
	@Deprecated
	normal,
}
