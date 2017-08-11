package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;

import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseAndProfile;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Persistent model service for Teacher results. Retrieves DomResultsPerTeacher
 * data. In the future it may cache this data and merge updates into it. it may
 * also request Updates if required. In example, fetch new data if older than xx
 * seconds or Check if changes of results exist within the schoolgroup.
 *
 * @author Gert van der Plas
 */
class CoursesOfSchoolclassService {

    private static final Logger LOG = Logger.getLogger(CoursesOfSchoolclassService.class.getName());

    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();

    public Promise<DomCoursesOfSchoolClass4Teacher> getModules(final DomSchoolClass sc) {
        DomContext context = new DomContext();
        context.setDomHasRole(DwoGlobalVars.instance().getActiveSchoolRoleAndClass().getHasRole());
        return DwoGlobalVars.instance().getProfile().then(new Success<DomDwoProfile, DomCoursesOfSchoolClass4Teacher>() {

            @Override
            public Promise<DomCoursesOfSchoolClass4Teacher> call(
                    Promise<DomDwoProfile> resolved) throws Exception {
                DomSchoolClassAndProfile sap = new DomSchoolClassAndProfile();
                sap.setDomDwoProfile(resolved.getValue());
                sap.setDomSchoolClass(sc);
                return manager.getModules(context, sap);
            }
        });
    }

    public Promise<Boolean> attachCourseToClass(final DomSchoolClass sc, final DomCourse course) {
        DomContext context = new DomContext();
        context.setDomHasRole(DwoGlobalVars.instance().getActiveSchoolRoleAndClass().getHasRole());
        
        return DwoGlobalVars.instance().getProfile().then(new Success<DomDwoProfile, Boolean>() {

            @Override
            public Promise<Boolean> call(
                    Promise<DomDwoProfile> resolved) throws Exception {
                DomSchoolClassCourseAndProfile data = new DomSchoolClassCourseAndProfile();
                DomSchoolClassCourseAndProfile sap = new DomSchoolClassCourseAndProfile();
                sap.setDomDwoProfile(resolved.getValue());
                sap.setDomSchoolClass(sc);
                sap.setCourse(course);
                RestSchoolClassCourseAndProfile rest = new RestSchoolClassCourseAndProfile();
                rest.setRestContext(context);
                rest.setDomSchoolClassCourseAndProfile(sap);
                return manager.attachCourseToClass(rest);
            }
        });
    }    

    public Promise<Boolean> detachCourseFromClass(final DomSchoolClass sc, final DomCourse course) {
        DomContext context = new DomContext();
        context.setDomHasRole(DwoGlobalVars.instance().getActiveSchoolRoleAndClass().getHasRole());
        
        return DwoGlobalVars.instance().getProfile().then(new Success<DomDwoProfile, Boolean>() {

            @Override
            public Promise<Boolean> call(
                    Promise<DomDwoProfile> resolved) throws Exception {
                DomSchoolClassCourseAndProfile data = new DomSchoolClassCourseAndProfile();
                DomSchoolClassCourseAndProfile sap = new DomSchoolClassCourseAndProfile();
                sap.setDomDwoProfile(resolved.getValue());
                sap.setDomSchoolClass(sc);
                sap.setCourse(course);
                RestSchoolClassCourseAndProfile rest = new RestSchoolClassCourseAndProfile();
                rest.setRestContext(context);
                rest.setDomSchoolClassCourseAndProfile(sap);
                return manager.detachCourseFromClass(rest);
            }
        });
    }    
}
