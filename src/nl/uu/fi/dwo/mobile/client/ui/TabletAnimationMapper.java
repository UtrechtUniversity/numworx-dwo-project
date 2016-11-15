package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.mobile.client.ui.places.FlatModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;

import com.google.gwt.place.shared.Place;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.mvp.client.AnimationMapper;

/**
 * Defines animations between places
 * 
 * @author Danny Hendrix
 * 
 */
public class TabletAnimationMapper implements AnimationMapper
{
	@Override
	public Animation getAnimation(Place oldPlace, Place newPlace)
	{
		return null; // geen animaties, deze is gebroken. AnimationEnd event ontbreekt ineens. why?
		
/*		if (oldPlace instanceof ViewModulePlace && newPlace instanceof SelectModulePlace)
			return Animation.SLIDE_REVERSE;
		if (oldPlace instanceof ViewModulePlace && newPlace instanceof TreeModulePlace)
			return Animation.SLIDE_REVERSE;
		if (oldPlace instanceof SelectModulePlace && newPlace instanceof TreeModulePlace)
			return Animation.SLIDE_REVERSE;
		if (oldPlace instanceof SelectModulePlace && newPlace instanceof FlatModulePlace)
			return Animation.SLIDE_REVERSE;
		if (oldPlace instanceof SelectModulePlace && newPlace instanceof ProfilePlace)
			return Animation.SLIDE_REVERSE;
		if (oldPlace instanceof SelectModulePlace && newPlace instanceof LoginPlace)
			return Animation.SLIDE_REVERSE;
		if (oldPlace instanceof ProfilePlace && newPlace instanceof LoginPlace)
			return Animation.SLIDE_REVERSE;
		if (oldPlace instanceof TreeModulePlace && newPlace instanceof LoginPlace)
			return Animation.SLIDE_REVERSE;
		if (oldPlace instanceof TreeModulePlace && newPlace instanceof ProfilePlace)
			return Animation.SLIDE_REVERSE;
		if (oldPlace instanceof FlatModulePlace && newPlace instanceof ProfilePlace)
			return Animation.SLIDE_REVERSE;
		if (oldPlace == null || oldPlace.getClass() .equals( newPlace.getClass()) )
			return null;
		
		return Animation.SLIDE;
*/	}

}
