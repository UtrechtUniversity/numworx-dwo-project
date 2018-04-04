# NOTES


## Toelichting huidige status 23/03/2018

Navigatie en header kloppen nog niet. Ik heb nu gefocust op alle losse (formulier-) elementen.

Op zicht gebouwd. Marges, formaten en kleuren zijn nog niet *exact* maar komen wel in de buurt. De "shades of blue" wijken erg af.

Bevat een javascript met wat helper functies voor selecteren en uitklappen. Moet nog verbeterd worden qua code.

Nog niet crossbrowser getest. Ontwikkeld in Chrome.

Hovers e.d. zouden we nog moeten testen op touch. Wat "voelt" lekker.

De resultaten tabel is niet zo eenvoudig. Deze moet nog goed crossbrowser gecheckt worden. 
Het is me niet gelukt om *binnen* de border horizontaal te scrollen.
Daarom nu een fade oplossing aan de rechterkant. Ben benieuwd naar jullie reactie.

Er zit nog geen script in om de hoogtes goed te trekken.


## Vragen mbt layout

### Inconsistenties 
* Bij account: De eerste twee sectieheaders zijn breder dan de laatste, waarom?
* Bij account: De SLA OP knop zit -naast- het formulier en de VOEG TOE knop -eronder-, waarom?
* Op desktop ziet de help er (continu uitgeklapt) heel anders uit dan op tablet.
* Bij personen wordt docent en leerling afgekort tot D en L. Bij *account beheren* DOC en LL.

### Overige vragen
* Scrollbar styling is niet mogelijk in firefox en Edge. Willen we dit uberhaupt?