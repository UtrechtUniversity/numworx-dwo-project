package fi.dwo.dwojapplet.domain;

import java.awt.Component;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.beans.scorm.PartialScoreIF;
import fi.beans.scorm.SCORM12APIInterface;
import fi.dwo.commons.system.TextMapper;

public class DefaultPartialScore implements PartialScoreIF {

	private Component applet;

        @Override
	public List getScoreMapList(SCORM12APIInterface api) {
		HashMap result = new HashMap();
		String raw = api.LMSGetValue("cmi.core.score.raw");
		result.put(SCORE_RAW, raw);
		String max = api.LMSGetValue("cmi.core.score.max");
		if(null == max || "".equals(max)) max = "100";
		result.put(SCORE_MAX, max);
		result.put(DESCRIPTION, TextMapper.getText("resultaat"));
		result.put(LOCATION, "");
		String time = api.LMSGetValue("cmi.core.session_time");
		result.put(SESSION_TIME, time);
		return Collections.singletonList(result);
	}

        @Override
	public Component getContentPage() {
		return applet;
	}

	public DefaultPartialScore(Component applet) {
		super();
		this.applet = applet;
	}

        @Override
	public Map getScoreObjectivesMap(SCORM12APIInterface api) {
		return null;
	}
}
