package com.mohammadi.medical.shifter.constraint;

public enum ConstraintImportance
{
	Important(10), Optional(5);

	private int cost;

	private ConstraintImportance(int cost)
	{
		this.cost = cost;
	}

	public int getCost()
	{
		return cost;
	}
}
