%JAVA_HOME%\bin\jar.exe cvfm ..\output\jar\dwo.jar ..\output\classes\meta-inf\Manifest.mf @jar_all.txt
%JAVA_HOME%\bin\jar.exe cvfm ..\output\jar\dwo-test.jar ..\output\classes\meta-inf\Manifest.mf @jar_all.txt
cd ..\output\jar
call jarindex
call jarindex-test
cd ..\..\toolfiles

