package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class MainMessageView extends Composite{

    private static final Logger LOG = Logger.getLogger(MainMessageView.class.getName());
    
    interface MyUiBinder extends UiBinder<Widget, MainMessageView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    
    @UiField
    Label msgLabel;
    @UiField
    PushButton okButton;

    public MainMessageView(){
        initWidget(uiBinder.createAndBindUi(this));
//        loginPresenter = lp;
        //controller must be before clicks occur
//        loginBtn.addClickHandler(this);
    }
    
    public void setMsg(String msg){
        msgLabel.setText(msg);    
    }
    
    public void addOkClickHandler(ClickHandler handler) {
        okButton.addClickHandler(handler);
    }
}
