package fi.beans.lineairealgebra.transformations;

/**
 * Signalizes that there were not enough data points available to estimate the
 * {@link AbstractModel}.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class NotEnoughDataPointsException extends Exception
{
	private static final long serialVersionUID = 492656623783477968L;

	public NotEnoughDataPointsException()
	{
		super( "Not enough data points to solve the Model." );
	}


	public NotEnoughDataPointsException( final String message )
	{
		super( message );
	}


	public NotEnoughDataPointsException( final Throwable cause )
	{
		super( cause );
	}


	public NotEnoughDataPointsException( final String message, final Throwable cause )
	{
		super( message, cause );
	}
}