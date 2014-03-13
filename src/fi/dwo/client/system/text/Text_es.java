//Source file:
//N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\Text_en.java

package fi.dwo.client.system.text;

import java.util.ListResourceBundle;

import fi.dwo.client.system.TextMapper;

public class Text_es extends ListResourceBundle {
 private final Object[][] contents = {
         { TextMapper.USER_GUEST, "Invitado"},
         { TextMapper.BTN_LOGIN, "Iniciar sesión" },
         { TextMapper.BTN_CANCEL, "Cancelar" },
         { TextMapper.BTN_NO, "No" },
         { TextMapper.BTN_OK, "OK" },
         { TextMapper.BTN_YES, "Sí" },
         { TextMapper.BTN_CLOSE, "Cerrar"},
         { TextMapper.DLG_CONFIRM, "Confirmar" },
         { TextMapper.DLG_ENTER_INPUT, "Introduzca una entrada" },
         { TextMapper.DLG_MESSAGE, "Mensaje" },

         { TextMapper.EX_UNKNOWN_ERROR, "Se ha producido un error interno" },
         { TextMapper.EXR_USER_EXISTS, "Este nombre de usuario ya existe" },
         { TextMapper.EXR_USER_EXISTS2, "El nombre de usuario {0} ya existe" },
         { TextMapper.EXR_WRONG_SECOND_PASSWORD, "Las contraseñas proporcionadas no coinciden" },
         { TextMapper.EXR_WRONG_USERNAME_PASSWORD, "No se ha encontrado un usuario con este nombre y contraseña de usuario" },
         { TextMapper.EXR_UNKNOWN_SCHOOLGROUP, "Combinación de Institución / grupo / contraseña desconocida" },
         { TextMapper.EXR_MANDATORY, "El campo ''{0}'' no está completo." },
        	// "{0} de {1} se han completado. Este es un campo obligatorio"},
         { TextMapper.EXR_WRONG_FORMAT, "{0} de {1} contiene caracteres no aceptados" },
         { TextMapper.EXR_WRONG_EMAILFORMAT, "{0} de {1} es incorrecto" },
         { TextMapper.EXC_CLASS_EXISTS, "Esta clase ya existe" },
         { TextMapper.EXS_SCHOOL_EXISTS, "Ya existe una escuela con este nombre de usuario" },
         { TextMapper.EXL_UNKNOWN_USER, "No se ha encontrado ningún usuario con este nombre y contraseña" },
         { TextMapper.EXC_COURSE_EXISTS, "Este módulo ya existe" },
         { TextMapper.EXS_SCO_EXISTS, "Esta Actividad ya existe" },
         { TextMapper.EXS_NO_APPLET, "Applet no encontrado"},

         { TextMapper.GUI_WAIT_A_MOMENT, "Un momento por favor"},

         { TextMapper.GUIW_LOGINDATA, "Datos de inicio de sesión" },
         { TextMapper.GUIW_USERNAME, "Nombre de usuario" },
         { TextMapper.GUIW_PASSWORD, "Contraseña" },
         { TextMapper.GUIW_WELCOME, "Bienvenido/a" },
         { TextMapper.GUIW_GUESTLOGIN, "Entrar como invitado" },
         { TextMapper.GUIW_REGISTER, "Registrarse" },
         { TextMapper.GUIW_MSG_WORK_NOT_SAVE, "El trabajo NO QUEDARÁ GUARDADO" },
         { TextMapper.GUIW_MSG_REGISTER_NEW, "Registrarse como un nuevo usuario" },
         { TextMapper.GUIW_BTN_GUESTLOGIN, "Entrar como invitado" },
         { TextMapper.GUIW_BTN_LOGIN, "Nombre de usuario" },
         { TextMapper.GUIW_BTN_REGISTER, "Registrarse" },
         { TextMapper.GUIW_ERR_LOGIN, "Error al iniciar sesión" },

         { TextMapper.GUIR_REGISTER, "Registrarse" },

         { TextMapper.GUIR_REGISTERINFO, "Registro de nuevos usuarios" },
         { TextMapper.GUIR_PERSONALINFO, "Información personal" },
         { TextMapper.GUIR_SCHOOLINFO, "Información del centro educativo " },

         { TextMapper.GUIR_USERNAME, "Usuario" },
		 { TextMapper.GUIR_PASSWORD, "Contraseña" },
		 { TextMapper.GUIR_RE_PASSWORD, "Confirma tu contraseña" },

		 { TextMapper.GUIR_FIRSTNAME, "Nombre" },
		 { TextMapper.GUIR_MIDDLENAME, "Prefijo" },
		 { TextMapper.GUIR_LASTNAME, "Apellido" },
		 { TextMapper.GUIR_EMAIL, "Correo electrónico" },

		 { TextMapper.GUIR_SCHOOLLOGIN, "Nombre del centro educativo " },
		 { TextMapper.GUIR_SCHOOLGROUP, "Yo soy" },
		 { TextMapper.GUIR_SCHOOLPASSWORD, "Contraseña" },

		 { TextMapper.GUIR_BTN_REGISTER, "Registrarse" },
		 { TextMapper.GUIR_BTN_RESET, "Reiniciar" },
         { TextMapper.GUIR_BTN_BACK, "Volver a los módulos" },

            { TextMapper.GUIR_MSG_PROVIDED_SCHOOL, "Información proporcionada por el centro educativo" },

            { TextMapper.GUIR_OPT_SELECT_GROUP, "Escoge una opción" },
            { TextMapper.GUIR_OPT_STUDENT, "Estudiante" },
            { TextMapper.GUIR_OPT_TEACHER, "Profesor/a" },
            { TextMapper.GUIR_OPT_ADMIN, "Administrador/a"},
            { TextMapper.GUIR_OPT_SCHOOLADMIN, "Administrador/a del centro educativo " },
            { TextMapper.GUIR_OPT_SCHOOLCODE, "Código del centro educativo "},
            
            { TextMapper.GUIR_ERR_REGISTER, "Ha ocurrido un error" },

            { TextMapper.GUIR_MSG_REGISTERED, "Te has registrado de forma satisfactoria"},

            { TextMapper.GUIM_DWO_FULL, "Entorno Digital de Matemáticas" },
            { TextMapper.GUIM_DWO_SHORT, "EDM" },            { TextMapper.GUIM_FI_NAME, "Instituto Freudenthal"},
            { TextMapper.GUIM_MAIN_MENU, "Módulos" },

            { TextMapper.GUIL_LOGGED_IN_AS, "Estás conectado como" },
            { TextMapper.GUIL_NOT_LOGGED_IN, "No estás conectado"},
            { TextMapper.GUIL_BTN_LOGIN, "Iniciar sesión"},
            { TextMapper.GUIL_BTN_LOGOFF, "Cerrar sesión " },

            { TextMapper.GUIMNU_MAIN_MENU, "Módulos" },
            { TextMapper.GUIMNU_MY_PROFILE, "Mi perfil" },
            { TextMapper.GUIMNU_STUDENT_IN_CLASS, "Estudiante de la clase" },
            { TextMapper.GUIMNU_STUDENT_NO_CLASS_0, "Todavía no eres "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_1, "miembro de la clase.  "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_2, "Ir a"},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_3, "\"Mi perfil\" y "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_4, "escoge una clase."},
            { TextMapper.GUIMNU_CLASS_RESULTS, "Resultados de clase" },
            { TextMapper.GUIMNU_RESULTS, "Ir a resultados" },
            { TextMapper.GUIMNU_CLASS_MANAGEMENT, "Gestión de la clase" },
            { TextMapper.GUIMNU_SCHOOL_MANAGEMENT, "Gestión del centro educativo" },
            { TextMapper.GUIMNU_COURSE_MANAGEMENT, "Gestión del módulo"},
            { TextMapper.GUIMNU_MSG_ADD_CLASS, "Nombre de la nueva clase" },
            { TextMapper.GUIMNU_MSG_ADD_CLASS_TITLE, "Añadir una nueva clase" },
            { TextMapper.GUIMNU_MSG_ADD_SCHOOL, "Nombre del nuevo centro educativo "},   
         	{ TextMapper.GUIMNU_MSG_ADD_SCHOOL_TITLE, "Añadir un nuevo centro educativo"},
         	{ TextMapper.GUIMNU_USERS_SCHOOL, "Usuarios del centro educativo"},
            { TextMapper.GUIMNU_CLASSES_SCHOOL, "Clases del centro educativo "},
            { TextMapper.GUIMNU_FEATURES_SCHOOLADMIN, "Características del administrador del centro educativo "},
            
            { TextMapper.GUIUMP_MANAGE_USERS, "Gestión de usuarios"},
            { TextMapper.GUIUMP_REMOVE_FROM_SCHOOL, "Eliminar solo del centro educativo "},
            { TextMapper.GUIUMP_REMOVE_COMPLETE, "Eliminar la cuenta completamente"},
            { TextMapper.GUIUMP_ADD_STUDENTS, "Añadir un/a nuevo/a estudiante"},
            { TextMapper.GUIUMP_ADD_TEACHERS, "Añadir un/a nuevo/a profesor/a"},
            { TextMapper.GUIUMP_IMPORT_CLIPBOARD, "Importar desde el portapapeles"},
            { TextMapper.GUIUMP_MAKE_ACCOUNTS, "Abrir cuentas"},
            { TextMapper.GUIUMP_EXTRA_ROW, "Fila adicional"},

         	{ TextMapper.GUICO_HEADER, "Módulos"},
         	{ TextMapper.GUICO_SCO_LIST_TITLE, "Actividades"},

            { TextMapper.GUIP_MY_PROFILE, "Mi perfil" },
            { TextMapper.GUIP_REGISTERINFO, "Información para registrarse" },
            { TextMapper.GUIP_PERSONALINFO, "Información personal" },
            { TextMapper.GUIP_SCHOOLINFO, "Información del centro educativo " },

            { TextMapper.GUIP_USERNAME, "Usuario" },
            { TextMapper.GUIP_OLD_PASSWORD, "Contraseña actual" },
            { TextMapper.GUIP_PASSWORD, "Nueva contraseña" },
            { TextMapper.GUIP_RE_PASSWORD, "Confirmar contraseña" },

            { TextMapper.GUIP_FIRSTNAME, "Nombre" },
            { TextMapper.GUIP_MIDDLENAME, "Prefijo" },
            { TextMapper.GUIP_LASTNAME, "Apellido" },
            { TextMapper.GUIP_EMAIL, "Correo electrónico" },

            { TextMapper.GUIP_SCHOOLLOGIN, "Nombre del centro educativo " },
            { TextMapper.GUIP_SCHOOLGROUP, "Yo soy" },
            { TextMapper.GUIP_SCHOOLPASSWORD, "Contraseña" },
            { TextMapper.GUIP_CLASS, "Clase" },

            { TextMapper.GUIP_BTN_SAVE, "Guardar" },
            { TextMapper.GUIP_BTN_RESET, "Reiniciar" },
            { TextMapper.GUIP_BTN_DELETE_PROFILE, "Borrar perfil" },

            { TextMapper.GUIP_MSG_PROVIDED_SCHOOL, "Información proporcionada por el centro educativo " },

            { TextMapper.GUIP_ERR_CHANGE, "Ha ocurrido un error" },

            { TextMapper.GUIP_OPT_SELECT_GROUP, "Escoge una opción" },

            { TextMapper.GUIP_CONFIRM_REMOVE_USER, "¿Seguro que quieres eliminar la cuenta?" },
            { TextMapper.GUIP_CONFIRM_REMOVE_USER_TITLE, "Borrar cuenta" },

            { TextMapper.GUIP_MSG_PROFILE_CHANGED, "Tu cuenta ha sido cambiada "},

            { TextMapper.GUIPT_SCHOOL, "Centro educativo" },
            { TextMapper.GUIPT_TEACHER_FROM_CLASS, "Profesor/a de la clase" },
            { TextMapper.GUIPT_BTN_ADD_CLASS, "Añadir una clase" },
            
         { TextMapper.GUIS_STUDENTS, "Estudiantes"},
         { TextMapper.GUIS_TEACHERS, "Profesores"},
         { TextMapper.GUIS_SCHOOL_MANAGEMENT, "Gestión del centro educativo "},

         { TextMapper.GUIS_TLTP_DELETE_SCHOOL, "Borrar centro educativo {0} "},
         { TextMapper.GUIS_TLTP_EDIT_SCHOOL, "Editar nombre del centro educativo "},
         { TextMapper.GUIS_TLTP_USERS_SCHOOL, "Estudiante de {0}"},
         
         { TextMapper.GUIS_ADD_SCHOOL, "Añadir centro educativo "},
         { TextMapper.GUIS_DELETE_SCHOOL, "Borrar centro educativo "},
         { TextMapper.GUIS_RENAME_SCHOOL, "Editar nombre del centro educativo "}, 
         { TextMapper.GUIS_MSG_RENAME_SCHOOL, "Introducir un nuevo centro educativo "},
         { TextMapper.GUIS_MSG_DELETE_SCHOOL, "¿Seguro que quieres eliminar este centro educativo?"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY, "Este centro educativo contiene usuarios. ¿Seguro que quieres eliminarlo?"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY_TITLE, "Este centro educativo contiene usuarios."},
         { TextMapper.GUIS_MSG_DELETE_STUDENT, "¿Seguro que quieres eliminar a {0} del centro educativo "},
         { TextMapper.GUIS_DELETE_STUDENT, "Borrar estudiantes de este centro educativo "},
         { TextMapper.GUIS_NO_STUDENTS, "El centro educativo {0} no contiene estudiantes"}, 

         { TextMapper.GUIC_STUDENTS, "Estudiantes"},
         { TextMapper.GUIC_CLASS_MANAGEMENT, "Gestión de la clase"},

         { TextMapper.GUIC_TLTP_DELETE_CLASS, "Borrar clase {0} "},
         { TextMapper.GUIC_TLTP_EDIT_CLASS, "Editar el nombre de la clase"},
         { TextMapper.GUIC_TLTP_USERS_CLASS, "Estudiantes en la clase {0}"},

         ////peter
         { TextMapper.GUIC_TLTP_ASSIGN_CLASS, "Asignar módulos a la clase {0}"},
		 ////peter

            { TextMapper.GUIC_STUDENTS, "Estudiantes" },
            { TextMapper.GUIC_ADD_CLASS, "Crear una clase"},
            { TextMapper.GUIC_DELETE_CLASS, "Borrar una clase" },
            { TextMapper.GUIC_RENAME_CLASS, "Editar el nombre de la clase" },
            { TextMapper.GUIC_MSG_RENAME_CLASS, "Nuevo nombre de la clase" },
            { TextMapper.GUIC_MSG_DELETE_CLASS, "¿Seguro que quieres eliminar esta clase?" },
            { TextMapper.GUIC_CLASS_NOT_EMPTY, "Hay algunos estudiantes en esta clase, ¿Seguro que quieres eliminarla?" },
            { TextMapper.GUIC_CLASS_NOT_EMPTY_TITLE, "Hay algunos estudiantes en esta clase" },
         { TextMapper.GUIC_MSG_DELETE_STUDENT, "¿Seguro que quieres borrar a {0} de la clase"},
         { TextMapper.GUIC_DELETE_STUDENT, "Borrar alumno de la clase"},
         { TextMapper.GUIC_NO_STUDENTS, "No hay alumnos en la clase {0}"},

         { TextMapper.GUIRS_RESULTS, "Resultados"},
         { TextMapper.GUIRS_NO_RESULTS, "No hay resultados"},
         { TextMapper.GUIRS_BTN_SELECT_COURSES, "Seleccionar Módulos"},

         { TextMapper.GUIRS_TLTP_SELECT_COURSES, "Selecciona un módulo"},

         { TextMapper.GUIRS_TLTP_ZOOM, "Resultados de {0}"},
         { TextMapper.GUIRS_TLTP_ZOOM_ORDER, "Ordenar por {0}"},

         { TextMapper.GUIRS_TLTP_RESULT_SCORE_BUTTON, "Mostrar los resultados de la actividad {0} de {1}"},
         { TextMapper.GUIRSDLG_MSG, "Borrar todos los resultados de ''{0}'' para {1}?"},

         { TextMapper.UG_RESULTS_OF_STUDENT, "Resultados de la actividad {0} de {1}"},

         { TextMapper.GUISC_TITLE, "Seleccionar módulos"},
         { TextMapper.GUISC_BTN_SELECT_ALL, "Seleccionar todo"},
         { TextMapper.GUISC_BTN_DESELECT_ALL, "Anular la selección"},

         { TextMapper.UG_CLASSES, "Clase"},
         { TextMapper.UG_STUDENTS_OF_CLASS, "Estudiantes de {0}"},

         { TextMapper.UG_USER_TITLE,"Estudiante"},
         { TextMapper.UG_CLASS_TITLE,"Clase"},

         { TextMapper.UG_CLASS_CHILD, "Estudiante {0}"},
         { TextMapper.UG_CLASS_ORDER_ASC, "Nombre de la clase (A-Z)"},
         { TextMapper.UG_CLASS_ORDER_DESC, "Nombre de la clase (Z-A)"},

         { TextMapper.UG_USER_PARENT, "Clase"},
         { TextMapper.UG_USER_ORDER_ASC, "Apellido (A-Z)"},
         { TextMapper.UG_USER_ORDER_DESC, "Apellido (Z-A)"},

         { TextMapper.LG_COURSES, "Módulos"},
         { TextMapper.LG_SCOS_OF_COURSE, "Actividades de {0}"},

         { TextMapper.LG_COURSE_CHILD, "Actividades de {0}"},
         { TextMapper.LG_COURSE_ORDER_ASC, "resultados (0-100)"},
         { TextMapper.LG_COURSE_ORDER_DESC, "resultados (100-0)"},

         { TextMapper.LG_SCO_PARENT, "módulos"},
         { TextMapper.LG_SCO_ORDER_ASC, "resultados (0-100)"},
         { TextMapper.LG_SCO_ORDER_DESC, "resultados (100-0)"},

         { TextMapper.LG_SCO_NAME , "Actividad {0}"},

         { TextMapper.GUIC_ADD_COURSE, "Añadir un nuevo módulo"},
         { TextMapper.GUIC_ADD_MAP, "Añadir una nueva carpeta" },
         { TextMapper.GUIC_COURSE_MANAGEMENT, "Gestión del módulo"},

         { TextMapper.GUIC_TLTP_DELETE_COURSE, "Borrar módulo {0}"},
         { TextMapper.GUIC_TLTP_DELETE_MAP, "Borrar carpeta {0}"},
         { TextMapper.GUIC_TLTP_EDIT_COURSE, "Editar módulo"},
         { TextMapper.GUIC_TLTP_SCO_COURSE, "Gestión de la actividad"},

         { TextMapper.GUICDLG_COURSE_NAME, "Nombre del módulo"},
         { TextMapper.GUICDLG_MAP_NAME, "Nombre de la carpeta" },

         { TextMapper.GUICDLG_COURSE_DESCRIPTION, "Descripción"},

         { TextMapper.GUICDLG_TTL_ADD_COURSE, "Añadir un nuevo módulo"},
         { TextMapper.GUICDLG_TTL_EDIT_COURSE, "Editar módulo"},
         { TextMapper.GUIC_TLTP_EDIT_MAP, "Editar carpeta" },

         { TextMapper.GUIC_NO_COURSES, "No hay módulos para mostrar"},
         { TextMapper.GUIC_COURSE_SHARE, "Compartir módulos" },

         { TextMapper.GUIC_MSG_COURSE_DELETE, "Hay Actividades creadas. Cuando borres el módulo los resultados y las actividades también se borrarán. ¿Seguro que quieres eliminar este módulo?"},
         { TextMapper.GUIC_MSG_COURSE_DELETE_NO_SCO, "¿Seguro que quieres borrar el módulo?"},
         { TextMapper.GUIC_MSG_TTL_COURSE_DELETE, "Borrar el módulo"},

         { TextMapper.GUIS_ADD_SCO, "Añadir una nueva actividad"},
         { TextMapper.GUIS_LBL_SCO_OF_COURSE, "Actividades del módulo {0}"},
         { TextMapper.GUIS_SCO_MANAGEMENT, "Gestión de las actividades"},
         { TextMapper.GUIS_SHOW_SCORE, "Los estudiantes verán los resultados"},


         { TextMapper.GUIS_TLTP_DELETE_SCO, "Borrar Actividad {0}"},
         { TextMapper.GUIS_TLTP_EDIT_SCO, "Editar el nombre de la Actividad"},
         { TextMapper.GUIS_TLTP_PARAMETERS_SCO, "Editar la Actividad"},
         { TextMapper.GUIS_TLTP_COURSE_SCO, "Volver a los módulos"},

         { TextMapper.GUISDLG_SCO_NAME, "Nombre de la Actividad"},
         { TextMapper.GUISDLG_SCO_DESCRIPTION, "Descripción de la Actividad"},

         { TextMapper.GUISDLG_TTL_ADD_SCO, "Añadir una nueva Actividad"},
         { TextMapper.GUISDLG_TTL_EDIT_SCO, "Editar Actividad"},

         { TextMapper.GUIS_MSG_SCO_DELETE, "Cuando borras una Actividad los resultados también se borrarán. ¿Seguro que quieres borrar esta actividad?"},
         { TextMapper.GUIS_MSG_TTL_SCO_DELETE, "Borrar Actividad"},
         { TextMapper.GUIS_NO_SCOS, "No hay Actividades en el módulo {0}"},
         { TextMapper.GUIS_LOAD_LOGO, "Icono de descarga de {0}"},
         
         { TextMapper.GUISDLG_BTN_ADD_SCO, "Añadir"},
         { TextMapper.GUISDLG_BTN_PREVIEW_SCO, "Actividad anterior"},
         { TextMapper.GUISDLG_MSG_SELECT_SCO, "Escoge una Actividad"},
         { TextMapper.GUISDLG_MSG_NO_APPLETS, "No hay Actividades para agregar"},
         { TextMapper.GUISDLG_SHOW, "Mostrar"},
         { TextMapper.GUISDLG_ALL, "Todas"},
         { TextMapper.GUISDLG_MSG_NO_SELECTION, "No has seleccionado ninguna Actividad"},
         { TextMapper.GUISDLG_RB_STANDARD_SCOS, "Actividades preestablecidas"},
         { TextMapper.GUISDLG_RB_OWN_SCOS, "Actividades propias"},

         { TextMapper.GUIPA_BTN_PREVIEW, "Actividad anterior"},
         { TextMapper.GUIPA_BTN_SAVE, "Guardar"},
         { TextMapper.GUIPA_BTN_RESET, "Borrar"},
         { TextMapper.GUIPA_BTN_CANCEL, "Cerrar"},
         
         { TextMapper.GUIPA_SCO_EDIT, "Editar Actividad"},
         
         { TextMapper.GUIPA_NO_PARAMS, "Esta Actividad no puede ser cambiada"},

         { TextMapper.GUIPA_DLG_TTL, "Modo - Edición de la Actividad {0}"},
         
         { TextMapper.GUIPA_MSG_PARAM_SAVE, "Si guardas esta nueva configuración, los resultados previos se perderán. ¿Seguro que quieres guardar esta configuración?"},
         { TextMapper.GUIPA_MSG_TTL_PARAM_SAVE, "Guardar configuración"},
         
         { TextMapper.GUIPA_PARAMS_OF_SCO, "Parámetros ({0})"},
         
         { "Menu", "Menú" },
         { "cut", "Cortar" },
         { "copy" , "Copiar" },
         { "paste", "Pegar"},
         { "delete", "Eliminar" },
         { "edit", "Editar" },
         { "file", "Archivo" },
         { "rename", "Cambiar el nombre" },
         
         { "Import", "Importar" },
         { "Backup module", "Copia de seguridad del módulo" },


         { TextMapper.GUIA_INSERT_SCOS, "Insertar actividades de la copia de seguridad"},
         { TextMapper.GUIH_STOP_EDIT, "Detener la edición" },
         { TextMapper.GUIH_EDIT, "Editar" },

//¿???????????????????? DE AQUÍ EN ADELANTE, NO SE SI TRADUCIR... LO HAGO PERO SIN BORRAR NI ALEMÁN NI INGLËS Y NO ENTRA EN EL CÓDIGO POR NO TENER "" Traducció revisada sin cambiar el formato.
         
         { "Alle modules", "Todos los módulos"},  // "All modules"
         { "Standaard DWO modules", "Módulos estándar de EDM"},  // "Standard DME modules"

         { "Nieuwe Modulemap", "Nueva carpeta de módulos" },  // "New Module folder"

         // classadminpanel
         { "Klassen toewijzen", "Asignar clases" },  //"Assign classes"
         { "Klas", "Clase" },  // "Class"
         { "Docent", "Profesor/a" },  // "Teacher"
         { "Verwijder", "Eliminar" },  //Remove
         // classpanel  
         { "boomstructuur?", "Vista de árbol" },  // 
         // select courses dialog
         { "Leerlinggegevens verwijderen", "Borrar resultados de los estudiantes" },  // Remove studentresults
         { "Wilt u alle resultaten van {0} voor {1} verwijderen?", "¿Deseas borrar todos los resultados de {0} a {1}?" },  // "Do you wish to remove all result of {0} for {1}?
         { "soort", "tipo" },  // kind
         { "vanaf", "desde" },  //from
         { "tot aan", "hasta" },  //until
         { "tot", "hasta" },  // until
         { "Ll ggvns", "resultados" },  //results
         { "normaal", "normal" }, // normal
         { "afgeschermd", "asegurado" }, // secured
         { "Geef tijdstip {0}", "Fijar fecha y hora \"{0}\""}, //Set date and time
         { " dag: " , " fecha: " }, //date
         { "tijd:", "hora:" }, //time
         // resultLoogger 
         { "Overzicht Logs", "Resumen de registros" }, //Overview Logs
         { "deel-scores", "resultados parciales" }, //partial scores
         { "tijdsduur", "duración" }, //duration
         // default partial score
         { "resultaat", "resultados" }, //result
         //importexportdialog
         {"Kopiëer modules", "Copiar módulos" },  //Copy modules
         {"Toestaan", "Permitir" }, //Allow
         {"Modules beschikbaar stellen", "Proporcionar módulos" }, //Provide modules
         {"Modules opvragen", "Solicitar módulos"}, //Request modules
         {"Delen met","Compartir con"},  //Share with
         {"Alle scholen","Todos los centros educativos"}, //All schools
         {"Scholen", "Centros educativos"}, //Schools
         {"toepassen", "Aplicar"}, //Apply
         
         { TextMapper.GUIEID_MSG1, "<html>(1) Selecciona un centro educativo<br>" + 
			   "(2) Vista previa de los módulos<br>" + 
			   "(3) Selecciona uno o más módulos <br><br>" +
			   "Los módulos seleccionados se copiarán en tu vista personal del módulo<br>"+
			   "y pueden ser utilizados tu centro educativo." },
		  { TextMapper.GUIEID_MSG2, "<html>Deseo participar, compartir y hacerme visible como centro educativo en esta lista"},
		  { TextMapper.GUIEID_MSG3, "<html>(1) Selecciona los módulos<br>(2) Selecciona el centro educativo<br><br>Los módulos seleccionados están disponibles<br>para los centros educativos seleccionados." },

		  { "leerlingen ook", "los alumnos"},
		  { TextMapper.GUIUMP_REMOVE_CLASS, "Eliminar todos los estudiantes de la clase" },
		  { TextMapper.GUIUMP_ALL_STUDENTS, "Eliminar todos los estudiantes" },
		  { TextMapper.GUIH_SETTINGS	,"Configuración escolar"},
		  { TextMapper.GUIC_SETTINGS, "Configuración del centro educativo"},
		  { TextMapper.GUIC_SETTINGS_STUDENT, "Los estudiantes escogen su clase"},
		  { TextMapper.GUIC_SETTINGS_TEACHER, "Los profesores pueden escoger la clase para sus alumnos"},
		  { TextMapper.GUIC_SETTINGS_MODULE, "Los profesores pueden modificar los módulos"},

		  {"Name", "Nombre"},
		  {"Username", "Usuario"},
		  {"Login as", "Registrado como"},
		  {"Password", "Contraseña"},
		  {"Remove", "Eliminar"},
		  {"In class", "En clase"},
		  { "Module", "Módulo" },
		  
};

 public Text_es() {

 }

 /**
  * @return Object[][]
  */
 public Object[][] getContents() {
     return contents;
 }
}
