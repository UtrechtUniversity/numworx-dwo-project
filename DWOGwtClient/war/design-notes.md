# NOTES

## Toelichting status 10/04/2018

* Tablet, desktop, dekstop-groot variant zijn nu zoals ontworpen. Ingeklapt menu zit er ook in. Help wordt breder op grotere schermen.

* Menu-icons zitten erin als SVG

* Kleuren zijn richting definitief getrokken, maar zijn nog niet helemaal goed.

* Nog niet crossbrowser gecheckt.

* Helpers.js moet nog worden verfraaid. Doe ik later omdat er mogelijk functies naar andere js files verhuizen.


## Toelichting status 23/03/2018

* Navigatie en header kloppen nog niet. Ik heb nu gefocust op alle losse (formulier-) elementen.

* Op zicht gebouwd. Marges, formaten en kleuren zijn nog niet *exact* maar komen wel in de buurt. De "shades of blue" wijken erg af.

* Bevat een javascript met wat helper functies voor selecteren en uitklappen. Moet nog verbeterd worden qua code.

* Nog niet crossbrowser getest. Ontwikkeld in Chrome.

* Hovers e.d. zouden we nog moeten testen op touch. Wat "voelt" lekker.

* De resultaten tabel is niet zo eenvoudig. Deze moet nog goed crossbrowser gecheckt worden. Het is me niet gelukt om *binnen* de border horizontaal te scrollen. Daarom nu een fade oplossing aan de rechterkant. Ben benieuwd naar jullie reactie.

* Er zit nog geen script in om de hoogtes goed te trekken.


## Vragen mbt layout


01. Bij account beheren: De eerste twee sectieheaders zijn breder dan de laatste, opzettelijk? >> Onderste is te small

*02. Bij account beheren: De SLA OP knop zit -naast- het formulier en de VOEG TOE knop -eronder-, opzettelijk? >>Ja, maar discutabel. Ik heb de onderste regel van de pagina genomen. Maar wellicht is gelijk met invulvlakken beter. 

03. Bij personen wordt docent en leerling afgekort tot D en L. Bij *account beheren* DOC en LL. >> moet zijn D  A  en L

*04. Scrollbar styling is niet mogelijk in firefox en Edge. Ik heb het nu volledig weggelaten. Willen we dit uberhaupt voor de browsers waar het wel kan? >>Ik heb er wel een voorkeur voor, maar heb liever alle browsers zoveel mogelijk gelijk. Is het een optie dit besluit uit te stellen, dus nu eerst niet te stylen en te kijken of het bevalt? 

05. Krijgen buttons hover-states? Zo ja, welke kleur? >>Ja.  Zie pagina 2 van document Detailontwerp scherminteractie GUI. Rode buttons moeten bij hover lichter worden

06. Tableheaders resultatentabel: bij een active header (blauw gekleurd, schaduwtje) zie je nog 1 pixel onderrand van de "onderliggende" headers. Is dat opzettelijk? Dat is namelijk moeilijk te realiseren, want je ziet de onderrand van de header die active is, dus van zichzelf in oude status. >>Nee. Is imperfectie van het ontwerp 
*07. Tableheaders resultatentabel: wat doen we als de header active is en dan nog te klein is voor de tekst-inhoud? >>Afbreken met drie puntjes bijvoorbeeld:   verschillende meth... 

08. Tableheaders resultatentabel: Er is op dit moment geen visual feedback op welke kolom gesorteerd is. Suggestie: betreffende pijltje primary-blauw maken. >>Yep 

*09. Resultatentabel: maken we gebruik van 3 of meer kleurcodes of gebruiken we een glijdende schaal? (zoals huidige implementatie) >>Mijn voorstel is te beperken tot     groen  geel  oranje  rood,  bjvoorbeeld  100-80    80-60    60-40    40-00  %   (of een andere verdeling als dat gewenst is, maar wel 4 kleuren)



**. Tableheaders resultatentabel: drie puntjes (ellipsis) voor multiline is niet mogelijk en vereist veel truucs om het voor sommige browsers te krijgen. Ook zijn er worden die te lang zijn voor de header, waarbij afbreken (zonders streepje) mogelijk is, maar dit geeft niet zo'n mooi resultaat. Je zult binnenkort zien dat ik voorlopig.