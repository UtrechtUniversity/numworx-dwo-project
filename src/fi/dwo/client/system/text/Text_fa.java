//Source file:
//N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\Text_en.java

package fi.dwo.client.system.text;

import java.util.ListResourceBundle;

import fi.dwo.client.system.TextMapper;

public class Text_fa extends ListResourceBundle {
 private final Object[][] contents = {
         { TextMapper.USER_GUEST, "مهمان"},
         { TextMapper.BTN_LOGIN, " ورود به سیستم" },
         { TextMapper.BTN_CANCEL, "عدم تایید" },
         { TextMapper.BTN_NO, "نه" },
         { TextMapper.BTN_OK, "تایید" },
         { TextMapper.BTN_YES, "بله" },
         { TextMapper.BTN_CLOSE, "بستن"},
         { TextMapper.DLG_CONFIRM, " تایید قطعی" },
         { TextMapper.DLG_ENTER_INPUT, "اطلاعات را وارد کنید" },
         { TextMapper.DLG_MESSAGE, "پیغام" },

         { TextMapper.EX_UNKNOWN_ERROR, "خطای داخلی رخ داده است" },
         { TextMapper.EXR_USER_EXISTS, "نام کاربری قبلا وجود دارد" },
         { TextMapper.EXR_USER_EXISTS2, "نام کاربری {0} قبلا وجود دارد" },
         { TextMapper.EXR_WRONG_SECOND_PASSWORD, "رمزهای عبور یکی نیستند" },
         { TextMapper.EXR_WRONG_USERNAME_PASSWORD, "کاربری با نام و رمز وارد شده وجود ندارد" },
         { TextMapper.EXR_UNKNOWN_SCHOOLGROUP, "ترکیب مدرسه/گروه/رمز وجود ندارد" },
         { TextMapper.EXR_MANDATORY, "{0} at {1} isn't filled. This is a required field"},
         { TextMapper.EXR_WRONG_FORMAT, "{0} در {1} حاوی کاراکترهای نامناسب است" },
         { TextMapper.EXR_WRONG_EMAILFORMAT, "{0} در {1} نامناسب است" },
         { TextMapper.EXC_CLASS_EXISTS, "کلاس تعریف شذه قبلا وجود دارد" },
         { TextMapper.EXS_SCHOOL_EXISTS, "نام ورودی مدرسه قبلا وجود دارد" },
         { TextMapper.EXL_UNKNOWN_USER, "کاربری با نام و رمز وارد شده پیدا نشد" },
         { TextMapper.EXC_COURSE_EXISTS, "ماژول وارد شده از قبل وجود دارد" },
         { TextMapper.EXS_SCO_EXISTS, "فعالیت وارد شده از قبل وجود دارد" },
         { TextMapper.EXS_NO_APPLET, "اپلت پیدا نشد"},

         { TextMapper.GUI_WAIT_A_MOMENT, "لطفا کمی صبر کنید"},

         { TextMapper.GUIW_LOGINDATA, "داده های ورود به سیستم" },
         { TextMapper.GUIW_USERNAME, "نام کاربری" },
         { TextMapper.GUIW_PASSWORD, "رمز عبور" },
         { TextMapper.GUIW_WELCOME, "خوش آمدید" },
         { TextMapper.GUIW_GUESTLOGIN, "ورود مهمان به سیستم" },
         { TextMapper.GUIW_REGISTER, "ثبت نام" },
         { TextMapper.GUIW_MSG_WORK_NOT_SAVE, "کارهای شما ذخیره نخواهد شد" },
         { TextMapper.GUIW_MSG_REGISTER_NEW, "ثبت نام عضو جدید" },
         { TextMapper.GUIW_BTN_GUESTLOGIN, "ورود مهمان به سیستم" },
         { TextMapper.GUIW_BTN_LOGIN, "ورود به سیستم" },
         { TextMapper.GUIW_BTN_REGISTER, "ثبت نام" },
         { TextMapper.GUIW_ERR_LOGIN, "خطا در ورود به سیستم" },

         { TextMapper.GUIR_REGISTER, "ثبت نام" },

         { TextMapper.GUIR_REGISTERINFO, "بت نام کاریر جدید" },
         { TextMapper.GUIR_PERSONALINFO, "اطلاعات شخصی" },
         { TextMapper.GUIR_SCHOOLINFO, "اطلاعات مدرسه" },

         { TextMapper.GUIR_USERNAME, "نام کابری" },
		 { TextMapper.GUIR_PASSWORD, "رمز عبور" },
		 { TextMapper.GUIR_RE_PASSWORD, "تایید رمز عبور" },

		 { TextMapper.GUIR_FIRSTNAME, "نام" },
		 { TextMapper.GUIR_MIDDLENAME, "" },
		 { TextMapper.GUIR_LASTNAME, "نام خانوادگی" },
		 { TextMapper.GUIR_EMAIL, "آدرس ایمیل" },

		 { TextMapper.GUIR_SCHOOLLOGIN, "ورود مدرسه" },
		 { TextMapper.GUIR_SCHOOLGROUP, "من هستم" },
		 { TextMapper.GUIR_SCHOOLPASSWORD, "رمز عبور" },

		 { TextMapper.GUIR_BTN_REGISTER, "ثبت نام" },
		 { TextMapper.GUIR_BTN_RESET, "ریست" },
         { TextMapper.GUIR_BTN_BACK, "برگست به ماژول" },

            { TextMapper.GUIR_MSG_PROVIDED_SCHOOL, "داده های توسط مدرسه آماده شد" },

            { TextMapper.GUIR_OPT_SELECT_GROUP, "یک گزینه انتخاب کنبد" },
            { TextMapper.GUIR_OPT_STUDENT, "دانش آموز" },
            { TextMapper.GUIR_OPT_TEACHER, "معلم" },
            { TextMapper.GUIR_OPT_ADMIN, "کاربر ارشد"},
            { TextMapper.GUIR_OPT_SCHOOLADMIN, "کاربر ارشد مدرسه" },
            { TextMapper.GUIR_OPT_SCHOOLCODE, "کد اصلی"},
            
            { TextMapper.GUIR_ERR_REGISTER, "یک خطا رخ داده است" },

            { TextMapper.GUIR_MSG_REGISTERED, "ثبت نام با موفقیت انجام شد"},

            { TextMapper.GUIM_DWO_FULL, "Digital Mathematics Environment" },
            { TextMapper.GUIM_DWO_SHORT, "DME" },
            { TextMapper.GUIM_FI_NAME, "Freudenthal Institute"},
            { TextMapper.GUIM_MAIN_MENU, "ماژولها" },

            { TextMapper.GUIL_LOGGED_IN_AS, "شما وارد سیستم شدید یه عنوان:" },
            { TextMapper.GUIL_NOT_LOGGED_IN, "واردی سیستم نشدید"},
            { TextMapper.GUIL_BTN_LOGIN, "ورود به سیستم"},
            { TextMapper.GUIL_BTN_LOGOFF, "خروج از سیستم" },

            { TextMapper.GUIMNU_MAIN_MENU, "ماژولها" },
            { TextMapper.GUIMNU_MY_PROFILE, "پروفایل من" },
            { TextMapper.GUIMNU_STUDENT_IN_CLASS, "دانش آموزان کلاس" },
            { TextMapper.GUIMNU_STUDENT_NO_CLASS_0, "شما هنوز نیستید "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_1, "عضو کلاس  "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_2, "برو به"},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_3, "\"پروفایل من\" and "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_4, "یک کلاس انتخاب کنید"},
            { TextMapper.GUIMNU_CLASS_RESULTS, "نتیجه کلاس" },
            { TextMapper.GUIMNU_RESULTS, "به نتایج نگاه کنید" },
            { TextMapper.GUIMNU_CLASS_MANAGEMENT, "مدیریت کلاس" },
            { TextMapper.GUIMNU_SCHOOL_MANAGEMENT, "مدیدیت کلاس" },
            { TextMapper.GUIMNU_COURSE_MANAGEMENT, "مدیدیت ماژول"},
            { TextMapper.GUIMNU_MSG_ADD_CLASS, "نام کلاس جدید" },
            { TextMapper.GUIMNU_MSG_ADD_CLASS_TITLE, "اضافه کردن کلاس جدید" },
            { TextMapper.GUIMNU_MSG_ADD_SCHOOL, "نام کلاس جدید"},   
         	{ TextMapper.GUIMNU_MSG_ADD_SCHOOL_TITLE, "اضافه کردن مدرسه جدید"},
         	{ TextMapper.GUIMNU_USERS_SCHOOL, "کاربران کلاس"},
            { TextMapper.GUIMNU_CLASSES_SCHOOL, "کلاسهای مدرسه"},
            { TextMapper.GUIMNU_FEATURES_SCHOOLADMIN, "امکانات کاربر ارشد مدرسه"},
            
            { TextMapper.GUIUMP_MANAGE_USERS, "مدیریت کاربران"},
            { TextMapper.GUIUMP_REMOVE_FROM_SCHOOL, "فقط از مدرسه حذف شود"},
            { TextMapper.GUIUMP_REMOVE_COMPLETE, " حذف کامل کاربر"},
            { TextMapper.GUIUMP_ADD_STUDENTS, "اضافه کردن دانش آموز جدید"},
            { TextMapper.GUIUMP_ADD_TEACHERS, "اضافه کردن معلم جدید"},
            { TextMapper.GUIUMP_IMPORT_CLIPBOARD, "کپی کردن از کلیپبورد"},
            { TextMapper.GUIUMP_MAKE_ACCOUNTS, "ایجاد کاربری جدید"},
            { TextMapper.GUIUMP_EXTRA_ROW, "سطر اضافه"},

         	{ TextMapper.GUICO_HEADER, "ماژولها"},
         	{ TextMapper.GUICO_SCO_LIST_TITLE, "فعالیتها"},

            { TextMapper.GUIP_MY_PROFILE, "پروفایل من" },
            { TextMapper.GUIP_REGISTERINFO, "اطلاعات ثبت نام" },
            { TextMapper.GUIP_PERSONALINFO, "اطلاعات شخصی" },
            { TextMapper.GUIP_SCHOOLINFO, "اطلاعات مدرسه" },

            { TextMapper.GUIP_USERNAME, "نام کاربری" },
            { TextMapper.GUIP_OLD_PASSWORD, "کلمه عبور جاری" },
            { TextMapper.GUIP_PASSWORD, "رمز عبور جدید" },
            { TextMapper.GUIP_RE_PASSWORD, "تایید رمز عبور" },

            { TextMapper.GUIP_FIRSTNAME, "نام" },
            { TextMapper.GUIP_MIDDLENAME, "" },
            { TextMapper.GUIP_LASTNAME, "نام خانوادگی" },
            { TextMapper.GUIP_EMAIL, "آدرس ایمیل" },

            { TextMapper.GUIP_SCHOOLLOGIN, "وروذ مدرسه به سیستم" },
            { TextMapper.GUIP_SCHOOLGROUP, "من هستم" },
            { TextMapper.GUIP_SCHOOLPASSWORD, "رمز عبور" },
            { TextMapper.GUIP_CLASS, "کلاس" },

            { TextMapper.GUIP_BTN_SAVE, "ذخیره" },
            { TextMapper.GUIP_BTN_RESET, "ریست" },
            { TextMapper.GUIP_BTN_DELETE_PROFILE, "حذف پروفایل" },

            { TextMapper.GUIP_MSG_PROVIDED_SCHOOL, "داده ها توسط مدرسه آماده شد" },

            { TextMapper.GUIP_ERR_CHANGE, "خطایی رخ داد" },

            { TextMapper.GUIP_OPT_SELECT_GROUP, "یک گزینه انتخاب کنبد" },

            { TextMapper.GUIP_CONFIRM_REMOVE_USER, "آیا مطمئنید میخواهید کاربری خود را حذف کنید؟" },
            { TextMapper.GUIP_CONFIRM_REMOVE_USER_TITLE, "حذف کاربری" },

            { TextMapper.GUIP_MSG_PROFILE_CHANGED, "کاربری شما تغییر کرد"},

            { TextMapper.GUIPT_SCHOOL, "مدرسه" },
            { TextMapper.GUIPT_TEACHER_FROM_CLASS, "معلم کلاس" },
            { TextMapper.GUIPT_BTN_ADD_CLASS, "ایجاد کلاس" },
            
         { TextMapper.GUIS_STUDENTS, "دانش آموزان"},
         { TextMapper.GUIS_TEACHERS, "معلمان"},
         { TextMapper.GUIS_SCHOOL_MANAGEMENT, "مدیریت مدرسه"},

         { TextMapper.GUIS_TLTP_DELETE_SCHOOL, "حذف مدرسه {0} "},
         { TextMapper.GUIS_TLTP_EDIT_SCHOOL, "ویرایش نام مدرسه"},
         { TextMapper.GUIS_TLTP_USERS_SCHOOL, "دانش آموزان  {0}"},
         
         { TextMapper.GUIS_ADD_SCHOOL, "ایجاد مدرسه"},
         { TextMapper.GUIS_DELETE_SCHOOL, "حذف مدرسه"},
         { TextMapper.GUIS_RENAME_SCHOOL, "ویرایش نام مدرسه"}, 
         { TextMapper.GUIS_MSG_RENAME_SCHOOL, "ایجاد نام یک مدرسه جدید"},
         { TextMapper.GUIS_MSG_DELETE_SCHOOL, "آیا واقعا میخواهید این مدرسه را حذف کنید؟"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY, "این مدرسه حاوی تعدادی کاربر است. آیا واقعا قصد حذف آنرا دارید؟"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY_TITLE, "این مدرسه حاوی تعدادی کاربر است."},
         { TextMapper.GUIS_MSG_DELETE_STUDENT, "آیا واقعا قصد حذف {0} از این مدرسه را دارید؟"},
         { TextMapper.GUIS_DELETE_STUDENT, "حذف دانش آموزان مدرسه"},
         { TextMapper.GUIS_NO_STUDENTS, "مدرسه {0} حاوی دانش آموزی نیست"}, 

         { TextMapper.GUIC_STUDENTS, "دانش آموزان"},
         { TextMapper.GUIC_CLASS_MANAGEMENT, "مدیریت کلاس"},

         { TextMapper.GUIC_TLTP_DELETE_CLASS, "حذف کلاس {0} "},
         { TextMapper.GUIC_TLTP_EDIT_CLASS, "ویرایش نام کلاس"},
         { TextMapper.GUIC_TLTP_USERS_CLASS, "دانش آموزان کلاس {0}"},

         ////peter
         { TextMapper.GUIC_TLTP_ASSIGN_CLASS, "تخصیص ماژول به کلاس {0}"},
		 ////peter

            { TextMapper.GUIC_STUDENTS, "دانش آموزان" },
            { TextMapper.GUIC_ADD_CLASS, "ایجاد کلاس"},
            { TextMapper.GUIC_DELETE_CLASS, "حذف کلاس" },
            { TextMapper.GUIC_RENAME_CLASS, "ویرایش نام کلاس" },
            { TextMapper.GUIC_MSG_RENAME_CLASS, "نام جدید کلاس" },
            { TextMapper.GUIC_MSG_DELETE_CLASS, "آیا واقعا مبخواهید کلاس را حذف کنید؟" },
            { TextMapper.GUIC_CLASS_NOT_EMPTY, "این مدرسه حاوی تعدادی دانش آموز است. آیا واقعا قصد حذف آنرا دارید؟" },
            { TextMapper.GUIC_CLASS_NOT_EMPTY_TITLE, "این مدرسه حاوی تعدادی دانش آموز است" },
         { TextMapper.GUIC_MSG_DELETE_STUDENT, "آیا واقعا قصد حذف {0} از این کلاس را دارید؟"},
         { TextMapper.GUIC_DELETE_STUDENT, "حذف دانش آموزان کلاس"},
         { TextMapper.GUIC_NO_STUDENTS, "کلاس {0} حاوی دانش آموزی نیست"},

         { TextMapper.GUIRS_RESULTS, "نتایج"},
         { TextMapper.GUIRS_NO_RESULTS, "نتایج موجود نیست"},
         { TextMapper.GUIRS_BTN_SELECT_COURSES, "انتخاب ماژولها"},

         { TextMapper.GUIRS_TLTP_SELECT_COURSES, "یک ماژول انتخال کنید"},

         { TextMapper.GUIRS_TLTP_ZOOM, "نتابج {0}"},
         { TextMapper.GUIRS_TLTP_ZOOM_ORDER, "بر اساس {0} مرتب کن"},

         { TextMapper.GUIRS_TLTP_RESULT_SCORE_BUTTON, "نتایج فعالیت {0} از {1} را نمایش بده"},
         { TextMapper.GUIRSDLG_MSG, "همه نتایج ''{0}'' برای {1} حذف شود؟"},

         { TextMapper.UG_RESULTS_OF_STUDENT, "نتایج فعالیت {0} از {1}"},

         { TextMapper.GUISC_TITLE, "ماژولها را انتخاب کنید"},
         { TextMapper.GUISC_BTN_SELECT_ALL, "انتخاب همه"},
         { TextMapper.GUISC_BTN_DESELECT_ALL, "برگرداندن انتخاب همه"},

         { TextMapper.UG_CLASSES, "کلاسها"},
         { TextMapper.UG_STUDENTS_OF_CLASS, "دانش آموزان {0}"},

         { TextMapper.UG_USER_TITLE,"دانش اموز"},
         { TextMapper.UG_CLASS_TITLE,"کلاس"},

         { TextMapper.UG_CLASS_CHILD, "دانش اموزان {0}"},
         { TextMapper.UG_CLASS_ORDER_ASC, "نام کلاس (A-Z)"},
         { TextMapper.UG_CLASS_ORDER_DESC, "نام کلاس (Z-A)"},

         { TextMapper.UG_USER_PARENT, "کلاسها"},
         { TextMapper.UG_USER_ORDER_ASC, "نام خانوادگی (A-Z)"},
         { TextMapper.UG_USER_ORDER_DESC, "نام خانوادگی (Z-A)"},

         { TextMapper.LG_COURSES, "ماژولها"},
         { TextMapper.LG_SCOS_OF_COURSE, "فعالیتهای {0}"},

         { TextMapper.LG_COURSE_CHILD, "فعالیتهای {0}"},
         { TextMapper.LG_COURSE_ORDER_ASC, "نتایج (0-100)"},
         { TextMapper.LG_COURSE_ORDER_DESC, "نتایج (100-0)"},

         { TextMapper.LG_SCO_PARENT, "ماژولها"},
         { TextMapper.LG_SCO_ORDER_ASC, "نتایج (0-100)"},
         { TextMapper.LG_SCO_ORDER_DESC, "نتایج (100-0)"},

         { TextMapper.LG_SCO_NAME , "فعالیت {0}"},

         { TextMapper.GUIC_ADD_COURSE, "ایجاد ماژول جدید"},
         { TextMapper.GUIC_ADD_MAP, "ایجاد پوشه جدید" },
         { TextMapper.GUIC_COURSE_MANAGEMENT, "مدیریت ماژول"},

         { TextMapper.GUIC_TLTP_DELETE_COURSE, "حذف ماژول {0}"},
         { TextMapper.GUIC_TLTP_DELETE_MAP, "حذف پوشه {0}"},
         { TextMapper.GUIC_TLTP_EDIT_COURSE, "ویرایش ماژول"},
         { TextMapper.GUIC_TLTP_SCO_COURSE, "مدیریت فعالیت"},

         { TextMapper.GUICDLG_COURSE_NAME, "نام ماژول"},
         { TextMapper.GUICDLG_MAP_NAME, "نام پوشه" },

         { TextMapper.GUICDLG_COURSE_DESCRIPTION, "توضیحات"},

         { TextMapper.GUICDLG_TTL_ADD_COURSE, "ایجاد ماژول جدید"},
         { TextMapper.GUICDLG_TTL_EDIT_COURSE, "ویرایش ماژول"},
         { TextMapper.GUIC_TLTP_EDIT_MAP, "ویرایش پوشه" },

         { TextMapper.GUIC_NO_COURSES, "مازولی برای نمایش وجود ندارد"},
         { TextMapper.GUIC_COURSE_SHARE, "به اشتراک گذاری ماژول" },

         { TextMapper.GUIC_MSG_COURSE_DELETE, "در حال حاضر فعالیتهایی وجود دارد \nاگر ماژول را حذف کنید \n فعالیتها نیز حذف خواهد شد\n \nآیا واقعا  میخواهید ماژول حذف شود؟"},
         { TextMapper.GUIC_MSG_COURSE_DELETE_NO_SCO, "آیا واقعا  میخواهید ماژول حذف شود؟"},
         { TextMapper.GUIC_MSG_TTL_COURSE_DELETE, "خذف ماژول"},

         { TextMapper.GUIS_ADD_SCO, "ایجاد فعالیت جدید"},
         { TextMapper.GUIS_LBL_SCO_OF_COURSE, "فعالیتهای ماژول  {0}"},
         { TextMapper.GUIS_SCO_MANAGEMENT, "مدیریت فعالیت"},
         { TextMapper.GUIS_SHOW_SCORE, "نمایش نتایج دانش آموران"},


         { TextMapper.GUIS_TLTP_DELETE_SCO, "حذف فعالیت {0}"},
         { TextMapper.GUIS_TLTP_EDIT_SCO, "ویرایش نام فعالیت"},
         { TextMapper.GUIS_TLTP_PARAMETERS_SCO, "ویرایش فعالیت"},
         { TextMapper.GUIS_TLTP_COURSE_SCO, "برگشت به ماژول"},

         { TextMapper.GUISDLG_SCO_NAME, "نام فعالیت"},
         { TextMapper.GUISDLG_SCO_DESCRIPTION, "توضیحات فعالیت"},

         { TextMapper.GUISDLG_TTL_ADD_SCO, "ایجاد فعالیت جدید"},
         { TextMapper.GUISDLG_TTL_EDIT_SCO, "ویرایش فعالیت"},

         { TextMapper.GUIS_MSG_SCO_DELETE, "اگر فعالیت را حذف کنید \n نتایج فعالیتها نیز حذف خواهد شد\n \nآیا واقعا  میخواهید فعالیت حذف شود؟"},
         { TextMapper.GUIS_MSG_TTL_SCO_DELETE, "حذف فعالیت"},
         { TextMapper.GUIS_NO_SCOS, "فعالیتی در ماژول وجود ندارد {0}"},
         { TextMapper.GUIS_LOAD_LOGO, "بارگیری شکلک {0}"},
         
         { TextMapper.GUISDLG_BTN_ADD_SCO, "ایجاد"},
         { TextMapper.GUISDLG_BTN_PREVIEW_SCO, "پیش نمایش فعالیت"},
         { TextMapper.GUISDLG_MSG_SELECT_SCO, "انتخاب فعالیت"},
         { TextMapper.GUISDLG_MSG_NO_APPLETS, "فعالیتی برای ایجاد وجود ندارد"},
         { TextMapper.GUISDLG_SHOW, "نمایش"},
         { TextMapper.GUISDLG_ALL, "همه"},
         { TextMapper.GUISDLG_MSG_NO_SELECTION, "فعالیتی انتخاب نشده"},
         { TextMapper.GUISDLG_RB_STANDARD_SCOS, "فعالیتهای استاندارد"},
         { TextMapper.GUISDLG_RB_OWN_SCOS, "فعالیتهای خودم"},

         { TextMapper.GUIPA_BTN_PREVIEW, "پیش نمایش فعالیت"},
         { TextMapper.GUIPA_BTN_SAVE, "ذخیره"},
         { TextMapper.GUIPA_BTN_RESET, "ریست"},
         { TextMapper.GUIPA_BTN_CANCEL, "بستن"},
         
         { TextMapper.GUIPA_SCO_EDIT, "ویرایش فعالیت"},
         
         { TextMapper.GUIPA_NO_PARAMS, "قادر به تغییر این فعالیت نیستید"},

         { TextMapper.GUIPA_DLG_TTL, "وضعیت ویرایش فعالیت {0}"},
         
         { TextMapper.GUIPA_MSG_PARAM_SAVE, "اگر تنظیمات جدید را ذخیره کنید,\nنتایج قبلی حذف میشوند\n \nآیا واقعا میخواهید ذخیره شود؟"},
         { TextMapper.GUIPA_MSG_TTL_PARAM_SAVE, "دخیره تنظیمات"},
         
         { TextMapper.GUIPA_PARAMS_OF_SCO, "پارامترهای ({0})"},
       
         { "cut", "بریدن" },
         { "copy" , "کپی" },
         { "paste", "Paste"},
         { "delete", "حذف" },
         { "edit", "ویرایش" },
         { "file", "فایل" },
         { "rename", "تغییر نام" },

         { TextMapper.GUIA_INSERT_SCOS, "اضافه کردن فعالیتها از نسخه پشتیبان"},
         { TextMapper.GUIH_STOP_EDIT, "توقف ویرایش" },
         { TextMapper.GUIH_EDIT, "ویرایش" },
         
         { "Alle modules", "همه ماژولها"},
         { "Standaard DWO modules", "ماژولهای استاندارد DME"},

         { "Nieuwe Modulemap", "پوشه ماژول جدید" },

         // classadminpanel
         { "Klassen toewijzen", "تخصیص کلاس" },
         { "Klas", "کلاس" },
         { "Docent", "معلم" },
         { "Verwijder", "حذف" },
         // classpanel 
         { "boomstructuur?", "مشاهده درخت؟" },
         // select courses dialog
         { "Leerlinggegevens verwijderen", "حذف نتایج دانش اموز" },
         { "Wilt u alle resultaten van {0} voor {1} verwijderen?", "آیا میخواهید کلیه نتایج {0} از {1} را حذف کنید؟" },
         { "soort", "نوع" },
         { "vanaf", "از" },
         { "tot aan", "تا" },
         { "tot", "تا" },
         { "Ll ggvns", "نتایج" },
         { "normaal", "عادی" },
         { "afgeschermd", "امن شده" },
         { "Geef tijdstip {0}", "تنظیم تاریخ و ساعت  \"{0}\""},
         { " dag: " , " تاریخ: " },
         { "tijd:", "ساعت:" },
         // resultLoogger 
         { "Overzicht Logs", "مشاهده لاگها" },
         { "deel-scores", "نمرات مرحله ای" },
         { "tijdsduur", "مدت" },
         // default partial score
         { "resultaat", "نتیجه" },
         //importexportdialog
         {"Kopiëer modules", "کپی ماژولها" }, 
         {"Toestaan", "اجازه دادن" },
         {"Modules beschikbaar stellen", "آماده سازی ماژولها" },
         {"Modules opvragen", "درخواست ماژولها"},
         {"Delen met","به اشتراک گذاشتن با"},
         {"Alle scholen","همه مدارس"},
         {"Scholen", "مدارس"},
         {"toepassen", "اعمال کردن"},
         
         { TextMapper.GUIEID_MSG1, "<html>(1) Select a school<br>" +
			   "(2) Eventually preview the shown modules<br>" +
			   "(3) Select one or more modules for use in your own school<br><br>" +
			   "The selected modules are copied to your own module view<br>"+
			   "and can be used at your own school." },
		  { TextMapper.GUIEID_MSG2, "<html>I wish to participate in this way of sharing and become visible as school in the lists"},
		  { TextMapper.GUIEID_MSG3, "<html>(1) Select modules<br>(2) Select schools<br><br>The selected modules are available<br>to the selected schools." },

};



 /**
  * @return Object[][]
  */
 public Object[][] getContents() {
     return contents;
 }
}