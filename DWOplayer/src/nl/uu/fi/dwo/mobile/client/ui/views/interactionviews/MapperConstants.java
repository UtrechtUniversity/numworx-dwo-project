package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

import com.google.gwt.core.client.GWT;

import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.mapperconstants.Feedback;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.mapperconstants.Hint;

public class MapperConstants {

	private Map<String,Map<String,String>> constants;
	private final Hint hints = GWT.create(Hint.class);
	private final Feedback feedback = GWT.create(Feedback.class);
	
	public Map<String,String> getMap(String label) {
		return constants.get(label);
	}
	
	public Feedback getFeedback() {
		return feedback;
	}

	public String getFeedback(String input) {
		try {
			return getFeedback().getString(massage(input));
		} catch (MissingResourceException e) {
			return input;
		}
	}
	
	public Hint getHints() {
		return hints;
	}
	
	public String getHint(String hint) {
		hint = massage(hint);
		try {
			return getHints().getString(hint);
		} catch (MissingResourceException e) {
			return hint;
		}
	}

	private String massage(String hint) {
		return hint.replace(' ', '_').replace(':', '_').replace('-', '_').replace('.', '_');
	}

	public MapperConstants() { 
		constants = new HashMap<String,Map<String,String>>();
		
		Map nullHypothesisMap = new HashMap<String,String>();
		constants.put("h0", nullHypothesisMap);
		nullHypothesisMap.put("afhankelijk", "dependent");
		nullHypothesisMap.put("onafhankelijk", "independent");
		
		Map alternativeHypothesisMap = new HashMap<String,String>();
		constants.put("ha", alternativeHypothesisMap);
		alternativeHypothesisMap.put("afhankelijk", "dependent");
		alternativeHypothesisMap.put("onafhankelijk", "independent");
		
		Map testMap = new HashMap<String,String>();
		constants.put("test", testMap);
		testMap.put("z-toets", "ztest"); 
		testMap.put("z-test", "ztest"); 
		testMap.put("t-toets voor één steekproef", "ttestone"); 
		testMap.put("t-test for one group", "ttestone"); 
		testMap.put("t-toets voor afhankelijke groepen", "ttestpaired"); 
		testMap.put("t-test for dependent groups", "ttestpaired"); 
		testMap.put("t-toets voor onafhankelijke groepen", "ttesttwo"); 
		testMap.put("t-test for independent groups", "ttesttwo"); 
		testMap.put("ANOVA", "anova");
		testMap.put("Pearson correlatietoets", "rpearson");
		testMap.put("Spearman correlatietoets", "rspearman");
		testMap.put("Pearson correlation test", "rpearson");
		testMap.put("Spearman correlation test", "rspearman");
		testMap.put("Chi-kwadraat", "chisquared");
		testMap.put("Chi-squard", "chisquared");
		
		Map sidedMap = new HashMap<String, String>();
        constants.put("sided", sidedMap);
        sidedMap.put("linkszijdig", "leftsided");
        sidedMap.put("one sided, left tail", "leftsided");
        sidedMap.put("rechtszijdig", "rightsided");
        sidedMap.put("one sided, right tail", "rightsided");
        sidedMap.put("tweezijdig", "twosided");
        sidedMap.put("two sided", "twosided");
        
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
        conclusionMap.put("Do not reject $fH$s0@.@", "dontrejecth0");
        conclusionMap.put("Verwerp $fH$s1@.@", "rejecth1");
        conclusionMap.put("Reject $fH$s1@.@", "rejecth1");
        conclusionMap.put("Accepteer $fH$s1@.@", "accepth1");
        conclusionMap.put("Accept $fH$s1@.@", "accepth1");
        conclusionMap.put("Verwerp $fH$s1@@ niet.", "dontrejecth1");
        conclusionMap.put("Do not reject $fH$s1@.@", "dontrejecth1");
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
        //components: the mentioned rule (adding a component) was recognized to be performed correctly
        
        //Replaced by feedback.java, so this is old and can be removed?
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
        feedbackMap.put("component.test.t-test", "Een t-toets voor één groep is hier inderdaad de meest geschikte toets.");
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
        feedbackMap.put("check.test", "Je hebt niet de meest geschikte toets gekozen.");
        feedbackMap.put("check.test-formula", "De formule van de toetsingsgrootheid is niet correct.");
        feedbackMap.put("check.test-formula: component is not of type relation", "Bepaal eerst beide kanten van de formule, voor je nakijkt.");
        //feedbackMap.put("check.test-formula: TestChoice missing", "Wat voor toets ga je uitvoeren? Specificeer dit eerst, voordat je de toetsingsgrootheid gaat bepalen.");
        feedbackMap.put("check.test-formula: cannot solve", "Heb je alle variabelen in de formule gedefinieerd en gebruik je gangbare namen voor de variabelen?");
        feedbackMap.put("check.test-formula: no match", "De formule van de toetsingsgrootheid is niet correct.");
        feedbackMap.put("check.test-formula: test mismatch", "Bepaal je een t-waarde of een z-waarde?");
        feedbackMap.put("check.test-value", "Dit is niet de juiste waarde van de toetsingsgrootheid.");
        feedbackMap.put("check.test-value: no match", "Dit is niet de juiste waarde van de toetsingsgrootheid.");
        feedbackMap.put("check.test-value: test mismatch", "Bereken je een t-waarde of een z-waarde?");
        feedbackMap.put("check.test-value: component is not of type relation", "Bepaal zowel de variabelenaam als de waarde van de toetsingsgrootheid voor je nakijkt.");
        feedbackMap.put("check.df", "Het aantal vrijheidsgraden is niet correct.");
        //feedbackMap.put("check.df: TestChoice missing", "Wat voor toets ga je uitvoeren? Specificeer dit eerst, voordat je het aantal vrijheidsgraden bepaalt.");
        feedbackMap.put("check.critical", "Dit is niet de juiste kritieke waarde.");
        feedbackMap.put("check.critical: alternative hypothesis missing", "Bij welke hypotheses hoort deze kritieke waarde? Stel eerst hypotheses op.");
        //feedbackMap.put("check.critical: TestChoice missing", "Wat voor toets ga je uitvoeren? Specificeer dit eerst, voordat je de kritieke waarde gaat bepalen.");
        //feedbackMap.put("check.critical: SignificanceLevel missing", "Voor welk significantieniveau bepaal je de kritieke waarde? Geef eerst het significantieniveau.");
        //feedbackMap.put("check.critical: df missing", "Voor welk aantal vrijheidsgraden bepaal je de kritieke waarde? Geef eerst het aantal vrijheidsgraden.");
        feedbackMap.put("check.critical: test mismatch", "Bepaal je een kritieke t-waarde of een kritieke z-waarde?");
        feedbackMap.put("check.critical: component is not of type relation", "Geef zowel de naam als de waarde van de kritieke waarde, voor je nakijkt.");
        feedbackMap.put("check.rejectioncritical", "Dit is niet het juiste kritieke gebied.");
        feedbackMap.put("check.rejectioncritical: test mismatch", "Bepaal je een kritiek gebied voor een t-waarde of voor een z-waarde?");
        feedbackMap.put("check.rejectioncritical: sidedness mismatch", "Past het teken dat je in het kritiek gebied gebruikt bij de richting van de alternatieve hypothese?");
        feedbackMap.put("check.rejectioncritical: alternative hypothesis missing", "Bij welke hypotheses hoort dit kritieke gebied? Stel eerst hypotheses op.");
        feedbackMap.put("check.rejectioncritical: component is not of type relation", "Bepaal eerst het gehele kritieke gebied, voor je nakijkt.");
        feedbackMap.put("check.p-value", "Dit is niet de juiste p-waarde bij de toetsingsgrootheid.");
        feedbackMap.put("check.p-value: TestValue missing", "Om een p-waarde te bepalen, moet je eerst de toetsingsgrootheid berekenen.");
        //feedbackMap.put("check.p-value: Df missing", "Voor het bepalen van de p-waarde bij een t-toets, heb je het aantal vrijheidsgraden nodig. Geef eerst het aantal vrijheidsgraden.");
        feedbackMap.put("check.p-value: alternative hypothesis missing", "Bij welke hypotheses hoort deze p-waarde? Stel eerst hypotheses op.");
        feedbackMap.put("check.p-value: component is not of type relation", "Er is iets fout gegaan met de p-waarde. Vul deze nogmaals in."); //TODO: kijken wat hier goede feedbacktekst is. 
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
        feedbackMap.put("buggy.component.h0-samplemean", "De nulhypothese moet gaan over parameters in de populatie, niet over eigenschappen van één specifieke steekproef.");
        feedbackMap.put("buggy.component.ha", "Deze alternatieve hypothese past niet bij de claim uit de opdracht.");
        feedbackMap.put("buggy.component.t-test-one-sample.should-be-t-test-paired", "Een t-toets voor één groep is geschikt als je maar één steekproef hebt.");
        feedbackMap.put("buggy.component.t-test-one-sample.should-be-t-test-two-sample", "Een t-toets voor één groep is geschikt als je maar één steekproef hebt.");
        feedbackMap.put("buggy.component.t-test-one-sample.should-be-z-test", "Een t-toets voor één groep is geschikt bij kleine steekproefgroottes, wanneer de standaardafwijking van de populatie niet bekend is.");
        feedbackMap.put("buggy.component.t-test-paired.should-be-t-test-one-sample", "Een t-toets voor afhankelijke groepen is geschikt als je twee afhankelijke steekproeven hebt.");
        feedbackMap.put("buggy.component.t-test-paired.should-be-t-test-two-sample", "Een t-toets voor afhankelijke groepen is geschikt als de objecten in de twee steekproeven te koppelen zijn.");
        feedbackMap.put("buggy.component.t-test-paired.should-be-z-test", "Een t-toets voor afhankelijke groepen is geschikt als je twee afhankelijke steekproeven hebt.");
        feedbackMap.put("buggy.component.t-test-two-sample.should-be-t-test-one-sample", "Een t-toets voor onafhankelijke groepen is geschikt als je twee steekproeven hebt.");
        feedbackMap.put("buggy.component.t-test-two-sample.should-be-t-test-paired", "Een t-toets voor onafhankelijke groepen is geschikt als de objecten in de twee steekproeven niet te koppelen zijn.");
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
        feedbackMap.put("buggy.component.rejecth0.should-be-dontrejecth0", "Geven de data inderdaad voldoende bewijs om aan te denken dat $fH$s0@@ niet waar is?");
        feedbackMap.put("buggy.component.dontrejecth0.should-be-rejecth0", "Geven de data inderdaad voldoende bewijs om te denken dat $fH$s0@@ waar is?");
        
        /*
        //English version (for Economics)
        Map feedbackMap = new HashMap<String, String>();
        constants.put("feedback", feedbackMap);
        //components: the mentioned rule (adding a component) was recognized to be performed correctly
        feedbackMap.put("component.alpha", "This is an appropriate significance level.");
        feedbackMap.put("component.conclusion.p-value", "This is the correct conclusion about this p-value.");
        feedbackMap.put("component.critical-conclusion", "This is the correct conclusion about this test statistic and rejection region.");
        feedbackMap.put("component.critical.t-value", "This is the correct critical t-value.");
        feedbackMap.put("component.critical.z-value", "This is the correct critical z-value.");
        feedbackMap.put("component.df", "This number of degrees of freedom is correct.");
        feedbackMap.put("component.h0", "This is an appropriate null hypothesis.");
        feedbackMap.put("component.h0-from-ha", "This is an appropriate null hypothesis.");
        feedbackMap.put("component.h0-from-ha-eq", "This is an appropriate null hypothesis.");
        feedbackMap.put("component.ha", "This is an appropriate alternative hypothesis.");
        feedbackMap.put("component.hypotheses", "These are appropriate hypotheses.");
        feedbackMap.put("component.hypotheses-conclusion-critical", "This is the correct conclusion about these hypotheses; you have completed the hypothesis test correctly.");
        feedbackMap.put("component.hypotheses-conclusion-pvalue", "This is the correct conclusion about these hypotheses; you have completed the hypothesis test correctly.");
        feedbackMap.put("component.mean", "This is the correct mean.");
        feedbackMap.put("component.n", "This is the correct sample size.");
        feedbackMap.put("component.p-value.t-test", "This is the correct p-value for this test statistic.");
        feedbackMap.put("component.p-value.z-test", "This is the correct p-value for this test statistic.");
        feedbackMap.put("component.rejection.critical", "This is the correct rejection region for this test.");
        feedbackMap.put("component.sided", "This is the correct direction of the test.");
        feedbackMap.put("component.standard-deviation", "This is the correct standard deviation.");
        feedbackMap.put("component.standard-error-sigma", "This is the correct standard error.");
        feedbackMap.put("component.standard-error-sd", "This is the correct standard error.");
        feedbackMap.put("component.test-formula", "This formula for the test statistic is correct.");
        feedbackMap.put("component.test-value", "This value of the test statistic is correct.");
        feedbackMap.put("component.test.t-test", "A t-test for one group indeed is the most appropriate test here.");
        feedbackMap.put("component.test.t-test-paired", "A t-test for dependent groups indeed is the most appropriate test here.");
        feedbackMap.put("component.test.t-test-two", "A t-test for independent groups indeed is the most appropriate test here.");
        feedbackMap.put("component.test.z-test", "A z-test indeed is the most appropriate test here.");
        feedbackMap.put("component.variance", "This is the correct variance.");
        feedbackMap.put("expr.substitute", "");
        feedbackMap.put("noFeedback", "This is a correct step.");
        
        //checks: state is not equivalent with previous state; checking of mentioned constraint resulted in error
        feedbackMap.put("check.samplemean", "The sample mean you have specified is incorrect.");
        feedbackMap.put("check.populationsdev", "The standard deviation of the variable in the population is incorrect. Also check your notation.");
        feedbackMap.put("check.samplesdev", "The sample standard deviation of the variable is incorrect. Also check your notation.");
        feedbackMap.put("check.samplesize", "The sample size you have specified is incorrect.");
        feedbackMap.put("check.alpha", "The significance level is incorrect.");
        feedbackMap.put("check.h0", "This null hypothesis does not match the alternative hypothesis or the claim from the task.");
        feedbackMap.put("check.h0: parameter mismatch", "The null hypothesis should describe parameters in the population, not characteristics of a specific sample.");
        feedbackMap.put("check.h0: value mismatch", "Reconsider the right hand side of your null hypothesis. Which value should be stated there?");
        feedbackMap.put("check.h0: sign mismatch", "The direction of your null hypothesis does not match with the direction of the alternative hypothesis.");
        feedbackMap.put("check.h0: component is not of type relation", "Fill in a complete null hypothesis, before pressing the check button.");
        feedbackMap.put("check.ha", "This alternative hypothesis does not match with the claim from the task.");
        feedbackMap.put("check.ha: parameter mismatch", "The alternative hypothesis should describe parameters in the population, not characteristics of a specific sample.");
        feedbackMap.put("check.ha: value mismatch", "Reconsider the right hand side of your alternative hypothesis. Which value should be stated there?");
        feedbackMap.put("check.ha: sign mismatch", "The direction of your null hypothesis does not match with the claim from the task.");
        feedbackMap.put("check.ha: component is not of type relation", "Fill in a complete alternative hypothesis, before pressing the check button.");
        feedbackMap.put("check.sided", "The test direction does not match with the hypothesis or with the claim from the task.");
        feedbackMap.put("check.test", "You did not choose the most appropriate test.");
        feedbackMap.put("check.test-formula", "The formula for the test statistic is incorrect.");
        feedbackMap.put("check.test-formula: component is not of type relation", "First specify both sides of the formula, before pressing the check button.");
        //feedbackMap.put("check.test-formula: TestChoice missing", "Which test are you going to carry out? Specify this before calculating a test statistic.");
        feedbackMap.put("check.test-formula: cannot solve", "Did you define all variables in the formula and did you use common names for the variables?");
        feedbackMap.put("check.test-formula: no match", "The formula for the test statistic is incorrect.");
        feedbackMap.put("check.test-formula: test mismatch", "Are you calculating a t-value or a z-value?");
        feedbackMap.put("check.test-value", "This is not the correct value of the test statistic.");
        feedbackMap.put("check.test-value: no match", "This is not the correct value of the test statistic.");
        feedbackMap.put("check.test-value: test mismatch", "Are you calculating a t-value or a z-value?");
        feedbackMap.put("check.test-value: component is not of type relation", "Fill in both the name and the value of the test statistic, before pressing the check button. ");
        feedbackMap.put("check.df", "The number of degrees of freedom is incorrect.");
        //feedbackMap.put("check.df: TestChoice missing", "Which test are you going to carry out? Specify this before determining the number of degrees of freedom.");
        feedbackMap.put("check.critical", "The critical value is incorrect.");
        feedbackMap.put("check.critical: alternative hypothesis missing", "To which hypotheses does this critical value belong? First state hypotheses.");
        //feedbackMap.put("check.critical: TestChoice missing", "Which test are you going to carry out? Specify this before determining the critical value.")
        //feedbackMap.put("check.critical: SignificanceLevel missing", "What is the significance level for this critical value? First specify the significance level.");
        //feedbackMap.put("check.critical: df missing", "What is the number of degrees of freedom for this critical value? First give the number of degrees of freedom.");
        feedbackMap.put("check.critical: test mismatch", "Are you determining a critical t-value or a critical z-value?");
        feedbackMap.put("check.critical: component is not of type relation", "Fill in both the name and the value of the critical value, before pressing the check button.");
        feedbackMap.put("check.rejectioncritical", "This rejection region is incorrect.");
        feedbackMap.put("check.rejectioncritical: test mismatch", "Are you specifying a rejection region for a t-value or for a z-value?");
        feedbackMap.put("check.rejectioncritical: sidedness mismatch", "Does the sign you use in the rejection region match with the direction of the alternative hypothesis?");
        feedbackMap.put("check.rejectioncritical: alternative hypothesis missing", "To which hypotheses does this rejection region belong? First state hypotheses.");
        feedbackMap.put("check.rejectioncritical: component is not of type relation", "Fill in a complete rejection region, before pressing the check button.");
        feedbackMap.put("check.p-value", "This is not the correct p-value for the value of the test statistic.");
        feedbackMap.put("check.p-value: TestValue missing", "To determine a p-value, you first need to calculate a test statistic.");
        //feedbackMap.put("check.p-value: Df missing", "To find a p-value in a t-test, you need the number of degrees of freedom. First specify the number of degrees of freedom.");
        feedbackMap.put("check.p-value: alternative hypothesis missing", "To which hypotheses does this p-value belong? First state hypotheses.");
        feedbackMap.put("check.p-value: component is not of type relation", "Something went wrong with the p-value. Try filling it in again."); //TODO: kijken wat hier goede feedbacktekst is. 
        feedbackMap.put("check.p-value: value not a probability", "The p-value is a probability. Did you indeed fill in a probability (a number between 0 and 1)?");
        feedbackMap.put("check.conclusion.p-value", "Your conclusion about the p-value and the significance level is incorrect.");
        feedbackMap.put("check.conclusion.p-value: PValue missing", "First give an estimation of the p-value itself, before comparing it with the significance level.");
        //feedbackMap.put("check.conclusion.p-value: SignificanceLevel missing", "To which significance level do you compare the p-value? First specify the significance level.");
        feedbackMap.put("check.conclusion-critical", "Your conclusion about the test statistic and the rejection region is incorrect.");
        feedbackMap.put("check.conclusion-critical: RejectionCritical missing", "What is the rejection region? Before specifying this, it is too early to draw this conclusion.");
        feedbackMap.put("check.conclusion-critical: no match", "What are the critical value and the test statistic? First specify or calculate them, before trying to compare them.");
        feedbackMap.put("check.conclusion-hypotheses", "On what base do you draw this conclusion? Can you first say something about the rejection region, or about the p-value that corresponds to the test statistic?");
        feedbackMap.put("check.conclusion-hypotheses: conclusion mismatch critical", "This conclusion does not match your conclusion about whether or not the test statistic lies inside the rejection region.");
        feedbackMap.put("check.conclusion-hypotheses: conclusion mismatch pvalue", "This conclusion does not match your conclusion about whether the p-value is larger or smaller than the significance level.");
        feedbackMap.put("check.standard-error", "This is not the correct value of the standard error.");
        feedbackMap.put("check.standard-error: component is not of type relation", "Specify both name and value of the standard error, before pressing the check button.");
        feedbackMap.put("check.standard-error: standard error mismatch", "Can you use the standard deviation from the population, or do you have to estimate it based on the sample? And which notation should you use?");
               
        //Buggy: state is not equivalent to previous state; mentioned buggy rule detected
        feedbackMap.put("buggy.component.alpha", "You did not use the significance level that was specified in the task.");
        feedbackMap.put("buggy.component.df", "The number of degrees of freedom you use is incorrect.");
        feedbackMap.put("buggy.component.h0", "The null hypothesis should describe parameters in the population, not characteristics of a specific sample.");
        feedbackMap.put("buggy.component.ha", "This alternative hypothesis does not match with the claim from the task.");
        feedbackMap.put("buggy.component.t-test-one-sample.should-be-t-test-paired", "A t-test for one group is appropriate if you only have one sample.");
        feedbackMap.put("buggy.component.t-test-one-sample.should-be-t-test-two-sample", "A t-test for one group is appropriate if you only have one sample.");
        feedbackMap.put("buggy.component.t-test-one-sample.should-be-z-test", "A t-test for one group is appropriate for small sample sizes, when the standard deviation in the population is unknown.");
        feedbackMap.put("buggy.component.t-test-paired.should-be-t-test-one-sample", "A t-test for dependent groups is appropriate if you have two samples.");
        feedbackMap.put("buggy.component.t-test-paired.should-be-t-test-two-sample", "A t-tes for dependent groups is appropriate if the objects in the two samples can be paired."); 
        feedbackMap.put("buggy.component.t-test-paired.should-be-z-test", "A t-test for dependent groups is appropriate if you have two samples.");
        feedbackMap.put("buggy.component.t-test-two-sample.should-be-t-test-one-sample", "A t-test for independent groups is appropriate if you have two samples.");
        feedbackMap.put("buggy.component.t-test-two-sample.should-be-t-test-paired", "A t-test for independent groups is appropriate if the objects in the two samples cannot be paired.");
        feedbackMap.put("buggy.component.t-test-two-sample.should-be-z-test", "A t-test for independent groups is appropriate if you have two samples.");
        feedbackMap.put("buggy.component.z-test.should-be-t-test-one-sample", "A z-test is appropriate for large sample sizes, or when the standard deviation in the population is known.");
        feedbackMap.put("buggy.component.z-test.should-be-t-test-paired", "A z-test is appropriate if you only have one sample.");
        feedbackMap.put("buggy.component.z-test.should-be-t-test-two-sample", "A z-test is appropriate if you only have one sample.");
        feedbackMap.put("buggy.component.test-z-value", "Did you divide by the standard deviation or by the standard error to calculate this tes statistic?");
        feedbackMap.put("buggy.component.test-t-value", "Did you divide by the standard deviation or by the standard error to calculate this tes statistic?");
        feedbackMap.put("buggy.component.critical.t-value-positive", "In which tail(s) of the distribution does the rejection region fall in this case? Left, right, or both?");
        feedbackMap.put("buggy.component.critical.z-value-positive", "In which tail(s) of the distribution does the rejection region fall in this case? Left, right, or both?");
        feedbackMap.put("buggy.component.accepth0.should-be-dontrejecth0", "In a hypothesis test, we never accept hypotheses. If the data do not provide enough evidence to reject $fH$s0@@, we say that we do not reject $fH$s0@@.");
        feedbackMap.put("buggy.component.accepth0.should-be-rejecth0", "In a hypothesis test, we never accept hypotheses. But do the data indeed give a reason to think that $fH$s0@@ is true?");
        feedbackMap.put("buggy.component.rejecth1.should-be-dontrejecth0", "With a hypothesis test you can in the end make a statement about the null hypothesis, not about the alternative hypothesis.");
        feedbackMap.put("buggy.component.rejecth1.should-be-rejecth0", "With a hypothesis test you can in the end make a statement about the null hypothesis, not about the alternative hypothesis.");
        feedbackMap.put("buggy.component.accepth1.should-be-dontrejecth0", "With a hypothesis test you can in the end make a statement about the null hypothesis, not about the alternative hypothesis.");
        feedbackMap.put("buggy.component.accepth1.should-be-rejecth0", "With a hypothesis test you can in the end make a statement about the null hypothesis, not about the alternative hypothesis.");
        feedbackMap.put("buggy.component.dontrejecth1.should-be-dontrejecth0", "With a hypothesis test you can in the end make a statement about the null hypothesis, not about the alternative hypothesis.");
        feedbackMap.put("buggy.component.dontrejecth1.should-be-rejecth0", "With a hypothesis test you can in the end make a statement about the null hypothesis, not about the alternative hypothesis.");
        feedbackMap.put("buggy.component.rejecth0.should-be-dontrejecth0", "Do the data indeed provide enough evidence to think that $fH$s0@@ is not true?");
        feedbackMap.put("buggy.component.dontrejecth0.should-be-rejecth0", "Do the data indeed provide enough evidence to think that $fH$s0@@ is true?");
        */ 
        
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
        hintMap.put("component.hypotheses-conclusion-critical", "Trek een conclusie over je hypotheses.");
        hintMap.put("component.hypotheses-conclusion-pvalue", "Trek een conclusie over je hypotheses.");
        hintMap.put("component.mean", ""); //-- sample or population?
        hintMap.put("component.n", "Bepaal de steekproefgrootte.");
        hintMap.put("component.p-value.t-test", "Bepaal de p-waarde die hoort bij de gevonden toetsingsgrootheid.");
        hintMap.put("component.p-value.z-test", "Bepaal de p-waarde die hoort bij de gevonden toetsingsgrootheid.");
        hintMap.put("component.rejection.critical", "Bepaal het verwerpingsgebied dat past bij je hypotheses.");
        hintMap.put("component.sided", "Bepaal de toetsrichting die past bij de opdracht of je hypotheses.");
        hintMap.put("component.standard-deviation", ""); //-- sample or population?
        hintMap.put("component.standard-error", ""); //-- sample or population?
        hintMap.put("component.standard-error-sigma", "Bepaal de standaardfout."); //-- sample or population?
        hintMap.put("component.standard-error-sd", "Bepaal de standaardfout."); //-- sample or population?
        hintMap.put("component.test-formula", "Bepaal de formule voor de toetsingsgrootheid.");
        hintMap.put("component.test-value", "Bereken de toetsingsgrootheid bij de gekozen toets.");
        hintMap.put("component.test.t-test", "Bepaal welke toets geschikt is voor deze situatie.");
        hintMap.put("component.test.t-test-paired", "Bepaal welke toets geschikt is voor deze situatie.");
        hintMap.put("component.test.t-test-two", "Bepaal welke toets geschikt is voor deze situatie.");
        hintMap.put("component.test.z-test", "Bepaal welke toets geschikt is voor deze situatie.");
        hintMap.put("component.variance", ""); //-- sample or population?
	
        /*
        //English version (for economics)
        Map hintMap = new HashMap<String, String>();
        constants.put("hint", hintMap);
        hintMap.put("exception", "Something went wrong.");
        hintMap.put("component.alpha", "Specify the significance level for the test you are carrying out.");
        hintMap.put("component.conclusion.p-value", "Draw a conclusion based on the p-value you found.");
        hintMap.put("component.critical-conclusion", "Draw a conclusion based on the test value and rejection region you found.");
        hintMap.put("component.critical.t-value", "Find the critical value(s) for this test.");
        hintMap.put("component.critical.z-value", "Find the critical value(s) for this test.");
        hintMap.put("component.df", "Find the number of degrees of freedom for this test.");
        hintMap.put("component.h0", "State a null hypothesis to test.");
        hintMap.put("component.h0-from-ha", "State a null hypothesis to test.");
        hintMap.put("component.h0-from-ha-eq", "State a null hypothesis to test.");
        hintMap.put("component.ha", "State an alternative hypothesis to test.");
        hintMap.put("component.hypotheses", "State hypotheses to test.");
        hintMap.put("component.hypotheses-conclusion", "Draw a conclusion about your hypotheses.");
        hintMap.put("component.hypotheses-conclusion-critical", "Draw a conclusion about your hypotheses.");
        hintMap.put("component.hypotheses-conclusion-pvalue", "Draw a conclusion about your hypotheses.");
        hintMap.put("component.mean", ""); //-- sample or population?
        hintMap.put("component.n", "Specify the sample size.");
        hintMap.put("component.p-value.t-test", "Find the p-value for the test statistic you found.");
        hintMap.put("component.p-value.z-test", "Find the p-value for the test statistic you found.");
        hintMap.put("component.rejection.critical", "Find the rejection region that corresponds with your hypotheses.");
        hintMap.put("component.sided", "Specify the direction that corresponds with your hypotheses.");
        hintMap.put("component.standard-deviation", ""); //-- sample or population?
        hintMap.put("component.standard-error", ""); //-- sample or population?
        hintMap.put("component.standard-error-sigma", "Find the standard error."); 
        hintMap.put("component.standard-error-sd", "Find the standard error."); 
        hintMap.put("component.test-formula", "Specify the formula for calculating the test statistic.");
        hintMap.put("component.test-value", "Calculate the test statistic for the chosen test.");
        hintMap.put("component.test.t-test", "Determine which test is appropriate for the given situation.");
        hintMap.put("component.test.t-test-paired", "Determine which test is appropriate for the given situation.");
        hintMap.put("component.test.t-test-two", "Determine which test is appropriate for the given situation.");
        hintMap.put("component.test.z-test", "Determine which test is appropriate for the given situation.");
        hintMap.put("component.variance", ""); //-- sample or population?
		*/
	
	}
	
}
