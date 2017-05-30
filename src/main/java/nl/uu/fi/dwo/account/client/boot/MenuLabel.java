package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Label;

/**
 * Menu label for the DWO menus.
 *
 * @author G.A.J. van der Plas
 */
public class MenuLabel extends Label implements MouseOverHandler, MouseOutHandler {

    public MenuLabel() {
        super();
        this.addMouseOverHandler(this);
        this.addMouseOutHandler(this);
    }

    public void onMouseOver(final MouseOverEvent event) {
        this.getElement().getStyle().setBackgroundColor("royalblue");

    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        // TODO Auto-generated method stub
        this.getElement().getStyle().setBackgroundColor("#c2d5e1");
    }
}
