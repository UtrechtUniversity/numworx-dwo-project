package nl.uu.fi.dwo.mobile.utils;

import java.util.List;
import java.util.Vector;

import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class FacetHelper {

	private ObjectMap launchData;
	private List<FacetAware.Type> list;
	
	public FacetHelper(ObjectMap launchData) {
		this.launchData = launchData;
	}

	public List<FacetAware.Type> getResponseTypes() {
		if(list == null) {
			list = new Vector<FacetAware.Type>();
			ObjectMap customInteraction = launchData.getObjectMap("customInteraction");
			if(customInteraction != null) {
				String[] resourceTypes = customInteraction.getStringArray("resourseTypes");
				for (int i = 0; i < resourceTypes.length; i++) {
					list.add(FacetAware.Type.valueOf(resourceTypes[i]));
				}
			}
		}
		return list;
	}
}
