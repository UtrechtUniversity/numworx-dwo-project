package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import static org.junit.Assert.*;

import org.junit.Test;

import static nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.ScoreWidget.Util.*;

public class ScoreWidgetTest {

	@Test
	public void testParsePaginaNrs() {
		assertTrue(parsePaginaNrs("").isEmpty());
	}

}
