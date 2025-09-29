package fi.dwo.server.PersistentDataManagers.actions;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentApplet;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentImage;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.server.PersistentDataManagers.core.AppletManager;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.ImageManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.ScoPageManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;
import fi.dwo.server.PersistentDataManagers.util.ScoPageUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletId;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class MySQLScoContextActions {

    private static final Logger LOG = Logger.getLogger(MySQLScoContextActions.class.getName());

    private MySQLScoContextActions() {
    }

    public static DomScoContextFull update(PersistentScoContext pc, PersistentScoData sd,
            DomScoContextFull scoContext, DomScoData scoData, boolean delete) {
        try {
            Long scoID = pc.getScoID();
            // editable fields?
            if (scoContext.getScoName() != null) {
                pc.setSconame(scoContext.getScoName());
            }
            if (scoContext.getShowScore() != null) {
                pc.setShowscore(scoContext.getShowScore());
            }
            if (scoContext.getShowDocent() != null) {
            	pc.setShowdocent(scoContext.getShowDocent());
            }
            if (scoContext.getImageData() != null) {
                byte[] data = (scoContext.getImageData());
                PersistentImage image = ImageManager.findEntity(scoID);
                if (image == null) {
                    image = new PersistentImage(scoID);
                    image.setImage(data);
                    ImageManager.create(image);
                } else {
                    image.setImage(data);
                    image = ImageManager.edit(image);
                }
                scoContext.setImageData(null);
            } else if (scoContext.getUrnId() != null) {
                PersistenceId id = scoContext.getUrnId();
                switch (id.getType()) {
                    case PersistentScoContext:
                        if (id.equals(scoContext.getId())) {
                            break;
                        }
                    // TODO Zie "add"
                    default:
                        LOG.log(Level.SEVERE, scoContext.getId() + ": unsupported " + id);
                        throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "unsupported " + id);

                }
            }
            if (scoContext.getScoType() != null) {
                pc.setScoType(scoContext.getScoType());
            }
            if (scoContext.getStudentModelContext() != null) {
                Long model = MySQLPersistenceId
                        .getNativeId(new DomStudentModelContextId(scoContext.getStudentModelContext()));
                pc.setModelID(model);
            }

            if(scoContext.getCourseId() != null) {
              DomCourse dc = new DomCourse();
              dc.setId(scoContext.getCourseId());
              Long newcourseID = MySQLPersistenceId.getNativeId(dc);
              Long oldCourseID = pc.getCourseID();
              if (!oldCourseID .equals(newcourseID)) {
                // 1) update sequence nrs old Course.scos
                Long oldseq = pc.getSequencenr();
                Long newseq = scoContext.getSequencenr();
                if (newseq == null) newseq = oldseq;
                PersistentCourse course;
                course = CourseManager.findEntity(oldCourseID);
                relocateScos(course, oldseq.longValue(), -1L);
                // 2) make room in new Course.scos
                course = CourseManager.findEntity(newcourseID);
                relocateScos(course, newseq.longValue()-1L, +1L);                
              }
              
            }
            
            if (scoContext.getSequencenr() != null) {
                pc.setSequencenr(scoContext.getSequencenr());
            }

            if (scoContext.getCourseId() != null) {
                DomCourse dc = new DomCourse();
                dc.setId(scoContext.getCourseId());
                Long courseID = MySQLPersistenceId.getNativeId(dc);
                pc.setCourseID(courseID);
            }
            pc.setTrashID(0L);
            pc = ScoContextManager.edit(pc);
            // if edit fails skip this.
            if (scoContext.getDescription() != null) {
                sd.setDescription(scoContext.getDescription());
                sd = ScoDataManager.edit(sd);
            }

            if (scoData != null) {
                if (scoData.getLaunchdata() != null) {
                    sd.setLaunchdata(scoData.getLaunchdata());
                }
                if (scoData.getLaunchdatabytes() != null) {
                    sd.setLaunchdatabytes(scoData.getLaunchdatabytes());
                }
                if (delete) {
                    destroyStudentWork(pc);
                }
                sd = ScoDataManager.edit(sd);
                ScoPageUtilManager.updatePages(pc, sd);
            }

            pc.fillDomScoContextFull(scoContext);
            sd.fillDomScoContextFull(scoContext);

        } catch (RollbackException e) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_ScoNameExists, e.getMessage());
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        } catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
        }

        return scoContext;
    }

    private static void destroyStudentWork(PersistentScoContext pc) {
    	ScoPageManager.destroyStudentWork(pc);
        // Destroy all studentSco's
        List<PersistentStudentScoContext> list = StudentScoContextManager.findEntities(pc);
        for (PersistentStudentScoContext item : list) {
            try {
                StudentScoDataManager.destroy(item.getStudentSco()); //  non-fatal. studentscodata
            } catch (EntityNotFoundException e1) {
            }
            try {
                StudentScoContextManager.destroy(item.getStudentSco());
            } catch (EntityNotFoundException e) {
            }
        }
        // Destroy all studentModels of that sco.
        List<PersistentStudentModelData> list2 = StudentModelDataManager.findEntity(pc);
        for (PersistentStudentModelData item : list2) {
            StudentModelDataManager.destroy(item.getModelDataId());
        }
        Long courseID = pc.getCourseID();
// invalidate course result cache
//        List<PersistentClassCourse> ccs = ClassCourseManager.findEntities(new PersistentCourse(courseID));
//        ccs.forEach(cc -> {
//        	if (Boolean.TRUE.equals(cc.hasResults())
////        			&& (cc.getViewState() == ViewState.students||cc.getViewState() == ViewState.studentsOrTeachers)
//        			) {
//        		ClassCourseManager.editResults(cc.getClassCourseID(), null);
//        	}
//        });
        ClassCourseManager.uncacheResults(new PersistentCourse(courseID)); // XXX in bovenstaande alleen voor TRUE
    }

    static DomScoContextFull add(PersistentCourse c, DomScoContextFull scoContext,
            DomScoData scoData) {
        try {
            PersistentScoContext pc = new PersistentScoContext();
            DomAppletId applet = new DomAppletId(scoContext.getAppletId());
            Long appletID = MySQLPersistenceId.getNativeId(applet);
            PersistentApplet a = AppletManager.findEntity(appletID);
            assert a != null;
            pc.setAppletID(appletID);
            // assert school of course = school of user, done by authorizer
            // assert profile of rest = profile of course, done by authorizer
            pc.setCourseID(c.getCourseID());
            String sconame = scoContext.getScoName();
            assert sconame != null && !sconame.isEmpty();
            pc.setSconame(sconame);
            Long sequencenr = scoContext.getSequencenr();
            if (sequencenr == null) {
                sequencenr = Long.valueOf(ScoContextManager.findEntities(c).size());
            }
            pc.setSequencenr(sequencenr);
            ScoType scoType = scoContext.getScoType();
            if (scoType == null) {
                scoType = ScoType.OEFENEN; // NotNull!
            }
            pc.setScoType(scoType);
            Boolean showscore = scoContext.getShowScore();
            if (showscore == null) {
                showscore = Boolean.TRUE; // notnull?
            }
            pc.setShowscore(showscore);
            Boolean showdocent = scoContext.getShowDocent();
            if (showdocent == null) {
            	showdocent = Boolean.TRUE;
            }
            pc.setShowdocent(showdocent);
            
            pc.setUrnID(null); // XXX als images in UrnResource staan.
            // fill schoolid and profile id
            pc.setSchoolID(c.getSchoolID());
            pc.setDwoProfileID(c.getDwoProfileID());
            // fill reference to model
            if (scoContext.getStudentModelContext() != null) {
                Long model = MySQLPersistenceId
                        .getNativeId(new DomStudentModelContextId(scoContext.getStudentModelContext()));
                pc.setModelID(model);
            }
            ScoContextManager.create(pc);
            PersistentScoData sd = new PersistentScoData(pc.getScoID(), scoContext.getDescription());
            if (sd.getDescription() == null) {
                sd.setDescription("");
            }
            if (scoData != null) {
                DomScoData data = scoData;
                sd.setLaunchdata(data.getLaunchdata());
                sd.setLaunchdatabytes(data.getLaunchdatabytes());
            }
            ScoDataManager.create(sd);
            pc.fillDomScoContextFull(scoContext);
            sd.fillDomScoContextFull(scoContext);
            if (scoContext.getImageData() != null) {
                PersistentImage image = new PersistentImage(pc.getScoID(), scoContext.getImageData());
                ImageManager.create(image);
                scoContext.setImageData(null);
                scoContext.setUrnId(scoContext.getId());
            } else if (scoContext.getUrnId() != null) {
                PersistenceId id = scoContext.getUrnId();
                switch (id.getType()) {
                    case PersistentScoContext:
                        DomScoContextId scid = new DomScoContextId();
                        scid.setId(id);
                        Long imageid = MySQLPersistenceId.getNativeId(scid);
                        PersistentImage img = ImageManager.findEntity(imageid);
                        if (img != null) {
                            img.setCourseID(pc.getScoID());
                            ImageManager.create(img);
                            scoContext.setUrnId(scoContext.getId());
                        } else {
                            scoContext.setUrnId(null);
                        }
                        break;
                    default:
                        throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "unsupported " + id);
                }
            }

            return scoContext;
        } catch (RollbackException e) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_ScoNameExists, e.getMessage());
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        } catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
        }
    }

    public static void remove(PersistentScoContext pc, PersistentCourse c) {
        destroyStudentWork(pc);
        final long seq = pc.getSequencenr().longValue();
        //in some cases there is no scodata!
        try {
            ScoDataManager.destroy(pc.getScoID());
        } catch (PersistenceException ex) {
//            if (ex instanceof EntityNotFoundException) {
//            }else{
//                LOG.log(Level.SEVERE, "unexpected error", ex);
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, ex.getMessage());
//                //
//            }

        }
        ScoContextManager.destroy(pc.getScoID());
        if (pc.getTrashID() == 0) relocateScos(c, seq, -1L);
    }

    public static void trash(PersistentScoContext pc, PersistentCourse c) {
        final long seq = pc.getSequencenr().longValue();
        long trashid = pc.getTrashID();
    	pc.setTrashID(System.currentTimeMillis());
        ScoContextManager.edit(pc);
        if (trashid == 0) relocateScos(c, seq, -1L);
    }

    private static void relocateScos(PersistentCourse c, final long seq, final long incr) {
      ClassCourseManager.uncacheResults(c);
      List<PersistentScoContext> list = ScoContextManager.findEntities(c);
      list.forEach(item -> {
          long s = item.getSequencenr().longValue();
          if (s > seq) {
              item.setSequencenr(Long.valueOf(s + incr));
              try {
                  ScoContextManager.edit(item);
              } catch (PersistenceException e) {
                  LOG.log(Level.SEVERE, "relocate sco", e);
                  throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
              }
          }

      });
    }

}
