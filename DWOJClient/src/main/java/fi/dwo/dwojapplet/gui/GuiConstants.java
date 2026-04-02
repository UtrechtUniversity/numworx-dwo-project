package fi.dwo.dwojapplet.gui;

import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.User;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

/**
 * Some constants used in the GUI.
 *
 * @author M.J.B. Kupers
 *
 */
public abstract class GuiConstants {

    private static final Logger LOG = Logger.getLogger(GuiConstants.class.getName());

    public static Color MAIN_BACKGROUND = new Color(221, 238, 255);
    public static Color MAIN_FOREGROUND = Color.BLACK;
    public static Color CELL_BACKGROUND = new Color(221, 238, 255);//new Color(210, 230, 255);
    public static Color SUB_BACKGROUND = new Color(255, 255, 255);

    public final static Color RED_COLOR = new Color(190, 61, 46);
    public static Color HEADER_COLOR = Color.black;

    public static String FI_LOGO_LOCATION = "resources/fi.gif";
    public static String WISWEB_LOGO_LOCATION = "resources/wisweb.gif";
    public static String WISWEB_LOGO_SMALL_LOCATION = "resources/numworx.png";

    public final static String EMPTY_COURSE_IMAGE = "resources/course_basis.png";
    public final static String EMPTY_COURSE_MAP = "resources/map.png";

    public final static String REMOVE_STUDENT_IMAGE = "resources/delete.gif";
    public final static String REMOVE_STUDENTMODEL_IMAGE = "resources/delete.gif";

    public final static String REMOVE_CLASS_IMAGE = "resources/delete.gif";
    public final static String EDIT_CLASS_IMAGE = "resources/edit.gif";
    public final static String NOEDIT_CLASS_IMAGE = "resources/noedit.gif";
    public final static String EMPTY_IMAGE = "resources/empty.gif";
    public final static String USERS_CLASS_IMAGE = "resources/userlist.gif";
    public final static String TEACHER_CLASS_IMAGE = "resources/teacherlist.gif";
    public final static String STUDENT_IMAGE = "resources/student.png";
    public final static String COPY_IMAGE = "resources/copy16.png";


    ////peter
    public final static String ASSIGN_CLASS_IMAGE = "resources/assign.gif";
    ////peter

    public final static String REMOVE_COURSE_IMAGE = "resources/delete.gif";
    public final static String EDIT_COURSE_IMAGE = "resources/edit.gif";
    public final static String EDIT_STUDENTMODEL_IMAGE = "resources/eye.png";
    public final static String SCO_COURSE_IMAGE = "resources/zoomin.gif";

    public final static String REMOVE_SCO_IMAGE = "resources/delete.gif";
    public final static String EDIT_SCO_IMAGE = "resources/edit.gif";
    public final static String PARAMETERS_SCO_IMAGE = "resources/parameters.gif";
    public final static String COURSE_SCO_IMAGE = "resources/zoomout.gif";
    public final static String UP_SCO_IMAGE = "resources/orderasc.gif";   // TODO betere plaatjes
    public final static String DOWN_SCO_IMAGE = "resources/orderdesc.gif"; // TODO beter plaatje!

    public final static String RESULTS_ZOOM_IN = "resources/zoomin.gif";

    public final static String RESULTS_ZOOM_OUT = "resources/zoomout.gif";

    public final static String RESULTS_ORDER_ASC = "resources/orderasc.gif";

    public final static String RESULTS_ORDER_DESC = "resources/orderdesc.gif";
    public final static String RESULTS_ORDER_ASCDESC = "resources/orderascdesc.png";
    public final static String RESULTS_STATS = "resources/stat.gif";

    public final static String BACK_MAINMENU_IMAGE = "resources/home.png";
    public final static String BACK_COURSEMENU_IMAGE = "resources/terugknopklein.png";

    public static String GUI_IMAGE_WELCOME = "resources/EPN-welkom.jpg";
    public static String GUI_IMAGE_SCO = "resources/EPN-sco.png";
    public static String GUI_IMAGE_COURSE = "resources/EPN-course.png";

    public static boolean GUI_ICONIZED = false;
    public static String GUI_BGIMAGE_ICON = "resources/iconized-bgimage.png";
    public static String GUI_BGIMAGE_MENU = "resources/menu-bgimage.png";
    public static String GUI_BGIMAGE_SCO = "resources/sco-bgimage.png";
    public static String GUI_RIGHTS_IMAGE = "resources/rights.gif";

    public static int[] GUI_9PATCH_ICON;
    public static int[] GUI_9PATCH_MENU;
    public static int[] GUI_9PATCH_SCO;
    public static Insets GUI_INSETS_ICON;
    public static Insets GUI_INSETS_MENU;
    public static Insets GUI_INSETS_SCO, GUI_INSETS_TREE;

    public static boolean GUI_SCOUPDATE_UNSAFE;

    public final static String RESOURCES =  "" ;

    public final static Font RED_TEXT = new Font("SansSerif", Font.BOLD, 13);
    public final static Font RED_TEXT_ITALIC = new Font("SansSerif", Font.BOLD | Font.ITALIC, 13);

    public final static Font NORMAL_TEXT = new Font("SansSerif", Font.PLAIN, 12);
    public final static Font RESULTS_HEADER_TEXT = new Font("SansSerif", Font.BOLD, 12);
    public final static Font SCO_TEXT = new Font("SansSerif", Font.PLAIN, 16);
    public final static Font SCO_HEADER_TEXT = new Font("SansSerif", Font.PLAIN, 18);
    public final static Font SUB_HEADER_TEXT = new Font("SansSerif", Font.BOLD, 16);
    public final static Font SMALL_TEXT = new Font("SansSerif", Font.BOLD, 10);

    public final static int DWO_HEIGHT = 800;
    public static Font HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);

    public static boolean GUI_IMAGE_BG = false;

    public static int dwoProfile = 1;

    /* Size constants */
    public final static int DWO_WIDTH = 1280;

    public final static int CENTER_WIDTH = 792;

    public final static int CENTER_HEIGHT = 503;
    private static final String INCLUDE = "include";
    public static final String SEARCH_IMAGE = "resources/vergrootglas.gif";

	public static String PLAYER_BASE = null;
    public static String DEPLOY_VARIANT = "";
    public static boolean DEEP_LINK;

    public static String PLAYER, STUDENT_PLAYER;
    
    public static boolean DBL_BUFFER = true;

    public static int getDwoProfile() {
        return dwoProfile;
    }

    /**
     * Zet de huisstijl van een profiel. Alleen profiel 3 (rekenweb) heeft een
     * aparte kleurstelling dan al die andere. Mochten andere profielen weer een
     * eigen huisstijl krijgen, moet je de if(...) { } code weer activeren.
     *
     * @param profile
     * @param ext
     */
    public static void setDwoProfile(DomDwoProfileFull profile, String ext) {
        Properties prop = getProperties(profile, ext);
        installFonts(getString(prop,"fonts"));
        GUI_IMAGE_BG = getBoolean(prop, "gui_image_bg");
        HEADER_TEXT = getFont(prop, "header_text");
        MAIN_BACKGROUND = getColor(prop, "main_background");
        MAIN_FOREGROUND = getColor(prop, "main_foreground");
        CELL_BACKGROUND = getColor(prop, "cell_background");
        FI_LOGO_LOCATION = getString(prop, "fi_logo_location");
        WISWEB_LOGO_SMALL_LOCATION = getString(prop, "wisweb_logo_small_location");
        WISWEB_LOGO_LOCATION = getString(prop, "wisweb_logo_location");
        HEADER_COLOR = getColor(prop, "header_color");

        GUI_IMAGE_WELCOME = getString(prop, "gui_image_welcome");
        GUI_IMAGE_SCO = getString(prop, "gui_image_sco");
        GUI_IMAGE_COURSE = getString(prop, "gui_image_course");

        GUI_ICONIZED = getBoolean(prop, "gui_iconized");
        User.DEFAULT_ICONIZER = getBoolean(prop, "default_iconized") && GUI_ICONIZED;
        GUI_BGIMAGE_ICON = getString(prop, "gui_bgimage_icon");
        GUI_BGIMAGE_MENU = getString(prop, "gui_bgimage_menu");
        GUI_BGIMAGE_SCO = getString(prop, "gui_bgimage_sco");
        GUI_9PATCH_ICON = getIntegerArray(prop, "gui_9patch_icon");
        GUI_9PATCH_MENU = getIntegerArray(prop, "gui_9patch_menu");
        GUI_9PATCH_SCO = getIntegerArray(prop, "gui_9patch_sco");
        GUI_INSETS_ICON = getInsets(prop, "gui_insets_icon");
        GUI_INSETS_MENU = getInsets(prop, "gui_insets_menu");
        GUI_INSETS_SCO = getInsets(prop, "gui_insets_sco");
        GUI_INSETS_TREE = getInsets(prop, "gui_insets_tree");
        //GUI_INSETS_SCO = new Insets(0,0,0,0);
        DWO.SEQUENCE = getBoolean(prop, "sequence_module") || GUI_ICONIZED;
        GUI_SCOUPDATE_UNSAFE = getBoolean(prop, "scoupdate_unsafe");
        DEPLOY_VARIANT = getString(prop, "deployVariant");
        PLAYER = getString(prop, "player");
        STUDENT_PLAYER = getString(prop, "student_player");
        PLAYER_BASE = getString(prop, "player_base");
// TODO deze code opnemen in profile.properties:
        dwoProfile = profile.asLong().intValue();
        
//        if (profile == 5 || profile == 56) {
//            MAIN_BACKGROUND = new Color(255, 255, 255);
//            CELL_BACKGROUND = new Color(255, 255, 255);
//            FI_LOGO_LOCATION = "resources/fi.gif";
//            HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
//            HEADER_COLOR = new Color(0, 0, 0);
//            GUI_IMAGE_BG = true;
//            GUI_IMAGE_WELCOME = "resources/UU-dwo-en-welkom.png";
//            GUI_IMAGE_SCO = "resources/UU-dwo-en-sco.png";
//            GUI_IMAGE_COURSE = "resources/UU-dwo-en-course.png";
//        } else if (profile == 28) {
//            MAIN_BACKGROUND = new Color(255, 255, 255);
//            CELL_BACKGROUND = new Color(255, 255, 255);
//            FI_LOGO_LOCATION = "resources/fi.gif";
//            HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
//            HEADER_COLOR = new Color(0, 0, 0);
//            GUI_IMAGE_BG = true;
//            GUI_IMAGE_WELCOME = "resources/UU-nkbw-welkom.png";
//            GUI_IMAGE_SCO = "resources/UU-nkbw-sco.png";
//            GUI_IMAGE_COURSE = "resources/UU-nkbw-course.png";
//        } else if (profile == 32) {
//            MAIN_BACKGROUND = new Color(255, 255, 255);
//            CELL_BACKGROUND = new Color(255, 255, 255);
//            FI_LOGO_LOCATION = "resources/fi.gif";
//            HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
//            HEADER_COLOR = new Color(0, 0, 0);
//            GUI_IMAGE_BG = true;
//            GUI_IMAGE_WELCOME = "resources/zoefi-welkom22.jpg";
//            GUI_IMAGE_SCO = "resources/zoefi-sco.jpg";
//            GUI_IMAGE_COURSE = "resources/zoefi-course.jpg";
//        } else if (profile == 36) {
//            MAIN_BACKGROUND = new Color(255, 255, 255);
//            CELL_BACKGROUND = new Color(255, 255, 255);
//            FI_LOGO_LOCATION = "resources/fi.gif";
//            HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
//            HEADER_COLOR = new Color(0, 0, 0);
//            GUI_IMAGE_BG = true;
//            GUI_IMAGE_WELCOME = "resources/UU-mi-welkom.png";
//            GUI_IMAGE_SCO = "resources/UU-mi-sco.png";
//            GUI_IMAGE_COURSE = "resources/UU-mi-course.png";
//        } else if (profile == 16 || profile == 25 || profile == 26 || profile == 29 || profile == 30 || profile == 35 || profile == 37 || profile == 50) {
//            MAIN_BACKGROUND = new Color(255, 255, 255);
//            CELL_BACKGROUND = new Color(255, 255, 255);
//            FI_LOGO_LOCATION = "resources/fi.gif";
//            HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
//            HEADER_COLOR = new Color(0, 0, 0);
//            GUI_IMAGE_BG = true;
//            GUI_IMAGE_WELCOME = "resources/UU-mbo-welkom.png";
//            GUI_IMAGE_SCO = "resources/UU-mbo-sco.png";
//            GUI_IMAGE_COURSE = "resources/UU-mbo-course.png";
//        } else if (profile == 38 || profile == 13 || profile == 20 || profile == 48) {
//            MAIN_BACKGROUND = new Color(255, 255, 255);
//            CELL_BACKGROUND = new Color(255, 255, 255);
//            FI_LOGO_LOCATION = "resources/fi.gif";
//            HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
//            HEADER_COLOR = new Color(0, 0, 0);
//            GUI_IMAGE_BG = true;
//            GUI_IMAGE_WELCOME = "resources/EPN-welkom.jpg";
//            GUI_IMAGE_SCO = "resources/EPN-sco.png";
//            GUI_IMAGE_COURSE = "resources/EPN-course.png";
//        } else if (profile == 51 || profile == 27) {
//            MAIN_BACKGROUND = new Color(255, 255, 255);
//            CELL_BACKGROUND = new Color(255, 255, 255);
//            FI_LOGO_LOCATION = "resources/fi.gif";
//            HEADER_TEXT = new Font("SansSerif", Font.BOLD, 33);
//            HEADER_COLOR = new Color(0, 95, 169);
//            GUI_IMAGE_BG = true;
//            GUI_IMAGE_WELCOME = "resources/MW-welkom.png";
//            GUI_IMAGE_SCO = "resources/MW-tf-sco.png";
//            GUI_IMAGE_COURSE = "resources/MW-tf-course.png";
//        } else if (profile == 39 || profile == 58) {
//            MAIN_BACKGROUND = new Color(255, 255, 255);
//            CELL_BACKGROUND = new Color(255, 255, 255);
//            FI_LOGO_LOCATION = "resources/fi.gif";
//            HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
//            HEADER_COLOR = new Color(0, 0, 0);
//            GUI_IMAGE_BG = true;
//            GUI_IMAGE_WELCOME = "resources/UU-sk-welkom.png";
//            GUI_IMAGE_SCO = "resources/UU-sk-sco.png";
//            GUI_IMAGE_COURSE = "resources/UU-sk-course.png";
//        } else if (profile == 40 || profile == 41) {
//            MAIN_BACKGROUND = new Color(255, 255, 255);
//            CELL_BACKGROUND = new Color(255, 255, 255);
//            FI_LOGO_LOCATION = "resources/fi.gif";
//            HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
//            HEADER_COLOR = new Color(0, 0, 0);
//            GUI_IMAGE_BG = true;
//            GUI_IMAGE_WELCOME = "resources/UU-ec-welkom.png";
//            GUI_IMAGE_SCO = "resources/UU-ec-sco.png";
//            GUI_IMAGE_COURSE = "resources/UU-ec-course.png";
//        } else if (profile == 42) {
//            MAIN_BACKGROUND = new Color(255, 255, 255);
//            CELL_BACKGROUND = new Color(255, 255, 255);
//            FI_LOGO_LOCATION = "resources/fi.gif";
//            HEADER_TEXT = new Font("SansSerif", Font.BOLD, 36);
//            HEADER_COLOR = new Color(0, 0, 0);
//            GUI_IMAGE_BG = true;
//            GUI_IMAGE_WELCOME = "resources/UU-bvsd-welkom.png";
//            GUI_IMAGE_SCO = "resources/UU-bvsd-sco.png";
//            GUI_IMAGE_COURSE = "resources/UU-bvsd-course.png";
//        } else {
//        }
    }

    private static void installFonts(String fonts) {
    	if(fonts == null) return;
    	StringTokenizer st = new StringTokenizer(fonts.trim(), ",");
    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	URL root = DwoHelper.getResourceUrlPath();
		while ( st.hasMoreTokens()) {
			try {
				String font = st.nextToken().trim();
				URL u = new URL(root, font);
				InputStream in = u.openStream();
				Font f = Font.createFont(Font.TRUETYPE_FONT, in);
				ge.registerFont(f);
				in.close();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "installFonts", e);
			}
		}
		
	}

	private static Insets getInsets(Properties prop, String key) {
        int[] data = getIntegerArray(prop, key);
        return new Insets(data[0], data[1], data[2], data[3]);
    }

    private static int[] getIntegerArray(Properties prop, String key) {
        String data = prop.getProperty(key);
        StringTokenizer st = new StringTokenizer(data);
        int[] result = new int[st.countTokens()];
        int i = 0;
        while (st.hasMoreElements()) {
            result[i++] = Integer.parseInt(st.nextToken());
        }
        return result;
    }

    private static Properties getProperties(DomDwoProfileFull profile, String testExtension) {
        if (testExtension == null) {
            testExtension = "";
        }
        Properties result = new Properties();
        ResourceBundle rb = ResourceBundle.getBundle("fi.dwo.dwojapplet.gui.resources.default", JComponent.getDefaultLocale());
        InputStream in = GuiConstants.class.getResourceAsStream("resources/default.properties");
        try {
            result.load(in);
 // convert resourcebundle to (locallized) properties 
            for(String key: rb.keySet()) {
            	result.put(key, rb.getObject(key));
            }
            if (profile.getBase() != null) {
            	result.put("student_player", profile.getBase() + "/");
            }
            result.put("player_base", DwoHelper.getAppURLPath().toString());
            
            if ( !testExtension.isEmpty()) {
              String r = "resources/profile-" + profile.asLong()  + ".properties";
              URL u = new URL(DwoHelper.getResourceUrlPath(),r);
              result = getProperties(u, result);
            }
            String resource = "resources/profile-" + profile.asLong() + testExtension + ".properties";
            URL u;
            u = new URL(DwoHelper.getResourceUrlPath(),resource);
            result = getProperties(u, result);
            resource = "resources/profile-" + encode(profile) + testExtension + ".properties";
            u = new URL(DwoHelper.getResourceUrlPath(), resource);
            result = getProperties(u, result);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return result;
    }

	@SuppressWarnings("deprecation")
	private static String encode(DomDwoProfileFull profile) {
		return URLEncoder.encode(profile.getDwoProfileName()).replace("+", "%20");
	}

    private static Properties getProperties(URL resource, Properties base) {
        Properties result = base;
        try {
            InputStream in = resource.openStream();
            result = new Properties(base);
            result.load(in);
            String include = result.getProperty(INCLUDE);
            if (include != null) {
                URL u = new URL(resource, include);
                result = getProperties(u, base);
                in = resource.openStream();
                result = new Properties(result);
                result.load(in);
            }

        } catch (Exception e) {
        }
        return result;
    }

    private static boolean getBoolean(Properties p, String key) {
        String value = p.getProperty(key);
        return Boolean.parseBoolean(value); // since 1.5
    }

    private static String getString(Properties p, String key) {
        return p.getProperty(key);
    }

    private static Color getColor(Properties p, String key) {
        String value = getString(p, key);
        return Color.decode(value);
    }

    private static Font getFont(Properties p, String key) {
        String value = getString(p, key);
        StringTokenizer st = new StringTokenizer(value);
        String fontname = st.nextToken();
        int size = Integer.parseInt(st.nextToken());
        int style = 0;
        while (st.hasMoreTokens()) {
            switch (st.nextToken().charAt(0)) {
                case 'B':
                case 'b':
                    style |= Font.BOLD;
                    break;
                case 'I':
                case 'i':
                    style |= Font.ITALIC;
                    break;
            }
        }
        return new Font(fontname, style, size);
    }

    /**
     * Als we te maken hebben met een deeplink, staat de iconizer uit. XXX nog
     * meer?
     *
     * @param scoViewNr
     * @param courseViewNr
     */
    public static void fixIconizer(int scoViewNr,
            int courseViewNr) {
        if (scoViewNr != 0 || courseViewNr != 0) {
            GUI_ICONIZED = false;
            User.DEFAULT_ICONIZER = false;
            DEEP_LINK = true;
        }

    }

}
