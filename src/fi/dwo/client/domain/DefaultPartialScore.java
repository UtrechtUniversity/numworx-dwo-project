package fi.dwo.client.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import fi.beans.scorm.PartialScoreIF;
import fi.beans.scorm.SCORM12APIInterface;

public class DefaultPartialScore implements PartialScoreIF {

	public List getScoreMapList(SCORM12APIInterface api) {
		HashMap result = new HashMap();
		String raw = api.LMSGetValue("cmi.core.score.raw");
		result.put(SCORE_RAW, raw);
		String max = api.LMSGetValue("cmi.core.score.max");
		if(null == max || "".equals(max)) max = "100";
		result.put(DESCRIPTION, "resultaat");
		result.put(LOCATION, "");
		return Collections.singletonList(result);
	}

}
