; -- Sample1.iss --
; Demonstrates copying 3 files and creating an icon.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=DWO
AppVerName=DWO version 1.0
AppCopyright=Copyright (C) Freudenthal Instituut.
DefaultDirName={pf}\Wisweb\DWO
DefaultGroupName=Wisweb
UninstallDisplayIcon={app}\DWO.exe
MessagesFile=compiler:Dutch-1-2_0_18.isl
OutputDir=..\output\setup

[Files]
Source: "..\output\exe\DWO.exe"; DestDir: "{app}"


[Icons]
Name: "{group}\DWO"; Filename: "{app}\DWO.exe"
