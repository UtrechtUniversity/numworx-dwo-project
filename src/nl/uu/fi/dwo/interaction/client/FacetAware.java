package nl.uu.fi.dwo.interaction.client;

import java.util.List;

public interface FacetAware {

	enum Type {
		formula,
		integer,
		decimal,
		coordinate,
	}

	/**
	 * Request the responses.
	 * @param responses output parameter, add responses
	 */
	void getResponses(List<String> responses);
	
}
