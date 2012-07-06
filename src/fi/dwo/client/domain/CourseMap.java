package fi.dwo.client.domain;

import java.util.Set;

public interface CourseMap {

	void addChild(Course c);
	void removeChild(int i);
	CourseMap[] getChildren();

	void setChildren(CourseMap[] courses);
	Object getUserObject();
	Set getChildNames();
	CourseMap getParentMap();

}
