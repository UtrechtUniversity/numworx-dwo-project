package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.reviewvak;

import java.util.Map;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.client.sco.SMLogger;
import nl.uu.fi.dwo.mobile.utils.Logging;

public class ReviewLogging implements Logging {

	final Logging delegate;
	
	public ReviewLogging(ReviewLogBuilder reviewLogBuilder) {
		delegate = reviewLogBuilder.org.getLogging();
		delegate.setClassName(reviewLogBuilder.getClassName());
		delegate.setSMObjectives(reviewLogBuilder.getSmObjectives());
		delegate.setSMForeknowledge(reviewLogBuilder.getSMForeKnowledge());
		delegate.setMaxScore(reviewLogBuilder.getMaxScore());
		delegate.setLogID(reviewLogBuilder.getLogID());
	}

	@Override
	public void log(Map<String, ?> parameters) {
		((Map)parameters).put("verb", SMLogger.CORRECTED);
		delegate.updateLog(parameters);
	}
	

	@Override
	public void updateLog(Map<String, ?> map) {
		log(map);
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		delegate.setCommunicationRoot(comRoot);
	}

	@Override
	public void setLogID(String string) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setClassName(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLogObjectives(boolean[][] objectives) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSMObjectives(String[] objectives) {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] getSMObjectives() {
		return delegate.getSMObjectives();
	}

	@Override
	public void setMaxScore(int max) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLogOption(boolean logOption) {
		// TODO Auto-generated method stub

	}

	public void setSMForeknowledge(String[] foreknowledge) {
		delegate.setSMForeknowledge(foreknowledge);
	}

	public void getStateHook(Map<String, Object> h) {
		delegate.getStateHook(h);
	}

}
