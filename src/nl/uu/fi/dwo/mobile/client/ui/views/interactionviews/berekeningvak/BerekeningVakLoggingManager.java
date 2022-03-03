package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.utils.LogBuilder;
import nl.uu.fi.dwo.mobile.utils.Logging;

public class BerekeningVakLoggingManager {

	protected Logging logging;
	private BerekeningVak berekeningVak;
	
	public BerekeningVakLoggingManager(BerekeningVak berekeningVak, BerekeningVakSettings settings, ActivityComponent activity) {
		this.berekeningVak = berekeningVak;
		if(settings.smObjectives() !=null) {
			LogBuilder dwoLogger = new LogBuilder(activity).setLogOption(settings.logOption());
			dwoLogger.setMaxScore(settings.scoreMax());
			dwoLogger.setLogIDLabel(settings.logIDLabel());
			dwoLogger.setLogID(settings.logID());
			dwoLogger.setClassName("fi.wiskopdr.BerekeningVak");
			dwoLogger.setLogObjectives(settings.logObjectives()).setSmObjectives(settings.smObjectives()).setSmForeknowledge(settings.smForeknowledge());
			dwoLogger.setTeltMee(settings.teltMee());
			logging = dwoLogger.build();
		}
	}
	
	public void logAttempt()	{
		if (logging != null)	{
			Map<String, Object> map = buildLoggingMap();
			logging.log(map);
		}
	}

	Map<String, Object> buildLoggingMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("verb", "http://adlnet.gov/expapi/verbs/answered");
		map.put("response",
			"<math xmlns='http://www.w3.org/1998/Math/MathML'>"
				+ berekeningVak.geefVakRegel(0).geefFormuleEditor().getMainRegel().toMathML() + "</math>");
		map.put("score", Collections.singletonMap("raw", berekeningVak.getScore()));
		if (berekeningVak.isCorrect() != null)
		{
			map.put("success", berekeningVak.isCorrect());
		}
		map.put("formula", berekeningVak.geefVakRegel(0).geefFormuleEditor().getMainRegel().toString());
		//map.put("step", getStep());
		//String feedback = getFeedback();
		//if (feedback != null && !feedback.isEmpty())
		//	map.put("feedback", feedback);
		return map;
	}
}
