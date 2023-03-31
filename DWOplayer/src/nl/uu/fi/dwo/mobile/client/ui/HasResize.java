package nl.uu.fi.dwo.mobile.client.ui;


/**
 * Interface to notify parent container that a child has changed size.
 * Request for relayout.
 * @author Wim van Velthoven
 *
 */
public interface HasResize {
	void resize();
}
