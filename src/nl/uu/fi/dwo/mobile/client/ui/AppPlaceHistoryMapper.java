package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ReloginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SearchPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.FlatModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.c;
import nl.uu.fi.dwo.mobile.client.ui.places.guest;
import nl.uu.fi.dwo.mobile.client.ui.places.s;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * History mapper
 * 
 * @author Danny Hendrix
 * 
 */
@WithTokenizers(
{ SelectModulePlace.Tokenizer.class, ViewModulePlace.Tokenizer.class, LoginPlace.Tokenizer.class, ReloginPlace.Tokenizer.class, TreeModulePlace.Tokenizer.class, FlatModulePlace.Tokenizer.class, c.Tokenizer.class, s.Tokenizer.class, guest.Tokenizer.class, SearchPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper
{

}
