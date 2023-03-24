
package nl.uu.fi.dwo.lms.gwtclient.gwt.results;
/**
 * Results presenters. Only 4 are needed. The resultTree is passed
 * from the initial resultsPresenter down to the others via 
 * events. Conversion to a javascript objecttree occurs in the Viewers.
 * While this is slightly inefficient. Future implementations may 
 * pass this object separately in the number of tree nodes become 
 * very large.
*/