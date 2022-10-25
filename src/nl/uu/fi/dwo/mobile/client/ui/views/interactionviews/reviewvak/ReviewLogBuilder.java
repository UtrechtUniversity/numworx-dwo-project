package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.reviewvak;

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

}
