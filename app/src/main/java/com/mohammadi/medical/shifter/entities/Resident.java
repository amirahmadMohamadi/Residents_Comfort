package com.mohammadi.medical.shifter.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Resident implements Cloneable, Comparable<Resident>, Serializable
{
	private int		id;
	private String	name;

	public Resident(String string)
	{
		name = string;
	}

	protected Resident(Parcel in)
	{
		id = in.readInt();
		name = in.readString();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	@Override
	public String toString()
	{
		return getName();
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
		Resident other = (Resident) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public Resident clone()
	{
		try
		{
			Resident resident = (Resident) super.clone();
			return resident;
		}
		catch (CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public int compareTo(Resident o)
	{
		return getName().compareTo(o.getName());
	}

}
