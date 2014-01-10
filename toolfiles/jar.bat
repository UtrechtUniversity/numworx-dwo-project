%JAVA_HOME%\bin\jar.exe cvfm ..\output\jar\dwo.jar ..\output\classes\meta-inf\Manifest.mf @jar.txt
cd ..\output\jar
call jarindex
cd ..\..\toolfiles

