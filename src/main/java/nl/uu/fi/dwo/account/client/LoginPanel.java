/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author Gert van der Plas
 */
public class LoginPanel extends PopupPanel {

    public LoginPanel() {
        super(true);
        this.setAutoHideEnabled(false);
        this.setModal(true);
        VerticalPanel vPanel = new VerticalPanel();
        Label userLabel = new Label("username");
        Label pwLabel = new Label("password");
        vPanel.add(userLabel);
        vPanel.add(pwLabel);
        VerticalPanel v2Panel = new VerticalPanel();
        TextBox userText = new TextBox();
        userText.setText("");
        TextBox pwText = new TextBox();
        pwText.setText("");
        v2Panel.add(userText);
        v2Panel.add(pwText);
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add(vPanel);
        hPanel.add(v2Panel);
        HorizontalPanel h2Panel = new HorizontalPanel();
        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("CANCEL");
        h2Panel.add(cancelBtn);
        h2Panel.add(okBtn);
        VerticalPanel v3Panel = new VerticalPanel();
        v3Panel.add(hPanel);
        v3Panel.add(h2Panel);
        this.add(v3Panel);
    }
}
