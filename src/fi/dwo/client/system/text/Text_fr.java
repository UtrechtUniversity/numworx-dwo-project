//Source file:
//N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\Text_en.java


package fi.dwo.client.system.text;


import java.util.ListResourceBundle;


import fi.dwo.client.system.TextMapper;


public class Text_fr extends ListResourceBundle {
 private final Object[][] contents = {
         { TextMapper.USER_GUEST, "Invité"},
         { TextMapper.BTN_LOGIN, "Connexion" },
         { TextMapper.BTN_CANCEL, "Annuler" },
         { TextMapper.BTN_NO, "Non" },
         { TextMapper.BTN_OK, "OK" },
         { TextMapper.BTN_YES, "Oui" },
         { TextMapper.BTN_CLOSE, "Fermer"},
         { TextMapper.DLG_CONFIRM, "Confirmer" },
         { TextMapper.DLG_ENTER_INPUT, "Entrer donnée" },
         { TextMapper.DLG_MESSAGE, "Message" },


         { TextMapper.EX_UNKNOWN_ERROR, "Une erreur interne s’est produite" },
         { TextMapper.EXR_USER_EXISTS, "Le nom d’utilisateur existe déjà" },
         { TextMapper.EXR_USER_EXISTS2, "Le nom d’utilisateur {0} existe déjà" },
         { TextMapper.EXR_WRONG_SECOND_PASSWORD, "Les mots de passes sont différents" },
         { TextMapper.EXR_WRONG_USERNAME_PASSWORD, "Un utilisateur avec ce nom et ce mot de passe n’a pas été trouvé" },
         { TextMapper.EXR_UNKNOWN_SCHOOLGROUP, "Ecole inconnue/groupe/mot de passe" },
         { TextMapper.EXR_MANDATORY, "{0} à {1} n’est pas complété. C’est un champ obligatoire"},
         { TextMapper.EXR_WRONG_FORMAT, "{0} à {1} contient des caractères non autorisés" },
         { TextMapper.EXR_WRONG_EMAILFORMAT, "{0} à {1} n’est pas autorisé" },
         { TextMapper.EXC_CLASS_EXISTS, "Les classes existent déjà" },
         { TextMapper.EXS_SCHOOL_EXISTS, "Une école avec le même identifiant existe déjà" },
         { TextMapper.EXL_UNKNOWN_USER, "Un utilisateur avec le même nom et le même mot de passe n’a pas été trouvé" },
         { TextMapper.EXC_COURSE_EXISTS, "Le module existe déjà" },
         { TextMapper.EXS_SCO_EXISTS, "L’activité existe déjà" },
         { TextMapper.EXS_NO_APPLET, "Applet non trouvé"},


         { TextMapper.GUI_WAIT_A_MOMENT, "Un moment s’il vous plaît"},


         { TextMapper.GUIW_LOGINDATA, "Identifiant" },
         { TextMapper.GUIW_USERNAME, "Nom d'utilisateur" },
         { TextMapper.GUIW_PASSWORD, "Mot de passe" },
         { TextMapper.GUIW_WELCOME, "Bienvenue" },
         { TextMapper.GUIW_GUESTLOGIN, "Connecté comme invité" },
         { TextMapper.GUIW_REGISTER, "S’inscire" },
         { TextMapper.GUIW_MSG_WORK_NOT_SAVE, "Votre travail ne sera pas sauvegardé" },
         { TextMapper.GUIW_MSG_REGISTER_NEW, "S’inscrire comme nouveau membre." },
         { TextMapper.GUIW_BTN_GUESTLOGIN, "Connecté comme invité" },
         { TextMapper.GUIW_BTN_LOGIN, "Connexion" },
         { TextMapper.GUIW_BTN_REGISTER, "S’inscrire" },
         { TextMapper.GUIW_ERR_LOGIN, "Erreur connexion" },


         { TextMapper.GUIR_REGISTER, "S’inscrire" },


         { TextMapper.GUIR_REGISTERINFO, "Inscription d’un nouvel utilisateur" },
         { TextMapper.GUIR_PERSONALINFO, "Informations personnelles" },
         { TextMapper.GUIR_SCHOOLINFO, "Information sur l’école" },


         { TextMapper.GUIR_USERNAME, "Nom d'utilisateur" },
                 { TextMapper.GUIR_PASSWORD, "Mot de passe" },
                 { TextMapper.GUIR_RE_PASSWORD, "Confirmer mot de passe" },


                 { TextMapper.GUIR_FIRSTNAME, "Prénom" },
                 { TextMapper.GUIR_MIDDLENAME, "Second prénom" },
                 { TextMapper.GUIR_LASTNAME, "Nom" },
                 { TextMapper.GUIR_EMAIL, "Adresse mail" },


                 { TextMapper.GUIR_SCHOOLLOGIN, "Identifiant école" },
                 { TextMapper.GUIR_SCHOOLGROUP, "Je suis" },
                 { TextMapper.GUIR_SCHOOLPASSWORD, "Mot de passe" },


                 { TextMapper.GUIR_BTN_REGISTER, "S’inscrire" },
                 { TextMapper.GUIR_BTN_RESET, "Effacer" },
         { TextMapper.GUIR_BTN_BACK, "Retour aux modules" },


            { TextMapper.GUIR_MSG_PROVIDED_SCHOOL, "Données fournies par l’école" },


            { TextMapper.GUIR_OPT_SELECT_GROUP, "Faire un choix" },
            { TextMapper.GUIR_OPT_STUDENT, "Etudiant" },
            { TextMapper.GUIR_OPT_TEACHER, "Enseignant" },
            { TextMapper.GUIR_OPT_ADMIN, "Administrateur"},
            { TextMapper.GUIR_OPT_SCHOOLADMIN, "Ecole admin" },
            { TextMapper.GUIR_OPT_SCHOOLCODE, "Code"},
            
            { TextMapper.GUIR_ERR_REGISTER, "Une erreur s’est produite" },


            { TextMapper.GUIR_MSG_REGISTERED, "Inscription réussie"},


            { TextMapper.GUIM_DWO_FULL, "Environnement Mathématique Numérique" },
            { TextMapper.GUIM_DWO_SHORT, "EMN" },
            { TextMapper.GUIM_FI_NAME, "Institut Freudenthal"},
            { TextMapper.GUIM_MAIN_MENU, "Modules" },


            { TextMapper.GUIL_LOGGED_IN_AS, "Vous êtes connecté en tant que" },
            { TextMapper.GUIL_NOT_LOGGED_IN, "Vous n’êtes pas connecté"},
            { TextMapper.GUIL_BTN_LOGIN, "Connexion"},
            { TextMapper.GUIL_BTN_LOGOFF, "Déconnexion" },


            { TextMapper.GUIMNU_MAIN_MENU, "Modules" },
            { TextMapper.GUIMNU_MY_PROFILE, "Mon profile" },
            { TextMapper.GUIMNU_STUDENT_IN_CLASS, "Étudiant de la classe" },
            { TextMapper.GUIMNU_STUDENT_NO_CLASS_0, "Vous n’êtes pas encore "},
                 { TextMapper.GUIMNU_STUDENT_NO_CLASS_1, "membre de la classe.  "},
                 { TextMapper.GUIMNU_STUDENT_NO_CLASS_2, "Aller à"},
                 { TextMapper.GUIMNU_STUDENT_NO_CLASS_3, "\"Mon profile\" et "},
                 { TextMapper.GUIMNU_STUDENT_NO_CLASS_4, "choisir une classe."},
            { TextMapper.GUIMNU_CLASS_RESULTS, "Résultats de la classe" },
            { TextMapper.GUIMNU_RESULTS, "Voir les résultats" },
            { TextMapper.GUIMNU_CLASS_MANAGEMENT, "Responsable classe" },
            { TextMapper.GUIMNU_SCHOOL_MANAGEMENT, "Direction école" },
            { TextMapper.GUIMNU_COURSE_MANAGEMENT, "Responsable module"},
            { TextMapper.GUIMNU_MSG_ADD_CLASS, "Nom de la nouvelle classe" },
            { TextMapper.GUIMNU_MSG_ADD_CLASS_TITLE, "Ajouter une nouvelle classe" },
            { TextMapper.GUIMNU_MSG_ADD_SCHOOL, "Nom de la nouvelle école"},   
                 { TextMapper.GUIMNU_MSG_ADD_SCHOOL_TITLE, "Ajoute une nouvelle école"},
                 { TextMapper.GUIMNU_USERS_SCHOOL, "Utilisateurs école"},
            { TextMapper.GUIMNU_CLASSES_SCHOOL, "Les classes de l’école"},
            { TextMapper.GUIMNU_FEATURES_SCHOOLADMIN, "Caractéristiques école admin"},
            
            { TextMapper.GUIUMP_MANAGE_USERS, "Gestion utilisateurs"},
            { TextMapper.GUIUMP_REMOVE_FROM_SCHOOL, "Supprimer seulement de l’école"},
            { TextMapper.GUIUMP_REMOVE_COMPLETE, "Suppression complète du compte"},
            { TextMapper.GUIUMP_ADD_STUDENTS, "Ajouter un nouvel étudiant"},
            { TextMapper.GUIUMP_ADD_TEACHERS, "Ajouter de nouveau enseignants"},
            { TextMapper.GUIUMP_IMPORT_CLIPBOARD, "Importer d’un presse-papiers"},
            { TextMapper.GUIUMP_MAKE_ACCOUNTS, "Donner un avis"},
            { TextMapper.GUIUMP_EXTRA_ROW, "Ligne supplémentaire"},


                 { TextMapper.GUICO_HEADER, "Modules"},
                 { TextMapper.GUICO_SCO_LIST_TITLE, "Activités"},


            { TextMapper.GUIP_MY_PROFILE, "Mon profile" },
            { TextMapper.GUIP_REGISTERINFO, "Information inscription" },
            { TextMapper.GUIP_PERSONALINFO, "Information personnel " },
            { TextMapper.GUIP_SCHOOLINFO, "Information sur l’école" },


            { TextMapper.GUIP_USERNAME, "Nom d’utilisateur" },
            { TextMapper.GUIP_OLD_PASSWORD, "Ancien mot de passe" },
            { TextMapper.GUIP_PASSWORD, "Nouveau mot de passe" },
            { TextMapper.GUIP_RE_PASSWORD, "Confirmer mot de passe" },


            { TextMapper.GUIP_FIRSTNAME, "Prénom" },
            { TextMapper.GUIP_MIDDLENAME, "Second prénom" },
            { TextMapper.GUIP_LASTNAME, "Nom" },
            { TextMapper.GUIP_EMAIL, "Adresse e-mail" },


            { TextMapper.GUIP_SCHOOLLOGIN, "Identifiant école" },
            { TextMapper.GUIP_SCHOOLGROUP, "Je suis" },
            { TextMapper.GUIP_SCHOOLPASSWORD, "Mot de passe" },
            { TextMapper.GUIP_CLASS, "Classe" },


            { TextMapper.GUIP_BTN_SAVE, "Enregistrer" },
            { TextMapper.GUIP_BTN_RESET, "Effacer" },
            { TextMapper.GUIP_BTN_DELETE_PROFILE, "Effacer profile" },


            { TextMapper.GUIP_MSG_PROVIDED_SCHOOL, "Information fourni par l’écolel" },


            { TextMapper.GUIP_ERR_CHANGE, "Une erreur s’est produite" },


            { TextMapper.GUIP_OPT_SELECT_GROUP, "Faire un choix" },


            { TextMapper.GUIP_CONFIRM_REMOVE_USER, "Êtes-vous certain de vouloir supprimer votre compteAre" },
            { TextMapper.GUIP_CONFIRM_REMOVE_USER_TITLE, "Compte supprimé" },


            { TextMapper.GUIP_MSG_PROFILE_CHANGED, "Votre compte est modifié avec succès"},


            { TextMapper.GUIPT_SCHOOL, "Ecole" },
            { TextMapper.GUIPT_TEACHER_FROM_CLASS, "Enseignant de la classe" },
            { TextMapper.GUIPT_BTN_ADD_CLASS, "Ajouter une classe" },
            
         { TextMapper.GUIS_STUDENTS, "Étudiants"},
         { TextMapper.GUIS_TEACHERS, "Enseignants"},
         { TextMapper.GUIS_SCHOOL_MANAGEMENT, "Gestion école"},


         { TextMapper.GUIS_TLTP_DELETE_SCHOOL, "Supprimer l’école {0} "},
         { TextMapper.GUIS_TLTP_EDIT_SCHOOL, "Modifier le nom de l”école"},
         { TextMapper.GUIS_TLTP_USERS_SCHOOL, "Étudiants de {0}"},
         
         { TextMapper.GUIS_ADD_SCHOOL, "Ajouter une école"},
         { TextMapper.GUIS_DELETE_SCHOOL, "Supprimer l’école"},
         { TextMapper.GUIS_RENAME_SCHOOL, "Modifier le nom de l’école"}, 
         { TextMapper.GUIS_MSG_RENAME_SCHOOL, "Entrer un nouveau nom d’école"},
         { TextMapper.GUIS_MSG_DELETE_SCHOOL, "Êtes-vous certain de vouloir supprimer cette école"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY, "Cette école a des utilisateurs. Êtes-vous certain de vouloir la supprimer"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY_TITLE, "Cette école a des utilisateurs"},
         { TextMapper.GUIS_MSG_DELETE_STUDENT, "Êtes-vous certain de vouloir supprimer {0} de cette école"},
         { TextMapper.GUIS_DELETE_STUDENT, "Supprimer les étudiants de cette école"},
         { TextMapper.GUIS_NO_STUDENTS, "L’école {0} n’a pas d’étudiants"}, 


         { TextMapper.GUIC_STUDENTS, "Étudiants"},
         { TextMapper.GUIC_CLASS_MANAGEMENT, "Gestion classe"},


         { TextMapper.GUIC_TLTP_DELETE_CLASS, "Supprimer la classe {0} "},
         { TextMapper.GUIC_TLTP_EDIT_CLASS, "Modifier le nom de la classe"},
         { TextMapper.GUIC_TLTP_USERS_CLASS, "Étudiant de la classe {0}"},


         ////peter
         { TextMapper.GUIC_TLTP_ASSIGN_CLASS, "Attribuer le module à la classe {0}"},
                 ////peter


            { TextMapper.GUIC_STUDENTS, "Étudiants" },
            { TextMapper.GUIC_ADD_CLASS, "Créer la classe"},
            { TextMapper.GUIC_DELETE_CLASS, "Supprimer la classe" },
            { TextMapper.GUIC_RENAME_CLASS, "Modifier le nom de la classe" },
            { TextMapper.GUIC_MSG_RENAME_CLASS, "Nouveau nom de classe" },
            { TextMapper.GUIC_MSG_DELETE_CLASS, "Êtes-vous certain de vouloir supprimer la classe" },
            { TextMapper.GUIC_CLASS_NOT_EMPTY, "Il y a des étudiants dans la classe. êtes-vous certain de vouloir supprimer la classe" },
            { TextMapper.GUIC_CLASS_NOT_EMPTY_TITLE, "Il y a des étudiants dans la classe" },
         { TextMapper.GUIC_MSG_DELETE_STUDENT, "Êtes-vous certain de vouloir supprimer {0} de la classe"},
         { TextMapper.GUIC_DELETE_STUDENT, "Supprimer l’étudiant de la classe"},
         { TextMapper.GUIC_NO_STUDENTS, "Il n’y a pas d’étudiant dans la classe {0}"},


         { TextMapper.GUIRS_RESULTS, "Résultats"},
         { TextMapper.GUIRS_NO_RESULTS, "Il n’y a pas de résultats"},
         { TextMapper.GUIRS_BTN_SELECT_COURSES, "Sélectionner les Modules"},


         { TextMapper.GUIRS_TLTP_SELECT_COURSES, "Sélectionner un  module"},


         { TextMapper.GUIRS_TLTP_ZOOM, "Résultats de {0}"},
         { TextMapper.GUIRS_TLTP_ZOOM_ORDER, "Trier {0}"},


         { TextMapper.GUIRS_TLTP_RESULT_SCORE_BUTTON, "Montrer les résultats de l’ Activité {0} de {1}"},
         { TextMapper.GUIRSDLG_MSG, "Supprimer tous les résultats de ''{0}'' pour {1}?"},


         { TextMapper.UG_RESULTS_OF_STUDENT, "Résultats de l’ Activité {0} de {1}"},


         { TextMapper.GUISC_TITLE, "Sélectionner les modules"},
         { TextMapper.GUISC_BTN_SELECT_ALL, "Tout sélectionner"},
         { TextMapper.GUISC_BTN_DESELECT_ALL, "Tout désélectionner"},


         { TextMapper.UG_CLASSES, "Classes"},
         { TextMapper.UG_STUDENTS_OF_CLASS, "Étudiants de {0}"},


         { TextMapper.UG_USER_TITLE,"Étudiant"},
         { TextMapper.UG_CLASS_TITLE,"Classe"},


         { TextMapper.UG_CLASS_CHILD, "Étudiants {0}"},
         { TextMapper.UG_CLASS_ORDER_ASC, "Nom de la classe (A-Z)"},
         { TextMapper.UG_CLASS_ORDER_DESC, "Nom de la classe (Z-A)"},


         { TextMapper.UG_USER_PARENT, "Classes"},
         { TextMapper.UG_USER_ORDER_ASC, "Nom (A-Z)"},
         { TextMapper.UG_USER_ORDER_DESC, "Nom (Z-A)"},


         { TextMapper.LG_COURSES, "Modules"},
         { TextMapper.LG_SCOS_OF_COURSE, "Activités de {0}"},


         { TextMapper.LG_COURSE_CHILD, "Activités de {0}"},
         { TextMapper.LG_COURSE_ORDER_ASC, "résultats (0-100)"},
         { TextMapper.LG_COURSE_ORDER_DESC, "résultats (100-0)"},


         { TextMapper.LG_SCO_PARENT, "modules"},
         { TextMapper.LG_SCO_ORDER_ASC, "résultats (0-100)"},
         { TextMapper.LG_SCO_ORDER_DESC, "résultats (100-0)"},


         { TextMapper.LG_SCO_NAME , "Activité {0}"},


         { TextMapper.GUIC_ADD_COURSE, "Ajouter un nouveau module"},
         { TextMapper.GUIC_ADD_MAP, "Ajouter un nouveau dossier" },
         { TextMapper.GUIC_COURSE_MANAGEMENT, "Gestion des modules"},


         { TextMapper.GUIC_TLTP_DELETE_COURSE, "Supprimer module {0}"},
         { TextMapper.GUIC_TLTP_DELETE_MAP, "Supprimer dossier {0}"},
         { TextMapper.GUIC_TLTP_EDIT_COURSE, "Modifier module"},
         { TextMapper.GUIC_TLTP_SCO_COURSE, "Gestion  des activités"},


         { TextMapper.GUICDLG_COURSE_NAME, "Nom du module"},
         { TextMapper.GUICDLG_MAP_NAME, "Nom du dossier" },


         { TextMapper.GUICDLG_COURSE_DESCRIPTION, "Description"},


         { TextMapper.GUICDLG_TTL_ADD_COURSE, "Ajouter un nouveau module"},
         { TextMapper.GUICDLG_TTL_EDIT_COURSE, "Modifier module"},
         { TextMapper.GUIC_TLTP_EDIT_MAP, "Modifier dossier" },


         { TextMapper.GUIC_NO_COURSES, "Il n’y a pas de modules à montrer"},
         { TextMapper.GUIC_COURSE_SHARE, "Partager modules" },


         { TextMapper.GUIC_MSG_COURSE_DELETE, "Il y a des Activités actuellement. \nQuand vous voulez supprimer le module \nen les résultats des Activités seront également supprimés.\n \nÊtes-vous certain de vouloir supprimer le module?"},
         { TextMapper.GUIC_MSG_COURSE_DELETE_NO_SCO, "Êtes-vous certain de vouloir supprimer le module?"},
         { TextMapper.GUIC_MSG_TTL_COURSE_DELETE, "Supprimer module"},


         { TextMapper.GUIS_ADD_SCO, "Ajouter une nouvelle Activité"},
         { TextMapper.GUIS_LBL_SCO_OF_COURSE, "Activités du module {0}"},
         { TextMapper.GUIS_SCO_MANAGEMENT, "Gestion Activité"},
         { TextMapper.GUIS_SHOW_SCORE, "Les étudiants voient leurs résultats"},




         { TextMapper.GUIS_TLTP_DELETE_SCO, "Supprimer Activité {0}"},
         { TextMapper.GUIS_TLTP_EDIT_SCO, "Modifier nom Activité"},
         { TextMapper.GUIS_TLTP_PARAMETERS_SCO, "Modifier Activité"},
         { TextMapper.GUIS_TLTP_COURSE_SCO, "Retour aux modules"},


         { TextMapper.GUISDLG_SCO_NAME, "Nom de l”activité"},
         { TextMapper.GUISDLG_SCO_DESCRIPTION, "Description activité"},


         { TextMapper.GUISDLG_TTL_ADD_SCO, "Ajouter une nouvelle Activité"},
         { TextMapper.GUISDLG_TTL_EDIT_SCO, "Modifier Activité"},


         { TextMapper.GUIS_MSG_SCO_DELETE, "Quand vous supprimer l’Activité \nen Les résultats seront aussi supprimés \n \nÊtes-vous certain de supprimer l’Activité?"},
         { TextMapper.GUIS_MSG_TTL_SCO_DELETE, "Supprimer Activité"},
         { TextMapper.GUIS_NO_SCOS, "Il n’a pas d’Activités dans le module {0}"},
         { TextMapper.GUIS_LOAD_LOGO, "Charger l’icône de {0}"},
         
         { TextMapper.GUISDLG_BTN_ADD_SCO, "Ajouter"},
         { TextMapper.GUISDLG_BTN_PREVIEW_SCO, "Aperçu Activité"},
         { TextMapper.GUISDLG_MSG_SELECT_SCO, "Choisir Activité"},
         { TextMapper.GUISDLG_MSG_NO_APPLETS, "Il n’y a pas d’activités à ajouter"},
         { TextMapper.GUISDLG_SHOW, "Monter"},
         { TextMapper.GUISDLG_ALL, "Tout"},
         { TextMapper.GUISDLG_MSG_NO_SELECTION, "Vous n’avez pas sélectionné d’Activités"},
         { TextMapper.GUISDLG_RB_STANDARD_SCOS, "Activités standards "},
         { TextMapper.GUISDLG_RB_OWN_SCOS, "Activités propres"},


         { TextMapper.GUIPA_BTN_PREVIEW, "Aperçu Activité"},
         { TextMapper.GUIPA_BTN_SAVE, "Enregistrer"},
         { TextMapper.GUIPA_BTN_RESET, "Réinitialiser"},
         { TextMapper.GUIPA_BTN_CANCEL, "Fermer"},
         
         { TextMapper.GUIPA_SCO_EDIT, "Modifier Activité"},
         
         { TextMapper.GUIPA_NO_PARAMS, "Cette activité ne peut être modifiée"},


         { TextMapper.GUIPA_DLG_TTL, "Mode-Modifier de l’Activité {0}"},
         
         { TextMapper.GUIPA_MSG_PARAM_SAVE, "Si vous enregistez cette nouvelle  configuration,\nle resultat des anciens champs seront supprimés\n \nÊtes-vous certain de vouloir enregistrer cette configuration?"},
         { TextMapper.GUIPA_MSG_TTL_PARAM_SAVE, "Enregistrer configuration"},
         
         { TextMapper.GUIPA_PARAMS_OF_SCO, "Paramètres ({0})"},
         
         { "cut", "Couper" },
         { "copy" , "Copier" },
         { "paste", "Poller"},
         { "delete", "Supprimer" },
         { "edit", "Modifier" },
         { "file", "Fichier" },
         { "rename", "Renommer" },


         { TextMapper.GUIA_INSERT_SCOS, "Insérer des activités sauvegardées"},
         { TextMapper.GUIH_STOP_EDIT, "Stop modification" },
         { TextMapper.GUIH_EDIT, "Modifier" },
         
         { "Alle modules", "Tous les modules"},
         { "Standaard DWO modules", " EMN modules standards"},


         { "Nieuwe Modulemap", "Nouveau dossier Module" },


         // classadminpanel
         { "Klassen toewijzen", "Attribuer classes" },
         { "Klas", "Classe" },
         { "Docent", "Enseignant" },
         { "Verwijder", "Supprimer" },
         // classpanel 
         { "boomstructuur?", "Arborescence?" },
         // select courses dialog
         { "Leerlinggegevens verwijderen", "Supprimer résultat étudiant" },
         { "Wilt u alle resultaten van {0} voor {1} verwijderen?", "Souhaitez-vous supprimer tous les  résultats de {0} pour {1}?" },
         { "soort", "sorte" },
         { "vanaf", "de" },
         { "tot aan", "jusqu’à" },
         { "tot", "jusqu’à" },
         { "Ll ggvns", "résultats" },
         { "normaal", "normal" },
         { "afgeschermd", "sécuriser" },
         { "Geef tijdstip {0}", "Donner la date et l’heure \"{0}\""},
         { " dag: " , " date: " },
         { "tijd:", "heure:" },
         // resultLoogger 
         { "Overzicht Logs", "Vue d”ensemble Logs" },
         { "deel-scores", " Scores partiels" },
         { "tijdsduur", "durée" },
         // default partial score
         { "resultaat", "resultat" },
         //importexportdialog
         {"Kopiëer modules", "Copier modules" }, 
         {"Toestaan", "Permettre" },
         {"Modules beschikbaar stellen", "Fournir modules" },
         {"Modules opvragen", "Modules requis"},
         {"Delen met","Partager avec"},
         {"Alle scholen","Toutes les écoles"},
         {"Scholen", "Écoles"},
         {"toepassen", "Appliquer"},
         
         { TextMapper.GUIEID_MSG1, "<html>(1) Sélectionner une école<br>" +
                           "(2) Aperçu final des modules montrés<br>" +
                           "(3) Sélectionner un ou plusieurs modules pour utiliser dans votre propre école<br><br>" +
                           "Les modules sélectionnés sont copiés  sur votre propre aperçu module <br>"+
                           "et peuvent être utilisés par votre propre école." },
                  { TextMapper.GUIEID_MSG2, "<html>Je souhaite participer à cette façon de partager et apparaître comme école dans les  listes"},
                  { TextMapper.GUIEID_MSG3, "<html>(1) Sélectionner modules<br>(2) Sélectionner écoles<br><br>Les modules sélectionnés sont disponibles<br>pour l’école selectionné." },


};


 public Text_fr() {


 }


 /**
  * @return Object[][]
  */
 public Object[][] getContents() {
     return contents;
 }
}