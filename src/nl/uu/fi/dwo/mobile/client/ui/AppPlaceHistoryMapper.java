package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ProfilePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * History mapper
 * 
 * @author Danny Hendrix
 * 
 */
@WithTokenizers(
{ SelectModulePlace.Tokenizer.class, ViewModulePlace.Tokenizer.class, LoginPlace.Tokenizer.class, ProfilePlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper
{

}
