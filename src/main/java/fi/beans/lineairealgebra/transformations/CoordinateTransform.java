package fi.beans.lineairealgebra.transformations;

import java.io.Serializable;

public interface CoordinateTransform extends Serializable
{
	/**
	 * Apply the {@link CoordinateTransform} to a location.
	 *
	 * @param location
	 * @return transformed location
	 */
	public double[] apply( double[] location );


	/**
	 * Apply the {@link CoordinateTransform} to a location.
	 *
	 * @param location
	 * @return transformed location
	 */
	public void applyInPlace( double[] location );
}