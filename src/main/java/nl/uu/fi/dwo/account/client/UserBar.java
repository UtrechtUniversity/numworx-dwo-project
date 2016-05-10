package nl.uu.fi.dwo.account.client;

import nl.uu.fi.dwo.account.client.text.Text;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import fi.dwo.rest.dom.entities.DomUserFull;

public class UserBar extends Composite {

    private final MenuBar top = new MenuBar();;
    private final MenuBar items = new MenuBar(true);
    private final Text rb = Text.constants;
    private final ProfileCommand profileCmd = new ProfileCommand();
    private final SchoolLoginCommand schoolLoginCmd = new SchoolLoginCommand();
    private final SchoolClassStudentCommand schoolClassCmd = new SchoolClassStudentCommand();
    private DomUserFull user;

    public UserBar() {
        init();
    }

    private void init() {
        initWidget(top);

        MenuItem item;
        MenuItem item2;
        MenuItem item3;
        final int correctie = 10; // width popup 
        item = new MenuItem("<i class='fa fa-navicon fa-2x'></i>", true, items) {
            @Override
            public int getAbsoluteLeft() {
                int w1 = items.getOffsetWidth();
                int w2 = this.getOffsetWidth();
                return super.getAbsoluteLeft() - w1 + w2 - correctie;
            }

        };

        top.addItem(item);

        item = new MenuItem(rb.GUIMNU_MY_PROFILE(), profileCmd);

        items.addItem(item);
        item2 = new MenuItem(rb.GUIMNU_MY_SCHOOLLOGINS(), schoolLoginCmd);

        items.addItem(item2);
        item3 = new MenuItem(rb.GUIMNU_MY_SCHOOLCLASSES(), schoolClassCmd);

        items.addItem(item3);
    }
}
