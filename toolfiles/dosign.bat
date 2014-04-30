REM aanroepen dosign wiskopdr

%JAVA_HOME%\bin\pack200 --repack ../output/jar/%1.jar
%JAVA_HOME%\bin\jarsigner -sigfile UU -tsa https://timestamp.geotrust.com/tsa -keystore ../../../tools/pboon.keystore -storepass passww -keypass passw ../output/jar/%1.jar pboon
%JAVA_HOME%\bin\pack200 ../output/jar/%1.jar.pack.gz ../output/jar/%1.jar