package nl.uu.fi.dwo.account.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.google.gwt.user.client.ui.RootPanel;
import fi.dwo.gwt.lib.rest.CallManagers.DWO2RestManager;
import fi.dwo.rest.dom.entities.DomRole;
import fi.dwo.rest.dom.entities.DomUserFull;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Account implements EntryPoint {

    private static final Logger LOG = Logger.getLogger(Account.class.getName());
    DomUserFull user = null;
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";
    private DWO2RestManager handler = new DWO2RestManager();

    @Override
    public void onModuleLoad() {
        LOG.log(Level.INFO, "onModuleLoad...");
        HeaderPanel header = new HeaderPanel();
        RootPanel.get().add(header);

        header.setCenter("Account");
        if (user == null) {
        LOG.log(Level.INFO, "filling in test user...");
            DomUserFull curUser = new DomUserFull();
            curUser.setGivenName("Wim");
            curUser.setInsertion("van");
            curUser.setFamilyName("Velthoven");
            curUser.setId(null);
            curUser.setSingleSchool(false);
            curUser.setPassword("passw"); //md5Hash = d79096188b670c2f81b7001f73801117
            curUser.setUserName("project_wim");
            user=curUser;
            //Try to login and fetch the user
        LOG.log(Level.INFO, "filled in test user.");
        LOG.log(Level.INFO, "Getting roles...");
        handler.getRoles(new AsyncCallback<List<DomRole>>() {

                        @Override
                        public void onFailure(Throwable t) {
                            LOG.log(Level.INFO, t.getStackTrace().toString());
//                            dialogBox.setText("Remote Procedure Call - " + t);
//                            serverResponseLabel
//                                    .addStyleName("serverResponseLabelError");
//                            serverResponseLabel.setHTML(SERVER_ERROR);
//                            dialogBox.center();
//                            closeButton.setFocus(true);
                        }

                        @Override
                        public void onSuccess(List<DomRole> result) {
                            LOG.log(Level.INFO, "ArraySize: "+result.size());
//                            GWT.log(String.valueOf(result));
//                            dialogBox.setText("Rest Call " + result);
//                            serverResponseLabel
//                                    .removeStyleName("serverResponseLabelError");
//                            serverResponseLabel.setHTML("");
//                            dialogBox.center();
//                            closeButton.setFocus(true);
                        }
                    });
        
        handler.login(curUser.getUserName(), curUser.getPassword(), new AsyncCallback<Map<String, Object>>() {

                        @Override
                        public void onFailure(Throwable t) {
                            LOG.log(Level.INFO, t.getStackTrace().toString());
//                            dialogBox.setText("Remote Procedure Call - " + t);
//                            serverResponseLabel
//                                    .addStyleName("serverResponseLabelError");
//                            serverResponseLabel.setHTML(SERVER_ERROR);
//                            dialogBox.center();
//                            closeButton.setFocus(true);
                        }

                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            LOG.log(Level.INFO, String.valueOf(result));
//                            GWT.log(String.valueOf(result));
//                            dialogBox.setText("Rest Call " + result);
//                            serverResponseLabel
//                                    .removeStyleName("serverResponseLabelError");
//                            serverResponseLabel.setHTML("");
//                            dialogBox.center();
//                            closeButton.setFocus(true);
                        }
                    });
        }else{
                    LOG.log(Level.INFO, "Configured username is: "+user.getUserName()+".");
        }
        UserBar userBar = new UserBar(user);

        header.setRightWidget(userBar);
// Bootstrap		
//        final Button sendButton = new Button("Send");
//        final TextBox nameField = new TextBox();
//        final TextBox passField = new TextBox();
//        nameField.setText("project_wim");
//        passField.setText("passw");
//        final Label errorLabel = new Label();
//
//        // We can add style names to widgets
//        sendButton.addStyleName("sendButton");
//
//        // Add the nameField and sendButton to the RootPanel
//        // Use RootPanel.get() to get the entire body element
//        RootPanel.get("nameFieldContainer").add(nameField);
//        RootPanel.get("passFieldContainer").add(passField);
//        RootPanel.get("sendButtonContainer").add(sendButton);
//        RootPanel.get("errorLabelContainer").add(errorLabel);
//
//        // Focus the cursor on the name field when the app loads
//        nameField.setFocus(true);
//        nameField.selectAll();
//
//        // Create the popup dialog box
//        final DialogBox dialogBox = new DialogBox();
//        dialogBox.setText("Remote Procedure Call");
//        dialogBox.setAnimationEnabled(true);
//        final Button closeButton = new Button("Close");
//        // We can set the id of a widget by accessing its Element
//        closeButton.getElement().setId("closeButton");
//        final Label textToServerLabel = new Label();
//        final HTML serverResponseLabel = new HTML();
//        VerticalPanel dialogVPanel = new VerticalPanel();
//        dialogVPanel.addStyleName("dialogVPanel");
//        dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
//        dialogVPanel.add(textToServerLabel);
//        dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
//        dialogVPanel.add(serverResponseLabel);
//        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
//        dialogVPanel.add(closeButton);
//        dialogBox.setWidget(dialogVPanel);
//
//        // Add a handler to close the DialogBox
//        closeButton.addClickHandler(new ClickHandler() {
//            public void onClick(ClickEvent event) {
//                dialogBox.hide();
//                sendButton.setEnabled(true);
//                sendButton.setFocus(true);
//            }
//        });
//
//        // Create a handler for the sendButton and nameField
//        class MyHandler implements ClickHandler, KeyUpHandler {
//
//            /**
//             * Fired when the user clicks on the sendButton.
//             */
//            public void onClick(ClickEvent event) {
//                sendNameToServer();
//            }
//
//            /**
//             * Fired when the user types in the nameField.
//             */
//            public void onKeyUp(KeyUpEvent event) {
//                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
//                    sendNameToServer();
//                }
//            }
//
//            /**
//             * Send the name from the nameField to the server and wait for a
//             * response.
//             */
//            private void sendNameToServer() {
//                // First, we validate the input.
//                errorLabel.setText("");
//                String textToServer = nameField.getText();
//                if (!FieldVerifier.isValidName(textToServer)) {
//                    errorLabel.setText("Please enter at least four characters");
//                    return;
//                }
//
//                // Then, we send the input to the server.
//                sendButton.setEnabled(false);
//                textToServerLabel.setText(textToServer);
//                serverResponseLabel.setText("");
//
//                String username = nameField.getText();
//                String password = passField.getText();
//                boolean goon = true;
//
//                if (goon) {
//                    handler.login(username, password, new AsyncCallback<Map<String, Object>>() {
//
//                        @Override
//                        public void onFailure(Throwable t) {
//                            dialogBox.setText("Remote Procedure Call - " + t);
//                            serverResponseLabel
//                                    .addStyleName("serverResponseLabelError");
//                            serverResponseLabel.setHTML(SERVER_ERROR);
//                            dialogBox.center();
//                            closeButton.setFocus(true);
//                        }
//
//                        @Override
//                        public void onSuccess(Map<String, Object> result) {
//                            GWT.log(String.valueOf(result));
//                            dialogBox.setText("Rest Call " + result);
//                            serverResponseLabel
//                                    .removeStyleName("serverResponseLabelError");
//                            serverResponseLabel.setHTML("");
//                            dialogBox.center();
//                            closeButton.setFocus(true);
//                        }
//                    });
//                }
//            }
//        }
//        // Add a handler to send the name to the server
//        MyHandler handler = new MyHandler();
//        sendButton.addClickHandler(handler);
//        nameField.addKeyUpHandler(handler);
//        passField.addKeyUpHandler(handler);
    }

}
