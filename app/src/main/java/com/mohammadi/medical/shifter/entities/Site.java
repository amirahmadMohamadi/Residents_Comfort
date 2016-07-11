package com.mohammadi.medical.shifter.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Site implements Comparable<Site>, Cloneable, Serializable
{
	private int		id;
	private String	name;
	private int		capacity;

	public Site(String string)
	{
		name = string;
		setCapacity(1);
	}

	public Site(String string, int capacity)
	{
		name = string;
		setCapacity(capacity);
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getCapacity()
	{
		return capacity;
	}

	public void setCapacity(int capacity)
	{
		this.capacity = capacity;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public int compareTo(Site o)
	{
		return getName().compareTo(o.getName());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Site other = (Site) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public Site clone()
	{
		try
		{
			Site site = (Site) super.clone();
			return site;
		}
		catch (CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}

}
