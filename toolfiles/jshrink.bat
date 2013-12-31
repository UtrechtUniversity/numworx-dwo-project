java -jar ..\..\..\tools\jshrink.exe -script jshrink_all.txt
java -jar ..\..\..\tools\jshrink.exe -script jshrink_all-test.txt
cd ..\output\jar
call jarindex
call jarindex-test
cd ..\..\toolfiles
