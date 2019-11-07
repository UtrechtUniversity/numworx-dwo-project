package fi.dwo.dwojapplet.persistence;

import fi.dwo.dwojapplet.domain.ClassCourse;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

class ClassCourseMapper  {

    Map<Integer, ClassCourse> objects;
  
  
    ClassCourseMapper() {
      objects = new HashMap<>();
    }

    ClassCourse getObjectFromReturn(Hashtable data) {
        ClassCourse c = null;
        if (data == null || data.get("ClassCourseID") == null) { //We don't know enough to make a
            // classobject
            return null;
        } else if (data.get("ClassCourseID") instanceof String) { //If it is a string, it was null
            return null;
        } else if (objects.containsKey(data.get("ClassCourseID"))) { // Did we know
            // the class?
            c = (ClassCourse) objects.get(data.get("ClassCourseID"));
        } else {
            c = new ClassCourse();
        }
        c = (ClassCourse) update(c, data);
        if (!objects.containsKey(new Integer(c.getID()))) {
            objects.put(new Integer(c.getID()), c);
        }
        return c;
    }


    ClassCourse update(ClassCourse obj, Hashtable data)  {
        ClassCourse cc = (ClassCourse) obj;
        cc.setClassCourseID(((Integer) data.get("ClassCourseID")).intValue());
        cc.setClassID(((Integer) data.get("ClassID")).intValue());
        cc.setCourseID(((Integer) data.get("CourseID")).intValue());
        cc.setType(((Integer) data.get("type")).intValue());
        cc.setViewState(((Integer) data.get("viewState")).intValue());
        cc.setAccessKey((String) data.get("accessKey"));
        Object o = data.get("notAfter");
        if (o instanceof Date) // string or null 
        {
            cc.setNotAfter((Date) o);
        } else {
            cc.setNotAfter(null);
        }
        o = data.get("notBefore");
        if (o instanceof Date) // string or null 
        {
            cc.setNotBefore((Date) o);
        } else {
            cc.setNotBefore(null);
        }
        return cc;
    }

}
