package fi.dwo.server.PersistentDataManagers.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;

import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentScoPage;
import fi.dwo.commons.persistence.entities.PersistentScoPagePK;
import fi.dwo.server.PersistentDataManagers.core.ScoPageManager;

public class ScoPageUtilManager {
	
	static final private Logger LOG = Logger.getLogger(ScoPageUtilManager.class.getName());

	private ScoPageUtilManager() {
	}

	public static void updatePages(PersistentScoContext sc, PersistentScoData sd) {
		byte[] launchData = sd.getLaunchdatabytes();
        ByteArrayInputStream inStream = new ByteArrayInputStream(launchData);
        try {
    		List<PersistentScoPage> list = ScoPageManager.find(sc);
			GZIPInputStream gzIn = new GZIPInputStream(inStream);
			JsonReader reader = Json.createReader(gzIn);
			JsonObject object = reader.readObject();
			int aantalOpdrachten = Integer.parseInt(object.getString("aantalOpdrachten_1", "0"));
			while (list.size() > aantalOpdrachten) {
				ScoPageManager.destroy(list.remove(list.size()-1));
			}
			while (list.size() < aantalOpdrachten) {
				PersistentScoPage scopage = new PersistentScoPage();
				PersistentScoPagePK pk = new PersistentScoPagePK(sc.getScoID(), Long.valueOf(list.size()), null);
				scopage.setId(pk);
				list.add(scopage);
			}

			for(int i = 0; i < list.size(); i++) {
				PersistentScoPage scopage = list.get(i);
				scopage.setCourseID(sc.getCourseID());
				scopage.setOptlock(sd.getOptlock());
				JsonObject page = object.getJsonObject("opdracht_1_" + (i+1));
				JsonNumber number = page.getJsonNumber("scoreMax");
				scopage.setMaxScore(number.intValue());
				boolean b = page.getBoolean("checkDocent", false);
				scopage.setCheckDocent(b);
				ScoPageManager.edit(scopage);
			}
		} catch (IOException e) {
			LOG.log(Level.WARNING, "updatePages", e);		
		}

	}
	
}
