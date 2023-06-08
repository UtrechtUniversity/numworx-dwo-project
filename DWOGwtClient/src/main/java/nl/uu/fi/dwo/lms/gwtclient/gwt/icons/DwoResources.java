package nl.uu.fi.dwo.lms.gwtclient.gwt.icons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 *
 * @author G.A.J. van der Plas
 */
public interface DwoResources extends ClientBundle{

   public DwoResources instance = GWT.create(DwoResources.class);
    
   @Source("num-results.png")
   public ImageResource numResultsIcon();
   @Source("num-class.png")
   public ImageResource numClassIcon();
   @Source("num-role.png")
   public ImageResource numRoleIcon();
   @Source("num-account.png")
   public ImageResource numAccountIcon();
   @Source("num-menu.png")
   public ImageResource numMenuIcon();
   @Source("num-logout.png")
   public ImageResource numLogoutIcon();
    
    
   @Source("docent.png")
   public ImageResource teacherIcon();

   @Source("student.png")
   public ImageResource studentIcon();
   
   @Source("empty.gif")
   public ImageResource emptyIcon();

   @Source("delete.gif")
   public ImageResource deleteIcon();
   
   @Source("edit.gif")
   public ImageResource editIcon();

   @Source("modules.gif")
   public ImageResource modulesIcon();

   @Source("userlist.gif")
   public ImageResource usersIcon();

   @Source("studentlist.gif")
   public ImageResource studentsIcon();

   @Source("teacherlist.gif")
   public ImageResource teachersIcon();

   @Source("loading.gif")
   public ImageResource loadingIcon();
   
   @Source("drillUp.gif")
   public ImageResource drillUpIcon();
   
   @Source("drillDown.gif")
   public ImageResource drillDownIcon();
}
