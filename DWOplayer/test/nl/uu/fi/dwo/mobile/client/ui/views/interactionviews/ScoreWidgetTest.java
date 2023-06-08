package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import org.junit.Test;

import static nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.ScoreWidget.Util.*;

public class ScoreWidgetTest {

	@Test
	public void testParsePaginaNrs() {
		assertTrue(parsePaginaNrs("").isEmpty());
	}

	@Test
	public void eenPagina() {
		String string = "123";
		Collection<Integer> result = parsePaginaNrs(string);
		assertEquals(string, Collections.singleton(123), result);
	}
	
	@Test
	public void meerPaginas() {
		String string = "1,2,3";
		Collection<Integer> want = new TreeSet<>(Arrays.asList(1,2,3));
		assertEquals(string, want, parsePaginaNrs(string));
	}
	@Test
	public void spacesPaginas() {
		String string = "1 , 2 - 3";
		Collection<Integer> want = new TreeSet<>(Arrays.asList(1,2,3));
		assertEquals(string, want, parsePaginaNrs(string));
	}
	
	@Test
	public void rangePaginas() {
		String string = "1-3";
		Collection<Integer> want = new TreeSet<>(Arrays.asList(1,2,3));
		assertEquals(string, want, parsePaginaNrs(string));
	}

}
