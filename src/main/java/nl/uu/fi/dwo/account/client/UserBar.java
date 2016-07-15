package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import fi.dwo.rest.locale.DwoLocalesForGWT;

public class UserBar extends Composite {

    private final MenuBar top = new MenuBar();;
    private final MenuBar items = new MenuBar(true);
    private final ProfileCommand profileCmd = new ProfileCommand();
    private final SchoolLoginCommand schoolLoginCmd = new SchoolLoginCommand();
    private final SchoolClassStudentCommand schoolClassCmd = new SchoolClassStudentCommand();

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

        item = new MenuItem(DwoLocalesForGWT.instance.GUI_MyProfile(), profileCmd);
        items.addItem(item);

//        item2 = new MenuItem(DwoLocalesForGWT.instance.GUI_MySchoolLogins(), schoolLoginCmd);
//        items.addItem(item2);

        item3 = new MenuItem(DwoLocalesForGWT.instance.GUI_MySchoolClasses(), schoolClassCmd);
        items.addItem(item3);
    }
}
