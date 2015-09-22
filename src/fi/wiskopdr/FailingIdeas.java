package fi.wiskopdr;

import nl.uu.fi.dwo.ideas.client.AbstractIdeas;
import nl.uu.fi.dwo.ideas.client.ExerciseArrayCallback;
import nl.uu.fi.dwo.ideas.client.IdeasIF;
import nl.uu.fi.dwo.ideas.client.RuleArrayCallback;
import nl.uu.fi.dwo.ideas.client.RuleCallback;
import nl.uu.fi.dwo.ideas.client.RuleIF;

public class FailingIdeas extends AbstractIdeas implements IdeasIF {
	
	static final Throwable NOT_IMPLEMENTED = new RuntimeException("always failing");

	@Override
	public void getDerivation(RuleIF rule, String strategie,
			RuleArrayCallback callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void getAllFirsts(RuleIF rule, String strategie,
			RuleArrayCallback callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void getOneFirst(RuleIF expr, String strategie, RuleCallback callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void findBuggyRules(RuleIF expr, RuleIF input, String stategie,
			RuleArrayCallback callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void getExerciseList(ExerciseArrayCallback callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void diagnose(RuleIF vgl, RuleIF input, String strategie,
			RuleCallback callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void getRuleList(String strategie, RuleArrayCallback callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void getRulesInfo(String strategie, RuleArrayCallback callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void getExamples(String strategie, RuleArrayCallback callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void interpret(String how, RuleIF[] args, RuleCallback callback) {
		NOT_IMPLEMENTED.fillInStackTrace();
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void diagnose(RuleIF[] exprs, String strategie,
			RuleArrayCallback callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

}
