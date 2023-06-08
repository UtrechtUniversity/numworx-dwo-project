package nl.uu.fi.dwo.lms.gwtclient.gwt.icons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 *
 * @author G.A.J. van der Plas
 */
public interface AccountImageBundle extends ClientBundle {

    public AccountImageBundle instance = GWT.create(AccountImageBundle.class);

    @Source("delete.gif")
    public ImageResource delete();

    @Source("empty.gif")
    public ImageResource empty();

    @Source("student.png")
    public ImageResource student();

    @Source("numworx-logo-wit-1.png")
    public ImageResource dwoLogo();
//@Source("logo-Numworx-grijs2.svg")
//public SVGResource dwoLogo();

    @Source("settings.png")
    public ImageResource settingMenuIcon();
}
