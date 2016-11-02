package fi.writemathgwt.client;

import java.util.ArrayList;

import fi.beans.lineairealgebra.Point2d;
import fi.writemath.engine.core.Stroke;
import fi.writemath.engine.strokematching.StrokeMatcher;
import fi.writemath.engine.strokematching.StrokeMatcherFactory;
import fi.writemath.engine.strokematching.results.MatchResult;
import fi.writemath.engine.strokematching.strokesampleset.StrokeSampleSetFactory;

public class StrokeMatcherWrapper {
//	DoublePoint doublePoint;
	private StrokeMatcher strokeMatcher;
	
	public StrokeMatcherWrapper(int tekenSet) {
		if (tekenSet == 2) {
			strokeMatcher = StrokeMatcherFactory.createStrokeMatcher(
					StrokeSampleSetFactory.SampleSetId.BASICMATH,
					StrokeMatcherFactory.MatchResultsId.UNIQUESORTED4, 
					StrokeMatcherFactory.MatchMethodId.SIMILARITY, 
					StrokeMatcherFactory.MatchMetricId.P2PDISTANCE
					);
		} else {
			// Minimal set = default
			strokeMatcher = StrokeMatcherFactory.createStrokeMatcher(
					StrokeSampleSetFactory.SampleSetId.BASICMATHMINIMAL,
					StrokeMatcherFactory.MatchResultsId.UNIQUESORTED4, 
					StrokeMatcherFactory.MatchMethodId.SIMILARITY, 
					StrokeMatcherFactory.MatchMetricId.P2PDISTANCE
					);
		}
	}
	
	public String findTeken(ArrayList<DoublePoint> doublePoints) {
		// convert to Stroke
		Stroke inputStroke = convertDoublePointListToStroke(doublePoints);
		
		// apply StrokeMatcher
		strokeMatcher.matchStroke(inputStroke);
		return getTeken1();
	}
	
	public String getTeken1() {
		return getTeken(0);
	}
	
	public String getTeken2() {
		return getTeken(1);
	}

	public String getTeken3() {
		return getTeken(2);
	}

	public String getTeken4() {
		return getTeken(3);
	}
	
	/* Private Parts */
	private Stroke convertDoublePointListToStroke(ArrayList<DoublePoint> doublePoints) {
		Stroke convertedStroke = new Stroke();
		for (int i=0; i<doublePoints.size(); i++) {
			DoublePoint currentPoint = doublePoints.get(i);
			convertedStroke.addPoint(new Point2d(currentPoint.getX(), currentPoint.getY()));
		}
		return convertedStroke;
	}
	
	private String getTeken(int rank) {
		MatchResult matchResult = strokeMatcher.getResult(rank);
		if (matchResult != null) {
			return matchResult.getIdentifier();
		} else {
			return "null";
		}
	}

}
