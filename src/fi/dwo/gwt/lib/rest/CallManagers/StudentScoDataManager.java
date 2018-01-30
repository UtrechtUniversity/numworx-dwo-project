package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.Collection;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.json.client.JSONValue;

public interface StudentScoDataManager {

	public abstract Promise<Map<String, String>> getValues(DomScoContext sco, DomSchoolClassId schoolClassID, 
			DomContext domContext, Collection<String> keys);

	@Deprecated
	public abstract Promise<Boolean> setValues(DomScoContext sco, DomSchoolClassId schoolClassID, DomContext domContext,
			Map<String, String> map);

	default Promise<String> setValuesETag(DomScoContext sco, DomSchoolClassId schoolClassID, DomContext domContext,
			Map<String, String> map) {
		return setValues(sco, schoolClassID, domContext, map).map(b -> { if(b) return null; throw new RuntimeException(); });
	}

	Promise<JSONValue> getJSONLaunchDataBytes(DomScoContext id, DomDwoProfile value, DomSchoolClassId schoolClassID, DomContext context);

	default Promise<?> patchValues(DomScoContext sco, DomSchoolClassId schoolClassID, DomContext context,
			Map<String, String> map) {
		return Promises.failed(new IllegalArgumentException());
	}
}