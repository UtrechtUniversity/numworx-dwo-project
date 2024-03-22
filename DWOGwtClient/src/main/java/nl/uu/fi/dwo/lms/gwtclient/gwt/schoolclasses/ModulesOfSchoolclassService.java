package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherClassCourseManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.Date;

import java.util.logging.Logger;

import javax.inject.Inject;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseAndProfileNew;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewAccessKey;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewFrom;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewTo;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewType;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseAndProfileNew;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewAccessKey;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewFrom;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewTo;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

/**
 * Persistent model service for Teacher results. Retrieves DomResultsPerTeacher
 * data. In the future it may cache this data and merge updates into it. it may
 * also request Updates if required. In example, fetch new data if older than xx
 * seconds or Check if changes of results exist within the schoolgroup.
 *
 * @author Gert van der Plas
 */
public class ModulesOfSchoolclassService {

    //private static final Logger LOG = Logger.getLogger(ModulesOfSchoolclassService.class.getName());

    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
    private SecuredTeacherClassCourseManager ccm = new SecuredTeacherClassCourseManager();
    private final DwoGlobalVars dwoGlobalVars;

    @Inject ModulesOfSchoolclassService(DwoGlobalVars aDwoGlobalVars) {
        dwoGlobalVars = aDwoGlobalVars;
    }

    public Promise<DomCoursesOfSchoolClass4Teacher> getModules(final DomSchoolClass sc, boolean remedial) {
        DomContext context = new DomContext();
        context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());
        return dwoGlobalVars.getProfile().then(new Success<DomDwoProfile, DomCoursesOfSchoolClass4Teacher>() {

            @Override
            public Promise<DomCoursesOfSchoolClass4Teacher> call(
                    Promise<DomDwoProfile> resolved) throws Exception {
                DomSchoolClassAndProfile sap = new DomSchoolClassAndProfile();
                DomDwoProfile profile = new DomDwoProfile(resolved.getValue());
                if (remedial && dwoGlobalVars.isPremium() && dwoGlobalVars.isRemedial()) {               	
                	String rights;
                	if (dwoGlobalVars.isModulesOnly()) rights = "R4"; else rights = "R";
					profile.setDwoProfileRights(rights); // see setRemedial + setModulesOnly
                } else 
                	profile.setDwoProfileRights("");

                sap.setDomDwoProfile(profile);
                sap.setDomSchoolClass(sc);
                return manager.getModules(context, sap);
            }
        });
    }

    public Promise<Boolean> addCourseToClass(final DomSchoolClass sc, final DomCourse course, CourseType courseType, Date fromDate, Date toDate, String accessKey) {
        DomContext context = new DomContext();
        context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());

        return dwoGlobalVars.getProfile().then(new Success<DomDwoProfile, Boolean>() {

            @Override
            public Promise<Boolean> call(
                    Promise<DomDwoProfile> resolved) throws Exception {
                DomSchoolClassCourseAndProfileNew sap = new DomSchoolClassCourseAndProfileNew();
                sap.setDomDwoProfile(resolved.getValue());
                sap.setDomSchoolClass(sc);
                sap.setCourse(course);
                sap.setCourseType(courseType);
                sap.setFrom(fromDate);
                sap.setTo(toDate);
                sap.setAccessKey(accessKey);
                RestSchoolClassCourseAndProfileNew rest = new RestSchoolClassCourseAndProfileNew();
                rest.setRestContext(context);
                rest.setDomSchoolClassCourseAndProfileNew(sap);
                return manager.addCourseToClass(rest);
            }
        });
    }

    public Promise<Boolean> attachCourseToClass(final DomSchoolClass sc, final DomCourse course) {
        DomContext context = new DomContext();
        context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());

        return dwoGlobalVars.getProfile().then(new Success<DomDwoProfile, Boolean>() {

            @Override
            public Promise<Boolean> call(
                    Promise<DomDwoProfile> resolved) throws Exception {
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
        context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());

        return dwoGlobalVars.getProfile().then(new Success<DomDwoProfile, Boolean>() {

            @Override
            public Promise<Boolean> call(
                    Promise<DomDwoProfile> resolved) throws Exception {
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

    public Promise<Boolean> setTypeClassCourse(final DomSchoolClass sc, final DomCourse course, final CourseType type) {
        DomContext context = new DomContext();
        context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());

        return dwoGlobalVars.getProfile().then(new Success<DomDwoProfile, Boolean>() {
            @Override
            public Promise<Boolean> call(
                    Promise<DomDwoProfile> resolved) throws Exception {
                DomSchoolClassCourseProfilewType sap = new DomSchoolClassCourseProfilewType();
                sap.setDomDwoProfile(resolved.getValue());
                sap.setDomSchoolClass(sc);
                sap.setCourse(course);
                sap.setType(type);
                RestSchoolClassCourseProfilewType rest = new RestSchoolClassCourseProfilewType();
                rest.setRestContext(context);
                rest.setDomSchoolClassCourseProfilewType(sap);
                return manager.setClassCourseType(rest);
            }
        });
    }

    public Promise<Boolean> setFromDateClassCourse(final DomSchoolClass sc, final DomCourse course, final Date from) {
        DomContext context = new DomContext();
        context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());

        return dwoGlobalVars.getProfile().then(new Success<DomDwoProfile, Boolean>() {

            @Override
            public Promise<Boolean> call(
                    Promise<DomDwoProfile> resolved) throws Exception {
                DomSchoolClassCourseProfilewFrom sap = new DomSchoolClassCourseProfilewFrom();
                sap.setDomDwoProfile(resolved.getValue());
                sap.setDomSchoolClass(sc);
                sap.setCourse(course);
                sap.setFrom(from);
                RestSchoolClassCourseProfilewFrom rest = new RestSchoolClassCourseProfilewFrom();
                rest.setRestContext(context);
                rest.setDomSchoolClassCourseProfilewFrom(sap);
                return manager.setFromDateClassCourse(rest);
            }
        });
    }

    public Promise<Boolean> setToDateClassCourse(final DomSchoolClass sc, final DomCourse course, final Date to) {
        DomContext context = new DomContext();
        context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());

        return dwoGlobalVars.getProfile().then((resolved) -> {
            DomSchoolClassCourseProfilewTo sap = new DomSchoolClassCourseProfilewTo();
            sap.setDomDwoProfile(resolved.getValue());
            sap.setDomSchoolClass(sc);
            sap.setCourse(course);
            sap.setTo(to);
            RestSchoolClassCourseProfilewTo rest = new RestSchoolClassCourseProfilewTo();
            rest.setRestContext(context);
            rest.setDomSchoolClassCourseProfilewTo(sap);
            return manager.setToDateClassCourse(rest);
        }
        );
    }

    public Promise<Boolean> setAccessKey(DomSchoolClass sc, DomCourse course, String accessKey) {
        DomContext context = new DomContext();
        context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());

        return dwoGlobalVars.getProfile().then((resolved) -> {
            DomSchoolClassCourseProfilewAccessKey sap = new DomSchoolClassCourseProfilewAccessKey();
            sap.setDomDwoProfile(resolved.getValue());
            sap.setDomSchoolClass(sc);
            sap.setCourse(course);
            sap.setAccessKey(accessKey);
            RestSchoolClassCourseProfilewAccessKey rest = new RestSchoolClassCourseProfilewAccessKey();
            rest.setRestContext(context);
            rest.setDomSchoolClassCourseProfilewAccessKey(sap);
            return manager.setAccessKeyClassCourse(rest);
        });
    }
    
    public Promise<Boolean> commit() {
    	return Promises.resolved(Boolean.TRUE);
    }
    
    public Promise<DomClassCourseFull> setClassCourse(
    		PersistenceId id,
    		DomSchoolClass sc, DomCourse course, CourseType type, String accessKey, Date from, Date to) {
    	DomClassCourseFull dom = new DomClassCourseFull();
    	dom.setId(id);
    	dom.setAccessKey(accessKey);
    	dom.setClassId(sc.getId());
    	dom.setCourseId(course.getId());
    	dom.setCourseType(type);
    	dom.setNotBefore(from);
    	dom.setNotAfter(to);
    	DomContext context = new DomContext();
        context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());
		Promise<DomClassCourseFull> result = ccm.update(context, dom);    	
		return result;
    }
    
    
}
