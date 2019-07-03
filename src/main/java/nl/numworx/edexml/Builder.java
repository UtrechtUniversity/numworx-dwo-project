package nl.numworx.edexml;

import java.util.Collection;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

public interface Builder {
	public Map<String, DomUserFull> parseLeerlingen();
	public Map<String,DomSchoolClassFull> parseGroepen();

	/** Memberships of teachers and students. 
	 * key = person, value = collection of schoolclass identifiers.
	 * 
	 * @return map
	 */
	public Map<String, Collection<String>> memberships();
	public Map<String, DomUserFull> parseLeerkrachten();
}
