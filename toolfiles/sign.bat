%JAVA_HOME%\bin\pack200 --repack ../output/jar/dwo.jar
%JAVA_HOME%\bin\jarsigner -keystore ../../../tools/pboon.keystore -storepass passww -keypass passw ../output/jar/dwo.jar pboon
%JAVA_HOME%\bin\pack200 ../output/jar/dwo.jar.pack.gz ../output/jar/dwo.jar
%JAVA_HOME%\bin\pack200 --repack ../output/jar/dwo-test.jar
%JAVA_HOME%\bin\jarsigner -keystore ../../../tools/pboon.keystore -storepass passww -keypass passw ../output/jar/dwo-test.jar pboon
%JAVA_HOME%\bin\pack200 ../output/jar/dwo-test.jar.pack.gz ../output/jar/dwo-test.jar