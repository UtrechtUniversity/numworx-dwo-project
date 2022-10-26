package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.reviewvak;

import java.util.Map;

import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.utils.LogBuilder;
import nl.uu.fi.dwo.mobile.utils.Logging;

public class ReviewLogBuilder extends LogBuilder {

	private final ReviewActivity activity;
	public ReviewLogBuilder(ReviewActivity activity) {
		super(null);
		this.activity = activity;
	}
	@Override
	public Logging build() {
		// super.build geeft een NPE
		return null;
	}
	@Override
	public LogBuilder setLaunchData(ObjectMap map) {
		// TODO Auto-generated method stub
		return super.setLaunchData(map);
	}
	@Override
	public LogBuilder setSmObjectives(String[] smObjectives) {
		// TODO Auto-generated method stub
		return super.setSmObjectives(smObjectives);
	}
	@Override
	public LogBuilder setSmForeknowledge(String[] smForeknowledge) {
		// TODO Auto-generated method stub
		return super.setSmForeknowledge(smForeknowledge);
	}
	@Override
	public LogBuilder setClassName(String className) {
		// TODO Auto-generated method stub
		return super.setClassName(className);
	}
	@Override
	public LogBuilder setMaxScore(Integer maxScore) {
		// TODO Auto-generated method stub
		return super.setMaxScore(maxScore);
	}

}
