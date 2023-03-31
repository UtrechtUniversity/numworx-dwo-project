package nl.uu.fi.dwo.mobile.utils;

import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;

public class Review {

  private Review() {
  }

  static public boolean isReview(OpdrNavIF comRoot) {
    return LessonMode.review == comRoot.getLessonMode();
  }
}
