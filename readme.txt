fi.dwo.DWO

20050626
- parameters opslaan aangepast (confirmdialog)
- Teksten courses sco's aangepast.
- preview bij parameters nu ook herhaald mogelijk (steeds een nieuwe apletinstantie)

20050627
- Foutje uit DbAccesServlet gehaald (jarfolder klopte niet)

20050628
- CourseIcon TextArea ingebouwd voor auto-multiline
- CourseChoicePanel 4 kolommen
- preview nu in orde gemaakt
- ScoText kleiner.
- Titel bij scoList
- CourseDescription nu in textArea boven scolist
- tooltip coursedescription uitgezet.
- Imageloading aangepast (buffer in hashtable om herhaald laden te voorkomen)
- TextArea aangepast alligment center mogelijk.
- Header bij scopanel plaatje erbij
- coursepanel plaatje erbij
- resultknop op coursepanel naar beneden.
- Teksten aangepast in interface
- fiButton toegevoegd

20050703
- Traagheid bij parameters toonitems opgelost door (4) plaatjes in de jar toe te voegen.
- Aanpassing van DbAccesCreator, DwoHelper en GuiConstants voor application running niet meer nodig 

20050707
- Interfacezaken:
- Tabel aangepast en alle panel die deze tabel gebruiken
- ParameterManagementPanel scrollbalken
- Verschillende teksten
- Extra Klassen: Admin, GuiCreatorAdmin, AdminMenuPanel.
- Tabelheaders multiLine, zodat modules erop kunnen

20050708
- Foutje ParameterManagementPanel gemaakt (regel 180 parameters!=null)
- ScrolPanel size aangepast bij editmode (regel 168)

20050725
- Administrator staat nu als choiceoptie in registerpanel. Onderdrukken hiervan bij het het vullen van het keuzemenu gaf problemen.

20051113
- Achtergrondkleur en language van de applets worden nu door de dwo overruled

20060223
-	appletutil aangepast (getImage() was te traag.

20060510
- TooltipManager aangepast vanwege footmeldingen. Aantekening: Deze versie werkt goed in de dwo, maar (waarschijnlijk) niet in de losse applets. De beans in de dwo is dus verschillend van die in de codebase.

20060511
- Om out of memory te voorkomen: bij loadCourse alle scos's opruimen: finalize() in SCO

20060628
- In ParameterManagementPanel: previewbutton disabled bij popupurlapplet

20060704
- Class Sco methode end(): applet wordt nu steeds opgeruimd bij wisseling van Sco, om geheugenproblemen te voorkomen
- Applets worden geacht bij destroy de boel op te ruimen.
- Class DWO methode loadCourse: De obejecten die in de mappers worden opgeslagen worden bij wisseling van course gewist
	om geheugenproblemen te voorkomen
	
20060705
- ScoMapper en CourseMapper slaan hun objecten weer om (voor gebruik later) 
- Ook laatste punt 20060704 ongedaan gemaakt. Geheugens wordt nu beter vrijgegeven, maar waarom??
- Extra effect is dat popupurl weer werkt (deed het niet meer na vorige aanpassing)

20060706
- DwoWebPageServlet van Wim opgenomen
- Sco methode finalize verwijderd
- DWO methode loadCourse aangepast (finalize van alle  sco's van de vorige course werkte niet om geheugen vrij te krijgen.

20061007
- DWO stop() aangepast. logoff() toegevoegd.
- Tooltipmanager: window.getLocationOnScreen() afgeschermd met controle window.isShowing()

