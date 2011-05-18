package fi.dwo.client.domain;

public interface CourseMap {

	void addChild(Course c);
	void removeChild(int i);
	Course[] getChildren();

	void setChildren(Course[] courses);
	Object getUserObject();
	

}
