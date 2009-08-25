// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\GuiConstants.java

package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Font;

import fi.dwo.client.domain.DwoHelper;

/**
 * Some constants used in the GUI.
 * 
 * @author M.J.B. Kupers
 *  
 */
public abstract class GuiConstants {
//    public static Color MAIN_BACKGROUND = new Color(230, 240, 255);
    public static Color MAIN_BACKGROUND = new Color(221, 238, 255);
    public static Color CELL_BACKGROUND = new Color(221, 238, 255);//new Color(210, 230, 255);
    public static Color SUB_BACKGROUND = new Color(255, 255, 255);

    public final static Color RED_COLOR = new Color(190, 61, 46);

    public static String FI_LOGO_LOCATION = "resources/fi.gif";
    public static String WISWEB_LOGO_LOCATION = "resources/wisweb.gif";
    public static String WISWEB_LOGO_SMALL_LOCATION = "resources/wiswebklein.gif";

    public final static String EMPTY_COURSE_IMAGE = "resources/course_basis.gif";

    public final static String REMOVE_STUDENT_IMAGE = "resources/delete.gif";

    public final static String REMOVE_CLASS_IMAGE = "resources/delete.gif";
    public final static String EDIT_CLASS_IMAGE = "resources/edit.gif";
    public final static String USERS_CLASS_IMAGE = "resources/userlist.gif";
    
    ////peter
    public final static String ASSIGN_CLASS_IMAGE = "resources/assign.gif";
    ////peter

    public final static String REMOVE_COURSE_IMAGE = "resources/delete.gif";
    public final static String EDIT_COURSE_IMAGE = "resources/edit.gif";
    public final static String SCO_COURSE_IMAGE = "resources/zoomin.gif";
    
    public final static String REMOVE_SCO_IMAGE = "resources/delete.gif";
    public final static String EDIT_SCO_IMAGE = "resources/edit.gif";
    public final static String PARAMETERS_SCO_IMAGE = "resources/parameters.gif";
    public final static String COURSE_SCO_IMAGE = "resources/zoomout.gif";
    public final static String UP_SCO_IMAGE = "resources/orderasc.gif";   // TODO betere plaatjes
    public final static String DOWN_SCO_IMAGE= "resources/orderdesc.gif"; // TODO beter plaatje!

    public final static String RESULTS_ZOOM_IN = "resources/zoomin.gif";

    public final static String RESULTS_ZOOM_OUT = "resources/zoomout.gif";

    public final static String RESULTS_ORDER_ASC = "resources/orderasc.gif";

    public final static String RESULTS_ORDER_DESC = "resources/orderdesc.gif";
    public final static String RESULTS_ORDER_ASCDESC = "resources/orderascdesc.png";
    
    public final static String BACK_MAINMENU_IMAGE = "resources/home.png";
    public final static String BACK_COURSEMENU_IMAGE = "resources/terugknopklein.png";
    
    public static String GUI_IMAGE_WELCOME = "resources/EPN-welkom.jpg";
    public static String GUI_IMAGE_SCO = "resources/EPN-sco.png";
    public static String GUI_IMAGE_COURSE = "resources/EPN-course.png";
    
    public final static String RESOURCES = (DwoHelper.isApplication() ? "" : "/dwo/");
    //public final static String RESOURCES = "";

    public final static Font RED_TEXT = new Font("SansSerif", Font.BOLD, 13);
    public final static Font RED_TEXT_ITALIC = new Font("SansSerif", Font.BOLD | Font.ITALIC, 13);

    public final static Font NORMAL_TEXT = new Font("SansSerif", Font.PLAIN, 12);
    public final static Font RESULTS_HEADER_TEXT = new Font("SansSerif", Font.BOLD, 12);
    public final static Font SCO_TEXT = new Font("SansSerif", Font.PLAIN, 16);
    public final static Font SCO_HEADER_TEXT = new Font("SansSerif", Font.PLAIN, 18);

    public final static Font SMALL_TEXT = new Font("SansSerif", Font.BOLD, 10);

    public final static int DWO_HEIGHT = 600;
    public static Font HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
    
    public static boolean GUI_IMAGE_BG = false;

    /* Size constants */
    public final static int DWO_WIDTH = 800;

    public final static int CENTER_WIDTH = 792;

    public final static int CENTER_HEIGHT = 503;
 
    /**
     * Zet de huisstijl van een profiel.
     * Alleen profiel 3 (rekenweb) heeft een aparte kleurstelling dan al 
     * die andere.
     * Mochten andere profielen weer een eigen huisstijl krijgen, moet je
     * de if(...) { }  code weer activeren.
     * @param profile
     */
	public static void setDwoProfile(int profile)
	{
//		if(profile==1)
//		{	MAIN_BACKGROUND = new Color(221, 238, 255);
//    		CELL_BACKGROUND = new Color(221, 238, 255);
//			FI_LOGO_LOCATION = "resources/fi.gif";
//			HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
//		} else
//		if(profile==2)
//		{	MAIN_BACKGROUND = new Color(221, 238, 255);
//    		CELL_BACKGROUND = new Color(221, 238, 255);
//			FI_LOGO_LOCATION = "resources/fi.gif";
//			HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
//		} else
		if(profile==3)
		{	MAIN_BACKGROUND = new Color(255,255,200);
    		CELL_BACKGROUND = new Color(255,255,200);
			FI_LOGO_LOCATION = "resources/rekenweb.png";
			WISWEB_LOGO_SMALL_LOCATION = FI_LOGO_LOCATION; // Dit is REKENWEB!
			WISWEB_LOGO_LOCATION = FI_LOGO_LOCATION;
			HEADER_TEXT = new Font("SansSerif", Font.BOLD, 33);
			GUI_IMAGE_BG = false;
		} else
		if(profile==1)
		{	MAIN_BACKGROUND = new Color(255,255,255);
		   	CELL_BACKGROUND = new Color(255,255,255);
			FI_LOGO_LOCATION = "resources/fi.gif";
			HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
			GUI_IMAGE_BG = true;
			GUI_IMAGE_WELCOME = "resources/UU-dwo-welkom.png";
			GUI_IMAGE_SCO = "resources/UU-dwo-sco.png";
			GUI_IMAGE_COURSE = "resources/UU-dwo-course.png";
		} else	
		if(profile==28)
		{	MAIN_BACKGROUND = new Color(255,255,255);
		   	CELL_BACKGROUND = new Color(255,255,255);
			FI_LOGO_LOCATION = "resources/fi.gif";
			HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
			GUI_IMAGE_BG = true;
			GUI_IMAGE_WELCOME = "resources/UU-nkbw-welkom.png";
			GUI_IMAGE_SCO = "resources/UU-nkbw-sco.png";
			GUI_IMAGE_COURSE = "resources/UU-nkbw-course.png";
		} else
		if(profile==34)
		{	MAIN_BACKGROUND = new Color(255,255,255);
		   	CELL_BACKGROUND = new Color(255,255,255);
			FI_LOGO_LOCATION = "resources/fi.gif";
			HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
			GUI_IMAGE_BG = true;
			GUI_IMAGE_WELCOME = "resources/UU-dwo-welkom.png";
			GUI_IMAGE_SCO = "resources/UU-dwo-sco.png";
			GUI_IMAGE_COURSE = "resources/UU-dwo-course.png";
		} else
		if(profile==36)
		{	MAIN_BACKGROUND = new Color(255,255,255);
		   	CELL_BACKGROUND = new Color(255,255,255);
			FI_LOGO_LOCATION = "resources/fi.gif";
			HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
			GUI_IMAGE_BG = true;
			GUI_IMAGE_WELCOME = "resources/UU-mi-welkom.png";
			GUI_IMAGE_SCO = "resources/UU-mi-sco.png";
			GUI_IMAGE_COURSE = "resources/UU-mi-course.png";
		} else
		if(profile==16 || profile==25 || profile==26 || profile==29 || profile==30 || profile==35 || profile==37)
		{	MAIN_BACKGROUND = new Color(255,255,255);
		   	CELL_BACKGROUND = new Color(255,255,255);
			FI_LOGO_LOCATION = "resources/fi.gif";
			HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
			GUI_IMAGE_BG = true;
			GUI_IMAGE_WELCOME = "resources/UU-mbo-welkom.png";
			GUI_IMAGE_SCO = "resources/UU-mbo-sco.png";
			GUI_IMAGE_COURSE = "resources/UU-mbo-course.png";
		}else
		if(profile==38)
		{	MAIN_BACKGROUND = new Color(255,255,255);
	    	CELL_BACKGROUND = new Color(255,255,255);
			FI_LOGO_LOCATION = "resources/fi.gif";
			HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
			GUI_IMAGE_BG = true;
			GUI_IMAGE_WELCOME = "resources/EPN-welkom.jpg";
			GUI_IMAGE_SCO = "resources/EPN-sco.png";
			GUI_IMAGE_COURSE = "resources/EPN-course.png";
		} else
			if(profile==39)
			{	MAIN_BACKGROUND = new Color(255,255,255);
		    	CELL_BACKGROUND = new Color(255,255,255);
				FI_LOGO_LOCATION = "resources/fi.gif";
				HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
				GUI_IMAGE_BG = true;
				GUI_IMAGE_WELCOME = "resources/UU-sk-welkom.png";
				GUI_IMAGE_SCO = "resources/UU-sk-sco.png";
				GUI_IMAGE_COURSE = "resources/UU-sk-course.png";
			} else
			if(profile==40 || profile==41)
			{	MAIN_BACKGROUND = new Color(255,255,255);
		    	CELL_BACKGROUND = new Color(255,255,255);
				FI_LOGO_LOCATION = "resources/fi.gif";
				HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
				GUI_IMAGE_BG = true;
				GUI_IMAGE_WELCOME = "resources/UU-ec-welkom.png";
				GUI_IMAGE_SCO = "resources/UU-ec-sco.png";
				GUI_IMAGE_COURSE = "resources/UU-ec-course.png";
			}else
			if(profile==42)
			{	MAIN_BACKGROUND = new Color(255,255,255);
			   	CELL_BACKGROUND = new Color(255,255,255);
				FI_LOGO_LOCATION = "resources/fi.gif";
				HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
				GUI_IMAGE_BG = true;
				GUI_IMAGE_WELCOME = "resources/UU-bvsd-welkom.png";
				GUI_IMAGE_SCO = "resources/UU-bvsd-sco.png";
				GUI_IMAGE_COURSE = "resources/UU-bvsd-course.png";
			} else
//		if(profile==4)
//		{	MAIN_BACKGROUND = new Color(221, 238, 255);
//    		CELL_BACKGROUND = new Color(221, 238, 255);
//			FI_LOGO_LOCATION = "resources/fi.gif";
//			HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
//		} else 
		{
// de default als profile != 3
			MAIN_BACKGROUND = new Color(221, 238, 255);
    		CELL_BACKGROUND = new Color(221, 238, 255);
			FI_LOGO_LOCATION = "resources/fi.gif";
			HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
			GUI_IMAGE_BG = false;
		}
	}
}