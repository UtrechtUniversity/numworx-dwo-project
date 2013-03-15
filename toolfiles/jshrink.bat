java -jar ..\..\..\tools\jshrink.exe -script jshrink_all.txt
cd ..\output\jar
call jarindex
%JAVA_HOME%\bin\pack200 --repack dwo.jar
%JAVA_HOME%\bin\pack200 dwo.jar.pack.gz dwo.jar