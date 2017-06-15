package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

/**
 *
 * @author Gert van der Plas
 */
public class MainMessageView extends Composite {

    private static final Logger LOG = Logger.getLogger(MainMessageView.class.getName());
    
    interface MyUiBinder extends UiBinder<Widget, MainMessageView> {}
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    
    @UiField
    Label msgLabel;
    @UiField
    Button okButton;

    static {
        //Initialize an Exception translator.
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
    }
    MainMessageView(String msg){
        msgLabel.setText(msg);
        initWidget(uiBinder.createAndBindUi(this));
//        loginPresenter = lp;
        //controller must be before clicks occur
//        loginBtn.addClickHandler(this);
        
    }
    
}
