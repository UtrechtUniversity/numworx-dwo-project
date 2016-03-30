/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author G.A.J. van der Plas
 */
public class ProfilePanel extends VerticalPanel implements ClickHandler {

    ProfilePanel(){
//        add(uiBinder.createAndBindUi(this));
        init();
    }
    
    private void init() {
        this.setSize("400", "500");
////        https://groups.google.com/forum/#!topic/google-web-toolkit/aIrm3mzaeyE
//// Grids must be sized explicitly, though they can be resized later.
        Grid g = new Grid(2, 5);
        // Put some values in the grid cells.
        g.setText(0,0, "login");
        TextBox login = new TextBox();
        login.setText("gert");
        g.setWidget(0,1, login);

        // Just for good measure, let's put a button in the center.
        Button okBtn = new Button("OK");
        okBtn.addClickHandler(this);
        g.setWidget(1, 0, okBtn);
        Button cnlBtn = new Button("CANCEL");
        cnlBtn.addClickHandler(this);
        g.setWidget(1, 1, cnlBtn);

        // You can use the CellFormatter to affect the layout of the grid's cells.
        //g.getCellFormatter().setWidth(0, 2, "256px");
        RootPanel.get().add(g);
 //       setWidget(new Label("Click outside of this popup to close it"));
    }
    
     @Override
    public void onClick(ClickEvent event) {
        Window.alert("OK!");
    }
}
