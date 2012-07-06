package fi.dwo.client.domain;

public interface Descriptor {

	String getText();
	String getHeader();
	CourseMap[] getChildren();

}
