package nl.uu.fi.dwo.account.client;

import java.util.Map;

import com.google.gwt.user.client.Command;

public class ProfileCommand implements Command {

    String displayName;

    @Override
    public void execute() {
        // Create the new popup.
        ProfilePopupPanel popup = new ProfilePopupPanel();
        popup.setSize("300", "200");
        popup.show();
//        RootLayoutPanel root = RootLayoutPanel.get();
//        root.add(popup);
//        // Position the popup 1/3rd of the way down and across the screen, and
        // show the popup. Since the position calculation is based on the
        // offsetWidth and offsetHeight of the popup, you have to use the
        // setPopupPositionAndShow(callback) method. The alternative would
        // be to call show(), calculate the left and top positions, and
        // call setPopupPosition(left, top). This would have the ugly side
        // effect of the popup jumping from its original position to its
        // new position.
//        popup.setPopupPositionAndShow(new ProfilePopUpPanel.PositionCallback() {
//            public void setPosition(int offsetWidth, int offsetHeight) {
//                int left = (Window.getClientWidth() - offsetWidth) / 3;
//                int top = (Window.getClientHeight() - offsetHeight) / 3;
//                popup.setPopupPosition(left, top);
//            }
//        });

    }

    public void setProfile(Map<String, Object> map) {
        displayName = map.get("firstname") + " " + map.get("middlename") + " " + map.get("lastname");
    }

}
