package fi.beans.scorm;

import java.applet.Applet;
import java.lang.reflect.Constructor;

public abstract class Scorm
{
  /**
  * Method used to retrieve an implemenation of SCORM12APIInterface.
  * @param applet The Applet invoking this method.
  * @return SCORM12APIInterface the appropriate implementation of SCORM12APIInterface determined by using Applet.getParameter( "API" ).
     * @throws java.lang.Exception
  */
  public static SCORM12APIInterface findAPI( Applet applet ) throws Exception
  {
    if (applet.getParent()instanceof SCORM12APIInterface)
      return (SCORM12APIInterface)applet.getParent();

    String API = applet.getParameter( "API" );
    if (API != null) {
      // sequence for new API(applet);
      Class c = Class.forName( API );
      Constructor cc = c.getDeclaredConstructor(new Class[] { Applet.class } );
      return (SCORM12APIInterface) cc.newInstance(new Object[] { applet } );
    }
    //Hier komt de default Scorm.findAPI(applet)....
    try {
		return new JSScormAPI(applet);
	} catch (RuntimeException r)
	{
		return null; 
	}
    catch (NoClassDefFoundError e) { // package netstape not found.
		return null;
	}
    }
}