package fi.beans.lineairealgebra.transformations;

public class IllDefinedDataPointsException extends Exception
{
	private static final long serialVersionUID = -8384893194524443449L;

	public IllDefinedDataPointsException()
	{
		super( "The set of data points is ill defined.  No Model could be solved." );
	}


	public IllDefinedDataPointsException( final String message )
	{
		super( message );
	}


	public IllDefinedDataPointsException( final Throwable cause )
	{
		super( cause );
	}


	public IllDefinedDataPointsException( final String message, final Throwable cause )
	{
		super( message, cause );
	}
}