package fi.dwo.server.PersistentDataManagers.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;

import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentScoPage;
import fi.dwo.commons.persistence.entities.PersistentScoPagePK;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
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

	public static void updateSuspendData(PersistentStudentScoContext ssc, JsonObject json) {
		PersistentScoContext sco = new PersistentScoContext(ssc.getScoID());
		List<PersistentScoPage> data  = ScoPageManager.find(sco);
		List<PersistentScoPage> pages = ScoPageManager.find(ssc);
		Function<PersistentScoPage, Long> key = p -> p.getId().getSequencenr();
		Map<Long, PersistentScoPage> map = pages.stream()
				.collect(Collectors.toMap( key,  p->p));
		json = json.getJsonObject("onsState");
		JsonArray scores = json.getJsonArray("orScores");
		for (PersistentScoPage src: data) {
			Long i = src.getId().getSequencenr();
			PersistentScoPage dst = map.get(i);
			if (dst == null) {
				PersistentScoPagePK pk = new PersistentScoPagePK();
				pk.setScoID(src.getId().getScoID());
				pk.setSequencenr(i);
				pk.setHasRolePK(ssc.getPersistentHasRolePK());
				dst = new PersistentScoPage();
				dst.setId(pk);
				dst.setCheckDocent(src.getCheckDocent());
				dst.setCourseID(src.getCourseID());
				dst.setMaxScore(src.getMaxScore());
				dst.setOptlock(src.getOptlock());
				dst.setCorrectie(null);
				ScoPageManager.create(dst);
				map.put(i, dst);
			}
			Integer score = null;
			int index = i.intValue();
			if (scores != null && scores.size()> index) {
				JsonNumber num = scores.getJsonNumber(index);
				if (num != null) score = num.intValue();
			}
			// bepaal score uit json
			dst.setScore(score);
			// bepaal correctie uit json
		}
	}

	public static void updateSuspendData(PersistentStudentScoContext pssc, String value) {
		JsonReader parser = Json.createReader(new StringReader(value));
		JsonObject object = parser.readObject();
		updateSuspendData(pssc,object);		
	}
	
}
