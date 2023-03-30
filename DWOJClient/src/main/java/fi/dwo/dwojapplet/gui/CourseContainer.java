package fi.dwo.dwojapplet.gui;

import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.ResultsModuleIF;

interface CourseContainer {

    void showClassList();

    void hideClassList();

    void loadCenter(CenterSubPanel cp);

    void loadTotal(CenterSubPanel csp);

    ResultsModuleIF getUserResultsModule(Course course);

}
