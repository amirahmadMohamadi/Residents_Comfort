/**
 *
 */
package com.mohammadi.medical.shifter.schedule;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.loop.monitors.SearchMonitorFactory;
import org.joda.time.Duration;

import java.io.Serializable;

/**
 * @author Mohammadi
 *
 */
public class ScheduleSettings implements Cloneable, Serializable
{

	private Duration timeLimit;

	public ScheduleSettings()
	{
		setTimeLimit(Duration.standardMinutes(1));
	}

	public Duration getTimeLimit()
	{
		return timeLimit;
	}

	public void setTimeLimit(Duration timeLimit)
	{
		this.timeLimit = timeLimit;
	}

	public void applySettings(Solver solver)
	{
		SearchMonitorFactory.limitTime(solver, timeLimit.getMillis());
	}

	@Override
	public ScheduleSettings clone()
	{
		try
		{
			ScheduleSettings clone = (ScheduleSettings) super.clone();
			if (getTimeLimit() != null)
				clone.timeLimit = Duration.millis(getTimeLimit().getMillis());
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}

}
