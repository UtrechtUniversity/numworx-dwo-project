/**
 * Copyrighted Mar 7, 2018
 */
package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentMethod;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.commons.persistence.entities.PersistentStudentModelOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer.StudentState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.MethodManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.util.StudentModelContextUtilManager;
import fi.dwo.server.rest.SecuredTeacherStudentModelManager;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;
import javax.ws.rs.core.UriInfo;

import org.json.simple.parser.ParseException;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelData;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

/**
 *
 * @author Gert van der Plas
 */
class StudentBuilder implements StudentDomainAuthorizer.StudentState_HR_R_S_SG_U {

    private static final Logger LOG = Logger.getLogger(StudentBuilder.class.getName());

    protected StudentDomainAuthorizer instance;

//    @Override
//    public StudentDomainAuthorizer.StudentState_HR_R_S_SG_U Student() throws Dwo2Exception {
//        if (this.instance.userCtx.roleType.equals(RoleType.STUDENT)) {
//            return this;
//        } else {
//            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Not a student.");
//        }
//    }
    public StudentBuilder() throws Dwo2Exception {
        super();
        instance = new StudentDomainAuthorizer();
    }
//
//    protected StudentBuilder(UserBuilder builder) throws Dwo2Exception {
//        super();
//        if (builder.instance.userCtx.roleType != null && builder.instance.userCtx.roleType == RoleType.STUDENT) {
//            instance.userCtx = builder.instance.getContext().getUserCtx();
//            //instance.userCtx = builder.instance.userCtx;
//        } else {
//            String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: It is not in a student role.", new Object[]{instance.userCtx.getUser().getUsername()});
//            LOG.log(Level.WARNING, msg);
//            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
//        }
//    }

    /**
     * If a studentModelData id is set, and update is expected. If it is null an
     * insert or update is expected.
     *
     * @param data
     * @throws Dwo2Exception
     */
    @Override
    public void setStudentModelData(DomStudentModelData data) throws Dwo2Exception {
        String msg = null;
        PersistentStudentModelData pData;
        PersistentScoContext pScoContext = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(data.getScoContextId()));
        //scoContext exists
        if (pScoContext == null) {
            msg = MessageFormat.format("Username {0}: ILLEGAL OPERATION: Can't update, ScoContext not given.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
        }
        //School matches with user state
        if (pScoContext.getSchoolID() != null && instance.getContext().getUserCtx().school.getSchoolID().longValue() != pScoContext.getSchoolID().longValue()) {
            msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Can't update, schoolID not given or wrong.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
        }
        //Sco has modelId
        if (pScoContext.getModelID() == null || data.getModelId() == null || pScoContext.getModelID().longValue() != (MySQLPersistenceId.getNativeId(data.getModelId()).longValue())) {
            msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Can't update, modelID not given.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
        }
//        //SchoolId of school matches user state
//        if (pScoContext.getSchoolID() == null && instance.getContext().getUserCtx().school.getSchoolID().longValue() != pScoContext.getSchoolID().longValue()) {
//            msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Can't update, schoolID not given or wrong.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername()});
//            LOG.log(Level.WARNING, msg);
//            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
//        }
        //find StudentModelData
        Long id = MySQLPersistenceId.getNativeId(data);
        if (id != null) {
            pData = StudentModelDataManager.findEntity(id);
            if (pData != null) {
                //data exists
                //check if HasRole matches with user state
                pData.setOptlock(data.getOptLock());
                if (pData.getPersistentHasRolePK().getUserID().longValue() != instance.getContext().getUserCtx().user.getId().longValue()
                        && pData.getPersistentHasRolePK().getSchoolGroupID().longValue() != instance.getContext().getUserCtx().schoolGroup.getSchoolGroupID().longValue()) {
                    msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Can't update, hasRole mismatch.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername()});
                    LOG.log(Level.WARNING, msg);
                    throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
                }
            } else if (pData == null) {
                //StudentModelData id was given but not found.
                msg = MessageFormat.format("Username {0}: ILLEGAL OPERATION: Can't update, does not exist.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
            }
        } else {
            //data id is NULL. Hence create a PersistentStudentModelData and do an insert or update
            pData = new PersistentStudentModelData();
            pData.setScoID(MySQLPersistenceId.getNativeId(data.getScoContextId()));
            pData.setPersistentHasRolePK(instance.getContext().getUserCtx().hasRole.getPersistentHasRolePK());
            pData.setModelID(MySQLPersistenceId.getNativeId(data.getModelId()));
        }

        //insert or updateModelDataScore in pData.
        pData.setModelData(data.getDomStudentModelStructureScore());
        instance.getStudentActions().setStudentModelData(instance.getContext(), pData);
    }

    @Override
    public DomStudentModelData getStudentModelData(DomScoContextId domScoId) throws Dwo2Exception {
        PersistentScoContext pScoContext = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(domScoId));
        return instance.getStudentActions().getStudentModelData(instance.getContext(), pScoContext).buildDomStudentModelData();
    }

    public StudentState_HR_R_S_SG_U init(UserDomainAuthorizer.Context ctx) throws Dwo2Exception {
        this.instance.setContext(new StudentDomainAuthorizer.Context(ctx));
        if (ctx.getUserCtx().roleType != null && ctx.getUserCtx().roleType == RoleType.STUDENT) {
            this.instance.getContext().setUserCtx(ctx.getUserCtx());
            this.instance.getContext().setAnonCtx(ctx.getAnonCtx());
            return this;
        } else {
            String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: It is not in a student role.", new Object[]{ctx.getUserCtx().getUser().getUsername()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
    }

    @Override
    public StudentDomainAuthorizer.Context getContext() {
        return instance.getContext();
    }

    @Override
    public void setContext(StudentDomainAuthorizer.Context context) {
        instance.setContext(context);
    }

    @Override
    public DomStudentModelDataScore getStudentModelDataScore(DomStudentModelContextId domModelId) throws Dwo2Exception {
        PersistentStudentModelContext pStudentModel = StudentModelContextManager.findEntity(MySQLPersistenceId.getNativeId(domModelId));
        StudentModelContextUtilManager.merge(pStudentModel);
        return instance.getStudentActions().getStudentModelData(instance.getContext(), pStudentModel);
    }

    @Override
    public List<DomStudentModelContext> getStudentModelContextList() throws Dwo2Exception{
        List<PersistentStudentModelContext> pModels = instance.getStudentActions().getStudentModels(instance.getContext());
                    List<DomStudentModelContext>  result = new ArrayList<>(pModels.size());
            pModels.forEach(m -> result.add(m.buildDomStudentModelContext()));
            return result;
    }

    @Override
    public List<DomStudentModelContext> getMergedStudentModelContextList() throws Dwo2Exception{
        List<PersistentStudentModelContext> pModels = instance.getStudentActions().getStudentModels(instance.getContext());
                    List<DomStudentModelContext>  result = new ArrayList<>(pModels.size());
            pModels.forEach(m -> {
                StudentModelContextUtilManager.merge(m);
            	result.add(m.buildDomStudentModelContext());	
            });
            return result;
    }

    @Override
    public DomLRS getLRS(UriInfo info) {
      return instance.getStudentActions().getLRS(instance.getContext(), info);
    }

	@Override
	public DomStudentModelContext getStudentModel(DomStudentModelContextId domModelId) throws Dwo2Exception {
        PersistentStudentModelContext pStudentModel = StudentModelContextManager.findEntity(MySQLPersistenceId.getNativeId(domModelId));
        StudentModelContextUtilManager.merge(pStudentModel);
        return pStudentModel.buildDomStudentModelContext();
	}

	@Override
	public List<DomStudentModelContext4Student> getStudentModelContextListForClass() throws Dwo2Exception {
        PersistentSchoolClass sc = instance.getContext().getStudentCtx().schoolClass;
        List<PersistentStudentModelOfClass> p4Class = StudentModelOfClassManager.findEntities(sc);
        List<DomStudentModelContext4Student>  result = new ArrayList<>(p4Class.size());
        for(PersistentStudentModelOfClass item: p4Class) {
        	Long id = item.getId().getModelID();
        	PersistentStudentModelContext context = StudentModelContextManager.findEntity(id);
        	DomStudentModelContext r = context.buildDomStudentModelContext();
        	DomStudentModelContext4Student rr = new DomStudentModelContext4Student(r.getId());
        	rr.setModelStructure(r.getModelStructure());
        	try {
				final Map<String, Map<String, Set<Integer>>> filter = SecuredTeacherStudentModelManager.toFilter(item);
				rr.setFilter(filter);
				String filterKey = key(filter);
				String methodKey = DomMethod.key(rr.getModelStructure().getActiveMethod());
				if (filterKey != null && !Objects.equals(filterKey, methodKey)) {
					try {
						List<PersistentMethod> ms = MethodManager.findEntities(instance.getContext().getUserCtx().school);
						for(PersistentMethod m: ms) {
							String mKey = DomMethod.key(m.buildPersistenceId());
							if (Objects.equals(mKey, filterKey)) {
								rr.getModelStructure().setActiveMethod(m.buildPersistenceId());
								break;
							}
						}
					} catch(Exception oops) {
						LOG.log(Level.WARNING, "extract filter method");
					}
				}
				
				
			} catch (ParseException e) {
				LOG.log(Level.SEVERE, "conversie to filter", e);
			}
        	rr.setOptLock(item.getOptlock());
        	rr.setSchoolClass(sc.buildDomSchoolClass());
        	result.add(rr);
        }
		return result;
	}

	private String key(Map<String, Map<String, Set<Integer>>> filter) {
		return filter.keySet().stream().filter(key -> key != null && !key.isEmpty()).findAny().orElse(null);
	}

	@Override
	public StudentState_HR_R_S_SG_U setSchoolClass(DomSchoolClass domSchoolClass) throws Dwo2Exception {
		if (domSchoolClass == null) {
			instance.getContext().getStudentCtx().schoolClass = null;
		} else {
			instance.getContext().getStudentCtx().schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getNativeId(domSchoolClass));
			Long userID = instance.getContext().getUserCtx().user.getId();
			Long classID = instance.getContext().getStudentCtx().schoolClass.getClassID();
			Long schoolGroupID = instance.getContext().getUserCtx().schoolGroup.getSchoolGroupID();
			PersistentStudentOfClass soc = StudentOfClassManager.findEntity(new PersistentStudentOfClassPK(userID, classID, schoolGroupID));
			if (soc == null) throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "not a member of schoolclass");
		}
		return this;
	}
}
