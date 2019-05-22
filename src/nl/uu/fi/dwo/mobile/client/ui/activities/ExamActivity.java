package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Inject;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;

import nl.uu.fi.dwo.mobile.client.ui.Actions;

public class ExamActivity extends AbstractActivity {

  public ExamActivity() {  }
  
  
  @Override
  public void start(AcceptsOneWidget panel, EventBus eventBus) {
    panel.setWidget(new Label());
    Actions.EXAM.execute();
    gotoExam("exam/");
  }

  private static native void gotoExam(String ref) /*-{
    top.location.href = ref;
  }-*/;

}
