# Note the commented instructions directly below are now in the pom.xml file.
# For running proguard on the command line copy this file and remove the hashes
# and update the paths.
#-injars DWOJApplet-1.0-SNAPSHOT-r9081.jar
#-outjars  DWOJApplet-1.0-SNAPSHOT-r9081-proguarded.jar
# Note these are system dependent, hence moved to pom.xml and made compile platform dependent.
#-libraryjars C:\Program Files\Java\jdk1.7.0_67\jre\lib\rt.jar
#-libraryjars C:\Program Files\Java\jdk1.7.0_67\jre\lib\jfxrt.jar
#-libraryjars C:\Program Files\Java\jdk1.7.0_67\jre\lib\jsse.jar

-target 1.6

-forceprocessing

-keep class uk.co.wilson.xml.*{
    <fields>;
    <methods>;
}

-keep class uk.org.xml.sax.*{
    <fields>;
    <methods>;
}

-keep class org.xml.sax.*{
    <fields>;
    <methods>;
}

-keep class fi.dwo.commons.system.text.* {
    <fields>;
    <methods>;
}

-keep class fi.dwo.dwojapplet.system.* {
    <fields>;
    <methods>;
}

#-keep class fi.graphtool.text.* {
#    <fields>;
#    <methods>;
#}


#-keep class fi.graphtool.text.* {
#    <fields>;
#    <methods>;
#}

#-keep class fi.shaded.wiskopdr2.tekstobjects.LinkIF{
#    <fields>;
#    <methods>;
#}

#-keep class fi.shaded.wiskopdr2.tekstobjects.TekstArea{
#    <fields>;
#    <methods>;
#}

#-keep class fi.shaded.wiskopdr2.RealPoint{
#    <fields>;
#    <methods>;
#}

-keep class fi.beans.*.* {
    <fields>;
    <methods>;
}

-keep class fi.dwo.commons.persistence.*{
    <fields>;
    <methods>;
}

-keep class fi.dwo.dwojapplet.parameters.**{
    <methods>;
}

-keep class fi.dwo.dwojapplet.system.*{
    <fields>;
    <methods>;
}

-keep class fi.dwo.dwojapplet.persistence.AppletMapper{
    <methods>;
}
-keep class fi.dwo.dwojapplet.persistence.UserMapper{
    <methods>;
}
-keep class fi.dwo.dwojapplet.domain.User
-keep class fi.dwo.dwojapplet.persistence.SchoolMapper{
    <methods>;
}
-keep class fi.dwo.dwojapplet.domain.School
-keep class fi.dwo.dwojapplet.persistence.GroupMapper{
    <methods>;
}
-keep class fi.dwo.dwojapplet.domain.Group
-keep class fi.dwo.dwojapplet.persistence.SchoolGroupMapper{
    <methods>;
}
-keep class fi.dwo.dwojapplet.domain.SchoolGroup
-keep class fi.dwo.dwojapplet.persistence.CourseMapper{
    <methods>;
}
-keep class fi.dwo.dwojapplet.domain.Course
-keep class fi.dwo.dwojapplet.persistence.ScoMapper{
    <methods>;
}
-keep class fi.dwo.dwojapplet.domain.Sco
-keep class fi.dwo.dwojapplet.persistence.ClassMapper{
    <methods>;
}
-keep class fi.dwo.dwojapplet.domain.SchoolClass
-keep class fi.dwo.dwojapplet.persistence.UserResultListMapper{
    <methods>;
}
-keep class fi.dwo.dwojapplet.domain.UserResultList
-keep class fi.dwo.dwojapplet.domain.ResultsModuleIF

-keep class fi.dwo.dwojapplet.persistence.AppletConfigMapper{
    <methods>;
}
-keep class fi.dwo.dwojapplet.domain.AppletConfig
-keep class fi.dwo.dwojapplet.persistence.DwoProfileMapper{
    <methods>;
}
-keep class fi.dwo.dwojapplet.domain.DwoProfile
-keep class fi.dwo.dwojapplet.persistence.AppletDataMapper{
    <methods>;
}
-keep class fi.dwo.dwojapplet.domain.AppletData
-keep class fi.dwo.dwojapplet.persistence.CourseSequenceMapper{
    <methods>;
}
-keep class fi.dwo.dwojapplet.domain.CourseSequence
-keep class fi.dwo.dwojapplet.persistence.ClassCourseMapper{
    <methods>;
}
-keep class fi.dwo.dwojapplet.domain.ClassCourse
-keep class fi.dwo.dwojapplet.persistence.XmlRpcMapper{
    <fields>;
    <methods>;
}

-keep  class  fi.dwo.rest.exceptions.** { *; } 
-keep  class  fi.dwo.rest.util.** { *; } 
#-keep  class fi.dwo.commons.persistence.** { *; } 
-keep  class fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator { *; } 
-keep  class fi.dwo.commons.persistence.MySQLPersistenceId { *; } 
-keep  class  fi.dwo.rest.dom.entities.** { *; } 
-keep  class fi.dwo.rest.entities.** { *; } 
-keepattributes Signature
-keepattributes *Annotation*
#keep stuff for Genson
#-keep class  com.owlike.genson.** { *; } 
-keep class com.owlike.genson.*{ *; }  


#-keep class fi.shaded.wiskopdr2.WiskOpdr{
#    <fields>;
#    <methods>;
#}

#-keep class org.**
#-keep class com.**
-keep class java.***
-keep class fi.dwo.rest.**
-keepattributes *Annotation*
-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

-keep,allowshrinking class uk.** {
    <fields>;
    <methods>;
}

-keep,allowshrinking class hplb.** {
    <fields>;
    <methods>;
}

# Keep - Applications. Keep all application classes, along with their 'main'
# methods.
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

# Keep - Applets. Keep all extensions of java.applet.Applet.
-keep public class * extends java.applet.Applet {
	<init>();
	<init>(java.lang.String[]);
}




# Also keep - Enumerations. Keep the special static methods that are required in
# enumeration classes.
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Also keep - Serialization code. Keep all fields and methods that are used for
# serialization.
-keepclassmembers class * extends java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Also keep - Database drivers. Keep all implementations of java.sql.Driver.
-keep class * extends java.sql.Driver

# Also keep - Swing UI L&F. Keep all extensions of javax.swing.plaf.ComponentUI,
# along with the special 'createUI' method.
-keep class * extends javax.swing.plaf.ComponentUI {
    public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent);
}

# Also keep - RMI interfaces. Keep all interfaces that extend the
# java.rmi.Remote interface, and their methods.
-keep interface  * extends java.rmi.Remote {
    <methods>;
}

# Also keep - RMI implementations. Keep all implementations of java.rmi.Remote,
# including any explicit or implicit implementations of Activatable, with their
# two-argument constructors.
-keep class * extends java.rmi.Remote {
    <init>(java.rmi.activation.ActivationID,java.rmi.MarshalledObject);
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

# Remove - System method calls. Remove all invocations of System
# methods without side effects whose return values are not used.
-assumenosideeffects public class java.lang.System {
    public static long currentTimeMillis();
    static java.lang.Class getCallerClass();
    public static int identityHashCode(java.lang.Object);
    public static java.lang.SecurityManager getSecurityManager();
    public static java.util.Properties getProperties();
    public static java.lang.String getProperty(java.lang.String);
    public static java.lang.String getenv(java.lang.String);
    public static java.lang.String mapLibraryName(java.lang.String);
    public static java.lang.String getProperty(java.lang.String,java.lang.String);
}

# Remove - StringBuffer method calls. Remove all invocations of StringBuffer
# methods without side effects whose return values are not used.
-assumenosideeffects public class java.lang.StringBuffer {
    public java.lang.String toString();
    public char charAt(int);
    public int capacity();
    public int codePointAt(int);
    public int codePointBefore(int);
    public int indexOf(java.lang.String,int);
    public int lastIndexOf(java.lang.String);
    public int lastIndexOf(java.lang.String,int);
    public int length();
    public java.lang.String substring(int);
    public java.lang.String substring(int,int);
}

# Remove - StringBuilder method calls. Remove all invocations of StringBuilder
# methods without side effects whose return values are not used.
-assumenosideeffects public class java.lang.StringBuilder {
    public java.lang.String toString();
    public char charAt(int);
    public int capacity();
    public int codePointAt(int);
    public int codePointBefore(int);
    public int indexOf(java.lang.String,int);
    public int lastIndexOf(java.lang.String);
    public int lastIndexOf(java.lang.String,int);
    public int length();
    public java.lang.String substring(int);
    public java.lang.String substring(int,int);
}
