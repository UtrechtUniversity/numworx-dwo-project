package fi.dwo.server.PersistentDataManagers.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
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
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentScoPage;
import fi.dwo.commons.persistence.entities.PersistentScoPagePK;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
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
				if ( page.getBoolean("hasTitle", false)) {
					scopage.setLabel(page.getString("titel", String.valueOf(i+1)));
				} else {
					scopage.setLabel("Problem " + (i+1)); // FIXME STUB!!!
				}
				scopage.setMaxScore(number.intValue());
				boolean b = page.getBoolean("checkDocent", false);
				scopage.setCheckDocent(b);
				ScoPageManager.edit(scopage);
			}
		} catch (IOException e) {
			LOG.log(Level.WARNING, "updatePages", e);		
		}

	}

	private final static Function<PersistentScoPage, Long> key = p -> p.getId().getSequencenr();

	public static Map<Long, PersistentScoPage> getPagesMap(PersistentStudentScoContext ssc) {
		if (ssc==null) return Collections.emptyMap();
		Map<Long, PersistentScoPage> result;
		PersistentScoContext sco = new PersistentScoContext(ssc.getScoID());
		List<PersistentScoPage> data  = ScoPageManager.find(sco);
		result = data.stream().collect(Collectors.toMap( key,  Function.identity()));
		if (ssc.getPersistentHasRolePK() != null) {
			List<PersistentScoPage> pages = ScoPageManager.find(ssc);
			result.putAll(pages.stream().collect(Collectors.toMap( key,  Function.identity())));	
		}
		return result;
	}
	
	
	public static void updateSuspendData(PersistentStudentScoContext ssc, JsonObject json) {
		PersistentScoContext sco = new PersistentScoContext(ssc.getScoID());
		List<PersistentScoPage> data  = ScoPageManager.find(sco);
		List<PersistentScoPage> pages = ScoPageManager.find(ssc);
		Map<Long, PersistentScoPage> map = pages.stream()
				.collect(Collectors.toMap( key,  Function.identity()));
		json = json.getJsonObject("onsState");
		JsonArray scores = json.getJsonArray("orScores");
		if (scores != null) scores = scores.getJsonArray(0); // activiteit 0;
		JsonArray bezocht = json.getJsonArray("bezocht");
		if (bezocht != null) bezocht = bezocht.getJsonArray(0); // idem
		JsonArray nakijken = json.getJsonArray("aantalNakijken");
		JsonArray visited = json.getJsonArray("visited");
		if (visited != null)
			visited = visited.getJsonArray(0);
		if (visited == null)
			visited = bezocht;
		JsonArray goedfout = json.getJsonArray("orGoedFout");
		if( goedfout != null) goedfout = goedfout.getJsonArray(0);
		
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
				dst.setLabel(src.getLabel());
				ScoPageManager.create(dst);
				map.put(i, dst);
			}
			Integer score = null;
			int index = i.intValue();
			if (nakijken != null) {
				JsonArray scoresZelftoets = json.getJsonArray("scoresZelftoets");
				int aantal = nakijken.getInt(0);
				if (aantal > 0 && scoresZelftoets != null) {
					scoresZelftoets = scoresZelftoets.getJsonArray(0);
					score = scoresZelftoets.getJsonNumber(index).intValue();
				}
			} else {
			if (scores != null && scores.size()> index) {
				JsonValue num = scores.get(index);
				if (num != null && num.getValueType() == ValueType.NUMBER) score = ((JsonNumber) num).intValue();		
			}
			if (score == null) {
				//if (bezocht[index] ) score = 0;
				if (bezocht != null && index < bezocht.size()) {
					JsonValue b = bezocht.get(index);
					if (b == JsonValue.TRUE) score = 0;
				}
			}}

			// bepaal score uit json
			dst.setScore(score);
			// bepaal others uit json

			if (index >= visited.size())
				dst.setVisited(false);
			else
				dst.setVisited(isGedaan(visited, index));

			if (goedfout == null || index >= goedfout.size()) 
				dst.setCorrect(null);
			else {
				switch(goedfout.get(index).getValueType()) {
				case FALSE: dst.setCorrect(Boolean.FALSE); break;
				case TRUE: dst.setCorrect(Boolean.TRUE); break;
				case NULL: default:
					dst.setCorrect(null);
				}
			}
			
			dst.setOptlock(src.getOptlock());
			ScoPageManager.edit(dst);
		}
	}

	public static boolean isGedaan(JsonArray bezocht, int pagenr) {
		JsonValue v = bezocht.get(pagenr);
		boolean ok;
		if (v.getValueType() == ValueType.ARRAY) {
			ok = v.asJsonArray().isEmpty();
		} else {
			ok = v.getValueType() == ValueType.TRUE;
		}
		return ok;
	}

	public static void updateSuspendData(PersistentStudentScoContext pssc, String value) {
		JsonReader parser = Json.createReader(new StringReader(value));
		JsonObject object = parser.readObject();
		updateSuspendData(pssc,object);		
	}
	
	public static void updateDocentCorrect(PersistentStudentScoContext pssc, String value) {
		if (value == null || value.isEmpty()) {
			///emptyDocentCorrectie(pssc); /// ?
			return; 
		}
		List<PersistentScoPage> pages = ScoPageManager.find(pssc);
		Map<Long, PersistentScoPage> map = pages.stream()
				.collect(Collectors.toMap( key,  Function.identity()));
		int size = value.length();
		for (int i = 0; i < size; i++) {
			char state = value.charAt(i);
			PersistentScoPage page = map.get(Long.valueOf(i));
				if (page == null) {
					page = new PersistentScoPage();
					page.setId (new PersistentScoPagePK(pssc.getScoID(), Long.valueOf(i), pssc.getPersistentHasRolePK()));
					PersistentScoPagePK id = new PersistentScoPagePK(pssc.getScoID(), Long.valueOf(i), null);
					PersistentScoPage org = ScoPageManager.findEntity(id);
					if (org == null) continue;  // should not happen!
					page.setCheckDocent(org.getCheckDocent());
					page.setCourseID(org.getCourseID());
					page.setMaxScore(org.getMaxScore());
					map.put(page.getId().getSequencenr(), page);
				}
			switch(state) {
			case 'T': page.setDocentCorrect(Boolean.TRUE); break;
			case 'F': page.setDocentCorrect(Boolean.FALSE); break;
			default: page.setDocentCorrect(null);
			}
			page.setOptlock(pssc.getOptlock());			
			ScoPageManager.edit(page);
		}
	}
	
	
	
	public static void updateDocentCorrectie(PersistentStudentScoContext pssc, String value) {
		if  (value == null || !value.startsWith("{")) {
			emptyDocentCorrectie(pssc);
			return;
		}
		JsonReader parser = Json.createReader(new StringReader(value));
		JsonObject json = parser.readObject();
		JsonArray array = json.getJsonArray("opdrContStates");
		array = array.getJsonArray(0);
		List<PersistentScoPage> pages = ScoPageManager.find(pssc);
		Map<Long, PersistentScoPage> map = pages.stream()
				.collect(Collectors.toMap( key,  Function.identity()));
		int size = array.size();
		for (int i = 0; i < size; i++) {
			JsonValue state = array.get(i);
			PersistentScoPage page = map.get(Long.valueOf(i));
			if (state == null) {
				if (page != null) {
					page.setCorrectie(null);
				}
			} else {
				if (page == null) {
					page = new PersistentScoPage();
					page.setOptlock(Long.valueOf(0)); // NOT Null here, not used.
					page.setId (new PersistentScoPagePK(pssc.getScoID(), Long.valueOf(i), pssc.getPersistentHasRolePK()));
					PersistentScoPagePK id = new PersistentScoPagePK(pssc.getScoID(), Long.valueOf(i), null);
					PersistentScoPage org = ScoPageManager.findEntity(id);
					if (org == null) continue;  // should not happen!
					page.setCheckDocent(org.getCheckDocent());
					page.setCourseID(org.getCourseID());
					page.setMaxScore(org.getMaxScore());
					map.put(page.getId().getSequencenr(), page);
				}
				page.setCorrectie(sumCorrectie(state));
				Boolean result = sumDocentCorrectie(state);
				if (result != null) page.setCheckDocent(result);
			}
			ScoPageManager.edit(page);
		}
		
	}

	private static int sumCorrectie(JsonValue value) {
		int sum = 0;
		if (value == null) return sum;
		ValueType type = value.getValueType();
		switch (type) {
		case ARRAY:
			JsonArray array = value.asJsonArray();
			for (int i = 0; i < array.size(); i++) {
				sum += sumCorrectie(array.get(i));
			}
			break;
		case OBJECT:
			JsonObject o = value.asJsonObject();
			JsonValue s;
			s = o.get("interactiePanelStates");
			sum += sumCorrectie(s);
			s = o.get("reviewInteractieData");
			sum += sumCorrectie(s);
			sum += o.getInt("reviewScoreCorrectie",0);
			break;
		default:
			break;		
		}
		return sum;
	}

	private static void emptyDocentCorrectie(PersistentStudentScoContext ssc) {
		List<PersistentScoPage> pages = ScoPageManager.find(ssc);
		PersistentScoContext sco = ScoContextManager.findEntity(ssc.getScoID());
		List<PersistentScoPage> template = ScoPageManager.find(sco);
		for (PersistentScoPage p: pages) {
			p.setCorrectie(null);
			p.setDocentCorrect(null);
			p.setCheckDocent(template.get(p.getId().getSequencenr().intValue()).getCheckDocent());
			ScoPageManager.edit(p);
		}
	}
	
	private static Boolean sumDocentCorrectie(JsonValue value) {
		if (value == null) return null;
		ValueType type = value.getValueType();
		Boolean result;
		switch(type) {
		case ARRAY:
			JsonArray array = value.asJsonArray();
			for(int i = 0; i < array.size(); i++) {
				result = sumDocentCorrectie( array.get(i));
				if (Boolean.TRUE.equals(result)) return Boolean.TRUE; // 
			}
			break;
		case OBJECT: 
			JsonObject o = value.asJsonObject();
			JsonValue s;
			s = o.get("checkDocent");
			if (s != null) {
				return s == JsonValue.TRUE;				
			}
			s = o.get("interactiePanelStates");
			result = sumDocentCorrectie(s);
			if (Boolean.TRUE.equals(result)) return Boolean.TRUE; // 
			s = o.get("reviewInteractieData");
			result = sumDocentCorrectie(s);
			if (Boolean.TRUE.equals(result)) return Boolean.TRUE; // 
			break;
		default:
			break;		
		}
		return null;
	}
	
	
}
