package nl.uu.fi.dwo.account.client;


import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import fi.dwo.rest.dom.entities.DomUser;
import java.util.Map;

public class ProfileCommand implements Command {

    String displayName;

    @Override
    public void execute() {
        // Create the new popup.
        PopupPanel popup = new PopupPanel();
        //popup.setSize("500", "400");
        DomUser testUser = new DomUser();
        testUser.setGivenName("Gert");
        testUser.setInsertion("van der");
        testUser.setFamilyName("Plas");
        testUser.setUserName("project_gert");
        testUser.setSingleSchool(false);
//        testUser.setId();
        SchoolLoginPanel panel = new SchoolLoginPanel(testUser);
        panel.setPopup(popup);
        panel.setSize("300", "200");
        popup.add(panel);
        popup.center();
    }

    public void setProfile(Map<String, Object> map) {
        displayName = map.get("firstname") + " " + map.get("middlename") + " " + map.get("lastname");
    }

}
