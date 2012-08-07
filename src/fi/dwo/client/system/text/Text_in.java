//Source file:
//N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\Text_en.java

package fi.dwo.client.system.text;

import java.util.ListResourceBundle;

import fi.dwo.client.system.TextMapper;

public class Text_in extends ListResourceBundle {
 private final Object[][] contents = {
         { TextMapper.USER_GUEST, "Tamu"},
         { TextMapper.BTN_LOGIN, "Masuk" },
         { TextMapper.BTN_CANCEL, "Batalkan" },
         { TextMapper.BTN_NO, "Tidak" },
         { TextMapper.BTN_OK, "OK" },
         { TextMapper.BTN_YES, "Ya" },
         { TextMapper.BTN_CLOSE, "Tutup"},
         { TextMapper.DLG_CONFIRM, "Konfirmasi" },
         { TextMapper.DLG_ENTER_INPUT, "Enter masukan" },
         { TextMapper.DLG_MESSAGE, "Pesan" },

         { TextMapper.EX_UNKNOWN_ERROR, "Kesalahan internal terjadi" },
         { TextMapper.EXR_USER_EXISTS, "Nama pengguna tertentu sudah ada" },
         { TextMapper.EXR_USER_EXISTS2, "Nama pengguna {0} sudah ada" },
         { TextMapper.EXR_WRONG_SECOND_PASSWORD, "Kata sandi tertentu berbeda" },
         { TextMapper.EXR_WRONG_USERNAME_PASSWORD, "Pengguna dengan nama pengguna dan kata sandi tertentu tidak ditemukan" },
         { TextMapper.EXR_UNKNOWN_SCHOOLGROUP, "Sekolah tak dikenal/kelompok/kombinasi kata sandi" },
         { TextMapper.EXR_MANDATORY, "{0} pada {1} isn't filled. This is a required field"},
         { TextMapper.EXR_WRONG_FORMAT, "{0} pada {1} mengandung karakter ilegal" },
         { TextMapper.EXR_WRONG_EMAILFORMAT, "{0} pada {1} adalah ilegal" },
         { TextMapper.EXC_CLASS_EXISTS, "Kelas tertentu sudah ada" },
         { TextMapper.EXS_SCHOOL_EXISTS, "Sekolah dengan login tertentu sudah ada" },
         { TextMapper.EXL_UNKNOWN_USER, "Pengguna dengan nama pengguna tertentu tidak ditemukan" },
         { TextMapper.EXC_COURSE_EXISTS, "Modul tertentu sudah ada" },
         { TextMapper.EXS_SCO_EXISTS, "Aktivitas tertentu sudah ada" },
         { TextMapper.EXS_NO_APPLET, "Applet tidak ditemukan"},

         { TextMapper.GUI_WAIT_A_MOMENT, "Silakan tunggu sebentar"},

         { TextMapper.GUIW_LOGINDATA, "Masuk data" },
         { TextMapper.GUIW_USERNAME, "Nama pengguna" },
         { TextMapper.GUIW_PASSWORD, "Kata sandi" },
         { TextMapper.GUIW_WELCOME, "Selamat datang" },
         { TextMapper.GUIW_GUESTLOGIN, "Masuk sebagai tamu" },
         { TextMapper.GUIW_REGISTER, "Daftar" },
         { TextMapper.GUIW_MSG_WORK_NOT_SAVE, "Hasil pekerjaan Anda tidak akan disimpan" },
         { TextMapper.GUIW_MSG_REGISTER_NEW, "Daftar sebagai anggota baru." },
         { TextMapper.GUIW_BTN_GUESTLOGIN, "Masuk sebagai tamu" },
         { TextMapper.GUIW_BTN_LOGIN, "Masuk" },
         { TextMapper.GUIW_BTN_REGISTER, "Daftar" },
         { TextMapper.GUIW_ERR_LOGIN, "Salah Masuk" },

         { TextMapper.GUIR_REGISTER, "Daftar" },

         { TextMapper.GUIR_REGISTERINFO, "Daftar pengguna baru" },
         { TextMapper.GUIR_PERSONALINFO, "Informasi pribadi" },
         { TextMapper.GUIR_SCHOOLINFO, "Informasi sekolah" },

         { TextMapper.GUIR_USERNAME, "Nama pengguna" },
		 { TextMapper.GUIR_PASSWORD, "Kata sandi" },
		 { TextMapper.GUIR_RE_PASSWORD, "Konfirmasi kata sandi" },

		 { TextMapper.GUIR_FIRSTNAME, "Nama" },
		 { TextMapper.GUIR_MIDDLENAME, "Nama tengah" },
		 { TextMapper.GUIR_LASTNAME, "Nama keluarga" },
		 { TextMapper.GUIR_EMAIL, "Alamat E-mail" },

		 { TextMapper.GUIR_SCHOOLLOGIN, "Login sekolah" },
		 { TextMapper.GUIR_SCHOOLGROUP, "Saya adalah" },
		 { TextMapper.GUIR_SCHOOLPASSWORD, "Kata sandi" },

		 { TextMapper.GUIR_BTN_REGISTER, "Daftar" },
		 { TextMapper.GUIR_BTN_RESET, "Reset" },
         { TextMapper.GUIR_BTN_BACK, "Kembali ke modul" },

            { TextMapper.GUIR_MSG_PROVIDED_SCHOOL, "Data disediakan oleh sekolah" },

            { TextMapper.GUIR_OPT_SELECT_GROUP, "Membuat pilihan" },
            { TextMapper.GUIR_OPT_STUDENT, "Siswa" },
            { TextMapper.GUIR_OPT_TEACHER, "Guru" },
            { TextMapper.GUIR_OPT_ADMIN, "Administrator"},
            { TextMapper.GUIR_OPT_SCHOOLCODE, "Kode kunci"},
            
            { TextMapper.GUIR_ERR_REGISTER, "Kesalahan telah terjadi" },

            { TextMapper.GUIR_MSG_REGISTERED, "Anda berhasil terdaftar"},

            { TextMapper.GUIM_DWO_FULL, "Digital Mathematics Environment" },
            { TextMapper.GUIM_DWO_SHORT, "DME" },
            { TextMapper.GUIM_FI_NAME, "Freudenthal Institute"},
            { TextMapper.GUIM_MAIN_MENU, "Modul" },

            { TextMapper.GUIL_LOGGED_IN_AS, "Anda masuk sebagai" },
            { TextMapper.GUIL_NOT_LOGGED_IN, "Anda tidak masuk"},
            { TextMapper.GUIL_BTN_LOGIN, "Masuk"},
            { TextMapper.GUIL_BTN_LOGOFF, "Keluar" },

            { TextMapper.GUIMNU_MAIN_MENU, "Modul" },
            { TextMapper.GUIMNU_MY_PROFILE, "Profil Saya" },
            { TextMapper.GUIMNU_STUDENT_IN_CLASS, "Siswa dari kelas" },
            { TextMapper.GUIMNU_STUDENT_NO_CLASS_0, "Anda bukan"},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_1, "anggota dari kelas.  "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_2, "Menuju ke"},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_3, "\"Profil saya\" dan "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_4, "pilih sebuah kelas."},
            { TextMapper.GUIMNU_CLASS_RESULTS, "Hasil dari kelas" },
            { TextMapper.GUIMNU_RESULTS, "Lihat pada hasil" },
            { TextMapper.GUIMNU_CLASS_MANAGEMENT, "Pengelolaan kelas" },
            { TextMapper.GUIMNU_SCHOOL_MANAGEMENT, "Pengelolaan sekolah" },
            { TextMapper.GUIMNU_COURSE_MANAGEMENT, "Pengelolaan modul"},
            { TextMapper.GUIMNU_MSG_ADD_CLASS, "Nama dari kelas baru" },
            { TextMapper.GUIMNU_MSG_ADD_CLASS_TITLE, "Tambahkan kelas baru" },
            { TextMapper.GUIMNU_MSG_ADD_SCHOOL, "Nama dari sekolah baru"},   
         	{ TextMapper.GUIMNU_MSG_ADD_SCHOOL_TITLE, "Tambahkan sekolah baru"},
         	{ TextMapper.GUIMNU_USERS_SCHOOL, "School users"},
            { TextMapper.GUIMNU_CLASSES_SCHOOL, "School classes"},
            { TextMapper.GUIMNU_FEATURES_SCHOOLADMIN, "Features schooladmin"},
            
            { TextMapper.GUIUMP_MANAGE_USERS, "Manage users"},
            { TextMapper.GUIUMP_REMOVE_FROM_SCHOOL, "Remove only from school"},
            { TextMapper.GUIUMP_REMOVE_COMPLETE, "Remove complete account"},
            { TextMapper.GUIUMP_ADD_STUDENTS, "Add new students"},
            { TextMapper.GUIUMP_ADD_TEACHERS, "Add new teachers"},
            { TextMapper.GUIUMP_IMPORT_CLIPBOARD, "Import from clipboard"},
            { TextMapper.GUIUMP_MAKE_ACCOUNTS, "Make accounts"},
            { TextMapper.GUIUMP_EXTRA_ROW, "Additional row"},

         	{ TextMapper.GUICO_HEADER, "Modul"},
         	{ TextMapper.GUICO_SCO_LIST_TITLE, "Aktivitas"},

            { TextMapper.GUIP_MY_PROFILE, "Profil Saya" },
            { TextMapper.GUIP_REGISTERINFO, "Informasi pendaftaran" },
            { TextMapper.GUIP_PERSONALINFO, "Informasi pribadi" },
            { TextMapper.GUIP_SCHOOLINFO, "Informasi sekolah" },

            { TextMapper.GUIP_USERNAME, "Nama pengguna" },
            { TextMapper.GUIP_OLD_PASSWORD, "Kata sandi saat ini" },
            { TextMapper.GUIP_PASSWORD, "Kata sandi baru" },
            { TextMapper.GUIP_RE_PASSWORD, "Konfirmasi kata sandi" },

            { TextMapper.GUIP_FIRSTNAME, "Nama depan" },
            { TextMapper.GUIP_MIDDLENAME, "Nama tengah" },
            { TextMapper.GUIP_LASTNAME, "Nama" },
            { TextMapper.GUIP_EMAIL, "Alamat E-mail" },

            { TextMapper.GUIP_SCHOOLLOGIN, "Login sekolah" },
            { TextMapper.GUIP_SCHOOLGROUP, "Saya adalah" },
            { TextMapper.GUIP_SCHOOLPASSWORD, "Kata sandi" },
            { TextMapper.GUIP_CLASS, "Kelas" },

            { TextMapper.GUIP_BTN_SAVE, "Simpan" },
            { TextMapper.GUIP_BTN_RESET, "Reset" },
            { TextMapper.GUIP_BTN_DELETE_PROFILE, "Hapus profil" },

            { TextMapper.GUIP_MSG_PROVIDED_SCHOOL, "Data disediakan oleh sekolah" },

            { TextMapper.GUIP_ERR_CHANGE, "Kesalahan telah terjadi" },

            { TextMapper.GUIP_OPT_SELECT_GROUP, "Membuat pilihan" },

            { TextMapper.GUIP_CONFIRM_REMOVE_USER, "Apakah Anda yakin untuk menghapus Akun Anda?" },
            { TextMapper.GUIP_CONFIRM_REMOVE_USER_TITLE, "Hapus Akun" },

            { TextMapper.GUIP_MSG_PROFILE_CHANGED, "Akun Anda berhasil diubah"},

            { TextMapper.GUIPT_SCHOOL, "Sekolah" },
            { TextMapper.GUIPT_TEACHER_FROM_CLASS, "Guru dari kelas" },
            { TextMapper.GUIPT_BTN_ADD_CLASS, "Tambahkan kelas" },
            
         { TextMapper.GUIS_STUDENTS, "Siswa"},
         { TextMapper.GUIS_TEACHERS, "Guru"},
         { TextMapper.GUIS_SCHOOL_MANAGEMENT, "Pengelolaan sekolah"},

         { TextMapper.GUIS_TLTP_DELETE_SCHOOL, "Hapus sekolah {0} "},
         { TextMapper.GUIS_TLTP_EDIT_SCHOOL, "Edit nama sekolah"},
         { TextMapper.GUIS_TLTP_USERS_SCHOOL, "Siswa dari {0}"},
         
         { TextMapper.GUIS_ADD_SCHOOL, "Tambahkan sekolah"},
         { TextMapper.GUIS_DELETE_SCHOOL, "Hapus sekolah"},
         { TextMapper.GUIS_RENAME_SCHOOL, "Edit nama sekolah"}, 
         { TextMapper.GUIS_MSG_RENAME_SCHOOL, "Enter nama sekolah baru"},
         { TextMapper.GUIS_MSG_DELETE_SCHOOL, "Apakah Anda yakin akan menghapus sekolah ini"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY, "Sekolah ini mengandung pengguna. Apakah Anda yakin akan menghapus sekolah ini"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY_TITLE, "Sekolah ini mengandung pengguna."},
         { TextMapper.GUIS_MSG_DELETE_STUDENT, "Apakah kamu yakin akan menghapus {0} dari sekolah ini"},
         { TextMapper.GUIS_DELETE_STUDENT, "Hapus siswa dari sekolah ini"},
         { TextMapper.GUIS_NO_STUDENTS, "Sekolah {0} tidak memiliki siswa"}, 

         { TextMapper.GUIC_STUDENTS, "Siswa"},
         { TextMapper.GUIC_CLASS_MANAGEMENT, "Pengelolaan kelas"},

         { TextMapper.GUIC_TLTP_DELETE_CLASS, "Hapus kelas [0} "},
         { TextMapper.GUIC_TLTP_EDIT_CLASS, "Edit nama kelas"},
         { TextMapper.GUIC_TLTP_USERS_CLASS, "Siswa dalam kelas {0}"},

         ////peter
         { TextMapper.GUIC_TLTP_ASSIGN_CLASS, "Tempatkan modul ke kelas {0}"},
		 ////peter

            { TextMapper.GUIC_STUDENTS, "Siswa" },
            { TextMapper.GUIC_ADD_CLASS, "Buat kelas"},
            { TextMapper.GUIC_DELETE_CLASS, "Hapus kelas" },
            { TextMapper.GUIC_RENAME_CLASS, "Edit nama kelas" },
            { TextMapper.GUIC_MSG_RENAME_CLASS, "Nama baru dari kelas" },
            { TextMapper.GUIC_MSG_DELETE_CLASS, "Apakah kamu yakin akan menghapus kelas" },
            { TextMapper.GUIC_CLASS_NOT_EMPTY, "Terdapat beberapa siswa dalam kelas. Apakah Anda yakin akan menghapus kelas" },
            { TextMapper.GUIC_CLASS_NOT_EMPTY_TITLE, "Terdapat beberapa siswa dalam kelas" },
         { TextMapper.GUIC_MSG_DELETE_STUDENT, "Apakah Anda yakin akan menghapus {0} dari kelas"},
         { TextMapper.GUIC_DELETE_STUDENT, "Hapus siswa dari kelas"},
         { TextMapper.GUIC_NO_STUDENTS, "Tidak ada siswa dalam kelas {0}"},

         { TextMapper.GUIRS_RESULTS, "Hasil"},
         { TextMapper.GUIRS_NO_RESULTS, "Tidak ada hasil"},
         { TextMapper.GUIRS_BTN_SELECT_COURSES, "Pilih modul"},

         { TextMapper.GUIRS_TLTP_SELECT_COURSES, "Pilih sebuah modul"},

         { TextMapper.GUIRS_TLTP_ZOOM, "Hasil dari {0}"},
         { TextMapper.GUIRS_TLTP_ZOOM_ORDER, "Kelompokkan berdasar {0}"},

         { TextMapper.GUIRS_TLTP_RESULT_SCORE_BUTTON, "Perlihatkan hasil aktivitas {0} dari {1}"},

         { TextMapper.UG_RESULTS_OF_STUDENT, "Hasil aktivitas {0} dari {1}"},

         { TextMapper.GUISC_TITLE, "Pilih modul"},
         { TextMapper.GUISC_BTN_SELECT_ALL, "Pilih semua"},
         { TextMapper.GUISC_BTN_DESELECT_ALL, "Tidak pilih semua"},

         { TextMapper.UG_CLASSES, "Kelas"},
         { TextMapper.UG_STUDENTS_OF_CLASS, "Siswa-siswa dari {0}"},

         { TextMapper.UG_USER_TITLE,"Siswa"},
         { TextMapper.UG_CLASS_TITLE,"Kelas"},

         { TextMapper.UG_CLASS_CHILD, "Siswa-siswa {0}"},
         { TextMapper.UG_CLASS_ORDER_ASC, "Nama kelas(A-Z)"},
         { TextMapper.UG_CLASS_ORDER_DESC, "Nama kelas (Z-A)"},

         { TextMapper.UG_USER_PARENT, "Kelas-kelas"},
         { TextMapper.UG_USER_ORDER_ASC, "Nama belakang (A-Z)"},
         { TextMapper.UG_USER_ORDER_DESC, "Nama belakang (Z-A)"},

         { TextMapper.LG_COURSES, "Modul"},
         { TextMapper.LG_SCOS_OF_COURSE, "Aktivitas dari {0}"},

         { TextMapper.LG_COURSE_CHILD, "Aktivitas dari {0}"},
         { TextMapper.LG_COURSE_ORDER_ASC, "Hasil (0-100)"},
         { TextMapper.LG_COURSE_ORDER_DESC, "Hasil (100-0)"},

         { TextMapper.LG_SCO_PARENT, "modul"},
         { TextMapper.LG_SCO_ORDER_ASC, "hasil(0-100)"},
         { TextMapper.LG_SCO_ORDER_DESC, "hasil (100-0)"},

         { TextMapper.LG_SCO_NAME , "Aktivitas {0}"},

         { TextMapper.GUIC_ADD_COURSE, "Tambahkan modul baru"},
         { TextMapper.GUIC_ADD_MAP, "Tambahkan folder baru" },
         { TextMapper.GUIC_COURSE_MANAGEMENT, "Pengelolaan modul"},

         { TextMapper.GUIC_TLTP_DELETE_COURSE, "Hapus modul {0}"},
         { TextMapper.GUIC_TLTP_DELETE_MAP, "Hapus folder {0}"},
         { TextMapper.GUIC_TLTP_EDIT_COURSE, "Edit modul"},
         { TextMapper.GUIC_TLTP_SCO_COURSE, "Pengelolaan aktivitas"},

         { TextMapper.GUICDLG_COURSE_NAME, "Nama modul"},
         { TextMapper.GUICDLG_MAP_NAME, "Nama folder" },

         { TextMapper.GUICDLG_COURSE_DESCRIPTION, "Deskripsi"},

         { TextMapper.GUICDLG_TTL_ADD_COURSE, "Tambahkan modul baru"},
         { TextMapper.GUICDLG_TTL_EDIT_COURSE, "Edit modul"},
         { TextMapper.GUIC_TLTP_EDIT_MAP, "Edit folder" },

         { TextMapper.GUIC_NO_COURSES, "Tidak ada modul untuk diperlihatkan"},
         { TextMapper.GUIC_COURSE_SHARE, "Berbagi modul" },

         { TextMapper.GUIC_MSG_COURSE_DELETE, "Terdapat Aktivitas saat ini. \nSaat Anda hapus modul \nen hasil aktivitas juga akan dihapus.\n \nApakah Anda yakin untuk menghapus modul?"},
         { TextMapper.GUIC_MSG_COURSE_DELETE_NO_SCO, "Apakah Anda yakin akan menghapus modul?"},
         { TextMapper.GUIC_MSG_TTL_COURSE_DELETE, "Hapus modul"},

         { TextMapper.GUIS_ADD_SCO, "Tambahkan aktivitas baru"},
         { TextMapper.GUIS_LBL_SCO_OF_COURSE, "Aktivitas dari modul {0}"},
         { TextMapper.GUIS_SCO_MANAGEMENT, "Pengelolaan aktivitas"},
         { TextMapper.GUIS_SHOW_SCORE, "Siswa dapat melihat hasil pekerjaannya"},


         { TextMapper.GUIS_TLTP_DELETE_SCO, "Hapus aktivitas {0}"},
         { TextMapper.GUIS_TLTP_EDIT_SCO, "Edit nama Aktivitas"},
         { TextMapper.GUIS_TLTP_PARAMETERS_SCO, "Edit Aktivitas"},
         { TextMapper.GUIS_TLTP_COURSE_SCO, "Kembali ke modul"},

         { TextMapper.GUISDLG_SCO_NAME, "Nama aktivitas"},
         { TextMapper.GUISDLG_SCO_DESCRIPTION, "Deskripsi Aktivitas"},

         { TextMapper.GUISDLG_TTL_ADD_SCO, "Tambahkan aktivitas baru"},
         { TextMapper.GUISDLG_TTL_EDIT_SCO, "Edit aktivitas"},

         { TextMapper.GUIS_MSG_SCO_DELETE, "Bila Anda hapus aktivitas\nen hasil juga akan dihapus.\n \nApakah Anda yakin akan menghapus Aktivitas?"},
         { TextMapper.GUIS_MSG_TTL_SCO_DELETE, "Hapus aktivitas"},
         { TextMapper.GUIS_NO_SCOS, "Tidak ada aktivitas dalam modul {0}"},

         { TextMapper.GUISDLG_BTN_ADD_SCO, "Tambahkan"},
         { TextMapper.GUISDLG_BTN_PREVIEW_SCO, "Pratinjau aktivitas"},
         { TextMapper.GUISDLG_MSG_SELECT_SCO, "Pilih aktivitas"},
         { TextMapper.GUISDLG_MSG_NO_APPLETS, "Tidak ada aktivitas untuk ditambahkan"},
         { TextMapper.GUISDLG_SHOW, "Perlihatkan"},
         { TextMapper.GUISDLG_ALL, "Semua"},
         { TextMapper.GUISDLG_MSG_NO_SELECTION, "Anda belum memilih aktivitas"},
         { TextMapper.GUISDLG_RB_STANDARD_SCOS, "Aktivitas Standar"},
         { TextMapper.GUISDLG_RB_OWN_SCOS, "Aktivitas Sendiri"},

         { TextMapper.GUIPA_BTN_PREVIEW, "Pratinjau aktivitas"},
         { TextMapper.GUIPA_BTN_SAVE, "Simpan"},
         { TextMapper.GUIPA_BTN_RESET, "Atur Ulang"},
         { TextMapper.GUIPA_BTN_CANCEL, "Tutup"},
         
         { TextMapper.GUIPA_SCO_EDIT, "Edit Aktivitas"},
         
         { TextMapper.GUIPA_NO_PARAMS, "Aktivitas ini tidak dapat diubah"},

         { TextMapper.GUIPA_DLG_TTL, "Mode-Edit dari Aktivitas {0}"},
         
         { TextMapper.GUIPA_MSG_PARAM_SAVE, "Jika Anda menyimpan konfigurasi baru ini,\nhasil item lama akan dihapus\n \nApakah Anda yakin akan menyimpan konfigurasi ini?"},
         { TextMapper.GUIPA_MSG_TTL_PARAM_SAVE, "Simpan Konfigurasi"},
         
         { TextMapper.GUIPA_PARAMS_OF_SCO, "Parameter ({0})"},
         
         { "cut", "Cut" },
         { "copy" , "Salin" },
         { "paste", "Tempel"},
         { "delete", "Hapus" },
         { "edit", "Edit" },
         { "file", "File" },
         { "rename", "Ubah nama" },

         { TextMapper.GUIA_INSERT_SCOS, "Sisipkan aktivitas dari backup"},
         { TextMapper.GUIH_STOP_EDIT, "Hentikan pengeditan" },
         { TextMapper.GUIH_EDIT, "Edit" },
         
         { "Alle modules", "Semua modul"},
         { "Standaard DWO modules", "Modul Standar DME"},

         { "Nieuwe Modulemap", "Folder modul baru" },

         

};

 public Text_in() {

 }

 /**
  * @return Object[][]
  */
 public Object[][] getContents() {
     return contents;
 }
}