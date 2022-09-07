package nl.uu.fi.dwo.mobile.client.ui;


import java.util.Optional;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.utils.LogBuilder;

public interface ActivityInterface {

	LogBuilder logBuilder();

	boolean isPremium();

	boolean isReview();

	boolean isEindtoetsVerzegeld();

	Scorm2004IF api();

	boolean isTest();

	String getResource(String string);

	LessonMode getLessonMode();

	boolean isNoordhoff();

	TrafficAgent agent();

	Optional<DwoGlobalVars> vars();

}
