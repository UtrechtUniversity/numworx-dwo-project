package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;

public class MapperConstants {

	private Map<String,Map<String,String>> constants;
	
	public Map<String,String> getMap(String label) {
		return constants.get(label);
	}

	public MapperConstants() { 
		constants = new HashMap<String,Map<String,String>>();
		
		Map testMap = new HashMap<String,String>();
		constants.put("test", testMap);
		testMap.put("z-toets", "ztest"); 
		testMap.put("z-test", "ztest"); 
		testMap.put("t-toets voor één steekproef", "ttestone"); 
		testMap.put("t-test for one sample", "ttestone"); 
		testMap.put("t-toets voor afhankelijke groepen", "ttestpaired"); 
		testMap.put("t-test for two dependent samples", "ttestpaired"); 
		testMap.put("t-toets voor onafhankelijke groepen", "ttesttwo"); 
		testMap.put("t-test for two independent samples", "ttesttwo"); 
	
		Map sidedMap = new HashMap<String, String>();
        constants.put("sided", sidedMap);
        sidedMap.put("linkszijdig", "leftsided");
        sidedMap.put("left-sided", "leftsided");
        sidedMap.put("rechtszijdig", "rightsided");
        sidedMap.put("right-sided", "rightsided");
        sidedMap.put("tweezijdig", "twosided");
        sidedMap.put("two-sided", "twosided");
        
        Map rejectioncriticalMap = new HashMap<String, String>();
        constants.put("rejectioncritical", rejectioncriticalMap);
        rejectioncriticalMap.put("wel", "rejection");
        rejectioncriticalMap.put("inside", "rejection");
        rejectioncriticalMap.put("niet", "norejection");
        rejectioncriticalMap.put("outside", "norejection");
        
        Map conclusionMap = new HashMap<String, String>();
        constants.put("conclusion", conclusionMap);
        constants.put("conclusionhypotheses", conclusionMap);
        conclusionMap.put("Verwerp $fH$s0@.@", "rejecth0");
        conclusionMap.put("Reject $fH$s0@.@", "rejecth0");
        conclusionMap.put("Accepteer $fH$s0@.@", "accepth0");
        conclusionMap.put("Accept $fH$s0@.@", "accepth0");
        conclusionMap.put("Verwerp $fH$s0@@ niet.", "dontrejecth0");
        conclusionMap.put("Don't reject $fH$s0@.@", "dontrejecth0");
        conclusionMap.put("Verwerp $fH$s1@.@", "rejecth1");
        conclusionMap.put("Reject $fH$s1@.@", "rejecth1");
        conclusionMap.put("Accepteer $fH$s1@.@", "accepth1");
        conclusionMap.put("Accept $fH$s1@.@", "accepth1");
        conclusionMap.put("Verwerp $fH$s1@@ niet.", "dontrejecth1");
        conclusionMap.put("Don't reject $fH$s1@.@", "dontrejecth1");
        conclusionMap.put("wel", "rejection");
        conclusionMap.put("inside", "rejection");
        conclusionMap.put("niet", "norejection");
        conclusionMap.put("outside", "norejection");
        
        Map conclusionpvalueMap = new HashMap<String, String>();
        constants.put("conclusionpvalue", conclusionpvalueMap);
        conclusionpvalueMap.put("groter dan", "p>alpha");
        conclusionpvalueMap.put("larger than", "p>alpha");
        conclusionpvalueMap.put("kleiner of gelijk aan", "p\u2264alpha");
        conclusionpvalueMap.put("smaller than or equal to", "p\u2264alpha");
        
        Map feedbackMap = new HashMap<String, String>();
        constants.put("feedback", feedbackMap);
        //componenents: the mentioned rule (adding a component) was recognized to be carried out correctly
        feedbackMap.put("component.alpha", "Dit is een geschikt significantieniveau.");
        feedbackMap.put("component.conclusion.p-value", "Dit is de juiste conclusie over deze p-waarde.");
        feedbackMap.put("component.critical-conclusion", "Dit is de juiste conclusie over deze toetsingsgrootheid en kritiek gebied.");
        feedbackMap.put("component.critical.t-value", "Dit is de juiste kritieke t-waarde.");
        feedbackMap.put("component.critical.z-value", "Dit is de juiste kritieke z-waarde.");
        feedbackMap.put("component.df", "Dit aantal vrijheidsgraden is correct.");
        feedbackMap.put("component.h0", "Dit is een geschikte nulhypothese.");
        feedbackMap.put("component.h0-from-ha", "Dit is een geschikte nulhypothese.");
        feedbackMap.put("component.h0-from-ha-eq", "Dit is een geschikte nulhypothese.");
        feedbackMap.put("component.ha", "Dit is een geschikte alternatieve hypothese.");
        feedbackMap.put("component.hypotheses", "Dit zijn geschikte hypotheses.");
        feedbackMap.put("component.hypotheses-conclusion-critical", "Dit is de correcte conclusie over deze hypotheses; je hebt de toets correct uitgevoerd.");
        feedbackMap.put("component.hypotheses-conclusion-pvalue", "Dit is de correcte conclusie over deze hypotheses; je hebt de toets correct uitgevoerd.");
        feedbackMap.put("component.mean", "Dit is het juiste gemiddelde.");
        feedbackMap.put("component.n", "Dit is de juiste steekproefgrootte.");
        feedbackMap.put("component.p-value.t-test", "Dit is de juiste p-waarde bij deze toetsingsgrootheid.");
        feedbackMap.put("component.p-value.z-test", "Dit is de juiste p-waarde bij deze toetsingsgrootheid.");
        feedbackMap.put("component.rejection.critical", "Dit is het juiste kritieke gebied voor deze toets.");
        feedbackMap.put("component.sided", "Dit is de juiste toetsrichting.");
        feedbackMap.put("component.standard-deviation", "Dit is de correcte standaardafwijking.");
        feedbackMap.put("component.standard-error-sigma", "Dit is de correcte standaardfout.");
        feedbackMap.put("component.standard-error-sd", "Dit is de correcte standaardfout.");
        feedbackMap.put("component.test-formula", "Deze formule voor de toetsingsgrootheid is correct.");
        feedbackMap.put("component.test-value", "Deze waarde van de toetsingsgrootheid is correct.");
        feedbackMap.put("component.test.t-test", "Een t-toets voor één steekproef is hier inderdaad de meest geschikte toets.");
        feedbackMap.put("component.test.t-test-paired", "Een t-toets voor afhankelijke groepen is hier inderdaad de meest geschikte toets.");
        feedbackMap.put("component.test.t-test-two", "Een t-toets voor onafhankelijke groepen is hier inderdaad de meest geschikte toets.");
        feedbackMap.put("component.test.z-test", "Een z-toets is hier inderdaad de meest geschikte toets.");
        feedbackMap.put("component.variance", "Dit is de correcte variantie.");
        feedbackMap.put("expr.substitute", "");
        feedbackMap.put("noFeedback", "Dit is een correcte stap");
        
        //checks: state is not equivalent with previous state; checking of mentioned constraint resulted in error
        feedbackMap.put("check.samplemean", "Je hebt niet het juiste steekproefgemiddelde ingevuld.");
        feedbackMap.put("check.populationsdev", "Je hebt niet de juiste standaardafwijking van de variabele in de populatie ingevuld. Let ook op de notatie.");
        feedbackMap.put("check.samplesdev", "Je hebt niet de juiste steekproefstandaardafwijking ingevuld. Let ook op de notatie.");
        feedbackMap.put("check.samplesize", "Je hebt niet de juist steekproefgrootte ingevuld.");
        feedbackMap.put("check.alpha", "Het significantieniveau is niet correct.");
        feedbackMap.put("check.h0", "Deze nulhypothese past niet bij de opgestelde alternatieve hypothese of de claim uit de opdracht.");
        feedbackMap.put("check.h0: parameter mismatch", "De nulhypothese moet gaan over parameters in de populatie, niet over eigenschappen van één specifieke steekproef.");
        feedbackMap.put("check.h0: value mismatch", "Kijk nog eens naar de rechterkant van je nulhypothese. Welke waarde moet daar staan?");
        feedbackMap.put("check.h0: sign mismatch", "De richting van je nulhypothese past niet bij de alternatieve hypothese.");
        feedbackMap.put("check.h0: component is not of type relation", "Vul eerst de gehele nulhypothese in, voor je nakijkt.");
        feedbackMap.put("check.ha", "Deze alternatieve hypothese past niet bij de claim uit de opdracht.");
        feedbackMap.put("check.ha: parameter mismatch", "De alternatieve hypothese moet gaan over parameters in de populatie, niet over eigenschappen van één specifieke steekproef.");
        feedbackMap.put("check.ha: value mismatch", "Kijk nog eens naar de rechterkant van je alternatieve hypothese. Welke waarde moet daar staan?");
        feedbackMap.put("check.ha: sign mismatch", "De richting van je alternatieve hypothese past niet bij de claim uit de opdracht.");
        feedbackMap.put("check.ha: component is not of type relation", "Vul eerst de gehele alternatieve hypothese in, voor je nakijkt.");
        feedbackMap.put("check.sided", "Deze toetsrichting past niet bij de hypotheses of claim uit de opdracht.");
        feedbackMap.put("check.sided: Alternative hypothesis missing", "Bepaal eerst hypotheses voor je specificeert of je toets linkszijdig, rechtszijdig of tweezijdig is.");
        feedbackMap.put("check.test", "Je hebt niet de meest geschikte toets gekozen.");
        feedbackMap.put("check.test-formula", "De formule van de toetsingsgrootheid is niet correct.");
        feedbackMap.put("check.test-formula: component is not of type relation", "Bepaal eerst beide kanten van de formule, voor je nakijkt.");
        //feedbackMap.put("check.test-formula: TestChoice missing", "Wat voor toets ga je uitvoeren? Specificeer dit eerst, voordat je de toetsingsgrootheid gaat bepalen.");
        feedbackMap.put("check.test-formula: cannot solve", "Heb je alle variabelen in de formule gedefinieerd en gebruik je gangbare namen voor de variabelen?");
        feedbackMap.put("check.test-formula: no match", "De formule van de toetsingsgrootheid is niet correct.");
        feedbackMap.put("check.test-formula: test mismatch", "Bepaal je een t-waarde of een z-waarde?");
        feedbackMap.put("check.test-value", "Dit is niet de juiste waarde van de toetsingsgrootheid.");
        feedbackMap.put("check.test-value: test mismatch", "Bereken je een t-waarde of een z-waarde?");
        feedbackMap.put("check.test-value: component is not relation", "Bepaal zowel de variabelenaam als de waarde van de toetsingsgrootheid voor je nakijkt.");
        feedbackMap.put("check.df", "Het aantal vrijheidsgraden is niet correct.");
        //feedbackMap.put("check.df: TestChoice missing", "Wat voor toets ga je uitvoeren? Specificeer dit eerst, voordat je het aantal vrijheidsgraden bepaalt.");
        feedbackMap.put("check.critical", "Dit is niet de juiste kritieke waarde.");
        feedbackMap.put("check.critical: Sidedness missing", "Bij welke hypotheses hoort deze kritieke waarde? Stel eerst hypotheses op.");
        //feedbackMap.put("check.critical: TestChoice missing", "Wat voor toets ga je uitvoeren? Specificeer dit eerst, voordat je de kritieke waarde gaat bepalen.");
        //feedbackMap.put("check.critical: SignificanceLevel missing", "Voor welk significantieniveau bepaal je de kritieke waarde? Geef eerst het significantieniveau.");
        //feedbackMap.put("check.critical: df missing", "Voor welk aantal vrijheidsgraden bepaal je de kritieke waarde? Geef eerst het aantal vrijheidsgraden.");
        feedbackMap.put("check.critical: test mismatch", "Bepaal je een kritieke t-waarde of een kritieke z-waarde?");
        feedbackMap.put("check.critical: component is not relation", "Geef zowel de naam als de waarde van de kritieke waarde, voor je nakijkt.");
        feedbackMap.put("check.rejectioncritical", "Dit is niet het juiste kritieke gebied.");
        feedbackMap.put("check.rejectioncritical: test mismatch", "Bepaal je een kritiek gebied voor een t-waarde of voor een z-waarde?");
        feedbackMap.put("check.rejectioncritical: sidedness mismatch", "Past het teken dat je in het kritiek gebruikt bij de richting van de alternatieve hypothese?");
        feedbackMap.put("check.rejectioncritical: Sidedness missing", "Bij welke hypotheses hoort dit kritieke gebied? Stel eerst hypotheses op.");
        feedbackMap.put("check.rejectioncritical: component is not of type relation", "Bepaal eerst het gehele kritieke gebied, voor je nakijkt.");
        feedbackMap.put("check.p-value", "Dit is niet de juiste p-waarde bij de toetsingsgrootheid.");
        feedbackMap.put("check.p-value: TestValue missing", "Om een p-waarde te bepalen, moet je eerst de toetsingsgrootheid berekenen.");
        //feedbackMap.put("check.p-value: Df missing", "Voor het bepalen van de p-waarde bij een t-toets, heb je het aantal vrijheidsgraden nodig. Geef eerst het aantal vrijheidsgraden.");
        feedbackMap.put("check.p-value: Sidedness missing", "Bij welke hypotheses hoort deze p-waarde? Stel eerst hypotheses op.");
        feedbackMap.put("check.p-value: value not a probability", "De p-waarde is de overschrijdingskans. Heb je inderdaad een kans (een getal tussen 0 en 1) bepaald?");
        feedbackMap.put("check.conclusion.p-value", "Je bewering over de p-waarde en het significantieniveau klopt niet.");
        feedbackMap.put("check.conclusion.p-value: PValue missing", "Geef eerst een schatting van de p-waarde zelf, voordat je deze vergelijkt met het significantieniveau.");
        //feedbackMap.put("check.conclusion.p-value: SignificanceLevel missing", "Met welk significantieniveau vergelijk je de p-waarde? Geef eerst het significantieniveau.");
        feedbackMap.put("check.conclusion-critical", "Je bewering over de toetsingsgrootheid en het kritieke gebied klopt niet.");
        feedbackMap.put("check.conclusion-critical: RejectionCritical missing", "Wat is het kritieke gebied? Voordat je dit hebt bepaald is het te vroeg om deze conclusie te trekken.");
        feedbackMap.put("check.conclusion-critical: no match", "Wat zijn de kritieke waarde en de waarde van de toetsingsgrootheid? Bepaal ze eerst, voordat je ze probeert te vergelijken.");
        feedbackMap.put("check.conclusion-hypotheses", "Waar baseer je deze conclusie op? Kun je eerst iets zeggen over het kritieke gebied, of over de p-waarde die bij de toetsingsgrootheid hoort?");
        feedbackMap.put("check.conclusion-hypotheses: conclusion mismatch critical", "Deze conclusie past niet bij je conclusie over of de toetsingsgrootheid wel of niet in het kritieke gebied ligt.");
        feedbackMap.put("check.conclusion-hypotheses: conclusion mismatch pvalue", "Deze conclusie past niet bij je conclusie over of de p-waarde groter of kleiner is dan het significantieniveau.");
        feedbackMap.put("check.standard-error", "Dit is niet de juiste waarde van de standaardfout.");
        feedbackMap.put("check.standard-error: component is not of type relation", "Geef zowel de variabelenaam als de waarde van de standaardfout, voor je nakijkt.");
        feedbackMap.put("check.standard-error: standard error mismatch", "Kun je de standaardafwijking uit de populatie gebruiken, of moet je die schatten met behulp van de steekproef? En welke notatie hoort hierbij?");
        
        //Buggy: state is not equivalent to previous state; mentioned buggy rule detected
        feedbackMap.put("buggy.component.alpha", "Je hebt niet het gevraagde significantieniveau gebruikt.");
        feedbackMap.put("buggy.component.df", "Het aantal vrijheidsgraden dat je gebruikt is niet correct.");
        feedbackMap.put("buggy.component.h0", "De nulhypothese moet gaan over parameters in de populatie, niet over eigenschappen van één specifieke steekproef.");
        feedbackMap.put("buggy.component.ha", "Deze alternatieve hypothese past niet bij de claim uit de opdracht.");
        feedbackMap.put("buggy.component.t-test-one-sample.should-be-t-test-paired", "Een t-toets voor één steekproef is geschikt als je maar één steekproef hebt.");
        feedbackMap.put("buggy.component.t-test-one-sample.should-be-t-test-two-sample", "Een t-toets voor één steekproef is geschikt als je maar één steekproef hebt.");
        feedbackMap.put("buggy.component.t-test-one-sample.should-be-z-test", "Een t-toets voor één steekproef is geschikt bij kleine steekproefgroottes, wanneer de standaardafwijking van de populatie niet bekend is.");
        feedbackMap.put("buggy.component.t-test-paired.should-be-t-test-one-sample", "Een t-toets voor afhankelijke groepen is geschikt als je twee afhankelijke steekproeven hebt.");
        feedbackMap.put("buggy.component.t-test-paired.should-be-t-test-two-sample", "Een t-toets voor afhankelijke groepen is geschikt als de objecten in de twee steekproeven te koppelen zijn.");
        feedbackMap.put("buggy.component.t-test-paired.should-be-z-test", "Een t-toets voor afhankelijke groepen is geschikt als je twee afhankelijke steekproeven hebt.");
        feedbackMap.put("buggy.component.t-test-two-sample.should-be-t-test-one-sample", "Een t-toets voor onafhankelijke groepen is geschikt als je twee steekproeven hebt.");
        feedbackMap.put("buggy.component.t-test-two-sample.should-be-t-test-paired", "Een t-toets voor onafhankelijke groepen is geschikt als de objecten in de twee steekproeven niet te koppelen zijn. ");
        feedbackMap.put("buggy.component.t-test-two-sample.should-be-z-test", "Een t-toets voor onafhankelijke groepen is geschikt als je twee steekproeven hebt.");
        feedbackMap.put("buggy.component.z-test.should-be-t-test-one-sample", "Een z-toets is geschikt bij grote steekproeven, of wanneer de standaardafwijking van de populatie bekend is.");
        feedbackMap.put("buggy.component.z-test.should-be-t-test-paired", "Een z-toets is geschikt als je maar één steekproef hebt.");
        feedbackMap.put("buggy.component.z-test.should-be-t-test-two-sample", "Een z-toets is geschikt als je maar één steekproef hebt.");
        feedbackMap.put("buggy.component.test-z-value", "Waar heb je door gedeeld om deze toetsingsgrootheid te berekenen? De standaardafwijking of de standaardfout?");
        feedbackMap.put("buggy.component.test-t-value", "Waar heb je door gedeeld om deze toetsingsgrootheid te berekenen? De standaardafwijking of de standaardfout?");
        feedbackMap.put("buggy.component.critical.t-value-positive", "In welke staart(en) van de verdeling ligt het kritieke gebied in dit geval? Links, rechts, of allebei?");
        feedbackMap.put("buggy.component.critical.z-value-positive", "In welke staart(en) van de verdeling ligt het kritieke gebied in dit geval? Links, rechts, of allebei?");
        
        feedbackMap.put("buggy.component.accepth0.should-be-dontrejecth0", "In een hypothesetoets accepteren we hypotheses nooit. Als de data niet genoeg aanleiding geven om $fH$s0@@ te verwerpen, dan zeggen we dat we $fH$s0@@ niet verwerpen.");
        feedbackMap.put("buggy.component.accepth0.should-be-rejecth0", "In een hypothesetoets accepteren we hypotheses nooit. Maar geven de data inderdaad aanleiding om te denken dat $fH$s0@@ waar is?");
        feedbackMap.put("buggy.component.rejecth1.should-be-dontrejecth0", "Met een hypothesetoets kun je uiteindelijk een uitspraak doen over de nulhypothese, niet over de alternatieve hypothese.");
        feedbackMap.put("buggy.component.rejecth1.should-be-rejecth0", "Met een hypothesetoets kun je uiteindelijk een uitspraak doen over de nulhypothese, niet over de alternatieve hypothese.");
        feedbackMap.put("buggy.component.accepth1.should-be-dontrejecth0", "Met een hypothesetoets kun je uiteindelijk een uitspraak doen over de nulhypothese, niet over de alternatieve hypothese.");
        feedbackMap.put("buggy.component.accepth1.should-be-rejecth0", "Met een hypothesetoets kun je uiteindelijk een uitspraak doen over de nulhypothese, niet over de alternatieve hypothese.");
        feedbackMap.put("buggy.component.dontrejecth1.should-be-dontrejecth0", "Met een hypothesetoets kun je uiteindelijk een uitspraak doen over de nulhypothese, niet over de alternatieve hypothese.");
        feedbackMap.put("buggy.component.dontrejecth1.should-be-rejecth0", "Met een hypothesetoets kun je uiteindelijk een uitspraak doen over de nulhypothese, niet over de alternatieve hypothese.");
        feedbackMap.put("buggy.component.rejecth0.should-be-dontrejecth0", "Geven de data inderdaad voldoende bewijs om aan te denken dat de nullhypothese niet waar is?");
        feedbackMap.put("buggy.component.dontrejecth0.should-be-rejecth0", "Geven de data inderdaad voldoende bewijs om te denken dat $fH$s0@@ waar is?");
        
       
        
        Map hintMap = new HashMap<String, String>();
        constants.put("hint", hintMap);
        hintMap.put("exception", "Er is iets fout gegaan.");
        hintMap.put("component.test.t-test", "Bepaal welke toets geschikt is voor deze situatie.");
        hintMap.put("component.alpha", "Definiëer het significantieniveau waarmee je de toets gaat uitvoeren.");
        hintMap.put("component.conclusion.p-value", "Trek een conclusie op basis van de gevonden p-waarde.");
        hintMap.put("component.critical-conclusion", "Trek een conclusie op basis van de gevonden toetsingsgrootheid en het kritieke gebied.");
        hintMap.put("component.critical.t-value", "Bepaal de kritieke waarde(n) voor deze toets.");
        hintMap.put("component.critical.z-value", "Bepaal de kritieke waarde(n) voor deze toets.");
        hintMap.put("component.df", "Bepaal het aantal vrijheidsgraden in deze toets.");
        hintMap.put("component.h0", "Geef aan welke nulhypothese je wil toetsen.");
        hintMap.put("component.h0-from-ha", "Geef aan welke nulhypothese je wil toetsen.");
        hintMap.put("component.h0-from-ha-eq", "Geef aan welke nulhypothese je wil toetsen.");
        hintMap.put("component.ha", "Geef aan welke alternatieve hypothese je wil toetsen.");
        hintMap.put("component.hypotheses", "Bepaal hypotheses om te toetsen.");
        hintMap.put("component.hypotheses-conclusion", "Trek een conclusie over je hypotheses.");
        hintMap.put("component.mean", ""); //-- sample or population?
        hintMap.put("component.n", "Bepaal de steekproefgrootte.");
        hintMap.put("component.p-value.t-test", "Bepaal de p-waarde die hoort bij de gevonden toetsingsgrootheid.");
        hintMap.put("component.p-value.z-test", "Bepaal de p-waarde die hoort bij de gevonden toetsingsgrootheid.");
        hintMap.put("component.rejection.critical", "Bepaal het verwerpingsgebied dat past bij je hypotheses.");
        hintMap.put("component.sided", "Bepaal de toetsrichting die past bij de opdracht of je hypotheses.");
        hintMap.put("component.standard-deviation", ""); //-- sample or population?
        hintMap.put("component.standard-error", ""); //-- sample or population?
        hintMap.put("component.test-statistic", "Bereken de toetsingsgrootheid bij de gekozen toets.");
        hintMap.put("component.test.t-test", "Bepaal welke toets geschikt is voor deze situatie.");
        hintMap.put("component.test.t-test-paired", "Bepaal welke toets geschikt is voor deze situatie.");
        hintMap.put("component.test.t-test-two", "Bepaal welke toets geschikt is voor deze situatie.");
        hintMap.put("component.test.z-test", "Bepaal welke toets geschikt is voor deze situatie.");
        hintMap.put("component.variance", ""); //-- sample or population?
	
	
	
	}
	
}
