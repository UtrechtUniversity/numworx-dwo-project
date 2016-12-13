package nl.uu.fi.dwo.account.client.boot.Results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * 
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public class ResultPanel extends Composite {

    private static final Logger LOG = Logger.getLogger(ResultPanel.class.getName());

    interface MyUiBinder extends UiBinder<Widget, ResultPanel> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

//    @UiField TextBox usernameText;
//    @UiField PasswordTextBox passwordTextBox;
//    @UiField Button loginBtn;
//    @UiField(provided = true)
//CellTable<MyType> cellTable;
    
    public ResultPanel() {
        initWidget(uiBinder.createAndBindUi(this));        
    }

}
