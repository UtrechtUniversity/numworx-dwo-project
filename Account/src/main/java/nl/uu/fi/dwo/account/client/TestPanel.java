/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author Gert van der Plas
 */
public class TestPanel extends VerticalPanel  implements ClickHandler{
    
    /**
     *
     */
    public TestPanel(){
        Button b = new Button("Test");
//        b.addAttachHandler()
        add(b);        
    }

    @Override
    public void onClick(ClickEvent event) {
        
    }
}
