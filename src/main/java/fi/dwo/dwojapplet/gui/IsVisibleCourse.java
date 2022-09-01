package fi.dwo.dwojapplet.gui;

import java.util.function.Predicate;

import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;

class IsVisibleCourse implements Predicate<CourseMap> {

  /**
   * Visible zijn alle modules en mappen met notVisible false
   * @param c coursemap
   * @return visibility in een map
   */
  public boolean test(CourseMap c) {
    return !c.isNotVisible() || (c instanceof Course && !((Course)c).isWithChildren());
  }

}
