copy target\osiris-import-0.0.1-SNAPSHOT.jar target\dependency\
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_201
set JRE_HOME="%JAVA_HOME%\jre"
set JRE_HOME=C:\Program Files\Java\jre1.8.0_201
path %JAVA_HOME%\bin;C:\Program Files (x86)\Inno Setup 5;%PATH%

javapackager -deploy -BappVersion=0.0.1 -Bruntime="%JRE_HOME%" -BjvmOptions=-Xmx1024m -native exe -name Numworx-import -title Numworx-import -vendor Numworx -description "Import OSIRIS files into Numworx" -height 600 -width 800 -appclass nl.numworx.osiris.Main -srcdir target\dependency -outdir target\deploy -outfile Numworx-import-install
