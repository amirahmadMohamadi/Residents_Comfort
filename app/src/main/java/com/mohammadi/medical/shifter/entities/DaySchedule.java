package com.mohammadi.medical.shifter.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class DaySchedule implements Serializable
{
	private Map<Site, List<Resident>> map;

	public DaySchedule()
	{
		map = new TreeMap<>();
	}

	public Map<Site, List<Resident>> getMap()
	{
		return map;
	}

	public void setMap(Map<Site, List<Resident>> map)
	{
		this.map = map;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (Entry<Site, List<Resident>> entry : getMap().entrySet())
		{
			sb.append("\t" + entry.getKey()+ ":");
			for (Resident resident : entry.getValue())
			{
				sb.append(resident + " ");
			}
		}
		return sb.toString();

	}
}
