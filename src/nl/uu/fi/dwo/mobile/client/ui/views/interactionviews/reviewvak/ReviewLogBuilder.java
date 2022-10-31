package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.reviewvak;

import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.utils.LogBuilder;
import nl.uu.fi.dwo.mobile.utils.Logging;

public class ReviewLogBuilder extends LogBuilder {

	//private final ReviewActivity activity;
	final ActivityComponent org;
	private String[] smForeknowledge;
	public ReviewLogBuilder(ReviewActivity activity) {
		super(null);
		//this.activity = activity;
		org = (ActivityComponent) activity.delegate;
	}
	@Override
	public Logging build() {
		if (getSmObjectives() != null) {
			return new ReviewLogging(this);
		}

		return null;
	}
	@Override
	public LogBuilder setLaunchData(ObjectMap map) {
		return super.setLaunchData(map);
	}
	@Override
	public LogBuilder setSmObjectives(String[] smObjectives) {		
		return super.setSmObjectives(smObjectives);
	}
	@Override
	public LogBuilder setSmForeknowledge(String[] smForeknowledge) {
		this.smForeknowledge = smForeknowledge;
		return super.setSmForeknowledge(smForeknowledge);
	}
	@Override
	public LogBuilder setClassName(String className) {
		return super.setClassName(className);
	}
	@Override
	public LogBuilder setMaxScore(Integer maxScore) {
		return super.setMaxScore(maxScore);
	}
	String[] getSMForeKnowledge() {
		return smForeknowledge;
	}

}
