package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import nl.uu.fi.dwo.interaction.client.InteractionView;

public interface InteractionViewWithMisconceptions extends InteractionView{

	public int[][] getMeasuredMisconceptions();
	
	public int[][] getPossibleMisconceptions();
}
