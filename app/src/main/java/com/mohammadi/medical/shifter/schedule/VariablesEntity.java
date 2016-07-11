/**
 *
 */
package com.mohammadi.medical.shifter.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chocosolver.solver.variables.IntVar;

import com.mohammadi.medical.shifter.entities.Resident;

/**
 * @author Mohammadi
 *
 */
public class VariablesEntity
{
	private List<IntVar>						allVars				= new ArrayList<>();
	private Map<Resident, Map<Integer, IntVar>>	residenatDataMap	= new HashMap<>();
	private Map<Integer, Map<Resident, IntVar>>	dateResidentMap		= new HashMap<>();

	public VariablesEntity(List<IntVar> allVars, Map<Resident, Map<Integer, IntVar>> residenatDataMap, Map<Integer, Map<Resident, IntVar>> dateResidentMap)
	{
		super();
		this.allVars = allVars;
		this.residenatDataMap = residenatDataMap;
		this.dateResidentMap = dateResidentMap;
	}

	public List<IntVar> getAllVars()
	{
		return allVars;
	}

	public void setAllVars(List<IntVar> allVars)
	{
		this.allVars = allVars;
	}

	public Map<Resident, Map<Integer, IntVar>> getResidentDateMap()
	{
		return residenatDataMap;
	}

	public void setResidenatDataMap(Map<Resident, Map<Integer, IntVar>> residenatDataMap)
	{
		this.residenatDataMap = residenatDataMap;
	}

	public Map<Integer, Map<Resident, IntVar>> getDateResidentMap()
	{
		return dateResidentMap;
	}

	public void setDateResidentMap(Map<Integer, Map<Resident, IntVar>> dateResidentMap)
	{
		this.dateResidentMap = dateResidentMap;
	}

}
