package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.LogoutPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.MaybeLogout;
import nl.uu.fi.dwo.mobile.client.ui.places.ReloginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SearchPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewCoursePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ClassesPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.Exam;
import nl.uu.fi.dwo.mobile.client.ui.places.last;
import nl.uu.fi.dwo.mobile.client.ui.places.m;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.c;
import nl.uu.fi.dwo.mobile.client.ui.places.cc;
import nl.uu.fi.dwo.mobile.client.ui.places.guest;
import nl.uu.fi.dwo.mobile.client.ui.places.s;
import nl.uu.fi.dwo.mobile.client.ui.places.up;
import nl.uu.fi.dwo.mobile.client.ui.places.xc;
import nl.uu.fi.dwo.mobile.client.ui.places.xs;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * History mapper
 * 
 * @author Danny Hendrix
 * 
 */
@WithTokenizers(
{ Exam.Tokenizer.class, ViewModulePlace.Tokenizer.class, ViewCoursePlace.Tokenizer.class, LoginPlace.Tokenizer.class, ReloginPlace.Tokenizer.class, 
	TreeModulePlace.Tokenizer.class, c.Tokenizer.class, xc.Tokenizer.class, s.Tokenizer.class, xs.Tokenizer.class, guest.Tokenizer.class, 
	/*SearchPlace.Tokenizer.class, */ LogoutPlace.Tokenizer.class, MaybeLogout.Tokenizer.class, ClassesPlace.Tokenizer.class, cc.Tokenizer.class, 
	last.Tokenizer.class, m.Tokenizer.class, up.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper
{

}
