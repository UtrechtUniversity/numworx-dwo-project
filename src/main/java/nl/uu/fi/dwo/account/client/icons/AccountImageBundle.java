package nl.uu.fi.dwo.account.client.icons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
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
}