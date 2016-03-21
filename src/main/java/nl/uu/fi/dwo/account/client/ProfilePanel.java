/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 *
 * @author G.A.J. van der Plas
 */
public class ProfilePanel extends PopupPanel implements EntryPoint {

    ProfilePanel(){
               logger.fine("Creating SamplePopup");
               add(uiBinder.createAndBindUi(this));        
        init();
    }
    
    private void init() {
//        https://groups.google.com/forum/#!topic/google-web-toolkit/aIrm3mzaeyE
// Grids must be sized explicitly, though they can be resized later.
        Grid g = new Grid(5, 5);
        // Put some values in the grid cells.
        for (int row = 0; row < 5; ++row) {
            for (int col = 0; col < 5; ++col) {
                g.setText(row, col, "" + row + ", " + col);
            }
        }

        // Just for good measure, let's put a button in the center.
        g.setWidget(2, 2, new Button("Does nothing, but could"));

        // You can use the CellFormatter to affect the layout of the grid's cells.
        g.getCellFormatter().setWidth(0, 2, "256px");
        RootPanel.get().add(g);
//        setWidget(new Label("Click outside of this popup to close it"));
    }

    public void onModuleLoad() {
        init();
    }
}
