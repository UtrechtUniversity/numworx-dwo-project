package fi.dwo.client.domain;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.JOptionPane;

import fi.beans.appletutil.AppletUtil;
import fi.beans.fidentity.Fidentity;
import fi.beans.jvmchecker.JVMChecker;
import fi.beans.mainframe.MainFrame;
import fi.beans.scorm.SCORM12APIInterface;
import fi.beans.tooltip.ToolTipManager;
import fi.dwo.client.gui.CenterPanel;
import fi.dwo.client.gui.CenterSubPanel;
import fi.dwo.client.gui.DwoMessageDialog;
import fi.dwo.client.gui.GuiConstants;
import fi.dwo.client.gui.GuiCreator;
import fi.dwo.client.gui.ScoPanel;
import fi.dwo.client.persistence.MapperCreator;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.ClassException;
import fi.dwo.client.system.LoginException;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.RegisterException;
import fi.dwo.client.system.SchoolException;
import fi.dwo.client.system.TextMapper;

public class DWOlight extends Applet implements SCORM12APIInterface, DwoIF {

	private Course currentCourse;
    private User currentUser;
	private Fidentity fidentity;
    private DwoProfile dwoProfile;
    private int dwoProfileID;
    private String userName;
    private String passWord;
	private int courseViewNr;  // Deze funcionaliteit werkt niet! TODO? 
	private int scoViewNr = 1; // FIXME via DWOlight(args) invullen!
	private String languageOveride;
	

    /**
     * Creates a new DWO object.
     *  
     */
    public DWOlight() {
        dwoProfileID = 1;
    }
    
    /**
     * Creates a new DWO object with an argument.
     * <pre>
     * -s SERVLET
     * profileID
     * username
     * password
     * </pre> 
     */
    public DWOlight(String[] args) {
        dwoProfileID = 1;
        int o = 0;
// allow update van SERVLET
        if(args!= null && args.length>1 && "-s".equals(args[0]))
        {
        	fi.dwo.client.persistence.DbAccessCreator.SERVLET = args[1];
        	o = 2;
        }
// allow definitie van Locale.
        if(args != null && args.length>1+o && "-l".equals(args[o]))
        {
        	languageOveride=args[o+1];
        	o += 2;
        }
        if (args != null && args.length>o && args[o] != null) {
            try	{
            	dwoProfileID = Integer.parseInt(args[o]);
           	}catch(NumberFormatException e){}
           	if (args.length>2+o && args[1+o] != null && args[2+o]!= null) {
           		userName = args[1+o];
           		passWord = args[2+o];
           	}
        }
        
    }


	/**
     * The main method of the class.
     * 
     * @param args
     */
    public static void main(String[] args) {
        int width = 789;
        int height = 492;
        MainFrame mf = new MainFrame(new DWOlight(args), width, height);
        mf.setTitle("DWOlight");
        mf.pack();
        mf.show();
        //mf.setSize(width + 10, height + 20);
    }

	
	
	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#addClass(java.lang.String)
	 */
	public boolean addClass(String className) throws ClassException {
		return false;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#addCourse(java.lang.String, java.lang.String)
	 */
	public Course addCourse(String name, String description) {
		return null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#addSchool(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public School addSchool(int id, String schoolName, String schoolLogin,
			String studentPassw, String teacherPassw) throws SchoolException {
		return null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#addSco(fi.dwo.client.domain.Course, fi.dwo.client.domain.AppletConfig, java.lang.String, java.lang.String)
	 */
	public Sco addSco(Course course, AppletConfig appletConfig, String name,
			String description) {
		return null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#changeAccount(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, fi.dwo.client.domain.SchoolClass)
	 */
	public void changeAccount(String password, String newPassword,
			String reNewPassword, String firstName, String middleName,
			String lastName, String email, SchoolClass c)
			throws RegisterException {
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#changeAccount(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, fi.dwo.client.domain.Group, java.lang.String)
	 */
	public void changeAccount(String password, String newPassword,
			String reNewPassword, String firstName, String middleName,
			String lastName, String email, String schoolLogin, Group group,
			String groupPassword) throws RegisterException {
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#changeAccount(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void changeAccount(String password, String newPassword,
			String reNewPassword, String firstName, String middleName,
			String lastName, String email) throws RegisterException {
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#clearCurrentUserData()
	 */
	public void clearCurrentUserData() {
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#deleteClass(fi.dwo.client.domain.SchoolClass)
	 */
	public boolean deleteClass(SchoolClass c) {
		return false;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#deleteCourse(fi.dwo.client.domain.Course)
	 */
	public boolean deleteCourse(Course course) {
		return false;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#deleteSchool(fi.dwo.client.domain.School)
	 */
	public boolean deleteSchool(School sc) {
		return false;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#deleteSco(fi.dwo.client.domain.Sco)
	 */
	public boolean deleteSco(Sco sco) {
		return false;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#deleteUser()
	 */
	public void deleteUser() {
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#editSchool(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public School editSchool(int schoolID, String schoolName,
			String schoolLogin, String studentPassw, String teacherPassw)
			throws SchoolException {
		return null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#getAppletConfig()
	 */
	public AppletConfig[] getAppletConfig() {
		return null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#getCourses()
	 */
	public Course[] getCourses() {
		return null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#getCourseViewNr()
	 */
	public int getCourseViewNr() {
		return courseViewNr;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#getDwoProfile()
	 */
	public DwoProfile getDwoProfile() {
		return dwoProfile;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#getEditableCourses()
	 */
	public Course[] getEditableCourses() {
		return null;
	}

    /**
     * Returns all the available groups.
     * 
     * @return An array of all the available groups.
     *  
     */
    public Group[] getGroups() {
        try {
            return (Group[]) PersistenceFacade.instance().get(Group.class);
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#getResultsModule()
	 */
	public ResultsModuleIF getResultsModule() {
		return null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#getResultsModule(fi.dwo.client.domain.Course[], boolean)
	 */
	public ResultsModuleIF getResultsModule(Course[] courses, boolean showSco) {
		return null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#getResultsModule(fi.dwo.client.domain.Course[])
	 */
	public ResultsModuleIF getResultsModule(Course[] courses) {
		return null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#getResultsModule(fi.dwo.client.domain.SchoolClass)
	 */
	public ResultsModuleIF getResultsModule(SchoolClass schoolClass) {
		return null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#getSchool()
	 */
	public School[] getSchool() {
		return null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#getScoViewNr()
	 */
	public int getScoViewNr() {
		return scoViewNr;
	}

    /**
     * Returns the current user who is logged in. If the user is logged in as a
     * guest, NULL is returned.
     * 
     * @return the current user who is logged in. If the user is logged in as a
     *         guest, NULL is returned.
     *  
     */
    public User getUser() {
        return currentUser;
    }

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#getUserResultsModule(fi.dwo.client.domain.Course)
	 */
	public ResultsModuleIF getUserResultsModule(Course course) {
		return null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#loadCourse(fi.dwo.client.domain.Course)
	 */
	public CenterSubPanel loadCourse(Course course) {
        currentCourse = course;
        return course.getCoursePanel();
	}
	private CenterSubPanel scoPanel;
	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#loadSco(fi.dwo.client.domain.Sco)
	 */
	public CenterSubPanel loadSco(Sco sco) {
        if(currentCourse==null)
        	currentCourse = sco.getCourse();
        currentCourse.setCurrentSco(sco); 
        return scoPanel = sco.getScoPanel(this, currentUser);
	}

    /**
     * Login as guest.
     * CurrentUser becomes an instance of class Guest.
     * @see Guest
     * 
     * @return If the guest was successfully logged in it returns true.
     *         Otherwise it returns false.
     * @throws fi.dwo.client.system.LoginException If some login-information is
     *             incorrect.z
     *  
     */
    public boolean login() throws LoginException {
        currentUser = Guest.instance();
        return true;
    }

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#login(java.lang.String, java.lang.String)
	 */
	public boolean login(String username, String password)
			throws LoginException {
        currentUser = PersistenceFacade.instance().login(username, password);
		return currentUser != null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#logoff()
	 */
	public void logoff() {
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#previewSco(fi.dwo.client.domain.AppletConfig)
	 */
	public ScoPanel previewSco(AppletConfig appletConfig) {
		return null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#previewSco(fi.dwo.client.domain.Sco)
	 */
	public ScoPanel previewSco(Sco sco) {
		return null;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#register(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, fi.dwo.client.domain.Group, java.lang.String)
	 */
	public boolean register(String username, String password,
			String rePassword, String firstname, String middlename,
			String lastname, String email, String schoolLogin, Group group,
			String groupPassword) throws RegisterException {
		return false;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#register(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean register(String username, String password,
			String rePassword, String firstname, String middlename,
			String lastname, String email) throws RegisterException {
		return false;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#renameClass(fi.dwo.client.domain.SchoolClass, java.lang.String)
	 */
	public boolean renameClass(SchoolClass schoolClass, String newName) {
		return false;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#setPanel(java.awt.Panel)
	 */
	public void setPanel(Panel p) {
		System.out.println(p);
		setLayout(new GridLayout(1,1));
		if(scoPanel != null)
			add(scoPanel.getComponent());
		else if(currentCourse != null)
			add(currentCourse.getCoursePanel().getComponent());
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#setReady()
	 */
	public void setReady() {
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#setWait()
	 */
	public void setWait() {
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#setWait(java.lang.String)
	 */
	public void setWait(String waitText) {
		System.out.println(waitText);
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#swapSco(fi.dwo.client.domain.Sco, fi.dwo.client.domain.Sco)
	 */
	public boolean swapSco(Sco sco1, Sco sco2) {
		return false;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#updateCourse(fi.dwo.client.domain.Course)
	 */
	public boolean updateCourse(Course course) {
		return false;
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.domain.DwoIF#updateSco(fi.dwo.client.domain.Sco)
	 */
	public boolean updateSco(Sco sco) {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	public void init() {
		setSize(789,492);
        String lang = getParameter("language");
        if ((lang != null) && (!lang.equals(""))) {
            TextMapper.setLanguage(lang);
            fi.dwo.parameters.system.TextMapper.setLanguage(lang);
        }
        
        boolean guestUser = false; // Wim: teruggezet
        String guestUserString = getParameter("guestUser");
        if(guestUserString!=null && guestUserString.equals("true")) {
        	guestUser = true;
        }
        
        String scoViewNrString = getParameter("scoViewNr");
        if(scoViewNrString!=null && (!scoViewNrString.equals(""))) {
        	try	{
        		scoViewNr = Integer.parseInt(scoViewNrString);
            }catch(Exception e){}
        }
        
        String courseViewNrString = getParameter("courseViewNr");
        if(courseViewNrString!=null && (!courseViewNrString.equals(""))) {
        	try	{
        		courseViewNr = Integer.parseInt(courseViewNrString);
            }catch(Exception e){}
        }
        
        boolean umpc = false;
        String umpcString = getParameter("umpc");
        if(umpcString!=null && umpcString.equals("true")) {
        	umpc = true;
        }
        
        String key = getParameter("key");
        if(key == null) {
            key = "";
        }
        DwoHelper.setKey(key);
        DwoHelper.setAu(new AppletUtil(this));
        DwoHelper.setApplet(this);
        DwoHelper.setUmpc(umpc);
        
        if(!DwoHelper.isApplication()) {
        	dwoProfileID = 1; 
        	String dwoProfileString = getParameter("profile");
             if ((dwoProfileString != null) && (!dwoProfileString.equals(""))) {
                 try	{
                 	dwoProfileID = Integer.parseInt(dwoProfileString);
                	}catch(Exception e){}
             }
             JVMChecker jvmChecker = new JVMChecker(this);
             jvmChecker.check();
        }
       
        try {
			dwoProfile = (DwoProfile)PersistenceFacade.instance().get(dwoProfileID,DwoProfile.class);
		} catch (PersistenceException e) {
		}
		GuiConstants.setDwoProfile(dwoProfileID);
		
        new ToolTipManager(this);
        GuiCreator gc = new GuiCreator(this);
        try {
			PersistenceFacade.instance().reConnect();
		} catch (PersistenceException e) {
		}
        
        if(userName!=null && passWord!=null) {
	        try {
	            GuiCreator.instance().login(userName, passWord);
	            return;
	        } catch (LoginException exc) {
	        	JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), JOptionPane.ERROR_MESSAGE);
	        }
        }
        else if(guestUser) {
        	try {
	            GuiCreator.instance().login();
	            return;
	        } catch (LoginException exc) {
	        	JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), JOptionPane.ERROR_MESSAGE);
	        }
        }
        
// Hier wordt A-Select in DWO actief
        currentUser = getInitialUser();
        if (currentUser != null) // Dit is de enige plaats waar op null
                                 // getest mag worden!
        {
            gc.login(currentUser);
            return;
        }
        try {
			gc.login();
		} catch (LoginException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), JOptionPane.ERROR_MESSAGE);
		}
    }
    /**
     * Stops the current applet. Indicates at the current course that the applet
     * will be stopped.
     */
    public void stop() {
    	this.setWait();
        super.stop();
        if (currentCourse != null) {
            currentCourse.end();
        }
        logoff();
    	this.setReady();
    }

	public String LMSInitialize(String iParam) {
	        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
	        {	return currentCourse.getCurrentSco().LMSInitialize(iParam);
	        }
	        else return null;
	    }

	    public String LMSFinish(String iParam) {
	        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
	        {	return currentCourse.getCurrentSco().LMSFinish(iParam);
	        }
	        else return null;
	    }

	    public String LMSGetValue(String iDataModelElement) {
	        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
	        {	return currentCourse.getCurrentSco().LMSGetValue(iDataModelElement);
	        }
	        else return null;
	    }

	    public String LMSSetValue(String iDataModelElement, String iValue) {
	        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
	        {	return currentCourse.getCurrentSco().LMSSetValue(iDataModelElement, iValue);
	        }
	        else return null;
	    }

	    public String LMSCommit(String iParam) {
	        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
	        {	return currentCourse.getCurrentSco().LMSCommit(iParam);
	        }
	        else return null;
	    }

	    public String LMSGetLastError() {
	        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
	        {	return currentCourse.getCurrentSco().LMSGetLastError();
	        }
	        else return null;
	    }

	    public String LMSGetErrorString(String iErrorCode) {
	        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
	        {	return currentCourse.getCurrentSco().LMSGetErrorString(iErrorCode);
	        }
	        else return null;
	    }

	     public String LMSGetDiagnostic(String iErrorCode) {
	        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
	        {	return currentCourse.getCurrentSco().LMSGetDiagnostic(iErrorCode);
	        }
	        else return null;
	    }

	     /**
	      * Geef mij een gebruiker buitenom.
	      * 
	      * @return a user.
	      */
	     private User getInitialUser()
	     {
	 		this.fidentity = Fidentity.getInstance(this);
	        String username = fidentity.getUid();
	 System.out.println("[" + username + "]");
	        if(username == null||"".equals(username))
	            return null;
	        System.out.println(fidentity.getRole());
	        System.out.println(fidentity.getSchoolUid());
	        String className = fidentity.getClassName();
			System.out.println(className);
	        if ("school".equals(fidentity.getRole()))
	        {
	            System.out.println("Guest from school " + fidentity.getSchoolUid());
	            return null;
	        }
	        
	        
	        User u =  null; // Guest.instance();
	        try
	     {
	         u = PersistenceFacade.instance().login(username);
	         u.setFirstname(fidentity.getFirstName());
	         u.setMiddleName(fidentity.getMiddleName());
	         u.setLastName(fidentity.getSurName());
	         u.setEmail(fidentity.getEmailAddress());
	         
	         /* TODO if user geen lid van school en fidentity.getBrin() != null
	          * meld de user aan bij school
	          */
	         if(null != fidentity.getSchoolUid())
	         {
	        	 School school = u.getSchool();
	        	 if( school == null ||
	        	     !
	        	     (school.getSchoolLogin().equals(fidentity.getSchoolUid())||
	        	      fidentity.getSchoolUid().equals(String.valueOf(school.getSchoolID()))
	        	     )
	        	 )
	        	 {
	        		 try {
						addInitialUserToSchool(u);
					} catch (RegisterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                 u = PersistenceFacade.instance().login(username);
	                 school = u.getSchool();
	        	 }
	        	 setInitialUserInClass(className, u, school);
	         } else
	         {	 /* TODO als user lid en getBrin == null, meld user af! */
	         }
	         
	         u.setLogout(!fidentity.isForeign()); // op verzoek van Peter, logout als          									  // een eigen account.
	         u.setReadonly(false); // voor de klas keuze
	         return u;
	     } catch (LoginException e)
	     {   String msg = TextMapper.getText(TextMapper.EXL_UNKNOWN_USER);
	         if( msg.equals(e.getMessage()) )
	         {  
	             try
	             {
	                 if(fidentity.isRegistered()) 
	                 {   
	                     PersistenceFacade.instance().register(username, 
	                             null, /* no password! */
	                             fidentity.getFirstName(), 
	                             fidentity.getMiddleName(),
	                             fidentity.getSurName(), fidentity.getEmailAddress());
	                     u = PersistenceFacade.instance().login(username);
	                     if (fidentity.getBrin()!=null)
	                     {
	 System.out.println(fidentity.getBrin());
	 System.out.println(fidentity.getSchoolUid());

	                         addInitialUserToSchool(u);
	                     }
	                     // u kan nu een Teacher zijn...
	                     u = PersistenceFacade.instance().login(username);
	                     setInitialUserInClass(className, u, u.getSchool());
	                     u.setLogout(!fidentity.isForeign()); // op verzoek van peter
	                     u.setReadonly(true); // TODO is dit wel
	                                                             // ok?
	                 } else { 
	                     u = new Guest() { 
	                         public String getName() { 
	                             return fidentity.getName();
	                         }
	                     };
	                     u.setLogout(false); // fi-ers en uu-ers.
	                 }
	                 return u;
	             } catch (RegisterException e1)
	             { e1.printStackTrace();
	             } catch (LoginException e2)
	             { 
	                 e2.printStackTrace();
	             }
	             
	         }
	     }
	         return null;
	     }
	 	/**
	 	 * @param u
	 	 * @throws RegisterException
	 	 */
	 	private void addInitialUserToSchool(User u) throws RegisterException {
	 		try
	 		 {
	 		     Group group = findGroup(fidentity.getRole());
	 		     if(group!=null)
	 		     {
	 System.out.println(group.getName() + " " + group.getGroupID());
	  				String schoolUid = fidentity.getSchoolUid();
	 System.out.println(schoolUid);
	  				 String schoolname   = "";
	 		         String schoolpasswd = "";
	 		         School[] school = (School[]) PersistenceFacade.instance().get(School.class);
	 		         for (int i = 0; i < school.length; i++)
	 		         {
	 		             if(school[i].getSchoolLogin().equals (schoolUid) ||
	 		            	schoolUid.equals(String.valueOf(school[i].getSchoolID()))     
	 		             )
	 		             {
	 		                 schoolpasswd = school[i].getPasswd(group.getGroupID());
	 		                 schoolname   = school[i].getSchoolLogin();
	 		                 System.out.println(school[i].getSchoolLogin() + " " + group.getName() + " " + schoolpasswd);
	 		                 break;
	 		             }
	 		         }
	 		         PersistenceFacade.instance().addToSchool(u, schoolname, group, schoolpasswd);
	 		         MapperCreator.instance(User.class).removeAllObjects();
	 		     }
	 		 } catch (PersistenceException e1)
	 		 {
	 		     // TODO Auto-generated catch block
	 		     e1.printStackTrace();
	 		 }
	 	}
	     /**
	      * Converteer een role naar een Group. De namen van de group zijn niet
	      * gelijk aan die van de 'role' (entree.kennisnet.nl). Er moet daarom
	      * gemapped worden.
	      * 
	      * @param role
	      * @return de Group die role representeerd.
	      */
	     private Group findGroup(String role)
	     {
	         if (role == null)
	             return null;
	         // TODO is deze mapping compleet?
	         if ("docent".equals(role))
	             role = "TEACHER";
	         if ("leerling".equals(role))
	             role = "STUDENT";
	         //
	         Group[] groups = getGroups();
	         for (int i = 0; i < groups.length; i++)
	         {
	             Group group = groups[i];
	             if (role.equalsIgnoreCase(group.getName()))
	                 return group;
	         }
	         return null;
	     }
	 	/**
	 	 * Zet een gebruiker in een klas.
	 	 * @param className naam van klas
	 	 * @param u de gebruiker
	 	 * @param school die van u
	 	 */
	 	private void setInitialUserInClass(String className, User u, School school) {
	 		SchoolClass schoolClass = u.getInClass();
	 		 if ( className != null && 
	 		 	  (schoolClass == null || !schoolClass.getName().equals(className)))
	 		 {
	 			 SchoolClass[] classes = school.getClassList();
	 			 for (int i = 0; i < classes.length; i++) {
	 				if(className.equals(classes[i].getName()))
	 				{
	 					u.setInClass(classes[i]);
	 					try {
	 						PersistenceFacade.instance().changeAccount(u,null,null,u.getFirstname(),u.getMiddleName(),u.getLastName(),u.getEmail(),u.getInClass());
	 					} catch (RegisterException e) {
	 						// TODO Auto-generated catch block
	 						e.printStackTrace();
	 					}
	 				}
	 			}
	 		 }
	 	}

	    /**
	     * Returns the LMS value for the specified sco and the specified user.
	     * 
	     * @param sco The sco wherefrom the LMS value is asked.
	     * @param user The usere wherefrom the LMS value is asked.
	     * @param iDataModelElement The parameter to ask for.
	     * @return The value representing for the specified sco, user and parameter.
	     */
	    public String LMSGetValue(Sco sco, User user, String iDataModelElement) {
	        if(iDataModelElement.equals(SCORM12APIInterface.USER_GROUP)) {
	            if(currentUser == null || currentUser instanceof Guest) {
	                return SCORM12APIInterface.UG_GUEST;
	            } else if(currentUser instanceof Teacher) {
	                return SCORM12APIInterface.UG_TEACHER;
	            } else {
	                return SCORM12APIInterface.UG_STUDENT;
	            }
	            
	        } else {
		        try {
		            return PersistenceFacade.instance().LMSGetValue(sco, user, iDataModelElement);
		        } catch (PersistenceException e) {
		        	JOptionPane.showMessageDialog(this, e.getMessage());
		            return "";
		        }
	        }

	    }

	    /**
	     * Sets the LMS value for the specified sco for the current user.
	     * 
	     * @param sco The sco wherefrom the LMS value is set.
	     * @param iDataModelElement The dataModeElement to set.
	     * @param iValue The new value for the dataModeElement.
	     * @return String representing a boolean
	     *         <ul>
	     *         <li><code>true</code> result indicates that the LMSSetValue()
	     *         was successful</li>
	     *         <li><code>false</code> result indicates that the LMSSetValue()
	     *         was unsuccessful</li>
	     *         </ul>
	     */
	    public String LMSSetValue(Sco sco, String iDataModelElement, String iValue) {
	        try {
	            return PersistenceFacade.instance().LMSSetValue(sco, currentUser, iDataModelElement, iValue);
	        } catch (PersistenceException e) {
	        	JOptionPane.showMessageDialog(this, e.getMessage());
	            return Boolean.FALSE.toString();
	        }

	    }
		/* (non-Javadoc)
		 * @see java.applet.Applet#getParameter(java.lang.String)
		 */
		public String getParameter(String name) {
			if("language".equals(name)&& languageOveride != null)
				return languageOveride;
			return super.getParameter(name);
		}

}
