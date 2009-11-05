package fi.dwo.client.gui;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.ResultsModuleIF;

interface CourseContainer {

	void showClassList();
	void hideClassList();

	void loadCenter(CenterSubPanel cp);
	void loadTotal(CenterSubPanel csp);

	ResultsModuleIF getUserResultsModule(Course course);

}
