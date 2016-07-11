/**
 *
 */
package com.mohammadi.medical.shifter.schedule;

/**
 * @author Mohammadi
 *
 */
public class SchedulingFailedException extends Exception
{

	/**
	 *
	 */
	private static final long serialVersionUID = 7424382018901949942L;

	public SchedulingFailedException()
	{
		super();
	}

	public SchedulingFailedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SchedulingFailedException(String message)
	{
		super(message);
	}

	public SchedulingFailedException(Throwable cause)
	{
		super(cause);
	}

}
